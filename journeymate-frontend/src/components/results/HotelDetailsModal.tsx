import { X, MapPin, Building2 } from 'lucide-react';

interface Photo { url_max750: string; }
interface RoomDetails { photos?: Photo[]; }

export const HotelDetailsModal = ({ isOpen, onClose, details, loading }: any) => {
  if (!isOpen) return null;

  const hotelData = details?.data;
  const roomsArray = Object.values(hotelData?.rooms || {}) as RoomDetails[];
  
  // FORMATEADOR DE PRECIOS: Redondea y aplica formato local (ej. 1.250,50 €)
  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: hotelData?.currency_code || 'EUR',
    }).format(value);
  };

  const rawPrice = hotelData?.product_price_breakdown?.gross_amount?.value;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-900/60 backdrop-blur-md animate-fade-in">
      <div className="bg-white w-full max-w-4xl max-h-[90vh] rounded-[3rem] overflow-hidden shadow-2xl relative flex flex-col">
        
        <button onClick={onClose} className="absolute top-6 right-6 z-10 bg-white/80 p-2 rounded-full hover:bg-white transition-colors">
          <X size={24} className="text-teal-900" />
        </button>

        {loading ? (
          <div className="flex flex-col items-center justify-center h-96">
            <div className="w-12 h-12 border-4 border-teal-100 border-t-teal-600 rounded-full animate-spin mb-4" />
            <p className="text-teal-900 font-black uppercase tracking-widest text-[10px]">Cargando detalles...</p>
          </div>
        ) : (
          <div className="overflow-y-auto">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-2 p-2">
              <div className="h-80 bg-teal-50 rounded-[2.5rem] overflow-hidden">
                <img 
                  src={roomsArray[0]?.photos?.[0]?.url_max750 || "https://images.unsplash.com/photo-1566073771259-6a8506099945"} 
                  className="w-full h-full object-cover" 
                  alt="Principal" 
                />
              </div>
              <div className="hidden md:grid grid-cols-2 gap-2 h-80">
                 <div className="bg-teal-100 rounded-[2rem] overflow-hidden">
                    <img src={roomsArray[0]?.photos?.[1]?.url_max750 || "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=400"} className="w-full h-full object-cover opacity-80" />
                 </div>
                 <div className="bg-teal-900 rounded-[2rem] flex flex-col items-center justify-center text-white p-4">
                    <span className="text-3xl font-black">{roomsArray.length}</span>
                    <span className="text-[10px] font-bold uppercase">Habitaciones</span>
                 </div>
              </div>
            </div>

            <div className="p-10">
              <div className="flex flex-col md:flex-row justify-between items-start gap-8">
                <div className="flex-1">
                  <h2 className="text-4xl font-black text-teal-900 uppercase tracking-tighter mb-2 leading-none">
                    {hotelData?.hotel_name}
                  </h2>
                  <div className="flex items-center gap-2 text-teal-600 font-bold mb-8">
                    <MapPin size={18} />
                    <span>{hotelData?.address}, {hotelData?.city}</span>
                  </div>
                  
                  <div className="bg-teal-50 p-6 rounded-[2rem] border border-teal-100 text-sm text-teal-800/70">
                      <h4 className="font-black text-teal-900 uppercase text-xs mb-3 flex items-center gap-2">
                        <Building2 size={16}/> Descripción
                      </h4>
                      <p>Alojamiento de alta calidad en el centro de {hotelData?.city}. Gestión exclusiva JourneyMate.</p>
                  </div>
                </div>

                <div className="w-full md:w-80 bg-teal-900 rounded-[2.5rem] p-8 text-white shadow-2xl text-center">
                  <span className="text-[10px] font-black uppercase tracking-[0.3em] opacity-50">Precio Final</span>
                  <div className="mt-4 mb-8">
                    {/* PRECIO REDONDEADO Y FORMATEADO */}
                    <p className="text-4xl font-black">
                      {rawPrice ? formatPrice(rawPrice) : "Consultar"}
                    </p>
                    <p className="text-[10px] opacity-60 mt-1 uppercase font-bold tracking-tighter">Todo incluido</p>
                  </div>
                  <button className="w-full bg-teal-400 text-teal-900 font-black py-4 rounded-2xl uppercase tracking-widest hover:bg-white transition-all">
                    RESERVAR AHORA
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};