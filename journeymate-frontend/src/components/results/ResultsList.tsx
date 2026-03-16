import { HotelCard } from '../HotelCard';
import { Sparkles } from 'lucide-react';

interface ResultsListProps {
  results: any[];
  activeSection: string;
  onViewDetails: (hotel: any) => void;
  destination: string; 
}

export const ResultsList = ({ results, activeSection, onViewDetails, destination }: ResultsListProps) => {
  if (!results || results.length === 0) return null;

  return (
    <div className="w-full max-w-7xl mx-auto mt-20 animate-fade-in pb-20">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-12 px-6">
        <div>
          <div className="flex items-center gap-2 text-teal-600 font-black uppercase text-[10px] tracking-[0.3em] mb-2">
            <Sparkles size={14} /> JourneyMate Selecciones
          </div>
          <h2 className="text-4xl md:text-5xl font-black text-teal-900 uppercase tracking-tighter">
            Alojamientos en <span className="text-teal-500">{destination || activeSection}</span>
          </h2>
        </div>
        <div className="bg-teal-50 px-6 py-3 rounded-2xl border border-teal-100">
           <span className="text-teal-900 font-black text-sm">{results.length} alojamientos encontrados</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 px-6">
        {activeSection === 'alojamiento' && 
          results.map((item, index) => (
            <HotelCard 
              key={item.hotelId || index} 
              hotel={item} 
              onViewDetails={() => onViewDetails(item)}
              destination={destination} 
            />
          ))
        }
      </div>
    </div>
  );
};