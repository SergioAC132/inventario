package com.progastro.inventario.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.progastro.inventario.models.Entities.Usuario;
import com.progastro.inventario.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
 
    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    public void settearUsuario() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();

        
        jdbcTemplate.update("SET @usuario_id = ?", usuario.getIdUsuario());
    
    }
}