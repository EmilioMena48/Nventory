package com.nventory.service;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import com.nventory.model.Venta;
import com.nventory.model.VentaArticulo;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.VentaArticuloRepositori;
import com.nventory.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaService {

    private ArticuloRepository articuloRepository;
    private VentaRepository ventaRepository;
    private VentaArticuloRepositori ventaArticuloRepositori;
    private StockMovimientoService stockMovimientoService;

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
        for (VentaArticulo ventaArticulo : ventasArticulo) {
            VentaArticuloDTO ventaArticuloDTO = new VentaArticuloDTO(
                    ventaArticulo.getOrdenVentaArticulo(),
                    ventaArticulo.getCantidadVendida(),
                    ventaArticulo.getPrecioVenta(),
                    ventaArticulo.getSubTotalVenta(),
                    ventaArticulo.getArticulo().getNombreArticulo(),
                    ventaArticulo.getArticulo().getCodArticulo()
            );
            ventaArticuloDTOList.add(ventaArticuloDTO);
        }
        return ventaArticuloDTOList;
    }

    public void registrarVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        articuloRepository = new ArticuloRepository();
        stockMovimientoService = new StockMovimientoService();

        venta.setFechaHoraVenta(ventaDTO.getFechaHoraVenta());

        List<VentaArticuloDTO> ventasArticuloDTO = ventaDTO.getVentaArticuloDTO();
        for (VentaArticuloDTO ventaArticuloDTO : ventasArticuloDTO) {
            VentaArticulo ventaArticulo = new VentaArticulo();
            ventaArticulo.setCantidadVendida(ventaArticuloDTO.getCantidadVendida());
            ventaArticulo.setPrecioVenta(ventaArticuloDTO.getPrecioVenta());
            ventaArticulo.setSubTotalVenta(calcularSubtotalVenta(ventaArticuloDTO));
            Articulo articulo = articuloRepository.buscarPorId(ventaArticuloDTO.getCodArticulo());
            if (!comprobarStockArticulo(ventaArticulo.getCantidadVendida(), articulo.getStockActual())) {
                throw new IllegalArgumentException("No hay stock suficiente para el artículo con ID: " + articulo.getCodArticulo());
            }
            ventaArticulo.setArticulo(articulo);
            venta.addVentaArticulo(ventaArticulo);
        }
        venta.setMontoTotalVenta(calcularTotalVenta(ventasArticuloDTO));

        // Revisar
        Venta ventaGuardada = ventaRepository.guardarNuevaVenta(venta);

        List<VentaArticulo> ventaArticuloList = ventaGuardada.getVentaArticulo();
        for(VentaArticulo vA : ventaArticuloList) {
            System.out.println("orden ventaArt"+vA.getOrdenVentaArticulo());
            StockMovimientoDTO smDTO = new StockMovimientoDTO();
            smDTO.setCantidad(vA.getCantidadVendida());
            smDTO.setComentario(null);
            smDTO.setFechaHoraMovimiento(venta.getFechaHoraVenta());
            smDTO.setOrdenDeCompraArticuloID(null);
            smDTO.setTipoStockMovimientoID(2L);
            smDTO.setArticuloID(vA.getArticulo().getCodArticulo());
            smDTO.setVentaArticuloID(vA.getOrdenVentaArticulo());
            stockMovimientoService.generarStockMovimiento(smDTO);
        }
    }

    public boolean comprobarStockArticulo(int cantidadVenta, Integer stockArticulo) {
        if (cantidadVenta > stockArticulo) {
            return false;
        } else {
            return true;
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


