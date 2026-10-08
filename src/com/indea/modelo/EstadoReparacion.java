package com.indea.modelo;

public enum EstadoReparacion {
    RECIBIDO("Recibido"),
    EN_DIAGNOSTICO("En diagnóstico"),
    EN_REPARACION("En reparación"),
    LISTO_PARA_ENTREGA("Listo para entrega"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoReparacion(String etiqueta) {
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
