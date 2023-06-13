package controllers;

import DAO.CategoriasDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Categoria;
import views.frmMenuPrincipal;
import views.frmProductos;

public class CategoriasControlador implements ActionListener, MouseListener {

    Categoria categoria = new Categoria();
    CategoriasDAO categoriasDAO;
    frmProductos vistaCategorias;
    DefaultTableModel modelo = new DefaultTableModel();

    public CategoriasControlador(Categoria categoria, CategoriasDAO categoriasDAO, frmProductos vistaCategorias) {
        this.categoria = categoria;
        this.categoriasDAO = categoriasDAO;
        this.vistaCategorias = vistaCategorias;
        this.vistaCategorias.btnRegitrarCat.addActionListener(this);
        this.vistaCategorias.btnNuevoCat.addActionListener(this);
        this.vistaCategorias.btnModificarCat.addActionListener(this);
        this.vistaCategorias.TableCat.addMouseListener(this);
        this.vistaCategorias.JLabelCategoria.addMouseListener(this);
        this.vistaCategorias.JMenuEliminarCat.addActionListener(this);
        this.vistaCategorias.JLabelSalirProd.addMouseListener(this);
        listarCategorias();
        listarBox();
        this.vistaCategorias.TableCat.setDefaultEditor(Object.class, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Registrar Categoria
        if (e.getSource() == vistaCategorias.btnRegitrarCat) {
            String nombreCategoria = vistaCategorias.txtNombreCat.getText();
            //Verificar si el campo no se encuentra vacio
            if (nombreCategoria.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor complete todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                vistaCategorias.txtNombreCat.requestFocus();
            } else {
                categoria.setNombreCategoria(nombreCategoria);
                //Llamado al metodo para el registro de la categoria
                if (categoriasDAO.registrarCategoria(categoria)) {
                    limpiar();
                    limpiarTable();
                    listarCategorias();
                    JOptionPane.showMessageDialog(null, "Categoria registrada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        //-------------------------------------------------------------------------------------------------------------------------------
        // Actualizar categoria
        if (e.getSource() == vistaCategorias.btnModificarCat) {
            String idCat = vistaCategorias.txtIdCat.getText();
            String nombreCat = vistaCategorias.txtNombreCat.getText();
            //Verificar si no se ha seleccionado una fila de la tabla
            if (idCat.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una categoria");
                return;
            }

            if (nombreCat.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor complete todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            categoria.setNombreCategoria(nombreCat);
            categoria.setIdCategoria(idCat);
            //Llamado al metodo para actualizar
            if (categoriasDAO.actualizarCategoria(categoria)) {
                limpiar();
                limpiarTable();
                listarCategorias();
                JOptionPane.showMessageDialog(null, "Categoria actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        //-------------------------------------------------------------------------------------------------------------------------------
        // Eliminar Categoria
        if (e.getSource() == vistaCategorias.JMenuEliminarCat) {
            String codigo = vistaCategorias.txtIdCat.getText();

            if (codigo.equals("")) {
                JOptionPane.showMessageDialog(null, "Selecione una categoria");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (categoriasDAO.eliminarCategoria(codigo)) {
                    limpiar();
                    limpiarTable();
                    listarCategorias();
                    listarBox();
                    JOptionPane.showMessageDialog(null, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar producto");
                }
            }
        }

        //-------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaCategorias.btnNuevoCat) {
            limpiar();
            vistaCategorias.btnRegitrarCat.setEnabled(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == vistaCategorias.TableCat) {
            int fila = vistaCategorias.TableCat.rowAtPoint(e.getPoint());
            vistaCategorias.txtIdCat.setText(vistaCategorias.TableCat.getValueAt(fila, 0).toString());
            vistaCategorias.txtNombreCat.setText(vistaCategorias.TableCat.getValueAt(fila, 1).toString());
            vistaCategorias.btnRegitrarCat.setEnabled(false);
        }

        if (e.getSource() == vistaCategorias.JLabelCategoria) {
            vistaCategorias.jTabbedPane1.setSelectedIndex(1);
            limpiarTable();
            listarCategorias();
        }

        if (e.getSource() == vistaCategorias.JLabelSalirProd) {
            new frmMenuPrincipal().setVisible(true);
            vistaCategorias.dispose();
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        ////////////////////////////////////////////////////////
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        ////////////////////////////////////////////////////////
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        ////////////////////////////////////////////////////////
    }

    @Override
    public void mouseExited(MouseEvent me) {
        ////////////////////////////////////////////////////////
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarCategorias() {
        ArrayList<Categoria> lista = categoriasDAO.listarCategorias();
        modelo = (DefaultTableModel) vistaCategorias.TableCat.getModel();
        Object[] obj = new Object[2];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCategoria();
            obj[1] = lista.get(i).getNombreCategoria();
            modelo.addRow(obj);
        }
        vistaCategorias.TableCat.setModel(modelo);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarTable() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vistaCategorias.txtIdCat.setText(null);
        vistaCategorias.txtNombreCat.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    //Listar las categorias en el comboBox
    public void listarBox() {
        vistaCategorias.cbxCatPro.removeAllItems();
        ArrayList<Categoria> lista = categoriasDAO.listarCategorias();
        for (Categoria cat : lista) {
            vistaCategorias.cbxCatPro.addItem(cat.getNombreCategoria());
        }
    }

}
