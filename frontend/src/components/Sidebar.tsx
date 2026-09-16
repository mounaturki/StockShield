import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getNavForRole, ROLE_LABELS, UserRole } from "../config/roles";
import eskimoLogo from "../assets/eskimo.webp";
import {
  LayoutDashboard,
  Users,
  Building2,
  Cog,
  Package,
  Warehouse,
  ClipboardList,
  Percent,
  Bell,
  ShieldAlert,
  FileText,
  LogOut,
  TrendingDown,
  type LucideIcon,
} from "lucide-react";

const iconMap: Record<string, LucideIcon> = {
  LayoutDashboard,
  Users,
  Building2,
  Cog,
  Package,
  Warehouse,
  ClipboardList,
  Percent,
  Bell,
  ShieldAlert,
  FileText,
  TrendingDown,
};

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const role = (user?.role || "VENDEUR") as UserRole;
  const navSections = getNavForRole(role);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <img src={eskimoLogo} alt="StockShield" className="sidebar-logo" />
        <div className="sidebar-brand">
          <h1>StockShield</h1>
          <span>Ghamem Trading</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navSections.map((section) => (
          <div key={section.section} className="nav-section">
            <div className="nav-section-title">{section.section}</div>
            {section.items.map((item) => {
              const Icon = iconMap[item.icon] || LayoutDashboard;
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === "/"}
                  className={({ isActive }) =>
                    `nav-link ${isActive ? "active" : ""}`
                  }
                >
                  <Icon />
                  {item.label}
                </NavLink>
              );
            })}
          </div>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-avatar">
            {user?.firstName?.[0]}
            {user?.lastName?.[0]}
          </div>
          <div className="user-details">
            <div className="name">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="role">{ROLE_LABELS[role]}</div>
          </div>
          <button
            className="btn-icon"
            onClick={handleLogout}
            title="Déconnexion"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </aside>
  );
}
