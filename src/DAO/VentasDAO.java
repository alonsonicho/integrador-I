package DAO;

import Connection.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import models.*;

public class VentasDAO extends Conexion {

    PreparedStatement ps;
    ResultSet rs;

    public Factura registrarVenta(Factura factura) {
        String sql = "{call crearFactura(?,?,?,?,?,?,?)}";
        try {
            Connection con = getConnection();
            CallableStatement ps = con.prepareCall(sql);
            ps.setString(1, factura.getTipoPago());
            ps.setString(2, factura.getTipoDocumentoVenta());
            ps.setInt(3, factura.getCliente().getIdCliente());
            ps.setInt(4, factura.getUsuario().getIdUsuario());
            ps.setDate(5, factura.getFecha());
            ps.setDouble(6, factura.getTotal());
            ps.setString(7, "ACTIVO");
            ps.execute();
            return factura;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean registrarDetalleFactura(DetalleFactura detalle) {
        String sql = "INSERT INTO detallefactura (idProducto, idFactura, subtotal, cantidad) VALUES (?,?,?,?)";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, detalle.getProducto().getIdProducto());
            ps.setString(2, detalle.getFactura().getCodigo());
            ps.setDouble(3, detalle.getTotal());
            ps.setInt(4, detalle.getCantidad());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public String obtenerUltimoIdFactura() {
        String sql = "SELECT MAX(idFactura) FROM factura";
        String idFactura = null;
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                idFactura = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return idFactura;
    }

    public ArrayList<Factura> listarFacturas() {
        ArrayList<Factura> listado = new ArrayList();
        String sql = "SELECT f.idFactura, f.tipoPago, f.tipoDocumentoVenta, f.idCliente, f.idVendedor, f.fecha, f.total, f.estado, u.nombre AS nombreVendedor, c.nombre AS nombreCliente "
            + "FROM factura f "
            + "JOIN usuario u ON f.idVendedor = u.idUsuario "
            + "JOIN cliente c ON f.idCliente = c.idCliente "
            + "WHERE f.estado = 'ACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Factura factura = new Factura();
                factura.setCodigo(rs.getString("idFactura"));
                factura.setTipoDocumentoVenta(rs.getString("tipoDocumentoVenta"));
                factura.setFecha(rs.getDate("fecha"));
                factura.setTotal(rs.getDouble("total"));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idVendedor"));
                usuario.setNombre(rs.getString("nombreVendedor"));
                factura.setUsuario(usuario);

                Cliente cliente = new Cliente();
                cliente.setDni(rs.getInt("idCliente"));
                cliente.setNombre(rs.getString("nombreCliente"));
                factura.setCliente(cliente);

                listado.add(factura);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return listado;
    }

    public boolean actualizarStock(int cantidad, String codigo) {
        String sql = "UPDATE producto SET cantidad = ? WHERE idProducto = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ps.setString(2, codigo);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean anularFactura(String cod) {
        String nuevoEstado = "ELIMINADO";
        String sql = "UPDATE factura SET estado = ? WHERE idFactura = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setString(2, cod);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    //Reportes
    public ArrayList<Factura> obtenerVentasPorFecha(String fechaInicio, String fechaFin) {
        //String sql = "SELECT idFactura, tipoPago, tipoDocumentoVenta, idCliente, idVendedor, fecha, total, estado "
        //       + "FROM factura "
        //        + "WHERE fecha BETWEEN ? AND ?";
        String sql = "SELECT f.idFactura, f.tipoPago, f.tipoDocumentoVenta, f.idCliente, f.idVendedor, f.fecha, f.total, f.estado, u.nombre AS nombreVendedor, c.nombre AS nombreCliente "
                + "FROM factura f "
                + "JOIN usuario u ON f.idVendedor = u.idUsuario "
                + "JOIN cliente c ON f.idCliente = c.idCliente "
                + "WHERE f.fecha BETWEEN ? AND ? AND f.estado = 'ACTIVO'";
        ArrayList<Factura> listaVentas = new ArrayList<>();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            rs = ps.executeQuery();
            while (rs.next()) {
                Factura factura = new Factura();
                factura.setCodigo(rs.getString("idFactura"));
                factura.setTipoPago(rs.getString("tipoPago"));
                factura.setTipoDocumentoVenta(rs.getString("tipoDocumentoVenta"));
                //AGREGAR CLIENTE
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNombre(rs.getString("nombreCliente"));
                factura.setCliente(cliente);
                //AGREGAR VENDEDOR
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idVendedor"));
                usuario.setNombre(rs.getString("nombreVendedor"));
                factura.setUsuario(usuario);
                factura.setFecha(rs.getDate("fecha"));
                factura.setTotal(rs.getDouble("total"));
                factura.setEstado(rs.getString("estado"));

                listaVentas.add(factura);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return listaVentas;
    }
    
    
    public ArrayList<DetalleFactura> obtenerDetalleFacturaVenta(String codigo){
        String sql = "SELECT p.idProducto, p.nombreProducto, p.descripcion, p.precio, d.cantidad, d.subtotal "
                + "FROM producto p "
                + "INNER JOIN detalleFactura d ON p.idProducto = d.idProducto "
                + "WHERE d.idFactura = ?";
        ArrayList<DetalleFactura> listaDetalle = new ArrayList<>();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            while(rs.next()){
                DetalleFactura detalle = new DetalleFactura();
                //Insertar datos del producto
                Producto producto = new Producto();
                producto.setIdProducto(rs.getString("idProducto"));
                producto.setNombreProducto(rs.getString("nombreProducto"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio"));
                detalle.setProducto(producto);
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setTotal(rs.getDouble("subtotal"));
                listaDetalle.add(detalle);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listaDetalle;
    }

}
