package com.indea.vista;

import com.indea.controlador.AutenticacionController;
import com.indea.controlador.ClienteController;
import com.indea.controlador.ReparacionController;
import com.indea.modelo.Rol;
import com.indea.util.Sesion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PanelPrincipalFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ClienteController clienteController = new ClienteController();
    private final ReparacionController reparacionController = new ReparacionController();
    private final AutenticacionController autenticacionController = new AutenticacionController();
    private final JLabel lblReparaciones = new JLabel("-", SwingConstants.CENTER);
    private final JLabel lblClientes = new JLabel("-", SwingConstants.CENTER);

    public PanelPrincipalFrame() {
        Rol rol = Sesion.getRol();
        setTitle("INDEA - Panel principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Ui.encabezado("INDEA - Panel principal",
                "Bienvenido, " + Sesion.getEmpleado().getNombre() + " (" + rol.getEtiqueta() + ")"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel resumen = new JPanel(new GridLayout(1, 2, 12, 0));
        boolean veReparaciones = rol == Rol.ADMINISTRADOR || rol == Rol.TECNICO;
        boolean veClientes = rol == Rol.ADMINISTRADOR || rol == Rol.VENDEDOR || rol == Rol.TECNICO;
        if (veReparaciones) {
            resumen.add(tarjeta("Equipos en reparación", lblReparaciones));
        }
        if (veClientes) {
            resumen.add(tarjeta("Clientes registrados", lblClientes));
        }
        if (!veReparaciones && !veClientes) {
            JLabel aviso = new JLabel("Las publicaciones de marketing estarán disponibles en los próximos sprints.", SwingConstants.CENTER);
            resumen.add(aviso);
        }
        centro.add(resumen, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        JButton btnClientes = new JButton("Clientes");
        JButton btnReparaciones = new JButton("Reparaciones");
        JButton btnFolio = new JButton("Consultar reparación por folio");
        JButton btnSalir = new JButton("Cerrar sesión");
        btnClientes.setEnabled(veClientes);
        btnReparaciones.setEnabled(veReparaciones);
        menu.add(btnClientes);
        menu.add(btnReparaciones);
        menu.add(btnFolio);
        menu.add(btnSalir);
        centro.add(menu, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        btnClientes.addActionListener(e -> new ClientesFrame().setVisible(true));
        btnReparaciones.addActionListener(e -> new ReparacionesFrame().setVisible(true));
        btnFolio.addActionListener(e -> new ConsultaFolioDialog(this).setVisible(true));
        btnSalir.addActionListener(e -> cerrarSesion());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                actualizarResumen(veReparaciones, veClientes);
            }
        });

        setPreferredSize(new Dimension(460, 420));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel tarjeta(String titulo, JLabel valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(new Color(180, 190, 210)));
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 28f));
        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 4));
        p.add(t, BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createCompoundBorder(p.getBorder(), BorderFactory.createEmptyBorder(0, 0, 8, 0)));
        return p;
    }

    private void actualizarResumen(boolean reparaciones, boolean clientes) {
        try {
            if (reparaciones) {
                lblReparaciones.setText(String.valueOf(reparacionController.contarActivas()));
            }
            if (clientes) {
                lblClientes.setText(String.valueOf(clienteController.contar()));
            }
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
        }
    }

    private void cerrarSesion() {
        autenticacionController.cerrarSesion();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
