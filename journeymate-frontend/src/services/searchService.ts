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

export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  // 1. Normalizamos los datos de búsqueda (especialmente fechas)
  const normalizedData = {
    ...searchData,
    startDate: formatDate(searchData.startDate),
    endDate: formatDate(searchData.endDate),
  };

  switch (activeSection) {
    case 'alojamiento': {
      const { data: dId } = await api.get('/hotels/destination', {
        params: { name: searchData.destination }
      });
      // Aquí el mapper recibe 2 argumentos explícitamente
      const params = paramsMapper.alojamiento(normalizedData, dId);
      response = await api.get('/hotels/search', { params });
      break;
    }

    case 'vuelos': {
      // Vuelos solo recibe 1 argumento (searchData con fromId/toId)
      const params = paramsMapper.vuelos(normalizedData);
      response = await api.get('/flights/search', { params });
      break;
    }

    case 'actividades': {
      const { data: locations } = await api.get('/activities/location', {
        params: { query: searchData.destination }
      });
      const ufi = locations[0]?.id; 
      const params = paramsMapper.actividades(normalizedData, ufi);
      response = await api.get('/activities/search', { params });
      break;
    }

    // 2. Manejo dinámico para casos que solo requieren 1 argumento (Coches, Cruceros, Trenes)
    default: {
      const mapperKey = activeSection as keyof typeof paramsMapper;
      const mapperFn = paramsMapper[mapperKey];

      if (typeof mapperFn === 'function') {
        // Forzamos a TS a entender que en este flujo genérico usamos 1 solo argumento
        const params = (mapperFn as (data: any) => any)(normalizedData);
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
  };

  const params = {
    ...paramsMapper.hotelDetails(hotelId, normalizedData),
    _t: Date.now() 
  };
  
  const response = await api.get('/hotels/details', { params });
  return response.data;
};

/**
 * Detalles de Vuelo (Sincronizado con ExternalFlightController de Java)
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