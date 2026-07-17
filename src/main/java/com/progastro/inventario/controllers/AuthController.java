package com.progastro.inventario.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.LoginRequestDTO;
import com.progastro.inventario.models.DTO.LoginResponseDTO;
import com.progastro.inventario.models.DTO.UsuarioResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.JwtService;
import com.progastro.inventario.services.UsuarioServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioServiceBridge usuarioServiceBridge;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Usuario o contraseña incorrectos", null));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        UsuarioResponseDTO usuario = usuarioServiceBridge.me(request.getUsername());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", new LoginResponseDTO(token, usuario)));
    }
}
