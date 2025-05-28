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
public class ArticuloProveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codArticuloProveedor;
    private BigDecimal costoEnvio;
    private BigDecimal costoPedido;
    private int demoraEntregaDias;
    private LocalDateTime fechaHoraBajaArticuloProveedor;
    private BigDecimal precioUnitario;

    //Relacion ArticuloProveedor - Articulo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codArticulo")
    private Articulo articulo;

    //Relacion ArticuloProveedor - Proveedor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codProveedor")
    private Proveedor proveedor;

    //Relacion ArticuloProveedor - ConfiguracionInventario
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codConfiguracionInventario")
    private ConfiguracionInventario configuracionInventario;
}