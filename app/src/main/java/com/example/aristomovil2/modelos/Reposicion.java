package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class Reposicion {
    private Integer acomid,acomrengs,encarro;
    private String acomdcinfolio ,ubicacion,claveubicacion,dcinfefin,usuario,movimiento,dcin,prov;
    private boolean selected,captura,esreposicion;
    private BigDecimal piezas;

    public Reposicion(Integer acomid, Integer acomrengs, String acomdcinfolio, String ubicacion, String claveubicacion, String dcinfefin,String pUsuario,Integer pEncarro) {
        this.acomid = acomid;
        this.acomrengs = acomrengs;
        this.acomdcinfolio = acomdcinfolio;
        this.ubicacion = ubicacion;
        this.claveubicacion = claveubicacion;
        this.dcinfefin = dcinfefin;
        this.usuario = pUsuario;
        this.encarro = pEncarro;
    }

    public Integer getAcomid() {
        return acomid;
    }

    public void setAcomid(Integer acomid) {
        this.acomid = acomid;
    }

    public Integer getAcomrengs() {
        return acomrengs;
    }

    public void setAcomrengs(Integer acomrengs) {
        this.acomrengs = acomrengs;
    }

    public String getAcomdcinfolio() {
        return acomdcinfolio;
    }

    public void setAcomdcinfolio(String acomdcinfolio) {
        this.acomdcinfolio = acomdcinfolio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getClaveubicacion() {
        return claveubicacion;
    }

    public void setClaveubicacion(String claveubicacion) {
        this.claveubicacion = claveubicacion;
    }

    public String getDcinfefin() {
        return dcinfefin;
    }

    public void setDcinfefin(String dcinfefin) {
        this.dcinfefin = dcinfefin;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCaptura() {
        return captura;
    }

    public void setCaptura(boolean captura) {
        this.captura = captura;
    }

    public boolean isEsreposicion() {
        return esreposicion;
    }

    public void setEsreposicion(boolean esreposicion) {
        this.esreposicion = esreposicion;
    }

    public Integer getEncarro() {
        return encarro;
    }

    public void setEncarro(Integer encarro) {
        this.encarro = encarro;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public BigDecimal getPiezas() {
        return piezas;
    }

    public void setPiezas(BigDecimal piezas) {
        this.piezas = piezas;
    }

    public String getDcin() {
        return dcin;
    }

    public void setDcin(String dcin) {
        this.dcin = dcin;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }
}
