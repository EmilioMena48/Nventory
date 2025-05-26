package com.nventory.service;

import com.nventory.DTO.*;
import com.nventory.model.*;
import com.nventory.repository.*;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
public class OrdenCompraService {

    private final OrdenDeCompraRepository ordenCompraRepo;
    private final EstadoOrdenDeCompraRepository estadoOrdenDeCompraRepo;
    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepo;
    private final ArticuloProveedorRepository articuloProveedorRepo;
    private final ProveedorRepository proveedorRepo;
    private final ArticuloRepository articuloRepo;
    private final TipoStockMovimientoRepository tipoStockMovimientoRepo;
    private final StockMovimientoRepository stockMovimientoRepo;

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {

        List<OrdenDeCompraDTO> ordenesDto = new ArrayList<>();
        List<OrdenDeCompra> ordenesCompra = ordenCompraRepo.buscarTodos();

        for (OrdenDeCompra ordenCompra : ordenesCompra) {
            OrdenDeCompraDTO ordenCompraDTO = new OrdenDeCompraDTO();

            Long codOrd = ordenCompra.getCodOrdenDeCompra();
            ordenCompraDTO.setCodOrdenDeCompra(codOrd);

            String total = ordenCompra.getTotalOrdenDeCompra().toString();
            ordenCompraDTO.setTotalOrden(total);

            String prov = ordenCompra.getProveedor().getNombreProveedor();
            ordenCompraDTO.setProveedor(prov);

            String estado = ordenCompra.getEstadoOrdenDeCompra().getNombreEstadoOC();
            ordenCompraDTO.setEstadoOrdenDeCompra(estado);

            ordenesDto.add(ordenCompraDTO);
        }

        return ordenesDto;
    }


