import { PlaneTakeoff, PlaneLanding, MoveRight, Ticket, Clock } from 'lucide-react';
import { formatCurrency } from '../utils/dateUtils';

interface FlightCardProps {
  flight: any;
  onViewDetails: () => void;
}

export const FlightCard = ({ flight, onViewDetails }: FlightCardProps) => {
  
  const formatTime = (timeString: string) => {
    if (!timeString) return '--:--';
    // Soporta tanto ISO como formatos de hora simple que a veces envían las APIs
    const date = new Date(timeString);
    return isNaN(date.getTime()) ? timeString : date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  // Lógica para determinar si es directo o tiene escalas
  const stops = flight.stops || 0;
  const isDirect = stops === 0;

  return (
    <div className="bg-white rounded-[2.5rem] p-7 shadow-xl shadow-teal-900/5 border border-teal-50 hover:border-teal-300 hover:shadow-teal-900/10 transition-all group flex flex-col h-full relative overflow-hidden">
      
      {/* Badge dinámico según el precio o clase */}
      <div className="absolute top-0 right-0 bg-teal-600 text-white px-6 py-2 rounded-bl-[1.5rem] font-black text-[9px] uppercase tracking-widest shadow-sm z-10">
        {flight.cabinClass || 'Economy'}
      </div>

      {/* CABECERA: Aerolínea */}
      <div className="flex justify-between items-center mb-6 mt-2">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-2xl bg-slate-50 border border-slate-100 p-2 flex items-center justify-center group-hover:scale-110 transition-transform duration-500 shadow-inner">
            {flight.logoUrl ? (
              <img 
                src={flight.logoUrl} 
                alt={flight.aerolinea} 
                className="w-full h-full object-contain filter grayscale group-hover:grayscale-0 transition-all"
                onError={(e: any) => { e.target.src = 'https://via.placeholder.com/50?text=✈️'; }}
              />
            ) : (
              <Ticket className="text-teal-500" size={20} />
            )}
          </div>
          <div>
            <span className="block text-[10px] font-black text-teal-800/40 uppercase tracking-[0.2em]">Aerolínea</span>
            <span className="font-black text-teal-900 uppercase text-xs tracking-tighter line-clamp-1">
              {flight.aerolinea || 'Compañía Aérea'}
            </span>
          </div>
        </div>
        
        {/* Duración total del vuelo */}
        {flight.duracion && (
          <div className="flex items-center gap-1.5 text-teal-600/60 bg-teal-50 px-3 py-1 rounded-full">
            <Clock size={12} />
            <span className="text-[10px] font-bold">{flight.duracion}</span>
          </div>
        )}
      </div>

      {/* CUERPO: El trayecto */}
      <div className="bg-teal-50/30 rounded-[2rem] p-6 mb-8 border border-teal-100/20 flex-grow relative">
        <div className="flex items-center justify-between gap-2">
          
          {/* Salida */}
          <div className="flex-1">
            <div className="flex items-center gap-2 text-teal-500/50 mb-2">
              <PlaneTakeoff size={14} />
              <span className="text-[8px] font-black uppercase tracking-widest">Salida</span>
            </div>
            <span className="block text-2xl font-black text-teal-900 leading-none mb-1">
              {formatTime(flight.horaSalida)}
            </span>
            <span className="text-[11px] font-extrabold text-teal-700 uppercase tracking-tighter">
              {flight.origenCode || flight.origen}
            </span>
          </div>

          {/* Línea de conexión dinámica */}
          <div className="flex-[1.5] flex flex-col items-center justify-center px-2">
            <div className="w-full h-[2px] relative flex items-center justify-center">
               <div className="w-full border-t-2 border-teal-200 border-dashed absolute top-1/2"></div>
               <div className="bg-white p-1 rounded-full border border-teal-100 z-10 group-hover:rotate-[360deg] transition-transform duration-1000">
                  <MoveRight className="text-teal-500" size={14} />
               </div>
            </div>
            <span className={`text-[8px] font-black uppercase tracking-widest mt-3 ${isDirect ? 'text-teal-400' : 'text-amber-500'}`}>
              {isDirect ? 'Vuelo Directo' : `${stops} Escala${stops > 1 ? 's' : ''}`}
            </span>
          </div>

          {/* Llegada */}
          <div className="flex-1 text-right">
            <div className="flex items-center justify-end gap-2 text-teal-500/50 mb-2">
              <span className="text-[8px] font-black uppercase tracking-widest">Llegada</span>
              <PlaneLanding size={14} />
            </div>
            <span className="block text-2xl font-black text-teal-900 leading-none mb-1">
              {formatTime(flight.horaLlegada)}
            </span>
            <span className="text-[11px] font-extrabold text-teal-700 uppercase tracking-tighter">
              {flight.destinoCode || flight.destino}
            </span>
          </div>
        </div>
      </div>

      {/* FOOTER */}
      <div className="flex items-center justify-between pt-4">
        <div>
          <span className="block text-[9px] font-black text-teal-800/30 uppercase tracking-[0.2em]">Precio Final</span>
          <div className="flex items-baseline gap-1">
            <span className="text-2xl font-black text-teal-900 tracking-tighter">
              {formatCurrency(flight.precio, flight.moneda || 'EUR')}
            </span>
          </div>
        </div>

        <button 
          onClick={onViewDetails}
          className="bg-teal-900 text-white px-7 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] hover:bg-teal-600 hover:-translate-y-1 transition-all shadow-lg shadow-teal-900/20 active:scale-95 flex items-center gap-2"
        >
          Detalles
          <MoveRight size={14} className="opacity-50" />
        </button>
      </div>
    </div>
  );
};