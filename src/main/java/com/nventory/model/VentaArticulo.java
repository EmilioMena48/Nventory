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
public class VentaArticulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordenVentaArticulo")
    private Long ordenVentaArticulo;
    private int cantidadVendida;
    private BigDecimal precioVenta;
    private BigDecimal subTotalVenta;

    //Relacion VentaArticulo - Articulo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codArticulo")
    private Articulo articulo;


    //private Venta venta;



}
