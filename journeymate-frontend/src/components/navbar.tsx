import React from 'react';
import { Hotel, Plane, Car, Ticket, Ship, User, Train } from 'lucide-react';
import logoImg from '../assets/logo.png'; 

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
      <div className="max-w-7xl mx-auto backdrop-blur-md bg-white/70 border border-white/40 shadow-2xl rounded-3xl px-6 py-3 flex justify-between items-center transition-all duration-500">
        <div className="flex items-center gap-3 cursor-pointer group" onClick={() => onTabChange('alojamiento')}>
          <img src={logoImg} alt="Logo" className="h-10 w-auto group-hover:scale-110 transition-transform" />
          <div className="hidden sm:block leading-none text-left">
            <h1 className="font-black text-xl text-teal-900 tracking-tighter uppercase">JourneyMate</h1>
            <p className="text-[8px] font-bold text-teal-600 tracking-[0.3em]">TRAVEL SMART</p>
          </div>
        </div>

        <div className="hidden lg:flex items-center gap-1 bg-teal-900/5 p-1 rounded-2xl border border-teal-900/5">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => onTabChange(tab.id)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-[11px] font-black uppercase tracking-widest transition-all duration-300 relative
                ${activeTab === tab.id ? 'bg-white text-teal-900 shadow-sm' : 'text-teal-800/60 hover:text-teal-900'}`}
            >
              {tab.icon} {tab.label}
              {tab.soon && <span className="absolute -top-1 -right-2 bg-yellow-400 text-teal-950 text-[7px] px-1.5 py-0.5 rounded-full font-black animate-bounce">SOON</span>}
            </button>
          ))}
        </div>

        <button className="group relative overflow-hidden bg-teal-900 text-white px-6 py-2.5 rounded-2xl font-bold text-sm flex items-center gap-2">
          <User size={16} /> Acceder
          <div className="absolute top-0 -inset-full h-full w-1/2 z-5 block transform -skew-x-12 bg-gradient-to-r from-transparent via-white/20 to-transparent group-hover:animate-shine" />
        </button>
      </div>
    </nav>
  );
};

export default Navbar;