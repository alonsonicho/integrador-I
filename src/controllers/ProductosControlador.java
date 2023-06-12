package controllers;

import DAO.CategoriasDAO;
import DAO.ProductosDAO;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import views.frmProductos;
import views.frmVentas;

public class ProductosControlador implements ActionListener, MouseListener, KeyListener {

    Producto pro;
    ProductosDAO productosDAO;
    frmProductos vistaProductos;
    frmVentas vistaVentas;
    CategoriasDAO categoriasDAO;
    DefaultTableModel modeloProductos = new DefaultTableModel();

    
    public ProductosControlador(Producto pro, ProductosDAO productosDAO, frmProductos vistaProductos, frmVentas vistaVentas, CategoriasDAO categoriasDAO) {
        this.pro = pro;
        this.productosDAO = productosDAO;
        this.vistaProductos = vistaProductos;
        this.vistaVentas = vistaVentas;
        this.categoriasDAO = categoriasDAO;
        this.vistaProductos.btnRegistrarPro.addActionListener(this);
        this.vistaProductos.btnModificarPro.addActionListener(this);
        this.vistaProductos.btnNuevoPro.addActionListener(this);
        this.vistaProductos.JMenuEliminarProd.addActionListener(this);
        this.vistaProductos.TableProductos.addMouseListener(this);
        this.vistaProductos.JLabelProductos.addMouseListener(this);
        this.vistaProductos.txtbuscarprod.addKeyListener(this);
        this.vistaProductos.btnBuscarProducto.addActionListener(this);
        listarProductos();
        this.vistaProductos.TableProductos.setDefaultEditor(Object.class, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Registrar producto
        if (e.getSource() == vistaProductos.btnRegistrarPro) {
            String descripcion = vistaProductos.txtDescripcionPro.getText();
            String precioVenta = vistaProductos.txtPrecioVentaPro.getText();
            String cantidad = vistaProductos.txtCantidadPro.getText();

            if (descripcion.isEmpty() || precioVenta.isEmpty() || cantidad.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor complete todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            }
            
            if(!validarCamposNumericos(cantidad, precioVenta)){
                return;
            }
            
            String idProducto = vistaProductos.txtidprod.getText();
            String nombreCategoria = vistaProductos.cbxCatPro.getSelectedItem().toString();
            String idCategoria = categoriasDAO.obtenerIdCategoria(nombreCategoria);

            Categoria categoria = new Categoria();
            categoria.setIdCategoria(idCategoria);

            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            producto.setCategoria(categoria);
            producto.setNombreProducto(vistaProductos.txtNombreProducto.getText());
            producto.setDescripcion(descripcion);
            producto.setCantidad(Integer.parseInt(cantidad));
            producto.setPrecio(Double.parseDouble(precioVenta));

            if (productosDAO.registrarProducto(producto)) {
                limpiar();
                limpiarTable();
                listarProductos();
                JOptionPane.showMessageDialog(null, "Producto registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el producto");
            }

        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Actualizar producto
        if (e.getSource() == vistaProductos.btnModificarPro) {
            String idProducto = vistaProductos.txtidprod.getText();

            if (idProducto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila");
            } else {
                String descripcion = vistaProductos.txtDescripcionPro.getText();
                String precioVenta = vistaProductos.txtPrecioVentaPro.getText();
                String cantidad = vistaProductos.txtCantidadPro.getText();

                if (descripcion.isEmpty() || precioVenta.isEmpty() || cantidad.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
                } else {
                    String nombreCategoria = vistaProductos.cbxCatPro.getSelectedItem().toString();
                    String idCategoria = null;

                    if (!nombreCategoria.equals("Sin categoria")) {
                        idCategoria = categoriasDAO.obtenerIdCategoria(nombreCategoria);
                    }

                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(idCategoria);

                    pro.setCategoria(categoria);
                    pro.setNombreProducto(vistaProductos.txtNombreProducto.getText());
                    pro.setDescripcion(descripcion);
                    pro.setCantidad(Integer.parseInt(cantidad));
                    pro.setPrecio(Double.parseDouble(precioVenta));
                    pro.setIdProducto(idProducto);

                    if (productosDAO.actualizarProducto(pro)) {
                        limpiar();
                        limpiarTable();
                        listarProductos();
                        JOptionPane.showMessageDialog(null, "Producto actualizado");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al actualizar");
                    }
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Eliminar producto
        if (e.getSource() == vistaProductos.JMenuEliminarProd) {
            String codigo = vistaProductos.txtidprod.getText();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el producto");
            } else {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    if (productosDAO.eliminarProducto(codigo)) {
                        limpiar();
                        limpiarTable();
                        listarProductos();
                        JOptionPane.showMessageDialog(null, "Producto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al eliminar producto");
                    }
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Buscar producto
        if (e.getSource() == vistaProductos.btnBuscarProducto) {
            String codigoProducto = vistaProductos.txtbuscarprod.getText();

            if (codigoProducto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el código de producto", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
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

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nuevo producto
        if (e.getSource() == vistaProductos.btnNuevoPro) {
            limpiar();
            vistaProductos.btnRegistrarPro.setEnabled(true);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == vistaProductos.TableProductos) {
            int fila = vistaProductos.TableProductos.rowAtPoint(e.getPoint());
            vistaProductos.txtidprod.setText(vistaProductos.TableProductos.getValueAt(fila, 0).toString());
            vistaProductos.cbxCatPro.setSelectedItem(vistaProductos.TableProductos.getValueAt(fila, 1).toString());
            vistaProductos.txtNombreProducto.setText(vistaProductos.TableProductos.getValueAt(fila, 2).toString());
            vistaProductos.txtDescripcionPro.setText(vistaProductos.TableProductos.getValueAt(fila, 3).toString());
            vistaProductos.txtCantidadPro.setText(vistaProductos.TableProductos.getValueAt(fila, 4).toString());
            vistaProductos.txtPrecioVentaPro.setText(vistaProductos.TableProductos.getValueAt(fila, 5).toString());
            vistaProductos.btnRegistrarPro.setEnabled(false);
        }

        if (e.getSource() == vistaProductos.JLabelProductos) {
            vistaProductos.jTabbedPane1.setSelectedIndex(0);
            limpiarTable();
            listarProductos();
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void mouseExited(MouseEvent me) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        ///////////////////////////////////////////////////////
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        ///////////////////////////////////////////////////////
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarProductos() {
        ArrayList<Producto> lista = productosDAO.listarProductos();
        modeloProductos = new DefaultTableModel();
        modeloProductos = (DefaultTableModel) vistaProductos.TableProductos.getModel();
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

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vistaProductos.txtidprod.setText(null);
        vistaProductos.txtNombreProducto.setText(null);
        vistaProductos.txtDescripcionPro.setText(null);
        vistaProductos.txtCantidadPro.setText(null);
        vistaProductos.txtPrecioVentaPro.setText(null);
        vistaProductos.txtbuscarprod.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarTable() {
        for (int i = 0; i < modeloProductos.getRowCount(); i++) {
            modeloProductos.removeRow(i);
            i = i - 1;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
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
