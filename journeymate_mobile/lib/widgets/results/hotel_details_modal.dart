// lib/widgets/results/hotel/hotel_details_modal.dart
//
// FIXES aplicados:
// 1. ✅ _handleReserve usa AuthService real (no const vacío)
// 2. ✅ Llama a api.createReserva con el cuerpo correcto
// 3. ✅ Muestra link a /login si no hay sesión

import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../../core/app_colors.dart';
import '../../services/auth_service.dart';
import '../../services/api_service.dart';

class HotelDetailsModal extends StatefulWidget {
  final Map<String, dynamic> details;
  final Map<String, dynamic> basicData;
  final Map<String, dynamic> searchData;

  const HotelDetailsModal({
    super.key,
    required this.details,
    required this.basicData,
    required this.searchData,
  });

  @override
  State<HotelDetailsModal> createState() => _HotelDetailsModalState();
}

class _HotelDetailsModalState extends State<HotelDetailsModal> {
  int    _photoIndex  = 0;
  bool   _isReserving = false;
  bool   _isBooked    = false;
  String _error       = '';

  late final PageController _pageCtrl;
  late final Map<String, dynamic> _hotelData;
  late final List<String>         _photos;
  late final double               _lat, _lng, _price;
  late final int                  _nights, _stars, _roomsLeft;
  late final String               _name, _address, _currency;
  late final dynamic              _reviewScore, _reviewWord, _numReviews, _breakfast;
  late final List<dynamic>        _facilities;

  @override
  void initState() {
    super.initState();
    _pageCtrl = PageController();
    _processData();
  }

  @override
  void dispose() {
    _pageCtrl.dispose();
    super.dispose();
  }

  void _processData() {
    _hotelData = Map<String, dynamic>.from(
        (widget.details['data'] ?? {}) as Map);

    _lat   = double.tryParse(widget.basicData['latitud']?.toString()  ?? '') ?? 0.0;
    _lng   = double.tryParse(widget.basicData['longitud']?.toString() ?? '') ?? 0.0;
    _price = double.tryParse(widget.basicData['precio']?.toString()   ?? '0') ?? 0.0;

    _name     = (_hotelData['hotelName']  ?? _hotelData['hotel_name']
        ?? widget.basicData['nombre']   ?? 'Hotel').toString();
    _address  = (_hotelData['address']    ?? widget.basicData['direccion']
        ?? 'Dirección no disponible').toString();
    _currency = (widget.basicData['moneda'] ?? _hotelData['currencyCode'] ?? 'EUR').toString();

    _stars     = (widget.basicData['propertyClass'] ?? 0) as int;
    _roomsLeft = (_hotelData['availableRooms'] ?? _hotelData['available_rooms'] ?? 0) as int;

    _reviewScore = _hotelData['reviewScore']     ?? _hotelData['review_score']      ?? widget.basicData['calificacion'];
    _reviewWord  = _hotelData['reviewScoreWord'] ?? _hotelData['review_score_word'] ?? widget.basicData['reviewWord'];
    _numReviews  = _hotelData['reviewNr']        ?? _hotelData['review_nr']         ?? 0;
    _breakfast   = _hotelData['breakfastReviewScore'] ?? _hotelData['breakfast_review_score'];

    _facilities = (_hotelData['facilitiesBlock']?['facilities'] ??
        _hotelData['facilities_block']?['facilities'] ?? []) as List;

    try {
      final s = DateTime.parse(widget.searchData['startDate'] ?? '');
      final e = DateTime.parse(widget.searchData['endDate']   ?? '');
      _nights = e.difference(s).inDays.clamp(1, 365);
    } catch (_) { _nights = 1; }

    final seen = <String>{};
    final main = widget.basicData['urlFoto']?.toString() ?? '';
    if (main.isNotEmpty) seen.add(main);

    void extractRoomPhotos(dynamic room) {
      if (room is! Map) return;
      final roomPhotos = room['photos'] as List? ?? [];
      for (final p in roomPhotos) {
        if (p is! Map) continue;
        final url = (p['urlMax750'] ?? p['url_max750'] ?? p['url'] ?? '').toString();
        if (url.isNotEmpty) seen.add(url);
      }
    }

    final rooms = _hotelData['rooms'];
    if (rooms is Map)       rooms.values.forEach(extractRoomPhotos);
    else if (rooms is List) rooms.forEach(extractRoomPhotos);

    _photos = seen.toList();
  }

