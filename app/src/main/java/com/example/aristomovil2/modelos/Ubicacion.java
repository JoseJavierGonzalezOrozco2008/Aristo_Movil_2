package com.example.aristomovil2.modelos;

public class Ubicacion {
    private String codigo, disponible, estatus, ubicacion, asifid;
    private int ubiid;
    private boolean selected;

    public Ubicacion(String codigo, String disponible, String estatus, int ubiid, String ubicacion, String asifid) {
        this.codigo = codigo;
        this.disponible = disponible;
        this.estatus = estatus;
        this.ubiid = ubiid;
        this.ubicacion = ubicacion;
        this.asifid = asifid;
        selected = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDisponible() {
        return disponible;
    }

    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getUbiid() {
        return ubiid;
    }

    public void setUbiid(int ubiid) {
        this.ubiid = ubiid;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getAsifid() {
        return asifid;
    }

    public void setAsifid(String asifid) {
        this.asifid = asifid;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
