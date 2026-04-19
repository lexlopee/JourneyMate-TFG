import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";

// Componentes y Servicios
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import { SearchForm } from './components/SearchForm';
import { ResultsList } from './components/results/ResultsList';
import { HotelDetailsModal } from './components/results/HotelDetailsModal';
import { FlightDetailsModal } from './components/results/FlightDetailsModal'; 
import { ActivityDetailsModal } from './components/results/ActivityDetailsModal'; 
import { CarDetailsModal } from './components/results/CarDetailsModal';
import { AITravelAssistant } from './components/AITravelAssistant';
import { LoadingVideo } from './components/LoadingVideo';
import { Car3D } from './components/Car3D';
import { useSearchParams } from 'react-router-dom';
import { CruiseDetailsModal } from './components/results/CruiseDetailsModal';


// Servicios
import { 
  performSearch, 
  getHotelDetails, 
  getFlightDetails, 
  getActivityDetails 
} from './services/searchService'; 
import { formatDateForBackend } from './utils/dateUtils';

// Iconos
import { Hotel, Plane, Ticket, Ship, Train, Search } from 'lucide-react';

function App() {
  // --- FECHAS POR DEFECTO ---
  const tomorrowStr = new Date(Date.now() + 86400000).toISOString().split('T')[0];
  const dayAfterTomorrowStr = new Date(Date.now() + 172800000).toISOString().split('T')[0];

  // --- ESTADOS DE NAVEGACIÓN Y BÚSQUEDA ---
  const [searchParams] = useSearchParams();
  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any[]>([]);
  const [searchData, setSearchData] = useState({
    fromId: '', 
    toId: '',
    originText: '',      
    destinationText: '', 
    destination: '',      
    startDate: tomorrowStr,
    endDate: dayAfterTomorrowStr,
    pickupTime: '10:00', 
    dropoffTime: '10:00', 
    driverAge: 30,      
    adults: 1,
    roomQty: 1,
    childrenAge: '',     
    cabinClass: 'ECONOMY',
    sort: 'BEST',
    currencyCode: 'EUR',
    carType: 'all',
    cruiseDestination: '',
    cruisePort: '',
  });

  // --- ESTADOS DE MODALES Y DETALLES ---
  const [modalLoading, setModalLoading] = useState(false);

  // Hoteles
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedHotelBasic, setSelectedHotelBasic] = useState<any>(null);
  const [selectedHotelDetails, setSelectedHotelDetails] = useState<any>(null);

  // Vuelos
  const [isFlightModalOpen, setIsFlightModalOpen] = useState(false);
  const [selectedFlightBasic, setSelectedFlightBasic] = useState<any>(null);
  const [selectedFlightDetails, setSelectedFlightDetails] = useState<any>(null);

  // Actividades
  const [isActivityModalOpen, setIsActivityModalOpen] = useState(false);
  const [selectedActivityBasic, setSelectedActivityBasic] = useState<any>(null);
  const [selectedActivityDetails, setSelectedActivityDetails] = useState<any>(null);

  // Coches
  const [isCarModalOpen, setIsCarModalOpen] = useState(false);
  const [selectedCar, setSelectedCar] = useState<any>(null);

  // Cruceros
  const [isCruiseModalOpen, setIsCruiseModalOpen] = useState(false);
  const [selectedCruise, setSelectedCruise] = useState<any>(null);

  const iconRef = useRef<HTMLDivElement>(null);
  const resultsRef = useRef<HTMLDivElement>(null);

  // --- ANIMACIÓN DE ICONO ---
  useEffect(() => {
    if (iconRef.current) {
      anime({
        targets: iconRef.current,
        rotate: [0, 360], scale: [0.5, 1], opacity: [0, 1],
        duration: 800, easing: "easeOutBack"
      });
    }
  }, [activeSection]);

  // --- MANEJADORES ---
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
      };

      const data = await performSearch(activeSection, payload);
      let finalResults = Array.isArray(data) ? data : (data?.data?.flightOffers || data?.result || data?.data || []);
      
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
      } catch (e) { console.error(e); } 
      finally { setModalLoading(false); }
    } 
    else if (activeSection === 'vuelos') {
      setSelectedFlightBasic(item);
      setIsFlightModalOpen(true);
      setSelectedFlightDetails(null);
      try {
        const details = await getFlightDetails(item.token, searchData.currencyCode);
        setSelectedFlightDetails(details);
      } catch (e) { console.error(e); } 
      finally { setModalLoading(false); }
    }
    else if (activeSection === 'actividades') {
      setSelectedActivityBasic(item);
      setIsActivityModalOpen(true);
      setSelectedActivityDetails(null);
      try {
        const details = await getActivityDetails(item.slug);
        console.log("Detalles recibidos:",details);
        setSelectedActivityDetails(details);
      } catch (e) { console.error(e); } 
      finally { setModalLoading(false); }
    }
    else if (activeSection === 'cruceros') {
      setSelectedCruise(item);
      setIsCruiseModalOpen(true);
      setModalLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-sans overflow-x-hidden selection:bg-teal-200" 
         style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}>
      
      <Navbar activeTab={activeSection} onTabChange={setActiveSection} />

      <main className="relative z-10 pt-24 sm:pt-28 pb-40 px-4 sm:px-6 flex flex-col items-center flex-grow">
        
        {/* ICONO SECCIÓN */}
        <div ref={iconRef} className="mb-8 bg-white/20 backdrop-blur-3xl p-8 rounded-[3rem] border border-white/40 shadow-2xl">
          {activeSection === 'alojamiento' && <Hotel size={70} className="text-teal-900" />}
          {activeSection === 'vuelos' && <Plane size={70} className="text-teal-900" />}
          {activeSection === 'coches' && <Car3D carType={searchData.carType} height={100} interactive={true} showLabel={false} className="w-24" />}
          {activeSection === 'actividades' && <Ticket size={70} className="text-teal-900" />}
          {activeSection === 'cruceros' && <Ship size={70} className="text-teal-900" />}
          {activeSection === 'trenes' && <Train size={70} className="text-teal-900" />}
        </div>

        {/* CONTENEDOR BUSCADOR */}
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
              minDate={new Date().toISOString().split('T')[0]}
            />
            
            <button 
              onClick={handleSearch}
              disabled={loading}
              className="md:col-span-1 bg-teal-900 text-white rounded-[2rem] h-[60px] font-black uppercase text-[11px] tracking-widest hover:bg-teal-800 transition-all flex items-center justify-center gap-2 shadow-2xl disabled:opacity-50"
            >
              {loading ? <LoadingVideo size={48} /> : <><Search size={18} /><span>BUSCAR</span></>}
            </button>
          </div>
        </div>

        {/* CARGA PRINCIPAL */}
        {loading && (
          <div className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-teal-950/60 backdrop-blur-md">
            <LoadingVideo size={220} />
            <p className="mt-4 text-white font-black uppercase tracking-[0.4em] text-[11px] animate-pulse">
              Buscando las mejores opciones...
            </p>
          </div>
        )}

        {/* LISTADO DE RESULTADOS */}
        <div ref={resultsRef} className="w-full mt-16 max-w-7xl mb-16">
          <ResultsList 
            results={results} 
            activeSection={activeSection} 
            onViewDetails={handleViewDetails}
            destination={searchData.destinationText || searchData.destination}
            searchData={searchData}
            onRentCar={(car) => { setSelectedCar(car); setIsCarModalOpen(true); }}
            onBookActivity={handleViewDetails}
          />
        </div>
      </main>

      {/* --- MODALES --- */}
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

      <ActivityDetailsModal 
        isOpen={isActivityModalOpen}
        onClose={() => setIsActivityModalOpen(false)}
        details={selectedActivityDetails}
        loading={modalLoading}
        activityBasicData={selectedActivityBasic}
        searchData={searchData}
      />

      <CarDetailsModal
        isOpen={isCarModalOpen}
        onClose={() => setIsCarModalOpen(false)}
        car={selectedCar}
        searchData={searchData}
      />

      <CruiseDetailsModal
        isOpen={isCruiseModalOpen}
        onClose={() => setIsCruiseModalOpen(false)}
        cruise={selectedCruise}
        searchData={searchData}
      />

      <Footer />
      <AITravelAssistant />
    </div>
  );
}

export default App;