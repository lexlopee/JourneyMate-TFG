// 1. Importamos los tipos usando 'import type'
import type { ReactNode } from 'react';
import type { LucideIcon } from 'lucide-react';

interface Option {
  label: string;
  value: string;
}

interface Props {
  label: string;
  // Usamos los tipos importados
  icon: ReactNode | LucideIcon; 
  value: string;
  options: Option[];
  onChange: (val: string) => void;
}

export const SearchSelect = ({ label, icon, value, options, onChange }: Props) => {
  return (
    <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all shadow-sm">
      <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">
        {label}
      </span>
      <div className="flex items-center gap-2">
        <span className="text-teal-500">
          {/* Al ser un componente o elemento React, se renderiza así */}
          {icon}
        </span>
        <select 
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="bg-transparent border-none outline-none text-[11px] font-bold text-teal-900 w-full appearance-none cursor-pointer focus:ring-0"
        >
          {options.map((opt) => (
            <option key={opt.value} value={opt.value} className="bg-white text-teal-900">
              {opt.label}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
};