package com.indea.vista;

import com.indea.controlador.ReparacionController;
import com.indea.modelo.CambioEstado;
import com.indea.modelo.Reparacion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Window;

public class ConsultaFolioDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final ReparacionController controlador = new ReparacionController();
    private final JTextField txtFolio = new JTextField(18);
    private final JTextArea txtResultado = new JTextArea(12, 40);

    public ConsultaFolioDialog(Window padre) {
        super(padre, "Consultar estado de reparación", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        add(Ui.encabezado("Estado de mi equipo", "Escribe el folio de tu recibo (ejemplo: REP-20261008-0001)"), BorderLayout.NORTH);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConsultar = new JButton("Consultar");
        busqueda.add(new JLabel("Folio:"));
        busqueda.add(txtFolio);
        busqueda.add(btnConsultar);

        txtResultado.setEditable(false);
        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        centro.add(busqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> consultar());
        txtFolio.addActionListener(e -> consultar());
        getRootPane().setDefaultButton(btnConsultar);

        pack();
        setLocationRelativeTo(padre);
    }

    private void consultar() {
        try {
            Reparacion r = controlador.consultarPorFolio(txtFolio.getText());
            StringBuilder sb = new StringBuilder();
            sb.append("Folio:          ").append(r.getFolio()).append("\n");
            sb.append("Equipo:         ").append(r.getEquipoDescripcion()).append("\n");
            sb.append("Falla:          ").append(r.getFallaReportada()).append("\n");
            sb.append("Estado actual:  ").append(r.getEstado().getEtiqueta()).append("\n");
            sb.append("Costo:          ").append(Ui.dinero(r.getCostoServicio())).append("\n");
            sb.append("Ingreso:        ").append(r.getFechaIngreso().format(Ui.FECHA_HORA)).append("\n");
            if (r.getFechaEntrega() != null) {
                sb.append("Entrega:        ").append(r.getFechaEntrega().format(Ui.FECHA_HORA)).append("\n");
            }
            sb.append("\nSeguimiento:\n");
            for (CambioEstado c : r.getHistorial()) {
                sb.append("  ").append(c.getFechaCambio().format(Ui.FECHA_HORA)).append("  ").append(c.getEstado().getEtiqueta()).append("\n");
            }
            txtResultado.setText(sb.toString());
            txtResultado.setCaretPosition(0);
        } catch (RuntimeException ex) {
            txtResultado.setText("");
            Ui.manejar(this, ex);
        }
    }
}
