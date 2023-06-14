package operaciones;

import DAO.CategoriasDAO;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.Categoria;

public class CategoriasOp {

    CategoriasDAO categoriasDAO;

    public CategoriasOp(CategoriasDAO categoriasDAO) {
        this.categoriasDAO = categoriasDAO;
    }

    public void listarCategorias(DefaultTableModel modelo, JTable tabla) {
        ArrayList<Categoria> lista = this.categoriasDAO.listarCategorias();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[2];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCategoria();
            obj[1] = lista.get(i).getNombreCategoria();
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

    public void listarCategoriasComboBox(JComboBox<Object> comboBox) {
        comboBox.removeAllItems();
        ArrayList<Categoria> lista = this.categoriasDAO.listarCategorias();
        for (Categoria cat : lista) {
            comboBox.addItem(cat.getNombreCategoria());
        }
    }

}
