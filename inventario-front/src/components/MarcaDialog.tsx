import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { crearMarca } from '../api/marcas';
import type { MarcaRequest } from '../api/marcas';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';

interface Props {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onCreada?: () => void;
}

const MarcaDialog = ({ open, onOpenChange, onCreada }: Props) => {
    const queryClient = useQueryClient();
    const [form, setForm] = useState<MarcaRequest>({ nombre: '' });
    const [error, setError] = useState('');

    const mutation = useMutation({
        mutationFn: crearMarca,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['marcas'] });
            onOpenChange(false);
            setForm({ nombre: '' });
            setError('');
            onCreada?.();
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error'),
    });

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader><DialogTitle>Nueva marca</DialogTitle></DialogHeader>
                <div className="space-y-4 py-2">
                    <div className="space-y-2">
                        <Label>Nombre</Label>
                        <Input value={form.nombre}
                            onChange={e => setForm({ ...form, nombre: e.target.value })} />
                    </div>
                    {error && <p className="text-sm text-red-500">{error}</p>}
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>Cancelar</Button>
                    <Button onClick={() => mutation.mutate(form)} disabled={mutation.isPending}>
                        {mutation.isPending ? 'Guardando...' : 'Guardar'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};

export default MarcaDialog;
