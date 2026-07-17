import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { registrarCompra } from '../api/compras';
import { getDoctores } from '../api/doctores';
import { getProductos } from '../api/productos';
import type { CompraProductoRequest } from '../types/compra';
import type { DoctorResponse } from '../types/doctor';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Separator } from '../components/ui/separator';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import { Popover, PopoverContent, PopoverTrigger } from '../components/ui/popover';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '../components/ui/command';
import { Plus, Trash2, ChevronLeft, ChevronsUpDown, Check, Pencil } from 'lucide-react';
import DoctorDialog from '../components/DoctorDialog';
import ProductoDialog from '../components/ProductoDialog';
import MarcaDialog from '../components/MarcaDialog';

const EMPTY_PRODUCTO_ROW: CompraProductoRequest = {
    productoId: 0,
    lote: '',
    fechaCaducidad: '',
    cantidad: 1,
    subtotal: 0,
    costoTotal: 0,
};

const RegistrarIngresoDoctor = () => {
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // Form principal
    const [doctorId, setDoctorId] = useState<number>(0);
    const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0]);
    const [productos, setProductos] = useState<CompraProductoRequest[]>([{ ...EMPTY_PRODUCTO_ROW }]);
    const [nota, setNota] = useState('');
    const [error, setError] = useState('');
    const [rowErrors, setRowErrors] = useState<Record<number, Set<string>>>({});

    // Popover doctor
    const [doctorOpen, setDoctorOpen] = useState(false);
    const [doctorSearch, setDoctorSearch] = useState('');

    // Popover producto por fila
    const [productoOpen, setProductoOpen] = useState<number | null>(null);

    // Dialogs
    const [doctorDialogOpen, setDoctorDialogOpen] = useState(false);
    const [doctorEditar, setDoctorEditar] = useState<DoctorResponse | null>(null);
    const [productoDialogOpen, setProductoDialogOpen] = useState(false);
    const [productoRowIndex, setProductoRowIndex] = useState<number>(0);
    const [marcaDialogOpen, setMarcaDialogOpen] = useState(false);

    // Queries
    const { data: doctoresData } = useQuery({
        queryKey: ['doctores', doctorSearch],
        queryFn: () => getDoctores({ nombre: doctorSearch || undefined, size: 50 }).then(r => r.data.content)
    });

    const { data: productosData } = useQuery({
        queryKey: ['productos'],
        queryFn: () => getProductos({ size: 100 }).then(r => r.data.content)
    });

    const registrarMutation = useMutation({
        mutationFn: registrarCompra,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['compras'] });
            navigate('/compras');
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error')
    });

    const fieldErr = (i: number, campo: string) =>
        rowErrors[i]?.has(campo) ? 'border-red-500 focus-visible:ring-red-500' : '';

    const actualizarProducto = (index: number, campo: keyof CompraProductoRequest, valor: any) => {
        const nuevos = [...productos];
        nuevos[index] = { ...nuevos[index], [campo]: valor };
        if (campo === 'subtotal') {
            nuevos[index].costoTotal = Number(valor);
        }
        setProductos(nuevos);
        if (rowErrors[index]?.has(campo)) {
            const updated = { ...rowErrors, [index]: new Set(rowErrors[index]) };
            updated[index].delete(campo);
            setRowErrors(updated);
        }
    };

    const agregarFila = () => setProductos([...productos, { ...EMPTY_PRODUCTO_ROW }]);

    const eliminarFila = (index: number) => {
        if (productos.length === 1) return;
        setProductos(productos.filter((_, i) => i !== index));
    };

    const handleSubmit = () => {
        if (!doctorId) { setError('Selecciona un doctor'); return; }
        const newRowErrors: Record<number, Set<string>> = {};
        productos.forEach((p, i) => {
            const fields = new Set<string>();
            if (!p.productoId) fields.add('productoId');
            if (!p.lote) fields.add('lote');
            if (!p.fechaCaducidad) fields.add('fechaCaducidad');
            if (!p.cantidad) fields.add('cantidad');
            if (!p.subtotal) fields.add('subtotal');
            if (fields.size > 0) newRowErrors[i] = fields;
        });
        if (Object.keys(newRowErrors).length > 0) {
            setRowErrors(newRowErrors);
            setError('Completa todos los campos de los productos');
            return;
        }
        setRowErrors({});
        setError('');
        registrarMutation.mutate({ doctorId, fecha, estatus: 'REGISTRADA', productos, nota: nota || undefined });
    };

    const doctorSeleccionado = doctoresData?.find(d => d.idDoctor === doctorId);

    return (
        <div className="space-y-6 max-w-5xl mx-auto">
            {/* Header */}
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" onClick={() => navigate('/compras')}>
                    <ChevronLeft size={20} />
                </Button>
                <h1 className="text-2xl font-semibold text-gray-800">Ingreso de doctor</h1>
            </div>

            <div className="bg-white border rounded-lg p-4 md:p-6 space-y-6">
                {/* Datos generales */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <Label>Doctor</Label>
                        <div className="flex gap-2">
                            <Popover open={doctorOpen} onOpenChange={setDoctorOpen}>
                                <PopoverTrigger asChild>
                                    <Button variant="outline" className="flex-1 justify-between font-normal">
                                        {doctorSeleccionado ? doctorSeleccionado.nombre : 'Selecciona un doctor'}
                                        <ChevronsUpDown size={14} className="text-gray-400" />
                                    </Button>
                                </PopoverTrigger>
                                <PopoverContent className="w-[300px] p-0">
                                    <Command>
                                        <CommandInput
                                            placeholder="Buscar doctor..."
                                            value={doctorSearch}
                                            onValueChange={setDoctorSearch}
                                        />
                                        <CommandList className="max-h-60 overflow-y-auto" onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                            <CommandEmpty>No se encontraron doctores</CommandEmpty>
                                            <CommandGroup>
                                                {doctoresData?.map(d => (
                                                    <CommandItem
                                                        key={d.idDoctor}
                                                        value={d.nombre}
                                                        onSelect={() => { setDoctorId(d.idDoctor); setDoctorOpen(false); }}
                                                    >
                                                        <Check size={14} className={doctorId === d.idDoctor ? 'opacity-100' : 'opacity-0'} />
                                                        <span>{d.nombre}</span>
                                                    </CommandItem>
                                                ))}
                                            </CommandGroup>
                                        </CommandList>
                                    </Command>
                                </PopoverContent>
                            </Popover>
                            <Button variant="outline" size="icon" onClick={() => { setDoctorEditar(null); setDoctorDialogOpen(true); }}>
                                <Plus size={16} />
                            </Button>
                            <Button variant="outline" size="icon" disabled={!doctorSeleccionado}
                                onClick={() => { setDoctorEditar(doctorSeleccionado ?? null); setDoctorDialogOpen(true); }}>
                                <Pencil size={16} />
                            </Button>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label>Fecha</Label>
                        <Input type="date" value={fecha} onChange={e => setFecha(e.target.value)} />
                    </div>
                </div>

                <Separator />

                {/* Productos */}
                <div className="space-y-3">
                    <div className="flex items-center justify-between">
                        <h2 className="font-medium text-gray-700">Productos</h2>
                        <div className="flex gap-2">
                            <Button variant="outline" size="sm" className="gap-2 text-red-500 hover:text-red-700"
                                onClick={() => setProductos([{ ...EMPTY_PRODUCTO_ROW }])}>
                                <Trash2 size={14} />
                                Limpiar
                            </Button>
                            <Button variant="outline" size="sm" onClick={agregarFila} className="gap-2">
                                <Plus size={14} />
                                Agregar producto
                            </Button>
                        </div>
                    </div>
                    <div className="max-h-80 overflow-y-auto overflow-x-auto border rounded-md">
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Producto</TableHead>
                                    <TableHead>Lote</TableHead>
                                    <TableHead>Caducidad</TableHead>
                                    <TableHead>Cantidad</TableHead>
                                    <TableHead>Valor del producto</TableHead>
                                    <TableHead></TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {productos.map((prod, index) => (
                                    <TableRow key={index}>
                                        <TableCell className="min-w-[200px]">
                                            <div className="flex gap-1">
                                                <Popover open={productoOpen === index} onOpenChange={open => setProductoOpen(open ? index : null)}>
                                                    <PopoverTrigger asChild>
                                                        <Button variant="outline" className={`flex-1 justify-between font-normal text-sm h-8 ${fieldErr(index, 'productoId')}`}>
                                                            {productosData?.find(p => p.idProducto === prod.productoId)?.nombre || 'Seleccionar'}
                                                            <ChevronsUpDown size={12} className="text-gray-400" />
                                                        </Button>
                                                    </PopoverTrigger>
                                                    <PopoverContent className="w-[250px] p-0">
                                                        <Command>
                                                            <div className="flex items-center border-b px-2">
                                                                <CommandInput placeholder="Buscar producto..." className="flex-1 border-0 focus:ring-0" />
                                                                <button
                                                                    className="text-gray-400 hover:text-gray-700 p-1 rounded"
                                                                    onClick={(e) => {
                                                                        e.stopPropagation();
                                                                        setProductoRowIndex(index);
                                                                        setProductoOpen(null);
                                                                        setProductoDialogOpen(true);
                                                                    }}
                                                                >
                                                                    <Plus size={14} />
                                                                </button>
                                                            </div>
                                                            <CommandList className="max-h-80 overflow-y-auto" onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                                <CommandEmpty>No se encontraron productos</CommandEmpty>
                                                                <CommandGroup>
                                                                    {productosData?.map(p => (
                                                                        <CommandItem
                                                                            key={p.idProducto}
                                                                            value={`${p.nombre} ${p.codigo} ${p.nombreMarca}`}
                                                                            onSelect={() => { actualizarProducto(index, 'productoId', p.idProducto); setProductoOpen(null); }}
                                                                        >
                                                                            <Check size={12} className={prod.productoId === p.idProducto ? 'opacity-100' : 'opacity-0'} />
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
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <Input className={`h-8 text-sm min-w-[130px] ${fieldErr(index, 'lote')}`} value={prod.lote}
                                                onChange={e => actualizarProducto(index, 'lote', e.target.value)} />
                                        </TableCell>
                                        <TableCell>
                                            <Input className={`h-8 text-sm w-36 ${fieldErr(index, 'fechaCaducidad')}`} type="date" value={prod.fechaCaducidad}
                                                onChange={e => actualizarProducto(index, 'fechaCaducidad', e.target.value)} />
                                        </TableCell>
                                        <TableCell>
                                            <Input className={`h-8 text-sm w-20 ${fieldErr(index, 'cantidad')}`} type="number" min={1} value={prod.cantidad || ''}
                                                onChange={e => actualizarProducto(index, 'cantidad', Number(e.target.value))}
                                                onFocus={e => e.target.select()} placeholder='0' />
                                        </TableCell>
                                        <TableCell>
                                            <Input className={`h-8 text-sm w-24 ${fieldErr(index, 'subtotal')}`} type="number" min={0} value={prod.subtotal || ''}
                                                onChange={e => actualizarProducto(index, 'subtotal', Number(e.target.value))}
                                                onFocus={e => e.target.select()} placeholder="0.00" />
                                        </TableCell>
                                        <TableCell>
                                            <Button variant="ghost" size="icon" className="h-8 w-8 text-red-400 hover:text-red-600"
                                                onClick={() => eliminarFila(index)}>
                                                <Trash2 size={14} />
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
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

                {/* Total y acciones */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <div>
                        {error && <p className="text-sm text-red-500">{error}</p>}
                    </div>
                    <div className="flex items-center gap-6">
                        <Button onClick={handleSubmit} disabled={registrarMutation.isPending} className="px-8">
                            {registrarMutation.isPending ? 'Registrando...' : 'Registrar ingreso'}
                        </Button>
                    </div>
                </div>
            </div>

            <DoctorDialog
                open={doctorDialogOpen}
                onOpenChange={setDoctorDialogOpen}
                onCreado={setDoctorId}
                doctor={doctorEditar}
            />

            <ProductoDialog
                open={productoDialogOpen}
                onOpenChange={setProductoDialogOpen}
                onCreado={(idProducto) => {
                    const nuevos = [...productos];
                    nuevos[productoRowIndex].productoId = idProducto;
                    setProductos(nuevos);
                }}
            />

            <MarcaDialog open={marcaDialogOpen} onOpenChange={setMarcaDialogOpen} />
        </div>
    );
};

export default RegistrarIngresoDoctor;
