package com.nventory.interfaces;

import com.nventory.DTO.VentaDTO;

import java.util.List;

public interface ModuloVenta {
    /*
     * Modulo Venta
     *
     * Este modulo permite gestionar las ventas de la aplicacion.
     *
     * @author Emilio Mena
     * @version 1.0
     */
    void AltaVenta(VentaDTO ventaDTO);
    List<VentaDTO> ListarVentas();
    void BuscarVenta();
}