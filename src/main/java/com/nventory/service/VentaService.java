package com.nventory.service;

import com.nventory.DTO.*;
import com.nventory.controller.ArticuloController;
import com.nventory.controller.OrdenDeCompraController;
import com.nventory.model.*;
import com.nventory.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaService {

    private ArticuloService articuloService;
    private ArticuloProveedorService articuloProveedorService;
    private VentaRepository ventaRepository;
    private VentaArticuloRepositori ventaArticuloRepositori;
    private StockMovimientoService stockMovimientoService;
    private OrdenCompraService ordenCompraService;
    private OrdenDeCompraArticuloRepository ordenDeCompraArticuloRepository;
    private OrdenDeCompraController ordenDeCompraController;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<VentaDTO> mostrarVentas() {
        List<VentaDTO> ventasDto = new ArrayList<>();
        List<Venta> ventas = ventaRepository.buscarTodos();
        ventaArticuloRepositori = new VentaArticuloRepositori();
        for (Venta venta : ventas) {
            VentaDTO ventaDTO = new VentaDTO(
                    venta.getNumeroVenta(),
                    venta.getFechaHoraVenta(),
                    venta.getMontoTotalVenta()
            );
            ventasDto.add(ventaDTO);
        }
        return ventasDto;
    }

    public List<VentaArticuloDTO> mostrarVentasArticulo(Long idVenta) {
        List<VentaArticuloDTO> ventaArticuloDTOList = new ArrayList<>();
        List<VentaArticulo> ventasArticulo = ventaArticuloRepositori.buscarVentasArticuloPorId(idVenta);
        Long numeroLinea = 1L;
        for (VentaArticulo ventaArticulo : ventasArticulo) {
            VentaArticuloDTO ventaArticuloDTO = new VentaArticuloDTO();
            ventaArticuloDTO.setCantidadVendida(ventaArticulo.getCantidadVendida());
            ventaArticuloDTO.setPrecioVenta(ventaArticulo.getPrecioVenta());
            ventaArticuloDTO.setSubTotalVenta(ventaArticulo.getSubTotalVenta());
            ventaArticuloDTO.setNombreArticulo(ventaArticulo.getArticulo().getNombreArticulo());
            ventaArticuloDTO.setCodArticulo(ventaArticulo.getArticulo().getCodArticulo());
            ventaArticuloDTO.setOrdenVentaArticulo(numeroLinea++);
            ventaArticuloDTOList.add(ventaArticuloDTO);
        }
        return ventaArticuloDTOList;
    }

    public void registrarVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        articuloService = new ArticuloService();
        stockMovimientoService = new StockMovimientoService();

        venta.setFechaHoraVenta(ventaDTO.getFechaHoraVenta());

        List<VentaArticuloDTO> ventasArticuloDTO = ventaDTO.getVentaArticuloDTO();
        for (VentaArticuloDTO ventaArticuloDTO : ventasArticuloDTO) {
            VentaArticulo ventaArticulo = new VentaArticulo();
            ventaArticulo.setCantidadVendida(ventaArticuloDTO.getCantidadVendida());
            ventaArticulo.setPrecioVenta(ventaArticuloDTO.getPrecioVenta());
            ventaArticulo.setSubTotalVenta(calcularSubtotalVenta(ventaArticuloDTO));
            ArticuloDTO articuloDTO = articuloService.buscarArtPorNombre(ventaArticuloDTO.getNombreArticulo());
            if (!comprobarStockArticulo(ventaArticulo.getCantidadVendida(), articuloDTO.getStockActual())) {
                throw new IllegalArgumentException("No hay stock suficiente para el artículo: " + articuloDTO.getNombreArticulo());
            }
            Articulo articulo = articuloService.buscarArticuloPorId(articuloDTO.getCodArticulo());
            ventaArticulo.setArticulo(articulo);
            venta.addVentaArticulo(ventaArticulo);
        }
        venta.setMontoTotalVenta(calcularTotalVenta(ventasArticuloDTO));

        Venta ventaGuardada = ventaRepository.guardarNuevaVenta(venta);

        List<VentaArticulo> ventaArticuloList = ventaGuardada.getVentaArticulo();
        for(VentaArticulo vA : ventaArticuloList) {
            System.out.println("orden ventaArt"+vA.getCodVentaArticulo());
            StockMovimientoDTO smDTO = new StockMovimientoDTO();
            smDTO.setCantidad(vA.getCantidadVendida());
            smDTO.setComentario(null);
            smDTO.setFechaHoraMovimiento(venta.getFechaHoraVenta());
            smDTO.setOrdenDeCompraArticuloID(null);
            smDTO.setTipoStockMovimientoID(2L);
            smDTO.setArticuloID(vA.getArticulo().getCodArticulo());
            smDTO.setVentaArticuloID(vA.getCodVentaArticulo());
            stockMovimientoService.generarStockMovimiento(smDTO);
        }

        for (VentaArticuloDTO ventaArticuloDTO : ventasArticuloDTO) {
            ArticuloDTO articuloDTO = articuloService.buscarArtPorNombre(ventaArticuloDTO.getNombreArticulo());
            Articulo articulo = articuloService.buscarArticuloPorId(articuloDTO.getCodArticulo());
            generarOrdenCompra(articulo);
        }
    }

    public boolean comprobarStockArticulo(int cantidadVenta, Integer stockArticulo) {
        if (cantidadVenta > stockArticulo) {
            return false;
        } else {
            return true;
        }
    }

    public void generarOrdenCompra(Articulo articulo) {
        ArticuloProveedor artProv = articulo.getArticuloProveedor();
        ConfiguracionInventario configInventario = artProv.getConfiguracionInventario();
        TipoModeloInventario tipoModeloInventario = configInventario.getTipoModeloInventario();
        Long idModelo = tipoModeloInventario.getCodTipoModeloI();

        if(idModelo == 1L) {
            ordenDeCompraController = new OrdenDeCompraController();
            Optional<OrdenDeCompraDTO> ordenCompra = ordenDeCompraController.buscarOrdenAbiertaPorArticulo(articulo.getCodArticulo());
            if(ordenCompra.isEmpty()){
                Integer stock = articulo.getStockActual();
                Integer pp = configInventario.getPuntoPedido();
                if(stock <= pp) {
                    Proveedor proveedor = artProv.getProveedor();
                    Long codOrden = ordenDeCompraController.crearOrdenDeCompra(proveedor.getCodProveedor());
                    ordenDeCompraController.agregarArticuloAOrden(codOrden, artProv.getCodArticuloProveedor(), configInventario.getCantidadPedir());
                }
            }
        }
    }

    public BigDecimal calcularSubtotalVenta(VentaArticuloDTO ventaArticuloDTO) {
        BigDecimal cantidad = new BigDecimal(ventaArticuloDTO.getCantidadVendida()); // si es int
        BigDecimal precio = ventaArticuloDTO.getPrecioVenta(); // ya es BigDecimal

        BigDecimal subtotal = cantidad.multiply(precio);
        ventaArticuloDTO.setSubTotalVenta(subtotal);
        return subtotal;
    }

    public BigDecimal calcularTotalVenta(List<VentaArticuloDTO> ventasArticuloDTO) {
        BigDecimal total = new BigDecimal("0");
        for (VentaArticuloDTO ventaArticuloDTO : ventasArticuloDTO) {
            total = total.add(ventaArticuloDTO.getSubTotalVenta());
        }
        return total;
    }


}


