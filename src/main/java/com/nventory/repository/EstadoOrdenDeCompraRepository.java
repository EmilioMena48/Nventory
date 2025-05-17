package com.nventory.repository;

import com.nventory.model.EstadoOrdenDeCompra;

public class EstadoOrdenDeCompraRepository extends SoftDeletableRepositoryImpl<EstadoOrdenDeCompra, Long> {
    public EstadoOrdenDeCompraRepository() {
        super(EstadoOrdenDeCompra.class);
    }
}
