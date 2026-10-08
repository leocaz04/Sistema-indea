package com.indea.servicio;

import com.indea.dao.UsuarioDAO;
import com.indea.dao.UsuarioDAOJdbc;
import com.indea.excepcion.ReglaNegocioException;
import com.indea.excepcion.ValidacionException;
import com.indea.modelo.Usuario;
import com.indea.util.SeguridadUtil;
import com.indea.util.Sesion;

public class AutenticacionService {

    private static final int MAX_INTENTOS = 3;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOJdbc();

    public Usuario iniciarSesion(String nombreUsuario, char[] clave) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null || clave.length == 0) {
            throw new ValidacionException("Escribe tu usuario y tu contraseña.");
        }
        Usuario u = usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim());
        if (u == null) {
            throw new ReglaNegocioException("Usuario o contraseña incorrectos.");
        }
        if (u.isBloqueado()) {
            throw new ReglaNegocioException("La cuenta está bloqueada por intentos fallidos. Pide al administrador que la desbloquee.");
        }
        if (!u.getEmpleado().isActivo()) {
            throw new ReglaNegocioException("Este empleado está dado de baja y no puede ingresar.");
        }
        if (!SeguridadUtil.verificar(new String(clave), u.getContrasenaHash())) {
            u.registrarIntentoFallido(MAX_INTENTOS);
            usuarioDAO.actualizarIntentos(u);
            if (u.isBloqueado()) {
                throw new ReglaNegocioException("Demasiados intentos fallidos. La cuenta quedó bloqueada.");
            }
            throw new ReglaNegocioException("Usuario o contraseña incorrectos. Intentos restantes: " + (MAX_INTENTOS - u.getIntentosFallidos()) + ".");
        }
        u.reiniciarIntentos();
        usuarioDAO.actualizarIntentos(u);
        Sesion.iniciar(u);
        return u;
    }

    public void cerrarSesion() {
        Sesion.cerrar();
    }
}
