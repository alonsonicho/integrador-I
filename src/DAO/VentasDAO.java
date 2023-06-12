package DAO;

import Connection.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        String sql = "SELECT f.idFactura, v.dni_usuario, f.fecha, c.dni, f.total FROM factura f"
                            + " LEFT JOIN cliente c ON f.idCliente = c.idCliente "
                            + "LEFT JOIN usuario v ON f.idVendedor = v.idUsuario "
                            + "WHERE f.estado = 'ACTIVO';";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Factura factura = new Factura();
                factura.setCodigo(rs.getString("idFactura"));
                factura.setFecha(rs.getDate("fecha"));
                factura.setTotal(rs.getDouble("total"));

                Usuario usuario = new Usuario();
                usuario.setDniUsuario(rs.getInt("dni_usuario"));
                factura.setUsuario(usuario);

                Cliente cliente = new Cliente();
                cliente.setDni(rs.getInt("dni"));
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
    
    public boolean anularFactura(String cod){
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

}
