import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { RiskCenter, RiskItem } from '../types';
import { ShieldAlert } from 'lucide-react';

const riskSections = [
  { key: 'riskyUsers' as const, title: 'Utilisateurs à risque', indicator: 'critical', icon: '🔴' },
  { key: 'criticalProducts' as const, title: 'Produits critiques', indicator: 'high', icon: '🟠' },
  { key: 'maintenanceMachines' as const, title: 'Maintenance requise', indicator: 'medium', icon: '🟡' },
  { key: 'expiringContracts' as const, title: 'Contrats expirants', indicator: 'low', icon: '🔵' },
  { key: 'inventoryAnomalies' as const, title: 'Anomalies d\'inventaire', indicator: 'high', icon: '⚠️' },
];

export default function RiskCenterPage() {
  const [data, setData] = useState<RiskCenter | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<RiskCenter>('/dashboard/risk-center').then((res) => {
      setData(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  if (loading) return <Layout title="Risk Center"><div className="loading"><div className="spinner" /></div></Layout>;

  const totalRisks = data ? Object.values(data).reduce((sum, arr) => sum + arr.length, 0) : 0;

  return (
    <Layout title="Risk Center" subtitle="Surveillance proactive des risques opérationnels et sécurité" actions={
      <span className="badge badge-danger badge-lg">
        <ShieldAlert size={14} />
        {totalRisks} risque{totalRisks > 1 ? 's' : ''} détecté{totalRisks > 1 ? 's' : ''}
      </span>
    }>
      <div className="risk-grid">
        {riskSections.map((section) => {
          const items: RiskItem[] = data?.[section.key] || [];
          return (
            <div key={section.key} className="risk-card">
              <div className="risk-card-header">
                <div className={`indicator ${section.indicator}`} />
                <h3>{section.title}</h3>
                <span className="count">{items.length}</span>
              </div>
              {items.length === 0 ? (
                <div className="empty-state empty-state--compact">
                  Aucun risque détecté
                </div>
              ) : (
                items.map((item, i) => (
                  <div key={i} className="risk-item">
                    <div className="risk-item-info">
                      <h4>{item.title}</h4>
                      <p>{item.description}</p>
                    </div>
                    <span className={`badge badge-${item.severity === 'CRITICAL' ? 'danger' : item.severity === 'HIGH' ? 'warning' : 'info'}`}>
                      {item.severity}
                    </span>
                  </div>
                ))
              )}
            </div>
          );
        })}
      </div>
    </Layout>
  );
}
