package com.progastro.inventario.services.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.mappers.UsuarioMapper;
import com.progastro.inventario.models.DTO.UsuarioRequestDTO;
import com.progastro.inventario.models.DTO.UsuarioResponseDTO;
import com.progastro.inventario.models.Entities.Rol;
import com.progastro.inventario.models.Entities.Usuario;
import com.progastro.inventario.repositories.RolRepository;
import com.progastro.inventario.repositories.UsuarioRepository;
import com.progastro.inventario.services.UsuarioServiceBridge;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioServiceBridge {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO request) {

        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Ya existe un usuario con ese username");
        }

        Rol rol = rolRepository.findById(request.getIdRol()).orElseThrow(() ->
            new ResourceNotFoundException("Rol no encontrado con id " + request.getIdRol())
        );

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO editarUsuario(UsuarioRequestDTO request) {

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario()).orElseThrow(() ->
            new ResourceNotFoundException("Usuario no encontrado con id " + request.getIdUsuario())
        );

        if (!usuario.getUsername().equals(request.getUsername()) &&
            usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Ya existe un usuario con ese username");
        }

        Rol rol = rolRepository.findById(request.getIdRol()).orElseThrow(() ->
            new ResourceNotFoundException("Rol no encontrado con id " + request.getIdRol())
        );

        usuario.setUsername(request.getUsername());
        usuario.setRol(rol);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void cambiarEstadoUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() ->
            new ResourceNotFoundException("Usuario no encontrado con id " + idUsuario)
        );
        usuario.setEnabled(!usuario.getEnabled());
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(String username, String rol, Boolean enabled, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return usuarioRepository.findByFiltros(username, rol, enabled, pageable)
                                .map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO me(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")
        );
        return usuarioMapper.toResponse(usuario);
    }
}