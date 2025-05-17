package com.nventory.interfaces;

public interface ModuloMaestoArticulo {
    /*
     * Modulo Maestro Articulo
     *
     * Este modulo permite gestionar los articulos de la aplicacion.
     *
     * @author Fernando Rubiales
     * @author Juan Cruz Yanardi
     * @version 1.0
     */
    void AltaArticulo();
    void ModificarArticulo();
    void EliminarArticulo();
    void ListarArticulos();
    void BuscarArticulo();
    void ListarProveedoresxArticulo();
    void ModificarEntrategia();
    void SeleccionarProvedorPredeterminado();
    void CalcularCGI();
    void ListadoPuntoDePedido();
    void ListadoArticulosEnStockSeguridad();
    void AjusteDiscrecional();
}
