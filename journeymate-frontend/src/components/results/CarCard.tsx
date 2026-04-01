import { Users, Briefcase, Settings2, ExternalLink, Building2, ShieldCheck, Fuel } from 'lucide-react';

export const CarCard = ({ car }: any) => {
  // Función para formatear el precio con la moneda que viene del DTO
  const formatPrice = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: car.currency || 'EUR', // Usamos car.currency del DTO
      minimumFractionDigits: 2,
    }).format(value);
  };

  // El botón ahora no hace nada (e.preventDefault())
  const handleRentClick = (e: React.MouseEvent) => {
    e.preventDefault();
    // Aquí podrías abrir un modal o mostrar un mensaje si quisieras en el futuro
    console.log("Clic en alquilar coche:", car.carName);
  };

  return (
    <div className="bg-white/90 backdrop-blur-md rounded-[2.5rem] overflow-hidden border border-white/20 shadow-xl hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 group flex flex-col md:flex-row h-full md:h-60">
      
      {/* --- SECCIÓN IZQUIERDA: IMAGEN Y PROVEEDOR --- */}
      <div className="w-full md:w-2/5 bg-gray-50 flex items-center justify-center p-6 relative overflow-hidden border-r border-gray-100">
        <div className="absolute inset-0 bg-gradient-to-br from-teal-500/5 to-transparent z-0" />
        
        {car.imageUrl ? (
          <img 
            src={car.imageUrl} // car.imageUrl del DTO
            alt={car.carName} 
            className="w-full h-auto max-h-44 object-contain z-10 group-hover:scale-105 transition-transform duration-500 mix-blend-multiply p-2" 
          />
        ) : (
          <div className="w-full h-44 flex items-center justify-center bg-teal-100/50 rounded-2xl z-10 text-teal-600">
            <Building2 size={48} strokeWidth={1} />
          </div>
        )}
        
        {/* Nombre del Proveedor (car.vendorName del DTO) */}
        <div className="absolute top-4 left-4 bg-white/90 backdrop-blur-md px-4 py-2 rounded-full shadow-sm z-10 border border-gray-100 flex items-center gap-2">
          <Building2 size={12} className="text-teal-600" />
          <span className="text-[10px] font-black text-teal-950 uppercase tracking-widest">
            {car.vendorName || 'Proveedor'}
          </span>
        </div>
      </div>

      {/* --- SECCIÓN DERECHA: INFORMACIÓN Y PRECIO --- */}
      <div className="w-full md:w-3/5 p-8 flex flex-col justify-between text-left">
        <div>
          {/* Nombre del Coche (car.carName del DTO) */}
          <h3 className="text-2xl font-black text-teal-950 uppercase tracking-tighter mb-5 leading-none italic">
            {car.carName}
          </h3>
          
          {/* GRID DE CARACTERÍSTICAS TÉCNICAS (USANDO TODO EL DTO) */}
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-x-6 gap-y-3 mb-6">
            
            {/* Asientos (car.seats) */}
            <div className="flex items-center gap-2.5 text-teal-800/70">
              <Users size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950">
                {car.seats || 5} <span className="font-medium text-teal-800/60">Plazas</span>
              </span>
            </div>

            {/* Maletas (car.bags) */}
            <div className="flex items-center gap-2.5 text-teal-800/70">
              <Briefcase size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950">
                {car.bags || 2} <span className="font-medium text-teal-800/60">Maletas</span>
              </span>
            </div>

            {/* Transmisión (car.transmission) */}
            <div className="flex items-center gap-2.5 text-teal-800/70 col-span-2 sm:col-span-1">
              <Settings2 size={16} className="text-teal-500" />
              <span className="text-xs font-bold text-teal-950 uppercase tracking-tight">
                {car.transmission === 'Automatic' ? 'Automático' : 'Manual'}
              </span>
            </div>

            {/* Extras visuales (puedes hardcodear estos si la API no los da) */}
            <div className="flex items-center gap-2.5 text-teal-800/70">
              <Fuel size={16} className="text-teal-500" />
              <span className="text-xs font-medium text-teal-800/60">Combustible Lleno</span>
            </div>
            <div className="flex items-center gap-2.5 text-teal-800/70">
              <ShieldCheck size={16} className="text-teal-500" />
              <span className="text-xs font-medium text-teal-800/60">Seguro Básico</span>
            </div>
          </div>
        </div>

        {/* --- FOOTER: PRECIO Y BOTÓN REDUCIDO --- */}
        <div className="flex items-end justify-between mt-auto pt-6 border-t border-gray-100">
          <div>
            <p className="text-[9px] font-black text-gray-400 uppercase tracking-[0.2em] mb-1.5">
              Precio total (tasas incl.)
            </p>
            {/* Precio (car.price del DTO) */}
            <p className="text-4xl font-black text-teal-600 tracking-tighter leading-none">
              {car.price ? formatPrice(car.price) : "---"}
            </p>
          </div>
          
          {/* BOTÓN MÁS PEQUEÑO Y DESHABILITADO VISUALMENTE */}
          <button 
            onClick={handleRentClick}
            className="bg-teal-900 hover:bg-teal-700 text-white px-6 py-3.5 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all flex items-center gap-2 shadow-lg shadow-teal-900/10 active:scale-95 group-hover:shadow-teal-900/20"
          >
            Alquilar <ExternalLink size={12} className="opacity-60" />
          </button>
        </div>
      </div>

    </div>
  );
};