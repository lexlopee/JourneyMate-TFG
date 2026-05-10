// lib/screens/profile_screen.dart
//
// FIX: Todas las navegaciones usan context.go() de go_router
// NO pushNamed (que busca un Navigator local sin rutas registradas)

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});
  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  String _userName  = '';
  String _userEmail = '';
  bool   _isLoggedIn = false;

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final loggedIn = await AuthService.isLoggedIn();
    final name     = await AuthService.getUserName();
    if (mounted) {
      setState(() {
      _isLoggedIn = loggedIn;
      _userName   = name ?? '';
    });
    }
  }

  Future<void> _logout() async {
    final ok = await showModalBottomSheet<bool>(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(28)),
        padding: const EdgeInsets.fromLTRB(24, 20, 24, 32),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(width: 40, height: 4, margin: const EdgeInsets.only(bottom: 20),
              decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),
          const Icon(LucideIcons.logOut, size: 40, color: Colors.redAccent),
          const SizedBox(height: 12),
          const Text('Cerrar sesión', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900)),
          const SizedBox(height: 6),
          const Text('¿Seguro que quieres salir?', style: TextStyle(color: Colors.grey, fontSize: 13)),
          const SizedBox(height: 24),
          Row(children: [
            Expanded(child: OutlinedButton(
              onPressed: () => Navigator.pop(context, false),
              style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14))),
              child: const Text('Cancelar', style: TextStyle(fontWeight: FontWeight.w700)),
            )),
            const SizedBox(width: 12),
            Expanded(child: ElevatedButton(
              onPressed: () => Navigator.pop(context, true),
              style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.redAccent, foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  minimumSize: const Size(0, 0),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14))),
              child: const Text('Salir', style: TextStyle(fontWeight: FontWeight.w900)),
            )),
          ]),
        ]),
      ),
    );

    if (ok == true) {
      await AuthService.logout();
      if (mounted) setState(() { _isLoggedIn = false; _userName = ''; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: SafeArea(
          child: CustomScrollView(
            physics: const BouncingScrollPhysics(),
            slivers: [
              SliverToBoxAdapter(child: _buildHeader()),
              SliverToBoxAdapter(child: _buildMenu()),
              const SliverToBoxAdapter(child: SizedBox(height: 100)),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() => Padding(
    padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
    child: Column(children: [
      // Avatar
      Container(
        width: 88, height: 88,
        decoration: BoxDecoration(
          color: _isLoggedIn ? AppColors.teal700 : Colors.white.withOpacity(0.2),
          shape: BoxShape.circle,
          border: Border.all(color: Colors.white.withOpacity(0.4), width: 3),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.1), blurRadius: 12)],
        ),
        child: _isLoggedIn && _userName.isNotEmpty
            ? Center(child: Text(_userName[0].toUpperCase(),
            style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 34)))
            : const Icon(LucideIcons.user, color: Colors.white, size: 40),
      ),
      const SizedBox(height: 14),
      Text(
        _isLoggedIn ? _userName : 'Invitado',
        style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900,
            fontSize: 22, letterSpacing: -0.3),
      ),
      const SizedBox(height: 4),
      Text(
        _isLoggedIn ? 'Viajero JourneyMate' : 'Inicia sesión para ver tus reservas',
        style: const TextStyle(color: Colors.white60, fontSize: 12),
      ),
      const SizedBox(height: 20),

      // Botones de login/registro si no está logueado
      if (!_isLoggedIn)
        Row(children: [
          Expanded(child: ElevatedButton(
            // FIX: context.go en vez de pushNamed
            onPressed: () => context.go('/login'),
            style: ElevatedButton.styleFrom(
                backgroundColor: Colors.white, foregroundColor: AppColors.teal900,
                minimumSize: const Size(0, 48),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
            child: const Text('INICIAR SESIÓN',
                style: TextStyle(fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1.5)),
          )),
          const SizedBox(width: 10),
          Expanded(child: OutlinedButton(
            // FIX: context.go en vez de pushNamed
            onPressed: () => context.go('/register'),
            style: OutlinedButton.styleFrom(
                foregroundColor: Colors.white,
                side: const BorderSide(color: Colors.white38),
                minimumSize: const Size(0, 48),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
            child: const Text('REGISTRO',
                style: TextStyle(fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1.5)),
          )),
        ]),
    ]),
  );

  Widget _buildMenu() => Padding(
    padding: const EdgeInsets.fromLTRB(16, 20, 16, 0),
    child: Container(
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(24),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 12)]),
      child: Column(children: [
        if (_isLoggedIn) ...[
          _menuTile(
            icon: LucideIcons.bookOpen, label: 'Mis Reservas',
            color: AppColors.teal600,
            // FIX: context.go en vez de pushNamed
            onTap: () => context.go('/mis-reservas'),
          ),
          const _Divider(),
        ],
        _menuTile(icon: LucideIcons.globe,       label: 'Idioma',               color: AppColors.teal500, onTap: () {}),
        const _Divider(),
        _menuTile(icon: LucideIcons.shieldCheck, label: 'Privacidad',           color: AppColors.teal500, onTap: () {}),
        const _Divider(),
        _menuTile(icon: LucideIcons.helpCircle,  label: 'Ayuda',                color: AppColors.teal500, onTap: () {}),
        const _Divider(),
        _menuTile(icon: LucideIcons.info,        label: 'Sobre JourneyMate',    color: AppColors.teal400, onTap: () {}),
        if (_isLoggedIn) ...[
          const Divider(height: 1, indent: 16, endIndent: 16),
          _menuTile(
            icon: LucideIcons.logOut, label: 'Cerrar sesión',
            color: Colors.redAccent, textColor: Colors.redAccent,
            showArrow: false,
            onTap: _logout,
          ),
        ],
      ]),
    ),
  );

  Widget _menuTile({
    required IconData icon, required String label, required Color color,
    required VoidCallback onTap, Color? textColor, bool showArrow = true,
  }) => ListTile(
    onTap: onTap,
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 2),
    leading: Container(
      width: 38, height: 38,
      decoration: BoxDecoration(color: color.withOpacity(0.1), borderRadius: BorderRadius.circular(10)),
      child: Icon(icon, size: 18, color: color),
    ),
    title: Text(label, style: TextStyle(fontWeight: FontWeight.w700, fontSize: 14,
        color: textColor ?? AppColors.teal900)),
    trailing: showArrow
        ? const Icon(LucideIcons.chevronRight, size: 16, color: Colors.grey)
        : null,
  );
}

class _Divider extends StatelessWidget {
  const _Divider();
  @override
  Widget build(BuildContext context) =>
      const Divider(height: 1, indent: 16, endIndent: 16, color: Color(0xFFF3F4F6));
}