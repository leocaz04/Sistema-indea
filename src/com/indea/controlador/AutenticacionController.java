package com.indea.controlador;

import com.indea.modelo.Usuario;
import com.indea.servicio.AutenticacionService;

public class AutenticacionController {

    private final AutenticacionService servicio = new AutenticacionService();

    public Usuario iniciarSesion(String usuario, char[] clave) {
        return servicio.iniciarSesion(usuario, clave);
    }

    public void cerrarSesion() {
        servicio.cerrarSesion();
    }
}
