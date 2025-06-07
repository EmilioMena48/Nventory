package com.nventory.service;

import com.nventory.DTO.ArticuloDTO;
import com.nventory.DTO.ArticuloProveedorDTO;
import com.nventory.model.*;
import com.nventory.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticuloService {

    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepository = new OrdenDeCompraArticuloRepository();
    private final ArticuloProveedorRepository articuloProveedorRepository = new ArticuloProveedorRepository();

    private final ConfiguracionInventarioService configuracionInventarioService = new ConfiguracionInventarioService();

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

    public List<Articulo> listarArticulos() {
        return articuloRepository.buscarTodos();
    }

    //----------------------------Metodo para traerme todos los articulos a reponer---------------------------------
    public List<ArticuloDTO> obtenerArticulosReponer() {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<ArticuloDTO> articulosAReponer = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {

                //Leemos el proveedor predeterminado
                ArticuloProveedor provPredeterminado = articulo.getArticuloProveedor();

                if (provPredeterminado != null) {

                    int puntoPedido = articulo.getArticuloProveedor().getConfiguracionInventario().getPuntoPedido();
                    int stockActual = articulo.getStockActual();

                    if (stockActual <= puntoPedido) {
                        Long codArticuloProveedor = provPredeterminado.getCodArticuloProveedor();
                        //Buscar ordenesCompraArticulo relacionadas a ese codArticuloProveedor
                        List<OrdenDeCompraArticulo> ordenesCompraArticulo = ordenDeCompraArticuloRepository.buscarOrdenCompraArticuloDeArticulo(codArticuloProveedor);

                        boolean tieneOrdenPendienteOEnviada = false;
                        for (OrdenDeCompraArticulo oca : ordenesCompraArticulo) {
                            OrdenDeCompra orden = oca.getOrdenDeCompra();
                            EstadoOrdenDeCompra estado = orden.getEstadoOrdenDeCompra();
                            String nombreEstado = estado.getNombreEstadoOC();

                            if ("Pendiente".equalsIgnoreCase(nombreEstado) || "Enviada".equalsIgnoreCase(nombreEstado)) {
                                tieneOrdenPendienteOEnviada = true;
                                break;
                            }
                        }
                        if (tieneOrdenPendienteOEnviada == false) {
                            ArticuloDTO articuloDTO = new ArticuloDTO();
                            articuloDTO.setCodArticulo(articulo.getCodArticulo());
                            articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                            articulosAReponer.add(articuloDTO);
                        }
                    }
                }
            }
        }
        return articulosAReponer;
    }

    //-----------------Metodo del service para buscar articulo por ID---------------------------------
    public Articulo buscarArticuloPorId(Long id) {
        Articulo articulo = articuloRepository.buscarPorId(id);
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo con ID " + id + " no existe.");
        }
        return articulo;
    }

    //-----------------Metodo para buscar art por nombre, lo uso en ventas--------------------------------
    public ArticuloDTO buscarArtPorNombre(String nombre) {
        ArticuloDTO artDTO = new ArticuloDTO();
        Articulo art = articuloRepository.buscarArticuloPorNombre(nombre);
        artDTO.setNombreArticulo(art.getNombreArticulo());
        artDTO.setCodArticulo(art.getCodArticulo());
        artDTO.setDemandaArt(art.getDemandaArt());
        artDTO.setStockActual(art.getStockActual());
        artDTO.setDescripcionArticulo(art.getDescripcionArticulo());
        artDTO.setCostoAlmacenamiento(art.getCostoAlmacenamiento());
        artDTO.setPrecioArticulo(art.getPrecioArticulo());


        return artDTO;
    }

    //-----------------Metodo para buscar art que no estén dados de baja------------------------------------------
    public List<ArticuloDTO> listarArticulosDisponibles () {
        List<Articulo> articulos = articuloRepository.buscarTodos();
        List<ArticuloDTO> articulosDisponibles = new ArrayList<>();
        for (Articulo articulo : articulos) {
            if (articulo.getFechaHoraBajaArticulo() == null) {
                ArticuloDTO articuloDTO = new ArticuloDTO();
                articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
                articulosDisponibles.add(articuloDTO);
            }
        }
        return articulosDisponibles;
    }



    //-----------------Metodo del service crear un artículo-----------------------------
    public void crearArticulo(ArticuloDTO articuloDTO) {
        Long codArticulo = articuloDTO.getCodArticulo();
        BigDecimal costoAlmacenamiento = articuloDTO.getCostoAlmacenamiento();
        BigDecimal nivelServicioArticulo = articuloDTO.getNivelServicioArticulo();
        BigDecimal precioArticulo = articuloDTO.getPrecioArticulo();
        String nombreArticulo = articuloDTO.getNombreArticulo();
        String descripcionArticulo = articuloDTO.getDescripcionArticulo();
        int demandaArt = articuloDTO.getDemandaArt();
        LocalDateTime fechaHoraBajaArticulo = articuloDTO.getFechaHoraBajaArticulo();
        Integer stockActual = articuloDTO.getStockActual();
        int diasEntreRevisiones = articuloDTO.getDiasEntreRevisiones();
        int desviacionEstandarArticulo = articuloDTO.getDesviacionEstandarArticulo();

        Articulo articulo = Articulo.builder()
                .codArticulo(codArticulo)
                .costoAlmacenamiento(costoAlmacenamiento)
                .nivelServicioArticulo(nivelServicioArticulo)
                .precioArticulo(precioArticulo)
                .nombreArticulo(nombreArticulo)
                .descripcionArticulo(descripcionArticulo)
                .demandaArt(demandaArt)
                .fechaHoraBajaArticulo(fechaHoraBajaArticulo)
                .stockActual(stockActual)
                .diasEntreRevisiones(diasEntreRevisiones)
                .desviacionEstandarArticulo(desviacionEstandarArticulo)
                .build();
        articuloRepository.guardar(articulo);
    }

    //-----------------Metodo del service modificar un artículo-----------------------------
    public void modificarArticulo(ArticuloDTO articuloDTO) {

        Long codArticulo = articuloDTO.getCodArticulo();
        Articulo articuloExistente = articuloRepository.buscarPorId(codArticulo);
        if (articuloExistente == null) {
            throw new IllegalArgumentException("El articulo con ID " + codArticulo + " no existe.");
        }

        if (articuloExistente.getCostoAlmacenamiento().compareTo(articuloDTO.getCostoAlmacenamiento()) != 0
        || articuloExistente.getNivelServicioArticulo().compareTo(articuloDTO.getNivelServicioArticulo()) != 0
        || articuloExistente.getDemandaArt() != articuloDTO.getDemandaArt()
        || articuloExistente.getDesviacionEstandarArticulo() != articuloDTO.getDesviacionEstandarArticulo()
        || articuloExistente.getDiasEntreRevisiones() != articuloDTO.getDiasEntreRevisiones()
        || articuloExistente.getStockActual().compareTo(articuloDTO.getStockActual()) != 0){
            configuracionInventarioService.recalcularFormulas(articuloExistente, articuloDTO);
        }

        articuloExistente.setCostoAlmacenamiento(articuloDTO.getCostoAlmacenamiento());
        articuloExistente.setNivelServicioArticulo(articuloDTO.getNivelServicioArticulo());
        articuloExistente.setPrecioArticulo(articuloDTO.getPrecioArticulo());
        articuloExistente.setNombreArticulo(articuloDTO.getNombreArticulo());
        articuloExistente.setDescripcionArticulo(articuloDTO.getDescripcionArticulo());
        articuloExistente.setDemandaArt(articuloDTO.getDemandaArt());
        articuloExistente.setFechaHoraBajaArticulo(articuloDTO.getFechaHoraBajaArticulo());
        articuloExistente.setStockActual(articuloDTO.getStockActual());
        articuloExistente.setDiasEntreRevisiones(articuloDTO.getDiasEntreRevisiones());
        articuloExistente.setDesviacionEstandarArticulo(articuloDTO.getDesviacionEstandarArticulo());

        articuloRepository.guardar(articuloExistente);
    }
}

