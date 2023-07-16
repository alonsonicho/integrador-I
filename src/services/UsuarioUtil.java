package services;

import DAO.UsuariosDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Session;
import models.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import util.Utilidades;
import views.frmClientes;

public class UsuarioUtil {

    Usuario usuario = new Usuario();
    UsuariosDAO usuariosDAO = new UsuariosDAO();
    frmClientes vista;
    DefaultTableModel modeloUsuarioActivo = new DefaultTableModel();
    DefaultTableModel modeloUsuarioInactivo = new DefaultTableModel();

    public UsuarioUtil(frmClientes vista) {
        this.vista = vista;
        listarUsuarios();
        listarUsuariosInactivo();
    }

    public void registarUsuario() {
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

        //Verificar el tipo de documento ingresado sea DNI
        //Hashear password antes de enviarla a la BD
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        usuario.setDniUsuario(Integer.parseInt(dni));
        usuario.setNombre(nombre);
        usuario.setUsuario(user);
        usuario.setPassword(hashedPassword);
        usuario.setRol(vista.cbroluser.getSelectedItem().toString());

        if (usuariosDAO.registrarUsuario(usuario)) {
            listarUsuarios();
            limpiar();
            JOptionPane.showMessageDialog(null, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarUsuario() {
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
            listarUsuarios();
            JOptionPane.showMessageDialog(null, "Usuario modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void buscarUsuario() {
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

    public void eliminarUsuario() {
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
                listarUsuarios();
                listarUsuariosInactivo();
                JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
            }
        }
    }

    public void activarUsuario() {
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
                listarUsuarios();
                listarUsuariosInactivo();
                JOptionPane.showMessageDialog(null, "El usuario se activo correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void actualizarPassword() {
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

    public void listarUsuarios() {
        ArrayList<Usuario> lista = usuariosDAO.listarUsuarios();
        modeloUsuarioActivo = (DefaultTableModel) vista.TableMostrarUsuarios.getModel();
        modeloUsuarioActivo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdUsuario();
            obj[1] = lista.get(i).getDniUsuario();
            obj[2] = lista.get(i).getNombre();
            obj[3] = lista.get(i).getUsuario();
            obj[4] = lista.get(i).getRol();
            modeloUsuarioActivo.addRow(obj);
        }
        vista.TableMostrarUsuarios.setModel(modeloUsuarioActivo);
    }

    public void listarUsuariosInactivo() {
        ArrayList<Usuario> lista = usuariosDAO.listarUsuariosInactivo();
        modeloUsuarioInactivo = (DefaultTableModel) vista.TableInactiveUsuarios.getModel();
        modeloUsuarioInactivo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdUsuario();
            obj[1] = lista.get(i).getDniUsuario();
            obj[2] = lista.get(i).getNombre();
            obj[3] = lista.get(i).getUsuario();
            obj[4] = lista.get(i).getRol();
            modeloUsuarioInactivo.addRow(obj);
        }
        vista.TableInactiveUsuarios.setModel(modeloUsuarioInactivo);
    }

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

}
