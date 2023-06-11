package DAO;

import Connection.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import models.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuariosDAO extends Conexion {

    PreparedStatement ps;
    ResultSet rs;

    public Usuario loginUsuario(String usuario, String clave) {
        String sql = "SELECT * FROM usuario WHERE usuario = ?";
        Usuario us = new Usuario();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("password");
                // Comprueba si la contraseña es correcta
                if (BCrypt.checkpw(clave, passwordHash)) {
                    us.setIdUsuario(rs.getInt("idUsuario"));
                    us.setDniUsuario(rs.getInt("dni_usuario"));
                    us.setNombre(rs.getString("nombre"));
                    us.setUsuario(rs.getString("usuario"));
                    us.setPassword(passwordHash);
                    us.setRol(rs.getString("rol"));
                    us.setEstado(rs.getString("estado"));
                } 
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return us;
    }

    public boolean registrarUsuario(Usuario user) {
        String sql = "INSERT INTO usuario(dni_usuario, nombre, usuario, password, rol, estado) VALUES (?,?,?,?,?,?)";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, user.getDniUsuario());
            ps.setString(2, user.getNombre());
            ps.setString(3, user.getUsuario());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRol());
            ps.setString(6, "ACTIVO");
            ps.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("uk_usuario")) {
                JOptionPane.showMessageDialog(null, "El usuario '" + user.getUsuario() + "' ya existe. Por favor, elige otro nombre de usuario.");
            } else if (errorMessage.contains("uk_dni_usuario")) {
                JOptionPane.showMessageDialog(null, "El DNI '" + user.getDniUsuario() + "' ya está registrado para otro usuario.");
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean actualizarUsuario(Usuario user) {
        String sql = "UPDATE usuario SET dni_usuario=?, nombre=?, usuario=?, rol=? WHERE idUsuario=?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, user.getDniUsuario());
            ps.setString(2, user.getNombre());
            ps.setString(3, user.getUsuario());
            ps.setString(4, user.getRol());
            ps.setInt(5, user.getIdUsuario());
            ps.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMessage = e.getMessage();
            System.out.println(e.getMessage());
            if (errorMessage.contains("uk_usuario")) {
                JOptionPane.showMessageDialog(null, "El usuario '" + user.getUsuario() + "' ya existe. Por favor, elige otro nombre de usuario.");
            } else if (errorMessage.contains("uk_dni_usuario")) {
                JOptionPane.showMessageDialog(null, "El DNI '" + user.getDniUsuario() + "' ya está registrado para otro usuario.");
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Usuario> listarUsuarios() {
        ArrayList<Usuario> listaUsuario = new ArrayList();
        String sql = "SELECT * FROM usuario where estado = 'ACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario user = new Usuario();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setDniUsuario(rs.getInt("dni_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setUsuario(rs.getString("usuario"));
                user.setPassword(rs.getString("password"));
                user.setRol(rs.getString("rol"));
                listaUsuario.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaUsuario;
    }

    public ArrayList<Usuario> listarUsuariosInactivo() {
        ArrayList<Usuario> listaUsuario = new ArrayList();
        String sql = "SELECT * FROM usuario where estado = 'INACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario user = new Usuario();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setDniUsuario(rs.getInt("dni_usuario"));
                user.setNombre(rs.getString("nombre"));
                user.setUsuario(rs.getString("usuario"));
                user.setPassword(rs.getString("password"));
                user.setRol(rs.getString("rol"));
                listaUsuario.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaUsuario;
    }

    public Usuario buscarUsuario(int dni) throws Exception {
        Usuario us = new Usuario();
        try {
            Connection con = getConnection();
            String sql = "SELECT * FROM usuario where dni_usuario=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            rs = ps.executeQuery();
            if (rs.next()) {

                // Lanzar exception si el usuario a buscar se encuentra inactivo
                String estado = rs.getString("estado");
                if (estado.equals("INACTIVO")) {
                    throw new Exception("El usuario está inactivo. Actívelo antes de continuar.");
                }

                us.setIdUsuario(rs.getInt("idUsuario"));
                us.setDniUsuario(rs.getInt("dni_usuario"));
                us.setNombre(rs.getString("nombre"));
                us.setUsuario(rs.getString("usuario"));
                us.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return us;
    }
    
    public ArrayList<Usuario> listarUsuariosFiltrados(String textoBusqueda){
        String sql = "SELECT * FROM usuario WHERE (nombre LIKE ? OR dni_usuario LIKE ?) AND estado = 'ACTIVO'";
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + textoBusqueda + "%");
            ps.setString(2, "%" + textoBusqueda + "%");
            rs = ps.executeQuery();
            while(rs.next()){
                Usuario usuario = new Usuario();
                usuario.setDniUsuario(rs.getInt("dni_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setUsuario(rs.getString("usuario"));

                listaUsuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaUsuarios;
    }
    

    public boolean eliminarUsuario(int id) {
        String nuevoEstado = "INACTIVO";
        String sql = "UPDATE usuario SET estado = ? WHERE idUsuario = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean activarUsuario (int id) {
        String nuevoEstado = "ACTIVO";
        String sql = "UPDATE usuario SET estado = ? WHERE idUsuario = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean actualizarPassword (String usuario, String password){
        String sql = "UPDATE usuario SET password = ? WHERE usuario = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, password);
            ps.setString(2, usuario);       
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
