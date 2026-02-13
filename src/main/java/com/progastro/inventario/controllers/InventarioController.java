package com.progastro.inventario.controllers;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.InventarioResponseDTO;
import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.InventarioServiceBridge;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {
    
    private final InventarioServiceBridge inventarioServiceBridge;

    @GetMapping("/{idProducto}/inventarios")
     public ResponseEntity<PageResponse<InventarioResponseDTO>> listarInventarios(@PathVariable Long idProducto,
                                                                                @RequestParam(required= false) String lote,
                                                                                @RequestParam(required= false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                                                @RequestParam(required= false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                                                                @RequestParam(defaultValue = "0")int page,
                                                                                @RequestParam(defaultValue = "20") int size
                                                                            ) {

        Page<InventarioResponseDTO> result = inventarioServiceBridge.listarInventarios(idProducto, lote, fechaInicio, fechaFin, page, size);
        
        PageResponse<InventarioResponseDTO> response =
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

    //Modificacion: true= aumenta, false= resta
    @PatchMapping("/{idInventario}/{modificacion}")
    public ResponseEntity<ApiResponse<Void>> modificarStock(@PathVariable Long idInventario, @PathVariable Boolean modificacion) {
        inventarioServiceBridge.modificarStock(idInventario, modificacion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock modificado correctamente", null));
    }
    
}
