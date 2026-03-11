import { MapPin, Calendar, Users, Globe, Ship, Plane, Car, Clock } from 'lucide-react';
import { SearchInput } from "./SearchInput";

export const SearchForm = ({ activeSection, searchData, handleChange }: any) => {

  switch (activeSection) {

    // 🏨 ALOJAMIENTO
    case 'alojamiento':
      return (
        <>
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="Ciudad..." 
            val={searchData.destination} 
            onChange={(v:any) => handleChange('destination', v)} 
          />

          <SearchInput 
            label="Entrada" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v:any) => handleChange('startDate', v)} 
          />

          <SearchInput 
            label="Salida" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v:any) => handleChange('endDate', v)} 
          />

          <SearchInput 
            label="Adultos" 
            icon={<Users size={18} />} 
            val={searchData.adults} 
            onChange={(v:any) => handleChange('adults', v)} 
          />
        </>
      );

    // ✈️ VUELOS
    case 'vuelos':
      return (
        <>
          <SearchInput 
            label="Origen" 
            icon={<Plane size={18} />} 
            placeholder="MAD..." 
            val={searchData.origin} 
            onChange={(v:any) => handleChange('origin', v)} 
          />

          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="JFK..." 
            val={searchData.destination} 
            onChange={(v:any) => handleChange('destination', v)} 
          />

          <SearchInput 
            label="Fecha" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v:any) => handleChange('startDate', v)} 
          />

          <SearchInput 
            label="Clase" 
            icon={<Users size={18} />} 
            val={searchData.cabinClass} 
            onChange={(v:any) => handleChange('cabinClass', v)} 
          />
        </>
      );

    // 🚗 COCHES
    case 'coches':
      return (
        <>
          <SearchInput 
            label="Recogida" 
            icon={<Car size={18} />} 
            placeholder="Aeropuerto..." 
            val={searchData.origin} 
            onChange={(v:any) => handleChange('origin', v)} 
          />

          <SearchInput 
            label="Fecha" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v:any) => handleChange('startDate', v)} 
          />

          <SearchInput 
            label="Hora" 
            icon={<Clock size={18} />} 
            type="time" 
            val={searchData.pickupTime} 
            onChange={(v:any) => handleChange('pickupTime', v)} 
          />

          <SearchInput 
            label="Devolución" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v:any) => handleChange('endDate', v)} 
          />
        </>
      );

    // 🎟 ACTIVIDADES
    case 'actividades':
      return (
        <>
          <SearchInput 
            label="Ciudad" 
            icon={<MapPin size={18} />} 
            placeholder="¿A dónde vas?" 
            val={searchData.destination} 
            onChange={(v:any) => handleChange('destination', v)} 
          />

          <SearchInput 
            label="Desde" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v:any) => handleChange('startDate', v)} 
          />

          <SearchInput 
            label="Hasta" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v:any) => handleChange('endDate', v)} 
          />

          {/* Espaciador para mantener el grid */}
          <div className="hidden md:block search-input-field opacity-0" />
        </>
      );

    // 🚢 CRUCEROS
    case 'cruceros':
      return (
        <>
          <SearchInput 
            label="Zona" 
            icon={<Globe size={18} />} 
            placeholder="CARIB..." 
            val={searchData.destination} 
            onChange={(v:any) => handleChange('destination', v)} 
          />

          <SearchInput 
            label="Puerto" 
            icon={<Ship size={18} />} 
            placeholder="BCN..." 
            val={searchData.origin} 
            onChange={(v:any) => handleChange('origin', v)} 
          />

          <SearchInput 
            label="Salida" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v:any) => handleChange('startDate', v)} 
          />

          <SearchInput 
            label="Regreso" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v:any) => handleChange('endDate', v)} 
          />
        </>
      );

    default:
      return null;
  }
};
