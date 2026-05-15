import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:http/http.dart' as http;
import '../../core/app_colors.dart';

const _kBaseUrl = 'http://10.0.2.2:8080/api/v1';

final _flightCache = <String, List<FlightLocation>>{};
final _activityCache = <String, List<ActivityLocation>>{};
final _carCache = <String, List<CarLocation>>{};

Widget _fieldWrap({required String label, required Widget child}) => Container(
  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
  decoration: BoxDecoration(
    color: Colors.white.withOpacity(0.9),
    borderRadius: BorderRadius.circular(16),
    border: Border.all(color: AppColors.teal100.withOpacity(0.5)),
    boxShadow: [
      BoxShadow(
          color: Colors.black.withOpacity(0.04),
          blurRadius: 6,
          offset: const Offset(0, 2)
      )
    ],
  ),
  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
    Text(label.toUpperCase(),
        style: const TextStyle(
            fontSize: 8,
            fontWeight: FontWeight.w900,
            letterSpacing: 2,
            color: AppColors.teal300
        )),
    const SizedBox(height: 6),
    child,
  ]),
);

class FlightLocation {
  final String id, name, code, cityName, countryName, photoUri, type;
  const FlightLocation({
    required this.id, required this.name, required this.code,
    required this.cityName, required this.countryName,
    required this.photoUri, required this.type,
  });
  factory FlightLocation.fromJson(Map<String, dynamic> j) => FlightLocation(
    id: j['id']?.toString() ?? '',
    name: j['name'] ?? '',
    code: j['code'] ?? '',
    cityName: j['cityName'] ?? '',
    countryName: j['countryName'] ?? '',
    photoUri: j['photoUri'] ?? '',
    type: j['type'] ?? 'CITY',
  );
}

class AutocompleteInput extends StatefulWidget {
  final String label, placeholder, value;
  final IconData icon;
  final void Function(FlightLocation) onSelect;

  const AutocompleteInput({
    super.key,
    required this.label,
    required this.placeholder,
    required this.icon,
    required this.value,
    required this.onSelect
  });

  @override
  State<AutocompleteInput> createState() => _AutocompleteInputState();
}

class _AutocompleteInputState extends State<AutocompleteInput> {
  late final TextEditingController _ctrl;
  final _layerLink = LayerLink();
  OverlayEntry? _overlay;
  List<FlightLocation> _suggestions = [];
  bool _loading = false;
  Timer? _debounce;
  http.Client? _client;

  @override
  void initState() {
    super.initState();
    _ctrl = TextEditingController(text: widget.value);
  }

  @override
  void dispose() {
    _debounce?.cancel();
    _client?.close();
    _ctrl.dispose();
    _removeOverlay();
    super.dispose();
  }

  void _removeOverlay() { _overlay?.remove(); _overlay = null; }

  void _showOverlay() {
    _removeOverlay();
    if (_suggestions.isEmpty) return;
    final renderBox = context.findRenderObject() as RenderBox?;
    if (renderBox == null || !renderBox.hasSize) return;  // ← guard
    final size = renderBox.size;

    final screenWidth = MediaQuery.of(context).size.width;
    final overlayWidth = size.width > 0 ? size.width : screenWidth - 48;

    _overlay = OverlayEntry(builder: (_) => Positioned(
        width: overlayWidth,
        child: CompositedTransformFollower(
            link: _layerLink,
            showWhenUnlinked: false,
            offset: Offset(0, size.height + 4),
            child: _SuggestionDropdown<FlightLocation>(
              items: _suggestions,
              itemBuilder: (item) => _FlightSuggestionTile(item: item),
              onSelect: (item) {
                _ctrl.text = item.name;
                widget.onSelect(item);
                _removeOverlay();
                setState(() => _suggestions = []);
              },
            )
        )
    ));
    Overlay.of(context).insert(_overlay!);
  }

