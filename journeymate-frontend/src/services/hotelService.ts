import api from './api';

export const buscarHoteles = async (searchData: any) => {
  // 1. Obtener ID del destino
  const destRes = await api.get(`/hotels/destination?name=${searchData.destination}`);
  const destId = destRes.data;

  if (!destId) throw new Error("Ciudad no encontrada");

  // 2. Buscar hoteles con ese ID
  const response = await api.get('/hotels/search', {
    params: {
      destId: destId,
      checkinDate: searchData.startDate,
      checkoutDate: searchData.endDate,
      adults: searchData.adults
    }
  });

  return response.data;
};