package com.nventory.controller;

import com.nventory.model.Articulo;
import com.nventory.service.ArticuloService;

import java.util.List;

public class ArticuloController {

    ArticuloService articuloService;
    public ArticuloController() {this.articuloService = new ArticuloService();}

    public void actualizarStock(Long id, Integer Cantidad) { articuloService.actualizarStock(id, Cantidad); }

    public List<Articulo> listarArticulos() {
        return articuloService.listarArticulos();
    }
}
