package com.example.aristomovil2.modelos;

import android.annotation.SuppressLint;
import android.database.Cursor;

import java.math.BigDecimal;

public class Ubikprod {
    /*(codigo,cant,ubicacion,tipo,producto,ubikid)*/
    private String codigo,ubicacion,tipo,producto;
    private BigDecimal cant;
    private Integer ubikid,id;

    public Ubikprod(Integer id,String codigo, String ubicacion, String tipo, String producto, BigDecimal cant, Integer ubikid) {
        this.codigo = codigo;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.producto = producto;
        this.cant = cant;
        this.ubikid = ubikid;
        this.id = id;
    }

    public Ubikprod() {}

    @SuppressLint("Range")
    public static Ubikprod nuevoRegistro(Cursor contenido){
        String campo="";
        if(contenido!=null){
            Ubikprod temp=new Ubikprod();
            campo=contenido.getString(contenido.getColumnIndex("cant"));
            temp.setCant(new BigDecimal(campo));
            temp.setId(contenido.getInt(contenido.getColumnIndex("id")));
            temp.setCodigo(contenido.getString(contenido.getColumnIndex("codigo")));
            temp.setTipo(contenido.getString(contenido.getColumnIndex("tipo")));
            temp.setUbicacion(contenido.getString(contenido.getColumnIndex("ubicacion")));
            temp.setUbikid(contenido.getInt(contenido.getColumnIndex("ubikid")));
            temp.setProducto(contenido.getString(contenido.getColumnIndex("producto")));
            return temp;
        }
        return null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public BigDecimal getCant() {
        return cant;
    }

    public void setCant(BigDecimal cant) {
        this.cant = cant;
    }

    public Integer getUbikid() {
        return ubikid;
    }

    public void setUbikid(Integer ubikid) {
        this.ubikid = ubikid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
