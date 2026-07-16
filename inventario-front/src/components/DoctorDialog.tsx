import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { crearDoctor } from '../api/doctores';
import type { DoctorRequest } from '../types/doctor';
import { capitalizarPalabras } from '../lib/utils';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';

interface Props {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onCreado: (idDoctor: number) => void;
}

const EMPTY_FORM: DoctorRequest = { nombre: '', telefono: '' };

type FieldErrors = Partial<Record<keyof DoctorRequest | '_server', string>>;

const validate = (form: DoctorRequest): FieldErrors => {
    const errors: FieldErrors = {};
    if (!form.nombre.trim()) errors.nombre = 'no debe estar vacío';
    return errors;
};

const FIELD_LABELS: Partial<Record<keyof DoctorRequest | '_server', string>> = {
    nombre: 'Nombre',
    telefono: 'Teléfono',
    _server: 'Error',
};

const DoctorDialog = ({ open, onOpenChange, onCreado }: Props) => {
    const queryClient = useQueryClient();
    const [form, setForm] = useState<DoctorRequest>(EMPTY_FORM);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

    const mutation = useMutation({
        mutationFn: crearDoctor,
        onSuccess: (res) => {
            queryClient.invalidateQueries({ queryKey: ['doctores'] });
            onCreado(res.data.data.idDoctor);
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
        mutation.mutate({ ...form, nombre: capitalizarPalabras(form.nombre) });
    };

    const err = (field: keyof DoctorRequest) =>
        fieldErrors[field] ? 'border-red-500 focus-visible:ring-red-500' : '';

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader><DialogTitle>Nuevo doctor</DialogTitle></DialogHeader>
                <div className="space-y-4 py-2">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2 col-span-2">
                            <Label>Nombre*</Label>
                            <Input value={form.nombre} className={err('nombre')}
                                onChange={e => setForm({ ...form, nombre: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2 col-span-2">
                            <Label>Teléfono</Label>
                            <Input value={form.telefono || ''}
                                onChange={e => setForm({ ...form, telefono: e.target.value })}
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

export default DoctorDialog;
