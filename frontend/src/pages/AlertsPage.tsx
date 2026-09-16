import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { Alert } from '../types';
import { Check, X } from 'lucide-react';

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.get<Alert[]>('/alerts').then((res) => { setAlerts(res.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const handleResolve = async (id: number) => {
    await api.patch(`/alerts/${id}/resolve`);
    load();
  };

  const severityClass = (s: string) => {
    const map: Record<string, string> = { CRITICAL: 'badge-danger', HIGH: 'badge-warning', MEDIUM: 'badge-info', LOW: 'badge-gray' };
    return map[s] || 'badge-gray';
  };

  return (
    <Layout title="Centre d'alertes" subtitle="Suivi et résolution des alertes système">
      <div className="card">
        <div className="card-body">
          {loading ? <div className="loading"><div className="spinner" /></div> : alerts.length === 0 ? (
            <div className="empty-state">Aucune alerte active</div>
          ) : (
            alerts.map((alert) => (
              <div key={alert.id} className="alert-item">
                <div className={`alert-dot ${alert.severity.toLowerCase()}`} />
                <div className="alert-content">
                  <div className="alert-title-row">
                    <h4>{alert.title}</h4>
                    <span className={`badge ${severityClass(alert.severity)}`}>{alert.severity}</span>
                  </div>
                  <p>{alert.message}</p>
                  <span className="alert-time">{new Date(alert.createdAt).toLocaleString('fr-FR')}</span>
                </div>
                <button className="btn btn-sm btn-secondary" onClick={() => handleResolve(alert.id)}>
                  <Check size={14} /> Résoudre
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </Layout>
  );
}
