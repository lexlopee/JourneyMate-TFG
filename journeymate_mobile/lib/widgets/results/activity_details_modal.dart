import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../services/auth_service.dart';
import '../../services/api_service.dart';

// ══════════════════════════════════════════════════════════════════════════════
// ACTIVITY DETAILS MODAL — Equivalente a ActivityDetailsModal.tsx
// ══════════════════════════════════════════════════════════════════════════════
class ActivityDetailsModal extends StatefulWidget {
  final Map<String, dynamic> basicData;   // Datos de la tarjeta (lista)
  final Map<String, dynamic> details;     // Respuesta de /activities/details
  final Map<String, dynamic> searchData;

  const ActivityDetailsModal({
    super.key,
    required this.basicData,
    required this.details,
    required this.searchData,
  });

  @override
  State<ActivityDetailsModal> createState() => _ActivityDetailsModalState();
}

class _ActivityDetailsModalState extends State<ActivityDetailsModal> {
  bool   _isReserving = false;
  bool   _isBooked    = false;
  String _error       = '';
  int    _activePhoto = 0;
  int _personas       = 1;

  final PageController _pageController = PageController();


  // ── Helpers de datos (espejo de las variables del TSX) ────────────────────
  String get _nombre =>
      (widget.details['nombre'] ?? widget.basicData['nombre'] ?? 'Actividad').toString();

  double get _precioUnitario =>
      double.tryParse((widget.details['precio'] ?? widget.basicData['precio'] ?? 0).toString()) ?? 0.0;

  double get _precioTotal => _precioUnitario * _personas;

  double get _rating =>
      double.tryParse((widget.details['averageRating'] ?? widget.basicData['calificacion'] ?? 4.5).toString()) ?? 4.5;

  bool get _freeCancellation =>
      widget.details['hasFreeCancellation'] == true;

  String get _duration =>
      (widget.details['duration'] ?? 'Flexible').toString();

  String get _description =>
      (widget.details['descripcionLarga'] ?? widget.basicData['descripcion'] ?? '').toString();

  String get _shortDescription =>
      (widget.details['shortDescription'] ?? 'Actividad turística').toString();

  List<String> get _fotos {
    final detailFotos = widget.details['fotos'];
    if (detailFotos is List && detailFotos.isNotEmpty) {
      return detailFotos.map((f) => f.toString()).toList();
    }
    final cover = widget.basicData['urlFoto']?.toString();
    return cover != null && cover.isNotEmpty ? [cover] : [];
  }

  List<String> get _included {
    final v = widget.details['whatsIncluded'];
    if (v is List) return v.map((e) => e.toString()).toList();
    return [];
  }

  List<String> get _notIncluded {
    final v = widget.details['notIncluded'];
    if (v is List) return v.map((e) => e.toString()).toList();
    return [];
  }

  String _fmtDate(String? d) {
    if (d == null || d.isEmpty) return 'Hoy';
    try {
      final dt = DateTime.parse(d);
      const months = [
        'enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
        'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'
      ];
      return '${dt.day} de ${months[dt.month - 1]} de ${dt.year}';
    } catch (_) { return d; }
  }

  // ── Reserva — mismo patrón que CarDetailsModal ────────────────────────────
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

      if (_precioTotal == 0) {
        setState(() => _error = 'No se pudo obtener el precio.');
        return;
      }

      final startDate = widget.searchData['startDate'] ?? '';

