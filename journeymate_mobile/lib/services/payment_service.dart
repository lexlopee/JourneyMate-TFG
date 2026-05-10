// lib/services/payment_service.dart
//
// Equivalente a PaymentModal.tsx y PostBookingModal.tsx
// Llama a las mismas APIs del backend Java que usabas en React:
//   POST /api/v1/stripe/create-checkout  → Stripe
//   POST /api/v1/payment/create          → PayPal
//   PATCH /api/v1/reservas/{id}/estado   → cambiar estado

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'auth_service.dart';
import 'api_service.dart';

// URL base — misma que api_service.dart
const String _baseUrl = 'http://10.0.2.2:8080/api/v1';

class PaymentService {

  // ── STRIPE ─────────────────────────────────────────────────────────────────
  /// Devuelve la URL de checkout de Stripe.
  /// Si es pago múltiple pasa [reservaIds], si es individual pasa [reservaId].
  static Future<String> createStripeSession({
    int? reservaId,
    List<int>? reservaIds,
  }) async {
    final token = await AuthService.getToken();
    if (token == null) throw Exception('No autenticado');

    final body = reservaIds != null && reservaIds.isNotEmpty
        ? {'reservaIds': reservaIds}
        : {'idReserva': reservaId};

    final res = await http.post(
      Uri.parse('$_baseUrl/stripe/create-checkout'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(body),
    );

    if (res.statusCode >= 200 && res.statusCode < 300) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final url = data['url'] as String?;
      if (url != null && url.isNotEmpty) return url;
      throw Exception(data['error'] ?? 'Error al iniciar Stripe');
    }
    throw Exception('Error ${res.statusCode}: ${res.body}');
  }

  // ── PAYPAL ─────────────────────────────────────────────────────────────────
  /// Devuelve la URL de aprobación de PayPal.
  static Future<String> createPaypalSession({
    int? reservaId,
    List<int>? reservaIds,
  }) async {
    final token = await AuthService.getToken();
    if (token == null) throw Exception('No autenticado');

    final body = reservaIds != null && reservaIds.isNotEmpty
        ? {'reservaIds': reservaIds}
        : {'idReserva': reservaId};

    final res = await http.post(
      Uri.parse('$_baseUrl/payment/create'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(body),
    );

    if (res.statusCode >= 200 && res.statusCode < 300) {
      final data = jsonDecode(res.body) as Map<String, dynamic>;
      final url = data['url'] as String?;
      if (url != null && url.isNotEmpty) return url;
      throw Exception(data['error'] ?? 'Error al iniciar PayPal');
    }
    throw Exception('Error ${res.statusCode}: ${res.body}');
  }

  // ── CAMBIAR ESTADO ─────────────────────────────────────────────────────────
  static Future<void> cambiarEstado(int idReserva, String estado) async {
    await api.post('/reservas/$idReserva/estado', {'estado': estado});
  }

  // ── COMPLETAR EXPIRADAS (equivalente al loop de Misreservas.tsx) ───────────
  /// Manda PATCH al backend para todas las confirmadas con fecha pasada.
  static Future<void> completarExpiradas(List<Map<String, dynamic>> reservas) async {
    for (final r in reservas) {
      final estado = (r['estadoNombre'] ?? '').toString().toLowerCase();
      final fecha  = (r['fechaReserva'] ?? r['fechaServicio'] ?? '').toString();
      if (estado == 'confirmada' && _estaExpirada(fecha)) {
        try {
          await cambiarEstado(r['idReserva'] as int, 'COMPLETADA');
        } catch (_) { /* silencioso */ }
      }
    }
  }

  static bool _estaExpirada(String fecha) {
    if (fecha.isEmpty) return false;
    try {
      final p = fecha.split('-');
      final inicio = DateTime(int.parse(p[0]), int.parse(p[1]), int.parse(p[2]));
      return DateTime.now().isAtSameMomentAs(inicio) || DateTime.now().isAfter(inicio);
    } catch (_) { return false; }
  }
}