// lib/screens/search_screen.dart
//
// FIXES:
// 1. ✅ Scroll va al INICIO de resultados (no al final)
// 2. ✅ Footer eliminado
// 3. ✅ Coches: overflow corregido (SizedBox con constraints)
// 4. ✅ GlobalKey en el widget de resultados para calcular offset exacto

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';
import '../services/search_service.dart';
import '../services/api_service.dart';
import '../services/auth_service.dart';
import '../utils/date_utils.dart';
import '../widgets/loading_video.dart';
import '../widgets/ai_travel_assistant.dart';
import '../widgets/search/search_form_widget.dart';
import '../widgets/results/results_list_widget.dart';
import '../screens/search_section.dart';

class SearchScreen extends StatefulWidget {
  final String? initialTab;
  final String? initialToId;
  final String? initialDestText;
  final int navVersion;
  const SearchScreen({super.key, this.initialTab, this.initialToId, this.initialDestText, this.navVersion = 0});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> with SingleTickerProviderStateMixin {
  Section _section = Section.alojamiento;
  bool _loading = false;
  List<dynamic> _results = [];
  late Map<String, dynamic> _searchData;

  final _scrollController = ScrollController();

  // FIX 1: GlobalKey para medir la posición exacta donde empiezan los resultados
  final _resultsKey = GlobalKey();

  late AnimationController _iconAnimCtrl;
  late Animation<double> _iconAnim;
  String? _userName;

  @override
  void initState() {
    super.initState();
    _iconAnimCtrl = AnimationController(vsync: this, duration: const Duration(milliseconds: 600));
    _iconAnim = CurvedAnimation(parent: _iconAnimCtrl, curve: Curves.easeOutBack);
    _iconAnimCtrl.forward();

    _searchData = {
      'fromId': '', 'toId': '', 'originText': '', 'destinationText': '',
      'destination': '', 'startDate': addDays(1), 'endDate': addDays(2),
      'pickupTime': '10:00', 'dropoffTime': '10:00', 'driverAge': 30,
      'adults': 1, 'roomQty': 1, 'childrenAge': '',
      'cabinClass': 'ECONOMY', 'sort': 'BEST', 'currencyCode': 'EUR',
      'carType': 'all', 'cruiseDestination': '', 'cruisePort': '',
    };

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (widget.initialTab != null) {
        final tab = Section.values.firstWhere(
                (s) => s.name == widget.initialTab, orElse: () => Section.alojamiento);
        setState(() => _section = tab);
      }
      if (widget.initialToId != null) _searchData['toId'] = widget.initialToId;
      if (widget.initialDestText != null) {
        _searchData['destinationText'] = widget.initialDestText;
        _searchData['destination'] = widget.initialDestText;
      }
    });

