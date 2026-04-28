import 'api_service.dart';

// ── Equivalente a paramsMapper.ts ────────────────────────────────────────────
class ParamsMapper {
  static Map<String, dynamic> alojamiento(Map<String, dynamic> data, String destId) => {
    'destId': destId,
    'searchType': 'CITY',
    'checkinDate': data['startDate'],
    'checkoutDate': data['endDate'],
    'adults': (data['adults'] ?? 2).toString(),
    'roomQty': (data['roomQty'] ?? 1).toString(),
    'currencyCode': 'EUR',
    'pageNo': '1',
  };

  static Map<String, dynamic> hotelDetails(String hotelId, Map<String, dynamic> data) => {
    'hotelId': hotelId,
    'arrivalDate': data['startDate'],
    'departureDate': data['endDate'],
    'adults': (data['adults'] ?? 1).toString(),
    'roomQty': (data['roomQty'] ?? 1).toString(),
    if (data['childrenAge'] != null) 'childrenAge': data['childrenAge'].toString(),
    'currencyCode': 'EUR',
  };

  static Map<String, dynamic> vuelos(Map<String, dynamic> data) {
    final params = <String, dynamic>{
      'fromId': data['fromId'],
      'toId': data['toId'],
      'departDate': data['startDate'],
      'adults': (data['adults'] ?? 1).toString(),
      'cabinClass': data['cabinClass'] ?? 'ECONOMY',
      'currencyCode': data['currencyCode'] ?? 'EUR',
      'sort': data['sort'] ?? 'BEST',
      'pageNo': '1',
    };
    final end = data['endDate'] as String?;
    if (end != null && end.isNotEmpty && end != data['startDate']) {
      params['returnDate'] = end;
    }
    return params;
  }

  static Map<String, dynamic> coches(Map<String, dynamic> data) {
    final params = <String, dynamic>{
      'pickUpId': data['fromId'],
      'dropOffId': data['toId'] ?? data['fromId'],
      'pickUpDate': data['startDate'],
      'pickUpTime': data['pickupTime'] ?? '10:00',
      'dropOffDate': data['endDate'],
      'dropOffTime': data['dropoffTime'] ?? '10:00',
      'driverAge': '30',
      'currencyCode': 'EUR',
      'units': 'metric',
    };
    final carType = data['carType'] as String?;
    if (carType != null && carType != 'all') {
      params['carType'] = 'carCategory::$carType';
    }
    return params;
  }

  static Map<String, dynamic> actividades(Map<String, dynamic> data, String ufi) => {
    'id': ufi,
    'startDate': data['startDate'],
    'endDate': data['endDate'],
    'sortBy': 'trending',
    'page': '1',
    'currencyCode': 'EUR',
  };

  static Map<String, dynamic> cruceros(Map<String, dynamic> data) => {
    'startDate': data['startDate'],
    'endDate': data['endDate'],
    'destination': (data['destination'] ?? '').replaceAll(' ', '_'),
    'departurePort': (data['origin'] ?? '').replaceAll(' ', '_'),
    'currency': 'EUR',
  };
}

// ── Equivalente a searchService.ts ───────────────────────────────────────────
class SearchService {
  // Filtra null/vacíos del mapa de params
  static Map<String, dynamic> _clean(Map<String, dynamic> p) =>
      Map.fromEntries(p.entries.where((e) => e.value != null && e.value.toString().isNotEmpty));

  static Future<List<dynamic>> performSearch(
      String section,
      Map<String, dynamic> searchData,
      ) async {
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
        if (res is Map) return _toList(res['data']?['flightOffers'] ?? res['result'] ?? res['data']);
        return _toList(res);
      }

      case 'coches': {
        final params = _clean(ParamsMapper.coches(searchData));
        final res = await api.get('/external/cars/search', params: params);
        var cars = _toList(res);

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
        final locData = await api.get('/activities/location', params: {'query': query});
        String? ufi;
        if (locData is Map) {
          final dests = locData['destinations'] as List?;
          final prods = locData['products'] as List?;
          if (dests != null && dests.isNotEmpty) ufi = dests[0]['id']?.toString();
          else if (prods != null && prods.isNotEmpty) ufi = prods[0]['id']?.toString();
        }
        if (ufi == null) return [];
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

  static Future<dynamic> getHotelDetails(String hotelId, Map<String, dynamic> searchData) async {
    final params = _clean({
      ...ParamsMapper.hotelDetails(hotelId, searchData),
      '_t': DateTime.now().millisecondsSinceEpoch.toString(),
    });
    return api.get('/hotels/details', params: params);
  }

  static Future<dynamic> getFlightDetails(String token, {String currency = 'EUR'}) async {
    return api.get('/flights/details', params: {
      'token': token,
      'currencyCode': currency,
      '_t': DateTime.now().millisecondsSinceEpoch.toString(),
    });
  }

  static Future<dynamic> getActivityDetails(String slug) async {
    return api.get('/activities/details', params: {
      'slug': slug,
      'currencyCode': 'EUR',
      '_t': DateTime.now().millisecondsSinceEpoch.toString(),
    });
  }

  static Future<String> askAI(String pref, String budget) async {
    try {
      final res = await api.get('/ai/recommend', params: {
        'pref': pref,
        'budget': budget,
      });
      return res?.toString() ?? 'Sin respuesta';
    } catch (_) {
      return 'La IA está descansando ahora mismo, prueba en un momento.';
    }
  }

  static Future<String> getItinerary(String query) async {
    try {
      final res = await api.get('/ai/itinerary', params: {'query': query});
      return res?.toString() ?? 'Sin respuesta';
    } catch (_) {
      return 'Se me ha perdido el mapa... Inténtalo de nuevo.';
    }
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