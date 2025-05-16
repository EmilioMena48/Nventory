package com.nventory.DTO;

public class OrdenDeCompraArticuloDTO {

    private Long id;
    private String nombreArticulo;
    private int cantidad;
    private double precioUnitario;
    private String nombreProveedor;

    public OrdenDeCompraArticuloDTO() {}

    public OrdenDeCompraArticuloDTO(Long id, String nombreArticulo, int cantidad, double precioUnitario, String nombreProveedor) {
        this.id = id;
        this.nombreArticulo = nombreArticulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nombreProveedor = nombreProveedor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
}
