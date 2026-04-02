// src/components/Car3D.tsx

import { Suspense, useRef, useState, useEffect } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { useGLTF, ContactShadows, OrbitControls } from '@react-three/drei';
import * as THREE from 'three';
import type { Group, Mesh } from 'three';

const CONFIG: Record<string, {
  model: string;
  label: string;
  scale: number;
  recolor: boolean;
  posY: number;
}> = {
  small:    { model: '/car-small.glb',  label: 'Pequeño',        scale: 3.5,  recolor: true,  posY: -0.5 },
  medium:   { model: '/car-medium.glb', label: 'Mediano',         scale: 1.0,  recolor: false, posY: -0.5 },
  large:    { model: '/car-large.glb',  label: 'Grande',          scale: 0.8,  recolor: false, posY: -0.5 },
  suvs:     { model: '/car-suv.glb',    label: 'SUV',             scale: 0.8,  recolor: false, posY: -0.5 },
  premium:  { model: '/car-sport.glb',  label: 'Premium / Sport', scale: 0.9,  recolor: false, posY: -0.5 },
  carriers: { model: '/car-van.glb',    label: 'Furgoneta',       scale: 0.04, recolor: true,  posY: -0.5 },
};

const ALL_TYPES = ['small', 'medium', 'large', 'suvs', 'premium', 'carriers'];

// ── Shuffle aleatorio sin repetir el mismo dos veces seguidas ─────────────
function getNextRandom(currentIndex: number): number {
  const options = ALL_TYPES.map((_, i) => i).filter(i => i !== currentIndex);
  return options[Math.floor(Math.random() * options.length)];
}

function CarModel({ modelPath, scale, recolor, posY }: {
  modelPath: string;
  scale: number;
  recolor: boolean;
  posY: number;
}) {
  const { scene } = useGLTF(modelPath);
  const ref = useRef<Group>(null);

  if (recolor) {
    scene.traverse((child) => {
      const mesh = child as Mesh;
      if (!mesh.isMesh || !mesh.material) return;
      const mat = (mesh.material as THREE.MeshStandardMaterial).clone();
      const n = mesh.name.toLowerCase();
      if (n.includes('glass') || n.includes('window')) {
        mat.color.set('#e8f4f8');
        mat.roughness = 0.05;
        mat.metalness = 0.1;
        mat.transparent = true;
        mat.opacity = 0.5;
      } else if (n.includes('wheel') || n.includes('tire') || n.includes('rubber')) {
        mat.color.set('#cccccc');
        mat.roughness = 0.8;
        mat.metalness = 0.0;
      } else {
        mat.color.set('#ffffff');
        mat.roughness = 0.2;
        mat.metalness = 0.1;
      }
      mesh.material = mat;
    });
  }

  useFrame((_, delta) => {
    if (ref.current) ref.current.rotation.y += delta * 0.45;
  });

  return (
    <group ref={ref}>
      <primitive object={scene} scale={scale} position={[0, posY, 0]} />
    </group>
  );
}

interface Car3DProps {
  carType?: string;
  height?: number;
  className?: string;
  interactive?: boolean;
  showLabel?: boolean;
}

export const Car3D = ({
  carType = 'all',
  height = 220,
  className = '',
  interactive = true,
  showLabel = true,
}: Car3DProps) => {

  const [currentIndex, setCurrentIndex] = useState(0);
  // ✅ Controla la opacidad para la transición fade suave
  const [opacity, setOpacity] = useState(1);

  useEffect(() => {
    if (carType !== 'all') return;

    const interval = setInterval(() => {
      // 1. Fade out
      setOpacity(0);

      // 2. Cambiar modelo cuando ya está invisible
      setTimeout(() => {
        setCurrentIndex(prev => getNextRandom(prev));
        // 3. Fade in
        setOpacity(1);
      }, 400); // duración del fade out

    }, 3500); // cada 3.5 segundos

    return () => clearInterval(interval);
  }, [carType]);

  const resolvedType = carType === 'all' ? ALL_TYPES[currentIndex] : carType;
  const cfg = CONFIG[resolvedType] ?? CONFIG['small'];

  return (
    <div className={`flex flex-col items-center ${className}`}>
      {/* ✅ Transición fade con CSS transition en opacity */}
      <div
        style={{
          height,
          width: '100%',
          opacity,
          transition: 'opacity 0.4s ease-in-out',
        }}
      >
        <Canvas
          camera={{ position: [4, 1.8, 4], fov: 35 }}
          style={{ background: 'transparent' }}
          gl={{ alpha: true, antialias: true }}
        >
          <ambientLight intensity={1.0} />
          <directionalLight position={[5, 8, 5]}  intensity={1.2} castShadow />
          <directionalLight position={[-4, 3, -3]} intensity={0.6} />
          <pointLight       position={[0, 5, 0]}   intensity={0.4} color="#5eead4" />

          <Suspense fallback={null}>
            <CarModel
              modelPath={cfg.model}
              scale={cfg.scale}
              recolor={cfg.recolor}
              posY={cfg.posY}
            />
            <ContactShadows position={[0, -0.55, 0]} opacity={0.15} scale={6} blur={2.5} />
          </Suspense>

          {interactive && (
            <OrbitControls
              enableZoom={false}
              enablePan={false}
              minPolarAngle={Math.PI / 5}
              maxPolarAngle={Math.PI / 2.2}
            />
          )}
        </Canvas>
      </div>

      {showLabel && (
        <p
          className="text-[9px] font-black uppercase tracking-[0.3em] text-teal-900/40 mt-1"
          style={{ opacity, transition: 'opacity 0.4s ease-in-out' }}
        >
          {cfg.label}
        </p>
      )}
    </div>
  );
};

const uniqueModels = [...new Set(Object.values(CONFIG).map(c => c.model))];
uniqueModels.forEach(path => useGLTF.preload(path));