import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getSalidas, editarSalida } from '../api/salidas';
import { getProductos } from '../api/productos';
import { getInventariosPorProducto } from '../api/inventarios';
import type { SalidaProductoRequest, TipoSalida, DestinoSalida } from '../types/salida';
import type { ProductoResponse } from '../types/producto';
import type { InventarioResponse } from '../types/inventario';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Separator } from '../components/ui/separator';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import { Popover, PopoverContent, PopoverTrigger } from '../components/ui/popover';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '../components/ui/command';
import { Plus, Trash2, ChevronLeft, ChevronsUpDown, Check } from 'lucide-react';

interface FilaSalida {
    productoId: number;
    idInventario: number;
    cantidad: number;
    total?: number;
}

const EditarSalida = () => {
    const navigate = useNavigate();
    const { idSalida } = useParams();
    const queryClient = useQueryClient();

    const [fecha, setFecha] = useState('');
    const [tipo, setTipo] = useState<TipoSalida>('VENTA');
    const [destino, setDestino] = useState<DestinoSalida>('PARTICULAR');
    const [filas, setFilas] = useState<FilaSalida[]>([]);
    const [error, setError] = useState('');
    const [productoOpen, setProductoOpen] = useState<number | null>(null);
    const [loteOpen, setLoteOpen] = useState<number | null>(null);
    const [inventariosPorFila, setInventariosPorFila] = useState<Record<number, InventarioResponse[]>>({});

    const { data: salidaData } = useQuery({
        queryKey: ['salida', idSalida],
        queryFn: () => getSalidas({ page: 0, size: 1000 }).then(r =>
            r.data.content.find(s => s.idSalida === Number(idSalida))
        ),
        enabled: !!idSalida
    });

    const { data: productosData } = useQuery({
        queryKey: ['productos'],
        queryFn: () => getProductos({ size: 100 }).then(r => r.data.content)
    });

    const cargarInventarios = async (index: number, productoId: number) => {
        if (productoId === 0) return;
        const result = await getInventariosPorProducto(productoId);
        const activos = result.data.content.filter(inv => inv.active && inv.cantidadDisponible > 0);
        setInventariosPorFila(prev => ({ ...prev, [index]: activos }));
    };

    useEffect(() => {
        if (salidaData && productosData) {
            setFecha(salidaData.fecha);
            setTipo(salidaData.tipo as TipoSalida);
            setDestino(salidaData.destino as DestinoSalida);

            const filasIniciales: FilaSalida[] = salidaData.productos.map(p => {
                return {
                    productoId: p.idProducto,
                    idInventario: p.idInventario,
                    cantidad: p.cantidad,
                    total: p.costoTotal,
                };
            });
            setFilas(filasIniciales);

            filasIniciales.forEach((fila, index) => {
                cargarInventarios(index, fila.productoId);
            });
        }
    }, [salidaData, productosData]);

    const editarMutation = useMutation({
        mutationFn: editarSalida,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['salidas'] });
            navigate('/salidas');
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error')
    });

    const actualizarFila = (index: number, campo: keyof FilaSalida, valor: any) => {
        const nuevas = [...filas];
        if (campo === 'productoId') {
            nuevas[index] = { productoId: valor, idInventario: 0, cantidad: 1 };
            cargarInventarios(index, valor);
        } else if (campo === 'idInventario') {
            const yaSeleccionado = filas.some((f, i) => i !== index && f.idInventario === valor);
            if (yaSeleccionado) {
                setError('Este lote ya fue seleccionado en otra fila');
                setLoteOpen(null);
                return;
            }
            setError('');
            nuevas[index] = { ...nuevas[index], idInventario: valor };
            setLoteOpen(null);
        } else {
            nuevas[index] = { ...nuevas[index], [campo]: valor };
        }
        setFilas(nuevas);
    };

    const agregarFila = () => setFilas([...filas, { productoId: 0, idInventario: 0, cantidad: 1 }]);

    const eliminarFila = (index: number) => {
        if (filas.length === 1) return;
        setFilas(filas.filter((_, i) => i !== index));
        setInventariosPorFila(prev => {
            const nuevo: Record<number, InventarioResponse[]> = {};
            Object.keys(prev).forEach(key => {
                const k = Number(key);
                if (k < index) nuevo[k] = prev[k];
                else if (k > index) nuevo[k - 1] = prev[k];
            });
            return nuevo;
        });
    };

    const totalSalida = filas.reduce((acc, f) => acc + (f.total || 0), 0);
    const totalConIva = parseFloat((totalSalida * 1.16).toFixed(2));

    const calcularUtilidad = (fila: FilaSalida, index: number): number | null => {
        const inv = (inventariosPorFila[index] || []).find(i => i.idInventario === fila.idInventario);
        if (!inv || fila.total === undefined) return null;
        return fila.total - inv.costoUnitario * fila.cantidad;
    };

    const utilidadTotal = filas.reduce((acc, f, i) => {
        const u = calcularUtilidad(f, i);
        return u !== null ? acc + u : acc;
    }, 0);

    const handleSubmit = () => {
        if (!fecha) { setError('La fecha es obligatoria'); return; }
        if (filas.some(f => !f.productoId || !f.idInventario || f.cantidad < 1)) {
            setError('Completa todos los campos de los productos');
            return;
        }
        setError('');
        const productos: SalidaProductoRequest[] = filas.map(f => ({
            idInventario: f.idInventario,
            cantidad: f.cantidad,
            total: f.total,
        }));
        editarMutation.mutate({ idSalida: Number(idSalida), fecha, tipo, destino, productos });
    };

    return (
        <div className="space-y-6 max-w-5xl mx-auto">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" onClick={() => navigate('/salidas')}>
                    <ChevronLeft size={20} />
                </Button>
                <h1 className="text-2xl font-semibold text-gray-800">Editar salida</h1>
            </div>

            <div className="bg-white border rounded-lg p-4 md:p-6 space-y-6">
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div className="space-y-2">
                        <Label>Fecha</Label>
                        <Input type="date" value={fecha} onChange={e => setFecha(e.target.value)} />
                    </div>
                    <div className="space-y-2">
                        <Label>Tipo</Label>
                        <Select value={tipo} onValueChange={v => setTipo(v as TipoSalida)}>
                            <SelectTrigger><SelectValue /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="VENTA">Venta</SelectItem>
                                <SelectItem value="CIRUGIA">Cirugía</SelectItem>
                                <SelectItem value="ESTUDIO">Estudio</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div className="space-y-2">
                        <Label>Destino</Label>
                        <Select value={destino} onValueChange={v => setDestino(v as DestinoSalida)}>
                            <SelectTrigger><SelectValue /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="PARTICULAR">Particular</SelectItem>
                                <SelectItem value="ASEGURADORA">Aseguradora</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                <Separator />

                <div className="space-y-3">
                    <div className="flex items-center justify-between">
                        <h2 className="font-medium text-gray-700">Productos</h2>
                        <div className="flex gap-2">
                            <Button variant="outline" size="sm" className="gap-2 text-red-500 hover:text-red-700"
                                onClick={() => { setFilas([{ productoId: 0, idInventario: 0, cantidad: 1 }]); setInventariosPorFila({}); }}>
                                <Trash2 size={14} />
                                Limpiar
                            </Button>
                            <Button variant="outline" size="sm" onClick={agregarFila} className="gap-2">
                                <Plus size={14} />
                                Agregar producto
                            </Button>
                        </div>
                    </div>

                    <div className="max-h-72 overflow-y-auto overflow-x-auto border rounded-md">
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Producto</TableHead>
                                    <TableHead>Lote</TableHead>
                                    <TableHead>Disponible</TableHead>
                                    <TableHead>Costo unit.</TableHead>
                                    <TableHead>Cantidad</TableHead>
                                    <TableHead>Total venta</TableHead>
                                    <TableHead>Utilidad</TableHead>
                                    <TableHead></TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {filas.map((fila, index) => {
                                    const inventarios = inventariosPorFila[index] || [];
                                    const invSeleccionado = inventarios.find(i => i.idInventario === fila.idInventario);

                                    return (
                                        <TableRow key={index}>
                                            <TableCell className="min-w-[200px]">
                                                <Popover open={productoOpen === index} onOpenChange={open => setProductoOpen(open ? index : null)}>
                                                    <PopoverTrigger asChild>
                                                        <Button variant="outline" className="w-full justify-between font-normal text-sm h-8">
                                                            {productosData?.find(p => p.idProducto === fila.productoId)?.nombre || 'Seleccionar'}
                                                            <ChevronsUpDown size={12} className="text-gray-400" />
                                                        </Button>
                                                    </PopoverTrigger>
                                                    <PopoverContent className="w-[250px] p-0">
                                                        <Command>
                                                            <CommandInput placeholder="Buscar producto..." />
                                                            <CommandList className="max-h-52 overflow-y-auto"
                                                                onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                                <CommandEmpty>No se encontraron productos</CommandEmpty>
                                                                <CommandGroup>
                                                                    {productosData?.map((p: ProductoResponse) => (
                                                                        <CommandItem key={p.idProducto}
                                                                            value={`${p.nombre} ${p.codigo} ${p.nombreMarca}`}
                                                                            onSelect={() => { actualizarFila(index, 'productoId', p.idProducto); setProductoOpen(null); }}>
                                                                            <Check size={12} className={fila.productoId === p.idProducto ? 'opacity-100' : 'opacity-0'} />
                                                                            <div className="flex flex-col">
                                                                                <span className="text-sm">{p.nombre}</span>
                                                                                <span className="text-xs text-gray-400">{p.codigo} · {p.nombreMarca}</span>
                                                                            </div>
                                                                        </CommandItem>
                                                                    ))}
                                                                </CommandGroup>
                                                            </CommandList>
                                                        </Command>
                                                    </PopoverContent>
                                                </Popover>
                                            </TableCell>
                                            <TableCell className="min-w-[150px]">
                                                <Popover open={loteOpen === index} onOpenChange={open => setLoteOpen(open ? index : null)}>
                                                    <PopoverTrigger asChild>
                                                        <Button variant="outline" className="w-full justify-between font-normal text-sm h-8"
                                                            disabled={fila.productoId === 0}>
                                                            {invSeleccionado ? invSeleccionado.lote : 'Seleccionar'}
                                                            <ChevronsUpDown size={12} className="text-gray-400" />
                                                        </Button>
                                                    </PopoverTrigger>
                                                    <PopoverContent className="w-[200px] p-0">
                                                        <Command>
                                                            <CommandInput placeholder="Buscar lote..." />
                                                            <CommandList className="max-h-52 overflow-y-auto"
                                                                onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                                <CommandEmpty>No hay lotes disponibles</CommandEmpty>
                                                                <CommandGroup>
                                                                    {inventarios.map((inv: InventarioResponse) => (
                                                                        <CommandItem key={inv.idInventario}
                                                                            value={inv.lote}
                                                                            onSelect={() => actualizarFila(index, 'idInventario', inv.idInventario)}>
                                                                            <Check size={12} className={fila.idInventario === inv.idInventario ? 'opacity-100' : 'opacity-0'} />
                                                                            <div className="flex flex-col">
                                                                                <span className="text-sm">{inv.lote}</span>
                                                                                <span className="text-xs text-gray-400">Vence: {inv.fechaCaducidad} · {inv.cantidadDisponible} uds</span>
                                                                            </div>
                                                                        </CommandItem>
                                                                    ))}
                                                                </CommandGroup>
                                                            </CommandList>
                                                        </Command>
                                                    </PopoverContent>
                                                </Popover>
                                            </TableCell>
                                            <TableCell className="text-sm text-gray-500">
                                                {invSeleccionado ? invSeleccionado.cantidadDisponible : '-'}
                                            </TableCell>
                                            <TableCell className="text-sm text-gray-500">
                                                {invSeleccionado ? `$${invSeleccionado.costoUnitario.toFixed(2)}` : '-'}
                                            </TableCell>
                                            <TableCell>
                                                <Input className="h-8 text-sm w-20" type="number" min={1}
                                                    max={invSeleccionado?.cantidadDisponible}
                                                    value={fila.cantidad}
                                                    onChange={e => actualizarFila(index, 'cantidad', Number(e.target.value))} />
                                            </TableCell>
                                            <TableCell>
                                                <Input className="h-8 text-sm w-24" type="number" min={0}
                                                    value={fila.total || ''}
                                                    onChange={e => actualizarFila(index, 'total', e.target.value ? Number(e.target.value) : undefined)}
                                                    placeholder="0.00" />
                                            </TableCell>
                                            <TableCell className="text-sm font-medium">
                                                {(() => {
                                                    const u = calcularUtilidad(fila, index);
                                                    if (u === null) return <span className="text-gray-400">-</span>;
                                                    return <span className={u >= 0 ? 'text-green-600' : 'text-red-500'}>${u.toFixed(2)}</span>;
                                                })()}
                                            </TableCell>
                                            <TableCell>
                                                <Button variant="ghost" size="icon" className="h-8 w-8 text-red-400 hover:text-red-600"
                                                    onClick={() => eliminarFila(index)}>
                                                    <Trash2 size={14} />
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    </div>
                </div>

                <Separator />

                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <div>{error && <p className="text-sm text-red-500">{error}</p>}</div>
                    <div className="flex items-center gap-6">
                        <div className="text-right">
                            <p className="text-xs text-gray-500">Utilidad</p>
                            <p className={`text-lg font-semibold ${utilidadTotal >= 0 ? 'text-green-600' : 'text-red-500'}`}>
                                ${utilidadTotal.toFixed(2)}
                            </p>
                        </div>
                        <div className="text-right">
                            <p className="text-xs text-gray-500">Subtotal</p>
                            <p className="text-lg font-semibold text-gray-600">${totalSalida.toFixed(2)}</p>
                        </div>
                        <div className="text-right">
                            <p className="text-sm text-gray-500">Total (IVA 16%)</p>
                            <p className="text-2xl font-semibold text-gray-800">${totalConIva.toFixed(2)}</p>
                        </div>
                        <Button onClick={handleSubmit} disabled={editarMutation.isPending} className="px-8">
                            {editarMutation.isPending ? 'Guardando...' : 'Guardar cambios'}
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EditarSalida;