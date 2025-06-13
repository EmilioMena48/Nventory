package com.nventory.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDTO {
    private Long numeroVenta;
    private LocalDateTime fechaHoraVenta;
    private BigDecimal montoTotalVenta;
    private List<VentaArticuloDTO> ventasArticuloDTO = new ArrayList<>();

    public Long getNumeroVenta() {
        return numeroVenta;
    }

    public VentaDTO() {
    }

    public void setFechaHoraVenta(LocalDateTime fechaHoraVenta) {
        this.fechaHoraVenta = fechaHoraVenta;
    }

    public void setMontoTotalVenta(BigDecimal montoTotalVenta) {
        this.montoTotalVenta = montoTotalVenta;
    }

    public void setVentasArticuloDTO(List<VentaArticuloDTO> ventasArticuloDTO) {
        this.ventasArticuloDTO = ventasArticuloDTO;
    }

    public LocalDateTime getFechaHoraVenta() {
        return fechaHoraVenta;
    }

    public BigDecimal getMontoTotalVenta() {
        return montoTotalVenta;
    }

    public List<VentaArticuloDTO> getVentaArticuloDTO() {
        return ventasArticuloDTO;
    }

    public void addVentaArticuloDTO(VentaArticuloDTO v) {
        this.ventasArticuloDTO.add(v);
    }



    public VentaDTO(Long numeroVenta, LocalDateTime fechaHoraVenta, BigDecimal montoTotalVenta) {
        this.numeroVenta = numeroVenta;
        this.fechaHoraVenta = fechaHoraVenta;
        this.montoTotalVenta = montoTotalVenta;
    }

}
