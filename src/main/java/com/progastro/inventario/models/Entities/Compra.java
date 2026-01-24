package com.progastro.inventario.models.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import com.progastro.inventario.models.Enums.EstatusCompra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    name = "compras",
    uniqueConstraints= {
        @UniqueConstraint(columnNames= {"numero_factura", "id_proveedor"})
    }
)
public class Compra {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;

    @Column(nullable= false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_proveedor",
        nullable= false,
        foreignKey=@ForeignKey(name = "fk_compra_proveedor")
    )
    private Proveedor proveedor;

    @Column(name = "numero_factura", nullable= false, length= 50)
    private String numeroFactura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstatusCompra estatus;



    
    
}
