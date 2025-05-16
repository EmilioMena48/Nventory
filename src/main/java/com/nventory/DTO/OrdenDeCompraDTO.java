package com.nventory.DTO;

public class OrdenDeCompraDTO {

    private Long id;
    private String estado;

    public OrdenDeCompraDTO() {}

    public OrdenDeCompraDTO(Long id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
