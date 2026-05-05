import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../screens/search_screen.dart';
import '../cards/result_cards.dart';
import '../../utils/date_utils.dart';

// ══════════════════════════════════════════════════════════════════════════════
// WIDGET PRINCIPAL DE LISTA DE RESULTADOS
// ══════════════════════════════════════════════════════════════════════════════
class ResultsListWidget extends StatefulWidget {
  final List<dynamic> results;
  final Section section;
  final Map<String, dynamic> searchData;
  final String destination;

  const ResultsListWidget({
    super.key,
    required this.results,
    required this.section,
    required this.searchData,
    required this.destination,
  });

  @override
  State<ResultsListWidget> createState() => _ResultsListWidgetState();
}

class _ResultsListWidgetState extends State<ResultsListWidget> {
  String _sortBy = 'default';

  List<dynamic> get _sorted {
    final copy = List<dynamic>.from(widget.results);
    switch (_sortBy) {
      case 'price_asc':
        copy.sort((a, b) {
          final pa = double.tryParse((a['precioDesde'] ?? a['price_from'] ?? a['precio'] ?? a['price'] ?? 0).toString()) ?? 0;
          final pb = double.tryParse((b['precioDesde'] ?? b['price_from'] ?? b['precio'] ?? b['price'] ?? 0).toString()) ?? 0;
          return pa.compareTo(pb);
        });
        break;
      case 'rating_desc':
        copy.sort((a, b) {
          final ra = (a['calificacion'] ?? a['rating'] ?? 0) as num;
          final rb = (b['calificacion'] ?? b['rating'] ?? 0) as num;
          return rb.compareTo(ra);
        });
        break;
    }
    return copy;
  }

  @override
  Widget build(BuildContext context) {
    if (widget.results.isEmpty) {
      return const Center(child: Padding(padding: EdgeInsets.all(40), child: Text("Buscando ofertas...")));
    }

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12),
      child: SingleChildScrollView(
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(height: 24),
          _buildHeader(),
          const SizedBox(height: 12),
          _buildSortFilters(),
          const SizedBox(height: 16),
          // Aquí se generan las tarjetas sin filtros restrictivos
          ..._sorted.map((item) => Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: _buildCard(item),
          )),
          const SizedBox(height: 40),
        ]),
      ),
    );
  }

  Widget _buildHeader() {
    final place = widget.destination.isNotEmpty ? widget.destination : 'tu destino';
    String title = 'Resultados';
    if (widget.section == Section.alojamiento) title = 'Hoteles en $place';
    if (widget.section == Section.vuelos)      title = 'Vuelos a $place';
    if (widget.section == Section.coches)      title = 'Coches disponibles';
    if (widget.section == Section.actividades) title = 'Experiencias en $place';
    if (widget.section == Section.cruceros)    title = 'Cruceros disponibles';

    return Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
      Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const Row(children: [
          Icon(LucideIcons.sparkles, size: 12, color: AppColors.teal600),
          SizedBox(width: 4),
          Text('JOURNEYMATE SELECCIONES', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal600)),
        ]),
        const SizedBox(height: 4),
        Text(title.toUpperCase(), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5), maxLines: 2, overflow: TextOverflow.ellipsis),
      ])),
      Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(16)),
        child: Row(children: [
          Icon(widget.section.icon, size: 14, color: Colors.white),
          const SizedBox(width: 6),
          Text('${widget.results.length} disponibles', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 10, letterSpacing: 1)),
        ]),
      ),
    ]);
  }

  Widget _buildSortFilters() {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(children: [
        const Padding(padding: EdgeInsets.only(right: 8), child: Text('Ordenar por:', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: AppColors.teal600))),
        ...[
          ('default',    'Recomendados',  LucideIcons.slidersHorizontal),
          ('price_asc',  'Más Barato',    LucideIcons.arrowUpNarrowWide),
          if (widget.section == Section.alojamiento) ('rating_desc', 'Mejor Valorados', LucideIcons.star),
        ].map((o) {
          final active = _sortBy == o.$1;
          return GestureDetector(
            onTap: () => setState(() => _sortBy = o.$1),
            child: Container(
              margin: const EdgeInsets.only(right: 6),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
              decoration: BoxDecoration(color: active ? AppColors.teal500 : Colors.white.withOpacity(0.5), borderRadius: BorderRadius.circular(20)),
              child: Row(children: [
                Icon(o.$3, size: 12, color: active ? Colors.white : AppColors.teal600),
                const SizedBox(width: 4),
                Text(o.$2, style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1, color: active ? Colors.white : AppColors.teal600)),
              ]),
            ),
          );
        }),
      ]),
    );
  }

  // MÉTODO CORREGIDO: Llama a las clases de tarjetas externas correctamente[cite: 18, 19]
  Widget _buildCard(Map<String, dynamic> item) {
    switch (widget.section) {
      case Section.alojamiento:
        return HotelCard(hotel: item, destination: widget.destination, onViewDetails: () => _showHotelDetail(item));
      case Section.vuelos:
        return FlightCard(flight: item, onViewDetails: () => _showFlightDetail(item));
      case Section.actividades:
        return ActivityCard(activity: item, onViewDetails: () => _showActivityDetail(item));
      case Section.cruceros:
        return CruiseCard(cruise: item, onViewDetails: () => _showCruiseDetail(item));
      case Section.coches:
      // Se llama a la clase CarCard definida abajo
        return CarCardWidget(car: item, searchData: widget.searchData, onRent: () => _showCarDetail(item));
      default:
        return const SizedBox.shrink();
    }
  }

  // ── MODALES DE DETALLE ────────────────────────────────────────────────────
  void _showHotelDetail(Map<String, dynamic> item) {
    showModalBottomSheet(context: context, isScrollControlled: true, useSafeArea: true, backgroundColor: Colors.transparent, builder: (_) => _DetailSheet(title: item['nombre'] ?? 'Hotel', child: _HotelDetailContent(hotel: item, searchData: widget.searchData)));
  }

  void _showFlightDetail(Map<String, dynamic> item) {
    showModalBottomSheet(context: context, isScrollControlled: true, useSafeArea: true, backgroundColor: Colors.transparent, builder: (_) => _DetailSheet(title: item['aerolinea'] ?? 'Vuelo', child: _FlightDetailContent(flight: item)));
  }

  void _showActivityDetail(Map<String, dynamic> item) {
    showModalBottomSheet(context: context, isScrollControlled: true, useSafeArea: true, backgroundColor: Colors.transparent, builder: (_) => _DetailSheet(title: item['nombre'] ?? 'Actividad', child: _GenericDetailContent(data: item)));
  }

  void _showCruiseDetail(Map<String, dynamic> item) {
    showModalBottomSheet(context: context, isScrollControlled: true, useSafeArea: true, backgroundColor: Colors.transparent, builder: (_) => _DetailSheet(title: item['nombreCrucero'] ?? 'Crucero', child: _GenericDetailContent(data: item)));
  }

  void _showCarDetail(Map<String, dynamic> item) {
    showModalBottomSheet(context: context, isScrollControlled: true, useSafeArea: true, backgroundColor: Colors.transparent, builder: (_) => _DetailSheet(title: item['carName'] ?? 'Coche', child: _CarDetailContent(car: item, searchData: widget.searchData)));
  }
}

