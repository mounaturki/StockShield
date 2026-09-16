# StockShield

> Plateforme de gestion sécurisée pour **Ghamem Trading** — clients, machines, stocks & inventaires, avec une couche cybersécurité (JWT, RBAC, ABAC, audit, détection d’anomalies).

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB)](https://react.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## Présentation

**StockShield** est une application full-stack conçue pour une société de distribution (produits glacés / machines Eskimo).  
Elle combine la **gestion métier** et des mécanismes de **cybersécurité** :

- Gestion des **clients (acheteurs)**, **machines** (glace / glaçons), **produits par période**, **stocks**, **inventaires**, **remises**
- Authentification **JWT**, contrôle d’accès **RBAC + ABAC**
- **Journal d’audit**, **alertes automatiques**, **Risk Center** (admin) et **Risque Stock** (magasinier / secrétaire)
- Interfaces **différenciées selon le rôle** de l’utilisateur

> Document détaillé pour entretien / présentation : [`docs/StockShield-Presentation.pdf`](docs/StockShield-Presentation.pdf)

---

## Fonctionnalités principales

| Module | Description |
|--------|-------------|
| Authentification | Login JWT, verrouillage après 5 tentatives, historique des connexions |
| Utilisateurs | CRUD, rôles, affectation dépôt (admin) |
| Clients | Acheteurs, contrats, dates d’expiration |
| Machines | Types **Glaçon** / **Glace**, maintenance, états |
| Produits | Catalogue lié à une **période commerciale** (gammes Paname, Eskimo, Magnum…) |
| Stocks | Entrée, sortie, retour, perdu, cassé |
| Inventaire | Mensuel / annuel + détection d’écarts |
| Remises | Règles % + validation |
| Alertes | Stock faible, contrats, pannes, activité suspecte |
| Risk Center | Vue consolidée des risques (admin) |
| Risque Stock | Focus stock / inventaire (magasinier, secrétaire) |
| Audit | Traçabilité complète des actions |

---

## Architecture

```
┌─────────────────┐     /api      ┌──────────────────────┐
│  React + Vite   │ ───────────►  │  Spring Boot (8081)  │
│  TypeScript     │               │  Spring Security     │
│  (port 5173)    │               │  JWT · RBAC · ABAC   │
└─────────────────┘               └──────────┬───────────┘
                                             │
                                             ▼
                                  ┌──────────────────────┐
                                  │     PostgreSQL       │
                                  │   DB: stockshield    │
                                  └──────────────────────┘
```

```
StockShield/
├── backend/                 # API Java 21 / Spring Boot / Gradle
│   └── src/main/java/.../
│       ├── entity/          # Modèle JPA
│       ├── repository/      # Spring Data
│       ├── service/         # Logique métier + anomalies
│       ├── controller/      # REST API
│       ├── security/        # JWT, ABAC, SecurityConfig
│       ├── dto/             # Objets de transfert
│       └── config/          # Seed & migrations
├── frontend/                # React 18 / Vite / TypeScript
│   └── src/
│       ├── pages/           # Écrans
│       ├── components/      # UI réutilisable
│       ├── context/         # Auth
│       └── config/          # Menus & droits par rôle
└── docs/                    # Présentation PDF
```

---

## Stack technique

| Couche | Technologies |
|--------|----------------|
| Backend | Java 21, Spring Boot 3.2, Spring Security, Spring Data JPA, Gradle |
| Frontend | React 18, TypeScript, Vite, Axios, Lucide React |
| Base de données | PostgreSQL |
| Sécurité | JWT, BCrypt, RBAC, ABAC, Audit trail, détection d’anomalies (@Scheduled) |

---

## Rôles & interfaces

| Rôle | Accès typiques |
|------|----------------|
| **ADMIN** | Accès complet, Risk Center, utilisateurs, journal d’audit |
| **MAGASINIER** | Produits, stocks, inventaire, **Risque Stock** |
| **SECRETAIRE** | Clients, machines, inventaire, remises, **Risque Stock** |
| **VENDEUR** | Clients (acheteurs), machines, produits (lecture), remises |

### Sécurité ABAC (exemple)

Un **magasinier** ne peut modifier le stock que :

1. pendant les **heures de travail** (8h–18h),
2. pour le **dépôt** auquel il est affecté.

---

## Prérequis

- JDK **21**
- Node.js **18+**
- PostgreSQL **14+**
- Gradle 8+ (ou Gradle installé localement)

---

## Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/<votre-username>/StockShield.git
cd StockShield
```

### 2. Base de données

```sql
CREATE DATABASE stockshield;
```

### 3. Configuration backend

Copiez le fichier d’exemple puis adaptez vos identifiants locaux :

```bash
cd backend
copy src\main\resources\application-example.yml src\main\resources\application.yml
```

Variables utiles (optionnel via variables d’environnement) :

| Variable | Description | Défaut |
|----------|-------------|--------|
| `DB_URL` | URL JDBC | `jdbc:postgresql://localhost:5432/stockshield` |
| `DB_USER` | Utilisateur PostgreSQL | `postgres` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres` |
| `JWT_SECRET` | Clé secrète JWT | (valeur d’exemple à changer) |
| `SERVER_PORT` | Port API | `8081` |

> **Important :** ne committez jamais de vrais mots de passe ou secrets dans GitHub.

### 4. Lancer le backend

```bash
cd backend
gradle bootRun
```

API : `http://localhost:8081`

### 5. Lancer le frontend

```bash
cd frontend
npm install
npm run dev
```

Application : `http://localhost:5173`

---

## Comptes de démonstration

| Utilisateur | Mot de passe | Rôle |
|-------------|--------------|------|
| `admin` | `admin123` | Administrateur |
| `magasinier` | `mag123` | Magasinier |
| `vendeur` | `ven123` | Vendeur |
| `secretaire` | `sec123` | Secrétaire |

Ces comptes sont créés automatiquement au premier démarrage (seed data).

---

## API (aperçu)

Toutes les routes (sauf login) nécessitent :

```http
Authorization: Bearer <token>
```

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/auth/login` | Connexion |
| `GET` | `/api/dashboard` | Tableau de bord |
| `GET` | `/api/dashboard/risk-center` | Risk Center (admin) |
| `GET` | `/api/dashboard/stock-risk` | Risque stock |
| `GET/POST` | `/api/clients` | Clients |
| `GET/POST` | `/api/machines` | Machines |
| `GET/POST` | `/api/products` | Produits |
| `POST` | `/api/stock/movement` | Mouvement de stock |
| `GET` | `/api/alerts` | Alertes |
| `GET` | `/api/audit/recent` | Journal d’audit |

---

## Captures / démonstration

Après connexion, explorez :

1. **Admin** → Risk Center + Audit  
2. **Magasinier** → Stocks + Risque Stock  
3. **Vendeur** → Clients (acheteurs)  
4. **Secrétaire** → Machines + inventaire  

---

## Améliorations futures

- [ ] Tests unitaires / d’intégration (JUnit, Mockito, React Testing Library)
- [ ] Upload réel des contrats PDF & photos machines
- [ ] Refresh token JWT
- [ ] Docker Compose (backend + frontend + PostgreSQL)
- [ ] CI/CD (GitHub Actions)
- [ ] Notifications e-mail / SMS

---

## Auteur

Projet développé dans le cadre d’un portfolio / candidature — plateforme orientée **gestion + cybersécurité** pour Ghamem Trading.

---

## Licence

Ce projet est fourni à des fins éducatives et de démonstration (MIT).
