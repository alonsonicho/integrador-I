package services;

import DAO.CategoriasDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Categoria;
import util.Utilidades;
import views.frmProductos;

public class CategoriaUtil {

    Categoria categoria = new Categoria();
    CategoriasDAO categoriasDAO = new CategoriasDAO();
    frmProductos vistaCategorias;
    DefaultTableModel modeloCategoria = new DefaultTableModel();

    public CategoriaUtil(frmProductos vistaCategorias) {
        this.vistaCategorias = vistaCategorias;
        listarCategorias();
        listarCategoriasComboBox();
    }

    public void registrarCategoria() {
        String nombreCategoria = vistaCategorias.txtNombreCategoria.getText();
        //Verificar si el campo no se encuentra vacio
        if (!Utilidades.validarCamposVacios(nombreCategoria)) {
            return;
        }

        categoria.setNombreCategoria(nombreCategoria);
        //Llamado al metodo para el registro de la categoria
        if (categoriasDAO.registrarCategoria(categoria)) {
            limpiar();
            listarCategorias();
            listarCategoriasComboBox();
            JOptionPane.showMessageDialog(null, "Categoria registrada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCategoria() {
        String idCat = vistaCategorias.txtIdCategoria.getText();
        String nombreCat = vistaCategorias.txtNombreCategoria.getText();
        //Verificar que los campos se encuentren completos
        if (!Utilidades.validarCamposVacios(idCat, nombreCat)) {
            return;
        }

        categoria.setNombreCategoria(nombreCat);
        categoria.setIdCategoria(idCat);
        //Llamado al metodo para actualizar
        if (categoriasDAO.actualizarCategoria(categoria)) {
            limpiar();
            listarCategorias();
            listarCategoriasComboBox();
            JOptionPane.showMessageDialog(null, "Categoria actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void eliminarCategoria() {
        String codigo = vistaCategorias.txtIdCategoria.getText();

        if (codigo.equals("")) {
            JOptionPane.showMessageDialog(null, "Selecione una categoria");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (categoriasDAO.eliminarCategoria(codigo)) {
                limpiar();
                listarCategorias();
                listarCategoriasComboBox();
                JOptionPane.showMessageDialog(null, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar producto");
            }
        }
    }

    public void listarCategorias() {
        ArrayList<Categoria> lista = this.categoriasDAO.listarCategorias();
        modeloCategoria = (DefaultTableModel) vistaCategorias.TableCategorias.getModel();
        modeloCategoria.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[2];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCategoria();
            obj[1] = lista.get(i).getNombreCategoria();
            modeloCategoria.addRow(obj);
        }
        vistaCategorias.TableCategorias.setModel(modeloCategoria);
    }

    public void listarCategoriasComboBox() {
        vistaCategorias.cbxCatPro.removeAllItems();
        ArrayList<Categoria> lista = this.categoriasDAO.listarCategorias();
        for (Categoria cat : lista) {
            vistaCategorias.cbxCatPro.addItem(cat.getNombreCategoria());
        }
    }

    private void limpiar() {
        vistaCategorias.txtIdCategoria.setText(null);
        vistaCategorias.txtNombreCategoria.setText(null);
    }

}
