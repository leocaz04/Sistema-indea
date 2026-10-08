package com.indea.dao;

import com.indea.modelo.Usuario;

public interface UsuarioDAO extends CrudDAO<Usuario> {

    Usuario buscarPorNombreUsuario(String nombreUsuario);

    void actualizarIntentos(Usuario usuario);
}
