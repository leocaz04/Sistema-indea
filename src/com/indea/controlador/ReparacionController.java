package com.indea.controlador;

import com.indea.excepcion.ValidacionException;
import com.indea.modelo.Cliente;
import com.indea.modelo.Empleado;
import com.indea.modelo.EstadoReparacion;
import com.indea.modelo.Reparacion;
import com.indea.servicio.ReparacionService;

import java.math.BigDecimal;
import java.util.List;

public class ReparacionController {

    private final ReparacionService servicio = new ReparacionService();

    public Reparacion registrar(Cliente cliente, Empleado tecnico, String tipo, String marca, String modelo, String falla) {
        if (tecnico == null) {
            throw new ValidacionException("Selecciona un técnico.");
        }
        return servicio.registrarReparacion(cliente, tecnico.getId(), tipo, marca, modelo, falla);
    }

    public Reparacion actualizar(String folio, EstadoReparacion estado, String diagnostico, String costoTexto, boolean pagado) {
        BigDecimal costo;
        try {
            costo = new BigDecimal(costoTexto == null || costoTexto.isBlank() ? "0" : costoTexto.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new ValidacionException("El costo debe ser un número válido, por ejemplo 350.50.");
        }
        return servicio.actualizarReparacion(folio, estado, diagnostico, costo, pagado);
    }

    public Reparacion consultarPorFolio(String folio) {
        return servicio.consultarPorFolio(folio);
    }

    public List<Reparacion> listar(String texto) {
        return servicio.listarReparaciones(texto);
    }

    public List<Empleado> listarTecnicos() {
        return servicio.listarTecnicos();
    }

    public int contarActivas() {
        return servicio.contarActivas();
    }
}
