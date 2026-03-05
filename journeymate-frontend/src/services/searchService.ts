import api from './api';

export const getCruiseCatalogs = async () => {
  const [destRes, portRes] = await Promise.all([
    api.get('/cruises/destinations'),
    api.get('/cruises/ports')
  ]);
  return { destinations: destRes.data, ports: portRes.data };
};

export const performSearch = async (activeSection: string, searchData: any) => {
  let response;

  switch (activeSection) {
    case 'alojamiento':
      const hotelDest = await api.get(`/hotels/destination?name=${searchData.destination}`);
      response = await api.get('/hotels/search', {
        params: {
          destId: hotelDest.data,
          checkinDate: searchData.startDate,
          checkoutDate: searchData.endDate,
          adults: searchData.adults
        }
      });
      break;

    case 'vuelos':
      response = await api.get('/flights/search', {
        params: {
          fromId: searchData.origin,
          toId: searchData.destination,
          departDate: searchData.startDate,
          adults: searchData.adults
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
          dTime: '10:00'
        }
      });
      break;

    case 'actividades':
      const locRes = await api.get(`/activities/location?query=${searchData.destination}`);
      const ufi = locRes.data[0]?.id;
      response = await api.get('/activities/search', {
        params: { id: ufi, startDate: searchData.startDate, endDate: searchData.endDate }
      });
      break;

    case 'cruceros':
      response = await api.get('/cruises/search', {
        params: {
          startDate: searchData.startDate,
          endDate: searchData.endDate,
          destination: searchData.destination,
          departurePort: searchData.origin
        }
      });
      break;
  }

  return response?.data;
};