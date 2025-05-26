package com.nventory.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloProveedorGuardadoDTO {
    private BigDecimal costoEnvio;
    private BigDecimal costoPedido;
    private int demoraEntregaDias;
    private BigDecimal precioUnitario;
}