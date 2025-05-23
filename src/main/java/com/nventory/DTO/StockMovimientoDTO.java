package com.nventory.DTO;

import com.nventory.model.OrdenDeCompraArticulo;
import com.nventory.model.TipoStockMovimiento;

import java.time.LocalDateTime;

public class StockMovimientoDTO {
    private Long id;
    private int cantidad;
    private String comentario;
    private LocalDateTime fechaHoraMovimiento;
    private Long ordenDeCompraArticuloID;
    private Long tipoStockMovimientoID;
    private Long articuloID;
    private Long ventaArticuloID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaHoraMovimiento() {
        return fechaHoraMovimiento;
    }

    public void setFechaHoraMovimiento(LocalDateTime fechaHoraMovimiento) {
        this.fechaHoraMovimiento = fechaHoraMovimiento;
    }

    public Long getOrdenDeCompraArticuloID() {
        return ordenDeCompraArticuloID;
    }

    public void setOrdenDeCompraArticuloID(Long ordenDeCompraArticuloID) {
        this.ordenDeCompraArticuloID = ordenDeCompraArticuloID;
    }

    public Long getTipoStockMovimientoID() {
        return tipoStockMovimientoID;
    }

    public void setTipoStockMovimientoID(Long tipoStockMovimientoID) {
        this.tipoStockMovimientoID = tipoStockMovimientoID;
    }

    public Long getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(Long articuloID) {
        this.articuloID = articuloID;
    }

    public Long getVentaArticuloID() {
        return ventaArticuloID;
    }

    public void setVentaArticuloID(Long ventaArticuloID) {
        this.ventaArticuloID = ventaArticuloID;
    }


}
