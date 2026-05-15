// lib/widgets/payment/payment_sheet.dart
//
// Equivalente a PostBookingModal.tsx + PaymentModal.tsx
// Se muestra como BottomSheet nativo en vez de modal web.
// Abre Stripe/PayPal en el navegador del dispositivo con url_launcher.

import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../core/app_colors.dart';
import '../../services/payment_service.dart';

// ── Uso desde cualquier screen ────────────────────────────────────────────────
//
//   await PaymentSheet.show(
//     context,
//     reservaId:   data.idReserva,
//     precio:      car.price,
//     descripcion: 'Coche · 3 días',
//   );
//
//   // Para pago múltiple:
//   await PaymentSheet.show(
//     context,
//     reservaIds:  [1, 2, 3],
//     precio:      totalPendiente,
//     descripcion: '3 reservas pendientes',
//   );

class PaymentSheet extends StatefulWidget {
  final int?       reservaId;
  final List<int>? reservaIds;
  final double     precio;
  final String     descripcion;

  const PaymentSheet._({
    this.reservaId,
    this.reservaIds,
    required this.precio,
    required this.descripcion,
  });

  /// Muestra el BottomSheet y espera.
  /// Devuelve 'paid' si el usuario eligió pagar (y abrió el navegador),
  /// null si cerró sin pagar.
  static Future<String?> show(
      BuildContext context, {
        int?       reservaId,
        List<int>? reservaIds,
        required double precio,
        required String descripcion,
      }) {
    return showModalBottomSheet<String>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => PaymentSheet._(
        reservaId:   reservaId,
        reservaIds:  reservaIds,
        precio:      precio,
        descripcion: descripcion,
      ),
    );
  }

  @override
  State<PaymentSheet> createState() => _PaymentSheetState();
}

class _PaymentSheetState extends State<PaymentSheet> {
  // Paso 1 = "¿Pagar ahora o seguir?"  (equivale al primer estado de PostBookingModal)
  // Paso 2 = selección de método
  int    _step    = 1;
  String _loading = ''; // '' | 'stripe' | 'paypal'
  String _error   = '';

  bool get _isMultiple =>
      widget.reservaIds != null && widget.reservaIds!.isNotEmpty;

  String _formatPrice(double v) =>
      '${v.toStringAsFixed(2).replaceAll('.', ',')} €';

