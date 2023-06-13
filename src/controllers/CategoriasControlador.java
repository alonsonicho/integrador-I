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
import util.Utilidades;
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
        this.vistaCategorias.btnRegitrarCategoria.addActionListener(this);
        this.vistaCategorias.btnNuevaCategoria.addActionListener(this);
        this.vistaCategorias.btnModificarCategoria.addActionListener(this);
        this.vistaCategorias.TableCategorias.addMouseListener(this);
        this.vistaCategorias.JLabelCategoria.addMouseListener(this);
        this.vistaCategorias.JMenuEliminarCat.addActionListener(this);
        this.vistaCategorias.JLabelSalirProd.addMouseListener(this);
        listarCategorias();
        listarBox();
        this.vistaCategorias.TableCategorias.setDefaultEditor(Object.class, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Registrar Categoria
        if (e.getSource() == vistaCategorias.btnRegitrarCategoria) {
            String nombreCategoria = vistaCategorias.txtNombreCategoria.getText();
            //Verificar si el campo no se encuentra vacio
            if (!Utilidades.validarCamposVacios(nombreCategoria)) {
                return;
            }

            categoria.setNombreCategoria(nombreCategoria);
            //Llamado al metodo para el registro de la categoria
            if (categoriasDAO.registrarCategoria(categoria)) {
                limpiar();
                Utilidades.limpiarTable(modelo);
                listarCategorias();
                JOptionPane.showMessageDialog(null, "Categoria registrada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        }

        //-------------------------------------------------------------------------------------------------------------------------------
        // Actualizar categoria
        if (e.getSource() == vistaCategorias.btnModificarCategoria) {
            String idCat = vistaCategorias.txtIdCategoria.getText();
            String nombreCat = vistaCategorias.txtNombreCategoria.getText();
            //Verificar que los campos se encuentren completos
            if(!Utilidades.validarCamposVacios(idCat, nombreCat)){
                return;
            }
            
            categoria.setNombreCategoria(nombreCat);
            categoria.setIdCategoria(idCat);
            //Llamado al metodo para actualizar
            if (categoriasDAO.actualizarCategoria(categoria)) {
                limpiar();
                Utilidades.limpiarTable(modelo);
                listarCategorias();
                JOptionPane.showMessageDialog(null, "Categoria actualizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        //-------------------------------------------------------------------------------------------------------------------------------
        // Eliminar Categoria
        if (e.getSource() == vistaCategorias.JMenuEliminarCat) {
            String codigo = vistaCategorias.txtIdCategoria.getText();

            if (codigo.equals("")) {
                JOptionPane.showMessageDialog(null, "Selecione una categoria");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (categoriasDAO.eliminarCategoria(codigo)) {
                    limpiar();
                    Utilidades.limpiarTable(modelo);
                    listarCategorias();
                    listarBox();
                    JOptionPane.showMessageDialog(null, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar producto");
                }
            }
        }

        //-------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaCategorias.btnNuevaCategoria) {
            limpiar();
            vistaCategorias.btnRegitrarCategoria.setEnabled(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == vistaCategorias.TableCategorias) {
            int fila = vistaCategorias.TableCategorias.rowAtPoint(e.getPoint());
            vistaCategorias.txtIdCategoria.setText(vistaCategorias.TableCategorias.getValueAt(fila, 0).toString());
            vistaCategorias.txtNombreCategoria.setText(vistaCategorias.TableCategorias.getValueAt(fila, 1).toString());
            vistaCategorias.btnRegitrarCategoria.setEnabled(false);
        }

        if (e.getSource() == vistaCategorias.JLabelCategoria) {
            vistaCategorias.jTabbedPane1.setSelectedIndex(1);
            Utilidades.limpiarTable(modelo);
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
        modelo = (DefaultTableModel) vistaCategorias.TableCategorias.getModel();
        Object[] obj = new Object[2];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCategoria();
            obj[1] = lista.get(i).getNombreCategoria();
            modelo.addRow(obj);
        }
        vistaCategorias.TableCategorias.setModel(modelo);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vistaCategorias.txtIdCategoria.setText(null);
        vistaCategorias.txtNombreCategoria.setText(null);
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
