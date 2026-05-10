// lib/screens/booking_detail_screen.dart
//
// Pantalla de detalle de reserva — equivalente visual a eDreams/Booking app
// Se abre al pulsar cualquier reserva en MyBookingsScreen.
// Adapta el contenido según el tipo: HOTEL | VUELO | COCHE | ACTIVIDAD | CRUCERO

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';
import '../services/auth_service.dart';
import '../services/api_service.dart';

// ── Modelo reutilizado de MyBookingsScreen ────────────────────────────────────
// (importa desde my_bookings_screen.dart en tu proyecto real)
class BookingDetailScreen extends StatefulWidget {
  final Map<String, dynamic> reserva; // el JSON completo de la reserva

  const BookingDetailScreen({super.key, required this.reserva});

  @override
  State<BookingDetailScreen> createState() => _BookingDetailScreenState();
}

class _BookingDetailScreenState extends State<BookingDetailScreen>
    with SingleTickerProviderStateMixin {

  late TabController _tabCtrl;
  late String        _tipo;
  late List<_Tab>    _tabs;

  bool   _cancelling = false;

  @override
  void initState() {
    super.initState();
    _tipo = _resolveTipo();
    _tabs = _buildTabs();
    _tabCtrl = TabController(length: _tabs.length, vsync: this);
  }

  @override
  void dispose() {
    _tabCtrl.dispose();
    super.dispose();
  }

  // ── Tipo de reserva ────────────────────────────────────────────────────────
  String _resolveTipo() {
    final t = (widget.reserva['tipoReservaNombre'] ?? '').toString().toUpperCase();
    if (t.contains('HOTEL'))     return 'HOTEL';
    if (t.contains('VUELO'))     return 'VUELO';
    if (t.contains('COCHE') || t.contains('VTC')) return 'COCHE';
    if (t.contains('CRUCERO'))   return 'CRUCERO';
    if (t.contains('ACTIVIDAD')) return 'ACTIVIDAD';
    return 'HOTEL';
  }

  // ── Tabs según tipo ────────────────────────────────────────────────────────
  List<_Tab> _buildTabs() {
    switch (_tipo) {
      case 'VUELO':
        return [
          _Tab('Reserva',    _buildVueloReserva()),
          _Tab('Itinerario', _buildVueloItinerario()),
          _Tab('Equipaje',   _buildVueloEquipaje()),
        ];
      case 'HOTEL':
        return [
          _Tab('Detalles',   _buildHotelDetalles()),
          _Tab('Habitación', _buildHotelHabitacion()),
          _Tab('Políticas',  _buildHotelPoliticas()),
        ];
      case 'COCHE':
        return [
          _Tab('Vehículo',   _buildCocheDetalle()),
          _Tab('Recogida',   _buildCocheRecogida()),
        ];
      case 'CRUCERO':
        return [
          _Tab('Crucero',    _buildCruceroDetalle()),
          _Tab('Itinerario', _buildCruceroItinerario()),
        ];
      case 'ACTIVIDAD':
        return [
          _Tab('Actividad',  _buildActividadDetalle()),
          _Tab('Información',_buildActividadInfo()),
        ];
      default:
        return [_Tab('Detalles', _buildGenerico())];
    }
  }

  // ── Helpers ────────────────────────────────────────────────────────────────
  Map<String, dynamic> get _s =>
      (widget.reserva['servicio'] as Map<String, dynamic>?) ?? {};

  String get _nombre    => widget.reserva['servicioNombre']?.toString()    ?? _s['nombre']?.toString()    ?? 'Reserva';
  String get _estado    => widget.reserva['estadoNombre']?.toString()      ?? '';
  String get _fecha     => widget.reserva['fechaServicio']?.toString()     ?? _s['fechaSalida']?.toString() ?? '';
  String get _fechaFin  => _s['horaLlegada']?.toString().split('T').first  ?? '';
  double get _precio    => double.tryParse((widget.reserva['precioTotal']  ?? 0).toString()) ?? 0;
  int    get _id        => (widget.reserva['idReserva'] ?? 0) as int;
  String get _imagen    => _s['urlFoto']?.toString() ?? _s['imagen']?.toString() ?? '';

  String _fmt(String? iso) {
    if (iso == null || iso.isEmpty) return '—';
    try {
      final d = DateTime.parse(iso);
      const months = ['', 'ene', 'feb', 'mar', 'abr', 'may', 'jun',
        'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];
      return '${d.day} ${months[d.month]} ${d.year}';
    } catch (_) {
      if (iso.contains('-') && iso.length >= 10) {
        final p = iso.split('-');
        return '${p[2]}/${p[1]}/${p[0]}';
      }
      return iso;
    }
  }

  String _fmtTime(String? iso) {
    if (iso == null || iso.isEmpty) return '--:--';
    try {
      final d = DateTime.parse(iso);
      return '${d.hour.toString().padLeft(2,'0')}:${d.minute.toString().padLeft(2,'0')}';
    } catch (_) {
      if (iso.contains('T')) return iso.split('T')[1].substring(0, 5);
      if (iso.contains(':'))  return iso.substring(0, 5);
      return iso;
    }
  }

  Color get _estadoColor {
    final e = _estado.toLowerCase();
    if (e.contains('confirm'))  return const Color(0xFF059669);
    if (e.contains('pendient')) return const Color(0xFFD97706);
    if (e.contains('cancel'))   return const Color(0xFFDC2626);
    if (e.contains('complet'))  return AppColors.teal600;
    return Colors.grey;
  }

  bool get _puedeCancel {
    if (_fecha.isEmpty) return false;
    try {
      final p = _fecha.split('-');
      final inicio = DateTime(int.parse(p[0]), int.parse(p[1]), int.parse(p[2]));
      return DateTime.now().isBefore(inicio);
    } catch (_) { return false; }
  }

  // ── Cancelar ──────────────────────────────────────────────────────────────
  Future<void> _cancelar() async {
    final ok = await showModalBottomSheet<bool>(
      context: context, backgroundColor: Colors.transparent,
      builder: (_) => const _ConfirmSheet(
        icon: LucideIcons.alertTriangle, iconColor: Color(0xFFDC2626),
        title: 'Cancelar reserva',
        message: '¿Seguro que quieres cancelar esta reserva?\nEl reembolso se procesará en 3-5 días hábiles.',
        confirmLabel: 'Sí, cancelar', danger: true,
      ),
    );
    if (ok != true) return;

    setState(() => _cancelling = true);
    try {
      await api.post('/reservas/$_id/estado', {'estado': 'CANCELADA'});
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
          content: Text('🔄 Reserva cancelada. Reembolso en 3-5 días.', style: TextStyle(fontWeight: FontWeight.w700)),
          backgroundColor: AppColors.teal700, behavior: SnackBarBehavior.floating,
        ));
        Navigator.pop(context, 'cancelled');
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text('No se pudo cancelar. Inténtalo de nuevo.', style: TextStyle(fontWeight: FontWeight.w700)),
        backgroundColor: Color(0xFFDC2626), behavior: SnackBarBehavior.floating,
      ));
      }
    } finally {
      if (mounted) setState(() => _cancelling = false);
    }
  }

  // ══════════════════════════════════════════════════════════════════════════
  // BUILD PRINCIPAL
  // ══════════════════════════════════════════════════════════════════════════
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: CustomScrollView(
        physics: const BouncingScrollPhysics(),
        slivers: [
          _buildHero(),
          SliverToBoxAdapter(child: _buildEstadoBanner()),
          SliverToBoxAdapter(child: _buildTabBar()),
          SliverToBoxAdapter(child: _buildTabContent()),
          SliverToBoxAdapter(child: _buildActions()),
          const SliverToBoxAdapter(child: SizedBox(height: 40)),
        ],
      ),
    );
  }

  // ── Hero con imagen / gradiente ───────────────────────────────────────────
  Widget _buildHero() => SliverAppBar(
    expandedHeight: 220,
    pinned: true,
    backgroundColor: _tipoColor(_tipo),
    leading: GestureDetector(
      onTap: () => Navigator.pop(context),
      child: Container(
        margin: const EdgeInsets.all(8),
        decoration: const BoxDecoration(color: Colors.black26, shape: BoxShape.circle),
        child: const Icon(LucideIcons.arrowLeft, color: Colors.white, size: 20),
      ),
    ),
    actions: [
      GestureDetector(
        onTap: () => _copyToClipboard('$_id'),
        child: Container(
          margin: const EdgeInsets.all(8),
          decoration: const BoxDecoration(color: Colors.black26, shape: BoxShape.circle),
          child: const Padding(
            padding: EdgeInsets.all(8),
            child: Icon(LucideIcons.share2, color: Colors.white, size: 18),
          ),
        ),
      ),
    ],
    flexibleSpace: FlexibleSpaceBar(
      titlePadding: const EdgeInsets.fromLTRB(56, 0, 16, 16),
      title: Text(_nombre, style: const TextStyle(color: Colors.white,
          fontWeight: FontWeight.w900, fontSize: 15, shadows: [
            Shadow(color: Colors.black45, blurRadius: 4),
          ]), maxLines: 1, overflow: TextOverflow.ellipsis),
      background: Stack(children: [
        // Imagen o gradiente de fondo
        if (_imagen.isNotEmpty)
          Positioned.fill(child: Image.network(_imagen, fit: BoxFit.cover,
              errorBuilder: (_, __, ___) => _heroGradient()))
        else
          Positioned.fill(child: _heroGradient()),
        // Gradiente oscuro abajo
        Positioned.fill(child: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter, end: Alignment.bottomCenter,
              colors: [Colors.transparent, Colors.black.withOpacity(0.65)],
            ),
          ),
        )),
        // Icono de tipo centrado
        Center(child: Icon(_tipoIcon(_tipo), size: 56, color: Colors.white.withOpacity(0.3))),
        // Badge de tipo
        Positioned(top: 100, left: 16, child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
          decoration: BoxDecoration(
              color: _tipoColor(_tipo), borderRadius: BorderRadius.circular(10)),
          child: Row(mainAxisSize: MainAxisSize.min, children: [
            Icon(_tipoIcon(_tipo), size: 12, color: Colors.white),
            const SizedBox(width: 4),
            Text(_tipo, style: const TextStyle(color: Colors.white,
                fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 1)),
          ]),
        )),
      ]),
    ),
  );

  Widget _heroGradient() => Container(
    decoration: BoxDecoration(
      gradient: LinearGradient(
        colors: [_tipoColor(_tipo), _tipoColor(_tipo).withOpacity(0.6)],
        begin: Alignment.topLeft, end: Alignment.bottomRight,
      ),
    ),
  );

  // ── Banner de estado (verde/amarillo/rojo) ────────────────────────────────
  Widget _buildEstadoBanner() => Container(
    color: _estadoColor.withOpacity(0.1),
    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
    child: Row(children: [
      Icon(_estadoIcon, size: 20, color: _estadoColor),
      const SizedBox(width: 10),
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(_estado.toUpperCase(), style: TextStyle(color: _estadoColor,
            fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1)),
        Text('Reserva #$_id', style: const TextStyle(color: Colors.grey, fontSize: 11)),
      ])),
      // Precio
      Text('${_precio.toStringAsFixed(2)} €',
          style: TextStyle(color: _estadoColor, fontWeight: FontWeight.w900, fontSize: 16)),
    ]),
  );

  IconData get _estadoIcon {
    final e = _estado.toLowerCase();
    if (e.contains('confirm'))  return LucideIcons.checkCircle2;
    if (e.contains('pendient')) return LucideIcons.clock;
    if (e.contains('cancel'))   return LucideIcons.xCircle;
    if (e.contains('complet'))  return LucideIcons.badgeCheck;
    return LucideIcons.info;
  }

  // ── TabBar ────────────────────────────────────────────────────────────────
  Widget _buildTabBar() => Container(
    color: Colors.white,
    child: TabBar(
      controller: _tabCtrl,
      labelColor: _tipoColor(_tipo),
      unselectedLabelColor: Colors.grey,
      labelStyle: const TextStyle(fontWeight: FontWeight.w900, fontSize: 12),
      indicatorColor: _tipoColor(_tipo),
      indicatorWeight: 3,
      tabs: _tabs.map((t) => Tab(text: t.label)).toList(),
    ),
  );

  Widget _buildTabContent() => SizedBox(
    // Altura dinámica: no anidamos TabBarView (incompatible con CustomScrollView)
    child: AnimatedBuilder(
      animation: _tabCtrl,
      builder: (_, __) => _tabs[_tabCtrl.index].content,
    ),
  );

  // ── Acciones ──────────────────────────────────────────────────────────────
  Widget _buildActions() {
    if (_estado.toLowerCase().contains('cancel') ||
        _estado.toLowerCase().contains('complet')) return const SizedBox.shrink();

    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
      child: Column(children: [
        if (_puedeCancel)
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: _cancelling ? null : _cancelar,
              icon: _cancelling
                  ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2))
                  : const Icon(LucideIcons.ban, size: 16),
              label: Text(_cancelling ? 'Cancelando...' : 'Cancelar reserva',
                  style: const TextStyle(fontWeight: FontWeight.w900)),
              style: OutlinedButton.styleFrom(
                foregroundColor: const Color(0xFFDC2626),
                side: const BorderSide(color: Color(0xFFDC2626)),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              ),
            ),
          ),
        if (_estado.toLowerCase().contains('pendient')) ...[
          const SizedBox(height: 10),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () => ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Pasarela de pago próximamente 🚀',
                    style: TextStyle(fontWeight: FontWeight.w700)),
                    backgroundColor: AppColors.teal700, behavior: SnackBarBehavior.floating),
              ),
              icon: const Icon(LucideIcons.creditCard, size: 16),
              label: const Text('PAGAR AHORA', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.teal600, foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                minimumSize: const Size(0, 0),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              ),
            ),
          ),
        ],
      ]),
    );
  }

  void _copyToClipboard(String text) {
    Clipboard.setData(ClipboardData(text: text));
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
      content: Text('ID copiado al portapapeles', style: TextStyle(fontWeight: FontWeight.w700)),
      backgroundColor: AppColors.teal700, behavior: SnackBarBehavior.floating,
    ));
  }

  // ══════════════════════════════════════════════════════════════════════════
  // CONTENIDOS POR TIPO
  // ══════════════════════════════════════════════════════════════════════════

  // ── VUELO — Tab Reserva ───────────────────────────────────────────────────
  Widget _buildVueloReserva() => _Section(children: [
    _SectionTitle('Referencias de reserva'),
    _InfoRow(icon: LucideIcons.ticket,     label: 'Referencia JourneyMate', value: '#$_id'),
    _InfoRow(icon: LucideIcons.plane,      label: 'Aerolínea',    value: _s['descripcion']?.toString() ?? '—'),
    _InfoRow(icon: LucideIcons.creditCard, label: 'Precio total', value: '${_precio.toStringAsFixed(2)} €'),
    const SizedBox(height: 16),
    _SectionTitle('Detalles del vuelo'),
    _InfoRow(icon: LucideIcons.planeTakeoff, label: 'Salida',   value: '${_fmt(_s['horaSalida']?.toString())} · ${_fmtTime(_s['horaSalida']?.toString())}'),
    _InfoRow(icon: LucideIcons.planeLanding, label: 'Llegada',  value: '${_fmt(_s['horaLlegada']?.toString())} · ${_fmtTime(_s['horaLlegada']?.toString())}'),
    _InfoRow(icon: LucideIcons.users,        label: 'Pasajeros', value: '${_s['pasajeros'] ?? 1} adulto(s)'),
    _InfoRow(icon: LucideIcons.tag,          label: 'Clase',     value: _s['cabinClass']?.toString() ?? 'Economy'),
  ]);

  // ── VUELO — Tab Itinerario ────────────────────────────────────────────────
  Widget _buildVueloItinerario() => _Section(children: [
    _SectionTitle('Vuelo de ida'),
    _FlightLeg(
      origin:       _s['origen']?.toString()       ?? '—',
      destination:  _s['destino']?.toString()       ?? '—',
      depTime:      _fmtTime(_s['horaSalida']?.toString()),
      arrTime:      _fmtTime(_s['horaLlegada']?.toString()),
      date:         _fmt(_fecha),
      duration:     _s['duracion']?.toString()      ?? '—',
      airline:      _s['descripcion']?.toString()   ?? '—',
      direct:       true,
    ),
    if (_s['horaLlegadaRegreso'] != null) ...[
      const SizedBox(height: 20),
      _SectionTitle('Vuelo de vuelta'),
      _FlightLeg(
        origin:      _s['destino']?.toString()             ?? '—',
        destination: _s['origen']?.toString()              ?? '—',
        depTime:     _fmtTime(_s['horaSalidaRegreso']?.toString()),
        arrTime:     _fmtTime(_s['horaLlegadaRegreso']?.toString()),
        date:        _fmt(_s['fechaRegreso']?.toString()),
        duration:    _s['duracionRegreso']?.toString()     ?? '—',
        airline:     _s['descripcion']?.toString()         ?? '—',
        direct:      true,
      ),
    ],
  ]);

  // ── VUELO — Tab Equipaje ──────────────────────────────────────────────────
  Widget _buildVueloEquipaje() => _Section(children: [
    _SectionTitle('Equipaje incluido'),
    _BagRow(icon: LucideIcons.shoppingBag, label: 'Artículo personal', detail: '1 pieza'),
    _BagRow(icon: LucideIcons.briefcase,   label: 'Equipaje de mano',   detail: 'Según tarifa'),
    _BagRow(icon: LucideIcons.luggage,     label: 'Maleta facturada',   detail: _s['maletas']?.toString() ?? 'No incluida'),
    const SizedBox(height: 16),
    Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(color: const Color(0xFFFEF3C7), borderRadius: BorderRadius.circular(14)),
      child: const Row(children: [
        Icon(LucideIcons.alertTriangle, size: 16, color: Color(0xFFD97706)),
        SizedBox(width: 8),
        Expanded(child: Text('Consulta con la aerolínea para añadir equipaje extra.',
            style: TextStyle(color: Color(0xFF92400E), fontSize: 12, fontWeight: FontWeight.w600))),
      ]),
    ),
  ]);

  // ── HOTEL — Tab Detalles ──────────────────────────────────────────────────
  Widget _buildHotelDetalles() => _Section(children: [
    _SectionTitle('Fechas de estancia'),
    _CheckInOut(checkIn: _fecha, checkOut: _fechaFin.isNotEmpty ? _fechaFin : _s['horaLlegada']?.toString()),
    const SizedBox(height: 16),
    _SectionTitle('Detalles de propiedad'),
    _InfoRow(icon: LucideIcons.mapPin,  label: 'Dirección',   value: _s['descripcion_direccion']?.toString() ?? '—'),
    _InfoRow(icon: LucideIcons.star,    label: 'Estrellas',   value: '${_s['estrellas'] ?? '—'}'),
    _InfoRow(icon: LucideIcons.users,   label: 'Adultos',     value: '${_s['adultos'] ?? 1}'),
    _InfoRow(icon: LucideIcons.bedDouble,label:'Habitaciones', value: '${_s['habitaciones'] ?? 1}'),
    _InfoRow(icon: LucideIcons.creditCard,label:'Precio total',value: '${_precio.toStringAsFixed(2)} €'),
  ]);

  // ── HOTEL — Tab Habitación ────────────────────────────────────────────────
  Widget _buildHotelHabitacion() => _Section(children: [
    _SectionTitle('Tu habitación'),
    _InfoRow(icon: LucideIcons.bedDouble, label: 'Tipo',      value: _s['tipoHabitacion']?.toString() ?? 'Habitación estándar'),
    _InfoRow(icon: LucideIcons.users,     label: 'Capacidad', value: '${_s['adultos'] ?? 1} adulto(s)'),
    _InfoRow(icon: LucideIcons.coffee,    label: 'Régimen',   value: _s['regimen']?.toString() ?? 'Solo habitación'),
  ]);

  // ── HOTEL — Tab Políticas ─────────────────────────────────────────────────
  Widget _buildHotelPoliticas() => _Section(children: [
    const _SectionTitle('Check-in / Check-out'),
    const _InfoRow(icon: LucideIcons.logIn,  label: 'Check-in',  value: 'A partir de las 15:00'),
    const _InfoRow(icon: LucideIcons.logOut, label: 'Check-out', value: 'Antes de las 12:00'),
    const SizedBox(height: 16),
    _SectionTitle('Cancelación'),
    Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: _puedeCancel ? const Color(0xFFD1FAE5) : const Color(0xFFFEE2E2),
        borderRadius: BorderRadius.circular(14),
      ),
      child: Row(children: [
        Icon(_puedeCancel ? LucideIcons.checkCircle2 : LucideIcons.xCircle,
            size: 18, color: _puedeCancel ? const Color(0xFF059669) : const Color(0xFFDC2626)),
        const SizedBox(width: 10),
        Expanded(child: Text(
          _puedeCancel
              ? 'Puedes cancelar esta reserva antes del ${_fmt(_fecha)}.'
              : 'No se puede cancelar: el día de entrada ya ha llegado.',
          style: TextStyle(
              color: _puedeCancel ? const Color(0xFF065F46) : const Color(0xFF991B1B),
              fontSize: 12, fontWeight: FontWeight.w600),
        )),
      ]),
    ),
  ]);

  // ── COCHE ─────────────────────────────────────────────────────────────────
  Widget _buildCocheDetalle() => _Section(children: [
    const _SectionTitle('Vehículo'),
    _InfoRow(icon: LucideIcons.car,       label: 'Modelo',        value: _s['modelo']?.toString()     ?? _nombre),
    _InfoRow(icon: LucideIcons.building2, label: 'Proveedor',     value: _s['marca']?.toString()      ?? '—'),
    _InfoRow(icon: LucideIcons.users,     label: 'Plazas',        value: _s['plazas']?.toString()     ?? '5'),
    _InfoRow(icon: LucideIcons.briefcase, label: 'Maletas',       value: _s['maletas']?.toString()    ?? '2'),
    _InfoRow(icon: LucideIcons.settings2, label: 'Transmisión',   value: _s['transmision']?.toString() ?? '—'),
    const _InfoRow(icon: LucideIcons.shieldCheck,label:'Seguro',        value: 'Básico incluido'),
    _InfoRow(icon: LucideIcons.creditCard, label:'Precio total',  value: '${_precio.toStringAsFixed(2)} €'),
  ]);

  Widget _buildCocheRecogida() => _Section(children: [
    const _SectionTitle('Fechas y horarios'),
    _CheckInOut(label1: 'Recogida', label2: 'Devolución', checkIn: _fecha, checkOut: _fechaFin),
    const SizedBox(height: 16),
    const _SectionTitle('Lugar de recogida'),
    _InfoRow(icon: LucideIcons.mapPin, label: 'Punto',    value: _s['lugarRecogida']?.toString()   ?? '—'),
    _InfoRow(icon: LucideIcons.clock,  label: 'Hora rec.', value: _s['horaRecogida']?.toString()   ?? '—'),
    _InfoRow(icon: LucideIcons.clock,  label: 'Hora dev.', value: _s['horaDev']?.toString()        ?? '—'),
  ]);

  // ── CRUCERO ───────────────────────────────────────────────────────────────
  Widget _buildCruceroDetalle() => _Section(children: [
    const _SectionTitle('Información del crucero'),
    _InfoRow(icon: LucideIcons.ship,      label: 'Barco',          value: _s['nombreBarco']?.toString()   ?? '—'),
    _InfoRow(icon: LucideIcons.anchor,    label: 'Puerto salida',  value: _s['puertoSalida']?.toString()  ?? '—'),
    _InfoRow(icon: LucideIcons.moon,      label: 'Noches',         value: _s['noches']?.toString()        ?? '—'),
    _InfoRow(icon: LucideIcons.calendar,  label: 'Fecha salida',   value: _fmt(_fecha)),
    _InfoRow(icon: LucideIcons.creditCard,label: 'Precio total',   value: '${_precio.toStringAsFixed(2)} €'),
  ]);

  Widget _buildCruceroItinerario() => _Section(children: [
    const _SectionTitle('Puertos visitados'),
    Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
      child: const Text('El itinerario detallado de puertos estará disponible próximamente.',
          style: TextStyle(color: AppColors.teal700, fontSize: 13)),
    ),
  ]);

  // ── ACTIVIDAD ─────────────────────────────────────────────────────────────
  Widget _buildActividadDetalle() => _Section(children: [
    const _SectionTitle('Datos de la actividad'),
    _InfoRow(icon: LucideIcons.ticket,    label: 'Actividad',    value: _nombre),
    _InfoRow(icon: LucideIcons.calendar,  label: 'Fecha',        value: _fmt(_fecha)),
    _InfoRow(icon: LucideIcons.clock,     label: 'Hora',         value: _fmtTime(_s['horaSalida']?.toString())),
    _InfoRow(icon: LucideIcons.users,     label: 'Participantes',value: '${_s['adultos'] ?? 1}'),
    _InfoRow(icon: LucideIcons.creditCard,label: 'Precio total', value: '${_precio.toStringAsFixed(2)} €'),
  ]);

  Widget _buildActividadInfo() => _Section(children: [
    const _SectionTitle('Información importante'),
    _InfoRow(icon: LucideIcons.mapPin,   label: 'Ubicación',  value: _s['descripcion_direccion']?.toString() ?? '—'),
    _InfoRow(icon: LucideIcons.clock,    label: 'Duración',   value: _s['duracion']?.toString()              ?? '—'),
    const SizedBox(height: 12),
    Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
      child: Text(_s['descripcion']?.toString() ?? 'Sin descripción adicional.',
          style: const TextStyle(color: AppColors.teal700, fontSize: 13, height: 1.5)),
    ),
  ]);

  // ── Genérico ──────────────────────────────────────────────────────────────
  Widget _buildGenerico() => _Section(children: [
    const _SectionTitle('Detalles de la reserva'),
    _InfoRow(icon: LucideIcons.tag,        label: 'Tipo',    value: _tipo),
    _InfoRow(icon: LucideIcons.calendar,   label: 'Fecha',   value: _fmt(_fecha)),
    _InfoRow(icon: LucideIcons.creditCard, label: 'Precio',  value: '${_precio.toStringAsFixed(2)} €'),
    _InfoRow(icon: LucideIcons.info,       label: 'Descripción', value: _s['descripcion']?.toString() ?? '—'),
  ]);

  // ── Helpers estáticos ─────────────────────────────────────────────────────
  static Color _tipoColor(String tipo) {
    switch (tipo) {
      case 'VUELO':     return const Color(0xFF0284C7);
      case 'HOTEL':     return AppColors.teal600;
      case 'COCHE':     return const Color(0xFFD97706);
      case 'CRUCERO':   return const Color(0xFF2563EB);
      case 'ACTIVIDAD': return const Color(0xFF7C3AED);
      default:          return AppColors.teal600;
    }
  }

  static IconData _tipoIcon(String tipo) {
    switch (tipo) {
      case 'VUELO':     return LucideIcons.plane;
      case 'HOTEL':     return LucideIcons.hotel;
      case 'COCHE':     return LucideIcons.car;
      case 'CRUCERO':   return LucideIcons.ship;
      case 'ACTIVIDAD': return LucideIcons.ticket;
      default:          return LucideIcons.bookOpen;
    }
  }
}

