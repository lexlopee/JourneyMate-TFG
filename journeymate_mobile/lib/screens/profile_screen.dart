// lib/screens/profile_screen.dart
//
// Pantalla de perfil — reemplaza el dropdown de usuario del navbar web.
// Muestra info del usuario, ajustes y logout de forma nativa móvil.

import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});
  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  String _userName = '';
  bool _isLoggedIn = false;

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final loggedIn = await AuthService.isLoggedIn();
    final name     = await AuthService.getUserName();
    if (mounted) setState(() { _isLoggedIn = loggedIn; _userName = name ?? ''; });
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
        title: const Text('Cerrar sesión', style: TextStyle(fontWeight: FontWeight.w900, color: AppColors.teal900)),
        content: const Text('¿Seguro que quieres salir?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red, foregroundColor: Colors.white, minimumSize: const Size(80, 40), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12))),
            child: const Text('Salir'),
          ),
        ],
      ),
    );
    if (confirm == true) {
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
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
      child: Column(children: [
        // Avatar
        Container(
          width: 80, height: 80,
          decoration: BoxDecoration(
            color: _isLoggedIn ? AppColors.teal700 : Colors.white.withOpacity(0.2),
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white.withOpacity(0.4), width: 3),
          ),
          child: _isLoggedIn && _userName.isNotEmpty
              ? Center(child: Text(_userName[0].toUpperCase(), style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 32)))
              : const Icon(LucideIcons.user, color: Colors.white, size: 36),
        ),
        const SizedBox(height: 12),
        Text(
          _isLoggedIn ? _userName : 'Invitado',
          style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 22, letterSpacing: -0.3),
        ),
        const SizedBox(height: 4),
        Text(
          _isLoggedIn ? 'Viajero JourneyMate' : 'Inicia sesión para guardar tus reservas',
          style: const TextStyle(color: Colors.white60, fontSize: 12),
        ),
        const SizedBox(height: 16),
        if (!_isLoggedIn) Row(children: [
          Expanded(child: ElevatedButton(
            onPressed: () => Navigator.of(context, rootNavigator: true).pushNamed('/login'),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.white, foregroundColor: AppColors.teal900, minimumSize: const Size(0, 48), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
            child: const Text('INICIAR SESIÓN', style: TextStyle(fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1.5)),
          )),
          const SizedBox(width: 10),
          Expanded(child: OutlinedButton(
            onPressed: () => Navigator.of(context, rootNavigator: true).pushNamed('/register'),
            style: OutlinedButton.styleFrom(foregroundColor: Colors.white, side: const BorderSide(color: Colors.white38), minimumSize: const Size(0, 48), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
            child: const Text('REGISTRO', style: TextStyle(fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1.5)),
          )),
        ]),
      ]),
    );
  }

  Widget _buildMenu() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Container(
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(24)),
        child: Column(children: [
          if (_isLoggedIn) ...[
            _menuItem(LucideIcons.bookOpen, 'Mis Reservas', AppColors.teal600, () => Navigator.of(context, rootNavigator: true).pushNamed('/mis-reservas')),
            const Divider(height: 1, indent: 56),
          ],
          _menuItem(LucideIcons.globe, 'Idioma', AppColors.teal500, () {}),
          const Divider(height: 1, indent: 56),
          _menuItem(LucideIcons.shieldCheck, 'Privacidad', AppColors.teal500, () {}),
          const Divider(height: 1, indent: 56),
          _menuItem(LucideIcons.helpCircle, 'Ayuda', AppColors.teal500, () {}),
          const Divider(height: 1, indent: 56),
          _menuItem(LucideIcons.info, 'Sobre JourneyMate', AppColors.teal400, () {}),
          if (_isLoggedIn) ...[
            const Divider(height: 1),
            _menuItem(LucideIcons.logOut, 'Cerrar sesión', Colors.red, _logout, textColor: Colors.red),
          ],
        ]),
      ),
    );
  }

  Widget _menuItem(IconData icon, String label, Color iconColor, VoidCallback onTap, {Color? textColor}) =>
      ListTile(
        onTap: onTap,
        leading: Container(
          width: 36, height: 36,
          decoration: BoxDecoration(color: iconColor.withOpacity(0.1), borderRadius: BorderRadius.circular(10)),
          child: Icon(icon, size: 18, color: iconColor),
        ),
        title: Text(label, style: TextStyle(fontWeight: FontWeight.w700, fontSize: 14, color: textColor ?? AppColors.teal900)),
        trailing: textColor == null ? const Icon(LucideIcons.chevronRight, size: 16, color: Colors.grey) : null,
      );
}