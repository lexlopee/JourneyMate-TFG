import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../widgets/ai_travel_assistant.dart';
import '../widgets/footer_widget.dart';

// ── Datos estáticos equivalentes a Home.tsx ───────────────────────────────────
class _Category {
  final String id, label, desc;
  final IconData icon;
  final List<Color> colors;
  const _Category(this.id, this.label, this.icon, this.desc, this.colors);
}

const _categories = [
  _Category('alojamiento', 'Alojamiento', LucideIcons.hotel,   'Hoteles y apartamentos',      [Color(0xFF2DD4BF), Color(0xFF0D9488)]),
  _Category('vuelos',      'Vuelos',      LucideIcons.plane,   'Vuelos directos y con escala', [Color(0xFF38BDF8), Color(0xFF0284C7)]),
  _Category('coches',      'Coches',      LucideIcons.car,     'Alquiler sin complicaciones',  [Color(0xFFFBBF24), Color(0xFFD97706)]),
  _Category('actividades', 'Actividades', LucideIcons.ticket,  'Experiencias únicas',          [Color(0xFFA78BFA), Color(0xFF7C3AED)]),
  _Category('cruceros',    'Cruceros',    LucideIcons.ship,    'Navega por el mundo',          [Color(0xFF60A5FA), Color(0xFF2563EB)]),
  _Category('trenes',      'Trenes',      LucideIcons.train,   'Viaja por tierra',             [Color(0xFFF87171), Color(0xFFDC2626)]),
];

const _destinos = [
  {'nombre': 'Tokio',      'tag': 'Tendencia', 'iata': 'TYO', 'destText': 'Tokyo'},
  {'nombre': 'Roma',       'tag': 'Cultura',   'iata': 'ROM', 'destText': 'Rome'},
  {'nombre': 'Bali',       'tag': 'Relax',     'iata': 'DPS', 'destText': 'Bali'},
  {'nombre': 'Nueva York', 'tag': 'Urbano',    'iata': 'NYC', 'destText': 'New York'},
  {'nombre': 'Dubái',      'tag': 'Lujo',      'iata': 'DXB', 'destText': 'Dubái'},
  {'nombre': 'París',      'tag': 'Romántico', 'iata': 'PAR', 'destText': 'París'},
];

const _stats = [
  {'valor': '2M+',  'label': 'Viajeros satisfechos'},
  {'valor': '150+', 'label': 'Destinos disponibles'},
  {'valor': '24/7', 'label': 'Soporte con IA'},
  {'valor': '€0',   'label': 'Sin comisiones ocultas'},
];

// ── Imágenes de carrusel (Unsplash públicas) ──────────────────────────────────
// Sustituye por tus assets locales una vez que los muevas a assets/images/
const _carouselImages = [
  'assets/images/foto1',
  'assets/images/foto2',
  'assets/images/foto3',
  'assets/images/foto4',
  'assets/images/foto5',
  'assets/images/foto6',
  'assets/images/foto7',
  'assets/images/foto8',
  'assets/images/foto9',
  'assets/images/foto10',
  'assets/images/foto11',
];

