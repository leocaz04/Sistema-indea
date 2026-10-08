package com.indea.util;

import com.indea.excepcion.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatosIniciales {

    private DatosIniciales() {
    }

    public static void crearSiVacio() {
        try (Connection c = ConexionBD.obtener()) {
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return;
                }
            }
            c.setAutoCommit(false);
            try {
                crear(c, "Dueño INDEA", "2281000001", "ADMINISTRADOR", "admin", "admin123");
                crear(c, "Técnico de Prueba", "2281000002", "TECNICO", "tecnico1", "tecnico123");
                crear(c, "Vendedor de Prueba", "2281000003", "VENDEDOR", "vendedor1", "vendedor123");
                crear(c, "Marketing de Prueba", "2281000004", "MARKETING", "marketing1", "marketing123");
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron crear los usuarios iniciales. ¿Ya ejecutaste el script SQL en MySQL Workbench?", e);
        }
    }

    private static void crear(Connection c, String nombre, String telefono, String rol, String usuario, String clave) throws SQLException {
        int idEmpleado;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO empleados (nombre, telefono, rol, fecha_ingreso) VALUES (?, ?, ?, CURDATE())",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, rol);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idEmpleado = rs.getInt(1);
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO usuarios (id_empleado, nombre_usuario, contrasena_hash) VALUES (?, ?, ?)")) {
            ps.setInt(1, idEmpleado);
            ps.setString(2, usuario);
            ps.setString(3, SeguridadUtil.hashear(clave));
            ps.executeUpdate();
        }
    }
}
