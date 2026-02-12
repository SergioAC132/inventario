package com.progastro.inventario.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.Proveedor;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    boolean existsByNumeroFacturaAndProveedor(String numeroFactura, Proveedor proveedor);

    boolean existsByNumeroFacturaAndProveedorAndIdCompraNot(String numeroFactura, Proveedor proveedor, Long idCompra);

    @Query("""
        SELECT c FROM Compra c JOIN c.proveedor p
        WHERE (:proveedor IS NULL OR LOWER(c.proveedor.nombre) LIKE LOWER(CONCAT('%', :proveedor, '%')))
        AND (:estatus IS NULL OR c.estatus = :estatus)
        AND (:fechaInicio IS NULL OR c.fecha >= :fechaInicio)
        AND (:fechaFin IS NULL OR c.fecha <= :fechaFin)
    """)
    Page<Compra> findByFiltros(@Param("proveedor") String proveedor, @Param("estatus") String estatus,
                               @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                               Pageable pageable);
}
