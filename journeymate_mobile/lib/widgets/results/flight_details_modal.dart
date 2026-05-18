import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';

import '../../core/app_colors.dart';
import '../../services/api_service.dart';
import '../../services/auth_service.dart';
import '../../utils/date_utils.dart';

class FlightDetailsModal extends StatefulWidget {
  final Map<String, dynamic> details;
  final VoidCallback onClose;

  const FlightDetailsModal({
    super.key,
    required this.details,
    required this.onClose,
  });

  @override
  State<FlightDetailsModal> createState() => _FlightDetailsModalState();
}

class _FlightDetailsModalState extends State<FlightDetailsModal> {
  bool   _isReserving = false;
  bool   _isBooked    = false;
  String _error       = '';

  // ── Datos extraídos del DTO ────────────────────────────────────────────────
  late final Map<String, dynamic> _data;
  late final List<dynamic>        _segments;
  late final bool                 _isRoundTrip;
  late final Map<String, dynamic> _price;
  late final String               _aerolinea, _origen, _destino;
  late final double               _precioTotal;
  late final String               _currency;

  @override
  void initState() {
    super.initState();
    _data        = Map<String, dynamic>.from(widget.details['data'] ?? {});
    _segments    = (_data['segments']  as List? ?? []);
    _isRoundTrip = (_data['tripType']  ?? '') == 'ROUNDTRIP';
    _price       = Map<String, dynamic>.from(_data['priceBreakdown'] ?? {});

    final primerSeg = _segments.isNotEmpty ? _segments[0] as Map : {};
    final primerLeg = (primerSeg['legs'] as List? ?? []).isNotEmpty
        ? primerSeg['legs'][0] as Map : {};
    final carrier   = (primerLeg['carriersData'] as List? ?? []).isNotEmpty
        ? primerLeg['carriersData'][0] as Map : {};

    _aerolinea   = (carrier['name']   ?? 'Aerolínea').toString();
    _origen      = (primerSeg['departureAirport']?['code'] ?? '').toString();
    _destino     = (primerSeg['arrivalAirport']?['code']   ?? '').toString();
    _precioTotal = _toDouble(_price['total']?['units']) ?? 0.0;
    _currency    = (_price['total']?['currencyCode'] ?? 'EUR').toString();
  }

  // ── Helpers ────────────────────────────────────────────────────────────────
  String _fmtTime(String? d) {
    if (d == null || d.isEmpty) return '--:--';
    try {
      final dt = DateTime.parse(d);
      return '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
    } catch (_) { return '--:--'; }
  }

  String _fmtDate(String? d) {
    if (d == null || d.isEmpty) return '';
    try {
      final dt = DateTime.parse(d);
      const days   = ['lunes','martes','miércoles','jueves','viernes','sábado','domingo'];
      const months = ['enero','febrero','marzo','abril','mayo','junio',
        'julio','agosto','septiembre','octubre','noviembre','diciembre'];
      return '${days[dt.weekday - 1]}, ${dt.day} de ${months[dt.month - 1]}';
    } catch (_) { return d; }
  }

  String _calcDuration(dynamic totalTime) {
    final secs = _toDouble(totalTime)?.toInt() ?? 0;
    if (secs == 0) return '0h 0min';
    final mins = secs ~/ 60;
    return '${mins ~/ 60}h ${mins % 60}min';
  }

  double? _toDouble(dynamic v) {
    if (v == null) return null;
    if (v is num)    return v.toDouble();
    return double.tryParse(v.toString());
  }

  String formatToLocalDate(dynamic dateStr) {
    if (dateStr == null || dateStr.toString().isEmpty) return '';
    return dateStr.toString().substring(0, 10);
  }

  double parsePrecio(dynamic valor) {
    if (valor == null) return 0.0;
    String str = valor.toString()
        .replaceAll(RegExp(r'[^0-9.,]'), '')
        .replaceAll(',', '.');
    return double.tryParse(str) ?? 0.0;
  }

