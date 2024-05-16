package com.example.aristomovil2.modelos;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.aristomovil2.utileria.Libreria;

public class Cuenta {
    private Integer id,domiid,coloid,cuclid,clteid;
    private String info,tel,calle,exterior,interior,colonia,muni,estado,cp,cuenta,nombre;

    public Cuenta(Integer pId) {
        this.id = pId;
    }

    @SuppressLint("Range")
    public static Cuenta leerCursor(Cursor cursor){
        return leerCursor(cursor,"domiid,coloid,cuclid,info,tel,calle,exterior,interior,colonia,muni,estado,cp,clteid,cuenta,nombre");
    }

    @SuppressLint("Range")
    public static Cuenta leerCursor(Cursor cursor, String pCampos){
        if(cursor!=null){
            String info=cursor.getString(cursor.getColumnIndex("id"));
            if(Libreria.tieneInformacion(info)){
                Cuenta retorno =new Cuenta(Integer.parseInt(info));
                String campos[] = pCampos.split(",");
                for(String llave:campos){
                    switch(llave){
                        case "domiid":
                            retorno.setDomiid(traeInteger(cursor,"domiid"));
                            break;
                        case "coloid":
                            retorno.setColoid(traeInteger(cursor,"coloid"));
                            break;
                        case "cuclid":
                            retorno.setCuclid(traeInteger(cursor,"cuclid"));
                            break;
                        case "clteid":
                            retorno.setClteid(traeInteger(cursor,"clteid"));
                            break;
                        case "cp":
                            retorno.setCp(traeString(cursor,"cp"));
                            break;
                        case "info":
                            retorno.setInfo(traeString(cursor,"info"));
                            break;
                        case "calle":
                            retorno.setCalle(traeString(cursor,"calle"));
                            break;
                        case "exterior":
                            retorno.setExterior(traeString(cursor,"exterior"));
                            break;
                        case "interior":
                            retorno.setInterior(traeString(cursor,"interior"));
                            break;
                        case "colonia":
                            retorno.setColonia(traeString(cursor,"colonia"));
                            break;
                        case "muni":
                            retorno.setMuni(traeString(cursor,"muni"));
                            break;
                        case "estado":
                            retorno.setEstado(traeString(cursor,"estado"));
                            break;
                        case "cuenta":
                            retorno.setCuenta(traeString(cursor,"cuenta"));
                            break;
                        case "nombre":
                            retorno.setNombre(traeString(cursor,"nombre"));
                            break;
                        case "tel":
                            retorno.setTel(traeString(cursor,"tel"));
                            break;
                    }
                }
                return retorno;
            }
        }
        return null;
    }

    private static String  traeString(Cursor pCursor,String pCampo){
        Integer columna=pCursor.getColumnIndex(pCampo);
        if(columna>0){
            return pCursor.getString(columna);
        }
        return "";
    }

    private static int  traeInteger(Cursor pCursor,String pCampo){
        Integer columna=pCursor.getColumnIndex(pCampo);
        if(columna>0){
            return pCursor.getInt(columna);
        }
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDomiid() {
        return domiid;
    }

    public void setDomiid(Integer domiid) {
        this.domiid = domiid;
    }

    public Integer getColoid() {
        return coloid;
    }

    public void setColoid(Integer coloid) {
        this.coloid = coloid;
    }

    public Integer getCuclid() {
        return cuclid;
    }

    public void setCuclid(Integer cuclid) {
        this.cuclid = cuclid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getExterior() {
        return exterior;
    }

    public void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public String getInterior() {
        return interior;
    }

    public void setInterior(String interior) {
        this.interior = interior;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getMuni() {
        return muni;
    }

    public void setMuni(String muni) {
        this.muni = muni;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getClteid() {
        return clteid;
    }

    public void setClteid(Integer clteid) {
        this.clteid = clteid;
    }
}

