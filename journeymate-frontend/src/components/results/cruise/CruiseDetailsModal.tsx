import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Ship, Moon, MapPin, Anchor, Calendar, CheckCircle2, ChevronRight, Info } from 'lucide-react';

export const CruiseDetailsModal = ({ isOpen, onClose, cruise, searchData }: any) => {
    const [loginError, setLoginError] = useState('');
    const [isReserving, setIsReserving] = useState(false);
    const [isBooked, setIsBooked] = useState(false);
    const [personas, setPersonas] = useState(1);


    useEffect(() => {
        if (isOpen) {
            setLoginError('');
            setIsReserving(false);
            setIsBooked(false);
            document.body.style.overflow = 'hidden';
        }
        return () => { document.body.style.overflow = ''; };
    }, [isOpen]);

    if (!isOpen || !cruise) return null;

    // Mapeo de datos del DTO
    const nombre = cruise.nombreCrucero || 'Crucero';
    const barco = cruise.nombreBarco || 'N/A';
    const puerto = cruise.puertoSalida || 'N/A';
    const noches = cruise.noches || 0;
    const precio = cruise.precioDesde || 0;
    const moneda = cruise.moneda || 'USD';
    const fecha = cruise.fechaSalida;
    const paradas = cruise.paradas || [];
    const cabinas = cruise.cabinas || [];

    const formatPrice = (v: number) =>
        new Intl.NumberFormat('es-ES', { style: 'currency', currency: moneda, minimumFractionDigits: 0 }).format(v);

    const formatFecha = (d: string) =>
        d ? new Date(d + 'T00:00:00').toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }) : 'N/A';

    const isLogged = typeof window !== 'undefined' && !!localStorage.getItem('token');

    const handleReserve = async () => {
        if (isReserving || isBooked) return;
        setIsReserving(true);
        setLoginError('');

        try {
            const token = localStorage.getItem('token');
            const idUsuario = localStorage.getItem('idUsuario');

            if (!token || !idUsuario) {
                setLoginError('Debes iniciar sesión para reservar.');
                setIsReserving(false);
                return;
            }

            const fSalida = new Date(fecha + 'T00:00:00');
            const fLlegada = new Date(fSalida);
            fLlegada.setDate(fSalida.getDate() + noches + 1);

            const puertoFinal = paradas.length > 0
                ? paradas[paradas.length - 1].puerto
                : puerto;

            const precioTotalCalculado = precio * personas;

            const body = {
                idUsuario: Number(localStorage.getItem('idUsuario')),
                idTipoReserva: 2,
                idEstado: 1,
    fechaServicio: fecha ?? null,
                precioTotal: precioTotalCalculado,
                servicio: {
                    tipo: 'CRUCERO',
                    nombre: nombre,
                    precioBase: precio,
                    descripcion: `${noches} noches · ${personas} pasajero(s) · Naviera: ${barco}`,
                    ciudad: puerto,

                    naviera: barco,
                    origen: puerto,
                    destino: puertoFinal,
                    fechaSalida: fecha,
                    fechaRegreso: fLlegada.toISOString().split('T')[0]
                },
            };

            const response = await fetch('http://localhost:8080/api/v1/reservas/completa', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
                body: JSON.stringify(body),
            });

            if (!response.ok) throw new Error('Error al procesar la reserva');
            setIsBooked(true);
        } catch (e: any) {
            setLoginError(e.message || 'Error de conexión');
        } finally {
            setIsReserving(false);
        }
    };

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-[100] flex items-center justify-center p-2 sm:p-6">
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={onClose} className="absolute inset-0 bg-teal-950/60 backdrop-blur-xl" />

                <motion.div
                    initial={{ scale: 0.9, opacity: 0, y: 40 }} animate={{ scale: 1, opacity: 1, y: 0 }} exit={{ scale: 0.9, opacity: 0, y: 40 }}
                    className="relative bg-white w-full max-w-5xl max-h-[95vh] rounded-[2.5rem] sm:rounded-[3.5rem] shadow-2xl overflow-hidden flex flex-col"
                >
                    <button onClick={onClose} className="absolute top-6 right-6 z-50 bg-white/90 backdrop-blur-md p-3 rounded-full hover:bg-teal-600 hover:text-white transition-all shadow-xl border border-teal-100">
                        <X size={20} />
                    </button>

                    <div className="overflow-y-auto flex-1">
                        {/* Hero Section con Imagen Principal */}
                        <div className="relative h-64 sm:h-80 bg-teal-900 flex items-end overflow-hidden">
                            {cruise.imagenPrincipal ? (
                                <img src={cruise.imagenPrincipal} alt={nombre} className="absolute inset-0 w-full h-full object-cover opacity-60" />
                            ) : (
                                <Ship size={120} className="absolute right-10 bottom-10 text-white/10" />
                            )}
                            <div className="absolute inset-0 bg-gradient-to-t from-teal-950 via-teal-950/20 to-transparent" />
                            <div className="relative p-8 sm:p-12 w-full">
                                <div className="flex items-center gap-2 text-teal-300 text-[10px] font-black uppercase tracking-[0.3em] mb-3">
                                    <Anchor size={14} /> {barco}
                                </div>
                                <h2 className="text-3xl sm:text-5xl font-black text-white uppercase tracking-tighter leading-none max-w-3xl">
                                    {nombre}
                                </h2>
                            </div>
                        </div>

                        <div className="px-6 sm:px-12 py-10 grid grid-cols-1 lg:grid-cols-12 gap-10">
                            <div className="lg:col-span-8 space-y-12">

                                {/* Datos Rápidos */}
                                <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                                    <div className="p-5 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1">
                                        <Moon className="text-teal-600 mb-1" size={24} />
                                        <span className="text-[10px] text-teal-900/40 font-black uppercase">Duración</span>
                                        <span className="text-base font-bold text-teal-900">{noches} noches</span>
                                    </div>
                                    <div className="p-5 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1">
                                        <MapPin className="text-teal-600 mb-1" size={24} />
                                        <span className="text-[10px] text-teal-900/40 font-black uppercase">Puerto Salida</span>
                                        <span className="text-base font-bold text-teal-900">{puerto}</span>
                                    </div>
                                    <div className="p-5 rounded-3xl bg-teal-50 border border-teal-100 flex flex-col gap-1 col-span-2 sm:col-span-1">
                                        <Calendar className="text-teal-600 mb-1" size={24} />
                                        <span className="text-[10px] text-teal-900/40 font-black uppercase">Fecha Salida</span>
                                        <span className="text-base font-bold text-teal-900">{formatFecha(fecha)}</span>
                                    </div>
                                </div>

                                {/* Itinerario Estructurado */}
                                <section>
                                    <h3 className="text-sm font-black text-teal-900 uppercase tracking-widest mb-8 flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center">
                                            <MapPin size={16} className="text-teal-600" />
                                        </div>
                                        Ruta del Crucero
                                    </h3>
                                    <div className="relative border-l-2 border-teal-100 ml-4 pl-8 space-y-6">
                                        {paradas.length > 0 ? (
                                            paradas.map((p: any, i: number) => {
                                                // Lógica para evitar el "null"
                                                const esNavegacion = p.tipo === 'CRUISING' || !p.puerto || p.puerto === 'null';
                                                const nombrePuerto = esNavegacion ? 'Navegación en alta mar' : p.puerto;

                                                return (
                                                    <div key={i} className="relative">
                                                        <div className={`absolute -left-[41px] top-1 w-4 h-4 rounded-full border-4 border-white shadow-sm ${esNavegacion ? 'bg-teal-300' : 'bg-teal-500'
                                                            }`} />
                                                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                                                            <div>
                                                                <p className="text-[10px] font-black text-teal-400 uppercase">
                                                                    Día {p.dia} — {esNavegacion ? 'Navegación' : 'Escala'}
                                                                </p>
                                                                <p className={`text-lg font-black ${esNavegacion ? 'text-teal-900/50 italic' : 'text-teal-900'}`}>
                                                                    {nombrePuerto}
                                                                </p>
                                                                {p.region && !esNavegacion && (
                                                                    <p className="text-xs text-teal-600/60 font-bold">{p.region}</p>
                                                                )}
                                                            </div>

                                                            {!esNavegacion && (p.llegada || p.salida) && (
                                                                <div className="text-left sm:text-right">
                                                                    <p className="text-[10px] font-bold text-teal-900/30 uppercase">Horario</p>
                                                                    <p className="text-xs font-black text-teal-800">
                                                                        {p.llegada || '--:--'} — {p.salida || '--:--'}
                                                                    </p>
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                );
                                            })
                                        ) : (
                                            <p className="text-sm italic text-teal-900/40">Itinerario detallado no disponible</p>
                                        )}
                                    </div>
                                </section>

                                {/* Opciones de Cabinas */}
                                {cabinas.length > 0 && (
                                    <section>
                                        <h3 className="text-sm font-black text-teal-900 uppercase tracking-widest mb-6 flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center"><Anchor size={16} className="text-teal-600" /></div>
                                            Tipos de Camarote Disponibles
                                        </h3>
                                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                            {cabinas.map((cab: any, i: number) => (
                                                <div key={i} className="group p-6 rounded-[2.5rem] bg-white border-2 border-teal-50 hover:border-teal-500 transition-all duration-300">
                                                    <p className="text-[10px] font-black text-teal-400 uppercase mb-1">{cab.tipo}</p>
                                                    <div className="flex items-baseline gap-2">
                                                        <span className="text-2xl font-black text-teal-900">{formatPrice(cab.precio)}</span>
                                                        <span className="text-[10px] font-bold text-teal-900/30 uppercase">/ persona</span>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </section>
                                )}
                            </div>

                            {/* Columna Lateral - Card de Reserva */}
                            <div className="lg:col-span-4">
                                <div className="sticky top-8 space-y-4">
                                    <div className="bg-teal-900 rounded-[3rem] p-8 text-white shadow-2xl relative overflow-hidden">
                                        <div className="absolute top-0 right-0 p-6 opacity-10"><Ship size={80} /></div>

                                        <p className="text-teal-300/50 text-[10px] font-black uppercase tracking-widest mb-6">Resumen de Reserva</p>

                                        {/* SECCIÓN DE PERSONAS */}
                                        <div className="mb-8">
                                            <p className="text-teal-300/40 text-[10px] font-black uppercase tracking-widest mb-3">Número de Pasajeros</p>
                                            <div className="flex items-center justify-between bg-white/10 backdrop-blur-md p-2 rounded-2xl border border-white/10">
                                                <button
                                                    onClick={() => setPersonas(Math.max(1, personas - 1))}
                                                    className="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center hover:bg-white/20 transition-colors font-bold"
                                                >
                                                    -
                                                </button>
                                                <div className="flex flex-col items-center">
                                                    <span className="text-xl font-black">{personas}</span>
                                                    <span className="text-[8px] uppercase opacity-50 font-bold">{personas === 1 ? 'Persona' : 'Personas'}</span>
                                                </div>
                                                <button
                                                    onClick={() => setPersonas(personas + 1)}
                                                    className="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center hover:bg-white/20 transition-colors font-bold"
                                                >
                                                    +
                                                </button>
                                            </div>
                                        </div>

                                        <div className="mb-10">
                                            <p className="text-5xl font-black tracking-tighter mb-1">
                                                {formatPrice(precio * personas)}
                                            </p>
                                            <p className="text-xs text-teal-300/60 font-medium italic">
                                                {personas > 1 ? `(${formatPrice(precio)} por persona)` : '*Tasas y cargos incluidos'}
                                            </p>
                                        </div>

                                        <div className="space-y-4 mb-10">
                                            <div className="flex items-center gap-3 text-sm font-bold text-teal-100">
                                                <CheckCircle2 size={18} className="text-teal-400" /> Pensión completa
                                            </div>
                                            <div className="flex items-center gap-3 text-sm font-bold text-teal-100">
                                                <CheckCircle2 size={18} className="text-teal-400" /> Entretenimiento a bordo
                                            </div>
                                        </div>

                                        {loginError && (
                                            <div className="mb-4 bg-red-500/20 text-red-100 text-[10px] font-bold p-4 rounded-2xl border border-red-500/30 text-center">
                                                {loginError}
                                                {!isLogged && <a href="/login" className="block underline mt-1">Ir al login</a>}
                                            </div>
                                        )}

                                        <button
                                            onClick={handleReserve}
                                            disabled={isReserving || isBooked}
                                            className={`w-full h-20 rounded-[2rem] font-black uppercase tracking-widest text-xs transition-all flex items-center justify-center gap-3 shadow-xl
                        ${isBooked ? 'bg-green-600 text-white' : 'bg-white text-teal-900 hover:bg-teal-50 active:scale-95 disabled:opacity-50'}`}
                                        >
                                            {isReserving ? (
                                                <span className="animate-pulse">Procesando...</span>
                                            ) : isBooked ? (
                                                <><CheckCircle2 size={18} /> ¡Listo!</>
                                            ) : (
                                                <>Confirmar Reserva <ChevronRight size={18} /></>
                                            )}
                                        </button>
                                    </div>

                                    {/* Info Extra */}
                                    <div className="bg-teal-50 p-6 rounded-[2.5rem] border border-teal-100">
                                        <div className="flex gap-3">
                                            <Info className="text-teal-600 shrink-0" size={20} />
                                            <p className="text-[10px] text-teal-800/60 font-bold leading-relaxed">
                                                Precios calculados para {personas} {personas === 1 ? 'pasajero' : 'pasajeros'}. La confirmación final se realizará tras el pago del depósito.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </motion.div>
            </div>
        </AnimatePresence>
    );
};