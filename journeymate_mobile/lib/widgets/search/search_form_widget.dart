import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../../core/app_colors.dart';
import '../../utils/date_utils.dart';
import '../../screens/search_section.dart';
import '../../services/api_service.dart';


class SearchFormWidget extends StatelessWidget {
  final Section section;
  final Map<String, dynamic> searchData;
  final void Function(String field, dynamic value) onChanged;
  final int navVersion;

  const SearchFormWidget({
    super.key,
    required this.section,
    required this.searchData,
    required this.onChanged,
    this.navVersion = 0,
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

class _VuelosForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _VuelosForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Origen', icon: LucideIcons.plane, hint: 'Ej: Madrid', value: data['originText'] ?? '', onChanged: (v) { onChange('originText', v); onChange('fromId', v); }),
    const SizedBox(height: 8),
    _TextSearchField(label: 'Destino', icon: LucideIcons.plane, hint: 'Ej: Barcelona', value: data['destinationText'] ?? '', onChanged: (v) { onChange('destinationText', v); onChange('toId', v); }),
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

class _AlojamientoForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _AlojamientoForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _TextSearchField(label: 'Destino', icon: LucideIcons.home, hint: '¿A dónde vas?', value: (data['destination'] ?? '').replaceAll('_', ' '), onChanged: (v) { onChange('destination', v.replaceAll(' ', '_')); onChange('destinationText', v); }),
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
      Expanded(child: _CounterField(label: 'Habitaciones', icon: LucideIcons.home, value: data['roomQty'] ?? 1, min: 1, onChanged: (v) => onChange('roomQty', v))),
    ]),
  ]);
}

// Widget con autocomplete real — llama a /external/cars/autocomplete
class _CarLocationField extends StatefulWidget {
  final String value;
  final void Function(String id, String name) onSelect;
  const _CarLocationField({required this.value, required this.onSelect});
  @override
  State<_CarLocationField> createState() => _CarLocationFieldState();
}

class _CarLocationFieldState extends State<_CarLocationField> {
  final _ctrl = TextEditingController();
  final _focus = FocusNode();
  List<Map<String, dynamic>> _suggestions = [];
  bool _loading = false;
  bool _showSuggestions = false;

  @override
  void initState() {
    super.initState();
    _ctrl.text = widget.value;
    _focus.addListener(() {
      if (!_focus.hasFocus) {
        Future.delayed(const Duration(milliseconds: 200), () {
          if (mounted) setState(() => _showSuggestions = false);
        });
      }
    });
  }

  @override
  void dispose() { _ctrl.dispose(); _focus.dispose(); super.dispose(); }

  Future<void> _search(String query) async {
    if (query.length < 2) { setState(() { _suggestions = []; _showSuggestions = false; }); return; }
    setState(() => _loading = true);
    try {
      final res = await api.get('/external/cars/autocomplete', params: {'query': query});
      final list = (res as List? ?? []).map((e) => e as Map<String, dynamic>).toList();
      if (mounted) setState(() { _suggestions = list; _showSuggestions = list.isNotEmpty; });
    } catch (_) {
      if (mounted) setState(() => _suggestions = []);
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    _fieldWrap(
      label: 'Lugar de recogida',
      child: Row(children: [
        _loading
            ? const SizedBox(width: 16, height: 16,
            child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.teal500))
            : const Icon(LucideIcons.car, size: 16, color: AppColors.teal500),
        const SizedBox(width: 8),
        Expanded(child: TextField(
          controller: _ctrl,
          focusNode: _focus,
          style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900),
          decoration: const InputDecoration(
            hintText: 'Ciudad o aeropuerto',
            hintStyle: TextStyle(color: AppColors.teal300, fontSize: 12),
            border: InputBorder.none, isDense: true, contentPadding: EdgeInsets.zero,
          ),
          onChanged: _search,
        )),
        if (_ctrl.text.isNotEmpty)
          GestureDetector(
            onTap: () { _ctrl.clear(); _suggestions = []; setState(() => _showSuggestions = false); widget.onSelect('', ''); },
            child: const Icon(LucideIcons.x, size: 14, color: AppColors.teal300),
          ),
      ]),
    ),
    if (_showSuggestions && _suggestions.isNotEmpty)
      Container(
        margin: const EdgeInsets.only(top: 4),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.12), blurRadius: 16, offset: const Offset(0, 4))],
        ),
        child: Column(children: _suggestions.take(6).map((loc) {
          final name = loc['name']?.toString() ?? '';
          final city = loc['city']?.toString() ?? '';
          final country = loc['country']?.toString() ?? '';
          final id = loc['id']?.toString() ?? '';
          return GestureDetector(
            onTap: () {
              _ctrl.text = name;
              widget.onSelect(id, name);
              setState(() => _showSuggestions = false);
              _focus.unfocus();
            },
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
              decoration: BoxDecoration(
                border: Border(bottom: BorderSide(color: AppColors.teal100.withOpacity(0.5))),
              ),
              child: Row(children: [
                Container(
                  width: 32, height: 32,
                  decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(8)),
                  child: const Icon(LucideIcons.mapPin, size: 14, color: AppColors.teal600),
                ),
                const SizedBox(width: 10),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(name, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal900)),
                  if (city.isNotEmpty || country.isNotEmpty)
                    Text('$city${city.isNotEmpty && country.isNotEmpty ? ", " : ""}$country',
                        style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.w600)),
                ])),
              ]),
            ),
          );
        }).toList()),
      ),
  ]);
}

class _CochesForm extends StatelessWidget {
  final Map<String, dynamic> data;
  final void Function(String, dynamic) onChange;
  const _CochesForm({required this.data, required this.onChange});

  @override
  Widget build(BuildContext context) => Column(children: [
    _CarLocationField(
      value: data['originText'] ?? '',
      onSelect: (id, name) {
        onChange('fromId', id);
        onChange('toId', id);
        onChange('originText', name);
      },
    ),
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