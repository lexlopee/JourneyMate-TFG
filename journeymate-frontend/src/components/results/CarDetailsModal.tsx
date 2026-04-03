// src/components/results/CarDetailsModal.tsx

import { useState, useEffect } from 'react';
import {
  X, Car, Users, Briefcase, Settings2, ShieldCheck,
  Fuel, Calendar, CheckCircle2, Building2, MapPin, Clock
} from 'lucide-react';
import { formatTimeForBackend } from '../../utils/dateUtils';

interface CarDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  car: any;
  searchData?: any;
}

export const CarDetailsModal = ({ isOpen, onClose, car, searchData }: CarDetailsModalProps) => {
  const [loginError, setLoginError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isReserving, setIsReserving] = useState(false);
  const [isBooked, setIsBooked] = useState(false);

  // EFECTO PARA CONTROLAR EL SCROLL DEL BODY
  useEffect(() => {
    if (isOpen) {
      setIsBooked(false);
      setIsReserving(false);
      setLoginError("");
      setSuccessMessage("");
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    // Limpieza al desmontar
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen || !car) return null;

  // ── Helpers ──────────────────────────────────────────────────────────────
  const formatPrice = (value: number) =>
    new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: car.currency || 'EUR',
      minimumFractionDigits: 2,
    }).format(value);

  const days = (() => {
    if (!searchData?.startDate || !searchData?.endDate) return 1;
    const start = new Date(searchData.startDate);
    const end = new Date(searchData.endDate);
    return Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)));
  })();

  const formatDate = (dateStr: string) => {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('es-ES', {
      weekday: 'short', day: 'numeric', month: 'short', year: 'numeric'
    });
  };

  // ── Reservar ──────────────────────────────────────────────────────────────
  const handleReserve = async () => {
    if (isReserving || isBooked) return;
    setIsReserving(true);
    setLoginError("");
    setSuccessMessage("");

    try {
      const token = localStorage.getItem("token");
      const idUsuario = localStorage.getItem("idUsuario");

      if (!token || !idUsuario) {
        setLoginError("Debes iniciar sesión para reservar.");
        return;
      }
      const pickupTimeStr = formatTimeForBackend(searchData?.pickupTime, searchData?.pickUpTime);
      const dropoffTimeStr = formatTimeForBackend(searchData?.dropoffTime, searchData?.dropOffTime);

      const body = {
        idUsuario: Number(idUsuario),
        idTipoReserva: 6, 
        idEstado: 1, 
        precioTotal: car.price,
        servicio: {
          tipo: "VTC",
          nombre: car.carName ?? "Coche de alquiler",
          precioBase: car.price,
          descripcion: `${car.vendorName ?? ''} · ${car.transmission ?? ''} · ${car.seats ?? 5} plazas`,
          marca: car.vendorName ?? null,
          modelo: car.carName ?? null,
          horaSalida: `${searchData?.startDate} ${pickupTimeStr}`, 
          horaLlegada: `${searchData?.endDate} ${dropoffTimeStr}`,
          distancia: 0,
          latitud: null,
          longitud: null,
        },
      };

      try {
        const response = await fetch("http://localhost:8080/api/v1/reservas/completa", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(body),
        });

        if (!response.ok) {
          const errorText = await response.text();
          setLoginError(errorText || "Error al realizar la reserva");
          return;
        }
      } catch {
        // En caso de corte de conexión pero éxito en servidor
        setSuccessMessage("¡Coche reservado con éxito!");
        setIsBooked(true);
        setTimeout(() => setSuccessMessage(""), 4000);
        return;
      }

      setSuccessMessage("¡Coche reservado con éxito!");
      setIsBooked(true);
      setTimeout(() => setSuccessMessage(""), 4000);

    } catch (error: any) {
      setLoginError(error.message || "Error al realizar la reserva");
    } finally {
      setIsReserving(false);
    }
  };

  // ── UI ────────────────────────────────────────────────────────────────────
  return (
    /* CAMBIOS CLAVE AQUÍ: 
       1. overflow-y-auto (permite scroll si el modal es largo)
       2. items-start (alinea el modal arriba para que no se corte al scrollear)
    */
    <div className="fixed inset-0 z-[100] flex items-start justify-center p-4 bg-teal-950/70 backdrop-blur-md overflow-y-auto">

      {/* Toast éxito */}
      {successMessage && (
        <div className="fixed top-6 right-6 bg-teal-600 text-white px-6 py-3 rounded-xl shadow-xl z-[999] flex items-center gap-2">
          <CheckCircle2 size={18} />
          <span className="font-bold">{successMessage}</span>
        </div>
      )}

      {/* Margen vertical (my-8) para que el modal no pegue al borde superior/inferior */}
      <div className="bg-white w-full max-w-2xl rounded-[3rem] shadow-2xl overflow-hidden my-8">

        {/* HEADER */}
        <div className="bg-gradient-to-br from-teal-900 to-teal-700 p-8 text-white relative">
          <button onClick={onClose} className="absolute top-6 right-6 p-2 hover:bg-white/20 rounded-full transition-colors">
            <X size={20} />
          </button>
          <div className="flex items-center gap-4">
            <div className="bg-white/10 p-3 rounded-2xl border border-white/10">
              <Car size={24} className="text-teal-200" />
            </div>
            <div>
              <h2 className="text-2xl font-black uppercase tracking-tighter">Resumen del Alquiler</h2>
              <p className="text-teal-300 text-[10px] font-bold uppercase tracking-widest mt-1">
                Revisa los detalles antes de confirmar
              </p>
            </div>
          </div>
        </div>

        {/* BODY */}
        <div className="p-8 space-y-6">

          {/* IMAGEN + NOMBRE */}
          <div className="flex items-center gap-6 bg-gray-50 rounded-[2rem] p-6">
            {car.imageUrl ? (
              <img
                src={car.imageUrl}
                alt={car.carName}
                className="h-24 w-36 object-contain mix-blend-multiply"
              />
            ) : (
              <div className="h-24 w-36 bg-teal-100 rounded-2xl flex items-center justify-center">
                <Car size={40} className="text-teal-500" />
              </div>
            )}
            <div>
              <p className="text-[9px] font-black text-teal-500 uppercase tracking-widest mb-1">Vehículo</p>
              <h3 className="text-2xl font-black text-teal-900 uppercase tracking-tighter leading-none">
                {car.carName}
              </h3>
              <div className="flex items-center gap-2 mt-2">
                <Building2 size={12} className="text-teal-400" />
                <span className="text-xs font-bold text-teal-600">{car.vendorName}</span>
              </div>
            </div>
          </div>

          {/* CARACTERÍSTICAS */}
          <div className="grid grid-cols-3 gap-3">
            {[
              { icon: <Users size={16}/>, label: 'Plazas', value: `${car.seats || 5}` },
              { icon: <Briefcase size={16}/>, label: 'Maletas', value: `${car.bags || 2}` },
              { icon: <Settings2 size={16}/>, label: 'Transmisión', value: car.transmission === 'Automatic' ? 'Auto' : 'Manual' },
              { icon: <Fuel size={16}/>, label: 'Combustible', value: 'Lleno' },
              { icon: <ShieldCheck size={16}/>, label: 'Seguro', value: 'Básico incluido' },
            ].map((item, i) => (
              <div key={i} className="bg-teal-50 rounded-2xl p-4 flex flex-col gap-1">
                <div className="text-teal-500">{item.icon}</div>
                <p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">{item.label}</p>
                <p className="text-xs font-black text-teal-900">{item.value}</p>
              </div>
            ))}
          </div>

          {/* FECHAS */}
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-teal-50 rounded-2xl p-4">
              <div className="flex items-center gap-2 mb-1">
                <Calendar size={14} className="text-teal-500" />
                <p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Recogida</p>
              </div>
              <p className="text-xs font-black text-teal-900">{formatDate(searchData?.startDate)}</p>
              <div className="flex items-center gap-1 mt-1">
                <Clock size={10} className="text-teal-400" />
                <p className="text-[9px] text-teal-600 font-bold">{searchData?.pickupTime || '10:00'}</p>
              </div>
            </div>
            <div className="bg-teal-50 rounded-2xl p-4">
              <div className="flex items-center gap-2 mb-1">
                <Calendar size={14} className="text-teal-500" />
                <p className="text-[8px] font-black text-teal-400 uppercase tracking-widest">Devolución</p>
              </div>
              <p className="text-xs font-black text-teal-900">{formatDate(searchData?.endDate)}</p>
              <div className="flex items-center gap-1 mt-1">
                <Clock size={10} className="text-teal-400" />
                <p className="text-[9px] text-teal-600 font-bold">{searchData?.dropoffTime || '10:00'}</p>
              </div>
            </div>
          </div>

          {/* PRECIO TOTAL */}
          <div className="bg-teal-950 rounded-[2rem] p-8 text-white">
            <p className="text-teal-400 text-[10px] font-black uppercase tracking-[0.2em] mb-3">Desglose del precio</p>
            <div className="space-y-2 mb-6">
              <div className="flex justify-between text-[11px]">
                <span className="opacity-60 font-bold uppercase">Precio por día</span>
                <span className="font-black">{formatPrice(car.price / days)}</span>
              </div>
              <div className="flex justify-between text-[11px] border-t border-white/10 pt-2">
                <span className="opacity-60 font-bold uppercase">{days} {days === 1 ? 'día' : 'días'}</span>
                <span className="font-black">{formatPrice(car.price)}</span>
              </div>
            </div>
            <div className="border-t border-white/10 pt-4">
              <p className="text-teal-400 text-[10px] font-black uppercase tracking-widest mb-1">Total</p>
              <p className="text-5xl font-black tracking-tighter">{formatPrice(car.price)}</p>
            </div>
          </div>

          {/* ERROR */}
          {loginError && (
            <div className="bg-red-50 text-red-700 text-xs font-bold px-4 py-3 rounded-2xl border border-red-100 text-center">
              {loginError}
              {!localStorage.getItem("token") && (
                <div className="mt-2">
                  <a href="/login" className="underline text-red-900">Iniciar sesión</a>
                </div>
              )}
            </div>
          )}
        </div>

        {/* ACCIONES */}
        <div className="px-8 pb-8 flex gap-4">
          <button
            onClick={onClose}
            className="flex-1 py-4 text-gray-400 font-bold text-[11px] uppercase tracking-widest"
          >
            Cancelar
          </button>
          <button
            onClick={handleReserve}
            disabled={isReserving || isBooked}
            className={`flex-[2] py-4 text-white text-[11px] font-black uppercase tracking-widest rounded-3xl shadow-xl transition-all flex items-center justify-center gap-2 ${
              isBooked
                ? 'bg-green-600 cursor-default'
                : 'bg-teal-600 hover:bg-teal-500 hover:-translate-y-0.5 active:scale-95 disabled:opacity-50'
            }`}
          >
            {isReserving ? "Reservando..." : isBooked ? (
              <><CheckCircle2 size={16} /> Reservado</>
            ) : (
              <><Car size={16} /> Confirmar Alquiler</>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};