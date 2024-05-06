package com.example.aristomovil2.modelos;

public class Cliente {
    private final int clteid;
    private final String cliente, razon, domicilio, rfc;
    private final boolean vntacredito, credito;

    public Cliente(int clteid, String cliente, String razon, boolean credito, String domicilio, String rfc, boolean vntacredito) {
        this.clteid = clteid;
        this.cliente = cliente;
        this.razon = razon;
        this.credito = credito;
        this.domicilio = domicilio;
        this.rfc = rfc;
        this.vntacredito = vntacredito;
    }

    public int getClteid() {
        return clteid;
    }

    public String getCliente() {
        return cliente;
    }

    public String getRazon() {
        return razon;
    }

    public Boolean getCredito() {
        return credito;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getRfc() {
        return rfc;
    }

    public boolean isVntacredito() {
        return vntacredito;
    }
}
