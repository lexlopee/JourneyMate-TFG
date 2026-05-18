import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';

import '../core/app_colors.dart';
import '../services/search_service.dart';

class AITravelAssistant extends StatefulWidget {
  const AITravelAssistant({super.key});

  @override
  State<AITravelAssistant> createState() => _AITravelAssistantState();
}

class _AITravelAssistantState extends State<AITravelAssistant> with SingleTickerProviderStateMixin {
  bool   _isOpen     = false;
  String _mode       = 'recommend';
  bool   _isLoading  = false;
  String _result     = '';

  final _prefCtrl   = TextEditingController();
  final _budgetCtrl = TextEditingController();
  final _queryCtrl  = TextEditingController();

  late AnimationController _animCtrl;
  late Animation<double>   _scaleAnim;

  @override
  void initState() {
    super.initState();
    _animCtrl = AnimationController(vsync: this, duration: const Duration(milliseconds: 300));
    _scaleAnim = CurvedAnimation(parent: _animCtrl, curve: Curves.easeOutBack);
  }

  @override
  void dispose() {
    _animCtrl.dispose();
    _prefCtrl.dispose();
    _budgetCtrl.dispose();
    _queryCtrl.dispose();
    super.dispose();
  }

  void _toggle() {
    setState(() => _isOpen = !_isOpen);
    if (_isOpen) {
      _animCtrl.forward();
    } else {
      _animCtrl.reverse();
    }
  }

