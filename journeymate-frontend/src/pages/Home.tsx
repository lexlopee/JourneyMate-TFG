import { useEffect, useRef, useState, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AITravelAssistant } from '../components/AITravelAssistant';
import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  ArrowRight, Star, Zap, Globe, Shield, ChevronDown,
  ChevronLeft, ChevronRight, BookOpen, LogOut
} from 'lucide-react';

// ── Imágenes del carrusel hero ────────────────────────────
import foto1  from '../assets/foto1.jpg';
import foto2  from '../assets/foto2.jpg';
import foto3  from '../assets/foto3.jpg';
import foto4  from '../assets/foto4.jpg';
import foto5  from '../assets/foto5.jpg';
import foto6  from '../assets/foto6.jpg';
import foto7  from '../assets/foto7.jpg';
import foto9  from '../assets/foto9.jpg';
import foto10 from '../assets/foto10.jpg';
import foto11 from '../assets/foto11.jpg';

// ── Imágenes de destinos ──────────────────────────────────
import imgTokio    from '../assets/fotoTokio.jpg';
import imgRoma     from '../assets/foto5.jpg';
import imgBali     from '../assets/fotoBali.png';
import imgNuevaYork from '../assets/fotoNuevaYork.jpg';
import imgDubai    from '../assets/fotoDubai.jpg';
import imgParis    from '../assets/fotoParis.jpg';

const CAROUSEL_IMAGES = [foto1, foto2, foto3, foto4, foto5, foto6, foto7, foto9, foto10, foto11];

// ── Categorías ────────────────────────────────────────────
const CATEGORIES = [
  { id: 'alojamiento', label: 'Alojamiento', icon: Hotel,  color: 'from-teal-400 to-teal-600',    desc: 'Hoteles y apartamentos' },
  { id: 'vuelos',      label: 'Vuelos',      icon: Plane,  color: 'from-sky-400 to-sky-600',      desc: 'Vuelos directos y con escala' },
  { id: 'coches',      label: 'Coches',      icon: Car,    color: 'from-amber-400 to-amber-600',  desc: 'Alquiler sin complicaciones' },
  { id: 'actividades', label: 'Actividades', icon: Ticket, color: 'from-violet-400 to-violet-600',desc: 'Experiencias únicas' },
  { id: 'cruceros',    label: 'Cruceros',    icon: Ship,   color: 'from-blue-400 to-blue-600',    desc: 'Navega por el mundo' },
  { id: 'trenes',      label: 'Trenes',      icon: Train,  color: 'from-rose-400 to-rose-600',    desc: 'Viaja por tierra' },
] as const;

// ── Destinos con foto e IATA ──────────────────────────────
const DESTINOS = [
  { nombre: 'Tokio',      tag: 'Tendencia', iata: 'TYO', destText: 'Tokio',       img: imgTokio },
  { nombre: 'Roma',       tag: 'Cultura',   iata: 'ROM', destText: 'Roma',        img: imgRoma },
  { nombre: 'Bali',       tag: 'Relax',     iata: 'DPS', destText: 'Bali',        img: imgBali },
  { nombre: 'Nueva York', tag: 'Urbano',    iata: 'NYC', destText: 'Nueva York',  img: imgNuevaYork },
  { nombre: 'Dubái',      tag: 'Lujo',      iata: 'DXB', destText: 'Dubái',       img: imgDubai },
  { nombre: 'París',      tag: 'Romántico', iata: 'PAR', destText: 'París',       img: imgParis },
];

const STATS = [
  { valor: '2M+',  label: 'Viajeros satisfechos' },
  { valor: '150+', label: 'Destinos disponibles' },
  { valor: '24/7', label: 'Soporte con IA' },
  { valor: '€0',   label: 'Sin comisiones ocultas' },
];

