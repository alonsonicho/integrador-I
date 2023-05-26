package controllers;

import DAO.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import views.frmClientes;
import views.frmProductos;

public class UsuariosControlador implements ActionListener, MouseListener, KeyListener {

    Usuario usuario;
    UsuariosDAO usuariosDAO;
    frmClientes vista;
    DefaultTableModel modeloUsuario = new DefaultTableModel();
    DefaultTableModel modeloUsuarioInactivo = new DefaultTableModel();

    public UsuariosControlador(Usuario usuario, UsuariosDAO usuariosDAO, frmClientes vista) {
        this.usuario = usuario;
        this.usuariosDAO = usuariosDAO;
        this.vista = vista;
        this.vista.btnregistraruser.addActionListener(this);
        this.vista.TableMostrarUsuarios.addMouseListener(this);
        this.vista.txtbuscarUser.addKeyListener(this);
        this.vista.JMenuEliminarUser.addActionListener(this);
        this.vista.btnmodificarUser.addActionListener(this);
        this.vista.btnnuevouser.addActionListener(this);
        this.vista.btnBuscarUsuario.addActionListener(this);
        listarUsuarios();
        permisosUsuario();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Registrar usuario
        if (e.getSource() == vista.btnregistraruser) {
            String user = vista.txtuser.getText();
            String nombre = vista.txtnombreUser.getText();
            String dni = vista.txtDniUser.getText();
            String contrasena = String.valueOf(vista.txtcontrasenaUser.getPassword());

            if (user.isEmpty() || nombre.isEmpty() || dni.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                return;
            }

            //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposNumericos(dni)) {
                return;
            }

            usuario.setDniUsuario(Integer.parseInt(dni));
            usuario.setNombre(nombre);
            usuario.setUsuario(user);
            usuario.setPassword(contrasena);
            usuario.setRol(vista.cbroluser.getSelectedItem().toString());

            if (usuariosDAO.registrarUsuario(usuario)) {
                limpiarTable();
                listarUsuarios();
                limpiar();
                JOptionPane.showMessageDialog(null, "Usuario registrado con éxito");
            }

        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Actualizar usuario
        if (e.getSource() == vista.btnmodificarUser) {
            String dni = vista.txtDniUser.getText();
            String nombre = vista.txtnombreUser.getText();
            String user = vista.txtuser.getText();
            String rol = vista.cbroluser.getSelectedItem().toString();
            String idUsuario = vista.txtidusuario.getText();

            if (dni.isEmpty() || nombre.isEmpty() || user.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                return;
            }

            //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposNumericos(dni)) {
                return;
            }

            usuario.setDniUsuario(Integer.parseInt(dni));
            usuario.setNombre(nombre);
            usuario.setUsuario(user);
            usuario.setRol(rol);
            usuario.setIdUsuario(Integer.parseInt(idUsuario));

            if (usuariosDAO.actualizarUsuario(usuario)) {
                limpiarTable();
                limpiar();
                listarUsuarios();
                JOptionPane.showMessageDialog(null, "Usuario modificado correctamente");
            } 
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Buscar usuario
        if (e.getSource() == vista.btnBuscarUsuario) {
            String dniUsuarioStr = vista.txtbuscarUser.getText();

            if (dniUsuarioStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingresar DNI del usuario");
                return;
            }

            //Validar campos con numeros
            if (!validarCamposNumericos(dniUsuarioStr)) {
                return;
            }

            int dniUsuario = Integer.parseInt(dniUsuarioStr);
            
            try {
                usuario = usuariosDAO.buscarUsuario(dniUsuario);
                if (usuario.getNombre() != null) {
                    vista.txtDniUser.setText(String.valueOf(usuario.getDniUsuario()));
                    vista.txtidusuario.setText(String.valueOf(usuario.getIdUsuario()));
                    vista.txtuser.setText(usuario.getUsuario());
                    vista.txtnombreUser.setText(usuario.getNombre());
                    vista.cbroluser.setSelectedItem(usuario.getRol());
                    vista.txtcontrasenaUser.setEnabled(false);
                    vista.btnregistraruser.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No existe DNI de usuario");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
        //------------------------------------------------------------------------------------------------------------------------------------------
        if(e.getSource() == vista.JMenuEliminarUser){
            String idUsuarioStr = vista.txtidusuario.getText();
            
            if(idUsuarioStr.isEmpty()){
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el usuario");
                return;
            }
            
            int idUsuario = Integer.parseInt(idUsuarioStr);
            
            //Denegar elimicion de usuario activo
            Usuario usuarioActual = Session.getUsuarioActual();
            if(idUsuario == usuarioActual.getIdUsuario()){
                JOptionPane.showMessageDialog(null, "No se puede eliminar un usuario con sesion activa");
                return;
            }
            
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);
            
            if(respuesta == JOptionPane.YES_OPTION){
                if(usuariosDAO.eliminarUsuario(idUsuario)){
                    limpiar();
                    limpiarTable();
                    listarUsuarios();
                    JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nuevo usuario
        if (e.getSource() == vista.btnnuevouser) {
            limpiar();
            vista.btnregistraruser.setEnabled(true);
            vista.txtcontrasenaUser.setEnabled(true);
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
    }

    @Override
    public void mousePressed(MouseEvent me) {
        ///////////////////////////////////////////
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        ////////////////////////////////////////////
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        ////////////////////////////////////////////
    }

    @Override
    public void mouseExited(MouseEvent me) {
        ///////////////////////////////////////////
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        ///////////////////////////////////////////
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        ////////////////////////////////////////////
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        /////////////////////////////////////////////
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarUsuarios() {
        ArrayList<Usuario> lista = usuariosDAO.listarUsuarios();
        modeloUsuario = (DefaultTableModel) vista.TableMostrarUsuarios.getModel();
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdUsuario();
            obj[1] = lista.get(i).getDniUsuario();
            obj[2] = lista.get(i).getNombre();
            obj[3] = lista.get(i).getUsuario();
            obj[4] = lista.get(i).getRol();
            modeloUsuario.addRow(obj);
        }
        vista.TableMostrarUsuarios.setModel(modeloUsuario);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vista.txtDniUser.setText(null);
        vista.txtnombreUser.setText(null);
        vista.txtuser.setText(null);
        vista.txtcontrasenaUser.setText(null);
        vista.txtbuscarUser.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarTable() {
        for (int i = 0; i < modeloUsuario.getRowCount(); i++) {
            modeloUsuario.removeRow(i);
            i = i - 1;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void permisosUsuario() {
        //Obtener usuario actual
        Usuario usuarioActual = Session.getUsuarioActual();
        if (usuarioActual.getRol().equals("Usuario")) {
            vista.jTabbedPane1.setEnabledAt(1, false);
            vista.JLabelUsuariosReg.setEnabled(false);          
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public boolean validarCamposNumericos(String... valores) {
        for (String valor : valores) {
            try {
                Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres");
                return false;
            }
        }
        return true;
    }

}
