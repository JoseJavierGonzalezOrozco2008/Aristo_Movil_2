package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class RenglonCalcula {
    private int id,prodid;
    private BigDecimal requerido,disponible,asignado;
    private String codigo,producto,origen,destino,origena,destinoa;

    public RenglonCalcula(int id, int prodid, BigDecimal requerido, BigDecimal disponible, BigDecimal asignado, String codigo, String producto, String origen, String destino, String origena, String destinoa) {
        this.id = id;
        this.prodid = prodid;
        this.requerido = requerido;
        this.disponible = disponible;
        this.asignado = asignado;
        this.codigo = codigo;
        this.producto = producto;
        this.origen = origen;
        this.destino = destino;
        this.origena = origena;
        this.destinoa = destinoa;
    }

    public RenglonCalcula() {
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

    public BigDecimal getRequerido() {
        return requerido;
    }

    public void setRequerido(BigDecimal requerido) {
        this.requerido = requerido;
    }

    public BigDecimal getDisponible() {
        return disponible;
    }

    public void setDisponible(BigDecimal disponible) {
        this.disponible = disponible;
    }

    public BigDecimal getAsignado() {
        return asignado;
    }

    public void setAsignado(BigDecimal asignado) {
        this.asignado = asignado;
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

    public String getOrigena() {
        return origena;
    }

    public void setOrigena(String origena) {
        this.origena = origena;
    }

    public String getDestinoa() {
        return destinoa;
    }

    public void setDestinoa(String destinoa) {
        this.destinoa = destinoa;
    }
}
