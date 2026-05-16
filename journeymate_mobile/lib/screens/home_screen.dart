import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../widgets/ai_travel_assistant.dart';

class _Category {
  final String id, label;
  final IconData icon;
  final List<Color> colors;
  const _Category(this.id, this.label, this.icon, this.colors);
}

const _categories = [
  _Category('alojamiento', 'Hoteles',     LucideIcons.hotel,  [Color(0xFF2DD4BF), Color(0xFF0D9488)]),
  _Category('vuelos',      'Vuelos',      LucideIcons.plane,  [Color(0xFF38BDF8), Color(0xFF0284C7)]),
  _Category('coches',      'Coches',      LucideIcons.car,    [Color(0xFFFBBF24), Color(0xFFD97706)]),
  _Category('actividades', 'Actividades', LucideIcons.ticket, [Color(0xFFA78BFA), Color(0xFF7C3AED)]),
  _Category('cruceros',    'Cruceros',    LucideIcons.ship,   [Color(0xFF60A5FA), Color(0xFF2563EB)]),
  _Category('trenes',      'Trenes',      LucideIcons.car,  [Color(0xFFF87171), Color(0xFFDC2626)]),
];

const _destinos = [
  {'nombre': 'Tokio',      'tag': '🔥 Tendencia', 'image': 'assets/images/fotoTokio.jpg',     'iata': 'TYO', 'destText': 'Tokyo'},
  {'nombre': 'Bali',       'tag': '🌴 Relax',     'image': 'assets/images/fotoBali.png',      'iata': 'DPS', 'destText': 'Bali'},
  {'nombre': 'París',      'tag': '❤️ Romántico', 'image': 'assets/images/fotoParis.jpg',     'iata': 'PAR', 'destText': 'Paris'},
  {'nombre': 'Dubai',      'tag': '✨ Lujo',       'image': 'assets/images/fotoDubai.jpg',     'iata': 'DXB', 'destText': 'Dubai'},
  {'nombre': 'Nueva York', 'tag': '🏙️ Urbano',    'image': 'assets/images/fotoNuevaYork.jpg', 'iata': 'NYC', 'destText': 'New York'},
];

const _stats = [
  {'valor': '2M+',  'label': 'Viajeros'},
  {'valor': '150+', 'label': 'Destinos'},
  {'valor': '24/7', 'label': 'Soporte IA'},
  {'valor': '€0',   'label': 'Comisiones'},
];

// Imágenes del carrusel hero — usa tus assets locales
const _carouselImages = [
  'assets/images/foto1.jpg',
  'assets/images/foto2.jpg',
  'assets/images/foto3.jpg',
  'assets/images/foto4.jpg',
  'assets/images/foto5.jpg',
];

class HomeScreen extends StatefulWidget {
  /// Callback que el AppShell puede inyectar para cambiar de tab.
  final void Function(int tabIndex, {String? section, String? destText})? onChangeTab;

  const HomeScreen({super.key, this.onChangeTab});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String _userName = '';
  int    _currentImg = 0;

  @override
  void initState() {
    super.initState();
    _loadUser();
    _startCarousel();
  }

  Future<void> _loadUser() async {
    final name = await AuthService.getUserName();
    if (mounted && name != null) setState(() => _userName = name);
  }

  void _startCarousel() {
    Future.delayed(const Duration(seconds: 4), () {
      if (!mounted) return;
      setState(() => _currentImg = (_currentImg + 1) % _carouselImages.length);
      _startCarousel();
    });
  }

  // navega al tab de búsqueda con la sección correcta
  void _goSearch(String? section, {String? destText}) {
    HapticFeedback.lightImpact();
    if (widget.onChangeTab != null) {
      widget.onChangeTab!(1, section: section, destText: destText);
    } else {
      String extra = section != null ? '?tab=$section' : '';
      if (destText != null) extra += '&destText=${Uri.encodeComponent(destText)}';
      context.go('/buscar$extra');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      // AITravelAssistant como FAB nativo (no Positioned manual)
      floatingActionButton: const AITravelAssistant(),
      body: RefreshIndicator(
        color: AppColors.teal600,
        backgroundColor: Colors.white,
        onRefresh: () async { await _loadUser(); await Future.delayed(const Duration(milliseconds: 400)); },
        child: CustomScrollView(
          physics: const BouncingScrollPhysics(),
          slivers: [
            _buildAppBar(),
            SliverToBoxAdapter(child: _buildHeroCard()),
            SliverToBoxAdapter(child: _buildSectionTitle('¿QUÉ BUSCAS?')),
            SliverToBoxAdapter(child: _buildCategories()),
            SliverToBoxAdapter(child: _buildSectionTitle('DESTINOS POPULARES')),
            SliverToBoxAdapter(child: _buildDestinos()),
            SliverToBoxAdapter(child: _buildStats()),
            // FIX: Sin footer — padding final para el bottom nav
            const SliverToBoxAdapter(child: SizedBox(height: 100)),
          ],
        ),
      ),
    );
  }

