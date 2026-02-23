package com.progastro.inventario.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progastro.inventario.models.Entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    Optional<Usuario> findByUsername(String username);
    
    @Query("""
        SELECT u FROM Usuario u JOIN u.rol r
        WHERE (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
        AND (:rol IS NULL OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :rol, '%')))
        AND (:enabled IS NULL OR u.enabled = :enabled)
    """)
    Page<Usuario> findByFiltros(@Param("username") String username,
                                @Param("rol") String rol,
                                @Param("enabled") Boolean enabled,
                                Pageable pageable);
}
