// src/components/LoadingVideo.tsx
// Coloca el vídeo en: public/logo-loading.mp4

interface LoadingVideoProps {
  size?: number; // tamaño en px, por defecto 180
}

export const LoadingVideo = ({ size = 180 }: LoadingVideoProps) => {
  return (
    <div
      style={{ width: size, height: size }}
      className="relative flex items-center justify-center"
    >
      {/* 
        🔥 CLAVE: mix-blend-mode: screen
        El negro del fondo del vídeo se vuelve transparente visualmente.
        Sin borrar píxeles, sin halos, sin procesamiento pesado.
        Funciona igual en todos los navegadores modernos.
      */}
      <video
        autoPlay
        loop
        muted
        playsInline
        src="/logo-loading.mp4"
        style={{
          width: '100%',
          height: '100%',
          objectFit: 'contain',
          mixBlendMode: 'screen',
        }}
      />
    </div>
  );
};
