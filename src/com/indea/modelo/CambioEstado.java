package com.indea.modelo;

import java.time.LocalDateTime;

public class CambioEstado {

    private int idCambio;
    private EstadoReparacion estado;
    private LocalDateTime fechaCambio;
    private Empleado empleado;

    public CambioEstado() {
    }

    public CambioEstado(int idCambio, EstadoReparacion estado, LocalDateTime fechaCambio, Empleado empleado) {
        this.idCambio = idCambio;
        this.estado = estado;
        this.fechaCambio = fechaCambio;
        this.empleado = empleado;
    }

    public int getIdCambio() {
        return idCambio;
    }

    public void setIdCambio(int idCambio) {
        this.idCambio = idCambio;
    }

    public EstadoReparacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReparacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}
