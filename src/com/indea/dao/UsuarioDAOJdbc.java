package com.indea.dao;

import com.indea.excepcion.PersistenciaException;
import com.indea.modelo.Empleado;
import com.indea.modelo.Rol;
import com.indea.modelo.Usuario;
import com.indea.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOJdbc implements UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.nombre_usuario, u.contrasena_hash, u.intentos_fallidos, u.bloqueado, "
            + "e.id_empleado, e.nombre, e.telefono, e.correo, e.rol, e.activo, e.fecha_ingreso "
            + "FROM usuarios u JOIN empleados e ON e.id_empleado = u.id_empleado ";

    private Usuario mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("id_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setTelefono(rs.getString("telefono"));
        e.setCorreo(rs.getString("correo"));
        e.setRol(Rol.valueOf(rs.getString("rol")));
        e.setActivo(rs.getBoolean("activo"));
        e.setFechaIngreso(rs.getDate("fecha_ingreso").toLocalDate());
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setContrasenaHash(rs.getString("contrasena_hash"));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        u.setBloqueado(rs.getBoolean("bloqueado"));
        u.setEmpleado(e);
        return u;
    }

    @Override
    public int insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (id_empleado, nombre_usuario, contrasena_hash) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, u.getEmpleado().getId());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getContrasenaHash());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el usuario.", e);
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, contrasena_hash = ?, intentos_fallidos = ?, bloqueado = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasenaHash());
            ps.setInt(3, u.getIntentosFallidos());
            ps.setBoolean(4, u.isBloqueado());
            ps.setInt(5, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el usuario.", e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE u.id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY u.nombre_usuario")) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la lista de usuarios.", e);
        }
    }

    @Override
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE u.nombre_usuario = ?")) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public void actualizarIntentos(Usuario u) {
        String sql = "UPDATE usuarios SET intentos_fallidos = ?, bloqueado = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getIntentosFallidos());
            ps.setBoolean(2, u.isBloqueado());
            ps.setInt(3, u.getIdUsuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar los intentos de acceso.", e);
        }
    }
}
