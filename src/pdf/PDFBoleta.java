package pdf;

import DAO.VentasDAO;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import views.frmVentas;

public class PDFBoleta {

    frmVentas vistaVentas;
    VentasDAO ventasDAO;

    public PDFBoleta(frmVentas vistaVentas, VentasDAO ventasDAO) {
        this.vistaVentas = vistaVentas;
        this.ventasDAO = ventasDAO;
    }

    public void iniciarPDF() {
        try {
            
            //String idFactura = ventasDAO.obtenerUltimoIdFactura();
            String ruc = "20343953671";
            String nombreEmpresa = "Minimarket Los Andes";
            String direccionEmpresa = "Av. Leoncio Prado #165";

            FileOutputStream archivo;
            File file = new File("src/pdf/demo.pdf");
            archivo = new FileOutputStream(file);
            
            Document document = new Document(PageSize.A6);
            PdfWriter.getInstance(document, archivo);
            document.open();
            
            //---------------------------------------------------------------------//   
            // -- Celda de la imagen
            Image img = Image.getInstance("src/img/minimarket-los-andes.jpeg");
            img.scaleToFit(100, 100);
            img.setAlignment(Element.ALIGN_CENTER);
            document.add(img);
            // Salto de línea
            document.add(Chunk.NEWLINE);

            //---------------------------------------------------------------------// 
            // Información de la empresa
            Paragraph empresa = new Paragraph(nombreEmpresa, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
            empresa.setAlignment(Element.ALIGN_CENTER);
            document.add(empresa);
            
            Paragraph rucEmpresa = new Paragraph("RUC :"+ruc, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
            rucEmpresa.setAlignment(Element.ALIGN_CENTER);
            document.add(rucEmpresa);

            Paragraph direccion = new Paragraph(direccionEmpresa, new Font(Font.FontFamily.HELVETICA, 8));
            direccion.setAlignment(Element.ALIGN_CENTER);
            document.add(direccion);

            // Separación
            document.add(Chunk.NEWLINE);

            //---------------------------------------------------------------------//
            //Informacion del ticket
            Paragraph numeroTicket = new Paragraph("N° Ticket: F0001", new Font(Font.FontFamily.HELVETICA, 9));
            numeroTicket.setAlignment(Element.ALIGN_LEFT);
            document.add(numeroTicket);
            
            // Información del cliente
            String capturarCliente = vistaVentas.txtNombreCliente.getText();
            String nombreCliente = (!capturarCliente.isEmpty()) ? capturarCliente : " --------- ";
            Paragraph cliente = new Paragraph("Cliente: "+nombreCliente, new Font(Font.FontFamily.HELVETICA, 9));
            cliente.setAlignment(Element.ALIGN_LEFT);
            document.add(cliente);
            
            // Documento del cliente
            String docCliente = vistaVentas.txtBuscarCliente.getText();
            Paragraph documentoCliente = new Paragraph("N° documento: "+docCliente, new Font(Font.FontFamily.HELVETICA, 9));
            documentoCliente.setAlignment(Element.ALIGN_LEFT);
            document.add(documentoCliente);
            
            //Informacion de la fecha
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(date);
            Paragraph fecha = new Paragraph("Fecha: "+formattedDate, new Font(Font.FontFamily.HELVETICA, 9));
            fecha.setAlignment(Element.ALIGN_LEFT);
            document.add(fecha);

            // Separación
            document.add(Chunk.NEWLINE);
            
            //---------------------------------------------------------------------//

            // Detalles de la boleta
            PdfPTable tablaDetalle = new PdfPTable(3);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setWidths(new float[]{1f, 3f, 1.5f});

            PdfPCell celdaItem = new PdfPCell(new Phrase("Unid", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
            celdaItem.setBorder(Rectangle.BOTTOM);
            celdaItem.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaDetalle.addCell(celdaItem);

            PdfPCell celdaDescripcion = new PdfPCell(new Phrase("Producto", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
            celdaDescripcion.setBorder(Rectangle.BOTTOM);
            celdaDescripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaDetalle.addCell(celdaDescripcion);

            PdfPCell celdaPrecio = new PdfPCell(new Phrase("Precio", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
            celdaPrecio.setBorder(Rectangle.BOTTOM);
            celdaPrecio.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaDetalle.addCell(celdaPrecio);

            // Agregar filas de productos
            for (int i = 0; i < vistaVentas.TableNuevaVenta.getRowCount(); i++) {
                
                String nombreProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 1).toString();
                String CantidadProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 2).toString();
                String precioProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 3).toString();
                
                PdfPCell celdaNumItem = new PdfPCell(new Phrase(CantidadProducto, new Font(Font.FontFamily.HELVETICA, 9)));
                celdaNumItem.setBorder(Rectangle.NO_BORDER);
                celdaNumItem.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDetalle.addCell(celdaNumItem);

                PdfPCell celdaDescProducto = new PdfPCell(new Phrase(nombreProducto, new Font(Font.FontFamily.HELVETICA, 9)));
                celdaDescProducto.setBorder(Rectangle.NO_BORDER);
                celdaDescProducto.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDetalle.addCell(celdaDescProducto);

                PdfPCell celdaPrecioProducto = new PdfPCell(new Phrase("S/"+precioProducto, new Font(Font.FontFamily.HELVETICA, 9)));
                celdaPrecioProducto.setBorder(Rectangle.NO_BORDER);
                celdaPrecioProducto.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDetalle.addCell(celdaPrecioProducto);
            }
            document.add(tablaDetalle);

            // Separación
            document.add(Chunk.NEWLINE);
            
            //---------------------------------------------------------------------//
            // Total de la boleta
            String subtotalPago = vistaVentas.txtSubtotal.getText();
            String igvPago = vistaVentas.txtIGV.getText();
            String totalPago = vistaVentas.txtTotalPagar.getText();
            
            Paragraph subTotal = new Paragraph("Subtotal: S/"+subtotalPago, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD));
            subTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(subTotal);
            
            Paragraph igvTotal = new Paragraph("IGV: S/"+igvPago, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD));
            igvTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(igvTotal);
            
            Paragraph total = new Paragraph("Total: S/"+totalPago, new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            archivo.close();
            
            //Abrir pdf automáticamente
            Desktop.getDesktop().open(file);

        } catch (DocumentException | IOException e) {
            System.out.println(e);
        }
    }

}
