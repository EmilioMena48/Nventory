package com.nventory.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ArticuloDTO {

    private Long codArticulo;
    private BigDecimal costoAlmacenamiento;
    private BigDecimal nivelServicioArticulo;
    private BigDecimal precioArticulo;
    private int demandaArt;
    private String nombreArticulo;
    private String descripcionArticulo;
    private LocalDateTime fechaHoraBajaArticulo;
    private Integer stockActual;
    private int diasEntreRevisiones;
    private String proveedorPredeterminado;

}
