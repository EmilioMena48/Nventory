package com.nventory.controller;

import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.OrdenDeCompraArticuloDTO;
import com.nventory.DTO.OrdenDeCompraDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.service.OrdenCompraService;

import java.util.List;

public class OrdenDeCompraController {
    private final OrdenCompraService ordenCompraService = new OrdenCompraService();

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        return ordenCompraService.obtenerTodasOrdenesDeCompra();
    }

    public void enviarOrdenDeCompra(Long id){

    }

    public void cancelarOrdenDeCompra(Long id){

    }

    public void recibirOrdenDeCompra(Long id){

    }


    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long idOrdenDeCompra) {
        return null;
    }

    public List<ArticuloProveedorDTO> obtenerArticulosProveedorDisponibles(Long idOrdenDeCompra) {
       return null;
    }

    public void agregarArticuloAOrden(Long idOrdenDeCompra, int id, int cantidad) {
    }

    public void eliminarArticuloDeOrden(Long idOrdenDeCompra, Long id) {
    }

    public void modificarCantidadArticulo(Long idOrdenDeCompra, Long id, int nuevoCantidad) {
    }

    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        return null;
    }

    public Long crearOrdenDeCompra(Long codProveedor) {
        return null;
    }
}

