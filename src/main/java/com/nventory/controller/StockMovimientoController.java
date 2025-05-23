package com.nventory.controller;

import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.service.StockMovimientoService;

public class StockMovimientoController {

    StockMovimientoService stockMovimientoService;
    public StockMovimientoController(StockMovimientoService stockMovimientoService) {this.stockMovimientoService = stockMovimientoService;}

    public void generarStockMovimiento(StockMovimientoDTO smDTO) {stockMovimientoService.generarStockMovimiento(smDTO);}
}
