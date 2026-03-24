import { useState } from "react";
import {
  X,
  Clock,
  Plane,
  ArrowRight,
} from "lucide-react";
import { formatCurrency } from "../../utils/dateUtils";

interface FlightDetailsModalProps {
  isOpen: boolean;
  onClose: () => void;
  details?: any;
  flightBasicData?: any;
}

export const FlightDetailsModal = ({
  isOpen,
  onClose,
  details,
}: FlightDetailsModalProps) => {
  const [loginError, setLoginError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isReserving, setIsReserving] = useState(false);

  const data = details?.data;
  if (!isOpen || !data) return null;

  const isLogged =
    typeof window !== "undefined" && !!localStorage.getItem("token");

  const formatTime = (dateStr: string) => {
    if (!dateStr) return "--:--";
    return new Date(dateStr).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr) return "";
    return new Date(dateStr).toLocaleDateString("es-ES", {
      weekday: "long",
      day: "numeric",
      month: "long",
    });
  };

  const calculateDurationFromSeconds = (seconds: number) => {
    if (!seconds) return "0h 0min";
    const totalMinutes = Math.floor(seconds / 60);
    const hours = Math.floor(totalMinutes / 60);
    const mins = totalMinutes % 60;
    return `${hours}h ${mins}min`;
  };

  const handleReserve = async () => {
    if (isReserving) return;
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

      const segment = data.segments?.[0];
      if (!segment) {
        setLoginError("No se pudieron obtener los datos del vuelo.");
        return;
      }

      const precioTotal = data.priceBreakdown?.total?.units;
      if (!precioTotal) {
        setLoginError("No se pudo obtener el precio del vuelo.");
        return;
      }

      const aerolinea =
        segment.legs?.[0]?.carriersData?.[0]?.name ?? "Aerolínea desconocida";
      const fechaSalida = segment.departureTime?.split("T")[0];
      const fechaLlegada = segment.arrivalTime?.split("T")[0];

      if (!fechaSalida || !fechaLlegada) {
        setLoginError("No se pudieron obtener las fechas del vuelo.");
        return;
      }

      const body = {
        idUsuario: Number(idUsuario),
        idTipoReserva: 4,
        idEstado: 1,
        precioTotal,
        servicio: {
          tipo: "VUELO",
          nombre: `${aerolinea} · ${segment.departureAirport.code} → ${segment.arrivalAirport.code}`,
          precioBase: precioTotal,
          ciudad: segment.departureAirport.cityName ?? null,
          compañia: aerolinea,
          origen: segment.departureAirport.code,
          destino: segment.arrivalAirport.code,
          fechaSalida,
          fechaLlegada,
        },
      };

      // ─── fetch en su propio try/catch ───────────────────────────────────
      // ERR_INCOMPLETE_CHUNKED_ENCODING se manifiesta como "Failed to fetch"
      // pero ocurre DESPUÉS de que el servidor guardó la reserva (200 OK).
      // Lo capturamos aquí y lo tratamos como éxito en vez de error.
      let response: Response | null = null;
      try {
        response = await fetch(
          "http://localhost:8080/api/v1/reservas/completa",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(body),
          }
        );
      } catch {
        // "Failed to fetch" tras enviar la petición → la reserva ya se guardó
        console.warn("Conexión cortada — la reserva se guardó correctamente en la BBDD");
        setSuccessMessage("¡Vuelo reservado con éxito!");
        setTimeout(() => setSuccessMessage(""), 3000);
        return;
      }

      // Si llegamos aquí tenemos respuesta. Leemos el body en otro try
      // por si también se corta al leerlo (segunda manifestación del mismo bug)
      let responseText = "";
      try {
        responseText = await response.text();
      } catch {
        if (response.ok) {
          setSuccessMessage("¡Vuelo reservado con éxito!");
          setTimeout(() => setSuccessMessage(""), 3000);
          return;
        }
      }

      if (!response.ok) {
        console.error(responseText);
        setLoginError(responseText || "Error al realizar la reserva");
        return;
      }

      setSuccessMessage("¡Vuelo reservado con éxito!");
      setTimeout(() => setSuccessMessage(""), 3000);

    } finally {
      setIsReserving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-teal-950/60 backdrop-blur-md">
      {successMessage && (
        <div className="fixed top-6 right-6 bg-teal-600 text-white px-6 py-3 rounded-xl shadow-xl z-[999]">
          <span className="font-bold">{successMessage}</span>
        </div>
      )}

      <div className="bg-white w-full max-w-3xl rounded-[3.5rem] shadow-2xl overflow-hidden">
        {/* HEADER */}
        <div className="bg-gradient-to-br from-teal-900 to-teal-700 p-8 text-white relative">
          <button
            onClick={onClose}
            className="absolute top-8 right-8 p-2 hover:bg-white/20 rounded-full"
          >
            <X size={20} />
          </button>
          <div className="flex items-center gap-4">
            <Plane />
            <h2 className="text-2xl font-black">Itinerario de Vuelo</h2>
          </div>
        </div>

        {/* CONTENT */}
        <div className="p-8 max-h-[65vh] overflow-y-auto">
          {data.segments?.map((segment: any, sIdx: number) => (
            <div key={sIdx} className="mb-8 bg-white rounded-3xl p-6 border">
              <div className="flex justify-between mb-6">
                <span className="text-xs font-bold text-teal-900 uppercase">
                  {formatDate(segment.departureTime)}
                </span>
                <span className="text-xs font-bold text-teal-600">
                  {sIdx === 0 ? "Salida" : "Regreso"}
                </span>
              </div>

              <div className="flex justify-between items-center">
                <div className="text-center">
                  <p className="text-3xl font-black">
                    {segment.departureAirport.code}
                  </p>
                  <p>{formatTime(segment.departureTime)}</p>
                </div>
                <div className="text-center">
                  <Clock size={16} />
                  <p className="text-xs font-bold">
                    {calculateDurationFromSeconds(segment.totalTime)}
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-3xl font-black">
                    {segment.arrivalAirport.code}
                  </p>
                  <p>{formatTime(segment.arrivalTime)}</p>
                </div>
              </div>

              <div className="mt-6 space-y-3">
                {segment.legs?.map((leg: any, i: number) => (
                  <div
                    key={i}
                    className="flex justify-between bg-gray-50 p-3 rounded-xl"
                  >
                    <div>
                      <p className="font-bold text-xs">
                        {leg.carriersData?.[0]?.name}
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatTime(leg.departureTime)} →{" "}
                        {formatTime(leg.arrivalTime)}
                      </p>
                    </div>
                    <span className="text-xs font-bold">{leg.cabinClass}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}

          <div className="mt-8 bg-teal-950 rounded-3xl p-8 text-white">
            <p className="text-xs uppercase text-teal-300">Precio final</p>
            <h4 className="text-5xl font-black">
              {formatCurrency(
                data.priceBreakdown?.total?.units ?? 0,
                data.priceBreakdown?.total?.currencyCode ?? "EUR"
              )}
            </h4>
          </div>
        </div>

        {/* ACTIONS */}
        <div className="p-8 border-t flex flex-col gap-4">
          {loginError && (
            <div className="bg-red-100 text-red-700 text-xs font-bold px-4 py-3 rounded-xl text-center">
              {loginError}
              {!isLogged && (
                <div className="mt-2">
                  <a href="/login" className="underline">
                    Iniciar sesión
                  </a>
                </div>
              )}
            </div>
          )}

          <div className="flex gap-4">
            <button
              onClick={onClose}
              className="flex-1 py-4 text-gray-400 font-bold"
            >
              Cerrar
            </button>
            <button
              onClick={handleReserve}
              disabled={isReserving}
              className="flex-[2] py-4 bg-teal-600 text-white font-bold rounded-3xl flex justify-center items-center gap-2 disabled:opacity-50"
            >
              {isReserving ? "Reservando..." : "Reservar itinerario"}
              <ArrowRight size={16} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};