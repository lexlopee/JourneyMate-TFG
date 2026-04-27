import { useEffect, useRef } from "react";
import anime from "animejs/lib/anime.es.js";
import {
  Instagram, Twitter, Facebook,
  MapPin, Phone, Mail, Send,
  Shield, FileText, MessageCircle,
  Plane, Hotel, Car, Ship, Ticket,
  ArrowUpRight
} from "lucide-react";

export default function Footer() {
  const footerRef = useRef<HTMLDivElement>(null);
  const colsRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!footerRef.current) return;
    const cols = colsRef.current?.querySelectorAll(".footer-col");
    anime({
      targets: footerRef.current,
      opacity: [0, 1],
      translateY: [30, 0],
      duration: 700,
      easing: "easeOutQuad",
    });
    if (cols) {
      anime({
        targets: cols,
        opacity: [0, 1],
        translateY: [20, 0],
        delay: anime.stagger(80, { start: 200 }),
        duration: 600,
        easing: "easeOutQuad",
      });
    }
  }, []);

  const handleSubscribe = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const input = e.currentTarget.querySelector("input") as HTMLInputElement;
    if (input?.value) {
      input.value = "";
      input.placeholder = "¡Suscrito con éxito! ✓";
      setTimeout(() => { input.placeholder = "Tu correo electrónico"; }, 3000);
    }
  };

  return (
    <footer
      ref={footerRef}
      style={{ opacity: 0 }}
      className="relative bg-teal-950 text-white overflow-hidden mt-auto"
    >
      {/* Decoración de fondo */}
      <div className="absolute inset-0 pointer-events-none overflow-hidden">
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-teal-800/20 rounded-full blur-3xl" />
        <div className="absolute -bottom-20 -right-20 w-80 h-80 bg-teal-600/10 rounded-full blur-3xl" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-px bg-gradient-to-r from-transparent via-teal-700/30 to-transparent" />
      </div>

      {/* Banda superior con logo y claim */}
      <div className="relative border-b border-teal-800/60 px-6 py-8">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div>
            <h2 className="text-3xl md:text-4xl font-black uppercase tracking-tighter text-white leading-none">
              Journey<span className="text-teal-400">Mate</span>
            </h2>
            <p className="text-teal-400/70 text-[10px] font-black uppercase tracking-[0.4em] mt-1">
              Tu compañero de viaje inteligente
            </p>
          </div>
        </div>
      </div>

      {/* Columnas principales */}
      <div ref={colsRef} className="relative max-w-7xl mx-auto px-6 py-12 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-10">

        {/* Col 1 — Sobre JourneyMate */}
        <div className="footer-col opacity-0 space-y-4">
          <h3 className="text-[10px] font-black uppercase tracking-[0.3em] text-teal-400 mb-5">
            Sobre Nosotros
          </h3>
          <p className="text-teal-300/60 text-xs leading-relaxed">
            Exploramos el mundo para traerte las mejores ofertas de viaje. Hoteles, vuelos, actividades y más — todo en un solo lugar.
          </p>
          <div className="flex gap-3 pt-2">
            {[
              { icon: <Instagram size={16} />, href: "#" },
              { icon: <Twitter size={16} />, href: "#" },
              { icon: <Facebook size={16} />, href: "#" },
            ].map(({ icon, href }, i) => (
              <a
                key={i}
                href={href}
                className="w-9 h-9 rounded-xl bg-teal-800/50 hover:bg-teal-600 border border-teal-700/40 flex items-center justify-center text-teal-300 hover:text-white transition-all hover:scale-110"
              >
                {icon}
              </a>
            ))}
          </div>
        </div>

        {/* Col 2 — Legal */}
        <div className="footer-col opacity-0 space-y-3">
          <h3 className="text-[10px] font-black uppercase tracking-[0.3em] text-teal-400 mb-5">
            Legal
          </h3>
          {[
            { icon: <FileText size={12} />, label: "Aviso Legal" },
            { icon: <Shield size={12} />, label: "Política de Privacidad" },
            { icon: <FileText size={12} />, label: "Términos de Uso" },
            { icon: <MessageCircle size={12} />, label: "Contacto" },
          ].map(({ icon, label }) => (
            <a
              key={label}
              href="#"
              className="flex items-center gap-2.5 text-teal-300/60 hover:text-white text-xs font-bold transition-colors group"
            >
              <span className="text-teal-600 group-hover:text-teal-400 transition-colors">{icon}</span>
              {label}
              <ArrowUpRight size={10} className="opacity-0 group-hover:opacity-100 transition-opacity ml-auto" />
            </a>
          ))}
        </div>

        {/* Col 3 — Contacto */}
        <div className="footer-col opacity-0 space-y-4">
          <h3 className="text-[10px] font-black uppercase tracking-[0.3em] text-teal-400 mb-5">
            Contacto
          </h3>
          <div className="space-y-3">
            {[
              { icon: <Phone size={14} />, text: "+34 123 456 789" },
              { icon: <Mail size={14} />, text: "info@journeymate.com" },
              { icon: <MapPin size={14} />, text: "Madrid, España" },
            ].map(({ icon, text }) => (
              <div key={text} className="flex items-center gap-3 text-teal-300/70 text-xs font-bold">
                <div className="w-7 h-7 rounded-lg bg-teal-800/50 border border-teal-700/30 flex items-center justify-center text-teal-400 shrink-0">
                  {icon}
                </div>
                {text}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Barra inferior */}
      <div className="relative border-t border-teal-800/50 px-6 py-5">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-3">
          <p className="text-teal-500/50 text-[10px] font-bold uppercase tracking-widest">
            © {new Date().getFullYear()} JourneyMate — Todos los derechos reservados
          </p>
          <div className="flex items-center gap-1.5 text-teal-600/40 text-[9px] font-black uppercase tracking-widest">
            <Shield size={10} /> Pago seguro
          </div>
        </div>
      </div>
    </footer>
  );
}