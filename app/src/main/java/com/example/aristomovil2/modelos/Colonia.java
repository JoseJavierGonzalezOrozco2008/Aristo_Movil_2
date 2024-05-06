package com.example.aristomovil2.modelos;

public class Colonia {
    private final String clave, colonia, codigo;

    public Colonia(String clave, String colonia, String codigo) {
        this.clave = clave;
        this.colonia = colonia;
        this.codigo = codigo;
    }

    public String getClave() {
        return clave;
    }

    public String getColonia() {
        return colonia;
    }

    public String getCodigo() {
        return codigo;
    }
}