  Future<void> _search(String q) async {
    if (q.length < 3) {
      _removeOverlay();
      setState(() => _suggestions = []);
      return;
    }
    final key = q.toLowerCase();
    if (_flightCache.containsKey(key)) {
      setState(() => _suggestions = _flightCache[key]!);
      _showOverlay();
      return;
    }

    _debounce?.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () async {
      _client?.close();
      _client = http.Client();
      setState(() => _loading = true);
      try {
        final uri = Uri.parse('$_kBaseUrl/flights/location').replace(queryParameters: {'query': q});
        final res = await _client!.get(uri);
        if (res.statusCode == 200) {
          final results = (jsonDecode(res.body)['data'] as List)
              .map((e) => FlightLocation.fromJson(e)).toList();
          _flightCache[key] = results;
          if (mounted) { setState(() => _suggestions = results); _showOverlay(); }
        }
      } catch (_) {} finally { if (mounted) setState(() => _loading = false); }
    });
  }

  @override
  Widget build(BuildContext context) => CompositedTransformTarget(
      link: _layerLink,
      child: _fieldWrap(label: widget.label, child: Row(children: [
        _loading
            ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.teal500))
            : Icon(widget.icon, size: 16, color: AppColors.teal500),
        const SizedBox(width: 8),
        Expanded(child: TextField(
            controller: _ctrl,
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900),
            decoration: InputDecoration(hintText: widget.placeholder, border: InputBorder.none, isDense: true),
            onChanged: _search,
            onTap: () { if (_suggestions.isNotEmpty) _showOverlay(); }
        )),
      ]))
  );
}

class _FlightSuggestionTile extends StatelessWidget {
  final FlightLocation item;
  const _FlightSuggestionTile({required this.item});
  @override
  Widget build(BuildContext context) => Row(children: [
    ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: item.photoUri.isNotEmpty
            ? Image.network(item.photoUri, width: 40, height: 40, fit: BoxFit.cover, errorBuilder: (_,__,___)=>_fb())
            : _fb()
    ),
    const SizedBox(width: 10),
    Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text("${item.name} (${item.code})", style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: AppColors.teal900), overflow: TextOverflow.ellipsis),
      Text('${item.cityName}, ${item.countryName}', style: const TextStyle(fontSize: 8, fontWeight: FontWeight.w700, color: AppColors.teal600), overflow: TextOverflow.ellipsis),
    ])),
    Icon(item.type == 'AIRPORT' ? LucideIcons.plane : LucideIcons.mapPin, size: 12, color: AppColors.teal400),
  ]);
  Widget _fb() => Container(width: 40, height: 40, decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(8)), child: const Center(child: Text('✈️')));
}

class ActivityLocation {
  final String id, nombre, descripcion;
  const ActivityLocation({required this.id, required this.nombre, required this.descripcion});
}

class ActivityAutocomplete extends StatefulWidget {
  final String label, placeholder, value;
  final void Function(ActivityLocation) onSelect;
  const ActivityAutocomplete({super.key, required this.label, required this.placeholder, required this.value, required this.onSelect});

  @override
  State<ActivityAutocomplete> createState() => _ActivityAutocompleteState();
}

class _ActivityAutocompleteState extends State<ActivityAutocomplete> {
  late final TextEditingController _ctrl;
  final _layerLink = LayerLink();
  OverlayEntry? _overlay;
  List<ActivityLocation> _suggestions = [];
  bool _loading = false;
  Timer? _debounce;
  http.Client? _client;

  @override
  void initState() { super.initState(); _ctrl = TextEditingController(text: widget.value); }

  @override
  void dispose() { _debounce?.cancel(); _client?.close(); _ctrl.dispose(); _removeOverlay(); super.dispose(); }

  void _removeOverlay() { _overlay?.remove(); _overlay = null; }

  void _showOverlay() {
    _removeOverlay();
    if (_suggestions.isEmpty) return;
    final renderBox = context.findRenderObject() as RenderBox?;
    if (renderBox == null || !renderBox.hasSize) return;  // ← guard
    final size = renderBox.size;

    final screenWidth = MediaQuery.of(context).size.width;
    final overlayWidth = size.width > 0 ? size.width : screenWidth - 48;

    _overlay = OverlayEntry(builder: (_) => Positioned(
        width: overlayWidth,
        child: CompositedTransformFollower(
            link: _layerLink,
            showWhenUnlinked: false,
            offset: Offset(0, size.height + 4),
            child: _SuggestionDropdown<ActivityLocation>(
              items: _suggestions,
              itemBuilder: (item) => _ActivitySuggestionTile(item: item),
              onSelect: (item) {
                _ctrl.text = item.nombre;
                widget.onSelect(item);
                _removeOverlay();
                setState(() => _suggestions = []);
              },
            )
        )
    ));
    Overlay.of(context).insert(_overlay!);
  }

