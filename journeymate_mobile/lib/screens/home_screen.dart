// lib/screens/home_screen.dart
//
// Home rediseñado para móvil:
// - Sin footer web
// - Sin navbar horizontal
// - Cards de destinos deslizables
// - Categorías en grid compacto
// - Pull-to-refresh
// - Imágenes locales (assets/images/)

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../widgets/ai_travel_assistant.dart';

// ── Datos ─────────────────────────────────────────────────────────────────────
class _Category {
  final String id, label;
  final IconData icon;
  final List<Color> colors;
  const _Category(this.id, this.label, this.icon, this.colors);
}

const _categories = [
  _Category('alojamiento', 'Hoteles',      LucideIcons.hotel,   [Color(0xFF2DD4BF), Color(0xFF0D9488)]),
  _Category('vuelos',      'Vuelos',        LucideIcons.plane,   [Color(0xFF38BDF8), Color(0xFF0284C7)]),
  _Category('coches',      'Coches',        LucideIcons.car,     [Color(0xFFFBBF24), Color(0xFFD97706)]),
  _Category('actividades', 'Actividades',   LucideIcons.ticket,  [Color(0xFFA78BFA), Color(0xFF7C3AED)]),
  _Category('cruceros',    'Cruceros',      LucideIcons.ship,    [Color(0xFF60A5FA), Color(0xFF2563EB)]),
  _Category('trenes',      'Trenes',        LucideIcons.train,   [Color(0xFFF87171), Color(0xFFDC2626)]),
];

const _destinos = [
  {'nombre': 'Tokio',       'tag': '🔥 Tendencia', 'image': 'assets/images/fotoTokio.jpg',     'iata': 'TYO'},
  {'nombre': 'Bali',        'tag': '🌴 Relax',     'image': 'assets/images/fotoBali.png',      'iata': 'DPS'},
  {'nombre': 'París',       'tag': '❤️ Romántico', 'image': 'assets/images/fotoParis.jpg',     'iata': 'PAR'},
  {'nombre': 'Dubai',       'tag': '✨ Lujo',       'image': 'assets/images/fotoDubai.jpg',     'iata': 'DXB'},
  {'nombre': 'Nueva York',  'tag': '🏙️ Urbano',    'image': 'assets/images/fotoNuevaYork.jpg', 'iata': 'NYC'},
];

const _stats = [
  {'valor': '2M+',  'label': 'Viajeros'},
  {'valor': '150+', 'label': 'Destinos'},
  {'valor': '24/7', 'label': 'Soporte IA'},
  {'valor': '€0',   'label': 'Comisiones'},
];

