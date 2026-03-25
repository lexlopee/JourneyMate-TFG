import React, { useEffect, useRef, useState } from 'react';
import anime from "animejs/lib/anime.es.js";
import { Hotel, Plane, Car, Ticket, Ship, Train, User, LogOut, BookOpen, ChevronDown } from 'lucide-react';
import logoImg from '../assets/logo.png';
import { Link, useNavigate } from "react-router-dom";

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
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState("");
  const [dropdownOpen, setDropdownOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const idUsuario = localStorage.getItem("idUsuario");

    if (!token || !idUsuario) return;

    setIsLoggedIn(true);

    // ✅ SOLUCIÓN PRINCIPAL: leer el nombre directamente del localStorage
    // Login y Register ya lo guardan en "userName" al hacer login/register.
    // Así evitamos completamente el fetch que causaba ERR_INCOMPLETE_CHUNKED_ENCODING.
    const savedName = localStorage.getItem("userName");
    if (savedName) {
      setUserName(savedName);
      return; // Ya tenemos el nombre, no hace falta fetch
    }

    // Fallback: si por algún motivo no está en localStorage (sesión antigua),
    // hacemos el fetch pero capturando el ERR_INCOMPLETE_CHUNKED_ENCODING igual que
    // hacemos en las reservas: si falla el fetch pero el token existe, lo ignoramos.
    const cleanId = String(Number(idUsuario)); // limpia cualquier "1:1" → "1"
    fetch(`http://localhost:8080/usuarios/${cleanId}`, {
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    })
      .then(res => {
        if (!res.ok) throw new Error("Error en respuesta");
        return res.json();
      })
      .then(data => {
        if (data?.nombre) {
          setUserName(data.nombre);
          localStorage.setItem("userName", data.nombre); // guardamos para la próxima
        }
      })
      .catch(() => {
        // Si falla (ERR_INCOMPLETE_CHUNKED_ENCODING u otro), no mostramos error
        // El usuario verá su inicial del email como fallback
        const email = localStorage.getItem("userEmail") ?? "";
        if (email) setUserName(email.split("@")[0]);
      });
  }, []);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("idUsuario");
    localStorage.removeItem("userName");   // ✅ limpiar también
    localStorage.removeItem("userEmail");
    setIsLoggedIn(false);
    setUserName("");
    setDropdownOpen(false);
    navigate("/");
  };

  useEffect(() => {
    anime({ targets: logoRef.current, opacity: [0, 1], scale: [0.8, 1], duration: 900, easing: "easeOutQuad" });
  }, []);

  useEffect(() => {
    anime({ targets: `.tab-${activeTab}`, scale: [1, 1.15], duration: 300, easing: "easeOutQuad" });
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

        {/* LOGO */}
        <div className="flex items-center gap-3 cursor-pointer group col-span-1" onClick={() => onTabChange('alojamiento')}>
          <div className="h-12 flex items-center justify-center overflow-visible">
            <img
              ref={logoRef}
              src={logoImg}
              alt="JourneyMate Logo"
              className="h-10 w-auto scale-[2.6] origin-center translate-y-1 transition-transform duration-300 group-hover:scale-[2.45]"
            />
          </div>
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
              {tab.soon && (
                <span className="absolute -top-1 -right-2 bg-yellow-400 text-teal-950 text-[7px] px-1.5 py-0.5 rounded-full font-black animate-bounce">
                  SOON
                </span>
              )}
            </button>
          ))}
        </div>

        {/* BOTONES DERECHA */}
        <div className="flex items-center gap-3 justify-end col-span-1">
          {isLoggedIn ? (
            <div className="relative" ref={dropdownRef}>
              <button
                onClick={() => setDropdownOpen(prev => !prev)}
                className="flex items-center gap-2 bg-teal-900 text-white px-4 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all shadow-md"
              >
                <div className="w-6 h-6 rounded-full bg-teal-500 flex items-center justify-center text-[11px] font-black uppercase shrink-0">
                  {userName ? userName.charAt(0).toUpperCase() : <User size={12} />}
                </div>
                <span className="hidden sm:block max-w-[110px] truncate">
                  {userName || "Usuario"}
                </span>
                <ChevronDown
                  size={14}
                  className={`transition-transform duration-200 shrink-0 ${dropdownOpen ? "rotate-180" : ""}`}
                />
              </button>

              {dropdownOpen && (
                <div className="absolute right-0 mt-2 w-52 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50">
                  <div className="px-4 py-3 bg-teal-50 border-b border-teal-100">
                    <p className="text-[10px] text-teal-500 font-bold uppercase tracking-widest">Conectado como</p>
                    <p className="text-teal-900 font-black text-sm truncate">{userName || "Usuario"}</p>
                  </div>
                  <Link
                    to="/mis-reservas"
                    onClick={() => setDropdownOpen(false)}
                    className="flex items-center gap-3 px-4 py-3 text-sm font-semibold text-teal-900 hover:bg-teal-50 transition-colors"
                  >
                    <BookOpen size={16} className="text-teal-600" />
                    Mis reservas
                  </Link>
                  <div className="border-t border-gray-100" />
                  <button
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-sm font-semibold text-red-600 hover:bg-red-50 transition-colors"
                  >
                    <LogOut size={16} />
                    Cerrar sesión
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link
              to="/login"
              className="flex items-center gap-2 bg-teal-900 text-white px-6 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all shadow-md"
            >
              <User size={18} />
              Acceder
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;