import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import api from '../api/client';
import { AuditLog } from '../types';

export default function AuditPage() {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<AuditLog[]>('/audit/recent').then((res) => { setLogs(res.data); setLoading(false); })
      .catch(() => setLoading(false));
  }, []);

  return (
    <Layout title="Journal d'audit" subtitle="Traçabilité complète des actions utilisateurs">
      <div className="card">
        <div className="table-container">
          {loading ? <div className="loading"><div className="spinner" /></div> : logs.length === 0 ? (
            <div className="empty-state">Aucune entrée d'audit</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Date</th><th>Utilisateur</th><th>Action</th>
                  <th>Entité</th><th>Ancienne valeur</th><th>Nouvelle valeur</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id}>
                    <td className="cell-nowrap">{new Date(log.createdAt).toLocaleString('fr-FR')}</td>
                    <td className="cell-primary">{log.username}</td>
                    <td><span className="badge badge-info">{log.action}</span></td>
                    <td>{log.entityType} #{log.entityId}</td>
                    <td className="cell-muted">{log.oldValue || '—'}</td>
                    <td className="cell-muted">{log.newValue || '—'}</td>
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
