package com.progastro.inventario.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Destinatario;

@Repository
public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {

    @Query("""
            SELECT d FROM Destinatario d
            WHERE (:nombre IS NULL OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        """)
    Page<Destinatario> findByFiltros(@Param("nombre") String nombre, Pageable pageable);
}
