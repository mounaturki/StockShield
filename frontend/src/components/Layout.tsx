import { ReactNode } from 'react';

interface LayoutProps {
  title: string;
  subtitle?: string;
  children: ReactNode;
  actions?: ReactNode;
}

export default function Layout({ title, subtitle, children, actions }: LayoutProps) {
  return (
    <div className="main-content">
      <header className="top-header">
        <div>
          <h1 className="page-title">{title}</h1>
          {subtitle && <p className="page-subtitle">{subtitle}</p>}
        </div>
        {actions && <div className="header-actions">{actions}</div>}
      </header>
      <div className="page-content">{children}</div>
    </div>
  );
}
