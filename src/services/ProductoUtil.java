package services;

import DAO.CategoriasDAO;
import DAO.ProductosDAO;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Categoria;
import models.Producto;
import util.Utilidades;
import views.frmProductos;


public class ProductoUtil {

    Producto pro = new Producto();
    ProductosDAO productosDAO = new ProductosDAO();
    CategoriasDAO categoriasDAO = new CategoriasDAO();
    frmProductos vistaProductos;
    DefaultTableModel modeloProductos = new DefaultTableModel();
    //ProductosOp productosOp = new ProductosOp(productosDAO);

    public ProductoUtil(frmProductos vistaProductos) {
        this.vistaProductos = vistaProductos;
        listarProductos();
    }

    public void registrarProducto() {
        String nombreProducto = vistaProductos.txtNombreProducto.getText();
        String descripcion = vistaProductos.txtDescripcionPro.getText();
        String precioVenta = vistaProductos.txtPrecioVentaPro.getText();
        String cantidad = vistaProductos.txtCantidadPro.getText();

        if (!Utilidades.validarCamposVacios(nombreProducto, descripcion, precioVenta, cantidad)) {
            return;
        }

        if (!validarCamposNumericos(cantidad, precioVenta)) {
            return;
        }

        String nombreCategoria = vistaProductos.cbxCatPro.getSelectedItem().toString();
        String idCategoria = categoriasDAO.obtenerIdCategoria(nombreCategoria);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);

        Producto producto = new Producto();
        producto.setCategoria(categoria);
        producto.setNombreProducto(nombreProducto);
        producto.setDescripcion(descripcion);
        producto.setCantidad(Integer.parseInt(cantidad));
        producto.setPrecio(Double.parseDouble(precioVenta));

        if (productosDAO.registrarProducto(producto)) {
            limpiar();
            listarProductos();
            JOptionPane.showMessageDialog(null, "Producto registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar el producto");
        }
    }

    public void actualizarProducto() {
        String idProducto = vistaProductos.txtidprod.getText();

        if (idProducto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else {
            String nombreProducto = vistaProductos.txtNombreProducto.getText();
            String descripcion = vistaProductos.txtDescripcionPro.getText();
            String precioVenta = vistaProductos.txtPrecioVentaPro.getText();
            String cantidad = vistaProductos.txtCantidadPro.getText();

            if (!Utilidades.validarCamposVacios(nombreProducto, descripcion, precioVenta, cantidad)) {
                return;
            }

            String nombreCategoria = vistaProductos.cbxCatPro.getSelectedItem().toString();
            String idCategoria = null;

            if (!nombreCategoria.equals("Sin categoria")) {
                idCategoria = categoriasDAO.obtenerIdCategoria(nombreCategoria);
            }

            Categoria categoria = new Categoria();
            categoria.setIdCategoria(idCategoria);

            pro.setCategoria(categoria);
            pro.setNombreProducto(nombreProducto);
            pro.setDescripcion(descripcion);
            pro.setCantidad(Integer.parseInt(cantidad));
            pro.setPrecio(Double.parseDouble(precioVenta));
            pro.setIdProducto(idProducto);

            if (productosDAO.actualizarProducto(pro)) {
                limpiar();
                listarProductos();
                JOptionPane.showMessageDialog(null, "Producto actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar");
            }

        }
    }

    public void eliminarProducto() {
        String codigo = vistaProductos.txtidprod.getText();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila con el producto");
        } else {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                if (productosDAO.eliminarProducto(codigo)) {
                    limpiar();
                    listarProductos();
                    JOptionPane.showMessageDialog(null, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar producto");
                }
            }
        }
    }

    
    
    public void buscarProducto() {
        String codigoProducto = vistaProductos.txtbuscarprod.getText();

        if (!Utilidades.validarCamposVacios(codigoProducto)) {
            return;
        }

        try {
            pro = productosDAO.buscarProducto(codigoProducto);
            if (pro.getIdProducto() != null) {
                vistaProductos.txtidprod.setText(pro.getIdProducto());
                vistaProductos.txtCantidadPro.setText(String.valueOf(pro.getCantidad()));
                vistaProductos.txtNombreProducto.setText(pro.getNombreProducto());
                vistaProductos.txtDescripcionPro.setText(pro.getDescripcion());
                vistaProductos.txtPrecioVentaPro.setText(String.valueOf(pro.getPrecio()));

                String codigoCategoria = pro.getCategoria().getIdCategoria();
                String nombreCategoria = categoriasDAO.obtenerNombreCategoria(codigoCategoria);
                vistaProductos.cbxCatPro.setSelectedItem(nombreCategoria);

                vistaProductos.btnRegistrarPro.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "No existe código de producto");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    
    public void listarProductos() {
        ArrayList<Producto> lista = productosDAO.listarProductos();
        modeloProductos = (DefaultTableModel) vistaProductos.TableProductos.getModel();
        modeloProductos.setRowCount(0); // Limpiar el modelo de la tabla
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
            modeloProductos.addRow(obj);
        }
        vistaProductos.TableProductos.setModel(modeloProductos);
    }
    

    public void limpiar() {
        vistaProductos.txtidprod.setText(null);
        vistaProductos.txtNombreProducto.setText(null);
        vistaProductos.txtDescripcionPro.setText(null);
        vistaProductos.txtCantidadPro.setText(null);
        vistaProductos.txtPrecioVentaPro.setText(null);
        vistaProductos.txtbuscarprod.setText(null);
    }

    public boolean validarCamposNumericos(String valorInt, String valorDouble) {
        try {
            int cantidad = Integer.parseInt(valorInt);
            double precio = Double.parseDouble(valorDouble);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los campos deben ser números válidos");
            return false;
        }
    }

}
