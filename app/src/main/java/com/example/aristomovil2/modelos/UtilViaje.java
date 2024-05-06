package com.example.aristomovil2.modelos;

import java.math.BigDecimal;

public class UtilViaje {
    private Integer id, rid;
    private String nombre,  nombre2;
    private BigDecimal cant;

    public UtilViaje() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public BigDecimal getCant() {
        return cant;
    }

    public void setCant(BigDecimal cant) {
        this.cant = cant;
    }
}
