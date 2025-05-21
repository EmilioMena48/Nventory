package com.nventory.service;

import com.nventory.DTO.*;
import com.nventory.model.*;
import com.nventory.repository.*;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraService {

    private final OrdenDeCompraRepository ordenCompraRepo = new OrdenDeCompraRepository();
    private final EstadoOrdenDeCompraRepository estadoOrdenDeCompraRepo = new EstadoOrdenDeCompraRepository();
    private final OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepo = new OrdenDeCompraArticuloRepository();
    private final ArticuloProveedorRepository articuloProveedorRepo = new ArticuloProveedorRepository();
    private final ProveedorRepository proveedorRepo = new ProveedorRepository();
    private final ArticuloRepository articuloRepo = new ArticuloRepository();


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

    @Transactional
    public void enviarOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);

        if (ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente")) ) {
            EstadoOrdenDeCompra estadoEnviada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Enviada");
            ordenCompra.setEstadoOrdenDeCompra(estadoEnviada);
            ordenCompra.setFechaHoraEnvioProv(LocalDateTime.now());
        }
    }

    @Transactional
    public void cancelarOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);

        if (ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente")) ) {
            EstadoOrdenDeCompra estadoCancelada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Cancelada");
            ordenCompra.setEstadoOrdenDeCompra(estadoCancelada);
            //Actualizar orden de compra
        }
    }

    @Transactional
    public void recibirOrdenCompra(Long codOrdenCompra) {
        TipoStockMovimientoRepository tipoStockMovimientoRepo = new TipoStockMovimientoRepository();
        StockMovimientoRepository stockMovimientoRepo = new StockMovimientoRepository();
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);

        if (ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Enviada")) ) {
            List<OrdenDeCompraArticulo> articulosOrd = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);
            for (OrdenDeCompraArticulo OCarticulo : articulosOrd) {
                //Reponer stock
                ArticuloProveedor artProv = OCarticulo.getArticuloProveedor();
                Articulo art = artProv.getArticulo();
                int stockActual = art.getStockActual();
                int stockReposicion = OCarticulo.getCantidadSolicitadaOCA();
                Integer nuevoStock = stockReposicion + stockActual;
                art.setStockActual(nuevoStock);

                //Auditar movimiento de stock
                StockMovimiento entradaStock = new StockMovimiento();
                entradaStock.setArticulo(art);
                entradaStock.setOrdenDeCompraArticulo(OCarticulo);
                entradaStock.setCantidad(stockReposicion);
                entradaStock.setFechaHoraMovimiento(LocalDateTime.now());
                TipoStockMovimiento entrada = tipoStockMovimientoRepo.buscarTSMPorNombre("Entrada");
                entradaStock.setTipoStockMovimiento(entrada);
                stockMovimientoRepo.guardar(entradaStock);

                /* Implementar que Si con la orden la cantidad del artículo no supera
                 el Pto de Ped con modelo LoteFijo informar al usuario.*/
            }
            //Cambiar estado de la Orden
            EstadoOrdenDeCompra finalizada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Finalizada");
            ordenCompra.setEstadoOrdenDeCompra(finalizada);
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
        Proveedor prov = ordenCompraRepo.buscarPorId(codOrdenCompra).getProveedor();

        return articulosOrdDTO;
    }

    @Transactional
    public void agregarArticuloAOrden(Long codOrdenCompra, Long codArticuloProveedor, int cantidadSolicitadaOCA) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        ArticuloProveedor articuloProveedor = articuloProveedorRepo.buscarPorId(codArticuloProveedor);
        boolean esPendiente = ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente"));

        if (ordenCompra.getProveedor().equals(articuloProveedor.getProveedor()) && esPendiente) {
            OrdenDeCompraArticulo ordenCompraArticulo = new OrdenDeCompraArticulo();
            ordenCompraArticulo.setCantidadSolicitadaOCA(cantidadSolicitadaOCA);
            ordenCompraArticulo.setPrecioUnitarioOCA(articuloProveedor.getPrecioUnitario());

            BigDecimal subTotal = articuloProveedor.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidadSolicitadaOCA));
            ordenCompraArticulo.setSubTotalOCA(subTotal);
            ordenCompraArticulo.setArticuloProveedor(articuloProveedor);
            ordenDeCompraArticuloRepo.guardar(ordenCompraArticulo);

            ordenCompra.setOrdenDeCompraArticulo(List.of(ordenCompraArticulo));
            recalcularTotalOrdenCompra(codOrdenCompra);
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

    @Transactional
    public void eliminarArticuloDeOrden(Long codOrdenCompra, Long codOrdenCompraArticulo) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente"))) {
            OrdenDeCompraArticulo OCA = ordenDeCompraArticuloRepo.buscarPorCodOrdenCompraYArticulo(codOrdenCompra, codOrdenCompraArticulo);
            boolean esElMismo = OCA.equals(ordenDeCompraArticuloRepo.buscarPorId(codOrdenCompraArticulo));
            if (esElMismo) {
                ordenDeCompraArticuloRepo.borrar(codOrdenCompraArticulo);
            }
        }
    }

    @Transactional
    public void modificarCantidadArticulo(Long codOrdenCompra, Long codOrdenCompraA, int nuevoCantidad) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        if (ordenCompra.getEstadoOrdenDeCompra().equals(estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente"))) {
            OrdenDeCompraArticulo OCA = ordenDeCompraArticuloRepo.buscarPorCodOrdenCompraYArticulo(codOrdenCompra, codOrdenCompraA);
            boolean esElMismo = OCA.equals(ordenDeCompraArticuloRepo.buscarPorId(codOrdenCompraA));
            if (esElMismo) {
                OCA.setCantidadSolicitadaOCA(nuevoCantidad);
                BigDecimal nuevoSubTotal = OCA.getPrecioUnitarioOCA().multiply(BigDecimal.valueOf(nuevoCantidad));
                OCA.setSubTotalOCA(nuevoSubTotal);
                recalcularTotalOrdenCompra(codOrdenCompra);
            }
        }
    }


    @Transactional
    public void recalcularTotalOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra orden = ordenCompraRepo.buscarPorId(codOrdenCompra);
        List<OrdenDeCompraArticulo> listaOCA = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);
        BigDecimal nuevoTotalOrdenCompra = BigDecimal.ZERO;
        for (OrdenDeCompraArticulo ordenCompraArticulo : listaOCA) {
            BigDecimal subTotal = ordenCompraArticulo.getSubTotalOCA();
            nuevoTotalOrdenCompra = nuevoTotalOrdenCompra.add(subTotal);
        }
        orden.setTotalOrdenDeCompra(nuevoTotalOrdenCompra);
    }

    @Transactional
    public Long crearOrdenDeCompra(Long codProveedor) {
        Proveedor proveedor = proveedorRepo.buscarPorId(codProveedor);
        EstadoOrdenDeCompra estadoPendiente = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente");
        OrdenDeCompra orden  = new OrdenDeCompra();
        orden.setProveedor(proveedor);
        orden.setEstadoOrdenDeCompra(estadoPendiente);
        ordenCompraRepo.guardar(orden);
        return orden.getCodOrdenDeCompra();
    }

    @Transactional
    public Long crearOrdenDeCompraPorArticulo(Long codArticulo, Long codProveedor, int cantidadSolicitada) {
        ArticuloProveedor articuloProveedor = articuloProveedorRepo.buscarPorCodArticuloYProveedor(codArticulo, codProveedor);
        Long codOrdenNueva = crearOrdenDeCompra(codProveedor);
        agregarArticuloAOrden(codOrdenNueva, articuloProveedor.getCodArticuloProveedor(), cantidadSolicitada);
        return codOrdenNueva;
    }
}
