import { useState } from "react";
import Flatpickr from "react-flatpickr";
import { Spanish } from "flatpickr/dist/l10n/es";

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

      <div className="flex items-center gap-2 text-teal-900">
        <span className="text-teal-500 group-hover:rotate-12 transition-transform duration-300">
          {icon}
        </span>

        {type === "date" ? (
          <Flatpickr
            value={val}
            options={{
              dateFormat: "d/m/Y",
              minDate: "today",
              locale: Spanish
            }}
            onChange={([date]) => onChange(date)}
            className="bg-transparent border-none outline-none w-full text-[11px] font-bold"
          />
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

      {suggestions.length > 0 && (
        <div className="absolute top-full left-0 w-full bg-white rounded-xl shadow-xl mt-2 z-50 overflow-hidden">
          {suggestions.map((s, i) => (
            <div
              key={i}
              className="p-3 text-sm hover:bg-teal-100 cursor-pointer transition-all"
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
