package com.indea.vista;

import com.indea.controlador.ClienteController;
import com.indea.controlador.ReparacionController;
import com.indea.modelo.CambioEstado;
import com.indea.modelo.Cliente;
import com.indea.modelo.Empleado;
import com.indea.modelo.EstadoReparacion;
import com.indea.modelo.Reparacion;
import com.indea.modelo.Rol;
import com.indea.util.Sesion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class ReparacionesFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ReparacionController controlador = new ReparacionController();
    private final ClienteController clienteController = new ClienteController();

    private final JTextField txtTelefonoCliente = new JTextField(14);
    private final JLabel lblCliente = new JLabel("Ningún cliente seleccionado");
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[] {"Laptop", "PC de escritorio", "All in one", "Impresora", "Monitor", "Otro"});
    private final JTextField txtMarca = new JTextField(20);
    private final JTextField txtModelo = new JTextField(20);
    private final JTextArea txtFalla = new JTextArea(4, 20);
    private final JComboBox<Empleado> cmbTecnico = new JComboBox<>();
    private Cliente clienteSeleccionado;

    private final JTextField txtBuscar = new JTextField(18);
    private final DefaultTableModel modelo = Ui.modeloTabla("Folio", "Cliente", "Equipo", "Estado", "Ingreso");
    private final JTable tabla = new JTable(modelo);
    private final List<Reparacion> mostradas = new ArrayList<>();
    private final JLabel lblFolio = new JLabel("Selecciona una reparación de la tabla");
    private final JComboBox<EstadoReparacion> cmbEstado = new JComboBox<>(EstadoReparacion.values());
    private final JTextField txtCosto = new JTextField(10);
    private final JCheckBox chkPagado = new JCheckBox("Servicio pagado");
    private final JTextArea txtDiagnostico = new JTextArea(4, 20);
    private final JTextArea txtHistorial = new JTextArea(6, 22);
    private final JButton btnGuardarCambios = new JButton("Guardar cambios");
    private String folioSeleccionado;

    public ReparacionesFrame() {
        setTitle("INDEA - Reparaciones");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Ui.encabezado("Reparaciones", "Recibir equipos, generar folio y dar seguimiento al servicio"), BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Nueva reparación", construirNueva());
        pestanas.addTab("Seguimiento", construirSeguimiento());
        add(pestanas, BorderLayout.CENTER);

        cargarTecnicos();
        setSize(900, 600);
        setLocationRelativeTo(null);
        refrescarTabla();
        habilitarEdicion(false);
    }

    private JPanel construirNueva() {
        JPanel form = Ui.formulario();
        form.setBorder(BorderFactory.createTitledBorder("Datos del equipo recibido"));

        JPanel filaCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton btnBuscarCliente = new JButton("Buscar");
        JButton btnNuevoCliente = new JButton("Registrar cliente nuevo");
        filaCliente.add(txtTelefonoCliente);
        filaCliente.add(btnBuscarCliente);
        filaCliente.add(btnNuevoCliente);

        cmbTipo.setEditable(true);
        txtFalla.setLineWrap(true);
        txtFalla.setWrapStyleWord(true);

        Ui.fila(form, 0, "Teléfono del cliente *", filaCliente);
        Ui.fila(form, 1, "Cliente", lblCliente);
        Ui.fila(form, 2, "Tipo de equipo *", cmbTipo);
        Ui.fila(form, 3, "Marca", txtMarca);
        Ui.fila(form, 4, "Modelo", txtModelo);
        Ui.fila(form, 5, "Falla reportada *", new JScrollPane(txtFalla));
        Ui.fila(form, 6, "Técnico asignado *", cmbTecnico);

        JButton btnRegistrar = new JButton("Registrar y generar folio");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnLimpiar);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(form, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.CENTER);

        btnBuscarCliente.addActionListener(e -> buscarCliente());
        txtTelefonoCliente.addActionListener(e -> buscarCliente());
        btnNuevoCliente.addActionListener(e -> new ClientesFrame().setVisible(true));
        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiarNueva());
        return panel;
    }

    private JPanel construirSeguimiento() {
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodas = new JButton("Ver todas");
        busqueda.add(new JLabel("Folio, cliente o teléfono:"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(btnTodas);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);

        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel izquierda = Ui.formulario();
        izquierda.setBorder(BorderFactory.createTitledBorder("Actualizar reparación"));
        Ui.fila(izquierda, 0, "Folio", lblFolio);
        Ui.fila(izquierda, 1, "Estado", cmbEstado);
        Ui.fila(izquierda, 2, "Costo del servicio", txtCosto);
        Ui.fila(izquierda, 3, "", chkPagado);
        Ui.fila(izquierda, 4, "Diagnóstico", new JScrollPane(txtDiagnostico));

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBorder(BorderFactory.createTitledBorder("Historial de estados"));
        derecha.add(new JScrollPane(txtHistorial), BorderLayout.CENTER);

        JPanel detalle = new JPanel(new GridLayout(1, 2, 8, 0));
        detalle.add(izquierda);
        detalle.add(derecha);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(detalle, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGuardarCambios);
        sur.add(botones, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(busqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> refrescarTabla());
        txtBuscar.addActionListener(e -> refrescarTabla());
        btnTodas.addActionListener(e -> {
            txtBuscar.setText("");
            refrescarTabla();
        });
        btnGuardarCambios.addActionListener(e -> guardarCambios());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
        return panel;
    }

    private void cargarTecnicos() {
        try {
            cmbTecnico.removeAllItems();
            for (Empleado t : controlador.listarTecnicos()) {
                cmbTecnico.addItem(t);
            }
            if (Sesion.getRol() == Rol.TECNICO) {
                for (int i = 0; i < cmbTecnico.getItemCount(); i++) {
                    if (cmbTecnico.getItemAt(i).getId() == Sesion.getEmpleado().getId()) {
                        cmbTecnico.setSelectedIndex(i);
                    }
                }
                cmbTecnico.setEnabled(false);
            }
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void buscarCliente() {
        try {
            Cliente c = clienteController.buscarPorTelefono(txtTelefonoCliente.getText());
            if (c == null) {
                clienteSeleccionado = null;
                lblCliente.setText("Cliente no encontrado. Usa \"Registrar cliente nuevo\".");
            } else {
                clienteSeleccionado = c;
                lblCliente.setText(c.getNombre() + " - " + c.getTelefono());
            }
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void registrar() {
        try {
            Object tipo = cmbTipo.getSelectedItem();
            Reparacion r = controlador.registrar(clienteSeleccionado, (Empleado) cmbTecnico.getSelectedItem(),
                    tipo == null ? "" : tipo.toString(), txtMarca.getText(), txtModelo.getText(), txtFalla.getText());
            mostrarRecibo(r);
            limpiarNueva();
            refrescarTabla();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void mostrarRecibo(Reparacion r) {
        StringBuilder sb = new StringBuilder();
        sb.append("INDEA - RECIBO DE EQUIPO\n");
        sb.append("------------------------------------\n");
        sb.append("Folio:    ").append(r.getFolio()).append("\n");
        sb.append("Fecha:    ").append(r.getFechaIngreso().format(Ui.FECHA_HORA)).append("\n");
        sb.append("Cliente:  ").append(r.getCliente().getNombre()).append("\n");
        sb.append("Teléfono: ").append(r.getCliente().getTelefono()).append("\n");
        sb.append("Equipo:   ").append(r.getEquipoDescripcion()).append("\n");
        sb.append("Falla:    ").append(r.getFallaReportada()).append("\n");
        sb.append("Técnico:  ").append(r.getTecnico().getNombre()).append("\n");
        sb.append("Estado:   ").append(r.getEstado().getEtiqueta()).append("\n");
        sb.append("------------------------------------\n");
        sb.append("Conserva este folio para consultar el avance.");
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        javax.swing.JOptionPane.showMessageDialog(this, area, "Reparación registrada", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarNueva() {
        clienteSeleccionado = null;
        txtTelefonoCliente.setText("");
        lblCliente.setText("Ningún cliente seleccionado");
        cmbTipo.setSelectedIndex(0);
        txtMarca.setText("");
        txtModelo.setText("");
        txtFalla.setText("");
    }

    private void refrescarTabla() {
        try {
            List<Reparacion> lista = controlador.listar(txtBuscar.getText());
            modelo.setRowCount(0);
            mostradas.clear();
            for (Reparacion r : lista) {
                mostradas.add(r);
                modelo.addRow(new Object[] {r.getFolio(), r.getCliente().getNombre(), r.getEquipoDescripcion(),
                        r.getEstado().getEtiqueta(), r.getFechaIngreso().format(Ui.FECHA_HORA)});
            }
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= mostradas.size()) {
            return;
        }
        try {
            Reparacion r = controlador.consultarPorFolio(mostradas.get(fila).getFolio());
            folioSeleccionado = r.getFolio();
            lblFolio.setText(r.getFolio() + " - " + r.getCliente().getNombre());
            cmbEstado.setSelectedItem(r.getEstado());
            txtCosto.setText(r.getCostoServicio().toPlainString());
            chkPagado.setSelected(r.isPagado());
            txtDiagnostico.setText(r.getDiagnostico() == null ? "" : r.getDiagnostico());
            StringBuilder sb = new StringBuilder();
            for (CambioEstado c : r.getHistorial()) {
                sb.append(c.getFechaCambio().format(Ui.FECHA_HORA)).append("  ").append(c.getEstado().getEtiqueta())
                        .append("\n   por ").append(c.getEmpleado().getNombre()).append("\n");
            }
            txtHistorial.setText(sb.toString());
            txtHistorial.setCaretPosition(0);
            habilitarEdicion(r.getEstado() != EstadoReparacion.ENTREGADO);
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void guardarCambios() {
        if (folioSeleccionado == null) {
            Ui.aviso(this, "Selecciona una reparación de la tabla.");
            return;
        }
        try {
            controlador.actualizar(folioSeleccionado, (EstadoReparacion) cmbEstado.getSelectedItem(),
                    txtDiagnostico.getText(), txtCosto.getText(), chkPagado.isSelected());
            Ui.info(this, "Reparación " + folioSeleccionado + " actualizada.");
            int fila = tabla.getSelectedRow();
            refrescarTabla();
            if (fila >= 0 && fila < tabla.getRowCount()) {
                tabla.setRowSelectionInterval(fila, fila);
            }
            cargarSeleccion();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void habilitarEdicion(boolean activa) {
        cmbEstado.setEnabled(activa);
        txtCosto.setEnabled(activa);
        chkPagado.setEnabled(activa);
        txtDiagnostico.setEnabled(activa);
        btnGuardarCambios.setEnabled(activa);
    }
}
