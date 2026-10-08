package com.indea.modelo;

import java.time.LocalDate;

public class Empleado extends Persona {

    private Rol rol;
    private boolean activo;
    private LocalDate fechaIngreso;

    public Empleado() {
    }

    public Empleado(int id, String nombre, String telefono, String correo, Rol rol, boolean activo, LocalDate fechaIngreso) {
        super(id, nombre, telefono, correo);
        this.rol = rol;
        this.activo = activo;
        this.fechaIngreso = fechaIngreso;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
