// src/pages/PaymentSuccess.tsx
import { useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { CheckCircle, BookOpen, Home } from 'lucide-react';

export default function PaymentSuccess() {
  const [params] = useSearchParams();
  const metodo    = params.get('metodo');
  const reservaId = params.get('reservaId');

  useEffect(() => {
    sessionStorage.removeItem('pendingPayment');
  }, []);

  return (
    <div
      className="min-h-screen flex items-center justify-center px-4"
      style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}
    >
      <div className="bg-white rounded-[3rem] shadow-2xl p-12 text-center max-w-md w-full">

        <div className="bg-teal-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
          <CheckCircle size={56} className="text-teal-500" />
        </div>

        <h1 className="text-3xl font-black text-teal-900 uppercase tracking-tight mb-2">
          ¡Pago completado!
        </h1>

        {reservaId && (
          <p className="text-teal-900/50 mb-1 font-bold">
            Reserva <strong className="text-teal-700">#{reservaId}</strong> confirmada
          </p>
        )}
        {metodo && (
          <p className="text-teal-600/60 text-xs font-bold uppercase tracking-widest mb-8">
            vía <span className="capitalize text-teal-700 font-black">{metodo}</span>
          </p>
        )}

        <div className="bg-teal-50 border border-teal-100 rounded-2xl px-5 py-4 mb-8 text-left">
          <p className="text-teal-700 text-xs font-bold">
            ✅ Tu reserva está marcada como <strong>CONFIRMADA</strong>.
            Recibirás un email con tu factura en breve.
          </p>
        </div>

        <div className="flex flex-col gap-3">
          <Link to="/mis-reservas"
            className="bg-teal-900 text-white rounded-2xl px-8 py-4 font-black uppercase text-sm tracking-widest hover:bg-teal-800 transition-all flex items-center justify-center gap-2">
            <BookOpen size={18} /> Ver mis reservas
          </Link>
          <Link to="/"
            className="bg-teal-50 hover:bg-teal-100 text-teal-800 border border-teal-200 rounded-2xl px-8 py-4 font-black uppercase text-sm tracking-widest transition-all flex items-center justify-center gap-2">
            <Home size={18} /> Volver al inicio
          </Link>
        </div>
      </div>
    </div>
  );
}