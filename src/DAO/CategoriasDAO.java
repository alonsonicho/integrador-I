package DAO;

import Connection.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Categoria;

public class CategoriasDAO extends Conexion {

    PreparedStatement ps;
    ResultSet rs;

    public boolean registrarCategoria(Categoria categoria) {
        String sql = "{call crearCategoria(?,?)}";
        try {
            Connection con = getConnection();
            CallableStatement ps = con.prepareCall(sql);
            ps.setString(1, categoria.getNombreCategoria());
            ps.setString(2, "ACTIVO");
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Categoria> listarCategorias() {
        ArrayList<Categoria> lista = new ArrayList();
        String sql = "SELECT * FROM categorias where estado = 'ACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getString("idCategoria"));
                cat.setNombreCategoria(rs.getString("nombreCategoria"));
                lista.add(cat);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }

    public String obtenerIdCategoria(String nombre) {
        String idCategoria = "";
        try {
            Connection con = getConnection();
            String sql = "SELECT idCategoria FROM categorias WHERE nombreCategoria = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                idCategoria = rs.getString("idCategoria");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return idCategoria;
    }

    public String obtenerNombreCategoria(String id) {
        String nombre = "";
        try {
            Connection con = getConnection();
            String sql = "SELECT nombreCategoria FROM categorias where idCategoria = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombreCategoria");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nombre;
    }

    public boolean actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE categorias SET nombreCategoria = ? WHERE idCategoria = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, categoria.getNombreCategoria());
            ps.setString(2, categoria.getIdCategoria());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminarCategoria(String cod) {
        String nuevoEstado = "ELIMINADO";
        String sql = "UPDATE categorias SET estado = ? WHERE idCategoria = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setString(2, cod);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
