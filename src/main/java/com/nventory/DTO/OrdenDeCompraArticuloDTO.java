package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrdenDeCompraArticuloDTO {

    private Long codOrdenCompraA;
    private String nombreArticulo;
    private int cantidadSolicitadaOCA;
    private String precioUnitarioOCA;
    private String subTotalOCA;

}
