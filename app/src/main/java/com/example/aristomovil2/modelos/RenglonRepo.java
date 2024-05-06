package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class RenglonRepo {
    private int id,prodid,rengs,dacoid;
    private BigDecimal faltan;
    private String codigo,producto,origen,destino;

    public RenglonRepo(int id, int prodid, int rengs, BigDecimal faltan, String codigo, String producto, String origen, String destino,int pDacoid) {
        this.id = id;
        this.prodid = prodid;
        this.rengs = rengs;
        this.faltan = faltan;
        this.codigo = codigo;
        this.producto = producto;
        this.origen = origen;
        this.destino = destino;
        this.dacoid = pDacoid;
    }

    public RenglonRepo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProdid() {
        return prodid;
    }

    public void setProdid(int prodid) {
        this.prodid = prodid;
    }

    public int getRengs() {
        return rengs;
    }

    public void setRengs(int rengs) {
        this.rengs = rengs;
    }

    public BigDecimal getFaltan() {
        return faltan;
    }

    public void setFaltan(BigDecimal faltan) {
        this.faltan = faltan;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDacoid() {
        return dacoid;
    }

    public void setDacoid(int dacoid) {
        this.dacoid = dacoid;
    }
}
