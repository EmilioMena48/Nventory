package com.nventory.service;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloProveedorRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArticuloProveedorService {
    ArticuloProveedorRepository repository;

    public ArticuloProveedorService(ArticuloProveedorRepository repository) {
        this.repository = repository;
    }

    public List<ArticuloProveedor> listarArticulosProveedor() {
        return repository.buscarTodos();
    }

    public void eliminarArticuloProveedor(Long id) {
        ArticuloProveedor articuloProveedor = repository.buscarPorId(id);
        if (articuloProveedor != null) {
            articuloProveedor.setFechaHoraBajaArticuloProveedor(LocalDateTime.now());
            repository.guardar(articuloProveedor);
        } else {
            throw new IllegalArgumentException("El Articulo Proveedor no existe");
        }
    }

    public void guardarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto){
        ArticuloProveedor articuloProveedor = new ArticuloProveedor();
        articuloProveedor.setArticulo(articulo);
        articuloProveedor.setProveedor(proveedor);

        ArticuloProveedor articuloProveedorAux = buscarArticuloProveedorPorId(articulo.getCodArticulo(), proveedor.getCodProveedor());
        if (articuloProveedorAux != null) {
            articuloProveedor.setCodArticuloProveedor(articuloProveedorAux.getCodArticuloProveedor());
        }
        articuloProveedor.setPrecioUnitario(articuloProveedorDto.getPrecioUnitario());
        articuloProveedor.setCostoEnvio(articuloProveedorDto.getCostoEnvio());
        articuloProveedor.setCostoPedido(articuloProveedorDto.getCostoPedido());
        articuloProveedor.setDemoraEntregaDias(articuloProveedorDto.getDemoraEntregaDias());
        repository.guardar(articuloProveedor);
    }

    public List<Articulo> obtenerArticulosDeEseProveedor(Long idProveedor) {
        return repository.buscarTodosArticulosDelProveedor(idProveedor).stream()
                .filter(Objects::nonNull)
                .filter(articuloProveedor -> articuloProveedor.getFechaHoraBajaArticuloProveedor() == null)
                .map(ArticuloProveedor::getArticulo)
                .collect(Collectors.toList());
    }

    public ArticuloProveedor buscarArticuloProveedorPorId(Long articuloId, Long proveedorId) {
        List<ArticuloProveedor> articulosProveedores = repository.buscarTodos();
        for (ArticuloProveedor articuloProveedor : articulosProveedores) {
            if (articuloProveedor.getArticulo().getCodArticulo().equals(articuloId) &&
                articuloProveedor.getProveedor().getCodProveedor().equals(proveedorId)) {
                return articuloProveedor;
            }
        }
        return null;
    }
}