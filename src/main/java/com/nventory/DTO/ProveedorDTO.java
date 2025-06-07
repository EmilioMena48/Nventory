package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProveedorDTO {
    private Long codProveedor;
    private String descripcionProveedor;
    private String nombreProveedor;

    public ProveedorDTO(long codProveedor, String nombreProveedor) {
        this.codProveedor = codProveedor;
        this.nombreProveedor = nombreProveedor;
    }
}
