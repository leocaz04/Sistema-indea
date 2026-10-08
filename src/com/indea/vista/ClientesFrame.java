package com.indea.vista;

import com.indea.controlador.ClienteController;
import com.indea.modelo.Cliente;
import com.indea.modelo.Rol;
import com.indea.util.Sesion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class ClientesFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ClienteController controlador = new ClienteController();
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(20);
    private final JTextField txtBuscar = new JTextField(18);
    private final DefaultTableModel modelo = Ui.modeloTabla("Nombre", "Teléfono", "Correo", "Dirección", "Registro");
    private final JTable tabla = new JTable(modelo);
    private final List<Cliente> mostrados = new ArrayList<>();
    private int idSeleccionado = 0;

    public ClientesFrame() {
        setTitle("INDEA - Clientes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Ui.encabezado("Clientes", "Registrar, consultar y editar los datos de los clientes"), BorderLayout.NORTH);

        JPanel form = Ui.formulario();
        form.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
        Ui.fila(form, 0, "Nombre *", txtNombre);
        Ui.fila(form, 1, "Teléfono * (10 dígitos)", txtTelefono);
        Ui.fila(form, 2, "Correo", txtCorreo);
        Ui.fila(form, 3, "Dirección", txtDireccion);

        JButton btnGuardar = new JButton("Guardar nuevo");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnEliminar.setEnabled(Sesion.getRol() == Rol.ADMINISTRADOR);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 4));
        izquierda.add(form, BorderLayout.NORTH);
        izquierda.add(botones, BorderLayout.CENTER);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        busqueda.add(new JLabel("Nombre o teléfono:"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));
        derecha.add(busqueda, BorderLayout.NORTH);
        derecha.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(izquierda, BorderLayout.WEST);
        add(derecha, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnBuscar.addActionListener(e -> refrescar());
        txtBuscar.addActionListener(e -> refrescar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        setSize(960, 480);
        setLocationRelativeTo(null);
        refrescar();
    }

    private void refrescar() {
        try {
            List<Cliente> lista = controlador.buscar(txtBuscar.getText());
            modelo.setRowCount(0);
            mostrados.clear();
            for (Cliente c : lista) {
                mostrados.add(c);
                modelo.addRow(new Object[] {c.getNombre(), c.getTelefono(), c.getCorreo(), c.getDireccion(),
                        c.getFechaRegistro().format(Ui.FECHA_HORA)});
            }
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= mostrados.size()) {
            return;
        }
        Cliente c = mostrados.get(fila);
        idSeleccionado = c.getId();
        txtNombre.setText(c.getNombre());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo() == null ? "" : c.getCorreo());
        txtDireccion.setText(c.getDireccion() == null ? "" : c.getDireccion());
    }

    private void guardar() {
        try {
            Cliente c = controlador.registrar(txtNombre.getText(), txtTelefono.getText(), txtCorreo.getText(), txtDireccion.getText());
            Ui.info(this, "Cliente registrado: " + c.getNombre());
            limpiar();
            refrescar();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void actualizar() {
        try {
            controlador.actualizar(idSeleccionado, txtNombre.getText(), txtTelefono.getText(), txtCorreo.getText(), txtDireccion.getText());
            Ui.info(this, "Datos del cliente actualizados.");
            limpiar();
            refrescar();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void eliminar() {
        if (idSeleccionado <= 0) {
            Ui.aviso(this, "Selecciona un cliente de la tabla para eliminarlo.");
            return;
        }
        if (!Ui.confirmar(this, "¿Seguro que quieres eliminar a " + txtNombre.getText() + "?")) {
            return;
        }
        try {
            controlador.eliminar(idSeleccionado);
            limpiar();
            refrescar();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        tabla.clearSelection();
    }
}
