package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class Detviaje {
    private String envio;
    private String dcin;
    private String estatus;
    private String texto;
    private String surtidor;
    private String cliente;
    private String ubica;
    private Integer id;
    private Integer clteid;
    private Integer rengs;
    private Integer pzas;
    private Integer totreng;
    private Integer renglon;
    private BigDecimal vol;

    public Detviaje() {
    }

    public String getEnvio() {
        return envio;
    }

    public void setEnvio(String envio) {
        this.envio = envio;
    }

    public String getDcin() {
        return dcin;
    }

    public void setDcin(String dcin) {
        this.dcin = dcin;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(String surtidor) {
        this.surtidor = surtidor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getUbica() {
        return ubica;
    }

    public void setUbica(String ubica) {
        this.ubica = ubica;
    }

    public Integer getClteid() {
        return clteid;
    }

    public void setClteid(Integer clteid) {
        this.clteid = clteid;
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

    public Integer getTotreng() {
        return totreng;
    }

    public void setTotreng(Integer totreng) {
        this.totreng = totreng;
    }

    public Integer getRenglon() {
        return renglon;
    }

    public void setRenglon(Integer renglon) {
        this.renglon = renglon;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
