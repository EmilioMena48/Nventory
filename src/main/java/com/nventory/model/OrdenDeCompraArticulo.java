package com.nventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
public class OrdenDeCompraArticulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codOrdenCompraA;
    private int cantidadSolicitadaOCA;
    private BigDecimal precioUnitarioOCA;
    private BigDecimal subTotalOCA;


    //Relacion OrdenDeCompraArticulo - ArticuloProveedor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codArticuloProveedor")
    private ArticuloProveedor articuloProveedor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codOrdenCompra")
    private OrdenDeCompra ordenDeCompra;


}
