import { useState } from "react";
import Flatpickr from "react-flatpickr";
import { Spanish } from "flatpickr/dist/l10n/es";
import { X } from "lucide-react"; // Importamos el icono para borrar

export const SearchInput = ({
  label,
  icon,
  placeholder,
  val,
  type = "text",
  onChange
}: any) => {
  const [suggestions, setSuggestions] = useState<any[]>([]);

  const fetchSuggestions = async (text: string) => {
    if (label !== "Destino" || text.length < 2) {
      setSuggestions([]);
      return;
    }
    try {
      const res = await fetch(`/api/maps/autocomplete?query=${encodeURIComponent(text)}`);
      const data = await res.json();
      setSuggestions(data);
    } catch {
      setSuggestions([]);
    }
  };

  return (
    <div className="relative search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all group shadow-sm hover:shadow-md">
      <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">
        {label}
      </span>

      <div className="flex items-center gap-2 text-teal-900 relative">
        <span className="text-teal-500 group-hover:rotate-12 transition-transform duration-300 shrink-0">
          {icon}
        </span>

        {type === "date" ? (
          <div className="flex-grow flex items-center">
            <Flatpickr
              value={val}
              options={{
                dateFormat: "Y-m-d",
                altInput: true,
                altFormat: "d/m/Y",
                minDate: "today",
                locale: Spanish,
                allowInput: true, // Permite borrar con el teclado
                monthSelectorType: "static",
              }}
              placeholder={placeholder}
              onChange={(dates) => {
                if (dates.length > 0) {
                  const localDate = dates[0].toLocaleDateString('en-CA');
                  onChange(localDate);
                } else {
                  onChange("");
                }
              }}
              className="bg-transparent border-none outline-none w-full text-[11px] font-bold cursor-pointer"
            />
            
            {/* BOTÓN "X" PARA BORRAR LA FECHA */}
            {val && (
              <button
                type="button"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onChange(""); // Limpiamos el valor
                }}
                className="ml-1 p-1 hover:bg-teal-100 rounded-full text-teal-400 hover:text-teal-600 transition-colors z-10"
              >
                <X size={14} strokeWidth={3} />
              </button>
            )}
          </div>
        ) : (
          <input
            type="text"
            value={val}
            onChange={(e) => {
              const value = e.target.value.replace(/_/g, " ");
              onChange(value);
              fetchSuggestions(value);
            }}
            placeholder={placeholder}
            className="bg-transparent border-none outline-none w-full text-[11px] font-bold placeholder:text-teal-900/20"
          />
        )}
      </div>

      {/* SUGERENCIAS (AUTOCOMPLETE) */}
      {suggestions.length > 0 && (
        <div className="absolute top-full left-0 w-full bg-white rounded-xl shadow-xl mt-2 z-50 overflow-hidden border border-teal-50">
          {suggestions.map((s, i) => (
            <div
              key={i}
              className="p-3 text-[11px] font-bold text-teal-900 hover:bg-teal-50 cursor-pointer transition-all border-b border-teal-50 last:border-none"
              onClick={() => {
                onChange(s.description);
                setSuggestions([]);
              }}
            >
              {s.description}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};