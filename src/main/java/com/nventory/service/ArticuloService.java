package com.nventory.service;

import com.nventory.model.Articulo;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.StockMovimientoRepository;
import com.nventory.repository.TipoStockMovimientoRepository;

public class ArticuloService {

    ArticuloRepository articuloRepository;
    public ArticuloService(ArticuloRepository articuloRepository) {this.articuloRepository = articuloRepository;}
    public ArticuloService() {this.articuloRepository = new ArticuloRepository();}

    public void actualizarStock(Long idArticulo, Integer cantidad) {
       Articulo articulo = articuloRepository.buscarPorId(idArticulo);
       Integer cantidadVieja = articulo.getStockActual();
       Integer cantidadNueva = cantidadVieja + cantidad;
        articulo.setStockActual(cantidadNueva);
        articuloRepository.guardar(articulo);
    }
}
