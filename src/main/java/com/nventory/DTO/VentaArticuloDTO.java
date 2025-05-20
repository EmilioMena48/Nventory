package com.nventory.DTO;

import java.math.BigDecimal;

public class VentaArticuloDTO {
    private Long ordenVentaArticulo;
    private int cantidadVendida;
    private BigDecimal precioVenta;
    private BigDecimal subTotalVenta;
    private Long codArticulo;

    public Long getCodArticulo() {
        return codArticulo;
    }

    public void setCodArticulo(Long codArticulo) {
        this.codArticulo = codArticulo;
    }

    private String nombreArticulo;

    public Long getOrdenVentaArticulo() {
        return ordenVentaArticulo;
    }

    public void setOrdenVentaArticulo(Long ordenVentaArticulo) {
        this.ordenVentaArticulo = ordenVentaArticulo;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getSubTotalVenta() {
        return subTotalVenta;
    }

    public void setSubTotalVenta(BigDecimal subTotalVenta) {
        this.subTotalVenta = subTotalVenta;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public VentaArticuloDTO() {
    }

    public VentaArticuloDTO(Long ordenVentaArticulo, int cantidadVendida, BigDecimal precioVenta, BigDecimal subTotalVenta, String nombreArticulo, Long codArticulo) {
        this.ordenVentaArticulo = ordenVentaArticulo;
        this.cantidadVendida = cantidadVendida;
        this.precioVenta = precioVenta;
        this.subTotalVenta = subTotalVenta;
        this.nombreArticulo = nombreArticulo;
        this.codArticulo = codArticulo;
    }
}
