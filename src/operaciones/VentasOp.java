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

}
