import { 
  MapPin, 
  Calendar, 
  Users, 
  Bed, 
  Plane, 
  Globe, 
  Ship, 
  Car, 
  Clock, 
  Briefcase 
} from 'lucide-react';
import { SearchInput } from "./SearchInput";
import { SearchCounter } from "./SearchCounter";

export const SearchForm = ({ activeSection, searchData, handleChange }: any) => {

  switch (activeSection) {

    // 🏨 ALOJAMIENTO (5 Inputs + Botón Buscar = 6)
    case 'alojamiento':
      return (
        <>
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="¿A dónde vas?" 
            val={searchData.destination} 
            onChange={(v: any) => handleChange('destination', v)} 
          />
          <SearchInput 
            label="Entrada" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Salida" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v: any) => handleChange('endDate', v)} 
          />
          <SearchCounter 
            label="Adultos" 
            icon={<Users size={18} />} 
            val={searchData.adults} 
            min={1} 
            onChange={(v: string) => handleChange('adults', v)} 
          />
          <SearchCounter 
            label="Habitaciones" 
            icon={<Bed size={18} />} 
            val={searchData.roomQty} 
            min={1} 
            onChange={(v: string) => handleChange('roomQty', v)} 
          />
        </>
      );

    // ✈️ VUELOS (5 Inputs + Botón Buscar = 6)
    case 'vuelos':
      return (
        <>
          <SearchInput 
            label="Origen" 
            icon={<Plane size={18} />} 
            placeholder="MAD" 
            val={searchData.origin} 
            onChange={(v: any) => handleChange('origin', v)} 
          />
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="JFK" 
            val={searchData.destination} 
            onChange={(v: any) => handleChange('destination', v)} 
          />
          <SearchInput 
            label="Fecha" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchCounter 
            label="Pasajeros" 
            icon={<Users size={18} />} 
            val={searchData.adults} 
            min={1} 
            onChange={(v: string) => handleChange('adults', v)} 
          />
          <SearchInput 
            label="Clase" 
            icon={<Briefcase size={18} />} 
            val={searchData.cabinClass} 
            onChange={(v: any) => handleChange('cabinClass', v)} 
          />
        </>
      );

    // 🚗 COCHES (4 Inputs + 1 Empty + Botón Buscar = 6)
    case 'coches':
      return (
        <>
          <SearchInput 
            label="Recogida" 
            icon={<Car size={18} />} 
            placeholder="Lugar de entrega" 
            val={searchData.origin} 
            onChange={(v: any) => handleChange('origin', v)} 
          />
          <SearchInput 
            label="Fecha" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Hora" 
            icon={<Clock size={18} />} 
            type="time" 
            val={searchData.pickupTime} 
            onChange={(v: any) => handleChange('pickupTime', v)} 
          />
          <SearchInput 
            label="Devolución" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v: any) => handleChange('endDate', v)} 
          />
          {/* Espaciador para mantener el botón de buscar al final */}
          <div className="hidden lg:block search-input-field opacity-0" />
        </>
      );

    // 🎟 ACTIVIDADES (3 Inputs + 2 Empty + Botón Buscar = 6)
    case 'actividades':
      return (
        <>
          <SearchInput 
            label="Ciudad" 
            icon={<MapPin size={18} />} 
            placeholder="¿Qué quieres hacer?" 
            val={searchData.destination} 
            onChange={(v: any) => handleChange('destination', v)} 
          />
          <SearchInput 
            label="Desde" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Hasta" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v: any) => handleChange('endDate', v)} 
          />
          <div className="hidden lg:block search-input-field opacity-0" />
          <div className="hidden lg:block search-input-field opacity-0" />
        </>
      );

    // 🚢 CRUCEROS (4 Inputs + 1 Empty + Botón Buscar = 6)
    case 'cruceros':
      return (
        <>
          <SearchInput 
            label="Zona" 
            icon={<Globe size={18} />} 
            placeholder="Mediterráneo..." 
            val={searchData.destination} 
            onChange={(v: any) => handleChange('destination', v)} 
          />
          <SearchInput 
            label="Puerto" 
            icon={<Ship size={18} />} 
            placeholder="Barcelona..." 
            val={searchData.origin} 
            onChange={(v: any) => handleChange('origin', v)} 
          />
          <SearchInput 
            label="Salida" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Regreso" 
            icon={<Calendar size={18} />} 
            type="date" 
            val={searchData.endDate} 
            onChange={(v: any) => handleChange('endDate', v)} 
          />
          <div className="hidden lg:block search-input-field opacity-0" />
        </>
      );

    default:
      return null;
  }
};