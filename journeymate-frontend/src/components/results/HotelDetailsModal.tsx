import { useState } from 'react';
import { X, MapPin, Check, Coffee, Luggage, Info, ChevronLeft, ChevronRight, MessageSquare, Calendar, Users, Bed } from 'lucide-react';
import { HotelMap } from '../HotelMap'; // Asegúrate de importar tu componente de mapa

// Recibimos hotelBasicData para las coordenadas inmediatas
export const HotelDetailsModal = ({ isOpen, onClose, details, loading, searchData, hotelBasicData }: any) => {
  const [activePhotoIndex, setActivePhotoIndex] = useState(0);

  if (!isOpen) return null;

  // 1. FUENTES DE DATOS
  const hotelData = details?.data;
  
  // Coordenadas extraídas del hotel seleccionado en la lista (inmediatas)
  const lat = hotelBasicData?.latitud;
  const lng = hotelBasicData?.longitud;

  // 2. LÓGICA DE PRECIO Y FECHAS
  const formatPrice = (value: number, currencyCode: string) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currencyCode,
      maximumFractionDigits: 2,
    }).format(value);
  };

  const getNights = () => {
    if (!searchData?.startDate || !searchData?.endDate) return 0;
    const start = new Date(searchData.startDate);
    const end = new Date(searchData.endDate);
    const diff = end.getTime() - start.getTime();
    const nights = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return nights > 0 ? nights : 0;
  };

  const nights = getNights();

  // 3. EXTRACCIÓN DE FOTOS (Mejorada para evitar duplicados y errores)
  const roomsMap = hotelData?.rooms || {};
  const allPhotos: string[] = [];
  
  // Añadimos la foto principal de la lista como primera opción
  if (hotelBasicData?.urlFoto) allPhotos.push(hotelBasicData.urlFoto);

  Object.values(roomsMap).forEach((room: any) => {
    room.photos?.forEach((photo: any) => {
      if (photo.url_max750 && !allPhotos.includes(photo.url_max750)) {
        allPhotos.push(photo.url_max750);
      }
    });
  });

  const nextPhoto = () => setActivePhotoIndex((prev) => (prev + 1) % allPhotos.length);
  const prevPhoto = () => setActivePhotoIndex((prev) => (prev - 1 + allPhotos.length) % allPhotos.length);

  // Fallback de precio: si la API de detalles no lo trae, usamos el de la lista
  const rawPrice = hotelData?.product_price_breakdown?.all_inclusive_amount?.value || hotelBasicData?.precio;
  const currency = hotelData?.currency_code || 'EUR';
  const facilities = hotelData?.facilities_block?.facilities || [];
  
  const reviewScore = hotelData?.review_score || hotelBasicData?.calificacion;
  const reviewWord = hotelData?.review_score_word || hotelBasicData?.reviewWord;
  const reviewCount = hotelData?.review_nr;
  const breakfast = hotelData?.breakfast_review_score;
  const accommodationType = hotelData?.accommodation_type_name || hotelBasicData?.tipoAlojamiento;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/80 backdrop-blur-md animate-fade-in">
      <div className="bg-white w-full max-w-6xl max-h-[94vh] rounded-[3rem] overflow-hidden shadow-2xl relative flex flex-col">
        
        <button onClick={onClose} className="absolute top-6 right-6 z-50 bg-white/90 p-3 rounded-full hover:bg-teal-500 hover:text-white transition-all shadow-xl text-teal-900">
          <X size={24} />
        </button>

        {loading ? (
          <div className="flex flex-col items-center justify-center h-[600px]">
            <div className="w-12 h-12 border-4 border-teal-100 border-t-teal-600 rounded-full animate-spin mb-4" />
            <p className="text-teal-900 font-black uppercase tracking-widest text-[10px]">Actualizando JourneyMate...</p>
          </div>
        ) : (
          <div className="overflow-y-auto">
            
            {/* CARRUSEL */}
            <div className="relative group bg-black h-[350px] md:h-[450px]">
              {allPhotos.length > 0 ? (
                <img src={allPhotos[activePhotoIndex]} className="w-full h-full object-cover" alt="Hotel" />
              ) : (
                <div className="w-full h-full bg-teal-800 flex items-center justify-center" />
              )}
              
              {allPhotos.length > 1 && (
                <>
                  <button onClick={prevPhoto} className="absolute left-4 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-teal-500 p-3 rounded-full text-white transition-all">
                    <ChevronLeft size={24} />
                  </button>
                  <button onClick={nextPhoto} className="absolute right-4 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-teal-500 p-3 rounded-full text-white transition-all">
                    <ChevronRight size={24} />
                  </button>
                  <div className="absolute bottom-6 right-8 bg-teal-900/90 backdrop-blur-md px-4 py-1.5 rounded-full text-white text-[11px] font-black tracking-widest border border-white/20">
                    {activePhotoIndex + 1} / {allPhotos.length} FOTOS
                  </div>
                </>
              )}
            </div>

            <div className="p-8 md:p-12">
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                
                <div className="lg:col-span-2 space-y-10">
                  <section>
                    {accommodationType && (
                      <span className="inline-block bg-teal-100 text-teal-800 text-[10px] font-black px-4 py-1.5 rounded-full uppercase tracking-[0.2em] mb-4">
                        {accommodationType}
                      </span>
                    )}
                    
                    <h2 className="text-4xl md:text-5xl font-black text-teal-900 uppercase tracking-tighter leading-none mb-6">
                      {hotelData?.hotel_name || hotelBasicData?.nombre}
                    </h2>
                    <div className="flex items-center gap-2 text-teal-600 font-bold bg-teal-50 px-4 py-2 rounded-full border border-teal-100 w-fit">
                      <MapPin size={16} />
                      <span className="text-xs">{hotelData?.address || "Ubicación disponible en el mapa"}</span>
                    </div>
                  </section>

                  {/* NUEVA SECCIÓN: MAPA */}
                  <section>
                    <h4 className="font-black text-teal-900 uppercase text-[10px] tracking-widest mb-6 opacity-40">Ubicación exacta</h4>
                    <div className="h-[300px] w-full rounded-[2.5rem] overflow-hidden shadow-inner border-4 border-teal-50">
                      {lat && lng ? (
                        <HotelMap lat={Number(lat)} lng={Number(lng)} />
                      ) : (
                        <div className="w-full h-full bg-gray-100 flex items-center justify-center text-[10px] font-black text-gray-400">COORDENADAS NO DISPONIBLES</div>
                      )}
                    </div>
                  </section>

                  {/* REVIEWS Y DESAYUNO */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {reviewScore && (
                      <div className="bg-teal-50/50 p-6 rounded-[2.5rem] border border-teal-100 flex items-center gap-5">
                        <div className="bg-teal-600 text-white min-w-[56px] h-14 rounded-2xl flex items-center justify-center text-xl font-black">
                          {reviewScore}
                        </div>
                        <div>
                          <h4 className="font-black text-teal-900 uppercase text-xs">{reviewWord || "Valorado"}</h4>
                          <p className="text-[10px] font-bold text-teal-600/60 uppercase tracking-widest mt-1">
                            {reviewCount ? `${reviewCount} reseñas` : "Puntuación de JourneyMate"}
                          </p>
                        </div>
                      </div>
                    )}

                    {breakfast && breakfast.rating > 0 && (
                      <div className="bg-amber-50/50 p-6 rounded-[2.5rem] border border-amber-100 flex items-center gap-5">
                        <div className="bg-amber-100 p-3.5 rounded-2xl text-amber-600">
                          <Coffee size={24} />
                        </div>
                        <div>
                          <h4 className="font-black text-amber-900 uppercase text-xs">Desayuno {breakfast.review_score_word}</h4>
                          <p className="text-[10px] font-bold text-amber-800/60 uppercase tracking-widest mt-1">
                            Nota: {breakfast.rating}/10
                          </p>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* INSTALACIONES */}
                  <section>
                    <h4 className="font-black text-teal-900 uppercase text-[10px] tracking-widest mb-6 opacity-40">Instalaciones destacadas</h4>
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                      {facilities.length > 0 ? (
                        facilities.slice(0, 9).map((f: any, i: number) => (
                          <div key={i} className="flex items-center gap-3 p-4 bg-gray-50 rounded-2xl border border-gray-100">
                            <Check className="text-teal-500" size={14} />
                            <span className="text-[10px] font-bold text-gray-600 uppercase truncate">{f.name}</span>
                          </div>
                        ))
                      ) : (
                        <p className="text-[10px] font-bold text-gray-400">Información detallada en proceso...</p>
                      )}
                    </div>
                  </section>
                </div>

                {/* CARD RESERVA */}
                <div className="lg:col-span-1">
                  <div className="sticky top-6 bg-teal-900 rounded-[3rem] p-10 text-white shadow-2xl space-y-8 border border-white/5">
                    <div className="text-center">
                        <div className="flex flex-col items-center gap-1 mb-8 opacity-40">
                            <div className="flex items-center gap-2 text-[9px] font-black uppercase tracking-[0.2em]">
                                <Calendar size={12} className="text-teal-400" /> {nights} Noches
                            </div>
                            <div className="flex items-center gap-2 text-[9px] font-black uppercase tracking-[0.2em]">
                                <Users size={12} className="text-teal-400" /> {searchData?.adults} Adultos · <Bed size={12} className="text-teal-400" /> {searchData?.roomQty} Hab.
                            </div>
                        </div>

                        <span className="text-[10px] font-black uppercase tracking-[0.4em] text-teal-300 opacity-60">Precio Total Estancia</span>
                        <div className="mt-6 flex flex-col items-center">
                            <p className="text-5xl font-black leading-none tracking-tighter">
                                {rawPrice ? formatPrice(rawPrice, currency) : "---"}
                            </p>
                            <p className="text-[10px] font-bold opacity-30 uppercase tracking-widest mt-6 flex items-center justify-center gap-1.5">
                                <Info size={12} /> Gestión JourneyMate Incluida
                            </p>
                        </div>
                    </div>

                    <button className="w-full bg-teal-400 hover:bg-white text-teal-950 font-black py-5 rounded-2xl uppercase tracking-[0.2em] transition-all shadow-xl flex items-center justify-center gap-2 group">
                      RESERVAR <Luggage size={20} className="group-hover:rotate-12 transition-transform" />
                    </button>
                    
                    <div className="flex justify-between items-center text-[10px] font-black uppercase tracking-widest opacity-40 pt-4 border-t border-white/10">
                        <span className="flex items-center gap-1.5"><MessageSquare size={12}/> Reseñas</span>
                        <span>{reviewCount || 0}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};