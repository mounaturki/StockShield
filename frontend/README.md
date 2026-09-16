# 🛡️ StockShield - Plateforme de Gestion des Stocks Intelligente

Une application web moderne et sécurisée de gestion des stocks avec contrôle d'accès basé sur les rôles (RBAC), monitoring des risques et audit trail complet.

## 📋 Table des matières

- [Aperçu](#aperçu)
- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Installation](#installation)
- [Démarrage](#démarrage)
- [Structure du projet](#structure-du-projet)
- [Architecture](#architecture)
- [Licence](#licence)

## 🎯 Aperçu

StockShield est une solution complète de gestion de stocks conçue pour les entreprises qui nécessitent une visibilité totale sur leurs inventaires, une gestion efficace des risques et une conformité d'audit rigoureuse. L'application offre une interface intuitive avec un système de permissions granulaires.

## ✨ Fonctionnalités

### Gestion d'Inventaire

- 📦 Gestion complète des produits et stocks
- 🏢 Gestion des clients
- ⚙️ Gestion des machines et équipements
- 📊 Inventaire en temps réel
- 🏷️ Gestion des remises et promotions

### Sécurité & Contrôle d'Accès

- 🔐 Authentification sécurisée
- 👥 Contrôle d'accès basé sur les rôles (RBAC)
- 👤 Gestion des utilisateurs et permissions
- 📋 Trail d'audit complet
- ⚠️ Système d'alertes en temps réel

### Analyse des Risques

- 🎯 Centre de risques dédié
- 📈 Analyse des risques de stock
- 🔔 Alertes intelligentes
- 📊 Tableaux de bord analytiques
- 📉 Rapports détaillés

## 🛠️ Technologies

- **Frontend Framework**: React 18
- **Langage**: TypeScript
- **Build Tool**: Vite
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Styling**: CSS personnalisé
- **Icons**: Lucide React
- **Linting**: ESLint

## 📦 Installation

### Prérequis

- Node.js 16+
- npm ou yarn

### Étapes

1. **Cloner le repository**

```bash
git clone https://github.com/votre-username/stockshield.git
cd stockshield/frontend
```

2. **Installer les dépendances**

```bash
npm install
```

3. **Configurer l'environnement**
   Créer un fichier `.env` à la racine du projet :

```env
VITE_API_URL=http://localhost:3000/api
```

## 🚀 Démarrage

### Mode Développement

```bash
npm run dev
```

L'application sera disponible sur `http://localhost:5173`

### Build pour Production

```bash
npm run build
```

### Preview de Production

```bash
npm run preview
```

## 📁 Structure du Projet

```
src/
├── api/              # Client API et configurations
├── components/       # Composants réutilisables
│   ├── Layout.tsx
│   ├── Modal.tsx
│   ├── Sidebar.tsx
│   └── RoleRoute.tsx
├── config/          # Configurations d'application
│   └── roles.ts
├── context/         # Context API React
│   └── AuthContext.tsx
├── pages/           # Pages principales
│   ├── LoginPage.tsx
│   ├── DashboardPage.tsx
│   ├── ClientsPage.tsx
│   ├── ProductsPage.tsx
│   ├── StockPage.tsx
│   ├── InventoryPage.tsx
│   ├── RiskCenterPage.tsx
│   ├── AlertsPage.tsx
│   ├── UsersPage.tsx
│   └── AuditPage.tsx
├── types/           # Types TypeScript globaux
├── App.tsx          # Composant racine
├── main.tsx         # Point d'entrée
└── index.css        # Styles globaux
```

## 🏗️ Architecture

### Flux d'Authentification

- Authentification basée sur JWT
- Stockage sécurisé du token en localStorage
- Context API pour l'état d'authentification global
- Routes protégées avec vérification des permissions

### Système de Rôles

- RBAC (Role-Based Access Control) intégré
- Composant `RoleRoute` pour les routes protégées par rôle
- Configuration centralisée des rôles dans `config/roles.ts`
- Vérification des permissions côté client et serveur (recommandé)

### Gestion de l'État

- React Context pour l'authentification
- Appels API via Axios avec interceptors
- State management local via `useState`

## 🔒 Bonnes Pratiques de Sécurité

- Validation des tokens JWT
- Vérification des rôles à chaque route
- Trail d'audit de toutes les actions
- HTTPS recommandé en production
- Sanitization des données utilisateur

## 📞 Support & Contribution

Les contributions sont les bienvenues ! Veuillez :

1. Fork le repository
2. Créer une branche pour votre feature (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

**Développé avec ❤️ pour une meilleure gestion des stocks**
