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
    private LocalDateTime fechaHoraBajaConfigInv;
    private int inventarioMaximoIF;
    private int loteOptimoLF;
    private String nombreConfiguracionInventario;
    private int puntoPedidoLF;
    private int stockSeguridadIF;
    private int stockSeguridadLF;


    //Relacion ConfiguracionInventario - TipoModeloInventario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codTipoModeloInventario")
    private TipoModeloInventario tipoModeloInventario;

}