  // ── Reserva ────────────────────────────────────────────────────────────────
  Future<void> _handleReserve() async {
    if (_isReserving || _isBooked) return;

    setState(() {
      _isReserving = true;
      _error = '';
    });

    try {
      // 1. Verificación de sesión (igual que en hoteles)
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

      // 2. Extraer datos del vuelo desde widget.details
      final data = widget.details['data'];
      final segments = data['segments'] as List? ?? [];
      final firstSeg = segments.isNotEmpty ? segments.first : {};

      // 3. Construir el BODY exacto que me pasaste
      final body = {
        'idUsuario':     idUsuario,
        'idTipoReserva': 3,
        'idEstado':      1,
        'precioTotal':   _precioTotal,
        'fechaServicio': formatToLocalDate(firstSeg['departureTime']),
        'servicio': {
          'tipo':       'VUELO',
          'nombre':     '${firstSeg['marketingCarrier']?['name'] ?? 'Vuelo'} · $_origen ${_isRoundTrip ? '⇄' : '→'} $_destino',
          'precioBase':  _precioTotal,
          'ciudad':      firstSeg['departureAirport']?['cityName'] ?? 'N/A',
          'compania':    _aerolinea,
          'origen':      _origen,
          'destino':     _destino,
          'fechaSalida':  formatToLocalDate(firstSeg['departureTime']),
          'fechaRegreso': _isRoundTrip ? formatToLocalDate(segments.last['arrivalTime']) : null,
        },
      };

      // 4. Llamada al ApiService usando la instancia global 'api'
      await api.createReserva(body);

      setState(() {
        _isBooked = true;
        _isReserving = false;
      });

    } on ApiException catch (e) {
      setState(() {
        _isReserving = false;
        _error = e.statusCode == 401
            ? 'Sesión expirada. Vuelve a iniciar sesión.'
            : 'Error al reservar. Inténtalo de nuevo.';
      });
    } catch (e) {
      setState(() {
        _isReserving = false;
        _error = 'No se pudo conectar con el servidor.';
      });
    }
  }

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
              ..._segments.asMap().entries.map((e) => _buildSegment(e.key, e.value as Map)),
              const SizedBox(height: 16),
              if (_data['carbonEmissions']?['footprintForOffer']?['quantity'] != null)
                _buildCO2(),
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

