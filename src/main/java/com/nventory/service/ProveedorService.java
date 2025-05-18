package com.nventory.service;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.model.Proveedor;
import com.nventory.repository.ProveedorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProveedorService {
    ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<ProveedorDTO> listarProveedores(boolean B) {
        List<Proveedor> proveedores = proveedorRepository.buscarTodos();
        List<ProveedorDTO> proveedoresDto = new ArrayList<>();
        for (Proveedor proveedor : proveedores) {
            boolean A = proveedor.isActivo();
            if (!((A && B) || (!A && !B))) continue;
            ProveedorDTO proveedorDto = new ProveedorDTO();
            proveedorDto.setCodProveedor(proveedor.getCodProveedor());
            proveedorDto.setDescripcionProveedor(proveedor.getDescripcionProveedor());
            proveedorDto.setNombreProveedor(proveedor.getNombreProveedor());
            proveedoresDto.add(proveedorDto);
        }
        return proveedoresDto;
    }

    public void EliminarProveedor(Long codProveedor) {
        Proveedor proveedor = proveedorRepository.buscarPorId(codProveedor);
        if (proveedor != null) {
            proveedor.setActivo(false);
            proveedor.setFechaHoraBajaProveedor(LocalDateTime.now());
            proveedorRepository.guardar(proveedor);
        } else {
            throw new IllegalArgumentException("El proveedor no existe");
        }
    }

    public void guardarProveedor(ProveedorDTO proveedorDto){
        Proveedor proveedor = new Proveedor();
        if (proveedorDto.getCodProveedor() != 0L) {
            proveedor.setCodProveedor(proveedorDto.getCodProveedor());
        }
        proveedor.setDescripcionProveedor(proveedorDto.getDescripcionProveedor());
        proveedor.setNombreProveedor(proveedorDto.getNombreProveedor());
        proveedorRepository.guardar(proveedor);
    }
}