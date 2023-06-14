package controllers;

import DAO.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import operaciones.UsuariosOp;
import views.frmClientes;
import org.mindrot.jbcrypt.BCrypt;
import util.Utilidades;
import views.modal.modalAddUsuarioConfig;

public class UsuariosControlador implements ActionListener, MouseListener, KeyListener {

    Usuario usuario;
    UsuariosDAO usuariosDAO = new UsuariosDAO();
    frmClientes vista;
    DefaultTableModel modeloUsuarioActivo = new DefaultTableModel();
    DefaultTableModel modeloUsuarioInactivo = new DefaultTableModel();
    UsuariosOp usuariosOp = new UsuariosOp(usuariosDAO);

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
        this.vista.TableInactiveUsuarios.addMouseListener(this);
        this.vista.btnActivarUsuario.addActionListener(this);
        this.vista.radioButtonTrueCliente.addActionListener(this);
        this.vista.radioButtonFalseCliente.addActionListener(this);
        this.vista.btnBuscarUsuarioUpdatePassword.addActionListener(this);
        this.vista.btnActualizarPassword.addActionListener(this);
        usuariosOp.listarUsuarios(modeloUsuarioActivo, vista.TableMostrarUsuarios);
        usuariosOp.listarUsuariosInactivo(modeloUsuarioInactivo, vista.TableInactiveUsuarios);
        permisosUsuario();
        this.vista.radioButtonFalseUser.setSelected(true);
        this.vista.radioButtonTrueUser.setSelected(false);
        Utilidades.centrarDatosTabla(vista.TableMostrarUsuarios);
        Utilidades.centrarDatosTabla(vista.TableInactiveUsuarios);
        Utilidades.bloquearEdicionTabla(vista.TableMostrarUsuarios);
        Utilidades.bloquearEdicionTabla(vista.TableInactiveUsuarios);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Registrar usuario
        if (e.getSource() == vista.btnregistraruser) {
            String user = vista.txtuser.getText();
            String nombre = vista.txtnombreUser.getText();
            String dni = vista.txtDniUser.getText();
            String password = String.valueOf(vista.txtcontrasenaUser.getPassword());

            if (!Utilidades.validarCamposVacios(user, nombre, dni, password)) {
                return;
            }

            //Validar campos con numeros, se ejecuta si devuelve false
            boolean camposSonEnteros = Utilidades.validarCamposNumericos(dni);
            if (!camposSonEnteros) {
                return;
            }

            //Hashear password antes de enviarla a la BD
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            usuario.setDniUsuario(Integer.parseInt(dni));
            usuario.setNombre(nombre);
            usuario.setUsuario(user);
            usuario.setPassword(hashedPassword);
            usuario.setRol(vista.cbroluser.getSelectedItem().toString());

            if (usuariosDAO.registrarUsuario(usuario)) {
                usuariosOp.listarUsuarios(modeloUsuarioActivo, vista.TableMostrarUsuarios);
                limpiar();
                JOptionPane.showMessageDialog(null, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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

            if (!Utilidades.validarCamposVacios(dni, nombre, user)) {
                return;
            }

            //Validar campos con numeros, se ejecuta si devuelve false
            boolean camposSonEnteros = Utilidades.validarCamposNumericos(dni);
            if (!camposSonEnteros) {
                return;
            }

            usuario.setDniUsuario(Integer.parseInt(dni));
            usuario.setNombre(nombre);
            usuario.setUsuario(user);
            usuario.setRol(rol);
            usuario.setIdUsuario(Integer.parseInt(idUsuario));

            if (usuariosDAO.actualizarUsuario(usuario)) {
                limpiar();
                usuariosOp.listarUsuarios(modeloUsuarioActivo, vista.TableMostrarUsuarios);
                JOptionPane.showMessageDialog(null, "Usuario modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Buscar usuario
        if (e.getSource() == vista.btnBuscarUsuario) {
            String dniUsuarioStr = vista.txtbuscarUser.getText();

            if (!Utilidades.validarCamposVacios(dniUsuarioStr)) {
                return;
            }

            //Validar campos con numeros
            boolean camposSonEnteros = Utilidades.validarCamposNumericos(dniUsuarioStr);
            if (!camposSonEnteros) {
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
        //Eliminar usuario
        if (e.getSource() == vista.JMenuEliminarUser) {
            String idUsuarioStr = vista.txtidusuario.getText();

            if (idUsuarioStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el usuario");
                return;
            }

            int idUsuario = Integer.parseInt(idUsuarioStr);

            //Denegar elimicion de usuario activo
            Usuario usuarioActual = Session.getUsuarioActual();
            if (idUsuario == usuarioActual.getIdUsuario()) {
                JOptionPane.showMessageDialog(null, "No se puede eliminar un usuario con sesion activa");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (usuariosDAO.eliminarUsuario(idUsuario)) {
                    limpiar();
                    usuariosOp.listarUsuarios(modeloUsuarioActivo, vista.TableMostrarUsuarios);
                    usuariosOp.listarUsuariosInactivo(modeloUsuarioInactivo, vista.TableInactiveUsuarios);
                    JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------}
        //Activar usuario, cambio de estado a "ACTIVO"
        if (e.getSource() == vista.btnActivarUsuario) {
            if (!vista.radioButtonTrueUser.isSelected()) {
                JOptionPane.showMessageDialog(null, "Seleccione la opcion de 'ACTIVO' para continuar");
                return;
            }

            if (vista.radioButtonFalseUser.isSelected()) {
                JOptionPane.showMessageDialog(null, "La opcion de INACTIVO no debe estar seleccionada");
                return;
            }

            int idUsuario = Integer.parseInt(vista.txtActivarUsuarioId.getText());
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas activar el usuario?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (usuariosDAO.activarUsuario(idUsuario)) {
                    limpiarUsuarioInactivo();
                    usuariosOp.listarUsuarios(modeloUsuarioActivo, vista.TableMostrarUsuarios);
                    usuariosOp.listarUsuariosInactivo(modeloUsuarioInactivo, vista.TableInactiveUsuarios);
                    JOptionPane.showMessageDialog(null, "El usuario se activo correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Actualizacion de contraseña
        if (e.getSource() == vista.btnActualizarPassword) {
            String usuario = vista.txtUsuarioCambioPass.getText();
            String passwordNuevo = String.valueOf(vista.txtNuevoPassword.getPassword());
            String passwordRepetido = String.valueOf(vista.txtRepetirPassword.getPassword());

            if (!Utilidades.validarCamposVacios(usuario, passwordNuevo, passwordRepetido)) {
                return;
            }

            //Comparar las contraseñas ingresadas
            if (passwordNuevo.equals(passwordRepetido)) {
                //Hashear password antes de enviarla a la BD
                String hashedPassword = BCrypt.hashpw(passwordNuevo, BCrypt.gensalt());
                boolean actualizarPassword = usuariosDAO.actualizarPassword(usuario, hashedPassword);
                if (actualizarPassword) {
                    limpiarDatosActualizarPassword();
                    JOptionPane.showMessageDialog(null, "La contraseña se modifico correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.out.println("Error al modificar");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Las contraseñas ingresadas no coinciden");
            }
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
    public void limpiar() {
        vista.txtDniUser.setText(null);
        vista.txtnombreUser.setText(null);
        vista.txtuser.setText(null);
        vista.txtcontrasenaUser.setText(null);
        vista.txtbuscarUser.setText(null);
    }

    public void limpiarUsuarioInactivo() {
        vista.txtActivarUsuarioId.setText(null);
        vista.txtActiveDniUser.setText(null);
        vista.txtActiveNombreUser.setText(null);
        vista.txtActiveRolUser.setText(null);
        vista.txtActiveUsuarioUser.setText(null);
    }

    public void limpiarDatosActualizarPassword() {
        vista.txtUsuarioCambioPass.setText(null);
        vista.txtNombreCambioPass.setText(null);
        vista.txtNuevoPassword.setText(null);
        vista.txtRepetirPassword.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
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

    //------------------------------------------------------------------------------------------------------------------------------------------
}
