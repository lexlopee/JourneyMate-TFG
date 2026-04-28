import 'package:intl/intl.dart';

// ── Fechas ────────────────────────────────────────────────────────────────────

/// Formatea una fecha ISO (YYYY-MM-DD) a "lun, 3 ene 2025"
String formatDateSpanish(String? isoDate) {
  if (isoDate == null || isoDate.isEmpty) return '—';
  try {
    final d = DateTime.parse(isoDate);
    return DateFormat('EEE, d MMM yyyy', 'es').format(d);
  } catch (_) {
    return isoDate;
  }
}

/// Formatea "2025-01-03" → "03/01/2025"
String formatDateShort(String? isoDate) {
  if (isoDate == null || isoDate.isEmpty) return '—';
  try {
    final d = DateTime.parse(isoDate);
    return DateFormat('dd/MM/yyyy').format(d);
  } catch (_) {
    return isoDate;
  }
}

/// Calcula días entre dos fechas ISO (mínimo 1)
int daysBetween(String? start, String? end) {
  if (start == null || end == null) return 1;
  try {
    final s = DateTime.parse(start);
    final e = DateTime.parse(end);
    return e.difference(s).inDays.clamp(1, 9999);
  } catch (_) {
    return 1;
  }
}

/// Devuelve today + N días en formato YYYY-MM-DD
String addDays(int n) {
  final d = DateTime.now().add(Duration(days: n));
  return DateFormat('yyyy-MM-dd').format(d);
}

/// Hoy en formato YYYY-MM-DD
String get today => DateFormat('yyyy-MM-dd').format(DateTime.now());

// ── Horas ─────────────────────────────────────────────────────────────────────

/// Formatea un ISO datetime a "HH:mm"
String formatTime(String? isoDateTime) {
  if (isoDateTime == null || isoDateTime.isEmpty) return '--:--';
  try {
    final d = DateTime.parse(isoDateTime);
    return DateFormat('HH:mm').format(d);
  } catch (_) {
    // Si ya viene como "HH:mm", lo devolvemos tal cual
    if (RegExp(r'^\d{2}:\d{2}').hasMatch(isoDateTime)) return isoDateTime.substring(0, 5);
    return isoDateTime;
  }
}

/// Genera lista de horas cada 30 min: ["00:00", "00:30", ...]
List<String> get halfHourOptions {
  return List.generate(48, (i) {
    final h = (i ~/ 2).toString().padLeft(2, '0');
    final m = (i % 2 == 0) ? '00' : '30';
    return '$h:$m';
  });
}

// ── Divisas ───────────────────────────────────────────────────────────────────

/// Formatea un número como moneda (€ / $ etc.)
String formatCurrency(dynamic value, [String currency = 'EUR']) {
  if (value == null) return '—';
  final amount = double.tryParse(value.toString()) ?? 0.0;
  final format = NumberFormat.currency(
    locale: 'es_ES',
    symbol: _currencySymbol(currency),
    decimalDigits: 2,
  );
  return format.format(amount);
}

String _currencySymbol(String code) {
  const map = {'EUR': '€', 'USD': '\$', 'GBP': '£', 'JPY': '¥'};
  return map[code.toUpperCase()] ?? code;
}