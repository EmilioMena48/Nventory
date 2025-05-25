package com.nventory.service;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloProveedorRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticuloProveedorService {
    ArticuloProveedorRepository repository;

    public ArticuloProveedorService(ArticuloProveedorRepository repository) {
        this.repository = repository;
    }

    public List<ArticuloProveedor> listarArticulosProveedor() {
        return repository.buscarTodos();
    }

    public void eliminarArticuloProveedor(Long id) {
    }

    public void guardarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto){
        ArticuloProveedor articuloProveedor = new ArticuloProveedor();
        articuloProveedor.setArticulo(articulo);
        articuloProveedor.setProveedor(proveedor);

        articuloProveedor.setPrecioUnitario(articuloProveedorDto.getPrecioUnitario());
        articuloProveedor.setCostoEnvio(articuloProveedorDto.getCostoEnvio());
        articuloProveedor.setCostoPedido(articuloProveedorDto.getCostoPedido());
        articuloProveedor.setDemoraEntregaDias(articuloProveedorDto.getDemoraEntregaDias());
        repository.guardar(articuloProveedor);
    }

    public List<Proveedor> obtenerProveedoresDeEseArticulo(Long idArticulo) {
        List<ArticuloProveedor> articuloProveedores = repository.buscarTodosArticuloProveedor(idArticulo);
        List<Proveedor> proveedores = new ArrayList<>();
        for (ArticuloProveedor articuloProveedor : articuloProveedores) {
            Proveedor proveedor = articuloProveedor.getProveedor();
            if (proveedor != null) {
                proveedores.add(proveedor);
            }
        }
        return proveedores;
    }

    public List<Articulo> obtenerArticulosDeEseProveedor(Long idProveedor) {
        List<ArticuloProveedor> articuloProveedores = repository.buscarTodosArticulosDelProveedor(idProveedor);
        List<Articulo> articulos = new ArrayList<>();
        for (ArticuloProveedor articuloProveedor : articuloProveedores) {
            Articulo articulo = articuloProveedor.getArticulo();
            if (articulo != null) {
                articulos.add(articulo);
            }
        }
        return articulos;
    }
}