package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class Viaje {
    private Integer id ;
    private String viajfolio ;
    private String folio ;
    private String ruta ;
    private String operador ;
    private String feprog ;
    private Integer rengs ;
    private Integer pzas ;
    private BigDecimal vol ;
    private Integer cdestinos ;
    private Integer patrid ;
    private Integer choferid ;
    private String chofer ;
    private String vehiculo ;
    private String texto ;
    private String estatus ;
    private Integer tipo ;

    public Viaje() {
        id=-1;
        rengs = pzas = cdestinos = patrid = choferid = tipo = 0;
        viajfolio = folio = ruta = operador = feprog = chofer = vehiculo = estatus = texto = "";
        vol = BigDecimal.ZERO;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getViajfolio() {
        return viajfolio;
    }

    public void setViajfolio(String viajfolio) {
        this.viajfolio = viajfolio;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getFeprog() {
        return feprog;
    }

    public void setFeprog(String feprog) {
        this.feprog = feprog;
    }

    public Integer getRengs() {
        return rengs;
    }

    public void setRengs(Integer rengs) {
        this.rengs = rengs;
    }

    public Integer getPzas() {
        return pzas;
    }

    public void setPzas(Integer pzas) {
        this.pzas = pzas;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public Integer getCdestinos() {
        return cdestinos;
    }

    public void setCdestinos(Integer cdestinos) {
        this.cdestinos = cdestinos;
    }

    public Integer getPatrid() {
        return patrid;
    }

    public void setPatrid(Integer patrid) {
        this.patrid = patrid;
    }

    public Integer getChoferid() {
        return choferid;
    }

    public void setChoferid(Integer choferid) {
        this.choferid = choferid;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }
}
