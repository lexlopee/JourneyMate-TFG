import { useState } from "react";
import { Link } from "react-router-dom";
import { Mail, Lock, Eye, EyeOff, User, Phone, Calendar, ArrowRight, Plane } from "lucide-react";

// ── Selector de fecha con tres selects ───────────────────
function DateSelector({ value, onChange }: { value: string; onChange: (v: string) => void }) {
  const parts = value ? value.split("-") : ["", "", ""];
  const [year, setYear]   = useState(parts[0]);
  const [month, setMonth] = useState(parts[1]);
  const [day, setDay]     = useState(parts[2]);

  const update = (y: string, m: string, d: string) => {
    setYear(y); setMonth(m); setDay(d);
    if (y && m && d) onChange(`${y}-${m}-${d}`);
  };

  const currentYear = new Date().getFullYear();
  const years  = Array.from({ length: 100 }, (_, i) => currentYear - 18 - i);
  const months = [
    { v: "01", l: "Enero" }, { v: "02", l: "Febrero" }, { v: "03", l: "Marzo" },
    { v: "04", l: "Abril" }, { v: "05", l: "Mayo" },    { v: "06", l: "Junio" },
    { v: "07", l: "Julio" }, { v: "08", l: "Agosto" },  { v: "09", l: "Septiembre" },
    { v: "10", l: "Octubre"},{ v: "11", l: "Noviembre"},{ v: "12", l: "Diciembre" },
  ];
  const daysInMonth = month && year ? new Date(Number(year), Number(month), 0).getDate() : 31;
  const days = Array.from({ length: daysInMonth }, (_, i) => String(i + 1).padStart(2, "0"));

  const selectClass = `flex-1 bg-teal-50 border-2 border-transparent rounded-xl px-3 py-3
    text-teal-900 font-medium text-sm focus:outline-none focus:border-teal-400 focus:bg-white
    transition-all appearance-none cursor-pointer`;

  return (
    <div className="flex gap-2">
      <select value={day} onChange={e => update(year, month, e.target.value)} className={selectClass}>
        <option value="">Día</option>
        {days.map(d => <option key={d} value={d}>{Number(d)}</option>)}
      </select>
      <select value={month} onChange={e => update(year, e.target.value, day)} className={selectClass}>
        <option value="">Mes</option>
        {months.map(m => <option key={m.v} value={m.v}>{m.l}</option>)}
      </select>
      <select value={year} onChange={e => update(e.target.value, month, day)} className={selectClass}>
        <option value="">Año</option>
        {years.map(y => <option key={y} value={String(y)}>{y}</option>)}
      </select>
    </div>
  );
}

