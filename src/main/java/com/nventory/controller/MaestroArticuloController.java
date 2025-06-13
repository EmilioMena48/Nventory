package com.nventory.controller;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.CGIDTO;
import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.model.Articulo;
import com.nventory.service.ArticuloService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.util.List;

public class MaestroArticuloController {
    private final ArticuloService articuloService = new ArticuloService();

    //-----------Obtener todos los articulos para mostrar en pantalla------------------------------
    public List<ArticuloDTO> obtenerTodosArticulos(){
        return articuloService.obtenerTodosArticulos();

    }

    //-------------Obtener el articulo por id---------------------------------------------
    public ArticuloDTO obtenerArticuloPorId(Long codArticulo) {
        return articuloService.obtenerArticuloPorId(codArticulo);
    }

    //----------Edicion de campos de articulos---------------------
    public void actualizarArticulo(ArticuloDTO articuloDTO){
        articuloService.modificarArticulo(articuloDTO);
    }

    //----------Alta de articulos---------------------
    public void darDeAltaArticulo(ArticuloDTO articuloDTO){
        articuloService.crearArticulo(articuloDTO);
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

    public List<ArticuloDTO> obtenerArticulosParaReponer() {
        return articuloService.obtenerArticulosReponer();
    }

    //--------Metodo para buscar art por nombre, lo uso en venta
    public ArticuloDTO buscarArtPorNombre (String nombre){return articuloService.buscarArtPorNombre(nombre);}

    //----------Buscar Articulos que no estén dados de baja---------------------
    public List<ArticuloDTO> listarArticulosDisponibles(){ return articuloService.listarArticulosDisponibles(); }


    //----------Buscar Articulos en stock de seguridad---------------------
    public List<Articulo> listarArticulosEnStockSeg () {return articuloService.listarArticulosEnStockSeg();}

    //----------Calcular CGI de artículos---------------------
    public List<CGIDTO> calcularCGI () {return articuloService.calcularCGI();}

    public Integer obtenerStockActual (String nombre) {return articuloService.obtenerStockActual(nombre);}

    //----------Realizar ajuste de inventario---------------------
    public void realizarAjusteInventario (StockMovimientoDTO stockMovimientoDTO) {articuloService.realizarAjusteInventario(stockMovimientoDTO);}

}
