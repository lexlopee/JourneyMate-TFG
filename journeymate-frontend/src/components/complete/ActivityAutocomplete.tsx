import { useState, useEffect, useRef } from 'react';
import api from '../../services/api';
import { MapPin, Loader2, Search } from 'lucide-react';

// Ajustamos la interfaz para mapear lo que viene de Booking (vía tu Java)
interface ActivityLocation {
  id: string;
  nombre: string;
  descripcion: string;
}

const activityCache: { [key: string]: ActivityLocation[] } = {};

export const ActivityAutocomplete = ({ label, placeholder, value, onSelect }: any) => {
  const [query, setQuery] = useState(value || '');
  const [suggestions, setSuggestions] = useState<ActivityLocation[]>([]);
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const abortControllerRef = useRef<AbortController | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

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

    if (activityCache[query.toLowerCase()]) {
      setSuggestions(activityCache[query.toLowerCase()]);
      setShow(true);
      return;
    }

    const timer = setTimeout(async () => {
      if (abortControllerRef.current) abortControllerRef.current.abort();
      abortControllerRef.current = new AbortController();

      setLoading(true);
      try {
        const { data } = await api.get('/activities/location', { 
          params: { query },
          signal: abortControllerRef.current.signal 
        });

        // --- CAMBIO CRÍTICO AQUÍ ---
        // 'data' ahora es el objeto { destinations: [...], products: [...] }
        const rawDestinations = data.destinations || [];
        const rawProducts = data.products || [];

        // Mapeamos el formato de Booking al formato que usa tu componente
        const results: ActivityLocation[] = [
          ...rawDestinations.map((d: any) => ({
            id: d.id,
            nombre: d.cityName || d.name,
            descripcion: d.display_name || d.address?.country || 'Ciudad'
          })),
          ...rawProducts.map((p: any) => ({
            id: p.id,
            nombre: p.name,
            descripcion: 'Actividad específica'
          }))
        ];

        activityCache[query.toLowerCase()] = results;
        setSuggestions(results);
        setShow(true);
      } catch (e: any) {
        if (e.name !== 'CanceledError') console.error("Error API Actividades:", e);
      } finally {
        setLoading(false);
      }
    }, 500); 

    return () => clearTimeout(timer);
  }, [query]);

  return (
    <div ref={containerRef} className="relative w-full group">
      <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-orange-100/50 hover:bg-white transition-all shadow-sm focus-within:ring-2 focus-within:ring-orange-500/20">
        <span className="text-[9px] font-black text-orange-800/40 block mb-1 uppercase tracking-widest">{label}</span>
        <div className="flex items-center gap-2">
          <span className="text-orange-500">
            {loading ? <Loader2 size={14} className="animate-spin" /> : <Search size={14}/>}
          </span>
          <input
            type="text"
            className="bg-transparent border-none outline-none text-[11px] font-bold text-gray-900 w-full placeholder:text-gray-400"
            placeholder={placeholder}
            value={query}
            autoComplete="off"
            onChange={(e) => { setQuery(e.target.value); setShow(true); }}
            onFocus={() => setShow(true)}
          />
        </div>
      </div>

      {show && suggestions.length > 0 && (
        <div className="absolute top-full left-0 w-full bg-white rounded-2xl shadow-[0_20px_50px_rgba(0,0,0,0.15)] border border-gray-100 z-[9999] max-h-72 overflow-y-auto p-2 mt-2 animate-in fade-in zoom-in-95 duration-200">
          {suggestions.map((item) => (
            <button
              key={item.id}
              type="button"
              onClick={() => {
                setQuery(item.nombre);
                setShow(false);
                onSelect(item); 
              }}
              className="w-full flex items-center gap-3 p-3 hover:bg-orange-50 rounded-xl transition-all text-left group/item"
            >
              <div className="w-8 h-8 rounded-full bg-orange-100 flex items-center justify-center flex-shrink-0 text-orange-600">
                <MapPin size={16} />
              </div>
              <div className="flex-grow overflow-hidden">
                <div className="text-[11px] font-bold text-gray-900 truncate">
                  {item.nombre}
                </div>
                <div className="text-[9px] text-gray-500 truncate">
                  {item.descripcion}
                </div>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};