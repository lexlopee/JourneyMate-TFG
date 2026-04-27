import { useState, useEffect, useRef } from 'react';
import api from '../../services/api';
import { Plane, MapPin, Loader2 } from 'lucide-react';

interface FlightLocation {
  id: string;
  name: string;
  code: string;
  cityName: string;
  countryName: string;
  photoUri: string;
  type: 'AIRPORT' | 'CITY';
}

const searchCache: { [key: string]: FlightLocation[] } = {};

export const AutocompleteInput = ({ label, placeholder, icon, value, onSelect }: any) => {
  const [query, setQuery] = useState(value || '');
  const [suggestions, setSuggestions] = useState<FlightLocation[]>([]);
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const abortControllerRef = useRef<AbortController | null>(null);
  // 1. Ref para detectar clics fuera
  const containerRef = useRef<HTMLDivElement>(null);

  // 2. Efecto para cerrar al hacer clic fuera
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setShow(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (query.length < 3) {
      setSuggestions([]);
      return;
    }

    if (searchCache[query.toLowerCase()]) {
      setSuggestions(searchCache[query.toLowerCase()]);
      setShow(true);
      return;
    }

    const timer = setTimeout(async () => {
      if (abortControllerRef.current) abortControllerRef.current.abort();
      abortControllerRef.current = new AbortController();

      setLoading(true);
      try {
        const { data } = await api.get('/flights/location', { 
          params: { query },
          signal: abortControllerRef.current.signal 
        });

        const results = data.data || [];
        searchCache[query.toLowerCase()] = results;
        setSuggestions(results);
        setShow(true);
      } catch (e: any) {
        if (e.name !== 'CanceledError') console.error("Error API:", e);
      } finally {
        setLoading(false);
      }
    }, 500); 

    return () => clearTimeout(timer);
  }, [query]);

  return (
    // 3. Asignamos la ref al contenedor principal
    <div ref={containerRef} className="relative w-full group">
      <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all shadow-sm focus-within:ring-2 focus-within:ring-teal-500/20">
        <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">{label}</span>
        <div className="flex items-center gap-2">
          <span className="text-teal-500">
            {loading ? <Loader2 size={14} className="animate-spin" /> : icon}
          </span>
          <input
            type="text"
            className="bg-transparent border-none outline-none text-[11px] font-bold text-teal-900 w-full placeholder:text-teal-900/20"
            placeholder={placeholder}
            value={query}
            autoComplete="off" // Evita que el navegador ponga su propio autocompletado encima
            onChange={(e) => { setQuery(e.target.value); setShow(true); }}
            onFocus={() => setShow(true)}
          />
        </div>
      </div>

      {/* 4. Lista de sugerencias con animación y z-index alto */}
      {show && suggestions.length > 0 && (
        <div className="absolute top-full left-0 min-w-75 md:w-full bg-white rounded-2xl shadow-[0_20px_50px_rgba(0,0,0,0.2)] border border-teal-100 z-9999 max-h-72 overflow-y-auto p-2 mt-2 animate-in fade-in zoom-in-95 duration-200">
          {suggestions.map((item: any) => (
            <button
              key={item.id ?? item.placeId ?? item.place_id ?? item.name ?? Math.random()}
              type="button"
              onClick={() => {
                setQuery(item.name);
                setShow(false);
                onSelect(item);
              }}
              className="w-full flex items-center gap-3 p-2 hover:bg-teal-50/80 rounded-xl transition-all text-left group/item"
            >
              <div className="w-10 h-10 rounded-lg bg-teal-50 overflow-hidden flex-shrink-0 border border-teal-100/50 group-hover/item:scale-105 transition-transform">
                <img 
                  src={item.photoUri} 
                  className="w-full h-full object-cover" 
                  alt="" 
                  onError={(e:any) => e.target.src = 'https://via.placeholder.com/150?text=✈️'} 
                />
              </div>
              <div className="flex-grow overflow-hidden">
                <div className="text-[10px] font-black text-teal-900 uppercase truncate flex items-center gap-1">
                  {item.name} <span className="text-teal-500 font-bold">({item.code})</span>
                </div>
                <div className="text-[8px] text-teal-600 font-bold uppercase tracking-tight truncate opacity-70">
                  {item.cityName}, {item.countryName}
                </div>
              </div>
              <div className="opacity-30 group-hover/item:opacity-100 transition-opacity">
                {item.type === 'AIRPORT' ? <Plane size={12} className="text-teal-500" /> : <MapPin size={12} className="text-teal-500" />}
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};