package DAO;

import Connection.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import models.Cliente;

public class ClientesDAO extends Conexion {

    PreparedStatement ps;
    ResultSet rs;

    public boolean registrarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (tipoDocumento, dni, nombre, telefono, direccion, estado) VALUES (?,?,?,?,?,?)";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getTipoDocumento());
            ps.setString(2, cliente.getNumeroDocumento());
            ps.setString(3, cliente.getNombre());
            ps.setInt(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, "ACTIVO");
            ps.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "El N° de documento '" + cliente.getNumeroDocumento() + "' ya esta siendo utilizado.");
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean actualizarCliente(Cliente cli) {
        String sql = "UPDATE cliente SET tipoDocumento = ?, dni = ? ,nombre = ?,"
                + " telefono = ?, direccion = ? WHERE idCliente = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cli.getTipoDocumento());
            ps.setString(2, cli.getNumeroDocumento());
            ps.setString(3, cli.getNombre());
            ps.setInt(4, cli.getTelefono());
            ps.setString(5, cli.getDireccion());
            ps.setInt(6, cli.getIdCliente());
            ps.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            String errorMessage = e.getMessage();
            System.out.println(errorMessage);
            if (errorMessage.contains("dni")) {
                JOptionPane.showMessageDialog(null, "El N° de documento '" + cli.getNumeroDocumento() + "' ya esta registrado para otro cliente");
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    //Listar clientes en estado "ACTIVO"
    public ArrayList<Cliente> listarClientes() {
        ArrayList<Cliente> listaCliente = new ArrayList();
        String sql = "SELECT * FROM cliente where estado = 'ACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setTipoDocumento(rs.getString("tipoDocumento"));
                cli.setIdCliente(rs.getInt("idCliente"));
                cli.setNumeroDocumento(rs.getString("dni"));
                cli.setNombre(rs.getString("nombre"));
                cli.setTelefono(rs.getInt("telefono"));
                cli.setDireccion(rs.getString("direccion"));
                cli.setEstado(rs.getString("estado"));
                listaCliente.add(cli);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaCliente;
    }

    //Listar clientes filtrado con texto busqueda
    public ArrayList<Cliente> listarClientesFiltrado(String textoBusqueda) {
        String sql = "SELECT * FROM cliente WHERE (dni LIKE ? OR nombre LIKE ?) AND estado = 'ACTIVO'";
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + textoBusqueda + "%");
            ps.setString(2, "%" + textoBusqueda + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setTipoDocumento(rs.getString("tipoDocumento"));
                cliente.setNumeroDocumento(rs.getString("dni"));
                cliente.setNombre(rs.getString("nombre"));
                listaClientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaClientes;
    }

    //Listar clientes en estado "INACTIVO"
    public ArrayList<Cliente> listarClientesInactivo() {
        ArrayList<Cliente> listaCliente = new ArrayList();
        String sql = "SELECT * FROM cliente where estado = 'INACTIVO'";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setTipoDocumento(rs.getString("tipoDocumento"));
                cli.setIdCliente(rs.getInt("idCliente"));
                cli.setNumeroDocumento(rs.getString("dni"));
                cli.setNombre(rs.getString("nombre"));
                cli.setTelefono(rs.getInt("telefono"));
                cli.setDireccion(rs.getString("direccion"));
                cli.setEstado(rs.getString("estado"));
                listaCliente.add(cli);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listaCliente;
    }

    public Cliente buscarCliente(String dni) throws Exception {
        Cliente cli = new Cliente();
        try {
            Connection con = getConnection();
            String sql = "SELECT * FROM cliente where dni=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("estado");
                if (estado.equals("INACTIVO")) {
                    throw new Exception("El cliente está inactivo. Actívelo antes de continuar.");
                }

                cli.setTipoDocumento(rs.getString("tipoDocumento"));
                cli.setIdCliente(rs.getInt("idCliente"));
                cli.setNumeroDocumento(rs.getString("dni"));
                cli.setNombre(rs.getString("nombre"));
                cli.setTelefono(rs.getInt("telefono"));
                cli.setDireccion(rs.getString("direccion"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cli;
    }

    public int obtenerUltimoIdCliente() {
        String sql = "SELECT MAX(idCliente) FROM cliente";
        int idCliente = 0;
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                idCliente = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return idCliente;
    }

    public boolean eliminarCliente(int id) {
        String nuevoEstado = "INACTIVO";
        String sql = "UPDATE cliente SET estado = ? WHERE idCliente = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean activarCliente(int id) {
        String nuevoEstado = "ACTIVO";
        String sql = "UPDATE cliente SET estado = ? WHERE idCliente = ?";
        try {
            Connection con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
