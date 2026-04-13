import { useState, useMemo } from 'react';
import { HotelCard } from './HotelCard';
import { FlightCard } from './FlightCard';
import { CarCard } from './CarCard';
import { ActivityCard } from './ActivityCard';
import {
  Sparkles, ArrowUpNarrowWide, Star, SlidersHorizontal,
  Plane, Clock, Hotel as HotelIcon, Car, Ticket
} from 'lucide-react';

interface ResultsListProps {
  results: any[];
  activeSection: string;
  onViewDetails: (item: any) => void;
  onRentCar?: (car: any) => void;
  onBookActivity: (activity: any) => void;
  destination: string;
  searchData?: any;
}

export const ResultsList = ({
  results,
  activeSection,
  onViewDetails,
  onRentCar,
  onBookActivity,
  destination,
  searchData,
}: ResultsListProps) => {
  const [sortBy, setSortBy] = useState('default');

  const parseDurationToMinutes = (durationStr: string) => {
    if (!durationStr) return 0;
    const hours   = durationStr.match(/(\d+)h/);
    const minutes = durationStr.match(/(\d+)min/);
    return (hours ? parseInt(hours[1]) * 60 : 0) + (minutes ? parseInt(minutes[1]) : 0);
  };

  const sortedResults = useMemo(() => {
    if (!results) return [];
    const copy = [...results];
    switch (sortBy) {
      case 'price_asc':
        return copy.sort((a, b) => (a.precio || a.price || 0) - (b.precio || b.price || 0));
      case 'rating_desc':
        return copy.sort((a, b) => (b.calificacion || 0) - (a.calificacion || 0));
      case 'duration_asc':
        return copy.sort((a, b) => parseDurationToMinutes(a.duracion) - parseDurationToMinutes(b.duracion));
      default:
        return copy;
    }
  }, [results, sortBy]);

  const sortOptions = [
    { id: 'default',      label: 'Recomendados',    icon: <SlidersHorizontal size={14} /> },
    { id: 'price_asc',    label: 'Más Barato',       icon: <ArrowUpNarrowWide size={14} /> },
    { id: 'duration_asc', label: 'Más Rápido',       icon: <Clock size={14} />, showOnly: 'vuelos' },
    { id: 'rating_desc',  label: 'Mejor Valorados',  icon: <Star  size={14} />, showOnly: 'alojamiento' },
  ];

  if (!results || results.length === 0) return null;

  const getSectionTitle = () => {
    const place = destination?.replace(/_/g, ' ');
    if (activeSection === 'alojamiento') return `Hoteles en ${place || 'tu destino'}`;
    if (activeSection === 'vuelos')      return `Vuelos a ${place || 'tu destino'}`;
    if (activeSection === 'coches')      return `Coches disponibles`;
    if (activeSection === 'actividades') return `Experiencias en ${place || 'tu destino'}`;
    return `Resultados para ${activeSection}`;
  };

  const HeaderIcon = 
  activeSection === 'vuelos' ? Plane : 
  activeSection === 'coches' ? Car : 
  activeSection === 'actividades' ? Ticket :
  HotelIcon;

  return (
    <div className="w-full max-w-7xl mx-auto mt-20 animate-fade-in pb-20">

      {/* CABECERA */}
      <div className="flex flex-col gap-8 mb-12 px-6">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 text-teal-600 font-black uppercase text-[10px] tracking-[0.3em] mb-2">
              <Sparkles size={14} /> JourneyMate Selecciones
            </div>
            <h2 className="text-4xl md:text-5xl font-black text-teal-900 uppercase tracking-tighter">
              {getSectionTitle()}
            </h2>
          </div>
          <div className="flex items-center gap-3 bg-teal-900 text-white px-6 py-3 rounded-2xl shadow-lg">
            <HeaderIcon size={16} />
            <span className="font-black text-xs uppercase tracking-widest">{results.length} disponibles</span>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-3 bg-white/50 p-2 rounded-[2rem] border border-teal-100 w-fit backdrop-blur-sm">
          <span className="text-[9px] font-black uppercase tracking-widest text-teal-900/40 ml-4 mr-2">Ordenar por:</span>
          {sortOptions.map((option) => {
            if (option.showOnly && option.showOnly !== activeSection) return null;
            return (
              <button
                key={option.id}
                onClick={() => setSortBy(option.id)}
                className={`flex items-center gap-2 px-6 py-3 rounded-full text-[10px] font-black uppercase tracking-widest transition-all duration-300
                  ${sortBy === option.id
                    ? 'bg-teal-500 text-white shadow-md scale-105'
                    : 'hover:bg-teal-100 text-teal-800 opacity-60 hover:opacity-100'}`}
              >
                {option.icon}{option.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 px-6">

        {activeSection === 'alojamiento' &&
          sortedResults.map((item, index) => (
            <HotelCard
              key={item.hotelId || index}
              hotel={item}
              onViewDetails={() => onViewDetails(item)}
              destination={destination?.replace(/_/g, ' ')}
            />
          ))
        }

        {activeSection === 'vuelos' &&
          sortedResults.map((item, index) => (
            <FlightCard
              key={item.token || index}
              flight={item}
              onViewDetails={() => onViewDetails(item)}
            />
          ))
        }

        {activeSection === 'coches' &&
          sortedResults.map((item, index) => (
            <div key={index} className="md:col-span-2 lg:col-span-3">
              <CarCard
                car={item}
                searchData={searchData}
                onRent={() => onRentCar?.(item)} // ✅ abre el modal
              />
            </div>
          ))
        }

        {activeSection === 'actividades' &&
          sortedResults.map((item, index) => (
            <ActivityCard
              key={item.idActividad || index}
              activity={item}
              onViewDetails={() => onBookActivity(item)}
            />
          ))
        }

      </div>
    </div>
  );
};