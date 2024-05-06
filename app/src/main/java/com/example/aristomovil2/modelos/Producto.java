package com.example.aristomovil2.modelos;

import android.graphics.Bitmap;

public class Producto {
    private final int prodid;
    private final String codigo;
    private String codigos;
    private final String producto;
    private final float precio;
    private final int renglones;
    private final int preciomax;
    private final int preciomin;
    private final int disponible;
    private final String preciovolumen;
    private final String promo;
    private final Bitmap foto;

    public Producto(int prodid, String codigo, String producto, float precio, int renglones, int preciomax, int preciomin, int disponible, String preciovolumen, String promo, Bitmap foto) {
        this.prodid = prodid;
        this.codigo = codigo;
        this.producto = producto;
        this.precio = precio;
        this.renglones = renglones;
        this.preciomax = preciomax;
        this.preciomin = preciomin;
        this.disponible = disponible;
        this.preciovolumen = preciovolumen;
        this.promo = promo;
        this.foto = foto;
        codigos="";
    }

    public int getProdid() {
        return prodid;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public float getPrecio() {
        return precio;
    }

    public int getRenglones() {
        return renglones;
    }

    public int getPreciomax() {
        return preciomax;
    }

    public int getPreciomin() {
        return preciomin;
    }

    public int getDisponible() {
        return disponible;
    }

    public String getPreciovolumen() {
        return preciovolumen;
    }

    public String getPromo() {
        return promo;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public String getCodigos() {
        return codigos;
    }

    public void setCodigos(String codigos) {
        this.codigos = codigos;
    }
}