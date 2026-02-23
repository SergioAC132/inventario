package com.progastro.inventario.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.DTO.UsuarioRequestDTO;
import com.progastro.inventario.models.DTO.UsuarioResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.UsuarioServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceBridge usuarioServiceBridge;

    @PostMapping("/registrar-usuario")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crearUsuario(@RequestBody @Valid UsuarioRequestDTO request) {
        UsuarioResponseDTO response = usuarioServiceBridge.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Usuario creado correctamente", response));
    }

    @PostMapping("/editar-usuario")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> editarUsuario(@RequestBody @Valid UsuarioRequestDTO request) {
        UsuarioResponseDTO response = usuarioServiceBridge.editarUsuario(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Usuario editado correctamente", response));
    }

    @PatchMapping("/{idUsuario}/deshabilitar")
    public ResponseEntity<ApiResponse<Void>> deshabilitarUsuario(@PathVariable Long idUsuario) {
        usuarioServiceBridge.deshabilitarUsuario(idUsuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Usuario deshabilitado correctamente", null));
    }

    @GetMapping("/consultar-usuarios")
    public ResponseEntity<PageResponse<UsuarioResponseDTO>> listarUsuarios(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<UsuarioResponseDTO> result = usuarioServiceBridge.listarUsuarios(username, rol, enabled, page, size);

        PageResponse<UsuarioResponseDTO> response = new PageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}