package models;

import java.sql.Date;

public class Factura {

    private String codigo;
    private String tipoPago;
    private String tipoDocumentoVenta;
    private Cliente cliente;
    private Usuario usuario;
    private Date fecha;
    private double total;
    private String estado;

    public Factura() {
    }

    public Factura(String codigo, String tipoPago, String tipoDocumentoVenta, Cliente cliente, Usuario usuario, Date fecha, double total, String estado) {
        this.codigo = codigo;
        this.tipoPago = tipoPago;
        this.tipoDocumentoVenta = tipoDocumentoVenta;
        this.cliente = cliente;
        this.usuario = usuario;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getTipoDocumentoVenta() {
        return tipoDocumentoVenta;
    }

    public void setTipoDocumentoVenta(String tipoDocumentoVenta) {
        this.tipoDocumentoVenta = tipoDocumentoVenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }  
}
