import 'package:shared_preferences/shared_preferences.dart';
import 'api_service.dart';

// Equivalente a localStorage para token/usuario
class AuthService {
  static const _keyToken      = 'token';
  static const _keyIdUsuario  = 'idUsuario';
  static const _keyUserName   = 'userName';
  static const _keyUserEmail  = 'userEmail';

  // ── Login ─────────────────────────────────────────────────────────────────
  static Future<Map<String, dynamic>> login(String email, String password) async {
    final data = await api.login(email, password) as Map<String, dynamic>;
    await _saveSession(data);
    return data;
  }

  // ── Register ──────────────────────────────────────────────────────────────
  static Future<Map<String, dynamic>> register(Map<String, dynamic> userData) async {
    final data = await api.register(userData) as Map<String, dynamic>;
    await _saveSession(data);
    return data;
  }

  // ── Logout ────────────────────────────────────────────────────────────────
  static Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyToken);
    await prefs.remove(_keyIdUsuario);
    await prefs.remove(_keyUserName);
    await prefs.remove(_keyUserEmail);
    api.setToken(null);
  }

  // ── Leer sesión guardada al arrancar la app ────────────────────────────────
  static Future<bool> restoreSession() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(_keyToken);
    if (token == null) return false;
    api.setToken(token);
    return true;
  }

  // ── Getters ────────────────────────────────────────────────────────────────
  static Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyToken);
  }

  static Future<String?> getUserName() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_keyUserName);
  }

  static Future<int?> getIdUsuario() async {
    final prefs = await SharedPreferences.getInstance();
    final v = prefs.getString(_keyIdUsuario);
    return v != null ? int.tryParse(v) : null;
  }

  static Future<bool> isLoggedIn() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }

  // ── Interno ────────────────────────────────────────────────────────────────
  static Future<void> _saveSession(Map<String, dynamic> data) async {
    final prefs = await SharedPreferences.getInstance();
    final token = data['token'] as String?;
    if (token != null) {
      await prefs.setString(_keyToken, token);
      api.setToken(token);
    }
    final id = data['idUsuario'];
    if (id != null) await prefs.setString(_keyIdUsuario, id.toString());
    final nombre = data['nombre'] as String?;
    if (nombre != null) await prefs.setString(_keyUserName, nombre);
    final email = data['email'] as String?;
    if (email != null) await prefs.setString(_keyUserEmail, email);
  }
}