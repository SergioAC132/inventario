import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const formatearFecha = (fecha: string): string => {
    if (!fecha) return '';
    const [anio, mes, dia] = fecha.split('-');
    return `${dia}/${mes}/${anio}`;
};

export const capitalizarPalabras = (texto: string): string =>
    texto.trim().replace(/\s+/g, ' ').toLowerCase().replace(/(^|\s)\p{L}/gu, c => c.toUpperCase());