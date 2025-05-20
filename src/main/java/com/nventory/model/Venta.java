package com.nventory.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numeroVenta")
    private Long numeroVenta;
    private LocalDateTime fechaHoraVenta;
    private BigDecimal montoTotalVenta;

    //Relacion Venta - VentaArticulo
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "numeroVenta") //crea la fk en VentaArticulo
    private List<VentaArticulo> ventaArticulo = new ArrayList<>();

    public void addVentaArticulo(VentaArticulo ventaArticulo) {
        this.ventaArticulo.add(ventaArticulo);
    }

}
