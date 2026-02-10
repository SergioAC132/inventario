package com.progastro.inventario.controllers;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.progastro.inventario.models.DTO.CompraRequestDTO;
import com.progastro.inventario.models.DTO.CompraResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.CompraServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraServiceBridge compraServiceBridge;
    
    @PostMapping("/registrar-compra")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> registrarCompra(@RequestBody @Valid 
                                                                        CompraRequestDTO request) {
        CompraResponseDTO response = compraServiceBridge.registrarCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Compra registrada correctamente", response));
    }

    @GetMapping("/consultar-compras")
    public ResponseEntity<Page<CompraResponseDTO>> listarCompras(@RequestParam(required = false) String proveedor,
                                                                            @RequestParam(required = false) String estatus,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                                                            @RequestParam(defaultValue = "0")int page,
                                                                            @RequestParam(defaultValue = "20") int size
                                                                            ) {

        Page<CompraResponseDTO> result = compraServiceBridge.listarCompras(proveedor, estatus, fechaInicio, fechaFin, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/editar-compra")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> editarCompra(@RequestBody @Valid CompraRequestDTO request) {
        CompraResponseDTO response = compraServiceBridge.editarCompra(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Compra editada correctamente", response));
    }

    @PatchMapping("/{idCompra}/cancelar")
    public ResponseEntity<ApiResponse<Void>> cancelarCompra(@PathVariable Long idCompra) {
        compraServiceBridge.cancelarCompra(idCompra);
        return ResponseEntity.ok(new ApiResponse<>(true, "Compra cancelada correctamente", null));
    }
}
