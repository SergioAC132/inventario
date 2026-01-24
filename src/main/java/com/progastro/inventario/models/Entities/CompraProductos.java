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
    name = "compra_productos"
)
public class CompraProductos {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_compra_producto")
    private Long idCompraProducto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_compra",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_compra_producto_compra")
    )
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_inventario",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_compra_producto_inventario")
    )
    private Inventario inventario;

    @Column(nullable= false)
    private Integer cantidad;

    /*Este se pone debido a que puede que no siempre se conozca el valor unitario,
    entonces sólo se pondría el costo total, pero lo ideal sería tener este dato para conocer IVA*/
    @Column(precision= 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "costo_total",nullable= false,
            precision= 12, scale= 2)
    private BigDecimal costoTotal;
}
