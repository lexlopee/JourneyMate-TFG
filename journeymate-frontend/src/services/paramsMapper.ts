import { formatDateForBackend } from '../utils/dateUtils';

/**
 * Normaliza el texto para el backend (ej: "Nueva York" -> "Nueva_York")
 */
const normalize = (text: string) => {
  if (!text) return "";
  return text.trim().replace(/\s+/g, "_");
};

export const paramsMapper = {
  // Coincide con @GetMapping("/search") en Java
  alojamiento: (data: any, destId: string) => ({
    destId: destId,
    searchType: 'CITY',
    checkinDate: formatDateForBackend(data.startDate), // Param @RequestParam en Java
    checkoutDate: formatDateForBackend(data.endDate),  // Param @RequestParam en Java
    adults: Number(data.adults) || 2,
    roomQty: Number(data.roomQty) || 1,
    currencyCode: 'EUR',
    pageNo: 1
  }),

  // Coincide con @GetMapping("/details") en Java
  // AQUÍ ESTÁ LA MAGIA: Mismos valores de 'data', distintos nombres de params
  hotelDetails: (hotelId: string, data: any) => ({
    hotelId: hotelId,
    arrivalDate: formatDateForBackend(data.startDate),   // Usa la misma fecha que checkinDate
    departureDate: formatDateForBackend(data.endDate),   // Usa la misma fecha que checkoutDate
    adults: Number(data.adults),                         // Misma ocupación
    roomQty: Number(data.roomQty),                       // Mismas habitaciones
    childrenAge: data.childrenAge || null,
    currencyCode: 'EUR'
  }),

  vuelos: (data: any) => {
    const params: any = {
      fromId: data.fromId,
      toId: data.toId,
      departDate: data.startDate,
      adults: data.adults,
      childrenAge: data.childrenAge,
      cabinClass: data.cabinClass,
      currencyCode: data.currencyCode || 'EUR',
      sort: data.sort || 'BEST',
      pageNo: 1
    };

    if (data.endDate && data.endDate.trim() !== "") {
      params.returnDate = data.endDate;
    }

    return params;
  },

  coches: (data: any) => {
    const params: any = {
      pickUpId: data.fromId,
      dropOffId: data.toId || data.fromId, 
      pickUpDate: formatDateForBackend(data.startDate),
      pickUpTime: data.pickupTime || '10:00',
      dropOffDate: formatDateForBackend(data.endDate),
      dropOffTime: data.dropoffTime || '10:00',
      driverAge: 30,
      currencyCode: 'EUR',
      units: 'metric'
    };

    if (data.carType && data.carType !== 'all') {
      params.carType = `carCategory::${data.carType}`;
    }

    return params;
  },

  actividades: (data: any, ufi?: string) => ({
    id: ufi,
    startDate: data.startDate,
    endDate: data.endDate,sortBy: data.sort || 'trending',
    page: 1,
    currencyCode: 'EUR'
  }),

  activityDetails: (slug: string) => ({
    slug: slug,
    currencyCode: 'EUR'
  }),

  cruceros: (data: any) => ({
    startDate: formatDateForBackend(data.startDate),
    endDate: formatDateForBackend(data.endDate),
    destination: normalize(data.destination),
    departurePort: normalize(data.origin),
    currency: 'EUR'
  })
};