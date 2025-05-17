package com.nventory.repository;

import com.nventory.model.TipoStockMovimiento;

public class TipoStockMovimientoRepository extends SoftDeletableRepositoryImpl<TipoStockMovimiento, Long> {
    public TipoStockMovimientoRepository() {
        super(TipoStockMovimiento.class);
    }
}
