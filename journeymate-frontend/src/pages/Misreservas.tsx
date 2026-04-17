// src/pages/MisReservas.tsx
import { useEffect, useState, useCallback } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  CalendarDays, Tag, ArrowLeft, PackageOpen, AlertCircle,
  Clock, CreditCard, History, ShoppingBag, CheckCircle2,
  RotateCcw, X, Ban, BadgeCheck, AlertTriangle
} from "lucide-react";
import { PaymentModal } from "../components/payment/PaymentModal";

interface Reserva {
  idReserva: number;
  servicioNombre: string;
  precioTotal: number;
  estadoNombre: string;
  tipoReservaNombre: string;
  fechaReserva: string;
  idServicio?: number;
  idTipoReserva?: number;
  precioBase?: number;
}

// ── Helpers visuales ──────────────────────────────────────────────────────────
const TIPO_ICON: Record<string, React.ReactNode> = {
  hotel:     <Hotel    size={20} />,
  vuelo:     <Plane    size={20} />,
  coche:     <Car      size={20} />,
  vtc:       <Car      size={20} />,
  actividad: <Ticket   size={20} />,
  crucero:   <Ship     size={20} />,
  tren:      <Train    size={20} />,
};
function getTipoIcon(tipo: string) {
  const key = tipo?.toLowerCase() ?? "";
  for (const k of Object.keys(TIPO_ICON)) if (key.includes(k)) return TIPO_ICON[k];
  return <Ticket size={20} />;
}
const ESTADO_STYLE: Record<string, string> = {
  confirmada: "bg-emerald-100 text-emerald-700 border-emerald-200",
  pendiente:  "bg-amber-100  text-amber-700  border-amber-200",
  cancelada:  "bg-red-100    text-red-600    border-red-200",
  completada: "bg-teal-100   text-teal-700   border-teal-200",
};
function getEstadoStyle(estado: string) {
  const key = estado?.toLowerCase() ?? "";
  for (const k of Object.keys(ESTADO_STYLE)) if (key.includes(k)) return ESTADO_STYLE[k];
  return "bg-gray-100 text-gray-600 border-gray-200";
}
function formatFecha(fecha: string) {
  if (!fecha) return "—";
  const [y, m, d] = fecha.split("-");
  return `${d}/${m}/${y}`;
}
function getTipoServicio(tipoReserva: string): string {
  const t = tipoReserva?.toUpperCase() ?? "";
  if (t === "HOTEL")     return "HOTEL";
  if (t === "VUELO")     return "VUELO";
  if (t === "VTC" || t === "COCHE") return "VTC";
  if (t === "CRUCERO")   return "CRUCERO";
  if (t === "TREN")      return "TREN";
  if (t === "ACTIVIDAD") return "ACTIVIDAD";
  return "HOTEL";
}

// ── Lógica de fechas ──────────────────────────────────────────────────────────

/**
 * Puede cancelar si el momento actual es ANTES de las 00:00 del día de la reserva.
 * Es decir: puede cancelar cualquier momento del día anterior (incluyendo 00:00),
 * pero NO puede cancelar una vez que ha llegado el día de la reserva (00:00 de ese día).
 *
 * Ejemplo:
 *   - Reserva el 16 de mayo → puede cancelar hasta las 23:59 del 15 de mayo
 *   - A las 00:00 del 16 de mayo → ya NO puede cancelar
 */
function puedeCancel(fechaReserva: string): boolean {
  if (!fechaReserva) return false;
  // Medianoche del día de la reserva (inicio del día)
  const [y, m, d] = fechaReserva.split("-").map(Number);
  const inicioReserva = new Date(y, m - 1, d, 0, 0, 0, 0);
  const ahora = new Date();
  return ahora < inicioReserva; // puede cancelar si AÚN no ha llegado el día
}

/**
 * La reserva está expirada cuando ha llegado o pasado el día de la reserva.
 * Desde las 00:00 del día de la reserva → pasa al Historial.
 */