// ══════════════════════════════════════════════════════════════════════════════
// CAR CARD WIDGET — Definido como clase independiente para evitar errores de tipo
// ══════════════════════════════════════════════════════════════════════════════
class CarCardWidget extends StatelessWidget {
  final Map<String, dynamic> car;
  final Map<String, dynamic> searchData;
  final VoidCallback onRent;
  const CarCardWidget({super.key, required this.car, required this.searchData, required this.onRent});

  @override
  Widget build(BuildContext context) {
    final days = daysBetween(searchData['startDate'], searchData['endDate']);
    final price = car['price'] ?? car['precio'] ?? 0;

    return Container(
      decoration: BoxDecoration(color: Colors.white.withOpacity(0.92), borderRadius: BorderRadius.circular(40), boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 20)]),
      padding: const EdgeInsets.all(20),
      child: Row(children: [
        if (car['imageUrl'] != null)
          ClipRRect(borderRadius: BorderRadius.circular(16), child: Image.network(car['imageUrl'], width: 120, height: 80, fit: BoxFit.contain, errorBuilder: (_, __, ___) => const Icon(LucideIcons.car, size: 48, color: AppColors.teal300)))
        else
          const Icon(LucideIcons.car, size: 48, color: AppColors.teal300),
        const SizedBox(width: 16),
        Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text((car['carName'] ?? 'Coche').toString().toUpperCase(), style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.3)),
          Text(car['vendorName'] ?? 'Proveedor', style: const TextStyle(fontSize: 11, color: AppColors.teal600, fontWeight: FontWeight.w600)),
          const SizedBox(height: 8),
          Row(children: [
            const Icon(LucideIcons.users, size: 12, color: AppColors.teal500), const SizedBox(width: 3), Text('${car['seats'] ?? 5}', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: AppColors.teal900)), const SizedBox(width: 8),
            const Icon(LucideIcons.briefcase, size: 12, color: AppColors.teal500), const SizedBox(width: 3), Text('${car['bags'] ?? 2}', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: AppColors.teal900)),
          ]),
          const SizedBox(height: 8),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text('$days día${days == 1 ? '' : 's'}', style: const TextStyle(fontSize: 9, color: AppColors.teal400, fontWeight: FontWeight.w700)),
              Text(formatCurrency(price, car['currency'] ?? 'EUR'), style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: -0.5)),
            ]),
            ElevatedButton(
              onPressed: onRent,
              style: ElevatedButton.styleFrom(backgroundColor: AppColors.teal900, foregroundColor: Colors.white, padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16))),
              child: const Text('ALQUILAR', style: TextStyle(fontWeight: FontWeight.w900, fontSize: 10)),
            ),
          ]),
        ])),
      ]),
    );
  }
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES DE DETALLE (MODALES)
// ══════════════════════════════════════════════════════════════════════════════

