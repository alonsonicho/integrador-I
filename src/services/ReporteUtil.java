package services;

import DAO.VentasDAO;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import views.frmVentas;

public class ReporteUtil {

    VentasDAO ventasDAO = new VentasDAO();
    frmVentas vistaReportes;
    DefaultTableModel modeloVentas = new DefaultTableModel();
    DefaultTableModel modeloDetalleVenta = new DefaultTableModel();

    public ReporteUtil(frmVentas vistaReportes) {
        this.vistaReportes = vistaReportes;
        cargarListaFactura();
        mostrarDetalleRegistrosActuales();
    }

    public void anularVenta() {
        String idFactura = vistaReportes.txtIdFacturaPDF.getText();

        //Verificar que el campo no este vacio
        if (idFactura.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecciona una factura", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas anular la venta", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            if (ventasDAO.anularFactura(idFactura)) {
                vistaReportes.txtIdFacturaPDF.setText(null);
                cargarListaFactura();
                JOptionPane.showMessageDialog(null, "Factura anulada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
            }
        }
    }

    public void imprimirVenta() {
        String idFactura = vistaReportes.txtIdFacturaPDF.getText();

        //Verificar que el campo no este vacio
        if (idFactura.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecciona una venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File file = new File("src/pdf/" + idFactura + ".pdf");
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void cargarListaFactura() {
        ArrayList<Factura> lista = ventasDAO.listarFacturas();
        modeloVentas = (DefaultTableModel) vistaReportes.TableListadoVentas.getModel();
        modeloVentas.setRowCount(0);
        Object[] obj = new Object[7];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getCodigo();
            obj[1] = lista.get(i).getTipoDocumentoVenta();
            obj[2] = lista.get(i).getTipoPago();
            obj[3] = lista.get(i).getUsuario().getNombre();
            obj[4] = lista.get(i).getFecha();
            obj[5] = lista.get(i).getCliente().getNombre();
            obj[6] = lista.get(i).getTotal();
            modeloVentas.addRow(obj);
        }
        vistaReportes.TableListadoVentas.setModel(modeloVentas); //modeloVentas
        //Obtener el numero de filas de la tabla
        int numeroRegistros = vistaReportes.TableListadoVentas.getRowCount();
        vistaReportes.labelNumeroRegistros.setText("Registros encontrados :" + numeroRegistros);
    }

    public void mostrarDetalleRegistrosActuales() {
        ArrayList<Factura> listadoFacturas = ventasDAO.listarFacturas();

        double totalVentas = 0;
        int numeroVentas = listadoFacturas.size();

        for (Factura factura : listadoFacturas) {
            totalVentas += factura.getTotal();
        }

        vistaReportes.labelTotalIngresado.setText(String.format("%.2f", totalVentas));
        vistaReportes.labelTotalVentas.setText(String.valueOf(numeroVentas));
    }

    public void mostrarDetallePorFiltros() {
        modeloDetalleVenta = (DefaultTableModel) vistaReportes.TableListadoVentas.getModel();
        int rowCount = modeloDetalleVenta.getRowCount();

        double sumaTotalFiltrado = 0.0;
        double totalSumaEfectivo = 0.0;
        double totalSumaTarjeta = 0.0;

        for (int i = 0; i < rowCount; i++) {
            String tipoPago = (String) modeloDetalleVenta.getValueAt(i, 2);
            double totalVenta = (double) modeloDetalleVenta.getValueAt(i, 6);

            sumaTotalFiltrado += totalVenta;
            if (tipoPago.equalsIgnoreCase("EFECTIVO")) {
                totalSumaEfectivo += totalVenta;
            } else if (tipoPago.equalsIgnoreCase("TARJETA")) {
                totalSumaTarjeta += totalVenta;
            }
        }

        vistaReportes.labelTotalFiltrado.setText(String.valueOf(sumaTotalFiltrado));
        vistaReportes.labelTotalSumaEfectivoFiltrado.setText(String.valueOf(totalSumaEfectivo));
        vistaReportes.labelTotalSumaTarjetaFiltrado.setText(String.valueOf(totalSumaTarjeta));
    }

    public void filtrarVentasPorFecha() {
        // Obtener la fecha de inicio y fecha fin seleccionadas del JDateChooser
        Date fechaInicio = vistaReportes.fechaDesde.getDate();
        Date fechaFin = vistaReportes.fechaHasta.getDate();
        String tipoPago = vistaReportes.cbReporteTipoPago.getSelectedItem().toString();
        String tipoDocumentoVenta = vistaReportes.cbReporteTipoDocumentoVenta.getSelectedItem().toString();
        //Llamado a la funcion para filtrar
        if (fechaInicio != null && fechaFin != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strFechaInicio = dateFormat.format(fechaInicio);
            String strFechaFin = dateFormat.format(fechaFin);

            ArrayList<Factura> ventasFiltradas = ventasDAO.obtenerVentasPorFiltros(tipoPago, tipoDocumentoVenta, strFechaInicio, strFechaFin);
            modeloVentas = (DefaultTableModel) vistaReportes.TableListadoVentas.getModel();
            modeloVentas.setRowCount(0);
            for (Factura venta : ventasFiltradas) {
                Object[] fila = new Object[]{
                    venta.getCodigo(),
                    venta.getTipoDocumentoVenta(),
                    venta.getTipoPago(),
                    venta.getUsuario().getNombre(),
                    venta.getFecha(),
                    venta.getCliente().getNombre(),
                    venta.getTotal()
                };
                modeloVentas.addRow(fila);
            }
            
            mostrarDetallePorFiltros();

            vistaReportes.TableListadoVentas.setModel(modeloVentas);

            int numeroRegistros = vistaReportes.TableListadoVentas.getRowCount();
            vistaReportes.labelNumeroRegistros.setText("Registros encontrados: " + numeroRegistros);
        }
    }

    public void mostrarDetallePorVenta(MouseEvent e) {
        int fila = vistaReportes.TableListadoVentas.rowAtPoint(e.getPoint());
        vistaReportes.txtIdFacturaPDF.setText(vistaReportes.TableListadoVentas.getValueAt(fila, 0).toString());

        String codigoFactura = vistaReportes.TableListadoVentas.getValueAt(fila, 0).toString();
        // Obtener el detalle de la factura según la venta seleccionada
        ArrayList<DetalleFactura> detalleFactura = ventasDAO.obtenerDetalleFacturaVenta(codigoFactura);

        modeloDetalleVenta = (DefaultTableModel) vistaReportes.TableDetalleVenta.getModel();
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
        vistaReportes.TableDetalleVenta.setModel(modeloDetalleVenta);
    }

}
