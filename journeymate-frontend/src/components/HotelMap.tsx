import { useEffect, useRef } from 'react';

declare global {
  interface Window {
    google: any;
  }
}

export const HotelMap = ({ lat, lng }: { lat: number; lng: number }) => {
  const mapRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const renderMap = () => {
    const { google } = window as any;
      // Si el div o la API no están listos, salimos
      if (!mapRef.current || !window.google?.maps) return;

      const position = { lat: Number(lat), lng: Number(lng) };

      const map = new google.maps.Map(mapRef.current, {
        center: position,
        zoom: 15,
        mapId: "DEMO_MAP_ID", // Obligatorio para marcadores nuevos
      });

      // Marcador moderno
      new google.maps.marker.AdvancedMarkerElement({
        map,
        position,
        title: "Hotel",
      });
    };

    // Si google ya cargó, dibujamos. Si no, esperamos al evento del sistema.
    if (window.google?.maps) {
      renderMap();
    } else {
      // Escuchamos cuando el script del index.html termine de cargar
      window.addEventListener('load', renderMap);
      return () => window.removeEventListener('load', renderMap);
    }
  }, [lat, lng]);

  return (
    <div ref={mapRef} className="w-full h-full rounded-[2rem] bg-slate-100" style={{ minHeight: '350px' }} />
  );
};