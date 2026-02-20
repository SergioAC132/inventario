package com.progastro.inventario.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.progastro.inventario.models.Entities.Rol;
import com.progastro.inventario.models.Entities.Usuario;
import com.progastro.inventario.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRoles()
                        .stream()
                        .map(Rol::getNombre)
                        .toArray(String[]::new))
            .build();
    }
}
