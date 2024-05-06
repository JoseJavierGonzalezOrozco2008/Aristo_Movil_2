package com.example.aristomovil2.modelos;

public class Proveedor {
    private String rfc, aliasp, rsocial;
    private int provid,diascred;

    public Proveedor(){}

    public Proveedor(String rfc, String aliasp, String rsocial, int provid, int diascred) {
        this.rfc = rfc;
        this.aliasp = aliasp;
        this.rsocial = rsocial;
        this.provid = provid;
        this.diascred = diascred;
    }

    public String getRfc() { return rfc; }

    public String getAliasp() {
        return aliasp;
    }

    public String getRsocial() {
        return rsocial;
    }

    public int getProvid() {
        return provid;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setAliasp(String aliasp) {
        this.aliasp = aliasp;
    }

    public void setRsocial(String rsocial) {
        this.rsocial = rsocial;
    }

    public void setProvid(int provid) {
        this.provid = provid;
    }

    public int getDiascred() {
        return diascred;
    }

    public void setDiascred(int diascred) {
        this.diascred = diascred;
    }
}
