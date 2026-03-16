import { Star, MapPin, Building, Info } from 'lucide-react';

interface HotelCardProps {
  hotel: any;
  onViewDetails: (hotel: any) => void;
  destination: string;
}

export const HotelCard = ({ hotel, onViewDetails, destination }: HotelCardProps) => {
  // Mapeo seguro de datos del DTO de Java
  const name = hotel.nombre || "Alojamiento JourneyMate";
  const hotelId = hotel.hotelId;
  const rating = hotel.calificacion > 0 ? hotel.calificacion : "Nuevo";
  const image = hotel.urlFoto || "https://images.unsplash.com/photo-1566073771259-6a8506099945";
  const reviewText = hotel.reviewWord || "Sin calificar";
  const tipoAlojamiento = hotel.tipoAlojamiento || "Alojamiento";

  // Formateo de precio (SINCRONIZADO CON MODAL: 2 DECIMALES)
  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2, 
    }).format(value);
  };

  return (
    <div className="bg-white/90 backdrop-blur-md rounded-[2.5rem] overflow-hidden border border-white/20 shadow-xl hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 group">
      
      {/* Imagen y Badges */}
      <div className="relative h-56 overflow-hidden bg-teal-50">
        <img 
          src={image} 
          alt={name} 
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" 
        />
        
        {/* Badge: Tipo de Alojamiento */}
        <div className="absolute top-4 left-4 bg-white/90 backdrop-blur-md text-teal-900 text-[9px] font-black px-3 py-1 rounded-full uppercase tracking-widest shadow-sm border border-teal-100">
          {tipoAlojamiento}
        </div>

        {/* Badge: Rating */}
        <div className="absolute top-4 right-4 bg-teal-900/80 backdrop-blur-md text-white text-[10px] font-black px-3 py-1.5 rounded-full flex items-center gap-1">
          <Star 
            size={10} 
            className={hotel.calificacion > 0 ? "fill-yellow-400 text-yellow-400" : "fill-white"} 
          />
          <span>{rating} {reviewText !== "Sin calificar" && `(${reviewText})`}</span>
        </div>
      </div>

      {/* Contenido */}
      <div className="p-6 flex flex-col h-[200px] justify-between">
        <div>
          <h3 className="text-xl font-black text-teal-900 leading-tight mb-2 line-clamp-2 uppercase tracking-tighter">
            {name}
          </h3>
          <div className="flex items-center gap-1.5 text-teal-600/60 text-[10px] font-black uppercase tracking-widest">
            <MapPin size={12} className="text-teal-500" />
            <span className="truncate">{destination || "Destino seleccionado"}</span>
          </div>
        </div>

        {/* Footer de la Card: Precio y Acción */}
        <div className="flex items-center justify-between pt-4 border-t border-teal-100/30">
          <div className="flex flex-col relative">
            {/* Etiqueta de Precio Estimado */}
            <div className="flex items-center gap-1 mb-0.5">
              <span className="text-[8px] font-black text-teal-800/40 uppercase tracking-[0.2em]">
                Precio estimado
              </span>
              <Info size={8} className="text-teal-800/30" />
            </div>
            
            <span className="text-2xl font-black text-teal-600 tracking-tighter leading-none">
              {formatPrice(hotel.precio || 0)}
            </span>
            
            <span className="text-[9px] font-medium text-teal-800/30 mt-1 italic">
              *Sujeto a cambios según disponibilidad
            </span>
          </div>
          
          <button 
            onClick={() => onViewDetails(hotel)} 
            className="bg-teal-900 text-white h-12 w-12 rounded-2xl flex items-center justify-center hover:bg-teal-700 transition-all shadow-lg group-hover:scale-110 active:scale-95"
            title="Ver detalles"
          >
            <Building size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};