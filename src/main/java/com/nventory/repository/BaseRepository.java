package com.nventory.repository;

import java.util.List;

public interface BaseRepository<T, ID> {
    List<T> buscarTodos();
    T buscarPorId(ID id);
    void guardar(T entity);
}

