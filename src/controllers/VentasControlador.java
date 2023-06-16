package controllers;

import DAO.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import operaciones.*;
import pdf.PDFFactura;
import util.Utilidades;
import views.*;
import views.modal.*;

public class VentasControlador implements ActionListener, MouseListener, ItemListener {

    PDFFactura pdf;
    Cliente cliente = new Cliente();
    ClientesDAO clientesDAO;
    Producto producto;
    ProductosDAO productosDAO;
    Factura factura = new Factura();
    VentasDAO ventasDAO = new VentasDAO();
    frmVentas vistaVentas;
    DefaultTableModel modelo = new DefaultTableModel();
    DefaultTableModel modeloVentas = new DefaultTableModel();
    //DefaultTableModel modeloDetalleVenta = new DefaultTableModel();
    ReportesOp reportesOp = new ReportesOp(ventasDAO);
    VentasOp ventasOp = new VentasOp(ventasDAO);

    public VentasControlador(PDFFactura pdf, Cliente cliente, ClientesDAO clientesDAO, Producto producto, ProductosDAO productosDAO, Factura factura, VentasDAO ventasDAO, frmVentas vistaVentas) {
        this.pdf = pdf;
        this.cliente = cliente;
        this.clientesDAO = clientesDAO;
        this.producto = producto;
        this.productosDAO = productosDAO;
        this.factura = factura;
        this.ventasDAO = ventasDAO;
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
        ventasOp.cargarDatosVendedor(vistaVentas.txtNombreVendedor);
        reportesOp.cargarListaFactura(modeloVentas, vistaVentas.TableListadoVentas, vistaVentas.labelNumeroRegistros);
        Utilidades.centrarDatosTabla(vistaVentas.TableNuevaVenta);
        Utilidades.centrarDatosTabla(vistaVentas.TableListadoVentas);
        Utilidades.centrarDatosTabla(vistaVentas.TableDetalleVenta);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableNuevaVenta);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableListadoVentas);
        Utilidades.bloquearEdicionTabla(vistaVentas.TableDetalleVenta);
        this.vistaVentas.txtTipoDocumentoCliente.setText("DNI");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Buscar producto por su codigo
        if (e.getSource() == vistaVentas.btnBuscarProducto) {
            String codigo = vistaVentas.txtBuscarProducto.getText();
            if (!Utilidades.validarCamposVacios(codigo)) {
                return;
            }

            try {
                producto = productosDAO.buscarProducto(codigo);
                if (producto.getIdProducto() != null) {
                    vistaVentas.txtNombreProducto.setText(producto.getNombreProducto());
                    vistaVentas.txtPrecio.setText(String.valueOf(producto.getPrecio()));
                    vistaVentas.txtStockDisponible.setText(String.valueOf(producto.getCantidad()));
                } else {
                    JOptionPane.showMessageDialog(null, "No existe producto", "Campo incompleto", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Registrar producto en la tabla
        if (e.getSource() == vistaVentas.btnAgregarProducto) {
            String cantidadTexto = vistaVentas.txtCantidad.getText();
            String producto = vistaVentas.txtNombreProducto.getText();

            // Verificar que se ingresa una cantidad
            if (cantidadTexto.isEmpty() || !cantidadTexto.matches("\\d+") || Integer.parseInt(cantidadTexto) <= 0) {
                JOptionPane.showMessageDialog(null, "Ingrese una cantidad válida (números enteros positivos)", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Verificar si no se ha seleccionado un producto
            if (producto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto para continuar", "Campo incompleto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean camposSonNumeros = Utilidades.validarCamposNumericos(cantidadTexto);
            if (!camposSonNumeros) {
                return;
            }

            int cantidadComprar = Integer.parseInt(cantidadTexto);
            int stockDisponible = Integer.parseInt(vistaVentas.txtStockDisponible.getText());

            // Verificar que la cantidad a comprar no sea mayor al stock disponible
            if (cantidadComprar > stockDisponible) {
                JOptionPane.showMessageDialog(null, "Stock no disponible, seleccione menos unidades", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String codigoProducto = vistaVentas.txtBuscarProducto.getText().toUpperCase();
            String nombreProducto = vistaVentas.txtNombreProducto.getText();
            double precioProducto = Double.parseDouble(vistaVentas.txtPrecio.getText());
            double total = cantidadComprar * precioProducto;
            double totalFormateado = Math.floor(total * 100) / 100;

            modelo = (DefaultTableModel) vistaVentas.TableNuevaVenta.getModel();

            // Verificar si el producto ya existe en la tabla
            int rowCount = modelo.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String codigo = (String) modelo.getValueAt(i, 0);
                if (codigo.equals(codigoProducto)) {
                    int cantidadExistente = (int) modelo.getValueAt(i, 2);
                    double totalExistente = (double) modelo.getValueAt(i, 4);
                    int nuevaCantidad = cantidadExistente + cantidadComprar;
                    double nuevoTotal = totalExistente + totalFormateado;

                    // Verificar si la nueva cantidad supera el stock disponible
                    if (nuevaCantidad > stockDisponible) {
                        JOptionPane.showMessageDialog(null, "No se puede superar el stock disponible", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Actualizar datos de la tabla
                    modelo.setValueAt(nuevaCantidad, i, 2);
                    modelo.setValueAt(nuevoTotal, i, 4);
                    ventasOp.calcularTotalPagoFactura(
                            vistaVentas.TableNuevaVenta,
                            vistaVentas.txtSubtotal,
                            vistaVentas.txtIGV,
                            vistaVentas.txtTotalPagar);
                    limpiarDatosProducto();
                    return;
                }
            }

            // Añadir productos a la tabla
            Object[] fila = {codigoProducto, nombreProducto, cantidadComprar, precioProducto, totalFormateado};
            modelo.addRow(fila);
            ventasOp.calcularTotalPagoFactura(
                    vistaVentas.TableNuevaVenta,
                    vistaVentas.txtSubtotal,
                    vistaVentas.txtIGV,
                    vistaVentas.txtTotalPagar);
            limpiarDatosProducto();
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Eliminar producto del listado
        if (e.getSource() == vistaVentas.btnEliminar) {
            int filaSeleccionada = vistaVentas.TableNuevaVenta.getSelectedRow();
            if (filaSeleccionada == -1) {
                // No hay una línea seleccionada
                JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                modelo = (DefaultTableModel) vistaVentas.TableNuevaVenta.getModel();
                modelo.removeRow(filaSeleccionada);
                ventasOp.calcularTotalPagoFactura(
                        vistaVentas.TableNuevaVenta,
                        vistaVentas.txtSubtotal,
                        vistaVentas.txtIGV,
                        vistaVentas.txtTotalPagar);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Registrar Factura
        if (e.getSource() == vistaVentas.btnGenerarVenta) {
            String numeroDocumentoCliente = vistaVentas.txtNumeroDocumentoCliente.getText();
            double totalPagoFactura = Double.parseDouble(vistaVentas.txtTotalPagar.getText().replace(",", "."));

            // Validar los campos obligatorios
            if (!Utilidades.validarCamposVacios(numeroDocumentoCliente)) {
                return;
            }

            // Verificar que el doc cliente solo se ingresen numeros
            boolean camposSonNumeros = Utilidades.validarCamposNumericos(numeroDocumentoCliente);
            if (!camposSonNumeros) {
                return;
            }

            //  Verificar que importe no sea menor a el pago total factura cuando se selecciona efectivo
            String metodoPago = vistaVentas.cbTipoPago.getSelectedItem().toString();
            if (metodoPago.equals("EFECTIVO")) {
                double importe = Double.parseDouble(vistaVentas.txtImporte.getText().replace(",", "."));
                if (importe < totalPagoFactura) {
                    JOptionPane.showMessageDialog(null, "El importe ingresado no puede ser menor a el pago total");
                    return;
                }
            }

            //Validar el tipo de documento del cliente, debe coincider con el tipo de venta @boleta @factura
            String tipoDocCliente = vistaVentas.txtTipoDocumentoCliente.getText();
            String numeroDocCliente = vistaVentas.txtNumeroDocumentoCliente.getText();
            String tipoVenta = vistaVentas.cbTipoDocumentoVenta.getSelectedItem().toString();

            boolean tipoDocumentoCorrecto = ventasOp.validarTipoVentaDocumento(tipoVenta, tipoDocCliente, numeroDocCliente);
            if (!tipoDocumentoCorrecto) {
                return;
            }

            int idCliente = 0;
            int numeroCliente = Integer.parseInt(numeroDocumentoCliente);

            try {
                cliente = clientesDAO.buscarCliente(numeroCliente);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return;
            }

            // Verificar si el cliente existe para asignar el idCliente
            if (cliente == null || cliente.getIdCliente() == 0) {
                // El cliente no está registrado, se procede a registrar
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setTipoDocumento(tipoDocCliente);
                nuevoCliente.setDni(numeroCliente);
                clientesDAO.registrarCliente(nuevoCliente);
                idCliente = clientesDAO.obtenerUltimoIdCliente();
            } else {
                idCliente = cliente.getIdCliente();
            }

            //Si el cliente ya existe seguimos ...
            //Insertar tipo de pago
            String tipoPago = vistaVentas.cbTipoPago.getSelectedItem().toString();
            factura.setTipoPago(tipoPago);
            factura.setTipoDocumentoVenta(tipoVenta);

            Cliente estadoIdCliente = new Cliente();
            estadoIdCliente.setIdCliente(idCliente);
            factura.setCliente(estadoIdCliente);

            Usuario vendedor = new Usuario();
            Usuario usuarioActual = Session.getUsuarioActual();
            vendedor.setIdUsuario(usuarioActual.getIdUsuario());
            factura.setUsuario(vendedor);
            java.sql.Date fecha = java.sql.Date.valueOf(LocalDate.now());
            factura.setFecha(fecha);
            factura.setTotal(totalPagoFactura);
            // Registrar factura //
            ventasDAO.registrarVenta(factura);

            //Detalle factura y actualizacion de stock
            String idFactura = ventasDAO.obtenerUltimoIdFactura();
            for (int i = 0; i < vistaVentas.TableNuevaVenta.getRowCount(); i++) {
                String idProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 0).toString();
                double subtotal = (Double) vistaVentas.TableNuevaVenta.getValueAt(i, 4);
                int cantidad = (Integer) vistaVentas.TableNuevaVenta.getValueAt(i, 2);

                Producto productoDetalle = new Producto();
                productoDetalle.setIdProducto(idProducto);

                Factura facturaDetalle = new Factura();
                facturaDetalle.setCodigo(idFactura);

                DetalleFactura detalle = new DetalleFactura(productoDetalle, facturaDetalle, subtotal, cantidad);
                ventasDAO.registrarDetalleFactura(detalle);

                // Actualizar stock
                try {
                    producto = productosDAO.buscarProducto(idProducto);
                    int nuevoStock = producto.getCantidad() - cantidad;
                    ventasDAO.actualizarStock(nuevoStock, idProducto);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }

            reportesOp.cargarListaFactura(modeloVentas, vistaVentas.TableListadoVentas, vistaVentas.labelNumeroRegistros);

            // Imprimir el PDF segun el Tipo de venta
            ventasOp.imprimirPDF(vistaVentas, tipoVenta);

            JOptionPane.showMessageDialog(null, "Venta Realizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarDatosFactura();
            limpiarDatosProducto();
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Calcular cambio
        if (e.getSource() == vistaVentas.btnCalcularCambio) {
            String importe = vistaVentas.txtImporte.getText();

            if (importe.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el importe", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!ventasOp.esDouble(importe)) {
                return;
            }

            String valorTotal = vistaVentas.txtTotalPagar.getText();
            String valorCorregido = valorTotal.replace(",", ".");
            double total = Double.parseDouble(valorCorregido);
            double importeDouble = Double.parseDouble(importe);

            if (importeDouble < total) {
                JOptionPane.showMessageDialog(null, "El importe no puede ser menor a el total", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ventasOp.calcularCambio(
                    vistaVentas.txtTotalPagar,
                    vistaVentas.txtImporte,
                    vistaVentas.txtCambio);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nueva Factura
        if (e.getSource() == vistaVentas.btnNuevaFactura) {
            limpiarDatosProducto();
            limpiarDatosFactura();
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

    //------------------------------------------------------------------------------------------------------------------------------------------
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
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void itemStateChanged(ItemEvent e) {
        //------------------------------------------------------------------------------------------------------------------------------------------
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

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarDatosProducto() {
        vistaVentas.txtBuscarProducto.setText(null);
        vistaVentas.txtNombreProducto.setText(null);
        vistaVentas.txtCantidad.setText(null);
        vistaVentas.txtPrecio.setText(null);
        vistaVentas.txtStockDisponible.setText(null);
    }

    public void limpiarDatosFactura() {
        Utilidades.limpiarTable(modelo);
        vistaVentas.txtNumeroDocumentoCliente.setText(null);
        vistaVentas.txtNombreCliente.setText(null);
        vistaVentas.txtImporte.setText(null);
        vistaVentas.txtCambio.setText(null);
        vistaVentas.txtSubtotal.setText(null);
        vistaVentas.txtIGV.setText(null);
        vistaVentas.txtTotalPagar.setText(null);
    }

}