  // ── Stripe ─────────────────────────────────────────────────────────────────
  Future<void> _pagar(String metodo) async {
    setState(() { _loading = metodo; _error = ''; });
    try {
      final url = metodo == 'stripe'
          ? await PaymentService.createStripeSession(
        reservaId:  widget.reservaId,
        reservaIds: widget.reservaIds,
      )
          : await PaymentService.createPaypalSession(
        reservaId:  widget.reservaId,
        reservaIds: widget.reservaIds,
      );

      final uri = Uri.parse(url);
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
        if (mounted) Navigator.pop(context, 'paid');
      } else {
        setState(() => _error = 'No se pudo abrir el navegador.');
      }
    } catch (e) {
      setState(() => _error = e.toString().replaceFirst('Exception: ', ''));
    } finally {
      if (mounted) setState(() => _loading = '');
    }
  }

  // ══════════════════════════════════════════════════════════════════════════
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(12, 0, 12, 12),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.all(Radius.circular(32)),
      ),
      padding: const EdgeInsets.fromLTRB(24, 16, 24, 24),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        // Handle
        Container(width: 40, height: 4,
            margin: const EdgeInsets.only(bottom: 20),
            decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),

        AnimatedSwitcher(
          duration: const Duration(milliseconds: 250),
          child: _step == 1 ? _buildStep1() : _buildStep2(),
        ),
      ]),
    );
  }

  // ── Paso 1: Reserva creada — ¿Pagar ahora? ─────────────────────────────────
  Widget _buildStep1() => Column(key: const ValueKey(1), mainAxisSize: MainAxisSize.min, children: [
    // Icono
    Container(width: 64, height: 64, decoration: const BoxDecoration(color: AppColors.teal50, shape: BoxShape.circle),
        child: const Icon(LucideIcons.checkCircle, size: 36, color: AppColors.teal500)),
    const SizedBox(height: 12),
    const Text('¡Reserva creada!', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w900,
        color: AppColors.teal900, letterSpacing: -0.3)),
    const SizedBox(height: 4),
    Text(widget.descripcion, style: const TextStyle(color: Colors.grey, fontSize: 13), textAlign: TextAlign.center),
    const SizedBox(height: 12),
    // Precio
    Container(padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 10),
        decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(16)),
        child: Text(_formatPrice(widget.precio),
            style: const TextStyle(fontSize: 28, fontWeight: FontWeight.w900, color: AppColors.teal700))),
    const SizedBox(height: 16),
    // Sandbox notice
    _sandboxNotice(),
    const SizedBox(height: 20),
    // Botones
    SizedBox(width: double.infinity, child: ElevatedButton.icon(
      onPressed: () => setState(() => _step = 2),
      icon: const Icon(LucideIcons.lock, size: 16),
      label: const Text('PAGAR AHORA', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 13)),
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(0, 0),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
      ),
    )),
    const SizedBox(height: 10),
    SizedBox(width: double.infinity, child: OutlinedButton.icon(
      onPressed: () => Navigator.pop(context),
      icon: const Icon(LucideIcons.star, size: 16, color: AppColors.teal500),
      label: const Text('SEGUIR EXPLORANDO', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 13)),
      style: OutlinedButton.styleFrom(
        foregroundColor: AppColors.teal800,
        side: const BorderSide(color: AppColors.teal200),
        padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(0, 0),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
      ),
    )),
    const SizedBox(height: 8),
    const Text('Puedes pagar después desde Mis Reservas',
        style: TextStyle(color: Colors.grey, fontSize: 10, fontWeight: FontWeight.w600)),
  ]);

  // ── Paso 2: Selección de método ───────────────────────────────────────────
  Widget _buildStep2() => Column(key: const ValueKey(2), mainAxisSize: MainAxisSize.min, children: [
    // Volver
    Row(children: [
      GestureDetector(
        onTap: () => setState(() { _step = 1; _error = ''; }),
        child: const Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(LucideIcons.arrowLeft, size: 16, color: Colors.grey),
          SizedBox(width: 4),
          Text('Volver', style: TextStyle(color: Colors.grey, fontWeight: FontWeight.w700, fontSize: 12)),
        ]),
      ),
    ]),
    const SizedBox(height: 16),
    // Icono
    Container(width: 56, height: 56, decoration: const BoxDecoration(color: AppColors.teal50, shape: BoxShape.circle),
        child: const Icon(LucideIcons.lock, size: 28, color: AppColors.teal700)),
    const SizedBox(height: 10),
    const Text('Pago seguro', style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900,
        color: AppColors.teal900, letterSpacing: -0.3)),
    Text(widget.descripcion, style: const TextStyle(color: Colors.grey, fontSize: 12), textAlign: TextAlign.center),
    const SizedBox(height: 8),
    Container(padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
        decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(12)),
        child: Text(_formatPrice(widget.precio),
            style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w900, color: AppColors.teal700))),
    if (_isMultiple) Padding(
      padding: const EdgeInsets.only(top: 4),
      child: Text('Pago agrupado de ${widget.reservaIds!.length} reservas',
          style: const TextStyle(color: AppColors.teal500, fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 1)),
    ),
    const SizedBox(height: 16),
    _sandboxNotice(),
    // Error
    if (_error.isNotEmpty) ...[
      const SizedBox(height: 10),
      Container(padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: const Color(0xFFFEE2E2), borderRadius: BorderRadius.circular(12)),
          child: Text(_error, style: const TextStyle(color: Color(0xFFB91C1C), fontSize: 12, fontWeight: FontWeight.w700), textAlign: TextAlign.center)),
    ],
    const SizedBox(height: 20),
    // Stripe
    SizedBox(width: double.infinity, child: ElevatedButton.icon(
      onPressed: _loading.isNotEmpty ? null : () => _pagar('stripe'),
      icon: _loading == 'stripe'
          ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
          : const Icon(LucideIcons.creditCard, size: 18),
      label: Text(_loading == 'stripe' ? 'Redirigiendo...' : (_isMultiple ? 'PAGAR TODO CON STRIPE' : 'PAGAR CON STRIPE'),
          style: const TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 13)),
      style: ElevatedButton.styleFrom(
        backgroundColor: const Color(0xFF635BFF), foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(0, 0),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
        disabledBackgroundColor: const Color(0xFF635BFF).withOpacity(0.5),
      ),
    )),
    const SizedBox(height: 10),
    // Separador
    Row(children: [
      const Expanded(child: Divider(color: AppColors.teal100)),
      Padding(padding: const EdgeInsets.symmetric(horizontal: 12),
          child: Text('o', style: TextStyle(color: AppColors.teal900.withOpacity(0.3),
              fontSize: 11, fontWeight: FontWeight.w900))),
      const Expanded(child: Divider(color: AppColors.teal100)),
    ]),
    const SizedBox(height: 10),
    // PayPal
    SizedBox(width: double.infinity, child: ElevatedButton.icon(
      onPressed: _loading.isNotEmpty ? null : () => _pagar('paypal'),
      icon: _loading == 'paypal'
          ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(color: Color(0xFF003087), strokeWidth: 2))
          : const Icon(LucideIcons.wallet, size: 18),
      label: Text(_loading == 'paypal' ? 'Redirigiendo...' : (_isMultiple ? 'PAGAR TODO CON PAYPAL' : 'PAGAR CON PAYPAL'),
          style: const TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 13, color: Color(0xFF003087))),
      style: ElevatedButton.styleFrom(
        backgroundColor: const Color(0xFFFFC439), foregroundColor: const Color(0xFF003087),
        padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(0, 0),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
        disabledBackgroundColor: const Color(0xFFFFC439).withOpacity(0.5),
      ),
    )),
    const SizedBox(height: 12),
    const Text('Transacción cifrada SSL · JourneyMate',
        style: TextStyle(color: Colors.grey, fontSize: 10, fontWeight: FontWeight.w600)),
  ]);

  Widget _sandboxNotice() => Container(
    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
    decoration: BoxDecoration(color: const Color(0xFFFEFCE8),
        borderRadius: BorderRadius.circular(14), border: Border.all(color: const Color(0xFFFDE68A))),
    child: Row(children: [
      const Icon(LucideIcons.checkCircle, size: 15, color: Color(0xFFD97706)),
      const SizedBox(width: 8),
      const Expanded(child: Text('Modo Sandbox — no se cargará dinero real.',
          style: TextStyle(color: Color(0xFF92400E), fontSize: 11, fontWeight: FontWeight.w600))),
    ]),
  );
}