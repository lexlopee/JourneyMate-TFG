import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';
import '../services/search_service.dart';
import '../services/auth_service.dart';
import '../utils/date_utils.dart';
import '../widgets/loading_video.dart';
import '../widgets/car_3d_viewer.dart';
import '../widgets/ai_travel_assistant.dart';
import '../widgets/footer_widget.dart';
import '../widgets/search/search_form_widget.dart';
import '../widgets/results/results_list_widget.dart';

// ── Tipos de sección (equivalente a Section en navbar.tsx) ───────────────────
enum Section {
  alojamiento('Alojamiento', LucideIcons.hotel),
  vuelos('Vuelos',           LucideIcons.plane),
  coches('Coches',           LucideIcons.car),
  actividades('Actividades', LucideIcons.ticket),
  cruceros('Cruceros',       LucideIcons.ship),
  trenes('Trenes',           LucideIcons.train);

  const Section(this.label, this.icon);
  final String label;
  final IconData icon;
}

class SearchScreen extends StatefulWidget {
  final String? initialTab;
  final String? initialToId;
  final String? initialDestText;
  const SearchScreen({super.key, this.initialTab, this.initialToId, this.initialDestText});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> with SingleTickerProviderStateMixin {
  Section _section = Section.alojamiento;
  bool _loading = false;
  List<dynamic> _results = [];

  // Equivalente a searchData en App.tsx
  late Map<String, dynamic> _searchData;

  final _scrollController = ScrollController();
  late AnimationController _iconAnimCtrl;
  late Animation<double> _iconAnim;

  String? _userName;

  @override
  void initState() {
    super.initState();
    _iconAnimCtrl = AnimationController(vsync: this, duration: const Duration(milliseconds: 600));
    _iconAnim = CurvedAnimation(parent: _iconAnimCtrl, curve: Curves.easeOutBack);
    _iconAnimCtrl.forward();

    final tomorrow        = addDays(1);
    final dayAfterTomorrow = addDays(2);

    _searchData = {
      'fromId': '', 'toId': '', 'originText': '', 'destinationText': '',
      'destination': '', 'startDate': tomorrow, 'endDate': dayAfterTomorrow,
      'pickupTime': '10:00', 'dropoffTime': '10:00', 'driverAge': 30,
      'adults': 1, 'roomQty': 1, 'childrenAge': '',
      'cabinClass': 'ECONOMY', 'sort': 'BEST', 'currencyCode': 'EUR',
      'carType': 'all', 'cruiseDestination': '', 'cruisePort': '',
    };

    // Leer query params del router (equivalente a useSearchParams)
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
      if (field == 'startDate' && (_searchData['startDate'] as String).compareTo(_searchData['endDate'] as String) > 0) {
        _searchData['endDate'] = _searchData['startDate'];
      }
    });
  }

  Future<void> _handleSearch() async {
    // Validaciones
    if (_section == Section.vuelos && (_searchData['toId'] ?? '').isEmpty) {
      _showAlert('Por favor, selecciona al menos un destino.');
      return;
    }
    if (_section != Section.coches &&
        (_searchData['destinationText'] ?? '').isEmpty &&
        (_searchData['destination'] ?? '').isEmpty) {
      _showAlert('Por favor, introduce un destino.');
      return;
    }

    setState(() { _loading = true; _results = []; });
    try {
      final results = await SearchService.performSearch(_section.name, _searchData);
      setState(() => _results = results);
      if (results.isNotEmpty) {
        await Future.delayed(const Duration(milliseconds: 400));
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 800),
          curve: Curves.easeInOut,
        );
      }
    } catch (e) {
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
    setState(() { _section = s; _results = []; });
    _iconAnimCtrl.reset();
    _iconAnimCtrl.forward();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: Stack(children: [
          // Contenido principal scrollable
          CustomScrollView(
            controller: _scrollController,
            slivers: [
              // Navbar
              SliverToBoxAdapter(child: _buildNavbar()),
              // Icono sección
              SliverToBoxAdapter(child: _buildSectionIcon()),
              // Buscador
              SliverToBoxAdapter(child: _buildSearchBox()),
              // Resultados
              SliverToBoxAdapter(
                child: ResultsListWidget(
                  results: _results,
                  section: _section,
                  searchData: _searchData,
                  destination: (_searchData['destinationText'] ?? _searchData['destination'] ?? '').replaceAll('_', ' '),
                ),
              ),
              const SliverToBoxAdapter(child: FooterWidget()),
              const SliverToBoxAdapter(child: SizedBox(height: 100)),
            ],
          ),

          // Loading overlay
          if (_loading)
            Container(
              color: const Color(0xCC042F2E),
              child: Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
                const LoadingVideo(size: 180),
                const SizedBox(height: 16),
                const Text('BUSCANDO LAS MEJORES OPCIONES...', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 10, letterSpacing: 3)),
              ])),
            ),

          // AI Assistant
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
            // Logo + User
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              GestureDetector(
                onTap: () => context.go('/'),
                child: const Text('JourneyMate', style: TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
              ),
              Row(children: [
                if (_userName != null) ...[
                  Text('Hola, $_userName', style: const TextStyle(color: AppColors.teal700, fontSize: 12, fontWeight: FontWeight.w600)),
                  const SizedBox(width: 8),
                  GestureDetector(
                    onTap: () async { await AuthService.logout(); if (mounted) setState(() => _userName = null); },
                    child: const Icon(LucideIcons.logOut, size: 18, color: AppColors.teal600),
                  ),
                ] else
                  GestureDetector(
                    onTap: () => context.go('/login'),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(12)),
                      child: const Row(children: [Icon(LucideIcons.user, size: 14, color: Colors.white), SizedBox(width: 4), Text('Acceder', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 11))]),
                    ),
                  ),
              ]),
            ]),
            const SizedBox(height: 10),
            // Tabs
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
                      Icon(s.icon, size: 13, color: active ? AppColors.teal900 : AppColors.teal600.withOpacity(0.6)),
                      const SizedBox(width: 4),
                      Text(s.label.toUpperCase(), style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1, color: active ? AppColors.teal900 : AppColors.teal600.withOpacity(0.6))),
                      if (s == Section.trenes) ...[
                        const SizedBox(width: 4),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                          decoration: BoxDecoration(color: const Color(0xFFFBBF24), borderRadius: BorderRadius.circular(6)),
                          child: const Text('SOON', style: TextStyle(fontSize: 6, fontWeight: FontWeight.w900, color: AppColors.teal950)),
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

  // ── Icono sección ────────────────────────────────────────────────────────
  Widget _buildSectionIcon() {
    return Padding(
      padding: const EdgeInsets.only(top: 20, bottom: 8),
      child: Center(
        child: ScaleTransition(
          scale: _iconAnim,
          child: Container(
            padding: _section == Section.coches ? const EdgeInsets.all(12) : const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(40),
              border: Border.all(color: Colors.white.withOpacity(0.4)),
            ),
            child: _section == Section.coches
                ? SizedBox(height: 120, width: 120, child: Car3DViewer(carType: _searchData['carType'] ?? 'all', height: 120, showLabel: true))
                : Icon(_section.icon, size: 64, color: AppColors.teal900),
          ),
        ),
      ),
    ).animate().fadeIn(duration: 400.ms);
  }

  // ── Buscador ─────────────────────────────────────────────────────────────
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
          Text('JOURNEYMATE ${_section.label.toUpperCase()}', style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
          const SizedBox(height: 2),
          const Text('TU COMPAÑERO DE VIAJE INTELIGENTE', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: 3)),
          const SizedBox(height: 16),

          // Campos del formulario
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(color: Colors.white.withOpacity(0.3), borderRadius: BorderRadius.circular(28), border: Border.all(color: Colors.white.withOpacity(0.3))),
            child: Column(children: [
              SearchFormWidget(
                section: _section,
                searchData: _searchData,
                onChanged: _handleChange,
              ),
              const SizedBox(height: 10),
              // Botón buscar
              SizedBox(
                width: double.infinity,
                height: 52,
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
                    Text('BUSCAR', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 3, fontSize: 12)),
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