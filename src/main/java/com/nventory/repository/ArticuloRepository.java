package com.nventory.repository;

import com.nventory.model.Articulo;

public class ArticuloRepository extends SoftDeletableRepositoryImpl<Articulo, Long> {

    public ArticuloRepository() {
        super(Articulo.class);
    }
}