// ── Componentes reutilizables ─────────────────────────────────────────────────

class _Tab { final String label; final Widget content; _Tab(this.label, this.content); }

class _Section extends StatelessWidget {
  final List<Widget> children;
  const _Section({required this.children});
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: children),
  );
}

class _SectionTitle extends StatelessWidget {
  final String text;
  const _SectionTitle(this.text);
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.only(bottom: 12),
    child: Text(text, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: AppColors.teal900)),
  );
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String label, value;
  const _InfoRow({required this.icon, required this.label, required this.value});
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.only(bottom: 12),
    child: Row(children: [
      Container(width: 36, height: 36,
          decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(10)),
          child: Icon(icon, size: 16, color: AppColors.teal600)),
      const SizedBox(width: 12),
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(label, style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w600, letterSpacing: 0.5)),
        Text(value, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w700, color: AppColors.teal900)),
      ])),
    ]),
  );
}

class _BagRow extends StatelessWidget {
  final IconData icon; final String label, detail;
  const _BagRow({required this.icon, required this.label, required this.detail});
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.only(bottom: 10),
    child: Row(children: [
      Container(width: 40, height: 40,
          decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(12)),
          child: Icon(icon, size: 18, color: AppColors.teal600)),
      const SizedBox(width: 12),
      Expanded(child: Text(label, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w700, color: AppColors.teal900))),
      Text(detail, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal600)),
    ]),
  );
}