// ── Screen ────────────────────────────────────────────────────────────────────
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String _userName = '';

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final name = await AuthService.getUserName();
    if (mounted && name != null) setState(() => _userName = name);
  }

  Future<void> _onRefresh() async {
    await _loadUser();
    await Future.delayed(const Duration(milliseconds: 600));
  }

  void _goSearch(BuildContext context, String tab) {
    // Cambia al tab de búsqueda con la sección preseleccionada
    // (comunicación via callback al AppShell o Navigator)
    Navigator.of(context, rootNavigator: false).pushNamed('/buscar', arguments: tab);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      floatingActionButton: const AITravelAssistant(),
      body: RefreshIndicator(
        color: AppColors.teal600,
        backgroundColor: Colors.white,
        onRefresh: _onRefresh,
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
            const SliverToBoxAdapter(child: SizedBox(height: 100)),
          ],
        ),
      ),
    );
  }

  // ── App bar ──────────────────────────────────────────────────────────────
  Widget _buildAppBar() => SliverAppBar(
    backgroundColor: Colors.transparent,
    elevation: 0,
    floating: true,
    snap: true,
    toolbarHeight: 70,
    flexibleSpace: SafeArea(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.center, children: [
            Text(
              _userName.isNotEmpty ? 'Hola, $_userName 👋' : 'Bienvenido 👋',
              style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 18, letterSpacing: -0.3),
            ),
            const Text('¿A dónde vamos hoy?', style: TextStyle(color: Colors.white70, fontSize: 13)),
          ]),
          // Avatar / Login
          GestureDetector(
            onTap: () => Navigator.of(context, rootNavigator: true).pushNamed('/login'),
            child: Container(
              width: 44, height: 44,
              decoration: BoxDecoration(
                color: _userName.isNotEmpty ? AppColors.teal700 : Colors.white.withOpacity(0.2),
                shape: BoxShape.circle,
                border: Border.all(color: Colors.white.withOpacity(0.4), width: 2),
              ),
              child: _userName.isNotEmpty
                  ? Center(child: Text(_userName[0].toUpperCase(), style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 18)))
                  : const Icon(LucideIcons.user, color: Colors.white, size: 20),
            ),
          ),
        ]),
      ),
    ),
  );

  // ── Hero card buscador rápido ─────────────────────────────────────────────
  Widget _buildHeroCard() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 0, 16, 8),
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          gradient: const LinearGradient(
            colors: [Color(0xFF0D4F4C), Color(0xFF115E59)],
            begin: Alignment.topLeft, end: Alignment.bottomRight,
          ),
          borderRadius: BorderRadius.circular(28),
          boxShadow: [BoxShadow(color: AppColors.teal900.withOpacity(0.4), blurRadius: 20, offset: const Offset(0, 8))],
        ),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Text('TU PRÓXIMA AVENTURA', style: TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 2.5)),
          const SizedBox(height: 6),
          const Text('Descubre el\nmundo contigo', style: TextStyle(color: Colors.white, fontSize: 26, fontWeight: FontWeight.w900, letterSpacing: -0.5, height: 1.1)),
          const SizedBox(height: 16),
          // Barra de búsqueda rápida tipo app
          GestureDetector(
            onTap: () => _goSearch(context, 'alojamiento'),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Row(children: [
                const Icon(LucideIcons.search, size: 18, color: AppColors.teal600),
                const SizedBox(width: 10),
                const Expanded(child: Text('Hoteles, vuelos, coches...', style: TextStyle(color: AppColors.teal400, fontSize: 13, fontWeight: FontWeight.w600))),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                  decoration: BoxDecoration(color: AppColors.teal600, borderRadius: BorderRadius.circular(10)),
                  child: const Text('BUSCAR', style: TextStyle(color: Colors.white, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1.5)),
                ),
              ]),
            ),
          ),
        ]),
      ),
    ).animate().fadeIn(duration: 500.ms).slideY(begin: 0.2);
  }

  // ── Título de sección ────────────────────────────────────────────────────
  Widget _buildSectionTitle(String title) => Padding(
    padding: const EdgeInsets.fromLTRB(16, 20, 16, 12),
    child: Text(title, style: const TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 3)),
  );

  // ── Grid de categorías ───────────────────────────────────────────────────
  Widget _buildCategories() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: GridView.count(
        crossAxisCount: 3,
        crossAxisSpacing: 10, mainAxisSpacing: 10,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        childAspectRatio: 1.15,
        children: _categories.map((c) {
          final isSoon = c.id == 'trenes';
          return GestureDetector(
            onTap: () {
              HapticFeedback.lightImpact();
              if (!isSoon) _goSearch(context, c.id);
            },
            child: AnimatedContainer(
              duration: const Duration(milliseconds: 150),
              decoration: BoxDecoration(
                gradient: LinearGradient(colors: c.colors, begin: Alignment.topLeft, end: Alignment.bottomRight),
                borderRadius: BorderRadius.circular(20),
                boxShadow: [BoxShadow(color: c.colors.last.withOpacity(0.35), blurRadius: 10, offset: const Offset(0, 4))],
              ),
              child: Stack(children: [
                Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Icon(c.icon, color: Colors.white, size: 26),
                  const SizedBox(height: 6),
                  Text(c.label, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 11)),
                ])),
                if (isSoon)
                  Positioned(top: 6, right: 6, child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 2),
                    decoration: BoxDecoration(color: const Color(0xFFFBBF24), borderRadius: BorderRadius.circular(6)),
                    child: const Text('SOON', style: TextStyle(fontSize: 7, fontWeight: FontWeight.w900, color: AppColors.teal950)),
                  )),
              ]),
            ),
          );
        }).toList(),
      ),
    );
  }

  // ── Destinos horizontales ────────────────────────────────────────────────
  Widget _buildDestinos() {
    return SizedBox(
      height: 170,
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
              _goSearch(context, 'vuelos');
            },
            child: Container(
              width: 130,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(24),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.15), blurRadius: 12, offset: const Offset(0, 4))],
              ),
              clipBehavior: Clip.antiAlias,
              child: Stack(children: [
                // Imagen
                Positioned.fill(child: Image.asset(
                  d['image']!,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => Container(
                    decoration: const BoxDecoration(gradient: AppColors.gradientMain),
                    child: const Center(child: Icon(LucideIcons.mapPin, color: Colors.white, size: 32)),
                  ),
                )),
                // Gradiente oscuro abajo
                Positioned.fill(child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter, end: Alignment.bottomCenter,
                      colors: [Colors.transparent, Colors.black.withOpacity(0.7)],
                    ),
                  ),
                )),
                // Texto
                Positioned(bottom: 12, left: 12, right: 12, child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 3),
                      decoration: BoxDecoration(color: Colors.white.withOpacity(0.2), borderRadius: BorderRadius.circular(8)),
                      child: Text(d['tag']!, style: const TextStyle(color: Colors.white, fontSize: 8, fontWeight: FontWeight.w900)),
                    ),
                    const SizedBox(height: 4),
                    Text(d['nombre']!, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 15, letterSpacing: -0.3)),
                  ],
                )),
              ]),
            ),
          ).animate(delay: (i * 80).ms).fadeIn(duration: 400.ms).slideX(begin: 0.2);
        },
      ),
    );
  }

  // ── Stats row ────────────────────────────────────────────────────────────
  Widget _buildStats() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 20),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.15),
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: Colors.white.withOpacity(0.2)),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: _stats.map((s) => Column(children: [
            Text(s['valor']!, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 20, letterSpacing: -0.5)),
            Text(s['label']!, style: const TextStyle(color: Colors.white60, fontSize: 10, fontWeight: FontWeight.w600)),
          ])).toList(),
        ),
      ),
    );
  }
}