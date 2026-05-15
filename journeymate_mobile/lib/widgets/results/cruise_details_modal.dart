import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../services/auth_service.dart';
import '../../services/api_service.dart';
import '../../utils/date_utils.dart';

class CruiseDetailsModal extends StatefulWidget {
  final Map<String, dynamic> cruise;    // datos completos del crucero
  final Map<String, dynamic> searchData;

  const CruiseDetailsModal({
    super.key,
    required this.cruise,
    required this.searchData,
  });

  @override
  State<CruiseDetailsModal> createState() => _CruiseDetailsModalState();
}

class _CruiseDetailsModalState extends State<CruiseDetailsModal> {
  bool   _isReserving = false;
  bool   _isBooked    = false;
  String _error       = '';
  int    _personas    = 1;   // selector de pasajeros, igual que el TSX

  // ── Helpers de datos ────────────────────
  String get _nombre  => (widget.cruise['nombreCrucero'] ?? widget.cruise['cruise_name'] ?? 'Crucero').toString();
  String get _barco   => (widget.cruise['nombreBarco']   ?? widget.cruise['ship_name']   ?? 'N/A').toString();
  String get _puerto  => (widget.cruise['puertoSalida']  ?? widget.cruise['departure_port'] ?? 'N/A').toString();
  int    get _noches  => int.tryParse((widget.cruise['noches'] ?? widget.cruise['duration_nights'] ?? 0).toString()) ?? 0;
  double get _precio  => double.tryParse((widget.cruise['precioDesde'] ?? widget.cruise['price_from'] ?? 0).toString()) ?? 0.0;
  String get _moneda  => (widget.cruise['moneda'] ?? widget.cruise['currency'] ?? 'EUR').toString();
  String get _imagen  => (widget.cruise['imagenPrincipal'] ?? '').toString();
  String get _fecha   => (widget.cruise['fechaSalida'] ?? '').toString();

  List<Map<String, dynamic>> get _paradas {
    final v = widget.cruise['paradas'];
    if (v is List) return v.map((e) => Map<String, dynamic>.from(e as Map)).toList();
    return [];
  }

  List<Map<String, dynamic>> get _cabinas {
    final v = widget.cruise['cabinas'];
    if (v is List) return v.map((e) => Map<String, dynamic>.from(e as Map)).toList();
    return [];
  }

  double get _precioTotal => _precio * _personas;

  String _fmtPrice(double v) => formatCurrency(v, _moneda);

