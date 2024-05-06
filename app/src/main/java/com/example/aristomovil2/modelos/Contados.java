package com.example.aristomovil2.modelos;

public class Contados {
    private String codigo, producto, cant;
    private boolean selected;
    private final int id;

    public Contados(String codigo, String producto, String cant, int id) {
        this.codigo = codigo;
        this.producto = producto;
        this.cant = cant;
        this.selected = false;
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCant() {
        return cant;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setCant(String cant) {
        this.cant = cant;
    }

    public int getId() {
        return id;
    }
}
