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

  /**
   * Función auxiliar para limpiar el texto según requiere tu API
   */
  const handleLocationChange = (key: string, value: string) => {
    // Reemplaza todos los espacios por guiones bajos
    const formattedValue = value.replace(/ /g, '_');
    handleChange(key, formattedValue);
  };

  switch (activeSection) {

    // 🏨 ALOJAMIENTO
    case 'alojamiento':
      return (
        <>
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="¿A dónde vas?" 
            val={searchData.destination?.replace(/_/g, ' ')} // Mostramos espacios al usuario
            onChange={(v: any) => handleLocationChange('destination', v)} 
          />
          {/* ... resto de campos igual ... */}
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

    // ✈️ VUELOS
    case 'vuelos':
      return (
        <>
          <SearchInput 
            label="Origen" 
            icon={<Plane size={18} />} 
            placeholder="MAD" 
            val={searchData.origin?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLocationChange('origin', v)} 
          />
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="JFK" 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLocationChange('destination', v)} 
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

    // 🎟 ACTIVIDADES
    case 'actividades':
      return (
        <>
          <SearchInput 
            label="Ciudad" 
            icon={<MapPin size={18} />} 
            placeholder="¿Qué quieres hacer?" 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLocationChange('destination', v)} 
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

    // 🚢 CRUCEROS
    case 'cruceros':
      return (
        <>
          <SearchInput 
            label="Zona" 
            icon={<Globe size={18} />} 
            placeholder="Mediterráneo..." 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLocationChange('destination', v)} 
          />
          <SearchInput 
            label="Puerto" 
            icon={<Ship size={18} />} 
            placeholder="Barcelona..." 
            val={searchData.origin?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLocationChange('origin', v)} 
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

    // ... Caso de coches queda igual (suelen ser IDs o códigos IATA), pero puedes aplicarlo si lo necesitas
    default:
      return null;
  }
};