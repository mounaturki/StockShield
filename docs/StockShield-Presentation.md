# StockShield — Présentation complète du projet

**Document de préparation entretien / portfolio**  
Plateforme de gestion sécurisée — Ghamem Trading

---

## 1. Pitch (30 secondes)

StockShield est une plateforme web full-stack pour Ghamem Trading. Elle permet de gérer les clients acheteurs, les machines (glace / glaçons), les produits par période commerciale, les stocks et les inventaires, tout en intégrant une couche cybersécurité : JWT, RBAC, ABAC, journal d’audit, détection d’anomalies, Risk Center (admin) et Risque Stock (magasinier / secrétaire).

Stack : Java 21, Spring Boot, Spring Security, JPA, PostgreSQL, React, Vite, TypeScript.

---

## 2. Contexte métier

Ghamem Trading a besoin de :

- Gérer les acheteurs (clients) et leurs contrats
- Suivre les machines installées (types glace / glaçons, panne, maintenance)
- Gérer un catalogue de produits qui change selon les périodes
- Contrôler les stocks (entrées, sorties, pertes, inventaires)
- Sécuriser l’accès selon les rôles
- Détecter les risques (stock faible, contrats, connexions suspectes)

Valeur ajoutée : Risk Center + anomalies → plateforme « gestion + cybersécurité », pas un simple CRUD.

---

## 3. Architecture

Navigateur React (port 5173) → API Spring Boot (port 8081) → PostgreSQL (base stockshield).

Couches backend :

- Controller : API REST
- Service : logique métier
- Repository : accès données
- Entity : modèle JPA
- DTO : objets échangés avec le front
- Security : JWT, RBAC, ABAC

---

## 4. Stack technique

| Couche | Technologies |
|--------|----------------|
| Backend | Java 21, Spring Boot 3.2, Spring Security, Spring Data JPA, Gradle |
| Frontend | React 18, TypeScript, Vite, Axios |
| BDD | PostgreSQL |
| Sécurité | JWT, BCrypt, RBAC, ABAC, Audit, Scheduler d’anomalies |

---

## 5. Modèle de données (résumé)

- User : rôles ADMIN, MAGASINIER, VENDEUR, SECRETAIRE ; dépôt ; verrouillage compte
- Client : acheteur, contrat, expiration
- Machine : type GLACON / GLACE, statut, maintenance
- ProductPeriod : période commerciale active
- Product : gammes (Paname STK, Roché, BRKT, CNE, Vrac, Tropic, Eskimo, Kimo, Capp, Kimcone, Glacon, Magnum, Flash, Zonda)
- StockMovement : ENTRY, EXIT, RETURN, LOST, BROKEN
- Inventory / InventoryItem : écarts
- Discount : remises validées
- Alert, AuditLog, LoginHistory

---

## 6. Modules fonctionnels

1. Authentification (JWT, lock 5 tentatives, historique)
2. Utilisateurs (admin)
3. Clients / acheteurs
4. Machines glace / glaçons
5. Produits par période
6. Stocks
7. Inventaire mensuel / annuel
8. Remises
9. Alertes
10. Risk Center (admin)
11. Risque Stock (magasinier, secrétaire)
12. Journal d’audit (admin)

---

## 7. Sécurité

### JWT
Login → token signé → header Authorization Bearer → filtre JwtAuthenticationFilter. Session stateless.

### RBAC
- ADMIN : tout + Risk Center + audit + users
- MAGASINIER : produits, stocks, inventaire, risque stock
- SECRETAIRE : clients, machines, inventaire, remises, risque stock
- VENDEUR : clients, machines, produits (lecture), remises

### ABAC
Magasinier : modification stock uniquement aux heures ouvrées (8h–18h) et pour son dépôt.

### Audit
Chaque action sensible est journalisée (qui, quand, quoi, anciennes / nouvelles valeurs).

### Anomalies (toutes les 5 minutes)
Stock < minimum, contrats ≤ 30 jours, machines en panne, maintenance ≤ 7 jours, ≥ 10 échecs login / h, ≥ 5 pertes/casses / 24h.

---

## 8. Interfaces par rôle

| Page | Admin | Magasinier | Secrétaire | Vendeur |
|------|:-----:|:----------:|:----------:|:-------:|
| Dashboard | oui | oui | oui | oui |
| Risk Center | oui | non | non | non |
| Risque Stock | oui* | oui | oui | non |
| Clients | oui | non | oui | oui |
| Machines | oui | non | oui | oui |
| Produits | oui | oui | oui | oui |
| Stocks | oui | oui | non | non |
| Inventaire | oui | oui | oui | non |
| Remises | oui | non | oui | oui |
| Users / Audit | oui | non | non | non |

Double contrôle : backend (API) + frontend (menu + RoleRoute).

---

## 9. Flux clés

### Login
POST /api/auth/login → vérif BCrypt + lock → JWT → front stocke token → menu selon rôle.

### Mouvement de stock
ABAC → recalcul stock → sauvegarde mouvement → audit → alertes éventuelles plus tard.

### Risk Center
Agrégation users risqués, produits critiques, machines, contrats, inventaires.

---

## 10. Comptes démo

| User | Mot de passe | Rôle |
|------|--------------|------|
| admin | admin123 | Administrateur |
| magasinier | mag123 | Magasinier |
| vendeur | ven123 | Vendeur |
| secretaire | sec123 | Secrétaire |

---

## 11. Difficultés techniques rencontrées

1. Dépendance circulaire Spring (AuthService ↔ SecurityConfig) → CustomUserDetailsService
2. Popup Basic Auth navigateur → JwtAuthenticationEntryPoint (réponse JSON 401)
3. Erreur 400 après changement d’enum catégories produits → migration des données legacy

---

## 12. Améliorations futures

Tests automatisés, upload PDF/photos, refresh token, Docker Compose, CI/CD GitHub Actions, notifications e-mail/SMS.

---

## 13. Questions entretien — réponses courtes

**C’est quoi StockShield ?**  
Plateforme de gestion sécurisée pour Ghamem Trading : stock, clients, machines + cybersécurité.

**Pourquoi JWT ?**  
Auth stateless adaptée à une SPA React.

**RBAC vs ABAC ?**  
RBAC = selon le rôle. ABAC = selon attributs (heure, dépôt).

**Comment un vendeur n’accède pas au Risk Center ?**  
Refus API backend + menu/route filtrés côté frontend.

**Détection d’anomalies ?**  
Job @Scheduled toutes les 5 minutes qui crée des alertes.

**Pourquoi PostgreSQL ?**  
Données relationnelles (client↔machine, produit↔mouvements…).

**Ce qui rend le projet unique ?**  
Risk Center, ABAC, audit, anomalies — pas seulement un CRUD stock.

**Mots de passe ?**  
Hashés avec BCrypt.

---

## 14. Phrase de conclusion (entretien)

« J’ai conçu et développé une plateforme full-stack Spring Boot / React pour Ghamem Trading, avec gestion métier (clients, machines, produits par période, stocks, inventaires) et une couche cybersécurité (JWT, RBAC, ABAC, audit, détection d’anomalies et Risk Center), ainsi que des interfaces différenciées selon le rôle utilisateur. »

---

*Document généré pour le portfolio StockShield — usage entretien / candidature.*
