package com.nventory.service;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.model.*;
import com.nventory.repository.*;

import java.util.ArrayList;
import java.util.List;

public class ArticuloService {

    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepository = new OrdenDeCompraArticuloRepository();
    private final ArticuloProveedorRepository articuloProveedorRepository = new ArticuloProveedorRepository();
    ArticuloRepository articuloRepository;
    public ArticuloService(ArticuloRepository articuloRepository) {this.articuloRepository = articuloRepository;}
    public ArticuloService() {this.articuloRepository = new ArticuloRepository();}

    public void actualizarStock(Long idArticulo, Integer cantidad) {
       Articulo articulo = articuloRepository.buscarPorId(idArticulo);
       Integer cantidadVieja = articulo.getStockActual();
       Integer cantidadNueva = cantidadVieja + cantidad;
        articulo.setStockActual(cantidadNueva);
        articuloRepository.guardar(articulo);
    }

    //-----------------Metodo del service para dar de baja un articulo------------------------------------------
    public void darDeBajaArticulo(ArticuloDTO articuloDTO){
        Articulo articulo = articuloRepository.buscarPorId(articuloDTO.getCodArticulo());

        //Se valida que el articulo no tenga stock para darle de baja
        if(articulo.getStockActual() > 0){
            throw new IllegalStateException("No se puede dar de baja: el artículo aún tiene stock.");
        }

        //Se valida que el articulo no tenga ordenes de compra pendiente o enviada para poder dar de baja
        ArticuloProveedor proveedorPredeterminado = articulo.getArticuloProveedor();

        //Si no hay proveedor predeterminado, permitir la baja directamente
        if(proveedorPredeterminado != null ){
            Long codArticuloProveedor = proveedorPredeterminado.getCodArticuloProveedor();

            //Buscar ordenesCompraArticulo relacionadas a ese codArticuloProveedor
            List<OrdenDeCompraArticulo> ordenesCompraArticulo = ordenDeCompraArticuloRepository.buscarOrdenCompraArticuloDeArticulo(codArticuloProveedor);

            for(OrdenDeCompraArticulo oca : ordenesCompraArticulo){
                OrdenDeCompra orden = oca.getOrdenDeCompra();
                EstadoOrdenDeCompra estado = orden.getEstadoOrdenDeCompra();
                String nombreEstado = estado.getNombreEstadoOC();

                //Si es Pendiente o Enviada, no se puede hacer la baja
                if("Pendiente".equalsIgnoreCase(nombreEstado) || "Enviada".equalsIgnoreCase(nombreEstado)){

                    throw new IllegalStateException("No se puede dar de baja un artículo con órdenes de compra pendientes o enviadas.");
                }
            }
        }

        // Si pasa las validaciones, dar de baja
        articulo.setFechaHoraBajaArticulo(articuloDTO.getFechaHoraBajaArticulo());
        articuloRepository.guardar(articulo);
    }

    //-----------------Metodo del service para obtener Proveedores del articulo seleccionado------------------------
    public List<ArticuloProveedorDTO> obtenerProveedoresDeEseArticulo(Long codArticulo){
        List<ArticuloProveedorDTO> articulosProDTO = new ArrayList<>();
        List<ArticuloProveedor> articulosPro = articuloProveedorRepository.buscarTodosArticuloProveedor(codArticulo);

        for(ArticuloProveedor articulosProv : articulosPro){
            ArticuloProveedorDTO articuloProveedorDTO = new ArticuloProveedorDTO();
            articuloProveedorDTO.setId(articulosProv.getCodArticuloProveedor());
            articuloProveedorDTO.setNombre(articulosProv.getProveedor().getNombreProveedor());

            articulosProDTO.add(articuloProveedorDTO);
        }
        return articulosProDTO;
    }

    //----------------Metodo del service para asignar el proveedor predeterminado de un articulo-------------
    public void asignarProveedorPredeterminado(Long codArticuloProveedor){
        ArticuloProveedor articuloProveedorTraido = articuloProveedorRepository.buscarPorId(codArticuloProveedor);
        if (articuloProveedorTraido != null) {
            Articulo articulo = articuloProveedorTraido.getArticulo();
            articulo.setArticuloProveedor(articuloProveedorTraido);
            articuloRepository.guardar(articulo);
        }
    }

    //-----------------Metodo del service para listar todos los articulos-----------------------------
    public List<Articulo> listarArticulos() {
        return articuloRepository.buscarTodos();
    }

    public Articulo buscarArticuloPorId(Long id) {
        Articulo articulo = articuloRepository.buscarPorId(id);
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo con ID " + id + " no existe.");
        }
        return articulo;
    }
}

