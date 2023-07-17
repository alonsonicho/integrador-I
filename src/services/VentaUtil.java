package services;

import DAO.*;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import pdf.*;
import util.Utilidades;
import views.frmVentas;

public class VentaUtil {

    Cliente cliente = new Cliente();
    Producto producto = new Producto();
    Factura factura = new Factura();
    ClientesDAO clientesDAO = new ClientesDAO();
    ProductosDAO productosDAO = new ProductosDAO();
    VentasDAO ventasDAO = new VentasDAO();
    DefaultTableModel modelo = new DefaultTableModel();
    frmVentas vistaVentas;
    ReporteUtil reporteUtil;

    public VentaUtil(frmVentas vistaVentas) {
        this.vistaVentas = vistaVentas;
        reporteUtil = new ReporteUtil(vistaVentas);
        cargarDatosVendedor();
    }

    public void buscarProducto() {
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

    public void registrarProductoTabla() {
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

        boolean camposSonNumeros = Utilidades.validarCamposEntero(cantidadTexto);
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

    public void eliminarProductoTabla() {
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

    public void registrarFactura() {
        String numeroDocumentoCliente = vistaVentas.txtNumeroDocumentoCliente.getText();
        double totalPagoFactura = Double.parseDouble(vistaVentas.txtTotalPagar.getText().replace(",", "."));

        // Validar los campos obligatorios
        if (!Utilidades.validarCamposVacios(numeroDocumentoCliente)) {
            return;
        }

        // Verificar que el doc cliente solo se ingresen numeros
        boolean camposSonNumeros = Utilidades.validarCamposEntero(numeroDocumentoCliente);
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

        boolean tipoDocumentoCorrecto = validarTipoVentaDocumento(tipoVenta, tipoDocCliente, numeroDocCliente);
        if (!tipoDocumentoCorrecto) {
            return;
        }

        int idCliente = 0;

        try {
            cliente = clientesDAO.buscarCliente(numeroDocumentoCliente);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        }

        // Verificar si el cliente existe para asignar el idCliente
        if (cliente == null || cliente.getIdCliente() == 0) {
            // El cliente no está registrado, se procede a registrar
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setTipoDocumento(tipoDocCliente);
            nuevoCliente.setNumeroDocumento(numeroDocumentoCliente);
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

        // Imprimir el PDF segun el Tipo de venta
        imprimirPDF();
        
        reporteUtil.cargarListaFactura();
        reporteUtil.mostrarDetalleRegistrosActuales();

        JOptionPane.showMessageDialog(null, "Venta Realizada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarDatosFactura();
        limpiarDatosProducto();
    }

    public void calcularCambio() {
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

        calcularCambioPago();
    }

    public void calcularTotalPagoFactura() {
        double subtotalFactura = 0;
        double igvFactura = 0;
        double totalPagoFactura = 0;
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

    public void calcularCambioPago() {
        double totalPagoFactura = Double.parseDouble(vistaVentas.txtTotalPagar.getText().replace(",", "."));
        double importe = Double.parseDouble(vistaVentas.txtImporte.getText());
        double cambio = importe - totalPagoFactura;

        vistaVentas.txtCambio.setText(String.format("%.2f", cambio));
    }

    public void cargarDatosVendedor() {
        Usuario usuarioActual = Session.getUsuarioActual();
        String nombreVendedor = (usuarioActual != null) ? usuarioActual.getNombre() : " -- Vendedor no registrado --";
        vistaVentas.txtNombreVendedor.setText(nombreVendedor);
    }

    public void imprimirPDF() {
        String tipoVenta = vistaVentas.cbTipoDocumentoVenta.getSelectedItem().toString();
        if (tipoVenta.equals("FACTURA")) {
            PDFFactura pdf = new PDFFactura(vistaVentas, this.ventasDAO);
            pdf.iniciarPDF();
        } else {
            PDFBoleta pdf = new PDFBoleta(vistaVentas, this.ventasDAO);
            pdf.iniciarPDF();
        }
    }

    public boolean validarTipoVentaDocumento(String tipoVenta, String tipoDocCliente, String numeroDocCliente) {
        switch (tipoVenta) {
            case "BOLETA":
                if (!tipoDocCliente.equals("DNI")) {
                    Utilidades.mostrarAdvertencia("El tipo de documento del cliente debe ser DNI para una venta de tipo boleta.");
                    return false;
                }
                if (numeroDocCliente.length() != 8) {
                    Utilidades.mostrarAdvertencia("El número de documento del cliente debe tener una longitud de 8 caracteres para una venta de tipo boleta.");
                    return false;
                }
                break;
            case "FACTURA":
                if (!tipoDocCliente.equals("RUC")) {
                    Utilidades.mostrarAdvertencia("El tipo de documento del cliente debe ser RUC para una venta de tipo factura.");
                    return false;
                }
                if (numeroDocCliente.length() != 11) {
                    Utilidades.mostrarAdvertencia("El número de documento del cliente debe tener una longitud de 11 caracteres para una venta de tipo factura.");
                    return false;
                }
                if (!numeroDocCliente.startsWith("10") && !numeroDocCliente.startsWith("20")) {
                    Utilidades.mostrarAdvertencia("El número de documento del cliente debe comenzar con '10' o '20' para una venta de tipo factura.");
                    return false;
                }
                break;
            default:
                Utilidades.mostrarAdvertencia("Tipo de venta no válido.");
                return false;
        }
        return true;
    }

    public boolean esDouble(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

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
