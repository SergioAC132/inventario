import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getProductos, crearProducto, editarProducto } from '../api/productos';
import { getMarcas, crearMarca, editarMarca } from '../api/marcas';
import { modificarStock } from '../api/inventarios';
import type { ProductoRequest, ProductoResponse } from '../types/producto';
import type { MarcaResponse } from '../types/marca';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { usePermisos } from '../hooks/usePermisos';
import { Badge } from '../components/ui/badge';
import type { MarcaRequest } from '../api/marcas';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import {
    Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter
} from '../components/ui/dialog';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import {
    Collapsible, CollapsibleContent, CollapsibleTrigger
} from '../components/ui/collapsible';
import { PackagePlus, Pencil, ChevronDown, ChevronRight, Tags, Plus, Minus } from 'lucide-react';
import MarcaCombobox from '../components/MarcaCombobox';


const EMPTY_FORM: ProductoRequest = {
    codigo: '',
    nombre: '',
    idMarca: 0,
};

const Productos = () => {
    const queryClient = useQueryClient();
    const [page, setPage] = useState(0);
    const [filtroNombre, setFiltroNombre] = useState('');
    const [filtroMarca, setFiltroMarca] = useState('');
    const [filtroCodigo, setFiltroCodigo] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editando, setEditando] = useState<ProductoResponse | null>(null);
    const [form, setForm] = useState<ProductoRequest>(EMPTY_FORM);
    const [error, setError] = useState('');
    const [expandido, setExpandido] = useState<number | null>(null);
    const [marcaDialogOpen, setMarcaDialogOpen] = useState(false);
    const [editandoMarca, setEditandoMarca] = useState<MarcaResponse | null>(null);
    const [marcaForm, setMarcaForm] = useState<MarcaRequest>({ nombre: '' });
    const [marcaError, setMarcaError] = useState('');
    const { puedeRegistrar, puedeEditar } = usePermisos();

    const { data: productosData, isLoading } = useQuery({
        queryKey: ['productos', page, filtroNombre, filtroMarca, filtroCodigo],
        queryFn: () => getProductos({
            nombre: filtroNombre || undefined,
            marca: filtroMarca || undefined,
            codigo: filtroCodigo || undefined,
            page,
            size: 20
        }).then(r => r.data)
    });

    const { data: marcasData } = useQuery({
        queryKey: ['marcas'],
        queryFn: () => getMarcas({ size: 100 }).then(r => r.data.content)
    });

    const mutationOpts = {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['productos'] });
            setDialogOpen(false);
            setForm(EMPTY_FORM);
            setEditando(null);
            setError('');
        },
        onError: (e: any) => {
            setError(e.response?.data?.message || 'Ocurrió un error');
        }
    };

    const crearMutation = useMutation({ mutationFn: crearProducto, ...mutationOpts });
    const editarMutation = useMutation({ mutationFn: editarProducto, ...mutationOpts });

    const marcaMutationOpts = {
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['marcas'] });
            setMarcaDialogOpen(false);
            setMarcaForm({ nombre: '' });
            setEditandoMarca(null);
            setMarcaError('');
        },
        onError: (e: any) => {
            setMarcaError(e.response?.data?.message || 'Ocurrió un error');
        }
    };

    const crearMarcaMutation = useMutation({ mutationFn: crearMarca, ...marcaMutationOpts });
    const editarMarcaMutation = useMutation({ mutationFn: editarMarca, ...marcaMutationOpts });

    const modificarStockMutation = useMutation({
        mutationFn: ({ idInventario, modificacion }: { idInventario: number; modificacion: boolean }) =>
            modificarStock(idInventario, modificacion),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ['productos'] })
    });

    const abrirCrear = () => {
        setEditando(null);
        setForm(EMPTY_FORM);
        setError('');
        setDialogOpen(true);
    };

    const abrirCrearMarca = () => {
        setEditandoMarca(null);
        setMarcaForm({ nombre: '' });
        setMarcaError('');
        setMarcaDialogOpen(true);
    };

    const abrirEditarMarca = (marca: MarcaResponse) => {
        setEditandoMarca(marca);
        setMarcaForm({ idMarca: marca.idMarca, nombre: marca.nombre });
        setMarcaError('');
        setMarcaDialogOpen(true);
    };

    const handleSubmitMarca = () => {
        if (!marcaForm.nombre) {
            setMarcaError('El nombre es obligatorio');
            return;
        }
        if (editandoMarca) {
            editarMarcaMutation.mutate(marcaForm);
        } else {
            crearMarcaMutation.mutate(marcaForm);
        }
    };

    const abrirEditar = (producto: ProductoResponse) => {
        setEditando(producto);
        setForm({
            idProducto: producto.idProducto,
            codigo: producto.codigo,
            nombre: producto.nombre,
            idMarca: marcasData?.find((m: MarcaResponse) => m.nombre === producto.nombreMarca)?.idMarca || 0,
        });
        setError('');
        setDialogOpen(true);
    };

    const handleSubmit = () => {
        if (!form.codigo || !form.nombre || !form.idMarca) {
            setError('Todos los campos son obligatorios');
            return;
        }
        if (editando) {
            editarMutation.mutate(form);
        } else {
            crearMutation.mutate(form);
        }
    };

    const stockColor = (stock: number) => {
        if (stock === 0) return 'destructive';
        if (stock < 20) return 'secondary';
        return 'default';
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold text-gray-800">Productos</h1>
                <div className="flex gap-2">
                    {puedeRegistrar && (
                        <Button variant="outline" onClick={abrirCrearMarca} className="gap-2">
                            <Tags size={16} />
                            Nueva marca
                        </Button>
                    )}
                    {puedeRegistrar && (
                        <Button onClick={abrirCrear} className="gap-2">
                            <PackagePlus size={16} />
                            Nuevo producto
                        </Button>
                    )}
                </div>
            </div>

            {/* Filtros */}
            <div className="flex gap-3">
                <Input
                    placeholder="Buscar por nombre..."
                    value={filtroNombre}
                    onChange={e => { setFiltroNombre(e.target.value); setPage(0); }}
                    className="max-w-xs"
                />
                <Input
                    placeholder="Código..."
                    value={filtroCodigo}
                    onChange={e => { setFiltroCodigo(e.target.value); setPage(0); }}
                    className="max-w-xs"
                />
                <Input
                    placeholder="Marca..."
                    value={filtroMarca}
                    onChange={e => { setFiltroMarca(e.target.value); setPage(0); }}
                    className="max-w-xs"
                />
            </div>

            {/* Tabla */}
            <div className="border rounded-lg bg-white">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead className="w-8"></TableHead>
                            <TableHead>Código</TableHead>
                            <TableHead>Nombre</TableHead>
                            <TableHead>Marca</TableHead>
                            <TableHead>Stock total</TableHead>
                            <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center text-gray-400 py-8">
                                    Cargando...
                                </TableCell>
                            </TableRow>
                        ) : productosData?.content.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center text-gray-400 py-8">
                                    No se encontraron productos
                                </TableCell>
                            </TableRow>
                        ) : productosData?.content.map(producto => (
                            <Collapsible
                                key={producto.idProducto}
                                open={expandido === producto.idProducto}
                                onOpenChange={open => setExpandido(open ? producto.idProducto : null)}
                                asChild
                            >
                                <>
                                    <TableRow>
                                        <TableCell>
                                            <CollapsibleTrigger asChild>
                                                <button className="text-gray-400 hover:text-gray-700">
                                                    {expandido === producto.idProducto
                                                        ? <ChevronDown size={16} />
                                                        : <ChevronRight size={16} />
                                                    }
                                                </button>
                                            </CollapsibleTrigger>
                                        </TableCell>
                                        <TableCell className="font-mono text-sm">{producto.codigo}</TableCell>
                                        <TableCell className="font-medium">{producto.nombre}</TableCell>
                                        <TableCell>{producto.nombreMarca}</TableCell>
                                        <TableCell>
                                            <Badge variant={stockColor(producto.stockTotal)}>
                                                {producto.stockTotal}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            {puedeRegistrar && (
                                                <Button variant="ghost" size="icon" onClick={() => abrirEditar(producto)}>
                                                    <Pencil size={16} />
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                    <CollapsibleContent asChild>
                                        <TableRow className="bg-gray-50 hover:bg-gray-50">
                                            <TableCell colSpan={6} className="p-0">
                                                <div className="px-8 py-3">
                                                    <p className="text-xs font-semibold text-gray-500 uppercase mb-2">Lotes</p>
                                                    <table className="w-full text-sm">
                                                        <thead>
                                                            <tr className="text-gray-500 text-xs">
                                                                <th className="text-left font-medium pb-1">Lote</th>
                                                                <th className="text-left font-medium pb-1">Caducidad</th>
                                                                <th className="text-left font-medium pb-1">Disponible</th>
                                                                <th className="text-left font-medium pb-1">Estatus</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            {producto.inventarios.map(inv => (
                                                                <tr key={inv.idInventario} className="border-t border-gray-100">
                                                                    <td className="py-1 font-mono">{inv.lote}</td>
                                                                    <td className="py-1">{inv.fechaCaducidad}</td>
                                                                    <td className="py-1">
                                                                        {inv.active && inv.cantidadDisponible > 0 ? (
                                                                            <div className="flex items-center gap-2">
                                                                                <button
                                                                                    onClick={() => modificarStockMutation.mutate({ idInventario: inv.idInventario, modificacion: false })}
                                                                                    className="text-gray-400 hover:text-red-500 transition-colors"
                                                                                >
                                                                                    <Minus size={14} />
                                                                                </button>
                                                                                <span className="w-8 text-center">{inv.cantidadDisponible}</span>
                                                                                <button
                                                                                    onClick={() => modificarStockMutation.mutate({ idInventario: inv.idInventario, modificacion: true })}
                                                                                    className="text-gray-400 hover:text-green-500 transition-colors"
                                                                                >
                                                                                    <Plus size={14} />
                                                                                </button>
                                                                            </div>
                                                                        ) : (
                                                                            <span className="w-8 text-center">{inv.cantidadDisponible}</span>
                                                                        )}
                                                                    </td>
                                                                    <td className="py-1">
                                                                        <Badge variant={inv.active ? 'default' : 'secondary'} className="text-xs">
                                                                            {inv.active ? 'Activo' : 'Inactivo'}
                                                                        </Badge>
                                                                    </td>
                                                                </tr>
                                                            ))}
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    </CollapsibleContent>
                                </>
                            </Collapsible>
                        ))}
                    </TableBody>
                </Table>
            </div>

            {/* Paginación */}
            {productosData && productosData.totalPages > 1 && (
                <div className="flex justify-end gap-2">
                    <Button variant="outline" disabled={page === 0} onClick={() => setPage(p => p - 1)}>
                        Anterior
                    </Button>
                    <span className="text-sm text-gray-500 self-center">
                        Página {page + 1} de {productosData.totalPages}
                    </span>
                    <Button variant="outline" disabled={productosData.last} onClick={() => setPage(p => p + 1)}>
                        Siguiente
                    </Button>
                </div>
            )}

            {/* Dialog */}
            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editando ? 'Editar producto' : 'Nuevo producto'}</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4 py-2">
                        <div className="space-y-2">
                            <Label>Código</Label>
                            <Input
                                value={form.codigo}
                                onChange={e => setForm({ ...form, codigo: e.target.value })}
                                placeholder="PRD001"
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Nombre</Label>
                            <Input
                                value={form.nombre}
                                onChange={e => setForm({ ...form, nombre: e.target.value })}
                                placeholder="Nombre del producto"
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Marca</Label>
                            <MarcaCombobox
                                marcas={marcasData || []}
                                value={form.idMarca}
                                onChange={v => setForm({ ...form, idMarca: v })}
                                onEditarMarca={abrirEditarMarca}
                            />
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
            <Dialog open={marcaDialogOpen} onOpenChange={setMarcaDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editandoMarca ? 'Editar marca' : 'Nueva marca'}</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4 py-2">
                        <div className="space-y-2">
                            <Label>Nombre</Label>
                            <Input
                                value={marcaForm.nombre}
                                onChange={e => setMarcaForm({ ...marcaForm, nombre: e.target.value })}
                                placeholder="Nombre de la marca"
                            />
                        </div>
                        {marcaError && <p className="text-sm text-red-500">{marcaError}</p>}
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setMarcaDialogOpen(false)}>Cancelar</Button>
                        <Button onClick={handleSubmitMarca} disabled={crearMarcaMutation.isPending || editarMarcaMutation.isPending}>
                            {crearMarcaMutation.isPending || editarMarcaMutation.isPending ? 'Guardando...' : 'Guardar'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default Productos;