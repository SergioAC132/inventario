package com.progastro.inventario.models.Entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    name = "salida_productos"
)
public class SalidaProductos {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_salida_producto")
    private Long idSalidaProducto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_salida",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_salida_producto_salida")
    )
    private Salida salida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_inventario",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_compra_producto_inventario")
    )
    private Inventario inventario;

    @Column(nullable= false)
    private Integer cantidad;

    @Column(nullable= false, precision=10, scale=2)
    private BigDecimal subtotal;
}
