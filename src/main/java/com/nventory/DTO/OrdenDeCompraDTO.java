package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrdenDeCompraDTO {

    private Long codigo;
    private String estado;
    private String proveedor;
    private String totalOrden;
}