  Future<void> _search(String q) async {
    if (q.length < 3) { _removeOverlay(); setState(() => _suggestions = []); return; }
    final key = q.toLowerCase();
    if (_activityCache.containsKey(key)) { setState(() => _suggestions = _activityCache[key]!); _showOverlay(); return; }

    _debounce?.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () async {
      _client?.close(); _client = http.Client();
      setState(() => _loading = true);
      try {
        final uri = Uri.parse('$_kBaseUrl/activities/location').replace(queryParameters: {'query': q});
        final res = await _client!.get(uri);
        if (res.statusCode == 200) {
          final body = jsonDecode(res.body);
          final dests = (body['destinations'] as List? ?? []);
          final prods = (body['products'] as List? ?? []);
          final results = [
            ...dests.map((d) => ActivityLocation(
              id: (d['id'] ?? d['cityUfi'] ?? '').toString(),
              nombre: d['cityName'] ?? d['name'] ?? '',
              descripcion: d['display_name'] ?? d['address']?['country'] ?? 'Ciudad',
            )),
            ...prods.map((p) => ActivityLocation(
              id: (p['id'] ?? p['cityUfi'] ?? '').toString(),
              nombre: p['name'] ?? '',
              descripcion: 'Actividad específica',
            )),
          ];
          _activityCache[key] = results;
          if (mounted) { setState(() => _suggestions = results); _showOverlay(); }
        }
      } catch (_) {} finally { if (mounted) setState(() => _loading = false); }
    });
  }

  @override
  Widget build(BuildContext context) => CompositedTransformTarget(
      link: _layerLink,
      child: _fieldWrap(label: widget.label, child: Row(children: [
        _loading
            ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2, color: Color(0xFFEA580C)))
            : const Icon(LucideIcons.search, size: 16, color: Color(0xFFEA580C)),
        const SizedBox(width: 8),
        Expanded(child: TextField(
            controller: _ctrl,
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700),
            decoration: InputDecoration(hintText: widget.placeholder, border: InputBorder.none, isDense: true),
            onChanged: _search,
            onTap: () { if (_suggestions.isNotEmpty) _showOverlay(); }
        )),
      ]))
  );
}

class _ActivitySuggestionTile extends StatelessWidget {
  final ActivityLocation item;
  const _ActivitySuggestionTile({required this.item});
  @override
  Widget build(BuildContext context) => Row(children: [
    Container(width: 32, height: 32, decoration: const BoxDecoration(color: Color(0xFFFED7AA), shape: BoxShape.circle), child: const Icon(LucideIcons.mapPin, size: 16, color: Color(0xFFEA580C))),
    const SizedBox(width: 10),
    Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(item.nombre, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700), overflow: TextOverflow.ellipsis),
      Text(item.descripcion, style: const TextStyle(fontSize: 9, color: Color(0xFF6B7280)), overflow: TextOverflow.ellipsis),
    ])),
  ]);
}

class CarLocation {
  final String id; // ID técnico necesario para la API
  final String name, city, country, type;

  const CarLocation({
    required this.id,
    required this.name,
    required this.city,
    required this.country,
    required this.type
  });

  factory CarLocation.fromJson(Map<String, dynamic> j) => CarLocation(
      id: j['id']?.toString() ?? '',
      name: j['name'] ?? '',
      city: j['city'] ?? '',
      country: j['country'] ?? '',
      type: j['type'] ?? ''
  );
}

class CarLocationInput extends StatefulWidget {
  final String label, placeholder, value;
  final void Function(CarLocation) onSelect;
  const CarLocationInput({super.key, required this.label, required this.placeholder, required this.value, required this.onSelect});

  @override
  State<CarLocationInput> createState() => _CarLocationInputState();
}

class _CarLocationInputState extends State<CarLocationInput> {
  late final TextEditingController _ctrl;
  final _layerLink = LayerLink();
  OverlayEntry? _overlay;
  List<CarLocation> _suggestions = [];
  bool _loading = false;
  Timer? _debounce;
  http.Client? _client;

  @override
  void initState() {
    super.initState();
    _ctrl = TextEditingController(text: widget.value);
  }

