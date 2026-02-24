import { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    LayoutDashboard,
    ShoppingCart,
    BoxesIcon,
    Users,
    LogOut,
    Menu,
    X,
    Hospital
} from 'lucide-react';
import { Button } from './ui/button';

const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/compras', label: 'Compras', icon: ShoppingCart, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/salidas', label: 'Salidas', icon: Hospital, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/productos', label: 'Productos', icon: BoxesIcon, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/usuarios', label: 'Usuarios', icon: Users, roles: ['ADMIN'] },
];

const Layout = () => {
    const { usuario, logout } = useAuth();
    const navigate = useNavigate();
    const [sidebarOpen, setSidebarOpen] = useState(true);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const itemsVisibles = navItems.filter(item =>
        usuario?.rol && item.roles.includes(usuario.rol)
    );

    return (
        <div className="flex h-screen bg-gray-50">
            {/* Sidebar */}
            <aside className={`${sidebarOpen ? 'w-56' : 'w-16'} bg-white border-r flex flex-col transition-all duration-300`}>
                <div className="flex items-center justify-between p-4 border-b">
                    {sidebarOpen && <span className="font-semibold text-gray-800">Inventario</span>}
                    <button onClick={() => setSidebarOpen(!sidebarOpen)} className="text-gray-500 hover:text-gray-800">
                        {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
                    </button>
                </div>

                <nav className="flex-1 p-2 space-y-1">
                    {itemsVisibles.map(item => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({ isActive }) =>
                                `flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-colors ${
                                    isActive
                                        ? 'bg-gray-100 text-gray-900 font-medium'
                                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                                }`
                            }
                        >
                            <item.icon size={18} />
                            {sidebarOpen && <span>{item.label}</span>}
                        </NavLink>
                    ))}
                </nav>

                <div className="p-2 border-t">
                    {sidebarOpen && (
                        <p className="text-xs text-gray-500 px-3 py-1 truncate">{usuario?.username} · {usuario?.rol}</p>
                    )}
                    <Button variant="ghost" className="w-full justify-start gap-3 text-gray-600" onClick={handleLogout}>
                        <LogOut size={18} />
                        {sidebarOpen && <span>Cerrar sesión</span>}
                    </Button>
                </div>
            </aside>

            {/* Contenido */}
            <main className="flex-1 overflow-auto p-6">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;