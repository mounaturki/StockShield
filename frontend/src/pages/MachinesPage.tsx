import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { Machine, Client } from '../types';
import { useAuth } from '../context/AuthContext';
import { Plus, Pencil } from 'lucide-react';

const statusLabels: Record<string, string> = {
  OPERATIONAL: 'Opérationnelle', MAINTENANCE: 'En maintenance', BROKEN: 'En panne', DECOMMISSIONED: 'Désactivée',
};
const statusBadge: Record<string, string> = {
  OPERATIONAL: 'badge-success', MAINTENANCE: 'badge-warning', BROKEN: 'badge-danger', DECOMMISSIONED: 'badge-gray',
};
const machineTypes = [
  { value: 'GLACON', label: 'Machine à glaçons' },
  { value: 'GLACE', label: 'Machine à glace' },
];

export default function MachinesPage() {
  const { user } = useAuth();
  const canEdit = user?.role === 'ADMIN' || user?.role === 'SECRETAIRE';
  const [machines, setMachines] = useState<Machine[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Machine | null>(null);
  const [form, setForm] = useState({
    serialNumber: '', model: '', machineType: 'GLACE', clientId: '',
    status: 'OPERATIONAL', installationDate: '', nextMaintenance: '', notes: '',
  });
  const [filterType, setFilterType] = useState('');

  const load = () => {
    Promise.all([
      api.get<Machine[]>('/machines'),
      api.get<Client[]>('/clients'),
    ]).then(([m, c]) => { setMachines(m.data); setClients(c.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const filtered = machines.filter((m) => !filterType || m.machineType === filterType);

  const openCreate = () => {
    setEditing(null);
    setForm({ serialNumber: '', model: '', machineType: 'GLACE', clientId: '', status: 'OPERATIONAL', installationDate: '', nextMaintenance: '', notes: '' });
    setModalOpen(true);
  };

  const openEdit = (m: Machine) => {
    setEditing(m);
    setForm({
      serialNumber: m.serialNumber, model: m.model, machineType: m.machineType || 'GLACE',
      clientId: String(m.clientId), status: m.status,
      installationDate: m.installationDate || '', nextMaintenance: m.nextMaintenance || '', notes: m.notes || '',
    });
    setModalOpen(true);
  };

  const handleSave = async () => {
    const payload = { ...form, clientId: Number(form.clientId) };
    if (editing) await api.put(`/machines/${editing.id}`, payload);
    else await api.post('/machines', payload);
    setModalOpen(false);
    load();
  };

  return (
    <Layout title="Machines (Glaçon / Glace)" subtitle="Parc machines Eskimo et maintenance" actions={
      canEdit ? <button className="btn btn-primary" onClick={openCreate}><Plus size={18} /> Nouvelle machine</button> : undefined
    }>
      <div className="actions-bar">
        <select className="form-control filter-select" value={filterType} onChange={(e) => setFilterType(e.target.value)}>
          <option value="">Tous les types</option>
          {machineTypes.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
        </select>
      </div>

      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>N° Série</th><th>Type</th><th>Modèle</th><th>Client (Acheteur)</th><th>État</th>
                  <th>Maintenance</th>{canEdit && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {filtered.map((m) => (
                  <tr key={m.id}>
                    <td className="cell-primary">{m.serialNumber}</td>
                    <td><span className="badge badge-info">{m.machineTypeLabel || m.machineType}</span></td>
                    <td>{m.model}</td>
                    <td>{m.clientName}</td>
                    <td><span className={`badge ${statusBadge[m.status]}`}>{statusLabels[m.status]}</span></td>
                    <td>{m.nextMaintenance ? new Date(m.nextMaintenance).toLocaleDateString('fr-FR') : '—'}</td>
                    {canEdit && <td><button className="btn-icon" onClick={() => openEdit(m)}><Pencil size={16} /></button></td>}
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {canEdit && (
        <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Modifier la machine' : 'Nouvelle machine'}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
            <button className="btn btn-primary" onClick={handleSave}>Enregistrer</button>
          </>}>
          <div className="form-row">
            <div className="form-group"><label>N° Série</label><input className="form-control" value={form.serialNumber} onChange={(e) => setForm({ ...form, serialNumber: e.target.value })} /></div>
            <div className="form-group"><label>Type</label>
              <select className="form-control" value={form.machineType} onChange={(e) => setForm({ ...form, machineType: e.target.value })}>
                {machineTypes.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group"><label>Modèle</label><input className="form-control" value={form.model} onChange={(e) => setForm({ ...form, model: e.target.value })} /></div>
            <div className="form-group"><label>Client (Acheteur)</label>
              <select className="form-control" value={form.clientId} onChange={(e) => setForm({ ...form, clientId: e.target.value })}>
                <option value="">Sélectionner</option>
                {clients.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group"><label>État</label>
              <select className="form-control" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                {Object.entries(statusLabels).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
              </select>
            </div>
            <div className="form-group"><label>Prochaine maintenance</label><input className="form-control" type="date" value={form.nextMaintenance} onChange={(e) => setForm({ ...form, nextMaintenance: e.target.value })} /></div>
          </div>
        </Modal>
      )}
    </Layout>
  );
}
