import 'dart:convert';

import 'package:http/http.dart' as http;

import 'api_service.dart';
import 'auth_service.dart';

const String _baseUrl = 'https://journeymate-backend-ifbynfjw3a-ew.a.run.app/api/v1';

class PaymentService {

  // ── STRIPE ─────────────────────────────────────────────────────────────────
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