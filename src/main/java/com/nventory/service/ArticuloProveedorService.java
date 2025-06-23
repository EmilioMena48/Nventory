package com.nventory.service;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.model.*;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ConfiguracionInventarioRepository;
import com.nventory.repository.TipoModeloInventarioRepository;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArticuloProveedorService {
    ArticuloProveedorRepository repository;
    TipoModeloInventarioRepository tipoModeloInventarioRepository;
    ConfiguracionInventarioService configuracionInventarioService;

    public ArticuloProveedorService(ArticuloProveedorRepository repository, ConfiguracionInventarioService configuracionInventarioService) {
        this.repository = repository;
        this.configuracionInventarioService = configuracionInventarioService;
        this.tipoModeloInventarioRepository = new TipoModeloInventarioRepository();
    }

    public void eliminarArticuloProveedor(Long id) {
        ArticuloProveedor articuloProveedor = repository.buscarPorId(id);
        if (articuloProveedor != null) {
            articuloProveedor.setFechaHoraBajaArticuloProveedor(LocalDateTime.now());
            articuloProveedor.getConfiguracionInventario().setFechaHoraBajaConfiguracionInventario(LocalDateTime.now());
            configuracionInventarioService.guardarConfigInventario(articuloProveedor);
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
                if (!Objects.equals(articuloProveedorAux.getCostoPedido(), articuloProveedorDto.getCostoPedido()) || articuloProveedorAux.getDemoraEntregaDias() != articuloProveedorDto.getDemoraEntregaDias()
                        || !Objects.equals(articuloProveedorAux.getPrecioUnitario(), articuloProveedorDto.getPrecioUnitario()) || articuloProveedorAux.getFechaProxRevisionAP() != articuloProveedorDto.getFechaProxRevisionAP()) {
                    articuloProveedorAux.setCostoPedido(articuloProveedorDto.getCostoPedido());
                    articuloProveedorAux.setDemoraEntregaDias(articuloProveedorDto.getDemoraEntregaDias());
                    articuloProveedorAux.setPrecioUnitario(articuloProveedorDto.getPrecioUnitario());
                    articuloProveedorAux.setFechaProxRevisionAP(articuloProveedorDto.getFechaProxRevisionAP());
                    recalcularFormulas(articuloProveedorAux);
                }
            } catch (Exception e) {
                System.out.println("[!] Error al guardar articulo proveedor: " + e.getMessage());
            }
            articuloProveedor.getConfiguracionInventario().setFechaHoraBajaConfiguracionInventario(null);
            configuracionInventarioService.guardarConfigInventario(articuloProveedor);
        }
        articuloProveedor.setPrecioUnitario(articuloProveedorDto.getPrecioUnitario());
        articuloProveedor.setCostoPedido(articuloProveedorDto.getCostoPedido());
        articuloProveedor.setDemoraEntregaDias(articuloProveedorDto.getDemoraEntregaDias());
        articuloProveedor.setFechaProxRevisionAP(articuloProveedorDto.getFechaProxRevisionAP());
        repository.guardar(articuloProveedor);
    }

    private void recalcularFormulas(ArticuloProveedor articuloProveedor) {
        configuracionInventarioService.recalcularFormulasArticuloProveedor(articuloProveedor);
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

    public List<ArticuloProveedor> obtenerArtProvDeEseProveedor(Long idProveedor) {
        return repository.buscarTodosArticulosDelProveedor(idProveedor).stream()
                .filter(Objects::nonNull)
                .filter(articuloProveedor -> articuloProveedor.getFechaHoraBajaArticuloProveedor() == null)
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

    public void cambiarModeloInventario(ArticuloProveedor articuloProveedor) {
        ConfiguracionInventario configuracionInventario = articuloProveedor.getConfiguracionInventario();
        TipoModeloInventario tipoModelo = configuracionInventario.getTipoModeloInventario();
        if (tipoModelo.getNombreModeloInventario().equals("Modelo Lote Fijo")) {
            tipoModelo.setNombreModeloInventario("Modelo Periodo Fijo");
            articuloProveedor.setFechaProxRevisionAP(LocalDate.now());
        } else {
            tipoModelo.setNombreModeloInventario("Modelo Lote Fijo");
            articuloProveedor.setFechaProxRevisionAP(null);
        }
        tipoModeloInventarioRepository.guardar(tipoModelo);
        repository.guardar(articuloProveedor);
    }
}