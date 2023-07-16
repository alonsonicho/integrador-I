
package controllers;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import services.ReporteUtil;
import views.frmVentas;


public class ReporteControlador implements ActionListener, MouseListener, PropertyChangeListener {
    
    frmVentas vistaReportes;
    ReporteUtil reporteUtil;

    public ReporteControlador(frmVentas vistaReportes) {
        this.vistaReportes = vistaReportes;
        this.vistaReportes.btnAnularFactura.addActionListener(this);
        this.vistaReportes.btnFacturaPDF.addActionListener(this);
        this.vistaReportes.cbReporteTipoDocumentoVenta.addActionListener(this);
        this.vistaReportes.cbReporteTipoPago.addActionListener(this);
        this.vistaReportes.TableListadoVentas.addMouseListener(this);
        this.vistaReportes.fechaDesde.getDateEditor().addPropertyChangeListener(this);
        this.vistaReportes.fechaHasta.getDateEditor().addPropertyChangeListener(this);
        reporteUtil = new ReporteUtil(vistaReportes);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
       // Anular una venta
        if(e.getSource() == vistaReportes.btnAnularFactura){
            reporteUtil.anularVenta();
        } 
        
        //Imprimir una venta
        if(e.getSource() == vistaReportes.btnFacturaPDF){
            reporteUtil.imprimirVenta();
        }
        
        if (e.getSource() == vistaReportes.cbReporteTipoPago) {
            reporteUtil.filtrarVentasPorFecha();
        }

        if (e.getSource() == vistaReportes.cbReporteTipoDocumentoVenta) {
            reporteUtil.filtrarVentasPorFecha();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        // Completar los datos en la tabla por venta realizara #Reporte
        if (e.getSource() == vistaReportes.TableListadoVentas) {
            reporteUtil.mostrarDetallePorVenta(e);
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
    public void propertyChange(PropertyChangeEvent evt) {
    }
    
    
    
}
