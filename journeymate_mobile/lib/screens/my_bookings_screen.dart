// lib/screens/my_bookings_screen.dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';
import '../widgets/payment/payment_sheet.dart';
import 'booking_detail_screen.dart';

// ── Modelo ────────────────────────────────────────────────────────────────────
class Reserva {
  final int    idReserva;
  final String servicioNombre;
  final double precioTotal;
  String       estadoNombre;
  final String tipoReservaNombre;
  final String fechaReserva;

  Reserva({required this.idReserva, required this.servicioNombre,
    required this.precioTotal, required this.estadoNombre,
    required this.tipoReservaNombre, required this.fechaReserva});

  factory Reserva.fromJson(Map<String, dynamic> j) => Reserva(
    idReserva:         (j['idReserva']         ?? 0) as int,
    servicioNombre:    (j['servicioNombre']    ?? '').toString(),
    precioTotal:       double.tryParse((j['precioTotal'] ?? 0).toString()) ?? 0,
    estadoNombre:      (j['estadoNombre']      ?? '').toString(),
    tipoReservaNombre: (j['tipoReservaNombre'] ?? '').toString(),
    fechaReserva:      (j['fechaReserva']      ?? '').toString(),
  );

  Map<String, dynamic> toJson() => {
    'idReserva': idReserva, 'servicioNombre': servicioNombre,
    'precioTotal': precioTotal, 'estadoNombre': estadoNombre,
    'tipoReservaNombre': tipoReservaNombre, 'fechaServicio': fechaReserva,
    'servicio': {'nombre': servicioNombre, 'fechaSalida': fechaReserva},
  };

  // La fecha de inicio ya llegó o pasó
  bool get fechaPasada {
    if (fechaReserva.isEmpty) return false;
    try {
      final p = fechaReserva.split('-');
      final d = DateTime(int.parse(p[0]), int.parse(p[1]), int.parse(p[2]));
      return !DateTime.now().isBefore(d);
    } catch (_) { return false; }
  }

  bool get sinPagar    => estadoNombre.toLowerCase() == 'pendiente';
  bool get confirmada  => estadoNombre.toLowerCase() == 'confirmada';
  bool get puedeCancel => confirmada && !fechaPasada;
}

// ── Helpers visuales ──────────────────────────────────────────────────────────
String _fmt(String f) {
  if (f.isEmpty) return '—';
  try { final p = f.split('-'); return '${p[2]}/${p[1]}/${p[0]}'; }
  catch (_) { return f; }
}

IconData _tipoIcon(String t) {
  final s = t.toLowerCase();
  if (s.contains('hotel'))     return LucideIcons.hotel;
  if (s.contains('vuelo'))     return LucideIcons.plane;
  if (s.contains('coche') || s.contains('vtc')) return LucideIcons.car;
  if (s.contains('crucero'))   return LucideIcons.ship;
  if (s.contains('actividad')) return LucideIcons.ticket;
  return LucideIcons.bookOpen;
}

Color _estadoColor(String e) {
  final s = e.toLowerCase();
  if (s.contains('confirm'))  return const Color(0xFF059669);
  if (s.contains('pendient')) return const Color(0xFFD97706);
  if (s.contains('cancel'))   return const Color(0xFFDC2626);
  if (s.contains('complet'))  return AppColors.teal600;
  return Colors.grey;
}

Color _estadoBg(String e) {
  final s = e.toLowerCase();
  if (s.contains('confirm'))  return const Color(0xFFD1FAE5);
  if (s.contains('pendient')) return const Color(0xFFFEF3C7);
  if (s.contains('cancel'))   return const Color(0xFFFEE2E2);
  if (s.contains('complet'))  return AppColors.teal100;
  return const Color(0xFFF3F4F6);
}

// ── Screen ────────────────────────────────────────────────────────────────────
class MyBookingsScreen extends StatefulWidget {
  const MyBookingsScreen({super.key});
  @override State<MyBookingsScreen> createState() => _State();
}

