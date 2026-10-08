package com.indea;

import com.indea.excepcion.PersistenciaException;
import com.indea.util.DatosIniciales;
import com.indea.vista.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Se usará el aspecto visual por defecto.");
        }
        SwingUtilities.invokeLater(() -> {
            try {
                DatosIniciales.crearSiVacio();
                new LoginFrame().setVisible(true);
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
