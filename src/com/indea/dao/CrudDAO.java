package com.indea.dao;

import java.util.List;

public interface CrudDAO<T> {

    int insertar(T objeto);

    boolean actualizar(T objeto);

    boolean eliminar(int id);

    T buscarPorId(int id);

    List<T> listar();
}
