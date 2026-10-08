package com.indea.dao;

import com.indea.modelo.Cliente;

import java.util.List;

public interface ClienteDAO extends CrudDAO<Cliente> {

    Cliente buscarPorTelefono(String telefono);

    List<Cliente> buscar(String texto);

    int contar();
}
