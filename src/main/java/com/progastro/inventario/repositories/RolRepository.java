package com.progastro.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
    
}
