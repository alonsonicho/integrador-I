package operaciones;

import DAO.ProductosDAO;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import models.Producto;

public class ProductosOp {

    ProductosDAO productosDAO;

    public ProductosOp(ProductosDAO productosDAO) {
        this.productosDAO = productosDAO;
    }

    public void listarProductos(DefaultTableModel modelo, JTable tabla) {
        ArrayList<Producto> lista = this.productosDAO.listarProductos();
        modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdProducto();
            if (lista.get(i).getCategoria().getNombreCategoria() == null) {
                obj[1] = "Sin categoria";
            } else {
                obj[1] = lista.get(i).getCategoria().getNombreCategoria();
            }
            obj[2] = lista.get(i).getNombreProducto();
            obj[3] = lista.get(i).getDescripcion();
            obj[4] = lista.get(i).getCantidad();
            obj[5] = lista.get(i).getPrecio();
            modelo.addRow(obj);
        }
        tabla.setModel(modelo);
    }

}
