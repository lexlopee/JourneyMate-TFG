import { 
  MapPin, 
  Calendar, 
  Users, 
  Bed, 
  PlaneLanding,
  PlaneTakeoff,
  Briefcase,
  Globe,
  Ship
} from 'lucide-react';
import { SearchInput } from "./SearchInput";
import { SearchCounter } from "./SearchCounter";
import { AutocompleteInput } from "./AutoCompleteInput";
import { SearchSelect } from "./SearchSelect";

export const SearchForm = ({ activeSection, searchData, handleChange }: any) => {

  const handleLegacyLocationChange = (key: string, value: string) => {
    const formattedValue = value.replace(/ /g, '_');
    handleChange(key, formattedValue);
    handleChange(key + 'Text', value); 
  };

  // Fecha de hoy para deshabilitar días pasados en el calendario
  const today = new Date().toISOString().split('T')[0];

  switch (activeSection) {

    case 'vuelos':
      return (
        <>
          <div className="md:col-span-1">
            <AutocompleteInput 
              label="Origen" 
              icon={<PlaneTakeoff size={18} />} 
              placeholder="Ej: Madrid" 
              value={searchData.originText} 
              onSelect={(dest: any) => {
                handleChange('fromId', dest.id);
                handleChange('originText', dest.name);
              }} 
            />
          </div>
          <div className="md:col-span-1">
            <AutocompleteInput 
              label="Destino" 
              icon={<PlaneLanding size={18} />} 
              placeholder="Ej: Barcelona" 
              value={searchData.destinationText} 
              onSelect={(dest: any) => {
                handleChange('toId', dest.id);
                handleChange('destinationText', dest.name);
              }} 
            />
          </div>
          <SearchInput 
            label="Salida" 
            icon={<Calendar size={18} />} 
            type="date" 
            min={today}
            val={searchData.startDate} 
            onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Regreso" 
            icon={<Calendar size={18} />} 
            type="date" 
            min={searchData.startDate || today}
            val={searchData.endDate} 
            onChange={(v: any) => handleChange('endDate', v)} 
          />
          <SearchCounter 
            label="Pasajeros" 
            icon={<Users size={18} />} 
            val={searchData.adults} 
            min={1} 
            onChange={(v: string) => handleChange('adults', v)} 
          />
          <SearchSelect 
            label="Clase" 
            icon={<Briefcase size={18} />} 
            value={searchData.cabinClass} 
            options={[
              { label: 'Económica', value: 'ECONOMY' },
              { label: 'Business', value: 'BUSINESS' },
              { label: 'Primera Clase', value: 'FIRST' }
            ]}
            onChange={(v: string) => handleChange('cabinClass', v)} 
          />
        </>
      );

    case 'alojamiento':
      return (
        <>
          <SearchInput 
            label="Destino" 
            icon={<MapPin size={18} />} 
            placeholder="¿A dónde vas?" 
            val={searchData.destination?.replace(/_/g, ' ')} 
            onChange={(v: any) => handleLegacyLocationChange('destination', v)} 
          />
          <SearchInput 
            label="Entrada" icon={<Calendar size={18} />} type="date" 
            min={today}
            val={searchData.startDate} onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Salida" icon={<Calendar size={18} />} type="date" 
            min={searchData.startDate || today}
            val={searchData.endDate} onChange={(v: any) => handleChange('endDate', v)} 
          />
          <SearchCounter 
            label="Adultos" icon={<Users size={18} />} val={searchData.adults} 
            min={1} onChange={(v: string) => handleChange('adults', v)} 
          />
          <SearchCounter 
            label="Habitaciones" icon={<Bed size={18} />} val={searchData.roomQty} 
            min={1} onChange={(v: string) => handleChange('roomQty', v)} 
          />
        </>
      );

    case 'actividades':
      return (
        <>
          <SearchInput 
            label="Ciudad" 
            icon={<MapPin size={18} />} 
            placeholder="¿Qué quieres hacer?" 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLegacyLocationChange('destination', v)} 
          />
          <SearchInput 
            label="Desde" icon={<Calendar size={18} />} type="date" 
            min={today}
            val={searchData.startDate} onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Hasta" icon={<Calendar size={18} />} type="date" 
            min={searchData.startDate || today}
            val={searchData.endDate} onChange={(v: any) => handleChange('endDate', v)} 
          />
          <div className="hidden lg:block opacity-0" />
          <div className="hidden lg:block opacity-0" />
        </>
      );

    case 'cruceros':
      return (
        <>
          <SearchInput 
            label="Zona" 
            icon={<Globe size={18} />} 
            placeholder="Mediterráneo..." 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLegacyLocationChange('destination', v)} 
          />
          <SearchInput 
            label="Puerto" 
            icon={<Ship size={18} />} 
            placeholder="Barcelona..." 
            val={searchData.origin?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLegacyLocationChange('origin', v)} 
          />
          <SearchInput 
            label="Salida" icon={<Calendar size={18} />} type="date" 
            min={today}
            val={searchData.startDate} onChange={(v: any) => handleChange('startDate', v)} 
          />
          <SearchInput 
            label="Regreso" icon={<Calendar size={18} />} type="date" 
            min={searchData.startDate || today}
            val={searchData.endDate} onChange={(v: any) => handleChange('endDate', v)} 
          />
          <div className="hidden lg:block opacity-0" />
        </>
      );

    default:
      return null;
  }
};