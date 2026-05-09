// lib/screens/my_bookings_screen.dart
//
// Pantalla "Mis Reservas" con diseño nativo móvil.
// Cards deslizables con estado de la reserva.

import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

class MyBookingsScreen extends StatefulWidget {
  const MyBookingsScreen({super.key});
  @override
  State<MyBookingsScreen> createState() => _MyBookingsScreenState();
}

class _MyBookingsScreenState extends State<MyBookingsScreen> {
  bool _loading = true;
  bool _isLoggedIn = false;
  List<dynamic> _reservas = [];
  String _error = '';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() { _loading = true; _error = ''; });
    final loggedIn = await AuthService.isLoggedIn();
    if (!loggedIn) {
      if (mounted) setState(() { _isLoggedIn = false; _loading = false; });
      return;
    }
    setState(() => _isLoggedIn = true);
    try {
      final id = await AuthService.getIdUsuario();
      final data = await api.get('/reservas/usuario/$id');
      if (mounted) setState(() { _reservas = data is List ? data : []; _loading = false; });
    } catch (e) {
      if (mounted) setState(() { _error = 'No se pudieron cargar las reservas'; _loading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: SafeArea(
          child: Column(children: [
            // Header
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
              child: Row(children: [
                const Text('MIS RESERVAS', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 22, letterSpacing: -0.3)),
                const Spacer(),
                if (_isLoggedIn && !_loading)
                  GestureDetector(
                    onTap: _load,
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(color: Colors.white.withOpacity(0.2), shape: BoxShape.circle),
                      child: const Icon(LucideIcons.refreshCw, color: Colors.white, size: 16),
                    ),
                  ),
              ]),
            ),

            Expanded(child: _buildBody()),
          ]),
        ),
      ),
    );
  }

  Widget _buildBody() {
    if (!_isLoggedIn) return _buildNotLoggedIn();
    if (_loading) return const Center(child: CircularProgressIndicator(color: Colors.white));
    if (_error.isNotEmpty) return _buildError();
    if (_reservas.isEmpty) return _buildEmpty();
    return _buildList();
  }

  Widget _buildNotLoggedIn() => Center(child: Padding(
    padding: const EdgeInsets.all(32),
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Container(
        width: 80, height: 80,
        decoration: BoxDecoration(color: Colors.white.withOpacity(0.15), shape: BoxShape.circle),
        child: const Icon(LucideIcons.lock, color: Colors.white, size: 36),
      ),
      const SizedBox(height: 20),
      const Text('Inicia sesión', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 20)),
      const SizedBox(height: 8),
      const Text('Necesitas cuenta para ver tus reservas', style: TextStyle(color: Colors.white60, fontSize: 13), textAlign: TextAlign.center),
      const SizedBox(height: 24),
      ElevatedButton(
        onPressed: () => Navigator.of(context, rootNavigator: true).pushNamed('/login'),
        style: ElevatedButton.styleFrom(backgroundColor: Colors.white, foregroundColor: AppColors.teal900, minimumSize: const Size(200, 50), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
        child: const Text('INICIAR SESIÓN', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
      ),
    ]),
  ));

  Widget _buildEmpty() => Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
    const Icon(LucideIcons.bookOpen, size: 64, color: Colors.white30),
    const SizedBox(height: 16),
    const Text('Sin reservas aún', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 18)),
    const SizedBox(height: 8),
    const Text('¡Empieza a explorar y reserva tu próximo viaje!', style: TextStyle(color: Colors.white60, fontSize: 13), textAlign: TextAlign.center),
  ]));

  Widget _buildError() => Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
    const Icon(LucideIcons.alertCircle, size: 48, color: Colors.white60),
    const SizedBox(height: 12),
    Text(_error, style: const TextStyle(color: Colors.white70, fontSize: 13), textAlign: TextAlign.center),
    const SizedBox(height: 16),
    TextButton(onPressed: _load, child: const Text('Reintentar', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900))),
  ]));

  Widget _buildList() => ListView.separated(
    padding: const EdgeInsets.fromLTRB(16, 8, 16, 100),
    physics: const BouncingScrollPhysics(),
    itemCount: _reservas.length,
    separatorBuilder: (_, __) => const SizedBox(height: 12),
    itemBuilder: (_, i) => _BookingCard(reserva: _reservas[i]),
  );
}

// ── Card de reserva ───────────────────────────────────────────────────────────
class _BookingCard extends StatelessWidget {
  final Map<String, dynamic> reserva;
  const _BookingCard({required this.reserva});

  Color _statusColor(String? estado) {
    switch (estado?.toUpperCase()) {
      case 'CONFIRMADO': case 'CONFIRMED': return AppColors.teal500;
      case 'PENDIENTE':  case 'PENDING':   return const Color(0xFFFBBF24);
      case 'CANCELADO':  case 'CANCELLED': return Colors.redAccent;
      default: return Colors.grey;
    }
  }

  IconData _typeIcon(String? tipo) {
    switch (tipo?.toUpperCase()) {
      case 'HOTEL':    return LucideIcons.hotel;
      case 'VUELO':    return LucideIcons.plane;
      case 'COCHE':    return LucideIcons.car;
      case 'ACTIVIDAD':return LucideIcons.ticket;
      case 'CRUCERO':  return LucideIcons.ship;
      default:         return LucideIcons.bookOpen;
    }
  }

  @override
  Widget build(BuildContext context) {
    final servicio = reserva['servicio'] as Map<String, dynamic>? ?? {};
    final estado   = reserva['estado']?.toString() ?? '';
    final tipo     = servicio['tipo']?.toString() ?? '';
    final nombre   = servicio['nombre']?.toString() ?? 'Reserva';
    final precio   = reserva['precioTotal'];
    final fecha    = reserva['fechaServicio']?.toString()?.substring(0, 10) ?? '';
    final color    = _statusColor(estado);

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 12)],
      ),
      child: Column(children: [
        // Header colored
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
          ),
          child: Row(children: [
            Container(
              width: 36, height: 36,
              decoration: BoxDecoration(color: color.withOpacity(0.15), borderRadius: BorderRadius.circular(10)),
              child: Icon(_typeIcon(tipo), size: 18, color: color),
            ),
            const SizedBox(width: 10),
            Expanded(child: Text(nombre, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 14, color: AppColors.teal900), overflow: TextOverflow.ellipsis)),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(color: color.withOpacity(0.15), borderRadius: BorderRadius.circular(8)),
              child: Text(estado.toUpperCase(), style: TextStyle(color: color, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1)),
            ),
          ]),
        ),
        // Body
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(children: [
            if (fecha.isNotEmpty) _info(LucideIcons.calendar, fecha),
            const SizedBox(width: 16),
            _info(LucideIcons.tag, precio != null ? '$precio €' : '—'),
            const Spacer(),
            Text('#${reserva['idReserva'] ?? ''}', style: const TextStyle(color: Colors.grey, fontSize: 11, fontWeight: FontWeight.w600)),
          ]),
        ),
      ]),
    );
  }

  Widget _info(IconData icon, String text) => Row(mainAxisSize: MainAxisSize.min, children: [
    Icon(icon, size: 13, color: AppColors.teal400),
    const SizedBox(width: 4),
    Text(text, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal700)),
  ]);
}