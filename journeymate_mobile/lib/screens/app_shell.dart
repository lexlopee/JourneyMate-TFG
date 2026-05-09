// lib/screens/app_shell.dart
//
// FIX: pasa el callback onChangeTab al HomeScreen para que las categorías
// y destinos puedan cambiar el tab activo del Shell sin usar go_router.

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import 'home_screen.dart';
import 'search_screen.dart';
import 'my_bookings_screen.dart';
import 'profile_screen.dart';

class AppShell extends StatefulWidget {
  final int initialIndex;
  final String? initialSection; // sección para pre-seleccionar en SearchScreen
  const AppShell({super.key, this.initialIndex = 0, this.initialSection});

  @override
  State<AppShell> createState() => _AppShellState();
}

class _AppShellState extends State<AppShell> {
  late int _currentIndex;
  String? _pendingSection; // sección que SearchScreen debe pre-seleccionar

  @override
  void initState() {
    super.initState();
    _currentIndex   = widget.initialIndex;
    _pendingSection = widget.initialSection;
  }

  // FIX: callback que HomeScreen llama al pulsar categorías o destinos
  void _changeTab(int index, {String? section}) {
    HapticFeedback.selectionClick();
    setState(() {
      _currentIndex   = index;
      _pendingSection = section;
    });
  }

  // Intercepta el botón atrás del dispositivo
  Future<bool> _onWillPop() async {
    if (_currentIndex != 0) {
      setState(() { _currentIndex = 0; _pendingSection = null; });
      return false;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: Container(
          decoration: const BoxDecoration(gradient: AppColors.gradientMain),
          child: IndexedStack(
            index: _currentIndex,
            children: [
              // Tab 0 — Home: recibe el callback para cambiar de tab
              HomeScreen(onChangeTab: _changeTab),

              // Tab 1 — Búsqueda: recibe la sección pendiente
              SearchScreen(initialTab: _pendingSection),

              // Tab 2 — Mis Reservas
              const MyBookingsScreen(),

              // Tab 3 — Perfil
              const ProfileScreen(),
            ],
          ),
        ),
        bottomNavigationBar: _buildBottomNav(),
      ),
    );
  }

  Widget _buildBottomNav() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(28)),
        boxShadow: [
          BoxShadow(
            color: AppColors.teal900.withOpacity(0.12),
            blurRadius: 24,
            offset: const Offset(0, -8),
          ),
        ],
      ),
      child: SafeArea(
        top: false,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _NavItem(icon: LucideIcons.home,     label: 'Inicio',   index: 0, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.search,   label: 'Buscar',   index: 1, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.bookOpen, label: 'Reservas', index: 2, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.user,     label: 'Perfil',   index: 3, current: _currentIndex, onTap: _onTap),
            ],
          ),
        ),
      ),
    );
  }

  void _onTap(int index) {
    HapticFeedback.selectionClick();
    if (index == _currentIndex) return; // ya estamos aquí
    setState(() {
      _currentIndex   = index;
      _pendingSection = null; // limpiar sección al navegar manualmente
    });
  }
}

// ── Item del nav bar ──────────────────────────────────────────────────────────
class _NavItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final int index, current;
  final void Function(int) onTap;

  const _NavItem({
    required this.icon, required this.label,
    required this.index, required this.current, required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final active = index == current;
    return GestureDetector(
      onTap: () => onTap(index),
      behavior: HitTestBehavior.opaque,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 220),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: active ? AppColors.teal900 : Colors.transparent,
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 20, color: active ? Colors.white : AppColors.teal400),
            if (active) ...[
              const SizedBox(width: 6),
              Text(label, style: const TextStyle(color: Colors.white,
                  fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 0.5)),
            ],
          ],
        ),
      ),
    );
  }
}