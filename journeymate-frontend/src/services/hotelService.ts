import api from './api';

export const buscarHoteles = async (searchData: any) => {
  // 1. Obtener ID del destino
  // Nota: Asegúrate de si tu API devuelve solo el ID o un objeto con dest_id
  const destRes = await api.get(`/hotels/destination?name=${searchData.destination}`);
  const destId = destRes.data; 

  if (!destId) throw new Error("Ciudad no encontrada");

  // 2. Buscar hoteles con los parámetros EXACTOS de la documentación
  const response = await api.get('/hotels/search', {
    params: {
      dest_id: destId,                      // Antes: destId (Mal)
      arrival_date: searchData.startDate,    // Antes: checkinDate (Mal)
      departure_date: searchData.endDate,    // Antes: checkoutDate (Mal)
      adults: searchData.adults || 1,        // Mismos adultos que en el detalle
      room_qty: 1,                           // Forzamos 1 para que coincida con el detalle
      languagecode: 'es',
      currency_code: 'EUR',                 // Forzamos Euros aquí también
      units: 'metric'
    }
  });

  return response.data;
};