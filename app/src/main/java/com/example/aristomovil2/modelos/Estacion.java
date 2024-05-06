package com.example.aristomovil2.modelos;

public class Estacion {
    private final int estaid;
    private final String nombre;
    private final String tipo;
    private final boolean asignada;

    public Estacion(int estaid, String nombre, String tipo, boolean asignada) {
        this.estaid = estaid;
        this.nombre = nombre;
        this.tipo = tipo;
        this.asignada = asignada;
    }

    public int getEstaid() {
        return estaid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isAsignada() {
        return asignada;
    }
}
