import { Star, MapPin, Building } from 'lucide-react';

interface HotelCardProps {
  hotel: any;
  onViewDetails: (id: string) => void;
  destination: string;
}

export const HotelCard = ({ hotel, onViewDetails, destination }: HotelCardProps) => {
  // Extraemos los datos del DTO que definiste en Java (HotelDTO)
  const name = hotel.nombre || "Alojamiento JourneyMate";
  const hotelId = hotel.hotelId;
  const rating = hotel.calificacion > 0 ? hotel.calificacion : "Nuevo";
  const image = hotel.urlFoto || "https://images.unsplash.com/photo-1566073771259-6a8506099945";
  const reviewText = hotel.reviewWord || "Sin calificar";

  /**
   * Formatea el precio que viene del Backend.
   * Como ya forzamos EUR en el searchService, aquí solo damos formato visual.
   */
  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0, // Redondeo a entero para que coincida con el detalle
    }).format(value);
  };

  return (
    <div className="bg-white/90 backdrop-blur-md rounded-[2.5rem] overflow-hidden border border-white/20 shadow-xl hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 group">
      {/* Imagen y Rating */}
      <div className="relative h-56 overflow-hidden bg-teal-50">
        <img 
          src={image} 
          alt={name} 
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" 
        />
        <div className="absolute top-4 right-4 bg-teal-900/80 backdrop-blur-md text-white text-[10px] font-black px-3 py-1.5 rounded-full flex items-center gap-1">
          <Star size={10} className={hotel.calificacion > 0 ? "fill-yellow-400 text-yellow-400" : "fill-white"} />
          <span>{rating} ({reviewText})</span>
        </div>
      </div>

      {/* Contenido de la Tarjeta */}
      <div className="p-6 flex flex-col h-[200px] justify-between">
        <div>
          <h3 className="text-xl font-black text-teal-900 leading-tight mb-2 line-clamp-2 uppercase tracking-tighter">
            {name}
          </h3>
          <div className="flex items-center gap-1.5 text-teal-600/60 text-[10px] font-black uppercase tracking-widest">
            <MapPin size={12} className="text-teal-500" />
            <span className="truncate">{destination}</span>
          </div>
        </div>

        {/* Precio y Botón de Acción */}
        <div className="flex items-center justify-between pt-4 border-t border-teal-100/30">
          <div className="flex flex-col">
            <span className="text-[8px] font-black text-teal-800/40 uppercase tracking-[0.2em]">Total estancia</span>
            <div className="flex items-baseline gap-1">
              <span className="text-2xl font-black text-teal-600 tracking-tighter">
                {formatPrice(hotel.precio)}
              </span>
            </div>
          </div>
          
          <button 
            onClick={() => onViewDetails(hotelId)} 
            className="bg-teal-900 text-white h-12 w-12 rounded-2xl flex items-center justify-center hover:bg-teal-700 transition-all shadow-lg group-hover:scale-110 active:scale-95"
            title="Ver detalles del alojamiento"
          >
            <Building size={20} />
          </button>
        </div>
      </div>
    </div>
  ); 
};