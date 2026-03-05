import { MapPin, Calendar, Users, Globe, Ship, Plane, Car, Clock } from 'lucide-react';

const SearchInput = ({ label, icon, placeholder, val, type = "text", onChange }: any) => (
  <div className="search-input bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all group">
    <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">{label}</span>
    <div className="flex items-center gap-2 text-teal-900">
      <span className="text-teal-500 group-hover:rotate-12 transition-transform">{icon}</span>
      <input
        type={type}
        value={val}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="bg-transparent border-none outline-none w-full text-[11px] font-bold placeholder:text-teal-900/20"
      />
    </div>
  </div>
);

export const SearchForm = ({ activeSection, searchData, handleChange }: any) => {
  switch (activeSection) {
    case 'alojamiento':
      return (
        <>
          <SearchInput label="Destino" icon={<MapPin size={18} />} placeholder="Ciudad..." val={searchData.destination} onChange={(v:any) => handleChange('destination', v)} />
          <SearchInput label="Entrada" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v:any) => handleChange('startDate', v)} />
          <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v:any) => handleChange('endDate', v)} />
          <SearchInput label="Adultos" icon={<Users size={18} />} val={searchData.adults} onChange={(v:any) => handleChange('adults', v)} />
        </>
      );
    case 'vuelos':
      return (
        <>
          <SearchInput label="Origen" icon={<Plane size={18} />} placeholder="MAD..." val={searchData.origin} onChange={(v:any) => handleChange('origin', v)} />
          <SearchInput label="Destino" icon={<MapPin size={18} />} placeholder="JFK..." val={searchData.destination} onChange={(v:any) => handleChange('destination', v)} />
          <SearchInput label="Fecha" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v:any) => handleChange('startDate', v)} />
          <SearchInput label="Clase" icon={<Users size={18} />} val={searchData.cabinClass} onChange={(v:any) => handleChange('cabinClass', v)} />
        </>
      );
    case 'coches':
      return (
        <>
          <SearchInput label="Recogida" icon={<Car size={18} />} placeholder="Aeropuerto..." val={searchData.origin} onChange={(v:any) => handleChange('origin', v)} />
          <SearchInput label="Fecha" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v:any) => handleChange('startDate', v)} />
          <SearchInput label="Hora" icon={<Clock size={18} />} type="time" val={searchData.pickupTime} onChange={(v:any) => handleChange('pickupTime', v)} />
          <SearchInput label="Devolución" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v:any) => handleChange('endDate', v)} />
        </>
      );
    case 'actividades':
      return (
        <>
          <SearchInput label="Ciudad" icon={<MapPin size={18} />} placeholder="¿A dónde vas?" val={searchData.destination} onChange={(v:any) => handleChange('destination', v)} />
          <SearchInput label="Desde" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v:any) => handleChange('startDate', v)} />
          <SearchInput label="Hasta" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v:any) => handleChange('endDate', v)} />
          <div className="hidden md:block" />
        </>
      );
    case 'cruceros':
      return (
        <>
          <SearchInput label="Zona" icon={<Globe size={18} />} placeholder="CARIB..." val={searchData.destination} onChange={(v:any) => handleChange('destination', v)} />
          <SearchInput label="Puerto" icon={<Ship size={18} />} placeholder="BCN..." val={searchData.origin} onChange={(v:any) => handleChange('origin', v)} />
          <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v:any) => handleChange('startDate', v)} />
          <SearchInput label="Regreso" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v:any) => handleChange('endDate', v)} />
        </>
      );
    default: return null;
  }
};