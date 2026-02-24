package com.progastro.inventario.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByRfcIgnoreCase(String rfc);
    boolean existsByRfcIgnoreCaseAndIdProveedorNot(String rfc, Long idProveedor);

    @Query("""
            SELECT p FROM Proveedor p
            WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            AND (:rfc IS NULL OR LOWER(p.rfc) LIKE LOWER(CONCAT('%', :rfc, '%')))
        """)
    Page<Proveedor> findByFiltros(@Param("nombre") String nombre,
                                   @Param("rfc") String rfc,
                                   Pageable pageable);
}