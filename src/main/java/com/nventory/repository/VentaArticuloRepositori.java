package com.nventory.repository;

import com.nventory.model.VentaArticulo;

public class VentaArticuloRepositori extends HardDeletableRepositoryImpl<VentaArticulo, Long> {
    public VentaArticuloRepositori() {
        super(VentaArticulo.class);
    }
}
