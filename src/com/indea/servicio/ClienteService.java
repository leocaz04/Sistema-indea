package com.indea.servicio;

import com.indea.dao.ClienteDAO;
import com.indea.dao.ClienteDAOJdbc;
import com.indea.excepcion.PersistenciaException;
import com.indea.excepcion.ReglaNegocioException;
import com.indea.excepcion.ValidacionException;
import com.indea.modelo.Cliente;
import com.indea.modelo.Rol;
import com.indea.util.Sesion;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAOJdbc();

    public Cliente registrarCliente(String nombre, String telefono, String correo, String direccion) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.VENDEDOR, Rol.TECNICO);
        Cliente c = construir(0, nombre, telefono, correo, direccion);
        Cliente existente = clienteDAO.buscarPorTelefono(c.getTelefono());
        if (existente != null) {
            throw new ReglaNegocioException("Ya existe un cliente con ese teléfono (" + existente.getNombre() + "). Usa el registro existente.");
        }
        c.setId(clienteDAO.insertar(c));
        return clienteDAO.buscarPorId(c.getId());
    }

    public void actualizarCliente(int id, String nombre, String telefono, String correo, String direccion) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.VENDEDOR, Rol.TECNICO);
        if (id <= 0) {
            throw new ValidacionException("Selecciona un cliente de la tabla para actualizarlo.");
        }
        Cliente c = construir(id, nombre, telefono, correo, direccion);
        Cliente existente = clienteDAO.buscarPorTelefono(c.getTelefono());
        if (existente != null && existente.getId() != id) {
            throw new ReglaNegocioException("Ese teléfono ya pertenece a otro cliente (" + existente.getNombre() + ").");
        }
        if (!clienteDAO.actualizar(c)) {
            throw new ReglaNegocioException("El cliente ya no existe.");
        }
    }

    public void eliminarCliente(int id) {
        Sesion.exigirRol(Rol.ADMINISTRADOR);
        if (id <= 0) {
            throw new ValidacionException("Selecciona un cliente de la tabla para eliminarlo.");
        }
        try {
            clienteDAO.eliminar(id);
        } catch (PersistenciaException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ReglaNegocioException("No se puede eliminar: el cliente ya tiene reparaciones, compras o ventas registradas.");
            }
            throw e;
        }
    }

    public List<Cliente> buscarClientes(String texto) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.VENDEDOR, Rol.TECNICO);
        return clienteDAO.buscar(texto);
    }

    public Cliente buscarPorTelefono(String telefono) {
        Sesion.exigirRol(Rol.ADMINISTRADOR, Rol.VENDEDOR, Rol.TECNICO);
        return clienteDAO.buscarPorTelefono(normalizarTelefono(telefono));
    }

    public int contarClientes() {
        return clienteDAO.contar();
    }

    private Cliente construir(int id, String nombre, String telefono, String correo, String direccion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new ValidacionException("El nombre no puede pasar de 100 caracteres.");
        }
        String tel = normalizarTelefono(telefono);
        if (!tel.matches("\\d{10}")) {
            throw new ValidacionException("El teléfono debe tener 10 dígitos.");
        }
        String mail = vacioANulo(correo);
        if (mail != null && (!mail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || mail.length() > 100)) {
            throw new ValidacionException("El correo no tiene un formato válido.");
        }
        String dir = vacioANulo(direccion);
        if (dir != null && dir.length() > 200) {
            throw new ValidacionException("La dirección no puede pasar de 200 caracteres.");
        }
        Cliente c = new Cliente();
        c.setId(id);
        c.setNombre(nombre.trim());
        c.setTelefono(tel);
        c.setCorreo(mail);
        c.setDireccion(dir);
        return c;
    }

    private String normalizarTelefono(String telefono) {
        return telefono == null ? "" : telefono.replaceAll("[\\s\\-()]", "");
    }

    private String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
