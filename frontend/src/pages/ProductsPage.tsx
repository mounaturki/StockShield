import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import Modal from '../components/Modal';
import api from '../api/client';
import { Product, ProductPeriod } from '../types';
import { useAuth } from '../context/AuthContext';
import { Plus, Pencil } from 'lucide-react';

interface CategoryOption {
  value: string;
  label: string;
}

export default function ProductsPage() {
  const { user } = useAuth();
  const canEdit = user?.role === 'ADMIN' || user?.role === 'MAGASINIER';
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [currentPeriod, setCurrentPeriod] = useState<ProductPeriod | null>(null);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [form, setForm] = useState({ name: '', category: 'ESKIMO', price: '', minimumStock: '', supplier: '', description: '' });
  const [search, setSearch] = useState('');
  const [filterCategory, setFilterCategory] = useState('');

  const load = () => {
    Promise.all([
      api.get<Product[]>('/products/current-period'),
      api.get<CategoryOption[]>('/reference/product-categories'),
      api.get<ProductPeriod>('/reference/current-period').catch(() => ({ data: null })),
    ]).then(([p, c, period]) => {
      setProducts(p.data);
      setCategories(c.data);
      setCurrentPeriod(period.data);
      setLoading(false);
    });
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm({ name: '', category: 'ESKIMO', price: '', minimumStock: '', supplier: '', description: '' });
    setModalOpen(true);
  };

  const openEdit = (p: Product) => {
    setEditing(p);
    setForm({
      name: p.name, category: p.category, price: String(p.price),
      minimumStock: String(p.minimumStock), supplier: p.supplier, description: p.description || '',
    });
    setModalOpen(true);
  };

  const handleSave = async () => {
    const payload = {
      ...form,
      category: form.category,
      price: Number(form.price),
      minimumStock: Number(form.minimumStock),
      currentStock: editing?.currentStock || 0,
      periodCode: currentPeriod?.code,
    };
    if (editing) await api.put(`/products/${editing.id}`, payload);
    else await api.post('/products', payload);
    setModalOpen(false);
    load();
  };

  const filtered = products.filter((p) => {
    const matchSearch = p.name.toLowerCase().includes(search.toLowerCase())
      || (p.categoryLabel || p.category)?.toLowerCase().includes(search.toLowerCase());
    const matchCategory = !filterCategory || p.category === filterCategory;
    return matchSearch && matchCategory;
  });

  return (
    <Layout title="Produits — Période actuelle" subtitle="Catalogue produits de la saison en cours" actions={
      canEdit ? <button className="btn btn-primary" onClick={openCreate}><Plus size={18} /> Nouveau produit</button> : undefined
    }>
      {currentPeriod && (
        <div className="info-banner">
          <strong>Période active :</strong> {currentPeriod.name} ({currentPeriod.code})
          <span className="info-banner-meta">
            Les produits changent d'une période à l'autre
          </span>
        </div>
      )}

      <div className="actions-bar">
        <input className="search-input" placeholder="Rechercher..." value={search} onChange={(e) => setSearch(e.target.value)} />
        <select className="form-control filter-select" value={filterCategory} onChange={(e) => setFilterCategory(e.target.value)}>
          <option value="">Toutes les gammes</option>
          {categories.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
        </select>
      </div>

      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : (
            <table>
              <thead>
                <tr>
                  <th>Produit</th><th>Gamme</th><th>Prix (MAD)</th><th>Stock</th>
                  <th>Stock min.</th><th>Statut</th>{canEdit && <th>Actions</th>}
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => (
                  <tr key={p.id}>
                    <td className="cell-primary">{p.name}</td>
                    <td><span className="badge badge-primary">{p.categoryLabel || p.category}</span></td>
                    <td>{p.price?.toFixed(2)}</td>
                    <td className={p.lowStock ? 'cell-danger' : 'cell-numeric'}>{p.currentStock}</td>
                    <td>{p.minimumStock}</td>
                    <td>{p.lowStock && <span className="badge badge-danger">Stock faible</span>}</td>
                    {canEdit && <td><button className="btn-icon" onClick={() => openEdit(p)}><Pencil size={16} /></button></td>}
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {canEdit && (
        <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Modifier le produit' : 'Nouveau produit'}
          footer={<>
            <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Annuler</button>
            <button className="btn btn-primary" onClick={handleSave}>Enregistrer</button>
          </>}>
          <div className="form-group"><label>Nom</label><input className="form-control" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></div>
          <div className="form-group"><label>Gamme</label>
            <select className="form-control" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
              {categories.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
            </select>
          </div>
          <div className="form-row">
            <div className="form-group"><label>Prix (MAD)</label><input className="form-control" type="number" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} /></div>
            <div className="form-group"><label>Stock minimum</label><input className="form-control" type="number" value={form.minimumStock} onChange={(e) => setForm({ ...form, minimumStock: e.target.value })} /></div>
          </div>
          <div className="form-group"><label>Fournisseur</label><input className="form-control" value={form.supplier} onChange={(e) => setForm({ ...form, supplier: e.target.value })} /></div>
        </Modal>
      )}
    </Layout>
  );
}
