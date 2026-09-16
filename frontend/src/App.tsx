import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import RoleRoute from './components/RoleRoute';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import RiskCenterPage from './pages/RiskCenterPage';
import StockRiskPage from './pages/StockRiskPage';
import ClientsPage from './pages/ClientsPage';
import MachinesPage from './pages/MachinesPage';
import ProductsPage from './pages/ProductsPage';
import StockPage from './pages/StockPage';
import InventoryPage from './pages/InventoryPage';
import DiscountsPage from './pages/DiscountsPage';
import AlertsPage from './pages/AlertsPage';
import UsersPage from './pages/UsersPage';
import AuditPage from './pages/AuditPage';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return <div className="loading loading--fullscreen"><div className="spinner" /></div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function AppLayout() {
  return (
    <div className="app-layout">
      <Sidebar />
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/risk-center" element={<RoleRoute path="/risk-center"><RiskCenterPage /></RoleRoute>} />
        <Route path="/stock-risk" element={<RoleRoute path="/stock-risk"><StockRiskPage /></RoleRoute>} />
        <Route path="/alerts" element={<RoleRoute path="/alerts"><AlertsPage /></RoleRoute>} />
        <Route path="/clients" element={<RoleRoute path="/clients"><ClientsPage /></RoleRoute>} />
        <Route path="/machines" element={<RoleRoute path="/machines"><MachinesPage /></RoleRoute>} />
        <Route path="/products" element={<RoleRoute path="/products"><ProductsPage /></RoleRoute>} />
        <Route path="/stock" element={<RoleRoute path="/stock"><StockPage /></RoleRoute>} />
        <Route path="/inventory" element={<RoleRoute path="/inventory"><InventoryPage /></RoleRoute>} />
        <Route path="/discounts" element={<RoleRoute path="/discounts"><DiscountsPage /></RoleRoute>} />
        <Route path="/users" element={<RoleRoute path="/users"><UsersPage /></RoleRoute>} />
        <Route path="/audit" element={<RoleRoute path="/audit"><AuditPage /></RoleRoute>} />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/*" element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          } />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
