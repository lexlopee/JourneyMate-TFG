import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../utils/date_utils.dart';
import '../../screens/search_screen.dart';

// Equivalente a SearchForm.tsx + SearchInput.tsx + SearchSelect.tsx + SearchCounter.tsx

class SearchFormWidget extends StatelessWidget {
  final Section section;
  final Map<String, dynamic> searchData;
  final void Function(String field, dynamic value) onChanged;

  const SearchFormWidget({
    super.key,
    required this.section,
    required this.searchData,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    switch (section) {
      case Section.vuelos:      return _VuelosForm(data: searchData, onChange: onChanged);
      case Section.alojamiento: return _AlojamientoForm(data: searchData, onChange: onChanged);
      case Section.coches:      return _CochesForm(data: searchData, onChange: onChanged);
      case Section.actividades: return _ActividadesForm(data: searchData, onChange: onChanged);
      case Section.cruceros:    return _CrucerosForm(data: searchData, onChange: onChanged);
      default:                  return const _ComingSoon();
    }
  }
}

// ══════════════════════════════════════════════════════════════════════════════
// VUELOS
// ══════════════════════════════════════════════════════════════════════════════
class _VuelosForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _VuelosForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Origen', icon: LucideIcons.planeTakeoff, hint: 'Ej: Madrid', value: data['originText'] ?? '', onChanged: (v) { onChange('originText', v); onChange('fromId', v); }),
    const SizedBox(height: 8),
    _TextSearchField(label: 'Destino', icon: LucideIcons.planeLanding, hint: 'Ej: Barcelona', value: data['destinationText'] ?? '', onChanged: (v) { onChange('destinationText', v); onChange('toId', v); }),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Salida', value: data['startDate'], onChanged: (v) => onChange('startDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _DateField(label: 'Regreso', value: data['endDate'], onChanged: (v) => onChange('endDate', v))),
    ]),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _CounterField(label: 'Pasajeros', icon: LucideIcons.users, value: data['adults'] ?? 1, min: 1, onChanged: (v) => onChange('adults', v))),
      const SizedBox(width: 8),
      Expanded(child: _SelectField(
        label: 'Clase', icon: LucideIcons.briefcase,
        value: data['cabinClass'] ?? 'ECONOMY',
        options: const [('Económica','ECONOMY'), ('Business','BUSINESS'), ('Primera Clase','FIRST')],
        onChanged: (v) => onChange('cabinClass', v),
      )),
    ]),
  ]);
}

// ══════════════════════════════════════════════════════════════════════════════
// ALOJAMIENTO
// ══════════════════════════════════════════════════════════════════════════════
class _AlojamientoForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _AlojamientoForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Destino', icon: LucideIcons.bedDouble, hint: '¿A dónde vas?', value: (data['destination'] ?? '').replaceAll('_', ' '), onChanged: (v) { onChange('destination', v.replaceAll(' ', '_')); onChange('destinationText', v); }),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Entrada', value: data['startDate'], onChanged: (v) => onChange('startDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _DateField(label: 'Salida', value: data['endDate'], onChanged: (v) => onChange('endDate', v))),
    ]),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _CounterField(label: 'Adultos', icon: LucideIcons.users, value: data['adults'] ?? 1, min: 1, onChanged: (v) => onChange('adults', v))),
      const SizedBox(width: 8),
      Expanded(child: _CounterField(label: 'Habitaciones', icon: LucideIcons.bedDouble, value: data['roomQty'] ?? 1, min: 1, onChanged: (v) => onChange('roomQty', v))),
    ]),
  ]);
}

