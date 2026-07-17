package com.progastro.inventario.repositories;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Salida;
import com.progastro.inventario.models.Enums.DestinoSalida;
import com.progastro.inventario.models.Enums.TipoSalida;

@Repository
public interface SalidaRepository extends JpaRepository<Salida, Long> {

    @Query("""
    SELECT DISTINCT s
    FROM Salida s
    LEFT JOIN s.productos sp
    LEFT JOIN sp.inventario i
    LEFT JOIN i.producto p
    LEFT JOIN s.destinatario d
    WHERE (:destino IS NULL OR s.destino = :destino)
    AND (:tipo IS NULL OR s.tipo = :tipo)
    AND (:fechaInicio IS NULL OR s.fecha >= :fechaInicio)
    AND (:fechaFin IS NULL OR s.fecha <= :fechaFin)
    AND (:nombreProducto IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombreProducto, '%')))
    AND (:folio IS NULL OR s.folio = :folio)
    AND (:destinatario IS NULL OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :destinatario, '%')))
    """)
    Page<Salida> findByFiltros(
        @Param("nombreProducto") String nombreProducto,
        @Param("destino") DestinoSalida destino,
        @Param("tipo") TipoSalida tipo,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("folio") Long folio,
        @Param("destinatario") String destinatario,
        Pageable pageable
    );

}
