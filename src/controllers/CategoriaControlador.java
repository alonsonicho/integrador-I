package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import services.CategoriaUtil;
import util.Utilidades;
import views.frmMenuPrincipal;
import views.frmProductos;

public class CategoriaControlador implements ActionListener, MouseListener {

    frmProductos vistaCategorias;
    CategoriaUtil catUtil;
    
    public CategoriaControlador(frmProductos vistaCategorias) {
        this.vistaCategorias = vistaCategorias;
        this.vistaCategorias.btnRegitrarCategoria.addActionListener(this);
        this.vistaCategorias.btnNuevaCategoria.addActionListener(this);
        this.vistaCategorias.btnModificarCategoria.addActionListener(this);
        this.vistaCategorias.TableCategorias.addMouseListener(this);
        this.vistaCategorias.JLabelCategoria.addMouseListener(this);
        this.vistaCategorias.JMenuEliminarCat.addActionListener(this);
        this.vistaCategorias.JLabelSalirProd.addMouseListener(this);
        Utilidades.bloquearEdicionTabla(vistaCategorias.TableCategorias);
        Utilidades.centrarDatosTabla(vistaCategorias.TableCategorias);     
        catUtil = new CategoriaUtil(vistaCategorias);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == vistaCategorias.btnRegitrarCategoria){
           catUtil.registrarCategoria();
       }
       
       if(e.getSource() == vistaCategorias.btnModificarCategoria){
           catUtil.actualizarCategoria();
       }
       
       if (e.getSource() == vistaCategorias.JMenuEliminarCat){
           catUtil.eliminarCategoria();
       }
       
       if (e.getSource() == vistaCategorias.btnNuevaCategoria) {
            catUtil.limpiar();
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
            catUtil.listarCategorias();
        }

        if (e.getSource() == vistaCategorias.JLabelSalirProd) {
            new frmMenuPrincipal().setVisible(true);
            vistaCategorias.dispose();
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
