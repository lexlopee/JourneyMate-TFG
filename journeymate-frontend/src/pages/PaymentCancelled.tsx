// src/pages/PaymentCancelled.tsx
import { XCircle } from 'lucide-react';

export default function PaymentCancelled() {
  return (
    <div
      className="min-h-screen flex items-center justify-center px-4"
      style={{ background: 'linear-gradient(135deg, #1cb5b0 0%, #e9fc9e 50%, #1cb5b0 100%)' }}
    >
      <div className="bg-white rounded-[3rem] shadow-2xl p-12 text-center max-w-md w-full">
        <XCircle size={64} className="text-red-400 mx-auto mb-4" />
        <h1 className="text-3xl font-black text-teal-900 uppercase tracking-tight mb-2">
          Pago cancelado
        </h1>
        <p className="text-teal-900/50 mb-6 font-bold">
          No se ha realizado ningún cargo. Puedes intentarlo de nuevo cuando quieras.
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