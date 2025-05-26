package com.nventory.service;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.DTO.ProveedorEliminadoDTO;
import com.nventory.model.OrdenDeCompra;
import com.nventory.model.Proveedor;
import com.nventory.repository.OrdenDeCompraRepository;
import com.nventory.repository.ProveedorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProveedorService {
    ProveedorRepository proveedorRepository;
    OrdenDeCompraRepository ordenDeCompraRepository;

    public ProveedorService(ProveedorRepository proveedorRepository,
                            OrdenDeCompraRepository ordenDeCompraRepository) {
        this.proveedorRepository = proveedorRepository;
        this.ordenDeCompraRepository = ordenDeCompraRepository;
    }

    public List<ProveedorDTO> listarProveedores() {
        List<Proveedor> proveedores = proveedorRepository.buscarTodos();
        List<ProveedorDTO> proveedoresDto = new ArrayList<>();
        for (Proveedor proveedor : proveedores) {
            boolean A = proveedor.isActivo();
            if (!(proveedor.isActivo())) continue;
            ProveedorDTO proveedorDto = new ProveedorDTO();
            proveedorDto.setCodProveedor(proveedor.getCodProveedor());
            proveedorDto.setDescripcionProveedor(proveedor.getDescripcionProveedor());
            proveedorDto.setNombreProveedor(proveedor.getNombreProveedor());
            proveedoresDto.add(proveedorDto);
        }
        return proveedoresDto;
    }

    public List<ProveedorEliminadoDTO> listarProveedoresEliminados() {
        List<Proveedor> proveedores = proveedorRepository.buscarTodos();
        List<ProveedorEliminadoDTO> proveedoresDto = new ArrayList<>();
        for (Proveedor proveedor : proveedores) {
            if (proveedor.isActivo()) continue;
            ProveedorEliminadoDTO proveedorDto = new ProveedorEliminadoDTO();
            proveedorDto.setCodProveedor(proveedor.getCodProveedor());
            proveedorDto.setDescripcionProveedor(proveedor.getDescripcionProveedor());
            proveedorDto.setNombreProveedor(proveedor.getNombreProveedor());
            proveedorDto.setFechaHoraBajaProveedor(proveedor.getFechaHoraBajaProveedor());
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

    public Long guardarProveedorYRetornarID(ProveedorDTO proveedorDto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setDescripcionProveedor(proveedorDto.getDescripcionProveedor());
        proveedor.setNombreProveedor(proveedorDto.getNombreProveedor());
        return proveedorRepository.GuardarYRetornarID(proveedor);
    }

    public Proveedor buscarProveedorPorId(Long id) {
        return proveedorRepository.buscarPorId(id);
    }

    public boolean estaEnOrdenesDeCompra(Long codProveedor) {
        List<OrdenDeCompra> OrdensDeCompra = ordenDeCompraRepository.buscarTodos();
        for (OrdenDeCompra orden : OrdensDeCompra) {
            if (orden.getProveedor() != null && orden.getProveedor().getCodProveedor().equals(codProveedor)) {
                if (orden.getEstadoOrdenDeCompra() != null && orden.getEstadoOrdenDeCompra().getNombreEstadoOC().equals("Enviada")) {
                    return true;
                }
                if (orden.getEstadoOrdenDeCompra() != null && orden.getEstadoOrdenDeCompra().getNombreEstadoOC().equals("Pendiente")) {
                    return true;
                }
            }
        }
        return false;
    }
}