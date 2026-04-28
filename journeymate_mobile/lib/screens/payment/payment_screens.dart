import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';

// ── PaymentSuccess ────────────────────────────────────────────────────────────
class PaymentSuccessScreen extends StatelessWidget {
  final String? reservaId;
  final String? metodo;
  const PaymentSuccessScreen({super.key, this.reservaId, this.metodo});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 440),
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(48),
                  boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.15), blurRadius: 40, offset: const Offset(0, 16))],
                ),
                padding: const EdgeInsets.all(48),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 96, height: 96,
                      decoration: const BoxDecoration(color: AppColors.teal50, shape: BoxShape.circle),
                      child: const Icon(LucideIcons.checkCircle, size: 56, color: AppColors.teal500),
                    ),
                    const SizedBox(height: 24),
                    const Text('¡Pago completado!', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
                    if (reservaId != null) ...[
                      const SizedBox(height: 8),
                      RichText(text: TextSpan(
                        style: const TextStyle(fontSize: 14, color: Color(0xFF9CA3AF)),
                        children: [
                          const TextSpan(text: 'Reserva '),
                          TextSpan(text: '#$reservaId', style: const TextStyle(color: AppColors.teal700, fontWeight: FontWeight.w900)),
                          const TextSpan(text: ' confirmada'),
                        ],
                      )),
                    ],
                    if (metodo != null) ...[
                      const SizedBox(height: 4),
                      Text('vía ${metodo!.toUpperCase()}', style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal600)),
                    ],
                    const SizedBox(height: 24),
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(16), border: Border.all(color: AppColors.teal100)),
                      child: const Text('✅ Tu reserva está marcada como CONFIRMADA.\nRecibirás un email con tu factura en breve.', style: TextStyle(color: AppColors.teal700, fontSize: 12, fontWeight: FontWeight.w600), textAlign: TextAlign.left),
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () => context.go('/mis-reservas'),
                        icon: const Icon(LucideIcons.bookOpen, size: 18),
                        label: const Text('VER MIS RESERVAS', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 12)),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(vertical: 18),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        ),
                      ),
                    ),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton.icon(
                        onPressed: () => context.go('/'),
                        icon: const Icon(LucideIcons.home, size: 18),
                        label: const Text('VOLVER AL INICIO', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 12)),
                        style: OutlinedButton.styleFrom(
                          foregroundColor: AppColors.teal800,
                          side: const BorderSide(color: AppColors.teal200),
                          padding: const EdgeInsets.symmetric(vertical: 18),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

// ── PaymentCancelled ──────────────────────────────────────────────────────────
class PaymentCancelledScreen extends StatelessWidget {
  const PaymentCancelledScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 440),
              child: Container(
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(48),
                  boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.15), blurRadius: 40)],
                ),
                padding: const EdgeInsets.all(48),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(LucideIcons.xCircle, size: 64, color: Color(0xFFF87171)),
                    const SizedBox(height: 24),
                    const Text('Pago cancelado', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
                    const SizedBox(height: 12),
                    const Text('No se ha realizado ningún cargo.\nPuedes intentarlo de nuevo cuando quieras.', style: TextStyle(color: Color(0xFF9CA3AF), fontSize: 14, fontWeight: FontWeight.w600), textAlign: TextAlign.center),
                    const SizedBox(height: 32),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () => context.go('/'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(vertical: 18),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        ),
                        child: const Text('VOLVER AL INICIO', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 2, fontSize: 12)),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}