import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../services/auth_service.dart';
import '../core/app_colors.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});
  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _nombre          = TextEditingController();
  final _primerApellido  = TextEditingController();
  final _segundoApellido = TextEditingController();
  final _telefono        = TextEditingController();
  final _email           = TextEditingController();
  final _pass            = TextEditingController();
  String _fechaNacimiento = '';
  bool   _showPass = false;
  bool   _loading  = false;
  String _error    = '';

  // ── DateSelector state ────────────────────────────────────────────────────
  String _day = '', _month = '', _year = '';

  @override
  void dispose() {
    for (final c in [_nombre, _primerApellido, _segundoApellido, _telefono, _email, _pass]) c.dispose();
    super.dispose();
  }

  void _updateDate(String d, String m, String y) {
    setState(() { _day = d; _month = m; _year = y; });
    if (d.isNotEmpty && m.isNotEmpty && y.isNotEmpty) {
      _fechaNacimiento = '$y-$m-$d';
    }
  }

  Future<void> _handleRegister() async {
    if (_loading) return;
    setState(() { _loading = true; _error = ''; });
    try {
      await AuthService.register({
        'nombre': _nombre.text.trim(),
        'primerApellido': _primerApellido.text.trim(),
        'segundoApellido': _segundoApellido.text.trim(),
        'telefono': _telefono.text.trim(),
        'fechaNacimiento': _fechaNacimiento,
        'email': _email.text.trim(),
        'password': _pass.text,
      });
      if (mounted) context.go('/');
    } on Exception {
      setState(() => _error = 'No se pudo crear la cuenta');
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
            if (MediaQuery.of(context).size.width > 900)
              Expanded(child: _LeftPanelRegister()),
            Expanded(
              child: Center(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 48),
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
        boxShadow: [BoxShadow(color: Colors.black26, blurRadius: 40, offset: const Offset(0, 16))],
      ),
      padding: const EdgeInsets.all(40),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Center(
            child: Column(children: [
              Container(
                width: 56, height: 56,
                decoration: BoxDecoration(
                  gradient: const LinearGradient(colors: [AppColors.teal400, AppColors.teal600], begin: Alignment.topLeft, end: Alignment.bottomRight),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: const Icon(LucideIcons.plane, color: Colors.white, size: 24),
              ),
              const SizedBox(height: 12),
              const Text('Crea tu cuenta', style: TextStyle(fontSize: 28, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.5)),
              const SizedBox(height: 4),
              Text('Empieza a explorar el mundo', style: TextStyle(fontSize: 14, color: AppColors.teal600.withOpacity(0.7))),
            ]),
          ),
          const SizedBox(height: 28),

          // Nombre + Primer apellido
          Row(children: [
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              _label('Nombre'),
              _field(_nombre, 'Juan', LucideIcons.user),
            ])),
            const SizedBox(width: 12),
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              _label('1er apellido'),
              _field(_primerApellido, 'García', LucideIcons.user),
            ])),
          ]),
          const SizedBox(height: 14),

          _label('2º apellido (opcional)'),
          _field(_segundoApellido, 'López', LucideIcons.user),
          const SizedBox(height: 14),

          _label('Teléfono (opcional)'),
          _field(_telefono, '+34 600 000 000', LucideIcons.phone, type: TextInputType.phone),
          const SizedBox(height: 14),

          // Fecha nacimiento
          _label('Fecha de nacimiento'),
          _DateSelector(day: _day, month: _month, year: _year, onChanged: _updateDate),
          const SizedBox(height: 14),

          _label('Correo electrónico'),
          _field(_email, 'tu@email.com', LucideIcons.mail, type: TextInputType.emailAddress),
          const SizedBox(height: 14),

          _label('Contraseña'),
          _fieldPass(),
          const SizedBox(height: 16),

          if (_error.isNotEmpty) ...[
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(color: const Color(0xFFFEF2F2), borderRadius: BorderRadius.circular(12)),
              child: Text(_error, style: const TextStyle(color: Color(0xFFDC2626), fontSize: 12, fontWeight: FontWeight.w600), textAlign: TextAlign.center),
            ),
            const SizedBox(height: 12),
          ],

          SizedBox(
            width: double.infinity, height: 52,
            child: ElevatedButton(
              onPressed: _loading ? null : _handleRegister,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.teal600, foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)), elevation: 4,
              ),
              child: _loading
                  ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                  : const Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                Text('CREAR CUENTA', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 2, fontSize: 13)),
                SizedBox(width: 8), Icon(LucideIcons.arrowRight, size: 16),
              ]),
            ),
          ),
          const SizedBox(height: 20),
          Center(child: RichText(
            text: TextSpan(style: const TextStyle(fontSize: 13, color: Color(0xFF9CA3AF)), children: [
              const TextSpan(text: '¿Ya tienes cuenta? '),
              WidgetSpan(child: GestureDetector(
                onTap: () => context.go('/login'),
                child: const Text('Iniciar sesión', style: TextStyle(color: AppColors.teal600, fontWeight: FontWeight.w900, fontSize: 13)),
              )),
            ]),
          )),
          const SizedBox(height: 8),
          Center(child: GestureDetector(
            onTap: () => context.go('/'),
            child: const Text('← Volver al inicio', style: TextStyle(color: AppColors.teal400, fontSize: 12, fontWeight: FontWeight.w600)),
          )),
        ],
      ),
    );
  }

  Widget _label(String text) => Padding(
    padding: const EdgeInsets.only(bottom: 6),
    child: Text(text.toUpperCase(), style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 2, color: Color(0xFF9CA3AF))),
  );

  Widget _field(TextEditingController c, String hint, IconData icon, {TextInputType? type}) =>
      Padding(
        padding: const EdgeInsets.only(bottom: 0),
        child: Container(
          decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
          child: TextField(
            controller: c, keyboardType: type,
            style: const TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w500, fontSize: 14),
            decoration: InputDecoration(
              prefixIcon: Icon(icon, size: 16, color: AppColors.teal400),
              hintText: hint, hintStyle: const TextStyle(color: AppColors.teal300, fontSize: 13),
              border: InputBorder.none,
              contentPadding: const EdgeInsets.symmetric(vertical: 14),
            ),
          ),
        ),
      );

  Widget _fieldPass() => Container(
    decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
    child: TextField(
      controller: _pass, obscureText: !_showPass,
      style: const TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w500, fontSize: 14),
      decoration: InputDecoration(
        prefixIcon: const Icon(LucideIcons.lock, size: 16, color: AppColors.teal400),
        hintText: 'Mínimo 8 caracteres', hintStyle: const TextStyle(color: AppColors.teal300, fontSize: 13),
        suffixIcon: IconButton(
          icon: Icon(_showPass ? LucideIcons.eyeOff : LucideIcons.eye, size: 18, color: AppColors.teal400),
          onPressed: () => setState(() => _showPass = !_showPass),
        ),
        border: InputBorder.none,
        contentPadding: const EdgeInsets.symmetric(vertical: 14),
      ),
    ),
  );
}

