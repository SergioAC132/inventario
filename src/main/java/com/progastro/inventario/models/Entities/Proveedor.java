package com.progastro.inventario.models.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    name = "proveedores"
)
public class Proveedor {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @Column(nullable= false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 13)
    private String rfc;

    @Column(name = "tipo_persona")
    private int tipoPersona;

    @Column(nullable= false)
    private String telefono;

    @Column
    private String correo;

    @Column(name = "codigo_postal")
    private Integer codigoPostal;
}
