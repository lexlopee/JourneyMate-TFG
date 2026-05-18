import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';

import '../../core/app_colors.dart';
import '../../screens/search_section.dart';
import '../../services/search_service.dart';
import '../../utils/date_utils.dart';
import '../cards/result_cards.dart';
import '../common_widgets.dart';
import 'activity_details_modal.dart';
import 'car_details_modal.dart';
import 'cruise_details_modal.dart';
import 'flight_details_modal.dart';
import 'hotel_details_modal.dart';

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
      return const Center(child: Padding(padding: EdgeInsets.all(40)));
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
          Icon(LucideIcons.star, size: 12, color: AppColors.teal600),
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
          ('default',    'Recomendados',  LucideIcons.settings),
          ('price_asc',  'Más Barato',    LucideIcons.chevronDown),
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

  Widget _buildCard(dynamic rawItem) {
    final item = (rawItem is Map<String, dynamic>) ? rawItem : Map<String, dynamic>.from(rawItem as Map);
    switch (widget.section) {
      case Section.alojamiento:
        return HotelCard(
            hotel: item,
            destination: widget.destination,
            onViewDetails: () => _showHotelDetail(item)
        );
      case Section.vuelos:
        return FlightCard(flight: item, onViewDetails: () => _showFlightDetail(item));
      case Section.actividades:
        return ActivityCard(activity: item, onViewDetails: () => _showActivityDetail(item));
      case Section.cruceros:
        return CruiseCard(cruise: item, onViewDetails: () => _showCruiseDetail(item));
      case Section.coches:
        return CarCardWidget(car: item, searchData: widget.searchData, onRent: () => _showCarDetail(item));
      default:
        return const SizedBox.shrink();
    }
  }

  void _showHotelDetail(Map<String, dynamic> item) {
    final String hotelId = (item['hotelId'] ?? item['id'] ?? '').toString();

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      backgroundColor: Colors.transparent,
      useRootNavigator: true,
      builder: (context) => FutureBuilder(
        future: SearchService.getHotelDetails(hotelId, widget.searchData),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const LoadingModal();
          }

          if (snapshot.hasError || !snapshot.hasData) {
            return const ErrorModal(message: "No pudimos obtener los detalles del hotel en este momento.");
          }

          return HotelDetailsModal(
            details: snapshot.data as Map<String, dynamic>, // Pasa el mapa directo
            basicData: item,
            searchData: widget.searchData,
          );
        },
      ),
    );
  }

  void _showFlightDetail(Map<String, dynamic> item) {
    final String token = (item['token'] ?? '').toString();
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      backgroundColor: Colors.transparent,
      useRootNavigator: true,
      builder: (context) => FutureBuilder(
        future: SearchService.getFlightDetails(token, currency: 'EUR'),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const LoadingModal();
          }
          if (snapshot.hasError || !snapshot.hasData) {
            return const ErrorModal(message: "No pudimos obtener los detalles del vuelo en este momento.");
          }
          return FlightDetailsModal(
            details: snapshot.data as Map<String, dynamic>,
            onClose: () => Navigator.pop(context),
          );
        },
      ),
    );
  }

  void _showActivityDetail(Map<String, dynamic> item) {
    final slug = (item['slug'] ?? item['id'] ?? '').toString();

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      backgroundColor: Colors.transparent,
      useRootNavigator: true,
      builder: (context) => FutureBuilder(
        future: SearchService.getActivityDetails(slug),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const LoadingModal();
          }
          if (snapshot.hasError || !snapshot.hasData) {
            return const ErrorModal(message: "No pudimos obtener los detalles de la actividad.");
          }
          return ActivityDetailsModal(
            basicData: item,
            details: snapshot.data as Map<String, dynamic>,
            searchData: widget.searchData,
          );
        },
      ),
    );
  }

  void _showCruiseDetail(Map<String, dynamic> item) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      backgroundColor: Colors.transparent,
      builder: (_) => CruiseDetailsModal(
        cruise: item,
        searchData: widget.searchData,
      ),
    );
  }

  void _showCarDetail(Map<String, dynamic> item) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      backgroundColor: Colors.transparent,
      useRootNavigator: true,
      builder: (_) => CarDetailsModal(
        car: item,
        searchData: widget.searchData,
      ),
    );
  }
}

