package com.nventory.repository;

import com.nventory.model.Articulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class ArticuloRepository extends SoftDeletableRepositoryImpl<Articulo, Long> {

    public ArticuloRepository() {
        super(Articulo.class);
    }

    //Para buscar art por nombre, lo uso en venta
    public Articulo buscarArticuloPorNombre(String nombre) {
        EntityManager em = getEntityManager();
        Articulo articuloEncontrado = null;
        try {
            em.getTransaction().begin();


            TypedQuery<Articulo> query = em.createQuery(
                    "SELECT a FROM Articulo a WHERE a.nombreArticulo = :nombre", Articulo.class);
            query.setParameter("nombre", nombre);


            articuloEncontrado = query.getSingleResult();


            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("No se encontró un artículo con ese nombre.");
            em.getTransaction().rollback();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
        return articuloEncontrado;
    }

    //-------------------Metodo del repository para retornar la cantidad de articulos---------------------
    public Long contarArticulos() {
        EntityManager em = IndireccionJPA.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Articulo p", Long.class).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
