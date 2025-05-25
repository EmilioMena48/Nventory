package com.nventory.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProveedorEliminadoDTO {
    private Long codProveedor;
    private String descripcionProveedor;
    private String nombreProveedor;
    private LocalDateTime fechaHoraBajaProveedor;
}