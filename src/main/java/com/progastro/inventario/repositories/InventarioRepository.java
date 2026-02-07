package com.progastro.inventario.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Producto;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoAndLoteAndFechaCaducidad(Producto producto, String lote,
                                                                LocalDate fechaCaducidad);
}

