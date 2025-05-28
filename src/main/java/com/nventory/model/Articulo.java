package com.nventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity

public class Articulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codArticulo;
    private BigDecimal costoAlmacenamiento;
    private int demandaArt;
    private String nombreArticulo;
    private String descripcionArticulo;
    private LocalDateTime fechaHoraBajaArticulo;
    private Integer stockActual;
    private int desviacionEstandarArticulo;
    private int diasEntreRevisiones;
    private BigDecimal nivelServicioArticulo;
    private BigDecimal precioArticulo;

    //Relacion entre Articulo - ArticuloProveedor

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ProveedorPredeterminado")
    private ArticuloProveedor articuloProveedor;


}