class _DetailSheet extends StatelessWidget {
  final String title;
  final Widget child;
  const _DetailSheet({required this.title, required this.child});

  @override
  Widget build(BuildContext context) => DraggableScrollableSheet(
    initialChildSize: 0.92, minChildSize: 0.5, maxChildSize: 0.95,
    builder: (_, ctrl) => Container(
      decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.vertical(top: Radius.circular(40))),
      child: Column(children: [
        Container(margin: const EdgeInsets.symmetric(vertical: 10), width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),
        Padding(padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 4), child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Expanded(child: Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900), overflow: TextOverflow.ellipsis)),
          IconButton(icon: const Icon(LucideIcons.x), onPressed: () => Navigator.of(context).pop()),
        ])),
        Expanded(child: SingleChildScrollView(controller: ctrl, padding: const EdgeInsets.symmetric(horizontal: 20), child: child)),
      ]),
    ),
  );
}

class _HotelDetailContent extends StatelessWidget {
  final Map<String, dynamic> hotel;
  final Map<String, dynamic> searchData;
  const _HotelDetailContent({required this.hotel, required this.searchData});

  @override
  Widget build(BuildContext context) {
    final nights = daysBetween(searchData['startDate'], searchData['endDate']);
    return Column(children: [
      if (hotel['urlFoto'] != null) ClipRRect(borderRadius: BorderRadius.circular(20), child: Image.network(hotel['urlFoto'], height: 200, width: double.infinity, fit: BoxFit.cover)),
      const SizedBox(height: 16),
      Text((hotel['nombre'] ?? 'Hotel').toString().toUpperCase(), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900)),
      const SizedBox(height: 20),
      _reserveBtn(context, formatCurrency(hotel['precio'] ?? 0, hotel['moneda'] ?? 'EUR')),
    ]);
  }
}

class _FlightDetailContent extends StatelessWidget {
  final Map<String, dynamic> flight;
  const _FlightDetailContent({required this.flight});

  @override
  Widget build(BuildContext context) => Column(children: [
    Text((flight['aerolinea'] ?? 'Aerolínea').toString().toUpperCase(), style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: AppColors.teal900)),
    const SizedBox(height: 20),
    _reserveBtn(context, formatCurrency(flight['precio'] ?? 0, flight['moneda'] ?? 'EUR')),
  ]);
}

class _CarDetailContent extends StatelessWidget {
  final Map<String, dynamic> car;
  final Map<String, dynamic> searchData;
  const _CarDetailContent({required this.car, required this.searchData});

  @override
  Widget build(BuildContext context) => Column(children: [
    Text((car['carName'] ?? 'Coche').toString().toUpperCase(), style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: AppColors.teal900)),
    const SizedBox(height: 20),
    _reserveBtn(context, formatCurrency(car['price'] ?? 0, car['currency'] ?? 'EUR')),
  ]);
}

class _GenericDetailContent extends StatelessWidget {
  final Map<String, dynamic> data;
  const _GenericDetailContent({required this.data});

  @override
  Widget build(BuildContext context) => Column(children: [
    Text((data['nombre'] ?? data['nombreCrucero'] ?? 'Detalles').toString(), style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900)),
    const SizedBox(height: 20),
    _reserveBtn(context, formatCurrency(data['precio'] ?? data['precioDesde'] ?? 0, data['moneda'] ?? 'EUR')),
  ]);
}

Widget _reserveBtn(BuildContext context, String price) => SizedBox(
  width: double.infinity,
  child: ElevatedButton(
    onPressed: () => Navigator.of(context).pop(),
    style: ElevatedButton.styleFrom(backgroundColor: AppColors.teal600, foregroundColor: Colors.white, padding: const EdgeInsets.symmetric(vertical: 18), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20))),
    child: Text('RESERVAR — $price', style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 13)),
  ),
);