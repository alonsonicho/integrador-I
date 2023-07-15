package util;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Utilidades {

    public static void limpiarTable(DefaultTableModel modelo) {
        modelo.setRowCount(0);
    }

    public static void centrarDatosTabla(JTable table) {
        // Centrar el contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Centrar el contenido del encabezado
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
    }
    
    public static void bloquearEdicionTabla(JTable table){
        table.setDefaultEditor(Object.class, null);
    }

    public static boolean validarCamposNumericos(String... valores) {
        for (String valor : valores) {
            if (valor.isEmpty()) {
                continue; // Saltar a la siguiente iteración si el valor está vacío
            }

            try {
                Long.parseLong(valor.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El valor ingresado '" + valor + "' no puede contener letras o caracteres", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public static boolean validarCamposVacios(String... campos) {
        for (String campo : campos) {
            if (campo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor complete todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    // Alerta de mensajes
    public static void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

}
