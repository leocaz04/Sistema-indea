package com.indea.modelo;

public enum Rol {
    ADMINISTRADOR("Administrador"),
    TECNICO("Técnico"),
    VENDEDOR("Vendedor"),
    MARKETING("Marketing");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
