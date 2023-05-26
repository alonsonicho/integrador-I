package controllers;

import DAO.ClientesDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import views.*;

public class ClientesControlador implements MouseListener, ActionListener, KeyListener {

    frmClientes vistaClientes;
    frmVentas vistaVentas;
    Cliente cl;
    ClientesDAO clienteDAO;
    DefaultTableModel modelo = new DefaultTableModel();

    public ClientesControlador(Cliente cl, ClientesDAO clienteDAO, frmClientes vistaClientes) {
        this.vistaClientes = vistaClientes;
        this.cl = cl;
        this.clienteDAO = clienteDAO;
        this.vistaClientes.btnregistrarcli.addActionListener(this);
        this.vistaClientes.btnmodificarcli.addActionListener(this);
        this.vistaClientes.btnnuevocli.addActionListener(this);
        this.vistaClientes.btnBuscarCliente.addActionListener(this);
        this.vistaClientes.JMenuEliminarCli.addActionListener(this);
        this.vistaClientes.TableMostrarClientes.addMouseListener(this);
        this.vistaClientes.txtbuscarcli.addKeyListener(this);
        this.vistaClientes.JLabelCrearReg.addMouseListener(this);
        this.vistaClientes.JLabelUsuariosReg.addMouseListener(this);
        this.vistaClientes.JLabelSalirReg.addMouseListener(this);
        listarClientes();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == vistaClientes.TableMostrarClientes) {
            int fila = vistaClientes.TableMostrarClientes.rowAtPoint(e.getPoint());
            vistaClientes.txtidcli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 0).toString());
            vistaClientes.txtdni.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 1).toString());
            vistaClientes.txtnombrecli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 2).toString());
            vistaClientes.txttelefonocli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 3).toString());
            vistaClientes.txadireccioncli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 4).toString());
            vistaClientes.btnregistrarcli.setEnabled(false);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Formulario de clientes
        if (e.getSource() == vistaClientes.JLabelCrearReg) {
            vistaClientes.jTabbedPane1.setSelectedIndex(0);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Formulario de usuarios
        if (e.getSource() == vistaClientes.JLabelUsuariosReg) {
            Usuario usuarioActual = Session.getUsuarioActual();
            if (usuarioActual.getRol().equals("Usuario")) {
                vistaClientes.jTabbedPane1.setSelectedIndex(0);
            } else {
                vistaClientes.jTabbedPane1.setSelectedIndex(1);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaClientes.JLabelSalirReg) {
            new frmMenuPrincipal().setVisible(true);
            vistaClientes.dispose();
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        ///////////////////////////////
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        ///////////////////////////////
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //////////////////////////////
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //////////////////////////////
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Registrar Cliente
        if (e.getSource() == vistaClientes.btnregistrarcli) {
            String nombreCliente = vistaClientes.txtnombrecli.getText();
            String dniCliente = vistaClientes.txtdni.getText();
            String telefonoCliente = vistaClientes.txttelefonocli.getText();
            String direccionCliente = vistaClientes.txadireccioncli.getText();
            
            if (nombreCliente.isEmpty() || dniCliente.isEmpty() || telefonoCliente.isEmpty() || direccionCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                return;
            }
            
            //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposEnteros(dniCliente, telefonoCliente)) {
                return;
            }
 
            int dni = Integer.parseInt(dniCliente);
            int telefono = Integer.parseInt(telefonoCliente);

            cl.setDni(dni);
            cl.setNombre(nombreCliente);
            cl.setTelefono(telefono);
            cl.setDireccion(direccionCliente);
            
            if (clienteDAO.registrarCliente(cl)) {
                limpiarTable();
                listarClientes();
                limpiar();
                JOptionPane.showMessageDialog(null, "Cliente Registrado");
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Actualizar usuarioCliente
        if (e.getSource() == vistaClientes.btnmodificarcli) {
            String idCliente = vistaClientes.txtidcli.getText();
            String nombreCliente = vistaClientes.txtnombrecli.getText();
            String dniCliente = vistaClientes.txtdni.getText();
            String telefonoCliente = vistaClientes.txttelefonocli.getText();
            String direccionCliente = vistaClientes.txadireccioncli.getText();
            
            if (idCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
                return;
            }
            
            if (nombreCliente.isEmpty() || dniCliente.isEmpty() || telefonoCliente.isEmpty() || direccionCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                return;
            }
            
             //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposEnteros(dniCliente, telefonoCliente)) {
                return;
            }
            
            int idCli = Integer.parseInt(idCliente);
            int dni = Integer.parseInt(dniCliente);
            int telefono = Integer.parseInt(telefonoCliente);
            
            cl.setDni(dni);
            cl.setNombre(nombreCliente);
            cl.setTelefono(telefono);
            cl.setDireccion(direccionCliente);
            cl.setIdCliente(idCli);
            
            if (clienteDAO.actualizarCliente(cl)) {
                limpiarTable();
                listarClientes();
                limpiar();
                JOptionPane.showMessageDialog(null, "Cliente modificado");
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Buscar cliente
        if (e.getSource() == vistaClientes.btnBuscarCliente) {
            String dniClienteString = vistaClientes.txtbuscarcli.getText();
            
            if (dniClienteString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el DNI del cliente");
                return;
            }
            
             //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposEnteros(dniClienteString)) {
                return;
            }
            
            int dniCliente = Integer.parseInt(dniClienteString);
            
            try {
                cl = clienteDAO.buscarCliente(dniCliente);
                if (cl.getDni() !=0 ) {
                    vistaClientes.txtnombrecli.setText(cl.getNombre());
                    vistaClientes.txtdni.setText(String.valueOf(cl.getDni()));
                    vistaClientes.txttelefonocli.setText(String.valueOf(cl.getTelefono()));
                    vistaClientes.txadireccioncli.setText(cl.getDireccion());
                    vistaClientes.txtidcli.setText(String.valueOf(cl.getIdCliente()));
                    vistaClientes.btnregistrarcli.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No existe el DNI ingresado");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } 
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nuevo Cliente
        if (e.getSource() == vistaClientes.btnnuevocli) {
            limpiar();
            vistaClientes.btnregistrarcli.setEnabled(true);
        }
        
        //------------------------------------------------------------------------------------------------------------------------------------------
        //Eliminar Cliente
        if(e.getSource() == vistaClientes.JMenuEliminarCli){
            String idClienteStr = vistaClientes.txtidcli.getText();
            
            if (idClienteStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el cliente");
                return;
            }
            
            int idCliente = Integer.parseInt(idClienteStr);
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                if (clienteDAO.eliminarCliente(idCliente)) {
                    limpiar();
                    limpiarTable();
                    listarClientes();
                    JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent ke) {
        //////////////////////////////
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //////////////////////////////
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //////////////////////////////
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarClientes() {
        List<Cliente> lista = clienteDAO.listarClientes();
        modelo = (DefaultTableModel) vistaClientes.TableMostrarClientes.getModel();
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getDni();
            obj[2] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[3] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[4] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modelo.addRow(obj);
        }
        vistaClientes.TableMostrarClientes.setModel(modelo);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vistaClientes.txtidcli.setText(null);
        vistaClientes.txtdni.setText(null);
        vistaClientes.txtnombrecli.setText(null);
        vistaClientes.txttelefonocli.setText(null);
        vistaClientes.txadireccioncli.setText(null);
        vistaClientes.txtbuscarcli.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarTable() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public boolean validarCamposEnteros(String... valores) {
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
