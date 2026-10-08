package com.indea.vista;

import com.indea.excepcion.AccesoDenegadoException;
import com.indea.excepcion.ReglaNegocioException;
import com.indea.excepcion.ValidacionException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;

final class Ui {

    static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    static final Color AZUL = new Color(31, 56, 100);

    private Ui() {
    }

    static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "INDEA", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    static void manejar(Component padre, RuntimeException e) {
        if (e instanceof ValidacionException || e instanceof ReglaNegocioException || e instanceof AccesoDenegadoException) {
            aviso(padre, e.getMessage());
        } else {
            JOptionPane.showMessageDialog(padre, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new java.awt.BorderLayout());
        p.setBackground(AZUL);
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel t = new JLabel(titulo);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 18f));
        p.add(t, java.awt.BorderLayout.NORTH);
        if (subtitulo != null) {
            JLabel s = new JLabel(subtitulo);
            s.setForeground(new Color(210, 220, 240));
            p.add(s, java.awt.BorderLayout.SOUTH);
        }
        return p;
    }

    static void fila(JPanel panel, int y, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = y;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(4, 4, 4, 8);
        panel.add(new JLabel(etiqueta, SwingConstants.LEFT), g);
        g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = y;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 4, 4);
        panel.add(campo, g);
    }

    static JPanel formulario() {
        return new JPanel(new GridBagLayout());
    }

    static DefaultTableModel modeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    static String dinero(java.math.BigDecimal monto) {
        return String.format("$%,.2f", monto == null ? java.math.BigDecimal.ZERO : monto);
    }
}
