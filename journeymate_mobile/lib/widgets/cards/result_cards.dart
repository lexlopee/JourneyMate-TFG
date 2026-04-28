import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../utils/date_utils.dart';

// ══════════════════════════════════════════════════════════════════════════════
// HOTEL CARD — equivalente a HotelCard.tsx
// ══════════════════════════════════════════════════════════════════════════════
class HotelCard extends StatelessWidget {
  final Map<String, dynamic> hotel;
  final VoidCallback onViewDetails;
  final String destination;
  const HotelCard({super.key, required this.hotel, required this.onViewDetails, required this.destination});

  @override
  Widget build(BuildContext context) {
    final name        = hotel['nombre'] ?? 'Alojamiento JourneyMate';
    final rating      = (hotel['calificacion'] ?? 0) > 0 ? hotel['calificacion'].toString() : 'Nuevo';
    final image       = hotel['urlFoto'] as String?;
    final reviewWord  = hotel['reviewWord'] ?? '';
    final stars       = (hotel['propertyClass'] ?? 0) as int;
    final precio      = hotel['precio'];
    final precioOrig  = hotel['precioOriginal'];
    final moneda      = hotel['moneda'] ?? 'EUR';
    final hasDiscount = precioOrig != null && (precioOrig as num) > (precio as num? ?? 0);

    return Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.92),
        borderRadius: BorderRadius.circular(40),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 20, offset: const Offset(0, 8))],
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        // Imagen
        SizedBox(
          height: 200,
          child: Stack(children: [
            if (image != null)
              Positioned.fill(child: Image.network(image, fit: BoxFit.cover, errorBuilder: (_, __, ___) => _placeholder()))
            else
              Positioned.fill(child: _placeholder()),
            // Rating badge
            Positioned(top: 12, right: 12, child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(color: AppColors.teal900.withOpacity(0.8), borderRadius: BorderRadius.circular(20)),
              child: Row(mainAxisSize: MainAxisSize.min, children: [
                const Icon(LucideIcons.star, size: 10, color: Color(0xFFFBBF24)),
                const SizedBox(width: 4),
                Text('$rating${reviewWord.isNotEmpty ? ' ($reviewWord)' : ''}', style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w900)),
              ]),
            )),
          ]),
        ),

        Padding(
          padding: const EdgeInsets.all(20),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            // Estrellas
            if (stars > 0) Row(children: List.generate(stars, (_) => const Icon(LucideIcons.star, size: 12, color: Color(0xFFF59E0B), fill: 1.0))),
            if (stars > 0) const SizedBox(height: 6),

            Text(name.toString().toUpperCase(), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.3), maxLines: 2, overflow: TextOverflow.ellipsis),
            const SizedBox(height: 6),
            Row(children: [
              const Icon(LucideIcons.mapPin, size: 12, color: AppColors.teal500),
              const SizedBox(width: 4),
              Expanded(child: Text(destination, style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: 1.5), overflow: TextOverflow.ellipsis)),
            ]),
            const SizedBox(height: 12),
            const Divider(color: AppColors.teal100, height: 1),
            const SizedBox(height: 12),

            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                const Text('PRECIO ESTIMADO', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: Color(0xFFA7C4BD))),
                if (hasDiscount) Text(formatCurrency(precioOrig, moneda), style: const TextStyle(fontSize: 12, decoration: TextDecoration.lineThrough, color: Colors.red, fontWeight: FontWeight.w600)),
                Text(formatCurrency(precio ?? 0, moneda), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: -0.5)),
              ]),
              GestureDetector(
                onTap: onViewDetails,
                child: Container(
                  width: 48, height: 48,
                  decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(16)),
                  child: const Icon(LucideIcons.building2, color: Colors.white, size: 20),
                ),
              ),
            ]),
          ]),
        ),
      ]),
    );
  }

  Widget _placeholder() => Container(color: AppColors.teal50, child: const Center(child: Icon(LucideIcons.hotel, size: 48, color: AppColors.teal300)));
}

// ══════════════════════════════════════════════════════════════════════════════
// FLIGHT CARD — equivalente a FlightCard.tsx
// ══════════════════════════════════════════════════════════════════════════════
class FlightCard extends StatelessWidget {
  final Map<String, dynamic> flight;
  final VoidCallback onViewDetails;
  const FlightCard({super.key, required this.flight, required this.onViewDetails});

