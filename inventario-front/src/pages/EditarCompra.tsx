import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getCompras, editarCompra } from '../api/compras';
import { getProveedores } from '../api/proveedores';
import { getDoctores } from '../api/doctores';
import { getProductos } from '../api/productos';
import type { CompraProductoRequest, EstatusCompra } from '../types/compra';
import type { ProveedorResponse } from '../types/proveedor';
import type { DoctorResponse } from '../types/doctor';
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
import ProveedorDialog from '../components/ProveedorDialog';
import DoctorDialog from '../components/DoctorDialog';
import ProductoDialog from '../components/ProductoDialog';
import MarcaDialog from '../components/MarcaDialog';

const EditarCompra = () => {
    const navigate = useNavigate();
    const { idCompra } = useParams();
    const queryClient = useQueryClient();

    const [esDoctor, setEsDoctor] = useState(false);
    const [proveedorId, setProveedorId] = useState<number>(0);
    const [doctorId, setDoctorId] = useState<number>(0);
    const [fecha, setFecha] = useState('');
    const [numeroFactura, setNumeroFactura] = useState('');
    const [estatus, setEstatus] = useState<EstatusCompra>('REGISTRADA');
    const [productos, setProductos] = useState<CompraProductoRequest[]>([]);
    const [nota, setNota] = useState('');
    const [error, setError] = useState('');
    const [rowErrors, setRowErrors] = useState<Record<number, Set<string>>>({});
    const [proveedorOpen, setProveedorOpen] = useState(false);
    const [proveedorSearch, setProveedorSearch] = useState('');
    const [doctorOpen, setDoctorOpen] = useState(false);
    const [doctorSearch, setDoctorSearch] = useState('');
    const [productoOpen, setProductoOpen] = useState<number | null>(null);

    // Dialogs
    const [proveedorDialogOpen, setProveedorDialogOpen] = useState(false);
    const [proveedorEditar, setProveedorEditar] = useState<ProveedorResponse | null>(null);
    const [doctorDialogOpen, setDoctorDialogOpen] = useState(false);
    const [doctorEditar, setDoctorEditar] = useState<DoctorResponse | null>(null);
    const [productoDialogOpen, setProductoDialogOpen] = useState(false);
    const [productoRowIndex, setProductoRowIndex] = useState<number>(0);
    const [marcaDialogOpen, setMarcaDialogOpen] = useState(false);

    const { data: compraData } = useQuery({
        queryKey: ['compra', idCompra],
        queryFn: () => getCompras({ page: 0, size: 1000 }).then(r =>
            r.data.content.find(c => c.idCompra === Number(idCompra))
        ),
        enabled: !!idCompra
    });

    const { data: proveedoresData } = useQuery({
        queryKey: ['proveedores', proveedorSearch],
        queryFn: () => getProveedores({ nombre: proveedorSearch || undefined, size: 50 }).then(r => r.data.content)
    });

    const { data: doctoresData } = useQuery({
        queryKey: ['doctores', doctorSearch],
        queryFn: () => getDoctores({ nombre: doctorSearch || undefined, size: 50 }).then(r => r.data.content)
    });

    useEffect(() => {
        if (!compraData) return;

        setEsDoctor(!!compraData.doctor);

        if (compraData.doctor) {
            const doctor = doctoresData?.find(d => d.nombre === compraData.doctor);
            setDoctorId(doctor?.idDoctor || 0);
        } else if (proveedoresData) {
            const proveedor = proveedoresData.find(p => p.nombre === compraData.proveedor);
            setProveedorId(proveedor?.idProveedor || 0);
        }

        setFecha(compraData.fecha);
        setNumeroFactura(compraData.numeroFactura || '');
        setEstatus(compraData.estatus);
        setProductos(compraData.productos.map(p => ({
            productoId: p.idProducto,
            lote: p.lote,
            fechaCaducidad: p.fechaCaducidad,
            cantidad: p.cantidad,
            subtotal: compraData.doctor ? p.costoTotal : parseFloat((p.costoTotal / 1.16).toFixed(2)),
            costoTotal: p.costoTotal,
        })));
    }, [compraData, proveedoresData, doctoresData]);

    const { data: productosData } = useQuery({
        queryKey: ['productos'],
        queryFn: () => getProductos({ size: 100 }).then(r => r.data.content)
    });

    const editarMutation = useMutation({
        mutationFn: editarCompra,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['compras'] });
            navigate('/compras');
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error')
    });

    const totalSubtotal = productos.reduce((acc, p) => acc + (p.subtotal || 0), 0);
    const totalConIva = productos.reduce((acc, p) => acc + (p.costoTotal || 0), 0);

    const fieldErr = (i: number, campo: string) =>
        rowErrors[i]?.has(campo) ? 'border-red-500 focus-visible:ring-red-500' : '';

    const actualizarProducto = (index: number, campo: keyof CompraProductoRequest, valor: any) => {
        const nuevos = [...productos];
        nuevos[index] = { ...nuevos[index], [campo]: valor };
        if (campo === 'subtotal') {
            nuevos[index].costoTotal = esDoctor ? Number(valor) : parseFloat((Number(valor) * 1.16).toFixed(2));
        }
        setProductos(nuevos);
        if (rowErrors[index]?.has(campo)) {
            const updated = { ...rowErrors, [index]: new Set(rowErrors[index]) };
            updated[index].delete(campo);
            setRowErrors(updated);
        }
    };

    const agregarFila = () => setProductos([...productos, {
        productoId: 0, lote: '', fechaCaducidad: '', cantidad: 1, subtotal: 0, costoTotal: 0
    }]);

    const eliminarFila = (index: number) => {
        if (productos.length === 1) return;
        setProductos(productos.filter((_, i) => i !== index));
    };

    const handleSubmit = () => {
        if (esDoctor) {
            if (!doctorId) { setError('Selecciona un doctor'); return; }
        } else {
            if (!proveedorId) { setError('Selecciona un proveedor'); return; }
            if (!numeroFactura) { setError('El número de factura es obligatorio'); return; }
        }
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
        editarMutation.mutate(esDoctor
            ? { idCompra: Number(idCompra), doctorId, fecha, estatus, productos, nota: nota || undefined }
            : { idCompra: Number(idCompra), proveedorId, fecha, numeroFactura, estatus, productos, nota: nota || undefined }
        );
    };

    const proveedorSeleccionado = proveedoresData?.find(p => p.idProveedor === proveedorId);
    const doctorSeleccionado = doctoresData?.find(d => d.idDoctor === doctorId);

    return (
        <div className="space-y-6 max-w-5xl mx-auto">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" onClick={() => navigate('/compras')}>
                    <ChevronLeft size={20} />
                </Button>
                <h1 className="text-2xl font-semibold text-gray-800">{esDoctor ? 'Editar ingreso de doctor' : 'Editar compra'}</h1>
            </div>

            <div className="bg-white border rounded-lg p-4 md:p-6 space-y-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    {esDoctor ? (
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
                                            <CommandInput placeholder="Buscar doctor..."
                                                value={doctorSearch} onValueChange={setDoctorSearch} />
                                            <CommandList className="max-h-60 overflow-y-auto"
                                                onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                <CommandEmpty>No se encontraron doctores</CommandEmpty>
                                                <CommandGroup>
                                                    {doctoresData?.map(d => (
                                                        <CommandItem key={d.idDoctor} value={d.nombre}
                                                            onSelect={() => { setDoctorId(d.idDoctor); setDoctorOpen(false); }}>
                                                            <Check size={14} className={doctorId === d.idDoctor ? 'opacity-100' : 'opacity-0'} />
                                                            {d.nombre}
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
                    ) : (
                        <>
                        <div className="space-y-2">
                            <Label>Proveedor</Label>
                            <div className="flex gap-2">
                                <Popover open={proveedorOpen} onOpenChange={setProveedorOpen}>
                                    <PopoverTrigger asChild>
                                        <Button variant="outline" className="flex-1 justify-between font-normal">
                                            {proveedorSeleccionado ? proveedorSeleccionado.nombre : 'Selecciona un proveedor'}
                                            <ChevronsUpDown size={14} className="text-gray-400" />
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-[300px] p-0">
                                        <Command>
                                            <CommandInput placeholder="Buscar proveedor..."
                                                value={proveedorSearch} onValueChange={setProveedorSearch} />
                                            <CommandList className="max-h-60 overflow-y-auto"
                                                onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                <CommandEmpty>No se encontraron proveedores</CommandEmpty>
                                                <CommandGroup>
                                                    {proveedoresData?.map(p => (
                                                        <CommandItem key={p.idProveedor} value={p.nombre}
                                                            onSelect={() => { setProveedorId(p.idProveedor); setProveedorOpen(false); }}>
                                                            <Check size={14} className={proveedorId === p.idProveedor ? 'opacity-100' : 'opacity-0'} />
                                                            {p.nombre}
                                                        </CommandItem>
                                                    ))}
                                                </CommandGroup>
                                            </CommandList>
                                        </Command>
                                    </PopoverContent>
                                </Popover>
                                <Button variant="outline" size="icon" onClick={() => { setProveedorEditar(null); setProveedorDialogOpen(true); }}>
                                    <Plus size={16} />
                                </Button>
                                <Button variant="outline" size="icon" disabled={!proveedorSeleccionado}
                                    onClick={() => { setProveedorEditar(proveedorSeleccionado ?? null); setProveedorDialogOpen(true); }}>
                                    <Pencil size={16} />
                                </Button>
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label>Número de factura</Label>
                            <Input value={numeroFactura} onChange={e => setNumeroFactura(e.target.value)}/>
                        </div>
                        </>
                    )}

                    <div className="space-y-2">
                        <Label>Fecha</Label>
                        <Input type="date" value={fecha} onChange={e => setFecha(e.target.value)} />
                    </div>

                    <div className="space-y-2">
                        <Label>Estatus</Label>
                        <Select value={estatus} onValueChange={v => setEstatus(v as EstatusCompra)}>
                            <SelectTrigger><SelectValue /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="REGISTRADA">Registrada</SelectItem>
                                <SelectItem value="FACTURADA">Facturada</SelectItem>
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
                                onClick={() => setProductos([{ productoId: 0, lote: '', fechaCaducidad: '', cantidad: 1, subtotal: 0, costoTotal: 0 }])}>
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
                                    <TableHead>Caducidad</TableHead>
                                    <TableHead>Cantidad</TableHead>
                                    <TableHead>{esDoctor ? 'Valor del producto' : 'Subtotal'}</TableHead>
                                    <TableHead></TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {productos.map((prod, index) => (
                                    <TableRow key={index}>
                                        <TableCell className="min-w-[200px]">
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
                                                            <button className="text-gray-400 hover:text-gray-700 p-1 rounded"
                                                                onClick={e => { e.stopPropagation(); setProductoRowIndex(index); setProductoOpen(null); setProductoDialogOpen(true); }}>
                                                                <Plus size={14} />
                                                            </button>
                                                        </div>
                                                        <CommandList className="max-h-52 overflow-y-auto"
                                                            onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                                                            <CommandEmpty>No se encontraron productos</CommandEmpty>
                                                            <CommandGroup>
                                                                {productosData?.map(p => (
                                                                    <CommandItem key={p.idProducto}
                                                                        value={`${p.nombre} ${p.codigo} ${p.nombreMarca}`}
                                                                        onSelect={() => { actualizarProducto(index, 'productoId', p.idProducto); setProductoOpen(null); }}>
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
                                                onFocus={e => e.target.select()} placeholder='0'/>
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

                <div className="space-y-3">
                    <h2 className="font-medium text-gray-700">Notas</h2>
                    {compraData?.notas && compraData.notas.length > 0 && (
                        <div className="space-y-2 max-h-48 overflow-y-auto border rounded-md p-3 bg-gray-50">
                            {compraData.notas.map(n => (
                                <div key={n.idNota} className="text-sm border-b last:border-0 pb-2 last:pb-0">
                                    <p className="text-gray-700">{n.texto}</p>
                                    <p className="text-xs text-gray-400">{n.usuario} · {new Date(n.fecha).toLocaleString()}</p>
                                </div>
                            ))}
                        </div>
                    )}
                    <div className="space-y-2">
                        <Label>Agregar nota (opcional)</Label>
                        <Textarea value={nota} onChange={e => setNota(e.target.value)} placeholder="Agregar una nota..." />
                    </div>
                </div>

                <Separator />

                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <div>{error && <p className="text-sm text-red-500">{error}</p>}</div>
                    <div className="flex items-center gap-6">
                        {!esDoctor && (
                            <div className="text-right">
                                <p className="text-xs text-gray-500">Subtotal</p>
                                <p className="text-lg font-semibold text-gray-600">
                                    ${totalSubtotal.toFixed(2)}
                                </p>
                            </div>
                        )}
                        <div className="text-right">
                            <p className="text-sm text-gray-500">{esDoctor ? 'Costo de referencia' : 'Total (IVA 16%)'}</p>
                            <p className="text-2xl font-semibold text-gray-800">
                                ${totalConIva.toFixed(2)}
                            </p>
                        </div>
                        <Button onClick={handleSubmit} disabled={editarMutation.isPending} className="px-8">
                            {editarMutation.isPending ? 'Guardando...' : 'Guardar cambios'}
                        </Button>
                    </div>
                </div>
            </div>

            <ProveedorDialog
                open={proveedorDialogOpen}
                onOpenChange={setProveedorDialogOpen}
                onCreado={setProveedorId}
                proveedor={proveedorEditar}
            />

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

export default EditarCompra;
