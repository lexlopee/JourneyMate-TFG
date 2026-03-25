import { useState } from "react";
import { Link } from "react-router-dom";

export default function Register() {
  const [nombre, setNombre] = useState("");
  const [primerApellido, setPrimerApellido] = useState("");
  const [segundoApellido, setSegundoApellido] = useState("");
  const [telefono, setTelefono] = useState("");
  const [fechaNacimiento, setFechaNacimiento] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      const res = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nombre, primerApellido, segundoApellido,
          telefono, fechaNacimiento, email, password
        })
      });

      if (!res.ok) {
        setError("No se pudo crear la cuenta");
        return;
      }

      const data = await res.json();

      localStorage.setItem("token", data.token);
      // ✅ CORREGIDO: igual que en login, número limpio
      localStorage.setItem("idUsuario", String(Number(data.idUsuario)));
      // ✅ Guardamos el nombre para que el navbar lo lea directamente
      if (data.nombre) {
        localStorage.setItem("userName", data.nombre);
      }

      window.location.href = "/";

    } catch {
      setError("Error de conexión con el servidor");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-teal-50">
      <h1 className="text-3xl font-bold text-teal-900 mb-6">Crear cuenta</h1>

      <form onSubmit={handleRegister} className="bg-white p-6 rounded-xl shadow-md w-80">
        <input type="text" placeholder="Nombre" className="w-full mb-3 p-2 border rounded"
          value={nombre} onChange={(e) => setNombre(e.target.value)} />
        <input type="text" placeholder="Primer apellido" className="w-full mb-3 p-2 border rounded"
          value={primerApellido} onChange={(e) => setPrimerApellido(e.target.value)} />
        <input type="text" placeholder="Segundo apellido" className="w-full mb-3 p-2 border rounded"
          value={segundoApellido} onChange={(e) => setSegundoApellido(e.target.value)} />
        <input type="text" placeholder="Teléfono" className="w-full mb-3 p-2 border rounded"
          value={telefono} onChange={(e) => setTelefono(e.target.value)} />
        <input type="date" className="w-full mb-3 p-2 border rounded"
          value={fechaNacimiento} onChange={(e) => setFechaNacimiento(e.target.value)} />
        <input type="email" placeholder="Correo electrónico" className="w-full mb-3 p-2 border rounded"
          value={email} onChange={(e) => setEmail(e.target.value)} />
        <input type="password" placeholder="Contraseña" className="w-full mb-3 p-2 border rounded"
          value={password} onChange={(e) => setPassword(e.target.value)} />

        {error && <p className="text-red-600 text-sm mb-3">{error}</p>}

        <button className="w-full bg-teal-900 text-white py-2 rounded-lg font-bold">
          Registrarse
        </button>
        <p className="text-center text-sm mt-4">
          ¿Ya tienes cuenta?{" "}
          <Link to="/login" className="text-teal-700 font-bold">Iniciar sesión</Link>
        </p>
      </form>
    </div>
  );
}