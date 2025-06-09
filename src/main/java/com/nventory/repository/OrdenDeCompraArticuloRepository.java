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

        //String sql = "SELECT * FROM ordendecompraarticulo WHERE codOrdenCompra = :codOrdenCompra";
        String sql = "SELECT * FROM OrdenDeCompraArticulo WHERE codOrdenCompra = :codOrdenCompra";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codOrdenCompra", codOrdenCompra);

        return query.getResultList();
    }

    public OrdenDeCompraArticulo buscarPorCodOrdenCompraYArticulo(Long codOrdenCompra, Long codOrdenCompraA) {
        EntityManager em = IndireccionJPA.getEntityManager();

        //String sql = "SELECT * FROM ordendecompraarticulo WHERE codOrdenCompraA = :codOrdenCompraA AND codOrdenCompra = :codOrdenCompra";
        String sql = "SELECT * FROM OrdenDeCompraArticulo WHERE codOrdenCompraA = :codOrdenCompraA AND codOrdenCompra = :codOrdenCompra";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codOrdenCompraA", codOrdenCompraA);
        query.setParameter("codOrdenCompra", codOrdenCompra);

        List<OrdenDeCompraArticulo> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.getFirst();
    }


    //-------------Metodo para buscar una OrdenCompraArticulo de acuerdo al articulo seleccionado-----------
    public List<OrdenDeCompraArticulo > buscarOrdenCompraArticuloDeArticulo (Long codArticuloProveedor){
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM OrdenDeCompraArticulo WHERE codArticuloProveedor = :codArticuloProveedor";
        TypedQuery<OrdenDeCompraArticulo> query = (TypedQuery<OrdenDeCompraArticulo>) em.createNativeQuery(sql, OrdenDeCompraArticulo.class);
        query.setParameter("codArticuloProveedor", codArticuloProveedor);

        return query.getResultList();

    }

    public boolean existePorOrdenYArticuloProveedor(Long codOrden, Long codArticuloProveedor) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("""
                SELECT COUNT(o) FROM OrdenDeCompraArticulo o
                WHERE o.ordenDeCompra.id = :codOrden
                AND o.articuloProveedor.id = :codArticuloProveedor
            """, Long.class)
                    .setParameter("codOrden", codOrden)
                    .setParameter("codArticuloProveedor", codArticuloProveedor)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public int buscarStockPendiente(Long codArticulo) {
        EntityManager em = getEntityManager();

        String jpql = """
        SELECT COALESCE(SUM(oca.cantidadSolicitadaOCA), 0)
        FROM OrdenDeCompraArticulo oca
        JOIN oca.articuloProveedor ap
        JOIN ap.articulo a
        JOIN oca.ordenDeCompra oc
        JOIN oc.estadoOrdenDeCompra e
        WHERE a.codArticulo = :codArticulo
        AND e.nombreEstadoOC = 'Enviada'
    """;

        return em.createQuery(jpql, Long.class)
                .setParameter("codArticulo", codArticulo)
                .getSingleResult()
                .intValue();
    }


}