  // ── AppBar ─────────────────────────────
  Widget _buildAppBar() => SliverAppBar(
    backgroundColor: Colors.transparent,
    elevation: 0,
    floating: true,
    snap: true,
    toolbarHeight: 72,
    // FIX: automaticallyImplyLeading = false evita el botón atrás automático
    automaticallyImplyLeading: false,
    flexibleSpace: SafeArea(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            // Saludo (no hay botón atrás aquí)
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    _userName.isNotEmpty ? 'Hola, $_userName 👋' : 'Bienvenido 👋',
                    style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900,
                        fontSize: 18, letterSpacing: -0.3),
                    overflow: TextOverflow.ellipsis,
                  ),
                  const Text('¿A dónde vamos hoy?',
                      style: TextStyle(color: Colors.white70, fontSize: 12)),
                ],
              ),
            ),
            const SizedBox(width: 12),
            // Avatar / Login
            GestureDetector(
              onTap: () {
                if (_userName.isNotEmpty) {
                  if (widget.onChangeTab != null) widget.onChangeTab!(3);
                  else context.go('/perfil');
                } else {
                  context.go('/login');
                }
              },
              child: Container(
                width: 44, height: 44,
                decoration: BoxDecoration(
                  color: _userName.isNotEmpty
                      ? AppColors.teal700
                      : Colors.white.withOpacity(0.2),
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white.withOpacity(0.4), width: 2),
                ),
                child: _userName.isNotEmpty
                    ? Center(child: Text(_userName[0].toUpperCase(),
                    style: const TextStyle(color: Colors.white,
                        fontWeight: FontWeight.w900, fontSize: 18)))
                    : const Icon(LucideIcons.user, color: Colors.white, size: 20),
              ),
            ),
          ],
        ),
      ),
    ),
  );

  // ── Hero card ─────────────────────────────────────────────────────────────
  Widget _buildHeroCard() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 8),
      child: GestureDetector(
        onTap: () => _goSearch(null),
        child: Container(
          height: 200,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(28),
            boxShadow: [BoxShadow(color: AppColors.teal900.withOpacity(0.3),
                blurRadius: 20, offset: const Offset(0, 8))],
          ),
          clipBehavior: Clip.antiAlias,
          child: Stack(children: [
            // Carrusel de fondo
            AnimatedSwitcher(
              duration: const Duration(milliseconds: 800),
              child: Image.asset(
                _carouselImages[_currentImg],
                key: ValueKey(_currentImg),
                fit: BoxFit.cover, width: double.infinity, height: double.infinity,
                errorBuilder: (_, __, ___) => Container(
                  decoration: const BoxDecoration(gradient: LinearGradient(
                    colors: [Color(0xFF0D4F4C), Color(0xFF115E59)],
                    begin: Alignment.topLeft, end: Alignment.bottomRight,
                  )),
                ),
              ),
            ),
            // Gradiente oscuro
            Container(decoration: BoxDecoration(
              gradient: LinearGradient(begin: Alignment.topCenter, end: Alignment.bottomCenter,
                  colors: [Colors.transparent, Colors.black.withOpacity(0.65)]),
            )),
            // Contenido
            Positioned(bottom: 20, left: 20, right: 20, child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('TU PRÓXIMA AVENTURA',
                    style: TextStyle(color: Colors.white60, fontSize: 9,
                        fontWeight: FontWeight.w900, letterSpacing: 2.5)),
                const SizedBox(height: 4),
                const Text('Descubre el mundo\ncon JourneyMate',
                    style: TextStyle(color: Colors.white, fontSize: 20,
                        fontWeight: FontWeight.w900, letterSpacing: -0.3, height: 1.15)),
                const SizedBox(height: 12),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                  decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(14)),
                  child: Row(mainAxisSize: MainAxisSize.min, children: [
                    const Icon(LucideIcons.search, size: 15, color: AppColors.teal600),
                    const SizedBox(width: 8),
                    const Text('Hoteles, vuelos, coches...',
                        style: TextStyle(color: AppColors.teal400, fontSize: 12,
                            fontWeight: FontWeight.w600)),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(color: AppColors.teal600,
                          borderRadius: BorderRadius.circular(8)),
                      child: const Text('BUSCAR',
                          style: TextStyle(color: Colors.white, fontSize: 9,
                              fontWeight: FontWeight.w900, letterSpacing: 1.5)),
                    ),
                  ]),
                ),
              ],
            )),
          ]),
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) => Padding(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 10),
    child: Text(title, style: const TextStyle(color: Colors.white70, fontSize: 13,
        fontWeight: FontWeight.w900, letterSpacing: 2)),
  );

  // ── Categorías  ────────────────────────────────
  Widget _buildCategories() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: GridView.count(
        crossAxisCount: 3,
        crossAxisSpacing: 10, mainAxisSpacing: 10,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        childAspectRatio: 1.1,
        children: _categories.map((c) {
          final isSoon = c.id == 'trenes';
          return GestureDetector(
            onTap: () {
              if (!isSoon) _goSearch(c.id);
            },
            child: Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(colors: c.colors,
                    begin: Alignment.topLeft, end: Alignment.bottomRight),
                borderRadius: BorderRadius.circular(20),
                boxShadow: [BoxShadow(color: c.colors.last.withOpacity(0.35),
                    blurRadius: 10, offset: const Offset(0, 4))],
              ),
              child: Stack(children: [
                Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Icon(c.icon, color: Colors.white, size: 26),
                  const SizedBox(height: 5),
                  Text(c.label, style: const TextStyle(color: Colors.white,
                      fontWeight: FontWeight.w900, fontSize: 13)),
                ])),
                if (isSoon)
                  Positioned(top: 6, right: 6, child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 2),
                    decoration: BoxDecoration(color: const Color(0xFFFBBF24),
                        borderRadius: BorderRadius.circular(6)),
                    child: const Text('SOON', style: TextStyle(fontSize: 7,
                        fontWeight: FontWeight.w900, color: AppColors.teal950)),
                  )),
              ]),
            ),
          );
        }).toList(),
      ),
    );
  }

  // ── Destinos ──────────
  Widget _buildDestinos() {
    return SizedBox(
      height: 165,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        physics: const BouncingScrollPhysics(),
        itemCount: _destinos.length,
        separatorBuilder: (_, __) => const SizedBox(width: 12),
        itemBuilder: (_, i) {
          final d = _destinos[i];
          return GestureDetector(
            onTap: () {
              HapticFeedback.lightImpact();
              if (widget.onChangeTab != null) {
                widget.onChangeTab!(1, section: 'vuelos', destText: d['destText']);
              } else {
                context.go('/buscar?tab=vuelos&destText=${Uri.encodeComponent(d['destText']!)}');
              }
            },
            child: Container(
              width: 125,
              decoration: BoxDecoration(borderRadius: BorderRadius.circular(22),
                  boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.12),
                      blurRadius: 10, offset: const Offset(0, 4))]),
              clipBehavior: Clip.antiAlias,
              child: Stack(children: [
                Positioned.fill(child: Image.asset(
                  d['image']!,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => Container(
                    decoration: const BoxDecoration(gradient: AppColors.gradientMain),
                    child: const Center(child: Icon(LucideIcons.mapPin, color: Colors.white, size: 28)),
                  ),
                )),
                Positioned.fill(child: Container(
                  decoration: BoxDecoration(gradient: LinearGradient(
                    begin: Alignment.topCenter, end: Alignment.bottomCenter,
                    colors: [Colors.transparent, Colors.black.withOpacity(0.72)],
                  )),
                )),
                Positioned(bottom: 12, left: 10, right: 10, child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
                      decoration: BoxDecoration(color: Colors.white.withOpacity(0.18),
                          borderRadius: BorderRadius.circular(7)),
                      child: Text(d['tag']!, style: const TextStyle(color: Colors.white,
                          fontSize: 10, fontWeight: FontWeight.w900)),
                    ),
                    const SizedBox(height: 4),
                    Text(d['nombre']!, style: const TextStyle(color: Colors.white,
                        fontWeight: FontWeight.w900, fontSize: 15, letterSpacing: -0.2)),
                  ],
                )),
              ]),
            ),
          );
        },
      ),
    );
  }

  // ── Stats ─────────────────────────────────────────────────────────────────
  Widget _buildStats() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 18),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.12),
          borderRadius: BorderRadius.circular(22),
          border: Border.all(color: Colors.white.withOpacity(0.18)),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: _stats.map((s) => Column(children: [
            Text(s['valor']!, style: const TextStyle(color: Colors.white,
                fontWeight: FontWeight.w900, fontSize: 19, letterSpacing: -0.5)),
            Text(s['label']!, style: const TextStyle(color: Colors.white60, fontSize: 10)),
          ])).toList(),
        ),
      ),
    );
  }
}