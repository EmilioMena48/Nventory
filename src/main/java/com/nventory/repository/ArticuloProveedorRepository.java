package com.nventory.repository;

import com.nventory.model.Articulo;
import com.nventory.model.ArticuloProveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class ArticuloProveedorRepository extends SoftDeletableRepositoryImpl<ArticuloProveedor, Long> {
    public ArticuloProveedorRepository() {
        super(ArticuloProveedor.class);
    }

    public ArticuloProveedor buscarPorCodArticuloYProveedor(Long codArticulo, Long codProveedor) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String jpql = "SELECT ap FROM ArticuloProveedor ap WHERE ap.articulo.codArticulo = :codArticulo AND ap.proveedor.codProveedor = :codProveedor AND ap.fechaHoraBajaArticuloProveedor IS NULL";
        TypedQuery<ArticuloProveedor> query = em.createQuery(jpql, ArticuloProveedor.class);
        query.setParameter("codArticulo", codArticulo);
        query.setParameter("codProveedor", codProveedor);

        List<ArticuloProveedor> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.getFirst();
    }


    //-------------------Metodo del repository para obtener Proveedores del articulo seleccionado---------------------
    public List<ArticuloProveedor> buscarTodosArticuloProveedor(Long codArticulo){
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM ArticuloProveedor WHERE codArticulo = :codArticulo AND fechaHoraBajaArticuloProveedor IS NULL";
        TypedQuery<ArticuloProveedor> query = (TypedQuery<ArticuloProveedor>) em.createNativeQuery(sql, ArticuloProveedor.class);
        query.setParameter("codArticulo", codArticulo);
        return query.getResultList();

    }

    //-------------------Metodo del repository para obtener Articulos del proveedor seleccionado---------------------
    public List<ArticuloProveedor> buscarTodosArticulosDelProveedor(Long codProveedor) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM ArticuloProveedor WHERE codProveedor = :codProveedor AND fechaHoraBajaArticuloProveedor IS NULL";
        Query query = em.createNativeQuery(sql, ArticuloProveedor.class);
        query.setParameter("codProveedor", codProveedor);
        return query.getResultList();
    }

    public ArticuloProveedor buscarArticuloProveedorMasBarato(Long codArticulo) {
        EntityManager em = IndireccionJPA.getEntityManager();
        String jpql = "SELECT ap FROM ArticuloProveedor ap " +
                "WHERE ap.articulo.codArticulo = :codArticulo " +
                "ORDER BY ap.precioUnitario ASC";

        return em.createQuery(jpql, ArticuloProveedor.class)
                .setParameter("codArticulo", codArticulo)
                .setMaxResults(1)
                .getSingleResult();
    }


    //-------------------Metodo del repository para obtener todos los objetos ArticuloProveedor relacionados a un articulo en particular---------------------
    public List<ArticuloProveedor> buscarArticuloProveedorPorArticulo(Articulo articulo) {
        EntityManager em = getEntityManager();
        List<ArticuloProveedor> articuloProveedorList = new ArrayList<>();

        try {
            articuloProveedorList = em.createQuery(
                            "SELECT ap FROM ArticuloProveedor ap WHERE ap.articulo = :articulo AND ap.fechaHoraBajaArticuloProveedor IS NULL",
                            ArticuloProveedor.class)
                    .setParameter("articulo", articulo)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return articuloProveedorList;
    }
}