import { useEffect, useRef } from "react";
import anime from "animejs/lib/anime.es.js";
import "./Footer.css";

export default function Footer() {
  const footerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (footerRef.current) {
      anime({
        targets: footerRef.current,
        opacity: [0, 1],
        translateY: [20, 0],
        duration: 800,
        easing: "easeOutQuad"
      });
    }
  }, []);

  return (
    <footer ref={footerRef} className="footer">
      <div className="footer-container">

        <div className="footer-section">
          <h3>Redes Sociales</h3>
          <ul className="social-links">
            <li><a href="#">Facebook</a></li>
            <li><a href="#">Twitter</a></li>
            <li><a href="#">Instagram</a></li>
          </ul>
        </div>

        <div className="footer-section">
          <h3>Enlaces Rápidos</h3>
          <ul>
            <li><a href="#">Aviso Legal</a></li>
            <li><a href="#">Política de Privacidad</a></li>
            <li><a href="#">Contacto</a></li>
          </ul>
        </div>

        <div className="footer-section contact-info">
          <h3>Información de Contacto</h3>
          <p>Teléfono: +34 123 456 789</p>
          <p>Email: info@journeymate.com</p>

          <div className="subscription-form">
            <input type="email" placeholder="Tu correo electrónico" />
            <button type="submit">Suscribirse</button>
          </div>
        </div>

      </div>

      <div className="footer-bottom">
        © {new Date().getFullYear()} JourneyMate — Todos los derechos reservados
      </div>
    </footer>
  );
}
