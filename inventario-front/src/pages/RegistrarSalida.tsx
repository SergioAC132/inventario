import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { registrarSalida } from '../api/salidas';
import { getProductos } from '../api/productos';
import { getInventariosPorProducto } from '../api/inventarios';
import { getDestinatarios } from '../api/destinatarios';
import type { SalidaProductoRequest, TipoSalida, DestinoSalida } from '../types/salida';
import type { ProductoResponse } from '../types/producto';
import type { InventarioResponse } from '../types/inventario';
import type { DestinatarioResponse } from '../types/destinatario';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Separator } from '../components/ui/separator';
import {
    Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/ui/select';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import { Popover, PopoverContent, PopoverTrigger } from '../components/ui/popover';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '../components/ui/command';
import { Plus, Trash2, ChevronLeft, ChevronsUpDown, Check, Pencil } from 'lucide-react';
import DestinatarioDialog from '../components/DestinatarioDialog';

interface FilaSalida {
    productoId: number;
    idInventario: number;
    cantidad: number;
    total?: number;
}

const EMPTY_FILA: FilaSalida = {
    productoId: 0,
    idInventario: 0,
    cantidad: 1,
    total: undefined,
};

const RegistrarSalida = () => {
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0]);
    const [tipo, setTipo] = useState<TipoSalida>('VENTA');
    const [destino, setDestino] = useState<DestinoSalida>('PARTICULAR');
    const [identificador, setIdentificador] = useState<'NINGUNO' | 'FOLIO' | 'DESTINATARIO'>('NINGUNO');
    const [folio, setFolio] = useState('');
    const [destinatarioId, setDestinatarioId] = useState<number>(0);
    const [destinatarioOpen, setDestinatarioOpen] = useState(false);
    const [destinatarioSearch, setDestinatarioSearch] = useState('');
    const [destinatarioDialogOpen, setDestinatarioDialogOpen] = useState(false);
    const [destinatarioEditar, setDestinatarioEditar] = useState<DestinatarioResponse | null>(null);
    const [filas, setFilas] = useState<FilaSalida[]>([{ ...EMPTY_FILA }]);
    const [nota, setNota] = useState('');
    const [error, setError] = useState('');
    const [productoOpen, setProductoOpen] = useState<number | null>(null);
    const [loteOpen, setLoteOpen] = useState<number | null>(null);

    const { data: productosData } = useQuery({
        queryKey: ['productos'],
        queryFn: () => getProductos({ size: 100 }).then(r => r.data.content)
    });

    const { data: destinatariosData } = useQuery({
        queryKey: ['destinatarios', destinatarioSearch],
        queryFn: () => getDestinatarios({ nombre: destinatarioSearch || undefined, size: 50 }).then(r => r.data.content)
    });

    const destinatarioSeleccionado = destinatariosData?.find(d => d.idDestinatario === destinatarioId);

    // Query de inventarios por producto para cada fila
    const [inventariosPorFila, setInventariosPorFila] = useState<Record<number, InventarioResponse[]>>({});

    const cargarInventarios = async (index: number, productoId: number) => {
        if (productoId === 0) return;
        const result = await getInventariosPorProducto(productoId);
        const activos = result.data.content.filter(inv => inv.active && inv.cantidadDisponible > 0);
        setInventariosPorFila(prev => ({ ...prev, [index]: activos }));
    };

    const registrarMutation = useMutation({
        mutationFn: registrarSalida,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['salidas'] });
            navigate('/salidas');
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error')
    });

    const actualizarFila = (index: number, campo: keyof FilaSalida, valor: any) => {
        const nuevas = [...filas];
        if (campo === 'productoId') {
            nuevas[index] = { ...EMPTY_FILA, productoId: valor };
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

    const agregarFila = () => setFilas([...filas, { ...EMPTY_FILA }]);

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
        registrarMutation.mutate({
            fecha,
            tipo,
            destino,
            folio: identificador === 'FOLIO' && folio ? Number(folio) : undefined,
            destinatarioId: identificador === 'DESTINATARIO' && destinatarioId ? destinatarioId : undefined,
            productos,
            nota: nota || undefined,
        });
    };

    return (
        <div className="space-y-6 max-w-5xl mx-auto">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" onClick={() => navigate('/salidas')}>
                    <ChevronLeft size={20} />
                </Button>
                <h1 className="text-2xl font-semibold text-gray-800">Registrar salida</h1>
            </div>

            <div className="bg-white border rounded-lg p-4 md:p-6 space-y-6">
                {/* Datos generales */}
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

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div className="space-y-2">
                        <Label>Identificador (opcional)</Label>
                        <Select value={identificador} onValueChange={v => {
                            setIdentificador(v as typeof identificador);
                            setFolio('');
                            setDestinatarioId(0);
                        }}>
                            <SelectTrigger><SelectValue /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="NINGUNO">Ninguno</SelectItem>
                                <SelectItem value="FOLIO">Folio</SelectItem>
                                <SelectItem value="DESTINATARIO">Destinatario</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    {identificador === 'FOLIO' && (
                        <div className="space-y-2">
                            <Label>Folio</Label>
                            <Input type="number" value={folio} onChange={e => setFolio(e.target.value)} placeholder="Número de folio" />
                        </div>
                    )}
                    {identificador === 'DESTINATARIO' && (
                        <div className="space-y-2">
                            <Label>Destinatario</Label>
                            <div className="flex gap-2">
                                <Popover open={destinatarioOpen} onOpenChange={setDestinatarioOpen}>
                                    <PopoverTrigger asChild>
                                        <Button variant="outline" className="flex-1 justify-between font-normal">
                                            {destinatarioSeleccionado ? destinatarioSeleccionado.nombre : 'Selecciona un destinatario'}
                                            <ChevronsUpDown size={14} className="text-gray-400" />
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-[300px] p-0">
                                        <Command>
                                            <CommandInput placeholder="Buscar destinatario..."
                                                value={destinatarioSearch} onValueChange={setDestinatarioSearch} />
                                            <CommandList className="max-h-60 overflow-y-auto"
                                                onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                <CommandEmpty>No se encontraron destinatarios</CommandEmpty>
                                                <CommandGroup>
                                                    {destinatariosData?.map(d => (
                                                        <CommandItem key={d.idDestinatario} value={d.nombre}
                                                            onSelect={() => { setDestinatarioId(d.idDestinatario); setDestinatarioOpen(false); }}>
                                                            <Check size={14} className={destinatarioId === d.idDestinatario ? 'opacity-100' : 'opacity-0'} />
                                                            {d.nombre}
                                                        </CommandItem>
                                                    ))}
                                                </CommandGroup>
                                            </CommandList>
                                        </Command>
                                    </PopoverContent>
                                </Popover>
                                <Button variant="outline" size="icon" onClick={() => { setDestinatarioEditar(null); setDestinatarioDialogOpen(true); }}>
                                    <Plus size={16} />
                                </Button>
                                <Button variant="outline" size="icon" disabled={!destinatarioSeleccionado}
                                    onClick={() => { setDestinatarioEditar(destinatarioSeleccionado ?? null); setDestinatarioDialogOpen(true); }}>
                                    <Pencil size={16} />
                                </Button>
                            </div>
                        </div>
                    )}
                </div>

                <Separator />

                {/* Productos */}
                <div className="space-y-3">
                    <div className="flex items-center justify-between">
                        <h2 className="font-medium text-gray-700">Productos</h2>
                        <div className="flex gap-2">
                            <Button variant="outline" size="sm" className="gap-2 text-red-500 hover:text-red-700"
                                onClick={() => setFilas([{ ...EMPTY_FILA }])}>
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
                                    const invSeleccionado = inventarios.find((i: InventarioResponse) => i.idInventario === fila.idInventario);

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
                                                                            onSelect={() => { actualizarFila(index, 'idInventario', inv.idInventario); setLoteOpen(null); }}>
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

                <div className="space-y-2">
                    <Label>Nota (opcional)</Label>
                    <Textarea value={nota} onChange={e => setNota(e.target.value)} placeholder="Agregar una nota..." />
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
                        <Button onClick={handleSubmit} disabled={registrarMutation.isPending} className="px-8">
                            {registrarMutation.isPending ? 'Registrando...' : 'Registrar salida'}
                        </Button>
                    </div>
                </div>
            </div>

            <DestinatarioDialog
                open={destinatarioDialogOpen}
                onOpenChange={setDestinatarioDialogOpen}
                onCreado={setDestinatarioId}
                destinatario={destinatarioEditar}
            />
        </div>
    );
};

export default RegistrarSalida;