    _loadUser();
  }

  // CLAVE: IndexedStack mantiene vivo el widget; didUpdateWidget reacciona
  // cada vez que AppShell pasa nuevos props (tab o destino).
  @override
  void didUpdateWidget(SearchScreen old) {
    super.didUpdateWidget(old);

    final tabChanged  = widget.initialTab      != old.initialTab;
    // Usamos navVersion para detectar CUALQUIER nueva navegación, aunque el
    // destino sea el mismo (ej: Bali → Home → Bali de nuevo).
    final destChanged = widget.navVersion != old.navVersion &&
        widget.initialDestText != null;
    if (!tabChanged && !destChanged) return;

    setState(() {
      if (tabChanged && widget.initialTab != null) {
        _section = Section.values.firstWhere(
              (s) => s.name == widget.initialTab,
          orElse: () => _section,
        );
        _results = [];
      }
      if (destChanged && widget.initialDestText != null) {
        _searchData['destinationText'] = widget.initialDestText!;
        _searchData['destination']     = widget.initialDestText!;
        _searchData['toId']            = '';
      }
    });

    if (tabChanged) {
      _iconAnimCtrl.reset();
      _iconAnimCtrl.forward();
    }
  }

  Future<void> _loadUser() async {
    final name = await AuthService.getUserName();
    if (mounted && name != null) setState(() => _userName = name);
  }

  @override
  void dispose() {
    _iconAnimCtrl.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _handleChange(String field, dynamic value) {
    setState(() {
      _searchData[field] = value;
      if (field == 'startDate') {
        final start = _searchData['startDate'] as String;
        final end   = _searchData['endDate']   as String;
        if (start.compareTo(end) > 0) _searchData['endDate'] = start;
      }
    });
  }

  Future<void> _handleSearch() async {
    if (_section == Section.vuelos) {
      if ((_searchData['fromId'] ?? '').isEmpty || (_searchData['toId'] ?? '').isEmpty) {
        _showAlert('Selecciona origen y destino de la lista sugerida.');
        return;
      }
    }
    if (_section == Section.coches) {
      if ((_searchData['fromId'] ?? '').isEmpty) {
        _showAlert('Selecciona un punto de recogida de la lista.');
        return;
      }
    }

    debugPrint('▶ BUSCAR [${_section.name}]');
    setState(() { _loading = true; _results = []; });

    try {
      final results = await SearchService.performSearch(_section.name, _searchData);
      debugPrint('✅ Resultados: ${results.length}');
      setState(() => _results = results);

      if (results.isEmpty) {
        _showAlert('No se encontraron resultados. Prueba con otras fechas o destino.');
      } else {
        // FIX 2: scroll al INICIO de los resultados, no al final
        WidgetsBinding.instance.addPostFrameCallback((_) async {
          await Future.delayed(const Duration(milliseconds: 150));
          if (!mounted || !_scrollController.hasClients) return;

          // Usamos el GlobalKey para calcular la posición exacta del widget de resultados
          final ctx = _resultsKey.currentContext;
          if (ctx != null) {
            final box = ctx.findRenderObject() as RenderBox?;
            if (box != null) {
              final position = box.localToGlobal(Offset.zero);
              // Offset absoluto en el scroll: posición actual + desplazamiento en pantalla
              final scrollOffset = _scrollController.offset + position.dy - kToolbarHeight - 20;
              _scrollController.animateTo(
                scrollOffset.clamp(0.0, _scrollController.position.maxScrollExtent),
                duration: const Duration(milliseconds: 600),
                curve: Curves.easeInOut,
              );
            }
          } else {
            // Fallback: desplazar una cantidad fija calculada (aprox. navbar+icon+search)
            _scrollController.animateTo(
              (_scrollController.position.maxScrollExtent * 0.45).clamp(0.0, _scrollController.position.maxScrollExtent),
              duration: const Duration(milliseconds: 600),
              curve: Curves.easeInOut,
            );
          }
        });
      }
    } on ApiException catch (e) {
      debugPrint('❌ ApiException ${e.statusCode}: ${e.message}');
      _showAlert('Error del servidor (${e.statusCode}). Inténtalo de nuevo.');
    } catch (e, stack) {
      debugPrint('❌ Error: $e\n$stack');
      _showAlert('Error de conexión. Reintenta en unos segundos.');
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  void _showAlert(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(msg, style: const TextStyle(fontWeight: FontWeight.w700)),
      backgroundColor: AppColors.teal900,
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    ));
  }

  void _changeSection(Section s) {
    setState(() {
      _section = s;
      _results = [];
      _searchData['sort'] = s == Section.actividades ? 'trending' : 'BEST';
    });
    _iconAnimCtrl.reset();
    _iconAnimCtrl.forward();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: Stack(children: [
          CustomScrollView(
            controller: _scrollController,
            slivers: [
              SliverToBoxAdapter(child: _buildNavbar()),
              SliverToBoxAdapter(child: _buildSectionIcon()),
              SliverToBoxAdapter(child: _buildSearchBox()),
              // FIX 3: GlobalKey en el contenedor de resultados para medir su posición
              SliverToBoxAdapter(
                child: KeyedSubtree(
                  key: _resultsKey,
                  child: ResultsListWidget(
                    results: _results,
                    section: _section,
                    searchData: _searchData,
                    destination: (_searchData['destinationText'] ?? _searchData['destination'] ?? '')
                        .replaceAll('_', ' '),
                  ),
                ),
              ),
              // FIX 4: Footer ELIMINADO — solo padding final
              const SliverToBoxAdapter(child: SizedBox(height: 100)),
            ],
          ),

          // Loading overlay
          if (_loading)
            Container(
              color: const Color(0xCC042F2E),
              child: Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
                Container(
                  width: 100, height: 100,
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(50),
                    border: Border.all(color: Colors.white.withOpacity(0.2), width: 2),
                  ),
                  child: const Center(child: CircularProgressIndicator(
                      color: Color(0xFF14B8A6), strokeWidth: 3)),
                ),
                const SizedBox(height: 24),
                const Text('BUSCANDO LAS MEJORES OPCIONES...',
                    style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900,
                        fontSize: 10, letterSpacing: 3)),
              ])),
            ),

          const Positioned(bottom: 24, right: 24, child: AITravelAssistant()),
        ]),
      ),
    );
  }

  // ── Navbar ────────────────────────────────────────────────────────────────
  Widget _buildNavbar() {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.8),
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: Colors.white.withOpacity(0.5)),
            boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 12)],
          ),
          child: Column(children: [
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              GestureDetector(
                onTap: () => context.go('/'),
                child: const Text('JourneyMate', style: TextStyle(color: AppColors.teal900,
                    fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
              ),
              Row(children: [
                if (_userName != null) ...[
                  Text('Hola, $_userName',
                      style: const TextStyle(color: AppColors.teal700, fontSize: 12, fontWeight: FontWeight.w600)),
                  const SizedBox(width: 8),
                  GestureDetector(
                    onTap: () async {
                      await AuthService.logout();
                      if (mounted) setState(() => _userName = null);
                    },
                    child: const Icon(LucideIcons.logOut, size: 18, color: AppColors.teal600),
                  ),
                ] else
                  GestureDetector(
                    onTap: () => context.go('/login'),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(12)),
                      child: const Row(children: [
                        Icon(LucideIcons.user, size: 14, color: Colors.white),
                        SizedBox(width: 4),
                        Text('Acceder', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 11)),
                      ]),
                    ),
                  ),
              ]),
            ]),
            const SizedBox(height: 10),
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(children: Section.values.map((s) {
                final active = s == _section;
                return GestureDetector(
                  onTap: () => _changeSection(s),
                  child: Container(
                    margin: const EdgeInsets.only(right: 4),
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 7),
                    decoration: BoxDecoration(
                      color: active ? Colors.white : Colors.transparent,
                      borderRadius: BorderRadius.circular(14),
                      boxShadow: active ? [const BoxShadow(color: Colors.black12, blurRadius: 4)] : [],
                    ),
                    child: Row(children: [
                      Icon(s.icon, size: 13,
                          color: active ? AppColors.teal900 : AppColors.teal600.withOpacity(0.6)),
                      const SizedBox(width: 4),
                      Text(s.label.toUpperCase(), style: TextStyle(fontSize: 9,
                          fontWeight: FontWeight.w900, letterSpacing: 1,
                          color: active ? AppColors.teal900 : AppColors.teal600.withOpacity(0.6))),
                      if (s == Section.trenes) ...[
                        const SizedBox(width: 4),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                          decoration: BoxDecoration(color: const Color(0xFFFBBF24),
                              borderRadius: BorderRadius.circular(6)),
                          child: const Text('SOON', style: TextStyle(fontSize: 6,
                              fontWeight: FontWeight.w900, color: AppColors.teal950)),
                        ),
                      ],
                    ]),
                  ),
                );
              }).toList()),
            ),
          ]),
        ),
      ),
    );
  }

  // ── Icono sección ─────────────────────────────────────────────────────────
  Widget _buildSectionIcon() {
    return Padding(
      padding: const EdgeInsets.only(top: 20, bottom: 8),
      child: Center(
        child: ScaleTransition(
          scale: _iconAnim,
          child: Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(40),
              border: Border.all(color: Colors.white.withOpacity(0.4)),
            ),
            child: Icon(_section.icon, size: 64, color: AppColors.teal900),
          ),
        ),
      ),
    ).animate().fadeIn(duration: 400.ms);
  }

  // ── Buscador ──────────────────────────────────────────────────────────────
  Widget _buildSearchBox() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.4),
          borderRadius: BorderRadius.circular(40),
          border: Border.all(color: Colors.white.withOpacity(0.6)),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 20)],
        ),
        padding: const EdgeInsets.all(20),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text('JOURNEYMATE ${_section.label.toUpperCase()}',
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900,
                  color: AppColors.teal900, letterSpacing: -0.5)),
          const SizedBox(height: 2),
          const Text('TU COMPAÑERO DE VIAJE INTELIGENTE',
              style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                  color: AppColors.teal600, letterSpacing: 3)),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.3),
                borderRadius: BorderRadius.circular(28),
                border: Border.all(color: Colors.white.withOpacity(0.3))),
            child: Column(children: [
              SearchFormWidget(
                section: _section,
                searchData: _searchData,
                onChanged: _handleChange,
                navVersion: widget.navVersion,
              ),
              const SizedBox(height: 10),
              SizedBox(
                width: double.infinity, height: 52,
                child: ElevatedButton(
                  onPressed: _loading ? null : _handleSearch,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    elevation: 4,
                  ),
                  child: _loading
                      ? const LoadingSpinner()
                      : const Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                    Icon(LucideIcons.search, size: 16),
                    SizedBox(width: 8),
                    Text('BUSCAR', style: TextStyle(fontWeight: FontWeight.w900,
                        letterSpacing: 3, fontSize: 12)),
                  ]),
                ),
              ),
            ]),
          ),
        ]),
      ),
    );
  }
}