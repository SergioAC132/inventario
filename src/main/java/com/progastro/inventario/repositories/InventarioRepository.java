package com.progastro.inventario.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoAndLoteAndFechaCaducidad(Producto producto, String lote,
                                                                LocalDate fechaCaducidad);

    @Query("""
            SELECT i FROM Inventario i
            WHERE producto = :producto
            AND (:lote IS NULL OR LOWER(lote) LIKE LOWER(CONCAT('%', :lote, '%')))
            AND (:fechaInicio IS NULL OR fechaCaducidad >= :fechaInicio)
            AND (:fechaFin IS NULL OR fechaCaducidad <= :fechaFin)
            """)
    Page<Inventario> findByFiltros(@Param("producto") Producto producto, @Param("lote") String lote,
                                @Param("fechaInicio") LocalDate fechaInicio,
                                @Param("fechaFin") LocalDate fechaFin, Pageable pageable);
}

