package com.nventory.service;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.model.Articulo;
import com.nventory.model.Proveedor;
import com.nventory.model.Venta;
import com.nventory.model.VentaArticulo;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.VentaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaService {

    private ArticuloRepository articuloRepository;
    private VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<VentaDTO> mostrarVentas() {
        List<Venta> ventas = ventaRepository.buscarTodos();
        List<VentaDTO> ventasDto = new ArrayList<>();
        for (Venta venta : ventas) {
            VentaDTO ventaDTO = new VentaDTO(
                    venta.getNumeroVenta(),
                    venta.getFechaHoraVenta(),
                    venta.getMontoTotalVenta()
            );
            List<VentaArticulo> ventasArticulo = venta.getVentaArticulo();
            for (VentaArticulo ventaArticulo : ventasArticulo) {
                VentaArticuloDTO ventaArticuloDTO = new VentaArticuloDTO(
                        ventaArticulo.getOrdenVentaArticulo(),
                        ventaArticulo.getCantidadVendida(),
                        ventaArticulo.getPrecioVenta(),
                        ventaArticulo.getSubTotalVenta(),
                        ventaArticulo.getArticulo().getNombreArticulo(),
                        ventaArticulo.getArticulo().getCodArticulo()
                );
                ventaDTO.addVentaArticuloDTO(ventaArticuloDTO);
            }
            ;
            ventasDto.add(ventaDTO);
        }
        return ventasDto;
    }

    public void registrarVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        articuloRepository = new ArticuloRepository();
        venta.setFechaHoraVenta(ventaDTO.getFechaHoraVenta());

        List<VentaArticuloDTO> ventasArticuloDTO = ventaDTO.getVentaArticuloDTO();
        for (VentaArticuloDTO ventaArticuloDTO : ventasArticuloDTO) {
            VentaArticulo ventaArticulo = new VentaArticulo();
            ventaArticulo.setCantidadVendida(ventaArticuloDTO.getCantidadVendida());
            ventaArticulo.setPrecioVenta(ventaArticuloDTO.getPrecioVenta());
            ventaArticulo.setSubTotalVenta(calcularSubtotalVenta(ventaArticuloDTO));
            Articulo articulo = articuloRepository.buscarPorId(ventaArticuloDTO.getCodArticulo());
            ventaArticulo.setArticulo(articulo);
            venta.addVentaArticulo(ventaArticulo);
        }
        venta.setMontoTotalVenta(calcularTotalVenta(ventasArticuloDTO));
        ventaRepository.guardar(venta);
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


