package operaciones;

import DAO.UsuariosDAO;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.Usuario;

public class UsuariosOp {

    UsuariosDAO usuariosDAO;

    public UsuariosOp(UsuariosDAO usuariosDAO) {
        this.usuariosDAO = usuariosDAO;
    }

    public void listarUsuarios(DefaultTableModel modelo, JTable tabla) {
        ArrayList<Usuario> lista = usuariosDAO.listarUsuarios();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdUsuario();
            obj[1] = lista.get(i).getDniUsuario();
            obj[2] = lista.get(i).getNombre();
            obj[3] = lista.get(i).getUsuario();
            obj[4] = lista.get(i).getRol();
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

    public void listarUsuariosInactivo(DefaultTableModel modelo, JTable tabla) {
        ArrayList<Usuario> lista = usuariosDAO.listarUsuariosInactivo();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdUsuario();
            obj[1] = lista.get(i).getDniUsuario();
            obj[2] = lista.get(i).getNombre();
            obj[3] = lista.get(i).getUsuario();
            obj[4] = lista.get(i).getRol();
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

}
