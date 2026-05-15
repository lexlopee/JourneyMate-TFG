import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../services/auth_service.dart';
import '../../services/api_service.dart';
import '../../utils/date_utils.dart';

// ══════════════════════════════════════════════════════════════════════════════
// CAR DETAILS MODAL — Equivalente a CarDetailsModal.tsx
// ══════════════════════════════════════════════════════════════════════════════
class CarDetailsModal extends StatefulWidget {
  final Map<String, dynamic> car;
  final Map<String, dynamic> searchData;

  const CarDetailsModal({
    super.key,
    required this.car,
    required this.searchData,
  });

  @override
  State<CarDetailsModal> createState() => _CarDetailsModalState();
}

class _CarDetailsModalState extends State<CarDetailsModal> {
  bool   _isReserving = false;
  bool   _isBooked    = false;
  String _error       = '';

  // ── Helpers ────────────────────────────────────────────────────────────────
  int get _days => daysBetween(
      widget.searchData['startDate'], widget.searchData['endDate']);

  double get _price =>
      double.tryParse((widget.car['price'] ?? 0).toString()) ?? 0.0;

  String get _currency => (widget.car['currency'] ?? 'EUR').toString();

  String _fmtPrice(double v) => formatCurrency(v, _currency);

  String _fmtDate(String? d) {
    if (d == null || d.isEmpty) return '—';
    try {
      final dt = DateTime.parse(d);
      const months = ['ene','feb','mar','abr','may','jun',
        'jul','ago','sep','oct','nov','dic'];
      const days   = ['lun','mar','mié','jue','vie','sáb','dom'];
      return '${days[dt.weekday - 1]}, ${dt.day} ${months[dt.month - 1]} ${dt.year}';
    } catch (_) { return d; }
  }

  // Añade :00 si el string es HH:mm — igual que formatWithSeconds en React
  String _withSeconds(String? t) {
    if (t == null || t.isEmpty) return '10:00:00';
    return t.length == 5 ? '$t:00' : t;
  }

  // ── Reserva — mismo patrón que Hotel y Vuelo ──────────────────────────────
  Future<void> _handleReserve() async {
    if (_isReserving || _isBooked) return;
    setState(() { _isReserving = true; _error = ''; });

    try {
      final isLogged = await AuthService.isLoggedIn();
      if (!isLogged) {
        setState(() => _error = 'Debes iniciar sesión para reservar.');
        return;
      }

      final idUsuario = await AuthService.getIdUsuario();
      if (idUsuario == null) {
        setState(() => _error = 'No se pudo obtener el usuario. Inicia sesión de nuevo.');
        return;
      }

      if (_price == 0) {
        setState(() => _error = 'No se pudo obtener el precio.');
        return;
      }

      final startDate    = widget.searchData['startDate']  ?? '';
      final endDate      = widget.searchData['endDate']    ?? '';
      final pickupTime   = _withSeconds(widget.searchData['pickupTime']  ?? widget.searchData['pickUpTime']  ?? '10:00');
      final dropoffTime  = _withSeconds(widget.searchData['dropoffTime'] ?? widget.searchData['dropOffTime'] ?? '10:00');

      // Estructura idéntica al React: CarDetailsModal.handleReserve
      final body = {
        'idUsuario':     idUsuario,
        'idTipoReserva': 5,
        'idEstado':      1,
        'precioTotal':   _price,
        'fechaServicio': startDate,
        'servicio': {
          'tipo':        'COCHE',
          'nombre':      widget.car['carName'] ?? 'Coche de alquiler',
          'precioBase':  _price,
          'descripcion': '${widget.car['vendorName'] ?? ''} · '
              '${widget.car['transmission'] ?? ''} · '
              '${widget.car['seats'] ?? 5} plazas',
          'marca':       widget.car['vendorName'],
          'modelo':      widget.car['carName'],
          'horaSalida':  '$startDate $pickupTime',
          'horaLlegada': '$endDate $dropoffTime',
          'fechaSalida': startDate,
          'distancia':   0,
          'latitud':     null,
          'longitud':    null,
        },
      };

      await api.createReserva(body);
      setState(() => _isBooked = true);

    } on ApiException catch (e) {
      setState(() => _error = e.statusCode == 401
          ? 'Sesión expirada. Vuelve a iniciar sesión.'
          : 'Error al reservar (${e.statusCode}). Inténtalo de nuevo.');
    } catch (_) {
      setState(() => _error = 'No se pudo conectar con el servidor.');
    } finally {
      if (mounted) setState(() => _isReserving = false);
    }
  }

