import api from './api';
import { paramsMapper } from './paramsMapper';

/**
 * Realiza la búsqueda según la sección activa.
 * La clave aquí es el mapeo de params para que Java reciba exactamente
 * lo que necesita (checkinDate, checkoutDate, etc.)
 */
export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  switch (activeSection) {
    case 'alojamiento': {
      // 1. Obtener el ID de destino desde Java
      const { data: dId } = await api.get('/hotels/destination', {
        params: { name: searchData.destination }
      });

      // 2. Realizar búsqueda con 2 decimales implícitos en la respuesta del backend
      const params = paramsMapper.alojamiento(searchData, dId);
      response = await api.get('/hotels/search', { params });
      break;
    }

    case 'vuelos':
      response = await api.get('/flights/search', { 
        params: paramsMapper.vuelos(searchData) 
      });
      break;

    case 'coches':
      response = await api.get('/external/cars/search', { 
        params: paramsMapper.coches(searchData) 
      });
      break;

    case 'actividades': {
      const { data: locations } = await api.get('/activities/location', {
        params: { query: searchData.destination }
      });
      
      const ufi = locations[0]?.id;
      response = await api.get('/activities/search', { 
        params: paramsMapper.actividades(searchData, ufi) 
      });
      break;
    }

    case 'cruceros':
      response = await api.get('/cruises/search', { 
        params: paramsMapper.cruceros(searchData) 
      });
      break;
  }

  return response?.data;
};

/**
 * Obtiene los detalles y disponibilidad real.
 * Incluimos un cache-buster (_t) para asegurar que el precio y 
 * las habitaciones disponibles sean datos frescos.
 */
export const getHotelDetails = async (hotelId: string, searchData: any) => {
  // Combinamos los parámetros del mapper con un timestamp de seguridad
  const params = {
    ...paramsMapper.hotelDetails(hotelId, searchData),
    _t: new Date().getTime() // Evita que el navegador nos dé un precio guardado
  };
  
  const response = await api.get('/hotels/details', { params });
  return response.data;
};