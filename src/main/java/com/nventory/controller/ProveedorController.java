package com.nventory.controller;

import com.nventory.DTO.ProveedorDTO;
import com.nventory.interfaces.ModuloProveedores;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProveedorController implements ModuloProveedores {
    private List<ProveedorDTO> proveedores = new ArrayList<ProveedorDTO>()
            {{
                add(new ProveedorDTO(1L, "10% Desc.", LocalDateTime.now(), "Meli"));
                add(new ProveedorDTO(2L, "Oferta Oferta", LocalDateTime.now(), "ElBananero.com"));
            }};
    private Long codProveedor = 2L;

    @Override
    public void GuardarProveedor(@NonNull ProveedorDTO proveedor) {
        if (proveedor.getCodProveedor() == 0L) {
            proveedor.setCodProveedor(++codProveedor);
            proveedores.add(proveedor);
        } else {
            proveedores.stream()
                    .filter(p -> p.getCodProveedor().equals(proveedor.getCodProveedor()))
                    .findFirst()
                    .ifPresentOrElse(
                            existente -> {
                                int index = proveedores.indexOf(existente);
                                proveedores.set(index, proveedor);
                            },
                            () -> proveedores.add(proveedor)
                    );
        }
    }

    @Override
    public void EliminarProveedor(Long codProveedor) {
        for (int i = 0; i < proveedores.size(); i++) {
            if (proveedores.get(i).getCodProveedor().equals(codProveedor)) {
                proveedores.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("El proveedor no existe");
    }

    @Override
    public List<ProveedorDTO> ListarProveedores() {
        return proveedores;
    }

    @Override
    public ProveedorDTO BuscarProveedor(Long codProveedor) {
        for (ProveedorDTO proveedor : proveedores) {
            if (proveedor.getCodProveedor().equals(codProveedor)) {
                return proveedor;
            }
        }
        throw new IllegalArgumentException("El proveedor no existe");
    }

    @Override
    public void ListarArticulosxProveedor() {

    }

    @Override
    public void AsociarArticuloProveedor() {

    }
}
