package com.nventory.controller;

import com.nventory.DTO.ArticuloProveedorGuardadoDTO;
import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.interfaces.ModuloProveedores;
import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import com.nventory.model.Proveedor;
import com.nventory.repository.ArticuloProveedorRepository;
import com.nventory.repository.ArticuloRepository;
import com.nventory.repository.ProveedorRepository;
import com.nventory.service.ArticuloProveedorService;
import com.nventory.service.ProveedorService;

import java.math.BigDecimal;
import java.util.List;

public class ProveedorController implements ModuloProveedores {

    ProveedorService proveedorService;

    ArticuloProveedorService articuloProveedorService;

    ArticuloRepository articuloRepository = new ArticuloRepository();

    public ProveedorController() {
        this.proveedorService = new ProveedorService(new ProveedorRepository());
        this.articuloProveedorService = new ArticuloProveedorService(new ArticuloProveedorRepository());
        //----- Test Guardado en Base de Datos de Articulo -----
        articuloRepository.guardar(Articulo.builder()
                .codArticulo(1L)
                .costoAlmacenamiento(new BigDecimal("100.00"))
                .costoCapitalInmovilizado(new BigDecimal("50.00"))
                .costoCompra(new BigDecimal("200.00"))
                .demandaArt(100)
                .nombreArticulo("Articulo Test")
                .descripcionArticulo("Descripcion del Articulo Test")
                .fechaHoraBajaArticulo(null)
                .stockActual(50)
                .build());

        //----- Fin Test Guardado en Base de Datos de Articulo -----
    }

    @Override
    public void GuardarProveedor(ProveedorDTO proveedorDto) {
        proveedorService.guardarProveedor(proveedorDto);
    }

    @Override
    public void EliminarProveedor(Long codProveedor) {
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
    public void ListarArticulosxProveedor() {

    }

    @Override
    public void AsociarArticuloProveedor(Articulo articulo, Proveedor proveedor, ArticuloProveedorGuardadoDTO articuloProveedorDto) {
        articuloProveedorService.guardarArticuloProveedor(articulo, proveedor, articuloProveedorDto);
    }

    @Override
    public Proveedor guardarYRetornar(ProveedorDTO proveedorDto) {
        Long idProveedor = proveedorService.guardarProveedorYRetornarID(proveedorDto);
        return proveedorService.buscarProveedorPorId(idProveedor);
    }

    @Override
    public Proveedor buscarProveedorPorId(Long idProveedor) {
        return proveedorService.buscarProveedorPorId(idProveedor);
    }
}
