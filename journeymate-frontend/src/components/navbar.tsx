import React, { useEffect, useRef, useState } from 'react';
import anime from "animejs/lib/anime.es.js";
import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  User, LogOut, BookOpen, ChevronDown, Menu, X
} from 'lucide-react';
import logoImg from '../assets/logo.png';
import { Link, useNavigate } from "react-router-dom";

export type Section = 'alojamiento' | 'vuelos' | 'coches' | 'actividades' | 'cruceros' | 'trenes';

interface NavTab { id: Section; label: string; icon: React.ReactNode; soon?: boolean; }
interface NavbarProps { activeTab: Section; onTabChange: (tab: Section) => void; }

const Navbar: React.FC<NavbarProps> = ({ activeTab, onTabChange }) => {
  const logoRef  = useRef<HTMLImageElement>(null);
  const dropRef  = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const [isLoggedIn,   setIsLoggedIn]   = useState(false);
  const [userName,     setUserName]     = useState("");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileOpen,   setMobileOpen]   = useState(false);

  useEffect(() => {
    const token     = localStorage.getItem("token");
    const idUsuario = localStorage.getItem("idUsuario");
    if (!token || !idUsuario) return;
    setIsLoggedIn(true);
    const saved = localStorage.getItem("userName");
    if (saved) { setUserName(saved); return; }
    fetch(`http://localhost:8080/usuarios/${String(Number(idUsuario))}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(d => { if (d?.nombre) { setUserName(d.nombre); localStorage.setItem("userName", d.nombre); } })
      .catch(() => {});
  }, []);

  useEffect(() => {
    const h = (e: MouseEvent) => {
      if (dropRef.current && !dropRef.current.contains(e.target as Node)) setDropdownOpen(false);
    };
    document.addEventListener("mousedown", h);
    return () => document.removeEventListener("mousedown", h);
  }, []);

  const goTab = (tab: Section) => { onTabChange(tab); setMobileOpen(false); };

  const logout = () => {
    ["token","idUsuario","userName","userEmail"].forEach(k => localStorage.removeItem(k));
    setIsLoggedIn(false); setUserName(""); setDropdownOpen(false); setMobileOpen(false);
    navigate("/");
  };

  useEffect(() => {
    anime({ targets: logoRef.current, opacity:[0,1], scale:[0.8,1], duration:900, easing:"easeOutQuad" });
  }, []);

  const tabs: NavTab[] = [
    { id:'alojamiento', label:'Alojamiento', icon:<Hotel  size={16}/> },
    { id:'vuelos',      label:'Vuelos',      icon:<Plane  size={16}/> },
    { id:'coches',      label:'Coches',      icon:<Car    size={16}/> },
    { id:'actividades', label:'Actividades', icon:<Ticket size={16}/> },
    { id:'cruceros',    label:'Cruceros',    icon:<Ship   size={16}/> },
    { id:'trenes',      label:'Trenes',      icon:<Train  size={16}/>, soon:true },
  ];

  return (
    <>
      <nav className="fixed top-0 left-0 right-0 z-50 px-3 pt-3 sm:px-5 sm:pt-4">
        <div className="w-full max-w-screen-xl mx-auto
          bg-white/80 backdrop-blur-md border border-white/50 shadow-xl
          rounded-2xl px-3 py-2 sm:px-4 sm:py-2.5
          flex items-center justify-between gap-2">

          {/* ⭐ LOGO — Link a / (home) */}
          <Link to="/" className="flex items-center gap-2 group shrink-0">
            <span className="h-9 sm:h-11 flex items-center overflow-visible">
              <img
                ref={logoRef}
                src={logoImg}
                alt="JourneyMate"
                className="h-8 sm:h-10 w-auto scale-[2.1] sm:scale-[2.5]
                  origin-center translate-y-0.5
                  transition-transform group-hover:scale-[2.0] sm:group-hover:scale-[2.4]"
              />
            </span>
            <span className="hidden sm:block leading-none ml-2">
              <span className="block font-black text-lg sm:text-xl text-teal-900 tracking-tighter uppercase">JourneyMate</span>
              <span className="block text-[7px] sm:text-[8px] font-bold text-teal-600 tracking-[0.3em]">TRAVEL SMART</span>
            </span>
          </Link>

          {/* TABS DESKTOP */}
          <div className="hidden lg:flex items-center gap-0.5 bg-teal-900/5 p-1 rounded-2xl border border-teal-900/5 mx-2">
            {tabs.map(tab => (
              <button key={tab.id} onClick={() => goTab(tab.id)}
                className={`relative flex items-center gap-1.5 px-2.5 xl:px-3 py-2 rounded-xl
                  text-[9px] xl:text-[10px] font-black uppercase tracking-wider whitespace-nowrap
                  transition-all duration-200
                  ${activeTab===tab.id ? 'bg-white text-teal-900 shadow-sm' : 'text-teal-700/70 hover:text-teal-900 hover:bg-white/60'}`}>
                {tab.icon}<span>{tab.label}</span>
                {tab.soon && <span className="absolute -top-1 -right-1 bg-yellow-400 text-teal-950 text-[6px] px-1 py-0.5 rounded-full font-black animate-bounce leading-none">SOON</span>}
              </button>
            ))}
          </div>

          {/* TABS TABLET */}
          <div className="hidden md:flex lg:hidden items-center gap-1 mx-2">
            {tabs.map(tab => (
              <button key={tab.id} onClick={() => goTab(tab.id)} title={tab.label}
                className={`relative p-2 rounded-xl transition-all duration-200
                  ${activeTab===tab.id ? 'bg-teal-900 text-white shadow' : 'text-teal-700 hover:bg-teal-100'}`}>
                {tab.icon}
                {tab.soon && <span className="absolute top-0 right-0 w-2 h-2 bg-yellow-400 rounded-full" />}
              </button>
            ))}
          </div>

          {/* DERECHA */}
          <div className="flex items-center gap-2 shrink-0">
            {isLoggedIn ? (
              <div className="relative" ref={dropRef}>
                <button onClick={() => setDropdownOpen(p => !p)}
                  className="flex items-center gap-1.5 bg-teal-900 text-white pl-2 pr-2.5 py-1.5 rounded-xl font-bold text-xs sm:text-sm hover:bg-teal-800 transition-all shadow-md">
                  <span className="w-6 h-6 rounded-full bg-teal-500 flex items-center justify-center text-[10px] font-black uppercase shrink-0">
                    {userName ? userName[0].toUpperCase() : <User size={11}/>}
                  </span>
                  <span className="hidden sm:block max-w-[80px] xl:max-w-[110px] truncate">{userName || "Usuario"}</span>
                  <ChevronDown size={12} className={`transition-transform shrink-0 ${dropdownOpen?"rotate-180":""}`}/>
                </button>
                {dropdownOpen && (
                  <div className="absolute right-0 mt-2 w-52 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50">
                    <div className="px-4 py-3 bg-teal-50 border-b border-teal-100">
                      <p className="text-[10px] text-teal-500 font-bold uppercase tracking-widest">Conectado como</p>
                      <p className="text-teal-900 font-black text-sm truncate">{userName||"Usuario"}</p>
                    </div>
                    <Link to="/mis-reservas" onClick={()=>setDropdownOpen(false)}
                      className="flex items-center gap-3 px-4 py-3 text-sm font-semibold text-teal-900 hover:bg-teal-50 transition-colors">
                      <BookOpen size={15} className="text-teal-600"/> Mis reservas
                    </Link>
                    <div className="border-t border-gray-100"/>
                    <button onClick={logout}
                      className="w-full flex items-center gap-3 px-4 py-3 text-sm font-semibold text-red-600 hover:bg-red-50 transition-colors">
                      <LogOut size={15}/> Cerrar sesión
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link to="/login"
                className="flex items-center gap-1.5 bg-teal-900 text-white px-3 sm:px-4 py-2 rounded-xl font-bold text-xs sm:text-sm hover:bg-teal-800 transition-all shadow-md">
                <User size={15}/><span className="hidden xs:inline sm:inline">Acceder</span>
              </Link>
            )}
            <button onClick={() => setMobileOpen(p => !p)}
              className="md:hidden p-2 rounded-xl bg-teal-900/10 hover:bg-teal-900/20 text-teal-900 transition-colors">
              {mobileOpen ? <X size={20}/> : <Menu size={20}/>}
            </button>
          </div>
        </div>
      </nav>

      {mobileOpen && (
        <>
          <div className="fixed inset-0 z-40 md:hidden bg-black/20 backdrop-blur-sm" onClick={() => setMobileOpen(false)}/>
          <div className="fixed z-50 md:hidden top-[68px] left-3 right-3 bg-white rounded-2xl shadow-2xl border border-teal-100 overflow-hidden">
            {tabs.map(tab => (
              <button key={tab.id} onClick={() => goTab(tab.id)}
                className={`w-full flex items-center gap-3 px-5 py-4 text-sm font-black uppercase tracking-wider border-b border-teal-50 last:border-none transition-colors
                  ${activeTab===tab.id ? 'bg-teal-900 text-white' : 'text-teal-800 hover:bg-teal-50'}`}>
                {tab.icon}<span>{tab.label}</span>
                {tab.soon && <span className="ml-auto bg-yellow-400 text-teal-950 text-[8px] px-2 py-0.5 rounded-full font-black">SOON</span>}
              </button>
            ))}
            {isLoggedIn && (
              <>
                <div className="border-t-2 border-teal-100"/>
                <Link to="/mis-reservas" onClick={() => setMobileOpen(false)}
                  className="flex items-center gap-3 px-5 py-4 text-sm font-semibold text-teal-700 hover:bg-teal-50 transition-colors">
                  <BookOpen size={16}/> Mis reservas
                </Link>
                <button onClick={logout}
                  className="w-full flex items-center gap-3 px-5 py-4 text-sm font-semibold text-red-600 hover:bg-red-50 transition-colors">
                  <LogOut size={16}/> Cerrar sesión
                </button>
              </>
            )}
          </div>
        </>
      )}
    </>
  );
};

export default Navbar;