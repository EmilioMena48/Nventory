package com.nventory.service;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.model.*;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ConfiguracionInventarioRepository;
import com.nventory.repository.TipoModeloInventarioRepository;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArticuloProveedorService {
    ArticuloProveedorRepository repository;
    ConfiguracionInventarioService configuracionInventarioService;

    TipoModeloInventarioRepository tipoModeloInventarioRepository = new TipoModeloInventarioRepository();
    ConfiguracionInventarioRepository configuracionInventarioRepository = new ConfiguracionInventarioRepository();

    public ArticuloProveedorService(ArticuloProveedorRepository repository, ConfiguracionInventarioService configuracionInventarioService) {
        this.repository = repository;
        this.configuracionInventarioService = configuracionInventarioService;
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
            articuloProveedor.setConfiguracionInventario(articuloProveedorAux.getConfiguracionInventario());
            try {
                if (!Objects.equals(articuloProveedorAux.getCostoPedido(), articuloProveedor.getCostoPedido()) || articuloProveedorAux.getDemoraEntregaDias() != articuloProveedor.getDemoraEntregaDias()
                        || !Objects.equals(articuloProveedorAux.getPrecioUnitario(), articuloProveedor.getPrecioUnitario()) || articuloProveedorAux.getFechaProxRevisionAP() != articuloProveedor.getFechaProxRevisionAP()) {
                    recalcularFormulas(articuloProveedor);
                }
            } catch (Exception e) {
                System.out.println("[!] Error al guardar articulo proveedor");
            }

        }

        articuloProveedor.setPrecioUnitario(articuloProveedorDto.getPrecioUnitario());
        articuloProveedor.setCostoPedido(articuloProveedorDto.getCostoPedido());
        articuloProveedor.setDemoraEntregaDias(articuloProveedorDto.getDemoraEntregaDias());
        articuloProveedor.setFechaProxRevisionAP(articuloProveedorDto.getFechaProxRevisionAP());
        repository.guardar(articuloProveedor);
    }

    private void recalcularFormulas(ArticuloProveedor articuloProveedor) {
        configuracionInventarioService.recalcularFormulas(articuloProveedor);
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
        return configuracionInventarioService.crearConfiguracionInventario(isLoteFijo);
    }
}