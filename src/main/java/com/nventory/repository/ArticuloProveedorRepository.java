package com.nventory.repository;

import com.nventory.model.ArticuloProveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

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
}