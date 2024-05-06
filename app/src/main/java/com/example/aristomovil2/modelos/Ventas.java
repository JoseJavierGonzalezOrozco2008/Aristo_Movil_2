package com.example.aristomovil2.modelos;

public class Ventas {
    private final String folio;
    private final String consumidor;
    private final String domicilio;
    private final String fecha;
    private final String notas;
    private final String nombrecliente;
    private final String tienecredito;
    private final String credito;
    private final String titulo;
    private final int tipo;
    private final int usuario;
    private final int cliente;
    private final int estacion;
    private final int porcocinar;
    private final int telefono;
    private boolean pideFac;
    private String regimen,rfc;
    private Integer empr;

    public Ventas(String folio, int tipo, int usuario, String consumidor, String domicilio, int cliente, int estacion, int porcocinar, String fecha, int telefono, String notas, String nombrecliente, String tienecredito, String credito, String titulo) {
        this.folio = folio;
        this.tipo = tipo;
        this.usuario = usuario;
        this.consumidor = consumidor;
        this.domicilio = domicilio;
        this.cliente = cliente;
        this.estacion = estacion;
        this.porcocinar = porcocinar;
        this.fecha = fecha;
        this.telefono = telefono;
        this.notas = notas;
        this.nombrecliente = nombrecliente;
        this.tienecredito = tienecredito;
        this.credito = credito;
        this.titulo = titulo;
    }

    public String getFolio() {
        return folio;
    }

    public int getTipo() {
        return tipo;
    }

    public int getUsuario() {
        return usuario;
    }

    public String getConsumidor() {
        return consumidor;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public int getCliente() {
        return cliente;
    }

    public int getEstacion() {
        return estacion;
    }

    public int getPorcocinar() {
        return porcocinar;
    }

    public String getFecha() {
        return fecha;
    }

    public int getTelefono() {
        return telefono;
    }

    public String getNotas() {
        return notas;
    }

    public String getNombrecliente() {
        return nombrecliente;
    }

    public String getTienecredito() {
        return tienecredito;
    }

    public String getCredito() {
        return credito;
    }

    public String getTitulo() {return titulo;}

    public boolean isPideFac() {
        return pideFac;
    }

    public void setPideFac(boolean pideFac) {
        this.pideFac = pideFac;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Integer getEmpr() {
        return empr;
    }

    public void setEmpr(Integer empr) {
        this.empr = empr;
    }
}
