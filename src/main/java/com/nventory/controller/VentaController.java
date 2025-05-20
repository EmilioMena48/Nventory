package com.nventory.controller;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.VentaArticuloDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.interfaces.ModuloVenta;
import com.nventory.repository.VentaRepository;
import com.nventory.service.VentaService;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public class VentaController implements ModuloVenta {

    private VentaService ventaService;

    public VentaController() {this.ventaService = new VentaService(new VentaRepository());};

    @Override
    public void AltaVenta(VentaDTO ventaDTO) {ventaService.registrarVenta(ventaDTO);};

    @Override
    public List<VentaDTO> ListarVentas() { return ventaService.mostrarVentas();};

    @Override
    public void BuscarVenta() {

    };

    public BigDecimal calcularTotalVenta(List<VentaArticuloDTO> ventasArticuloDTO) {return ventaService.calcularTotalVenta(ventasArticuloDTO);};

    public BigDecimal calcularSubTotalVenta(VentaArticuloDTO ventaArticuloDTO) { return ventaService.calcularSubtotalVenta(ventaArticuloDTO); }

}
