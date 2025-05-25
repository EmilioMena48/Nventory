package com.nventory.controller;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.interfaces.ModuloProveedores;
import com.nventory.repository.ProveedorRepository;
import com.nventory.service.ProveedorService;

import java.util.List;

public class ProveedorController implements ModuloProveedores {

    ProveedorService proveedorService;

    public ProveedorController() {
        this.proveedorService = new ProveedorService(new ProveedorRepository());
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
    public ProveedorDTO BuscarProveedor(Long codProveedor) {
        return null;
    }

    @Override
    public void ListarArticulosxProveedor() {

    }

    @Override
    public void AsociarArticuloProveedor() {

    }
}
