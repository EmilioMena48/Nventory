package com.nventory.controller;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ConfigInvDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.interfaces.ModuloProveedores;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.OrdenDeCompraRepository;
import com.nventory.repository.ProveedorRepository;
import com.nventory.service.ArticuloProveedorService;
import com.nventory.service.ArticuloService;
import com.nventory.service.ConfiguracionInventarioService;
import com.nventory.service.ProveedorService;

import java.util.List;

public class ProveedorController implements ModuloProveedores {

    ProveedorService proveedorService;
    ArticuloProveedorService articuloProveedorService;
    ConfiguracionInventarioService configuracionInventarioService;
    ArticuloService articuloService;
    ArticuloRepository articuloRepository;
    ArticuloProveedorRepository articuloProveedorRepository;
    ProveedorRepository proveedorRepository;
    OrdenDeCompraRepository ordenDeCompraRepository;

    public ProveedorController() {
        this.configuracionInventarioService = new ConfiguracionInventarioService();
        this.articuloProveedorRepository = new ArticuloProveedorRepository();
        this.articuloRepository = new ArticuloRepository();
        this.proveedorRepository = new ProveedorRepository();
        this.ordenDeCompraRepository = new OrdenDeCompraRepository();
        this.articuloProveedorService = new ArticuloProveedorService(articuloProveedorRepository, configuracionInventarioService);
        this.articuloService = new ArticuloService(articuloRepository);
        this.proveedorService = new ProveedorService(proveedorRepository, ordenDeCompraRepository, articuloProveedorService, configuracionInventarioService);
    }

    @Override
    public void GuardarProveedor(ProveedorDTO proveedorDto) {
        proveedorService.guardarProveedor(proveedorDto);
    }

    @Override
    public void EliminarProveedor(Long codProveedor) {
        List<Articulo> articulos = ListarArticulos(codProveedor);
        for (Articulo articulo : articulos) {
            if (articulo.getArticuloProveedor() != null && articulo.getArticuloProveedor().getProveedor().getCodProveedor().equals(codProveedor)) {
                throw new IllegalStateException("Es proveedor predeterminado de "+ articulo.getNombreArticulo());
            }
        }
        if (proveedorService.estaEnOrdenesDeCompra(codProveedor)) {
            throw new IllegalStateException("El proveedor tiene ordenes de compra pendientes o en curso.");
        }
        proveedorService.EliminarProveedor(codProveedor);
    }

    @Override
    public List<ProveedorDTO> ListarProveedores() {
        return proveedorService.listarProveedores();
    }

    @Override
    public List<ProveedorEliminadoDTO> ListarProveedoresEliminados() {
        return proveedorService.listarProveedoresEliminados();
    }

    @Override
    public List<Articulo> ListarArticulos(Long codProveedor) {
        return articuloProveedorService.obtenerArticulosDeEseProveedor(codProveedor);
    }

    @Override
    public void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto) {
        articuloProveedorService.guardarArticuloProveedor(articulo, proveedor, articuloProveedorDto);
    }

    @Override
    public void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto, Boolean tipoModelo) {
        articuloProveedorService.guardarArticuloProveedor(articulo, proveedor, articuloProveedorDto);
        ArticuloProveedor articuloProveedor = articuloProveedorService.buscarArticuloProveedorPorId(articulo.getCodArticulo(), proveedor.getCodProveedor());
        articuloProveedor.setConfiguracionInventario(articuloProveedorService.inicializarModelo(tipoModelo));
        articuloProveedorService.guardarArticuloProveedor(articuloProveedor);
        configuracionInventarioService.recalcularFormulasArticuloProveedor(articuloProveedor);
    }

    @Override
    public void EliminarArticuloProveedor(Long articuloId, Long proveedorId) {
        ArticuloProveedor articuloProveedor = articuloProveedorService.buscarArticuloProveedorPorId(articuloId, proveedorId);
        Articulo articulo = articuloService.buscarArticuloPorId(articuloId);
        Proveedor proveedor = proveedorService.buscarProveedorPorId(proveedorId);
        if (articuloProveedor != null) {
            if (articulo.getArticuloProveedor() != null && articulo.getArticuloProveedor().getCodArticuloProveedor().equals(articuloProveedor.getCodArticuloProveedor())) {
                throw new IllegalStateException("El proveedor "+proveedor.getNombreProveedor()+" es predeterminado del articulo: " + articulo.getNombreArticulo());
            }
            articuloProveedorService.eliminarArticuloProveedor(articuloProveedor.getCodArticuloProveedor());
        } else {
            throw new IllegalStateException("No existe la asociacion entre el articulo y el proveedor.");
        }
    }

    @Override
    public ArticuloProveedorGuardadoDTO BuscarArticuloProveedor(Long articuloId, Long proveedorId) {
        ArticuloProveedor articuloProveedor = articuloProveedorService.buscarArticuloProveedorPorId(articuloId, proveedorId);
        if (articuloProveedor != null) {
            return ArticuloProveedorGuardadoDTO.builder()
                    .precioUnitario(articuloProveedor.getPrecioUnitario())
                    .costoPedido(articuloProveedor.getCostoPedido())
                    .demoraEntregaDias(articuloProveedor.getDemoraEntregaDias())
                    .fechaProxRevisionAP(articuloProveedor.getFechaProxRevisionAP())
                    .build();
        } else {
            return null;
        }
    }

    @Override
    public ConfigInvDTO BuscarConfigInventario(Long articuloId, Long proveedorId) {
        ArticuloProveedor articuloProveedor = articuloProveedorService.buscarArticuloProveedorPorId(articuloId, proveedorId);
        if (articuloProveedor != null && articuloProveedor.getConfiguracionInventario() != null) {
            return configuracionInventarioService.convertirAConfigInvDTO(articuloProveedor.getConfiguracionInventario());
        } else {
            return null;
        }
    }

    @Override
    public boolean EstaEliminadoArticuloProveedor(Long articuloId, Long proveedorId) {
        ArticuloProveedor articuloProveedor = articuloProveedorService.buscarArticuloProveedorPorId(articuloId, proveedorId);
        return articuloProveedor != null && articuloProveedor.getFechaHoraBajaArticuloProveedor() != null;
    }

    @Override
    public Proveedor GuardarYRetornar(ProveedorDTO proveedorDto) {
        Long idProveedor = proveedorService.guardarProveedorYRetornarID(proveedorDto);
        return proveedorService.buscarProveedorPorId(idProveedor);
    }

    @Override
    public Proveedor BuscarProveedorPorId(Long idProveedor) {
        return proveedorService.buscarProveedorPorId(idProveedor);
    }
}
