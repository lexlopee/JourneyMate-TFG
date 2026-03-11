import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import { performSearch, getHotelDetails } from './services/searchService';
import { SearchForm } from './components/SearchForm';
import { ResultsList } from './components/results/ResultsList';
import { HotelDetailsModal } from './components/results/HotelDetailsModal';
import { Hotel, Plane, Car, Ticket, Ship, Train, Search } from 'lucide-react';

function App() {
  // --- ESTADOS ---
  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any[]>([]);
  
  // Estados para el Modal de Detalles
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedHotelDetails, setSelectedHotelDetails] = useState<any>(null);
  const [modalLoading, setModalLoading] = useState(false);

  // Datos del formulario
  const [searchData, setSearchData] = useState({
    origin: '',
    destination: '',
    startDate: new Date(),   // ← FECHA DE HOY POR DEFECTO
    endDate: new Date(),     // ← FECHA DE HOY POR DEFECTO
    adults: '2',
    pickupTime: '10:00',
    cabinClass: 'ECONOMY'
  });

  // --- REFERENCIAS PARA ANIME.JS ---
  const iconRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // --- EFECTOS (ANIMACIONES) ---
  useEffect(() => {
    setResults([]);

    if (iconRef.current) {
      anime({
        targets: iconRef.current,
        rotate: [0, 360],
        scale: [0.5, 1],
        opacity: [0, 1],
        duration: 800,
        easing: "easeOutBack"
      });
    }

    if (containerRef.current) {
      anime({
        targets: containerRef.current,
        opacity: [0, 1],
        translateY: [30, 0],
        duration: 600,
        easing: "easeOutQuad"
      });
    }

    anime({
      targets: ".search-input-field",
      opacity: [0, 1],
      translateY: [15, 0],
      delay: anime.stagger(100),
      duration: 500,
      easing: "easeOutExpo"
    });
  }, [activeSection]);

  // --- MANEJADORES ---

  const handleChange = (field: string, value: any) => {
    setSearchData(prev => ({ ...prev, [field]: value }));
  };

  // 🔥 FORMATEADOR DE FECHAS PARA EL BACKEND
  const formatDate = (d: any) => {
    if (!d) return "";
    const date = new Date(d);
    return date.toISOString().split("T")[0]; // YYYY-MM-DD
  };

  // 🔥 NORMALIZADOR DE ESPACIOS → "_"
  const normalize = (text: string) => {
    if (!text) return "";
    return text.trim().replace(/\s+/g, "_");
  };

  const handleSearch = async () => {
    if (!searchData.destination && activeSection !== 'coches') {
      alert("Por favor, introduce un destino para buscar.");
      return;
    }

    setLoading(true);
    setResults([]);

    try {
      // 🔥 CORREGIDO: ENVIAMOS FECHAS COMO STRINGS Y DESTINO NORMALIZADO
      const payload = {
        ...searchData,
        destination: normalize(searchData.destination),
        origin: normalize(searchData.origin),
        startDate: formatDate(searchData.startDate),
        endDate: formatDate(searchData.endDate)
      };

      const data = await performSearch(activeSection, payload);

      if (data && Array.isArray(data)) {
        setResults(data);
      } else if (data?.result) { 
        setResults(data.result);
      } else if (data?.hotels) {
        setResults(data.hotels);
      }
      
    } catch (error) {
      console.error("Error en la búsqueda:", error);
      alert("Hubo un problema al conectar con el servidor. Verifica tu conexión.");
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = async (hotelId: string) => {
    setIsModalOpen(true);
    setModalLoading(true);
    setSelectedHotelDetails(null);
    try {
      const details = await getHotelDetails(hotelId, {
        ...searchData,
        destination: normalize(searchData.destination),
        origin: normalize(searchData.origin),
        startDate: formatDate(searchData.startDate),
        endDate: formatDate(searchData.endDate)
      });
      setSelectedHotelDetails(details);
    } catch (error) {
      console.error("Error al obtener detalles:", error);
    } finally {
      setModalLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-sans overflow-x-hidden selection:bg-teal-200" 
         style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}>
      
      <Navbar activeTab={activeSection} onTabChange={setActiveSection} />

      <main className="relative z-10 pt-32 pb-20 px-6 flex flex-col items-center flex-grow">
        
        {/* Icono Dinámico Animado */}
        <div ref={iconRef} className="mb-8 bg-white/20 backdrop-blur-3xl p-8 rounded-[3rem] border border-white/40 shadow-2xl">
          {activeSection === 'alojamiento' && <Hotel size={70} className="text-teal-900" />}
          {activeSection === 'vuelos' && <Plane size={70} className="text-teal-900" />}
          {activeSection === 'coches' && <Car size={70} className="text-teal-900" />}
          {activeSection === 'actividades' && <Ticket size={70} className="text-teal-900" />}
          {activeSection === 'cruceros' && <Ship size={70} className="text-teal-900" />}
          {activeSection === 'trenes' && <Train size={70} className="text-teal-900" />}
        </div>

        {/* Contenedor del Buscador Animado */}
        <div 
          ref={containerRef} 
          className="w-full max-w-5xl backdrop-blur-2xl bg-white/40 rounded-[4rem] border border-white/60 shadow-2xl p-12 text-center overflow-visible"
        >
          
          <h2 className="text-5xl md:text-7xl font-black text-teal-900 tracking-tighter uppercase mb-2 leading-none">
            JourneyMate <span className="text-teal-600/40">{activeSection}</span>
          </h2>
          
          <p className="text-teal-900/50 font-bold text-[10px] uppercase tracking-[0.6em] mb-12">
            Tu compañero de viaje inteligente
          </p>

          <div className="
            grid 
            grid-cols-1 
            sm:grid-cols-2 
            md:grid-cols-3 
            lg:grid-cols-5 
            gap-4 
            bg-white/30 
            p-4 
            rounded-[3rem] 
            border border-white/30
          ">
            
            <SearchForm 
              activeSection={activeSection} 
              searchData={searchData} 
              handleChange={handleChange} 
            />

            <button 
              onClick={handleSearch}
              disabled={loading}
              className="bg-teal-900 text-white rounded-[2rem] font-black uppercase text-[11px] tracking-widest hover:bg-teal-800 transition-all flex flex-col items-center justify-center gap-2 shadow-2xl disabled:opacity-50 group min-h-[80px]"
            >
              {loading ? (
                <div className="w-6 h-6 border-3 border-white/20 border-t-white rounded-full animate-spin" />
              ) : (
                <>
                  <Search size={24} className="group-hover:scale-125 transition-transform duration-300" />
                  <span>BUSCAR</span>
                </>
              )}
            </button>
          </div>
        </div>

        <ResultsList 
          results={results} 
          activeSection={activeSection} 
          onViewDetails={handleViewDetails}
          destination={searchData.destination}
        />

        {!loading && results.length === 0 && (
           <p className="mt-16 text-teal-900/30 font-black uppercase tracking-widest text-xs animate-pulse">
             {searchData.destination === '' ? "Escribe un destino y comienza a explorar" : "No hay resultados para mostrar"}
           </p>
        )}

      </main>

      <HotelDetailsModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        details={selectedHotelDetails}
        loading={modalLoading}
      />

      <Footer />
    </div>
  );
}

export default App;
