package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrdenDeCompraDTO {

    private Long codOrdenDeCompra;
    private String estadoOrdenDeCompra;
    private String proveedor;
    private String totalOrden;
}