class _State extends State<MyBookingsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabs;
  List<Reserva> _pend = [], _conf = [], _hist = [];
  bool _loading = true, _logged = false;
  String _err = '';

  // Solo suma las reservas que están en estado pendiente Y cuya fecha es futura
  double get _total => _pend
      .where((r) => r.sinPagar && !r.fechaPasada)
      .fold(0.0, (s, r) => s + r.precioTotal);

  @override
  void initState() {
    super.initState();
    _tabs = TabController(length: 3, vsync: this);
    _init();
  }

  @override
  void dispose() { _tabs.dispose(); super.dispose(); }

  Future<void> _init() async {
    final ok = await AuthService.isLoggedIn();
    if (!ok) { if (mounted) setState(() { _logged = false; _loading = false; }); return; }
    if (mounted) setState(() => _logged = true);
    await _load();
  }

  // ── Carga desde el backend ─────────────────────────────────────────────────
  Future<void> _load() async {
    setState(() { _loading = true; _err = ''; });
    try {
      final id = await AuthService.getIdUsuario();
      if (id == null) throw Exception('Sin ID');

      // Reservas pendientes (sin pagar)
      final rp = await api.get('/reservas/usuario/$id') as List? ?? [];
      final pend = rp.map((e) => Reserva.fromJson(e as Map<String, dynamic>)).toList();

      // Historial completo (confirmadas + completadas + canceladas)
      final rh = await api.get('/reservas/usuario/$id/historial') as List? ?? [];
      final todas = rh.map((e) => Reserva.fromJson(e as Map<String, dynamic>)).toList();

      // AUTO-COMPLETAR: confirmadas cuya fecha ya pasó → las marcamos COMPLETADA
      // (solo en Flutter, el backend se actualiza con PATCH /estado)
      for (final r in todas) {
        if (r.confirmada && r.fechaPasada) {
          // Llamada silenciosa al backend para actualizar el estado
          api.post('/reservas/${r.idReserva}/estado', {'estado': 'COMPLETADA'})
              .catchError((_) {});
          r.estadoNombre = 'COMPLETADA';
        }
      }

      if (mounted) {
        setState(() {
        // PENDIENTES: sin pagar
          _pend = pend.where((r) => r.sinPagar && !r.fechaPasada).toList();
        // CONFIRMADAS: pagadas y con fecha futura
          _conf = todas.where((r) =>
          r.estadoNombre.toLowerCase() == 'confirmada' && !r.fechaPasada).toList();
        // HISTORIAL: completadas (fecha pasada) + canceladas
        // También incluye pendientes con fecha pasada
        _hist = [
          ...todas.where((r) {
            final e = r.estadoNombre.toLowerCase();
            return e == 'completada' || e == 'cancelada' || (e == 'confirmada' && r.fechaPasada);
          }),
          // Pendientes con fecha ya pasada también van al historial visualmente
          ...pend.where((r) => r.fechaPasada),
        ];

        _loading = false;
      });
      }
    } on ApiException catch (e) {
      if (mounted) setState(() { _err = 'Error ${e.statusCode}.'; _loading = false; });
    } catch (_) {
      if (mounted) setState(() { _err = 'No se pudieron cargar las reservas.'; _loading = false; });
    }
  }

  // ── CANCELAR ──────────────────────────────────────────────────────────────
  Future<void> _cancelar(Reserva r) async {
    if (!r.puedeCancel) {
      _snack('No se puede cancelar: la fecha ya pasó.', err: true);
      return;
    }
    final ok = await _confirm('Cancelar reserva',
        '¿Cancelar "${r.servicioNombre}"?\nReembolso en 3-5 días hábiles.',
        'Sí, cancelar', danger: true);
    if (!ok) return;
    try {
      // PATCH /reservas/{id}/estado — este endpoint ya existe en tu backend
      await api.post('/reservas/${r.idReserva}/estado', {'estado': 'CANCELADA'});
      _snack('🔄 Cancelada. Reembolso en 3-5 días.');
      await _load();
      _tabs.animateTo(2);
    } catch (_) {
      _snack('No se pudo cancelar. Comprueba la conexión.', err: true);
    }
  }

  // ── ELIMINAR (solo pendientes) ─────────────────────────────────────────────
  // Si la fecha ya pasó → muestra aviso de que debe cambiar la fecha
  // Si la fecha es futura → elimina con DELETE /reservas/{id}
  Future<void> _eliminar(Reserva r) async {
    if (!r.sinPagar) {
      _snack('Solo puedes eliminar reservas sin pagar.', err: true);
      return;
    }

    // Si la fecha ya pasó → no eliminamos, pedimos que cambie la fecha
    if (r.fechaPasada) {
      await _mostrarCambiarFecha(r);
      return;
    }

    // Fecha futura → eliminar
    final ok = await _confirm('Eliminar reserva',
        '¿Eliminar "${r.servicioNombre}"?\nEsta acción no se puede deshacer.',
        'Eliminar', danger: true);
    if (!ok) return;

    try {
      // DELETE /api/v1/reservas/{id} → 204 No Content
      await api.delete('/reservas/${r.idReserva}');
      setState(() {
        _pend.removeWhere((x) => x.idReserva == r.idReserva);
      });
      _snack('Reserva eliminada.');
    } catch (_) {
      _snack('No se pudo eliminar.', err: true);
    }
  }

  // ── CAMBIAR FECHA (informativo — no toca el backend) ──────────────────────
  // Solo muestra al usuario que debe contactar o gestionar la nueva fecha
  Future<void> _mostrarCambiarFecha(Reserva r) async {
    await showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(
            color: Colors.white, borderRadius: BorderRadius.circular(28)),
        padding: const EdgeInsets.all(24),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(width: 40, height: 4, margin: const EdgeInsets.only(bottom: 20),
              decoration: BoxDecoration(color: Colors.grey[300],
                  borderRadius: BorderRadius.circular(2))),
          const Icon(LucideIcons.calendarClock, size: 44, color: Color(0xFFD97706)),
          const SizedBox(height: 12),
          const Text('Fecha de reserva pasada',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900,
                  color: AppColors.teal900)),
          const SizedBox(height: 8),
          Text(
            'La reserva "${r.servicioNombre}" tenía fecha ${_fmt(r.fechaReserva)} '
                'que ya ha pasado.\n\n'
                'Para cambiar la fecha, contacta con nosotros o cancela esta reserva '
                'y crea una nueva con la fecha correcta.',
            style: const TextStyle(color: Colors.grey, fontSize: 13),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 24),
          Row(children: [
            Expanded(child: OutlinedButton(
              onPressed: () => Navigator.pop(context),
              style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(14))),
              child: const Text('Cerrar',
                  style: TextStyle(fontWeight: FontWeight.w700)),
            )),
            const SizedBox(width: 12),
            Expanded(child: ElevatedButton(
              onPressed: () { Navigator.pop(context); _cancelar(r); },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFDC2626),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 14),
                minimumSize: const Size(0, 0),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(14)),
              ),
              child: const Text('Cancelar reserva',
                  style: TextStyle(fontWeight: FontWeight.w900)),
            )),
          ]),
          const SizedBox(height: 8),
        ]),
      ),
    );
  }

  // ── PAGAR ─────────────────────────────────────────────────────────────────
  Future<void> _pagar(Reserva r) async {
    final result = await PaymentSheet.show(context,
        reservaId: r.idReserva,
        precio: r.precioTotal,
        descripcion: r.servicioNombre);
    // Cuando el usuario cierra el navegador tras pagar → recargar
    if (result == 'paid') await _load();
  }

  Future<void> _pagarTodo() async {
    if (_pend.isEmpty) return;
    final result = await PaymentSheet.show(context,
        reservaIds: _pend.map((r) => r.idReserva).toList(),
        precio: _total,
        descripcion: '${_pend.length} reservas pendientes');
    if (result == 'paid') await _load();
  }

  // ── UI helpers ─────────────────────────────────────────────────────────────
  void _snack(String msg, {bool err = false}) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(msg, style: const TextStyle(fontWeight: FontWeight.w700)),
      backgroundColor: err ? const Color(0xFFDC2626) : AppColors.teal700,
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      margin: const EdgeInsets.all(16),
    ));
  }

  Future<bool> _confirm(String title, String msg, String label,
      {bool danger = false}) async {
    final r = await showModalBottomSheet<bool>(
      context: context, backgroundColor: Colors.transparent,
      builder: (_) => Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(color: Colors.white,
            borderRadius: BorderRadius.circular(28)),
        padding: const EdgeInsets.all(24),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Container(width: 40, height: 4, margin: const EdgeInsets.only(bottom: 20),
              decoration: BoxDecoration(color: Colors.grey[300],
                  borderRadius: BorderRadius.circular(2))),
          Icon(danger ? LucideIcons.alertTriangle : LucideIcons.helpCircle,
              size: 40, color: danger ? const Color(0xFFDC2626) : AppColors.teal600),
          const SizedBox(height: 12),
          Text(title, style: const TextStyle(fontSize: 18,
              fontWeight: FontWeight.w900, color: AppColors.teal900)),
          const SizedBox(height: 8),
          Text(msg, style: const TextStyle(color: Colors.grey, fontSize: 13),
              textAlign: TextAlign.center),
          const SizedBox(height: 24),
          Row(children: [
            Expanded(child: OutlinedButton(
              onPressed: () => Navigator.pop(context, false),
              style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(14))),
              child: const Text('Cancelar',
                  style: TextStyle(fontWeight: FontWeight.w700)),
            )),
            const SizedBox(width: 12),
            Expanded(child: ElevatedButton(
              onPressed: () => Navigator.pop(context, true),
              style: ElevatedButton.styleFrom(
                backgroundColor: danger ? const Color(0xFFDC2626) : AppColors.teal600,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 14),
                minimumSize: const Size(0, 0),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(14)),
              ),
              child: Text(label, style: const TextStyle(fontWeight: FontWeight.w900)),
            )),
          ]),
          const SizedBox(height: 8),
        ]),
      ),
    );
    return r == true;
  }

  // ══════════════════════════════════════════════════════════════════════════
  @override
  Widget build(BuildContext context) => Scaffold(
    backgroundColor: Colors.transparent,
    body: Container(
      decoration: const BoxDecoration(gradient: AppColors.gradientMain),
      child: SafeArea(child: Column(children: [
        _hdr(), if (_logged) _tabBar(), Expanded(child: _body()),
      ])),
    ),
  );

  Widget _hdr() => Padding(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 8),
    child: Row(children: [
      const Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('MIS RESERVAS', style: TextStyle(color: Colors.white,
                fontWeight: FontWeight.w900, fontSize: 22, letterSpacing: -0.3)),
            Text('Gestiona tus viajes',
                style: TextStyle(color: Colors.white60, fontSize: 12)),
          ])),
      if (_logged && _pend.isNotEmpty)
        GestureDetector(
          onTap: _pagarTodo,
          child: Container(
            margin: const EdgeInsets.only(right: 8),
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(color: AppColors.teal600,
                borderRadius: BorderRadius.circular(14)),
            child: Row(mainAxisSize: MainAxisSize.min, children: [
              const Icon(LucideIcons.creditCard, color: Colors.white, size: 13),
              const SizedBox(width: 4),
              Text('Todo (${_total.toStringAsFixed(0)}€)',
                  style: const TextStyle(color: Colors.white, fontSize: 10,
                      fontWeight: FontWeight.w900)),
            ]),
          ),
        ),
      if (_logged && !_loading)
        GestureDetector(
          onTap: _load,
          child: Container(padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(color: Colors.white.withOpacity(0.2),
                  shape: BoxShape.circle),
              child: const Icon(LucideIcons.refreshCw, color: Colors.white, size: 16)),
        ),
    ]),
  );

  Widget _tabBar() => Padding(
    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    child: Container(
      decoration: BoxDecoration(color: Colors.white.withOpacity(0.15),
          borderRadius: BorderRadius.circular(20)),
      child: TabBar(
        controller: _tabs,
        indicator: BoxDecoration(color: Colors.white,
            borderRadius: BorderRadius.circular(16)),
        indicatorSize: TabBarIndicatorSize.tab,
        dividerColor: Colors.transparent,
        labelColor: AppColors.teal900, unselectedLabelColor: Colors.white70,
        labelStyle: const TextStyle(fontWeight: FontWeight.w900, fontSize: 11),
        tabs: [
          Tab(text: 'PEND.${_pend.where((r)=>!r.fechaPasada).isNotEmpty
              ? " (${_pend.where((r)=>!r.fechaPasada).length})" : ""}'),
          Tab(text: 'CONF.${_conf.isNotEmpty ? " (${_conf.length})" : ""}'),
          Tab(text: 'HISTORIAL${_hist.isNotEmpty ? " (${_hist.length})" : ""}'),
        ],
      ),
    ),
  );

  Widget _body() {
    if (!_logged)          return _notLogged();
    if (_loading)          return const Center(child: CircularProgressIndicator(color: Colors.white));
    if (_err.isNotEmpty)   return _errW();
    return TabBarView(controller: _tabs, children: [
      // Solo pendientes con fecha futura en el tab Pendientes
      _list(_pend.where((r) => !r.fechaPasada).toList(),
          emptyMsg: '¡Sin reservas pendientes! 🎉', showPay: true, showDel: true),
      _list(_conf, emptyMsg: 'Sin confirmadas aún.', showCancel: true),
      _list(_hist, emptyMsg: 'Tu historial está vacío.'),
    ]);
  }

  Widget _list(List<Reserva> items, {required String emptyMsg,
    bool showPay = false, bool showCancel = false, bool showDel = false}) {
    if (items.isEmpty) return _empty(emptyMsg);
    return RefreshIndicator(
      color: AppColors.teal600, backgroundColor: Colors.white,
      onRefresh: _load,
      child: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 100),
        physics: const BouncingScrollPhysics(),
        itemCount: items.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (_, i) {
          final r = items[i];
          return GestureDetector(
            onTap: () async {
              final res = await Navigator.push(context, MaterialPageRoute(
                  builder: (_) => BookingDetailScreen(reserva: r.toJson())));
              if (res == 'cancelled') _load();
            },
            child: _Card(
              reserva: r,
              showPay: showPay,
              showCancel: showCancel && r.puedeCancel,
              // Mostrar "Eliminar" si fecha futura, "Cambiar fecha" si pasó
              showDel: showDel && r.sinPagar,
              onPay:    () => _pagar(r),
              onCancel: () => _cancelar(r),
              onDel:    () => _eliminar(r),
            ),
          );
        },
      ),
    );
  }

  Widget _notLogged() => Center(child: Padding(
    padding: const EdgeInsets.all(32),
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Container(width: 80, height: 80,
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.15),
              shape: BoxShape.circle),
          child: const Icon(LucideIcons.lock, color: Colors.white, size: 36)),
      const SizedBox(height: 20),
      const Text('Inicia sesión', style: TextStyle(color: Colors.white,
          fontWeight: FontWeight.w900, fontSize: 20)),
      const SizedBox(height: 8),
      const Text('Necesitas cuenta para ver tus reservas',
          style: TextStyle(color: Colors.white60, fontSize: 13),
          textAlign: TextAlign.center),
      const SizedBox(height: 24),
      ElevatedButton(
        onPressed: () => context.go('/login'),
        style: ElevatedButton.styleFrom(backgroundColor: Colors.white,
            foregroundColor: AppColors.teal900, minimumSize: const Size(200, 50),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
        child: const Text('INICIAR SESIÓN',
            style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
      ),
    ]),
  ));

  Widget _empty(String msg) => Center(child: Column(mainAxisSize: MainAxisSize.min,
      children: [
        Icon(LucideIcons.packageOpen, size: 56, color: Colors.white.withOpacity(0.3)),
        const SizedBox(height: 14),
        Text(msg, style: const TextStyle(color: Colors.white60, fontSize: 14),
            textAlign: TextAlign.center),
      ]));

  Widget _errW() => Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
    const Icon(LucideIcons.alertCircle, size: 48, color: Colors.white60),
    const SizedBox(height: 12),
    Text(_err, style: const TextStyle(color: Colors.white70, fontSize: 13),
        textAlign: TextAlign.center),
    const SizedBox(height: 16),
    TextButton.icon(onPressed: _load,
        icon: const Icon(LucideIcons.refreshCw, color: Colors.white, size: 16),
        label: const Text('Reintentar',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900))),
  ]));
}