  String _fmtPrice(double v) => '${v.toStringAsFixed(2)} $_currency';
  int get _adults => (widget.searchData['adults'] ?? 1) is int
      ? (widget.searchData['adults'] ?? 1) as int
      : int.tryParse(widget.searchData['adults'].toString()) ?? 1;

  bool get _hasBreakfast {
    if (_breakfast == null) return false;
    final r = _breakfast['rating'];
    return r is num && r > 0;
  }

  // ── FIX 1: Reserva real con AuthService ───────────────────────────────────
  Future<void> _handleReserve() async {
    if (_isReserving || _isBooked) return;
    setState(() { _isReserving = true; _error = ''; });

    try {
      // Leer token e idUsuario de SharedPreferences via AuthService
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

      final body = {
        'idUsuario':     idUsuario,
        'idTipoReserva': 1,
        'idEstado':      1,
        'precioTotal':   _price,
        'fechaServicio': widget.searchData['startDate'] ?? '',
        'servicio': {
          'tipo':        'HOTEL',
          'nombre':      _name,
          'precioBase':  _price,
          'descripcion': _reviewWord?.toString(),
          'estrellas':   _stars >= 1 ? _stars : null,
          'latitud':     _lat != 0 ? _lat : null,
          'longitud':    _lng != 0 ? _lng : null,
          'descripcion_direccion': _address,
          'fechaSalida': widget.searchData['startDate'] ?? '',
        },
      };

      await api.createReserva(body);
      setState(() => _isBooked = true);

    } on ApiException catch (e) {
      setState(() => _error = e.statusCode == 401
          ? 'Sesión expirada. Vuelve a iniciar sesión.'
          : 'Error al reservar (${e.statusCode}). Inténtalo de nuevo.');
    } catch (e) {
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
          _buildCarousel(),
          Expanded(child: ListView(
            controller: scrollCtrl,
            padding: const EdgeInsets.fromLTRB(24, 24, 24, 0),
            children: [
              _buildHeader(),
              const SizedBox(height: 20),
              if (_lat != 0 && _lng != 0) ...[_buildMap(), const SizedBox(height: 20)],
              _buildRatings(),
              const SizedBox(height: 16),
              if (_hasBreakfast) ...[_buildBreakfast(), const SizedBox(height: 16)],
              _buildFacilities(),
              const SizedBox(height: 110),
            ],
          )),
          _buildBookingBar(),
        ]),
      ),
    );
  }

  Widget _buildCarousel() {
    if (_photos.isEmpty) {
      return Container(height: 280, color: AppColors.teal900,
          child: const Center(child: Text('SIN IMÁGENES',
              style: TextStyle(color: Colors.white54, fontWeight: FontWeight.w900,
                  fontSize: 10, letterSpacing: 2))));
    }

    return SizedBox(height: 280,
      child: Stack(children: [
        PageView.builder(
          controller: _pageCtrl,
          itemCount: _photos.length,
          onPageChanged: (i) => setState(() => _photoIndex = i),
          itemBuilder: (_, i) => Image.network(
            _photos[i], fit: BoxFit.cover, width: double.infinity,
            errorBuilder: (_, __, ___) => Container(color: AppColors.teal50,
                child: const Center(child: Icon(LucideIcons.image,
                    size: 48, color: AppColors.teal200))),
          ),
        ),
        Positioned(top: 44, right: 16,
          child: GestureDetector(
            onTap: () => Navigator.pop(context),
            child: Container(padding: const EdgeInsets.all(10),
                decoration: const BoxDecoration(color: Colors.black38, shape: BoxShape.circle),
                child: const Icon(LucideIcons.x, color: Colors.white, size: 18)),
          ),
        ),
        if (_photos.length > 1) ...[
          _arrowBtn(left: true, onTap: () {
            final prev = (_photoIndex - 1 + _photos.length) % _photos.length;
            _pageCtrl.animateToPage(prev, duration: const Duration(milliseconds: 300), curve: Curves.easeInOut);
          }),
          _arrowBtn(left: false, onTap: () {
            final next = (_photoIndex + 1) % _photos.length;
            _pageCtrl.animateToPage(next, duration: const Duration(milliseconds: 300), curve: Curves.easeInOut);
          }),
        ],
        Positioned(bottom: 16, left: 0, right: 0,
            child: Center(child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                decoration: BoxDecoration(
                    color: Colors.black.withOpacity(0.6), borderRadius: BorderRadius.circular(20)),
                child: Text('${_photoIndex + 1} / ${_photos.length}',
                    style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w900))))),
      ]),
    );
  }

  Widget _arrowBtn({required bool left, required VoidCallback onTap}) =>
      Positioned(
        left: left ? 12 : null, right: !left ? 12 : null,
        top: 0, bottom: 0,
        child: Center(child: GestureDetector(
          onTap: onTap,
          child: Container(padding: const EdgeInsets.all(10),
              decoration: const BoxDecoration(color: Colors.black26, shape: BoxShape.circle),
              child: Icon(left ? LucideIcons.chevronLeft : LucideIcons.chevronRight,
                  color: Colors.white, size: 22)),
        )),
      );

  Widget _buildHeader() => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    if (_stars > 0)
      Padding(padding: const EdgeInsets.only(bottom: 8),
          child: Row(mainAxisSize: MainAxisSize.min,
              children: List.generate(_stars.clamp(0, 5), (_) =>
              const Icon(LucideIcons.star, size: 14, color: Color(0xFFF59E0B))))),
    Text(_name.toUpperCase(),
        style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900,
            color: AppColors.teal900, letterSpacing: -0.5)),
    const SizedBox(height: 12),
    if (_roomsLeft > 0)
      Container(
          margin: const EdgeInsets.only(bottom: 8),
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
              color: _roomsLeft < 5 ? const Color(0xFFFFF7ED) : const Color(0xFFFFF1F2),
              borderRadius: BorderRadius.circular(20),
              border: Border.all(color: _roomsLeft < 5
                  ? const Color(0xFFFDBA74) : const Color(0xFFFDA4AF))),
          child: Text(_roomsLeft < 5 ? '¡Solo quedan $_roomsLeft!' : '$_roomsLeft disponibles',
              style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1,
                  color: _roomsLeft < 5 ? const Color(0xFFEA580C) : const Color(0xFFE11D48)))),
    Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(color: AppColors.teal50,
          borderRadius: BorderRadius.circular(16), border: Border.all(color: AppColors.teal100)),
      child: Row(children: [
        const Icon(LucideIcons.mapPin, size: 16, color: AppColors.teal500),
        const SizedBox(width: 8),
        Flexible(child: Text(_address,
            style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600, color: AppColors.teal700))),
      ]),
    ),
  ]);

  Widget _buildMap() => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    const Row(children: [
      Expanded(child: Divider(color: AppColors.teal100)),
      Padding(padding: EdgeInsets.symmetric(horizontal: 12),
          child: Text('UBICACIÓN', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal500))),
      Expanded(child: Divider(color: AppColors.teal100)),
    ]),
    const SizedBox(height: 12),
    ClipRRect(
      borderRadius: BorderRadius.circular(24),
      child: SizedBox(height: 220,
          child: GoogleMap(
            initialCameraPosition: CameraPosition(target: LatLng(_lat, _lng), zoom: 15),
            markers: {Marker(markerId: const MarkerId('hotel'),
                position: LatLng(_lat, _lng), infoWindow: InfoWindow(title: _name))},
            zoomControlsEnabled: false, mapToolbarEnabled: false, myLocationButtonEnabled: false,
            gestureRecognizers: {Factory<OneSequenceGestureRecognizer>(() => EagerGestureRecognizer())},
          )),
    ),
  ]);

  Widget _buildRatings() => Container(
    padding: const EdgeInsets.all(20),
    decoration: BoxDecoration(color: AppColors.teal50.withOpacity(0.5),
        borderRadius: BorderRadius.circular(24), border: Border.all(color: AppColors.teal100)),
    child: Row(children: [
      Container(width: 64, height: 64,
          decoration: BoxDecoration(color: AppColors.teal600, borderRadius: BorderRadius.circular(16)),
          child: Center(child: Text(_reviewScore?.toString() ?? '—',
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Colors.white)))),
      const SizedBox(width: 16),
      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text((_reviewWord ?? 'Sin valoración').toString(),
            style: const TextStyle(fontWeight: FontWeight.w900, color: AppColors.teal900, fontSize: 14)),
        Text('$_numReviews reseñas',
            style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w700, color: AppColors.teal400, letterSpacing: 1)),
      ]),
    ]),
  );

  Widget _buildBreakfast() => Container(
    padding: const EdgeInsets.all(20),
    decoration: BoxDecoration(
        color: const Color(0xFFFFFBEB), borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFFDE68A))),
    child: Row(children: [
      Container(padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: const Color(0xFFFEF3C7), borderRadius: BorderRadius.circular(14)),
          child: const Icon(LucideIcons.coffee, size: 22, color: Color(0xFFD97706))),
      const SizedBox(width: 14),
      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text('Desayuno ${(_breakfast['reviewScoreWord'] ?? _breakfast['review_score_word'] ?? '')}',
            style: const TextStyle(fontWeight: FontWeight.w900, color: Color(0xFF78350F), fontSize: 13)),
        Text('Calificación: ${_breakfast['rating']}/10',
            style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w700, color: Color(0xFF92400E))),
      ]),
    ]),
  );

  Widget _buildFacilities() {
    if (_facilities.isEmpty) {
      return const Text('No hay instalaciones disponibles',
          style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: AppColors.teal300));
    }
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      const Text('SERVICIOS E INSTALACIONES',
          style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal300)),
      const SizedBox(height: 12),
      Wrap(spacing: 8, runSpacing: 8,
          children: _facilities.map((f) => Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(
                color: Colors.white, border: Border.all(color: const Color(0xFFF3F4F6)),
                borderRadius: BorderRadius.circular(12),
                boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.03), blurRadius: 4)]),
            child: Row(mainAxisSize: MainAxisSize.min, children: [
              const Icon(LucideIcons.check, size: 12, color: AppColors.teal500),
              const SizedBox(width: 6),
              Text((f['name'] ?? '').toString().toUpperCase(),
                  style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900,
                      color: AppColors.teal700, letterSpacing: 0.5)),
            ]),
          )).toList()),
    ]);
  }

  Widget _buildBookingBar() => Container(
    padding: const EdgeInsets.fromLTRB(24, 20, 24, 0),
    decoration: BoxDecoration(
        color: AppColors.teal900,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(32)),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.2), blurRadius: 10)]),
    child: SafeArea(top: false, child: Column(mainAxisSize: MainAxisSize.min, children: [
      Row(mainAxisAlignment: MainAxisAlignment.center, children: [
        _badge(LucideIcons.calendar, '$_nights ${_nights == 1 ? 'noche' : 'noches'}'),
        const SizedBox(width: 8),
        _badge(LucideIcons.users, '$_adults adulto${_adults > 1 ? 's' : ''}'),
      ]),
      const SizedBox(height: 14),
      Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
        Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const Text('IMPORTE TOTAL', style: TextStyle(color: Colors.white38,
              fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 2)),
          Text(_fmtPrice(_price),
              style: const TextStyle(color: Colors.white, fontSize: 22,
                  fontWeight: FontWeight.w900, letterSpacing: -0.5)),
          const Text('Tasas incluidas', style: TextStyle(color: AppColors.teal400,
              fontSize: 9, fontWeight: FontWeight.w700)),
        ]),
        GestureDetector(
          onTap: _handleReserve,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
            decoration: BoxDecoration(
                color: _isBooked ? const Color(0xFF16A34A)
                    : _isReserving ? AppColors.teal600 : AppColors.teal400,
                borderRadius: BorderRadius.circular(22)),
            child: _isReserving
                ? const SizedBox(width: 20, height: 20,
                child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                : Row(mainAxisSize: MainAxisSize.min, children: [
              Text(_isBooked ? '¡RESERVADO!' : 'RESERVAR',
                  style: const TextStyle(color: AppColors.teal900,
                      fontWeight: FontWeight.w900, fontSize: 10, letterSpacing: 1.5)),
              const SizedBox(width: 8),
              Icon(_isBooked ? LucideIcons.checkCircle : LucideIcons.briefcase,
                  size: 16, color: AppColors.teal900),
            ]),
          ),
        ),
      ]),

      // FIX 2: Error con link a login si no está autenticado
      if (_error.isNotEmpty) ...[
        const SizedBox(height: 8),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
          decoration: BoxDecoration(color: const Color(0xFFFEE2E2), borderRadius: BorderRadius.circular(14)),
          child: Column(children: [
            Text(_error, style: const TextStyle(color: Color(0xFFB91C1C),
                fontSize: 11, fontWeight: FontWeight.w700), textAlign: TextAlign.center),
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
      ],
      const SizedBox(height: 8),
    ])),
  );

  Widget _badge(IconData icon, String label) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
    decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.05), borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.1))),
    child: Row(mainAxisSize: MainAxisSize.min, children: [
      Icon(icon, size: 12, color: AppColors.teal400), const SizedBox(width: 6),
      Text(label, style: const TextStyle(color: Colors.white,
          fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1)),
    ]),
  );
}