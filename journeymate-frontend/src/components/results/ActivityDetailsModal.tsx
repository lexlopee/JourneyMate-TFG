import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  X, Star, Clock, MapPin, CheckCircle2, XCircle,
  Calendar, Info, ChevronRight, Image as ImageIcon,
  ShieldCheck, Languages, Smartphone
} from 'lucide-react';
import { LoadingVideo } from '../LoadingVideo';
import { PostBookingModal } from '../payment/PostBookingModal';

export const ActivityDetailsModal = ({ 
  isOpen, onClose, activityBasicData, details, loading, searchData 
}: any) => {
  
  // --- ESTADOS PARA LA BBDD Y RESERVA ---
  const [loginError, setLoginError] = useState('');
  const [isReserving, setIsReserving] = useState(false);
  const [isBooked, setIsBooked] = useState(false);
  const [postModalOpen, setPostModalOpen] = useState(false);
  const [reservaId, setReservaId] = useState<number | null>(null);

  // Resetear estados al abrir/cerrar
  useEffect(() => {
    if (isOpen) {
      setLoginError('');
      setIsReserving(false);
      setIsBooked(false);
      setPostModalOpen(false);
      setReservaId(null);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  // --- VARIABLES DE DATOS ---
  const nombre = details?.nombre || activityBasicData?.nombre;
  const precio = details?.precio || activityBasicData?.precio;
  const moneda = '€';
  const calificacion = details?.averageRating || activityBasicData?.calificacion;
  const fotos = details?.fotos && details.fotos.length > 0 
    ? details.fotos 
    : [activityBasicData?.urlFoto];

  const isLogged = typeof window !== 'undefined' && !!localStorage.getItem('token');

  // --- LÓGICA DE ENVÍO A LA BBDD ---
  const handleReserve = async () => {
    if (isReserving || isBooked) return;
    setIsReserving(true);
    setLoginError('');

    try {
      const token = localStorage.getItem('token');
      const idUsuario = localStorage.getItem('idUsuario');

      if (!token || !idUsuario) {
        setLoginError('Debes iniciar sesión para reservar esta actividad.');
        setIsReserving(false);
        return;
      }

      // Estructura que requiere tu BBDD según el esquema que pasaste
      const body = {
        idUsuario: Number(idUsuario),
        idTipoReserva: 6,
        idEstado: 1,
        precioTotal: precio,
        fechaServicio: searchData?.startDate ?? null,
        servicio: {
          tipo: 'ACTIVIDAD',
          nombre: nombre,
          precioBase: precio,
          descripcion: details?.shortDescription || "Actividad turística",
          fechaSalida: searchData?.startDate ?? null,
        },
      };

      const response = await fetch('http://localhost:8080/api/v1/reservas/completa', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json', 
          'Authorization': `Bearer ${token}` 
        },
        body: JSON.stringify(body),
      });

      if (!response.ok) throw new Error('Error al procesar la reserva en el servidor');

      const data = await response.json();
      setReservaId(data.idReserva); // Guardamos el ID que devuelve la BBDD
      setIsBooked(true);
      setPostModalOpen(true); // Abrimos el modal de pago

    } catch (e: any) {
      setLoginError(e.message || 'Error de conexión con el servidor');
    } finally {
      setIsReserving(false);
    }
  };

  const handleContinueShopping = () => {
    setPostModalOpen(false);
    onClose();
  };

  return (
    <>
      <AnimatePresence>
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-2 sm:p-6">
          <motion.div 
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-teal-950/60 backdrop-blur-xl"
          />

          <motion.div 
            initial={{ scale: 0.9, opacity: 0, y: 40 }}
            animate={{ scale: 1, opacity: 1, y: 0 }}
            exit={{ scale: 0.9, opacity: 0, y: 40 }}
            className="relative bg-white w-full max-w-6xl max-h-[95vh] rounded-[2.5rem] sm:rounded-[3.5rem] shadow-2xl overflow-hidden flex flex-col"
          >
            {/* Botón Cerrar */}
            <button onClick={onClose} className="absolute top-6 right-6 z-50 bg-white/90 backdrop-blur-md p-3 rounded-full hover:bg-teal-600 hover:text-white transition-all shadow-xl border border-teal-100">
              <X size={20} className="font-bold" />
            </button>

            <div className="overflow-y-auto custom-scrollbar pb-12">
              
              {/* 1. Galería Hero */}
              <div className="relative h-[40vh] sm:h-[55vh] bg-teal-900 group">
                <div className="flex h-full overflow-x-auto snap-x snap-mandatory hide-scrollbar scroll-smooth">
                  {fotos.map((foto: string, idx: number) => (
                    <div key={idx} className="h-full w-full flex-shrink-0 snap-center relative">
                      <img src={foto} className="h-full w-full object-cover" alt={`Imagen ${idx}`} />
                      <div className="absolute inset-0 bg-gradient-to-t from-teal-950 via-transparent to-black/20" />
                    </div>
                  ))}
                </div>
                
                <div className="absolute bottom-6 right-8 bg-black/50 backdrop-blur-md px-4 py-2 rounded-full text-white text-[10px] font-black uppercase tracking-widest flex items-center gap-2">
                  <ImageIcon size={14} /> {fotos.length} Fotos
                </div>

                <div className="absolute bottom-8 left-8 right-8 max-w-3xl">
                  <h2 className="text-3xl sm:text-6xl font-black text-white uppercase tracking-tighter leading-[0.9] mb-6 drop-shadow-2xl">
                    {nombre}
                  </h2>
                  <div className="flex flex-wrap gap-3">
                    <div className="flex items-center gap-2 bg-white/20 backdrop-blur-md border border-white/30 px-4 py-2 rounded-2xl text-white">
                      <Star size={18} className="text-yellow-400 fill-yellow-400" />
                      <span className="font-black text-lg">{calificacion || '4.5'}</span>
                    </div>
                    {details?.hasFreeCancellation && (
                      <div className="flex items-center gap-2 bg-teal-500/80 backdrop-blur-md px-4 py-2 rounded-2xl text-white text-[10px] font-black uppercase tracking-wider">
                        <ShieldCheck size={16} /> Cancelación Gratuita
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* 2. Contenido Grid */}
              <div className="px-6 sm:px-12 py-12 grid grid-cols-1 lg:grid-cols-12 gap-12">
                
                {/* Columna Izquierda */}
                <div className="lg:col-span-8 space-y-12">
                  {loading ? (
                    <div className="flex flex-col items-center justify-center py-20">
                      <LoadingVideo size={120} />
                    </div>
                  ) : (
                    <>
                      {/* Resumen rápido */}
                      <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                          <div className="p-4 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1">
                              <Clock className="text-teal-600 mb-1" size={20}/>
                              <span className="text-[10px] text-teal-900/40 font-black uppercase">Duración</span>
                              <span className="text-sm font-bold text-teal-900">{details?.duration || 'Flexible'}</span>
                          </div>
                          <div className="p-4 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1">
                              <Smartphone className="text-teal-600 mb-1" size={20}/>
                              <span className="text-[10px] text-teal-900/40 font-black uppercase">Ticket</span>
                              <span className="text-sm font-bold text-teal-900">Digital / Móvil</span>
                          </div>
                          <div className="p-4 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1">
                              <Languages className="text-teal-600 mb-1" size={20}/>
                              <span className="text-[10px] text-teal-900/40 font-black uppercase">Idioma</span>
                              <span className="text-sm font-bold text-teal-900">Español / Inglés</span>
                          </div>
                      </div>

                      <section>
                        <h3 className="text-sm font-black text-teal-900 uppercase tracking-widest mb-6 flex items-center gap-2">
                          <Info size={18} className="text-teal-600" /> Descripción completa
                        </h3>
                        <div className="text-teal-900/70 leading-relaxed text-lg font-medium whitespace-pre-line bg-gray-50/50 p-6 rounded-[2rem]">
                          {details?.descripcionLarga || activityBasicData?.descripcion}
                        </div>
                      </section>

                      {/* Inclusiones y Exclusiones */}
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {details?.whatsIncluded && (
                          <div className="bg-emerald-50/50 p-8 rounded-[2.5rem] border border-emerald-100">
                            <h4 className="text-xs font-black text-emerald-900/40 uppercase tracking-widest mb-6">Lo que incluye</h4>
                            <ul className="space-y-4">
                              {details.whatsIncluded.map((item: string, i: number) => (
                                <li key={i} className="flex items-start gap-3">
                                  <CheckCircle2 className="text-emerald-600 flex-shrink-0" size={18} />
                                  <span className="text-emerald-950 font-bold text-sm leading-tight">{item}</span>
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}
                        {details?.notIncluded && (
                          <div className="bg-rose-50/50 p-8 rounded-[2.5rem] border border-rose-100">
                            <h4 className="text-xs font-black text-rose-900/40 uppercase tracking-widest mb-6">No incluye</h4>
                            <ul className="space-y-4">
                              {details.notIncluded.map((item: string, i: number) => (
                                <li key={i} className="flex items-start gap-3">
                                  <XCircle className="text-rose-400 flex-shrink-0" size={18} />
                                  <span className="text-rose-950 font-bold text-sm leading-tight">{item}</span>
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}
                      </div>
                    </>
                  )}
                </div>

                {/* Columna Derecha: Tarjeta de Precio e Integración con BBDD */}
                <div className="lg:col-span-4">
                  <div className="sticky top-8 space-y-4">
                    <div className="bg-white border-2 border-teal-900 rounded-[3rem] p-8 shadow-2xl overflow-hidden relative">
                      <div className="absolute top-0 right-0 bg-teal-900 text-white px-6 py-2 rounded-bl-3xl font-black text-[10px] uppercase">
                          Garantía JourneyMate
                      </div>

                      <div className="mb-8 mt-4">
                        <p className="text-teal-900/40 text-[10px] font-black uppercase tracking-widest mb-1">Precio Total</p>
                        <div className="flex items-baseline gap-2">
                          <span className="text-6xl font-black text-teal-900 tracking-tighter">{precio}</span>
                          <span className="text-2xl font-bold text-teal-600">{moneda}</span>
                        </div>
                      </div>

                      <div className="space-y-3 mb-8">
                        <div className="flex items-center gap-4 bg-teal-50 p-5 rounded-3xl border border-teal-100">
                          <Calendar size={22} className="text-teal-600" />
                          <div>
                            <p className="text-[10px] text-teal-900/40 font-black uppercase">Fecha</p>
                            <p className="text-sm font-black text-teal-900">
                              {searchData?.startDate ? new Date(searchData.startDate).toLocaleDateString('es-ES', { day: '2-digit', month: 'long', year: 'numeric' }) : 'Hoy'}
                            </p>
                          </div>
                        </div>
                      </div>

                      {/* MENSAJE DE ERROR DE BBDD / LOGIN */}
                      {loginError && (
                        <div className="mb-4 bg-red-50 text-red-600 text-[10px] font-bold p-4 rounded-2xl border border-red-100 text-center">
                          {loginError}
                          {!isLogged && <a href="/login" className="block underline mt-1">Ir al login</a>}
                        </div>
                      )}

                      {/* BOTÓN DE RESERVA CONECTADO AL BACKEND */}
                      <button 
                        onClick={handleReserve}
                        disabled={isReserving || isBooked || loading}
                        className={`w-full h-20 rounded-[2rem] font-black uppercase tracking-widest text-xs transition-all flex items-center justify-center gap-3 shadow-xl 
                          ${isBooked ? 'bg-green-600 text-white' : 'bg-teal-900 text-white hover:bg-teal-800 shadow-teal-900/20'}`}
                      >
                        {isReserving ? (
                          <span className="animate-pulse">Sincronizando...</span>
                        ) : isBooked ? (
                          <><CheckCircle2 size={18} /> ¡Reservado!</>
                        ) : (
                          <>
                            <span>Reservar ahora</span>
                            <div className="bg-white/20 p-2 rounded-full">
                              <ChevronRight size={18} />
                            </div>
                          </>
                        )}
                      </button>

                      <p className="text-center text-[10px] text-teal-900/30 font-bold mt-6 uppercase tracking-widest">
                          Encriptación SSL de 256 bits
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </motion.div>
        </div>
      </AnimatePresence>

      {/* MODAL DE PAGO (Solo se muestra tras el éxito en la BBDD) */}
      {reservaId && (
        <PostBookingModal 
          isOpen={postModalOpen} 
          onClose={handleContinueShopping}
          reservaId={reservaId} 
          precio={precio}
          descripcion={`Reserva de Actividad: ${nombre}`}
        />
      )}
    </>
  );
};