  @override
  Widget build(BuildContext context) {
    final stops     = (flight['stops'] ?? 0) as int;
    final isDirect  = stops == 0;
    final moneda    = flight['moneda'] ?? 'EUR';

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(40),
        boxShadow: [BoxShadow(color: AppColors.teal900.withOpacity(0.05), blurRadius: 20, offset: const Offset(0, 8))],
        border: Border.all(color: AppColors.teal50),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        // Badge clase + Aerolínea
        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Row(children: [
            Container(
              width: 44, height: 44,
              decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14), border: Border.all(color: AppColors.teal100)),
              child: flight['logoUrl'] != null
                  ? ClipRRect(borderRadius: BorderRadius.circular(12), child: Image.network(flight['logoUrl'], fit: BoxFit.contain, errorBuilder: (_, __, ___) => const Icon(LucideIcons.ticket, color: AppColors.teal500, size: 20)))
                  : const Icon(LucideIcons.ticket, color: AppColors.teal500, size: 20),
            ),
            const SizedBox(width: 10),
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('AEROLÍNEA', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: AppColors.teal300, letterSpacing: 2)),
              Text((flight['aerolinea'] ?? '').toString().toUpperCase(), style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.2)),
            ]),
          ]),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
            decoration: BoxDecoration(color: AppColors.teal600, borderRadius: BorderRadius.circular(20)),
            child: Text((flight['cabinClass'] ?? 'Economy').toString().toUpperCase(), style: const TextStyle(color: Colors.white, fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 1.5)),
          ),
        ]),
        const SizedBox(height: 20),

        // Trayecto
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(color: AppColors.teal50.withOpacity(0.5), borderRadius: BorderRadius.circular(28), border: Border.all(color: AppColors.teal100.withOpacity(0.3))),
          child: Row(children: [
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Row(children: [Icon(LucideIcons.planeTakeoff, size: 12, color: AppColors.teal400), SizedBox(width: 4), Text('SALIDA', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: AppColors.teal300, letterSpacing: 1.5))]),
              const SizedBox(height: 4),
              Text(formatTime(flight['horaSalida']), style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -1)),
              Text((flight['origenCode'] ?? flight['origen'] ?? '').toString().toUpperCase(), style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal700)),
            ])),

            Expanded(child: Column(children: [
              const Divider(color: AppColors.teal200),
              Container(
                padding: const EdgeInsets.all(6),
                decoration: BoxDecoration(color: Colors.white, shape: BoxShape.circle, border: Border.all(color: AppColors.teal100)),
                child: const Icon(LucideIcons.moveRight, color: AppColors.teal500, size: 16),
              ),
              const Divider(color: AppColors.teal200),
              Text(isDirect ? 'DIRECTO' : '$stops ESCALA${stops > 1 ? 'S' : ''}', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: isDirect ? AppColors.teal400 : const Color(0xFFF59E0B))),
            ])),

            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
              const Row(mainAxisAlignment: MainAxisAlignment.end, children: [Text('LLEGADA', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, color: AppColors.teal300, letterSpacing: 1.5)), SizedBox(width: 4), Icon(LucideIcons.planeLanding, size: 12, color: AppColors.teal400)]),
              const SizedBox(height: 4),
              Text(formatTime(flight['horaLlegada']), style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -1)),
              Text((flight['destinoCode'] ?? flight['destino'] ?? '').toString().toUpperCase(), style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal700)),
            ])),
          ]),
        ),
        const SizedBox(height: 20),

        Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
          Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('TOTAL ESTIMADO', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: Color(0xFFA7C4BD))),
            Text(formatCurrency(flight['precio'], moneda), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
          ]),
          ElevatedButton.icon(
            onPressed: onViewDetails,
            icon: const Icon(LucideIcons.moveRight, size: 14),
            label: const Text('DETALLES', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 10)),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            ),
          ),
        ]),
      ]),
    );
  }
}

// ══════════════════════════════════════════════════════════════════════════════
// ACTIVITY CARD — equivalente a ActivityCard.tsx
// ══════════════════════════════════════════════════════════════════════════════
class ActivityCard extends StatelessWidget {
  final Map<String, dynamic> activity;
  final VoidCallback onViewDetails;
  const ActivityCard({super.key, required this.activity, required this.onViewDetails});

  @override
  Widget build(BuildContext context) {
    final image = activity['urlFoto'] as String?;
    final nombre = activity['nombre'] ?? 'Actividad';
    final precio = activity['precio'];
    final calificacion = activity['calificacion'] ?? 0;

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(40),
        border: Border.all(color: AppColors.teal50),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 16)],
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        SizedBox(height: 180, child: Stack(children: [
          Positioned.fill(child: image != null
              ? Image.network(image, fit: BoxFit.cover, errorBuilder: (_, __, ___) => _placeholder())
              : _placeholder()),
          Positioned(top: 12, right: 12, child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
            decoration: BoxDecoration(color: Colors.white.withOpacity(0.9), borderRadius: BorderRadius.circular(20)),
            child: Text(formatCurrency(precio ?? 0, 'EUR'), style: const TextStyle(color: AppColors.teal600, fontSize: 12, fontWeight: FontWeight.w900)),
          )),
        ])),

        Padding(padding: const EdgeInsets.all(16), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Expanded(child: Text(nombre.toString(), style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900, color: AppColors.teal900), maxLines: 2, overflow: TextOverflow.ellipsis)),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(color: const Color(0xFFFEF9C3), borderRadius: BorderRadius.circular(10)),
              child: Row(mainAxisSize: MainAxisSize.min, children: [
                const Icon(LucideIcons.star, size: 10, color: Color(0xFFF59E0B)),
                const SizedBox(width: 3),
                Text(calificacion > 0 ? calificacion.toString() : 'Nuevo', style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w700, color: Color(0xFF92400E))),
              ]),
            ),
          ]),
          const SizedBox(height: 6),
          Text(activity['descripcion'] ?? '', style: const TextStyle(fontSize: 11, color: Color(0xFF9CA3AF)), maxLines: 2, overflow: TextOverflow.ellipsis),
          const SizedBox(height: 12),
          const Divider(color: AppColors.teal50, height: 1),
          const SizedBox(height: 10),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            const Row(children: [Icon(LucideIcons.clock, size: 12, color: AppColors.teal400), SizedBox(width: 4), Text('Ver disponibilidad', style: TextStyle(fontSize: 10, fontWeight: FontWeight.w700, color: AppColors.teal400))]),
            GestureDetector(
              onTap: onViewDetails,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
                child: const Text('VER DETALLES', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: 1.5)),
              ),
            ),
          ]),
        ])),
      ]),
    );
  }

  Widget _placeholder() => Container(color: const Color(0xFFFFF7ED), child: const Center(child: Icon(LucideIcons.ticket, size: 40, color: Color(0xFFFBBF24))));
}

