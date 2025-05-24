package com.nventory.controller;

import com.nventory.DTO.*;
import com.nventory.service.OrdenCompraService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdenDeCompraController {
    private final OrdenCompraService ordenCompraService = new OrdenCompraService();

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        return ordenCompraService.obtenerTodasOrdenesDeCompra();
    }

    public void enviarOrdenDeCompra(Long id){

        ordenCompraService.enviarOrdenCompra(id);
    }

    public void cancelarOrdenDeCompra(Long id){
        ordenCompraService.cancelarOrdenCompra(id);

    }

    public void recibirOrdenDeCompra(Long id){
        ordenCompraService.recibirOrdenCompra(id);
    }


    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long idOrdenDeCompra) {
        return ordenCompraService.obtenerArticulosDeOrden(idOrdenDeCompra);
    }

    public List<ArticuloProveedorDTO> obtenerArticulosProveedorDisponibles(Long idOrdenDeCompra) {
       //Reotrnar todos los ArticulosProveedor del Proveedor asignado a esa OrdenDeCompra
        return null;
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

    public Long crearOrdenDeCompraPorArticulo(Long codArticulo, Long codProveedor, int cantidadSolicitada) {
        return ordenCompraService.crearOrdenDeCompraPorArticulo(codArticulo, codProveedor, cantidadSolicitada);
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorProveedor(Long codProveedor) {
        return ordenCompraService.buscarOrdenAbiertaPorProveedor(codProveedor);
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorArticulo(Long codArticulo) {
        return ordenCompraService.buscarOrdenAbiertaPorArticulo(codArticulo);
    }

    public SugerenciaOrdenDTO obtenerSugerenciaParaArticulo(Long codArticulo) {
        return null;
    }

    public List<ProveedorArticuloDTO> obtenerProveedoresParaArticulo(Long codArticulo) {
        return null;
    }

    public String obtenerEstadoDeUnaOrden(Long codOrdenCompra) {
        return ordenCompraService.obtenerEstadoDeUnaOrden(codOrdenCompra);
    }
}

