import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { crearProveedor } from '../api/proveedores';
import type { ProveedorRequest } from '../types/proveedor';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';

interface Props {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onCreado: (idProveedor: number) => void;
}

const EMPTY_FORM: ProveedorRequest = { nombre: '', rfc: '', tipoPersona: 1, telefono: '' };

type FieldErrors = Partial<Record<keyof ProveedorRequest | '_server', string>>;

const validate = (form: ProveedorRequest): FieldErrors => {
    const errors: FieldErrors = {};
    if (!form.nombre.trim()) errors.nombre = 'no debe estar vacío';
    if (!form.rfc.trim())    errors.rfc = 'no debe estar vacío';
    if (!form.telefono.trim()) errors.telefono = 'no debe estar vacío';
    return errors;
};

const FIELD_LABELS: Partial<Record<keyof ProveedorRequest | '_server', string>> = {
    nombre: 'Nombre',
    rfc: 'RFC',
    telefono: 'Teléfono',
    _server: 'Error',
};

const ProveedorDialog = ({ open, onOpenChange, onCreado }: Props) => {
    const queryClient = useQueryClient();
    const [form, setForm] = useState<ProveedorRequest>(EMPTY_FORM);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

    const mutation = useMutation({
        mutationFn: crearProveedor,
        onSuccess: (res) => {
            queryClient.invalidateQueries({ queryKey: ['proveedores'] });
            onCreado(res.data.data.idProveedor);
            onOpenChange(false);
            setForm(EMPTY_FORM);
            setFieldErrors({});
        },
        onError: (e: any) => setFieldErrors({ _server: e.response?.data?.message || 'Ocurrió un error' }),
    });

    const handleGuardar = () => {
        const errors = validate(form);
        if (Object.keys(errors).length > 0) {
            setFieldErrors(errors);
            return;
        }
        setFieldErrors({});
        mutation.mutate(form);
    };

    const err = (field: keyof ProveedorRequest) =>
        fieldErrors[field] ? 'border-red-500 focus-visible:ring-red-500' : '';

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader><DialogTitle>Nuevo proveedor</DialogTitle></DialogHeader>
                <div className="space-y-4 py-2">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2 col-span-2">
                            <Label>Nombre*</Label>
                            <Input value={form.nombre} className={err('nombre')}
                                onChange={e => setForm({ ...form, nombre: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>RFC*</Label>
                            <Input value={form.rfc} className={err('rfc')}
                                onChange={e => setForm({ ...form, rfc: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Tipo persona*</Label>
                            <Select value={String(form.tipoPersona)}
                                onValueChange={v => setForm({ ...form, tipoPersona: Number(v) })}>
                                <SelectTrigger><SelectValue /></SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="1">Física</SelectItem>
                                    <SelectItem value="0">Moral</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="space-y-2">
                            <Label>Teléfono*</Label>
                            <Input value={form.telefono} className={err('telefono')}
                                onChange={e => setForm({ ...form, telefono: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Correo</Label>
                            <Input value={form.correo || ''}
                                onChange={e => setForm({ ...form, correo: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <Label>Código postal</Label>
                            <Input type="number" value={form.codigoPostal || ''}
                                onChange={e => setForm({ ...form, codigoPostal: Number(e.target.value) })}
                            />
                        </div>
                    </div>
                    {Object.keys(fieldErrors).length > 0 && (
                        <ul className="text-sm text-red-500 space-y-0.5 list-none">
                            {(Object.entries(fieldErrors) as [keyof typeof FIELD_LABELS, string][]).map(([field, msg]) => (
                                <li key={field}>• <strong>{FIELD_LABELS[field]}</strong>: {msg}</li>
                            ))}
                        </ul>
                    )}
                </div>
                <DialogFooter className="flex-row items-center">
                    <p className="text-sm text-gray-500 mr-auto">* Campo obligatorio</p>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
                    <Button onClick={handleGuardar} disabled={mutation.isPending}>
                        {mutation.isPending ? 'Guardando...' : 'Guardar'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};

export default ProveedorDialog;
