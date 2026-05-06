import 'package:flutter/foundation.dart';
import 'api_service.dart';

// ── EQUIVALENTE A paramsMapper.ts ────────────────────────────────────────────
class ParamsMapper {
  static String _normalize(String? text) {
    if (text == null || text.isEmpty) return "";
    return text.trim().replaceAll(RegExp(r'\s+'), "_");
  }

  static Map<String, dynamic> alojamiento(Map<String, dynamic> data, String destId) => {
    'destId': destId,
    'searchType': 'CITY',
    'checkinDate': data['startDate'],
    'checkoutDate': data['endDate'],
    'adults': data['adults'] ?? 2,
    'roomQty': data['roomQty'] ?? 1,
    'currencyCode': 'EUR',
    'pageNo': 1,
  };

  static Map<String, dynamic> hotelDetails(String hotelId, Map<String, dynamic> data) => {
    'hotelId': hotelId,
    'arrivalDate': data['startDate'],
    'departureDate': data['endDate'],
    'adults': data['adults'] ?? 1,
    'roomQty': data['roomQty'] ?? 1,
    if (data['childrenAge'] != null) 'childrenAge': data['childrenAge'],
    'currencyCode': 'EUR',
  };

  static Map<String, dynamic> vuelos(Map<String, dynamic> data) {
    final params = <String, dynamic>{
      'fromId': data['fromId'] ?? '',  // Token base64 completo del autocomplete
      'toId': data['toId'] ?? '',      // Token base64 completo del autocomplete
      'departDate': data['startDate'],
      'adults': data['adults'] ?? 1,
      'childrenAge': data['childrenAge'],
      'cabinClass': data['cabinClass'] ?? 'ECONOMY',
      'currencyCode': data['currencyCode'] ?? 'EUR',
      'sort': data['sort'] ?? 'BEST',
      'pageNo': 1,
    };

    final String? end = data['endDate'];
    if (end != null && end.trim().isNotEmpty && end != data['startDate']) {
      params['returnDate'] = end;
    }
    return params;
  }

  static Map<String, dynamic> coches(Map<String, dynamic> data) {
    // Aquí data['fromId'] debe ser el token del autocomplete (ej: eyJsYXR...)
    final params = <String, dynamic>{
      'pickUpId': data['fromId'],
      'dropOffId': data['toId'] ?? data['fromId'],
      'pickUpDate': data['startDate'],
      'pickUpTime': data['pickupTime'] ?? '10:00',
      'dropOffDate': data['endDate'],
      'dropOffTime': data['dropoffTime'] ?? '10:00',
      'driverAge': 30,
      'currencyCode': 'EUR',
      'units': 'metric',
    };
    if (data['carType'] != null && data['carType'] != 'all') {
      params['carType'] = 'carCategory::${data['carType']}';
    }
    return params;
  }

  // En ParamsMapper (source 3)
  static Map<String, dynamic> actividades(Map<String, dynamic> data, String ufi) {
    String sortValue = data['sort'] ?? 'trending';
    if (sortValue == 'BEST') sortValue = 'trending';

    return {
      'id': ufi,
      'startDate': data['startDate'],
      'endDate': data['endDate'],
      'sortBy': sortValue,
      'page': 1,
      'currencyCode': 'EUR',
    };
  }

  static Map<String, dynamic> cruceros(Map<String, dynamic> data) => {
    'startDate': data['startDate'],
    'endDate': data['endDate'],
    'destination': _normalize(data['destination']),
    'departurePort': _normalize(data['origin']),
    'currency': 'EUR',
  };
}

// ── EQUIVALENTE A searchService.ts ───────────────────────────────────────────
class SearchService {
  static Map<String, dynamic> _clean(Map<String, dynamic> p) =>
      Map.fromEntries(p.entries.where((e) => e.value != null && e.value.toString().isNotEmpty));