export default function Register() {
  const [nombre,          setNombre]          = useState("");
  const [primerApellido,  setPrimerApellido]  = useState("");
  const [segundoApellido, setSegundoApellido] = useState("");
  const [telefono,        setTelefono]        = useState("");
  const [fechaNacimiento, setFechaNacimiento] = useState("");
  const [email,           setEmail]           = useState("");
  const [password,        setPassword]        = useState("");
  const [showPass,        setShowPass]        = useState(false);
  const [error,           setError]           = useState("");
  const [loading,         setLoading]         = useState(false);

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, primerApellido, segundoApellido, telefono, fechaNacimiento, email, password })
      });

      if (!res.ok) { setError("No se pudo crear la cuenta"); return; }

      const data = await res.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("idUsuario", String(Number(data.idUsuario)));
      if (data.nombre) localStorage.setItem("userName", data.nombre);
      window.location.href = "/";
    } catch {
      setError("Error de conexión con el servidor");
    } finally {
      setLoading(false);
    }
  };

  const inputClass = `w-full pl-10 pr-4 py-3 bg-teal-50 border-2 border-transparent
    rounded-xl text-teal-900 font-medium text-sm placeholder-teal-300
    focus:outline-none focus:border-teal-400 focus:bg-white transition-all`;

  const labelClass = "block text-xs font-black text-teal-900/60 uppercase tracking-widest mb-1.5";

  return (
    <div className="min-h-screen flex font-sans"
      style={{ background: 'linear-gradient(135deg, #0d4f4c 0%, #1cb5b0 50%, #e9fc9e 100%)' }}>

      {/* Panel izquierdo decorativo */}
      <div className="hidden lg:flex flex-1 flex-col items-center justify-center p-12 relative overflow-hidden">
        <div className="absolute inset-0">
          <div className="absolute top-20 left-10 w-64 h-64 bg-white/5 rounded-full blur-3xl" />
          <div className="absolute bottom-20 right-10 w-80 h-80 bg-teal-300/10 rounded-full blur-3xl" />
        </div>
        <div className="relative z-10 text-center">
          <div className="w-20 h-20 bg-white/20 backdrop-blur rounded-[2rem] flex items-center justify-center mx-auto mb-6 shadow-2xl border border-white/30">
            <Plane size={36} className="text-white" />
          </div>
          <h1 className="text-5xl font-black text-white tracking-tighter uppercase mb-3">JourneyMate</h1>
          <p className="text-white/60 font-medium text-lg">Tu compañero de viaje inteligente</p>
          <div className="mt-10 space-y-3 max-w-xs mx-auto text-left">
            {[
              '✈️  Busca vuelos al mejor precio',
              '🏨  Compara hoteles en todo el mundo',
              '🚗  Alquila coches sin complicaciones',
              '🎟️  Reserva actividades únicas',
            ].map(t => (
              <div key={t} className="bg-white/10 backdrop-blur border border-white/15 rounded-2xl px-4 py-3">
                <p className="text-white font-semibold text-sm">{t}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Panel derecho — formulario */}
      <div className="flex-1 flex items-center justify-center p-6 lg:p-12 overflow-y-auto">
        <div className="w-full max-w-md py-8">
          <div className="bg-white/95 backdrop-blur-xl rounded-[2.5rem] shadow-2xl p-8 sm:p-10">

            {/* Header */}
            <div className="text-center mb-8">
              <div className="w-14 h-14 bg-gradient-to-br from-teal-400 to-teal-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg lg:hidden">
                <Plane size={24} className="text-white" />
              </div>
              <h2 className="text-3xl font-black text-teal-900 tracking-tighter">Crea tu cuenta</h2>
              <p className="text-teal-600/70 text-sm font-medium mt-1">Empieza a explorar el mundo</p>
            </div>

            <form onSubmit={handleRegister} className="space-y-4">

              {/* Nombre + Primer apellido */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className={labelClass}>Nombre</label>
                  <div className="relative">
                    <User size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                    <input type="text" placeholder="Juan" className={inputClass}
                      value={nombre} onChange={e => setNombre(e.target.value)} required />
                  </div>
                </div>
                <div>
                  <label className={labelClass}>1er apellido</label>
                  <div className="relative">
                    <User size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                    <input type="text" placeholder="García" className={inputClass}
                      value={primerApellido} onChange={e => setPrimerApellido(e.target.value)} required />
                  </div>
                </div>
              </div>

              {/* Segundo apellido */}
              <div>
                <label className={labelClass}>2º apellido <span className="normal-case text-teal-400/60">(opcional)</span></label>
                <div className="relative">
                  <User size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input type="text" placeholder="López" className={inputClass}
                    value={segundoApellido} onChange={e => setSegundoApellido(e.target.value)} />
                </div>
              </div>

              {/* Teléfono */}
              <div>
                <label className={labelClass}>Teléfono <span className="normal-case text-teal-400/60">(opcional)</span></label>
                <div className="relative">
                  <Phone size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input type="tel" placeholder="+34 600 000 000" className={inputClass}
                    value={telefono} onChange={e => setTelefono(e.target.value)} />
                </div>
              </div>

              {/* Fecha de nacimiento — 3 selects */}
              <div>
                <label className={`${labelClass} flex items-center gap-1.5`}>
                  <Calendar size={11} className="text-teal-400" /> Fecha de nacimiento
                </label>
                <DateSelector value={fechaNacimiento} onChange={setFechaNacimiento} />
              </div>

              {/* Email */}
              <div>
                <label className={labelClass}>Correo electrónico</label>
                <div className="relative">
                  <Mail size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input type="email" placeholder="tu@email.com" className={inputClass}
                    value={email} onChange={e => setEmail(e.target.value)} required />
                </div>
              </div>

              {/* Password */}
              <div>
                <label className={labelClass}>Contraseña</label>
                <div className="relative">
                  <Lock size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input type={showPass ? "text" : "password"} placeholder="Mínimo 8 caracteres"
                    className={`${inputClass} pr-12`}
                    value={password} onChange={e => setPassword(e.target.value)} required />
                  <button type="button" onClick={() => setShowPass(p => !p)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-teal-400 hover:text-teal-600 transition-colors">
                    {showPass ? <EyeOff size={15}/> : <Eye size={15}/>}
                  </button>
                </div>
              </div>

              {/* Error */}
              {error && (
                <div className="bg-red-50 border border-red-100 text-red-600 text-xs font-semibold px-4 py-3 rounded-xl text-center">
                  {error}
                </div>
              )}

              {/* Submit */}
              <button type="submit" disabled={loading}
                className="w-full bg-gradient-to-r from-teal-500 to-teal-700 text-white
                  py-3.5 rounded-xl font-black uppercase tracking-widest text-sm
                  hover:from-teal-400 hover:to-teal-600 transition-all shadow-lg
                  hover:shadow-teal-500/30 hover:scale-[1.02] active:scale-[0.98]
                  disabled:opacity-50 flex items-center justify-center gap-2 mt-2">
                {loading
                  ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"/>
                  : <><span>Crear cuenta</span><ArrowRight size={16}/></>
                }
              </button>
            </form>

            <p className="text-center text-sm text-teal-900/50 mt-6">
              ¿Ya tienes cuenta?{" "}
              <Link to="/login" className="text-teal-600 font-black hover:text-teal-500 transition-colors">
                Iniciar sesión
              </Link>
            </p>

            <Link to="/"
              className="flex items-center justify-center gap-1 text-xs text-teal-400 hover:text-teal-600 transition-colors mt-4 font-semibold">
              ← Volver al inicio
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}