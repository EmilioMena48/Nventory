package com.nventory.controller;

import com.nventory.DTO.*;
import com.nventory.repository.*;
import com.nventory.service.OrdenCompraService;
import java.util.List;
import java.util.Optional;

public class OrdenDeCompraController {
    private final OrdenDeCompraRepository ordenCompraRepo = new OrdenDeCompraRepository();
    private final EstadoOrdenDeCompraRepository estadoOrdenDeCompraRepo = new EstadoOrdenDeCompraRepository();
    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepo = new OrdenDeCompraArticuloRepository();
    private final ArticuloProveedorRepository articuloProveedorRepo = new ArticuloProveedorRepository();
    private final ProveedorRepository proveedorRepo = new ProveedorRepository();
    private final ArticuloRepository articuloRepo = new ArticuloRepository();
    private final TipoStockMovimientoRepository tipoStockMovimientoRepo = new TipoStockMovimientoRepository();
    private final StockMovimientoRepository stockMovimientoRepo = new StockMovimientoRepository();


    private final OrdenCompraService ordenCompraService = new OrdenCompraService(
            ordenCompraRepo,estadoOrdenDeCompraRepo,ordenDeCompraArticuloRepo,articuloProveedorRepo,
            proveedorRepo,articuloRepo,tipoStockMovimientoRepo,stockMovimientoRepo
    );

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        return ordenCompraService.obtenerTodasOrdenesDeCompra();
    }

    public void enviarOrdenDeCompra(Long id){

        ordenCompraService.enviarOrdenCompra(id);
    }

    public void cancelarOrdenDeCompra(Long id){
        ordenCompraService.cancelarOrdenCompra(id);

    }

    public Optional<List<String>> recibirOrdenDeCompra(Long id){
        return ordenCompraService.recibirOrdenCompra(id);
    }


    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long idOrdenDeCompra) {
        return ordenCompraService.obtenerArticulosDeOrden(idOrdenDeCompra);
    }

    public List<ArticuloProveedorDTO> obtenerArticulosProveedorDisponibles(Long idOrdenDeCompra) {

        return ordenCompraService.obtenerArticulosDeProveedor(idOrdenDeCompra);
    }

    public void agregarArticuloAOrden(Long idOrdenDeCompra, Long  idArticuloProveedor, int cantidad) {
        ordenCompraService.agregarArticuloAOrden(idOrdenDeCompra, idArticuloProveedor, cantidad);
    }

    public void eliminarArticuloDeOrden(Long idOrdenDeCompra, Long idOrdenDeCompraArticulo) {
        ordenCompraService.eliminarArticuloDeOrden(idOrdenDeCompra, idOrdenDeCompraArticulo);
    }

    public void modificarCantidadArticulo(Long idOrdenDeCompra, Long idOrdenCompraA, int nuevoCantidad) {
        ordenCompraService.modificarCantidadArticulo(idOrdenDeCompra, idOrdenCompraA, nuevoCantidad);
    }

    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        return ordenCompraService.obtenerProveedores();
    }

    public Long crearOrdenDeCompra(Long codProveedor) {
        return ordenCompraService.crearOrdenDeCompra(codProveedor);
    }

    public List<ArticuloDTO> obtenerTodosLosArticulos() {
        return ordenCompraService.obtenerTodosLosArticulos();
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorProveedor(Long codProveedor) {
        return ordenCompraService.buscarOrdenAbiertaPorProveedor(codProveedor);
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorArticulo(Long codArticulo) {
        return ordenCompraService.buscarOrdenAbiertaPorArticulo(codArticulo);
    }

    public SugerenciaOrdenDTO obtenerSugerenciaParaArticulo(Long codArticulo) {
        return ordenCompraService.obtenerSugerenciaParaArticulo(codArticulo);
    }

    public List<ProveedorArticuloDTO> obtenerProveedoresParaArticulo(Long codArticulo) {
        return ordenCompraService.obtenerProveedoresParaArticulo(codArticulo);
    }

    public String obtenerEstadoDeUnaOrden(Long codOrdenCompra) {
        return ordenCompraService.obtenerEstadoDeUnaOrden(codOrdenCompra);
    }

    public Long buscarArticuloProveedorPorRelacion(Long codArticulo, Long codProveedor) {
        return ordenCompraService.buscarArticuloProveedorPorRelacion(codArticulo, codProveedor);
    }

    public List<String> generarOrdenesDelDia() {
        return ordenCompraService.generarOrdenesDelDia();
    }
}

