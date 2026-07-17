package com.progastro.inventario.controllers;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.progastro.inventario.models.DTO.EliminarSalidaRequestDTO;
import com.progastro.inventario.models.DTO.PageResponse;
import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;
import com.progastro.inventario.models.Response.ApiResponse;
import com.progastro.inventario.services.SalidaServiceBridge;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
                                                                            @RequestParam(required = false) Long folio,
                                                                            @RequestParam(required = false) String destinatario,
                                                                            @RequestParam(defaultValue= "0") int page,
                                                                            @RequestParam(defaultValue= "20") int size
                                                                        ) {
        Page<SalidaResponseDTO> result = salidaServiceBridge.listarSalidas(producto, destino, tipo, fechaInicio, fechaFin, folio, destinatario, page, size);
        
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

    @PostMapping("/editar-salida")
    public ResponseEntity<ApiResponse<SalidaResponseDTO>> editarSalida(@RequestBody @Valid SalidaRequestDTO request) {
        SalidaResponseDTO response = salidaServiceBridge.editarSalida(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(true, "Salida editada correctamente", response));
    }

    @PostMapping("/devolver-producto/{idSalidaProducto}")
    public ResponseEntity<ApiResponse<SalidaResponseDTO>> devolverProducto(@PathVariable Long idSalidaProducto) {
        SalidaResponseDTO response = salidaServiceBridge.devolverProducto(idSalidaProducto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Producto devuelto al inventario correctamente", response));
    }

    @PostMapping("/eliminar-salida/{idSalida}")
    public ResponseEntity<ApiResponse<Void>> eliminarSalida(@PathVariable Long idSalida,
                                                            @RequestBody @Valid EliminarSalidaRequestDTO request) {
        salidaServiceBridge.eliminarSalida(idSalida, request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Salida eliminada correctamente", null));
    }
}
