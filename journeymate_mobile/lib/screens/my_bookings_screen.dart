// lib/screens/my_bookings_screen.dart
//
// Equivalente completo a Misreservas.tsx
// FIXES:
// 1. ✅ Navegación a /login usa go_router (context.go) en vez de pushNamed
// 2. ✅ Tres tabs: Pendientes | Confirmadas | Historial
// 3. ✅ Cancelar reserva con validación de fecha
// 4. ✅ Eliminar reserva pendiente
// 5. ✅ Pull-to-refresh
// 6. ✅ Diseño nativo móvil

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

// ── Modelo ────────────────────────────────────────────────────────────────────
class Reserva {
  final int    idReserva;
  final String servicioNombre;
  final double precioTotal;
  String       estadoNombre;       // mutable para actualización local
  final String tipoReservaNombre;
  final String fechaReserva;       // YYYY-MM-DD
  final int?   idServicio;
  final int?   idTipoReserva;

  Reserva({
    required this.idReserva,
    required this.servicioNombre,
    required this.precioTotal,
    required this.estadoNombre,
    required this.tipoReservaNombre,
    required this.fechaReserva,
    this.idServicio,
    this.idTipoReserva,
  });

  factory Reserva.fromJson(Map<String, dynamic> j) => Reserva(
    idReserva:        (j['idReserva']        ?? 0)  as int,
    servicioNombre:   (j['servicioNombre']   ?? '').toString(),
    precioTotal:      double.tryParse((j['precioTotal'] ?? 0).toString()) ?? 0,
    estadoNombre:     (j['estadoNombre']     ?? '').toString(),
    tipoReservaNombre:(j['tipoReservaNombre']?? '').toString(),
    fechaReserva:     (j['fechaReserva']     ?? '').toString(),
    idServicio:       j['idServicio']   as int?,
    idTipoReserva:    j['idTipoReserva'] as int?,
  );
}

// ── Helpers ───────────────────────────────────────────────────────────────────
bool _puedeCancel(String fecha) {
  if (fecha.isEmpty) return false;
  try {
    final parts = fecha.split('-');
    final inicio = DateTime(int.parse(parts[0]), int.parse(parts[1]), int.parse(parts[2]));
    return DateTime.now().isBefore(inicio);
  } catch (_) { return false; }
}

bool _estaExpirada(String fecha) {
  if (fecha.isEmpty) return false;
  try {
    final parts = fecha.split('-');
    final inicio = DateTime(int.parse(parts[0]), int.parse(parts[1]), int.parse(parts[2]));
    return DateTime.now().isAfter(inicio) || DateTime.now().isAtSameMomentAs(inicio);
  } catch (_) { return false; }
}

String _formatFecha(String fecha) {
  if (fecha.isEmpty) return '—';
  try {
    final p = fecha.split('-');
    return '${p[2]}/${p[1]}/${p[0]}';
  } catch (_) { return fecha; }
}

IconData _tipoIcon(String tipo) {
  final t = tipo.toLowerCase();
  if (t.contains('hotel'))    return LucideIcons.hotel;
  if (t.contains('vuelo'))    return LucideIcons.plane;
  if (t.contains('coche') || t.contains('vtc')) return LucideIcons.car;
  if (t.contains('crucero'))  return LucideIcons.ship;
  if (t.contains('tren'))     return LucideIcons.train;
  if (t.contains('actividad'))return LucideIcons.ticket;
  return LucideIcons.bookOpen;
}

Color _estadoColor(String estado) {
  final e = estado.toLowerCase();
  if (e.contains('confirm'))  return const Color(0xFF059669);
  if (e.contains('pendient')) return const Color(0xFFD97706);
  if (e.contains('cancel'))   return const Color(0xFFDC2626);
  if (e.contains('complet'))  return AppColors.teal600;
  return Colors.grey;
}

