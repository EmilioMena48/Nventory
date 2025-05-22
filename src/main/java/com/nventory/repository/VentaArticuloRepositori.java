package com.nventory.repository;

import com.nventory.model.OrdenDeCompraArticulo;
import com.nventory.model.VentaArticulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class VentaArticuloRepositori extends HardDeletableRepositoryImpl<VentaArticulo, Long> {
    public VentaArticuloRepositori() {
        super(VentaArticulo.class);
    }

    public List<VentaArticulo> buscarVentasArticuloPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            String sql = "SELECT * FROM ventaArticulo WHERE numeroVenta = :idVenta";
            TypedQuery<VentaArticulo> query = (TypedQuery<VentaArticulo>) em.createNativeQuery(sql, VentaArticulo.class);
            query.setParameter("idVenta", id);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
