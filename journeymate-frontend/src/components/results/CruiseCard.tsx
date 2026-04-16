import { Ship, Moon, MapPin, Anchor } from 'lucide-react';

export const CruiseCard = ({ cruise, onViewDetails }: any) => {
  const formatPrice = (value: number, currency: string) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currency || 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  // Priorizamos los nombres del nuevo DTO
  const nombre = cruise.nombreCrucero || cruise.cruise_name;
  const barco = cruise.nombreBarco || cruise.ship_name;
  const imagen = cruise.imagenPrincipal;

  return (
    <div className="bg-white/90 backdrop-blur-md rounded-[2.5rem] overflow-hidden border border-white/20 shadow-xl hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 group">
      
      {/* Header con Imagen Real o Fallback */}
      <div className="relative h-48 bg-teal-800 flex items-center justify-center overflow-hidden">
        {imagen ? (
          <img 
            src={imagen} 
            alt={nombre}
            className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
          />
        ) : (
          <div className="absolute inset-0 bg-gradient-to-br from-teal-800 to-teal-600 opacity-50" />
        )}
        
        {/* Overlay para legibilidad */}
        <div className="absolute inset-0 bg-gradient-to-t from-teal-900/60 to-transparent" />
        
        {!imagen && <Ship size={72} className="relative text-white/20" />}

        {/* Badge noches */}
        <div className="absolute top-4 right-4 bg-black/40 backdrop-blur-md border border-white/20 text-white text-[10px] font-black px-3 py-1.5 rounded-full flex items-center gap-1.5">
          <Moon size={10} />
          <span>{cruise.noches || cruise.duration_nights} noches</span>
        </div>
      </div>

      {/* Contenido */}
      <div className="p-6 flex flex-col gap-4">
        <div>
          <h3 className="text-lg font-black text-teal-900 leading-tight mb-1 line-clamp-2 uppercase tracking-tighter">
            {nombre}
          </h3>
          <p className="text-[11px] font-bold text-teal-600/60 uppercase tracking-widest flex items-center gap-1">
            <Anchor size={10} />
            {barco}
          </p>
        </div>

        <div className="flex items-center gap-2 text-teal-600/60 text-[10px] font-black uppercase tracking-widest">
          <MapPin size={12} className="text-teal-500" />
          <span>{cruise.puertoSalida || cruise.departure_port}</span>
        </div>

        {/* Footer: Precio + Acción */}
        <div className="flex items-center justify-between pt-2 border-t border-teal-100/30">
          <div>
            <p className="text-[9px] font-black text-teal-800/40 uppercase tracking-widest mb-0.5">Desde</p>
            <p className="text-2xl font-black text-teal-600 tracking-tighter leading-none">
              {formatPrice(cruise.precioDesde || cruise.price_from || 0, cruise.moneda || cruise.currency || 'USD')}
            </p>
          </div>

          <button
            onClick={() => onViewDetails(cruise)}
            className="bg-teal-900 text-white h-12 w-12 rounded-2xl flex items-center justify-center hover:bg-teal-700 transition-all shadow-lg group-hover:scale-110 active:scale-95"
          >
            <Ship size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};