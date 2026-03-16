import { Plus, Minus } from 'lucide-react';

export const SearchCounter = ({ label, icon, val, min = 1, onChange }: any) => {
  const currentVal = parseInt(val.toString()) || min;
  const update = (inc: number) => {
    const newVal = Math.max(min, currentVal + inc);
    onChange(newVal.toString());
  };

  return (
    <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all group shadow-sm">
      <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">{label}</span>
      <div className="flex items-center justify-between text-teal-900">
        <div className="flex items-center gap-2">
          <span className="text-teal-500">{icon}</span>
          <span className="text-[11px] font-bold">{currentVal}</span>
        </div>
        <div className="flex gap-1">
          <button onClick={() => update(-1)} className="w-6 h-6 rounded-lg bg-teal-50 text-teal-600 flex items-center justify-center hover:bg-teal-900 hover:text-white transition-colors">
            <Minus size={12} strokeWidth={3} />
          </button>
          <button onClick={() => update(1)} className="w-6 h-6 rounded-lg bg-teal-50 text-teal-600 flex items-center justify-center hover:bg-teal-900 hover:text-white transition-colors">
            <Plus size={12} strokeWidth={3} />
          </button>
        </div>
      </div>
    </div>
  );
};