import api from './api';
import { paramsMapper } from './paramsMapper';

/**
 * Asegura que la fecha sea un String YYYY-MM-DD para Java.
 */
const formatDate = (date: any): string | undefined => {
  if (!date) return undefined;
  if (typeof date === 'string') return date; 
  try {
    return new Date(date).toISOString().split('T')[0];
  } catch (e) {
    return undefined;
  }
};

/**
 * Sanitiza parámetros para evitar que "NaN" o "undefined" lleguen al backend.
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

/**
 * Búsqueda General (Hoteles, Vuelos, Coches, Actividades)
 */
export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  const normalizedData = {
    ...searchData,
    startDate: formatDate(searchData.startDate),
    endDate: formatDate(searchData.endDate),
    adults: searchData.adults || 1,
    roomQty: searchData.roomQty || 1,
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

    case 'coches': {
      // 1. Mapeamos los datos con los nuevos nombres
      const rawParams = paramsMapper.coches(normalizedData);
      // 2. Limpiamos nulos/undefined
      const params = sanitizeParams(rawParams);
      console.log("Enviando parámetros a Java:", params);
      const res = await api.get('/external/cars/search', { params });
      
      if (res.data && Array.isArray(res.data)) {
        return res.data.slice(0, 20); 
      }
      return res.data;
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
 * Detalles de Hotel (Exportado correctamente)
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
 * Detalles de Vuelo (La función que te faltaba exportar)
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