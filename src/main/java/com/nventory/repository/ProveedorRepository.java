package com.nventory.repository;

import com.nventory.model.Proveedor;
import jakarta.persistence.EntityManager;

public class ProveedorRepository extends SoftDeletableRepositoryImpl<Proveedor, Long> {
    public ProveedorRepository() {
        super(Proveedor.class);
    }

    //-------------------Metodo del repository para obtener Proveedores del articulo seleccionado---------------------
    public Long GuardarYRetornarID(Proveedor proveedor){
        EntityManager em = IndireccionJPA.getEntityManager();
        em.getTransaction().begin();
        em.persist(proveedor);
        em.getTransaction().commit();
        return proveedor.getCodProveedor();
    }
}