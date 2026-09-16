import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { Inventory } from '../types';
import { Plus, ClipboardList } from 'lucide-react';

export default function InventoryPage() {
  const [inventories, setInventories] = useState<Inventory[]>([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    api.get<Inventory[]>('/inventories').then((res) => { setInventories(res.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const createInventory = async (type: string) => {
    await api.post('/inventories', { type });
    load();
  };

  return (
    <Layout title="Inventaire" subtitle="Contrôle et suivi des inventaires mensuels et annuels" actions={
      <div className="header-actions-group">
        <button className="btn btn-secondary" onClick={() => createInventory('MONTHLY')}><Plus size={18} /> Inventaire mensuel</button>
        <button className="btn btn-primary" onClick={() => createInventory('ANNUAL')}><Plus size={18} /> Inventaire annuel</button>
      </div>
    }>
      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : inventories.length === 0 ? (
            <div className="empty-state">
              <ClipboardList />
              <p>Aucun inventaire enregistré</p>
              <p className="empty-state__hint">Créez un inventaire mensuel ou annuel pour commencer</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>ID</th><th>Type</th><th>Date</th><th>Effectué par</th>
                  <th>Écarts</th><th>Statut</th>
                </tr>
              </thead>
              <tbody>
                {inventories.map((inv) => (
                  <tr key={inv.id}>
                    <td>#{inv.id}</td>
                    <td><span className="badge badge-primary">{inv.type === 'ANNUAL' ? 'Annuel' : 'Mensuel'}</span></td>
                    <td>{new Date(inv.inventoryDate).toLocaleDateString('fr-FR')}</td>
                    <td>{inv.performedBy}</td>
                    <td>
                      <span className={`badge ${inv.totalDiscrepancies > 0 ? 'badge-danger' : 'badge-success'}`}>
                        {inv.totalDiscrepancies} écart{inv.totalDiscrepancies > 1 ? 's' : ''}
                      </span>
                    </td>
                    <td><span className={`badge ${inv.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>{inv.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </Layout>
  );
}
