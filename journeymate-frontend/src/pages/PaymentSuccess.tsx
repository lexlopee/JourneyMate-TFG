// src/pages/PaymentSuccess.tsx
import { useSearchParams } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';

export default function PaymentSuccess() {
  const [params] = useSearchParams();
  const metodo = params.get('metodo');
  const reservaId = params.get('reservaId');

  return (
    <div
      className="min-h-screen flex items-center justify-center px-4"
      style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}
    >
      <div className="bg-white rounded-[3rem] shadow-2xl p-12 text-center max-w-md w-full">
        <CheckCircle size={64} className="text-teal-500 mx-auto mb-4" />
        <h1 className="text-3xl font-black text-teal-900 uppercase tracking-tight mb-2">
          ¡Pago completado!
        </h1>
        <p className="text-teal-900/50 mb-6 font-bold">
          Reserva <strong>#{reservaId}</strong> confirmada vía{' '}
          <span className="capitalize font-black text-teal-700">{metodo}</span>
        </p>
        <a
          href="/"
          className="bg-teal-900 text-white rounded-2xl px-8 py-3 font-black uppercase text-sm tracking-widest hover:bg-teal-800 transition-all inline-block"
        >
          Volver al inicio
        </a>
      </div>
    </div>
  );
}