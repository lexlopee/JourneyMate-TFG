import { Star, Clock } from 'lucide-react';

export const ActivityCard = ({ activity, onViewDetails }: any) => {
  return (
    <div className="bg-white rounded-3xl overflow-hidden shadow-sm hover:shadow-xl transition-all group border border-teal-50">
      {/* Imagen con badge de precio */}
      <div className="relative h-48 overflow-hidden">
        <img 
          src={activity.urlFoto || 'https://images.unsplash.com/photo-1533105079780-92b9be482077?q=80&w=400'} 
          alt={activity.nombre}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
        />
        <div className="absolute top-4 right-4 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full shadow-sm">
          <span className="text-teal-600 font-black text-sm">
            {activity.precio} {'€'}
          </span>
        </div>
      </div>

      {/* Contenido */}
      <div className="p-5">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-sm font-black text-teal-900 leading-tight flex-1 line-clamp-2">
            {activity.nombre}
          </h3>
          <div className="flex items-center gap-1 text-yellow-500 bg-yellow-50 px-2 py-0.5 rounded-lg ml-2">
            <Star size={10} fill="currentColor" />
            <span className="text-[10px] font-bold">
              {activity.calificacion > 0 ? activity.calificacion : 'Nuevo'}
            </span>
          </div>
        </div>

        <p className="text-[11px] text-teal-900/50 mb-4 line-clamp-2 h-8">
          {activity.descripcion}
        </p>

        <div className="flex items-center justify-between mt-auto pt-4 border-t border-teal-50">
          <div className="flex items-center gap-2 text-teal-600/60">
            <Clock size={12} />
            <span className="text-[10px] font-bold">Ver disponibilidad</span>
          </div>
          <button 
            onClick={() => onViewDetails(activity.slug)}
            className="bg-teal-50 hover:bg-teal-600 text-teal-600 hover:text-white text-[10px] font-black px-4 py-2 rounded-xl transition-all uppercase tracking-wider"
          >
            Ver Detalles
          </button>
        </div>
      </div>
    </div>
  );
};