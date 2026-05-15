// lib/screens/search_section.dart
//
// Enum Section en archivo propio para evitar importación circular entre
// search_screen.dart ↔ search_form_widget.dart
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter/widgets.dart';

enum Section {
  alojamiento('Alojamiento', LucideIcons.hotel),
  vuelos('Vuelos',           LucideIcons.plane),
  coches('Coches',           LucideIcons.car),
  actividades('Actividades', LucideIcons.ticket),
  cruceros('Cruceros',       LucideIcons.ship),
  trenes('Trenes',           LucideIcons.car); // LucideIcons.car no existe en v1.0.0

  final String label;
  final IconData icon;
  const Section(this.label, this.icon);
}