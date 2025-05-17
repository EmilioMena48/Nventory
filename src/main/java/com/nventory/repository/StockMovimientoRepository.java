package com.nventory.repository;

import com.nventory.model.StockMovimiento;

public class StockMovimientoRepository extends BaseRepositoryImpl<StockMovimiento, Long> {
    public StockMovimientoRepository() {
        super(StockMovimiento.class);
    }
}
