package com.progastro.inventario.models.Entities;

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
import jakarta.persistence.UniqueConstraint;
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
    name = "productos",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"codigo", "id_marca"})
    }
)
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false, length = 10)
    private String codigo;

    @Column(nullable = false, length=100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(
        name = "id_marca",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_producto_marca")
    )
    private Marca marca;
}