class _CheckInOut extends StatelessWidget {
  final String? checkIn, checkOut;
  final String label1, label2;
  const _CheckInOut({this.checkIn, this.checkOut, this.label1 = 'Check-in', this.label2 = 'Check-out'});

  String _fmt(String? iso) {
    if (iso == null || iso.isEmpty) return '—';
    try {
      final d = DateTime.parse(iso);
      const m = ['','ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];
      return '${d.day} ${m[d.month]} ${d.year}';
    } catch (_) {
      if (iso.length >= 10 && iso.contains('-')) {
        final p = iso.split('-');
        return '${p[2]}/${p[1]}/${p[0]}';
      }
      return iso;
    }
  }

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(color: const Color(0xFFF9FAFB), borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE5E7EB))),
    child: Row(children: [
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(label1, style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w600)),
        const SizedBox(height: 4),
        Text(_fmt(checkIn), style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w900, color: AppColors.teal900)),
      ])),
      const Icon(LucideIcons.arrowRight, size: 18, color: Colors.grey),
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
        Text(label2, style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w600)),
        const SizedBox(height: 4),
        Text(_fmt(checkOut), style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w900, color: AppColors.teal900)),
      ])),
    ]),
  );
}

class _FlightLeg extends StatelessWidget {
  final String origin, destination, depTime, arrTime, date, duration, airline;
  final bool direct;
  const _FlightLeg({required this.origin, required this.destination,
    required this.depTime, required this.arrTime, required this.date,
    required this.duration, required this.airline, required this.direct});

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(color: const Color(0xFFF9FAFB), borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE5E7EB))),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(date, style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.w600)),
      const SizedBox(height: 10),
      Row(children: [
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(depTime, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900)),
          Text(origin,  style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Colors.grey)),
        ]),
        Expanded(child: Column(children: [
          Text(duration, style: const TextStyle(fontSize: 11, color: Colors.grey)),
          Row(children: [
            Container(width: 8, height: 8, decoration: const BoxDecoration(color: Color(0xFFD97706), shape: BoxShape.circle)),
            Expanded(child: Container(height: 2, color: const Color(0xFFD97706))),
            Container(width: 8, height: 8, decoration: const BoxDecoration(color: Color(0xFFD97706), shape: BoxShape.circle)),
          ]),
          Text(direct ? 'Directo' : 'Con escala',
              style: TextStyle(fontSize: 10, color: direct ? AppColors.teal600 : const Color(0xFFD97706), fontWeight: FontWeight.w700)),
        ])),
        Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
          Text(arrTime,     style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900)),
          Text(destination, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Colors.grey)),
        ]),
      ]),
      const SizedBox(height: 10),
      Row(children: [
        const Icon(LucideIcons.plane, size: 13, color: Colors.grey),
        const SizedBox(width: 4),
        Text(airline, style: const TextStyle(fontSize: 11, color: Colors.grey, fontWeight: FontWeight.w600)),
      ]),
    ]),
  );
}

