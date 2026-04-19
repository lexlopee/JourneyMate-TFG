import { useState, useRef } from "react";
import Flatpickr from "react-flatpickr";
import { Spanish } from "flatpickr/dist/l10n/es";
import { X } from "lucide-react";

export const SearchInput = ({
  label,
  icon,
  placeholder,
  val,
  type = "text",
  onChange,
  fieldName,
  min,
  onEnter, // ✅ NUEVO: callback cuando se termina de escribir (para auto-avance)
}: any) => {
  const [suggestions, setSuggestions] = useState<any[]>([]);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // ── Auto-avance al siguiente campo cuando el usuario deja de escribir ──
  const handleTextChange = (value: string) => {
    const clean = value.replace(/_/g, " ");
    onChange(clean);

    // Si hay callback de "enter" (auto-avance), dispararlo con debounce
    if (onEnter && clean.length >= 2) {
      if (debounceRef.current) clearTimeout(debounceRef.current);
      debounceRef.current = setTimeout(() => {
        onEnter();
      }, 700); // 700ms tras dejar de escribir
    }
  };

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
    <div
      className={[
        // ✅ SIN shadow-sm ni hover:shadow-md para evitar la sombra interior bug
        "relative search-input-field",
        "bg-white/90 rounded-2xl p-3 text-left",
        "border border-teal-100/50",
        "hover:bg-white transition-all group",
      ].join(" ")}
    >
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
                minDate: min || "today",
                locale: Spanish,
                allowInput: true,
                monthSelectorType: "static",
              }}
              placeholder={placeholder}
              onChange={(dates) => {
                if (dates.length > 0) {
                  const localDate = dates[0].toLocaleDateString("en-CA");
                  onChange(localDate);
                } else {
                  onChange("");
                }
              }}
              className="bg-transparent border-none outline-none w-full text-[11px] font-bold cursor-pointer"
              data-field={fieldName}
            />
            {val && (
              <button
                type="button"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onChange("");
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
              handleTextChange(e.target.value);
              fetchSuggestions(e.target.value);
            }}
            placeholder={placeholder}
            className="bg-transparent border-none outline-none w-full text-[11px] font-bold placeholder:text-teal-900/20"
          />
        )}
      </div>

      {/* Sugerencias autocomplete */}
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