  @override
  void didUpdateWidget(covariant CarLocationInput old) {
    super.didUpdateWidget(old);
    if (old.value != widget.value) {
      _ctrl.text = widget.value;
      _ctrl.selection = TextSelection.collapsed(offset: widget.value.length);
    }
  }

  @override
  void dispose() {
    _debounce?.cancel();
    _client?.close();
    _ctrl.dispose();
    _removeOverlay();
    super.dispose();
  }

  void _removeOverlay() { _overlay?.remove(); _overlay = null; }

  void _showOverlay() {
    _removeOverlay();
    if (_suggestions.isEmpty) return;
    final renderBox = context.findRenderObject() as RenderBox?;
    if (renderBox == null || !renderBox.hasSize) return;
    final size = renderBox.size;

    final screenWidth = MediaQuery.of(context).size.width;
    final overlayWidth = size.width > 0 ? size.width : screenWidth - 48;

    _overlay = OverlayEntry(builder: (_) => Positioned(
        width: overlayWidth,
        child: CompositedTransformFollower(
            link: _layerLink,
            showWhenUnlinked: false,
            offset: Offset(0, size.height + 4),
            child: _SuggestionDropdown<CarLocation>(
              items: _suggestions,
              itemBuilder: (item) => _CarSuggestionTile(item: item),
              onSelect: (item) {
                _ctrl.text = item.name; // Visual
                widget.onSelect(item);  // Pasa el objeto con ID
                _removeOverlay();
                setState(() => _suggestions = []);
              },
            )
        )
    ));
    Overlay.of(context).insert(_overlay!);
  }

  Future<void> _search(String text) async {
    if (text.length < 3) { _removeOverlay(); setState(() => _suggestions = []); return; }

    final key = text.toLowerCase();
    if (_carCache.containsKey(key)) {
      setState(() => _suggestions = _carCache[key]!);
      _showOverlay();
      return;
    }

    _debounce?.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () async {
      _client?.close();
      _client = http.Client();
      setState(() => _loading = true);

      try {
        final uri = Uri.parse('$_kBaseUrl/external/cars/autocomplete')
            .replace(queryParameters: {'query': text});
        final res = await _client!.get(uri);
        if (res.statusCode == 200) {
          final results = (jsonDecode(res.body) as List)
              .map((e) => CarLocation.fromJson(e)).toList();
          _carCache[key] = results;
          if (mounted) { setState(() => _suggestions = results); _showOverlay(); }
        }
      } catch (_) {} finally { if (mounted) setState(() => _loading = false); }
    });
  }

  @override
  Widget build(BuildContext context) => CompositedTransformTarget(
      link: _layerLink,
      child: _fieldWrap(label: widget.label, child: Row(children: [
        _loading
            ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.teal500))
            : const Icon(LucideIcons.car, size: 16, color: AppColors.teal500),
        const SizedBox(width: 8),
        Expanded(child: TextField(
            controller: _ctrl,
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: AppColors.teal900),
            decoration: InputDecoration(hintText: widget.placeholder, border: InputBorder.none, isDense: true),
            onChanged: _search,
            onTap: () { if (_suggestions.isNotEmpty) _showOverlay(); }
        )),
      ]))
  );
}

class _CarSuggestionTile extends StatelessWidget {
  final CarLocation item;
  const _CarSuggestionTile({required this.item});
  @override
  Widget build(BuildContext context) => Row(children: [
    Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(color: AppColors.teal50, borderRadius: BorderRadius.circular(8)),
        child: const Icon(LucideIcons.mapPin, size: 14, color: AppColors.teal600)
    ),
    const SizedBox(width: 10),
    Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      Text(item.name, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w900, color: AppColors.teal900), overflow: TextOverflow.ellipsis),
      Text('${item.city}, ${item.country} (${item.type})', style: const TextStyle(fontSize: 9, fontWeight: FontWeight.w700, color: AppColors.teal600), overflow: TextOverflow.ellipsis),
    ])),
  ]);
}

class _CodeName { final String code, name; const _CodeName(this.code, this.name); }

class CruiseSearchSelects extends StatefulWidget {
  final String destinationValue, portValue;
  final void Function(String code, String label) onDestinationChange, onPortChange;
  const CruiseSearchSelects({super.key, required this.destinationValue, required this.portValue, required this.onDestinationChange, required this.onPortChange});

  @override
  State<CruiseSearchSelects> createState() => _CruiseSearchSelectsState();
}

