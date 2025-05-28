package com.nventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
public class StockMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int cantidad;
    private String comentario;
    private LocalDateTime fechaHoraMovimiento;


    //Relacion StockMovimiento - OrdenDeCompraArticulo
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "codOrdenDeCompraArticulo")
    private OrdenDeCompraArticulo ordenDeCompraArticulo;

    //Relacion StockMovimiento - TipoStockMovimiento
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codTipoStockMovimiento")
    private TipoStockMovimiento tipoStockMovimiento;


    //Relacion StockMovimiento - Articulo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codArticulo")
    private Articulo articulo;


    //Relacion StockMovimiento - VentaArticulo
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ordenVentaArticulo")
    private  VentaArticulo ventaArticulo;




}
