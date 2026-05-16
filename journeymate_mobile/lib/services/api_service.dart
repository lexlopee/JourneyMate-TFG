import 'dart:convert';
import 'package:http/http.dart' as http;

const String _baseUrl = 'https://journeymate-backend-ifbynfjw3a-ew.a.run.app/api/v1';
const String _authUrl = 'https://journeymate-backend-ifbynfjw3a-ew.a.run.app/auth';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  String? _token;
  void setToken(String? token) => _token = token;

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (_token != null) 'Authorization': 'Bearer $_token',
  };

  Future<dynamic> get(String path, {Map<String, dynamic>? params}) async {
    final uri = Uri.parse('$_baseUrl$path').replace(
      queryParameters: params?.map((k, v) => MapEntry(k, v?.toString())),
    );
    final response = await http.get(uri, headers: _headers);
    return _handle(response);
  }

  Future<dynamic> post(String path, Map<String, dynamic> body) async {
    final uri = Uri.parse('$_baseUrl$path');
    final response = await http.post(uri, headers: _headers, body: jsonEncode(body));
    return _handle(response);
  }

  // ── DELETE ────────────────────────────────────────────────────────────────
  // Tu backend: DELETE /api/v1/reservas/{id} → 204 No Content
  Future<dynamic> delete(String path) async {
    final uri = Uri.parse('$_baseUrl$path');
    final response = await http.delete(uri, headers: _headers);
    if (response.statusCode == 204) return null; // éxito sin cuerpo
    return _handle(response);
  }

  Future<dynamic> login(String email, String password) async {
    final uri = Uri.parse('$_authUrl/login');
    final response = await http.post(uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    return _handle(response);
  }

  Future<dynamic> register(Map<String, dynamic> data) async {
    final uri = Uri.parse('$_authUrl/register');
    final response = await http.post(uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(data),
    );
    return _handle(response);
  }

  Future<dynamic> createReserva(Map<String, dynamic> body) async {
    return post('/reservas/completa', body);
  }

  dynamic _handle(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      final body = response.body;
      if (body.isEmpty) return null;
      try { return jsonDecode(body); }
      catch (_) { return body; }
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

final api = ApiService();