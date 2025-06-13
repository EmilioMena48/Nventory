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
    private TableView<Articulo> tablaArticulos;
    private final ObservableList<Articulo> listaArticulos = FXCollections.observableArrayList();


    /*public MaestroArticuloController() {
        cargarArticulosDummy(); // o cargar desde base de datos
    }*/

    public void setTablaArticulos(TableView<Articulo> tablaArticulos) {
        this.tablaArticulos = tablaArticulos;
        this.tablaArticulos.setItems(listaArticulos);
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


    //Metodo de ejemplo para mostrar en pantalla
    /*private void cargarArticulosDummy() {
        Articulo a1 = new Articulo();
        a1.setCodArticulo(1L);
        a1.setNombreArticulo("Shampoo revitalizante");
        a1.setDescripcionArticulo("Shampoo de uso diario");
        a1.setStockActual(50);
        a1.setCostoAlmacenamiento(new BigDecimal("12.5"));
        a1.setPrecioArticulo(new BigDecimal("30.0"));
        a1.setNivelServicioArticulo(new BigDecimal("0.95"));
        a1.setDesviacionEstandarArticulo(3);
        a1.setDiasEntreRevisiones(20);
        a1.setDemandaArt(100);
        a1.setFechaHoraBajaArticulo(null);

        listaArticulos.addAll(a1);
    }*/

    //----------Buscar Articulos en stock de seguridad---------------------
    public List<Articulo> listarArticulosEnStockSeg () {return articuloService.listarArticulosEnStockSeg();}

    //----------Calcular CGI de artículos---------------------
    public List<CGIDTO> calcularCGI () {return articuloService.calcularCGI();}

    public Integer obtenerStockActual (String nombre) {return articuloService.obtenerStockActual(nombre);}

    //----------Realizar ajuste de inventario---------------------
    public void realizarAjusteInventario (StockMovimientoDTO stockMovimientoDTO) {articuloService.realizarAjusteInventario(stockMovimientoDTO);}

}
