import { useAuth } from '../context/AuthContext';

export const usePermisos = () => {
    const { usuario } = useAuth();
    const rol = usuario?.rol;

    return {
        puedeRegistrar: ['ADMIN', 'EDITOR', 'CAPTURISTA'].includes(rol || ''),
        puedeEditar: ['ADMIN', 'EDITOR'].includes(rol || ''),
        puedeCancelar: rol === 'ADMIN',
        puedeEliminarSalida: rol === 'ADMIN',
        puedeGestionarUsuarios: rol === 'ADMIN',
    };
};