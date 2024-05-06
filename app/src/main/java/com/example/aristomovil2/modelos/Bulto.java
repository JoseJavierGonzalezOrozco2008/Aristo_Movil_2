package com.example.aristomovil2.modelos;

public class Bulto {
    private final String contenedor, dcinfolio,detalles;
    private final String estatus, pedidi;
    private final String fecha;
    private final String usuario;
    private final int rengs;
    private final float piezas;

    public Bulto(String contenedor, String dcinfolio, String estatus, String pedidi, String fecha, String usuario, int rengs, float piezas, String detalles) {
        this.contenedor = contenedor;
        this.dcinfolio = dcinfolio;
        this.estatus = estatus;
        this.pedidi = pedidi;
        this.fecha = fecha;
        this.usuario = usuario;
        this.rengs = rengs;
        this.piezas = piezas;
        this.detalles = detalles;
    }

    public String getContenedor() {
        return contenedor;
    }

    public String getEstatus() {
        return estatus;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public int getRengs() {
        return rengs;
    }

    public String getDcinfolio() {
        return dcinfolio;
    }

    public String getPedidi() {
        return pedidi;
    }

    public float getPiezas() {
        return piezas;
    }

    public String getDetalles() {
        return detalles;
    }
}
