// lib/screens/app_shell.dart
//
// AppShell — estructura principal de la app móvil.
// Reemplaza el navbar web + footer por una BottomNavigationBar nativa.
// Cada tab mantiene su propio NavigatorKey para que el back button funcione.

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
  const AppShell({super.key, this.initialIndex = 0});

  @override
  State<AppShell> createState() => _AppShellState();
}

class _AppShellState extends State<AppShell> {
  late int _currentIndex;

  // Cada tab tiene su propio GlobalKey para mantener el estado
  final _keys = [
    GlobalKey<NavigatorState>(),
    GlobalKey<NavigatorState>(),
    GlobalKey<NavigatorState>(),
    GlobalKey<NavigatorState>(),
  ];

  @override
  void initState() {
    super.initState();
    _currentIndex = widget.initialIndex;
  }

  // Intercepta el botón atrás del dispositivo para navegar dentro del tab
  Future<bool> _onWillPop() async {
    final nav = _keys[_currentIndex].currentState;
    if (nav != null && nav.canPop()) {
      nav.pop();
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
              _TabNavigator(navigatorKey: _keys[0], child: const HomeScreen()),
              _TabNavigator(navigatorKey: _keys[1], child: const SearchScreen()),
              _TabNavigator(navigatorKey: _keys[2], child: const MyBookingsScreen()),
              _TabNavigator(navigatorKey: _keys[3], child: const ProfileScreen()),
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
              _NavItem(icon: LucideIcons.home,     label: 'Inicio',    index: 0, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.search,   label: 'Buscar',    index: 1, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.bookOpen, label: 'Reservas',  index: 2, current: _currentIndex, onTap: _onTap),
              _NavItem(icon: LucideIcons.user,     label: 'Perfil',    index: 3, current: _currentIndex, onTap: _onTap),
            ],
          ),
        ),
      ),
    );
  }

  void _onTap(int index) {
    HapticFeedback.selectionClick(); // vibración táctil al cambiar tab
    if (index == _currentIndex) {
      // Si pulsa el tab activo, hace pop hasta la raíz
      _keys[index].currentState?.popUntil((r) => r.isFirst);
    } else {
      setState(() => _currentIndex = index);
    }
  }
}

// ── Item del nav bar ──────────────────────────────────────────────────────────
class _NavItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final int index;
  final int current;
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
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
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
              Text(label, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 0.5)),
            ],
          ],
        ),
      ),
    );
  }
}

// ── Navigator por tab ────────────────────────────────────────────────────────
class _TabNavigator extends StatelessWidget {
  final GlobalKey<NavigatorState> navigatorKey;
  final Widget child;
  const _TabNavigator({required this.navigatorKey, required this.child});

  @override
  Widget build(BuildContext context) => Navigator(
    key: navigatorKey,
    onGenerateRoute: (_) => MaterialPageRoute(builder: (_) => child),
  );
}