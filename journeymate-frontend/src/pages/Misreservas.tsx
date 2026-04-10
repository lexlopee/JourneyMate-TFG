import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import {
  Hotel, Plane, Car, Ticket, Ship, Train,
  CalendarDays, Tag, ArrowLeft,
  PackageOpen, AlertCircle, Clock, Trash2
} from "lucide-react";

// ── Tipo plano del DTO ────────────────────────────────────
interface Reserva {
  idReserva: number;
  servicioNombre: string;
  precioTotal: number;
  estadoNombre: string;
  tipoReservaNombre: string;
  fechaReserva: string;
}

// ── Helpers ───────────────────────────────────────────────
const TIPO_ICON: Record<string, React.ReactNode> = {
  hotel:     <Hotel  size={20} />,
  vuelo:     <Plane  size={20} />,
  coche:     <Car    size={20} />,
  actividad: <Ticket size={20} />,
  crucero:   <Ship   size={20} />,
  tren:      <Train  size={20} />,
};

function getTipoIcon(tipo: string) {
  const key = tipo?.toLowerCase() ?? "";
  for (const k of Object.keys(TIPO_ICON)) {
    if (key.includes(k)) return TIPO_ICON[k];
  }
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
  for (const k of Object.keys(ESTADO_STYLE)) {
    if (key.includes(k)) return ESTADO_STYLE[k];
  }
  return "bg-gray-100 text-gray-600 border-gray-200";
}

function formatFecha(fecha: string) {
  if (!fecha) return "—";
  const [y, m, d] = fecha.split("-");
  return `${d}/${m}/${y}`;
}

