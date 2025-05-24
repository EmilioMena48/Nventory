package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloProveedorDTO {
    private Long id;
    private String nombre;
    private String precioUnitario;
}
