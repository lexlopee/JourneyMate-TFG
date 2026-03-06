import { HotelCard } from '../HotelCard';
import { Building2 } from 'lucide-react';

interface ResultsListProps {
  results: any[];
  activeSection: string;
  onViewDetails: (id: string) => void;
  destination: string; // Prop nueva para el destino real
}

export const ResultsList = ({ results, activeSection, onViewDetails, destination }: ResultsListProps) => {
  if (!results || results.length === 0) return null;

  return (
    <div className="w-full max-w-7xl mx-auto mt-12 animate-fade-in">
      <div className="flex items-center gap-3 mb-8 px-4">
        <Building2 className="text-teal-900" size={32} />
        <h2 className="text-3xl font-black text-teal-900 uppercase tracking-tighter">
          Resultados en <span className="text-teal-600">{destination || activeSection}</span>
        </h2>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 px-4">
        {activeSection === 'alojamiento' && 
          results.map((item, index) => (
            <HotelCard 
              key={item.hotelId || index} 
              hotel={item} 
              onViewDetails={onViewDetails}
              destination={destination} // Pasamos el destino a la tarjeta
            />
          ))
        }
      </div>
    </div>
  );
};