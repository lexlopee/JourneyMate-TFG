import { useState, useEffect } from 'react';
import { Globe, Ship } from 'lucide-react';
import api from '../services/api';

interface CruiseSearchSelectsProps {
  destinationValue: string;
  portValue: string;
  onDestinationChange: (code: string, label: string) => void;
  onPortChange: (code: string, label: string) => void;
}

export const CruiseSearchSelects = ({
  destinationValue,
  portValue,
  onDestinationChange,
  onPortChange,
}: CruiseSearchSelectsProps) => {
  const [destinations, setDestinations] = useState<{ code: string; name: string }[]>([]);
  const [ports, setPorts]               = useState<{ code: string; name: string }[]>([]);
  const [loadingDest, setLoadingDest]   = useState(false);
  const [loadingPort, setLoadingPort]   = useState(false);

  // Carga los destinos desde /api/v1/cruises/destinations
  useEffect(() => {
    setLoadingDest(true);
    api.get('/cruises/destinations')
      .then(({ data }) => {
        // La API devuelve un JsonNode — normalizamos las dos estructuras posibles
        const raw: any[] = Array.isArray(data) ? data : (data?.destinations ?? data?.data ?? []);
        setDestinations(
          raw.map((d: any) => ({
            code: d.code ?? d.destination_code ?? d.id ?? '',
            name: d.name ?? d.destination_name ?? d.code ?? '',
          }))
        );
      })
      .catch(console.error)
      .finally(() => setLoadingDest(false));
  }, []);

  // Carga los puertos desde /api/v1/cruises/ports
  useEffect(() => {
    setLoadingPort(true);
    api.get('/cruises/ports')
      .then(({ data }) => {
        const raw: any[] = Array.isArray(data) ? data : (data?.ports ?? data?.data ?? []);
        setPorts(
          raw.map((p: any) => ({
            code: p.code ?? p.port_code ?? p.id ?? '',
            name: p.name ?? p.port_name ?? p.code ?? '',
          }))
        );
      })
      .catch(console.error)
      .finally(() => setLoadingPort(false));
  }, []);

  return (
    <>
      {/* SELECT DESTINO */}
      <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all shadow-sm">
        <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">
          Zona / Destino
        </span>
        <div className="flex items-center gap-2">
          <Globe size={18} className="text-teal-500 shrink-0" />
          <select
            value={destinationValue}
            onChange={(e) => {
              const opt = destinations.find(d => d.code === e.target.value);
              onDestinationChange(e.target.value, opt?.name ?? e.target.value);
            }}
            disabled={loadingDest}
            className="bg-transparent border-none outline-none text-[11px] font-bold text-teal-900 w-full appearance-none cursor-pointer focus:ring-0"
          >
            <option value="">
              {loadingDest ? 'Cargando destinos...' : 'Selecciona una zona'}
            </option>
            {destinations.map((d) => (
              <option key={d.code} value={d.code} className="bg-white text-teal-900">
                {d.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* SELECT PUERTO */}
      <div className="search-input-field bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all shadow-sm">
        <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">
          Puerto de salida
        </span>
        <div className="flex items-center gap-2">
          <Ship size={18} className="text-teal-500 shrink-0" />
          <select
            value={portValue}
            onChange={(e) => {
              const opt = ports.find(p => p.code === e.target.value);
              onPortChange(e.target.value, opt?.name ?? e.target.value);
            }}
            disabled={loadingPort}
            className="bg-transparent border-none outline-none text-[11px] font-bold text-teal-900 w-full appearance-none cursor-pointer focus:ring-0"
          >
            <option value="">
              {loadingPort ? 'Cargando puertos...' : 'Selecciona un puerto'}
            </option>
            {ports.map((p) => (
              <option key={p.code} value={p.code} className="bg-white text-teal-900">
                {p.name} ({p.code})
              </option>
            ))}
          </select>
        </div>
      </div>
    </>
  );
};