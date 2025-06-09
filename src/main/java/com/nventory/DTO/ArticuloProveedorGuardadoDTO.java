package com.nventory.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloProveedorGuardadoDTO {
    private BigDecimal costoPedido;
    private int demoraEntregaDias;
    private BigDecimal precioUnitario;
    private LocalDate fechaProxRevisionAP;
}