package com.progastro.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.CompraProductos;

@Repository
public interface CompraProductosRepository extends JpaRepository<CompraProductos, Long> {
}
