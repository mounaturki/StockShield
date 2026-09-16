import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { StockRisk, RiskItem } from '../types';
import { TrendingDown, Package, ClipboardList, Bell } from 'lucide-react';

export default function StockRiskPage() {
  const [data, setData] = useState<StockRisk | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<StockRisk>('/dashboard/stock-risk').then((res) => {
      setData(res.data);
      setLoading(false);
    }).catch(() => setLoading(false));
  }, []);

  if (loading) return <Layout title="Risque Stock"><div className="loading"><div className="spinner" /></div></Layout>;

  const sections = [
    { key: 'criticalProducts', title: 'Produits en stock critique', items: data?.criticalProducts || [], icon: Package, color: 'critical' },
    { key: 'inventoryAnomalies', title: "Anomalies d'inventaire", items: data?.inventoryAnomalies || [], icon: ClipboardList, color: 'high' },
    { key: 'stockAlerts', title: 'Alertes stock', items: data?.stockAlerts || [], icon: Bell, color: 'medium' },
  ];

  const total = data ? data.lowStockCount + data.discrepancyCount + (data.stockAlerts?.length || 0) : 0;

  return (
    <Layout title="Risque Stock" subtitle="Suivi des alertes stock et anomalies d'inventaire" actions={
      <span className="badge badge-warning badge-lg">
        <TrendingDown size={14} />
        {total} alerte{total > 1 ? 's' : ''} stock
      </span>
    }>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon red"><Package size={24} /></div>
          <div className="stat-info">
            <h4>Stock faible</h4>
            <div className="value">{data?.lowStockCount || 0}</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon orange"><ClipboardList size={24} /></div>
          <div className="stat-info">
            <h4>Écarts inventaire</h4>
            <div className="value">{data?.discrepancyCount || 0}</div>
          </div>
        </div>
      </div>

      <div className="risk-grid">
        {sections.map((section) => (
          <div key={section.key} className="risk-card">
            <div className="risk-card-header">
              <div className={`indicator ${section.color}`} />
              <h3>{section.title}</h3>
              <span className="count">{section.items.length}</span>
            </div>
            {section.items.length === 0 ? (
              <div className="empty-state empty-state--compact">Aucun risque détecté</div>
            ) : (
              section.items.map((item: RiskItem, i: number) => (
                <div key={i} className="risk-item">
                  <div className="risk-item-info">
                    <h4>{item.title}</h4>
                    <p>{item.description}</p>
                  </div>
                  <span className={`badge badge-${item.severity === 'CRITICAL' || item.severity === 'HIGH' ? 'danger' : 'warning'}`}>
                    {item.severity}
                  </span>
                </div>
              ))
            )}
          </div>
        ))}
      </div>
    </Layout>
  );
}
