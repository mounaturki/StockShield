import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import {
  Shield,
  Lock,
  User,
  Eye,
  EyeOff,
  AlertCircle,
  ShieldCheck,
  Activity,
  ClipboardList,
} from "lucide-react";
import eskimoLogo from "../assets/eskimo.webp";

const FEATURES = [
  {
    icon: ShieldCheck,
    title: "RBAC & ABAC",
    description: "Contrôle d'accès avancé par rôle et contexte",
  },
  {
    icon: Activity,
    title: "Risk Center",
    description: "Détection proactive des risques stock & sécurité",
  },
  {
    icon: ClipboardList,
    title: "Journal d'audit",
    description: "Traçabilité complète de toutes les opérations",
  },
];

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(username, password);
      navigate("/");
    } catch (err: any) {
      if (!err.response) {
        setError(
          "Impossible de contacter le serveur. Vérifiez que le backend est démarré sur le port 8081.",
        );
      } else {
        setError(err.response?.data?.error || "Identifiants incorrects");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-form-panel">
        <div className="login-form-inner">
          <header className="login-header">
            <div className="login-brand-mark">
              <img src={eskimoLogo} alt="Eskimo" className="login-logo" />
              <div>
                <h1>StockShield</h1>
                <p>Ghamem Trading</p>
              </div>
            </div>
          </header>

          <div className="login-card">
            <div className="login-card-head">
              <h2>Bienvenue</h2>
              <p>Connectez-vous à votre espace sécurisé</p>
            </div>

            {error && (
              <div className="login-error" role="alert">
                <AlertCircle size={18} />
                <span>{error}</span>
              </div>
            )}

            <form className="login-form" onSubmit={handleSubmit}>
              <div className="login-field">
                <label htmlFor="username">Nom d'utilisateur</label>
                <div className="login-input-wrap">
                  <User size={18} className="login-input-icon" />
                  <input
                    id="username"
                    className="login-input"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Entrez votre identifiant"
                    autoComplete="username"
                    required
                  />
                </div>
              </div>

              <div className="login-field">
                <label htmlFor="password">Mot de passe</label>
                <div className="login-input-wrap">
                  <Lock size={18} className="login-input-icon" />
                  <input
                    id="password"
                    className="login-input"
                    type={showPassword ? "text" : "password"}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Entrez votre mot de passe"
                    autoComplete="current-password"
                    required
                  />
                  <button
                    type="button"
                    className="login-toggle-password"
                    onClick={() => setShowPassword((v) => !v)}
                    aria-label={
                      showPassword
                        ? "Masquer le mot de passe"
                        : "Afficher le mot de passe"
                    }
                  >
                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
              </div>

              <button
                className="login-submit"
                type="submit"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="login-spinner" />
                    Connexion en cours…
                  </>
                ) : (
                  "Se connecter"
                )}
              </button>
            </form>

            <div className="login-secure-note">
              <Shield size={14} />
              <span>Connexion chiffrée et protégée</span>
            </div>
          </div>

          <footer className="login-footer">
            © {new Date().getFullYear()} Ghamem Trading — Tous droits réservés
          </footer>
        </div>
      </div>

      <div className="login-hero-panel">
        <div className="login-hero-bg" aria-hidden="true">
          <div className="login-hero-orb login-hero-orb--1" />
          <div className="login-hero-orb login-hero-orb--2" />
          <div className="login-hero-grid" />
        </div>

        <div className="login-hero-content">
          <div className="login-hero-badge">
            <Shield size={16} />
            Plateforme certifiée entreprise
          </div>

          <h2>
            Sécurité &amp; contrôle
            <br />
            <span>de bout en bout</span>
          </h2>

          <p className="login-hero-desc">
            Solution intégrée de gestion des stocks, machines Eskimo et clients
            — avec détection d'anomalies et audit complet en temps réel.
          </p>

          <div className="login-feature-grid">
            {FEATURES.map(({ icon: Icon, title, description }) => (
              <div key={title} className="login-feature-card">
                <div className="login-feature-icon">
                  <Icon size={22} />
                </div>
                <div>
                  <h3>{title}</h3>
                  <p>{description}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="login-hero-stats">
            <div className="login-stat">
              <strong>4</strong>
              <span>Rôles RBAC</span>
            </div>
            <div className="login-stat-divider" />
            <div className="login-stat">
              <strong>24/7</strong>
              <span>Surveillance</span>
            </div>
            <div className="login-stat-divider" />
            <div className="login-stat">
              <strong>100%</strong>
              <span>Traçabilité</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
