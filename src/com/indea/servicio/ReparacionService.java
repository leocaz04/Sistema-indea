package com.indea.servicio;

import com.indea.dao.EmpleadoDAO;
import com.indea.dao.EmpleadoDAOJdbc;
import com.indea.dao.ReparacionDAO;
import com.indea.dao.ReparacionDAOJdbc;
import com.indea.excepcion.ReglaNegocioException;
import com.indea.excepcion.ValidacionException;
import com.indea.modelo.CambioEstado;
import com.indea.modelo.Cliente;
import com.indea.modelo.Empleado;
import com.indea.modelo.EstadoReparacion;
import com.indea.modelo.Reparacion;
import com.indea.modelo.Rol;
import com.indea.util.Sesion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReparacionService {

    private final ReparacionDAO reparacionDAO = new ReparacionDAOJdbc();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAOJdbc();

    public Reparacion registrarReparacion(Cliente cliente, int idTecnico, String tipoEquipo, String marca, String modelo, String falla) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.TECNICO);
        if (cliente == null || cliente.getId() <= 0) {
            throw new ValidacionException("Busca y selecciona el cliente que entrega el equipo.");
        }
        if (tipoEquipo == null || tipoEquipo.isBlank()) {
            throw new ValidacionException("Indica el tipo de equipo.");
        }
        if (tipoEquipo.trim().length() > 50 || (marca != null && marca.trim().length() > 50) || (modelo != null && modelo.trim().length() > 50)) {
            throw new ValidacionException("Tipo, marca y modelo no pueden pasar de 50 caracteres.");
        }
        if (falla == null || falla.isBlank()) {
            throw new ValidacionException("Describe la falla reportada por el cliente.");
        }
        if (falla.trim().length() > 500) {
            throw new ValidacionException("La falla no puede pasar de 500 caracteres.");
        }
        Empleado tecnico = empleadoDAO.buscarPorId(idTecnico);
        if (tecnico == null || tecnico.getRol() != Rol.TECNICO || !tecnico.isActivo()) {
            throw new ValidacionException("Selecciona un técnico activo.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        Reparacion r = new Reparacion();
        r.setCliente(cliente);
        r.setTecnico(tecnico);
        r.setTipoEquipo(tipoEquipo.trim());
        r.setMarca(vacioANulo(marca));
        r.setModelo(vacioANulo(modelo));
        r.setFallaReportada(falla.trim());
        r.setEstado(EstadoReparacion.RECIBIDO);
        r.setFechaIngreso(ahora);
        r.setFolio(reparacionDAO.siguienteFolio());
        r.agregarCambioEstado(new CambioEstado(0, EstadoReparacion.RECIBIDO, ahora, Sesion.getEmpleado()));
        if (reparacionDAO.buscarPorFolio(r.getFolio()) != null) {
            throw new ReglaNegocioException("El folio " + r.getFolio() + " ya existe. Intenta de nuevo.");
        }
        reparacionDAO.insertar(r);
        return reparacionDAO.buscarPorFolio(r.getFolio());
    }

    public Reparacion actualizarReparacion(String folio, EstadoReparacion nuevoEstado, String diagnostico, BigDecimal costo, boolean pagado) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.TECNICO);
        Reparacion r = buscarObligatorio(folio);
        if (r.getEstado() == EstadoReparacion.ENTREGADO) {
            throw new ReglaNegocioException("La reparación ya fue entregada y su folio está cerrado.");
        }
        if (nuevoEstado == null) {
            throw new ValidacionException("Selecciona el estado de la reparación.");
        }
        if (nuevoEstado.ordinal() < r.getEstado().ordinal()) {
            throw new ReglaNegocioException("No se puede regresar una reparación a un estado anterior.");
        }
        if (costo == null || costo.signum() < 0) {
            throw new ValidacionException("El costo no puede ser negativo.");
        }
        if (costo.scale() > 2 || costo.precision() - costo.scale() > 8) {
            throw new ValidacionException("El costo debe tener máximo 2 decimales y no exceder 99,999,999.99.");
        }
        if (diagnostico != null && diagnostico.trim().length() > 500) {
            throw new ValidacionException("El diagnóstico no puede pasar de 500 caracteres.");
        }
        if (nuevoEstado == EstadoReparacion.ENTREGADO && !pagado) {
            throw new ReglaNegocioException("Para marcar el equipo como entregado, el servicio debe estar pagado.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        r.setDiagnostico(vacioANulo(diagnostico));
        r.setCostoServicio(costo);
        r.setPagado(pagado);
        if (nuevoEstado != r.getEstado()) {
            r.setEstado(nuevoEstado);
            r.agregarCambioEstado(new CambioEstado(0, nuevoEstado, ahora, Sesion.getEmpleado()));
            if (nuevoEstado == EstadoReparacion.ENTREGADO) {
                r.setFechaEntrega(ahora);
            }
        }
        reparacionDAO.actualizar(r);
        return reparacionDAO.buscarPorFolio(r.getFolio());
    }

    public Reparacion consultarPorFolio(String folio) {
        return buscarObligatorio(folio);
    }

    public List<Reparacion> listarReparaciones(String texto) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.TECNICO);
        return reparacionDAO.buscar(texto);
    }

    public List<Empleado> listarTecnicos() {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.TECNICO);
        return empleadoDAO.listarActivosPorRol(Rol.TECNICO);
    }

    public int contarActivas() {
        return reparacionDAO.contarActivas();
    }

    private Reparacion buscarObligatorio(String folio) {
        if (folio == null || folio.isBlank()) {
            throw new ValidacionException("Escribe el folio de la reparación.");
        }
        Reparacion r = reparacionDAO.buscarPorFolio(folio.trim().toUpperCase());
        if (r == null) {
            throw new ReglaNegocioException("El folio " + folio.trim().toUpperCase() + " no existe.");
        }
        return r;
    }

    private String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
