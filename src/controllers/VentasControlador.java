package controllers;

import DAO.*;
import java.awt.Desktop;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import pdf.PDF;
import views.*;

public class VentasControlador implements ActionListener, MouseListener {

    PDF pdf;
    Cliente cliente = new Cliente();
    ClientesDAO clientesDAO;
    Producto producto;
    ProductosDAO productosDAO;
    Factura factura = new Factura();
    VentasDAO ventasDAO;
    frmVentas vistaVentas;
    DefaultTableModel modelo = new DefaultTableModel();
    DefaultTableModel modeloVentas = new DefaultTableModel();

    double subtotalFactura;
    double igvFactura;
    double totalPagoFactura;

    public VentasControlador(PDF pdf, Cliente cliente, ClientesDAO clientesDAO, Producto producto, ProductosDAO productosDAO, Factura factura, VentasDAO ventasDAO, frmVentas vistaVentas) {
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
        this.vistaVentas.btnAñadirProducto.addActionListener(this);
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
        this.vistaVentas.cbTipoDocumento.addActionListener(this);
        cargarVendedor();
        cargarListaFactura();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Buscar producto por su codigo
        if (e.getSource() == vistaVentas.btnBuscarProducto) {
            String codigo = vistaVentas.txtBuscarProducto.getText();
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el código de producto");
                return;
            }

            try {
                producto = productosDAO.buscarProducto(codigo);
                if (producto.getIdProducto() != null) {
                    vistaVentas.txtNombreProducto.setText(producto.getNombreProducto());
                    vistaVentas.txtPrecio.setText(String.valueOf(producto.getPrecio()));
                    vistaVentas.txtStockDisponible.setText(String.valueOf(producto.getCantidad()));
                } else {
                    JOptionPane.showMessageDialog(null, "No existe producto");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Registrar producto en la tabla
        if (e.getSource() == vistaVentas.btnAñadirProducto) {
            String cantidadTexto = vistaVentas.txtCantidad.getText();

            // Verificar que se ingresa una cantidad
            if (cantidadTexto.isEmpty() || !cantidadTexto.matches("\\d+") || Integer.parseInt(cantidadTexto) <= 0) {
                JOptionPane.showMessageDialog(null, "Ingrese una cantidad válida (números enteros positivos)");
                return;
            }

            if (!validarCamposNumericos(cantidadTexto)) {
                return;
            }

            int cantidadComprar = Integer.parseInt(cantidadTexto);
            int stockDisponible = Integer.parseInt(vistaVentas.txtStockDisponible.getText());

            // Verificar que la cantidad a comprar no sea mayor al stock disponible
            if (cantidadComprar > stockDisponible) {
                JOptionPane.showMessageDialog(null, "Stock no disponible, seleccione menos unidades");
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
                        JOptionPane.showMessageDialog(null, "No se puede superar el stock disponible");
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
        //Buscar cliente
        if (e.getSource() == vistaVentas.btnBuscarCliente) {
            String buscarCliente = vistaVentas.txtBuscarCliente.getText();

            if (buscarCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese DNI del cliente");
            }

            if (!validarCamposNumericos(buscarCliente)) {
                return;
            }

            int dniCliente = Integer.parseInt(buscarCliente);

            try {
                cliente = clientesDAO.buscarCliente(dniCliente);

                if (cliente.getDni() != 0) {
                    vistaVentas.txtNombreCliente.setText(cliente.getNombre());
                } else {
                    JOptionPane.showMessageDialog(null, "No existe cliente");
                    vistaVentas.txtNombreCliente.setText(null);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Registrar Factura
        if (e.getSource() == vistaVentas.btnGenerarVenta) {
            String buscarClienteDNI = vistaVentas.txtBuscarCliente.getText();
            String nombreVendedor = vistaVentas.txtNombreVendedor.getText();
            String cambio = vistaVentas.txtCambio.getText();

            if (!buscarClienteDNI.isEmpty() && !nombreVendedor.isEmpty() && !cambio.isEmpty()) {

                if (!validarCamposNumericos(buscarClienteDNI)) {
                    return;
                }

                int idCliente = 0;
                int dniCliente = Integer.parseInt(buscarClienteDNI);

                try {
                    cliente = clientesDAO.buscarCliente(dniCliente);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    return;
                }

                // Verificar si el cliente existe
                if (cliente == null || cliente.getIdCliente() == 0) {
                    // El cliente no está registrado, registrar el cliente
                    Cliente nuevoCliente = new Cliente();

                    // Capturar el tipo de documento del cliente
                    String numeroDocCliente = vistaVentas.txtBuscarCliente.getText();
                    String compararDocCliente = vistaVentas.cbTipoDocumento.getSelectedItem().toString();
                    
                    // Creacion de un nuevo flujo para filtrar el tipo de documento
                    String tipoDocCliente = Stream.of(numeroDocCliente)
                            .filter(doc -> doc.startsWith("10") && doc.length() == 10)
                            .map(doc -> "RUC")
                            .findFirst() // Obtenemos el primer resultado o un Optional vacío
                            .orElseGet(() -> numeroDocCliente.length() == 8 ? "DNI" : "");

                    tipoDocCliente = compararDocCliente.equals(tipoDocCliente) ? compararDocCliente : "";

                    if (tipoDocCliente.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El documento ingresado no coincide con el tipo de documento");
                        return;
                    }

                    nuevoCliente.setTipoDocumento(tipoDocCliente);
                    nuevoCliente.setDni(dniCliente);
                    clientesDAO.registrarCliente(nuevoCliente);
                    idCliente = clientesDAO.obtenerUltimoIdCliente();  // Obtener el id del cliente registrado o existente
                } else {
                    idCliente = cliente.getIdCliente();
                }

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
                // Registrar factura
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

                // Imprimir Venta PDF
                PDF pdf = new PDF(vistaVentas, ventasDAO);
                pdf.iniciarPDF();

                JOptionPane.showMessageDialog(null, "Venta Realizada");
            } else {
                JOptionPane.showMessageDialog(null, "Completar los datos");
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        // Abrir detalle factura PDF
        if (e.getSource() == vistaVentas.btnFacturaPDF) {
            String idFactura = vistaVentas.txtIdFacturaPDF.getText();

            //Verificar que el campo no este vacio
            if (idFactura.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una factura");
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
                JOptionPane.showMessageDialog(null, "Ingrese el importe");
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
                JOptionPane.showMessageDialog(null, "El importe no puede ser menor a el total");
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
                JOptionPane.showMessageDialog(null, "Selecciona una factura");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas anular la factura", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (ventasDAO.anularFactura(idFactura)) {
                    vistaVentas.txtIdFacturaPDF.setText(null);
                    limpiarTable();
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
            limpiarTable();
            vistaVentas.txtBuscarCliente.setText(null);
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
            vistaVentas.txtBuscarCliente.setText(null);
            vistaVentas.txtNombreCliente.setText(null);
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

        if (e.getSource() == vistaVentas.TableListadoVentas) {
            int fila = vistaVentas.TableListadoVentas.rowAtPoint(e.getPoint());
            vistaVentas.txtIdFacturaPDF.setText(vistaVentas.TableListadoVentas.getValueAt(fila, 0).toString());
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
    public void limpiarTable() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

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
            obj[1] = lista.get(i).getUsuario().getDniUsuario();
            obj[2] = lista.get(i).getFecha();
            obj[3] = lista.get(i).getCliente().getDni();
            obj[4] = lista.get(i).getTotal();
            modeloVentas.addRow(obj);
        }
        vistaVentas.TableListadoVentas.setModel(modeloVentas);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void calcularCambio() {
        double importe = Double.parseDouble(vistaVentas.txtImporte.getText());
        double cambio = importe - totalPagoFactura;

        vistaVentas.txtCambio.setText(String.valueOf(cambio));
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public boolean validarCamposNumericos(String... valores) {
        for (String valor : valores) {
            try {
                Long.parseLong(valor);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres");
                return false;
            }
        }
        return true;
    }

    public boolean esDouble(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres");
            return false;
        }
    }

}