class _CruiseSearchSelectsState extends State<CruiseSearchSelects> {
  List<_CodeName> _destinations = [], _ports = [];
  bool _loadingDest = false, _loadingPort = false;

  @override
  void initState() { super.initState(); _loadDestinations(); _loadPorts(); }

  Future<void> _loadDestinations() async {
    setState(() => _loadingDest = true);
    try {
      final res = await http.get(Uri.parse('$_kBaseUrl/cruises/destinations'));
      if (res.statusCode == 200) {
        final body = jsonDecode(res.body);
        final raw = (body is List ? body : (body['destinations'] ?? body['data'] ?? [])) as List;
        if (mounted) setState(() => _destinations = raw.map((d) => _CodeName(d['code'] ?? d['id'] ?? '', d['name'] ?? d['code'] ?? '')).toList());
      }
    } catch (_) {} finally { if (mounted) setState(() => _loadingDest = false); }
  }

  Future<void> _loadPorts() async {
    setState(() => _loadingPort = true);
    try {
      final res = await http.get(Uri.parse('$_kBaseUrl/cruises/ports'));
      if (res.statusCode == 200) {
        final body = jsonDecode(res.body);
        final raw = (body is List ? body : (body['ports'] ?? body['data'] ?? [])) as List;
        if (mounted) setState(() => _ports = raw.map((p) => _CodeName(p['code'] ?? p['id'] ?? '', p['name'] ?? p['code'] ?? '')).toList());
      }
    } catch (_) {} finally { if (mounted) setState(() => _loadingPort = false); }
  }

  @override
  Widget build(BuildContext context) => Column(children: [
    _fieldWrap(label: 'Zona / Destino', child: Row(children: [
      const Icon(LucideIcons.globe, size: 16, color: AppColors.teal500),
      const SizedBox(width: 8),
      Expanded(child: _loadingDest
          ? const Text('Cargando...', style: TextStyle(fontSize: 12))
          : DropdownButtonHideUnderline(child: DropdownButton<String>(
          value: _destinations.any((d) => d.code == widget.destinationValue) ? widget.destinationValue : null,
          isDense: true,
          items: _destinations.map((d) => DropdownMenuItem(value: d.code, child: Text(d.name))).toList(),
          onChanged: (v) => widget.onDestinationChange(v!, _destinations.firstWhere((d)=>d.code==v).name)
      ))),
    ])),
    const SizedBox(height: 8),
    _fieldWrap(label: 'Puerto de salida', child: Row(children: [
      const Icon(LucideIcons.ship, size: 16, color: AppColors.teal500),
      const SizedBox(width: 8),
      Expanded(child: _loadingPort
          ? const Text('Cargando...', style: TextStyle(fontSize: 12))
          : DropdownButtonHideUnderline(child: DropdownButton<String>(
          value: _ports.any((p) => p.code == widget.portValue) ? widget.portValue : null,
          isDense: true,
          items: _ports.map((p) => DropdownMenuItem(value: p.code, child: Text(p.name))).toList(),
          onChanged: (v) => widget.onPortChange(v!, _ports.firstWhere((p)=>p.code==v).name)
      ))),
    ])),
  ]);
}

class _SuggestionDropdown<T> extends StatelessWidget {
  final List<T> items;
  final Widget Function(T) itemBuilder;
  final void Function(T) onSelect;
  const _SuggestionDropdown({required this.items, required this.itemBuilder, required this.onSelect});

  @override
  Widget build(BuildContext context) => Material(
      elevation: 0,
      borderRadius: BorderRadius.circular(16),
      child: Container(
        constraints: const BoxConstraints(maxHeight: 288),
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: const Color(0xFFF0FDFA)),
            boxShadow: [
              BoxShadow(
                  color: Colors.black.withOpacity(0.15),
                  blurRadius: 30,
                  offset: const Offset(0, 10)
              )
            ]
        ),
        child: ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: ListView.builder(
                padding: const EdgeInsets.all(8),
                shrinkWrap: true,
                itemCount: items.length,
                itemBuilder: (_, i) => InkWell(
                    borderRadius: BorderRadius.circular(12),
                    onTap: () => onSelect(items[i]),
                    child: Container(
                        padding: const EdgeInsets.all(10),
                        child: itemBuilder(items[i])
                    )
                )
            )
        ),
      )
  );
}