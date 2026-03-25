import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) {
        setError("Credenciales incorrectas");
        return;
      }

      const data = await res.json();

      localStorage.setItem("token", data.token);
      // ✅ CORREGIDO: guardamos el número directamente como string limpio
      // Number() convierte cualquier valor a número real, eliminando basura como "1:1"
      localStorage.setItem("idUsuario", String(Number(data.idUsuario)));
      // ✅ Guardamos también el nombre directamente desde la respuesta del login
      // así el navbar no necesita hacer un fetch extra para obtenerlo
      if (data.nombre) {
        localStorage.setItem("userName", data.nombre);
      }

      window.location.href = "/";

    } catch (err) {
      setError("Error de conexión con el servidor");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-teal-50">
      <h1 className="text-3xl font-bold text-teal-900 mb-6">Iniciar sesión</h1>

      <form onSubmit={handleLogin} className="bg-white p-6 rounded-xl shadow-md w-80">
        <input
          type="email"
          placeholder="Correo electrónico"
          className="w-full mb-3 p-2 border rounded"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Contraseña"
          className="w-full mb-3 p-2 border rounded"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {error && <p className="text-red-600 text-sm mb-3">{error}</p>}
        <button className="w-full bg-teal-900 text-white py-2 rounded-lg font-bold">
          Acceder
        </button>
        <p className="text-center text-sm mt-4">
          ¿No tienes cuenta?{" "}
          <Link to="/register" className="text-teal-700 font-bold">Crear cuenta</Link>
        </p>
      </form>
    </div>
  );
}