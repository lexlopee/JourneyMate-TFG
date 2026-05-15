interface LoadingVideoProps {
  size?: number;
}

export const LoadingVideo = ({ size = 180 }: LoadingVideoProps) => {
  return (
    <div
      style={{ width: size, height: size }}
      className="relative flex items-center justify-center"
    >
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
