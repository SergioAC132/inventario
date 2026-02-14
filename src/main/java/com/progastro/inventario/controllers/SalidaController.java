package com.progastro.inventario.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.SalidaServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/salidas")
@RequiredArgsConstructor
public class SalidaController {
    
    private final SalidaServiceBridge salidaServiceBridge;

    @PostMapping("/registrar-salida")
    public ResponseEntity<ApiResponse<SalidaResponseDTO>> registrarSalida(@RequestBody @Valid
                                                                            SalidaRequestDTO request) {
        SalidaResponseDTO response = salidaServiceBridge.registrarSalida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Salida registrada correctamente", response));
    }

    @GetMapping("/consultar-salidas")
    public ResponseEntity<PageResponse<SalidaResponseDTO>> listarSalidas(@RequestParam(required = false) String producto,
                                                                            @RequestParam(required = false) String destino,
                                                                            @RequestParam(required = false) String tipo,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                                                            @RequestParam(defaultValue= "0") int page,
                                                                            @RequestParam(defaultValue= "20") int size
                                                                        ) {
        Page<SalidaResponseDTO> result = salidaServiceBridge.listarSalidas(producto, destino, tipo, fechaInicio, fechaFin, page, size);
        
        PageResponse<SalidaResponseDTO> response = 
            new PageResponse<>(
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
