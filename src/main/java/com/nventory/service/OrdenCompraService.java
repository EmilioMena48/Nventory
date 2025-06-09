package com.nventory.service;

import com.nventory.DTO.*;
import com.nventory.model.*;
import com.nventory.repository.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("es","AR"));

    public List<OrdenDeCompraDTO> obtenerTodasOrdenesDeCompra() {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        List<OrdenDeCompraDTO> ordenesDto = new ArrayList<>();
        List<OrdenDeCompra> ordenesCompra = ordenCompraRepo.buscarTodos();

        for (OrdenDeCompra ordenCompra : ordenesCompra) {
            OrdenDeCompraDTO ordenCompraDTO = new OrdenDeCompraDTO();

            Long codOrd = ordenCompra.getCodOrdenDeCompra();
            ordenCompraDTO.setCodOrdenDeCompra(codOrd);

            String total = numberFormat.format(ordenCompra.getTotalOrdenDeCompra());
            ordenCompraDTO.setTotalOrden(total);

            String prov = ordenCompra.getProveedor().getNombreProveedor();
            ordenCompraDTO.setProveedor(prov);

            String estado = ordenCompra.getEstadoOrdenDeCompra().getNombreEstadoOC();
            ordenCompraDTO.setEstadoOrdenDeCompra(estado);

            String fechaFormateada = ordenCompra.getFechaHoraInicioOC().format(formatter);
            ordenCompraDTO.setFechaHoraCreacion(fechaFormateada);


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


    public Optional<List<String>> recibirOrdenCompra(Long codOrdenCompra) {
        OrdenDeCompra ordenCompra = ordenCompraRepo.buscarPorId(codOrdenCompra);
        List<String> avisosArticulos = new ArrayList<>();
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

                //Emitir avisos
                ConfiguracionInventario configInv = OCarticulo.getArticuloProveedor().getConfiguracionInventario();
                if (configInv.getTipoModeloInventario().getNombreModeloInventario().equals("Modelo Lote Fijo")){
                    if (configInv.getPuntoPedido() >= art.getStockActual()){
                        avisosArticulos.add("Artículo: " + art.getNombreArticulo() + " Stock actual: " + art.getStockActual() + " Punto de pedido: " + configInv.getPuntoPedido());
                    }
                }
            }
            //Cambiar estado de la Orden
            EstadoOrdenDeCompra finalizada = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Finalizada");
            ordenCompra.setEstadoOrdenDeCompra(finalizada);
            ordenCompraRepo.guardar(ordenCompra);
        }
        if (!(avisosArticulos.isEmpty())) {
            return Optional.of(avisosArticulos);
        } else {
            return Optional.empty();
        }
    }

    public List<OrdenDeCompraArticuloDTO> obtenerArticulosDeOrden(Long codOrdenCompra) {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        List<OrdenDeCompraArticuloDTO> articulosOrdDTO = new ArrayList<>();
        List<OrdenDeCompraArticulo> OCarticulos = ordenDeCompraArticuloRepo.buscarOCAdeUnaOC(codOrdenCompra);

        for (OrdenDeCompraArticulo OCarticulo : OCarticulos) {
            OrdenDeCompraArticuloDTO articuloDTO = new OrdenDeCompraArticuloDTO();
            articuloDTO.setCodOrdenCompraA(OCarticulo.getCodOrdenCompraA());
            articuloDTO.setCantidadSolicitadaOCA(OCarticulo.getCantidadSolicitadaOCA());
            articuloDTO.setSubTotalOCA(numberFormat.format(OCarticulo.getSubTotalOCA()));
            articuloDTO.setPrecioUnitarioOCA(numberFormat.format(OCarticulo.getPrecioUnitarioOCA()));
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
            articuloProveedorDTO.setPrecioUnitario(numberFormat.format(articuloProveedor.getPrecioUnitario()));
            articuloProveedorDTO.setCostoPedido(numberFormat.format(articuloProveedor.getCostoPedido()));
            articulosOrdDTO.add(articuloProveedorDTO);
        }
        return articulosOrdDTO;
    }


    public void agregarArticuloAOrden(Long codOrden, Long codArticuloProveedor, int cantidad) {
        if (ordenDeCompraArticuloRepo.existePorOrdenYArticuloProveedor(codOrden, codArticuloProveedor)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Artículo duplicado");
                alert.setHeaderText("Este artículo ya está en la orden");
                alert.setContentText("Ya existe una entrada para este artículo en la orden. \nSi lo desea puede modificar la cantidad desde la tabla.");
                alert.showAndWait();
            });
            return;
        }
        // Lógica normal si no existe aún:
        ArticuloProveedor articuloProveedor = articuloProveedorRepo.buscarPorId(codArticuloProveedor);
        OrdenDeCompra orden = ordenCompraRepo.buscarPorId(codOrden);
        EstadoOrdenDeCompra pendiente = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente");
        Boolean esElMismoProv = orden.getProveedor().getCodProveedor().equals(articuloProveedor.getProveedor().getCodProveedor());
        if (orden.getEstadoOrdenDeCompra().getNombreEstadoOC().equals(pendiente.getNombreEstadoOC()) && esElMismoProv) {
            OrdenDeCompraArticulo nuevaEntrada = new OrdenDeCompraArticulo();
            nuevaEntrada.setOrdenDeCompra(orden);
            nuevaEntrada.setArticuloProveedor(articuloProveedor);
            nuevaEntrada.setCantidadSolicitadaOCA(cantidad);
            nuevaEntrada.setPrecioUnitarioOCA(articuloProveedor.getPrecioUnitario());
            nuevaEntrada.setSubTotalOCA(articuloProveedor.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)));
            ordenDeCompraArticuloRepo.guardar(nuevaEntrada);
            recalcularTotalOrdenCompra(codOrden);
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



    private void recalcularTotalOrdenCompra(Long codOrdenCompra) {
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
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor no encontrado");
        }

        EstadoOrdenDeCompra estadoPendiente = estadoOrdenDeCompraRepo.buscarEstadoPorNombre("Pendiente");
        if (estadoPendiente == null) {
            throw new IllegalStateException("Estado 'Pendiente' no configurado en el sistema");
        }

        OrdenDeCompra orden = new OrdenDeCompra();
        orden.setProveedor(proveedor);
        orden.setEstadoOrdenDeCompra(estadoPendiente);
        orden.setTotalOrdenDeCompra(BigDecimal.ZERO);
        orden.setFechaHoraInicioOC(LocalDateTime.now());

        Long codOrden = ordenCompraRepo.guardarYdevolverID(orden);

        return codOrden;
    }


    public Optional<OrdenDeCompraDTO> buscarOrdenAbiertaPorProveedor(Long codProveedor) {
        Optional<OrdenDeCompra> orden = ordenCompraRepo.buscarOrdenPendienteOEnviadaPorProveedor(codProveedor);
        if (orden.isPresent()) {
            OrdenDeCompraDTO ordenDTO = new OrdenDeCompraDTO();
            ordenDTO.setCodOrdenDeCompra(orden.get().getCodOrdenDeCompra());
            ordenDTO.setTotalOrden(numberFormat.format(orden.get().getTotalOrdenDeCompra()));
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
            ordenDTO.setTotalOrden(numberFormat.format(orden.get().getTotalOrdenDeCompra()));
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
            proveedorArticuloDTO.setPrecioUnitario(numberFormat.format(articulo.getPrecioUnitario()));
            proveedorArticuloDTO.setDemoraEntregaDias(articulo.getDemoraEntregaDias());
            proveedorArticuloDTO.setCostoPedido(numberFormat.format(articulo.getCostoPedido()));
            proveedorArticuloDTOS.add(proveedorArticuloDTO);
        }
        return proveedorArticuloDTOS;
    }

    public SugerenciaOrdenDTO obtenerSugerenciaParaArticulo(Long codArticulo) {
        Articulo art = articuloRepo.buscarPorId(codArticulo);
        ArticuloProveedor artProv = art.getArticuloProveedor();

        if (artProv == null) {
            artProv = articuloProveedorRepo.buscarArticuloProveedorMasBarato(codArticulo);
        }

        if (artProv != null) {
            return construirSugerenciaDesdeArticuloProveedor(artProv);
        } else {
            return null;
        }
    }

    private SugerenciaOrdenDTO construirSugerenciaDesdeArticuloProveedor(ArticuloProveedor artProv) {
        SugerenciaOrdenDTO dto = new SugerenciaOrdenDTO();
        dto.setNombreProveedorSugerido(artProv.getProveedor().getNombreProveedor());
        dto.setCodProveedor(artProv.getProveedor().getCodProveedor());

        ConfiguracionInventario configInventario = artProv.getConfiguracionInventario();
        String modelo = configInventario.getTipoModeloInventario().getNombreModeloInventario();

        if ("Modelo Lote Fijo".equals(modelo)) {
            int stockPendiente = ordenDeCompraArticuloRepo.buscarStockPendiente(artProv.getArticulo().getCodArticulo());
            int cantidadFinal = configInventario.getLoteOptimo() - stockPendiente;
            dto.setCantidadSugerida(Math.max(cantidadFinal, 0));

        } else if ("Modelo Periodo Fijo".equals(modelo)) {
            int stockPendiente = ordenDeCompraArticuloRepo.buscarStockPendiente(artProv.getArticulo().getCodArticulo());
            int cantidadApedir = configInventario.getInventarioMaximo() - artProv.getArticulo().getStockActual() - stockPendiente;
            dto.setCantidadSugerida(Math.max(cantidadApedir, 0));
        }

        return dto;
    }



    public Long buscarArticuloProveedorPorRelacion(Long codArticulo, Long codProveedor) {
        return articuloProveedorRepo.buscarPorCodArticuloYProveedor(codArticulo, codProveedor).getCodArticuloProveedor();
    }

    public List<String> generarOrdenesDelDia() {
        List<String> listaAvisosOrdenes = new ArrayList<>();
        List<Articulo> articulos = articuloRepo.buscarTodos();
       for (Articulo art : articulos) {
           ArticuloProveedor articuloProv = art.getArticuloProveedor();
           ConfiguracionInventario configInventario = articuloProv.getConfiguracionInventario();
           String modelo = configInventario.getTipoModeloInventario().getNombreModeloInventario();
           if ("Modelo Periodo Fijo".equals(modelo) && LocalDate.now().equals(articuloProv.getFechaProxRevisionAP())) {
               int stockPendiente = ordenDeCompraArticuloRepo.buscarStockPendiente(art.getCodArticulo());
               int cantidadApedir = configInventario.getInventarioMaximo() - art.getStockActual() - stockPendiente;
               if (cantidadApedir > 0) {
                   Long codOC = crearOrdenDeCompra(articuloProv.getProveedor().getCodProveedor());
                   agregarArticuloAOrden(codOC, articuloProv.getCodArticuloProveedor(),cantidadApedir);
                   listaAvisosOrdenes.add("Orden Generada para el Articulo: " + art.getNombreArticulo());
               }
               int T = art.getDiasEntreRevisiones();
               articuloProv.setFechaProxRevisionAP(LocalDate.now().plusDays(T));
               articuloProveedorRepo.guardar(articuloProv);
           }
       }
        if (listaAvisosOrdenes.isEmpty()) {
            listaAvisosOrdenes.add("No se generaron órdenes para los articulos de hoy");
            return listaAvisosOrdenes;
        } else {
            return listaAvisosOrdenes;
        }
    }
}
