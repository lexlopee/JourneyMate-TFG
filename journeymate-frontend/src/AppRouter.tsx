import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import App from './App';
import Login from './pages/Login';
import Register from './pages/Register';
import MisReservas from './pages/Misreservas';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentCancelled from './pages/PaymentCancelled';

const AppRouter = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/pago-exitoso" element={<PaymentSuccess />} />
        <Route path="/pago-cancelado" element={<PaymentCancelled />} />
        {/* ⭐ PÁGINA TEMPORAL DE MIS RESERVAS */}
        <Route path="/mis-reservas" element={<MisReservas />} />
      </Routes>
    </Router>
  );
};

export default AppRouter;
