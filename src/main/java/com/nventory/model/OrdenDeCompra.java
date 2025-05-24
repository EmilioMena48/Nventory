package com.nventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
public class OrdenDeCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codOrdenDeCompra;
    private LocalDateTime fechaHoraEnvioProv;
    private BigDecimal totalOrdenDeCompra = BigDecimal.ZERO;

    //Relacion Orden Compra - Proveedor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codProveedor")
    private Proveedor proveedor;

    //Relacion OrdenCompra - OrdenCompraArticulo
    @OneToMany(mappedBy = "ordenDeCompra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenDeCompraArticulo> ordenDeCompraArticulo;

    //Relacion OrdenDeCompra - EstadoOrdenDeCompra
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codEstadoOC")
    private EstadoOrdenDeCompra estadoOrdenDeCompra;




}
