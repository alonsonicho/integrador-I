
package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import models.Session;
import models.Usuario;
import services.UsuarioUtil;
import util.Utilidades;
import views.frmClientes;
import views.modal.modalAddUsuarioConfig;

public class UsuarioControlador implements ActionListener, MouseListener {

    frmClientes vista;
    UsuarioUtil usuarioUtil;

    public UsuarioControlador(frmClientes vista) {
        this.vista = vista;
        this.vista.btnregistraruser.addActionListener(this);
        this.vista.TableMostrarUsuarios.addMouseListener(this);
        this.vista.JMenuEliminarUser.addActionListener(this);
        this.vista.btnmodificarUser.addActionListener(this);
        this.vista.btnnuevouser.addActionListener(this);
        this.vista.btnBuscarUsuario.addActionListener(this);
        this.vista.TableInactiveUsuarios.addMouseListener(this);
        this.vista.btnActivarUsuario.addActionListener(this);
        this.vista.radioButtonTrueCliente.addActionListener(this);
        this.vista.radioButtonFalseCliente.addActionListener(this);
        this.vista.btnBuscarUsuarioUpdatePassword.addActionListener(this);
        this.vista.btnActualizarPassword.addActionListener(this);
        permisosUsuario();
        this.vista.radioButtonTrueUser.addActionListener(this);
        this.vista.radioButtonFalseUser.addActionListener(this);
        this.vista.radioButtonFalseUser.setSelected(true);
        this.vista.radioButtonTrueUser.setSelected(false);
        Utilidades.centrarDatosTabla(vista.TableMostrarUsuarios);
        Utilidades.centrarDatosTabla(vista.TableInactiveUsuarios);
        Utilidades.bloquearEdicionTabla(vista.TableMostrarUsuarios);
        Utilidades.bloquearEdicionTabla(vista.TableInactiveUsuarios);
        usuarioUtil = new UsuarioUtil(vista);
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vista.btnregistraruser){
            usuarioUtil.registarUsuario();
        }
        
        if (e.getSource() == vista.btnmodificarUser){
            usuarioUtil.actualizarUsuario();
        }
        
        if (e.getSource() == vista.btnBuscarUsuario){
            usuarioUtil.buscarUsuario();
        }
        
        if (e.getSource() == vista.JMenuEliminarUser){
            usuarioUtil.eliminarUsuario();
        }
        
        if (e.getSource() == vista.btnActivarUsuario){
            usuarioUtil.activarUsuario();
        }
        
        if (e.getSource() == vista.btnActualizarPassword){
            usuarioUtil.actualizarPassword();
        }
        
        //Nuevo usuario
        if (e.getSource() == vista.btnnuevouser) {
            limpiar();
            vista.btnregistraruser.setEnabled(true);
            vista.txtcontrasenaUser.setEnabled(true);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Control de evento para el radio button
        if (e.getSource() == vista.radioButtonTrueUser) {
            vista.radioButtonFalseUser.setSelected(false);
        }

        if (e.getSource() == vista.radioButtonFalseUser) {
            vista.radioButtonTrueUser.setSelected(false);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Abrir modal para seleccionar usuario y cambiar contraseña
        if (e.getSource() == vista.btnBuscarUsuarioUpdatePassword) {
            modalAddUsuarioConfig vistaModal = new modalAddUsuarioConfig(this.vista, true, vista);
            vistaModal.setVisible(true);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getSource() == vista.TableMostrarUsuarios) {
            int fila = vista.TableMostrarUsuarios.rowAtPoint(e.getPoint());
            vista.txtidusuario.setText(vista.TableMostrarUsuarios.getValueAt(fila, 0).toString());
            vista.txtDniUser.setText(vista.TableMostrarUsuarios.getValueAt(fila, 1).toString());
            vista.txtnombreUser.setText(vista.TableMostrarUsuarios.getValueAt(fila, 2).toString());
            vista.txtuser.setText(vista.TableMostrarUsuarios.getValueAt(fila, 3).toString());
            vista.cbroluser.setSelectedItem(vista.TableMostrarUsuarios.getValueAt(fila, 4).toString());
            vista.txtcontrasenaUser.setEnabled(false);
            vista.btnregistraruser.setEnabled(false);
        }

        if (e.getSource() == vista.TableInactiveUsuarios) {
            int fila = vista.TableInactiveUsuarios.rowAtPoint(e.getPoint());
            vista.txtActivarUsuarioId.setText(vista.TableInactiveUsuarios.getValueAt(fila, 0).toString());
            vista.txtActiveDniUser.setText(vista.TableInactiveUsuarios.getValueAt(fila, 1).toString());
            vista.txtActiveNombreUser.setText(vista.TableInactiveUsuarios.getValueAt(fila, 2).toString());
            vista.txtActiveUsuarioUser.setText(vista.TableInactiveUsuarios.getValueAt(fila, 3).toString());
            vista.txtActiveRolUser.setText(vista.TableInactiveUsuarios.getValueAt(fila, 4).toString());
            vista.radioButtonFalseUser.setSelected(true);
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
    
    
    public void limpiar() {
        vista.txtDniUser.setText(null);
        vista.txtnombreUser.setText(null);
        vista.txtuser.setText(null);
        vista.txtcontrasenaUser.setText(null);
        vista.txtbuscarUser.setText(null);
    }
    
    public void permisosUsuario() {
        //Obtener usuario actual
        Usuario usuarioActual = Session.getUsuarioActual();
        if (usuarioActual.getRol().equals("Usuario")) {
            vista.jTabbedPane1.setEnabledAt(1, false);
            vista.jTabbedPane2.setEnabledAt(1, false);
            vista.jTabbedPane2.setEnabledAt(2, false);
            vista.JLabelUsuariosReg.setEnabled(false);
        }
    }
    
    
}
