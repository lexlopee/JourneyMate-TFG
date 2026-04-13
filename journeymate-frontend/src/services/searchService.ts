import api from './api';
import { paramsMapper } from './paramsMapper';

/**
 * Asegura que la fecha sea un String YYYY-MM-DD para el backend.
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
 * Sanitiza parámetros para evitar que "NaN", null o "undefined" lleguen al backend.
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
  let response: any; // Declarada con let para poder ser asignada en el switch

  const normalizedData = {
    ...searchData,
    startDate: formatDate(searchData.startDate),
    endDate: formatDate(searchData.endDate),
    adults: searchData.adults || 1,
    roomQty: searchData.roomQty || 1,
  };

  const query = searchData.destinationText || searchData.destination;

  switch (activeSection) {
    case 'alojamiento': {
      if (!query) throw new Error("Destino no proporcionado");
      
      const { data: dId } = await api.get('/hotels/destination', {
        params: { name: query }
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
      const rawParams = paramsMapper.coches(normalizedData);
      const params = sanitizeParams(rawParams);
      const res = await api.get('/external/cars/search', { params });

      if (res.data && Array.isArray(res.data)) {
        let cars = res.data;
        const carTypeMap: Record<string, string[]> = {
          small:    ['mini', 'economy', 'small', 'compact'],
          medium:   ['medium', 'intermediate', 'standard'],
          large:    ['large', 'full-size', 'fullsize', 'full size'],
          suvs:     ['suv', 'crossover', '4x4'],
          premium:  ['premium', 'luxury', 'sport', 'convertible'],
          carriers: ['van', 'minivan', 'people carrier', 'bus', 'truck'],
        };

        const selectedType = normalizedData.carType;
        if (selectedType && selectedType !== 'all') {
          const keywords = carTypeMap[selectedType] ?? [];
          if (keywords.length > 0) {
            const filtered = cars.filter((car: any) => {
              const name = (car.carName ?? '').toLowerCase();
              return keywords.some(kw => name.includes(kw));
            });
            if (filtered.length > 0) cars = filtered;
          }
        }
        return cars.slice(0, 20);
      }
      return res.data;
    }

    case 'actividades': {
      if (!query) throw new Error("Destino no proporcionado");

      // Paso 1: Obtener localización
      const { data: locData } = await api.get('/activities/location', {
          params: { query: query }
      });

      // DEBUG: Descomenta la línea de abajo si vuelve a fallar para ver qué llega exactamente
      // console.log("Estructura recibida del backend:", locData);

      let ufi = null;

      // Navegación: El backend ya envía el objeto con destinos y productos
      // Accedemos directamente a locData
      if (locData.destinations && locData.destinations.length > 0) {
          ufi = locData.destinations[0].id; 
      } else if (locData.products && locData.products.length > 0) {
          // A veces el UFI viene como cityUfi en el producto
          ufi = locData.products[0].id;
      }

      if (!ufi) {
          console.warn("No se encontró un UFI válido para:", query);
          return [];
      }

      // Paso 2: Búsqueda con el ID obtenido (UFI)
      const searchParams = {
          id: ufi,
          startDate: normalizedData.startDate,
          endDate: normalizedData.endDate,
          currencyCode: searchData.currencyCode || 'EUR',
          sortBy: 'trending',
          page: 1
      };

      const cleanParams = sanitizeParams(searchParams);
      
      // Realizamos la búsqueda final
      response = await api.get('/activities/search', { params: cleanParams });
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

/**
 * Detalles de Actividad
 */
export const getActivityDetails = async (slug: string) => {
  const response = await api.get('/activities/details', {
    params: { 
      slug: slug,
      currencyCode: 'EUR',
      _t: Date.now()
    }
  });
  return response.data;
};