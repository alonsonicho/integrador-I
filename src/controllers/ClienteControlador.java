
package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import models.Session;
import models.Usuario;
import services.ClienteUtil;
import util.Utilidades;
import views.frmClientes;
import views.frmMenuPrincipal;


public class ClienteControlador implements ActionListener, MouseListener{ 

    frmClientes vistaClientes;
    ClienteUtil clienteUtil;

    public ClienteControlador(frmClientes vistaClientes) {
        this.vistaClientes = vistaClientes;
        this.vistaClientes.btnregistrarcli.addActionListener(this);
        this.vistaClientes.btnmodificarcli.addActionListener(this);
        this.vistaClientes.btnnuevocli.addActionListener(this);
        this.vistaClientes.btnBuscarCliente.addActionListener(this);
        this.vistaClientes.JMenuEliminarCli.addActionListener(this);
        this.vistaClientes.TableMostrarClientes.addMouseListener(this);
        this.vistaClientes.JLabelCrearReg.addMouseListener(this);
        this.vistaClientes.JLabelUsuariosReg.addMouseListener(this);
        this.vistaClientes.JLabelSalirReg.addMouseListener(this);
        this.vistaClientes.cbTipoDocumento.addActionListener(this);
        this.vistaClientes.TableInactiveClientes.addMouseListener(this);
        this.vistaClientes.btnActivarCliente.addActionListener(this);
        this.vistaClientes.radioButtonTrueCliente.addActionListener(this);
        this.vistaClientes.radioButtonFalseCliente.addActionListener(this);
        this.vistaClientes.radioButtonFalseCliente.setSelected(true);
        this.vistaClientes.radioButtonTrueCliente.setSelected(false);
        Utilidades.centrarDatosTabla(vistaClientes.TableMostrarClientes);
        Utilidades.centrarDatosTabla(vistaClientes.TableInactiveClientes);
        Utilidades.bloquearEdicionTabla(vistaClientes.TableMostrarClientes);
        Utilidades.bloquearEdicionTabla(vistaClientes.TableInactiveClientes);
        clienteUtil = new ClienteUtil(vistaClientes);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vistaClientes.btnregistrarcli){
            clienteUtil.registrarCliente();
        }
        
        if (e.getSource() == vistaClientes.btnmodificarcli){
            clienteUtil.actualizarCliente();
        }
        
        if (e.getSource() == vistaClientes.btnBuscarCliente){
            clienteUtil.buscarCliente();
        }
        
        if (e.getSource() == vistaClientes.JMenuEliminarCli){
            clienteUtil.eliminarCliente();
        }
        
        if (e.getSource() == vistaClientes.btnActivarCliente){
            clienteUtil.activarCliente();
        }
        
        //Nuevo Cliente, limpiar todos los datos del formulario
        if (e.getSource() == vistaClientes.btnnuevocli) {
            clienteUtil.limpiar();
            vistaClientes.btnregistrarcli.setEnabled(true);
        }
        
        //Control de evento para el radio button
        if (e.getSource() == vistaClientes.radioButtonTrueCliente) {
            vistaClientes.radioButtonFalseCliente.setSelected(false);
        }
        if (e.getSource() == vistaClientes.radioButtonFalseCliente) {
            vistaClientes.radioButtonTrueCliente.setSelected(false);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        //Llenar datos en el textField
        if (e.getSource() == vistaClientes.TableMostrarClientes) {
            int fila = vistaClientes.TableMostrarClientes.rowAtPoint(e.getPoint());
            vistaClientes.txtidcli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 0).toString());
            vistaClientes.cbTipoDocumento.setSelectedItem(vistaClientes.TableMostrarClientes.getValueAt(fila, 1));
            vistaClientes.txtdni.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 2).toString());
            vistaClientes.txtnombrecli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 3).toString());
            vistaClientes.txttelefonocli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 4).toString());
            vistaClientes.txadireccioncli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 5).toString());
            vistaClientes.btnregistrarcli.setEnabled(false);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaClientes.TableInactiveClientes) {
            int fila = vistaClientes.TableInactiveClientes.rowAtPoint(e.getPoint());
            vistaClientes.txtActivarClienteId.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 0).toString());
            vistaClientes.txtActiveTipoDocumento.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 1).toString());
            vistaClientes.txtActiveNumeroDocumentoCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 2).toString());
            vistaClientes.txtActiveNombreCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 3).toString());
            vistaClientes.txtActiveTelefonoCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 4).toString());
            vistaClientes.txtActiveDireccion.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 5).toString());
            vistaClientes.radioButtonFalseCliente.setSelected(true);
        }
        
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
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
