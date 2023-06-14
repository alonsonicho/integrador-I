package DAO;

import Connection.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Categoria;
import models.Producto;

public class ProductosDAO extends Conexion {

    PreparedStatement ps;
    ResultSet rs;

    public boolean registrarProducto(Producto producto) {
        String sql = "{call adicionarProducto(?,?,?,?,?,?)}";
        try {
            Connection con = getConnection();
            CallableStatement ps = con.prepareCall(sql);
            ps.setString(1, producto.getCategoria().getIdCategoria());
            ps.setString(2, producto.getNombreProducto());
            ps.setString(3, producto.getDescripcion());
            ps.setInt(4, producto.getCantidad());
            ps.setDouble(5, producto.getPrecio());
            ps.setString(6, "ACTIVO");
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE producto SET idCategoria=?, nombreProducto=?, descripcion=?, cantidad=?, precio=? WHERE idProducto=?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, producto.getCategoria().getIdCategoria());
            ps.setString(2, producto.getNombreProducto());
            ps.setString(3, producto.getDescripcion());
            ps.setInt(4, producto.getCantidad());
            ps.setDouble(5, producto.getPrecio());
            ps.setString(6, producto.getIdProducto());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Producto> listarProductos() {
        ArrayList<Producto> listaProducto = new ArrayList();
        String sql = "SELECT idProducto ,nombreCategoria, nombreProducto, descripcion, cantidad, precio "
                + "FROM producto p LEFT JOIN categorias c ON p.idCategoria = c.idCategoria WHERE p.estado = 'ACTIVO'; ";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto pro = new Producto();
                pro.setIdProducto(rs.getString("idProducto"));
                pro.setNombreProducto(rs.getString("nombreProducto"));
                pro.setDescripcion(rs.getString("descripcion"));
                pro.setCantidad(rs.getInt("cantidad"));
                pro.setPrecio(rs.getDouble("precio"));

                Categoria cat = new Categoria();
                cat.setNombreCategoria(rs.getString("nombreCategoria"));
                pro.setCategoria(cat);

                listaProducto.add(pro);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaProducto;
    }

    public Producto buscarProducto(String cod) throws Exception {
        Producto pro = new Producto();
        try {
            Connection con = getConnection();
            String sql = "SELECT * FROM producto where idProducto=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, cod);
            rs = ps.executeQuery();
            if (rs.next()) {

                //Lanza excepcion si el producto encontrado registrado como estado INACTIVO
                String estado = rs.getString("estado");
                if (estado.equals("ELIMINADO")) {
                    throw new Exception("El producto no se encuentra disponible");
                }

                pro.setIdProducto(rs.getString("idProducto"));
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getString("idCategoria"));
                pro.setCategoria(cat);
                pro.setNombreProducto(rs.getString("nombreProducto"));
                pro.setDescripcion(rs.getString("descripcion"));
                pro.setCantidad(rs.getInt("cantidad"));
                pro.setPrecio(rs.getDouble("precio"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pro;
    }

    public ArrayList<Producto> listarProductosFiltrado(String textoBusqueda) {
        String sql = "SELECT * FROM producto WHERE (idProducto LIKE ? OR nombreProducto LIKE ?) AND estado = 'ACTIVO' AND cantidad > 0";
        ArrayList<Producto> listaProductos = new ArrayList<>();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + textoBusqueda + "%");
            ps.setString(2, "%" + textoBusqueda + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getString("idProducto"));
                producto.setNombreProducto(rs.getString("nombreProducto"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setCantidad(rs.getInt("cantidad"));
                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaProductos;
    }

    public boolean eliminarProducto(String cod) {
        String nuevoEstado = "ELIMINADO";
        String sql = "UPDATE producto SET estado = ? WHERE idProducto = ?";
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
