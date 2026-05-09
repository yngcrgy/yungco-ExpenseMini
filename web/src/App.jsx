import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './features/auth/LoginPage';
import RegisterPage from './features/auth/RegisterPage';
import Dashboard from './features/dashboard/Dashboard';
import AddExpense from './features/expenses/AddExpense';
import History from './features/expenses/History';
import Profile from './features/profile/Profile';
import Layout from './components/Layout';
import { Toaster } from 'react-hot-toast';

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? <Layout>{children}</Layout> : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected Routes */}
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/add-expense" element={<PrivateRoute><AddExpense /></PrivateRoute>} />
        <Route path="/history" element={<PrivateRoute><History /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
