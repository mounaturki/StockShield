import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { Discount } from '../types';
import { Plus, CheckCircle } from 'lucide-react';

export default function DiscountsPage() {
  const [discounts, setDiscounts] = useState<Discount[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ name: '', percentage: '', conditions: '' });

  const load = () => {
    api.get<Discount[]>('/discounts').then((res) => { setDiscounts(res.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const handleSave = async () => {
    await api.post('/discounts', { name: form.name, percentage: Number(form.percentage), conditions: form.conditions });
    setModalOpen(false);
    setForm({ name: '', percentage: '', conditions: '' });
    load();
  };

  const handleValidate = async (id: number) => {
    await api.patch(`/discounts/${id}/validate`);
    load();
  };

  return (
    <Layout title="Gestion des remises" subtitle="Remises commerciales et validations" actions={
      <button className="btn btn-primary" onClick={() => setModalOpen(true)}><Plus size={18} /> Nouvelle remise</button>
    }>
      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>Nom</th><th>Pourcentage</th><th>Conditions</th><th>Validée</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {discounts.map((d) => (
                  <tr key={d.id}>
                    <td className="cell-primary">{d.name}</td>
                    <td><span className="badge badge-primary">{d.percentage}%</span></td>
                    <td>{d.conditions}</td>
                    <td>
                      <span className={`badge ${d.validated ? 'badge-success' : 'badge-warning'}`}>
                        {d.validated ? `Oui (${d.validatedBy})` : 'En attente'}
                      </span>
                    </td>
                    <td>
                      {!d.validated && (
                        <button className="btn btn-sm btn-primary" onClick={() => handleValidate(d.id)}>
                          <CheckCircle size={14} /> Valider
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Nouvelle remise"
        footer={<>
          <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
          <button className="btn btn-primary" onClick={handleSave}>Créer</button>
        </>}>
        <div className="form-group"><label>Nom</label><input className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></div>
        <div className="form-group"><label>Pourcentage (%)</label><input className="form-control" type="number" step="0.01" value={form.percentage} onChange={(e) => setForm({ ...form, percentage: e.target.value })} /></div>
        <div className="form-group"><label>Conditions</label><textarea className="form-control" rows={3} value={form.conditions} onChange={(e) => setForm({ ...form, conditions: e.target.value })} /></div>
      </Modal>
    </Layout>
  );
}