function estaExpirada(fechaReserva: string): boolean {
  if (!fechaReserva) return false;
  const [y, m, d] = fechaReserva.split("-").map(Number);
  const inicioReserva = new Date(y, m - 1, d, 0, 0, 0, 0);
  return new Date() >= inicioReserva;
}

// ── Componente principal ──────────────────────────────────────────────────────
export default function MisReservas() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  type Tab = "pendientes" | "confirmadas" | "historial";

  const [pendientes,  setPendientes]  = useState<Reserva[]>([]);
  const [confirmadas, setConfirmadas] = useState<Reserva[]>([]);
  const [historial,   setHistorial]   = useState<Reserva[]>([]);
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState("");
  const [userName,    setUserName]    = useState("");
  const [tab,         setTab]         = useState<Tab>("pendientes");
  const [pagoToast,   setPagoToast]   = useState("");

  // Pago
  const [payModalOpen, setPayModalOpen] = useState(false);
  const [payReserva,   setPayReserva]   = useState<Reserva | null>(null);
  const [payAllMode,   setPayAllMode]   = useState(false);

  // Cancelación
  const [cancelReserva, setCancelReserva] = useState<Reserva | null>(null);
  const [cancelling,    setCancelling]    = useState(false);

  // Repetir
  const [repeatModalOpen, setRepeatModalOpen] = useState(false);
  const [repeatReserva,   setRepeatReserva]   = useState<Reserva | null>(null);
  const [repeating,       setRepeating]       = useState(false);
  const [repeatSuccess,   setRepeatSuccess]   = useState(false);

  const totalPendiente = pendientes.reduce((s, r) => s + (Number(r.precioTotal) || 0), 0);

  // ── Carga ───────────────────────────────────────────────────────────────────
  const cargarReservas = useCallback(async () => {
    const token   = localStorage.getItem("token");
    const rawId   = localStorage.getItem("idUsuario");
    const cleanId = String(Number(rawId));
    if (!token || cleanId === "NaN") { navigate("/login"); return; }

    try {
      // Pendientes
      const rA = await fetch(
        `http://localhost:8080/api/v1/reservas/usuario/${cleanId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (rA.ok) {
        const data: Reserva[] = await rA.json();
        setPendientes(data.filter(r => r.estadoNombre?.toLowerCase() === "pendiente"));
      }

      // Historial completo
      const rH = await fetch(
        `http://localhost:8080/api/v1/reservas/usuario/${cleanId}/historial`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (rH.ok) {
        const todas: Reserva[] = await rH.json();

        // ✅ CONFIRMADAS: todas las que tienen estado CONFIRMADA
        // Se quedan aquí hasta que el backend las marque COMPLETADA (pasa la fecha)
        setConfirmadas(
          todas.filter(r => r.estadoNombre?.toLowerCase() === "confirmada")
        );

        // ✅ HISTORIAL: solo COMPLETADA y CANCELADA
        setHistorial(
          todas.filter(r => {
            const e = r.estadoNombre?.toLowerCase();
            return e === "completada" || e === "cancelada";
          })
        );
      }
    } catch {
      setError("No se pudieron cargar tus reservas. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    const nombre = localStorage.getItem("userName");
    if (nombre) setUserName(nombre);
    cargarReservas();
  }, [cargarReservas]);

  // ── Detectar retorno del pago ───────────────────────────────────────────────
  useEffect(() => {
    const pago = searchParams.get("pago");
    if (pago === "ok") {
      setPagoToast("✅ ¡Pago completado! Tu reserva está en Confirmadas.");
      setTab("confirmadas");
      cargarReservas();
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 5000);
    } else if (pago === "cancelado") {
      setPagoToast("❌ Pago cancelado. Tu reserva sigue pendiente.");
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 4000);
    } else if (pago === "error") {
      setPagoToast("⚠️ Error en el pago. Inténtalo de nuevo.");
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 4000);
    }
    const t = searchParams.get("tab");
    if (t === "confirmadas") setTab("confirmadas");
    if (t === "historial")   setTab("historial");
    if (t === "pendientes")  setTab("pendientes");
  }, [searchParams]); // eslint-disable-line

  // ── Pagar ───────────────────────────────────────────────────────────────────
  const handlePagar = (r: Reserva) => {
    setPayReserva(r); setPayAllMode(false); setPayModalOpen(true);
  };
  const handlePagarTodo = () => {
    if (pendientes.length === 0) return;
    setPayReserva({
      idReserva: pendientes[0].idReserva,
      servicioNombre: `${pendientes.length} reservas pendientes`,
      precioTotal: totalPendiente,
      estadoNombre: "pendiente",
      tipoReservaNombre: "múltiple",
      fechaReserva: "",
    });
    setPayAllMode(true);
    setPayModalOpen(true);
  };

  // ── Cancelar ────────────────────────────────────────────────────────────────
  const confirmarCancelacion = async () => {
    if (!cancelReserva) return;

    // ✅ Validar en el momento de confirmar si aún se puede cancelar
    if (!puedeCancel(cancelReserva.fechaReserva)) {
      alert("No se puede cancelar esta reserva porque ya ha llegado el día de entrada.");
      setCancelReserva(null);
      return;
    }

    setCancelling(true);
    try {
      const token = localStorage.getItem("token");
      if (!token) { navigate("/login"); return; }
      const res = await fetch(
        `http://localhost:8080/api/v1/reservas/${cancelReserva.idReserva}/estado`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
          body: JSON.stringify({ estado: "CANCELADA" }),
        }
      );
      if (!res.ok) throw new Error("No se pudo cancelar");
      setPagoToast("🔄 Reserva cancelada. El reembolso se procesará en 3-5 días hábiles.");
      setCancelReserva(null);
      await cargarReservas();
      setTab("historial");
      setTimeout(() => setPagoToast(""), 6000);
    } catch (e: any) {
      alert("Error al cancelar: " + e.message);
    } finally {
      setCancelling(false);
    }
  };

  // ── Eliminar (solo PENDIENTE) ───────────────────────────────────────────────
  const handleEliminar = async (idReserva: number) => {
    const token = localStorage.getItem("token");
    if (!token) { navigate("/login"); return; }
    try {
      const res = await fetch(`http://localhost:8080/api/v1/reservas/${idReserva}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error();
      setPendientes(prev => prev.filter(r => r.idReserva !== idReserva));
    } catch { alert("No se pudo eliminar la reserva."); }
  };

  // ── Repetir ─────────────────────────────────────────────────────────────────
  const handleRepetir = (r: Reserva) => {
    setRepeatReserva(r); setRepeatSuccess(false); setRepeatModalOpen(true);
  };
  const confirmarRepeticion = async () => {
    if (!repeatReserva || repeating) return;
    setRepeating(true);
    try {
      const token = localStorage.getItem("token");
      const rawId = localStorage.getItem("idUsuario");
      if (!token || !rawId) { navigate("/login"); return; }
      const tipoServicio = getTipoServicio(repeatReserva.tipoReservaNombre);
      const body = {
        idUsuario:     Number(rawId),
        idTipoReserva: repeatReserva.idTipoReserva ?? 1,
        idEstado:      1,
        precioTotal:   repeatReserva.precioTotal,
        servicio: {
          tipo:        tipoServicio,
          nombre:      repeatReserva.servicioNombre,
          precioBase:  repeatReserva.precioBase ?? repeatReserva.precioTotal,
          descripcion: "Reserva repetida desde historial",
          estrellas:   tipoServicio === "HOTEL" ? 1 : undefined,
          origen:      ["VUELO","TREN","CRUCERO"].includes(tipoServicio) ? "—" : undefined,
          destino:     ["VUELO","TREN","CRUCERO"].includes(tipoServicio) ? "—" : undefined,
        },
      };
      const res = await fetch("http://localhost:8080/api/v1/reservas/completa", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify(body),
      });
      if (!res.ok) throw new Error(await res.text());
      setRepeatSuccess(true);
      await cargarReservas();
      setTimeout(() => { setRepeatModalOpen(false); setRepeatReserva(null); setTab("pendientes"); }, 2000);
    } catch (e: any) {
      alert("No se pudo repetir la reserva: " + e.message);
    } finally {
      setRepeating(false);
    }
  };

  // ── Loading / Error ─────────────────────────────────────────────────────────
  if (loading) return (
    <div className="min-h-screen flex items-center justify-center"
      style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>
      <div className="w-full max-w-3xl px-4 space-y-4">
        {[1,2,3].map(i => <div key={i} className="bg-white/60 rounded-3xl h-28 animate-pulse"/>)}
      </div>
    </div>
  );
  if (error) return (
    <div className="min-h-screen flex items-center justify-center px-4"
      style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>
      <div className="bg-white/80 backdrop-blur rounded-3xl p-8 flex flex-col items-center gap-3 shadow-xl max-w-sm w-full">
        <AlertCircle size={40} className="text-red-500"/>
        <p className="text-red-600 font-bold text-center">{error}</p>
        <button onClick={() => window.location.reload()}
          className="mt-2 bg-teal-900 text-white px-6 py-2 rounded-xl font-bold hover:bg-teal-800 transition-all">
          Reintentar
        </button>
      </div>
    </div>
  );

  // ── Card ────────────────────────────────────────────────────────────────────
  const ReservaCard = ({ r, cardTab }: { r: Reserva; cardTab: Tab }) => {
    const [confirmElim, setConfirmElim] = useState(false);
    const cancelable = puedeCancel(r.fechaReserva);

    return (
      <div className="bg-white/75 backdrop-blur border border-white/60 rounded-3xl shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all duration-200">
        <div className="p-5 flex items-start gap-4">
          <div className="shrink-0 w-12 h-12 rounded-2xl bg-teal-900/10 flex items-center justify-center text-teal-900">
            {getTipoIcon(r.tipoReservaNombre)}
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-start justify-between gap-2 flex-wrap">
              <h2 className="text-teal-900 font-black text-base leading-tight truncate max-w-[180px] sm:max-w-none">
                {r.servicioNombre ?? "Servicio"}
              </h2>
              <span className={`shrink-0 text-[10px] font-black uppercase tracking-wider px-2.5 py-1 rounded-full border ${getEstadoStyle(r.estadoNombre)}`}>
                {r.estadoNombre ?? "—"}
              </span>
            </div>
            <div className="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-xs text-teal-700/70 font-semibold">
              <span className="flex items-center gap-1"><Tag size={11}/>{r.tipoReservaNombre ?? "—"}</span>
              <span className="flex items-center gap-1"><CalendarDays size={11}/>{formatFecha(r.fechaReserva)}</span>
              <span className="flex items-center gap-1"><Clock size={11}/>#{r.idReserva}</span>
            </div>
          </div>

          <div className="shrink-0 flex flex-col items-end gap-2">
            <div className="text-right">
              <p className="text-teal-900 font-black text-lg leading-none">
                {r.precioTotal != null ? `${Number(r.precioTotal).toFixed(2)} €` : "—"}
              </p>
              <p className="text-teal-600/60 text-[10px] font-bold mt-0.5 uppercase tracking-wider">total</p>
            </div>

            {/* PENDIENTES */}
            {cardTab === "pendientes" && (
              <>
                <button onClick={() => handlePagar(r)}
                  className="flex items-center gap-1.5 text-[10px] font-black px-3 py-2 rounded-xl bg-teal-900 text-white hover:bg-teal-700 transition-all shadow-md">
                  <CreditCard size={12}/> Pagar
                </button>
                {confirmElim ? (
                  <div className="flex items-center gap-1">
                    <button onClick={() => setConfirmElim(false)}
                      className="text-[10px] font-bold px-2 py-1 rounded-lg bg-gray-100 text-gray-600 hover:bg-gray-200 transition-all">
                      No
                    </button>
                    <button onClick={() => handleEliminar(r.idReserva)}
                      className="text-[10px] font-bold px-2 py-1 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-all">
                      Sí
                    </button>
                  </div>
                ) : (
                  <button onClick={() => setConfirmElim(true)}
                    className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5 rounded-xl bg-red-50 text-red-500 hover:bg-red-100 border border-red-100 transition-all">
                    <X size={11}/> Eliminar
                  </button>
                )}
              </>
            )}

            {/* CONFIRMADAS */}
            {cardTab === "confirmadas" && (
              <>
                <span className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5 rounded-xl bg-emerald-50 text-emerald-600 border border-emerald-100">
                  <BadgeCheck size={12}/> Pagada
                </span>
                {/* ✅ Botón cancelar siempre visible */}
                {/* Al pulsar comprueba si se puede cancelar */}
                <button onClick={() => setCancelReserva(r)}
                  className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5 rounded-xl bg-red-50 text-red-500 hover:bg-red-100 border border-red-100 transition-all">
                  <Ban size={11}/> Cancelar
                </button>
              </>
            )}

            {/* HISTORIAL */}
            {cardTab === "historial" && (
              <button onClick={() => handleRepetir(r)}
                className="flex items-center gap-1.5 text-[10px] font-black px-3 py-2 rounded-xl bg-teal-50 text-teal-700 hover:bg-teal-100 border border-teal-200 transition-all">
                <RotateCcw size={12}/> Repetir
              </button>
            )}
          </div>
        </div>
      </div>
    );
  };

  const totalReservas = pendientes.length + confirmadas.length + historial.length;

  // ── Render ──────────────────────────────────────────────────────────────────
  return (
    <>
      {pagoToast && (
        <div className="fixed top-6 left-1/2 -translate-x-1/2 z-[300] bg-teal-900 text-white px-6 py-3 rounded-2xl shadow-2xl font-bold text-sm max-w-sm text-center">
          {pagoToast}
        </div>
      )}

      <div className="min-h-screen flex flex-col font-sans"
        style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>

        <header className="sticky top-0 z-20 px-4 pt-4 pb-2">
          <div className="max-w-3xl mx-auto bg-white/70 backdrop-blur-md border border-white/50 rounded-2xl px-4 py-3 flex items-center justify-between shadow-lg">
            <Link to="/" className="flex items-center gap-2 text-teal-900 font-black text-sm hover:text-teal-600 transition-colors">
              <ArrowLeft size={18}/> Volver
            </Link>
            <div className="text-center">
              <h1 className="text-teal-900 font-black text-lg uppercase tracking-tighter leading-none">Mis Reservas</h1>
              {userName && <p className="text-teal-600 text-[10px] font-bold tracking-widest uppercase">{userName}</p>}
            </div>
            <span className="bg-teal-900 text-white text-xs font-black px-3 py-1 rounded-full">{totalReservas}</span>
          </div>
        </header>

        <main className="flex-grow px-4 py-6">
          <div className="max-w-3xl mx-auto space-y-4">

            {/* 3 PESTAÑAS */}
            <div className="flex gap-1.5 bg-white/40 backdrop-blur p-1.5 rounded-2xl border border-white/50">
              <button onClick={() => setTab("pendientes")}
                className={`flex-1 flex items-center justify-center gap-1.5 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${
                  tab === "pendientes" ? "bg-amber-500 text-white shadow-lg" : "text-teal-800 hover:bg-white/60"
                }`}>
                <ShoppingBag size={13}/>
                <span className="hidden sm:inline">Pendientes</span>
                <span className="sm:hidden">Pend.</span>
                {pendientes.length > 0 && (
                  <span className={`text-[9px] px-1.5 py-0.5 rounded-full font-black ${tab==="pendientes"?"bg-white/30":"bg-amber-100 text-amber-700"}`}>
                    {pendientes.length}
                  </span>
                )}
              </button>

              <button onClick={() => setTab("confirmadas")}
                className={`flex-1 flex items-center justify-center gap-1.5 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${
                  tab === "confirmadas" ? "bg-emerald-600 text-white shadow-lg" : "text-teal-800 hover:bg-white/60"
                }`}>
                <BadgeCheck size={13}/>
                <span className="hidden sm:inline">Confirmadas</span>
                <span className="sm:hidden">Conf.</span>
                {confirmadas.length > 0 && (
                  <span className={`text-[9px] px-1.5 py-0.5 rounded-full font-black ${tab==="confirmadas"?"bg-white/30":"bg-emerald-100 text-emerald-700"}`}>
                    {confirmadas.length}
                  </span>
                )}
              </button>

              <button onClick={() => setTab("historial")}
                className={`flex-1 flex items-center justify-center gap-1.5 py-3 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all ${
                  tab === "historial" ? "bg-teal-900 text-white shadow-lg" : "text-teal-800 hover:bg-white/60"
                }`}>
                <History size={13}/>
                Historial
                {historial.length > 0 && (
                  <span className={`text-[9px] px-1.5 py-0.5 rounded-full font-black ${tab==="historial"?"bg-white/30":"bg-teal-900/10"}`}>
                    {historial.length}
                  </span>
                )}
              </button>
            </div>

            {/* TAB PENDIENTES */}
            {tab === "pendientes" && (
              pendientes.length === 0 ? (
                <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                  <PackageOpen size={52} className="text-teal-900/30"/>
                  <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">No tienes reservas pendientes</p>
                  <Link to="/" className="bg-teal-900 text-white px-6 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all">
                    Explorar destinos
                  </Link>
                </div>
              ) : (
                <>
                  <div className="space-y-4">
                    {pendientes.map(r => <ReservaCard key={r.idReserva} r={r} cardTab="pendientes"/>)}
                  </div>
                  <div className="bg-white/80 backdrop-blur border border-white/60 rounded-3xl shadow-lg p-5">
                    <div className="flex items-start justify-between gap-4 mb-4">
                      <div>
                        <p className="text-teal-900/50 text-[10px] font-black uppercase tracking-widest">
                          {pendientes.length} reserva{pendientes.length !== 1 ? "s" : ""} pendiente{pendientes.length !== 1 ? "s" : ""}
                        </p>
                        <p className="text-teal-900 font-black text-3xl mt-0.5">{totalPendiente.toFixed(2)} €</p>
                        <p className="text-teal-600/50 text-[9px] font-bold uppercase tracking-widest mt-1">
                          Paga una a una o todas a la vez
                        </p>
                      </div>
                      <div className="w-14 h-14 rounded-2xl bg-amber-100 flex items-center justify-center shrink-0">
                        <span className="text-2xl">💳</span>
                      </div>
                    </div>
                    <button onClick={handlePagarTodo}
                      className="w-full bg-teal-900 hover:bg-teal-800 text-white rounded-2xl py-4 font-black uppercase tracking-widest text-sm flex items-center justify-center gap-3 transition-all shadow-lg active:scale-95">
                      <CreditCard size={18}/>
                      Pagar todo — {totalPendiente.toFixed(2)} €
                    </button>
                  </div>
                </>
              )
            )}

            {/* TAB CONFIRMADAS */}
            {tab === "confirmadas" && (
              confirmadas.length === 0 ? (
                <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                  <CheckCircle2 size={52} className="text-teal-900/30"/>
                  <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">No tienes reservas confirmadas</p>
                  <p className="text-teal-900/40 text-xs font-bold text-center">
                    Cuando pagues una reserva aparecerá aquí hasta el día de entrada.
                  </p>
                </div>
              ) : (
                <>
                  <div className="bg-teal-50/60 border border-teal-100 rounded-2xl px-4 py-3 flex items-center gap-2">
                    <AlertTriangle size={14} className="text-amber-500 shrink-0"/>
                    <p className="text-teal-700 text-[10px] font-bold">
                      Puedes cancelar hasta las <strong>00:00 del día de la reserva</strong>.
                      Después ya no es posible cancelar. El reembolso tarda 3-5 días hábiles.
                    </p>
                  </div>
                  <div className="space-y-4">
                    {confirmadas.map(r => <ReservaCard key={r.idReserva} r={r} cardTab="confirmadas"/>)}
                  </div>
                </>
              )
            )}

            {/* TAB HISTORIAL */}
            {tab === "historial" && (
              historial.length === 0 ? (
                <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                  <History size={52} className="text-teal-900/30"/>
                  <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">Aún no tienes historial</p>
                  <p className="text-teal-900/40 text-xs font-bold text-center">
                    Las reservas completadas y canceladas aparecerán aquí.
                  </p>
                </div>
              ) : (
                <div className="space-y-4">
                  {historial.map(r => <ReservaCard key={r.idReserva} r={r} cardTab="historial"/>)}
                </div>
              )
            )}

          </div>
        </main>
      </div>

      {/* Modal CANCELAR */}
      {cancelReserva && (
        <div className="fixed inset-0 z-[200] flex items-center justify-center bg-teal-950/70 backdrop-blur-md p-4">
          <div className="bg-white rounded-[3rem] shadow-2xl w-full max-w-md p-8 relative">
            <button onClick={() => setCancelReserva(null)}
              className="absolute top-6 right-6 text-teal-900/40 hover:text-teal-900">
              <X size={24}/>
            </button>
            <div className="text-center mb-6">
              <div className="bg-red-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Ban size={28} className="text-red-500"/>
              </div>
              <h2 className="text-2xl font-black text-teal-900 uppercase tracking-tight">Cancelar reserva</h2>
              <p className="text-teal-900/50 text-sm mt-1 font-bold">{cancelReserva.servicioNombre}</p>
            </div>
            <div className="bg-red-50 border border-red-100 rounded-2xl p-4 mb-4 space-y-2">
              <div className="flex justify-between text-xs">
                <span className="font-black text-red-400 uppercase tracking-widest">Reserva</span>
                <span className="font-black text-teal-900">#{cancelReserva.idReserva}</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="font-black text-red-400 uppercase tracking-widest">Fecha</span>
                <span className="font-black text-teal-900">{formatFecha(cancelReserva.fechaReserva)}</span>
              </div>
              <div className="flex justify-between text-xs border-t border-red-100 pt-2">
                <span className="font-black text-red-400 uppercase tracking-widest">Reembolso</span>
                <span className="font-black text-emerald-600">{Number(cancelReserva.precioTotal).toFixed(2)} €</span>
              </div>
            </div>
            {puedeCancel(cancelReserva.fechaReserva) ? (
              <div className="bg-amber-50 border border-amber-200 rounded-2xl px-4 py-3 mb-6">
                <p className="text-amber-700 text-xs font-semibold">
                  ⚠️ El reembolso de <strong>{Number(cancelReserva.precioTotal).toFixed(2)} €</strong> se procesará
                  en <strong>3-5 días hábiles</strong> por el mismo método de pago.
                </p>
              </div>
            ) : (
              <div className="bg-red-50 border border-red-200 rounded-2xl px-4 py-3 mb-6">
                <p className="text-red-700 text-xs font-semibold">
                  ❌ <strong>No se puede cancelar.</strong> Hoy es el día de la reserva o ya pasó.
                  El período de cancelación ha expirado.
                </p>
              </div>
            )}
            <div className="flex gap-3">
              <button onClick={() => setCancelReserva(null)}
                className="flex-1 py-4 text-gray-400 font-bold text-[11px] uppercase tracking-widest">
                Volver
              </button>
              <button onClick={confirmarCancelacion} disabled={cancelling}
                className="flex-[2] py-4 bg-red-500 hover:bg-red-600 text-white text-[11px] font-black uppercase tracking-widest rounded-3xl shadow-xl transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-95">
                {cancelling ? "Cancelando..." : <><Ban size={16}/> Confirmar cancelación</>}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal REPETIR */}
      {repeatModalOpen && repeatReserva && (
        <div className="fixed inset-0 z-[200] flex items-center justify-center bg-teal-950/70 backdrop-blur-md p-4">
          <div className="bg-white rounded-[3rem] shadow-2xl w-full max-w-md p-8 relative">
            <button onClick={() => setRepeatModalOpen(false)}
              className="absolute top-6 right-6 text-teal-900/40 hover:text-teal-900">
              <X size={24}/>
            </button>
            {repeatSuccess ? (
              <div className="text-center py-4">
                <CheckCircle2 size={52} className="text-teal-500 mx-auto mb-4"/>
                <h2 className="text-2xl font-black text-teal-900 uppercase tracking-tight mb-2">¡Reserva creada!</h2>
                <p className="text-teal-900/50 text-sm font-bold">Yendo a Pendientes...</p>
              </div>
            ) : (
              <>
                <div className="text-center mb-6">
                  <div className="bg-teal-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                    <RotateCcw size={28} className="text-teal-700"/>
                  </div>
                  <h2 className="text-2xl font-black text-teal-900 uppercase tracking-tight">Repetir reserva</h2>
                  <p className="text-teal-900/50 text-sm mt-1 font-bold">Se creará una nueva reserva idéntica</p>
                </div>
                <div className="bg-teal-50 rounded-2xl p-5 mb-4 space-y-2">
                  <div className="flex justify-between">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Servicio</span>
                    <span className="text-sm font-black text-teal-900 truncate max-w-[200px]">{repeatReserva.servicioNombre}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Tipo</span>
                    <span className="text-xs font-bold text-teal-700">{repeatReserva.tipoReservaNombre}</span>
                  </div>
                  <div className="flex justify-between border-t border-teal-100 pt-2">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Precio</span>
                    <span className="text-xl font-black text-teal-900">{Number(repeatReserva.precioTotal).toFixed(2)} €</span>
                  </div>
                </div>
                <div className="bg-amber-50 border border-amber-200 rounded-2xl px-4 py-3 mb-6">
                  <p className="text-amber-700 text-xs font-semibold">
                    ⚠️ La nueva reserva quedará en <strong>PENDIENTE</strong>. Podrás pagarla desde Pendientes.
                  </p>
                </div>
                <div className="flex gap-3">
                  <button onClick={() => setRepeatModalOpen(false)}
                    className="flex-1 py-4 text-gray-400 font-bold text-[11px] uppercase tracking-widest">
                    Cancelar
                  </button>
                  <button onClick={confirmarRepeticion} disabled={repeating}
                    className="flex-[2] py-4 bg-teal-900 hover:bg-teal-800 text-white text-[11px] font-black uppercase tracking-widest rounded-3xl shadow-xl transition-all flex items-center justify-center gap-2 disabled:opacity-50 active:scale-95">
                    {repeating ? "Creando..." : <><RotateCcw size={16}/> Confirmar</>}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* Modal PAGO */}
      {payReserva && (
        <PaymentModal
          isOpen={payModalOpen}
          onClose={() => { setPayModalOpen(false); setPayReserva(null); setPayAllMode(false); }}
          reservaId={payReserva.idReserva}
          precio={Number(payReserva.precioTotal)}
          descripcion={payReserva.servicioNombre}
          multiple={payAllMode}
          reservaIds={payAllMode ? pendientes.map(r => r.idReserva) : undefined}
        />
      )}
    </>
  );
}