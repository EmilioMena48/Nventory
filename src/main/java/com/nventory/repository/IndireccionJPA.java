package com.nventory.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class IndireccionJPA {
    private static final EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory("InventarioPU");
        } catch (Throwable ex) {
            System.err.println("Fallo al inicializar EntityManagerFactory" + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close(){
        if (emf != null){
            emf.close();
        }
    }
}
