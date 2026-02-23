package com.progastro.inventario.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long>{
    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Marca> findByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT m FROM Marca m
            WHERE (:nombre IS NULL OR LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            """)
    public Page<Marca> findByFiltro(@Param("nombre") String nombre, Pageable pageable);

    boolean existsByNombreIgnoreCaseAndIdMarcaNot(String nombre, Long idMarca);
}
