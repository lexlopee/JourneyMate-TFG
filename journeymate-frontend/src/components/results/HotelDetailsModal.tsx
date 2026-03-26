import { useState, useEffect } from 'react';
import { 
  X, MapPin, Check, Coffee, Luggage, Info, ChevronLeft, ChevronRight, 
  MessageSquare, Calendar, Users, Star, Building2, BedDouble, Map as MapIcon 
} from 'lucide-react';
import { HotelMap } from '../HotelMap';

export const HotelDetailsModal = ({ isOpen, onClose, details, loading, searchData, hotelBasicData }: any) => {
  const [activePhotoIndex, setActivePhotoIndex] = useState(0);
  const [loginError, setLoginError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (isOpen) setActivePhotoIndex(0);
  }, [isOpen, details]);

  if (!isOpen) return null;

  console.log("MODAL RECIBE details:", details);
  console.log("MODAL RECIBE hotelBasicData:", hotelBasicData);

  // ✅ FIX: El DTO usa camelCase tras aplicar @JsonProperty en Java
  // details.data contiene el DataPayload del backend
  const hotelData = details?.data;

  const lat = hotelBasicData?.latitud;
  const lng = hotelBasicData?.longitud;
  const stars = hotelBasicData?.propertyClass || 0;

  // 2. FOTOS — los campos ahora se llaman urlMax750 (camelCase del DTO)
  const allPhotos: string[] = [];
  if (hotelBasicData?.urlFoto) allPhotos.push(hotelBasicData.urlFoto);

  if (hotelData?.rooms) {
    Object.values(hotelData.rooms).forEach((room: any) => {
      room.photos?.forEach((photo: any) => {
        // ✅ FIX: era photo.url_max750, ahora es photo.urlMax750
        const url = photo.urlMax750 || photo.url_max750;
        if (url && !allPhotos.includes(url)) {
          allPhotos.push(url);
        }
      });
    });
  }

  // 3. HABITACIONES DISPONIBLES — ✅ FIX: era available_rooms, ahora availableRooms
  const roomsLeft = hotelData?.availableRooms ?? hotelData?.available_rooms ?? 0;

  // 4. PRECIOS — ✅ FIX: usar la moneda del hotel, no hardcodear EUR
  const currency = hotelBasicData?.moneda || hotelData?.currencyCode || hotelData?.currency_code || 'EUR';

  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  const nights = (() => {
    if (!searchData?.startDate || !searchData?.endDate) return 1;
    const start = new Date(searchData.startDate);
    const end = new Date(searchData.endDate);
    return Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)));
  })();

  const currentPrice = hotelBasicData?.precio;

  // ✅ FIX: los campos del DTO ahora son camelCase — facilitiesBlock, reviewScore, etc.
  const facilities = hotelData?.facilitiesBlock?.facilities
    ?? hotelData?.facilities_block?.facilities  // fallback por si acaso
    ?? [];

  const breakfast = hotelData?.breakfastReviewScore ?? hotelData?.breakfast_review_score;
  const hasBreakfast = breakfast && breakfast.rating > 0;

  // ✅ FIX: reviewScore, reviewScoreWord, reviewNr (camelCase)
  const reviewScore = hotelData?.reviewScore ?? hotelData?.review_score ?? hotelBasicData?.calificacion;
  const reviewWord  = hotelData?.reviewScoreWord ?? hotelData?.review_score_word ?? hotelBasicData?.reviewWord;
  const numReviews  = hotelData?.reviewNr ?? hotelData?.review_nr ?? 0;

  // ✅ FIX: accommodationTypeName (camelCase)
  const hotelType = hotelData?.accommodationTypeName ?? hotelData?.accommodation_type_name;

  // ✅ FIX: hotelName y address (camelCase)
  const hotelName = hotelData?.hotelName ?? hotelData?.hotel_name ?? hotelBasicData?.nombre;
  const address   = hotelData?.address ?? hotelBasicData?.direccion;

  const nextPhoto = () => setActivePhotoIndex((prev) => (prev + 1) % allPhotos.length);
  const prevPhoto = () => setActivePhotoIndex((prev) => (prev - 1 + allPhotos.length) % allPhotos.length);

  const handleReserve = async () => {
    setLoginError("");
    setSuccessMessage("");

    const token = localStorage.getItem("token");
    const idUsuario = localStorage.getItem("idUsuario");

    if (!token || !idUsuario) {
      setLoginError("Debes iniciar sesión para reservar.");
      return;
    }

    if (!hotelBasicData?.precio) {
      setLoginError("No se pudo obtener el precio del hotel.");
      return;
    }

    const estrellas = hotelBasicData.propertyClass && hotelBasicData.propertyClass >= 1
      ? hotelBasicData.propertyClass
      : null;

    const servicio = {
      tipo: "HOTEL",
      nombre: hotelBasicData.nombre ?? "Hotel sin nombre",
      precioBase: hotelBasicData.precio,
      descripcion: hotelBasicData.reviewWord ?? null,
      estrellas,
      calle: null,
      numero: null,
      ciudad: null,
      latitud: hotelBasicData.latitud ?? null,
      longitud: hotelBasicData.longitud ?? null,
    };

    const body = {
      idUsuario: Number(idUsuario),
      idTipoReserva: 1,
      idEstado: 1,
      precioTotal: hotelBasicData.precio,
      servicio,
    };

    try {
      const response = await fetch("http://localhost:8080/api/v1/reservas/completa", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error("Error del servidor:", errorText);
        setLoginError("Error al realizar la reserva: " + errorText);
        return;
      }

      setSuccessMessage("¡Reserva realizada con éxito!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (error) {
      console.error("Error de red:", error);
      setLoginError("No se pudo conectar con el servidor. Inténtalo de nuevo.");
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/80 backdrop-blur-md animate-fade-in">
      {successMessage && (
        <div className="fixed top-6 right-6 bg-teal-600 text-white px-6 py-3 rounded-xl shadow-xl flex items-center gap-3 animate-slide-in z-[999]">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="white" className="w-6 h-6">
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
          <span className="font-bold">{successMessage}</span>
        </div>
      )}

      <div className="bg-white w-full max-w-6xl max-h-[94vh] rounded-[3rem] overflow-hidden shadow-2xl relative flex flex-col">
        
        <button 
          onClick={onClose} 
          className="absolute top-6 right-6 z-50 bg-white/90 p-3 rounded-full hover:bg-teal-500 hover:text-white transition-all text-teal-900 shadow-lg"
        >
          <X size={24} />
        </button>

        {loading ? (
          <div className="flex flex-col items-center justify-center h-[600px]">
            <div className="w-12 h-12 border-4 border-teal-100 border-t-teal-600 rounded-full animate-spin mb-4" />
            <p className="text-teal-900 font-black uppercase tracking-widest text-[10px]">
              Cargando experiencia...
            </p>
          </div>
        ) : (
          <div className="overflow-y-auto">
            
            {/* CARRUSEL */}
            <div className="relative bg-black h-80 md:h-110">
              {allPhotos.length > 0 ? (
                <>
                  <img 
                    src={allPhotos[activePhotoIndex]} 
                    className="w-full h-full object-cover opacity-90" 
                    alt="Hotel" 
                  />
                  <div className="absolute bottom-6 left-1/2 -translate-x-1/2 bg-black/60 backdrop-blur-md px-4 py-2 rounded-full text-white text-[10px] font-black tracking-[0.2em] border border-white/10">
                    {activePhotoIndex + 1} / {allPhotos.length}
                  </div>
                </>
              ) : (
                <div className="w-full h-full bg-teal-800 flex items-center justify-center text-white text-[10px] font-black uppercase">
                  Sin imágenes disponibles
                </div>
              )}
              {allPhotos.length > 1 && (
                <>
                  <button onClick={prevPhoto} className="absolute left-6 top-1/2 -translate-y-1/2 bg-white/10 hover:bg-teal-500 backdrop-blur-md p-3 rounded-full text-white transition-all">
                    <ChevronLeft size={24} />
                  </button>
                  <button onClick={nextPhoto} className="absolute right-6 top-1/2 -translate-y-1/2 bg-white/10 hover:bg-teal-500 backdrop-blur-md p-3 rounded-full text-white transition-all">
                    <ChevronRight size={24} />
                  </button>
                </>
              )}
            </div>

            <div className="p-8 md:p-12">
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                
                {/* COLUMNA IZQUIERDA */}
                <div className="lg:col-span-2 space-y-12">
                  
                  {/* CABECERA */}
                  <section>
                    <div className="flex flex-wrap items-center gap-3 mb-6">
                      {stars > 0 && (
                        <div className="flex gap-1 bg-yellow-400/10 px-3 py-1.5 rounded-full">
                          {[...Array(Math.max(0, stars))].map((_, i) => (
                            <Star key={i} size={12} className="fill-yellow-500 text-yellow-500" />
                          ))}
                        </div>
                      )}
                      {hotelType && (
                        <span className="bg-teal-100 text-teal-700 text-[9px] font-black uppercase tracking-widest px-4 py-1.5 rounded-full flex items-center gap-1.5">
                          <Building2 size={12} /> {hotelType}
                        </span>
                      )}
                      
                      {roomsLeft > 0 && (
                        <span className={`text-[9px] font-black uppercase tracking-widest px-4 py-1.5 rounded-full flex items-center gap-1.5 border ${
                          roomsLeft < 5 
                            ? 'bg-orange-100 text-orange-600 border-orange-200 animate-pulse' 
                            : 'bg-rose-100 text-rose-600 border-rose-200'
                        }`}>
                          <BedDouble size={12} /> 
                          {roomsLeft < 5 ? `¡Solo quedan ${roomsLeft} habitaciones!` : `${roomsLeft} habitaciones disponibles`}
                        </span>
                      )}
                    </div>

                    <h2 className="text-4xl md:text-6xl font-black text-teal-900 uppercase tracking-tighter leading-[0.9] mb-8">
                      {hotelName}
                    </h2>
                    
                    <div className="flex items-center gap-3 text-teal-600 font-bold bg-teal-50 px-5 py-3 rounded-2xl border border-teal-100 w-fit">
                      <MapPin size={18} />
                      <span className="text-sm">{address || "Dirección no disponible"}</span>
                    </div>
                  </section>

                  {/* MAPA */}
                  <section className="space-y-4">
                    <div className="flex items-center gap-3 mb-2">
                      <div className="h-px flex-1 bg-teal-100"></div>
                      <h4 className="font-black text-teal-900 uppercase text-[10px] tracking-[0.3em] flex items-center gap-2">
                        <MapIcon size={14} className="text-teal-500"/> Ubicación estratégica
                      </h4>
                      <div className="h-px flex-1 bg-teal-100"></div>
                    </div>
                    <div className="h-[350px] w-full rounded-[3rem] overflow-hidden border-8 border-teal-50 shadow-inner group">
                      {lat && lng ? (
                        <HotelMap lat={Number(lat)} lng={Number(lng)} />
                      ) : (
                        <div className="h-full bg-gray-50 flex items-center justify-center text-[10px] font-black uppercase text-gray-400">
                          Mapa no disponible
                        </div>
                      )}
                    </div>
                  </section>

                  {/* REVIEWS */}
                  <div className={`grid grid-cols-1 ${hasBreakfast ? 'md:grid-cols-2' : 'md:grid-cols-1'} gap-6`}>
                    <div className="bg-teal-50/50 p-8 rounded-[2.5rem] border border-teal-100 flex items-center gap-6">
                      <div className="bg-teal-600 text-white min-w-[64px] h-16 rounded-2xl flex items-center justify-center text-2xl font-black shadow-lg shadow-teal-600/20">
                        {reviewScore ?? "—"}
                      </div>
                      <div>
                        <h4 className="font-black text-teal-900 uppercase text-sm">{reviewWord || "Sin valoración"}</h4>
                        <p className="text-[10px] font-bold text-teal-600/60 uppercase mt-1 tracking-widest">
                          {numReviews} reseñas verificadas
                        </p>
                      </div>
                    </div>

                    {hasBreakfast && (
                      <div className="bg-amber-50/50 p-8 rounded-[2.5rem] border border-amber-100 flex items-center gap-6">
                        <div className="bg-amber-100 p-4 rounded-2xl text-amber-600">
                          <Coffee size={28} />
                        </div>
                        <div>
                          <h4 className="font-black text-amber-900 uppercase text-sm">
                            Desayuno {breakfast.reviewScoreWord ?? breakfast.review_score_word}
                          </h4>
                          <p className="text-[10px] font-bold text-amber-800/60 uppercase tracking-widest mt-1">
                            Calificación: {breakfast.rating}/10
                          </p>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* INSTALACIONES */}
                  <section className="space-y-6">
                    <h4 className="font-black text-teal-900 uppercase text-[10px] tracking-[0.3em] opacity-40 mb-8 border-b border-teal-50 pb-4">
                      Servicios e Instalaciones
                    </h4>
                    {facilities.length === 0 ? (
                      <p className="text-[11px] text-teal-800/40 font-bold uppercase tracking-widest">
                        No hay instalaciones disponibles
                      </p>
                    ) : (
                      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                        {facilities.map((f: any, i: number) => (
                          <div 
                            key={i} 
                            className="flex items-center gap-4 p-5 bg-white rounded-3xl border border-gray-100 hover:border-teal-200 hover:bg-teal-50/30 transition-all group"
                          >
                            <div className="bg-gray-100 group-hover:bg-teal-500 group-hover:text-white p-2 rounded-lg transition-colors">
                              <Check size={14} />
                            </div>
                            <span className="text-[10px] font-black text-gray-600 uppercase truncate">
                              {f.name}
                            </span>
                          </div>
                        ))}
                      </div>
                    )}
                  </section>
                </div>

                {/* CARD RESERVA */}
                <div className="lg:col-span-1">
                  <div className="sticky top-12 bg-teal-900 rounded-[3.5rem] p-10 text-white shadow-2xl space-y-8 border border-white/5 overflow-hidden">
                    <div className="absolute -top-24 -right-24 w-48 h-48 bg-teal-500/20 rounded-full blur-3xl"></div>
                    
                    <div className="relative text-center">
                      <div className="flex flex-col items-center gap-2 mb-10">
                        <div className="flex items-center gap-3 text-[10px] font-black uppercase tracking-[0.25em] bg-white/5 px-4 py-2 rounded-full border border-white/10">
                          <Calendar size={14} className="text-teal-400" /> 
                          {nights} {nights === 1 ? 'Noche' : 'Noches'}
                        </div>
                        <div className="flex items-center gap-3 text-[10px] font-black uppercase tracking-[0.25em] bg-white/5 px-4 py-2 rounded-full border border-white/10">
                          <Users size={14} className="text-teal-400" /> 
                          {searchData?.adults || 1} Adultos
                        </div>
                      </div>

                      <span className="text-[11px] font-black uppercase tracking-[0.5em] text-teal-300/60">
                        Importe Total
                      </span>
                      <div className="mt-6 flex flex-col items-center">
                        <p className="text-6xl font-black leading-none tracking-tighter">
                          {currentPrice ? formatPrice(currentPrice) : "---"}
                        </p>
                        <div className="mt-6 flex items-center gap-2 bg-teal-400/10 text-teal-400 px-4 py-2 rounded-xl">
                          <Info size={14} />
                          <p className="text-[10px] font-black uppercase tracking-widest">
                            Tasas incluidas
                          </p>
                        </div>
                      </div>
                    </div>

                    {loginError && (
                      <div className="bg-red-100 text-red-700 text-xs font-bold px-4 py-3 rounded-xl border border-red-300 mb-4 text-center">
                        {loginError}
                        <div className="mt-2">
                          <a href="/login" className="underline text-red-800 hover:text-red-900">
                            Iniciar sesión
                          </a>
                        </div>
                      </div>
                    )}

                    <button 
                      onClick={handleReserve}
                      className="relative w-full bg-teal-400 hover:bg-white text-teal-950 font-black py-6 rounded-[2rem] uppercase tracking-[0.2em] transition-all shadow-xl flex items-center justify-center gap-3 group"
                    >
                      RESERVAR 
                      <Luggage size={22} className="group-hover:translate-x-1 transition-transform" />
                    </button>
                    
                    <div className="flex justify-between items-center text-[10px] font-black uppercase tracking-widest opacity-40 pt-6 border-t border-white/10">
                      <span className="flex items-center gap-2">
                        <MessageSquare size={14}/> Reseñas
                      </span>
                      <span>{numReviews}</span>
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