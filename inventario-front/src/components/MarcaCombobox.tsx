import { useState } from 'react';
import { Check, ChevronsUpDown, Pencil } from 'lucide-react';
import { Button } from './ui/button';
import {
    Popover, PopoverContent, PopoverTrigger
} from './ui/popover';
import {
    Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList
} from './ui/command';
import type { MarcaResponse } from '../types/marca';

interface Props {
    marcas: MarcaResponse[];
    value: number;
    onChange: (idMarca: number) => void;
    onEditarMarca: (marca: MarcaResponse) => void;
}

const MarcaCombobox = ({ marcas, value, onChange, onEditarMarca }: Props) => {
    const [open, setOpen] = useState(false);

    const marcaSeleccionada = marcas.find(m => m.idMarca === value);

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button variant="outline" role="combobox" className="w-full justify-between font-normal">
                    {marcaSeleccionada ? marcaSeleccionada.nombre : 'Selecciona una marca'}
                    <ChevronsUpDown size={14} className="text-gray-400" />
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[400px] p-0">
                <Command>
                    <CommandInput placeholder="Buscar marca..." />
                    <CommandList className="max-h-60 overflow-y-auto" onWheel={e => e.currentTarget.scrollTop += e.deltaY}>
                        <CommandEmpty>No se encontraron marcas</CommandEmpty>
                        <CommandGroup>
                            {marcas.map(marca => (
                                <CommandItem
                                    key={marca.idMarca}
                                    value={marca.nombre}
                                    onSelect={() => {
                                        onChange(marca.idMarca);
                                        setOpen(false);
                                    }}
                                    className="flex items-center justify-between"
                                >
                                    <div className="flex items-center gap-2">
                                        <Check
                                            size={14}
                                            className={value === marca.idMarca ? 'opacity-100' : 'opacity-0'}
                                        />
                                        {marca.nombre}
                                    </div>
                                    <button
                                        onClick={e => {
                                            e.stopPropagation();
                                            onEditarMarca(marca);
                                            setOpen(false);
                                        }}
                                        className="text-gray-400 hover:text-gray-700 p-1 rounded"
                                    >
                                        <Pencil size={12} />
                                    </button>
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    );
};

export default MarcaCombobox;