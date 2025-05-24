package com.nventory.repository;

import com.nventory.model.OrdenDeCompra;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class OrdenDeCompraRepository extends BaseRepositoryImpl <OrdenDeCompra, Long> {
    public OrdenDeCompraRepository() {
        super(OrdenDeCompra.class);
    }

    public Optional<OrdenDeCompra> buscarOrdenPendienteOEnviadaPorProveedor(Long codProveedor) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String jpql = "SELECT o FROM OrdenDeCompra o " +
                "WHERE o.proveedor.codProveedor = :codProveedor " +
                "AND (o.estadoOrdenDeCompra.nombreEstadoOC = 'Pendiente' OR o.estadoOrdenDeCompra.nombreEstadoOC = 'Enviada')";

        TypedQuery<OrdenDeCompra> query = em.createQuery(jpql, OrdenDeCompra.class);
        query.setParameter("codProveedor", codProveedor);

        List<OrdenDeCompra> resultados = query.getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    public Optional<OrdenDeCompra> buscarOrdenPendienteOEnviadaPorArticulo(Long codArticulo) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String jpql = "SELECT oca.ordenDeCompra FROM OrdenDeCompraArticulo oca " +
                "JOIN oca.articuloProveedor ap " +
                "JOIN ap.articulo a " +
                "JOIN oca.ordenDeCompra oc " +
                "JOIN oc.estadoOrdenDeCompra estado " +
                "WHERE a.codArticulo = :codArticulo " +
                "AND (estado.nombreEstadoOC = 'Pendiente' OR estado.nombreEstadoOC = 'Enviada')";

        TypedQuery<OrdenDeCompra> query = em.createQuery(jpql, OrdenDeCompra.class);
        query.setParameter("codArticulo", codArticulo);

        List<OrdenDeCompra> resultados = query.getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }










}
