package operaciones;

import DAO.ClientesDAO;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.Cliente;

public class ClientesOp {

    ClientesDAO clientesDAO;

    public ClientesOp(ClientesDAO clientesDAO) {
        this.clientesDAO = clientesDAO;
    }

    public void listarClientes(DefaultTableModel modelo, JTable tabla) {
        List<Cliente> lista = this.clientesDAO.listarClientes();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

    public void listarClientesInactivo(DefaultTableModel modelo, JTable tabla) {
        List<Cliente> lista = this.clientesDAO.listarClientesInactivo();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

}
