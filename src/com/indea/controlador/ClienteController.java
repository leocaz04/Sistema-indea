package com.indea.controlador;

import com.indea.modelo.Cliente;
import com.indea.servicio.ClienteService;

import java.util.List;

public class ClienteController {

    private final ClienteService servicio = new ClienteService();

    public Cliente registrar(String nombre, String telefono, String correo, String direccion) {
        return servicio.registrarCliente(nombre, telefono, correo, direccion);
    }

    public void actualizar(int id, String nombre, String telefono, String correo, String direccion) {
        servicio.actualizarCliente(id, nombre, telefono, correo, direccion);
    }

    public void eliminar(int id) {
        servicio.eliminarCliente(id);
    }

    public List<Cliente> buscar(String texto) {
        return servicio.buscarClientes(texto);
    }

    public Cliente buscarPorTelefono(String telefono) {
        return servicio.buscarPorTelefono(telefono);
    }

    public int contar() {
        return servicio.contarClientes();
    }
}
