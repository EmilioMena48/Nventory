package com.nventory.repository;

import com.nventory.model.ConfiguracionInventario;
import jakarta.persistence.EntityManager;

public class ConfiguracionInventarioRepository extends SoftDeletableRepositoryImpl<ConfiguracionInventario, Long> {
    public ConfiguracionInventarioRepository() {
        super(ConfiguracionInventario.class);
    }

    public Long GuardarYRetornarID(ConfiguracionInventario model){
        EntityManager em = IndireccionJPA.getEntityManager();
        em.getTransaction().begin();
        em.persist(model);
        em.getTransaction().commit();
        return model.getCodConfiguracionInventario();
    }
}