package com.example.aristomovil2.modelos;

public class Subalmacen {
    private String nombre;
    private int dcatid;

    public Subalmacen(){}

    public Subalmacen(String nombre, int dcatid) {
        this.nombre = nombre;
        this.dcatid = dcatid;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDcatid() {
        return dcatid;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDcatid(int dcatid) {
        this.dcatid = dcatid;
    }
}
