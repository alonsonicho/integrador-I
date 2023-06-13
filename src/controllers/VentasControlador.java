package controllers;

import DAO.*;
import java.awt.Desktop;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import pdf.PDFFactura;
import pdf.PDFBoleta;
import util.Utilidades;
import views.*;
import views.modal.modalAgregarCliente;
import views.modal.modalAgregarProducto;

public class VentasControlador implements ActionListener, MouseListener, ItemListener, PropertyChangeListener {

    PDFFactura pdf;
    Cliente cliente = new Cliente();
    ClientesDAO clientesDAO;
    Producto producto;
    ProductosDAO productosDAO;
    Factura factura = new Factura();
    VentasDAO ventasDAO;
    frmVentas vistaVentas;
    DefaultTableModel modelo = new DefaultTableModel();
    DefaultTableModel modeloVentas = new DefaultTableModel();
    DefaultTableModel modeloDetalleVenta = new DefaultTableModel();

    double subtotalFactura;
    double igvFactura;
    double totalPagoFactura;

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
        this.vistaVentas.btnFacturaPDF.addActionListener(this);
        this.vistaVentas.btnEliminarDatosCliente.addActionListener(this);
        this.vistaVentas.btnAnularFactura.addActionListener(this);
        this.vistaVentas.TableNuevaVenta.addMouseListener(this);
        this.vistaVentas.TableListadoVentas.addMouseListener(this);
        this.vistaVentas.btnModalAgregarProducto.addActionListener(this);
        this.vistaVentas.cbTipoPago.addItemListener(this);
        this.vistaVentas.cbTipoDocumentoVenta.addItemListener(this);
        this.vistaVentas.fechaDesde.getDateEditor().addPropertyChangeListener(this);
        this.vistaVentas.fechaHasta.getDateEditor().addPropertyChangeListener(this);
        cargarVendedor();
        cargarListaFactura();
        Utilidades.centrarDatosTabla(vistaVentas.TableNuevaVenta);
        Utilidades.centrarDatosTabla(vistaVentas.TableListadoVentas);
        Utilidades.centrarDatosTabla(vistaVentas.TableDetalleVenta);
        //Establecer tablas como no editables
        this.vistaVentas.TableNuevaVenta.setDefaultEditor(Object.class, null);
        this.vistaVentas.TableListadoVentas.setDefaultEditor(Object.class, null);
        this.vistaVentas.TableDetalleVenta.setDefaultEditor(Object.class, null);
        this.vistaVentas.txtTipoDocumentoCliente.setText("DNI");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Buscar producto por su codigo
        if (e.getSource() == vistaVentas.btnBuscarProducto) {
            String codigo = vistaVentas.txtBuscarProducto.getText();
            if(!Utilidades.validarCamposVacios(codigo)){
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
                    calcularTotalPagoFactura();
                    limpiarDatosProducto();
                    return;
                }
            }