// ── Date selector ─────────────────────────────────────────────────────────────
class _DateSelector extends StatelessWidget {
  final String day, month, year;
  final void Function(String d, String m, String y) onChanged;
  const _DateSelector({required this.day, required this.month, required this.year, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    final currentYear = DateTime.now().year;
    final years  = List.generate(100, (i) => (currentYear - 18 - i).toString());
    final months = ['01','02','03','04','05','06','07','08','09','10','11','12'];
    final monthLabels = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];
    final daysInMonth = (month.isNotEmpty && year.isNotEmpty)
        ? DateTime(int.parse(year), int.parse(month) + 1, 0).day : 31;
    final days = List.generate(daysInMonth, (i) => (i + 1).toString().padLeft(2, '0'));

    return Row(children: [
      Expanded(child: _drop('Día', day, ['', ...days], (v) => onChanged(v, month, year))),
      const SizedBox(width: 8),
      Expanded(flex: 2, child: _drop('Mes', month, ['', ...months], (v) => onChanged(day, v, year), labels: ['Mes', ...monthLabels])),
      const SizedBox(width: 8),
      Expanded(child: _drop('Año', year, ['', ...years], (v) => onChanged(day, month, v))),
    ]);
  }

  Widget _drop(String hint, String val, List<String> items, ValueChanged<String> onChanged, {List<String>? labels}) =>
      Container(
        decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14)),
        padding: const EdgeInsets.symmetric(horizontal: 12),
        child: DropdownButtonHideUnderline(
          child: DropdownButton<String>(
            value: val.isEmpty ? '' : val,
            isExpanded: true,
            style: const TextStyle(color: AppColors.teal900, fontWeight: FontWeight.w500, fontSize: 13),
            items: items.asMap().entries.map((e) => DropdownMenuItem(
              value: e.value,
              child: Text(labels != null ? labels[e.key] : (e.value.isEmpty ? hint : e.value)),
            )).toList(),
            onChanged: (v) { if (v != null) onChanged(v); },
          ),
        ),
      );
}

// ── Panel izquierdo ───────────────────────────────────────────────────────────
class _LeftPanelRegister extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    const features = ['✈️  Busca vuelos al mejor precio', '🏨  Compara hoteles en todo el mundo', '🚗  Alquila coches sin complicaciones', '🎟️  Reserva actividades únicas'];
    return Padding(
      padding: const EdgeInsets.all(48),
      child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Container(
          width: 80, height: 80,
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.2), borderRadius: BorderRadius.circular(28), border: Border.all(color: Colors.white.withOpacity(0.3))),
          child: const Icon(LucideIcons.plane, size: 36, color: Colors.white),
        ),
        const SizedBox(height: 24),
        const Text('JourneyMate', style: TextStyle(fontSize: 42, fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: -1)),
        const SizedBox(height: 40),
        ...features.map((f) => Container(
          margin: const EdgeInsets.only(bottom: 12),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
          decoration: BoxDecoration(color: Colors.white.withOpacity(0.1), borderRadius: BorderRadius.circular(20), border: Border.all(color: Colors.white.withOpacity(0.15))),
          child: Text(f, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600, fontSize: 14)),
        )),
      ]),
    );
  }
}