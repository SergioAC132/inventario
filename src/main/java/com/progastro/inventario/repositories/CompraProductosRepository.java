package com.progastro.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.CompraProductos;


@Repository
public interface CompraProductosRepository extends JpaRepository<CompraProductos, Long> {

    @Query("""
            SELECT cp
            FROM CompraProductos cp
            JOIN FETCH cp.inventario i
            JOIN FETCH i.producto p
            JOIN FETCH p.marca m
            WHERE cp.compra = :compra
            """)
    List<CompraProductos> findByCompra(Compra compra);
}