// ══════════════════════════════════════════════════════════════════════════════
// COCHES
// ══════════════════════════════════════════════════════════════════════════════
class _CochesForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _CochesForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Lugar de recogida', icon: LucideIcons.car, hint: 'Ciudad o aeropuerto', value: data['originText'] ?? '', onChanged: (v) { onChange('originText', v); onChange('fromId', v); }),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Recogida', value: data['startDate'], onChanged: (v) => onChange('startDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _SelectField(
        label: 'Hora recogida', icon: LucideIcons.clock,
        value: data['pickupTime'] ?? '10:00',
        options: halfHourOptions.map((h) => (h, h)).toList(),
        onChanged: (v) => onChange('pickupTime', v),
      )),
    ]),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Devolución', value: data['endDate'], onChanged: (v) => onChange('endDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _SelectField(
        label: 'Hora devolución', icon: LucideIcons.clock,
        value: data['dropoffTime'] ?? '10:00',
        options: halfHourOptions.map((h) => (h, h)).toList(),
        onChanged: (v) => onChange('dropoffTime', v),
      )),
    ]),
    const SizedBox(height: 8),
    _SelectField(
      label: 'Tipo de coche', icon: LucideIcons.car,
      value: data['carType'] ?? 'all',
      options: const [('Todos','all'), ('Pequeño','small'), ('Mediano','medium'), ('Grande','large'), ('SUV','suvs'), ('Premium','premium'), ('Furgoneta','carriers')],
      onChanged: (v) => onChange('carType', v),
    ),
  ]);
}

// ══════════════════════════════════════════════════════════════════════════════
// ACTIVIDADES
// ══════════════════════════════════════════════════════════════════════════════
class _ActividadesForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _ActividadesForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Ciudad', icon: LucideIcons.mapPin, hint: 'Ej: Madrid, París, Roma...', value: data['destinationText'] ?? '', onChanged: (v) { onChange('destinationText', v); onChange('destination', v); }),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Fecha desde', value: data['startDate'], onChanged: (v) => onChange('startDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _DateField(label: 'Fecha hasta', value: data['endDate'], onChanged: (v) => onChange('endDate', v))),
    ]),
  ]);
}

// ══════════════════════════════════════════════════════════════════════════════
// CRUCEROS
// ══════════════════════════════════════════════════════════════════════════════
class _CrucerosForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _CrucerosForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Zona / Destino', icon: LucideIcons.globe, hint: 'Ej: Mediterráneo', value: data['cruiseDestination'] ?? '', onChanged: (v) { onChange('cruiseDestination', v); onChange('destination', v); onChange('destinationText', v); }),
    const SizedBox(height: 8),
    _TextSearchField(label: 'Puerto de salida', icon: LucideIcons.ship, hint: 'Ej: Barcelona', value: data['cruisePort'] ?? '', onChanged: (v) { onChange('cruisePort', v); onChange('origin', v); onChange('originText', v); }),
    const SizedBox(height: 8),
    Row(children: [
      Expanded(child: _DateField(label: 'Salida', value: data['startDate'], onChanged: (v) => onChange('startDate', v))),
      const SizedBox(width: 8),
      Expanded(child: _DateField(label: 'Regreso', value: data['endDate'], onChanged: (v) => onChange('endDate', v))),
    ]),
  ]);
}

class _ComingSoon extends StatelessWidget {
  const _ComingSoon();
  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.all(24),
    child: const Center(child: Text('PRÓXIMAMENTE', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 4, color: AppColors.teal600, fontSize: 12))),
  );
}

// ══════════════════════════════════════════════════════════════════════════════
// COMPONENTES REUTILIZABLES (SearchInput / SearchSelect / SearchCounter)
// ══════════════════════════════════════════════════════════════════════════════

// ── Campo de texto ─────────────────────────────────────────────────────────
class _TextSearchField extends StatelessWidget {
  final String label, hint, value;
  final IconData icon;
  final ValueChanged<String> onChanged;
  const _TextSearchField({required this.label, required this.icon, required this.hint, required this.value, required this.onChanged});

  @override
  Widget build(BuildContext context) => _fieldWrap(
    label: label,
    child: Row(children: [
      Icon(icon, size: 16, color: AppColors.teal500),
      const SizedBox(width: 8),
      Expanded(child: TextField(
        controller: TextEditingController(text: value)..selection = TextSelection.collapsed(offset: value.length),
        style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900),
        decoration: InputDecoration(hintText: hint, hintStyle: const TextStyle(color: AppColors.teal300, fontSize: 12), border: InputBorder.none, isDense: true, contentPadding: EdgeInsets.zero),
        onChanged: onChanged,
      )),
    ]),
  );
}

