package models;

public class DetalleFactura {

    private Producto producto;
    private Factura factura;
    private double total;
    private int cantidad;

    public DetalleFactura() {
    }

    public DetalleFactura(Producto producto, Factura factura, double total, int cantidad) {
        this.producto = producto;
        this.factura = factura;
        this.total = total;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

}
