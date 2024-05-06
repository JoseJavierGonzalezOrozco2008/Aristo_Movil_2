package com.example.aristomovil2.modelos;

public class Caducidad {
    private final int dlot;
    private final String lote;
    private final String fecha;
    private final float cant;
    private float cantl;
    private final String notas;

    public Caducidad(int dlot, String lote, String fecha, float cant, float cantl, String notas) {
        this.dlot = dlot;
        this.lote = lote;
        this.fecha = fecha;
        this.cant = cant;
        this.cantl = cantl;
        this.notas = notas;
    }

    public int getDlot() {
        return dlot;
    }

    public String getLote() {
        return lote;
    }

    public String getFecha() {
        return fecha;
    }

    public float getCant() {
        return cant;
    }

    public float getCantl() {
        return cantl;
    }

    public void setCantl(float cantl) {
        this.cantl = cantl;
    }

    public String getNotas() {
        return notas;
    }
}
