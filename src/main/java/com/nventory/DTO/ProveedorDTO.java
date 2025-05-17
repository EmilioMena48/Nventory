package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProveedorDTO {
    private Long codProveedor;
    private String descripcionProveedor;
    private LocalDateTime fechaHoraBajaProveedor;
    private String nombreProveedor;

    public ProveedorDTO(long codProveedor, String nombreProveedor) {
        this.codProveedor = codProveedor;
        this.nombreProveedor = nombreProveedor;
    }
}
