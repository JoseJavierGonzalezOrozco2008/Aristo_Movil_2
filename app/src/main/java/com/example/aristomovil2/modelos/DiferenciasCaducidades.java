package com.example.aristomovil2.modelos;

public class DiferenciasCaducidades {
    private final String producto;
    private final String codigo;
    private final float cantidad;

    public DiferenciasCaducidades(String codigo, String producto, float cantidad) {
        this.codigo = codigo;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public float getCantidad() {
        return cantidad;
    }
}
