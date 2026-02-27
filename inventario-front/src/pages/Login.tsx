import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Stethoscope } from 'lucide-react';

const Login = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            await login(username, password);
            navigate('/dashboard');
        } catch {
            setError('Usuario o contraseña incorrectos');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex" style={{ background: 'hsl(210, 60%, 18%)' }}>
            {/* Panel izquierdo */}
            <div className="hidden lg:flex w-1/2 flex-col justify-between p-12"
                style={{ background: 'hsl(210, 60%, 14%)' }}>
                <div className="flex items-center gap-2">
                    <img
                        src="/logo.png"
                        alt="Logo"
                        className="h-auto w-40 object-contain"
                    />
                </div>
                <div className="space-y-4">
                    <h1 className="text-4xl font-bold text-white leading-tight">
                        Control de inventario<br />
                    </h1>
                    <p className="text-white/50 text-sm leading-relaxed max-w-sm">
                        Gestiona compras, salidas y stock de insumos médicos desde un solo lugar.
                    </p>
                </div>
                <p className="text-white/20 text-xs"></p>
            </div>

            {/* Panel derecho */}
            <div className="flex-1 flex items-center justify-center p-8">
                <div className="w-full max-w-sm space-y-8">
                    <div className="space-y-2">
                        <h2 className="text-2xl font-bold text-white">Bienvenido</h2>
                        <p className="text-white/40 text-sm">Ingresa tus credenciales para continuar</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label className="text-white/70 text-sm">Usuario</Label>
                            <Input
                                type="text"
                                value={username}
                                onChange={e => setUsername(e.target.value)}
                                placeholder="usuario"
                                required
                                className="border-white/10 text-white placeholder:text-white/20"
                                style={{ background: 'hsl(210, 60%, 22%)' }}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label className="text-white/70 text-sm">Contraseña</Label>
                            <Input
                                type="password"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                placeholder="••••••••"
                                required
                                className="border-white/10 text-white placeholder:text-white/20"
                                style={{ background: 'hsl(210, 60%, 22%)' }}
                            />
                        </div>
                        {error && <p className="text-red-400 text-sm">{error}</p>}
                        <Button
                            type="submit"
                            className="w-full font-medium"
                            disabled={loading}
                            style={{ background: 'hsl(205, 70%, 50%)' }}
                        >
                            {loading ? 'Ingresando...' : 'Ingresar'}
                        </Button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Login;