package com.example.aristomovil2.modelos;

import android.graphics.Bitmap;

public class Renglon {
    private final String folio;
    private final String codigo;
    private final String producto;
    private final String fraccionable;
    private final String refer;
    private final String notas;
    private final int dvtaid;
    private final int prodid;
    private final int estatus;
    private final float cant;
    private final float precio;
    private final float dscto;
    private final float dsctoad;
    private final float subtotal;
    private final float impuesto;
    private final float total;
    private final float vntatotal;
    private final float disponible;
    private final float futura;
    private final float caduca;
    private final Bitmap foto;

    public Renglon(String folio, String codigo, String producto, String fraccionable, String refer, String notas, Bitmap foto, int dvtaid, int prodid, int estatus, float cant, float precio, float dscto, float dsctoad, float subtotal, float impuesto, float total, float vntatotal, float disponible, float futura, float caduca) {
        this.folio = folio;
        this.codigo = codigo;
        this.producto = producto;
        this.fraccionable = fraccionable;
        this.refer = refer;
        this.notas = notas;
        this.foto = foto;
        this.dvtaid = dvtaid;
        this.prodid = prodid;
        this.estatus = estatus;
        this.cant = cant;
        this.precio = precio;
        this.dscto = dscto;
        this.dsctoad = dsctoad;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
        this.vntatotal = vntatotal;
        this.disponible = disponible;
        this.futura = futura;
        this.caduca = caduca;
    }

    public String getFolio() {
        return folio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getFraccionable() {
        return fraccionable;
    }

    public String getRefer() {
        return refer;
    }

    public String getNotas() {
        return notas;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public int getDvtaid() {
        return dvtaid;
    }

    public int getProdid() {
        return prodid;
    }

    public int getEstatus() {
        return estatus;
    }

    public float getCant() {
        return cant;
    }

    public float getPrecio() {
        return precio;
    }

    public float getDscto() {
        return dscto;
    }

    public float getDsctoad() {
        return dsctoad;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public float getImpuesto() {
        return impuesto;
    }

    public float getTotal() {
        return total;
    }

    public float getVntatotal() {
        return vntatotal;
    }

    public float getDisponible() {
        return disponible;
    }

    public float getFutura() {
        return futura;
    }

    public float getCaduca() {
        return caduca;
    }
}
