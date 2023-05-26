package controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import models.Session;
import models.Usuario;
import views.frmClientes;
import views.frmLogin;
import views.frmMenuPrincipal;
import views.frmProductos;
import views.frmVentas;

public class MenuPrincipalControlador implements MouseListener, ActionListener {

    frmMenuPrincipal vista;

    public MenuPrincipalControlador(frmMenuPrincipal vista) {
        this.vista = vista;
        this.vista.JLabelClientes.addMouseListener(this);
        this.vista.JLabelProductos.addMouseListener(this);
        this.vista.JLabelVentas.addMouseListener(this);
        this.vista.JLabelSalir.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == vista.JLabelClientes) {
            new frmClientes().setVisible(true);
            vista.dispose();
        }

        if (e.getSource() == vista.JLabelProductos) {
            Usuario usuarioActual = Session.getUsuarioActual();
            if (usuarioActual.getRol().equals("Usuario")) {
                vista.JPanelProductos.setEnabled(false);
                JOptionPane.showMessageDialog(vista, "No tienes permiso para acceder a esta función.", "Alerta", JOptionPane.CANCEL_OPTION);
            } else {
                new frmProductos().setVisible(true);
                vista.dispose();
            }
        }

        if (e.getSource() == vista.JLabelVentas) {
            new frmVentas().setVisible(true);
            vista.dispose();
        }

        if (e.getSource() == vista.JLabelSalir) {
            new frmLogin().setVisible(true);
            vista.dispose();
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == vista.JLabelClientes) {
            vista.JPanelClientes.setBackground(new Color(255, 51, 51));
        } else if (e.getSource() == vista.JLabelProductos) {
            vista.JPanelProductos.setBackground(new Color(255, 51, 51));
        } else if (e.getSource() == vista.JLabelVentas) {
            vista.JPanelVentas.setBackground(new Color(255, 51, 51));
        } else if (e.getSource() == vista.JLabelSalir) {
            vista.JPanelSalir.setBackground(new Color(255, 51, 51));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == vista.JLabelClientes) {
            vista.JPanelClientes.setBackground(new Color(51, 51, 51));
        } else if (e.getSource() == vista.JLabelProductos) {
            vista.JPanelProductos.setBackground(new Color(51, 51, 51));
        } else if (e.getSource() == vista.JLabelVentas) {
            vista.JPanelVentas.setBackground(new Color(51, 51, 51));
        } else if (e.getSource() == vista.JLabelSalir) {
            vista.JPanelSalir.setBackground(new Color(51, 51, 51));
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
