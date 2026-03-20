package com.progastro.inventario.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.RolResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.RolServiceBridge;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {
    
    private final RolServiceBridge rolServiceBridge;

    @GetMapping("/consultar-roles")
    public ResponseEntity<ApiResponse<List<RolResponseDTO>>> listarRoles() {
        
        List<RolResponseDTO> response = rolServiceBridge.listarRoles();
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", response));
    }
    
}
