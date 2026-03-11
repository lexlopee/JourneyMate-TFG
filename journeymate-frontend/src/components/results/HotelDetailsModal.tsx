import { useState } from 'react';
import { X, MapPin, Check, Star, Coffee, Luggage, Info, ChevronLeft, ChevronRight, ShieldCheck, MessageSquare } from 'lucide-react';

export const HotelDetailsModal = ({ isOpen, onClose, details, loading }: any) => {
  const [activePhotoIndex, setActivePhotoIndex] = useState(0);

  if (!isOpen) return null;

  const hotelData = details?.data;

  // 1. LÓGICA DE PRECIO: Euro Principal + Moneda Original Pequeña
  const EXCHANGE_RATE = 0.92; // Tasa de conversión fija

  const formatToEuro = (value: number, originalCurrency: string) => {
    let finalValue = value;
    if (originalCurrency !== 'EUR') {
      finalValue = value * EXCHANGE_RATE;
    }
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0,
    }).format(finalValue);
  };

  const formatOriginal = (value: number, currencyCode: string) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currencyCode,
    }).format(value);
  };

  // 2. EXTRACCIÓN DE FOTOS
  const roomsMap = hotelData?.rooms || {};
  const allPhotos: string[] = [];
  Object.values(roomsMap).forEach((room: any) => {
    room.photos?.forEach((photo: any) => {
      if (photo.url_max750) allPhotos.push(photo.url_max750);
    });
  });

  const nextPhoto = () => setActivePhotoIndex((prev) => (prev + 1) % allPhotos.length);
  const prevPhoto = () => setActivePhotoIndex((prev) => (prev - 1 + allPhotos.length) % allPhotos.length);

  const rawPrice = hotelData?.product_price_breakdown?.gross_amount?.value;
  const originalCurrency = hotelData?.currency_code || 'EUR';
  const facilities = hotelData?.facilities_block?.facilities || [];
  
  // Reseñas y Desayuno
  const reviewScore = hotelData?.review_score;
  const reviewWord = hotelData?.review_score_word;
  const reviewCount = hotelData?.review_nr;
  const breakfast = hotelData?.breakfast_review_score;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/80 backdrop-blur-md animate-fade-in">
      <div className="bg-white w-full max-w-6xl max-h-[94vh] rounded-[3rem] overflow-hidden shadow-2xl relative flex flex-col">
        
        <button onClick={onClose} className="absolute top-6 right-6 z-50 bg-white/90 p-3 rounded-full hover:bg-teal-500 hover:text-white transition-all shadow-xl">
          <X size={24} />
        </button>

        {loading ? (
          <div className="flex flex-col items-center justify-center h-[500px]">
            <div className="w-12 h-12 border-4 border-teal-100 border-t-teal-600 rounded-full animate-spin mb-4" />
            <p className="text-teal-900 font-black uppercase tracking-widest text-[10px]">Actualizando JourneyMate...</p>
          </div>
        ) : (
          <div className="overflow-y-auto">
            
            {/* CARRUSEL + CONTADOR */}
            <div className="relative group bg-black h-[350px] md:h-[450px]">
              <img src={allPhotos[activePhotoIndex]} className="w-full h-full object-cover" alt="Hotel" />
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
                    <h2 className="text-4xl md:text-5xl font-black text-teal-900 uppercase tracking-tighter leading-none mb-6">
                      {hotelData?.hotel_name}
                    </h2>
                    <div className="flex items-center gap-2 text-teal-600 font-bold bg-teal-50 px-4 py-2 rounded-full border border-teal-100 w-fit">
                      <MapPin size={16} />
                      <span className="text-xs">{hotelData?.address}</span>
                    </div>
                  </section>

                  {/* Bloque Reseñas y Desayuno */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {reviewScore && (
                      <div className="bg-teal-50/50 p-6 rounded-[2.5rem] border border-teal-100 flex items-center gap-5">
                        <div className="bg-teal-600 text-white w-14 h-14 rounded-2xl flex items-center justify-center text-xl font-black">
                          {reviewScore}
                        </div>
                        <div>
                          <h4 className="font-black text-teal-900 uppercase text-xs">{reviewWord}</h4>
                          <p className="text-[10px] font-bold text-teal-600/60 uppercase tracking-widest mt-1">
                            {reviewCount} reseñas totales
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

                  <section>
                    <h4 className="font-black text-teal-900 uppercase text-[10px] tracking-widest mb-6 opacity-40">Instalaciones</h4>
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                      {facilities.slice(0, 9).map((f: any, i: number) => (
                        <div key={i} className="flex items-center gap-3 p-4 bg-gray-50 rounded-2xl border border-gray-100">
                          <Check className="text-teal-500" size={14} />
                          <span className="text-[10px] font-bold text-gray-600 uppercase truncate">{f.name}</span>
                        </div>
                      ))}
                    </div>
                  </section>
                </div>

                {/* CARD RESERVA: PRECIO EUR + ORIGINAL PEQUEÑO */}
                <div className="lg:col-span-1">
                  <div className="sticky top-6 bg-teal-900 rounded-[3rem] p-10 text-white shadow-2xl space-y-8 border border-white/5">
                    <div className="text-center">
                        <span className="text-[10px] font-black uppercase tracking-[0.4em] text-teal-300 opacity-60">Precio Total Estancia</span>
                        <div className="mt-6 flex flex-col items-center">
                            {/* PRECIO EN EUROS REDONDEADO */}
                            <p className="text-6xl font-black leading-none tracking-tighter">
                                {rawPrice ? formatToEuro(rawPrice, originalCurrency) : "---"}
                            </p>

                            {/* PRECIO ORIGINAL EN PEQUEÑO (Solo si no es EUR) */}
                            {originalCurrency !== 'EUR' && rawPrice && (
                                <p className="mt-3 text-[11px] font-bold text-teal-300/60 uppercase tracking-widest bg-teal-950/50 px-4 py-1.5 rounded-full border border-white/5">
                                    Precio original: {formatOriginal(rawPrice, originalCurrency)}
                                </p>
                            )}

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