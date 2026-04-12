// src/components/payment/PaymentModal.tsx

import { useState } from 'react';
import {
  X,
  CreditCard,
  Wallet,
  Lock,
  CheckCircle,
  Loader2
} from 'lucide-react';

interface PaymentModalProps {
  isOpen: boolean;
  onClose: () => void;
  reservaId: number;
  precio: number;
  descripcion: string;

  // NUEVO
  multiple?: boolean;
  reservaIds?: number[];
}

export function PaymentModal({
  isOpen,
  onClose,
  reservaId,
  precio,
  descripcion,
  multiple = false,
  reservaIds = []
}: PaymentModalProps) {
  const [loading, setLoading] = useState<'stripe' | 'paypal' | null>(null);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const formatPrice = (v: number) =>
    new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2
    }).format(v);

  const handleStripe = async () => {
    setLoading('stripe');
    setError('');

    try {
      const token = localStorage.getItem('token');

      const body = multiple
        ? { reservaIds }
        : { idReserva: reservaId };

      const res = await fetch(
        'http://localhost:8080/api/v1/stripe/create-checkout',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(body)
        }
      );

      const data = await res.json();

      if (data.url) {
        window.location.href = data.url;
      } else {
        setError(data.error || 'Error al iniciar Stripe');
      }
    } catch {
      setError('Error de conexión con Stripe');
    } finally {
      setLoading(null);
    }
  };

  const handlePaypal = async () => {
    setLoading('paypal');
    setError('');

    try {
      const token = localStorage.getItem('token');

      const body = multiple
        ? { reservaIds }
        : { idReserva: reservaId };

      const res = await fetch(
        'http://localhost:8080/api/v1/payment/create',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(body)
        }
      );

      const data = await res.json();

      if (data.url) {
        window.location.href = data.url;
      } else {
        setError(data.error || 'Error al iniciar PayPal');
      }
    } catch {
      setError('Error de conexión con PayPal');
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="fixed inset-0 z-[200] flex items-center justify-center bg-teal-950/70 backdrop-blur-md p-4">
      <div className="bg-white rounded-[3rem] shadow-2xl w-full max-w-md p-8 relative animate-in fade-in zoom-in duration-200">

        {/* Cerrar */}
        <button
          onClick={onClose}
          className="absolute top-6 right-6 text-teal-900/40 hover:text-teal-900 transition-colors"
        >
          <X size={24} />
        </button>

        {/* Header */}
        <div className="text-center mb-8">
          <div className="bg-teal-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <Lock size={28} className="text-teal-700" />
          </div>

          <h2 className="text-2xl font-black text-teal-900 uppercase tracking-tight">
            Pago seguro
          </h2>

          <p className="text-teal-900/50 text-sm mt-1 font-bold">
            {descripcion}
          </p>

          {multiple && (
            <p className="text-xs text-teal-500 font-bold uppercase tracking-widest mt-2">
              Pago agrupado de {reservaIds.length} reservas
            </p>
          )}

          <div className="mt-3 bg-teal-50 rounded-2xl py-3 px-6 inline-block">
            <span className="text-3xl font-black text-teal-700">
              {formatPrice(precio)}
            </span>
          </div>
        </div>

        {/* Sandbox */}
        <div className="bg-yellow-50 border border-yellow-200 rounded-2xl px-4 py-3 mb-6 flex items-start gap-2">
          <CheckCircle size={16} className="text-yellow-600 mt-0.5 flex-shrink-0" />
          <p className="text-yellow-700 text-xs font-semibold">
            Modo <strong>Sandbox</strong> — no se cargará dinero real.
          </p>
        </div>

        {/* Error */}
        {error && (
          <div className="bg-red-50 text-red-700 text-xs font-bold px-4 py-3 rounded-2xl border border-red-100 text-center mb-4">
            {error}
          </div>
        )}

        {/* Botones */}
        <div className="space-y-3">

          <button
            onClick={handleStripe}
            disabled={loading !== null}
            className="w-full bg-[#635BFF] hover:bg-[#5147e6] text-white rounded-2xl py-4 font-black uppercase tracking-widest text-sm flex items-center justify-center gap-3 transition-all disabled:opacity-50 shadow-lg"
          >
            {loading === 'stripe' ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Redirigiendo...
              </>
            ) : (
              <>
                <CreditCard size={20} />
                {multiple ? 'Pagar Todo con Stripe' : 'Pagar con Stripe'}
              </>
            )}
          </button>

          <div className="flex items-center gap-3">
            <div className="flex-1 h-px bg-teal-100" />
            <span className="text-teal-900/30 text-xs font-black uppercase tracking-widest">
              o
            </span>
            <div className="flex-1 h-px bg-teal-100" />
          </div>

          <button
            onClick={handlePaypal}
            disabled={loading !== null}
            className="w-full bg-[#FFC439] hover:bg-[#f0b72f] text-[#003087] rounded-2xl py-4 font-black uppercase tracking-widest text-sm flex items-center justify-center gap-3 transition-all disabled:opacity-50 shadow-lg"
          >
            {loading === 'paypal' ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Redirigiendo...
              </>
            ) : (
              <>
                <Wallet size={20} />
                {multiple ? 'Pagar Todo con PayPal' : 'Pagar con PayPal'}
              </>
            )}
          </button>

        </div>

        <p className="text-center text-teal-900/25 text-[10px] uppercase tracking-widest mt-6 font-bold">
          Transacción cifrada SSL · JourneyMate TFG
        </p>
      </div>
    </div>
  );
}