import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import App from './App';

// Crea un componente temporal para el Login para que no de error
const LoginPlaceholder = () => (
  <div className="min-h-screen flex items-center justify-center bg-teal-500 text-white">
    <h1>Página de Login (Próximamente)</h1>
  </div>
);

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/login" element={<LoginPlaceholder />} />
      </Routes>
    </Router>
  );
};

export default AppRouter;