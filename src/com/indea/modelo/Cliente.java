package com.indea.modelo;

import java.time.LocalDateTime;

public class Cliente extends Persona {

    private String direccion;
    private LocalDateTime fechaRegistro;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String telefono, String correo, String direccion, LocalDateTime fechaRegistro) {
        super(id, nombre, telefono, correo);
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