// ── Screen ────────────────────────────────────────────────────────────────────
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

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
    Future.delayed(const Duration(seconds: 3), () {
      if (!mounted) return;
      setState(() => _currentImg = (_currentImg + 1) % _carouselImages.length);
      _startCarousel();
    });
  }

  void _goSearch(String? tab) {
    final extra = tab != null ? '?tab=$tab' : '';
    context.go('/buscar$extra');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientHome),
        child: Stack(children: [
          SingleChildScrollView(
            child: Column(children: [
              _buildNavbar(),
              _buildHero(),
              _buildCategories(),
              _buildDestinos(),
              _buildStats(),
              const FooterWidget(),
              const SizedBox(height: 80),
            ]),
          ),
          // AI Assistant flotante
          const Stack(children: [AITravelAssistant()]),
        ]),
      ),
    );
  }

  // ── Navbar ────────────────────────────────────────────────────────────────
  Widget _buildNavbar() {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.15),
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: Colors.white.withOpacity(0.2)),
          ),
          child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            const Text('JourneyMate', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.5)),
            Row(children: [
              if (_userName.isNotEmpty) ...[
                Text('Hola, $_userName', style: const TextStyle(color: Colors.white70, fontSize: 13)),
                const SizedBox(width: 10),
                GestureDetector(
                  onTap: () => context.go('/mis-reservas'),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(border: Border.all(color: Colors.white30), borderRadius: BorderRadius.circular(12)),
                    child: const Text('Mis reservas', style: TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.w700)),
                  ),
                ),
                const SizedBox(width: 8),
              ],
              GestureDetector(
                onTap: () => _goSearch(null),
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12)),
                  child: const Text('Buscar', style: TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w900, fontSize: 12)),
                ),
              ),
              if (_userName.isEmpty) ...[
                const SizedBox(width: 8),
                GestureDetector(
                  onTap: () => context.go('/login'),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                    decoration: BoxDecoration(color: Colors.white.withOpacity(0.15), borderRadius: BorderRadius.circular(12), border: Border.all(color: Colors.white30)),
                    child: const Text('Acceder', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w700, fontSize: 12)),
                  ),
                ),
              ],
            ]),
          ]),
        ),
      ),
    ).animate().fadeIn(duration: 600.ms).slideY(begin: -0.3);
  }

  // ── Hero ──────────────────────────────────────────────────────────────────
  Widget _buildHero() {
    return Container(
      height: 480,
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(borderRadius: BorderRadius.circular(40), color: Colors.black),
      clipBehavior: Clip.antiAlias,
      child: Stack(children: [
        // Carrusel
        AnimatedSwitcher(
          duration: const Duration(milliseconds: 800),
          child: Image.network(
            _carouselImages[_currentImg],
            key: ValueKey(_currentImg),
            fit: BoxFit.cover, width: double.infinity, height: double.infinity,
            errorBuilder: (_, __, ___) => Container(color: AppColors.teal800),
          ),
        ),
        // Overlay
        Container(decoration: BoxDecoration(gradient: LinearGradient(begin: Alignment.topCenter, end: Alignment.bottomCenter, colors: [Colors.transparent, Colors.black.withOpacity(0.7)]))),
        // CTA
        Positioned(bottom: 40, left: 32, right: 32, child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('DESCUBRE EL MUNDO', style: TextStyle(color: Colors.white38, fontSize: 11, fontWeight: FontWeight.w900, letterSpacing: 3)),
            const SizedBox(height: 8),
            const Text('Tu viaje\nperfecto\nte espera', style: TextStyle(color: Colors.white, fontSize: 40, fontWeight: FontWeight.w900, letterSpacing: -1, height: 1.1)),
            const SizedBox(height: 20),
            GestureDetector(
              onTap: () => _goSearch(null),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 16),
                decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(20)),
                child: const Row(mainAxisSize: MainAxisSize.min, children: [
                  Icon(LucideIcons.search, size: 18, color: AppColors.teal900),
                  SizedBox(width: 8),
                  Text('EXPLORAR AHORA', style: TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 2)),
                ]),
              ),
            ),
          ],
        ).animate().fadeIn(duration: 600.ms, delay: 200.ms).slideY(begin: 0.3)),
      ]),
    );
  }

  // ── Categorías ────────────────────────────────────────────────────────────
  Widget _buildCategories() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Padding(
          padding: EdgeInsets.only(left: 4, bottom: 16),
          child: Text('¿QUÉ BUSCAS?', style: TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 3)),
        ),
        GridView.count(
          crossAxisCount: 3, crossAxisSpacing: 12, mainAxisSpacing: 12,
          shrinkWrap: true, physics: const NeverScrollableScrollPhysics(),
          childAspectRatio: 1.1,
          children: _categories.map((c) => GestureDetector(
            onTap: () => _goSearch(c.id),
            child: Container(
              decoration: BoxDecoration(
                gradient: LinearGradient(colors: c.colors, begin: Alignment.topLeft, end: Alignment.bottomRight),
                borderRadius: BorderRadius.circular(24),
                boxShadow: [BoxShadow(color: c.colors.last.withOpacity(0.3), blurRadius: 12, offset: const Offset(0, 4))],
              ),
              child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                Icon(c.icon, color: Colors.white, size: 28),
                const SizedBox(height: 6),
                Text(c.label, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 11, letterSpacing: 0.5)),
                Text(c.desc, style: const TextStyle(color: Colors.white60, fontSize: 8), textAlign: TextAlign.center, maxLines: 2, overflow: TextOverflow.ellipsis),
              ]),
            ),
          )).toList(),
        ),
      ]),
    );
  }

  // ── Destinos ──────────────────────────────────────────────────────────────
  Widget _buildDestinos() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Padding(
          padding: EdgeInsets.only(left: 4, bottom: 16),
          child: Text('DESTINOS POPULARES', style: TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 3)),
        ),
        SizedBox(
          height: 180,
          child: ListView.separated(
            scrollDirection: Axis.horizontal,
            itemCount: _destinos.length,
            separatorBuilder: (_, __) => const SizedBox(width: 12),
            itemBuilder: (_, i) {
              final d = _destinos[i];
              return GestureDetector(
                onTap: () => context.go('/buscar?tab=vuelos&toId=${d['iata']}&destText=${Uri.encodeComponent(d['destText']!)}'),
                child: Container(
                  width: 140,
                  decoration: BoxDecoration(
                    color: Colors.white.withOpacity(0.15),
                    borderRadius: BorderRadius.circular(28),
                    border: Border.all(color: Colors.white.withOpacity(0.2)),
                  ),
                  padding: const EdgeInsets.all(16),
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.end, children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(color: Colors.white.withOpacity(0.2), borderRadius: BorderRadius.circular(10)),
                      child: Text(d['tag']!, style: const TextStyle(color: Colors.white, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1)),
                    ),
                    const SizedBox(height: 8),
                    Text(d['nombre']!, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 16, letterSpacing: -0.3)),
                    Text(d['iata']!, style: const TextStyle(color: Colors.white54, fontWeight: FontWeight.w700, fontSize: 11)),
                  ]),
                ),
              );
            },
          ),
        ),
      ]),
    );
  }

  // ── Stats ─────────────────────────────────────────────────────────────────
  Widget _buildStats() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 32),
      child: GridView.count(
        crossAxisCount: 2, crossAxisSpacing: 12, mainAxisSpacing: 12,
        shrinkWrap: true, physics: const NeverScrollableScrollPhysics(),
        childAspectRatio: 2.5,
        children: _stats.map((s) => Container(
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), borderRadius: BorderRadius.circular(20), border: Border.all(color: Colors.white.withOpacity(0.15))),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.center, children: [
            Text(s['valor']!, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 20, letterSpacing: -0.5)),
            Text(s['label']!, style: const TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.w600)),
          ]),
        )).toList(),
      ),
    );
  }
}