
package views.modal;

import DAO.ProductosDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Producto;
import views.frmVentas;


public class modalAgregarProducto extends javax.swing.JDialog {
    
    DefaultTableModel modeloProductos = new DefaultTableModel();
    frmVentas vistaVentas;
    ProductosDAO productosDAO = new ProductosDAO();
    

    public modalAgregarProducto(java.awt.Frame parent, boolean modal, frmVentas vistaVentas) {
        super(parent, modal);
        this.vistaVentas = vistaVentas;
        initComponents();
        this.setTitle("Listado de productos");
        this.setLocationRelativeTo(parent);
        listarProductos();
        configurarDocumentListener();
    }
    
    public void listarProductos(){
        ArrayList<Producto> lista = productosDAO.listarProductos();
        modeloProductos = (DefaultTableModel) this.TableBuscarProductoModal.getModel();
        Object[] obj = new Object[4];
        for(int i=0; i < lista.size(); i++){
            obj[0] = lista.get(i).getIdProducto();
            obj[1] = lista.get(i).getNombreProducto();
            obj[2] = lista.get(i).getPrecio();
            obj[3] = lista.get(i).getCantidad();
            modeloProductos.addRow(obj);
        }
        this.TableBuscarProductoModal.setModel(modeloProductos);
        
        // Establecer las celdas como no editables
        this.TableBuscarProductoModal.setDefaultEditor(Object.class, null);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtModalBuscarProducto = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableBuscarProductoModal = new javax.swing.JTable();
        btnModalAgregarProducto = new javax.swing.JButton();
        btnModalCancelarProducto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Buscar producto :");

        TableBuscarProductoModal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Producto", "Precio", "Stock"
            }
        ));
        jScrollPane1.setViewportView(TableBuscarProductoModal);

        btnModalAgregarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/insertar.png"))); // NOI18N
        btnModalAgregarProducto.setText("Agregar");
        btnModalAgregarProducto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalAgregarProducto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModalAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModalAgregarProductoActionPerformed(evt);
            }
        });

        btnModalCancelarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/eliminar.png"))); // NOI18N
        btnModalCancelarProducto.setText("Cancelar");
        btnModalCancelarProducto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalCancelarProducto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModalCancelarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModalCancelarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtModalBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(208, 208, 208)
                        .addComponent(btnModalAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(btnModalCancelarProducto)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModalBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnModalAgregarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                    .addComponent(btnModalCancelarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModalAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModalAgregarProductoActionPerformed
        
        int filaSeleccionada = this.TableBuscarProductoModal.getSelectedRow();
        
        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(null, "Seleccione un producto");
        }else{
            modeloProductos = (DefaultTableModel) this.TableBuscarProductoModal.getModel();
            String codigo = this.TableBuscarProductoModal.getValueAt(filaSeleccionada, 0).toString();
            String nombre = this.TableBuscarProductoModal.getValueAt(filaSeleccionada, 1).toString();
            String precio = this.TableBuscarProductoModal.getValueAt(filaSeleccionada, 2).toString();
            String stock = this.TableBuscarProductoModal.getValueAt(filaSeleccionada, 3).toString();
            
            //Enviar los datos al formulario Ventas
            vistaVentas.txtBuscarProducto.setText(codigo);
            vistaVentas.txtNombreProducto.setText(nombre);
            vistaVentas.txtPrecio.setText(precio);
            vistaVentas.txtStockDisponible.setText(stock);
            this.setVisible(false);
        }
        
    }//GEN-LAST:event_btnModalAgregarProductoActionPerformed

    private void btnModalCancelarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModalCancelarProductoActionPerformed
        
        this.setVisible(false);
        
    }//GEN-LAST:event_btnModalCancelarProductoActionPerformed

    private void txtBusquedaFiltradoProductos(javax.swing.event.DocumentEvent evt){
        // Obtener el texto actual del campo de texto
        String textoBusqueda = txtModalBuscarProducto.getText();
        
        // Realizar la búsqueda en la base de datos utilizando el texto de búsqueda
        ArrayList<Producto> productosFiltrados = productosDAO.listarProductosFiltrado(textoBusqueda);
        
        // Actualizar el modelo de la tabla con los usuarios filtrados
        modeloProductos.setRowCount(0); // Limpiar el modelo actual
        Object[] obj = new Object[4];
        for (Producto producto : productosFiltrados) {
            obj[0] = producto.getIdProducto();
            obj[1] = producto.getNombreProducto();
            obj[2] = producto.getPrecio();
            obj[3] = producto.getCantidad();
            modeloProductos.addRow(obj);
        }
        
        // Actualizar la tabla con el nuevo modelo
        TableBuscarProductoModal.setModel(modeloProductos);  
    }
    
    private void configurarDocumentListener() {
        txtModalBuscarProducto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoProductos(evt);
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoProductos(evt);
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoProductos(evt);
            }
        });
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(modalAgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(modalAgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(modalAgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(modalAgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                modalAgregarProducto dialog = new modalAgregarProducto(new javax.swing.JFrame(), true, frmVentas.getInstancia());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableBuscarProductoModal;
    private javax.swing.JButton btnModalAgregarProducto;
    private javax.swing.JButton btnModalCancelarProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtModalBuscarProducto;
    // End of variables declaration//GEN-END:variables
}