  Future<void> _submit() async {
    if (_mode == 'recommend' && (_prefCtrl.text.isEmpty || _budgetCtrl.text.isEmpty)) return;
    if (_mode == 'plan' && _queryCtrl.text.isEmpty) return;

    setState(() {
      _isLoading = true;
      _result = '';
    });

    try {
      final String response;
      if (_mode == 'recommend') {
        response = await SearchService.askAI(_prefCtrl.text, _budgetCtrl.text);
      } else {
        response = await SearchService.getItinerary(_queryCtrl.text);
      }

      setState(() {
        _result = response;
        _prefCtrl.clear();
        _budgetCtrl.clear();
        _queryCtrl.clear();
      });
    } catch (e) {
      setState(() => _result = 'Error de conexión con el asistente.');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        if (_isOpen)
          ScaleTransition(
            scale: _scaleAnim,
            alignment: Alignment.bottomRight,
            child: _buildPanel(),
          ),
        const SizedBox(height: 12),
        GestureDetector(
          onTap: _toggle,
          child: Container(
            width: 48, height: 48,
            decoration: BoxDecoration(
              color: AppColors.teal900,
              shape: BoxShape.circle,
              boxShadow: [BoxShadow(
                color: AppColors.teal900.withOpacity(0.4),
                blurRadius: 20,
                offset: const Offset(0, 8),
              )],
            ),
            child: _isOpen
                ? const Icon(LucideIcons.x, color: Colors.white, size: 20)
                : const _RobotIcon(size: 22),
          ),
        ),
      ],
    );
  }

  Widget _buildPanel() {
    return Container(
      width: 340,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(28),
        boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.15), blurRadius: 32, offset: const Offset(0, 12))],
        border: Border.all(color: AppColors.teal100),
      ),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Container(
          padding: const EdgeInsets.all(16),
          decoration: const BoxDecoration(color: AppColors.teal900, borderRadius: BorderRadius.vertical(top: Radius.circular(28))),
          child: Column(children: [
            Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
              const Row(children: [
                Icon(LucideIcons.star, color: AppColors.teal400, size: 18),
                SizedBox(width: 8),
                Text('JOURNEYMATE AI', style: TextStyle(color: Colors.white, fontWeight: FontWeight.w900, fontSize: 11, letterSpacing: 2)),
              ]),
              GestureDetector(onTap: _toggle, child: const Icon(LucideIcons.x, color: AppColors.teal300, size: 18)),
            ]),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(4),
              decoration: BoxDecoration(color: AppColors.teal800.withOpacity(0.4), borderRadius: BorderRadius.circular(16)),
              child: Row(children: [
                _modeBtn('DESCUBRIR', 'recommend'),
                _modeBtn('ITINERARIO', 'plan'),
              ]),
            ),
          ]),
        ),

        Container(
          height: 280,
          color: AppColors.teal50.withOpacity(0.2),
          child: _isLoading
              ? const Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
            CircularProgressIndicator(color: AppColors.teal600, strokeWidth: 2),
            SizedBox(height: 12),
            Text('TRAZANDO RUTA...', style: TextStyle(fontSize: 9, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal600)),
          ]))
              : _result.isEmpty
              ? Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
            const _RobotIcon(size: 40),
            const SizedBox(height: 8),
            Text(_mode == 'recommend' ? '¿A dónde quieres ir hoy?' : 'Cuéntame y te haré un plan', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700, letterSpacing: 1, color: AppColors.teal300)),
          ]))
              : SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: _FormattedText(text: _result),
          ),
        ),

        Container(
          padding: const EdgeInsets.all(12),
          decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.vertical(bottom: Radius.circular(28)), border: Border(top: BorderSide(color: AppColors.teal100))),
          child: _mode == 'recommend' ? _recommendInputs() : _planInput(),
        ),
      ]),
    );
  }

  Widget _modeBtn(String label, String mode) => Expanded(
    child: GestureDetector(
      onTap: () => setState(() { _mode = mode; _result = ''; }),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 8),
        decoration: BoxDecoration(
          color: _mode == mode ? AppColors.teal500 : Colors.transparent,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Text(label, textAlign: TextAlign.center, style: TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: _mode == mode ? Colors.white : AppColors.teal300, letterSpacing: 1.5)),
      ),
    ),
  );

  Widget _recommendInputs() => Row(children: [
    Expanded(flex: 2, child: _chip(_prefCtrl, 'Preferencia (Playa)')),
    const SizedBox(width: 6),
    Expanded(child: _chip(_budgetCtrl, 'Presupuesto')),
    const SizedBox(width: 6),
    _sendBtn(disabled: _prefCtrl.text.isEmpty || _budgetCtrl.text.isEmpty),
  ]);

  Widget _planInput() => Row(children: [
    Expanded(child: _chip(_queryCtrl, 'Ej: 7 días en Madrid para 4 personas...')),
    const SizedBox(width: 6),
    _sendBtn(disabled: _queryCtrl.text.isEmpty),
  ]);

  Widget _chip(TextEditingController c, String hint) => Container(
    decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(14), border: Border.all(color: AppColors.teal100)),
    child: TextField(
      controller: c,
      style: const TextStyle(fontSize: 11, color: AppColors.teal900),
      decoration: InputDecoration(hintText: hint, hintStyle: const TextStyle(color: AppColors.teal300, fontSize: 11), border: InputBorder.none, contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10)),
      onChanged: (_) => setState(() {}),
    ),
  );

  Widget _sendBtn({required bool disabled}) => GestureDetector(
    onTap: disabled ? null : _submit,
    child: Container(
      width: 42, height: 42,
      decoration: BoxDecoration(color: disabled ? AppColors.teal200 : AppColors.teal600, borderRadius: BorderRadius.circular(14)),
      child: const Icon(LucideIcons.send, color: Colors.white, size: 18),
    ),
  );
}

// ── Texto formateado (equivalente a renderFormattedText) ─────────────────────
class _FormattedText extends StatelessWidget {
  final String text;
  const _FormattedText({required this.text});

  @override
  Widget build(BuildContext context) {
    final lines = text.split('\n');
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: lines.map((line) {
        if (line.startsWith('###')) {
          return Padding(
            padding: const EdgeInsets.only(top: 16, bottom: 6),
            child: Text(line.replaceFirst('###', '').trim(), style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900, color: AppColors.teal900)),
          );
        }
        if (line.trim().isEmpty) return const SizedBox(height: 8);
        return Padding(
          padding: const EdgeInsets.only(bottom: 6),
          child: _BoldText(text: line),
        );
      }).toList(),
    );
  }
}

