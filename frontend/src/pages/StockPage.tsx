import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { StockMovement, Product } from '../types';
import { Plus, ArrowDown, ArrowUp, RotateCcw, AlertTriangle } from 'lucide-react';

const typeLabels: Record<string, string> = {
  ENTRY: 'Entrée', EXIT: 'Sortie', RETURN: 'Retour', LOST: 'Perdu', BROKEN: 'Cassé',
};
const typeBadge: Record<string, string> = {
  ENTRY: 'badge-success', EXIT: 'badge-info', RETURN: 'badge-primary', LOST: 'badge-warning', BROKEN: 'badge-danger',
};
const typeIcons: Record<string, typeof ArrowDown> = {
  ENTRY: ArrowDown, EXIT: ArrowUp, RETURN: RotateCcw, LOST: AlertTriangle, BROKEN: AlertTriangle,
};

export default function StockPage() {
  const [movements, setMovements] = useState<StockMovement[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ productId: '', type: 'ENTRY', quantity: '', reason: '', warehouseId: '1' });

  const load = () => {
    Promise.all([
      api.get<StockMovement[]>('/stock'),
      api.get<Product[]>('/products'),
    ]).then(([m, p]) => { setMovements(m.data); setProducts(p.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const handleSave = async () => {
    await api.post('/stock/movement', {
      productId: Number(form.productId),
      type: form.type,
      quantity: Number(form.quantity),
      reason: form.reason,
      warehouseId: Number(form.warehouseId),
    });
    setModalOpen(false);
    setForm({ productId: '', type: 'ENTRY', quantity: '', reason: '', warehouseId: '1' });
    load();
  };

  return (
    <Layout title="Gestion des stocks" subtitle="Mouvements et historique des entrées/sorties" actions={
      <button className="btn btn-primary" onClick={() => setModalOpen(true)}><Plus size={18} /> Nouveau mouvement</button>
    }>
      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>Date</th><th>Produit</th><th>Type</th><th>Quantité</th>
                  <th>Raison</th><th>Effectué par</th>
                </tr>
              </thead>
              <tbody>
                {movements.map((m) => {
                  const Icon = typeIcons[m.type] || ArrowDown;
                  return (
                    <tr key={m.id}>
                      <td>{new Date(m.createdAt).toLocaleString('fr-FR')}</td>
                      <td className="cell-primary">{m.productName}</td>
                      <td><span className={`badge ${typeBadge[m.type]}`}><Icon size={12} />{typeLabels[m.type]}</span></td>
                      <td className="cell-numeric">{m.quantity}</td>
                      <td>{m.reason || '—'}</td>
                      <td>{m.performedBy}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Nouveau mouvement de stock"
        footer={<>
          <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
          <button className="btn btn-primary" onClick={handleSave}>Enregistrer</button>
        </>}>
        <div className="form-group"><label>Produit</label>
          <select className="form-control" value={form.productId} onChange={(e) => setForm({ ...form, productId: e.target.value })}>
            <option value="">Sélectionner</option>
            {products.map((p) => <option key={p.id} value={p.id}>{p.name} (stock: {p.currentStock})</option>)}
          </select>
        </div>
        <div className="form-row">
          <div className="form-group"><label>Type</label>
            <select className="form-control" value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>
              {Object.entries(typeLabels).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
            </select>
          </div>
          <div className="form-group"><label>Quantité</label><input className="form-control" type="number" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} /></div>
        </div>
        <div className="form-group"><label>Raison</label><input className="form-control" value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} /></div>
      </Modal>
    </Layout>
  );
}
