import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Compras from '../pages/Compras';
import EditarCompra from '../pages/EditarCompra';
import Layout from '../components/Layout';
import Login from '../pages/Login';
import Productos from '../pages/Productos';
import ProtectedRoute from './ProtectedRoute';
import RegistrarCompra from '../pages/RegistrarCompra';
import RegistrarSalida from '../pages/RegistrarSalida';
import Usuarios from '../pages/Usuarios';

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
                        <Route path="/compras" element={<Compras />} />
                        <Route path="/compras/registrar" element={<RegistrarCompra />} />
                        <Route path="/compras/editar/:idCompra" element={<EditarCompra />} />
                        <Route path="/salidas" element={<div>Salidas</div>} />
                        <Route path="/salidas/registrar" element={<RegistrarSalida />} />
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