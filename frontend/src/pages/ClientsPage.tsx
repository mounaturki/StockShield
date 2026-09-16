import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { Client } from '../types';
import { Plus, Pencil, Trash2 } from 'lucide-react';

export default function ClientsPage() {
  const [clients, setClients] = useState<Client[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Client | null>(null);
  const [form, setForm] = useState({ name: '', address: '', phone: '', manager: '', contractExpiry: '' });
  const [search, setSearch] = useState('');

  const load = () => {
    api.get<Client[]>('/clients').then((res) => { setClients(res.data); setLoading(false); });
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm({ name: '', address: '', phone: '', manager: '', contractExpiry: '' });
    setModalOpen(true);
  };

  const openEdit = (c: Client) => {
    setEditing(c);
    setForm({ name: c.name, address: c.address, phone: c.phone, manager: c.manager, contractExpiry: c.contractExpiry || '' });
    setModalOpen(true);
  };

  const handleSave = async () => {
    if (editing) {
      await api.put(`/clients/${editing.id}`, form);
    } else {
      await api.post('/clients', form);
    }
    setModalOpen(false);
    load();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Désactiver ce client ?')) {
      await api.delete(`/clients/${id}`);
      load();
    }
  };

  const filtered = clients.filter((c) =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.manager?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <Layout title="Clients — Acheteurs" subtitle="Gestion des clients acheteurs et contrats" actions={
      <button className="btn btn-primary" onClick={openCreate}><Plus size={18} /> Nouveau client</button>
    }>
      <div className="actions-bar">
        <input className="search-input" placeholder="Rechercher un client..." value={search} onChange={(e) => setSearch(e.target.value)} />
      </div>

      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>Nom</th><th>Adresse</th><th>Téléphone</th><th>Responsable</th>
                  <th>Contrat</th><th>Statut</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.id}>
                    <td className="cell-primary">{c.name}</td>
                    <td>{c.address}</td>
                    <td>{c.phone}</td>
                    <td>{c.manager}</td>
                    <td>{c.contractExpiry ? new Date(c.contractExpiry).toLocaleDateString('fr-FR') : '—'}</td>
                    <td><span className={`badge ${c.active ? 'badge-success' : 'badge-gray'}`}>{c.active ? 'Actif' : 'Inactif'}</span></td>
                    <td>
                      <button className="btn-icon" onClick={() => openEdit(c)}><Pencil size={16} /></button>
                      <button className="btn-icon" onClick={() => handleDelete(c.id)}><Trash2 size={16} /></button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Modifier le client' : 'Nouveau client'}
        footer={<>
          <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
          <button className="btn btn-primary" onClick={handleSave}>{editing ? 'Enregistrer' : 'Créer'}</button>
        </>}>
        <div className="form-group"><label>Nom</label><input className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></div>
        <div className="form-group"><label>Adresse</label><input className="form-control" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} /></div>
        <div className="form-row">
          <div className="form-group"><label>Téléphone</label><input className="form-control" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} /></div>
          <div className="form-group"><label>Responsable</label><input className="form-control" value={form.manager} onChange={(e) => setForm({ ...form, manager: e.target.value })} /></div>
        </div>
        <div className="form-group"><label>Expiration du contrat</label><input className="form-control" type="date" value={form.contractExpiry} onChange={(e) => setForm({ ...form, contractExpiry: e.target.value })} /></div>
      </Modal>
    </Layout>
  );
}