Color _estadoBg(String estado) {
  final e = estado.toLowerCase();
  if (e.contains('confirm'))  return const Color(0xFFD1FAE5);
  if (e.contains('pendient')) return const Color(0xFFFEF3C7);
  if (e.contains('cancel'))   return const Color(0xFFFEE2E2);
  if (e.contains('complet'))  return AppColors.teal100;
  return const Color(0xFFF3F4F6);
}

// ── Screen ────────────────────────────────────────────────────────────────────
class MyBookingsScreen extends StatefulWidget {
  const MyBookingsScreen({super.key});
  @override
  State<MyBookingsScreen> createState() => _MyBookingsScreenState();
}

class _MyBookingsScreenState extends State<MyBookingsScreen>
    with SingleTickerProviderStateMixin {

  late TabController _tabCtrl;

  List<Reserva> _pendientes  = [];
  List<Reserva> _confirmadas = [];
  List<Reserva> _historial   = [];

  bool   _loading  = true;
  bool   _isLogged = false;
  String _error    = '';
  String _userName = '';

  @override
  void initState() {
    super.initState();
    _tabCtrl = TabController(length: 3, vsync: this);
    _init();
  }

  @override
  void dispose() {
    _tabCtrl.dispose();
    super.dispose();
  }

  Future<void> _init() async {
    final logged = await AuthService.isLoggedIn();
    if (!logged) {
      if (mounted) setState(() { _isLogged = false; _loading = false; });
      return;
    }
    final name = await AuthService.getUserName();
    if (mounted) setState(() { _isLogged = true; _userName = name ?? ''; });
    await _cargarReservas();
  }

  Future<void> _cargarReservas() async {
    setState(() { _loading = true; _error = ''; });
    try {
      final id = await AuthService.getIdUsuario();
      if (id == null) throw Exception('Sin ID de usuario');

      // Pendientes
      final rawPend = await api.get('/reservas/usuario/$id') as List? ?? [];
      final pend = rawPend.map((e) => Reserva.fromJson(e as Map<String, dynamic>)).toList();

      // Historial completo
      final rawHist = await api.get('/reservas/usuario/$id/historial') as List? ?? [];
      final todas   = rawHist.map((e) => Reserva.fromJson(e as Map<String, dynamic>)).toList();

      // Auto-completar en backend las confirmadas expiradas
      for (final r in todas) {
        if (r.estadoNombre.toLowerCase() == 'confirmada' && _estaExpirada(r.fechaReserva)) {
          _patchEstado(r.idReserva, 'COMPLETADA').catchError((_) {});
          r.estadoNombre = 'COMPLETADA';
        }
      }

      if (mounted) setState(() {
        _pendientes  = pend.where((r) => r.estadoNombre.toLowerCase() == 'pendiente').toList();
        _confirmadas = todas.where((r) =>
        r.estadoNombre.toLowerCase() == 'confirmada' && !_estaExpirada(r.fechaReserva)).toList();
        _historial   = todas.where((r) {
          final e = r.estadoNombre.toLowerCase();
          return e == 'completada' || e == 'cancelada';
        }).toList();
        _loading = false;
      });
    } on ApiException catch (e) {
      if (mounted) setState(() { _error = 'Error ${e.statusCode}. Inténtalo de nuevo.'; _loading = false; });
    } catch (e) {
      if (mounted) setState(() { _error = 'No se pudieron cargar tus reservas.'; _loading = false; });
    }
  }

  Future<void> _patchEstado(int id, String estado) async {
    await api.post('/reservas/$id/estado', {'estado': estado});
  }

  // ── Cancelar ──────────────────────────────────────────────────────────────
  Future<void> _cancelar(Reserva r) async {
    if (!_puedeCancel(r.fechaReserva)) {
      _showSnack('No se puede cancelar: el día de la reserva ya ha llegado.', error: true);
      return;
    }

    final ok = await _confirm(
      title:   'Cancelar reserva',
      message: '¿Seguro que quieres cancelar "${r.servicioNombre}"?\nEl reembolso se procesará en 3-5 días hábiles.',
      confirmLabel: 'Cancelar reserva',
      danger: true,
    );
    if (!ok) return;

    try {
      await _patchEstado(r.idReserva, 'CANCELADA');
      _showSnack('🔄 Reserva cancelada. Reembolso en 3-5 días hábiles.');
      await _cargarReservas();
      _tabCtrl.animateTo(2); // ir a historial
    } catch (_) {
      _showSnack('No se pudo cancelar la reserva.', error: true);
    }
  }

  // ── Eliminar (solo pendiente) ─────────────────────────────────────────────
  Future<void> _eliminar(int idReserva) async {
    final ok = await _confirm(
      title: 'Eliminar reserva',
      message: '¿Eliminar esta reserva pendiente? Esta acción no se puede deshacer.',
      confirmLabel: 'Eliminar',
      danger: true,
    );
    if (!ok) return;

    try {
      await api.post('/reservas/$idReserva/eliminar', {});
      setState(() => _pendientes.removeWhere((r) => r.idReserva == idReserva));
      _showSnack('Reserva eliminada.');
    } catch (_) {
      _showSnack('No se pudo eliminar la reserva.', error: true);
    }
  }

  // ── UI helpers ────────────────────────────────────────────────────────────
  void _showSnack(String msg, {bool error = false}) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(msg, style: const TextStyle(fontWeight: FontWeight.w700)),
      backgroundColor: error ? const Color(0xFFDC2626) : AppColors.teal700,
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      margin: const EdgeInsets.all(16),
    ));
  }

  Future<bool> _confirm({required String title, required String message,
    required String confirmLabel, bool danger = false}) async {
    final result = await showModalBottomSheet<bool>(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(28)),
        padding: const EdgeInsets.all(24),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(width: 40, height: 4, margin: const EdgeInsets.only(bottom: 20),
              decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),
          Icon(danger ? LucideIcons.alertTriangle : LucideIcons.helpCircle,
              size: 40, color: danger ? const Color(0xFFDC2626) : AppColors.teal600),
          const SizedBox(height: 12),
          Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900)),
          const SizedBox(height: 8),
          Text(message, style: const TextStyle(fontSize: 13, color: Colors.grey), textAlign: TextAlign.center),
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
                backgroundColor: danger ? const Color(0xFFDC2626) : AppColors.teal600,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 14),
                minimumSize: const Size(0, 0),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
              ),
              child: Text(confirmLabel, style: const TextStyle(fontWeight: FontWeight.w900)),
            )),
          ]),
          const SizedBox(height: 8),
        ]),
      ),
    );
    return result == true;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: SafeArea(
          child: Column(children: [
            _buildHeader(),
            if (_isLogged) _buildTabBar(),
            Expanded(child: _buildBody()),
          ]),
        ),
      ),
    );
  }

  // ── Header ────────────────────────────────────────────────────────────────
  Widget _buildHeader() => Padding(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 8),
    child: Row(children: [
      const Expanded(
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text('MIS RESERVAS', style: TextStyle(color: Colors.white,
              fontWeight: FontWeight.w900, fontSize: 22, letterSpacing: -0.3)),
          Text('Gestiona tus viajes', style: TextStyle(color: Colors.white60, fontSize: 12)),
        ]),
      ),
      if (_isLogged && !_loading)
        GestureDetector(
          onTap: _cargarReservas,
          child: Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(color: Colors.white.withOpacity(0.2), shape: BoxShape.circle),
            child: const Icon(LucideIcons.refreshCw, color: Colors.white, size: 18),
          ),
        ),
    ]),
  );

  // ── TabBar ────────────────────────────────────────────────────────────────
  Widget _buildTabBar() => Padding(
    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    child: Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.15),
        borderRadius: BorderRadius.circular(20),
      ),
      child: TabBar(
        controller: _tabCtrl,
        indicator: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        indicatorSize: TabBarIndicatorSize.tab,
        dividerColor: Colors.transparent,
        labelColor: AppColors.teal900,
        unselectedLabelColor: Colors.white70,
        labelStyle: const TextStyle(fontWeight: FontWeight.w900, fontSize: 11, letterSpacing: 0.5),
        tabs: [
          Tab(text: 'PENDIENTES${_pendientes.isNotEmpty ? ' (${_pendientes.length})' : ''}'),
          Tab(text: 'CONFIRMADAS${_confirmadas.isNotEmpty ? ' (${_confirmadas.length})' : ''}'),
          const Tab(text: 'HISTORIAL'),
        ],
      ),
    ),
  );

  // ── Body ─────────────────────────────────────────────────────────────────
  Widget _buildBody() {
    if (!_isLogged) return _buildNotLogged();
    if (_loading)   return const Center(child: CircularProgressIndicator(color: Colors.white));
    if (_error.isNotEmpty) return _buildError();

    return TabBarView(
      controller: _tabCtrl,
      children: [
        _buildList(_pendientes,  emptyMsg: 'Sin reservas pendientes 🎉', showPay: true,  showDelete: true),
        _buildList(_confirmadas, emptyMsg: 'No tienes reservas confirmadas aún.', showCancel: true),
        _buildList(_historial,   emptyMsg: 'Tu historial está vacío.'),
      ],
    );
  }

  Widget _buildList(List<Reserva> items, {
    required String emptyMsg,
    bool showPay    = false,
    bool showCancel = false,
    bool showDelete = false,
  }) {
    if (items.isEmpty) return _buildEmpty(emptyMsg);

    return RefreshIndicator(
      color: AppColors.teal600,
      backgroundColor: Colors.white,
      onRefresh: _cargarReservas,
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 100),
        physics: const BouncingScrollPhysics(),
        itemCount: items.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (_, i) => _ReservaCard(
          reserva: items[i],
          showPay: showPay,
          showCancel: showCancel && _puedeCancel(items[i].fechaReserva),
          showDelete: showDelete,
          onCancel:   () => _cancelar(items[i]),
          onDelete:   () => _eliminar(items[i].idReserva),
        ),
      ),
    );
  }

  // ── Estados vacíos ────────────────────────────────────────────────────────
  Widget _buildNotLogged() => Center(child: Padding(
    padding: const EdgeInsets.all(32),
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Container(width: 80, height: 80,
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.15), shape: BoxShape.circle),
          child: const Icon(LucideIcons.lock, color: Colors.white, size: 36)),
      const SizedBox(height: 20),
      const Text('Inicia sesión', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 20)),
      const SizedBox(height: 8),
      const Text('Necesitas cuenta para ver tus reservas',
          style: TextStyle(color: Colors.white60, fontSize: 13), textAlign: TextAlign.center),
      const SizedBox(height: 24),
      ElevatedButton(
        // FIX: go_router en vez de pushNamed
        onPressed: () => context.go('/login'),
        style: ElevatedButton.styleFrom(
            backgroundColor: Colors.white, foregroundColor: AppColors.teal900,
            minimumSize: const Size(200, 50),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
        child: const Text('INICIAR SESIÓN', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
      ),
    ]),
  ));

  Widget _buildEmpty(String msg) => Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
    Icon(LucideIcons.packageOpen, size: 56, color: Colors.white.withOpacity(0.3)),
    const SizedBox(height: 14),
    Text(msg, style: const TextStyle(color: Colors.white60, fontSize: 14), textAlign: TextAlign.center),
  ]));

  Widget _buildError() => Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
    const Icon(LucideIcons.alertCircle, size: 48, color: Colors.white60),
    const SizedBox(height: 12),
    Text(_error, style: const TextStyle(color: Colors.white70, fontSize: 13), textAlign: TextAlign.center),
    const SizedBox(height: 16),
    TextButton.icon(
      onPressed: _cargarReservas,
      icon: const Icon(LucideIcons.refreshCw, color: Colors.white, size: 16),
      label: const Text('Reintentar', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900)),
    ),
  ]));
}

