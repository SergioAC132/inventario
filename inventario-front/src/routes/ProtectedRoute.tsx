import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface Props {
    roles?: string[];
}

const ProtectedRoute = ({ roles }: Props) => {
    const { isAuthenticated, usuario } = useAuth();

    if (!isAuthenticated) return <Navigate to="/login" replace />;

    if (roles && usuario && !roles.includes(usuario.rol)) {
        return <Navigate to="/unauthorized" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;