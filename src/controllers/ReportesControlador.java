
package controllers;

import DAO.VentasDAO;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.DetalleFactura;
import operaciones.ReportesOp;
import pdf.PDFFactura;
import views.frmVentas;


public class ReportesControlador implements ActionListener, MouseListener, PropertyChangeListener {
    
    PDFFactura pdf;
    VentasDAO ventasDAO = new VentasDAO();
    frmVentas vistaReportes;
    DefaultTableModel modeloVentas = new DefaultTableModel();
    DefaultTableModel modeloDetalleVenta = new DefaultTableModel();
    ReportesOp reportesOp = new ReportesOp(ventasDAO);

    public ReportesControlador(PDFFactura pdf, frmVentas vistaReportes) {
        this.pdf = pdf;
        this.vistaReportes = vistaReportes;
        this.vistaReportes.btnAnularFactura.addActionListener(this);
        this.vistaReportes.btnFacturaPDF.addActionListener(this);
        this.vistaReportes.cbReporteTipoDocumentoVenta.addActionListener(this);
        this.vistaReportes.cbReporteTipoPago.addActionListener(this);
        this.vistaReportes.TableListadoVentas.addMouseListener(this);
        this.vistaReportes.fechaDesde.getDateEditor().addPropertyChangeListener(this);
        this.vistaReportes.fechaHasta.getDateEditor().addPropertyChangeListener(this);
        reportesOp.mostrarDetalleRegistrosActuales(vistaReportes.labelTotalIngresado, vistaReportes.labelTotalVentas);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Anular una venta
        if(e.getSource() == vistaReportes.btnAnularFactura){
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
                    reportesOp.cargarListaFactura(modeloVentas, vistaReportes.TableListadoVentas, vistaReportes.labelNumeroRegistros);
                    JOptionPane.showMessageDialog(null, "Factura anulada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }
        
        //Imprimir una venta
        if(e.getSource() == vistaReportes.btnFacturaPDF){
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
        
        if (e.getSource() == vistaReportes.cbReporteTipoPago) {
            filtrarVentas();
        }

        if (e.getSource() == vistaReportes.cbReporteTipoDocumentoVenta) {
            filtrarVentas();
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Completar los datos en la tabla por venta realizara #Reporte
        if (e.getSource() == vistaReportes.TableListadoVentas) {
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
    public void propertyChange(PropertyChangeEvent e) {
        if ("date".equals(e.getPropertyName())) {
            filtrarVentas();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void filtrarVentas() {
        // Obtener la fecha de inicio y fecha fin seleccionadas del JDateChooser
        Date fechaInicio = vistaReportes.fechaDesde.getDate();
        Date fechaFin = vistaReportes.fechaHasta.getDate();
        String tipoPago = vistaReportes.cbReporteTipoPago.getSelectedItem().toString();
        String tipoDocumentoVenta = vistaReportes.cbReporteTipoDocumentoVenta.getSelectedItem().toString();
        //Llamado a la funcion para filtrar
        reportesOp.filtrarVentasPorFecha(
                tipoPago, 
                tipoDocumentoVenta, 
                fechaInicio, 
                fechaFin, 
                modeloVentas, 
                vistaReportes.TableListadoVentas, 
                vistaReportes.labelNumeroRegistros);
        
        reportesOp.mostrarDetallePorFiltros(modeloVentas,
                vistaReportes.TableListadoVentas,
                vistaReportes.labelTotalFiltrado,
                vistaReportes.labelTotalSumaEfectivoFiltrado,
                vistaReportes.labelTotalSumaTarjetaFiltrado);
    }
    
}
