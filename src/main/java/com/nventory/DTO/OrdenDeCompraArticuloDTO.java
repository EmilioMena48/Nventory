package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrdenDeCompraArticuloDTO {

    private Long id;
    private String nombreArticulo;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String nombreProveedor;
    private BigDecimal precioTotal;

}
