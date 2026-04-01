import { useState, useEffect, useRef } from "react";
import { Car, MapPin } from "lucide-react";

export const CarLocationInput = ({ label, placeholder, value, onSelect }: any) => {
  const [query, setQuery] = useState(value || "");
  const [suggestions, setSuggestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const wrapperRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setQuery(value || "");
  }, [value]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setSuggestions([]);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSearch = async (text: string) => {
    setQuery(text);
    if (text.length < 3) {
      setSuggestions([]);
      return;
    }
    
    setLoading(true);
    try {
      // Llamada a tu endpoint de coches
      const res = await fetch(`http://localhost:8080/api/v1/external/cars/autocomplete?query=${encodeURIComponent(text)}`);
      if (res.ok) {
        const data = await res.json();
        setSuggestions(data || []);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div ref={wrapperRef} className="relative search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all shadow-sm group">
      <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">
        {label}
      </span>
      <div className="flex items-center gap-2 text-teal-900">
        <Car size={18} className="text-teal-500 group-hover:-translate-y-0.5 transition-transform" />
        <input
          type="text"
          value={query}
          onChange={(e) => handleSearch(e.target.value)}
          placeholder={placeholder}
          className="bg-transparent border-none outline-none w-full text-[11px] font-bold placeholder:text-teal-900/20"
        />
        {loading && <div className="w-3 h-3 border-2 border-teal-200 border-t-teal-500 rounded-full animate-spin absolute right-4" />}
      </div>

      {suggestions.length > 0 && (
        <div className="absolute top-[calc(100%+8px)] left-0 w-full bg-white rounded-2xl shadow-2xl z-50 overflow-hidden border border-teal-50 max-h-60 overflow-y-auto custom-scrollbar">
          {suggestions.map((loc: any, idx: number) => (
            <div
              key={idx}
              className="p-4 hover:bg-teal-50 cursor-pointer transition-colors border-b border-teal-50/50 last:border-none flex items-center gap-3"
              onClick={() => {
                setQuery(loc.name);
                onSelect(loc);
                setSuggestions([]);
              }}
            >
              <div className="bg-teal-100/50 p-2 rounded-lg text-teal-600">
                <MapPin size={14} />
              </div>
              <div>
                <p className="text-xs font-black text-teal-900">{loc.name}</p>
                <p className="text-[9px] font-bold text-teal-600/60 uppercase tracking-widest">
                  {loc.city}, {loc.country} ({loc.type})
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};