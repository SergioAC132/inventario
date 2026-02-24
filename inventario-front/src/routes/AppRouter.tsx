import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import { useAuth } from '../context/AuthContext';
import Layout from '../components/Layout';
import Login from '../pages/Login'
import Usuarios from '../pages/Usuarios';
import Productos from '../pages/Productos';

const AppRouter = () => {
    const { isAuthenticated } = useAuth();

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/unauthorized" element={<div>Sin permisos</div>} />

                <Route element={<ProtectedRoute />}>
                    <Route element={<Layout />}>
                        <Route path="/dashboard" element={<div>Dashboard</div>} />
                        <Route path="/compras" element={<div>Compras</div>} />
                        <Route path="/salidas" element={<div>Salidas</div>} />
                        <Route path="/productos" element={<Productos />} />


                        <Route element={<ProtectedRoute roles={['ADMIN']} />}>
                            <Route path="/usuarios" element={<Usuarios />} />
                        </Route>
                    </Route>
                </Route>

                <Route path="*" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />} />
            </Routes>
        </BrowserRouter>
    );
};

export default AppRouter;