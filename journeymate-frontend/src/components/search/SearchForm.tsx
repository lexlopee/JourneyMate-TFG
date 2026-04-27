import {
  Calendar, Users, Bed, PlaneLanding, PlaneTakeoff,
  Briefcase, Clock, Car
} from 'lucide-react';
import { SearchInput } from "./SearchInput";
import { SearchCounter } from "./SearchCounter";
import { AutocompleteInput } from "../complete/AutoCompleteInput";
import { SearchSelect } from "./SearchSelect";
import { CarLocationInput } from '../complete/CarLocationInput';
import { ActivityAutocomplete } from '../complete/ActivityAutocomplete';
import { CruiseSearchSelects } from '../complete/CruiseSearchSelects';

export const SearchForm = ({ activeSection, searchData, handleChange, minDate }: any) => {

  // ── Auto-avance al campo siguiente ─────────────────────────────────────
  const focusField = (fieldName: string) => {
    setTimeout(() => {
      // Intenta abrir Flatpickr si es campo de fecha
      const fp = document.querySelector(`[data-field="${fieldName}"]`) as any;
      if (fp && fp._flatpickr) {
        fp._flatpickr.open();
        return;
      }
      // Fallback: focus normal
      const el = document.querySelector(`[data-field="${fieldName}"]`) as HTMLElement;
      if (el) { el.focus(); el.click(); }
    }, 80);
  };

  const handleLegacyLocationChange = (key: string, value: string) => {
    const formattedValue = value.replace(/ /g, '_');
    handleChange(key, formattedValue);
    handleChange(key + 'Text', value);
  };

  const timeOptions = Array.from({ length: 48 }, (_, i) => {
    const h = Math.floor(i / 2).toString().padStart(2, '0');
    const m = (i % 2 === 0 ? '00' : '30');
    return { label: `${h}:${m}`, value: `${h}:${m}` };
  });

  const today = minDate || new Date().toISOString().split('T')[0];

  switch (activeSection) {

    // ── VUELOS ──────────────────────────────────────────────────────────
    case 'vuelos':
      return (
        <>
          <div className="md:col-span-1">
            <AutocompleteInput
              label="Origen" icon={<PlaneTakeoff size={18} />} placeholder="Ej: Madrid"
              value={searchData.originText}
              onSelect={(dest: any) => {
                handleChange('fromId', dest.id);
                handleChange('originText', dest.name);
                focusField('startDate');
              }}
            />
          </div>
          <div className="md:col-span-1">
            <AutocompleteInput
              label="Destino" icon={<PlaneLanding size={18} />} placeholder="Ej: Barcelona"
              value={searchData.destinationText}
              onSelect={(dest: any) => {
                handleChange('toId', dest.id);
                handleChange('destinationText', dest.name);
                focusField('startDate');
              }}
            />
          </div>
          <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" min={today}
            val={searchData.startDate}
            onChange={(v: any) => { handleChange('startDate', v); if (v) focusField('endDate'); }}
            fieldName="startDate"
          />
          <SearchInput label="Regreso" placeholder="Opcional (Solo ida)" icon={<Calendar size={18} />} type="date"
            min={searchData.startDate || today}
            val={searchData.endDate}
            onChange={(v: any) => handleChange('endDate', v)}
            fieldName="endDate"
          />
          <SearchCounter label="Pasajeros" icon={<Users size={18} />} val={searchData.adults} min={1}
            onChange={(v: string) => handleChange('adults', v)}
          />
          <SearchSelect label="Clase" icon={<Briefcase size={18} />} value={searchData.cabinClass}
            options={[
              { label: 'Económica', value: 'ECONOMY' },
              { label: 'Business', value: 'BUSINESS' },
              { label: 'Primera Clase', value: 'FIRST' }
            ]}
            onChange={(v: string) => handleChange('cabinClass', v)}
          />
        </>
      );

    // ── ALOJAMIENTO ─────────────────────────────────────────────────────
    case 'alojamiento':
      return (
        <>
          {/* ✅ Auto-avance: al terminar de escribir destino → abre fecha entrada */}
          <SearchInput
            label="Destino" icon={<Bed size={18} />} placeholder="¿A dónde vas?"
            val={searchData.destination?.replace(/_/g, ' ')}
            onChange={(v: any) => handleLegacyLocationChange('destination', v)}
            onEnter={() => focusField('startDate')}
          />
          <SearchInput label="Entrada" icon={<Calendar size={18} />} type="date" min={today}
            val={searchData.startDate}
            onChange={(v: any) => { handleChange('startDate', v); if (v) focusField('endDate'); }}
            fieldName="startDate"
          />
          <SearchInput label="Salida" icon={<Calendar size={18} />} type="date"
            min={searchData.startDate || today}
            val={searchData.endDate}
            onChange={(v: any) => handleChange('endDate', v)}
            fieldName="endDate"
          />
          <SearchCounter label="Adultos" icon={<Users size={18} />} val={searchData.adults} min={1}
            onChange={(v: string) => handleChange('adults', v)}
          />
          <SearchCounter label="Habitaciones" icon={<Bed size={18} />} val={searchData.roomQty} min={1}
            onChange={(v: string) => handleChange('roomQty', v)}
          />
        </>
      );

    // ── COCHES ──────────────────────────────────────────────────────────
    // ✅ Más compacto: misma estructura que los demás (grid normal, sin col-span-2)
    case 'coches':
      return (
        <>
          <div className="md:col-span-2">
            <CarLocationInput
              label="Lugar de recogida" placeholder="Ciudad o aeropuerto"
              value={searchData.originText}
              onSelect={(loc: any) => {
                handleChange('fromId', loc.id);
                handleChange('originText', loc.name);
              }}
            />
          </div>
          <SearchInput label="Recogida" icon={<Calendar size={18} />} type="date" min={today}
            val={searchData.startDate}
            onChange={(v: any) => handleChange('startDate', v)}
            fieldName="startDate"
          />
          <SearchSelect label="Hora recogida" icon={<Clock size={18} />}
            value={searchData.pickupTime || '10:00'} options={timeOptions}
            onChange={(v: string) => handleChange('pickupTime', v)}
          />
          <SearchInput label="Devolución" icon={<Calendar size={18} />} type="date"
            min={searchData.startDate || today}
            val={searchData.endDate}
            onChange={(v: any) => handleChange('endDate', v)}
            fieldName="endDate"
          />
          <SearchSelect label="Hora devolución" icon={<Clock size={18} />}
            value={searchData.dropoffTime || '10:00'} options={timeOptions}
            onChange={(v: string) => handleChange('dropoffTime', v)}
          />
          <SearchSelect label="Tipo" icon={<Car size={18} />}
            value={searchData.carType || 'all'}
            options={[
              { label: 'Todos', value: 'all' },
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

    // ── ACTIVIDADES ─────────────────────────────────────────────────────
    case 'actividades':
      return (
        <>
          <div className="md:col-span-2">
            <ActivityAutocomplete
              label="Ciudad" placeholder="Ej: Madrid, París, Roma..."
              value={searchData.destinationText}
              onSelect={(loc: any) => {
                handleChange('destination', loc.id);
                handleChange('destinationText', loc.nombre);
              }}
            />
          </div>
          <div className="md:col-span-2">
            <SearchInput label="Fecha desde" icon={<Calendar size={18} />} type="date" min={today}
              val={searchData.startDate}
              onChange={(v: any) => handleChange('startDate', v)}
            />
          </div>
          <div className="md:col-span-2">
            <SearchInput label="Fecha hasta" icon={<Calendar size={18} />} type="date"
              min={searchData.startDate || today}
              val={searchData.endDate}
              onChange={(v: any) => handleChange('endDate', v)}
            />
          </div>
          <div className="hidden md:block md:col-span-1" />
        </>
      );

    // ── CRUCEROS ────────────────────────────────────────────────────────
    case 'cruceros':
      return (
        <>
          <CruiseSearchSelects
            destinationValue={searchData.cruiseDestination || ''}
            portValue={searchData.cruisePort || ''}
            onDestinationChange={(code: string, label: string) => {
              handleChange('cruiseDestination', code);
              handleChange('destination', code);
              handleChange('destinationText', label);
            }}
            onPortChange={(code: string, label: string) => {
              handleChange('cruisePort', code);
              handleChange('origin', code);
              handleChange('originText', label);
            }}
          />
          <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" min={today}
            val={searchData.startDate}
            onChange={(v: any) => handleChange('startDate', v)}
          />
          <SearchInput label="Regreso" icon={<Calendar size={18} />} type="date"
            min={searchData.startDate || today}
            val={searchData.endDate}
            onChange={(v: any) => handleChange('endDate', v)}
          />
          <div className="hidden lg:block opacity-0" aria-hidden="true" />
          <div className="hidden lg:block opacity-0" aria-hidden="true" />
          <div className="hidden lg:block opacity-0" aria-hidden="true" />
        </>
      );

    default:
      return null;
  }
};