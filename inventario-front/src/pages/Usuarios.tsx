import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getUsuarios, crearUsuario, editarUsuario, cambiarEstadoUsuario } from '../api/usuarios';
import { getRoles } from '../api/roles';
import type { UsuarioRequest, UsuarioResponse } from '../types/usuario';
import type { RolResponse } from '../types/rol';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Badge } from '../components/ui/badge';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import {
    Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter
} from '../components/ui/dialog';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import { UserPlus, Pencil, UserX, UserCheck } from 'lucide-react';

const EMPTY_FORM: UsuarioRequest = {
    username: '',
    password: '',
    idRol: 0,
};

const Usuarios = () => {
    const queryClient = useQueryClient();
    const [page, setPage] = useState(0);
    const [filtroUsername, setFiltroUsername] = useState('');
    const [filtroRol, setFiltroRol] = useState('');
    const [filtroEnabled, setFiltroEnabled] = useState<boolean | undefined>(undefined);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editando, setEditando] = useState<UsuarioResponse | null>(null);
    const [form, setForm] = useState<UsuarioRequest>(EMPTY_FORM);
    const [error, setError] = useState('');

    const { data: usuariosData, isLoading } = useQuery({
        queryKey: ['usuarios', page, filtroUsername, filtroRol, filtroEnabled],
        queryFn: () => getUsuarios({
            username: filtroUsername || undefined,
            rol: filtroRol || undefined,
            enabled: filtroEnabled,
            page,
            size: 20
        }).then(r => r.data)
    });

    const { data: rolesData } = useQuery({
        queryKey: ['roles'],
        queryFn: () => getRoles().then(r => r.data.data)
    });

    const mutationOpts = {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['usuarios'] });
            setDialogOpen(false);
            setForm(EMPTY_FORM);
            setEditando(null);
            setError('');
        },
        onError: (e: any) => {
            setError(e.response?.data?.message || 'Ocurrió un error');
        }
    };

    const crearMutation = useMutation({
        mutationFn: crearUsuario,
        ...mutationOpts
    });

    const editarMutation = useMutation({
        mutationFn: editarUsuario,
        ...mutationOpts
    });

    const cambiarEstadoMutation = useMutation({
        mutationFn: cambiarEstadoUsuario,
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ['usuarios'] })
    });
    const abrirCrear = () => {
        setEditando(null);
        setForm(EMPTY_FORM);
        setError('');
        setDialogOpen(true);
    };

    const abrirEditar = (usuario: UsuarioResponse) => {
        setEditando(usuario);
        setForm({
            idUsuario: usuario.idUsuario,
            username: usuario.username,
            password: '',
            idRol: rolesData?.find((r: RolResponse) => r.nombre === usuario.rol)?.idRol || 0,
        });
        setError('');
        setDialogOpen(true);
    };

    const handleSubmit = () => {
        if (!form.username || !form.idRol) {
            setError('Username y rol son obligatorios');
            return;
        }
        if (!editando && !form.password) {
            setError('La contraseña es obligatoria al crear un usuario');
            return;
        }
        if (editando) {
            editarMutation.mutate(form);
        } else {
            crearMutation.mutate(form);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold text-gray-800">Usuarios</h1>
                <Button onClick={abrirCrear} className="gap-2">
                    <UserPlus size={16} />
                    Nuevo usuario
                </Button>
            </div>

            {/* Filtros */}
            <div className="flex gap-3">
                <Input
                    placeholder="Buscar por username..."
                    value={filtroUsername}
                    onChange={e => { setFiltroUsername(e.target.value); setPage(0); }}
                    className="max-w-xs"
                />
                <Select value={filtroRol} onValueChange={v => { setFiltroRol(v === 'TODOS' ? '' : v); setPage(0); }}>
                    <SelectTrigger className="w-40">
                        <SelectValue placeholder="Rol" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="TODOS">Todos</SelectItem>
                        {rolesData?.map((r: RolResponse) => (
                            <SelectItem key={r.idRol} value={r.nombre}>{r.nombre}</SelectItem>
                        ))}
                    </SelectContent>
                </Select>
                <Select
                    value={filtroEnabled === undefined ? 'TODOS' : filtroEnabled ? 'ACTIVO' : 'INACTIVO'}
                    onValueChange={v => {
                        setFiltroEnabled(v === 'TODOS' ? undefined : v === 'ACTIVO');
                        setPage(0);
                    }}
                >
                    <SelectTrigger className="w-40">
                        <SelectValue placeholder="Estatus" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="TODOS">Todos</SelectItem>
                        <SelectItem value="ACTIVO">Activo</SelectItem>
                        <SelectItem value="INACTIVO">Inactivo</SelectItem>
                    </SelectContent>
                </Select>
            </div>

            {/* Tabla */}
            <div className="border rounded-lg bg-white">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Username</TableHead>
                            <TableHead>Rol</TableHead>
                            <TableHead>Estatus</TableHead>
                            <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={4} className="text-center text-gray-400 py-8">
                                    Cargando...
                                </TableCell>
                            </TableRow>
                        ) : usuariosData?.content.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} className="text-center text-gray-400 py-8">
                                    No se encontraron usuarios
                                </TableCell>
                            </TableRow>
                        ) : usuariosData?.content.map(usuario => (
                            <TableRow key={usuario.idUsuario}>
                                <TableCell className="font-medium">{usuario.username}</TableCell>
                                <TableCell>{usuario.rol}</TableCell>
                                <TableCell>
                                    <Badge variant={usuario.enabled ? 'default' : 'secondary'}>
                                        {usuario.enabled ? 'Activo' : 'Inactivo'}
                                    </Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-2">
                                        <Button variant="ghost" size="icon" onClick={() => abrirEditar(usuario)}>
                                            <Pencil size={16} />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            className={usuario.enabled ? 'text-red-500 hover:text-red-700' : 'text-green-500 hover:text-green-700'}
                                            onClick={() => cambiarEstadoMutation.mutate(usuario.idUsuario)}
                                        >
                                            {usuario.enabled ? <UserX size={16} /> : <UserCheck size={16} />}
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            {/* Paginación */}
            {usuariosData && usuariosData.totalPages > 1 && (
                <div className="flex justify-end gap-2">
                    <Button variant="outline" disabled={page === 0} onClick={() => setPage(p => p - 1)}>
                        Anterior
                    </Button>
                    <span className="text-sm text-gray-500 self-center">
                        Página {page + 1} de {usuariosData.totalPages}
                    </span>
                    <Button variant="outline" disabled={usuariosData.last} onClick={() => setPage(p => p + 1)}>
                        Siguiente
                    </Button>
                </div>
            )}

            {/* Dialog crear/editar */}
            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editando ? 'Editar usuario' : 'Nuevo usuario'}</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4 py-2">
                        <div className="space-y-2">
                            <Label>Username</Label>
                            <Input
                                value={form.username}
                                onChange={e => setForm({ ...form, username: e.target.value })}
                                placeholder="Username"
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>{editando ? 'Nueva contraseña (opcional)' : 'Contraseña'}</Label>
                            <Input
                                type="password"
                                value={form.password}
                                onChange={e => setForm({ ...form, password: e.target.value })}
                                placeholder={editando ? 'Dejar vacío para no cambiar' : 'Contraseña'}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Rol</Label>
                            <Select
                                value={form.idRol ? String(form.idRol) : ''}
                                onValueChange={v => setForm({ ...form, idRol: Number(v) })}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Selecciona un rol" />
                                </SelectTrigger>
                                <SelectContent>
                                    {rolesData?.map((r: RolResponse) => (
                                        <SelectItem key={r.idRol} value={String(r.idRol)}>
                                            {r.nombre}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        {error && <p className="text-sm text-red-500">{error}</p>}
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDialogOpen(false)}>Cancelar</Button>
                        <Button onClick={handleSubmit} disabled={crearMutation.isPending || editarMutation.isPending}>
                            {crearMutation.isPending || editarMutation.isPending ? 'Guardando...' : 'Guardar'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default Usuarios;