    public void enviarOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);

        if (ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente" ).getCodEstadoOC())) {
            ordenCompra.setEstadoOrdenDeCompra(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Enviada"));
            ordenCompra.setFechaHoraEnvioProv(LocalDateTime.now());
            ordenCompraRepo.guardar(ordenCompra);
        }
    }



    public void cancelarOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente").getCodEstadoOC())) {
            EstadoOrdenDeCompra estadoCancelada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Cancelada");
            ordenCompra.setEstadoOrdenDeCompra(estadoCancelada);
            ordenCompraRepo.guardar(ordenCompra);
        }
    }


    public void recibirOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Enviada").getCodEstadoOC()) ) {
            List<OrdenDeCompraArticulo> articulosOrd = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);
            for (OrdenDeCompraArticulo OCarticulo : articulosOrd) {
                //Reponer stock
                ArticuloProveedor artProv = OCarticulo.getArticuloProveedor();
                Articulo art = artProv.getArticulo();
                int stockActual = art.getStockActual();
                int stockReposicion = OCarticulo.getCantidadSolicitadaOCA();
                Integer nuevoStock = stockReposicion + stockActual;
                art.setStockActual(nuevoStock);
                articuloRepo.guardar(art);

                //Auditar movimiento de stock
                StockMovimiento entradaStock = new StockMovimiento();
                entradaStock.setArticulo(art);
                entradaStock.setOrdenDeCompraArticulo(OCarticulo);
                entradaStock.setCantidad(stockReposicion);
                entradaStock.setFechaHoraMovimiento(LocalDateTime.now());
                TipoStockMovimiento entrada = tipoStockMovimientoRepo.buscarTSMPorNombre("Entrada");
                entradaStock.setTipoStockMovimiento(entrada);
                stockMovimientoRepo.guardar(entradaStock);
                ordenDeCompraArticuloRepo.guardar(OCarticulo);

                /* Implementar que Si con la orden la cantidad del artículo no supera
                 el Pto de Ped con modelo LoteFijo informar al usuario.*/
            }
            //Cambiar estado de la Orden
            EstadoOrdenDeCompra finalizada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Finalizada");
            ordenCompra.setEstadoOrdenDeCompra(finalizada);
            ordenCompraRepo.guardar(ordenCompra);
        }
    }

    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long codOrdenCompra) {
        List<OrdenDeCompraArticuloDTO> articulosOrdDTO = new ArrayList<>();
        List<OrdenDeCompraArticulo> OCarticulos = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);

        for (OrdenDeCompraArticulo OCarticulo : OCarticulos) {
            OrdenDeCompraArticuloDTO articuloDTO = new OrdenDeCompraArticuloDTO();
            articuloDTO.setCodOrdenCompraA(OCarticulo.getCodOrdenCompraA());
            articuloDTO.setCantidadSolicitadaOCA(OCarticulo.getCantidadSolicitadaOCA());
            articuloDTO.setSubTotalOCA(String.valueOf(OCarticulo.getSubTotalOCA()));
            articuloDTO.setPrecioUnitarioOCA(String.valueOf(OCarticulo.getPrecioUnitarioOCA()));
            String nombreArticulo = OCarticulo.getArticuloProveedor().getArticulo().getNombreArticulo();
            articuloDTO.setNombreArticulo(nombreArticulo);
            articulosOrdDTO.add(articuloDTO);
        }
        return articulosOrdDTO;
    }

    public List<ArticuloProveedorDTO> obtenerArticulosDeProveedor(Long codOrdenCompra) {
        List<ArticuloProveedorDTO> articulosOrdDTO = new ArrayList<>();
        Long codProv = ordenCompraRepo.buscarPorId(codOrdenCompra).getProveedor().getCodProveedor();
        List<ArticuloProveedor> articuloProveedors = articuloProveedorRepo.buscarTodosArticulosDelProveedor(codProv);
        for (ArticuloProveedor articuloProveedor : articuloProveedors) {
            ArticuloProveedorDTO articuloProveedorDTO = new ArticuloProveedorDTO();
            articuloProveedorDTO.setId(articuloProveedor.getCodArticuloProveedor());
            articuloProveedorDTO.setNombre(articuloProveedor.getArticulo().getNombreArticulo());
            articuloProveedorDTO.setPrecioUnitario(String.valueOf(articuloProveedor.getPrecioUnitario()));
            articulosOrdDTO.add(articuloProveedorDTO);
        }
        return articulosOrdDTO;
    }


    public void agregarArticuloAOrden(Long codOrdenCompra, Long codArticuloProveedor, int cantidadSolicitadaOCA) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        ArticuloProveedor articuloProveedor = articuloProveedorRepo.buscarPorId(codArticuloProveedor);
        boolean esPendiente = ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente").getCodEstadoOC());

        if (ordenCompra.getProveedor().getCodProveedor().equals(articuloProveedor.getProveedor().getCodProveedor()) && esPendiente) {
            OrdenDeCompraArticulo ordenCompraArticulo = new OrdenDeCompraArticulo();
            ordenCompraArticulo.setCantidadSolicitadaOCA(cantidadSolicitadaOCA);
            ordenCompraArticulo.setPrecioUnitarioOCA(articuloProveedor.getPrecioUnitario());

            BigDecimal subTotal = articuloProveedor.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidadSolicitadaOCA));
            ordenCompraArticulo.setSubTotalOCA(subTotal);
            ordenCompraArticulo.setArticuloProveedor(articuloProveedor);
            ordenCompraArticulo.setOrdenDeCompra(ordenCompra);
            ordenDeCompraArticuloRepo.guardar(ordenCompraArticulo);


            recalcularTotalOrdenCompra(codOrdenCompra);
            ordenCompraRepo.guardar(ordenCompra);
        }
    }

    public List<ProveedorDTO> obtenerProveedores() {
        List<ProveedorDTO> proveedoresDTOs = new ArrayList<>();
        List<Proveedor> proveedors = proveedorRepo.buscarTodos();

        for (Proveedor proveedor : proveedors) {
            ProveedorDTO proveedorDTO = new ProveedorDTO();
            proveedorDTO.setNombreProveedor(proveedor.getNombreProveedor());
            proveedorDTO.setCodProveedor(proveedor.getCodProveedor());
            proveedoresDTOs.add(proveedorDTO);
        }
        return proveedoresDTOs;
    }

    public List<ArticuloDTO> obtenerTodosLosArticulos() {
        List<ArticuloDTO> listaArticuloDTO = new ArrayList<>();
        List<Articulo> articulos = articuloRepo.buscarTodos();
        for (Articulo articulo : articulos) {
            ArticuloDTO articuloDTO = new ArticuloDTO();
            articuloDTO.setCodArticulo(articulo.getCodArticulo());
            articuloDTO.setNombreArticulo(articulo.getNombreArticulo());
            articuloDTO.setStockActual(articulo.getStockActual());
            listaArticuloDTO.add(articuloDTO);
        }
        return listaArticuloDTO;
    }


    public void eliminarArticuloDeOrden(Long codOrdenCompra, Long codOrdenCompraArticulo) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente").getCodEstadoOC())) {
            OrdenDeCompraArticulo OCA = ordenDeCompraArticuloRepo.buscarPorCodOrdenCompraYArticulo(codOrdenCompra, codOrdenCompraArticulo);
            boolean esElMismo = OCA.getCodOrdenCompraA().equals(ordenDeCompraArticuloRepo.buscarPorId(codOrdenCompraArticulo).getCodOrdenCompraA());
            if (esElMismo) {
                ordenDeCompraArticuloRepo.borrar(codOrdenCompraArticulo);
                recalcularTotalOrdenCompra(codOrdenCompra);
            }
        }
    }


    public void modificarCantidadArticulo(Long codOrdenCompra, Long codOrdenCompraA, int nuevoCantidad) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().getCodEstadoOC().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente").getCodEstadoOC())) {
            OrdenDeCompraArticulo OCA = ordenDeCompraArticuloRepo.buscarPorCodOrdenCompraYArticulo(codOrdenCompra, codOrdenCompraA);
            boolean esElMismo = OCA.getCodOrdenCompraA().equals(ordenDeCompraArticuloRepo.buscarPorId(codOrdenCompraA).getCodOrdenCompraA());
            if (esElMismo) {
                OCA.setCantidadSolicitadaOCA(nuevoCantidad);
                BigDecimal nuevoSubTotal = OCA.getPrecioUnitarioOCA().multiply(BigDecimal.valueOf(nuevoCantidad));
                OCA.setSubTotalOCA(nuevoSubTotal);
                ordenDeCompraArticuloRepo.guardar(OCA);
                recalcularTotalOrdenCompra(codOrdenCompra);
            }
        }
    }



    public void recalcularTotalOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra orden = ordenCompraRepo.buscarPorId(codOrdenCompra);
        List<OrdenDeCompraArticulo> listaOCA = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);
        BigDecimal nuevoTotalOrdenCompra = BigDecimal.ZERO;
        for (OrdenDeCompraArticulo ordenCompraArticulo : listaOCA) {
            BigDecimal subTotal = ordenCompraArticulo.getSubTotalOCA();
            nuevoTotalOrdenCompra = nuevoTotalOrdenCompra.add(subTotal);
        }
        orden.setTotalOrdenDeCompra(nuevoTotalOrdenCompra);
        ordenCompraRepo.guardar(orden);
    }


    public Long crearOrdenDeCompra(Long codProveedor) {
        Proveedor proveedor = proveedorRepo.buscarPorId(codProveedor);
        EstadoOrdenDeCompra estadoPendiente = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente");
        OrdenDeCompra orden  = new OrdenDeCompra();
        orden.setProveedor(proveedor);
        orden.setEstadoOrdenDeCompra(estadoPendiente);
        ordenCompraRepo.guardar(orden);
        return orden.getCodOrdenDeCompra();
    }


    public Long crearOrdenDeCompraPorArticulo(Long codArticulo, Long codProveedor, int cantidadSolicitada) {
        ArticuloProveedor articuloProveedor = articuloProveedorRepo.buscarPorCodArticuloYProveedor(codArticulo, codProveedor);
        Long codOrdenNueva = crearOrdenDeCompra(codProveedor);
        agregarArticuloAOrden(codOrdenNueva, articuloProveedor.getCodArticuloProveedor(), cantidadSolicitada);
        return codOrdenNueva;
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorProveedor(Long codProveedor) {
        Optional<OrdenDeCompra> orden = ordenCompraRepo.buscarOrdenPendienteOEnviadaPorProveedor(codProveedor);
        if (orden.isPresent()) {
            OrdenDeCompraDTO ordenDTO = new OrdenDeCompraDTO();
            ordenDTO.setCodOrdenDeCompra(orden.get().getCodOrdenDeCompra());
            ordenDTO.setTotalOrden(String.valueOf(orden.get().getTotalOrdenDeCompra()));
            ordenDTO.setEstadoOrdenDeCompra(orden.get().getEstadoOrdenDeCompra().getNombreEstadoOC());
            ordenDTO.setProveedor(orden.get().getProveedor().getNombreProveedor());
            return Optional.of(ordenDTO);
        }
        return Optional.empty();
    }

    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorArticulo(Long codArticulo) {
        Optional<OrdenDeCompra> orden = ordenCompraRepo.buscarOrdenPendienteOEnviadaPorArticulo(codArticulo);
        if (orden.isPresent()) {
            OrdenDeCompraDTO ordenDTO = new OrdenDeCompraDTO();
            ordenDTO.setCodOrdenDeCompra(orden.get().getCodOrdenDeCompra());
            ordenDTO.setTotalOrden(String.valueOf(orden.get().getTotalOrdenDeCompra()));
            ordenDTO.setEstadoOrdenDeCompra(orden.get().getEstadoOrdenDeCompra().getNombreEstadoOC());
            ordenDTO.setProveedor(orden.get().getProveedor().getNombreProveedor());
            return Optional.of(ordenDTO);
        }
        return Optional.empty();
    }

    public String obtenerEstadoDeUnaOrden(Long codOrdenCompra){
        OrdenDeCompra orden = ordenCompraRepo.buscarPorId(codOrdenCompra);
        return orden.getEstadoOrdenDeCompra().getNombreEstadoOC();
    }

    public List<ProveedorArticuloDTO> obtenerProveedoresParaArticulo(Long codArticulo) {
        List<ProveedorArticuloDTO> proveedorArticuloDTOS = new ArrayList<>();
        List<ArticuloProveedor> listaArticulosProveedor = articuloProveedorRepo.buscarTodosArticuloProveedor(codArticulo);
        for (ArticuloProveedor articulo : listaArticulosProveedor) {
            ProveedorArticuloDTO proveedorArticuloDTO = new ProveedorArticuloDTO();
            proveedorArticuloDTO.setCodProveedor(articulo.getProveedor().getCodProveedor());
            proveedorArticuloDTO.setNombreProveedor(articulo.getProveedor().getNombreProveedor());
            proveedorArticuloDTO.setPrecioUnitario(String.valueOf(articulo.getPrecioUnitario()));
            proveedorArticuloDTO.setDemoraEntregaDias(articulo.getDemoraEntregaDias());
            proveedorArticuloDTO.setCostoPedido(String.valueOf(articulo.getCostoPedido()));
            proveedorArticuloDTOS.add(proveedorArticuloDTO);
        }
        return proveedorArticuloDTOS;
    }

    public SugerenciaOrdenDTO obtenerSugerenciaParaArticulo(Long codArticulo) {
        SugerenciaOrdenDTO sugerenciaOrdenDTO = new SugerenciaOrdenDTO();
        Articulo art = articuloRepo.buscarPorId(codArticulo);
        sugerenciaOrdenDTO.setNombreProveedorSugerido(art.getArticuloProveedor().getProveedor().getNombreProveedor());
        //Hay que sugerir una cantidad dependiendo del Modelo de inventario.
        return sugerenciaOrdenDTO;
    }
}
