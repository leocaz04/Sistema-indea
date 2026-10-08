package com.indea.modelo;

public class Usuario {

    private int idUsuario;
    private String nombreUsuario;
    private String contrasenaHash;
    private int intentosFallidos;
    private boolean bloqueado;
    private Empleado empleado;

    public Usuario() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Rol getRol() {
        return empleado.getRol();
    }

    public void registrarIntentoFallido(int max) {
        intentosFallidos++;
        if (intentosFallidos >= max) {
            bloqueado = true;
        }
    }

    public void reiniciarIntentos() {
        intentosFallidos = 0;
    }
}
