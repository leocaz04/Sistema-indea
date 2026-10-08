package com.indea.dao;

import com.indea.excepcion.PersistenciaException;
import com.indea.modelo.CambioEstado;
import com.indea.modelo.Cliente;
import com.indea.modelo.Empleado;
import com.indea.modelo.EstadoReparacion;
import com.indea.modelo.Reparacion;
import com.indea.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReparacionDAOJdbc implements ReparacionDAO {

    private static final String SELECT_BASE =
            "SELECT r.id_reparacion, r.folio, r.id_cliente, r.id_tecnico, r.tipo_equipo, r.marca, r.modelo, "
            + "r.falla_reportada, r.diagnostico, r.costo_servicio, r.pagado, r.estado, r.fecha_ingreso, r.fecha_entrega, "
            + "c.nombre AS c_nombre, c.telefono AS c_telefono, t.nombre AS t_nombre "
            + "FROM reparaciones r "
            + "JOIN clientes c ON c.id_cliente = r.id_cliente "
            + "JOIN empleados t ON t.id_empleado = r.id_tecnico ";

    private Reparacion mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("c_nombre"));
        c.setTelefono(rs.getString("c_telefono"));
        Empleado t = new Empleado();
        t.setId(rs.getInt("id_tecnico"));
        t.setNombre(rs.getString("t_nombre"));
        Reparacion r = new Reparacion();
        r.setIdReparacion(rs.getInt("id_reparacion"));
        r.setFolio(rs.getString("folio"));
        r.setCliente(c);
        r.setTecnico(t);
        r.setTipoEquipo(rs.getString("tipo_equipo"));
        r.setMarca(rs.getString("marca"));
        r.setModelo(rs.getString("modelo"));
        r.setFallaReportada(rs.getString("falla_reportada"));
        r.setDiagnostico(rs.getString("diagnostico"));
        r.setCostoServicio(rs.getBigDecimal("costo_servicio"));
        r.setPagado(rs.getBoolean("pagado"));
        r.setEstado(EstadoReparacion.valueOf(rs.getString("estado")));
        r.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());
        Timestamp entrega = rs.getTimestamp("fecha_entrega");
        if (entrega != null) {
            r.setFechaEntrega(entrega.toLocalDateTime());
        }
        return r;
    }

    private void cargarHistorial(Connection con, Reparacion r) throws SQLException {
        String sql = "SELECT h.id_historial, h.estado, h.fecha_cambio, h.id_empleado, e.nombre "
                + "FROM historial_estado_reparacion h JOIN empleados e ON e.id_empleado = h.id_empleado "
                + "WHERE h.id_reparacion = ? ORDER BY h.id_historial";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdReparacion());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Empleado e = new Empleado();
                    e.setId(rs.getInt("id_empleado"));
                    e.setNombre(rs.getString("nombre"));
                    r.agregarCambioEstado(new CambioEstado(
                            rs.getInt("id_historial"),
                            EstadoReparacion.valueOf(rs.getString("estado")),
                            rs.getTimestamp("fecha_cambio").toLocalDateTime(),
                            e));
                }
            }
        }
    }

    private void guardarHistorialNuevo(Connection con, Reparacion r) throws SQLException {
        String sql = "INSERT INTO historial_estado_reparacion (id_reparacion, estado, fecha_cambio, id_empleado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (CambioEstado ch : r.getHistorial()) {
                if (ch.getIdCambio() == 0) {
                    ps.setInt(1, r.getIdReparacion());
                    ps.setString(2, ch.getEstado().name());
                    ps.setTimestamp(3, Timestamp.valueOf(ch.getFechaCambio()));
                    ps.setInt(4, ch.getEmpleado().getId());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    @Override
    public int insertar(Reparacion r) {
        String sql = "INSERT INTO reparaciones (folio, id_cliente, id_tecnico, tipo_equipo, marca, modelo, falla_reportada, "
                + "diagnostico, costo_servicio, pagado, estado, fecha_ingreso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, r.getFolio());
                    ps.setInt(2, r.getCliente().getId());
                    ps.setInt(3, r.getTecnico().getId());
                    ps.setString(4, r.getTipoEquipo());
                    ps.setString(5, r.getMarca());
                    ps.setString(6, r.getModelo());
                    ps.setString(7, r.getFallaReportada());
                    ps.setString(8, r.getDiagnostico());
                    ps.setBigDecimal(9, r.getCostoServicio());
                    ps.setBoolean(10, r.isPagado());
                    ps.setString(11, r.getEstado().name());
                    ps.setTimestamp(12, Timestamp.valueOf(r.getFechaIngreso()));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }
                r.setIdReparacion(id);
                guardarHistorialNuevo(con, r);
                con.commit();
                return id;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar la reparación.", e);
        }
    }

    @Override
    public boolean actualizar(Reparacion r) {
        String sql = "UPDATE reparaciones SET diagnostico = ?, costo_servicio = ?, pagado = ?, estado = ?, fecha_entrega = ? "
                + "WHERE id_reparacion = ?";
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                boolean ok;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, r.getDiagnostico());
                    ps.setBigDecimal(2, r.getCostoServicio());
                    ps.setBoolean(3, r.isPagado());
                    ps.setString(4, r.getEstado().name());
                    ps.setTimestamp(5, r.getFechaEntrega() == null ? null : Timestamp.valueOf(r.getFechaEntrega()));
                    ps.setInt(6, r.getIdReparacion());
                    ok = ps.executeUpdate() > 0;
                }
                guardarHistorialNuevo(con, r);
                con.commit();
                return ok;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar la reparación.", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM reparaciones WHERE id_reparacion = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la reparación.", e);
        }
    }

    @Override
    public Reparacion buscarPorId(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE r.id_reparacion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Reparacion r = mapear(rs);
                cargarHistorial(con, r);
                return r;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la reparación.", e);
        }
    }

    @Override
    public Reparacion buscarPorFolio(String folio) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE r.folio = ?")) {
            ps.setString(1, folio);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Reparacion r = mapear(rs);
                cargarHistorial(con, r);
                return r;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la reparación.", e);
        }
    }

    @Override
    public List<Reparacion> listar() {
        return buscar("");
    }

    @Override
    public List<Reparacion> buscar(String texto) {
        String sql = SELECT_BASE + "WHERE r.folio LIKE ? OR c.nombre LIKE ? OR c.telefono LIKE ? ORDER BY r.id_reparacion DESC LIMIT 200";
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        List<Reparacion> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la lista de reparaciones.", e);
        }
    }

    @Override
    public String siguienteFolio() {
        String prefijo = "REP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT MAX(folio) FROM reparaciones WHERE folio LIKE ?")) {
            ps.setString(1, prefijo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                String max = rs.getString(1);
                int n = max == null ? 0 : Integer.parseInt(max.substring(prefijo.length()));
                return prefijo + String.format("%04d", n + 1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo generar el folio.", e);
        }
    }

    @Override
    public int contarActivas() {
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM reparaciones WHERE estado <> 'ENTREGADO'")) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo contar las reparaciones.", e);
        }
    }
}
