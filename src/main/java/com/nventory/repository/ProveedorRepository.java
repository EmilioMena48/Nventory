package com.nventory.repository;

import com.nventory.model.Proveedor;

public class ProveedorRepository extends SoftDeletableRepositoryImpl<Proveedor, Long> {
    public ProveedorRepository() {
        super(Proveedor.class);
    }
}
