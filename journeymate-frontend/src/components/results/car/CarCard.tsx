// src/components/results/CarCard.tsx
import { Users, Briefcase, Settings2, ExternalLink, Building2, ShieldCheck, Fuel } from 'lucide-react';

interface CarCardProps {
  car: any;
  onRent: (car: any) => void; // ✅ NUEVO: abre el modal de reserva
  searchData?: any;            // para calcular días
}

export const CarCard = ({ car, onRent, searchData }: CarCardProps) => {

  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: car.currency || 'EUR',
      minimumFractionDigits: 2,
    }).format(value);
  };

  // Calcular días de alquiler
  const days = (() => {
    if (!searchData?.startDate || !searchData?.endDate) return 1;
    const start = new Date(searchData.startDate);
    const end   = new Date(searchData.endDate);
    return Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)));
  })();

  return (
    <div className="bg-white/90 backdrop-blur-md rounded-[2.5rem] overflow-hidden border border-white/20 shadow-xl hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 group flex flex-col md:flex-row h-full md:h-60">

      {/* IMAGEN Y PROVEEDOR */}
      <div className="w-full md:w-2/5 bg-gray-50 flex items-center justify-center p-6 relative overflow-hidden border-r border-gray-100">
        <div className="absolute inset-0 bg-gradient-to-br from-teal-500/5 to-transparent z-0" />
        {car.imageUrl ? (
          <img
            src={car.imageUrl}
            alt={car.carName}
            className="w-full h-auto max-h-44 object-contain z-10 group-hover:scale-105 transition-transform duration-500 mix-blend-multiply p-2"
          />
        ) : (
          <div className="w-full h-44 flex items-center justify-center bg-teal-100/50 rounded-2xl z-10 text-teal-600">
            <Building2 size={48} strokeWidth={1} />
          </div>
        )}
        <div className="absolute top-4 left-4 bg-white/90 backdrop-blur-md px-4 py-2 rounded-full shadow-sm z-10 border border-gray-100 flex items-center gap-2">
          <Building2 size={12} className="text-teal-600" />
          <span className="text-[10px] font-black text-teal-950 uppercase tracking-widest">
            {car.vendorName || 'Proveedor'}
          </span>
        </div>
      </div>

      {/* INFO Y PRECIO */}
      <div className="w-full md:w-3/5 p-8 flex flex-col justify-between text-left">
        <div>
          <h3 className="text-2xl font-black text-teal-950 uppercase tracking-tighter mb-5 leading-none italic">
            {car.carName}
          </h3>
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-x-6 gap-y-3 mb-6">
            <div className="flex items-center gap-2.5">
              <Users size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950">
                {car.seats || 5} <span className="font-medium text-teal-800/60">Plazas</span>
              </span>
            </div>
            <div className="flex items-center gap-2.5">
              <Briefcase size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950">
                {car.bags || 2} <span className="font-medium text-teal-800/60">Maletas</span>
              </span>
            </div>
            <div className="flex items-center gap-2.5 col-span-2 sm:col-span-1">
              <Settings2 size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950 uppercase tracking-tight">
                {car.transmission === 'Automatic' ? 'Automático' : 'Manual'}
              </span>
            </div>
            <div className="flex items-center gap-2.5">
              <Fuel size={16} className="text-teal-500" />
              <span className="text-xs font-medium text-teal-800/60">Combustible Lleno</span>
            </div>
            <div className="flex items-center gap-2.5">
              <ShieldCheck size={16} className="text-teal-500" />
              <span className="text-xs font-medium text-teal-800/60">Seguro Básico</span>
            </div>
          </div>
        </div>

        <div className="flex items-end justify-between mt-auto pt-6 border-t border-gray-100">
          <div>
            <p className="text-[9px] font-black text-gray-400 uppercase tracking-[0.2em] mb-1.5">
              {days} {days === 1 ? 'día' : 'días'} · Tasas incluidas
            </p>
            <p className="text-4xl font-black text-teal-600 tracking-tighter leading-none">
              {car.price ? formatPrice(car.price) : "---"}
            </p>
          </div>

          {/* ✅ Botón que abre el modal en vez de no hacer nada */}
          <button
            onClick={() => onRent(car)}
            className="bg-teal-900 hover:bg-teal-700 text-white px-6 py-3.5 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all flex items-center gap-2 shadow-lg active:scale-95"
          >
            Alquilar <ExternalLink size={12} className="opacity-60" />
          </button>
        </div>
      </div>
    </div>
  );
};