            // Añadir productos a la tabla
            Object[] fila = {codigoProducto, nombreProducto, cantidadComprar, precioProducto, totalFormateado};
            modelo.addRow(fila);
            calcularTotalPagoFactura();
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
                calcularTotalPagoFactura();
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Registrar Factura
        if (e.getSource() == vistaVentas.btnGenerarVenta) {
            String numeroDocumentoCliente = vistaVentas.txtNumeroDocumentoCliente.getText();

            if(!Utilidades.validarCamposVacios(numeroDocumentoCliente)){
                return;
            }

            boolean camposSonNumeros = Utilidades.validarCamposNumericos(numeroDocumentoCliente);
            if (!camposSonNumeros) {
                return;
            }

            //Validar el tipo de documento del cliente, debe coincider con el tipo de venta @boleta @factura
            String tipoDocCliente = vistaVentas.txtTipoDocumentoCliente.getText();
            String numeroDocCliente = vistaVentas.txtNumeroDocumentoCliente.getText();
            String tipoVenta = vistaVentas.cbTipoDocumentoVenta.getSelectedItem().toString();

            boolean tipoDocumentoCorrecto = validarTipoVentaDocumento(tipoVenta, tipoDocCliente, numeroDocCliente);
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

            cargarListaFactura();

            // Imprimir el PDF segun el Tipo de venta
            if (tipoVenta.equals("FACTURA")) {
                PDFFactura pdf = new PDFFactura(vistaVentas, ventasDAO);
                pdf.iniciarPDF();
            } else {
                PDFBoleta pdf = new PDFBoleta(vistaVentas, ventasDAO);
                pdf.iniciarPDF();
            }

            JOptionPane.showMessageDialog(null, "Venta Realizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Abrir detalle factura PDFFactura
        if (e.getSource() == vistaVentas.btnFacturaPDF) {
            String idFactura = vistaVentas.txtIdFacturaPDF.getText();

            //Verificar que el campo no este vacio
            if (idFactura.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una factura", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                File file = new File("src/pdf/" + idFactura + ".pdf");
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Calcular cambio
        if (e.getSource() == vistaVentas.btnCalcularCambio) {
            String importe = vistaVentas.txtImporte.getText();

            if (importe.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el importe", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!esDouble(importe)) {
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

            calcularCambio();
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Anular Factura
        if (e.getSource() == vistaVentas.btnAnularFactura) {
            String idFactura = vistaVentas.txtIdFacturaPDF.getText();

            //Verificar que el campo no este vacio
            if (idFactura.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una factura", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas anular la factura", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (ventasDAO.anularFactura(idFactura)) {
                    vistaVentas.txtIdFacturaPDF.setText(null);
                    Utilidades.limpiarTable(modelo);
                    cargarListaFactura();
                    JOptionPane.showMessageDialog(null, "Factura anulada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nueva Factura
        if (e.getSource() == vistaVentas.btnNuevaFactura) {
            limpiarDatosProducto();
            Utilidades.limpiarTable(modelo);
            vistaVentas.txtNumeroDocumentoCliente.setText(null);
            vistaVentas.txtNombreCliente.setText(null);
            vistaVentas.txtImporte.setText(null);
            vistaVentas.txtCambio.setText(null);
            vistaVentas.txtSubtotal.setText(null);
            vistaVentas.txtIGV.setText(null);
            vistaVentas.txtTotalPagar.setText(null);
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

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Filtrar ventas por fechas
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

        if (e.getSource() == vistaVentas.TableListadoVentas) {
            int fila = vistaVentas.TableListadoVentas.rowAtPoint(e.getPoint());
            vistaVentas.txtIdFacturaPDF.setText(vistaVentas.TableListadoVentas.getValueAt(fila, 0).toString());

            String codigoFactura = vistaVentas.TableListadoVentas.getValueAt(fila, 0).toString();
            // Obtener el detalle de la factura según la venta seleccionada
            ArrayList<DetalleFactura> detalleFactura = ventasDAO.obtenerDetalleFacturaVenta(codigoFactura);

            modeloDetalleVenta = (DefaultTableModel) vistaVentas.TableDetalleVenta.getModel();
            modeloDetalleVenta.setRowCount(0); // Limpiar la tabla antes de agregar los nuevos datos

            for (DetalleFactura detalle : detalleFactura) {
                Object[] rowData = {
                    detalle.getProducto().getIdProducto(),
                    detalle.getProducto().getNombreProducto(),
                    detalle.getProducto().getDescripcion(),
                    detalle.getProducto().getPrecio(),
                    detalle.getCantidad(),
                    detalle.getTotal()
                };
                modeloDetalleVenta.addRow(rowData);
            }
            // Actualizar la tabla visualmente
            vistaVentas.TableDetalleVenta.setModel(modeloDetalleVenta);
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
    public void limpiarDatosProducto() {
        vistaVentas.txtBuscarProducto.setText(null);
        vistaVentas.txtNombreProducto.setText(null);
        vistaVentas.txtCantidad.setText(null);
        vistaVentas.txtPrecio.setText(null);
        vistaVentas.txtStockDisponible.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void calcularTotalPagoFactura() {
        subtotalFactura = 0;
        igvFactura = 0;
        totalPagoFactura = 0;
        int numeroFilas = vistaVentas.TableNuevaVenta.getRowCount();
        for (int i = 0; i < numeroFilas; i++) {
            double totalPorProducto = (double) vistaVentas.TableNuevaVenta.getModel().getValueAt(i, 4);
            subtotalFactura += totalPorProducto;
        }
        igvFactura = subtotalFactura * 0.18;
        totalPagoFactura = subtotalFactura + igvFactura;

        vistaVentas.txtSubtotal.setText(String.format("%.2f", subtotalFactura));
        vistaVentas.txtIGV.setText(String.format("%.2f", igvFactura));
        vistaVentas.txtTotalPagar.setText(String.format("%.2f", totalPagoFactura));
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void cargarVendedor() {
        Usuario usuarioActual = Session.getUsuarioActual();
        String nombreVendedor = (usuarioActual != null) ? usuarioActual.getNombre() : "Vendedor no registrado";
        vistaVentas.txtNombreVendedor.setText(nombreVendedor);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void cargarListaFactura() {
        ArrayList<Factura> lista = ventasDAO.listarFacturas();
        modeloVentas = new DefaultTableModel();
        modeloVentas = (DefaultTableModel) vistaVentas.TableListadoVentas.getModel();
        modeloVentas.setRowCount(0);
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getCodigo();
            obj[1] = lista.get(i).getTipoDocumentoVenta();
            obj[2] = lista.get(i).getUsuario().getNombre();
            obj[3] = lista.get(i).getFecha();
            obj[4] = lista.get(i).getCliente().getNombre();
            obj[5] = lista.get(i).getTotal();
            modeloVentas.addRow(obj);
        }
        vistaVentas.TableListadoVentas.setModel(modeloVentas);
        //Obtener el numero de filas de la tabla
        int numeroRegistros = vistaVentas.TableListadoVentas.getRowCount();
        vistaVentas.labelNumeroRegistros.setText("Registros encontrados :"+numeroRegistros);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void calcularCambio() {
        double importe = Double.parseDouble(vistaVentas.txtImporte.getText());
        double cambio = importe - totalPagoFactura;

        vistaVentas.txtCambio.setText(String.valueOf(cambio));
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    
    public boolean esDouble(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private boolean validarTipoVentaDocumento(String tipoVenta, String tipoDocCliente, String numeroDocCliente) {
        if (tipoVenta.equals("BOLETA")) {
            if (!tipoDocCliente.equals("DNI")) {
                JOptionPane.showMessageDialog(null, "El tipo de documento del cliente debe ser DNI para una venta de tipo boleta.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (numeroDocCliente.length() != 8) {
                JOptionPane.showMessageDialog(null, "El número de documento del cliente debe tener una longitud de 8 caracteres para una venta de tipo boleta.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else if (tipoVenta.equals("FACTURA")) {
            if (!tipoDocCliente.equals("RUC")) {
                JOptionPane.showMessageDialog(null, "El tipo de documento del cliente debe ser RUC para una venta de tipo factura.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (numeroDocCliente.length() != 10) {
                JOptionPane.showMessageDialog(null, "El número de documento del cliente debe tener una longitud de 10 caracteres para una venta de tipo factura.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (!numeroDocCliente.startsWith("10") && !numeroDocCliente.startsWith("20")) {
                JOptionPane.showMessageDialog(null, "El número de documento del cliente debe comenzar con '10' o '20' para una venta de tipo factura.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Tipo de venta no válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

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

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if ("date".equals(e.getPropertyName())) {
            // Obtener la fecha de inicio y fecha fin seleccionadas del JDateChooser
            Date fechaInicio = vistaVentas.fechaDesde.getDate();
            Date fechaFin = vistaVentas.fechaHasta.getDate();

            // Verificar que ambas fechas estén seleccionadas
            if (fechaInicio != null && fechaFin != null) {
                // Convertir las fechas al formato deseado ("yyyy-MM-dd")
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strFechaInicio = dateFormat.format(fechaInicio);
                String strFechaFin = dateFormat.format(fechaFin);

                // Obtener las ventas por el rango de fechas
                ArrayList<Factura> ventasFiltradas = ventasDAO.obtenerVentasPorFecha(strFechaInicio, strFechaFin);

                // Utilizar las ventas filtradas...
                modeloVentas = (DefaultTableModel) vistaVentas.TableListadoVentas.getModel();

                // Limpiar la tabla
                modeloVentas.setRowCount(0);

                // Agregar las filas con los datos de las ventas
                for (Factura venta : ventasFiltradas) {
                    Object[] fila = new Object[]{
                        venta.getCodigo(),
                        venta.getTipoDocumentoVenta(),
                        venta.getUsuario().getNombre(),
                        venta.getFecha(),
                        venta.getCliente().getNombre(),
                        venta.getTotal()
                    };
                    modeloVentas.addRow(fila);
                }

                // Actualizar la tabla visualmente
                vistaVentas.TableListadoVentas.setModel(modeloVentas);
                // Actualizar el numero de registros
                int numeroRegistros = vistaVentas.TableListadoVentas.getRowCount();
                vistaVentas.labelNumeroRegistros.setText("Registros encontrados :"+numeroRegistros);
            }
        }
    }

    

}