// ── Card ──────────────────────────────────────────────────────────────────────
class _Card extends StatelessWidget {
  final Reserva r;
  final bool showPay, showCancel, showDel;
  final VoidCallback onPay, onCancel, onDel;

  const _Card({
    required Reserva reserva,
    required this.showPay, required this.showCancel, required this.showDel,
    required this.onPay, required this.onCancel, required this.onDel,
  }) : r = reserva;

  @override
  Widget build(BuildContext context) {
    final c = _estadoColor(r.estadoNombre);
    final b = _estadoBg(r.estadoNombre);
    return Container(
      decoration: BoxDecoration(color: Colors.white,
          borderRadius: BorderRadius.circular(24),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06),
              blurRadius: 12, offset: const Offset(0, 4))]),
      child: Column(children: [
        // Cabecera con tipo + nombre + estado
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          decoration: BoxDecoration(color: b,
              borderRadius: const BorderRadius.vertical(top: Radius.circular(24))),
          child: Row(children: [
            Container(width: 38, height: 38,
                decoration: BoxDecoration(color: c.withOpacity(0.15),
                    borderRadius: BorderRadius.circular(10)),
                child: Icon(_tipoIcon(r.tipoReservaNombre), size: 18, color: c)),
            const SizedBox(width: 10),
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(r.servicioNombre, style: const TextStyle(fontWeight: FontWeight.w900,
                      fontSize: 14, color: AppColors.teal900),
                      overflow: TextOverflow.ellipsis),
                  Text('#${r.idReserva}', style: const TextStyle(fontSize: 10,
                      color: Colors.grey, fontWeight: FontWeight.w600)),
                ])),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
              decoration: BoxDecoration(color: c.withOpacity(0.12),
                  borderRadius: BorderRadius.circular(8)),
              child: Text(r.estadoNombre.toUpperCase(), style: TextStyle(color: c,
                  fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 0.5)),
            ),
          ]),
        ),

        // Fecha + precio + flecha
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 0),
          child: Row(children: [
            _chip(LucideIcons.calendar, _fmt(r.fechaReserva)),
            const SizedBox(width: 10),
            _chip(LucideIcons.tag, '${r.precioTotal.toStringAsFixed(2)} €'),
            const Spacer(),
            const Icon(LucideIcons.chevronRight, size: 14, color: Colors.grey),
          ]),
        ),

        // Botones de acción
        if (showPay || showCancel || showDel)
          Padding(
            padding: const EdgeInsets.fromLTRB(12, 10, 12, 12),
            child: Row(children: [
              if (showDel)
              // Si la fecha pasó → "Cambiar fecha", si no → "Eliminar"
                r.fechaPasada
                    ? _btn(LucideIcons.calendarClock, 'Cambiar fecha',
                    const Color(0xFFD97706), onDel)
                    : _btn(LucideIcons.trash2, 'Eliminar',
                    const Color(0xFFDC2626), onDel),
              if (showDel && showCancel) const SizedBox(width: 8),
              if (showCancel)
                _btn(LucideIcons.ban, 'Cancelar',
                    const Color(0xFFEA580C), onCancel),
              const Spacer(),
              if (showPay)
                _btn(LucideIcons.creditCard, 'PAGAR',
                    AppColors.teal600, onPay, filled: true),
            ]),
          )
        else
          const SizedBox(height: 12),
      ]),
    );
  }

  Widget _chip(IconData i, String l) =>
      Row(mainAxisSize: MainAxisSize.min, children: [
        Icon(i, size: 13, color: AppColors.teal400), const SizedBox(width: 4),
        Text(l, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700,
            color: AppColors.teal700)),
      ]);

  Widget _btn(IconData icon, String label, Color color, VoidCallback onTap,
      {bool filled = false}) =>
      GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          decoration: BoxDecoration(
            color: filled ? color : color.withOpacity(0.08),
            borderRadius: BorderRadius.circular(12),
            border: filled ? null : Border.all(color: color.withOpacity(0.3)),
          ),
          child: Row(mainAxisSize: MainAxisSize.min, children: [
            Icon(icon, size: 13, color: filled ? Colors.white : color),
            const SizedBox(width: 4),
            Text(label, style: TextStyle(color: filled ? Colors.white : color,
                fontSize: 11, fontWeight: FontWeight.w900)),
          ]),
        ),
      );
}