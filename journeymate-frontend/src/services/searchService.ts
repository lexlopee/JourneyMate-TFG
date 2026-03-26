import api from './api';
import { paramsMapper } from './paramsMapper';

/**
 * Asegura que la fecha sea un String YYYY-MM-DD para Java.
 */
const formatDate = (date: any): string | undefined => {
  if (!date) return undefined;
  if (typeof date === 'string') return date; 
  try {
    return date.toISOString().split('T')[0];
  } catch (e) {
    return undefined;
  }
};

/**
 * ✅ FIX: Sanitiza parámetros numéricos antes de enviarlos al backend.
 * Evita que NaN, undefined o null lleguen como string "NaN" en la URL,
 * lo que causa un 500 al intentar parsear Integer en Spring.
 */
const sanitizeParams = (params: Record<string, any>): Record<string, any> => {
  const clean: Record<string, any> = {};
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null) continue;
    if (typeof value === 'number' && isNaN(value)) continue;
    clean[key] = value;
  }
  return clean;
};

export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  const normalizedData = {
    ...searchData,
    startDate: formatDate(searchData.startDate),
    endDate: formatDate(searchData.endDate),
    adults: searchData.adults || 1,
    roomQty: searchData.roomQty || 1,  // ✅ FIX: garantía extra aquí también
  };

  switch (activeSection) {
    case 'alojamiento': {
      const { data: dId } = await api.get('/hotels/destination', {
        params: { name: searchData.destination }
      });
      const params = sanitizeParams(paramsMapper.alojamiento(normalizedData, dId));
      response = await api.get('/hotels/search', { params });
      break;
    }

    case 'vuelos': {
      const params = sanitizeParams(paramsMapper.vuelos(normalizedData));
      response = await api.get('/flights/search', { params });
      break;
    }

    case 'actividades': {
      const { data: locations } = await api.get('/activities/location', {
        params: { query: searchData.destination }
      });
      const ufi = locations[0]?.id; 
      const params = sanitizeParams(paramsMapper.actividades(normalizedData, ufi));
      response = await api.get('/activities/search', { params });
      break;
    }

    default: {
      const mapperKey = activeSection as keyof typeof paramsMapper;
      const mapperFn = paramsMapper[mapperKey];

      if (typeof mapperFn === 'function') {
        const params = sanitizeParams((mapperFn as (data: any) => any)(normalizedData));
        response = await api.get(`/${activeSection}/search`, { params });
      }
      break;
    }
  }

  return response?.data;
};

/**
 * Detalles de Hotel
 */
export const getHotelDetails = async (hotelId: string, searchData: any) => {
  const normalizedData = {
    ...searchData,
    startDate: formatDate(searchData.startDate),
    endDate: formatDate(searchData.endDate),
    adults: searchData.adults || 1,
    roomQty: searchData.roomQty || 1,
  };

  const rawParams = paramsMapper.hotelDetails(hotelId, normalizedData);

  const params = sanitizeParams({
    ...rawParams,
    _t: Date.now(),
  });
  
  const response = await api.get('/hotels/details', { params });
  return response.data;
};

/**
 * Detalles de Vuelo
 */
export const getFlightDetails = async (token: string, currencyCode: string = 'EUR') => {
  const response = await api.get('/flights/details', {
    params: { 
      token, 
      currencyCode, 
      _t: Date.now() 
    }
  });
  return response.data;
};