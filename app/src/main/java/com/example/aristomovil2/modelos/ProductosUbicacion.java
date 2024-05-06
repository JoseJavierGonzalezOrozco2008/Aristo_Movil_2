package com.example.aristomovil2.modelos;

public class ProductosUbicacion {
    private final int productoID;
    private final String codigo;
    private final String producto;
    private final boolean activo;
    private final int cantidadMax;
    private final int cantidadMin;
    private final boolean lleno;

    public ProductosUbicacion(int productoID, String codigo, String producto, boolean activo, int cantidadMax, int cantidadMin, boolean lleno) {
        this.productoID = productoID;
        this.codigo = codigo;
        this.producto = producto;
        this.activo = activo;
        this.cantidadMax = cantidadMax;
        this.cantidadMin = cantidadMin;
        this.lleno = lleno;
    }

    public int getProductoID() {
        return productoID;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getCantidadMax() {
        return cantidadMax;
    }

    public int getCantidadMin() {
        return cantidadMin;
    }

    public boolean isLleno() {
        return lleno;
    }
}
