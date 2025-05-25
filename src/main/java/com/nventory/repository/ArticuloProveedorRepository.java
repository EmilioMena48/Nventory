package com.nventory.repository;

import com.nventory.model.ArticuloProveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ArticuloProveedorRepository extends SoftDeletableRepositoryImpl<ArticuloProveedor, Long> {
    public ArticuloProveedorRepository() {
        super(ArticuloProveedor.class);
    }

    public ArticuloProveedor buscarPorCodArticuloYProveedor(Long codArticulo, Long codProveedor) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM articuloproveedor WHERE codArticulo = :codArticulo AND codProveedor = :codProveedor AND fechaHoraBajaArticuloProveedor IS NULL";
        TypedQuery<ArticuloProveedor> query = (TypedQuery<ArticuloProveedor>) em.createNativeQuery(sql, ArticuloProveedor.class);
        query.setParameter("codProveedor", codProveedor);
        query.setParameter("codArticulo", codArticulo);

        List<ArticuloProveedor> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.getFirst();
    }

    //-------------------Metodo del repository para obtener Proveedores del articulo seleccionado---------------------
    public List<ArticuloProveedor> buscarTodosArticuloProveedor(Long codArticulo){
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM articuloproveedor WHERE codArticulo = :codArticulo AND fechaHoraBajaArticuloProveedor IS NULL";
        TypedQuery<ArticuloProveedor> query = (TypedQuery<ArticuloProveedor>) em.createNativeQuery(sql, ArticuloProveedor.class);
        query.setParameter("codArticulo", codArticulo);
        return query.getResultList();

    }

    //-------------------Metodo del repository para obtener Articulos del proveedor seleccionado---------------------
    public List<ArticuloProveedor> buscarTodosArticulosDelProveedor(Long codProveedor) {
        EntityManager em = IndireccionJPA.getEntityManager();

        String sql = "SELECT * FROM articuloproveedor WHERE codProveedor = :codProveedor AND fechaHoraBajaArticuloProveedor IS NULL";
        TypedQuery<ArticuloProveedor> query = (TypedQuery<ArticuloProveedor>) em.createNativeQuery(sql, ArticuloProveedor.class);
        query.setParameter("codProveedor", codProveedor);
        return query.getResultList();
    }
}