import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../core/app_colors.dart';

class FooterWidget extends StatelessWidget {
  const FooterWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppColors.teal950,
      child: Column(children: [
        // Banda superior
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
          decoration: const BoxDecoration(border: Border(bottom: BorderSide(color: Color(0xFF1F4A47)))),
          child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              RichText(text: const TextSpan(style: TextStyle(fontSize: 28, fontWeight: FontWeight.w900, letterSpacing: -1), children: [
                TextSpan(text: 'Journey', style: TextStyle(color: Colors.white)),
                TextSpan(text: 'Mate', style: TextStyle(color: AppColors.teal400)),
              ])),
              const SizedBox(height: 4),
              const Text('TU COMPAÑERO DE VIAJE INTELIGENTE', style: TextStyle(color: AppColors.teal400, fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 2)),
            ]),
          ]),
        ).animate().fadeIn(duration: 600.ms).slideY(begin: 0.3),

        // Columnas
        Padding(
          padding: const EdgeInsets.all(24),
          child: Column(children: [
            _buildAboutCol(),
            const SizedBox(height: 24),
            _buildLegalCol(),
            const SizedBox(height: 24),
            _buildContactCol(),
          ]),
        ),

        // Barra inferior
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          decoration: const BoxDecoration(border: Border(top: BorderSide(color: Color(0xFF1F4A47)))),
          child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Text('© ${DateTime.now().year} JourneyMate — Todos los derechos reservados', style: const TextStyle(color: Color(0xFF2D6B67), fontSize: 9, fontWeight: FontWeight.w700, letterSpacing: 1)),
            const Row(children: [
              Icon(LucideIcons.shieldCheck, size: 10, color: Color(0xFF1F5B57)),
              SizedBox(width: 4),
              Text('PAGO SEGURO', style: TextStyle(color: Color(0xFF1F5B57), fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 2)),
            ]),
          ]),
        ),
      ]),
    );
  }

  Widget _buildAboutCol() => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    const Text('SOBRE NOSOTROS', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 3, color: AppColors.teal400)),
    const SizedBox(height: 12),
    const Text('Exploramos el mundo para traerte las mejores ofertas de viaje. Hoteles, vuelos, actividades y más — todo en un solo lugar.', style: TextStyle(color: Color(0xFF5ABFBA), fontSize: 12, height: 1.6)),
    const SizedBox(height: 12),
    Row(children: [
      _socialBtn(LucideIcons.instagram),
      const SizedBox(width: 8),
      _socialBtn(LucideIcons.twitter),
      const SizedBox(width: 8),
      _socialBtn(LucideIcons.facebook),
    ]),
  ]);

  Widget _buildLegalCol() => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    const Text('LEGAL', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 3, color: AppColors.teal400)),
    const SizedBox(height: 12),
    ...[
      (LucideIcons.fileText, 'Aviso Legal'),
      (LucideIcons.shieldCheck, 'Política de Privacidad'),
      (LucideIcons.fileText, 'Términos de Uso'),
      (LucideIcons.messageCircle, 'Contacto'),
    ].map((item) => Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Row(children: [
        Icon(item.$1, size: 12, color: AppColors.teal600),
        const SizedBox(width: 8),
        Text(item.$2, style: const TextStyle(color: Color(0xFF5ABFBA), fontSize: 12, fontWeight: FontWeight.w700)),
      ]),
    )),
  ]);

  Widget _buildContactCol() => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    const Text('CONTACTO', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 3, color: AppColors.teal400)),
    const SizedBox(height: 12),
    ...[
      (LucideIcons.phone, '+34 123 456 789'),
      (LucideIcons.mail,  'info@journeymate.com'),
      (LucideIcons.mapPin,'Madrid, España'),
    ].map((item) => Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Row(children: [
        Container(
          width: 28, height: 28,
          decoration: BoxDecoration(color: const Color(0xFF1A3E3B), borderRadius: BorderRadius.circular(8), border: Border.all(color: const Color(0xFF1F5B57))),
          child: Icon(item.$1, size: 14, color: AppColors.teal400),
        ),
        const SizedBox(width: 10),
        Text(item.$2, style: const TextStyle(color: Color(0xFF5ABFBA), fontSize: 12, fontWeight: FontWeight.w700)),
      ]),
    )),
  ]);

  Widget _socialBtn(IconData icon) => Container(
    width: 36, height: 36,
    decoration: BoxDecoration(color: const Color(0xFF1A3E3B), borderRadius: BorderRadius.circular(10), border: Border.all(color: const Color(0xFF1F5B57))),
    child: Icon(icon, size: 16, color: AppColors.teal300),
  );
}