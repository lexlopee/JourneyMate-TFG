import { X, Clock, Plane, ShieldCheck, ArrowRight, Briefcase, Leaf, Ticket, Calendar } from 'lucide-react';
import { formatCurrency } from '../../utils/dateUtils';

export const FlightDetailsModal = ({ isOpen, onClose, details }: any) => {
  if (!isOpen || !details) return null;

  const { data } = details;

  const formatTime = (dateStr: string) => {
    if (!dateStr) return "--:--";
    return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr) return "";
    return new Date(dateStr).toLocaleDateString('es-ES', { 
      weekday: 'long', day: 'numeric', month: 'long' 
    });
  };

  // CORRECCIÓN: La API envía segundos (ej: 7800). 
  // 7800 / 60 = 130 minutos -> 2h 10min.
  const calculateDurationFromSeconds = (seconds: number) => {
    if (!seconds) return "0h 0min";
    const totalMinutes = Math.floor(seconds / 60);
    const hours = Math.floor(totalMinutes / 60);
    const mins = totalMinutes % 60;
    return `${hours}h ${mins}min`;
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/60 backdrop-blur-md">
      <div className="bg-white w-full max-w-3xl rounded-[3.5rem] shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300 border border-white/20">
        
        {/* Header */}
        <div className="bg-gradient-to-br from-teal-900 to-teal-700 p-8 text-white relative">
          <button onClick={onClose} className="absolute top-8 right-8 p-2 hover:bg-white/20 rounded-full transition-colors">
            <X size={20} />
          </button>
          <div className="flex items-center gap-4 mb-4">
            <div className="bg-white/10 p-3 rounded-2xl backdrop-blur-md border border-white/10">
              <Plane size={24} className="text-teal-200" />
            </div>
            <div>
              <h2 className="text-2xl font-black uppercase tracking-tighter">Itinerario de Vuelo</h2>
              <div className="flex gap-2 mt-1">
                <span className="text-[9px] bg-teal-500/30 px-2 py-0.5 rounded-md font-bold uppercase tracking-widest text-teal-100">
                  {data.tripType === 'ROUNDTRIP' ? 'Ida y Vuelta' : 'Solo Ida'}
                </span>
                <span className="text-[9px] text-teal-200 font-bold uppercase tracking-widest opacity-60">
                  REF: {data.offerReference?.substring(0, 12)}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="p-8 max-h-[65vh] overflow-y-auto custom-scrollbar bg-gray-50/50">
          
          {data.segments.map((segment: any, sIdx: number) => (
            <div key={sIdx} className="mb-8 last:mb-0 bg-white rounded-[2.5rem] p-8 border border-teal-100/50 shadow-sm relative overflow-hidden">
              
              <div className="flex justify-between items-center mb-8">
                <div className="flex items-center gap-2">
                  <Calendar size={14} className="text-teal-500" />
                  <span className="text-xs font-black text-teal-900 uppercase">{formatDate(segment.departureTime)}</span>
                </div>
                <div className="text-right">
                  <p className="text-[10px] font-black text-teal-600 uppercase tracking-widest">
                    {sIdx === 0 ? 'Vuelo de Salida' : 'Vuelo de Regreso'}
                  </p>
                </div>
              </div>

              {/* Trayecto Principal */}
              <div className="flex items-center justify-between mb-8">
                <div className="text-center min-w-[80px]">
                  <p className="text-4xl font-black text-teal-950 tracking-tighter leading-none">{segment.departureAirport.code}</p>
                  <p className="text-[10px] text-gray-400 font-bold uppercase mt-1 truncate max-w-[100px]">{segment.departureAirport.cityName}</p>
                  <p className="text-sm font-black text-teal-600 mt-2">{formatTime(segment.departureTime)}</p>
                </div>

                <div className="flex-grow px-4 flex flex-col items-center">
                  <div className="flex items-center gap-2 w-full">
                    <div className="h-[2px] flex-grow bg-gradient-to-r from-transparent to-teal-100" />
                    <Plane size={18} className="text-teal-200" />
                    <div className="h-[2px] flex-grow bg-gradient-to-l from-transparent to-teal-100" />
                  </div>
                  <div className="mt-2 flex items-center gap-1.5 bg-teal-50 px-3 py-1 rounded-full border border-teal-100/50">
                    <Clock size={12} className="text-teal-600" />
                    <span className="text-[10px] font-black text-teal-700 uppercase">
                      {calculateDurationFromSeconds(segment.totalTime)}
                    </span>
                  </div>
                </div>

                <div className="text-center min-w-[80px]">
                  <p className="text-4xl font-black text-teal-950 tracking-tighter leading-none">{segment.arrivalAirport.code}</p>
                  <p className="text-[10px] text-gray-400 font-bold uppercase mt-1 truncate max-w-[100px]">{segment.arrivalAirport.cityName}</p>
                  <p className="text-sm font-black text-teal-600 mt-2">{formatTime(segment.arrivalTime)}</p>
                </div>
              </div>

              {/* Detalle de Conexiones (LEGS) */}
              <div className="space-y-4 pt-6 border-t border-gray-100">
                {segment.legs.map((leg: any, lIdx: number) => (
                  <div key={lIdx} className="flex items-center justify-between bg-gray-50/70 p-4 rounded-2xl border border-gray-100">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 bg-white rounded-xl p-1 shadow-sm flex items-center justify-center border border-gray-100">
                        <img src={leg.carriersData[0]?.logo} alt="Airline" className="w-full h-full object-contain" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <p className="text-[11px] font-black text-teal-900 uppercase">{leg.carriersData[0]?.name}</p>
                          <span className="text-[9px] bg-teal-100 text-teal-700 px-1.5 py-0.5 rounded font-bold">
                            #{leg.flightInfo.flightNumber}
                          </span>
                        </div>
                        <p className="text-[9px] text-gray-500 font-bold uppercase tracking-tight">
                           {formatTime(leg.departureTime)} {leg.departureAirport.code} → {formatTime(leg.arrivalTime)} {leg.arrivalAirport.code}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                       <span className="text-[9px] font-black text-teal-600 uppercase bg-white px-2 py-1 rounded-lg border border-teal-100">
                          {leg.cabinClass}
                       </span>
                    </div>
                  </div>
                ))}
              </div>

              {/* Info de Equipaje - Corregido para leer la estructura de tu JSON */}
              <div className="grid grid-cols-2 gap-3 mt-6">
                <div className="flex items-center gap-3 bg-teal-50/50 p-3 rounded-2xl border border-teal-100/20">
                  <div className="p-2 bg-white rounded-lg text-teal-600 shadow-sm">
                    <Briefcase size={14} />
                  </div>
                  <div>
                    <p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Mano</p>
                    <p className="text-[10px] font-black text-teal-900">
                      {segment.travellerCabinLuggage[0]?.luggageAllowance.maxPiece || '0'} Pieza
                      {segment.travellerCabinLuggage[0]?.luggageAllowance.maxWeightPerPiece && 
                        ` (${segment.travellerCabinLuggage[0]?.luggageAllowance.maxWeightPerPiece} ${segment.travellerCabinLuggage[0]?.luggageAllowance.massUnit})`
                      }
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-3 bg-teal-50/50 p-3 rounded-2xl border border-teal-100/20">
                  <div className="p-2 bg-white rounded-lg text-teal-600 shadow-sm">
                    <ShieldCheck size={14} />
                  </div>
                  <div>
                    <p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Facturado</p>
                    <p className="text-[10px] font-black text-teal-900">
                      {segment.travellerCheckedLuggage[0]?.luggageAllowance.maxPiece || '0'} Pieza
                      {segment.travellerCheckedLuggage[0]?.luggageAllowance.maxWeightPerPiece && 
                        ` (${segment.travellerCheckedLuggage[0]?.luggageAllowance.maxWeightPerPiece} ${segment.travellerCheckedLuggage[0]?.luggageAllowance.massUnit})`
                      }
                    </p>
                  </div>
                </div>
              </div>
            </div>
          ))}

          {/* Sostenibilidad */}
          {data.carbonEmissions && (
            <div className="mt-6 flex items-center justify-between bg-green-50 p-5 rounded-[2rem] border border-green-100">
              <div className="flex items-center gap-3">
                <div className="bg-green-600 p-2 rounded-xl text-white">
                  <Leaf size={16} />
                </div>
                <div>
                  <p className="text-[10px] font-black text-green-800 uppercase tracking-tight">Vuelo más sostenible</p>
                  <p className="text-[9px] text-green-600 font-bold uppercase">Huella de CO2 estimada</p>
                </div>
              </div>
              <p className="text-sm font-black text-green-700">
                {data.carbonEmissions?.footprintForOffer.quantity} {data.carbonEmissions?.footprintForOffer.unit}
              </p>
            </div>
          )}

          {/* Desglose de Pago */}
          <div className="mt-8 bg-teal-950 rounded-[3rem] p-10 text-white relative overflow-hidden shadow-2xl shadow-teal-950/40">
            <div className="absolute top-0 right-0 p-8 opacity-10">
              <Ticket size={120} className="rotate-12" />
            </div>
            
            <div className="flex flex-col md:flex-row justify-between items-end gap-6 relative z-10">
              <div className="w-full md:w-auto">
                <p className="text-teal-400 text-[10px] font-black uppercase tracking-[0.2em] mb-4">Detalle del Pago</p>
                <div className="space-y-2">
                  <div className="flex justify-between items-center w-full min-w-[220px]">
                    <span className="text-[11px] font-bold uppercase opacity-60">Tarifa Base:</span>
                    <span className="text-[11px] font-black">
                      {formatCurrency(data.priceBreakdown.baseFare.units, data.priceBreakdown.baseFare.currencyCode)}
                    </span>
                  </div>
                  <div className="flex justify-between items-center w-full border-t border-white/5 pt-2">
                    <span className="text-[11px] font-bold uppercase opacity-60">Impuestos y Tasas:</span>
                    <span className="text-[11px] font-black">
                      {formatCurrency(data.priceBreakdown.tax.units, data.priceBreakdown.tax.currencyCode)}
                    </span>
                  </div>
                </div>
              </div>

              <div className="text-right w-full md:w-auto">
                <p className="text-teal-400 text-[10px] font-black uppercase tracking-widest mb-1">Precio Final</p>
                <h4 className="text-6xl md:text-7xl font-black tracking-tighter leading-none">
                  {formatCurrency(data.priceBreakdown.total.units, data.priceBreakdown.total.currencyCode)}
                </h4>
              </div>
            </div>
          </div>
        </div>

        {/* Acciones */}
        <div className="p-8 bg-white border-t border-gray-100 flex flex-col md:flex-row gap-4">
          <button onClick={onClose} className="flex-1 py-5 text-[11px] font-black uppercase tracking-widest text-gray-400 hover:text-teal-900 transition-all">
            Cerrar Detalles
          </button>
          <button className="flex-[2] py-5 bg-teal-600 text-white text-[11px] font-black uppercase tracking-widest rounded-3xl shadow-xl hover:bg-teal-500 hover:-translate-y-1 transition-all active:scale-95 flex items-center justify-center gap-3">
            <span>Reservar itinerario</span>
            <ArrowRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};