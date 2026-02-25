import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getCompras, cancelarCompra } from '../api/compras';
import type { CompraResponse, EstatusCompra } from '../types/compra';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { Separator } from '../components/ui/separator';
import { formatearFecha } from '../lib/utils';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import {
    Dialog, DialogContent, DialogHeader, DialogTitle
} from '../components/ui/dialog';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import { Plus, Eye, Ban, Pencil } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const estatusBadge = (estatus: EstatusCompra) => {
    switch (estatus) {
        case 'REGISTRADA': return 'secondary';
        case 'FACTURADA': return 'default';
        case 'CANCELADA': return 'destructive';
    }
};

const Compras = () => {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { usuario } = useAuth();

    const [page, setPage] = useState(0);
    const [filtroProveedor, setFiltroProveedor] = useState('');
    const [filtroEstatus, setFiltroEstatus] = useState('');
    const [filtroFechaInicio, setFiltroFechaInicio] = useState('');
    const [filtroFechaFin, setFiltroFechaFin] = useState('');
    const [detalleOpen, setDetalleOpen] = useState(false);
    const [compraSeleccionada, setCompraSeleccionada] = useState<CompraResponse | null>(null);

    const { data: comprasData, isLoading } = useQuery({
        queryKey: ['compras', page, filtroProveedor, filtroEstatus, filtroFechaInicio, filtroFechaFin],
        queryFn: () => getCompras({
            proveedor: filtroProveedor || undefined,
            estatus: filtroEstatus || undefined,
            fechaInicio: filtroFechaInicio || undefined,
            fechaFin: filtroFechaFin || undefined,
            page,
            size: 20
        }).then(r => r.data)
    });

    const cancelarMutation = useMutation({
        mutationFn: cancelarCompra,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['compras'] });
            setDetalleOpen(false);
        }
    });

    const abrirDetalle = (compra: CompraResponse) => {
        setCompraSeleccionada(compra);
        setDetalleOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold text-gray-800">Compras</h1>
                <Button onClick={() => navigate('/compras/registrar')} className="gap-2">
                    <Plus size={16} />
                    Nueva compra
                </Button>
            </div>

            {/* Filtros */}
            <div className="flex gap-3 flex-wrap">
                <Input
                    placeholder="Proveedor..."
                    value={filtroProveedor}
                    onChange={e => { setFiltroProveedor(e.target.value); setPage(0); }}
                    className="max-w-xs"
                />
                <Select value={filtroEstatus} onValueChange={v => { setFiltroEstatus(v === 'TODOS' ? '' : v); setPage(0); }}>
                    <SelectTrigger className="w-40">
                        <SelectValue placeholder="Estatus" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="TODOS">Todos</SelectItem>
                        <SelectItem value="REGISTRADA">Registrada</SelectItem>
                        <SelectItem value="FACTURADA">Facturada</SelectItem>
                        <SelectItem value="CANCELADA">Cancelada</SelectItem>
                    </SelectContent>
                </Select>
                <Input
                    type="date"
                    value={filtroFechaInicio}
                    onChange={e => { setFiltroFechaInicio(e.target.value); setPage(0); }}
                    className="w-40"
                />
                <Input
                    type="date"
                    value={filtroFechaFin}
                    onChange={e => { setFiltroFechaFin(e.target.value); setPage(0); }}
                    className="w-40"
                />
            </div>

            {/* Tabla */}
            <div className="border rounded-lg bg-white">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Fecha</TableHead>
                            <TableHead>Proveedor</TableHead>
                            <TableHead>Factura</TableHead>
                            <TableHead>Estatus</TableHead>
                            <TableHead>Total</TableHead>
                            <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center text-gray-400 py-8">Cargando...</TableCell>
                            </TableRow>
                        ) : comprasData?.content.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center text-gray-400 py-8">No se encontraron compras</TableCell>
                            </TableRow>
                        ) : comprasData?.content.map(compra => (
                            <TableRow key={compra.idCompra}>
                                <TableCell>{formatearFecha(compra.fecha)}</TableCell>
                                <TableCell>{compra.proveedor}</TableCell>
                                <TableCell className="font-mono text-sm">{compra.numeroFactura}</TableCell>
                                <TableCell>
                                    <Badge variant={estatusBadge(compra.estatus)}>
                                        {compra.estatus}
                                    </Badge>
                                </TableCell>
                                <TableCell>${compra.total.toFixed(2)}</TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-1">
                                        {compra.estatus !== 'CANCELADA' && (
                                            <Button variant="ghost" size="icon" onClick={() => navigate(`/compras/editar/${compra.idCompra}`)}>
                                                <Pencil size={16} />
                                            </Button>
                                        )}
                                        <Button variant="ghost" size="icon" onClick={() => abrirDetalle(compra)}>
                                            <Eye size={16} />
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            {/* Paginación */}
            {comprasData && comprasData.totalPages > 1 && (
                <div className="flex justify-end gap-2">
                    <Button variant="outline" disabled={page === 0} onClick={() => setPage(p => p - 1)}>Anterior</Button>
                    <span className="text-sm text-gray-500 self-center">Página {page + 1} de {comprasData.totalPages}</span>
                    <Button variant="outline" disabled={comprasData.last} onClick={() => setPage(p => p + 1)}>Siguiente</Button>
                </div>
            )}

            {/* Dialog detalle */}
            <Dialog open={detalleOpen} onOpenChange={setDetalleOpen}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Detalle de compra</DialogTitle>
                    </DialogHeader>
                    {compraSeleccionada && (
                        <div className="space-y-4">
                            <div className="grid grid-cols-2 gap-4 text-sm">
                                <div>
                                    <p className="text-gray-500">Proveedor</p>
                                    <p className="font-medium">{compraSeleccionada.proveedor}</p>
                                </div>
                                <div>
                                    <p className="text-gray-500">Factura</p>
                                    <p className="font-mono font-medium">{compraSeleccionada.numeroFactura}</p>
                                </div>
                                <div>
                                    <p className="text-gray-500">Fecha</p>
                                    <p className="font-medium">{formatearFecha(compraSeleccionada.fecha)}</p>
                                </div>
                                <div>
                                    <p className="text-gray-500">Estatus</p>
                                    <Badge variant={estatusBadge(compraSeleccionada.estatus)}>
                                        {compraSeleccionada.estatus}
                                    </Badge>
                                </div>
                            </div>

                            <Separator />

                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Producto</TableHead>
                                        <TableHead>Marca</TableHead>
                                        <TableHead>Lote</TableHead>
                                        <TableHead>Caducidad</TableHead>
                                        <TableHead>Cantidad</TableHead>
                                        <TableHead>Total</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {compraSeleccionada.productos.map(p => (
                                        <TableRow key={p.idCompraProducto}>
                                            <TableCell>{p.nombreProducto}</TableCell>
                                            <TableCell>{p.marca}</TableCell>
                                            <TableCell className="font-mono text-sm">{p.lote}</TableCell>
                                            <TableCell>{p.fechaCaducidad}</TableCell>
                                            <TableCell>{p.cantidad}</TableCell>
                                            <TableCell>${p.costoTotal.toFixed(2)}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>

                            <Separator />

                            <div className="flex items-center justify-between">
                                <div className="flex gap-2">
                                    {usuario?.rol === 'ADMIN' && compraSeleccionada.estatus !== 'CANCELADA' && (
                                        <Button
                                            variant="destructive"
                                            size="sm"
                                            className="gap-2"
                                            onClick={() => cancelarMutation.mutate(compraSeleccionada.idCompra)}
                                            disabled={cancelarMutation.isPending}
                                        >
                                            <Ban size={14} />
                                            Cancelar compra
                                        </Button>
                                    )}
                                </div>
                                <p className="text-lg font-semibold">
                                    Total: ${compraSeleccionada.total.toFixed(2)}
                                </p>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default Compras;