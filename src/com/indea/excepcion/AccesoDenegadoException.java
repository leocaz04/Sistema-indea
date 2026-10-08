package com.indea.excepcion;

public class AccesoDenegadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }

    public AccesoDenegadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
