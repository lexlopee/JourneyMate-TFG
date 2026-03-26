import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";

// Componentes y Servicios
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import { SearchForm } from './components/SearchForm';
import { ResultsList } from './components/results/ResultsList';
import { HotelDetailsModal } from './components/results/HotelDetailsModal';
import { FlightDetailsModal } from './components/results/FlightDetailsModal'; 
import { AITravelAssistant } from './components/AITravelAssistant';
import { LoadingVideo } from './components/LoadingVideo'; // ⭐ NUEVO
import { performSearch, getHotelDetails, getFlightDetails } from './services/searchService'; 
import { formatDateForBackend } from './utils/dateUtils'; 

// Iconos
import { Hotel, Plane, Car, Ticket, Ship, Train, Search } from 'lucide-react';

function App() {
  const todayStr = new Date().toISOString().split('T')[0];
  const tomorrowStr = new Date(Date.now() + 86400000).toISOString().split('T')[0];
  const dayAfterTomorrowStr = new Date(Date.now() + 172800000).toISOString().split('T')[0];

  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any[]>([]);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedHotelDetails, setSelectedHotelDetails] = useState<any>(null);
  const [selectedHotelBasic, setSelectedHotelBasic] = useState<any>(null);
  const [isFlightModalOpen, setIsFlightModalOpen] = useState(false);
  const [selectedFlightDetails, setSelectedFlightDetails] = useState<any>(null);
  const [selectedFlightBasic, setSelectedFlightBasic] = useState<any>(null);
  const [modalLoading, setModalLoading] = useState(false);

  const [searchData, setSearchData] = useState({
    fromId: '', 
    toId: '',
    originText: '',      
    destinationText: '', 
    destination: '',     
    startDate: tomorrowStr,
    endDate: dayAfterTomorrowStr,
    adults: 1,           
    childrenAge: '',     
    cabinClass: 'ECONOMY',
    sort: 'BEST',
    currencyCode: 'EUR'  
  });

  const iconRef = useRef<HTMLDivElement>(null);
  const resultsRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (iconRef.current) {
      anime({
        targets: iconRef.current,
        rotate: [0, 360], scale: [0.5, 1], opacity: [0, 1],
        duration: 800, easing: "easeOutBack"
      });
    }
  }, [activeSection]);

  const handleChange = (field: string, value: any) => {
    setSearchData(prev => {
      const newData = { ...prev, [field]: value };
      if (field === 'startDate' && newData.startDate > newData.endDate) {
        newData.endDate = newData.startDate;
      }
      return newData;
    });
  };

  const handleSearch = async () => {
    if (activeSection === 'vuelos') {
      if (!searchData.fromId || !searchData.toId) {
        alert("Por favor, selecciona origen y destino.");
        return;
      }
    } else if (activeSection !== 'coches' && !searchData.destinationText && !searchData.destination) {
      alert("Por favor, introduce un destino.");
      return;
    }

    setLoading(true);
    setResults([]); 

    try {
      const payload = {
        ...searchData,
        departDate: formatDateForBackend(searchData.startDate),
        returnDate: (activeSection === 'vuelos' || activeSection === 'alojamiento') 
                    ? formatDateForBackend(searchData.endDate) 
                    : '',
        adults: searchData.adults || 1
      };

      const data = await performSearch(activeSection, payload);
      
      let finalResults = [];
      if (Array.isArray(data)) {
        finalResults = data;
      } else {
        finalResults = data?.data?.flightOffers || data?.result || data?.data || [];
      }
      
      setResults(finalResults);

      if (finalResults.length > 0) {
        setTimeout(() => {
          resultsRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
        }, 500);
      }
    } catch (error) {
      console.error("Error en búsqueda:", error);
      alert("Error de conexión. Reintenta en unos segundos.");
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = async (item: any) => {
    setModalLoading(true);
    if (activeSection === 'alojamiento') {
      setSelectedHotelBasic(item);
      setIsModalOpen(true);
      setSelectedHotelDetails(null);
      try {
        const details = await getHotelDetails(item.hotelId, searchData);
        setSelectedHotelDetails(details);
      } catch (error) { console.error(error); } 
      finally { setModalLoading(false); }
    } 
    else if (activeSection === 'vuelos') {
      setSelectedFlightBasic(item);
      setIsFlightModalOpen(true);
      setSelectedFlightDetails(null);
      try {
        const details = await getFlightDetails(item.token, searchData.currencyCode);
        setSelectedFlightDetails(details);
      } catch (error) { console.error(error); } 
      finally { setModalLoading(false); }
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-sans overflow-x-hidden selection:bg-teal-200" 
         style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}>
      
      <Navbar activeTab={activeSection} onTabChange={setActiveSection} />

      <main className="relative z-10 pt-24 sm:pt-28 pb-20 px-4 sm:px-6 flex flex-col items-center flex-grow">
        
        <div ref={iconRef} className="mb-8 bg-white/20 backdrop-blur-3xl p-8 rounded-[3rem] border border-white/40 shadow-2xl">
          {activeSection === 'alojamiento' && <Hotel size={70} className="text-teal-900" />}
          {activeSection === 'vuelos' && <Plane size={70} className="text-teal-900" />}
          {activeSection === 'coches' && <Car size={70} className="text-teal-900" />}
          {activeSection === 'actividades' && <Ticket size={70} className="text-teal-900" />}
          {activeSection === 'cruceros' && <Ship size={70} className="text-teal-900" />}
          {activeSection === 'trenes' && <Train size={70} className="text-teal-900" />}
        </div>

        <div className="w-full max-w-7xl backdrop-blur-2xl bg-white/40 rounded-[4rem] border border-white/60 shadow-2xl p-8 lg:p-12 text-center">
          <h2 className="text-5xl md:text-7xl font-black text-teal-900 tracking-tighter uppercase mb-2 leading-none">
            JourneyMate <span className="text-teal-600/40">{activeSection}</span>
          </h2>
          <p className="text-teal-900/50 font-bold text-[10px] uppercase tracking-[0.6em] mb-12">
            Tu compañero de viaje inteligente
          </p>

          <div className="grid grid-cols-1 md:grid-cols-7 gap-3 bg-white/30 p-4 rounded-[3rem] border border-white/30 items-end">
            <SearchForm 
              activeSection={activeSection} 
              searchData={searchData} 
              handleChange={handleChange}
              minDate={todayStr}
            />
            
            <button 
              onClick={handleSearch}
              disabled={loading}
              className="md:col-span-1 bg-teal-900 text-white rounded-[2rem] h-[60px] font-black uppercase text-[11px] tracking-widest hover:bg-teal-800 transition-all flex items-center justify-center gap-2 shadow-2xl disabled:opacity-50"
            >
              {loading ? (
                // ⭐ VÍDEO en vez de spinner dentro del botón
                // Usamos tamaño pequeño (48px) para que quepa en el botón
                <LoadingVideo size={48} />
              ) : (
                <>
                  <Search size={18} />
                  <span>BUSCAR</span>
                </>
              )}
            </button>
          </div>
        </div>

        {loading && (
          <div
            className="fixed inset-0 z-50 flex flex-col items-center justify-center"
            style={{ backgroundColor: '#000000' }}
          >
            <LoadingVideo size={320} />
            <p className="mt-2 text-white font-black uppercase tracking-[0.4em] text-[11px] animate-pulse">
              Buscando las mejores opciones...
            </p>
          </div>
        )}

        <div ref={resultsRef} className="w-full mt-12 max-w-7xl">
          <ResultsList 
            results={results} 
            activeSection={activeSection} 
            onViewDetails={handleViewDetails}
            destination={searchData.destinationText || searchData.destination}
          />
        </div>

        {!loading && results.length === 0 && (
          <p className="mt-16 text-teal-900/30 font-black uppercase tracking-widest text-xs animate-pulse">
            Explora nuevos destinos y encuentra las mejores ofertas
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

      <FlightDetailsModal 
        isOpen={isFlightModalOpen} 
        onClose={() => setIsFlightModalOpen(false)} 
        details={selectedFlightDetails}
        loading={modalLoading}
        flightBasicData={selectedFlightBasic}
      />

      <Footer />
      <AITravelAssistant />
    </div>
  );
}

export default App;