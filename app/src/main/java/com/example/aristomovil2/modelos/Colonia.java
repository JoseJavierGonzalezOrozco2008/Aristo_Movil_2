package com.example.aristomovil2.modelos;

public class Colonia {
    private final String clave, colonia, codigo;
    private String id,muni,estado;

    public Colonia(String clave, String colonia, String codigo) {
        this.clave = clave;
        this.colonia = colonia;
        this.codigo = codigo;
    }

    public Colonia(String clave, String colonia, String codigo,String pId,String pMuni,String pEstado) {
        this.clave = clave;
        this.colonia = colonia;
        this.codigo = codigo;
        this.id = pId;
        this.muni = pMuni;
        this.estado = pEstado;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMuni() {
        return muni;
    }

    public void setMuni(String muni) {
        this.muni = muni;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
