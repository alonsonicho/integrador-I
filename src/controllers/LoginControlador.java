package controllers;

import DAO.UsuariosDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import models.Session;
import models.Usuario;
import views.frmLogin;
import views.frmMenuPrincipal;

public class LoginControlador implements ActionListener {

    Usuario usuario;
    UsuariosDAO usuarioDAO;
    frmLogin frmLogin;

    public LoginControlador(Usuario usuario, UsuariosDAO usuarioDAO, frmLogin frmLogin) {
        this.usuario = usuario;
        this.usuarioDAO = usuarioDAO;
        this.frmLogin = frmLogin;
        this.frmLogin.btnlogin.addActionListener(this);
        this.frmLogin.btncancelar.addActionListener(this);
        this.frmLogin.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == frmLogin.btnlogin) {
            String user = frmLogin.txtusuario.getText();
            String password = String.valueOf(frmLogin.txtclave.getPassword());

            if (user.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Los campos están vacíos");
                return;
            }

            usuario = usuarioDAO.loginUsuario(user, password);

            if (usuario.getUsuario() != null) {
                if (usuario.getEstado().equals("INACTIVO")) {
                    JOptionPane.showMessageDialog(null, "El usuario está inactivo. Actívelo antes de iniciar sesión.");
                } else {
                    Session.setUsuarioActual(usuario);
                    frmMenuPrincipal vistaMenu = new frmMenuPrincipal();
                    vistaMenu.setVisible(true);
                    frmLogin.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta");
            }
        } else {
            int pregunta = JOptionPane.showConfirmDialog(null, "¿Esta seguro que desea salir?", "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (pregunta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

    }

}
