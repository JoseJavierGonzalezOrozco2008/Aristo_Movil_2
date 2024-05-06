package com.example.aristomovil2.modelos;

public class Cobrados {
    private String producto;
    private int cantidad;
    private float preciou, subtotal;

    public Cobrados(String producto, int cantidad, float preciou, float subtotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.preciou = preciou;
        this.subtotal = subtotal;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPreciou() {
        return preciou;
    }

    public void setPreciou(float preciou) {
        this.preciou = preciou;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }
}