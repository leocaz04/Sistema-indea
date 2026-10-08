package com.indea.dao;

import com.indea.modelo.Reparacion;

import java.util.List;

public interface ReparacionDAO extends CrudDAO<Reparacion> {

    Reparacion buscarPorFolio(String folio);

    String siguienteFolio();

    List<Reparacion> buscar(String texto);

    int contarActivas();
}