  // ══════════════════════════════════════════════════════════════════════════
  // BUILD
  // ══════════════════════════════════════════════════════════════════════════
  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.92,
      minChildSize: 0.5,
      maxChildSize: 0.96,
      builder: (_, scrollCtrl) => Container(
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(40)),
        ),
        clipBehavior: Clip.antiAlias,
        child: Column(children: [
          _buildHeader(),
          Expanded(child: ListView(
            controller: scrollCtrl,
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
            children: [
              _buildCarPreview(),
              const SizedBox(height: 16),
              _buildSpecsGrid(),
              const SizedBox(height: 16),
              _buildDatesRow(),
              const SizedBox(height: 16),
              _buildPriceBlock(),
              const SizedBox(height: 110),
            ],
          )),
          _buildFooter(),
        ]),
      ),
    );
  }

  // ── Header degradado teal (= vuelos) ──────────────────────────────────────
  Widget _buildHeader() => Container(
    padding: const EdgeInsets.fromLTRB(24, 48, 24, 24),
    decoration: const BoxDecoration(
      gradient: LinearGradient(
          begin: Alignment.topLeft, end: Alignment.bottomRight,
          colors: [AppColors.teal900, AppColors.teal700]),
    ),
    child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.1),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: Colors.white.withOpacity(0.1))),
          child: const Icon(LucideIcons.car, size: 22, color: Color(0xFF99F6E4))),
      const SizedBox(width: 14),
      const Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text('RESUMEN DEL ALQUILER', style: TextStyle(fontSize: 18,
            fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: -0.5)),
        Text('Revisa los detalles antes de confirmar',
            style: TextStyle(fontSize: 9, color: Color(0xFF99F6E4),
                fontWeight: FontWeight.w700, letterSpacing: 1)),
      ])),
      GestureDetector(
        onTap: () => Navigator.pop(context),
        child: Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1), shape: BoxShape.circle),
            child: const Icon(LucideIcons.x, color: Colors.white, size: 18)),
      ),
    ]),
  );

  // ── Imagen + nombre + proveedor ───────────────────────────────────────────
  Widget _buildCarPreview() => Container(
    padding: const EdgeInsets.all(20),
    decoration: BoxDecoration(
        color: const Color(0xFFF9FAFB),
        borderRadius: BorderRadius.circular(24)),
    child: Row(children: [
      // Imagen
      SizedBox(width: 120, height: 80,
        child: widget.car['imageUrl'] != null
            ? Image.network(widget.car['imageUrl'], fit: BoxFit.contain,
            errorBuilder: (_, __, ___) =>
            const Icon(LucideIcons.car, size: 40, color: AppColors.teal300))
            : Container(
            decoration: BoxDecoration(color: AppColors.teal50,
                borderRadius: BorderRadius.circular(16)),
            child: const Center(child: Icon(LucideIcons.car,
                size: 40, color: AppColors.teal500))),
      ),
      const SizedBox(width: 16),
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Text('VEHÍCULO', style: TextStyle(fontSize: 8,
            fontWeight: FontWeight.w900, color: AppColors.teal500,
            letterSpacing: 1.5)),
        const SizedBox(height: 4),
        Text((widget.car['carName'] ?? '').toString().toUpperCase(),
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900,
                color: AppColors.teal900, letterSpacing: -0.5, height: 1.1),
            maxLines: 2, overflow: TextOverflow.ellipsis),
        const SizedBox(height: 6),
        Row(children: [
          const Icon(LucideIcons.building, size: 12, color: AppColors.teal400),
          const SizedBox(width: 4),
          Text((widget.car['vendorName'] ?? '').toString(),
              style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700,
                  color: AppColors.teal600)),
        ]),
      ])),
    ]),
  );

  // ── Grid de specs (igual que CarDetailsModal.tsx) ─────────────────────────
  Widget _buildSpecsGrid() {
    final isAutomatic = (widget.car['transmission'] ?? '')
        .toString().toLowerCase().contains('auto');

    final specs = [
      (LucideIcons.users,      'PLAZAS',      '${widget.car['seats'] ?? 5}'),
      (LucideIcons.briefcase,  'MALETAS',     '${widget.car['bags'] ?? 2}'),
      (LucideIcons.settings,  'TRANSMISIÓN', isAutomatic ? 'Auto' : 'Manual'),
      (LucideIcons.info,       'COMBUSTIBLE', 'Lleno'),
      (LucideIcons.shieldCheck,'SEGURO',      'Básico incluido'),
    ];

    return GridView.count(
      crossAxisCount: 3,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 8,
      mainAxisSpacing: 8,
      childAspectRatio: 1.2,
      children: specs.map((s) => Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
            color: AppColors.teal50,
            borderRadius: BorderRadius.circular(20)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              Icon(s.$1, size: 16, color: AppColors.teal500),
              Text(s.$2, style: const TextStyle(fontSize: 8,
                  fontWeight: FontWeight.w900, color: AppColors.teal400,
                  letterSpacing: 1)),
              Text(s.$3, style: const TextStyle(fontSize: 11,
                  fontWeight: FontWeight.w900, color: AppColors.teal900)),
            ]),
      )).toList(),
    );
  }

  // ── Fechas recogida / devolución ──────────────────────────────────────────
  Widget _buildDatesRow() => Row(children: [
    Expanded(child: _dateBox(
      'RECOGIDA',
      widget.searchData['startDate'],
      widget.searchData['pickupTime'] ?? widget.searchData['pickUpTime'] ?? '10:00',
    )),
    const SizedBox(width: 8),
    Expanded(child: _dateBox(
      'DEVOLUCIÓN',
      widget.searchData['endDate'],
      widget.searchData['dropoffTime'] ?? widget.searchData['dropOffTime'] ?? '10:00',
    )),
  ]);

  Widget _dateBox(String label, String? date, String time) => Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
        color: AppColors.teal50,
        borderRadius: BorderRadius.circular(20)),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Row(children: [
        const Icon(LucideIcons.calendar, size: 13, color: AppColors.teal500),
        const SizedBox(width: 6),
        Text(label, style: const TextStyle(fontSize: 8,
            fontWeight: FontWeight.w900, color: AppColors.teal400,
            letterSpacing: 1)),
      ]),
      const SizedBox(height: 6),
      Text(_fmtDate(date), style: const TextStyle(fontSize: 11,
          fontWeight: FontWeight.w900, color: AppColors.teal900)),
      const SizedBox(height: 4),
      Row(children: [
        const Icon(LucideIcons.clock, size: 10, color: AppColors.teal400),
        const SizedBox(width: 4),
        Text(time, style: const TextStyle(fontSize: 9,
            fontWeight: FontWeight.w700, color: AppColors.teal600)),
      ]),
    ]),
  );

  // ── Desglose del precio (fondo oscuro = CarDetailsModal.tsx) ──────────────
  Widget _buildPriceBlock() {
    final pricePerDay = _days > 0 ? _price / _days : _price;

    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
          color: AppColors.teal900,
          borderRadius: BorderRadius.circular(28),
          boxShadow: [BoxShadow(
              color: AppColors.teal900.withOpacity(0.4), blurRadius: 24)]),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Text('DESGLOSE DEL PRECIO', style: TextStyle(fontSize: 9,
            fontWeight: FontWeight.w900, color: AppColors.teal400,
            letterSpacing: 2)),
        const SizedBox(height: 16),

        _priceRow('PRECIO POR DÍA',
            _fmtPrice(pricePerDay)),
        const SizedBox(height: 6),
        Container(height: 1, color: Colors.white12),
        const SizedBox(height: 6),
        _priceRow('$_days ${_days == 1 ? 'DÍA' : 'DÍAS'}',
            _fmtPrice(_price)),

        Container(margin: const EdgeInsets.symmetric(vertical: 16),
            height: 1, color: Colors.white12),

        const Text('TOTAL', style: TextStyle(fontSize: 9,
            fontWeight: FontWeight.w900, color: AppColors.teal400,
            letterSpacing: 2)),
        const SizedBox(height: 4),
        Text(_fmtPrice(_price), style: const TextStyle(fontSize: 40,
            fontWeight: FontWeight.w900, color: Colors.white,
            letterSpacing: -1)),
      ]),
    );
  }

  Widget _priceRow(String label, String value) => Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(label, style: const TextStyle(fontSize: 10,
          fontWeight: FontWeight.w700, color: Colors.white54)),
      Text(value, style: const TextStyle(fontSize: 10,
          fontWeight: FontWeight.w900, color: Colors.white)),
    ],
  );

  // ── Footer: error + cancelar + confirmar ──────────────────────────────────
  Widget _buildFooter() => Container(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
    decoration: const BoxDecoration(
        color: Colors.white,
        border: Border(top: BorderSide(color: Color(0xFFF3F4F6)))),
    child: SafeArea(top: false, child: Column(mainAxisSize: MainAxisSize.min,
        children: [

          // Error con link a login si no está autenticado
          if (_error.isNotEmpty)
            Container(
              margin: const EdgeInsets.only(bottom: 10),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              decoration: BoxDecoration(
                  color: const Color(0xFFFEF2F2),
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(color: const Color(0xFFFECACA))),
              child: Column(children: [
                Text(_error, style: const TextStyle(fontSize: 11,
                    fontWeight: FontWeight.w700, color: Color(0xFFB91C1C)),
                    textAlign: TextAlign.center),
                if (_error.contains('sesión')) ...[
                  const SizedBox(height: 6),
                  GestureDetector(
                    onTap: () { Navigator.pop(context); context.go('/login'); },
                    child: const Text('→ Ir a iniciar sesión',
                        style: TextStyle(color: Color(0xFF991B1B), fontSize: 11,
                            fontWeight: FontWeight.w900,
                            decoration: TextDecoration.underline)),
                  ),
                ],
              ]),
            ),

          Row(children: [
            // Cancelar
            Expanded(child: GestureDetector(
              onTap: () => Navigator.pop(context),
              child: const Padding(padding: EdgeInsets.symmetric(vertical: 16),
                  child: Text('Cancelar', textAlign: TextAlign.center,
                      style: TextStyle(fontSize: 11, fontWeight: FontWeight.w900,
                          color: Color(0xFF9CA3AF), letterSpacing: 1))),
            )),

            // Confirmar alquiler
            Expanded(flex: 2, child: GestureDetector(
              onTap: _handleReserve,
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                margin: const EdgeInsets.only(left: 8, bottom: 8),
                padding: const EdgeInsets.symmetric(vertical: 18),
                decoration: BoxDecoration(
                    color: _isBooked
                        ? const Color(0xFF16A34A)
                        : _isReserving
                        ? AppColors.teal700
                        : AppColors.teal600,
                    borderRadius: BorderRadius.circular(24),
                    boxShadow: [BoxShadow(
                        color: AppColors.teal600.withOpacity(0.3),
                        blurRadius: 12, offset: const Offset(0, 4))]),
                child: _isReserving
                    ? const Center(child: SizedBox(width: 20, height: 20,
                    child: CircularProgressIndicator(
                        color: Colors.white, strokeWidth: 2)))
                    : Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Text(
                      _isBooked ? '¡RESERVADO!' : 'CONFIRMAR ALQUILER',
                      style: const TextStyle(color: Colors.white,
                          fontSize: 10, fontWeight: FontWeight.w900,
                          letterSpacing: 1.5)),
                  const SizedBox(width: 8),
                  Icon(
                      _isBooked ? LucideIcons.checkCircle : LucideIcons.car,
                      size: 15, color: Colors.white),
                ]),
              ),
            )),
          ]),
        ])),
  );
}