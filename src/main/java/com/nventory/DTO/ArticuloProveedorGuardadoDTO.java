package com.nventory.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ArticuloProveedorGuardadoDTO {
    private BigDecimal costoEnvio;
    private BigDecimal costoPedido;
    private int demoraEntregaDias;
    private BigDecimal precioUnitario;
}