  // ── Header verde oscuro ────────────────────────────────────────────────────
  Widget _buildHeader() => Container(
    padding: const EdgeInsets.fromLTRB(24, 48, 24, 24),
    decoration: const BoxDecoration(
      gradient: LinearGradient(
          begin: Alignment.topLeft, end: Alignment.bottomRight,
          colors: [AppColors.teal900, AppColors.teal700]),
    ),
    child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: Colors.white.withOpacity(0.1))),
              child: const Icon(LucideIcons.plane, size: 22, color: Color(0xFF99F6E4))),
          const SizedBox(width: 14),
          const Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text('ITINERARIO DE VUELO', style: TextStyle(fontSize: 18,
                fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: -0.5)),
            Text('Revisa los detalles antes de confirmar',
                style: TextStyle(fontSize: 9, color: Color(0xFF99F6E4),
                    fontWeight: FontWeight.w700, letterSpacing: 1)),
          ]),
        ]),
        const SizedBox(height: 16),
        // Badges: tipo de viaje + aerolínea + ruta
        Wrap(spacing: 6, runSpacing: 6, children: [
          _headerBadge(
            _isRoundTrip ? LucideIcons.arrowRight : LucideIcons.arrowRight,
            _isRoundTrip ? 'IDA Y VUELTA' : 'SOLO IDA',
          ),
          _headerBadge(LucideIcons.ticket, _aerolinea.toUpperCase()),
          _headerBadge(LucideIcons.mapPin, '$_origen → $_destino'),
        ]),
      ])),
      // Botón cerrar
      GestureDetector(
        onTap: widget.onClose,
        child: Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1), shape: BoxShape.circle),
            child: const Icon(LucideIcons.x, color: Colors.white, size: 18)),
      ),
    ]),
  );

  Widget _headerBadge(IconData icon, String label) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
    decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.15),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.white.withOpacity(0.1))),
    child: Row(mainAxisSize: MainAxisSize.min, children: [
      Icon(icon, size: 10, color: const Color(0xFF99F6E4)),
      const SizedBox(width: 4),
      Text(label, style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
          color: Color(0xFFCCFBF1), letterSpacing: 0.5)),
    ]),
  );

  // ── Segmento (salida o regreso) ────────────────────────────────────────────
  Widget _buildSegment(int idx, Map seg) {
    final legs       = (seg['legs'] as List? ?? []);
    final depAirport = (seg['departureAirport'] as Map? ?? {});
    final arrAirport = (seg['arrivalAirport']   as Map? ?? {});
    final cabinLug   = (seg['travellerCabinLuggage']   as List? ?? []);
    final checkedLug = (seg['travellerCheckedLuggage'] as List? ?? []);

    final cabinInfo   = cabinLug.isNotEmpty   ? (cabinLug[0]['luggageAllowance']   as Map? ?? {}) : <String, dynamic>{};
    final checkedInfo = checkedLug.isNotEmpty ? (checkedLug[0]['luggageAllowance'] as Map? ?? {}) : <String, dynamic>{};

    String _lugText(Map info) {
      final pieces = _toDouble(info['maxPiece'])?.toInt() ?? 0;
      if (pieces == 0) return 'No incluido';
      final w = _toDouble(info['maxWeightPerPiece']);
      final u = info['massUnit'] ?? '';
      return '$pieces Pieza${pieces > 1 ? 's' : ''}${w != null && w > 0 ? ' (${w.toInt()} $u)' : ''}';
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(28),
        border: Border.all(color: const Color(0xFFF3F4F6)),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 12)],
      ),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [

        // Fecha + etiqueta de segmento
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Row(children: [
            const Icon(LucideIcons.calendar, size: 13, color: AppColors.teal500),
            const SizedBox(width: 6),
            Text(_fmtDate(seg['departureTime']?.toString()),
                style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w900,
                    color: AppColors.teal900)),
          ]),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
            decoration: BoxDecoration(color: AppColors.teal50,
                borderRadius: BorderRadius.circular(10)),
            child: Text(idx == 0 ? 'VUELO DE SALIDA' : 'VUELO DE REGRESO',
                style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                    color: AppColors.teal600, letterSpacing: 1)),
          ),
        ]),
        const SizedBox(height: 20),

        // Aeropuertos + duración
        Row(children: [
          // Origen
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(depAirport['code']?.toString() ?? '',
                style: const TextStyle(fontSize: 36, fontWeight: FontWeight.w900,
                    color: AppColors.teal900, letterSpacing: -1, height: 1)),
            Text((depAirport['cityName'] ?? '').toString().toUpperCase(),
                style: const TextStyle(fontSize: 10, color: Color(0xFF9CA3AF),
                    fontWeight: FontWeight.w700, letterSpacing: 0.5),
                maxLines: 1, overflow: TextOverflow.ellipsis),
            Text(_fmtTime(seg['departureTime']?.toString()),
                style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900,
                    color: AppColors.teal600)),
          ])),

          // Línea central + duración
          Expanded(child: Column(children: [
            Row(children: [
              Expanded(child: Container(height: 2, decoration: const BoxDecoration(
                  gradient: LinearGradient(colors: [Colors.transparent, Color(0xFFCCFBF1)])))),
              const Icon(LucideIcons.plane, size: 18, color: AppColors.teal200),
              Expanded(child: Container(height: 2, decoration: const BoxDecoration(
                  gradient: LinearGradient(colors: [Color(0xFFCCFBF1), Colors.transparent])))),
            ]),
            const SizedBox(height: 6),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(color: AppColors.teal50,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: AppColors.teal100.withOpacity(0.5))),
              child: Row(mainAxisSize: MainAxisSize.min, children: [
                const Icon(LucideIcons.clock, size: 11, color: AppColors.teal600),
                const SizedBox(width: 4),
                Text(_calcDuration(seg['totalTime']),
                    style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
                        color: AppColors.teal700)),
              ]),
            ),
          ])),

          // Destino
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
            Text(arrAirport['code']?.toString() ?? '',
                style: const TextStyle(fontSize: 36, fontWeight: FontWeight.w900,
                    color: AppColors.teal900, letterSpacing: -1, height: 1)),
            Text((arrAirport['cityName'] ?? '').toString().toUpperCase(),
                style: const TextStyle(fontSize: 10, color: Color(0xFF9CA3AF),
                    fontWeight: FontWeight.w700, letterSpacing: 0.5),
                maxLines: 1, overflow: TextOverflow.ellipsis, textAlign: TextAlign.end),
            Text(_fmtTime(seg['arrivalTime']?.toString()),
                style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900,
                    color: AppColors.teal600)),
          ])),
        ]),
        const SizedBox(height: 20),

        // Legs (escalas individuales)
        ...legs.map((leg) => _buildLeg(leg as Map)),
        const SizedBox(height: 16),

        // Equipaje
        Row(children: [
          Expanded(child: _luggageBox(LucideIcons.briefcase, 'MANO',      _lugText(cabinInfo))),
          const SizedBox(width: 8),
          Expanded(child: _luggageBox(LucideIcons.shieldCheck,'FACTURADO', _lugText(checkedInfo))),
        ]),
      ]),
    );
  }

  // ── Una pierna (vuelo individual dentro del segmento) ─────────────────────
  Widget _buildLeg(Map leg) {
    final carriers  = (leg['carriersData'] as List? ?? []);
    final carrier   = carriers.isNotEmpty ? carriers[0] as Map : {};
    final logoUrl   = carrier['logo']?.toString();
    final nombre    = (carrier['name'] ?? '').toString();
    final flightNum = leg['flightInfo']?['flightNumber']?.toString() ?? '';
    final cabin     = (leg['cabinClass'] ?? '').toString();
    final plane     = leg['flightInfo']?['planeType']?.toString();

    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
          color: const Color(0xFFF9FAFB),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: const Color(0xFFF3F4F6))),
      child: Row(children: [

        // Logo aerolínea
        Container(
          width: 38, height: 38,
          decoration: BoxDecoration(color: Colors.white,
              borderRadius: BorderRadius.circular(10),
              border: Border.all(color: const Color(0xFFE5E7EB))),
          child: logoUrl != null
              ? ClipRRect(borderRadius: BorderRadius.circular(9),
              child: Image.network(logoUrl, fit: BoxFit.contain,
                  errorBuilder: (_, __, ___) =>
                  const Icon(LucideIcons.plane, size: 18, color: AppColors.teal300)))
              : const Icon(LucideIcons.plane, size: 18, color: AppColors.teal300),
        ),
        const SizedBox(width: 10),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            Text(nombre.toUpperCase(), style: const TextStyle(fontSize: 10,
                fontWeight: FontWeight.w900, color: AppColors.teal900)),
            const SizedBox(width: 6),
            if (flightNum.isNotEmpty)
              Container(
                  padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 2),
                  decoration: BoxDecoration(color: AppColors.teal50,
                      borderRadius: BorderRadius.circular(4)),
                  child: Text('#$flightNum', style: const TextStyle(fontSize: 8,
                      fontWeight: FontWeight.w700, color: AppColors.teal700))),
          ]),
          Text(
              '${_fmtTime(leg['departureTime']?.toString())} ${leg['departureAirport']?['code'] ?? ''}'
                  ' → ${_fmtTime(leg['arrivalTime']?.toString())} ${leg['arrivalAirport']?['code'] ?? ''}',
              style: const TextStyle(fontSize: 9, color: Color(0xFF6B7280),
                  fontWeight: FontWeight.w600)),
        ])),
        Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
          Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
              decoration: BoxDecoration(color: Colors.white,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: AppColors.teal100)),
              child: Text(cabin.toUpperCase(), style: const TextStyle(fontSize: 8,
                  fontWeight: FontWeight.w900, color: AppColors.teal600))),
          if (plane != null)
            Padding(padding: const EdgeInsets.only(top: 3),
                child: Text('Avión: $plane', style: const TextStyle(fontSize: 8,
                    color: Color(0xFF9CA3AF), fontWeight: FontWeight.w600))),
        ]),
      ]),
    );
  }

  Widget _luggageBox(IconData icon, String label, String value) => Container(
    padding: const EdgeInsets.all(12),
    decoration: BoxDecoration(
        color: AppColors.teal50.withOpacity(0.5),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: AppColors.teal100.withOpacity(0.3))),
    child: Row(children: [
      Container(padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(8)),
          child: Icon(icon, size: 13, color: AppColors.teal600)),
      const SizedBox(width: 8),
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(label, style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
            color: AppColors.teal400, letterSpacing: 1)),
        Text(value, style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
            color: AppColors.teal900)),
      ])),
    ]),
  );

  // ── CO2 ───────────────────────────────────────────────────────────────────
  Widget _buildCO2() {
    final co2 = _data['carbonEmissions']?['footprintForOffer'] as Map? ?? {};
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
          color: const Color(0xFFF0FDF4),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: const Color(0xFFBBF7D0))),
      child: Row(children: [
        Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(color: const Color(0xFF16A34A),
                borderRadius: BorderRadius.circular(10)),
            child: const Icon(LucideIcons.star, size: 16, color: Colors.white)),
        const SizedBox(width: 12),
        const Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text('VUELO MÁS SOSTENIBLE', style: TextStyle(fontSize: 9,
              fontWeight: FontWeight.w900, color: Color(0xFF166534), letterSpacing: 0.5)),
          Text('Huella de CO₂ estimada', style: TextStyle(fontSize: 9,
              color: Color(0xFF16A34A), fontWeight: FontWeight.w600)),
        ])),
        Text('${co2['quantity']} ${co2['unit'] ?? 'kg'}',
            style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900,
                color: Color(0xFF15803D))),
      ]),
    );
  }

  // ── Bloque de precio desglosado ────────────────────────────────────────────
  Widget _buildPriceBlock() {
    final base     = _price['baseFare']  as Map? ?? {};
    final fee      = _price['fee']       as Map? ?? {};
    final tax      = _price['tax']       as Map? ?? {};
    final discount = _price['discount']  as Map? ?? {};
    final total    = _price['total']     as Map? ?? {};

    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
          color: AppColors.teal900,
          borderRadius: BorderRadius.circular(32),
          boxShadow: [BoxShadow(color: AppColors.teal900.withOpacity(0.4), blurRadius: 30)]),
      child: Stack(children: [
        // Ticket decorativo de fondo
        Positioned(top: -10, right: -10,
            child: Icon(LucideIcons.ticket, size: 100, color: Colors.white.withOpacity(0.06))),
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Text('DETALLE DEL PAGO', style: TextStyle(fontSize: 9,
              fontWeight: FontWeight.w900, color: AppColors.teal400, letterSpacing: 2)),
          const SizedBox(height: 16),
          _priceRow('TARIFA BASE', base['units'], base['currencyCode']),
          if ((_toDouble(fee['units']) ?? 0) > 0)
            _priceRow('CARGOS / FEES', fee['units'], fee['currencyCode']),
          _priceRow('IMPUESTOS', tax['units'], tax['currencyCode']),
          if ((_toDouble(discount['units']) ?? 0) > 0)
            _priceRow('DESCUENTO', discount['units'], discount['currencyCode'],
                isDiscount: true),
          const Divider(color: Colors.white12, height: 24),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.end, children: [
                const Text('PRECIO FINAL', style: TextStyle(fontSize: 9,
                    fontWeight: FontWeight.w900, color: AppColors.teal400, letterSpacing: 2)),
                Text(formatCurrency(_toDouble(total['units']) ?? 0, total['currencyCode'] ?? _currency),
                    style: const TextStyle(fontSize: 40, fontWeight: FontWeight.w900,
                        color: Colors.white, letterSpacing: -1)),
              ]),
        ]),
      ]),
    );
  }

  Widget _priceRow(String label, dynamic units, dynamic cur, {bool isDiscount = false}) => Padding(
    padding: const EdgeInsets.symmetric(vertical: 3),
    child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
      Text(label, style: TextStyle(fontSize: 10, fontWeight: FontWeight.w700,
          color: isDiscount ? const Color(0xFF4ADE80) : Colors.white54)),
      Text('${isDiscount ? '-' : ''}${formatCurrency(_toDouble(units) ?? 0, cur?.toString() ?? _currency)}',
          style: TextStyle(fontSize: 10, fontWeight: FontWeight.w900,
              color: isDiscount ? const Color(0xFF4ADE80) : Colors.white)),
    ]),
  );

  // ── Footer: error + botones ───────────────────────────────────────────────
  Widget _buildFooter() => Container(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
    decoration: const BoxDecoration(
        color: Colors.white,
        border: Border(top: BorderSide(color: Color(0xFFF3F4F6)))),
    child: SafeArea(top: false, child: Column(mainAxisSize: MainAxisSize.min, children: [
      if (_error.isNotEmpty)
        Container(
            margin: const EdgeInsets.only(bottom: 10),
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            decoration: BoxDecoration(color: const Color(0xFFFEF2F2),
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: const Color(0xFFFECACA))),
            child: Text(_error, style: const TextStyle(fontSize: 11,
                fontWeight: FontWeight.w700, color: Color(0xFFB91C1C)),
                textAlign: TextAlign.center)),

      Row(children: [
        // Cerrar
        Expanded(child: GestureDetector(
          onTap: widget.onClose,
          child: const Padding(padding: EdgeInsets.symmetric(vertical: 16),
              child: Text('Cerrar', textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 11, fontWeight: FontWeight.w900,
                      color: Color(0xFF9CA3AF), letterSpacing: 1))),
        )),

        // Reservar
        Expanded(flex: 2, child: GestureDetector(
          onTap: _handleReserve,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            margin: const EdgeInsets.only(left: 8, bottom: 8),
            padding: const EdgeInsets.symmetric(vertical: 18),
            decoration: BoxDecoration(
                color: _isBooked ? const Color(0xFF16A34A) : AppColors.teal600,
                borderRadius: BorderRadius.circular(24),
                boxShadow: [BoxShadow(
                    color: (_isBooked ? const Color(0xFF16A34A) : AppColors.teal600)
                        .withOpacity(0.3), blurRadius: 12, offset: const Offset(0, 4))]),
            child: _isReserving
                ? const Center(child: SizedBox(width: 20, height: 20,
                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2)))
                : Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              Text(_isBooked ? '¡RESERVA CREADA!' : 'RESERVAR ITINERARIO',
                  style: const TextStyle(color: Colors.white, fontSize: 10,
                      fontWeight: FontWeight.w900, letterSpacing: 1.5)),
              const SizedBox(width: 8),
              Icon(_isBooked ? LucideIcons.checkCircle
                  : (_isRoundTrip ? LucideIcons.arrowRight : LucideIcons.arrowRight),
                  size: 15, color: Colors.white),
            ]),
          ),
        )),
        if (_error.isNotEmpty)
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(_error, style: const TextStyle(color: Colors.red, fontSize: 12)),
          ),
      ]),
    ])),
  );
}