package com.nventory.repository;

import com.nventory.model.ArticuloProveedor;

public class ArticuloProveedorRepository extends SoftDeletableRepositoryImpl<ArticuloProveedor, Long> {
    public ArticuloProveedorRepository() {
        super(ArticuloProveedor.class);
    }
}
