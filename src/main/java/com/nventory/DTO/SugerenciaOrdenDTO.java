package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SugerenciaOrdenDTO {
    public Long codProveedor;
    public String NombreProveedorSugerido;
    public Integer CantidadSugerida;

}