// ── Componente principal ──────────────────────────────────
export default function MisReservas() {
  const navigate = useNavigate();

  const [reservas,        setReservas]        = useState<Reserva[]>([]);
  const [loading,         setLoading]         = useState(true);
  const [error,           setError]           = useState("");
  const [userName,        setUserName]        = useState("");
  const [deletingId,      setDeletingId]      = useState<number | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);

  // ── Precio total calculado ──
  const precioTotal = reservas.reduce((sum, r) => sum + (Number(r.precioTotal) || 0), 0);

  useEffect(() => {
    const token     = localStorage.getItem("token");
    const idUsuario = localStorage.getItem("idUsuario");
    const nombre    = localStorage.getItem("userName");

    if (nombre) setUserName(nombre);

    if (!token || !idUsuario) {
      navigate("/login");
      return;
    }

    fetch(`http://localhost:8080/api/v1/reservas/usuario/${idUsuario}`, {
      headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" }
    })
      .then(res => { if (!res.ok) throw new Error(); return res.json(); })
      .then((data: Reserva[]) => setReservas(data))
      .catch(() => setError("No se pudieron cargar tus reservas. Inténtalo de nuevo."))
      .finally(() => setLoading(false));
  }, [navigate]);

  // ── Eliminar reserva ──
  const handleDelete = async (idReserva: number) => {
    const token = localStorage.getItem("token");
    setDeletingId(idReserva);
    setConfirmDeleteId(null);

    try {
      const res = await fetch(`http://localhost:8080/api/v1/reservas/${idReserva}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
      });

      if (!res.ok) throw new Error();
      // Eliminar de la lista local sin recargar
      setReservas(prev => prev.filter(r => r.idReserva !== idReserva));
    } catch {
      alert("No se pudo eliminar la reserva. Inténtalo de nuevo.");
    } finally {
      setDeletingId(null);
    }
  };

  // ── Skeletons ──
  if (loading) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-6 px-4"
        style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>
        <div className="w-full max-w-3xl space-y-4">
          {[1, 2, 3].map(i => (
            <div key={i} className="bg-white/60 rounded-3xl h-28 animate-pulse" />
          ))}
        </div>
      </div>
    );
  }

  // ── Error ──
  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 px-4"
        style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>
        <div className="bg-white/80 backdrop-blur rounded-3xl p-8 flex flex-col items-center gap-3 shadow-xl max-w-sm w-full">
          <AlertCircle size={40} className="text-red-500" />
          <p className="text-red-600 font-bold text-center">{error}</p>
          <button onClick={() => window.location.reload()}
            className="mt-2 bg-teal-900 text-white px-6 py-2 rounded-xl font-bold hover:bg-teal-800 transition-all">
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  // ── Vista principal ──
  return (
    <div className="min-h-screen flex flex-col font-sans"
      style={{ background: "linear-gradient(135deg,#1cb5b0 0%,#e9fc9e 50%,#1cb5b0 100%)" }}>

      {/* Header */}
      <header className="sticky top-0 z-20 px-4 pt-4 pb-2">
        <div className="max-w-3xl mx-auto bg-white/70 backdrop-blur-md border border-white/50
          rounded-2xl px-4 py-3 flex items-center justify-between shadow-lg">
          <Link to="/"
            className="flex items-center gap-2 text-teal-900 font-black text-sm hover:text-teal-600 transition-colors">
            <ArrowLeft size={18} />
            Volver
          </Link>
          <div className="text-center">
            <h1 className="text-teal-900 font-black text-lg uppercase tracking-tighter leading-none">
              Mis Reservas
            </h1>
            {userName && (
              <p className="text-teal-600 text-[10px] font-bold tracking-widest uppercase">{userName}</p>
            )}
          </div>
          <span className="bg-teal-900 text-white text-xs font-black px-3 py-1 rounded-full">
            {reservas.length}
          </span>
        </div>
      </header>

      {/* Contenido */}
      <main className="flex-grow px-4 py-6">
        <div className="max-w-3xl mx-auto">

          {reservas.length === 0 ? (
            <div className="flex flex-col items-center justify-center gap-4 mt-16">
              <div className="bg-white/60 backdrop-blur rounded-3xl p-10 flex flex-col items-center gap-4 shadow-xl">
                <PackageOpen size={52} className="text-teal-900/30" />
                <p className="text-teal-900/60 font-black uppercase tracking-widest text-sm text-center">
                  Aún no tienes reservas
                </p>
                <Link to="/"
                  className="bg-teal-900 text-white px-6 py-2 rounded-xl font-bold text-sm hover:bg-teal-800 transition-all">
                  Explorar destinos
                </Link>
              </div>
            </div>
          ) : (
            <>
              <div className="space-y-4">
                {reservas.map((r, i) => (
                  <div key={r.idReserva}
                    className="bg-white/75 backdrop-blur border border-white/60 rounded-3xl
                      shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all duration-200"
                    style={{ animationDelay: `${i * 60}ms` }}>
                    <div className="p-5 flex items-start gap-4">

                      {/* Icono tipo */}
                      <div className="shrink-0 w-12 h-12 rounded-2xl bg-teal-900/10
                        flex items-center justify-center text-teal-900">
                        {getTipoIcon(r.tipoReservaNombre)}
                      </div>

                      {/* Info */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-2 flex-wrap">
                          <h2 className="text-teal-900 font-black text-base leading-tight truncate max-w-[180px] sm:max-w-none">
                            {r.servicioNombre ?? "Servicio"}
                          </h2>
                          <span className={`shrink-0 text-[10px] font-black uppercase tracking-wider
                            px-2.5 py-1 rounded-full border ${getEstadoStyle(r.estadoNombre)}`}>
                            {r.estadoNombre ?? "—"}
                          </span>
                        </div>

                        <div className="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-xs text-teal-700/70 font-semibold">
                          <span className="flex items-center gap-1">
                            <Tag size={11} />{r.tipoReservaNombre ?? "—"}
                          </span>
                          <span className="flex items-center gap-1">
                            <CalendarDays size={11} />{formatFecha(r.fechaReserva)}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock size={11} />Reserva #{r.idReserva}
                          </span>
                        </div>
                      </div>

                      {/* Precio + botón eliminar */}
                      <div className="shrink-0 flex flex-col items-end gap-2">
                        <div className="text-right">
                          <p className="text-teal-900 font-black text-lg leading-none">
                            {r.precioTotal != null ? `${Number(r.precioTotal).toFixed(2)} €` : "—"}
                          </p>
                          <p className="text-teal-600/60 text-[10px] font-bold mt-0.5 uppercase tracking-wider">
                            precio total
                          </p>
                        </div>

                        {/* Botón eliminar con confirmación inline */}
                        {confirmDeleteId === r.idReserva ? (
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => setConfirmDeleteId(null)}
                              className="text-[10px] font-bold px-2 py-1 rounded-lg bg-gray-100 text-gray-600 hover:bg-gray-200 transition-all">
                              Cancelar
                            </button>
                            <button
                              onClick={() => handleDelete(r.idReserva)}
                              disabled={deletingId === r.idReserva}
                              className="text-[10px] font-bold px-2 py-1 rounded-lg bg-red-500 text-white hover:bg-red-600 transition-all disabled:opacity-50">
                              {deletingId === r.idReserva ? "..." : "Confirmar"}
                            </button>
                          </div>
                        ) : (
                          <button
                            onClick={() => setConfirmDeleteId(r.idReserva)}
                            className="flex items-center gap-1 text-[10px] font-bold px-2.5 py-1.5
                              rounded-xl bg-red-50 text-red-500 hover:bg-red-100 border border-red-100
                              transition-all">
                            <Trash2 size={12} />
                            Eliminar
                          </button>
                        )}
                      </div>

                    </div>
                  </div>
                ))}
              </div>

              {/* ⭐ TOTAL al final */}
              <div className="mt-6 bg-white/80 backdrop-blur border border-white/60 rounded-3xl shadow-lg p-5">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-teal-900/50 text-[10px] font-black uppercase tracking-widest">
                      Total de {reservas.length} reserva{reservas.length !== 1 ? "s" : ""}
                    </p>
                    <p className="text-teal-900 font-black text-2xl mt-0.5">
                      {precioTotal.toFixed(2)} €
                    </p>
                  </div>
                  <div className="w-14 h-14 rounded-2xl bg-teal-900/10 flex items-center justify-center">
                    <span className="text-2xl">🧾</span>
                  </div>
                </div>
              </div>
            </>
          )}

        </div>
      </main>
    </div>
  );
}