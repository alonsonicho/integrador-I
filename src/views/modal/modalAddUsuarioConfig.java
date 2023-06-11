package views.modal;

import DAO.UsuariosDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Usuario;
import views.frmClientes;


public class modalAddUsuarioConfig extends javax.swing.JDialog {

    DefaultTableModel modeloUsuario = new DefaultTableModel();
    frmClientes vistaClientes;
    UsuariosDAO usuariosDAO = new UsuariosDAO();
 
    
    public modalAddUsuarioConfig(java.awt.Frame parent, boolean modal, frmClientes vistaClientes) {
        super(parent, modal);
        this.vistaClientes = vistaClientes;
        initComponents();
        this.setTitle("Listado de usuarios");
        this.setLocationRelativeTo(parent);
        listarUsuarios();
        configurarDocumentListener();
    }
    
    public void listarUsuarios(){
        ArrayList<Usuario> lista = usuariosDAO.listarUsuarios();
        modeloUsuario = (DefaultTableModel) this.TableUpdateUsuarioLista.getModel();
        Object[] obj = new Object[3];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getDniUsuario();
            obj[1] = lista.get(i).getNombre();
            obj[2] = lista.get(i).getUsuario();
            modeloUsuario.addRow(obj);
        }
        this.TableUpdateUsuarioLista.setModel(modeloUsuario);
        
        // Establecer las celdas como no editables
        this.TableUpdateUsuarioLista.setDefaultEditor(Object.class, null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel26 = new javax.swing.JLabel();
        txtUpdateBuscarUsuario = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        TableUpdateUsuarioLista = new javax.swing.JTable();
        btnModalUpdateAgregar = new javax.swing.JButton();
        btnModalUpdateSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel26.setText("Buscar :");

        txtUpdateBuscarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUpdateBuscarUsuarioActionPerformed(evt);
            }
        });

        TableUpdateUsuarioLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DNI", "Nombre", "Usuario"
            }
        ));
        jScrollPane6.setViewportView(TableUpdateUsuarioLista);

        btnModalUpdateAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/insertar.png"))); // NOI18N
        btnModalUpdateAgregar.setText("Agregar");
        btnModalUpdateAgregar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalUpdateAgregar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModalUpdateAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModalUpdateAgregarActionPerformed(evt);
            }
        });

        btnModalUpdateSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/eliminar.png"))); // NOI18N
        btnModalUpdateSalir.setText("Cancelar");
        btnModalUpdateSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModalUpdateSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtUpdateBuscarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(btnModalUpdateAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(btnModalUpdateSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUpdateBuscarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnModalUpdateSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModalUpdateAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModalUpdateAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModalUpdateAgregarActionPerformed
        
        int filaSeleccionada = this.TableUpdateUsuarioLista.getSelectedRow();
        
        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(null, "Seleccione un usuario");
        }else{
            modeloUsuario = (DefaultTableModel) this.TableUpdateUsuarioLista.getModel();
            String nombres = this.TableUpdateUsuarioLista.getValueAt(filaSeleccionada, 1).toString();
            String usuario = this.TableUpdateUsuarioLista.getValueAt(filaSeleccionada, 2).toString();
            vistaClientes.txtNombreCambioPass.setText(nombres);
            vistaClientes.txtUsuarioCambioPass.setText(usuario);
            this.setVisible(false);
        }
        
    }//GEN-LAST:event_btnModalUpdateAgregarActionPerformed

    private void txtUpdateBuscarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUpdateBuscarUsuarioActionPerformed
   
    }//GEN-LAST:event_txtUpdateBuscarUsuarioActionPerformed

    private void txtUpdateBuscarUsuarioDocumentChanged(javax.swing.event.DocumentEvent evt){
        // Obtener el texto actual del campo de texto
        String textoBusqueda = txtUpdateBuscarUsuario.getText();
        
        // Realizar la búsqueda en la base de datos utilizando el texto de búsqueda
        ArrayList<Usuario> usuariosFiltrados = usuariosDAO.listarUsuariosFiltrados(textoBusqueda);
        
        // Actualizar el modelo de la tabla con los usuarios filtrados
        modeloUsuario.setRowCount(0); // Limpiar el modelo actual
        Object[] obj = new Object[3];
        for (Usuario usuario : usuariosFiltrados) {
            obj[0] = usuario.getDniUsuario();
            obj[1] = usuario.getNombre();
            obj[2] = usuario.getUsuario();
            modeloUsuario.addRow(obj);
        }
        // Actualizar la tabla con el nuevo modelo
        TableUpdateUsuarioLista.setModel(modeloUsuario);  
    }
    
    private void configurarDocumentListener() {
        txtUpdateBuscarUsuario.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent evt) {
                txtUpdateBuscarUsuarioDocumentChanged(evt);
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent evt) {
                txtUpdateBuscarUsuarioDocumentChanged(evt);
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent evt) {
                txtUpdateBuscarUsuarioDocumentChanged(evt);
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
            java.util.logging.Logger.getLogger(modalAddUsuarioConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(modalAddUsuarioConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(modalAddUsuarioConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(modalAddUsuarioConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                modalAddUsuarioConfig dialog = new modalAddUsuarioConfig(new javax.swing.JFrame(), true, frmClientes.getInstancia());
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
    public javax.swing.JTable TableUpdateUsuarioLista;
    public javax.swing.JButton btnModalUpdateAgregar;
    public javax.swing.JButton btnModalUpdateSalir;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JScrollPane jScrollPane6;
    public javax.swing.JTextField txtUpdateBuscarUsuario;
    // End of variables declaration//GEN-END:variables
}
