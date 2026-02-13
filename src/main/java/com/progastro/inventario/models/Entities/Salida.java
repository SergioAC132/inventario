package com.progastro.inventario.models.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.progastro.inventario.models.Enums.DestinoSalida;
import com.progastro.inventario.models.Enums.TipoSalida;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "salidas"
)
public class Salida {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_salida")
    private Long idSalida;

    @Column(name="fecha", nullable= false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo", nullable = false, length = 15)
    private TipoSalida tipo;

    @Enumerated(EnumType.STRING)
    @Column(name="destino", nullable = false)
    private DestinoSalida destino;

    @OneToMany(mappedBy="salida", cascade = CascadeType.ALL, orphanRemoval= true, fetch= FetchType.LAZY)
    private List<SalidaProductos> productos = new ArrayList<>();
}