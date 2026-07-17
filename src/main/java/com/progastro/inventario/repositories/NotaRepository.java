package com.progastro.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Compra;
import com.progastro.inventario.models.Entities.Nota;
import com.progastro.inventario.models.Entities.Salida;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByCompraOrderByFechaAsc(Compra compra);

    List<Nota> findBySalidaOrderByFechaAsc(Salida salida);
}
