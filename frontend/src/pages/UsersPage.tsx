import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { User } from '../types';
import { Plus, UserX } from 'lucide-react';

const roleLabels: Record<string, string> = {
  ADMIN: 'Administrateur', MAGASINIER: 'Magasinier', VENDEUR: 'Vendeur', SECRETAIRE: 'Secrétaire',
};

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ username: '', email: '', password: '', firstName: '', lastName: '', role: 'VENDEUR', warehouseId: '' });

  const load = () => {
    api.get<User[]>('/users').then((res) => { setUsers(res.data); setLoading(false); })
      .catch(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleSave = async () => {
    const payload = { ...form, warehouseId: form.warehouseId ? Number(form.warehouseId) : null };
    await api.post('/users', payload);
    setModalOpen(false);
    setForm({ username: '', email: '', password: '', firstName: '', lastName: '', role: 'VENDEUR', warehouseId: '' });
    load();
  };

  const handleDisable = async (id: number) => {
    if (confirm('Désactiver cet utilisateur ?')) {
      await api.patch(`/users/${id}/disable`);
      load();
    }
  };

  return (
    <Layout title="Gestion des utilisateurs" subtitle="Comptes, rôles et accès RBAC" actions={
      <button className="btn btn-primary" onClick={() => setModalOpen(true)}><Plus size={18} /> Nouvel utilisateur</button>
    }>
      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>Utilisateur</th><th>Email</th><th>Nom</th><th>Rôle</th><th>Dépôt</th><th>Statut</th><th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td className="cell-primary">{u.username}</td>
                    <td>{u.email}</td>
                    <td>{u.firstName} {u.lastName}</td>
                    <td><span className="badge badge-primary">{roleLabels[u.role] || u.role}</span></td>
                    <td>{u.warehouseId || '—'}</td>
                    <td><span className={`badge ${u.enabled !== false ? 'badge-success' : 'badge-gray'}`}>{u.enabled !== false ? 'Actif' : 'Désactivé'}</span></td>
                    <td>
                      {u.enabled !== false && (
                        <button className="btn-icon" onClick={() => handleDisable(u.id)} title="Désactiver"><UserX size={16} /></button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Nouvel utilisateur"
        footer={<>
          <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
          <button className="btn btn-primary" onClick={handleSave}>Créer</button>
        </>}>
        <div className="form-row">
          <div className="form-group"><label>Nom d'utilisateur</label><input className="form-control" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} /></div>
          <div className="form-group"><label>Email</label><input className="form-control" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></div>
        </div>
        <div className="form-group"><label>Mot de passe</label><input className="form-control" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></div>
        <div className="form-row">
          <div className="form-group"><label>Prénom</label><input className="form-control" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} /></div>
          <div className="form-group"><label>Nom</label><input className="form-control" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} /></div>
        </div>
        <div className="form-row">
          <div className="form-group"><label>Rôle</label>
            <select className="form-control" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              {Object.entries(roleLabels).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
            </select>
          </div>
          <div className="form-group"><label>ID Dépôt</label><input className="form-control" type="number" value={form.warehouseId} onChange={(e) => setForm({ ...form, warehouseId: e.target.value })} /></div>
        </div>
      </Modal>
    </Layout>
  );
}
