package com.nventory.service;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.model.*;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ConfiguracionInventarioRepository;
import com.nventory.repository.TipoModeloInventarioRepository;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArticuloProveedorService {
    ArticuloProveedorRepository repository;

    TipoModeloInventarioRepository tipoModeloInventarioRepository = new TipoModeloInventarioRepository();
    ConfiguracionInventarioRepository configuracionInventarioRepository = new ConfiguracionInventarioRepository();

    public ArticuloProveedorService(ArticuloProveedorRepository repository) {
        this.repository = repository;
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

    public void guardarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto) {
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

    public void guardarArticuloProveedor(@NonNull ArticuloProveedor articuloProveedor) {
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

    public ConfiguracionInventario inicializarModelo(boolean isLoteFijo) {
        ConfiguracionInventario config = new ConfiguracionInventario();
        TipoModeloInventario tipoModelo = new TipoModeloInventario();
        Long idCI;
        config.setInventarioMaximoIF(0);
        config.setLoteOptimoLF(0);
        config.setPuntoPedidoLF(0);
        config.setStockSeguridadIF(0);
        config.setStockSeguridadLF(0);
        if (isLoteFijo) {
            tipoModelo.setNombreModeloInventario("Modelo Lote Fijo");
            Long idTPI = tipoModeloInventarioRepository.GuardarYRetornarID(tipoModelo);
            config.setNombreConfiguracionInventario("Modelo Lote Fijo");
            config.setTipoModeloInventario(tipoModeloInventarioRepository.buscarPorId(idTPI));
        } else {
            tipoModelo.setNombreModeloInventario("Modelo Periodo Fijo");
            Long idTPI = tipoModeloInventarioRepository.GuardarYRetornarID(tipoModelo);
            config.setNombreConfiguracionInventario("Modelo Periodo Fijo");
            config.setTipoModeloInventario(tipoModeloInventarioRepository.buscarPorId(idTPI));
        }
        idCI = configuracionInventarioRepository.GuardarYRetornarID(config);
        return configuracionInventarioRepository.buscarPorId(idCI);
    }
}