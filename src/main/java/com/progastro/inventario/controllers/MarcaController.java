package com.progastro.inventario.controllers;

import static org.hibernate.engine.jdbc.Size.LobMultiplier.M;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.progastro.inventario.models.DTO.MarcaRequestDTO;
import com.progastro.inventario.models.DTO.MarcaResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.MarcaServiceBridge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("api/marcas")
@RequiredArgsConstructor
public class MarcaController {
    
    private final MarcaServiceBridge marcaServiceBridge;

    @PostMapping("/registrar-marca")
    public ResponseEntity<ApiResponse<MarcaResponseDTO>> registrarMarca(@RequestBody @Valid MarcaRequestDTO request) {
        MarcaResponseDTO response = marcaServiceBridge.registrarMarca(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Marca registrada correctamente", response));
    }

    @GetMapping("/consultar-marcas")
    public ResponseEntity<Page<MarcaResponseDTO>> listarMarcas(@RequestParam(required= false) String nombre,
                                                                @RequestParam(defaultValue= "0")int page,
                                                                @RequestParam(defaultValue= "20")int size) {
        Page<MarcaResponseDTO> result = marcaServiceBridge.listarMarcas(nombre, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
}