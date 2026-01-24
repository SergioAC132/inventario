package com.progastro.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}

