import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { Dashboard, Alert } from '../types';
import { useAuth } from '../context/AuthContext';
import { Building2, Cog, Package, Bell, Shield, TrendingDown } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function DashboardPage() {
  const { user } = useAuth();
  const role = user?.role || 'VENDEUR';
  const [data, setData] = useState<Dashboard | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<Dashboard>('/dashboard').then((res) => {
      setData(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  const welcomeMessages: Record<string, string> = {
    ADMIN: 'Vue complète — sécurité et gestion',
    MAGASINIER: 'Gestion des stocks et inventaire',
    VENDEUR: 'Suivi des clients acheteurs',
    SECRETAIRE: 'Administration clients et machines',
  };

  if (loading) return <Layout title="Tableau de bord"><div className="loading"><div className="spinner" /></div></Layout>;
  if (!data) return <Layout title="Tableau de bord"><div className="empty-state">Erreur de chargement</div></Layout>;

  const allStats = [
    { label: 'Clients (Acheteurs)', value: data.totalClients, icon: Building2, color: 'red', roles: ['ADMIN', 'VENDEUR', 'SECRETAIRE'] },
    { label: 'Machines', value: data.totalMachines, icon: Cog, color: 'blue', roles: ['ADMIN', 'SECRETAIRE', 'VENDEUR'] },
    { label: 'Produits', value: data.totalProducts, icon: Package, color: 'green', roles: ['ADMIN', 'MAGASINIER', 'SECRETAIRE', 'VENDEUR'] },
    { label: 'Alertes actives', value: data.activeAlerts, icon: Bell, color: 'orange', roles: ['ADMIN', 'MAGASINIER', 'SECRETAIRE'] },
    { label: 'Stock faible', value: data.lowStockProducts, icon: TrendingDown, color: 'red', roles: ['ADMIN', 'MAGASINIER', 'SECRETAIRE'] },
    { label: 'Échecs connexion', value: data.failedLogins, icon: Shield, color: 'orange', roles: ['ADMIN'] },
  ];

  const stats = allStats.filter((s) => s.roles.includes(role));

  const severityClass = (s: string) => {
    const map: Record<string, string> = { CRITICAL: 'critical', HIGH: 'high', MEDIUM: 'medium', LOW: 'low' };
    return map[s] || 'low';
  };

  return (
    <Layout
      title="Tableau de bord"
      subtitle={`Bienvenue ${user?.firstName} — ${welcomeMessages[role]}`}
    >
      <div className="stats-grid">
        {stats.map((stat) => (
          <div key={stat.label} className="stat-card">
            <div className={`stat-icon ${stat.color}`}>
              <stat.icon size={24} />
            </div>
            <div className="stat-info">
              <h4>{stat.label}</h4>
              <div className="value">{stat.value}</div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid-2">
        {(role === 'ADMIN' || role === 'MAGASINIER' || role === 'SECRETAIRE') && (
          <div className="card">
            <div className="card-header">
              <h3>Alertes récentes</h3>
              <Link to="/alerts" className="btn btn-sm btn-secondary">Voir tout</Link>
            </div>
            <div className="card-body">
              {data.recentAlerts.length === 0 ? (
                <div className="empty-state empty-state--compact">Aucune alerte active</div>
              ) : (
                data.recentAlerts.map((alert: Alert) => (
                  <div key={alert.id} className="alert-item">
                    <div className={`alert-dot ${severityClass(alert.severity)}`} />
                    <div className="alert-content">
                      <h4>{alert.title}</h4>
                      <p>{alert.message}</p>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        <div className="card">
          <div className="card-header">
            <h3>{role === 'ADMIN' ? 'Sécurité' : 'Stock'}</h3>
            {role === 'ADMIN' && <Link to="/risk-center" className="btn btn-sm btn-primary">Risk Center</Link>}
            {(role === 'MAGASINIER' || role === 'SECRETAIRE') && (
              <Link to="/stock-risk" className="btn btn-sm btn-primary">Risque Stock</Link>
            )}
          </div>
          <div className="card-body">
            <div className="metric-list">
              {role === 'ADMIN' && (
                <div className="metric-row">
                  <span>Échecs de connexion (24h)</span>
                  <span className={`badge ${data.failedLogins > 0 ? 'badge-danger' : 'badge-success'}`}>{data.failedLogins}</span>
                </div>
              )}
              <div className="metric-row">
                <span>Produits en stock critique</span>
                <span className={`badge ${data.lowStockProducts > 0 ? 'badge-warning' : 'badge-success'}`}>{data.lowStockProducts}</span>
              </div>
              <div className="metric-row">
                <span>Écarts d'inventaire</span>
                <span className={`badge ${data.inventoryDiscrepancies > 0 ? 'badge-danger' : 'badge-success'}`}>{data.inventoryDiscrepancies}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}
