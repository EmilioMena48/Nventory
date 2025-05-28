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
    private BigDecimal costoCapitalInmovilizado;
    private BigDecimal costoCompra;
    private int demandaArt;
    private String nombreArticulo;
    private String descripcionArticulo;
    private LocalDateTime fechaHoraBajaArticulo;
    private Integer stockActual;

    /**
     * Ya cree la relación articuloProveedor - ConfiguraciónInventario
     * Lo dejo por si se necesita, para que explote
     */
    //Relacion Articulo - ConfiguracionInventario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codConfiguracionInventario")
    private ConfiguracionInventario configuracionInventario;

    //Relacion entre Articulo - ArticuloProveedor

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ProveedorPredeterminado")
    private ArticuloProveedor articuloProveedor;


}
