import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import App from './App';
import Login from './pages/Login';
import Register from './pages/Register';

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* ⭐ PÁGINA TEMPORAL DE MIS RESERVAS */}
        <Route
          path="/mis-reservas"
          element={
            <div className="pt-40 text-center text-teal-900 font-black text-3xl tracking-tight">
              Próximamente podrás ver aquí tus reservas ✈️🏨
            </div>
          }
        />
      </Routes>
    </Router>
  );
};

export default AppRouter;
