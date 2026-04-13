// src/pages/MisReservas.tsx
import { useEffect, useState, useCallback } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  CalendarDays, Tag, ArrowLeft,
  PackageOpen, AlertCircle, Clock,
  CreditCard, Trash2, History, ShoppingBag, CheckCircle2, RotateCcw, X
} from "lucide-react";
import { PaymentModal } from "../components/payment/PaymentModal";

interface Reserva {
  idReserva: number;
  servicioNombre: string;
  precioTotal: number;
  estadoNombre: string;
  tipoReservaNombre: string;
  fechaReserva: string;
  // ✅ Nuevos campos para "Repetir reserva"
  idServicio?: number;
  idTipoReserva?: number;
  precioBase?: number;
}

const TIPO_ICON: Record<string, React.ReactNode> = {
  hotel:     <Hotel  size={20} />,
  vuelo:     <Plane  size={20} />,
  coche:     <Car    size={20} />,
  vtc:       <Car    size={20} />,
  actividad: <Ticket size={20} />,
  crucero:   <Ship   size={20} />,
  tren:      <Train  size={20} />,
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

export default function MisReservas() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [reservas,        setReservas]        = useState<Reserva[]>([]);
  const [historial,       setHistorial]       = useState<Reserva[]>([]);
  const [loading,         setLoading]         = useState(true);
  const [error,           setError]           = useState("");
  const [userName,        setUserName]        = useState("");
  const [tab,             setTab]             = useState<"activas" | "historial">("activas");
  const [deletingId,      setDeletingId]      = useState<number | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);
  const [pagoToast,       setPagoToast]       = useState("");

  const [payModalOpen, setPayModalOpen] = useState(false);
  const [payReserva,   setPayReserva]   = useState<Reserva | null>(null);
  const [payAllMode,   setPayAllMode]   = useState(false);

  // ── Estado para modal "Repetir reserva" ──
  const [repeatModalOpen, setRepeatModalOpen] = useState(false);
  const [repeatReserva,   setRepeatReserva]   = useState<Reserva | null>(null);
  const [repeating,       setRepeating]       = useState(false);
  const [repeatSuccess,   setRepeatSuccess]   = useState(false);

  // ✅ Activas = solo PENDIENTE (sin pagar)
  // CONFIRMADA ya fue pagada → va al historial
  const reservasActivas   = reservas.filter(r => {
    const e = r.estadoNombre?.toLowerCase();
    return e === "pendiente";
  });
  const reservasPendientes = reservasActivas.filter(
    r => r.estadoNombre?.toLowerCase() === "pendiente"
  );
  const totalPendiente = reservasPendientes.reduce(
    (s, r) => s + (Number(r.precioTotal) || 0), 0
  );

  // ── Carga de datos ────────────────────────────────────────────────────────
  const cargarReservas = useCallback(async () => {
    const token     = localStorage.getItem("token");
    const idUsuario = localStorage.getItem("idUsuario");
    const cleanId   = String(Number(idUsuario));
    if (!token || cleanId === "NaN") { navigate("/login"); return; }

    try {
      // Activas (solo PENDIENTE)
      const resActivas = await fetch(
        `http://localhost:8080/api/v1/reservas/usuario/${cleanId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (resActivas.ok) setReservas(await resActivas.json());

      // Historial (COMPLETADA + CANCELADA)
      const resHistorial = await fetch(
        `http://localhost:8080/api/v1/reservas/usuario/${cleanId}/historial`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (resHistorial.ok) {
        // ✅ El endpoint devuelve TODAS las reservas del usuario
        // sin filtro de estado — así siempre hay registro de todo
        setHistorial(await resHistorial.json());
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

  // ── Detectar retorno del pago (Stripe/PayPal redirigen aquí) ─────────────
  useEffect(() => {
    const pago = searchParams.get("pago");
    const tab  = searchParams.get("tab");

    if (pago === "ok") {
      // ✅ Pago exitoso: recargar datos, ir a historial y mostrar toast
      setPagoToast("✅ ¡Pago completado! Tu reserva está en el historial.");
      setTab("historial");
      cargarReservas();
      // Limpiar los params de la URL
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 5000);
    } else if (pago === "cancelado") {
      setPagoToast("❌ El pago fue cancelado. Tu reserva sigue pendiente.");
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 4000);
    } else if (pago === "error") {
      setPagoToast("⚠️ Ocurrió un error con el pago. Inténtalo de nuevo.");
      setSearchParams({});
      setTimeout(() => setPagoToast(""), 4000);
    }

    if (tab === "historial") setTab("historial");
  }, [searchParams]); // eslint-disable-line

  // ── Eliminar reserva ─────────────────────────────────────────────────────
  const handleDelete = async (idReserva: number) => {
    const token = localStorage.getItem("token");
    if (!token) { navigate("/login"); return; }
    setDeletingId(idReserva);
    setConfirmDeleteId(null);
    try {
      const res = await fetch(`http://localhost:8080/api/v1/reservas/${idReserva}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error();
      setReservas(prev => prev.filter(r => r.idReserva !== idReserva));
    } catch {
      alert("No se pudo eliminar la reserva.");
    } finally {
      setDeletingId(null);
    }
  };

  // ── Pagar una reserva ────────────────────────────────────────────────────
  const handlePagarReserva = (r: Reserva) => {
    setPayReserva(r);
    setPayAllMode(false);
    setPayModalOpen(true);
  };

  // ── Pagar todo ───────────────────────────────────────────────────────────
  const handlePagarTodo = () => {
    if (reservasPendientes.length === 0) return;
    setPayReserva({
      idReserva: reservasPendientes[0].idReserva,
      servicioNombre: `${reservasPendientes.length} reservas pendientes`,
      precioTotal: totalPendiente,
      estadoNombre: "pendiente",
      tipoReservaNombre: "múltiple",
      fechaReserva: "",
    });
    setPayAllMode(true);
    setPayModalOpen(true);
  };

  const cerrarModal = () => {
    setPayModalOpen(false);
    setPayReserva(null);
    setPayAllMode(false);
  };

  // ── Repetir reserva ─────────────────────────────────────────────────────
  const handleRepetir = (r: Reserva) => {
    setRepeatReserva(r);
    setRepeatSuccess(false);
    setRepeatModalOpen(true);
  };

  const confirmarRepeticion = async () => {
    if (!repeatReserva || repeating) return;
    setRepeating(true);
    try {
      const token     = localStorage.getItem("token");
      const idUsuario = localStorage.getItem("idUsuario");
      if (!token || !idUsuario) { navigate("/login"); return; }

      const body = {
        idUsuario:    Number(String(Number(idUsuario))),
        idTipoReserva: repeatReserva.idTipoReserva ?? 1,
        idEstado:     1, // PENDIENTE
        precioTotal:  repeatReserva.precioTotal,
        servicio: {
          tipo:       getTipoServicio(repeatReserva.tipoReservaNombre),
          nombre:     repeatReserva.servicioNombre,
          precioBase: repeatReserva.precioBase ?? repeatReserva.precioTotal,
          descripcion: "Reserva repetida desde historial",
          estrellas:  1,
        },
      };

      const res = await fetch("http://localhost:8080/api/v1/reservas/completa", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify(body),
      });

      if (!res.ok) throw new Error(await res.text());
      setRepeatSuccess(true);
      // Recargar reservas activas para que aparezca la nueva
      await cargarReservas();
      setTimeout(() => {
        setRepeatModalOpen(false);
        setRepeatReserva(null);
        setTab("activas");
      }, 2000);
    } catch (e: any) {
      alert("No se pudo repetir la reserva: " + e.message);
    } finally {
      setRepeating(false);
    }
  };

  // Mapea el nombre del tipo de reserva al tipo del servicio
  function getTipoServicio(tipoReserva: string): string {
    const t = tipoReserva?.toUpperCase() ?? "";
    if (t.includes("HOTEL")) return "HOTEL";
    if (t.includes("VUELO")) return "VUELO";
    if (t.includes("VTC") || t.includes("COCHE")) return "VTC";
    if (t.includes("CRUCERO")) return "CRUCERO";
    if (t.includes("TREN")) return "TREN";
    if (t.includes("ACTIVIDAD")) return "ACTIVIDAD";
    return "HOTEL";
  }

  // ── Loading / error ───────────────────────────────────────────────────────
  if (loading) return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-6 px-4"
      style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>
      <div className="w-full max-w-3xl space-y-4">
        {[1,2,3].map(i => <div key={i} className="bg-white/60 rounded-3xl h-28 animate-pulse"/>)}
      </div>
    </div>
  );

  if (error) return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-4 px-4"
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

  // ── Card de reserva ───────────────────────────────────────────────────────
  const ReservaCard = ({ r, showActions = true }: { r: Reserva; showActions?: boolean }) => {
    const isPendiente  = r.estadoNombre?.toLowerCase() === "pendiente";
    const isConfirmada = r.estadoNombre?.toLowerCase() === "confirmada";

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
              <p className="text-teal-600/60 text-[10px] font-bold mt-0.5 uppercase tracking-wider">precio total</p>
            </div>

            {/* ✅ Botón REPETIR — siempre visible en historial (showActions=false) */}
            {!showActions && (
              <button onClick={() => handleRepetir(r)}
                className="flex items-center gap-1.5 text-[10px] font-black px-3 py-2 rounded-xl bg-teal-50 text-teal-700 hover:bg-teal-100 border border-teal-200 transition-all">
                <RotateCcw size={12}/> Repetir
              </button>
            )}

            {showActions && (
              <>
                {/* Botón PAGAR — solo PENDIENTE */}
                {isPendiente && (
                  <button onClick={() => handlePagarReserva(r)}
                    className="flex items-center gap-1.5 text-[10px] font-black px-3 py-2 rounded-xl bg-teal-900 text-white hover:bg-teal-700 transition-all shadow-md shadow-teal-900/20">
                    <CreditCard size={12}/> Pagar
                  </button>
                )}

                {/* Badge pagada — CONFIRMADA */}
                {isConfirmada && (
                  <span className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5 rounded-xl bg-emerald-50 text-emerald-600 border border-emerald-100">
                    <CheckCircle2 size={12}/> Pagada
                  </span>
                )}

                {/* Eliminar — solo PENDIENTE, con confirmación */}
                {isPendiente && (
                  confirmDeleteId === r.idReserva ? (
                    <div className="flex items-center gap-1">
                      <button onClick={() => setConfirmDeleteId(null)}
                        className="text-[10px] font-bold px-2 py-1 rounded-lg bg-gray-100 text-gray-600 hover:bg-gray-200 transition-all">
                        Cancelar
                      </button>
                      <button onClick={() => handleDelete(r.idReserva)} disabled={deletingId === r.idReserva}
                        className="text-[10px] font-bold px-2 py-1 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-all disabled:opacity-50">
                        {deletingId === r.idReserva ? "..." : "Confirmar"}
                      </button>
                    </div>
                  ) : (
                    <button onClick={() => setConfirmDeleteId(r.idReserva)}
                      className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5 rounded-xl bg-red-50 text-red-500 hover:bg-red-100 border border-red-100 transition-all">
                      <Trash2 size={12}/> Eliminar
                    </button>
                  )
                )}
              </>
            )}
          </div>
        </div>
      </div>
    );
  };

  // ── Render ─────────────────────────────────────────────────────────────────
  return (
    <>
      {/* Toast de resultado de pago */}
      {pagoToast && (
        <div className="fixed top-6 left-1/2 -translate-x-1/2 z-[300] bg-teal-900 text-white px-6 py-3 rounded-2xl shadow-2xl font-bold text-sm max-w-sm text-center">
          {pagoToast}
        </div>
      )}

      <div className="min-h-screen flex flex-col font-sans"
        style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>

        {/* Header */}
        <header className="sticky top-0 z-20 px-4 pt-4 pb-2">
          <div className="max-w-3xl mx-auto bg-white/70 backdrop-blur-md border border-white/50 rounded-2xl px-4 py-3 flex items-center justify-between shadow-lg">
            <Link to="/" className="flex items-center gap-2 text-teal-900 font-black text-sm hover:text-teal-600 transition-colors">
              <ArrowLeft size={18}/> Volver
            </Link>
            <div className="text-center">
              <h1 className="text-teal-900 font-black text-lg uppercase tracking-tighter leading-none">Mis Reservas</h1>
              {userName && <p className="text-teal-600 text-[10px] font-bold tracking-widest uppercase">{userName}</p>}
            </div>
            <span className="bg-teal-900 text-white text-xs font-black px-3 py-1 rounded-full">
              {reservasActivas.length + historial.length}
            </span>
          </div>
        </header>

        <main className="flex-grow px-4 py-6">
          <div className="max-w-3xl mx-auto space-y-4">

            {/* Pestañas */}
            <div className="flex gap-2 bg-white/40 backdrop-blur p-1.5 rounded-2xl border border-white/50">
              <button onClick={() => setTab("activas")}
                className={`flex-1 flex items-center justify-center gap-2 py-3 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${
                  tab === "activas" ? "bg-teal-900 text-white shadow-lg" : "text-teal-800 hover:bg-white/60"
                }`}>
                <ShoppingBag size={14}/> Mis reservas
                {reservasActivas.length > 0 && (
                  <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${tab==="activas"?"bg-white/20":"bg-teal-900/10"}`}>
                    {reservasActivas.length}
                  </span>
                )}
              </button>
              <button onClick={() => setTab("historial")}
                className={`flex-1 flex items-center justify-center gap-2 py-3 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${
                  tab === "historial" ? "bg-teal-900 text-white shadow-lg" : "text-teal-800 hover:bg-white/60"
                }`}>
                <History size={14}/> Historial
                {historial.length > 0 && (
                  <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${tab==="historial"?"bg-white/20":"bg-teal-900/10"}`}>
                    {historial.length}
                  </span>
                )}
              </button>
            </div>

            {/* ── TAB ACTIVAS ── */}
            {tab === "activas" && (
              <>
                {reservasActivas.length === 0 ? (
                  <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                    <PackageOpen size={52} className="text-teal-900/30"/>
                    <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">No tienes reservas activas</p>
                    <Link to="/" className="bg-teal-900 text-white px-6 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all">
                      Explorar destinos
                    </Link>
                  </div>
                ) : (
                  <>
                    <div className="space-y-4">
                      {reservasActivas.map(r => <ReservaCard key={r.idReserva} r={r} showActions />)}
                    </div>

                    {/* Panel total pendiente + Pagar todo */}
                    {reservasPendientes.length > 0 && (
                      <div className="bg-white/80 backdrop-blur border border-white/60 rounded-3xl shadow-lg p-5">
                        <div className="flex items-start justify-between gap-4 mb-4">
                          <div>
                            <p className="text-teal-900/50 text-[10px] font-black uppercase tracking-widest">
                              {reservasPendientes.length} reserva{reservasPendientes.length !== 1 ? "s" : ""} pendiente{reservasPendientes.length !== 1 ? "s" : ""} de pago
                            </p>
                            <p className="text-teal-900 font-black text-3xl mt-0.5">{totalPendiente.toFixed(2)} €</p>
                            <p className="text-teal-600/50 text-[9px] font-bold uppercase tracking-widest mt-1">
                              Paga cada una con su botón "Pagar" o todas a la vez
                            </p>
                          </div>
                          <div className="w-14 h-14 rounded-2xl bg-amber-100 flex items-center justify-center shrink-0">
                            <span className="text-2xl">💳</span>
                          </div>
                        </div>
                        <button onClick={handlePagarTodo}
                          className="w-full bg-teal-900 hover:bg-teal-800 text-white rounded-2xl py-4 font-black uppercase tracking-widest text-sm flex items-center justify-center gap-3 transition-all shadow-lg shadow-teal-900/20 active:scale-95">
                          <CreditCard size={18}/>
                          Pagar todo — {totalPendiente.toFixed(2)} €
                        </button>
                      </div>
                    )}
                  </>
                )}
              </>
            )}

            {/* ── TAB HISTORIAL ── */}
            {tab === "historial" && (
              <>
                {historial.length === 0 ? (
                  <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                    <History size={52} className="text-teal-900/30"/>
                    <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">
                      Aún no tienes historial
                    </p>
                    <p className="text-teal-900/40 text-xs font-bold text-center">
                      Las reservas completadas o canceladas aparecerán aquí.
                    </p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {historial.map(r => (
                      // En historial no mostramos botones de acción
                      <ReservaCard key={r.idReserva} r={r} showActions={false} />
                    ))}
                  </div>
                )}
              </>
            )}

          </div>
        </main>
      </div>

      {/* ── Modal Repetir Reserva ── */}
      {repeatModalOpen && repeatReserva && (
        <div className="fixed inset-0 z-[200] flex items-center justify-center bg-teal-950/70 backdrop-blur-md p-4">
          <div className="bg-white rounded-[3rem] shadow-2xl w-full max-w-md p-8 relative">
            <button onClick={() => setRepeatModalOpen(false)}
              className="absolute top-6 right-6 text-teal-900/40 hover:text-teal-900 transition-colors">
              <X size={24}/>
            </button>

            {repeatSuccess ? (
              <div className="text-center py-4">
                <CheckCircle2 size={52} className="text-teal-500 mx-auto mb-4"/>
                <h2 className="text-2xl font-black text-teal-900 uppercase tracking-tight mb-2">¡Reserva creada!</h2>
                <p className="text-teal-900/50 text-sm font-bold">Te llevamos a Mis Reservas...</p>
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

                <div className="bg-teal-50 rounded-2xl p-5 mb-6 space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Servicio</span>
                    <span className="text-sm font-black text-teal-900 text-right max-w-[200px] truncate">{repeatReserva.servicioNombre}</span>
                  </div>
                  <div className="flex justify-between items-center">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Tipo</span>
                    <span className="text-xs font-bold text-teal-700">{repeatReserva.tipoReservaNombre}</span>
                  </div>
                  <div className="flex justify-between items-center border-t border-teal-100 pt-2 mt-2">
                    <span className="text-[10px] font-black text-teal-500 uppercase tracking-widest">Precio</span>
                    <span className="text-xl font-black text-teal-900">{Number(repeatReserva.precioTotal).toFixed(2)} €</span>
                  </div>
                </div>

                <div className="bg-amber-50 border border-amber-200 rounded-2xl px-4 py-3 mb-6">
                  <p className="text-amber-700 text-xs font-semibold">
                    ⚠️ La nueva reserva quedará en estado <strong>PENDIENTE</strong>. Podrás pagarla desde "Mis Reservas".
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

      {/* Modal de pago */}
      {payReserva && (
        <PaymentModal
          isOpen={payModalOpen}
          onClose={cerrarModal}
          reservaId={payReserva.idReserva}
          precio={Number(payReserva.precioTotal)}
          descripcion={payReserva.servicioNombre}
          multiple={payAllMode}
          reservaIds={payAllMode ? reservasPendientes.map(r => r.idReserva) : undefined}
        />
      )}
    </>
  );
}