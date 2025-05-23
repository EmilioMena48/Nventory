package com.nventory.service;

import com.nventory.DTO.StockMovimientoDTO;
import com.nventory.DTO.VentaDTO;
import com.nventory.model.Articulo;
import com.nventory.model.StockMovimiento;
import com.nventory.model.TipoStockMovimiento;
import com.nventory.model.VentaArticulo;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.StockMovimientoRepository;
import com.nventory.repository.TipoStockMovimientoRepository;
import com.nventory.repository.VentaArticuloRepositori;

import java.util.List;

public class StockMovimientoService {
    StockMovimientoRepository stockMovimientoRepository;
    VentaArticuloRepositori ventaArticuloRepository;
    ArticuloRepository articuloRepository;
    TipoStockMovimientoRepository tipoStockMovimientoRepository;
    ArticuloService articuloService;

    public StockMovimientoService(StockMovimientoRepository stockMovimientoRepository) {this.stockMovimientoRepository = stockMovimientoRepository;}
    public StockMovimientoService() {this.stockMovimientoRepository = new StockMovimientoRepository();}

    public void generarStockMovimiento(StockMovimientoDTO smDTO) {
        ventaArticuloRepository = new VentaArticuloRepositori();
        articuloRepository = new ArticuloRepository();
        tipoStockMovimientoRepository = new TipoStockMovimientoRepository();
        articuloService = new ArticuloService();

        VentaArticulo ventaArticulo = ventaArticuloRepository.buscarPorId(smDTO.getVentaArticuloID());
        Articulo articulo = articuloRepository.buscarPorId(smDTO.getArticuloID());
        TipoStockMovimiento tipoStockMovimiento = tipoStockMovimientoRepository.buscarPorId(smDTO.getTipoStockMovimientoID());

        StockMovimiento stockMovimiento = new StockMovimiento();
        stockMovimiento.setCantidad(smDTO.getCantidad());
        stockMovimiento.setComentario(smDTO.getComentario());
        stockMovimiento.setFechaHoraMovimiento(smDTO.getFechaHoraMovimiento());
        stockMovimiento.setVentaArticulo(ventaArticulo);
        stockMovimiento.setArticulo(articulo);
        stockMovimiento.setTipoStockMovimiento(tipoStockMovimiento);

        Long id = articulo.getCodArticulo();
        Integer cantidad = (-1)*stockMovimiento.getCantidad();

        articuloService.actualizarStock(id, cantidad);
        stockMovimientoRepository.guardar(stockMovimiento);
    }
}