export default function Home() {
  const navigate   = useNavigate();
  const heroRef    = useRef<HTMLDivElement>(null);
  const [userName, setUserName] = useState('');
  const [dropOpen, setDropOpen] = useState(false);

  // ── Carrusel ──
  const [current, setCurrent] = useState(0);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const next = useCallback(() => setCurrent(c => (c + 1) % CAROUSEL_IMAGES.length), []);
  const prev = useCallback(() => setCurrent(c => (c - 1 + CAROUSEL_IMAGES.length) % CAROUSEL_IMAGES.length), []);

  // Autoplay cada 3s
  useEffect(() => {
    timerRef.current = setInterval(next, 3000);
    return () => { if (timerRef.current) clearInterval(timerRef.current); };
  }, [next]);

  const pauseAndNext = () => {
    if (timerRef.current) clearInterval(timerRef.current);
    next();
    timerRef.current = setInterval(next, 3000);
  };
  const pauseAndPrev = () => {
    if (timerRef.current) clearInterval(timerRef.current);
    prev();
    timerRef.current = setInterval(next, 3000);
  };

  useEffect(() => {
    const nombre = localStorage.getItem('userName');
    if (nombre) setUserName(nombre);

    const handler = (e: MouseEvent) => {
      const el = document.getElementById('home-user-drop');
      if (el && !el.contains(e.target as Node)) setDropOpen(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const goToSearch = (section?: string) => navigate(section ? `/buscar?tab=${section}` : '/buscar');

  const goToDestino = (d: typeof DESTINOS[number]) => {
    navigate(`/buscar?tab=vuelos&toId=${d.iata}&destText=${encodeURIComponent(d.destText)}`);
  };

  const logout = () => {
    ["token","idUsuario","userName","userEmail"].forEach(k => localStorage.removeItem(k));
    setUserName('');
    setDropOpen(false);
  };

  return (
    <div className="min-h-screen font-sans overflow-x-hidden"
      style={{ background: 'linear-gradient(160deg, #0d4f4c 0%, #1cb5b0 40%, #3d7a6e 75%, #0d4f4c 100%)' }}>

      {/* ── NAVBAR ───────────────────────────────────────── */}
      <nav className="fixed top-0 left-0 right-0 z-50 px-4 pt-3">
        <div className="max-w-6xl mx-auto bg-white/15 backdrop-blur-xl border border-white/20
          rounded-2xl px-5 py-3 flex items-center justify-between shadow-lg">
          <div className="flex items-center gap-2">
            <span className="text-white font-black text-xl tracking-tighter uppercase">JourneyMate</span>
            <span className="text-white/40 text-[8px] font-bold tracking-[0.3em] hidden sm:block">TRAVEL SMART</span>
          </div>

          <div className="flex items-center gap-3">
            {userName ? (
              <div className="flex items-center gap-2">
                <span className="text-white/70 text-sm font-semibold hidden sm:block">
                  Hola, <span className="text-white font-black">{userName}</span>
                </span>
                <Link to="/mis-reservas"
                  className="flex items-center gap-1.5 text-white/80 hover:text-white text-xs font-bold
                    border border-white/20 px-3 py-2 rounded-xl hover:border-white/40 hover:bg-white/10 transition-all">
                  <BookOpen size={13}/> Mis reservas
                </Link>
                <button onClick={() => goToSearch()}
                  className="bg-white text-teal-900 px-4 py-2 rounded-xl font-black text-xs uppercase tracking-widest hover:bg-teal-50 transition-all shadow">
                  Buscar
                </button>
                <div id="home-user-drop" className="relative">
                  <button onClick={() => setDropOpen(p => !p)}
                    className="w-8 h-8 rounded-full bg-teal-500 flex items-center justify-center
                      text-white font-black text-sm hover:bg-teal-400 transition-all">
                    {userName[0].toUpperCase()}
                  </button>
                  {dropOpen && (
                    <div className="absolute right-0 mt-2 w-44 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50">
                      <button onClick={logout}
                        className="w-full flex items-center gap-3 px-4 py-3 text-sm font-semibold text-red-600 hover:bg-red-50 transition-colors">
                        <LogOut size={14}/> Cerrar sesión
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link to="/login"
                  className="text-white/80 hover:text-white text-sm font-bold transition-colors px-3 py-1.5">
                  Entrar
                </Link>
                <Link to="/register"
                  className="bg-white text-teal-900 px-4 py-2 rounded-xl font-black text-xs uppercase tracking-widest hover:bg-teal-50 transition-all shadow">
                  Registrarse
                </Link>
              </div>
            )}
          </div>
        </div>
      </nav>

      {/* ── HERO con CARRUSEL ────────────────────────────── */}
      <section ref={heroRef} className="relative min-h-screen flex flex-col items-center justify-center px-4 pt-20 overflow-hidden">

        {/* Carrusel de fondo */}
        <div className="absolute inset-0">
          {CAROUSEL_IMAGES.map((img, i) => (
            <div key={i}
              className="absolute inset-0 transition-opacity duration-1000"
              style={{ opacity: i === current ? 1 : 0 }}>
              <img src={img} alt="" className="w-full h-full object-cover" />
              {/* Overlay oscuro para legibilidad */}
              <div className="absolute inset-0"
                style={{ background: 'linear-gradient(to bottom, rgba(13,79,76,0.7) 0%, rgba(13,79,76,0.5) 50%, rgba(13,79,76,0.8) 100%)' }} />
            </div>
          ))}

          {/* Controles del carrusel */}
          <button onClick={pauseAndPrev}
            className="absolute left-4 top-1/2 -translate-y-1/2 z-10
              w-10 h-10 bg-white/20 backdrop-blur border border-white/30
              rounded-full flex items-center justify-center text-white
              hover:bg-white/30 transition-all">
            <ChevronLeft size={18}/>
          </button>
          <button onClick={pauseAndNext}
            className="absolute right-4 top-1/2 -translate-y-1/2 z-10
              w-10 h-10 bg-white/20 backdrop-blur border border-white/30
              rounded-full flex items-center justify-center text-white
              hover:bg-white/30 transition-all">
            <ChevronRight size={18}/>
          </button>

          {/* Dots */}
          <div className="absolute bottom-8 left-1/2 -translate-x-1/2 flex gap-1.5 z-10">
            {CAROUSEL_IMAGES.map((_, i) => (
              <button key={i} onClick={() => setCurrent(i)}
                className={`rounded-full transition-all duration-300
                  ${i === current ? 'w-6 h-2 bg-white' : 'w-2 h-2 bg-white/40'}`}
              />
            ))}
          </div>
        </div>

        {/* Contenido encima del carrusel */}
        <div className="relative z-10 text-center max-w-5xl mx-auto">
          <div className="inline-flex items-center gap-2 bg-white/15 backdrop-blur border border-white/20 rounded-full px-4 py-2 mb-8 shadow-lg">
            <Zap size={12} className="text-yellow-300" />
            <span className="text-white/90 text-xs font-black uppercase tracking-widest">Tu compañero de viaje inteligente</span>
          </div>

          <h1 className="text-6xl sm:text-7xl md:text-8xl lg:text-9xl font-black text-white leading-none tracking-tighter mb-6 drop-shadow-2xl">
            VIAJA<br />
            <span className="text-transparent bg-clip-text"
              style={{ backgroundImage: 'linear-gradient(90deg, #e9fc9e, #6ee7b7)' }}>SIN</span><br />
            LÍMITES
          </h1>

          <p className="text-white/80 text-lg sm:text-xl font-medium max-w-xl mx-auto mb-10 leading-relaxed drop-shadow">
            Hoteles, vuelos, coches, actividades y más — todo en un solo lugar, con precios reales y sin sorpresas.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
            <button onClick={() => goToSearch()}
              className="group flex items-center gap-3 bg-white text-teal-900 px-8 py-4
                rounded-2xl font-black text-base uppercase tracking-widest
                hover:bg-teal-50 transition-all shadow-2xl shadow-black/30 hover:scale-105 active:scale-95">
              Empezar a explorar
              <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
            </button>
            {!userName && (
              <Link to="/login"
                className="flex items-center gap-2 text-white/90 hover:text-white font-bold text-sm
                  border border-white/30 px-6 py-4 rounded-2xl hover:border-white/50 hover:bg-white/10 transition-all backdrop-blur">
                Ya tengo cuenta
              </Link>
            )}
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 max-w-2xl mx-auto">
            {STATS.map(s => (
              <div key={s.valor} className="bg-black/20 backdrop-blur border border-white/20 rounded-2xl p-4 text-center">
                <p className="text-white font-black text-2xl leading-none">{s.valor}</p>
                <p className="text-white/60 text-[10px] font-bold uppercase tracking-wider mt-1">{s.label}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="absolute bottom-20 left-1/2 -translate-x-1/2 animate-bounce z-10">
          <ChevronDown size={24} className="text-white/50" />
        </div>
      </section>

      {/* ── CATEGORÍAS ───────────────────────────────────── */}
      <section className="px-4 py-20">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-4xl sm:text-5xl font-black text-white tracking-tighter mb-3">¿Qué buscas?</h2>
            <p className="text-white/60 font-medium">Elige tu categoría y empieza a buscar</p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            {CATEGORIES.map(({ id, label, icon: Icon, color, desc }) => (
              <button key={id} onClick={() => goToSearch(id)}
                className="group relative bg-white/10 backdrop-blur-xl border border-white/20
                  rounded-3xl p-6 text-left hover:bg-white/20 hover:border-white/40
                  hover:scale-[1.02] hover:-translate-y-1 transition-all duration-300 shadow-lg overflow-hidden">
                <div className={`absolute inset-0 bg-gradient-to-br ${color} opacity-0 group-hover:opacity-10 transition-opacity duration-300 rounded-3xl`} />
                <div className={`inline-flex p-3 rounded-2xl bg-gradient-to-br ${color} mb-4 shadow-lg`}>
                  <Icon size={22} className="text-white" />
                </div>
                <h3 className="text-white font-black text-lg tracking-tight mb-1">{label}</h3>
                <p className="text-white/60 text-xs font-medium">{desc}</p>
                <ArrowRight size={16}
                  className="absolute bottom-5 right-5 text-white/30 group-hover:text-white/70 group-hover:translate-x-1 transition-all duration-300" />
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* ── DESTINOS con FOTOS ───────────────────────────── */}
      <section className="px-4 py-20">
        <div className="max-w-6xl mx-auto">
          <div className="flex items-end justify-between mb-12 flex-wrap gap-4">
            <div>
              <h2 className="text-4xl sm:text-5xl font-black text-white tracking-tighter mb-2">Destinos populares</h2>
              <p className="text-white/60 font-medium">Pincha en uno para buscar vuelos directamente</p>
            </div>
            <button onClick={() => goToSearch('vuelos')}
              className="flex items-center gap-2 text-white/70 hover:text-white font-bold text-sm
                border border-white/20 px-4 py-2 rounded-xl hover:border-white/40 transition-all">
              Ver vuelos <ArrowRight size={14} />
            </button>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            {DESTINOS.map(d => (
              <button key={d.nombre} onClick={() => goToDestino(d)}
                className="group relative rounded-2xl overflow-hidden
                  hover:scale-105 hover:-translate-y-1 transition-all duration-300 shadow-lg"
                style={{ aspectRatio: '3/4' }}>
                {/* Foto */}
                <img src={d.img} alt={d.nombre}
                  className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                {/* Overlay degradado */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/10 to-transparent" />
                {/* Contenido */}
                <div className="absolute bottom-0 left-0 right-0 p-3 text-left">
                  <p className="text-white font-black text-sm leading-tight drop-shadow">{d.nombre}</p>
                  <span className="inline-flex items-center gap-1 mt-1 bg-white/20 backdrop-blur text-white/90 text-[9px]
                    font-black uppercase tracking-wider px-2 py-0.5 rounded-full border border-white/20">
                    <Plane size={7}/> {d.tag}
                  </span>
                </div>
              </button>
            ))}
          </div>
        </div>
      </section>

      {/* ── FEATURES ─────────────────────────────────────── */}
      <section className="px-4 py-20">
        <div className="max-w-6xl mx-auto">
          <div className="grid md:grid-cols-3 gap-6">
            {[
              { icon: <Zap size={28} className="text-yellow-300" />, titulo: 'Resultados en segundos', desc: 'Comparamos miles de opciones en tiempo real para darte los mejores precios disponibles.' },
              { icon: <Globe size={28} className="text-teal-300" />, titulo: 'Asistente IA integrado', desc: 'Nuestro asistente inteligente te ayuda a planificar tu viaje perfecto con recomendaciones personalizadas.' },
              { icon: <Shield size={28} className="text-lime-300" />, titulo: 'Pago 100% seguro', desc: 'Stripe y PayPal integrados. Tus datos siempre protegidos con cifrado SSL de última generación.' },
            ].map((f, i) => (
              <div key={i} className="bg-white/10 border border-white/20 backdrop-blur rounded-3xl p-7">
                <div className="mb-4">{f.icon}</div>
                <h3 className="text-white font-black text-xl mb-2">{f.titulo}</h3>
                <p className="text-white/70 text-sm leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA FINAL ────────────────────────────────────── */}
      <section className="px-4 py-24">
        <div className="max-w-3xl mx-auto text-center">
          <div className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-[3rem] p-12 shadow-2xl">
            <div className="flex justify-center gap-1 mb-6">
              {[...Array(5)].map((_, i) => <Star key={i} size={20} fill="currentColor" className="text-yellow-300" />)}
            </div>
            <h2 className="text-4xl sm:text-5xl font-black text-white tracking-tighter mb-4">
              Empieza tu próxima aventura
            </h2>
            <p className="text-white/70 text-base mb-8 leading-relaxed">
              Miles de viajeros ya confían en JourneyMate para planificar sus viajes. Únete gratis y descubre un mundo de posibilidades.
            </p>
            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              {userName ? (
                <button onClick={() => goToSearch()}
                  className="group flex items-center justify-center gap-3 bg-white text-teal-900
                    px-8 py-4 rounded-2xl font-black text-sm uppercase tracking-widest
                    hover:bg-teal-50 transition-all shadow-xl hover:scale-105">
                  Buscar ahora <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
                </button>
              ) : (
                <>
                  <Link to="/register"
                    className="group flex items-center justify-center gap-3 bg-white text-teal-900
                      px-8 py-4 rounded-2xl font-black text-sm uppercase tracking-widest
                      hover:bg-teal-50 transition-all shadow-xl hover:scale-105">
                    Crear cuenta gratis <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
                  </Link>
                  <button onClick={() => goToSearch()}
                    className="flex items-center justify-center gap-2 text-white/80 hover:text-white
                      font-bold text-sm border border-white/30 px-6 py-4 rounded-2xl
                      hover:border-white/60 hover:bg-white/10 transition-all">
                    Explorar sin cuenta
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* ── FOOTER ───────────────────────────────────────── */}
      <footer className="px-4 py-8 border-t border-white/10">
        <div className="max-w-6xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <span className="text-white/50 font-black text-sm tracking-tighter uppercase">JourneyMate · Travel Smart</span>
          <span className="text-white/30 text-xs">© 2026 JourneyMate TFG · Todos los derechos reservados</span>
        </div>
      </footer>

      <AITravelAssistant />
    </div>
  );
}