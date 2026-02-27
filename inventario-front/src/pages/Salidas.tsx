import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getSalidas } from '../api/salidas';
import type { SalidaResponse } from '../types/salida';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { usePermisos } from '../hooks/usePermisos';
import { Separator } from '../components/ui/separator';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import {
    Dialog, DialogContent, DialogHeader, DialogTitle
} from '../components/ui/dialog';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import { Plus, Eye, Pencil, X } from 'lucide-react';
import { formatearFecha } from '../lib/utils';

const tipoBadge = (tipo: string) => {
    switch (tipo) {
        case 'CIRUGIA': return 'bg-purple-100 text-purple-700';
        case 'ESTUDIO': return 'bg-blue-100 text-blue-700';
        case 'VENTA': return 'bg-green-100 text-green-700';
        default: return 'bg-gray-100 text-gray-700';
    }
};

const Salidas = () => {
    const navigate = useNavigate();

    const [page, setPage] = useState(0);
    const [filtroProducto, setFiltroProducto] = useState('');
    const [filtroTipo, setFiltroTipo] = useState('');
    const [filtroDestino, setFiltroDestino] = useState('');
    const [filtroFechaInicio, setFiltroFechaInicio] = useState('');
    const [filtroFechaFin, setFiltroFechaFin] = useState('');
    const [detalleOpen, setDetalleOpen] = useState(false);
    const [salidaSeleccionada, setSalidaSeleccionada] = useState<SalidaResponse | null>(null);
    const { puedeRegistrar, puedeEditar } = usePermisos();

    const { data: salidasData, isLoading } = useQuery({
        queryKey: ['salidas', page, filtroProducto, filtroTipo, filtroDestino, filtroFechaInicio, filtroFechaFin],
        queryFn: () => getSalidas({
            producto: filtroProducto || undefined,
            tipo: filtroTipo || undefined,
            destino: filtroDestino || undefined,
            fechaInicio: filtroFechaInicio || undefined,
            fechaFin: filtroFechaFin || undefined,
            page,
            size: 20
        }).then(r => r.data)
    });

    const abrirDetalle = (salida: SalidaResponse) => {
        setSalidaSeleccionada(salida);
        setDetalleOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-semibold text-gray-800">Salidas</h1>
                {puedeRegistrar && (
                    <Button onClick={() => navigate('/salidas/registrar')} className="gap-2">
                        <Plus size={16} />
                        Nueva salida
                    </Button>
                )}
            </div>

            {/* Filtros */}
            <div className="flex gap-3 flex-wrap">
                <div className="relative w-72">
                    <Input
                        placeholder="Producto..."
                        value={filtroProducto}
                        onChange={e => { setFiltroProducto(e.target.value); setPage(0); }}
                        className="pr-8"
                    />
                    {filtroProducto && (
                        <button
                            className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-700"
                            onClick={() => { setFiltroProducto(''); setPage(0); }}
                        >
                            <X size={14} />
                        </button>
                    )}
                </div>
                <Select value={filtroTipo} onValueChange={v => { setFiltroTipo(v === 'TODOS' ? '' : v); setPage(0); }}>
                    <SelectTrigger className="w-40">
                        <SelectValue placeholder="Tipo" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="TODOS">Todos</SelectItem>
                        <SelectItem value="VENTA">Venta</SelectItem>
                        <SelectItem value="CIRUGIA">Cirugía</SelectItem>
                        <SelectItem value="ESTUDIO">Estudio</SelectItem>
                    </SelectContent>
                </Select>
                <Select value={filtroDestino} onValueChange={v => { setFiltroDestino(v === 'TODOS' ? '' : v); setPage(0); }}>
                    <SelectTrigger className="w-40">
                        <SelectValue placeholder="Destino" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="TODOS">Todos</SelectItem>
                        <SelectItem value="PARTICULAR">Particular</SelectItem>
                        <SelectItem value="ASEGURADORA">Aseguradora</SelectItem>
                    </SelectContent>
                </Select>
                <Input type="date" value={filtroFechaInicio}
                    onChange={e => { setFiltroFechaInicio(e.target.value); setPage(0); }}
                    className="w-40" />
                <Input type="date" value={filtroFechaFin}
                    onChange={e => { setFiltroFechaFin(e.target.value); setPage(0); }}
                    className="w-40" />
            </div>

            {/* Tabla */}
            <div className="border rounded-lg bg-white">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Fecha</TableHead>
                            <TableHead>Tipo</TableHead>
                            <TableHead>Destino</TableHead>
                            <TableHead>Total</TableHead>
                            <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={5} className="text-center text-gray-400 py-8">Cargando...</TableCell>
                            </TableRow>
                        ) : salidasData?.content.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={5} className="text-center text-gray-400 py-8">No se encontraron salidas</TableCell>
                            </TableRow>
                        ) : salidasData?.content.map(salida => (
                            <TableRow key={salida.idSalida}>
                                <TableCell>{formatearFecha(salida.fecha)}</TableCell>
                                <TableCell className="w-24">
                                    <Badge className={tipoBadge(salida.tipo)}>{salida.tipo}</Badge>
                                </TableCell>
                                <TableCell>{salida.destino}</TableCell>
                                <TableCell>${salida.total?.toFixed(2) ?? '0.00'}</TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-1">
                                        {puedeEditar && salida && (
                                            <Button variant="ghost" size="icon" onClick={() => navigate(`/salidas/editar/${salida.idSalida}`)}>
                                                <Pencil size={16} />
                                            </Button>
                                        )}
                                        <Button variant="ghost" size="icon" onClick={() => abrirDetalle(salida)}>
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
            {salidasData && salidasData.totalPages > 1 && (
                <div className="flex justify-end gap-2">
                    <Button variant="outline" disabled={page === 0} onClick={() => setPage(p => p - 1)}>Anterior</Button>
                    <span className="text-sm text-gray-500 self-center">Página {page + 1} de {salidasData.totalPages}</span>
                    <Button variant="outline" disabled={salidasData.last} onClick={() => setPage(p => p + 1)}>Siguiente</Button>
                </div>
            )}

            {/* Dialog detalle */}
            <Dialog open={detalleOpen} onOpenChange={setDetalleOpen}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Detalle de salida</DialogTitle>
                    </DialogHeader>
                    {salidaSeleccionada && (
                        <div className="space-y-4">
                            <div className="grid grid-cols-2 gap-4 text-sm">
                                <div>
                                    <p className="text-gray-500">Fecha</p>
                                    <p className="font-medium">{formatearFecha(salidaSeleccionada.fecha)}</p>
                                </div>
                                <div>
                                    <p className="text-gray-500">Tipo</p>
                                    <Badge className={tipoBadge(salidaSeleccionada.tipo)}>{salidaSeleccionada.tipo}</Badge>
                                </div>
                                <div>
                                    <p className="text-gray-500">Destino</p>
                                    <p className="font-medium">{salidaSeleccionada.destino}</p>
                                </div>
                                <div>
                                    <p className="text-gray-500">Total</p>
                                    <p className="font-medium">${salidaSeleccionada.total?.toFixed(2) ?? '0.00'}</p>
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
                                        <TableHead>Restante</TableHead>
                                        <TableHead>Total</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {salidaSeleccionada.productos.map(p => (
                                        <TableRow key={p.idSalidaProducto}>
                                            <TableCell>{p.nombreProducto}</TableCell>
                                            <TableCell>{p.marca}</TableCell>
                                            <TableCell className="font-mono text-sm">{p.lote}</TableCell>
                                            <TableCell>{formatearFecha(p.fechaCaducidad)}</TableCell>
                                            <TableCell>{p.cantidad}</TableCell>
                                            <TableCell>{p.cantidadRestante}</TableCell>
                                            <TableCell>${p.costoTotal?.toFixed(2) ?? '0.00'}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    );
};

export default Salidas;