package com.progastro.inventario.services.Impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progastro.inventario.exceptions.ResourceNotFoundException;
import com.progastro.inventario.exceptions.ValidationException;
import com.progastro.inventario.mappers.SalidaMapper;
import com.progastro.inventario.models.DTO.SalidaProductoRequestDTO;
import com.progastro.inventario.models.DTO.SalidaRequestDTO;
import com.progastro.inventario.models.DTO.SalidaResponseDTO;
import com.progastro.inventario.models.Entities.Destinatario;
import com.progastro.inventario.models.Entities.Inventario;
import com.progastro.inventario.models.Entities.Nota;
import com.progastro.inventario.models.Entities.Salida;
import com.progastro.inventario.models.Entities.SalidaProductos;
import com.progastro.inventario.models.Entities.Usuario;
import com.progastro.inventario.models.Enums.DestinoSalida;
import com.progastro.inventario.models.Enums.TipoSalida;
import com.progastro.inventario.repositories.DestinatarioRepository;
import com.progastro.inventario.repositories.InventarioRepository;
import com.progastro.inventario.repositories.NotaRepository;
import com.progastro.inventario.repositories.SalidaProductosRepository;
import com.progastro.inventario.repositories.SalidaRepository;
import com.progastro.inventario.services.InventarioServiceBridge;
import com.progastro.inventario.services.LoginService;
import com.progastro.inventario.services.SalidaServiceBridge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalidaServiceImpl implements SalidaServiceBridge {
    
    private final DestinatarioRepository destinatarioRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioServiceBridge inventarioService;
    private final LoginService loginService;
    private final NotaRepository notaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SalidaMapper salidaMapper;
    private final SalidaRepository salidaRepository;
    private final SalidaProductosRepository salidaProductosRepository;

    private Destinatario validarFolioDestinatario(SalidaRequestDTO request) {
        boolean tieneFolio = request.getFolio() != null;
        boolean tieneDestinatario = request.getDestinatarioId() != null;

        if (tieneFolio && tieneDestinatario) {
            throw new ValidationException("Sólo se puede indicar folio o destinatario, no ambos");
        }

        if (tieneDestinatario) {
            return destinatarioRepository.findById(request.getDestinatarioId()).orElseThrow(() ->
                new ResourceNotFoundException("Destinatario no encontrado con id " + request.getDestinatarioId())
            );
        }

        return null;
    }

    private void registrarNotaSiAplica(Salida salida, String texto) {
        if (texto == null || texto.isBlank()) return;

        Nota nota = new Nota();
        nota.setSalida(salida);
        nota.setFecha(new Date());
        nota.setTexto(texto);
        nota.setUsuario(loginService.getUsuarioActual());
        notaRepository.save(nota);
    }

    @Override
    @Transactional
    public SalidaResponseDTO registrarSalida(SalidaRequestDTO request) {
        List<SalidaProductos> listaProductos = new ArrayList<>();
    
        validarCreacion(request.getProductos());
        Destinatario destinatario = validarFolioDestinatario(request);

        Salida salida = new Salida();
        salida.setFecha(request.getFecha());
        salida.setTipo(request.getTipo());
        salida.setDestino(request.getDestino());
        salida.setFolio(request.getFolio());
        salida.setDestinatario(destinatario);

        BigDecimal totalSalida = request.getProductos()
                    .stream()
                    .map(SalidaProductoRequestDTO::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getProductos().forEach(p -> {
            Inventario inventario = inventarioService.restarStock(p);

            SalidaProductos sp = new SalidaProductos();

            sp.setSalida(salida);
            sp.setInventario(inventario);
            sp.setCantidad(p.getCantidad());
            sp.setCostoTotal(p.getTotal());
            sp.setSubtotal(p.getSubtotal() != null ? p.getSubtotal() : null);

            listaProductos.add(sp);
        });

        loginService.settearUsuario();

        salida.setTotal(totalSalida);
        salida.setProductos(listaProductos);
        salidaRepository.save(salida);
        listaProductos.forEach(lp -> salidaProductosRepository.save(lp));
        registrarNotaSiAplica(salida, request.getNota());

        return salidaMapper.toResponse(salida, notaRepository.findBySalidaOrderByFechaAsc(salida));
    }

    private void validarCreacion(List<SalidaProductoRequestDTO> productos) {
        productos.forEach(pr -> {
            Inventario inventario = inventarioRepository.findById(pr.getIdInventario()).orElseThrow(() ->
                new ResourceNotFoundException("Inventario no encontrado con id " + pr.getIdInventario())
            );

            if (pr.getCantidad() > inventario.getCantidadDisponible()) {
                throw new ValidationException("Inventario con stock insuficiente");
            }
        });
    }

    private void validarEdicion(
            List<SalidaProductoRequestDTO> productosRequest,
            List<SalidaProductos> productosOriginales) {

        for (SalidaProductoRequestDTO pr : productosRequest) {

            Inventario inventario = inventarioRepository.findById(pr.getIdInventario())
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Inventario no encontrado con id " + pr.getIdInventario()
                    )
                );

            // Buscar cuánto tenía originalmente ese producto en la salida
            int cantidadOriginal = productosOriginales.stream()
                .filter(po -> po.getInventario().getIdInventario()
                    .equals(pr.getIdInventario()))
                .map(SalidaProductos::getCantidad)
                .findFirst()
                .orElse(0);

            int diferencia = pr.getCantidad() - cantidadOriginal;

            // Sólo validar si está aumentando
            if (diferencia > 0 && diferencia > inventario.getCantidadDisponible()) {
                throw new ValidationException(
                    "Inventario con stock insuficiente. Disponible: "
                    + inventario.getCantidadDisponible()
                );
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SalidaResponseDTO> listarSalidas(String nombreProducto, String destino, String tipo,
                                                    LocalDate fechaInicio, LocalDate fechaFin, Long folio,
                                                    String destinatario, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());

        DestinoSalida destinoEnum = (destino != null && !destino.isBlank())
            ? DestinoSalida.valueOf(destino.toUpperCase()) : null;

        TipoSalida tipoEnum = (tipo != null && !tipo.isBlank())
            ? TipoSalida.valueOf(tipo.toUpperCase()) : null;

        Page<Salida> salidas = salidaRepository.findByFiltros(nombreProducto, destinoEnum, tipoEnum, fechaInicio, fechaFin, folio, destinatario, pageable);
        return salidas.map(s -> salidaMapper.toResponse(s, notaRepository.findBySalidaOrderByFechaAsc(s)));
    }

    @Override
    @Transactional
    public SalidaResponseDTO editarSalida(SalidaRequestDTO request) {

        Destinatario destinatario = validarFolioDestinatario(request);

        Salida salida = salidaRepository.findById(request.getIdSalida())
            .orElseThrow(() -> new ResourceNotFoundException("Salida no encontrada"));

        salida.setFecha(request.getFecha());
        salida.setTipo(request.getTipo());
        salida.setDestino(request.getDestino());
        salida.setFolio(request.getFolio());
        salida.setDestinatario(destinatario);

        validarEdicion(request.getProductos(), salida.getProductos());

        Map<Long, SalidaProductos> productosActuales = salida.getProductos().stream()
            .collect(Collectors.toMap(
                sp -> sp.getInventario().getIdInventario(),
                Function.identity()
            ));

        Map<Long, SalidaProductoRequestDTO> productosRequest = request.getProductos().stream()
            .collect(Collectors.toMap(
                SalidaProductoRequestDTO::getIdInventario,
                Function.identity()
            ));

        salida.getProductos().removeIf(sp -> {
            Long idInventario = sp.getInventario().getIdInventario();

            if (!productosRequest.containsKey(idInventario)) {
                // devolver stock
                Inventario inventario = sp.getInventario();
                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() + sp.getCantidad()
                );
                return true; 
            }
            return false;
        });

        for (SalidaProductoRequestDTO dto : request.getProductos()) {

            Inventario inventario = inventarioRepository.findById(dto.getIdInventario())
                .orElseThrow(() ->
                    new ResourceNotFoundException("Inventario no encontrado")
                );

            if (productosActuales.containsKey(dto.getIdInventario())) {
                SalidaProductos existente = productosActuales.get(dto.getIdInventario());

                int cantidadOriginal = existente.getCantidad();
                int cantidadNueva = dto.getCantidad();
                int diferencia = cantidadNueva - cantidadOriginal;

                if (diferencia > 0 && diferencia > inventario.getCantidadDisponible()) {
                    throw new ValidationException(
                        "Stock insuficiente para el producto " + inventario.getIdInventario()
                    );
                }

                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() - diferencia
                );

                existente.setCantidad(cantidadNueva);

                existente.setCostoTotal(dto.getTotal());
                existente.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal() : null);

            } else {
                if (dto.getCantidad() > inventario.getCantidadDisponible()) {
                    throw new ValidationException(
                        "Stock insuficiente para el producto " + inventario.getIdInventario()
                    );
                }

                inventario.setCantidadDisponible(
                    inventario.getCantidadDisponible() - dto.getCantidad()
                );

                SalidaProductos nuevo = new SalidaProductos();
                nuevo.setSalida(salida);
                nuevo.setInventario(inventario);
                nuevo.setCantidad(dto.getCantidad());
                nuevo.setCostoTotal(dto.getTotal());

                salida.getProductos().add(nuevo);
            }
        }

        BigDecimal totalSalida = salida.getProductos().stream()
        .map(SalidaProductos::getCostoTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        loginService.settearUsuario();

        salida.setTotal(totalSalida);

        salidaRepository.save(salida);
        registrarNotaSiAplica(salida, request.getNota());
        return salidaMapper.toResponse(salida, notaRepository.findBySalidaOrderByFechaAsc(salida));
    }

    @Override
    @Transactional
    public SalidaResponseDTO devolverProducto(Long idSalidaProducto) {
        SalidaProductos sp = salidaProductosRepository.findById(idSalidaProducto)
            .orElseThrow(() -> new ResourceNotFoundException("Producto de salida no encontrado con id " + idSalidaProducto));

        Salida salida = sp.getSalida();

        inventarioService.revertirSalida(sp);

        salida.getProductos().removeIf(p -> p.getIdSalidaProducto().equals(idSalidaProducto));

        BigDecimal totalSalida = salida.getProductos().stream()
            .map(SalidaProductos::getCostoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        salida.setTotal(totalSalida);

        salidaRepository.save(salida);
        return salidaMapper.toResponse(salida, notaRepository.findBySalidaOrderByFechaAsc(salida));
    }

    @Override
    @Transactional
    public void eliminarSalida(Long idSalida, String password) {
        Usuario usuario = loginService.getUsuarioActual();

        if (!"ADMIN".equals(usuario.getRol().getNombre())) {
            throw new ValidationException("Sólo un administrador puede eliminar una salida");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new ValidationException("Contraseña incorrecta");
        }

        Salida salida = salidaRepository.findById(idSalida)
            .orElseThrow(() -> new ResourceNotFoundException("Salida no encontrada"));

        if (!salida.getProductos().isEmpty()) {
            throw new ValidationException("Sólo se puede eliminar una salida que no tenga productos");
        }

        notaRepository.deleteAll(notaRepository.findBySalidaOrderByFechaAsc(salida));
        salidaRepository.delete(salida);
    }
}
