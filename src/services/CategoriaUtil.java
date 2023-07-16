package services;

import DAO.CategoriasDAO;
import java.util.ArrayList;
import java.util.stream.Stream;
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
        // Validar campos vacios
        if (!Utilidades.validarCamposVacios(nombreCategoria)) return;

        categoria.setNombreCategoria(nombreCategoria);
        // Metodo para registro de Categoria
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
        // Validar campos vacios
        if (!Utilidades.validarCamposVacios(idCat, nombreCat)) return;

        categoria.setNombreCategoria(nombreCat);
        categoria.setIdCategoria(idCat);
        // Metodo para actualizar categoria
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
        lista.forEach(cat -> {
            Object[] obj = {
                cat.getIdCategoria(),
                cat.getNombreCategoria()
            };
            modeloCategoria.addRow(obj);
        });
        vistaCategorias.TableCategorias.setModel(modeloCategoria);
    }

    public void listarCategoriasComboBox() {
        vistaCategorias.cbxCatPro.removeAllItems();
        ArrayList<Categoria> lista = this.categoriasDAO.listarCategorias();
        lista.forEach(cat -> vistaCategorias.cbxCatPro.addItem(cat.getNombreCategoria()));
    }

    private void limpiar() {
        Stream.of(
                vistaCategorias.txtIdCategoria,
                vistaCategorias.txtNombreCategoria
        ).forEach(textField -> textField.setText(null));
    }
}
