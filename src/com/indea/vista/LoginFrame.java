package com.indea.vista;

import com.indea.controlador.AutenticacionController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AutenticacionController controlador = new AutenticacionController();
    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtClave = new JPasswordField(18);

    public LoginFrame() {
        setTitle("INDEA - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Ui.encabezado("INDEA", "Sistema de gestión - Inicio de sesión"), BorderLayout.NORTH);

        JPanel form = Ui.formulario();
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        Ui.fila(form, 0, "Usuario:", txtUsuario);
        Ui.fila(form, 1, "Contraseña:", txtClave);
        add(form, BorderLayout.CENTER);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnConsulta = new JButton("Consultar estado de mi equipo");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.setBorder(BorderFactory.createEmptyBorder(0, 8, 12, 8));
        botones.add(btnIngresar);
        botones.add(btnConsulta);
        add(botones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresar());
        btnConsulta.addActionListener(e -> new ConsultaFolioDialog(this).setVisible(true));
        getRootPane().setDefaultButton(btnIngresar);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        char[] clave = txtClave.getPassword();
        try {
            controlador.iniciarSesion(txtUsuario.getText(), clave);
            new PanelPrincipalFrame().setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            Ui.manejar(this, ex);
            txtClave.setText("");
            txtClave.requestFocusInWindow();
        } finally {
            Arrays.fill(clave, ' ');
        }
    }
}
