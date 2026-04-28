import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../services/auth_service.dart';
import '../core/app_colors.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});
  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _emailCtrl    = TextEditingController();
  final _passCtrl     = TextEditingController();
  bool _showPass      = false;
  bool _loading       = false;
  String _error       = '';

  @override
  void dispose() {
    _emailCtrl.dispose();
    _passCtrl.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (_loading) return;
    setState(() { _loading = true; _error = ''; });
    try {
      await AuthService.login(_emailCtrl.text.trim(), _passCtrl.text);
      if (mounted) context.go('/');
    } on Exception catch (e) {
      setState(() { _error = e.toString().contains('401') ? 'Credenciales incorrectas' : 'Error de conexión con el servidor'; });
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientDark),
        child: Row(
          children: [
            // ── Panel izquierdo decorativo (solo tablet/desktop) ──────────
            if (MediaQuery.of(context).size.width > 900)
              Expanded(child: _LeftPanel()),

            // ── Formulario ────────────────────────────────────────────────
            Expanded(
              child: Center(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.all(24),
                  child: ConstrainedBox(
                    constraints: const BoxConstraints(maxWidth: 440),
                    child: _buildCard(),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCard() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.95),
        borderRadius: BorderRadius.circular(40),
        boxShadow: const [BoxShadow(color: Colors.black26, blurRadius: 40, offset: Offset(0, 16))],
      ),
      padding: const EdgeInsets.all(40),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Header
          Container(
            width: 56, height: 56,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [AppColors.teal400, AppColors.teal600],
                begin: Alignment.topLeft, end: Alignment.bottomRight,
              ),
              borderRadius: BorderRadius.circular(16),
            ),
            child: const Icon(LucideIcons.plane, color: Colors.white, size: 24),
          ),
          const SizedBox(height: 16),
          const Text('Bienvenido', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
          const SizedBox(height: 4),
          Text('Inicia sesión en tu cuenta', style: TextStyle(fontSize: 14, color: AppColors.teal600.withOpacity(0.7))),
          const SizedBox(height: 32),

          // Email
          _buildLabel('Correo electrónico'),
          _buildField(controller: _emailCtrl, hint: 'tu@email.com', icon: LucideIcons.mail, keyboardType: TextInputType.emailAddress),
          const SizedBox(height: 16),

          // Password
          _buildLabel('Contraseña'),
          _buildField(
            controller: _passCtrl,
            hint: '••••••••',
            icon: LucideIcons.lock,
            obscure: !_showPass,
            suffix: IconButton(
              icon: Icon(_showPass ? LucideIcons.eyeOff : LucideIcons.eye, size: 18, color: AppColors.teal400),
              onPressed: () => setState(() => _showPass = !_showPass),
            ),
          ),
          const SizedBox(height: 16),

          // Error
          if (_error.isNotEmpty) ...[
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(color: const Color(0xFFFEF2F2), border: Border.all(color: const Color(0xFFFEE2E2)), borderRadius: BorderRadius.circular(12)),
              child: Text(_error, style: const TextStyle(color: Color(0xFFDC2626), fontSize: 12, fontWeight: FontWeight.w600), textAlign: TextAlign.center),
            ),
            const SizedBox(height: 12),
          ],

          // Botón
          SizedBox(
            width: double.infinity,
            height: 52,
            child: ElevatedButton(
              onPressed: _loading ? null : _handleLogin,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.teal600,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                elevation: 4,
              ),
              child: _loading
                  ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : const Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                Text('ACCEDER', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 2, fontSize: 13)),
                SizedBox(width: 8),
                Icon(LucideIcons.arrowRight, size: 16),
              ]),
            ),
          ),
          const SizedBox(height: 20),

          // Link a registro
          RichText(
            text: TextSpan(
              style: const TextStyle(fontSize: 13, color: Color(0xFF9CA3AF)),
              children: [
                const TextSpan(text: '¿No tienes cuenta? '),
                WidgetSpan(
                  child: GestureDetector(
                    onTap: () => context.go('/register'),
                    child: const Text('Crear cuenta gratis', style: TextStyle(color: AppColors.teal600, fontWeight: FontWeight.w900, fontSize: 13)),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          GestureDetector(
            onTap: () => context.go('/'),
            child: const Text('← Volver al inicio', style: TextStyle(color: AppColors.teal400, fontSize: 12, fontWeight: FontWeight.w600)),
          ),
        ],
      ),
    );
  }

  Widget _buildLabel(String text) => Padding(
    padding: const EdgeInsets.only(bottom: 6),
    child: Align(
      alignment: Alignment.centerLeft,
      child: Text(text.toUpperCase(), style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, letterSpacing: 2, color: Color(0xFF9CA3AF))),
    ),
  );

  Widget _buildField({
    required TextEditingController controller,
    required String hint,
    required IconData icon,
    TextInputType? keyboardType,
    bool obscure = false,
    Widget? suffix,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.teal50,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: Colors.transparent, width: 2),
      ),
      child: TextField(
        controller: controller,
        obscureText: obscure,
        keyboardType: keyboardType,
        style: const TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w500, fontSize: 14),
        decoration: InputDecoration(
          prefixIcon: Icon(icon, size: 18, color: AppColors.teal400),
          suffixIcon: suffix,
          hintText: hint,
          hintStyle: const TextStyle(color: AppColors.teal300, fontSize: 14),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(vertical: 14),
        ),
        onSubmitted: (_) => _handleLogin(),
      ),
    );
  }
}

// ── Panel decorativo izquierdo ────────────────────────────────────────────────
class _LeftPanel extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    const stats = ['2M+ Viajeros', '150+ Destinos', '24/7 Soporte', 'Pago seguro'];
    return Padding(
      padding: const EdgeInsets.all(48),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 80, height: 80,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(28),
              border: Border.all(color: Colors.white.withOpacity(0.3)),
            ),
            child: const Icon(LucideIcons.plane, size: 36, color: Colors.white),
          ),
          const SizedBox(height: 24),
          const Text('JourneyMate', style: TextStyle(fontSize: 42, fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: -1)),
          const SizedBox(height: 8),
          Text('Tu compañero de viaje inteligente', style: TextStyle(fontSize: 16, color: Colors.white.withOpacity(0.6))),
          const SizedBox(height: 40),
          Wrap(
            spacing: 12, runSpacing: 12,
            children: stats.map((s) => Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: Colors.white.withOpacity(0.15)),
              ),
              child: Text(s, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 12, letterSpacing: 1)),
            )).toList(),
          ),
        ],
      ),
    );
  }
}