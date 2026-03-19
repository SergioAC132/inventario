import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { getCompras } from '../api/compras';
import { getSalidas } from '../api/salidas';
import { Badge } from '../components/ui/badge';
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow
} from '../components/ui/table';
import { formatearFecha } from '../lib/utils';
import type { EstatusCompra } from '../types/compra';

const estatusBadge = (estatus: EstatusCompra) => {
    switch (estatus) {
        case 'REGISTRADA': return 'secondary';
        case 'FACTURADA': return 'default';
        case 'CANCELADA': return 'destructive';
    }
};

const tipoBadge = (tipo: string) => {
    switch (tipo) {
        case 'CIRUGIA': return 'bg-purple-100 text-purple-700';
        case 'ESTUDIO': return 'bg-blue-100 text-blue-700';
        case 'VENTA': return 'bg-green-100 text-green-700';
        default: return 'bg-gray-100 text-gray-700';
    }
};

const Dashboard = () => {
    const navigate = useNavigate();

    const { data: comprasData } = useQuery({
        queryKey: ['compras-dashboard'],
        queryFn: () => getCompras({ page: 0, size: 5 }).then(r => r.data)
    });

    const { data: salidasData } = useQuery({
        queryKey: ['salidas-dashboard'],
        queryFn: () => getSalidas({ page: 0, size: 5 }).then(r => r.data)
    });

    return (
        <div className="space-y-6">
            <h1 className="text-2xl font-semibold text-gray-800">Dashboard</h1>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6">
                {/* Últimas compras */}
                <div className="bg-white border rounded-lg p-4 space-y-3">
                    <div className="flex items-center justify-between">
                        <h2 className="font-medium text-gray-700">Últimas compras</h2>
                        <button
                            className="text-sm text-gray-400 hover:text-gray-700"
                            onClick={() => navigate('/compras')}
                        >
                            Ver todas →
                        </button>
                    </div>
                    <div className="overflow-x-auto">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Fecha</TableHead>
                                <TableHead>Proveedor</TableHead>
                                <TableHead>Estatus</TableHead>
                                <TableHead>Total</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {comprasData?.content.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} className="text-center text-gray-400 py-6">
                                        No hay compras registradas
                                    </TableCell>
                                </TableRow>
                            ) : comprasData?.content.map(compra => (
                                <TableRow
                                    key={compra.idCompra}
                                    className="cursor-pointer hover:bg-gray-50"
                                    onClick={() => navigate(`/compras/editar/${compra.idCompra}`)}
                                >
                                    <TableCell>{formatearFecha(compra.fecha)}</TableCell>
                                    <TableCell className="max-w-[120px] truncate">{compra.proveedor}</TableCell>
                                    <TableCell>
                                        <Badge variant={estatusBadge(compra.estatus)}>{compra.estatus}</Badge>
                                    </TableCell>
                                    <TableCell>${compra.total.toFixed(2)}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                    </div>
                </div>

                {/* Últimas salidas */}
                <div className="bg-white border rounded-lg p-4 space-y-3">
                    <div className="flex items-center justify-between">
                        <h2 className="font-medium text-gray-700">Últimas salidas</h2>
                        <button
                            className="text-sm text-gray-400 hover:text-gray-700"
                            onClick={() => navigate('/salidas')}
                        >
                            Ver todas →
                        </button>
                    </div>
                    <div className="overflow-x-auto">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Fecha</TableHead>
                                <TableHead>Tipo</TableHead>
                                <TableHead>Destino</TableHead>
                                <TableHead>Total</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {salidasData?.content.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} className="text-center text-gray-400 py-6">
                                        No hay salidas registradas
                                    </TableCell>
                                </TableRow>
                            ) : salidasData?.content.map(salida => (
                                <TableRow
                                    key={salida.idSalida}
                                    className="cursor-pointer hover:bg-gray-50"
                                    onClick={() => navigate(`/salidas/editar/${salida.idSalida}`)}
                                >
                                    <TableCell>{formatearFecha(salida.fecha)}</TableCell>
                                    <TableCell>
                                        <Badge className={tipoBadge(salida.tipo)}>{salida.tipo}</Badge>
                                    </TableCell>
                                    <TableCell>{salida.destino}</TableCell>
                                    <TableCell>${salida.total?.toFixed(2) ?? '0.00'}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;