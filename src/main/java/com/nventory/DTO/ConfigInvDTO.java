package com.nventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ConfigInvDTO {
    private int inventarioMaximo;
    private int loteOptimo;
    private int puntoPedido;
    private int stockSeguridad;
    private int cantidadPedir;
    private String nombreModeloInventario;
}