package com.nventory.repository;

import com.nventory.model.TipoModeloInventario;

public class TipoModeloInventarioRepository extends BaseRepositoryImpl<TipoModeloInventario, Long> {
    public TipoModeloInventarioRepository() {
        super(TipoModeloInventario.class);
    }
}
