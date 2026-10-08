package com.indea.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reparacion {

    private int idReparacion;
    private String folio;
    private Cliente cliente;
    private Empleado tecnico;
    private String tipoEquipo;
    private String marca;
    private String modelo;
    private String fallaReportada;
    private String diagnostico;
    private BigDecimal costoServicio = BigDecimal.ZERO;
    private boolean pagado;
    private EstadoReparacion estado = EstadoReparacion.RECIBIDO;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEntrega;
    private final List<CambioEstado> historial = new ArrayList<>();

    public int getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(int idReparacion) {
        this.idReparacion = idReparacion;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getTecnico() {
        return tecnico;
    }

    public void setTecnico(Empleado tecnico) {
        this.tecnico = tecnico;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFallaReportada() {
        return fallaReportada;
    }

    public void setFallaReportada(String fallaReportada) {
        this.fallaReportada = fallaReportada;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public BigDecimal getCostoServicio() {
        return costoServicio;
    }

    public void setCostoServicio(BigDecimal costoServicio) {
        this.costoServicio = costoServicio;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public EstadoReparacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReparacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public List<CambioEstado> getHistorial() {
        return historial;
    }

    public void agregarCambioEstado(CambioEstado c) {
        historial.add(c);
    }

    public String getEquipoDescripcion() {
        StringBuilder sb = new StringBuilder(tipoEquipo == null ? "" : tipoEquipo);
        if (marca != null && !marca.isBlank()) {
            sb.append(" ").append(marca);
        }
        if (modelo != null && !modelo.isBlank()) {
            sb.append(" ").append(modelo);
        }
        return sb.toString();
    }
}
