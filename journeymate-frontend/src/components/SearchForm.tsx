import { 
  MapPin, 
  Calendar, 
  Users, 
  Bed, 
  PlaneLanding,
  PlaneTakeoff,
  Briefcase,
  Globe,
  Ship,
  Clock,
  Car
} from 'lucide-react';
import { SearchInput } from "./SearchInput";
import { SearchCounter } from "./SearchCounter";
import { AutocompleteInput } from "./AutoCompleteInput";
import { SearchSelect } from "./SearchSelect";
import { CarLocationInput } from './CarLocationInput';
import { ActivityAutocomplete } from './ActivityAutocomplete';

export const SearchForm = ({ activeSection, searchData, handleChange }: any) => {

  const handleLegacyLocationChange = (key: string, value: string) => {
    const formattedValue = value.replace(/ /g, '_');
    handleChange(key, formattedValue);
    handleChange(key + 'Text', value); 
  };

  // Generar opciones de horas cada 30 minutos para los selectores
  const timeOptions = Array.from({ length: 48 }, (_, i) => {
    const h = Math.floor(i / 2).toString().padStart(2, '0');
    const m = (i % 2 === 0 ? '00' : '30');
    return { label: `${h}:${m}`, value: `${h}:${m}` };
  });

  // Fecha de hoy para deshabilitar días pasados
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
            placeholder="Opcional (Solo ida)"
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

    case 'coches':
      return (
        <>
          <div className="md:col-span-2">
            <CarLocationInput 
              label="Lugar de recogida" 
              placeholder="Ciudad o aeropuerto" 
              value={searchData.originText}
              onSelect={(loc: any) => {
                handleChange('fromId', loc.id);
                handleChange('originText', loc.name);
              }}
            />
          </div>
          
          <SearchInput label="Recogida" icon={<Calendar size={18} />} type="date" min={today} val={searchData.startDate} onChange={(v: any) => handleChange('startDate', v)} />
          
          <SearchSelect 
            label="Hora Recogida" 
            icon={<Clock size={18} />} 
            value={searchData.pickupTime || '10:00'}
            options={timeOptions}
            onChange={(v: string) => handleChange('pickupTime', v)} 
          />

          <SearchInput label="Devolución" icon={<Calendar size={18} />} type="date" min={searchData.startDate || today} val={searchData.endDate} onChange={(v: any) => handleChange('endDate', v)} />
          
          <SearchSelect 
            label="Hora Devolución" 
            icon={<Clock size={18} />} 
            value={searchData.dropoffTime || '10:00'}
            options={timeOptions}
            onChange={(v: string) => handleChange('dropoffTime', v)} 
          />
          
          <SearchSelect 
            label="Tipo de coche" 
            icon={<Car size={18} />} 
            value={searchData.carType || 'all'}
            options={[
              { label: 'Todos los tipos', value: 'all' },
              { label: 'Pequeño', value: 'small' },
              { label: 'Mediano', value: 'medium' },
              { label: 'Grande', value: 'large' },
              { label: 'SUV', value: 'suvs' },
              { label: 'Premium', value: 'premium' },
              { label: 'Furgoneta', value: 'carriers' },
            ]}
            onChange={(v: string) => handleChange('carType', v)} 
          />
        </>
      );  

    case 'actividades':
      return (
        <>
          <div className="md:col-span-2">
            <ActivityAutocomplete 
              label="Ciudad o Atracción" 
              placeholder="Ej: Madrid, París, Roma..." 
              value={searchData.destinationText}
              onSelect={(loc: any) => {
                // Guardamos el ID técnico (ej: city:123) en destination
                handleChange('destination', loc.id); 
                // Guardamos el nombre bonito (ej: Madrid) en destinationText
                handleChange('destinationText', loc.nombre);
              }} 
            />
          </div>

          <div className="md:col-span-2">
            <SearchInput 
              label="Fecha desde" 
              icon={<Calendar size={18} />} 
              type="date" 
              min={today}
              val={searchData.startDate} 
              onChange={(v: any) => handleChange('startDate', v)} 
            />
          </div>

          <div className="md:col-span-2">
            <SearchInput 
              label="Fecha hasta" 
              icon={<Calendar size={18} />} 
              type="date" 
              min={searchData.startDate || today}
              val={searchData.endDate} 
              onChange={(v: any) => handleChange('endDate', v)} 
            />
          </div>
          
          {/* Un espacio vacío opcional para cuadrar la rejilla si tu grid es de 7 columnas */}
          <div className="hidden md:block md:col-span-1" />
        </>
      );

    case 'cruceros':
      return (
        <>
          <SearchInput 
            label="Zona" 
            icon={<Globe size={18} />} 
            placeholder="Ej: Mediterráneo" 
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLegacyLocationChange('destination', v)} 
          />
          <SearchInput 
            label="Puerto" 
            icon={<Ship size={18} />} 
            placeholder="Ej: Barcelona" 
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
          <div className="hidden lg:block opacity-0" aria-hidden="true" />
        </>
      );

    default:
      return null;
  }
};