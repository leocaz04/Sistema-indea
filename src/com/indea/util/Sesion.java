package com.indea.util;

import com.indea.excepcion.AccesoDenegadoException;
import com.indea.modelo.Empleado;
import com.indea.modelo.Rol;
import com.indea.modelo.Usuario;

public final class Sesion {

    private static Usuario usuario;

    private Sesion() {
    }

    public static void iniciar(Usuario u) {
        usuario = u;
    }

    public static void cerrar() {
        usuario = null;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static Empleado getEmpleado() {
        return usuario == null ? null : usuario.getEmpleado();
    }

    public static Rol getRol() {
        return usuario == null ? null : usuario.getRol();
    }

    public static void exigirRol(Rol... permitidos) {
        if (usuario == null) {
            throw new AccesoDenegadoException("Debes iniciar sesión para realizar esta operación.");
        }
        for (Rol r : permitidos) {
            if (r == usuario.getRol()) {
                return;
            }
        }
        throw new AccesoDenegadoException("Tu rol (" + usuario.getRol().getEtiqueta() + ") no tiene permiso para esta operación.");
    }
}
