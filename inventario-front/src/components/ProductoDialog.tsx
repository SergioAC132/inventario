import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { crearProducto } from '../api/productos';
import { getMarcas } from '../api/marcas';
import type { ProductoRequest } from '../types/producto';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import MarcaCombobox from './MarcaCombobox';

interface Props {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onCreado: (idProducto: number) => void;
}

const EMPTY_FORM: ProductoRequest = { codigo: '', nombre: '', idMarca: 0 };

const ProductoDialog = ({ open, onOpenChange, onCreado }: Props) => {
    const queryClient = useQueryClient();
    const [form, setForm] = useState<ProductoRequest>(EMPTY_FORM);
    const [error, setError] = useState('');

    const { data: marcasData } = useQuery({
        queryKey: ['marcas'],
        queryFn: () => getMarcas({ size: 100 }).then(r => r.data.content),
    });

    const mutation = useMutation({
        mutationFn: crearProducto,
        onSuccess: (res) => {
            queryClient.invalidateQueries({ queryKey: ['productos'] });
            onCreado(res.data.data.idProducto);
            onOpenChange(false);
            setForm(EMPTY_FORM);
            setError('');
        },
        onError: (e: any) => setError(e.response?.data?.message || 'Ocurrió un error'),
    });

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent>
                <DialogHeader><DialogTitle>Nuevo producto</DialogTitle></DialogHeader>
                <div className="space-y-4 py-2">
                    <div className="space-y-2">
                        <Label>Código</Label>
                        <Input value={form.codigo}
                            onChange={e => setForm({ ...form, codigo: e.target.value })}
                            placeholder="PRD001" />
                    </div>
                    <div className="space-y-2">
                        <Label>Nombre</Label>
                        <Input value={form.nombre}
                            onChange={e => setForm({ ...form, nombre: e.target.value })} />
                    </div>
                    <div className="space-y-2">
                        <Label>Marca</Label>
                        <MarcaCombobox
                            marcas={marcasData || []}
                            value={form.idMarca}
                            onChange={v => setForm({ ...form, idMarca: v })}
                            onEditarMarca={() => {}}
                        />
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

export default ProductoDialog;