class CarCardWidget extends StatelessWidget {
  final Map<String, dynamic> car;
  final Map<String, dynamic> searchData;
  final VoidCallback onRent;
  const CarCardWidget({super.key, required this.car, required this.searchData, required this.onRent});

  @override
  Widget build(BuildContext context) {
    final days = daysBetween(searchData['startDate'], searchData['endDate']);
    final price = car['price'] ?? car['precio'] ?? 0;
    final currency = car['currency'] ?? 'EUR';
    final transmission = (car['transmission'] ?? '').toString();
    final isAutomatic = transmission.toLowerCase().contains('auto');

    return Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.92),
        borderRadius: BorderRadius.circular(40),
        border: Border.all(color: Colors.white.withOpacity(0.2)),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 20, offset: const Offset(0, 4))],
      ),
      clipBehavior: Clip.antiAlias,
      child: IntrinsicHeight(
        child: Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
          Container(
            width: MediaQuery.of(context).size.width * 0.38,
            color: const Color(0xFFF9FAFB),
            child: Stack(children: [
              Positioned.fill(child: Container(decoration: const BoxDecoration(gradient: LinearGradient(begin: Alignment.topLeft, end: Alignment.bottomRight, colors: [Color(0x0D14B8A6), Colors.transparent])))),
              Center(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: car['imageUrl'] != null
                      ? Image.network(car['imageUrl'], fit: BoxFit.contain, errorBuilder: (_, __, ___) => const Icon(LucideIcons.car, size: 56, color: AppColors.teal300))
                      : const Icon(LucideIcons.car, size: 56, color: AppColors.teal300),
                ),
              ),
              Positioned(top: 10, left: 10,
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(color: Colors.white.withOpacity(0.9), borderRadius: BorderRadius.circular(20), border: Border.all(color: const Color(0xFFE5E7EB))),
                  child: Row(mainAxisSize: MainAxisSize.min, children: [
                    const Icon(LucideIcons.building, size: 10, color: AppColors.teal600),
                    const SizedBox(width: 4),
                    Text((car['vendorName'] ?? 'Proveedor').toString().toUpperCase(), style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: 1)),
                  ]),
                ),
              ),
            ]),
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                Text((car['carName'] ?? 'Coche').toString().toUpperCase(), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5, fontStyle: FontStyle.italic), maxLines: 2, overflow: TextOverflow.ellipsis),
                const SizedBox(height: 12),
                Wrap(spacing: 12, runSpacing: 8, children: [
                  _spec(LucideIcons.users, '${car['seats'] ?? 5} plazas'),
                  _spec(LucideIcons.briefcase, '${car['bags'] ?? 2} maletas'),
                  _spec(LucideIcons.settings, isAutomatic ? 'Automático' : 'Manual'),
                  _spec(LucideIcons.info, 'Lleno'),
                ]),
                const Divider(color: Color(0xFFF3F4F6), height: 32),
                Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, crossAxisAlignment: CrossAxisAlignment.end, children: [
                  Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('$days ${days == 1 ? 'día' : 'días'} · Tasas incl.', style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: Color(0xFF9CA3AF), letterSpacing: 1)),
                    Text(formatCurrency(price, currency), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal600)),
                  ]),
                  GestureDetector(
                    onTap: onRent,
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                      decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(20)),
                      child: const Text('ALQUILAR', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 9, letterSpacing: 1.5)),
                    ),
                  ),
                ]),
              ]),
            ),
          ),
        ]),
      ),
    );
  }

  Widget _spec(IconData icon, String label) => Row(mainAxisSize: MainAxisSize.min, children: [
    Icon(icon, size: 13, color: AppColors.teal500),
    const SizedBox(width: 4),
    Text(label, style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w600, color: AppColors.teal900)),
  ]);
}

// ── COMPONENTES DE SOPORTE PARA OTROS DETALLES ──────────────────────────────

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