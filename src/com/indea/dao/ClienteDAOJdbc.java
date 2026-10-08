package com.indea.dao;

import com.indea.excepcion.PersistenciaException;
import com.indea.modelo.Cliente;
import com.indea.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJdbc implements ClienteDAO {

    private static final String COLUMNAS = "id_cliente, nombre, telefono, correo, direccion, fecha_registro";

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setDireccion(rs.getString("direccion"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return c;
    }

    @Override
    public int insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, telefono, correo, direccion) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el cliente.", e);
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, correo = ?, direccion = ? WHERE id_cliente = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getDireccion());
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el cliente.", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM clientes WHERE id_cliente = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el cliente.", e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNAS + " FROM clientes WHERE id_cliente = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el cliente.", e);
        }
    }

    @Override
    public List<Cliente> listar() {
        return buscar("");
    }

    @Override
    public Cliente buscarPorTelefono(String telefono) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNAS + " FROM clientes WHERE telefono = ?")) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el cliente.", e);
        }
    }

    @Override
    public List<Cliente> buscar(String texto) {
        String sql = "SELECT " + COLUMNAS + " FROM clientes WHERE nombre LIKE ? OR telefono LIKE ? ORDER BY nombre";
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la lista de clientes.", e);
        }
    }

    @Override
    public int contar() {
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM clientes")) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo contar los clientes.", e);
        }
    }
}