  String _fmtFecha(String d) {
    if (d.isEmpty) return 'N/A';
    try {
      final dt = DateTime.parse('${d}T00:00:00');
      const weekdays = ['lunes', 'martes', 'miércoles', 'jueves', 'viernes', 'sábado', 'domingo'];
      const months   = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
        'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'];
      return '${weekdays[dt.weekday - 1]}, ${dt.day} de ${months[dt.month - 1]} de ${dt.year}';
    } catch (_) { return d; }
  }

  // ── Reserva — mismo patrón que ActivityDetailsModal ───────────────────────
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

      if (_precio == 0) {
        setState(() => _error = 'No se pudo obtener el precio.');
        return;
      }

      // Calcular fecha de llegada = salida + noches + 1
      final fSalida  = DateTime.tryParse('${_fecha}T00:00:00') ?? DateTime.now();
      final fLlegada = fSalida.add(Duration(days: _noches + 1));
      final fechaRegreso = fLlegada.toIso8601String().split('T').first;

      final puertoFinal = _paradas.isNotEmpty
          ? (_paradas.last['puerto'] ?? _puerto).toString()
          : _puerto;

      // Estructura idéntica al TSX: CruiseDetailsModal.handleReserve
      final body = {
        'idUsuario':     idUsuario,
        'idTipoReserva': 2,
        'idEstado':      1,
        'precioTotal':   _precioTotal,
        'fechaServicio': _fecha.isNotEmpty ? _fecha : null,
        'servicio': {
          'tipo':        'CRUCERO',
          'nombre':      _nombre,
          'precioBase':  _precio,
          'descripcion': '$_noches noches · $_personas pasajero(s) · Naviera: $_barco',
          'ciudad':      _puerto,
          'naviera':     _barco,
          'origen':      _puerto,
          'destino':     puertoFinal,
          'fechaSalida': _fecha.isNotEmpty ? _fecha : null,
          'fechaRegreso': fechaRegreso,
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
          // Hero con imagen de fondo
          _buildHero(),
          // Contenido scrollable
          Expanded(child: ListView(
            controller: scrollCtrl,
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
            children: [
              _buildQuickStats(),
              const SizedBox(height: 16),
              if (_paradas.isNotEmpty) ...[
                _buildItinerary(),
                const SizedBox(height: 16),
              ],
              if (_cabinas.isNotEmpty) ...[
                _buildCabins(),
                const SizedBox(height: 16),
              ],
              _buildPriceCard(),
              const SizedBox(height: 110),
            ],
          )),
          // Footer reserva
          _buildFooter(),
        ]),
      ),
    );
  }

  // ── Hero: imagen + degradado + nombre superpuesto ──
  Widget _buildHero() => SizedBox(
    height: 280,
    child: Stack(children: [
      // Imagen o placeholder de barco
      Positioned.fill(child: _imagen.isNotEmpty
          ? Image.network(_imagen, fit: BoxFit.cover, color: Colors.black.withOpacity(0.38), colorBlendMode: BlendMode.darken,
          errorBuilder: (_, __, ___) => _heroBg())
          : _heroBg()),

      // Degradado inferior
      Positioned.fill(child: DecoratedBox(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Colors.transparent, AppColors.teal900.withOpacity(0.9)],
          ),
        ),
      )),

      // Nombre + barco superpuestos
      Positioned(bottom: 20, left: 20, right: 60, child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Naviera badge (≈ <Anchor/> barco en React)
          Row(children: [
            const Icon(LucideIcons.ship, size: 12, color: AppColors.teal300),
            const SizedBox(width: 6),
            Text(_barco.toUpperCase(),
                style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
                    color: AppColors.teal300, letterSpacing: 2)),
          ]),
          const SizedBox(height: 6),
          Text(_nombre.toUpperCase(),
            style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w900,
                color: Colors.white, letterSpacing: -0.5,
                shadows: [Shadow(blurRadius: 8, color: Colors.black45)]),
            maxLines: 2, overflow: TextOverflow.ellipsis,
          ),
        ],
      )),

      // Badge noches
      Positioned(top: 16, right: 60, child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.4),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: Colors.white.withOpacity(0.2)),
        ),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          const Icon(LucideIcons.clock, size: 10, color: Colors.white),
          const SizedBox(width: 4),
          Text('$_noches noches',
              style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: Colors.white)),
        ]),
      )),

      // Botón cerrar
      Positioned(top: 16, right: 16, child: GestureDetector(
        onTap: () => Navigator.pop(context),
        child: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(color: Colors.black.withOpacity(0.3), shape: BoxShape.circle),
          child: const Icon(LucideIcons.x, color: Colors.white, size: 18),
        ),
      )),
    ]),
  );

  // Placeholder cuando no hay imagen
  Widget _heroBg() => Container(
    decoration: const BoxDecoration(
      gradient: LinearGradient(
        colors: [AppColors.teal800, AppColors.teal600],
        begin: Alignment.topLeft,
        end: Alignment.bottomRight,
      ),
    ),
    child: const Center(child: Icon(LucideIcons.ship, size: 80, color: Color(0x33FFFFFF))),
  );

  // ── Quick stats: noches / puerto / fecha ───────────
  Widget _buildQuickStats() => Row(children: [
    Expanded(child: _statBox(LucideIcons.clock,   'NOCHES',   '$_noches')),
    const SizedBox(width: 8),
    Expanded(child: _statBox(LucideIcons.ship, 'PUERTO',   _puerto)),
    const SizedBox(width: 8),
    Expanded(child: _statBox(LucideIcons.calendar, 'SALIDA', _fecha.isNotEmpty ? _fmtFechaCorta(_fecha) : 'Ver fechas')),
  ]);

  String _fmtFechaCorta(String d) {
    try {
      final dt = DateTime.parse('${d}T00:00:00');
      const months = ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];
      return '${dt.day} ${months[dt.month - 1]}. ${dt.year}';
    } catch (_) { return d; }
  }

  Widget _statBox(IconData icon, String label, String value) => Container(
    padding: const EdgeInsets.all(14),
    decoration: BoxDecoration(
      color: AppColors.teal50,
      borderRadius: BorderRadius.circular(20),
      border: Border.all(color: AppColors.teal100),
    ),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Icon(icon, size: 18, color: AppColors.teal600),
      const SizedBox(height: 6),
      Text(label, style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
          color: AppColors.teal400, letterSpacing: 1.2)),
      const SizedBox(height: 2),
      Text(value, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w900, color: AppColors.teal900),
          maxLines: 2, overflow: TextOverflow.ellipsis),
    ]),
  );

  // ── Itinerario de paradas ─────────────
  Widget _buildItinerary() => Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      // Cabecera de sección
      Row(children: [
        Container(width: 32, height: 32, decoration: BoxDecoration(color: AppColors.teal50, shape: BoxShape.circle),
            child: const Icon(LucideIcons.mapPin, size: 16, color: AppColors.teal600)),
        const SizedBox(width: 10),
        const Text('ITINERARIO', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
            color: AppColors.teal900, letterSpacing: 1.5)),
      ]),
      const SizedBox(height: 12),

      // Lista de paradas
      ..._paradas.asMap().entries.map((entry) {
        final i = entry.key;
        final p = entry.value;
        final isLast = i == _paradas.length - 1;
        return IntrinsicHeight(
          child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
            // Línea de tiempo vertical
            Column(children: [
              Container(width: 12, height: 12,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: isLast ? AppColors.teal600 : AppColors.teal300,
                ),
              ),
              if (!isLast) Expanded(child: Container(width: 2, color: AppColors.teal100)),
            ]),
            const SizedBox(width: 14),
            // Contenido de la parada
            Expanded(child: Container(
              margin: const EdgeInsets.only(bottom: 12),
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFFF9FAFB),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: const Color(0xFFF3F4F6)),
              ),
              child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text((p['puerto'] ?? 'Puerto').toString(),
                      style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal900)),
                  if ((p['pais'] ?? '').toString().isNotEmpty)
                    Text((p['pais'] ?? '').toString(),
                        style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w700, color: AppColors.teal400)),
                ])),
                // Horario llegada/salida si existe
                if ((p['llegada'] ?? p['salida'] ?? '').toString().isNotEmpty)
                  Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                    const Text('HORARIO', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                        color: AppColors.teal400, letterSpacing: 1)),
                    Text('${p['llegada'] ?? '--:--'} — ${p['salida'] ?? '--:--'}',
                        style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w900, color: AppColors.teal800)),
                  ]),
              ]),
            )),
          ]),
        );
      }),
    ],
  );

  // ── Tipos de camarote ────────────────────
  Widget _buildCabins() => Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      Row(children: [
        Container(width: 32, height: 32, decoration: BoxDecoration(color: AppColors.teal50, shape: BoxShape.circle),
            child: const Icon(LucideIcons.ship, size: 16, color: AppColors.teal600)),
        const SizedBox(width: 10),
        const Text('TIPOS DE CAMAROTE', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
            color: AppColors.teal900, letterSpacing: 1.5)),
      ]),
      const SizedBox(height: 12),
      GridView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: _cabinas.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2, crossAxisSpacing: 10, mainAxisSpacing: 10, childAspectRatio: 2.4),
        itemBuilder: (_, i) {
          final cab = _cabinas[i];
          final tipoCabina = (cab['tipo'] ?? 'Camarote').toString();
          final precioCab  = double.tryParse((cab['precio'] ?? 0).toString()) ?? 0.0;
          return Container(
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              border: Border.all(color: AppColors.teal100, width: 2),
            ),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.center, children: [
              Text(tipoCabina.toUpperCase(),
                  style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                      color: AppColors.teal400, letterSpacing: 1)),
              const SizedBox(height: 4),
              Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
                Text(_fmtPrice(precioCab),
                    style: const TextStyle(fontSize: 17, fontWeight: FontWeight.w900, color: AppColors.teal900)),
                const SizedBox(width: 4),
                const Padding(
                  padding: EdgeInsets.only(bottom: 2),
                  child: Text('/ persona', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w700, color: AppColors.teal400)),
                ),
              ]),
            ]),
          );
        },
      ),
    ],
  );

  // ── Tarjeta de precio con selector de personas ──
  Widget _buildPriceCard() => Container(
    padding: const EdgeInsets.all(24),
    decoration: BoxDecoration(
      color: AppColors.teal900,
      borderRadius: BorderRadius.circular(28),
      boxShadow: [BoxShadow(color: AppColors.teal900.withOpacity(0.4), blurRadius: 24)],
    ),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [

      // Label resumen
      const Text('RESUMEN DE RESERVA', style: TextStyle(
          fontSize: 9, fontWeight: FontWeight.w900,
          color: AppColors.teal400, letterSpacing: 2)),
      const SizedBox(height: 16),

      // ── Selector de pasajeros ─────────────────────────────────────────────
      const Text('NÚMERO DE PASAJEROS', style: TextStyle(
          fontSize: 8, fontWeight: FontWeight.w900,
          color: AppColors.teal400, letterSpacing: 1.2)),
      const SizedBox(height: 8),
      Container(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.08),
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: Colors.white.withOpacity(0.1)),
        ),
        child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          // Botón –
          GestureDetector(
            onTap: () { if (_personas > 1) setState(() => _personas--); },
            child: Container(
              width: 40, height: 40,
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1),
                borderRadius: BorderRadius.circular(14),
              ),
              child: const Icon(LucideIcons.minus, size: 16, color: Colors.white),
            ),
          ),
          // Valor central
          Column(children: [
            Text('$_personas', style: const TextStyle(
                fontSize: 22, fontWeight: FontWeight.w900, color: Colors.white)),
            Text(_personas == 1 ? 'Persona' : 'Personas',
                style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w700,
                    color: Colors.white38, letterSpacing: 1)),
          ]),
          // Botón +
          GestureDetector(
            onTap: () => setState(() => _personas++),
            child: Container(
              width: 40, height: 40,
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1),
                borderRadius: BorderRadius.circular(14),
              ),
              child: const Icon(LucideIcons.plus, size: 16, color: Colors.white),
            ),
          ),
        ]),
      ),
      const SizedBox(height: 20),

      // ── Precio total calculado ────────────────────────────────────────────
      Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
        Text(_fmtPrice(_precioTotal),
            style: const TextStyle(fontSize: 40, fontWeight: FontWeight.w900,
                color: Colors.white, letterSpacing: -1)),
      ]),
      Text(_personas > 1
          ? '(${_fmtPrice(_precio)} por persona)'
          : '*Tasas y cargos incluidos',
          style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w500,
              color: Colors.white38, fontStyle: FontStyle.italic)),
      const SizedBox(height: 20),

      // Fecha de salida
      if (_fecha.isNotEmpty)
        Container(
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.07),
            borderRadius: BorderRadius.circular(16),
          ),
          child: Row(children: [
            const Icon(LucideIcons.calendar, size: 18, color: AppColors.teal400),
            const SizedBox(width: 10),
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('SALIDA', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                  color: AppColors.teal400, letterSpacing: 1.2)),
              const SizedBox(height: 2),
              Text(_fmtFecha(_fecha),
                  style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w900, color: Colors.white)),
            ]),
          ]),
        ),

      const SizedBox(height: 16),

      // Ventajas incluidas (pensión completa / entretenimiento)
      ...[
        (LucideIcons.checkCircle, 'Pensión completa'),
        (LucideIcons.checkCircle, 'Entretenimiento a bordo'),
      ].map((item) => Padding(
        padding: const EdgeInsets.only(bottom: 8),
        child: Row(children: [
          Icon(item.$1, size: 16, color: AppColors.teal400),
          const SizedBox(width: 8),
          Text(item.$2, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Colors.white)),
        ]),
      )),

      const SizedBox(height: 8),
      // Nota info
      Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(color: Colors.white.withOpacity(0.05), borderRadius: BorderRadius.circular(14)),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Icon(LucideIcons.info, size: 14, color: AppColors.teal400),
          const SizedBox(width: 8),
          Expanded(child: Text(
            'Precios calculados para $_personas ${_personas == 1 ? 'pasajero' : 'pasajeros'}. '
                'La confirmación final se realizará tras el pago del depósito.',
            style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w600,
                color: Colors.white38, height: 1.5),
          )),
        ]),
      ),
    ]),
  );

  // ── Footer: error + cancelar + confirmar (igual que ActivityDetailsModal) ──
  Widget _buildFooter() => Container(
    padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
    decoration: const BoxDecoration(
      color: Colors.white,
      border: Border(top: BorderSide(color: Color(0xFFF3F4F6))),
    ),
    child: SafeArea(top: false, child: Column(mainAxisSize: MainAxisSize.min, children: [

      // Error
      if (_error.isNotEmpty)
        Container(
          margin: const EdgeInsets.only(bottom: 10),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
          decoration: BoxDecoration(
            color: const Color(0xFFFEF2F2),
            borderRadius: BorderRadius.circular(14),
            border: Border.all(color: const Color(0xFFFECACA)),
          ),
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
                        fontWeight: FontWeight.w900, decoration: TextDecoration.underline)),
              ),
            ],
          ]),
        ),

      Row(children: [
        // Cancelar
        Expanded(child: GestureDetector(
          onTap: () => Navigator.pop(context),
          child: const Padding(
            padding: EdgeInsets.symmetric(vertical: 16),
            child: Text('Cancelar', textAlign: TextAlign.center,
                style: TextStyle(fontSize: 11, fontWeight: FontWeight.w900,
                    color: Color(0xFF9CA3AF), letterSpacing: 1)),
          ),
        )),

        // Confirmar reserva
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
                  : Colors.white,
              borderRadius: BorderRadius.circular(24),
              border: _isBooked || _isReserving
                  ? null
                  : Border.all(color: AppColors.teal900, width: 0),
              boxShadow: [BoxShadow(
                color: (_isBooked ? const Color(0xFF16A34A) : AppColors.teal900).withOpacity(0.2),
                blurRadius: 12, offset: const Offset(0, 4),
              )],
            ),
            child: _isReserving
                ? const Center(child: SizedBox(width: 20, height: 20,
                child: CircularProgressIndicator(color: AppColors.teal600, strokeWidth: 2)))
                : Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              Text(
                _isBooked ? '¡RESERVADO!' : 'CONFIRMAR RESERVA',
                style: TextStyle(
                  color: _isBooked ? Colors.white : AppColors.teal900,
                  fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 1.5,
                ),
              ),
              const SizedBox(width: 8),
              Icon(
                _isBooked ? LucideIcons.checkCircle : LucideIcons.ship,
                size: 15,
                color: _isBooked ? Colors.white : AppColors.teal900,
              ),
            ]),
          ),
        )),
      ]),
    ])),
  );
}