import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { canAccess } from '../config/roles';

interface RoleRouteProps {
  path: string;
  children: React.ReactNode;
}

export default function RoleRoute({ path, children }: RoleRouteProps) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (!canAccess(user.role, path)) {
    return <Navigate to="/" replace />;
  }
  return <>{children}</>;
}
