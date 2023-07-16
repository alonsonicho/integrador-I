package services;

import DAO.ClientesDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Cliente;
import util.Utilidades;
import views.frmClientes;

public class ClienteUtil {

    Cliente cl = new Cliente();
    ClientesDAO clienteDAO = new ClientesDAO();
    frmClientes vistaClientes;
    DefaultTableModel modeloClienteActivo = new DefaultTableModel();
    DefaultTableModel modeloClienteInactivo = new DefaultTableModel();

    public ClienteUtil(frmClientes vistaClientes) {
        this.vistaClientes = vistaClientes;
        listarClientes();
        listarClientesInactivo();
    }

    public void registrarCliente() {
        String nombreCliente = vistaClientes.txtnombrecli.getText();
        String numeroDocumentoCliente = vistaClientes.txtdni.getText();
        String telefonoCliente = vistaClientes.txttelefonocli.getText();
        String direccionCliente = vistaClientes.txadireccioncli.getText();
        String tipoDocumento = vistaClientes.cbTipoDocumento.getSelectedItem().toString();

        if (!Utilidades.validarCamposVacios(nombreCliente, numeroDocumentoCliente, telefonoCliente, direccionCliente)) {
            return;
        }

        //Validar campos con numeros, se ejecuta si devuelve false
        boolean camposSonEnteros = Utilidades.validarCamposNumericos(numeroDocumentoCliente, telefonoCliente);
        if (!camposSonEnteros) {
            return;
        }

        //Verificar el tipo de documento ingresado #DNI o #RUC
        if (tipoDocumento.equals("DNI")) {
            if (numeroDocumentoCliente.length() != 8) {
                JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos");
                return;
            }
        } else if (tipoDocumento.equals("RUC")) {
            if (!(numeroDocumentoCliente.startsWith("10") || numeroDocumentoCliente.startsWith("20")) || numeroDocumentoCliente.length() != 10) {
                JOptionPane.showMessageDialog(null, "El RUC debe comenzar con '10' o '20' y tener 10 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int dni = Integer.parseInt(numeroDocumentoCliente);
        int telefono = Integer.parseInt(telefonoCliente);

        cl.setTipoDocumento(tipoDocumento);
        cl.setDni(dni);
        cl.setNombre(nombreCliente);
        cl.setTelefono(telefono);
        cl.setDireccion(direccionCliente);
        //Registro del cliente
        if (clienteDAO.registrarCliente(cl)) {
            listarClientes();
            //listarClientes();
            limpiar();
            JOptionPane.showMessageDialog(null, "Cliente Registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCliente() {
        String idCliente = vistaClientes.txtidcli.getText();
        String nombreCliente = vistaClientes.txtnombrecli.getText();
        String dniCliente = vistaClientes.txtdni.getText();
        String telefonoCliente = vistaClientes.txttelefonocli.getText();
        String direccionCliente = vistaClientes.txadireccioncli.getText();
        String tipoDocumento = vistaClientes.cbTipoDocumento.getSelectedItem().toString();

        //Verificar que se selecciono una fila de la tabla
        if (idCliente.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila");
            return;
        }

        if (dniCliente.isEmpty() || tipoDocumento.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El tipo y n° de documento son obligatorios", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Validar si se ingresan valores String o caracteres en campos numericos
        boolean camposSonEnteros = Utilidades.validarCamposNumericos(dniCliente, telefonoCliente);
        if (!camposSonEnteros) {
            return;
        }

        //Validacion para asignar el tipo de documento
        if (tipoDocumento.equals("DNI")) {
            if (dniCliente.length() != 8) {
                JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else if (tipoDocumento.equals("RUC")) {
            if (!(dniCliente.startsWith("10") || dniCliente.startsWith("20")) || dniCliente.length() != 10) {
                JOptionPane.showMessageDialog(null, "El RUC debe comenzar con '10' o '20' y tener 10 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int idCli = Integer.parseInt(idCliente);
        int dni = Integer.parseInt(dniCliente);
        int telefono;
        if (telefonoCliente.isEmpty()) {
            telefono = 0; // Asignar un valor predeterminado de 0 cuando la cadena está vacía
        } else {
            telefono = Integer.parseInt(telefonoCliente); // Convertir la cadena a entero
        }

        //int telefono = Integer.parseInt(telefonoCliente);
        cl.setTipoDocumento(tipoDocumento);
        cl.setDni(dni);
        cl.setNombre(nombreCliente);
        cl.setTelefono(telefono);
        cl.setDireccion(direccionCliente);
        cl.setIdCliente(idCli);

        if (clienteDAO.actualizarCliente(cl)) {
            listarClientes();
            limpiar();
            JOptionPane.showMessageDialog(null, "Los datos del cliente se actualizaron correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void buscarCliente() {
        String dniClienteString = vistaClientes.txtbuscarcli.getText();

        //Verificar que el campo de N°Documento no se encuentre vacio
        if (!Utilidades.validarCamposVacios(dniClienteString)) {
            vistaClientes.txtbuscarcli.requestFocus();
            return;
        }

        //Validar los campos numericos, se ejecuta si devuelve false
        boolean camposSonEnteros = Utilidades.validarCamposNumericos(dniClienteString);
        if (!camposSonEnteros) {
            return;
        }

        int dniCliente = Integer.parseInt(dniClienteString);

        try {
            cl = clienteDAO.buscarCliente(dniCliente);
            if (cl.getDni() != 0) {
                vistaClientes.txtnombrecli.setText(cl.getNombre());
                vistaClientes.cbTipoDocumento.setSelectedItem(cl.getTipoDocumento());
                vistaClientes.txtdni.setText(String.valueOf(cl.getDni()));
                vistaClientes.txttelefonocli.setText(String.valueOf(cl.getTelefono()));
                vistaClientes.txadireccioncli.setText(cl.getDireccion());
                vistaClientes.txtidcli.setText(String.valueOf(cl.getIdCliente()));
                vistaClientes.btnregistrarcli.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "No existe el DNI ingresado", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void eliminarCliente(){
        String idClienteStr = vistaClientes.txtidcli.getText();

            if (idClienteStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el cliente");
                return;
            }

            int idCliente = Integer.parseInt(idClienteStr);
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (clienteDAO.eliminarCliente(idCliente)) {
                    limpiar();
                    listarClientes();
                    listarClientesInactivo();
                    JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
    }
    
    
    public void activarCliente(){
        if (!vistaClientes.radioButtonTrueCliente.isSelected()) {
                JOptionPane.showMessageDialog(null, "Seleccione la opcion de 'ACTIVO' para continuar", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (vistaClientes.radioButtonFalseCliente.isSelected()) {
                JOptionPane.showMessageDialog(null, "La opcion de INACTIVO no debe estar seleccionada", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCliente = Integer.parseInt(vistaClientes.txtActivarClienteId.getText());
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas activar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (clienteDAO.activarCliente(idCliente)) {
                    listarClientes();
                    listarClientesInactivo();
                    limpiarClienteInactivo();
                    JOptionPane.showMessageDialog(null, "El cliente se activo correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
    }
    

    public void listarClientes() {
        List<Cliente> lista = clienteDAO.listarClientes();
        modeloClienteActivo = (DefaultTableModel) vistaClientes.TableMostrarClientes.getModel();
        modeloClienteActivo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modeloClienteActivo.addRow(obj);
        }
        vistaClientes.TableMostrarClientes.setModel(modeloClienteActivo);
    }

    public void listarClientesInactivo() {
        List<Cliente> lista = this.clienteDAO.listarClientesInactivo();
        modeloClienteInactivo = (DefaultTableModel) vistaClientes.TableInactiveClientes.getModel();
        modeloClienteInactivo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modeloClienteInactivo.addRow(obj);
        }
        vistaClientes.TableInactiveClientes.setModel(modeloClienteInactivo);
    }

    public void limpiar() {
        vistaClientes.txtidcli.setText(null);
        vistaClientes.txtdni.setText(null);
        vistaClientes.txtnombrecli.setText(null);
        vistaClientes.txttelefonocli.setText(null);
        vistaClientes.txadireccioncli.setText(null);
        vistaClientes.txtbuscarcli.setText(null);
    }

    public void limpiarClienteInactivo() {
        vistaClientes.txtActivarClienteId.setText(null);
        vistaClientes.txtActiveNombreCliente.setText(null);
        vistaClientes.txtActiveNumeroDocumentoCliente.setText(null);
        vistaClientes.txtActiveTipoDocumento.setText(null);
        vistaClientes.txtActiveTelefonoCliente.setText(null);
        vistaClientes.txtActiveDireccion.setText(null);
    }

}
