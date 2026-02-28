import { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    LayoutDashboard,
    ShoppingCart,
    PackageOpen,
    Tags,
    Users,
    LogOut,
    Menu,
    X
} from 'lucide-react';

const navItems = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/compras', label: 'Compras', icon: ShoppingCart, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/salidas', label: 'Salidas', icon: PackageOpen, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
    { to: '/productos', label: 'Productos', icon: Tags, roles: ['ADMIN', 'EDITOR', 'CAPTURISTA', 'VISOR'] },
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
        <div className="flex h-screen" style={{ background: 'hsl(210, 20%, 96%)' }}>
            {/* Sidebar */}
            <aside
                className={`${sidebarOpen ? 'w-60' : 'w-16'} flex flex-col transition-all duration-300 shadow-lg`}
                style={{ background: 'hsl(210, 60%, 18%)' }}
            >
                {/* Logo */}
                <div className="flex items-center justify-between px-4 py-5 border-b border-white/10">
                    {sidebarOpen && (
                        <div className="flex items-center gap-2">
                            <img
                                src="/logo.png"
                                alt="Logo"
                                className="h-auto w-30 object-contain"
                            />
                        </div>
                    )}
                    <button
                        onClick={() => setSidebarOpen(!sidebarOpen)}
                        className="text-white/50 hover:text-white transition-colors p-1 rounded"
                    >
                        {sidebarOpen ? <X size={18} /> : <Menu size={18} />}
                    </button>
                </div>

                {/* Nav */}
                <nav className="flex-1 px-2 py-4 space-y-1">
                    {itemsVisibles.map(item => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({ isActive }) =>
                                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${
                                    isActive
                                        ? 'bg-white/15 text-white font-medium'
                                        : 'text-white/60 hover:bg-white/10 hover:text-white'
                                }`
                            }
                        >
                            <item.icon size={17} />
                            {sidebarOpen && <span>{item.label}</span>}
                        </NavLink>
                    ))}
                </nav>

                {/* Footer */}
                <div className="px-2 py-4 border-t border-white/10">
                    {sidebarOpen && (
                        <div className="px-3 py-2 mb-2">
                            <p className="text-xs text-white/40 truncate">{usuario?.username}</p>
                            <p className="text-xs text-white/60 font-medium">{usuario?.rol}</p>
                        </div>
                    )}
                    <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-white/60 hover:bg-white/10 hover:text-white transition-all"
                    >
                        <LogOut size={17} />
                        {sidebarOpen && <span>Cerrar sesión</span>}
                    </button>
                </div>
            </aside>

            {/* Contenido */}
            <main className="flex-1 overflow-auto p-8">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;