// src/components/HotelMap.tsx
import { useEffect, useRef } from 'react';

declare global {
  interface Window {
    google: any;
    __gmapsReady__: () => void;
    __GMAPS_KEY__: string;
  }
}

export const HotelMap = ({ lat, lng }: { lat: number; lng: number }) => {
  const mapRef    = useRef<HTMLDivElement>(null);
  const mapInstance = useRef<any>(null);

  useEffect(() => {
    // ── Función que inicializa el mapa ────────────────────────────────
    const initMap = () => {
      if (!mapRef.current || !window.google?.maps) return;
      if (mapInstance.current) return; // evitar doble init

      const position = { lat: Number(lat), lng: Number(lng) };

      const map = new window.google.maps.Map(mapRef.current, {
        center: position,
        zoom: 15,
        mapId: 'DEMO_MAP_ID', // necesario para AdvancedMarkerElement
        // Opciones visuales limpias
        disableDefaultUI: false,
        zoomControl: true,
        streetViewControl: false,
        mapTypeControl: false,
        fullscreenControl: false,
      });

      new window.google.maps.marker.AdvancedMarkerElement({
        map,
        position,
        title: 'Hotel',
      });

      mapInstance.current = map;
    };

    // ── Decidir cuándo llamar a initMap ───────────────────────────────
    if (window.google?.maps) {
      // La API ya estaba cargada (p.ej. el modal se abre por segunda vez)
      initMap();
    } else {
      // Esperamos el evento personalizado que lanza el callback del script
      window.addEventListener('gmaps:ready', initMap, { once: true });
    }

    return () => {
      window.removeEventListener('gmaps:ready', initMap);
      mapInstance.current = null;
    };
  }, [lat, lng]);

  return (
    <div
      ref={mapRef}
      className="w-full h-full rounded-[2rem] bg-slate-100"
      style={{ minHeight: '350px' }}
    />
  );
};