package com.example.aristomovil2.modelos.contenedores;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VistaLotes {
    private TextView vista,porCapturar,vfecha,vlote,vcant,vmensaje;
    private Button agregar,guardar;
    private EditText fecha,lote,cant;

    public VistaLotes() {}

    public VistaLotes(TextView vista, TextView porCapturar, Button agregar, EditText fecha, EditText lote, EditText cant,Button pGuarda,TextView vfecha,TextView vlote,TextView vcant) {
        this.vista = vista;
        this.porCapturar = porCapturar;
        this.agregar = agregar;
        this.fecha = fecha;
        this.lote = lote;
        this.cant = cant;
        this.guardar=pGuarda;
        this.vfecha=vfecha;
        this.vlote=vlote;
        this.vcant=vcant;
    }

    public TextView getVista() {
        return vista;
    }

    public void setVista(TextView vista) {
        this.vista = vista;
    }

    public TextView getPorCapturar() {
        return porCapturar;
    }

    public void setPorCapturar(TextView porCapturar) {
        this.porCapturar = porCapturar;
    }

    public Button getAgregar() {
        return agregar;
    }

    public void setAgregar(Button agregar) {
        this.agregar = agregar;
    }

    public EditText getFecha() {
        return fecha;
    }

    public void setFecha(EditText fecha) {
        this.fecha = fecha;
    }

    public EditText getLote() {
        return lote;
    }

    public void setLote(EditText lote) {
        this.lote = lote;
    }

    public EditText getCant() {
        return cant;
    }

    public void setCant(EditText cant) {
        this.cant = cant;
    }

    public Button getGuardar() {
        return guardar;
    }

    public void setGuardar(Button guardar) {
        this.guardar = guardar;
    }

    public TextView getVfecha() {
        return vfecha;
    }

    public void setVfecha(TextView vfecha) {
        this.vfecha = vfecha;
    }

    public TextView getVlote() {
        return vlote;
    }

    public void setVlote(TextView vlote) {
        this.vlote = vlote;
    }

    public TextView getVcant() {
        return vcant;
    }

    public void setVcant(TextView vcant) {
        this.vcant = vcant;
    }

    public TextView getVmensaje() {
        return vmensaje;
    }

    public void setVmensaje(TextView vmensaje) {
        this.vmensaje = vmensaje;
    }
}