// ══════════════════════════════════════════════════════════════════════════════
// CRUISE CARD — equivalente a CruiseCard.tsx
// ══════════════════════════════════════════════════════════════════════════════
class CruiseCard extends StatelessWidget {
  final Map<String, dynamic> cruise;
  final VoidCallback onViewDetails;
  const CruiseCard({super.key, required this.cruise, required this.onViewDetails});

  @override
  Widget build(BuildContext context) {
    final nombre  = cruise['nombreCrucero'] ?? cruise['cruise_name'] ?? 'Crucero';
    final barco   = cruise['nombreBarco'] ?? cruise['ship_name'] ?? '';
    final imagen  = cruise['imagenPrincipal'] as String?;
    final noches  = cruise['noches'] ?? cruise['duration_nights'] ?? 0;
    final puerto  = cruise['puertoSalida'] ?? cruise['departure_port'] ?? '';
    final precio  = cruise['precioDesde'] ?? cruise['price_from'] ?? 0;
    final moneda  = cruise['moneda'] ?? cruise['currency'] ?? 'USD';

    return Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.92),
        borderRadius: BorderRadius.circular(40),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 20, offset: const Offset(0, 8))],
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        SizedBox(height: 180, child: Stack(children: [
          if (imagen != null)
            Positioned.fill(child: Image.network(imagen, fit: BoxFit.cover, errorBuilder: (_, __, ___) => _placeholder()))
          else
            Positioned.fill(child: _placeholder()),
          Positioned.fill(child: Container(decoration: BoxDecoration(gradient: LinearGradient(begin: Alignment.topCenter, end: Alignment.bottomCenter, colors: [Colors.transparent, AppColors.teal900.withOpacity(0.6)])))),
          Positioned(top: 12, right: 12, child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
            decoration: BoxDecoration(color: Colors.black.withOpacity(0.4), borderRadius: BorderRadius.circular(20), border: Border.all(color: Colors.white.withOpacity(0.2))),
            child: Row(mainAxisSize: MainAxisSize.min, children: [
              const Icon(LucideIcons.moon, size: 10, color: Colors.white),
              const SizedBox(width: 4),
              Text('$noches noches', style: const TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.w900)),
            ]),
          )),
        ])),

        Padding(padding: const EdgeInsets.all(20), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(nombre.toString().toUpperCase(), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.3), maxLines: 2, overflow: TextOverflow.ellipsis),
          const SizedBox(height: 4),
          Row(children: [
            const Icon(LucideIcons.anchor, size: 10, color: AppColors.teal500),
            const SizedBox(width: 4),
            Text(barco.toString(), style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: 1.5)),
          ]),
          const SizedBox(height: 8),
          Row(children: [
            const Icon(LucideIcons.mapPin, size: 12, color: AppColors.teal500),
            const SizedBox(width: 4),
            Text(puerto.toString(), style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: 1.5)),
          ]),
          const SizedBox(height: 12),
          const Divider(color: AppColors.teal100, height: 1),
          const SizedBox(height: 12),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('DESDE', style: TextStyle(fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: Color(0xFFA7C4BD))),
              Text(formatCurrency(precio, moneda), style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal600, letterSpacing: -0.5)),
            ]),
            GestureDetector(
              onTap: onViewDetails,
              child: Container(
                width: 48, height: 48,
                decoration: BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.circular(16)),
                child: const Icon(LucideIcons.ship, color: Colors.white, size: 20),
              ),
            ),
          ]),
        ])),
      ]),
    );
  }

  Widget _placeholder() => Container(
    decoration: const BoxDecoration(gradient: LinearGradient(colors: [AppColors.teal800, AppColors.teal600], begin: Alignment.topLeft, end: Alignment.bottomRight)),
    child: const Center(child: Icon(LucideIcons.ship, size: 64, color: Color(0x33FFFFFF))),
  );
}