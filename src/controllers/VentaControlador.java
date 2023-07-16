
package controllers;

import java.awt.event.*;
import services.VentaUtil;
import util.Utilidades;
import views.frmMenuPrincipal;
import views.frmVentas;
import views.modal.modalAgregarCliente;
import views.modal.modalAgregarProducto;

public class VentaControlador implements ActionListener, MouseListener, ItemListener {

    frmVentas vistaVentas;
    VentaUtil ventasUtil;
    
    public VentaControlador(frmVentas vistaVentas) {
        this.vistaVentas = vistaVentas;
        this.vistaVentas.JLabelRegVenta.addMouseListener(this);
        this.vistaVentas.JLabelVentas.addMouseListener(this);
        this.vistaVentas.JLabelSalirVentas.addMouseListener(this);
        this.vistaVentas.btnBuscarProducto.addActionListener(this);
        this.vistaVentas.btnAgregarProducto.addActionListener(this);
        this.vistaVentas.btnGenerarVenta.addActionListener(this);
        this.vistaVentas.btnCalcularCambio.addActionListener(this);
        this.vistaVentas.btnNuevaFactura.addActionListener(this);
        this.vistaVentas.btnEliminar.addActionListener(this);
        this.vistaVentas.btnBuscarCliente.addActionListener(this);
        this.vistaVentas.btnEliminarDatosCliente.addActionListener(this);
        this.vistaVentas.TableNuevaVenta.addMouseListener(this);
        this.vistaVentas.btnModalAgregarProducto.addActionListener(this);
        this.vistaVentas.cbTipoPago.addItemListener(this);
        this.vistaVentas.cbTipoDocumentoVenta.addItemListener(this);
        Utilidades.centrarDatosTabla(vistaVentas.TableNuevaVenta);
        Utilidades.centrarDatosTabla(vistaVentas.TableListadoVentas);
        Utilidades.centrarDatosTabla(vistaVentas.TableDetalleVenta);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableNuevaVenta);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableListadoVentas);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableDetalleVenta);
        this.vistaVentas.txtTipoDocumentoCliente.setText("DNI");
        ventasUtil = new VentaUtil(vistaVentas);
    }

   
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vistaVentas.btnBuscarProducto){
            ventasUtil.buscarProducto();
        }
        
        // Registrar producto en la tabla
        if (e.getSource() == vistaVentas.btnAgregarProducto){
            ventasUtil.registrarProductoTabla();
        }
        
        //Eliminar producto de la tabla
        if (e.getSource() == vistaVentas.btnEliminar){
            ventasUtil.eliminarProductoTabla();
        }
        
        //Registrar Factura
        if (e.getSource() == vistaVentas.btnGenerarVenta){
            ventasUtil.registrarFactura();
        }
        
        //Calcular cambio
        if (e.getSource() == vistaVentas.btnCalcularCambio){
            ventasUtil.calcularCambio();
        }
        
        //Nueva Factura
        if (e.getSource() == vistaVentas.btnNuevaFactura) {
            ventasUtil.limpiarDatosProducto();
            ventasUtil.limpiarDatosFactura();
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Limpiar datos cliente
        if (e.getSource() == vistaVentas.btnEliminarDatosCliente) {
            vistaVentas.txtNumeroDocumentoCliente.setText(null);
            vistaVentas.txtNombreCliente.setText(null);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Abrir modal para seleccionar el producto y carga de datos
        if (e.getSource() == vistaVentas.btnModalAgregarProducto) {
            modalAgregarProducto vistaModal = new modalAgregarProducto(this.vistaVentas, true, vistaVentas);
            vistaModal.setVisible(true);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Abrir modal para seleccionar el cliente y carga de datos
        if (e.getSource() == vistaVentas.btnBuscarCliente) {
            modalAgregarCliente vistaModal = new modalAgregarCliente(this.vistaVentas, true, vistaVentas);
            vistaModal.setVisible(true);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getSource() == vistaVentas.JLabelRegVenta) {
            vistaVentas.jTabbedPane1.setSelectedIndex(0);
        }

        if (e.getSource() == vistaVentas.JLabelVentas) {
            vistaVentas.jTabbedPane1.setSelectedIndex(1);
        }

        if (e.getSource() == vistaVentas.JLabelSalirVentas) {
            new frmMenuPrincipal().setVisible(true);
            vistaVentas.dispose();
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        
        //Desactivar botones si se selecciona metodo de pago "TARJETA"
        if (vistaVentas.cbTipoPago.getSelectedItem().equals("TARJETA")) {
            vistaVentas.btnCalcularCambio.setEnabled(false);
            vistaVentas.txtImporte.setEditable(false);
            vistaVentas.txtCambio.setEditable(false);
            vistaVentas.txtImporte.setOpaque(false);
            vistaVentas.txtCambio.setOpaque(false);
        } else {
            vistaVentas.btnCalcularCambio.setEnabled(true);
            vistaVentas.txtImporte.setEditable(true);
            vistaVentas.txtCambio.setEditable(true);
            vistaVentas.txtImporte.setOpaque(true);
            vistaVentas.txtCambio.setOpaque(true);
        }

        if (vistaVentas.cbTipoDocumentoVenta.getSelectedItem().equals("FACTURA")) {
            vistaVentas.txtTipoDocumentoCliente.setText("RUC");
        } else {
            vistaVentas.txtTipoDocumentoCliente.setText("DNI");
        }
        
    }
    
    
    
}
