package com.example.aristomovil2.modelos;

public class Lote {
    private String folioventa, descripcion, codigo;
    private int prodid;
    private float cantidad;

    public Lote(String folioventa, String descripcion, String codigo, int prodid, float cantidad) {
        this.folioventa = folioventa;
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.prodid = prodid;
        this.cantidad = cantidad;
    }

    public String getFolioventa() {
        return folioventa;
    }

    public void setFolioventa(String folioventa) {
        this.folioventa = folioventa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getProdid() {
        return prodid;
    }

    public void setProdid(int prodid) {
        this.prodid = prodid;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }
}