// ── Card de reserva ───────────────────────────────────────────────────────────
class _ReservaCard extends StatelessWidget {
  final Reserva   reserva;
  final bool      showPay, showCancel, showDelete;
  final VoidCallback onCancel, onDelete;

  const _ReservaCard({
    required this.reserva,
    required this.showPay,
    required this.showCancel,
    required this.showDelete,
    required this.onCancel,
    required this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final estado = reserva.estadoNombre;
    final tipo   = reserva.tipoReservaNombre;
    final color  = _estadoColor(estado);
    final bg     = _estadoBg(estado);

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 12, offset: const Offset(0, 4))],
      ),
      child: Column(children: [
        // ── Cabecera ─────────────────────────────────────────────────────────
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          decoration: BoxDecoration(
            color: bg,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
          ),
          child: Row(children: [
            Container(
              width: 38, height: 38,
              decoration: BoxDecoration(
                  color: color.withOpacity(0.15), borderRadius: BorderRadius.circular(10)),
              child: Icon(_tipoIcon(tipo), size: 18, color: color),
            ),
            const SizedBox(width: 10),
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(reserva.servicioNombre,
                  style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 14, color: AppColors.teal900),
                  overflow: TextOverflow.ellipsis, maxLines: 1),
              Text('#${reserva.idReserva}',
                  style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w600)),
            ])),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
              decoration: BoxDecoration(color: color.withOpacity(0.12), borderRadius: BorderRadius.circular(8)),
              child: Text(estado.toUpperCase(),
                  style: TextStyle(color: color, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 0.5)),
            ),
          ]),
        ),

        // ── Cuerpo ────────────────────────────────────────────────────────────
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 14, 16, 0),
          child: Row(children: [
            _infoChip(LucideIcons.calendar, _formatFecha(reserva.fechaReserva)),
            const SizedBox(width: 10),
            _infoChip(LucideIcons.creditCard,
                '${reserva.precioTotal.toStringAsFixed(2)} €'),
          ]),
        ),

        // ── Acciones ─────────────────────────────────────────────────────────
        if (showPay || showCancel || showDelete)
          Padding(
            padding: const EdgeInsets.fromLTRB(12, 10, 12, 12),
            child: Row(children: [
              if (showDelete)
                _actionBtn(icon: LucideIcons.trash2, label: 'Eliminar',
                    color: const Color(0xFFDC2626), onTap: onDelete),
              if (showDelete && showCancel) const SizedBox(width: 8),
              if (showCancel)
                _actionBtn(icon: LucideIcons.ban, label: 'Cancelar',
                    color: const Color(0xFFEA580C), onTap: onCancel),
              const Spacer(),
              if (showPay)
                _actionBtn(icon: LucideIcons.creditCard, label: 'Pagar',
                    color: AppColors.teal600, filled: true, onTap: () {
                      // TODO: integrar con tu PaymentModal cuando esté listo
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        content: Text('Pasarela de pago próximamente',
                            style: TextStyle(fontWeight: FontWeight.w700)),
                        backgroundColor: AppColors.teal700,
                      ));
                    }),
            ]),
          )
        else
          const SizedBox(height: 12),
      ]),
    );
  }

  Widget _infoChip(IconData icon, String label) => Row(mainAxisSize: MainAxisSize.min, children: [
    Icon(icon, size: 13, color: AppColors.teal400),
    const SizedBox(width: 4),
    Text(label, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal700)),
  ]);

  Widget _actionBtn({
    required IconData icon, required String label,
    required Color color, required VoidCallback onTap, bool filled = false,
  }) => GestureDetector(
    onTap: onTap,
    child: Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
      decoration: BoxDecoration(
        color: filled ? color : color.withOpacity(0.08),
        borderRadius: BorderRadius.circular(12),
        border: filled ? null : Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Icon(icon, size: 14, color: filled ? Colors.white : color),
        const SizedBox(width: 5),
        Text(label, style: TextStyle(color: filled ? Colors.white : color,
            fontSize: 11, fontWeight: FontWeight.w900)),
      ]),
    ),
  );
}