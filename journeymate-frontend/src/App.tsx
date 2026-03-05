import { useState, useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";
import Navbar, { type Section } from './components/navbar';
import Footer from "./components/Footer/Footer";
import api from './services/api';

import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  MapPin, Calendar, Users, Search, Clock, Globe
} from 'lucide-react';

interface SearchInputProps {
  label: string;
  icon: React.ReactNode;
  placeholder?: string;
  val: string;
  type?: string;
  onChange: (value: string) => void;
}

function App() {
  const [activeSection, setActiveSection] = useState<Section>('alojamiento');
  const [loading, setLoading] = useState(false);

  const iconRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const [cruiseDestinations, setCruiseDestinations] = useState<any[]>([]);
  const [cruisePorts, setCruisePorts] = useState<any[]>([]);

  const [searchData, setSearchData] = useState({
    origin: '',
    destination: '',
    startDate: '',
    endDate: '',
    adults: '2',
    pickupTime: '10:00',
    cabinClass: 'ECONOMY'
  });

  useEffect(() => {
    if (iconRef.current) {
      anime({
        targets: iconRef.current,
        rotate: [0, 360],
        scale: [0.8, 1],
        duration: 600,
        easing: "easeOutBack"
      });
    }

    if (containerRef.current) {
      anime({
        targets: containerRef.current,
        opacity: [0, 1],
        translateY: [20, 0],
        duration: 500,
        easing: "easeOutQuad"
      });
    }

    anime({
      targets: ".search-input",
      opacity: [0, 1],
      translateY: [10, 0],
      delay: anime.stagger(80),
      duration: 400,
      easing: "easeOutQuad"
    });

  }, [activeSection]);

  useEffect(() => {
    if (activeSection === 'cruceros') {
      const fetchCruiseData = async () => {
        try {
          const [destRes, portRes] = await Promise.all([
            api.get('/cruises/destinations'),
            api.get('/cruises/ports')
          ]);
          setCruiseDestinations(destRes.data);
          setCruisePorts(portRes.data);
        } catch (e) {
          console.error("Error cargando catálogos", e);
        }
      };
      fetchCruiseData();
    }
  }, [activeSection]);

  const handleChange = (field: string, value: string) => {
    setSearchData(prev => ({ ...prev, [field]: value }));
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      let response;

      switch (activeSection) {
        case 'alojamiento':
          const hotelDest = await api.get(`/hotels/destination?name=${searchData.destination}`);
          response = await api.get('/hotels/search', {
            params: {
              destId: hotelDest.data,
              checkinDate: searchData.startDate,
              checkoutDate: searchData.endDate,
              adults: searchData.adults
            }
          });
          break;

        case 'actividades':
          const locRes = await api.get(`/activities/location?query=${searchData.destination}`);
          const ufi = locRes.data[0]?.id;
          response = await api.get('/activities/search', {
            params: {
              id: ufi,
              startDate: searchData.startDate,
              endDate: searchData.endDate
            }
          });
          break;

        case 'cruceros':
          response = await api.get('/cruises/search', {
            params: {
              startDate: searchData.startDate,
              endDate: searchData.endDate,
              destination: searchData.destination,
              departurePort: searchData.origin
            }
          });
          break;

        case 'vuelos':
          response = await api.get('/flights/search', {
            params: {
              fromId: searchData.origin,
              toId: searchData.destination,
              departDate: searchData.startDate,
              adults: searchData.adults
            }
          });
          break;

        case 'coches':
          response = await api.get('/external/cars/search', {
            params: {
              pickUpId: searchData.origin,
              pDate: searchData.startDate,
              pTime: searchData.pickupTime,
              dDate: searchData.endDate,
              dTime: '10:00'
            }
          });
          break;
      }

      console.log("RESULTADOS API:", response?.data);
      alert(`Búsqueda de ${activeSection} completada con éxito. Revisa la consola.`);

    } catch (error) {
      console.error("Error en la petición:", error);
      alert("Error al conectar con el servidor. ¿Está el Backend encendido?");
    } finally {
      setLoading(false);
    }
  };

  const renderInputs = () => {
    switch (activeSection) {
      case 'alojamiento':
        return (
          <>
            <SearchInput label="Destino" icon={<MapPin size={18} />} placeholder="Ciudad, hotel..." val={searchData.destination} onChange={(v) => handleChange('destination', v)} />
            <SearchInput label="Entrada" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v) => handleChange('startDate', v)} />
            <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v) => handleChange('endDate', v)} />
            <SearchInput label="Adultos" icon={<Users size={18} />} val={searchData.adults} onChange={(v) => handleChange('adults', v)} />
          </>
        );

      case 'actividades':
        return (
          <>
            <SearchInput label="Ciudad" icon={<MapPin size={18} />} placeholder="¿A dónde vas?" val={searchData.destination} onChange={(v) => handleChange('destination', v)} />
            <SearchInput label="Desde" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v) => handleChange('startDate', v)} />
            <SearchInput label="Hasta" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v) => handleChange('endDate', v)} />
            <div className="hidden md:block" />
          </>
        );

      case 'cruceros':
        return (
          <>
            <SearchInput label="Zona" icon={<Globe size={18} />} placeholder="CARIB, EUROP..." val={searchData.destination} onChange={(v) => handleChange('destination', v)} />
            <SearchInput label="Puerto" icon={<Ship size={18} />} placeholder="MIA, BCN..." val={searchData.origin} onChange={(v) => handleChange('origin', v)} />
            <SearchInput label="Salida" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v) => handleChange('startDate', v)} />
            <SearchInput label="Regreso" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v) => handleChange('endDate', v)} />
          </>
        );

      case 'vuelos':
        return (
          <>
            <SearchInput label="Origen" icon={<Plane size={18} />} placeholder="MAD, BCN..." val={searchData.origin} onChange={(v) => handleChange('origin', v)} />
            <SearchInput label="Destino" icon={<MapPin size={18} />} placeholder="JFK, LHR..." val={searchData.destination} onChange={(v) => handleChange('destination', v)} />
            <SearchInput label="Fecha" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v) => handleChange('startDate', v)} />
            <SearchInput label="Clase" icon={<Users size={18} />} val={searchData.cabinClass} onChange={(v) => handleChange('cabinClass', v)} />
          </>
        );

      case 'coches':
        return (
          <>
            <SearchInput label="Recogida" icon={<Car size={18} />} placeholder="Aeropuerto..." val={searchData.origin} onChange={(v) => handleChange('origin', v)} />
            <SearchInput label="Fecha" icon={<Calendar size={18} />} type="date" val={searchData.startDate} onChange={(v) => handleChange('startDate', v)} />
            <SearchInput label="Hora" icon={<Clock size={18} />} type="time" val={searchData.pickupTime} onChange={(v) => handleChange('pickupTime', v)} />
            <SearchInput label="Devolución" icon={<Calendar size={18} />} type="date" val={searchData.endDate} onChange={(v) => handleChange('endDate', v)} />
          </>
        );

      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen flex flex-col font-sans overflow-x-hidden"
      style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}>

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

        <div ref={containerRef} className="w-full max-w-5xl backdrop-blur-xl bg-white/40 rounded-[3rem] border border-white/50 shadow-2xl p-10 text-center transition-all duration-700">

          <h2 className="text-4xl md:text-6xl font-black text-teal-900 tracking-tighter uppercase mb-2">
            JourneyMate <span className="text-teal-600/50">{activeSection}</span>
          </h2>

          <p className="text-teal-900/40 font-bold text-xs uppercase tracking-[0.5em] mb-10">
            Explora el mundo sin límites
          </p>

          <div className="grid grid-cols-1 md:grid-cols-5 gap-3 bg-white/30 p-3 rounded-[2.5rem] border border-white/20">
            {renderInputs()}

            <button
              onClick={handleSearch}
              disabled={loading}
              className="search-button bg-teal-900 text-white rounded-2xl font-black uppercase text-[10px] tracking-widest hover:bg-teal-800 transition-all flex flex-col items-center justify-center gap-1 shadow-xl disabled:opacity-50 group"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <>
                  <Search size={20} className="group-hover:scale-125 transition-transform" />
                  <span>BUSCAR</span>
                </>
              )}
            </button>
          </div>
        </div>
      </main>

      <Footer />

    </div>
  );
}

const SearchInput: React.FC<SearchInputProps> = ({ label, icon, placeholder, val, type = "text", onChange }) => (
  <div className="search-input bg-white/90 rounded-2xl p-3 text-left border border-teal-100/50 hover:bg-white transition-all group">
    <span className="text-[9px] font-black text-teal-800/40 block mb-1 uppercase tracking-widest">{label}</span>
    <div className="flex items-center gap-2 text-teal-900">
      <span className="text-teal-500 group-hover:rotate-12 transition-transform">{icon}</span>
      <input
        type={type}
        value={val}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className="bg-transparent border-none outline-none w-full text-[11px] font-bold placeholder:text-teal-900/20"
      />
    </div>
  </div>
);

export default App;