      // Estructura idéntica al TSX: ActivityDetailsModal.handleReserve
      final body = {
        'idUsuario':     idUsuario,
        'idTipoReserva': 6,
        'idEstado':      1,
        'precioTotal':   _precioTotal,
        'fechaServicio': startDate,
        'servicio': {
          'tipo':        'ACTIVIDAD',
          'nombre':      '$_nombre ($_personas personas)',
          'precioBase':  _precioTotal,
          'descripcion': _shortDescription,
          'fechaSalida': startDate,
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
          // Carrusel de fotos + nombre superpuesto
          _buildHero(),
          // Contenido scrollable
          Expanded(child: ListView(
            controller: scrollCtrl,
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 0),
            children: [
              _buildQuickStats(),
              const SizedBox(height: 16),
              _buildPersonSelector(),
              if (_description.isNotEmpty) ...[
                _buildDescription(),
                const SizedBox(height: 16),
              ],
              if (_included.isNotEmpty || _notIncluded.isNotEmpty) ...[
                _buildInclusionsRow(),
                const SizedBox(height: 16),
              ],
              _buildPriceCard(),
              const SizedBox(height: 110),
            ],
          )),
          // Footer con botón de reserva
          _buildFooter(),
        ]),
      ),
    );
  }

  // ── Hero: carrusel de fotos + badge nombre ────────────────────────────────
  Widget _buildHero() {
    final fotos = _fotos;

    return SizedBox(
      height: 280,
      child: Stack(children: [
        // 1. Carrusel (CAPA BASE)
        if (fotos.isEmpty)
          Container(
            color: AppColors.teal900,
            child: const Center(child: Icon(LucideIcons.image, color: Colors.white38, size: 48)),
          )
        else
          PageView.builder(
            controller: _pageController,
            itemCount: fotos.length,
            onPageChanged: (i) => setState(() => _activePhoto = i),
            itemBuilder: (_, i) => Image.network(
              fotos[i],
              fit: BoxFit.cover,
              errorBuilder: (_, __, ___) => Container(
                color: AppColors.teal50,
                child: const Icon(LucideIcons.imageOff, color: AppColors.teal200, size: 40),
              ),
            ),
          ),

        // 2. Degradado inferior (MOVIDO AQUÍ Y CON IgnorePointer)
        // Usamos IgnorePointer para que los eventos de toque pasen a través del degradado
        Positioned.fill(
          child: IgnorePointer(
            child: DecoratedBox(
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                  colors: [Colors.transparent, AppColors.teal900.withOpacity(0.85)],
                ),
              ),
            ),
          ),
        ),

        // 3. Nombre + rating (También con IgnorePointer por si acaso)
        Positioned(
          bottom: 20, left: 20, right: 60,
          child: IgnorePointer(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  _nombre.toUpperCase(),
                  style: const TextStyle(
                    fontSize: 20, fontWeight: FontWeight.w900,
                    color: Colors.white, letterSpacing: -0.5,
                    shadows: [Shadow(blurRadius: 8, color: Colors.black45)],
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                const SizedBox(height: 8),
                Row(children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(color: Colors.white30),
                    ),
                    child: Row(children: [
                      const Icon(LucideIcons.star, size: 14, color: Color(0xFFFACC15)),
                      const SizedBox(width: 4),
                      Text(_rating.toStringAsFixed(1),
                          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: Colors.white)),
                    ]),
                  ),
                  if (_freeCancellation) ...[
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                      decoration: BoxDecoration(
                        color: AppColors.teal500.withOpacity(0.8),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const Row(children: [
                        Icon(LucideIcons.shieldCheck, size: 12, color: Colors.white),
                        SizedBox(width: 4),
                        Text('Cancelación gratuita',
                            style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: Colors.white)),
                      ]),
                    ),
                  ],
                ]),
              ],
            ),
          ),
        ),

        // 4. BOTONES DE NAVEGACIÓN (CAPA SUPERIOR - Movidos al final del Stack)
        if (fotos.length > 1)
          Positioned.fill(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  _navCircleBtn(
                    icon: LucideIcons.chevronLeft,
                    onTap: () {
                      _pageController.previousPage(
                        duration: const Duration(milliseconds: 300),
                        curve: Curves.easeInOut,
                      );
                    },
                    visible: _activePhoto > 0,
                  ),
                  _navCircleBtn(
                    icon: LucideIcons.chevronRight,
                    onTap: () {
                      _pageController.nextPage(
                        duration: const Duration(milliseconds: 300),
                        curve: Curves.easeInOut,
                      );
                    },
                    visible: _activePhoto < fotos.length - 1,
                  ),
                ],
              ),
            ),
          ),

        // 5. Contador de fotos e Info adicional (Encima de los botones si es necesario)
        if (fotos.length > 1)
          Positioned(bottom: 20, right: 20, child: IgnorePointer(
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
              decoration: BoxDecoration(
                color: Colors.black54,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(children: [
                const Icon(LucideIcons.image, size: 12, color: Colors.white),
                const SizedBox(width: 4),
                Text('${_activePhoto + 1}/${fotos.length}',
                    style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: Colors.white)),
              ]),
            ),
          )),

        // 6. Botón cerrar
        Positioned(top: 16, right: 16, child: GestureDetector(
          onTap: () => Navigator.pop(context),
          child: Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Colors.black.withOpacity(0.3),
              shape: BoxShape.circle,
            ),
            child: const Icon(LucideIcons.x, color: Colors.white, size: 18),
          ),
        )),
      ]),
    );
  }

  // ── Quick stats: duración / ticket / idioma ───────────────────────────────
  Widget _buildQuickStats() => Row(children: [
    Expanded(child: _statBox(LucideIcons.clock,      'DURACIÓN',  _duration)),
    const SizedBox(width: 8),
    Expanded(child: _statBox(LucideIcons.smartphone, 'TICKET',    'Digital / Móvil')),
    const SizedBox(width: 8),
    Expanded(child: _statBox(LucideIcons.languages,  'IDIOMA',    'ES / EN')),
  ]);

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
      Text(label, style: const TextStyle(
          fontSize: 8, fontWeight: FontWeight.w900,
          color: AppColors.teal400, letterSpacing: 1.2)),
      const SizedBox(height: 2),
      Text(value, style: const TextStyle(
          fontSize: 11, fontWeight: FontWeight.w900, color: AppColors.teal900),
          maxLines: 2, overflow: TextOverflow.ellipsis),
    ]),
  );

  // ── Descripción completa ──────────────────────────────────────────────────
  Widget _buildDescription() => Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      const Row(children: [
        Icon(LucideIcons.info, size: 16, color: AppColors.teal600),
        SizedBox(width: 6),
        Text('DESCRIPCIÓN', style: TextStyle(
            fontSize: 9, fontWeight: FontWeight.w900,
            color: AppColors.teal900, letterSpacing: 1.5)),
      ]),
      const SizedBox(height: 10),
      Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: const Color(0xFFF9FAFB),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(_description, style: const TextStyle(
            fontSize: 13, color: AppColors.teal900,
            height: 1.6, fontWeight: FontWeight.w500)),
      ),
    ],
  );

  Widget _buildPersonSelector() => Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
      color: const Color(0xFFF9FAFB),
      borderRadius: BorderRadius.circular(20),
      border: Border.all(color: const Color(0xFFF3F4F6)),
    ),
    child: Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('PERSONAS', style: TextStyle(
                fontSize: 9, fontWeight: FontWeight.w900,
                color: AppColors.teal400, letterSpacing: 1.5)),
            Text('Número de participantes', style: TextStyle(fontSize: 11, color: Colors.grey)),
          ],
        ),
        Row(
          children: [
            _countBtn(LucideIcons.minus, () {
              if (_personas > 1) setState(() => _personas--);
            }),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Text('$_personas', style: const TextStyle(
                  fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900)),
            ),
            _countBtn(LucideIcons.plus, () {
              if (_personas < 10) setState(() => _personas++);
            }),
          ],
        ),
      ],
    ),
  );

  Widget _countBtn(IconData icon, VoidCallback onTap) => GestureDetector(
    onTap: onTap,
    child: Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
        border: Border.all(color: const Color(0xFFE5E7EB)),
      ),
      child: Icon(icon, size: 16, color: AppColors.teal900),
    ),
  );

  // ── Lo que incluye / no incluye ───────────────────────────────────────────
  Widget _buildInclusionsRow() => Row(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      if (_included.isNotEmpty)
        Expanded(child: _inclusionBox(
          label: 'LO QUE INCLUYE',
          items: _included,
          color: const Color(0xFFECFDF5),
          border: const Color(0xFFD1FAE5),
          iconColor: const Color(0xFF059669),
          icon: LucideIcons.checkCircle2,
        )),
      if (_included.isNotEmpty && _notIncluded.isNotEmpty)
        const SizedBox(width: 8),
      if (_notIncluded.isNotEmpty)
        Expanded(child: _inclusionBox(
          label: 'NO INCLUYE',
          items: _notIncluded,
          color: const Color(0xFFFFF1F2),
          border: const Color(0xFFFFE4E6),
          iconColor: const Color(0xFFF43F5E),
          icon: LucideIcons.xCircle,
        )),
    ],
  );

  Widget _inclusionBox({
    required String label,
    required List<String> items,
    required Color color,
    required Color border,
    required Color iconColor,
    required IconData icon,
  }) => Container(
    padding: const EdgeInsets.all(14),
    decoration: BoxDecoration(
      color: color,
      borderRadius: BorderRadius.circular(20),
      border: Border.all(color: border),
    ),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(label, style: TextStyle(
          fontSize: 8, fontWeight: FontWeight.w900,
          color: iconColor.withOpacity(0.6), letterSpacing: 1.2)),
      const SizedBox(height: 10),
      ...items.take(5).map((item) => Padding(
        padding: const EdgeInsets.only(bottom: 8),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Icon(icon, size: 14, color: iconColor),
          const SizedBox(width: 6),
          Expanded(child: Text(item, style: const TextStyle(
              fontSize: 11, fontWeight: FontWeight.w700,
              color: AppColors.teal900, height: 1.4))),
        ]),
      )),
    ]),
  );

  // 3. Widget auxiliar para el botón circular
  Widget _navCircleBtn({required IconData icon, required VoidCallback onTap, required bool visible}) {
    return AnimatedOpacity(
      duration: const Duration(milliseconds: 200),
      opacity: visible ? 1.0 : 0.0,
      child: GestureDetector(
        onTap: visible ? onTap : null,
        child: Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: Colors.black26,
            shape: BoxShape.circle,
            border: Border.all(color: Colors.white24),
          ),
          child: Icon(icon, color: Colors.white, size: 20),
        ),
      ),
    );
  }

  // ── Tarjeta de precio + fecha ─────────────────────────────────────────────
  Widget _buildPriceCard() => Container(
    padding: const EdgeInsets.all(24),
    decoration: BoxDecoration(
      color: AppColors.teal900,
      borderRadius: BorderRadius.circular(28),
      boxShadow: [BoxShadow(
          color: AppColors.teal900.withOpacity(0.4), blurRadius: 24)],
    ),
    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      // Garantía badge
      Align(alignment: Alignment.topRight, child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.1),
          borderRadius: BorderRadius.circular(20),
        ),
        child: const Text('Garantía JourneyMate',
            style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900,
                color: Colors.white70, letterSpacing: 1)),
      )),
      const SizedBox(height: 12),

      // Precio
      const Text('PRECIO TOTAL', style: TextStyle(
          fontSize: 9, fontWeight: FontWeight.w900,
          color: AppColors.teal400, letterSpacing: 2)),
      const SizedBox(height: 4),
      Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
        Text(_precioTotal.toStringAsFixed(2), style: const TextStyle(
            fontSize: 44, fontWeight: FontWeight.w900,
            color: Colors.white, letterSpacing: -1)),
        const SizedBox(width: 6),
        const Padding(
          padding: EdgeInsets.only(bottom: 8),
          child: Text('€', style: TextStyle(
              fontSize: 22, fontWeight: FontWeight.w700, color: AppColors.teal400)),
        ),
      ]),

      const SizedBox(height: 16),

      // Fecha
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
            const Text('FECHA', style: TextStyle(
                fontSize: 8, fontWeight: FontWeight.w900,
                color: AppColors.teal400, letterSpacing: 1.2)),
            const SizedBox(height: 2),
            Text(_fmtDate(widget.searchData['startDate']?.toString()),
                style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900,
                    color: Colors.white)),
          ]),
        ]),
      ),

      // SSL
      const SizedBox(height: 16),
      const Center(child: Text('Encriptación SSL de 256 bits',
          style: TextStyle(fontSize: 8, fontWeight: FontWeight.w700,
              color: Colors.white30, letterSpacing: 1.5))),
    ]),
  );

  // ── Footer: error + cancelar + confirmar ──────────────────────────────────
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
          child: const Padding(
            padding: EdgeInsets.symmetric(vertical: 16),
            child: Text('Cancelar', textAlign: TextAlign.center,
                style: TextStyle(fontSize: 11, fontWeight: FontWeight.w900,
                    color: Color(0xFF9CA3AF), letterSpacing: 1)),
          ),
        )),

        // Reservar
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
                  blurRadius: 12, offset: const Offset(0, 4))],
            ),
            child: _isReserving
                ? const Center(child: SizedBox(width: 20, height: 20,
                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2)))
                : Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              Text(
                  _isBooked ? '¡RESERVADO!' : 'RESERVAR ACTIVIDAD',
                  style: const TextStyle(color: Colors.white,
                      fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 1.5)),
              const SizedBox(width: 8),
              Icon(
                  _isBooked ? LucideIcons.checkCircle2 : LucideIcons.ticket,
                  size: 15, color: Colors.white),
            ]),
          ),
        )),
      ]),
    ])),
  );
}