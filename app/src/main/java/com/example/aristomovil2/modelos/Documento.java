package com.example.aristomovil2.modelos;

public class Documento {
    private final String foliodi, proveedor, factura, orcofe, estatusdi, operador, vntafolio, surtidor,bulto,prioridad;
    private final int provid, pedido, rengsoc, grupo, surtid,diascred;
    private final float importe, divisa;
    private boolean selected, llevar;
    private int prio;
    private String ruta;

    public Documento(String foliodi, String proveedor, String factura, String orcofe, String estatusdi,
                     String operador, String vntafolio, String surtidor, int provid, int pedido,
                     float importe, int rengsoc, int grupo, float divisa, int surtid,int diascred,String pBulto,String pPrioridad) {
        this.foliodi = foliodi;
        this.proveedor = proveedor;
        this.factura = factura;
        this.orcofe = orcofe;
        this.estatusdi = estatusdi;
        this.operador = operador;
        this.vntafolio = vntafolio;
        this.surtidor = surtidor;
        this.provid = provid;
        this.pedido = pedido;
        this.importe = importe;
        this.selected = false;
        this.rengsoc = rengsoc;
        this.grupo = grupo;
        this.divisa = divisa;
        this.surtid = surtid;
        this.diascred = diascred;
        this.bulto = pBulto;
        this.prioridad = pPrioridad;
    }

    public String getFoliodi() {
        return foliodi;
    }

    public String getProveedor() {
        return proveedor;
    }

    public String getFactura() {
        return factura;
    }

    public String getOrcofe() {
        return orcofe;
    }

    public String getEstatusdi() {
        return estatusdi;
    }

    public String getOperador() {
        return operador;
    }

    public String getVntafolio() {
        return vntafolio;
    }

    public String getSurtidor() {
        return surtidor;
    }

    public int getProvid() {
        return provid;
    }

    public float getDivisa() {
        return divisa;
    }

    public int getPedido() {
        return pedido;
    }

    public float getImporte() {
        return importe;
    }

    public boolean isSelected(){
        return selected;
    }

    public int getRengsoc() {
        return rengsoc;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public int getGrupo() { return grupo; }

    public int getSurtid() {
        return surtid;
    }

    public int getDiascred() {
        return diascred;
    }

    public String getBulto() {
        return bulto;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public int getPrio() {
        return prio;
    }

    public void setPrio(int prio) {
        this.prio = prio;
    }

    public boolean isLlevar() {
        return llevar;
    }

    public void setLlevar(boolean llevar) {
        this.llevar = llevar;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
