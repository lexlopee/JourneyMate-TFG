import { useState, useMemo } from 'react';
import { HotelCard } from '../HotelCard';
import { FlightCard } from '../FlightCard';
import { Sparkles, ArrowUpNarrowWide, Star, SlidersHorizontal, Plane, Hotel as HotelIcon } from 'lucide-react';

interface ResultsListProps {
  results: any[];
  activeSection: string;
  onViewDetails: (item: any) => void;
  destination: string; 
}

export const ResultsList = ({ results, activeSection, onViewDetails, destination }: ResultsListProps) => {
  const [sortBy, setSortBy] = useState('default');

  // El ordenamiento ahora es genérico (usa 'precio' que está en ambos DTOs)
  const sortedResults = useMemo(() => {
    if (!results) return [];
    const resultsCopy = [...results];
    switch (sortBy) {
      case 'price_asc': return resultsCopy.sort((a, b) => (a.precio || 0) - (b.precio || 0));
      case 'price_desc': return resultsCopy.sort((a, b) => (b.precio || 0) - (a.precio || 0));
      case 'rating_desc': return resultsCopy.sort((a, b) => (b.calificacion || 0) - (a.calificacion || 0));
      default: return resultsCopy;
    }
  }, [results, sortBy]);

  const sortOptions = [
    { id: 'default', label: 'Recomendados', icon: <SlidersHorizontal size={14} /> },
    { id: 'price_asc', label: 'Más Barato', icon: <ArrowUpNarrowWide size={14} /> },
    { id: 'rating_desc', label: 'Mejor Valorados', icon: <Star size={14} /> },
  ];

  if (!results || results.length === 0) return null;

  // Títulos dinámicos según la sección
  const getSectionTitle = () => {
    const place = destination?.replace(/_/g, ' ');
    if (activeSection === 'alojamiento') return `Hoteles en ${place || 'tu destino'}`;
    if (activeSection === 'vuelos') return `Vuelos a ${place || 'tu destino'}`;
    return `Resultados para ${activeSection}`;
  };

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
          
          <div className="flex items-center gap-3 bg-teal-900 text-white px-6 py-3 rounded-2xl shadow-lg shadow-teal-900/20">
             {activeSection === 'vuelos' ? <Plane size={16} /> : <HotelIcon size={16} />}
             <span className="font-black text-xs uppercase tracking-widest">{results.length} disponibles</span>
          </div>
        </div>

        {/* SELECTOR DE FILTROS (Visible para ambos) */}
        <div className="flex flex-wrap items-center gap-3 bg-white/50 p-2 rounded-[2rem] border border-teal-100 w-fit backdrop-blur-sm">
          <span className="text-[9px] font-black uppercase tracking-widest text-teal-900/40 ml-4 mr-2">Ordenar por:</span>
          {sortOptions.map((option) => (
            // Ocultamos "Mejor Valorados" en vuelos si tu API no devuelve rating de aerolíneas
            (option.id === 'rating_desc' && activeSection === 'vuelos') ? null : (
              <button
                key={option.id}
                onClick={() => setSortBy(option.id)}
                className={`
                  flex items-center gap-2 px-6 py-3 rounded-full text-[10px] font-black uppercase tracking-widest transition-all duration-300
                  ${sortBy === option.id 
                    ? 'bg-teal-500 text-white shadow-md scale-105' 
                    : 'hover:bg-teal-100 text-teal-800 opacity-60 hover:opacity-100'}
                `}
              >
                {option.icon}
                {option.label}
              </button>
            )
          ))}
        </div>
      </div>

      {/* GRID DE RESULTADOS MIXTO */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 px-6">
        
        {/* Renderizado de HOTELES */}
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

        {/* Renderizado de VUELOS */}
        {activeSection === 'vuelos' && 
          sortedResults.map((item, index) => (
            <FlightCard 
              key={item.token || index} 
              flight={item} 
              onViewDetails={() => onViewDetails(item)}
            />
          ))
        }

      </div>
    </div>
  );
};