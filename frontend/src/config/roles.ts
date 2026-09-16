export type UserRole = 'ADMIN' | 'MAGASINIER' | 'VENDEUR' | 'SECRETAIRE';

export const ROLE_LABELS: Record<UserRole, string> = {
  ADMIN: 'Administrateur',
  MAGASINIER: 'Magasinier',
  VENDEUR: 'Vendeur',
  SECRETAIRE: 'Secrétaire',
};

export interface NavItem {
  to: string;
  label: string;
  icon: string;
  roles: UserRole[];
}

export const NAV_SECTIONS: { section: string; items: NavItem[] }[] = [
  {
    section: 'Principal',
    items: [
      { to: '/', label: 'Tableau de bord', icon: 'LayoutDashboard', roles: ['ADMIN', 'MAGASINIER', 'VENDEUR', 'SECRETAIRE'] },
      { to: '/risk-center', label: 'Risk Center', icon: 'ShieldAlert', roles: ['ADMIN'] },
      { to: '/stock-risk', label: 'Risque Stock', icon: 'TrendingDown', roles: ['MAGASINIER', 'SECRETAIRE'] },
      { to: '/alerts', label: 'Alertes', icon: 'Bell', roles: ['ADMIN', 'MAGASINIER', 'SECRETAIRE'] },
    ],
  },
  {
    section: 'Gestion',
    items: [
      { to: '/clients', label: 'Clients (Acheteurs)', icon: 'Building2', roles: ['ADMIN', 'VENDEUR', 'SECRETAIRE'] },
      { to: '/machines', label: 'Machines', icon: 'Cog', roles: ['ADMIN', 'SECRETAIRE', 'VENDEUR'] },
      { to: '/products', label: 'Produits', icon: 'Package', roles: ['ADMIN', 'MAGASINIER', 'VENDEUR', 'SECRETAIRE'] },
      { to: '/stock', label: 'Stocks', icon: 'Warehouse', roles: ['ADMIN', 'MAGASINIER'] },
      { to: '/inventory', label: 'Inventaire', icon: 'ClipboardList', roles: ['ADMIN', 'MAGASINIER', 'SECRETAIRE'] },
      { to: '/discounts', label: 'Remises', icon: 'Percent', roles: ['ADMIN', 'VENDEUR', 'SECRETAIRE'] },
    ],
  },
  {
    section: 'Sécurité',
    items: [
      { to: '/users', label: 'Utilisateurs', icon: 'Users', roles: ['ADMIN'] },
      { to: '/audit', label: "Journal d'audit", icon: 'FileText', roles: ['ADMIN'] },
    ],
  },
];

export function getNavForRole(role: string) {
  const userRole = role as UserRole;
  return NAV_SECTIONS.map((section) => ({
    ...section,
    items: section.items.filter((item) => item.roles.includes(userRole)),
  })).filter((section) => section.items.length > 0);
}

export function canAccess(role: string, path: string): boolean {
  const userRole = role as UserRole;
  for (const section of NAV_SECTIONS) {
    for (const item of section.items) {
      if (item.to === path || (path !== '/' && item.to !== '/' && path.startsWith(item.to))) {
        return item.roles.includes(userRole);
      }
    }
  }
  return path === '/';
}