class _BoldText extends StatelessWidget {
  final String text;
  const _BoldText({required this.text});

  @override
  Widget build(BuildContext context) {
    final regex = RegExp(r'\*\*([^*]+)\*\*');
    final spans = <InlineSpan>[];
    int last = 0;

    for (final match in regex.allMatches(text)) {
      if (match.start > last) {
        spans.add(TextSpan(text: text.substring(last, match.start)));
      }
      spans.add(WidgetSpan(
        alignment: PlaceholderAlignment.middle,
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 1),
          decoration: BoxDecoration(
            color: const Color(0x1A0D9488),
            borderRadius: BorderRadius.circular(4),
          ),
          child: Text(
            match.group(1) ?? '',
            style: const TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w900,
              color: AppColors.teal800,
              height: 1.5,
            ),
          ),
        ),
      ));
      last = match.end;
    }

    if (last < text.length) {
      spans.add(TextSpan(text: text.substring(last)));
    }

    return RichText(
      text: TextSpan(
        style: const TextStyle(fontSize: 12, color: Color(0xFF0F3D3A), height: 1.5),
        children: spans,
      ),
    );
  }
}

class _BoldTextParser extends StatelessWidget {
  final String text;
  const _BoldTextParser({required this.text});

  @override
  Widget build(BuildContext context) {
    final parts = text.split(RegExp(r'(\*\*[^*]+\*\*)'));

    return RichText(
      text: TextSpan(
        style: const TextStyle(fontSize: 13, color: Color(0xFF134E4A), height: 1.5),
        children: parts.map((part) {
          if (part.startsWith('**') && part.endsWith('**')) {
            return TextSpan(
              text: part.substring(2, part.length - 2),
              style: const TextStyle(
                  fontWeight: FontWeight.w900,
              ),
            );
          }
          return TextSpan(text: part);
        }).toList(),
      ),
    );
  }
}

// ── Icono robot personalizado (igual al original) ────────────────────────────
class _RobotIcon extends StatelessWidget {
  final double size;
  const _RobotIcon({this.size = 22});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size,
      height: size,
      child: CustomPaint(painter: _RobotPainter()),
    );
  }
}

class _RobotPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final w = size.width;
    final h = size.height;
    final paint = Paint()..color = Colors.white..style = PaintingStyle.fill;
    final stroke = Paint()..color = Colors.white..style = PaintingStyle.stroke..strokeWidth = w * 0.07..strokeCap = StrokeCap.round;

    canvas.drawLine(Offset(w * 0.5, h * 0.0), Offset(w * 0.5, h * 0.18), stroke);
    canvas.drawCircle(Offset(w * 0.5, h * 0.0), w * 0.07, paint);

    final head = RRect.fromRectAndRadius(
      Rect.fromLTWH(w * 0.1, h * 0.18, w * 0.8, h * 0.48),
      Radius.circular(w * 0.15),
    );
    canvas.drawRRect(head, stroke);

    canvas.drawCircle(Offset(w * 0.33, h * 0.39), w * 0.09, paint);
    canvas.drawCircle(Offset(w * 0.67, h * 0.39), w * 0.09, paint);

    final mouthPath = Path()
      ..moveTo(w * 0.3, h * 0.55)
      ..quadraticBezierTo(w * 0.5, h * 0.64, w * 0.7, h * 0.55);
    canvas.drawPath(mouthPath, stroke..strokeWidth = w * 0.06);

    final body = RRect.fromRectAndRadius(
      Rect.fromLTWH(w * 0.15, h * 0.7, w * 0.7, h * 0.28),
      Radius.circular(w * 0.1),
    );
    canvas.drawRRect(body, stroke..strokeWidth = w * 0.07);

    canvas.drawLine(Offset(w * 0.15, h * 0.76), Offset(w * 0.0, h * 0.84), stroke);
    canvas.drawLine(Offset(w * 0.85, h * 0.76), Offset(w * 1.0, h * 0.84), stroke);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}