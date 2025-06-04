package com.nventory.controller;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.model.Articulo;
import com.nventory.service.ArticuloService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.util.List;

public class MaestroArticuloController {
    private final ArticuloService articuloService = new ArticuloService();
    private TableView<Articulo> tablaArticulos;
    private final ObservableList<Articulo> listaArticulos = FXCollections.observableArrayList();



    public void setTablaArticulos(TableView<Articulo> tablaArticulos) {
        this.tablaArticulos = tablaArticulos;
        this.tablaArticulos.setItems(listaArticulos);
    }

    //----------Edicion de campos de articulos---------------------
    public void actualizarArticulo(ArticuloDTO articuloDTO){

    }

    //----------Alta de articulos---------------------
    public void darDeAltaArticulo(ArticuloDTO articuloDTO){

    }

    //----------Baja de articulos------------------------
    public void darDeBajaArticulo(ArticuloDTO articuloDTO){
        articuloService.darDeBajaArticulo(articuloDTO);
    }

    //---------Metodo del controller para obtener todos los Proveedores de un articulo
    public List<ArticuloProveedorDTO> obtenerProveedoresDeEseArticulo (Long codArticulo){
        return articuloService.obtenerProveedoresDeEseArticulo(codArticulo);
    }
    //--------Metodo del controller que asigna un proveedor predeterminado de una articulo
    public void asignarProveedorPredeterminado (Long codArticuloProveedor){
        articuloService.asignarProveedorPredeterminado(codArticuloProveedor);
    }

    //--------Metodo para buscar art por nombre, lo uso en venta
    public ArticuloDTO buscarArtPorNombre (String nombre){return articuloService.buscarArtPorNombre(nombre);}

    //----------Buscar Articulos que no estén dados de baja---------------------
    public List<ArticuloDTO> listarArticulosDisponibles(){ return articuloService.listarArticulosDisponibles(); }

    // Métodos para añadir, borrar, editar, etc.


}
