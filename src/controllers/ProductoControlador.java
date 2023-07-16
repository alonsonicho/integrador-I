
package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import services.ProductoUtil;
import util.Utilidades;
import views.frmProductos;

public class ProductoControlador implements ActionListener, MouseListener{
    

    frmProductos vistaProductos;
    ProductoUtil productoUtil;
    
    public ProductoControlador(frmProductos vistaProductos) {
        this.vistaProductos = vistaProductos;
        this.vistaProductos.btnRegistrarPro.addActionListener(this);
        this.vistaProductos.btnModificarPro.addActionListener(this);
        this.vistaProductos.btnNuevoPro.addActionListener(this);
        this.vistaProductos.JMenuEliminarProd.addActionListener(this);
        this.vistaProductos.TableProductos.addMouseListener(this);
        this.vistaProductos.JLabelProductos.addMouseListener(this);
        this.vistaProductos.btnBuscarProducto.addActionListener(this);
        Utilidades.centrarDatosTabla(vistaProductos.TableProductos);
        Utilidades.bloquearEdicionTabla(vistaProductos.TableProductos);
        productoUtil = new ProductoUtil(vistaProductos);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vistaProductos.btnRegistrarPro){
            productoUtil.registrarProducto();
        }
        
        if (e.getSource() == vistaProductos.btnModificarPro){
            productoUtil.actualizarProducto();
        }
        
        if (e.getSource() == vistaProductos.JMenuEliminarProd){
            productoUtil.eliminarProducto();
        }
        
        if (e.getSource() == vistaProductos.btnBuscarProducto){
            productoUtil.buscarProducto();
        }
        
        //Nuevo producto
        if (e.getSource() == vistaProductos.btnNuevoPro) {
            productoUtil.limpiar();
            vistaProductos.btnRegistrarPro.setEnabled(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getSource() == vistaProductos.TableProductos) {
            int fila = vistaProductos.TableProductos.rowAtPoint(e.getPoint());
            vistaProductos.txtidprod.setText(vistaProductos.TableProductos.getValueAt(fila, 0).toString());
            vistaProductos.cbxCatPro.setSelectedItem(vistaProductos.TableProductos.getValueAt(fila, 1).toString());
            vistaProductos.txtNombreProducto.setText(vistaProductos.TableProductos.getValueAt(fila, 2).toString());
            vistaProductos.txtDescripcionPro.setText(vistaProductos.TableProductos.getValueAt(fila, 3).toString());
            vistaProductos.txtCantidadPro.setText(vistaProductos.TableProductos.getValueAt(fila, 4).toString());
            vistaProductos.txtPrecioVentaPro.setText(vistaProductos.TableProductos.getValueAt(fila, 5).toString());
            vistaProductos.btnRegistrarPro.setEnabled(false);
        }

        if (e.getSource() == vistaProductos.JLabelProductos) {
            vistaProductos.jTabbedPane1.setSelectedIndex(0);
        }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    
}
