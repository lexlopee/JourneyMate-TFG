import { useState, useEffect } from 'react';
import {
  X, Clock, Plane, ShieldCheck, ArrowRight,
  Briefcase, Leaf, Ticket, Calendar, ArrowLeftRight, Users, CheckCircle2
} from 'lucide-react';
import { formatCurrency } from '../../utils/dateUtils';
import { PostBookingModal } from '../payment/PostBookingModal';

export const FlightDetailsModal = ({ isOpen, onClose, details }: any) => {
  const [loginError,    setLoginError]    = useState('');
  const [isReserving,   setIsReserving]   = useState(false);
  const [isBooked,      setIsBooked]      = useState(false);
  const [postModalOpen, setPostModalOpen] = useState(false);
  const [reservaId,     setReservaId]     = useState<number | null>(null);

  useEffect(() => {
    if (isOpen) { setLoginError(''); setIsReserving(false); setIsBooked(false); setPostModalOpen(false); setReservaId(null); }
  }, [isOpen, details?.data?.id]);

  if (!isOpen || !details) return null;

  const { data } = details;
  const isLogged = typeof window !== 'undefined' && !!localStorage.getItem('token');

  const formatTime = (d: string) => d ? new Date(d).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }) : '--:--';
  const formatDate = (d: string) => d ? new Date(d).toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' }) : '';
  const calcDuration = (s: number) => { if (!s) return '0h 0min'; const m = Math.floor(s/60); return `${Math.floor(m/60)}h ${m%60}min`; };

  const handleReserve = async () => {
    if (isReserving || isBooked) return;
    setIsReserving(true); setLoginError('');
    try {
      const token = localStorage.getItem('token');
      const idUsuario = localStorage.getItem('idUsuario');
      if (!token || !idUsuario) { setLoginError('Debes iniciar sesión para reservar.'); return; }

      const primerSeg = data.segments?.[0];
      const segReg    = data.segments?.find((_: any, i: number) => i > 0);
      if (!primerSeg) { setLoginError('No se pudieron obtener los datos del vuelo.'); return; }

      const precioTotal = data.priceBreakdown?.total?.units;
      if (precioTotal === undefined) { setLoginError('No se pudo obtener el precio.'); return; }

      const aerolinea   = primerSeg.legs?.[0]?.carriersData?.[0]?.name ?? 'Aerolínea desconocida';
      const isRoundTrip = data.tripType === 'ROUNDTRIP';
      const fechaSalida = primerSeg.departureTime?.split('T')[0];
      const fechaReg    = isRoundTrip && segReg ? segReg.departureTime?.split('T')[0] : null;
      if (!fechaSalida || (isRoundTrip && !fechaReg)) { setLoginError('Error en las fechas del itinerario.'); return; }

      const body = {
        idUsuario: Number(idUsuario), idTipoReserva: 4, idEstado: 1, precioTotal,
        servicio: {
          tipo: 'VUELO',
          nombre: `${aerolinea} · ${primerSeg.departureAirport.code} ${isRoundTrip?'⇄':'→'} ${primerSeg.arrivalAirport.code}`,
          precioBase: precioTotal, ciudad: primerSeg.departureAirport.cityName ?? null,
          compania: aerolinea, origen: primerSeg.departureAirport.code,
          destino: primerSeg.arrivalAirport.code, fechaSalida, fechaRegreso: fechaReg,
        },
      };
      const response = await fetch('http://localhost:8080/api/v1/reservas/completa', {
        method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify(body),
      });
      if (!response.ok) throw new Error(await response.text() || 'Error en el servidor');
      const rd = await response.json();
      setReservaId(rd.idReserva);
      setIsBooked(true);
      setPostModalOpen(true);
    } catch (e: any) { setLoginError(e.message || 'Error al realizar la reserva'); }
    finally { setIsReserving(false); }
  };

  const handleContinueShopping = () => { setPostModalOpen(false); onClose(); };

  const precioTotal = data.priceBreakdown?.total?.units ?? 0;
  const primerSeg   = data.segments?.[0];
  const aerolinea   = primerSeg?.legs?.[0]?.carriersData?.[0]?.name ?? 'Vuelo';
  const origen      = primerSeg?.departureAirport?.code ?? '';
  const destino     = primerSeg?.arrivalAirport?.code ?? '';

  return (
    <>
      <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/60 backdrop-blur-md">
        <div className="bg-white w-full max-w-3xl rounded-[3.5rem] shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300 border border-white/20">

          {/* Header */}
          <div className="bg-gradient-to-br from-teal-900 to-teal-700 p-8 text-white relative">
            <button onClick={onClose} className="absolute top-8 right-8 p-2 hover:bg-white/20 rounded-full transition-colors"><X size={20}/></button>
            <div className="flex items-center gap-4 mb-4">
              <div className="bg-white/10 p-3 rounded-2xl backdrop-blur-md border border-white/10"><Plane size={24} className="text-teal-200"/></div>
              <div>
                <h2 className="text-2xl font-black uppercase tracking-tighter">Itinerario de Vuelo</h2>
                <div className="flex gap-2 mt-2">
                  <span className="text-[9px] bg-teal-500/30 px-2 py-1 rounded-md font-bold uppercase tracking-widest text-teal-100 flex items-center gap-1">
                    {data.tripType==='ROUNDTRIP'?<ArrowLeftRight size={10}/>:<ArrowRight size={10}/>}
                    {data.tripType==='ROUNDTRIP'?'Ida y Vuelta':'Solo Ida'}
                  </span>
                  {data.travellers&&<span className="text-[9px] bg-teal-500/30 px-2 py-1 rounded-md font-bold uppercase tracking-widest text-teal-100 flex items-center gap-1"><Users size={10}/>{data.travellers.length} Pasajero{data.travellers.length>1?'s':''}</span>}
                </div>
              </div>
            </div>
          </div>

          <div className="p-8 max-h-[65vh] overflow-y-auto custom-scrollbar bg-gray-50/50">
            {data.segments?.map((segment: any, sIdx: number) => (
              <div key={sIdx} className="mb-8 last:mb-0 bg-white rounded-[2.5rem] p-8 border border-teal-100/50 shadow-sm">
                <div className="flex justify-between items-center mb-8">
                  <div className="flex items-center gap-2"><Calendar size={14} className="text-teal-500"/><span className="text-xs font-black text-teal-900 uppercase">{formatDate(segment.departureTime)}</span></div>
                  <p className="text-[10px] font-black text-teal-600 uppercase tracking-widest">{sIdx===0?'Vuelo de Salida':'Vuelo de Regreso'}</p>
                </div>
                <div className="flex items-center justify-between mb-8">
                  <div className="text-center min-w-[80px]">
                    <p className="text-4xl font-black text-teal-950 tracking-tighter leading-none">{segment.departureAirport?.code}</p>
                    <p className="text-[10px] text-gray-400 font-bold uppercase mt-1 truncate max-w-[100px]">{segment.departureAirport?.cityName}</p>
                    <p className="text-sm font-black text-teal-600 mt-2">{formatTime(segment.departureTime)}</p>
                  </div>
                  <div className="flex-grow px-4 flex flex-col items-center">
                    <div className="flex items-center gap-2 w-full"><div className="h-[2px] flex-grow bg-gradient-to-r from-transparent to-teal-100"/><Plane size={18} className="text-teal-200"/><div className="h-[2px] flex-grow bg-gradient-to-l from-transparent to-teal-100"/></div>
                    <div className="mt-2 flex items-center gap-1.5 bg-teal-50 px-3 py-1 rounded-full border border-teal-100/50"><Clock size={12} className="text-teal-600"/><span className="text-[10px] font-black text-teal-700 uppercase">{calcDuration(segment.totalTime)}</span></div>
                  </div>
                  <div className="text-center min-w-[80px]">
                    <p className="text-4xl font-black text-teal-950 tracking-tighter leading-none">{segment.arrivalAirport?.code}</p>
                    <p className="text-[10px] text-gray-400 font-bold uppercase mt-1 truncate max-w-[100px]">{segment.arrivalAirport?.cityName}</p>
                    <p className="text-sm font-black text-teal-600 mt-2">{formatTime(segment.arrivalTime)}</p>
                  </div>
                </div>
                <div className="space-y-4 pt-6 border-t border-gray-100">
                  {segment.legs?.map((leg: any, lIdx: number) => (
                    <div key={lIdx} className="flex items-center justify-between bg-gray-50/70 p-4 rounded-2xl border border-gray-100">
                      <div className="flex items-center gap-4">
                        <div className="w-10 h-10 bg-white rounded-xl p-1 shadow-sm flex items-center justify-center border border-gray-100"><img src={leg.carriersData?.[0]?.logo} alt="Airline" className="w-full h-full object-contain"/></div>
                        <div>
                          <div className="flex items-center gap-2"><p className="text-[11px] font-black text-teal-900 uppercase">{leg.carriersData?.[0]?.name}</p><span className="text-[9px] bg-teal-100 text-teal-700 px-1.5 py-0.5 rounded font-bold">#{leg.flightInfo?.flightNumber}</span></div>
                          <p className="text-[9px] text-gray-500 font-bold uppercase tracking-tight">{formatTime(leg.departureTime)} {leg.departureAirport?.code} → {formatTime(leg.arrivalTime)} {leg.arrivalAirport?.code}</p>
                        </div>
                      </div>
                      <div className="text-right flex flex-col items-end gap-1">
                        <span className="text-[9px] font-black text-teal-600 uppercase bg-white px-2 py-1 rounded-lg border border-teal-100">{leg.cabinClass}</span>
                        {leg.flightInfo?.planeType&&<span className="text-[8px] font-bold text-gray-500 uppercase tracking-widest">Avión: {leg.flightInfo.planeType}</span>}
                      </div>
                    </div>
                  ))}
                </div>
                <div className="grid grid-cols-2 gap-3 mt-6">
                  <div className="flex items-center gap-3 bg-teal-50/50 p-3 rounded-2xl border border-teal-100/20">
                    <div className="p-2 bg-white rounded-lg text-teal-600 shadow-sm"><Briefcase size={14}/></div>
                    <div><p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Mano</p><p className="text-[10px] font-black text-teal-900">{segment.travellerCabinLuggage?.[0]?.luggageAllowance?.maxPiece>0?`${segment.travellerCabinLuggage[0].luggageAllowance.maxPiece} Pieza(s)`:'No incluido'}{segment.travellerCabinLuggage?.[0]?.luggageAllowance?.maxWeightPerPiece>0&&` (${segment.travellerCabinLuggage[0].luggageAllowance.maxWeightPerPiece} ${segment.travellerCabinLuggage[0].luggageAllowance.massUnit})`}</p></div>
                  </div>
                  <div className="flex items-center gap-3 bg-teal-50/50 p-3 rounded-2xl border border-teal-100/20">
                    <div className="p-2 bg-white rounded-lg text-teal-600 shadow-sm"><ShieldCheck size={14}/></div>
                    <div><p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Facturado</p><p className="text-[10px] font-black text-teal-900">{segment.travellerCheckedLuggage?.[0]?.luggageAllowance?.maxPiece>0?`${segment.travellerCheckedLuggage[0].luggageAllowance.maxPiece} Pieza(s)`:'No incluido'}{segment.travellerCheckedLuggage?.[0]?.luggageAllowance?.maxWeightPerPiece>0&&` (${segment.travellerCheckedLuggage[0].luggageAllowance.maxWeightPerPiece} ${segment.travellerCheckedLuggage[0].luggageAllowance.massUnit})`}</p></div>
                  </div>
                </div>
              </div>
            ))}

            {data.carbonEmissions?.footprintForOffer?.quantity&&(
              <div className="mb-8 flex items-center justify-between bg-green-50 p-5 rounded-[2rem] border border-green-100">
                <div className="flex items-center gap-3"><div className="bg-green-600 p-2 rounded-xl text-white"><Leaf size={16}/></div><div><p className="text-[10px] font-black text-green-800 uppercase tracking-tight">Vuelo más sostenible</p><p className="text-[9px] text-green-600 font-bold uppercase">Huella de CO2 estimada</p></div></div>
                <p className="text-sm font-black text-green-700">{data.carbonEmissions.footprintForOffer.quantity} {data.carbonEmissions.footprintForOffer.unit||'kg'}</p>
              </div>
            )}

            <div className="mt-8 bg-teal-950 rounded-[3rem] p-10 text-white relative overflow-hidden shadow-2xl shadow-teal-950/40">
              <div className="absolute top-0 right-0 p-8 opacity-10"><Ticket size={120} className="rotate-12"/></div>
              <div className="flex flex-col md:flex-row justify-between items-end gap-6 relative z-10">
                <div className="w-full md:w-auto">
                  <p className="text-teal-400 text-[10px] font-black uppercase tracking-[0.2em] mb-4">Detalle del Pago</p>
                  <div className="space-y-2">
                    <div className="flex justify-between items-center w-full min-w-[220px]"><span className="text-[11px] font-bold uppercase opacity-60">Tarifa Base:</span><span className="text-[11px] font-black">{formatCurrency(data.priceBreakdown?.baseFare?.units||0,data.priceBreakdown?.baseFare?.currencyCode)}</span></div>
                    {data.priceBreakdown?.fee?.units>0&&<div className="flex justify-between items-center w-full border-t border-white/5 pt-2"><span className="text-[11px] font-bold uppercase opacity-60">Cargos/Fees:</span><span className="text-[11px] font-black">{formatCurrency(data.priceBreakdown.fee.units,data.priceBreakdown.fee.currencyCode)}</span></div>}
                    <div className="flex justify-between items-center w-full border-t border-white/5 pt-2"><span className="text-[11px] font-bold uppercase opacity-60">Impuestos:</span><span className="text-[11px] font-black">{formatCurrency(data.priceBreakdown?.tax?.units||0,data.priceBreakdown?.tax?.currencyCode)}</span></div>
                    {data.priceBreakdown?.discount?.units>0&&<div className="flex justify-between items-center w-full border-t border-white/5 pt-2 text-green-400"><span className="text-[11px] font-bold uppercase opacity-80">Descuento:</span><span className="text-[11px] font-black">-{formatCurrency(data.priceBreakdown.discount.units,data.priceBreakdown.discount.currencyCode)}</span></div>}
                  </div>
                </div>
                <div className="text-right w-full md:w-auto mt-6 md:mt-0"><p className="text-teal-400 text-[10px] font-black uppercase tracking-widest mb-1">Precio Final</p><h4 className="text-6xl md:text-7xl font-black tracking-tighter leading-none">{formatCurrency(data.priceBreakdown?.total?.units||0,data.priceBreakdown?.total?.currencyCode)}</h4></div>
              </div>
            </div>
          </div>

          {loginError&&<div className="px-8 pt-6 pb-2 bg-white"><div className="bg-red-50 text-red-700 text-[11px] font-black uppercase tracking-widest px-6 py-4 rounded-2xl text-center border border-red-100">{loginError}{!isLogged&&<div className="mt-2"><a href="/login" className="underline text-red-900 hover:text-red-500 transition-colors">Iniciar sesión aquí</a></div>}</div></div>}

          <div className="p-8 bg-white border-t border-gray-100 flex flex-col md:flex-row gap-4">
            <button onClick={onClose} className="flex-1 py-5 text-[11px] font-black uppercase tracking-widest text-gray-400 hover:text-teal-900 transition-all">Cerrar</button>
            <button onClick={handleReserve} disabled={isReserving||isBooked}
              className={`flex-[2] py-5 text-white text-[11px] font-black uppercase tracking-widest rounded-3xl shadow-xl transition-all flex items-center justify-center gap-3 ${isBooked?'bg-green-600 cursor-default shadow-none opacity-90':'bg-teal-600 hover:bg-teal-500 hover:-translate-y-1 active:scale-95 disabled:opacity-50'}`}>
              {isReserving?'Reservando...':(isBooked?<><CheckCircle2 size={16}/>¡Reserva Creada!</>:<><span>Reservar Itinerario</span>{data.tripType==='ROUNDTRIP'?<ArrowLeftRight size={16}/>:<ArrowRight size={16}/>}</>)}
            </button>
          </div>
        </div>
      </div>

      {reservaId&&(
        <PostBookingModal isOpen={postModalOpen} onClose={handleContinueShopping}
          reservaId={reservaId} precio={precioTotal}
          descripcion={`${aerolinea} · ${origen} → ${destino}`}
        />
      )}
    </>
  );
};