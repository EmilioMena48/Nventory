package com.nventory.controller;

import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.DTO.OrdenDeCompraArticuloDTO;
import com.nventory.DTO.OrdenDeCompraDTO;
import com.nventory.DTO.ProveedorDTO;

import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompraController {

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        List<OrdenDeCompraDTO> ordenDeCompraDTOs = new ArrayList<OrdenDeCompraDTO>();
        OrdenDeCompraDTO ordenDeCompraDTO1 = new OrdenDeCompraDTO(123L, "Pendiente", "Coca-Cola", "19.99");
        ordenDeCompraDTOs.add(ordenDeCompraDTO1);

        OrdenDeCompraDTO ordComp2 = new OrdenDeCompraDTO(233L, "Enviada", "Arcos-SA", "1202330.54");
        ordenDeCompraDTOs.add(ordComp2);
        return ordenDeCompraDTOs;
    }

    public void enviarOrdenDeCompra(Long id){

    }

    public void cancelarOrdenDeCompra(Long id){

    }

    public void recibirOrdenDeCompra(Long id){

    }


    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long idOrdenDeCompra) {
        List<OrdenDeCompraArticuloDTO> articuloDTOS = new ArrayList<OrdenDeCompraArticuloDTO>();
        if (idOrdenDeCompra == 123L) {
            OrdenDeCompraArticuloDTO articuloDTO1 = new OrdenDeCompraArticuloDTO(13L, "CocaCola 1.5Lts", 12, "2000", "24000");
            articuloDTOS.add(articuloDTO1);
            return articuloDTOS;
        } else {
            return articuloDTOS;
        }

    }

    public List<ArticuloProveedorDTO> obtenerArticulosProveedorDisponibles(Long idOrdenDeCompra) {
        List<ArticuloProveedorDTO> artProvDTOS = new ArrayList<ArticuloProveedorDTO>();
            ArticuloProveedorDTO artProv1 = new ArticuloProveedorDTO(12, "Aquarius 500ml", "1000");
            artProvDTOS.add(artProv1);
            return artProvDTOS;
    }

    public void agregarArticuloAOrden(Long idOrdenDeCompra, int id, int cantidad) {
    }

    public void eliminarArticuloDeOrden(Long idOrdenDeCompra, Long id) {
    }

    public void modificarCantidadArticulo(Long idOrdenDeCompra, Long id, int nuevoCantidad) {
    }

    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        List<ProveedorDTO> proveedorDTOS = new ArrayList<ProveedorDTO>();
        ProveedorDTO prov1 = new ProveedorDTO(400L, "Juan Carlos");
        proveedorDTOS.add(prov1);
        return proveedorDTOS;
    }

    public Long crearOrdenDeCompra(Long codProveedor) {
        return (255L);
    }
}
