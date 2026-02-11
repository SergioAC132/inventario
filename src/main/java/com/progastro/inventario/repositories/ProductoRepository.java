package com.progastro.inventario.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Marca;
import com.progastro.inventario.models.Entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>{

    boolean existsByCodigoAndMarca(String codigo, Marca marca);

    boolean existsByCodigoAndMarcaAndIdProductoNot(String codigo, Marca marca, Long idProducto);
    
    @Query("""
            SELECT p FROM Producto p JOIN FETCH p.marca
            WHERE (:marca IS NULL OR LOWER(p.marca.nombre) LIKE LOWER(CONCAT('%', :marca, '%')))
            AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            AND (:codigo IS NULL OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')))
            """)
    Page<Producto> findByFiltros(@Param("marca") String marca, @Param("nombre") String nombre,
                                    @Param("codigo") String codigo, Pageable pageable);
}
