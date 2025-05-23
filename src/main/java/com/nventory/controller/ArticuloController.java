package com.nventory.controller;

import com.nventory.model.Articulo;
import com.nventory.service.ArticuloService;

public class ArticuloController {

    private ArticuloService articuloService;
    public ArticuloController(ArticuloService articuloService) {this.articuloService = articuloService;}

    public void actualizarStock(Long id, Integer Cantidad) { articuloService.actualizarStock(id, Cantidad); }

}
