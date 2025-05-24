package com.nventory.repository;

import com.nventory.model.OrdenDeCompraArticulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class OrdenDeCompraArticuloRepository extends HardDeletableRepositoryImpl<OrdenDeCompraArticulo, Long> {
    public OrdenDeCompraArticuloRepository() {
        super(OrdenDeCompraArticulo.class);
    }


    public List<OrdenDeCompraArticulo> buscarOCAdeUnaOC(Long codOrdenCompra) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM ordendecompraarticulo WHERE codOrdenCompra = :codOrdenCompra";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codOrdenCompra", codOrdenCompra);

        return query.getResultList();
    }

    public OrdenDeCompraArticulo buscarPorCodOrdenCompraYArticulo(Long codOrdenCompra, Long codOrdenCompraA) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM ordendecompraarticulo WHERE codOrdenCompraA = :codOrdenCompraA AND codOrdenCompra = :codOrdenCompra";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codOrdenCompraA", codOrdenCompraA);
        query.setParameter("codOrdenCompra", codOrdenCompra);

        List<OrdenDeCompraArticulo> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.getFirst();
    }


    //-------------Metodo para buscar una OrdenCompraArticulo de acuerdo al articulo seleccionado-----------
    public List<OrdenDeCompraArticulo > buscarOrdenCompraArticuloDeArticulo (Long codArticuloProveedor){
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM ordencompraarticulo WHERE codArticuloProveedor = :codArticuloProveedor";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codArticuloProveedor", codArticuloProveedor);

        return query.getResultList();

    }


}
