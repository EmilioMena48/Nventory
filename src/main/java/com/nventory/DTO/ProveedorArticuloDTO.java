package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorArticuloDTO {
    private Long codProveedor;
    private String nombreProveedor;
    private int demoraEntregaDias;
    private String precioUnitario;
    private String costoPedido;
}
