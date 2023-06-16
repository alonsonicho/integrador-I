package operaciones;

import DAO.VentasDAO;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import models.Session;
import models.Usuario;
import pdf.PDFBoleta;
import pdf.PDFFactura;
import views.frmVentas;

public class VentasOp {

    VentasDAO ventasDAO;
    frmVentas vistaVentas;

    public VentasOp(VentasDAO ventasDAO) {
        this.ventasDAO = ventasDAO;
    }

    public boolean validarTipoVentaDocumento(String tipoVenta, String tipoDocCliente, String numeroDocCliente) {
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

    public void imprimirPDF(frmVentas vistaVentas, String tipoVenta) {
        if (tipoVenta.equals("FACTURA")) {
            PDFFactura pdf = new PDFFactura(vistaVentas, this.ventasDAO);
            pdf.iniciarPDF();
        } else {
            PDFBoleta pdf = new PDFBoleta(vistaVentas, this.ventasDAO);
            pdf.iniciarPDF();
        }
    }

    public void cargarDatosVendedor(JTextField textField) {
        Usuario usuarioActual = Session.getUsuarioActual();
        String nombreVendedor = (usuarioActual != null) ? usuarioActual.getNombre() : " -- Vendedor no registrado --";
        textField.setText(nombreVendedor);
    }

    public void calcularTotalPagoFactura(JTable table, JTextField txtSubtotal, JTextField txtIGV, JTextField txtTotalPagar) {
        double subtotalFactura = 0;
        double igvFactura = 0;
        double totalPagoFactura = 0;
        int numeroFilas = table.getRowCount();
        for (int i = 0; i < numeroFilas; i++) {
            double totalPorProducto = (double) table.getModel().getValueAt(i, 4);
            subtotalFactura += totalPorProducto;
        }

        igvFactura = subtotalFactura * 0.18;
        totalPagoFactura = subtotalFactura + igvFactura;

        txtSubtotal.setText(String.format("%.2f", subtotalFactura));
        txtIGV.setText(String.format("%.2f", igvFactura));
        txtTotalPagar.setText(String.format("%.2f", totalPagoFactura));
    }

    public void calcularCambio(JTextField txtTotalPagar, JTextField txtImporte, JTextField txtCambio) {
        double totalPagoFactura = Double.parseDouble(txtTotalPagar.getText().replace(",", "."));
        double importe = Double.parseDouble(txtImporte.getText());
        double cambio = importe - totalPagoFactura;

        //txtCambio.setText(String.valueOf(cambio));
        txtCambio.setText(String.format("%.2f", cambio));
    }

    public boolean esDouble(String valor){
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }
}