  static Future<List<dynamic>> performSearch(String section, Map<String, dynamic> searchData) async {
    final query = (searchData['destinationText'] ?? searchData['destination'] ?? '') as String;

    switch (section) {
      case 'alojamiento': {
        final destData = await api.get('/hotels/destination', params: {'name': query});
        final destId = destData is String ? destData : destData.toString();
        final params = _clean(ParamsMapper.alojamiento(searchData, destId));
        final res = await api.get('/hotels/search', params: params);
        return _toList(res);
      }

      case 'vuelos': {
        final params = _clean(ParamsMapper.vuelos(searchData));
        final res = await api.get('/flights/search', params: params);
        // El backend Java devuelve un array plano de FlightDTO directamente
        return _toList(res);
      }

      case 'coches': {
        final params = _clean(ParamsMapper.coches(searchData));
        final res = await api.get('/external/cars/search', params: params);
        var cars = _toList(res);

        // Lógica de filtrado de coches por categoría
        const carTypeMap = <String, List<String>>{
          'small':    ['mini', 'economy', 'small', 'compact'],
          'medium':   ['medium', 'intermediate', 'standard'],
          'large':    ['large', 'full-size', 'fullsize'],
          'suvs':     ['suv', 'crossover', '4x4'],
          'premium':  ['premium', 'luxury', 'sport', 'convertible'],
          'carriers': ['van', 'minivan', 'people carrier'],
        };

        final carType = searchData['carType'] as String? ?? 'all';
        if (carType != 'all' && carTypeMap.containsKey(carType)) {
          final kws = carTypeMap[carType]!;
          final filtered = cars.where((c) {
            final name = (c['carName'] ?? '').toString().toLowerCase();
            return kws.any((kw) => name.contains(kw));
          }).toList();
          if (filtered.isNotEmpty) cars = filtered;
        }
        return cars.take(20).toList();
      }

      case 'actividades': {
        // Si el autocomplete ya nos dio el UFI directamente, usarlo sin llamada extra
        String? ufi = (searchData['activityUfi'] as String?)?.isNotEmpty == true
            ? searchData['activityUfi'] as String
            : null;

        if (ufi == null) {
          // Fallback: resolver por nombre (cuando se escribe manualmente sin autocompletar)
          if (query.isEmpty) return [];
          final locData = await api.get('/activities/location', params: {'query': query});
          if (locData is Map) {
            final dests = (locData['destinations'] as List? ?? []);
            final prods = (locData['products']     as List? ?? []);
            if (dests.isNotEmpty) {
              ufi = (dests[0]['id'] ?? dests[0]['cityUfi'] ?? '').toString();
            } else if (prods.isNotEmpty) {
              ufi = (prods[0]['id'] ?? prods[0]['cityUfi'] ?? '').toString();
            }
          }
        }

        if (ufi == null || ufi.isEmpty) {
          debugPrint('⚠️ Actividades: no se encontró UFI para query="$query"');
          return [];
        }

        debugPrint('▶ Actividades UFI=$ufi');
        final params = _clean(ParamsMapper.actividades(searchData, ufi));
        final res = await api.get('/activities/search', params: params);
        return _toList(res);
      }

      case 'cruceros': {
        final params = _clean(ParamsMapper.cruceros(searchData));
        final res = await api.get('/cruises/search', params: params);
        return _toList(res);
      }

      default:
        return [];
    }
  }

  // --- Detalles, IA e Itinerarios ---
  static Future<dynamic> getHotelDetails(String hotelId, Map<String, dynamic> searchData) async {
    final params = _clean({...ParamsMapper.hotelDetails(hotelId, searchData), '_t': DateTime.now().millisecondsSinceEpoch});
    final cleanParams = Map.fromEntries(
        params.entries.where((e) => e.value != null)
    );

    return await api.get('/hotels/details', params: cleanParams);
  }

  static Future<dynamic> getFlightDetails(String token, {String currency = 'EUR'}) async {
    return api.get('/flights/details', params: {'token': token, 'currencyCode': currency, '_t': DateTime.now().millisecondsSinceEpoch});
  }

  static Future<dynamic> getActivityDetails(String slug) async {
    return api.get('/activities/details', params: {'slug': slug, 'currencyCode': 'EUR', '_t': DateTime.now().millisecondsSinceEpoch});
  }

  static Future<String> askAI(String pref, String budget) async {
    try {
      final res = await api.get('/ai/recommend', params: {'pref': pref, 'budget': budget});
      return res?.toString() ?? 'Sin respuesta';
    } catch (_) { return 'La IA está descansando.'; }
  }

  static Future<String> getItinerary(String query) async {
    try {
      final res = await api.get('/ai/itinerary', params: {'query': query});
      return res?.toString() ?? 'Sin respuesta';
    } catch (_) { return 'Se me ha perdido el mapa.'; }
  }

  static List<dynamic> _toList(dynamic data) {
    if (data == null) return [];
    if (data is List) return data;
    if (data is Map) {
      final possible = data['result'] ?? data['data'] ?? data['results'];
      if (possible is List) return possible;
    }
    return [];
  }
}