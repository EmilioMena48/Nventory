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
public class ConfiguracionInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codConfiguracionInventario;
    private LocalDateTime fechaHoraBajaConfiguracionInventario;
    private int inventarioMaximo;
    private int loteOptimo;
    private int puntoPedido;
    private int stockSeguridad;

    //Relacion ConfiguracionInventario - TipoModeloInventario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codTipoModeloInventario")
    private TipoModeloInventario tipoModeloInventario;

}
