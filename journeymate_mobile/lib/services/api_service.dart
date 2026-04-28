import 'dart:convert';
import 'package:http/http.dart' as http;

// ── Cambia esta URL por la de tu backend ──────────────────────────────────────
const String _baseUrl = 'http://10.0.2.2:8080/api/v1';
const String _authUrl = 'http://10.0.2.2:8080/auth';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  // Token actual (se actualiza tras login)
  String? _token;
  void setToken(String? token) => _token = token;

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (_token != null) 'Authorization': 'Bearer $_token',
  };

  // ── GET genérico ─────────────────────────────────────────────────────────────
  Future<dynamic> get(String path, {Map<String, dynamic>? params}) async {
    final uri = Uri.parse('$_baseUrl$path').replace(
      queryParameters: params?.map((k, v) => MapEntry(k, v?.toString())),
    );
    final response = await http.get(uri, headers: _headers);
    return _handle(response);
  }

  // ── POST genérico ─────────────────────────────────────────────────────────────
  Future<dynamic> post(String path, Map<String, dynamic> body) async {
    final uri = Uri.parse('$_baseUrl$path');
    final response = await http.post(uri, headers: _headers, body: jsonEncode(body));
    return _handle(response);
  }

  // ── Auth ──────────────────────────────────────────────────────────────────────
  Future<dynamic> login(String email, String password) async {
    final uri = Uri.parse('$_authUrl/login');
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    return _handle(response);
  }

  Future<dynamic> register(Map<String, dynamic> data) async {
    final uri = Uri.parse('$_authUrl/register');
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(data),
    );
    return _handle(response);
  }

  // ── Reserva ───────────────────────────────────────────────────────────────────
  Future<dynamic> createReserva(Map<String, dynamic> body) async {
    return post('/reservas/completa', body);
  }

  dynamic _handle(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      final body = response.body;
      if (body.isEmpty) return null;
      try {
        return jsonDecode(body);
      } catch (_) {
        return body; // texto plano (p.ej. AI responses)
      }
    }
    throw ApiException(response.statusCode, response.body);
  }
}

class ApiException implements Exception {
  final int statusCode;
  final String message;
  ApiException(this.statusCode, this.message);

  @override
  String toString() => 'ApiException($statusCode): $message';
}

// Instancia global
final api = ApiService();