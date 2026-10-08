package com.indea.dao;

import com.indea.excepcion.PersistenciaException;
import com.indea.modelo.Empleado;
import com.indea.modelo.Rol;
import com.indea.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAOJdbc implements EmpleadoDAO {

    private static final String COLUMNAS = "id_empleado, nombre, telefono, correo, rol, activo, fecha_ingreso";

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setTelefono(rs.getString("telefono"));
        e.setCorreo(rs.getString("correo"));
        e.setRol(Rol.valueOf(rs.getString("rol")));
        e.setActivo(rs.getBoolean("activo"));
        e.setFechaIngreso(rs.getDate("fecha_ingreso").toLocalDate());
        return e;
    }

    @Override
    public int insertar(Empleado e) {
        String sql = "INSERT INTO empleados (nombre, telefono, correo, rol, activo, fecha_ingreso) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getCorreo());
            ps.setString(4, e.getRol().name());
            ps.setBoolean(5, e.isActivo());
            ps.setDate(6, Date.valueOf(e.getFechaIngreso()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo guardar el empleado.", ex);
        }
    }

    @Override
    public boolean actualizar(Empleado e) {
        String sql = "UPDATE empleados SET nombre = ?, telefono = ?, correo = ?, rol = ?, activo = ? WHERE id_empleado = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getCorreo());
            ps.setString(4, e.getRol().name());
            ps.setBoolean(5, e.isActivo());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo actualizar el empleado.", ex);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM empleados WHERE id_empleado = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo eliminar el empleado.", ex);
        }
    }

    @Override
    public Empleado buscarPorId(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNAS + " FROM empleados WHERE id_empleado = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo consultar el empleado.", ex);
        }
    }

    @Override
    public List<Empleado> listar() {
        List<Empleado> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT " + COLUMNAS + " FROM empleados ORDER BY nombre")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo consultar la lista de empleados.", ex);
        }
    }

    @Override
    public List<Empleado> listarActivosPorRol(Rol rol) {
        List<Empleado> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNAS + " FROM empleados WHERE rol = ? AND activo = TRUE ORDER BY nombre")) {
            ps.setString(1, rol.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo consultar la lista de empleados.", ex);
        }
    }
}