// ── Hoja de confirmación ──────────────────────────────────────────────────────
class _ConfirmSheet extends StatelessWidget {
  final IconData icon; final Color iconColor;
  final String title, message, confirmLabel; final bool danger;
  const _ConfirmSheet({required this.icon, required this.iconColor,
    required this.title, required this.message, required this.confirmLabel, required this.danger});

  @override
  Widget build(BuildContext context) => Container(
    margin: const EdgeInsets.all(12),
    decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(28)),
    padding: const EdgeInsets.all(24),
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      Container(width: 40, height: 4, margin: const EdgeInsets.only(bottom: 20),
          decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),
      Icon(icon, size: 44, color: iconColor),
      const SizedBox(height: 12),
      Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900)),
      const SizedBox(height: 8),
      Text(message, style: const TextStyle(color: Colors.grey, fontSize: 13), textAlign: TextAlign.center),
      const SizedBox(height: 24),
      Row(children: [
        Expanded(child: OutlinedButton(
          onPressed: () => Navigator.pop(context, false),
          style: OutlinedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 14),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14))),
          child: const Text('Cancelar', style: TextStyle(fontWeight: FontWeight.w700)),
        )),
        const SizedBox(width: 12),
        Expanded(child: ElevatedButton(
          onPressed: () => Navigator.pop(context, true),
          style: ElevatedButton.styleFrom(
              backgroundColor: danger ? const Color(0xFFDC2626) : AppColors.teal600,
              foregroundColor: Colors.white, padding: const EdgeInsets.symmetric(vertical: 14),
              minimumSize: const Size(0, 0), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14))),
          child: Text(confirmLabel, style: const TextStyle(fontWeight: FontWeight.w900)),
        )),
      ]),
      const SizedBox(height: 4),
    ]),
  );
}