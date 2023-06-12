package pdf;

import DAO.VentasDAO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
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

public class PDFFactura {

    frmVentas vistaVentas;
    VentasDAO ventasDAO;

    public PDFFactura(frmVentas vistaVentas, VentasDAO ventasDAO) {
        this.vistaVentas = vistaVentas;
        this.ventasDAO = ventasDAO;
    }

    public void iniciarPDF() {
        try {
            //Datos de la factura
            String idVenta = ventasDAO.obtenerUltimoIdFactura();
            String ruc = "10343953671";
            String nombreEmpresa = "Minimarket Los Andes";
            String direccion = "Av. Leoncio Prado #165";

            FileOutputStream archivo;
            File file = new File("src/pdf/" + idVenta + ".pdf");
            archivo = new FileOutputStream(file);

            Document doc = new Document(new Rectangle(600, 400));
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            // Tabla para imagen y datos de la empresa
            PdfPTable tablaEmpresa = new PdfPTable(4);
            tablaEmpresa.setWidthPercentage(100);
            tablaEmpresa.setWidths(new float[]{1f, 1f, 1f, 1f});

            // Celda de la imagen
            PdfPCell celdaImagen = new PdfPCell();
            Image img = Image.getInstance("src/img/minimarket-los-andes.jpeg");
            img.scaleToFit(100, 100);
            celdaImagen.addElement(img);
            celdaImagen.setBorder(Rectangle.NO_BORDER);
            celdaImagen.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaImagen.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaEmpresa.addCell(celdaImagen);
            
            // Celdas vacías para mantener la alineación
            for (int i = 0; i < 2; i++) {
                PdfPCell celdaVacia = new PdfPCell();
                celdaVacia.setBorder(Rectangle.NO_BORDER);
                tablaEmpresa.addCell(celdaVacia);
            }

            // Celda para los datos de la empresa
            PdfPCell celdaDatosEmpresa = new PdfPCell();
            celdaDatosEmpresa.setBorder(Rectangle.BOX);
            celdaDatosEmpresa.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaDatosEmpresa.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph datosEmpresa = new Paragraph();
            datosEmpresa.setAlignment(Element.ALIGN_CENTER);
            datosEmpresa.add(new Chunk(nombreEmpresa+"\n", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
            datosEmpresa.add(new Chunk("RUC : "+ruc+"\n"+direccion, FontFactory.getFont(FontFactory.HELVETICA, 10)));
            celdaDatosEmpresa.addElement(datosEmpresa);
            tablaEmpresa.addCell(celdaDatosEmpresa);

            doc.add(tablaEmpresa);

            // Salto de línea
            doc.add(Chunk.NEWLINE);

            // Tabla para el encabezado de la factura
            PdfPTable tablaEncabezado = new PdfPTable(4);
            tablaEncabezado.setWidthPercentage(100);
            tablaEncabezado.setWidths(new float[]{1f, 1f, 1f, 1f});

            // Celda de la factura
            PdfPCell celdaFactura = new PdfPCell();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(date);
            // Documento del cliente
            String docCliente = vistaVentas.txtNumeroDocumentoCliente.getText();
            celdaFactura.setBorder(Rectangle.NO_BORDER);
            celdaFactura.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaFactura.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaFactura.addElement(new Paragraph("Factura: " + idVenta+"\nFecha : "+formattedDate+"\nCliente : "+docCliente));
            tablaEncabezado.addCell(celdaFactura);

            // Celdas vacías para mantener la alineación
            for (int i = 0; i < 3; i++) {
                PdfPCell celdaVacia = new PdfPCell();
                celdaVacia.setBorder(Rectangle.NO_BORDER);
                tablaEncabezado.addCell(celdaVacia);
            }

            doc.add(tablaEncabezado);

            // Salto de línea
            doc.add(Chunk.NEWLINE);

            
            //---------------------  Productos ---------------------------
            PdfPTable tablaProductos = new PdfPTable(4);
            tablaProductos.setWidthPercentage(100);
            tablaProductos.setWidths(new float[]{20f, 50f, 10f, 20f});
            tablaProductos.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell producto1 = new PdfPCell(new Phrase("Código"));
            PdfPCell producto2 = new PdfPCell(new Phrase("Descripción"));
            PdfPCell producto3 = new PdfPCell(new Phrase("Cantidad"));
            PdfPCell producto4 = new PdfPCell(new Phrase("Precio"));
            producto1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            producto2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            producto3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            producto4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            producto1.setHorizontalAlignment(Element.ALIGN_CENTER);
            producto2.setHorizontalAlignment(Element.ALIGN_CENTER);
            producto3.setHorizontalAlignment(Element.ALIGN_CENTER);
            producto4.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaProductos.addCell(producto1);
            tablaProductos.addCell(producto2);
            tablaProductos.addCell(producto3);
            tablaProductos.addCell(producto4);

            for (int i = 0; i < vistaVentas.TableNuevaVenta.getRowCount(); i++) {
                String codigoProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 0).toString();
                String nombreProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 1).toString();
                String CantidadProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 2).toString();
                String precioProducto = vistaVentas.TableNuevaVenta.getValueAt(i, 3).toString();
                
                PdfPCell cellCodigo = new PdfPCell(new Phrase(codigoProducto));
                PdfPCell cellNombre = new PdfPCell(new Phrase(nombreProducto));
                PdfPCell cellCantidad = new PdfPCell(new Phrase(CantidadProducto));
                PdfPCell cellPrecio = new PdfPCell(new Phrase(precioProducto));

                cellCodigo.setHorizontalAlignment(Element.ALIGN_CENTER); // Alineación centrada
                cellNombre.setHorizontalAlignment(Element.ALIGN_CENTER); 
                cellCantidad.setHorizontalAlignment(Element.ALIGN_CENTER); 
                cellPrecio.setHorizontalAlignment(Element.ALIGN_CENTER); 

                tablaProductos.addCell(cellCodigo);
                tablaProductos.addCell(cellNombre);
                tablaProductos.addCell(cellCantidad);
                tablaProductos.addCell(cellPrecio);
            }
            doc.add(tablaProductos);

            
            // ------------------- Datos de importes de Factura -------------------
            String subtotalPago = vistaVentas.txtSubtotal.getText();
            String igvPago = vistaVentas.txtIGV.getText();
            String totalPago = vistaVentas.txtTotalPagar.getText();
            
            Font fontDatosPago = new Font(FontFactory.getFont(FontFactory.HELVETICA, 10));
            Paragraph total = new Paragraph("Subtotal: S/"+subtotalPago+"\nIGV : S/"+igvPago+"\nTotal: "+totalPago, fontDatosPago);
            total.setAlignment(Element.ALIGN_RIGHT);
            doc.add(total);

            Paragraph agradecimiento = new Paragraph("Gracias por su compra!", fontDatosPago);
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            doc.add(agradecimiento);

            doc.close();
            archivo.close();

            //Abrir pdf automáticamente
            Desktop.getDesktop().open(file);

        } catch (DocumentException | IOException e) {
            System.out.println(e);
        }
    }

}
