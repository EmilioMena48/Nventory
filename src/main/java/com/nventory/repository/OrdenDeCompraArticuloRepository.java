package com.nventory.repository;

import com.nventory.model.OrdenDeCompraArticulo;

public class OrdenDeCompraArticuloRepository extends HardDeletableRepositoryImpl<OrdenDeCompraArticulo, Long> {
    public OrdenDeCompraArticuloRepository() {
        super(OrdenDeCompraArticulo.class);
    }
}
