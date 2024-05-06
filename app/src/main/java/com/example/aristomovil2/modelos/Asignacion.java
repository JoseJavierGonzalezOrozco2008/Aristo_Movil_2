package com.example.aristomovil2.modelos;

public class Asignacion {
    private String ubicacion, estatus, numconteo;
    private int puedeabrir;

    public Asignacion(String ubicacion, String estatus, String numconteo, int puedeabrir) {
        this.ubicacion = ubicacion;
        this.estatus = estatus;
        this.numconteo = numconteo;
        this.puedeabrir = puedeabrir;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getNumconteo() {
        return numconteo;
    }

    public void setNumconteo(String numconteo) {
        this.numconteo = numconteo;
    }

    public int getPuedeabrir() {
        return puedeabrir;
    }

    public void setPuedeabrir(int puedeabrir) {
        this.puedeabrir = puedeabrir;
    }
}
