import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter/widgets.dart';

enum Section {
  alojamiento('Alojamiento', LucideIcons.hotel),
  vuelos('Vuelos',           LucideIcons.plane),
  coches('Coches',           LucideIcons.car),
  actividades('Actividades', LucideIcons.ticket),
  cruceros('Cruceros',       LucideIcons.ship),
  trenes('Trenes',           LucideIcons.car);

  final String label;
  final IconData icon;
  const Section(this.label, this.icon);
}