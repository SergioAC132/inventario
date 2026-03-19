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
    const [desktopCollapsed, setDesktopCollapsed] = useState(false);
    const [mobileOpen, setMobileOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const itemsVisibles = navItems.filter(item =>
        usuario?.rol && item.roles.includes(usuario.rol)
    );

    return (
        <div className="flex h-screen" style={{ background: 'hsl(210, 20%, 96%)' }}>

            {/* ── Desktop Sidebar ── */}
            <aside
                className={`hidden md:flex flex-col ${desktopCollapsed ? 'w-16' : 'w-60'} transition-all duration-300 shadow-lg flex-shrink-0`}
                style={{ background: 'hsl(210, 60%, 18%)' }}
            >
                {/* Logo */}
                <div className="flex items-center justify-between px-4 py-5 border-b border-white/10">
                    {!desktopCollapsed && (
                        <img src="/logo.png" alt="Logo" className="h-auto w-30 object-contain" />
                    )}
                    <button
                        onClick={() => setDesktopCollapsed(!desktopCollapsed)}
                        className="text-white/50 hover:text-white transition-colors p-1 rounded"
                    >
                        {desktopCollapsed ? <Menu size={18} /> : <X size={18} />}
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
                            {!desktopCollapsed && <span>{item.label}</span>}
                        </NavLink>
                    ))}
                </nav>

                {/* Footer */}
                <div className="px-2 py-4 border-t border-white/10">
                    {!desktopCollapsed && (
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
                        {!desktopCollapsed && <span>Cerrar sesión</span>}
                    </button>
                </div>
            </aside>

            {/* ── Mobile: backdrop ── */}
            {mobileOpen && (
                <div
                    className="md:hidden fixed inset-0 z-40 bg-black/50"
                    onClick={() => setMobileOpen(false)}
                />
            )}

            {/* ── Mobile: drawer ── */}
            <aside
                className={`md:hidden fixed inset-y-0 left-0 z-50 w-64 flex flex-col transition-transform duration-300 shadow-xl ${
                    mobileOpen ? 'translate-x-0' : '-translate-x-full'
                }`}
                style={{ background: 'hsl(210, 60%, 18%)' }}
            >
                <div className="flex items-center justify-between px-4 py-5 border-b border-white/10">
                    <img src="/logo.png" alt="Logo" className="h-auto w-28 object-contain" />
                    <button
                        onClick={() => setMobileOpen(false)}
                        className="text-white/50 hover:text-white transition-colors p-1 rounded"
                    >
                        <X size={18} />
                    </button>
                </div>

                <nav className="flex-1 px-2 py-4 space-y-1">
                    {itemsVisibles.map(item => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            onClick={() => setMobileOpen(false)}
                            className={({ isActive }) =>
                                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${
                                    isActive
                                        ? 'bg-white/15 text-white font-medium'
                                        : 'text-white/60 hover:bg-white/10 hover:text-white'
                                }`
                            }
                        >
                            <item.icon size={17} />
                            <span>{item.label}</span>
                        </NavLink>
                    ))}
                </nav>

                <div className="px-2 py-4 border-t border-white/10">
                    <div className="px-3 py-2 mb-2">
                        <p className="text-xs text-white/40 truncate">{usuario?.username}</p>
                        <p className="text-xs text-white/60 font-medium">{usuario?.rol}</p>
                    </div>
                    <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-white/60 hover:bg-white/10 hover:text-white transition-all"
                    >
                        <LogOut size={17} />
                        <span>Cerrar sesión</span>
                    </button>
                </div>
            </aside>

            {/* ── Mobile: top bar ── */}
            <div
                className="md:hidden fixed top-0 left-0 right-0 z-30 flex items-center justify-between px-4 py-3 shadow-md"
                style={{ background: 'hsl(210, 60%, 18%)' }}
            >
                <button
                    onClick={() => setMobileOpen(true)}
                    className="text-white/70 hover:text-white p-1 rounded"
                >
                    <Menu size={22} />
                </button>
                <img src="/logo.png" alt="Logo" className="h-7 w-auto object-contain" />
                <div className="w-8" />
            </div>

            {/* ── Main content ── */}
            <main className="flex-1 overflow-auto p-4 pt-20 md:pt-4 md:p-8">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
