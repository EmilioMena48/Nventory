package com.nventory.interfaces;

public interface ModuloOrdenDeCompra {
    /*
     * Modulo Orden de Compra
     *
     * Este modulo permite gestionar las ordenes de compra de la aplicacion.
     *
     * @author Ignacio Canzio
     * @version 1.0
     */;
    void AltaOrdenDeCompra();
    void ModificarOrdenDeCompra();
    void EliminarOrdenDeCompra();
    void ListarOrdenesDeCompra();
    void BuscarOrdenDeCompra();
    void ListarArticulosxOrdenDeCompra();
    void AsociarArticuloOrdenDeCompra();
}