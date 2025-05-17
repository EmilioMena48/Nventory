package com.nventory.repository;

import com.nventory.model.ConfiguracionInventario;

public class ConfiguracionInventarioRepository extends SoftDeletableRepositoryImpl<ConfiguracionInventario, Long> {
    public ConfiguracionInventarioRepository() {
        super(ConfiguracionInventario.class);
    }
}
