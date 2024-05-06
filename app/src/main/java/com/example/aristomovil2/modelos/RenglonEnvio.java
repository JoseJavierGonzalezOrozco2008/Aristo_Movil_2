package com.example.aristomovil2.modelos;

public class RenglonEnvio {
    private final int numrenglon;
    private final int numrengs;
    private final int ddin;
    private final String codigo;
    private String codigos;
    private final String producto;
    private final String ubicacion;
    private final String faltacadu;
    private final String notas;
    private final String pediid;
    private final String dcinfolio;
    private String listaubiks;
    private final float cantpedida;
    private final float ingresado;
    private final float dispo;
    private final float anaquel;
    private final float futura;

    public RenglonEnvio(int numrenglon, int numrengs, int ddin, String codigo, String producto, String ubicacion, String faltacadu, String notas, String pediid, String dcinfolio, float cantpedida, float ingresado, float dispo, float anaquel, float futura) {
        this.numrenglon = numrenglon;
        this.numrengs = numrengs;
        this.ddin = ddin;
        this.codigo = codigo;
        this.producto = producto;
        this.ubicacion = ubicacion;
        this.faltacadu = faltacadu;
        this.notas = notas;
        this.pediid = pediid;
        this.dcinfolio = dcinfolio;
        this.cantpedida = cantpedida;
        this.ingresado = ingresado;
        this.dispo = dispo;
        this.anaquel = anaquel;
        this.futura = futura;
        this.codigos="";
    }

    public int getNumrenglon() {
        return numrenglon;
    }

    public int getNumrengs() {
        return numrengs;
    }

    public int getDdin() {
        return ddin;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProducto() {
        return producto;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getFaltacadu() {
        return faltacadu;
    }

    public String getNotas() {
        return notas;
    }

    public float getCantpedida() {
        return cantpedida;
    }

    public float getIngresado() {
        return ingresado;
    }

    public float getDispo() {
        return dispo;
    }

    public float getAnaquel() {
        return anaquel;
    }

    public float getFutura() {
        return futura;
    }

    public String getPediid() { return pediid; }

    public String getDcinfolio() { return dcinfolio; }

    public String getCodigos() {
        return codigos;
    }

    public void setCodigos(String codigos) {
        this.codigos = codigos;
    }

    public String getListaubiks() {
        return listaubiks;
    }

    public void setListaubiks(String listaubiks) {
        this.listaubiks = listaubiks;
    }
}