// ── Campo de fecha ─────────────────────────────────────────────────────────
class _DateField extends StatelessWidget {
  final String label;
  final String? value;
  final ValueChanged<String> onChanged;
  const _DateField({required this.label, required this.value, required this.onChanged});

  @override
  Widget build(BuildContext context) => GestureDetector(
    onTap: () async {
      final picked = await showDatePicker(
        context: context,
        initialDate: (value != null && value!.isNotEmpty) ? DateTime.tryParse(value!) ?? DateTime.now().add(const Duration(days: 1)) : DateTime.now().add(const Duration(days: 1)),
        firstDate: DateTime.now(),
        lastDate: DateTime.now().add(const Duration(days: 730)),
        builder: (ctx, child) => Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(primary: AppColors.teal600, onSurface: AppColors.teal900),
          ),
          child: child!,
        ),
      );
      if (picked != null) {
        final formatted = '${picked.year.toString().padLeft(4,'0')}-${picked.month.toString().padLeft(2,'0')}-${picked.day.toString().padLeft(2,'0')}';
        onChanged(formatted);
      }
    },
    child: _fieldWrap(
      label: label,
      child: Row(children: [
        const Icon(LucideIcons.calendar, size: 16, color: AppColors.teal500),
        const SizedBox(width: 8),
        Text(value != null && value!.isNotEmpty ? formatDateShort(value) : 'Seleccionar', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900)),
      ]),
    ),
  );
}

// ── Select ─────────────────────────────────────────────────────────────────
class _SelectField extends StatelessWidget {
  final String label, value;
  final IconData icon;
  final List<(String, String)> options; // (label, value)
  final ValueChanged<String> onChanged;
  const _SelectField({required this.label, required this.icon, required this.value, required this.options, required this.onChanged});

  @override
  Widget build(BuildContext context) => _fieldWrap(
    label: label,
    child: Row(children: [
      Icon(icon, size: 16, color: AppColors.teal500),
      const SizedBox(width: 8),
      Expanded(child: DropdownButtonHideUnderline(
        child: DropdownButton<String>(
          value: options.any((o) => o.$2 == value) ? value : options.first.$2,
          isDense: true,
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900),
          icon: const Icon(LucideIcons.chevronDown, size: 14, color: AppColors.teal400),
          items: options.map((o) => DropdownMenuItem(value: o.$2, child: Text(o.$1))).toList(),
          onChanged: (v) { if (v != null) onChanged(v); },
        ),
      )),
    ]),
  );
}

// ── Counter ────────────────────────────────────────────────────────────────
class _CounterField extends StatelessWidget {
  final String label;
  final IconData icon;
  final dynamic value;
  final int min;
  final ValueChanged<int> onChanged;
  const _CounterField({required this.label, required this.icon, required this.value, required this.min, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    final current = int.tryParse(value?.toString() ?? '') ?? min;
    return _fieldWrap(
      label: label,
      child: Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
        Row(children: [
          Icon(icon, size: 16, color: AppColors.teal500),
          const SizedBox(width: 8),
          Text('$current', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900)),
        ]),
        Row(children: [
          _counterBtn(LucideIcons.minus, () => onChanged((current - 1).clamp(min, 99))),
          const SizedBox(width: 4),
          _counterBtn(LucideIcons.plus, () => onChanged(current + 1)),
        ]),
      ]),
    );
  }

  Widget _counterBtn(IconData icon, VoidCallback onTap) => GestureDetector(
    onTap: onTap,
    child: Container(
      width: 24, height: 24,
      decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(8)),
      child: Icon(icon, size: 12, color: AppColors.teal600),
    ),
  );
}

// ── Wrapper común ──────────────────────────────────────────────────────────
Widget _fieldWrap({required String label, required Widget child}) => Container(
  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
  decoration: BoxDecoration(
    color: Colors.white.withOpacity(0.9),
    borderRadius: BorderRadius.circular(16),
    border: Border.all(color: AppColors.teal100.withOpacity(0.5)),
  ),
  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    Text(label.toUpperCase(), style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w900, letterSpacing: 2, color: AppColors.teal300)),
    const SizedBox(height: 6),
    child,
  ]),
);