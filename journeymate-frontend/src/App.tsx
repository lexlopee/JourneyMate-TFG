import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import { performSearch, getCruiseCatalogs } from './services/searchService';
import { SearchForm } from './components/SearchForm';
import { Hotel, Plane, Car, Ticket, Ship, Train, Search } from 'lucide-react';

function App() {
  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);
  const [cruiseData, setCruiseData] = useState({ destinations: [], ports: [] });

  const iconRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const [searchData, setSearchData] = useState({
    origin: '', destination: '', startDate: '', endDate: '',
    adults: '2', pickupTime: '10:00', cabinClass: 'ECONOMY'
  });

  // Animaciones
  useEffect(() => {
    if (iconRef.current) anime({ targets: iconRef.current, rotate: [0, 360], scale: [0.8, 1], duration: 600, easing: "easeOutBack" });
    if (containerRef.current) anime({ targets: containerRef.current, opacity: [0, 1], translateY: [20, 0], duration: 500, easing: "easeOutQuad" });
    anime({ targets: ".search-input", opacity: [0, 1], translateY: [10, 0], delay: anime.stagger(80), duration: 400, easing: "easeOutQuad" });
  }, [activeSection]);

  // Catálogos de cruceros
  useEffect(() => {
    if (activeSection === 'cruceros') {
      getCruiseCatalogs().then(setCruiseData).catch(console.error);
    }
  }, [activeSection]);

  const handleChange = (field: string, value: string) => {
    setSearchData(prev => ({ ...prev, [field]: value }));
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      const results = await performSearch(activeSection, searchData);
      console.log("RESULTADOS API:", results);
      alert(`Búsqueda de ${activeSection} completada. Revisa la consola.`);
    } catch (error) {
      alert("Error al conectar con el servidor.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-sans overflow-x-hidden" style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}>
      <Navbar activeTab={activeSection} onTabChange={setActiveSection} />

      <main className="relative z-10 pt-32 pb-20 px-6 flex flex-col items-center flex-grow">
        <div ref={iconRef} className="mb-6 bg-white/20 backdrop-blur-2xl p-6 rounded-[2.5rem] border border-white/30 shadow-2xl">
          {activeSection === 'alojamiento' && <Hotel size={60} className="text-teal-900" />}
          {activeSection === 'vuelos' && <Plane size={60} className="text-teal-900" />}
          {activeSection === 'coches' && <Car size={60} className="text-teal-900" />}
          {activeSection === 'actividades' && <Ticket size={60} className="text-teal-900" />}
          {activeSection === 'cruceros' && <Ship size={60} className="text-teal-900" />}
          {activeSection === 'trenes' && <Train size={60} className="text-teal-900" />}
        </div>

        <div ref={containerRef} className="w-full max-w-5xl backdrop-blur-xl bg-white/40 rounded-[3rem] border border-white/50 shadow-2xl p-10 text-center">
          <h2 className="text-4xl md:text-6xl font-black text-teal-900 tracking-tighter uppercase mb-2">
            JourneyMate <span className="text-teal-600/50">{activeSection}</span>
          </h2>
          <p className="text-teal-900/40 font-bold text-xs uppercase tracking-[0.5em] mb-10">Explora el mundo sin límites</p>

          <div className="grid grid-cols-1 md:grid-cols-5 gap-3 bg-white/30 p-3 rounded-[2.5rem] border border-white/20">
            <SearchForm activeSection={activeSection} searchData={searchData} handleChange={handleChange} />
            
            <button onClick={handleSearch} disabled={loading} className="bg-teal-900 text-white rounded-2xl font-black uppercase text-[10px] tracking-widest hover:bg-teal-800 transition-all flex flex-col items-center justify-center gap-1 shadow-xl disabled:opacity-50">
              {loading ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : <><Search size={20} /><span>BUSCAR</span></>}
            </button>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default App;