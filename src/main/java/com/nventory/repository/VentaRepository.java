package com.nventory.repository;

import com.nventory.model.Venta;

public class VentaRepository extends BaseRepositoryImpl <Venta, Long> {
    public VentaRepository() {
        super(Venta.class);
    }
}
