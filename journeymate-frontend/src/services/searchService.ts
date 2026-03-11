import api from './api';

export { api };

/**
 * Obtiene catálogos de cruceros (Destinos y Puertos)
 */
export const getCruiseCatalogs = async () => {
  const [destRes, portRes] = await Promise.all([
    api.get('/cruises/destinations'),
    api.get('/cruises/ports')
  ]);
  return { destinations: destRes.data, ports: portRes.data };
};

/**
 * Realiza la búsqueda según la sección activa.
 * Los parámetros coinciden con los @RequestParam del Backend en Java.
 */
export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  switch (activeSection) {
    case 'alojamiento':
      // 1. Primero obtenemos el ID del destino (String puro según tu Controller)
      const hotelDest = await api.get(`/hotels/destination?name=${searchData.destination}`);
      const dId = hotelDest.data;

      // 2. Realizamos la búsqueda de hoteles
      response = await api.get('/hotels/search', {
        params: {
          destId: dId,                       // Coincide con Java @RequestParam
          checkinDate: searchData.startDate,  // Coincide con Java @RequestParam
          checkoutDate: searchData.endDate,   // Coincide con Java @RequestParam
          adults: searchData.adults || 2,     // Default 2 según tu Controller
          currencyCode: 'EUR',                // Forzamos EUR
          searchType: 'CITY',                 // Default CITY según tu Controller
          roomQty: 1,
          pageNo: 1
        }
      });
      break;

    case 'vuelos':
      response = await api.get('/flights/search', {
        params: {
          fromId: searchData.origin,
          toId: searchData.destination,
          departDate: searchData.startDate,
          adults: searchData.adults,
          currencyCode: 'EUR'
        }
      });
      break;

    case 'coches':
      response = await api.get('/external/cars/search', {
        params: {
          pickUpId: searchData.origin,
          pDate: searchData.startDate,
          pTime: searchData.pickupTime,
          dDate: searchData.endDate,
          dTime: '10:00',
          currencyCode: 'EUR'
        }
      });
      break;

    case 'actividades':
      const locRes = await api.get(`/activities/location?query=${searchData.destination}`);
      const ufi = locRes.data[0]?.id;
      response = await api.get('/activities/search', {
        params: { 
          id: ufi, 
          startDate: searchData.startDate, 
          endDate: searchData.endDate,
          currencyCode: 'EUR'
        }
      });
      break;

    case 'cruceros':
      response = await api.get('/cruises/search', {
        params: {
          startDate: searchData.startDate,
          endDate: searchData.endDate,
          destination: searchData.destination,
          departurePort: searchData.origin,
          currencyCode: 'EUR'
        }
      });
      break;
  }

  return response?.data;
};

/**
 * Obtiene los detalles de un hotel específico.
 * Es crucial que arrivalDate y departureDate sean los mismos que en la búsqueda.
 */
export const getHotelDetails = async (hotelId: string, searchData: any) => {
  const response = await api.get('/hotels/details', {
    params: {
      hotelId: hotelId,                  // @RequestParam String hotelId
      arrivalDate: searchData.startDate,  // @RequestParam String arrivalDate
      departureDate: searchData.endDate,  // @RequestParam String departureDate
      adults: searchData.adults || 2,     // @RequestParam Integer adults
      roomQty: 1,
      currencyCode: 'EUR'                 // Enviamos EUR para que el Backend lo use
    }
  });
  return response.data;
};