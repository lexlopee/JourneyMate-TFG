import React, { useEffect, useRef } from 'react';
import anime from "animejs/lib/anime.es.js";
import { Hotel, Plane, Car, Ticket, Ship, User, Train } from 'lucide-react';
import logoImg from '../assets/logo.png'; 
import { Link } from "react-router-dom";
import { Bookmark } from "lucide-react";




export type Section = 'alojamiento' | 'vuelos' | 'coches' | 'actividades' | 'cruceros' | 'trenes';

interface NavTab {
  id: Section;
  label: string;
  icon: React.ReactNode;
  soon?: boolean;
}

interface NavbarProps {
  activeTab: Section;
  onTabChange: (tab: Section) => void;
}

const Navbar: React.FC<NavbarProps> = ({ activeTab, onTabChange }) => {
  const logoRef = useRef<HTMLImageElement>(null);

  // Animación suave del logo
  useEffect(() => {
    anime({
      targets: logoRef.current,
      opacity: [0, 1],
      scale: [0.8, 1],
      duration: 900,
      easing: "easeOutQuad"
    });
  }, []);

  // Animación suave del tab activo
  useEffect(() => {
    anime({
      targets: `.tab-${activeTab}`,
      scale: [1, 1.15],
      duration: 300,
      easing: "easeOutQuad"
    });
  }, [activeTab]);

  const tabs: NavTab[] = [
    { id: 'alojamiento', label: 'Alojamiento', icon: <Hotel size={18} /> },
    { id: 'vuelos', label: 'Vuelos', icon: <Plane size={18} /> },
    { id: 'coches', label: 'Coches', icon: <Car size={18} /> },
    { id: 'actividades', label: 'Actividades', icon: <Ticket size={18} /> },
    { id: 'cruceros', label: 'Cruceros', icon: <Ship size={18} /> },
    { id: 'trenes', label: 'Trenes', icon: <Train size={18} />, soon: true },
  ];

  return (
    <nav className="fixed top-4 left-0 right-0 z-50 px-6">
      <div className="navbar-container max-w-[70%] mx-auto 
        backdrop-blur-md bg-white/70 border border-white/40 shadow-2xl rounded-3xl 
        px-4 py-2 flex items-center justify-between gap-4 transition-all duration-500">


        <div className="flex items-center gap-3 cursor-pointer group col-span-1" onClick={() => onTabChange('alojamiento')}>

  {/* CONTENEDOR DEL LOGO */}
  <div className="h-12 flex items-center justify-center overflow-visible">
    <img
      ref={logoRef}
      src={logoImg}
      alt="JourneyMate Logo"
      className="h-10 w-auto scale-[2.6] origin-center translate-y-1 transition-transform duration-300 group-hover:scale-[2.45]"
    />

  </div>

  {/* TEXTO */}
  <div className="hidden sm:block leading-none text-center ml-3">
    <h1 className="font-black text-xl text-teal-900 tracking-tighter uppercase">JourneyMate</h1>
    <p className="text-[8px] font-bold text-teal-600 tracking-[0.3em]">TRAVEL SMART</p>
  </div>

</div>


        {/* TABS */}
  <div className="hidden lg:flex items-center justify-center gap-1 bg-teal-900/5 p-1 rounded-2xl border border-teal-900/5 col-span-1">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className={`tab-${tab.id} flex items-center gap-2 px-4 py-2 rounded-xl text-[11px] font-black uppercase tracking-widest transition-all duration-300 relative
                ${activeTab === tab.id ? 'bg-white text-teal-900 shadow-sm' : 'text-teal-800/60 hover:text-teal-900'}`}
            >
              {tab.icon} {tab.label}
              {tab.soon && <span className="absolute -top-1 -right-2 bg-yellow-400 text-teal-950 text-[7px] px-1.5 py-0.5 rounded-full font-black animate-bounce">SOON</span>}
            </button>
          ))}
        </div>
          {/* BOTONES DERECHA — FUERA DEL BLOQUE DEL LOGO */}
          <div className="flex items-center gap-4 justify-end col-span-1">

            {/* ⭐ MIS RESERVAS */}
            <Link
              to="/mis-reservas"
              className="flex items-center gap-2 bg-teal-700 text-white px-5 py-2 rounded-xl font-bold text-sm hover:bg-teal-600 transition-all shadow-md"
            >
              <svg xmlns="http://www.w3.org/2000/svg" 
                  fill="none" viewBox="0 0 24 24" 
                  strokeWidth={2} stroke="white" 
                  className="w-4 h-4">
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 5h14v14H5z" />
              </svg>
              Mis reservas
            </Link>

            {/* ⭐ ACCEDER */}
            <Link
              to="/login"
              className="flex items-center gap-2 bg-teal-900 text-white px-6 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all shadow-md"
            >
              <User size={18} />
              Acceder
            </Link>

          </div>

      </div>
    </nav>
  );
};

export default Navbar;
