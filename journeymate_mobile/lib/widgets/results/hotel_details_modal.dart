import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../common_widgets.dart';
// ══════════════════════════════════════════════════════════════════════════════
// MODAL DETALLES DEL HOTEL
// ══════════════════════════════════════════════════════════════════════════════
class HotelDetailsModal extends StatefulWidget {
  final Map<String, dynamic> details;   // Respuesta directa del backend
  final Map<String, dynamic> basicData; // Datos de la tarjeta anterior
  final Map<String, dynamic> searchData;

  const HotelDetailsModal({
    super.key,
    required this.details,
    required this.basicData,
    required this.searchData
  });

  @override
  State<HotelDetailsModal> createState() => _HotelDetailsModalState();
}

class _HotelDetailsModalState extends State<HotelDetailsModal> {
  int _activePhotoIndex = 0;

  @override
  Widget build(BuildContext context) {
    // 1. Extraemos el payload según el DTO del backend[cite: 9]
    final hotelData = widget.details['data'] ?? {};

    // Si no hay datos, mostramos el modal de error en lugar de dejarlo en negro
    if (hotelData.isEmpty) {
      return const ErrorModal(message: "La información del hotel no está disponible en este momento.");
    }

    // 2. Mapeo de campos dinámicos
    final hotelName = (hotelData['hotelName'] ?? widget.basicData['nombre'] ?? 'Hotel').toString().toUpperCase();
    final price = widget.basicData['precio'] ?? 0.0;
    final currency = widget.basicData['moneda'] ?? 'EUR';
    final address = (hotelData['address'] ?? widget.basicData['direccion'] ?? 'Dirección no disponible').toString();

    // 3. Gestión de Fotos (Unimos foto principal y galería)
    List<String> allPhotos = [];
    if (widget.basicData['urlFoto'] != null) {
      allPhotos.add(widget.basicData['urlFoto'].toString());
    }
    if (hotelData['images'] != null) {
      allPhotos.addAll(List<String>.from(hotelData['images']));
    }
    // Eliminamos duplicados por si la principal está en la lista
    allPhotos = allPhotos.toSet().toList();

    return DraggableScrollableSheet(
      initialChildSize: 0.9,
      minChildSize: 0.5,
      maxChildSize: 0.95,
      // Dentro de tu builder del DraggableScrollableSheet
      builder: (context, scrollController) => Container(
        width: MediaQuery.of(context).size.width,
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(40)),
        ),
        clipBehavior: Clip.antiAlias, // Para que las imágenes respeten el redondeado superior
        child: Column(children: [
          // Carrusel de imágenes
          _buildCarousel(allPhotos),

          Expanded(
            child: ListView(
              controller: scrollController,
              padding: const EdgeInsets.all(24),
              children: [
                _buildHeaderInfo(hotelName, address),
                const SizedBox(height: 24),
                _buildLocationSection(),
                const SizedBox(height: 24),
                _buildFacilities(hotelData),
                const SizedBox(height: 100), // Espacio para no chocar con el botón de reserva
              ],
            ),
          ),

          // Barra inferior de precio y reserva
          _buildBookingCard(price, currency),
        ]),
      ),
    );
  }

  Widget _buildHeaderInfo(String name, String address) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(
          name,
          style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)
      ),
      const SizedBox(height: 8),
      Row(children: [
        const Icon(LucideIcons.mapPin, size: 14, color: AppColors.teal500),
        const SizedBox(width: 6),
        Flexible(
          child: Text(
              address,
              style: const TextStyle(color: AppColors.teal600, fontWeight: FontWeight.w600, fontSize: 13)
          ),
        ),
      ]),
    ]);
  }

  Widget _buildLocationSection() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(24)),
      child: const Row(children: [
        Icon(LucideIcons.map, color: AppColors.teal600),
        SizedBox(width: 12),
        Text("Ver en el mapa", style: TextStyle(fontWeight: FontWeight.w900, color: AppColors.teal900)),  // ← sin Expanded
        Spacer(),
        Icon(LucideIcons.chevronRight, size: 16, color: AppColors.teal300),
      ]),
    );
  }

  Widget _buildFacilities(Map<String, dynamic> hotelData) {
    // Mapeo dinámico de servicios igual que en la versión de React[cite: 10]
    final facilities = hotelData['facilitiesBlock']?['facilities'] ??
        hotelData['facilities_block']?['facilities'] ?? [];

    if (facilities.isEmpty) {
      return const Text("Servicios básicos disponibles", style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold));
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text("SERVICIOS DESTACADOS", style: TextStyle(fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 1.5, color: Colors.grey)),
        const SizedBox(height: 12),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: (facilities as List).take(8).map((f) => Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(
              color: Colors.white,
              border: Border.all(color: AppColors.teal100),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Text(
              f['name']?.toString().toUpperCase() ?? 'SERVICIO',
              style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900, color: AppColors.teal700),
            ),
          )).toList(),
        ),
      ],
    );
  }

  Widget _buildBookingCard(dynamic price, String currency) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
          color: AppColors.teal900,
          borderRadius: const BorderRadius.vertical(top: Radius.circular(32)),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.2), blurRadius: 10)]
      ),
      child: SafeArea(top: false, child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
        Column(crossAxisAlignment: CrossAxisAlignment.start, mainAxisSize: MainAxisSize.min, children: [
          const Text("TOTAL ESTIMADO", style: TextStyle(color: Colors.white54, fontSize: 10, fontWeight: FontWeight.bold, letterSpacing: 1.5)),
          Text("${price.toString()} $currency", style: const TextStyle(color: Colors.white, fontSize: 26, fontWeight: FontWeight.w900)),
        ]),
        SizedBox(
          width: 140,
        child: ElevatedButton(
          onPressed: () => _handleReserve(),
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF00E676),
            foregroundColor: Colors.black,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
            padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
          ),
          child: const Text("RESERVAR", style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1)),
        ),
        )
      ])),
    );
  }

  void _handleReserve() => print("Reserva iniciada");

  Widget _buildCarousel(List<String> photos) {
    if (photos.isEmpty) return const SizedBox.shrink();

    return SizedBox(
      height: 280,
      width: double.infinity,
      child: Stack(children: [
        PageView.builder(
          onPageChanged: (i) => setState(() => _activePhotoIndex = i),
          itemCount: photos.length,
          itemBuilder: (context, i) => Image.network(
            photos[i],
            fit: BoxFit.cover,
            errorBuilder: (context, error, stackTrace) => Container(
              color: AppColors.teal50,
              child: const Icon(LucideIcons.imageOff, color: AppColors.teal200),
            ),
          ),
        ),
        Positioned(bottom: 20, left: 0, right: 0, child: Center(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
            decoration: BoxDecoration(color: Colors.black.withOpacity(0.6), borderRadius: BorderRadius.circular(20)),
            child: Text("${_activePhotoIndex + 1} / ${photos.length}", style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w900)),
          ),
        )),
        Positioned(top: 40, right: 20, child: IconButton(
          icon: Container(
              padding: EdgeInsets.all(8),
              decoration: const BoxDecoration(color: Colors.black26, shape: BoxShape.circle),
              child: const Icon(LucideIcons.x, color: Colors.white, size: 20)
          ),
          onPressed: () => Navigator.pop(context),
        )),
      ]),
    );
  }
}