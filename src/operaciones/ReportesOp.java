package operaciones;

import DAO.VentasDAO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.Factura;

public class ReportesOp {

    VentasDAO ventasDAO;

    public ReportesOp(VentasDAO ventasDAO) {
        this.ventasDAO = ventasDAO;
    }

    public void filtrarVentasPorFecha(String tipoPago, String tipoDocumentoVenta, Date fechaInicio, Date fechaFin, DefaultTableModel modeloVentas, JTable tablaVentas, JLabel labelNumeroRegistros) {
        if (fechaInicio != null && fechaFin != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strFechaInicio = dateFormat.format(fechaInicio);
            String strFechaFin = dateFormat.format(fechaFin);

            ArrayList<Factura> ventasFiltradas = ventasDAO.obtenerVentasPorFiltros(tipoPago, tipoDocumentoVenta, strFechaInicio, strFechaFin);
            modeloVentas = (DefaultTableModel) tablaVentas.getModel();
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

            tablaVentas.setModel(modeloVentas);

            int numeroRegistros = tablaVentas.getRowCount();
            labelNumeroRegistros.setText("Registros encontrados: " + numeroRegistros);
        }
    }
    
    public void cargarListaFactura(DefaultTableModel modelo, JTable tabla, JLabel label) {
        ArrayList<Factura> lista = this.ventasDAO.listarFacturas();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        Object[] obj = new Object[7];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getCodigo();
            obj[1] = lista.get(i).getTipoDocumentoVenta();
            obj[2] = lista.get(i).getTipoPago();
            obj[3] = lista.get(i).getUsuario().getNombre();
            obj[4] = lista.get(i).getFecha();
            obj[5] = lista.get(i).getCliente().getNombre();
            obj[6] = lista.get(i).getTotal();
            modelo.addRow(obj);
        }
        tabla.setModel(modelo); //modeloVentas
        //Obtener el numero de filas de la tabla
        int numeroRegistros = tabla.getRowCount();
        label.setText("Registros encontrados :" + numeroRegistros);
    }
    
    public void mostrarDetalleRegistrosActuales(JLabel labelTotalIngresado, JLabel numeroTotalVentas){
        ArrayList<Factura> listadoFacturas = ventasDAO.listarFacturas();
        
        double totalVentas = 0;
        int numeroVentas = listadoFacturas.size();
        
        for(Factura factura: listadoFacturas){
            totalVentas += factura.getTotal();
        }
        
        labelTotalIngresado.setText(String.format("%.2f", totalVentas));
        numeroTotalVentas.setText(String.valueOf(numeroVentas));
    }
    
    public void mostrarDetallePorFiltros(
            DefaultTableModel modelo, 
            JTable tabla,
            JLabel labelSumaTotalFiltrado, 
            JLabel labelTotalSumaEfectivo,
            JLabel labelTotalSumaTarjeta){
        modelo = (DefaultTableModel) tabla.getModel();
        int rowCount = modelo.getRowCount();

        double sumaTotalFiltrado = 0.0;
        double totalSumaEfectivo = 0.0;
        double totalSumaTarjeta = 0.0;
        
        for(int i=0; i<rowCount; i++){
            String tipoPago = (String) modelo.getValueAt(i, 2);
            double totalVenta = (double) modelo.getValueAt(i, 6);
            
            sumaTotalFiltrado += totalVenta;
            if(tipoPago.equalsIgnoreCase("EFECTIVO")){
                totalSumaEfectivo += totalVenta;
            }else if(tipoPago.equalsIgnoreCase("TARJETA")){
                totalSumaTarjeta += totalVenta;
            }
        }
        
        labelSumaTotalFiltrado.setText(String.valueOf(sumaTotalFiltrado));
        labelTotalSumaEfectivo.setText(String.valueOf(totalSumaEfectivo));
        labelTotalSumaTarjeta.setText(String.valueOf(totalSumaTarjeta));
        
    }
}
