import { useState } from "react";
import { Link } from "react-router-dom";
import { Mail, Lock, Eye, EyeOff, ArrowRight, Plane } from "lucide-react";

export default function Login() {
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState("");
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading]   = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) { setError("Credenciales incorrectas"); return; }

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

  return (
    <div className="min-h-screen flex font-sans"
      style={{ background: 'linear-gradient(135deg, #0d4f4c 0%, #1cb5b0 50%, #e9fc9e 100%)' }}>

      {/* Panel izquierdo — decorativo */}
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
          <div className="mt-10 grid grid-cols-2 gap-4 max-w-xs mx-auto">
            {['2M+ Viajeros', '150+ Destinos', '24/7 Soporte', 'Pago seguro'].map(s => (
              <div key={s} className="bg-white/10 backdrop-blur border border-white/15 rounded-2xl px-4 py-3 text-center">
                <p className="text-white font-black text-xs uppercase tracking-wider">{s}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Panel derecho — formulario */}
      <div className="flex-1 flex items-center justify-center p-6 lg:p-12">
        <div className="w-full max-w-md">
          <div className="bg-white/95 backdrop-blur-xl rounded-[2.5rem] shadow-2xl p-8 sm:p-10">

            {/* Header */}
            <div className="text-center mb-8">
              <div className="w-14 h-14 bg-gradient-to-br from-teal-400 to-teal-600 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg lg:hidden">
                <Plane size={24} className="text-white" />
              </div>
              <h2 className="text-3xl font-black text-teal-900 tracking-tighter">Bienvenido</h2>
              <p className="text-teal-600/70 text-sm font-medium mt-1">Inicia sesión en tu cuenta</p>
            </div>

            <form onSubmit={handleLogin} className="space-y-4">

              {/* Email */}
              <div className="relative">
                <label className="block text-xs font-black text-teal-900/60 uppercase tracking-widest mb-1.5">
                  Correo electrónico
                </label>
                <div className="relative">
                  <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input
                    type="email"
                    placeholder="tu@email.com"
                    className="w-full pl-10 pr-4 py-3 bg-teal-50 border-2 border-transparent
                      rounded-xl text-teal-900 font-medium text-sm placeholder-teal-300
                      focus:outline-none focus:border-teal-400 focus:bg-white transition-all"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>

              {/* Password */}
              <div className="relative">
                <label className="block text-xs font-black text-teal-900/60 uppercase tracking-widest mb-1.5">
                  Contraseña
                </label>
                <div className="relative">
                  <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-teal-400" />
                  <input
                    type={showPass ? "text" : "password"}
                    placeholder="••••••••"
                    className="w-full pl-10 pr-12 py-3 bg-teal-50 border-2 border-transparent
                      rounded-xl text-teal-900 font-medium text-sm placeholder-teal-300
                      focus:outline-none focus:border-teal-400 focus:bg-white transition-all"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    required
                  />
                  <button type="button" onClick={() => setShowPass(p => !p)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-teal-400 hover:text-teal-600 transition-colors">
                    {showPass ? <EyeOff size={16}/> : <Eye size={16}/>}
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
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-teal-500 to-teal-700 text-white
                  py-3.5 rounded-xl font-black uppercase tracking-widest text-sm
                  hover:from-teal-400 hover:to-teal-600 transition-all shadow-lg
                  hover:shadow-teal-500/30 hover:scale-[1.02] active:scale-[0.98]
                  disabled:opacity-50 flex items-center justify-center gap-2 mt-2">
                {loading ? (
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"/>
                ) : (
                  <><span>Acceder</span><ArrowRight size={16}/></>
                )}
              </button>
            </form>

            <p className="text-center text-sm text-teal-900/50 mt-6">
              ¿No tienes cuenta?{" "}
              <Link to="/register" className="text-teal-600 font-black hover:text-teal-500 transition-colors">
                Crear cuenta gratis
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