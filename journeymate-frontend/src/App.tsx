import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";

// Componentes
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import { SearchForm } from './components/SearchForm';
import { ResultsList } from './components/results/ResultsList';
import { HotelDetailsModal } from './components/results/HotelDetailsModal';

// Servicios
import { performSearch, getHotelDetails } from './services/searchService';

// Iconos
import { Hotel, Plane, Car, Ticket, Ship, Train, Search } from 'lucide-react';

function App() {
  // --- 1. ESTADOS ---
  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any[]>([]);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedHotelDetails, setSelectedHotelDetails] = useState<any>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [selectedHotelBasic, setSelectedHotelBasic] = useState<any>(null);


  // El estado mantiene los objetos Date originales y strings puros
  const [searchData, setSearchData] = useState({
    origin: '',
    destination: '',
    startDate: new Date(),
    endDate: new Date(),
    adults: '2',
    roomQty: '1',
    pickupTime: '10:00',
    cabinClass: 'ECONOMY'
  });

  // --- 2. REFERENCIAS ---
  const iconRef = useRef<HTMLDivElement>(null);
  const resultsRef = useRef<HTMLDivElement>(null);

  // --- 3. EFECTOS (ANIMACIONES) ---
  useEffect(() => {
    setResults([]); 
    if (iconRef.current) {
      anime({
        targets: iconRef.current,
        rotate: [0, 360], scale: [0.5, 1], opacity: [0, 1],
        duration: 800, easing: "easeOutBack"
      });
    }
  }, [activeSection]);

  // --- 4. MANEJADORES DE EVENTOS ---

  const handleChange = (field: string, value: any) => {
    setSearchData(prev => ({ ...prev, [field]: value }));
  };

  const handleSearch = async () => {
    if (!searchData.destination && activeSection !== 'coches') {
      alert("Por favor, introduce un destino.");
      return;
    }

    setLoading(true);
    setResults([]);

    try {
      // Pasamos 'searchData' tal cual. El Service/Mapper se encarga del formato.
      const data = await performSearch(activeSection, searchData);
      
      const finalResults = Array.isArray(data) ? data : (data?.result || data?.hotels || []);
      setResults(finalResults);

      if (resultsRef.current) {
        setTimeout(() => {
          resultsRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
        }, 300);
      }
    } catch (error) {
      console.error("Error en búsqueda:", error);
      alert("Error en el servidor.");
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = async (hotel: any) => {
    setSelectedHotelBasic(hotel);
    setIsModalOpen(true);
    setModalLoading(true);
    setSelectedHotelDetails(null);

    try {
      const details = await getHotelDetails(hotel.hotelId, searchData);
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
        
        {/* Icono de Sección */}
        <div ref={iconRef} className="mb-8 bg-white/20 backdrop-blur-3xl p-8 rounded-[3rem] border border-white/40 shadow-2xl">
          {activeSection === 'alojamiento' && <Hotel size={70} className="text-teal-900" />}
          {activeSection === 'vuelos' && <Plane size={70} className="text-teal-900" />}
          {activeSection === 'coches' && <Car size={70} className="text-teal-900" />}
          {activeSection === 'actividades' && <Ticket size={70} className="text-teal-900" />}
          {activeSection === 'cruceros' && <Ship size={70} className="text-teal-900" />}
          {activeSection === 'trenes' && <Train size={70} className="text-teal-900" />}
        </div>

        {/* Buscador */}
        <div className="w-full max-w-6xl backdrop-blur-2xl bg-white/40 rounded-[4rem] border border-white/60 shadow-2xl p-8 lg:p-12 text-center">
          <h2 className="text-5xl md:text-7xl font-black text-teal-900 tracking-tighter uppercase mb-2 leading-none">
            JourneyMate <span className="text-teal-600/40">{activeSection}</span>
          </h2>
          <p className="text-teal-900/50 font-bold text-[10px] uppercase tracking-[0.6em] mb-12">
            Tu compañero de viaje inteligente
          </p>

          <div className="grid grid-cols-1 md:grid-cols-6 gap-4 bg-white/30 p-4 rounded-[3rem] border border-white/30">
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

        {/* Resultados */}
        <div ref={resultsRef} className="w-full mt-12">
          <ResultsList 
            results={results} 
            activeSection={activeSection} 
            onViewDetails={handleViewDetails}
            destination={searchData.destination}
          />
        </div>

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
        searchData={searchData}
        hotelBasicData={selectedHotelBasic}
      />

      <Footer />
    </div>
  );
}

export default App;