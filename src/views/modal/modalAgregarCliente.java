
package views.modal;

import DAO.ClientesDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Cliente;
import views.frmVentas;


public class modalAgregarCliente extends javax.swing.JDialog {

    DefaultTableModel modeloClientes = new DefaultTableModel();
    frmVentas vistaVentas;
    ClientesDAO clientesDAO = new ClientesDAO();
    
    public modalAgregarCliente(java.awt.Frame parent, boolean modal, frmVentas vistaVentas) {
        super(parent, modal);
        this.vistaVentas = vistaVentas;
        initComponents();
        this.setTitle("Listado de clientes");
        this.setLocationRelativeTo(parent);
        listarClientes();
        configurarDocumentListener();
    }

    
    public void listarClientes(){
        ArrayList<Cliente> lista = clientesDAO.listarClientes();
        modeloClientes = (DefaultTableModel) this.TableModalClientes.getModel();
        Object[] obj = new Object[4];
        for(int i=0; i < lista.size(); i++){
            obj[0] = lista.get(i).getTipoDocumento();
            obj[1] = lista.get(i).getDni();
            obj[2] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            modeloClientes.addRow(obj);
        }
        this.TableModalClientes.setModel(modeloClientes);
        
        // Establecer las celdas como no editables
        this.TableModalClientes.setDefaultEditor(Object.class, null);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtModalBuscarCliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableModalClientes = new javax.swing.JTable();
        btnModalAgregarCliente = new javax.swing.JButton();
        btnModalClienteCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Buscar cliente : ");

        TableModalClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TIPO DOC", "N° DOC", "NOMBRES"
            }
        ));
        jScrollPane1.setViewportView(TableModalClientes);

        btnModalAgregarCliente.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnModalAgregarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/insertar.png"))); // NOI18N
        btnModalAgregarCliente.setText("AGREGAR");
        btnModalAgregarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalAgregarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModalAgregarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModalAgregarClienteActionPerformed(evt);
            }
        });

        btnModalClienteCancelar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnModalClienteCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/eliminar.png"))); // NOI18N
        btnModalClienteCancelar.setText("CANCELAR");
        btnModalClienteCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalClienteCancelar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModalClienteCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModalClienteCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(btnModalAgregarCliente)
                        .addGap(55, 55, 55)
                        .addComponent(btnModalClienteCancelar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtModalBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModalBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModalAgregarCliente)
                    .addComponent(btnModalClienteCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModalClienteCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModalClienteCancelarActionPerformed
        
        this.setVisible(false);
        
    }//GEN-LAST:event_btnModalClienteCancelarActionPerformed

    private void btnModalAgregarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModalAgregarClienteActionPerformed
        
        int filaSeleccionada = this.TableModalClientes.getSelectedRow();
        
        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(null,"Seleccione un producto");
        }else{
            modeloClientes = (DefaultTableModel) this.TableModalClientes.getModel();
            String tipoDocumento = this.TableModalClientes.getValueAt(filaSeleccionada, 0).toString();
            String numeroDocumento = this.TableModalClientes.getValueAt(filaSeleccionada, 1).toString();
            String nombreCliente = this.TableModalClientes.getValueAt(filaSeleccionada, 2).toString();
            
            //Enviar los datos del cliente al formulario de Ventas
            vistaVentas.txtTipoDocumentoCliente.setText(tipoDocumento);
            vistaVentas.txtNumeroDocumentoCliente.setText(numeroDocumento);
            vistaVentas.txtNombreCliente.setText(nombreCliente != null && !nombreCliente.isEmpty() ? nombreCliente : " -- SIN COMPLETAR -- ");
            this.setVisible(false);
        }
    }//GEN-LAST:event_btnModalAgregarClienteActionPerformed

    
    private void txtBusquedaFiltradoClientes(javax.swing.event.DocumentEvent evt){
        // Obtener el texto actual del campo de texto
        String textoBusqueda = txtModalBuscarCliente.getText();
        
        // Realizar la búsqueda en la base de datos utilizando el texto de búsqueda
        ArrayList<Cliente> clientesFiltrados = clientesDAO.listarClientesFiltrado(textoBusqueda);
        
        // Actualizar el modelo de la tabla con los usuarios filtrados
        modeloClientes.setRowCount(0); // Limpiar el modelo actual
        Object[] obj = new Object[4];
        for (Cliente cliente : clientesFiltrados) {
            obj[0] = cliente.getTipoDocumento();
            obj[1] = cliente.getDni();
            obj[2] = cliente.getNombre();
            modeloClientes.addRow(obj);
        }
        
        // Actualizar la tabla con el nuevo modelo
        TableModalClientes.setModel(modeloClientes);
    }
    
    private void configurarDocumentListener() {
        txtModalBuscarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoClientes(evt);
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoClientes(evt);
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent evt) {
                txtBusquedaFiltradoClientes(evt);
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
            java.util.logging.Logger.getLogger(modalAgregarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(modalAgregarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(modalAgregarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(modalAgregarCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                modalAgregarCliente dialog = new modalAgregarCliente(new javax.swing.JFrame(), true, frmVentas.getInstancia());
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
    public javax.swing.JTable TableModalClientes;
    private javax.swing.JButton btnModalAgregarCliente;
    private javax.swing.JButton btnModalClienteCancelar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextField txtModalBuscarCliente;
    // End of variables declaration//GEN-END:variables
}
