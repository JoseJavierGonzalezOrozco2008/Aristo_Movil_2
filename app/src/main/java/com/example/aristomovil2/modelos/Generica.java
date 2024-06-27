package com.example.aristomovil2.modelos;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.aristomovil2.utileria.Libreria;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Generica  implements Serializable {
    private Integer id;
    private Integer  ent1,ent2,ent3,ent4,ent5;
    private String  tex1,tex2,tex3,tex4,tex5;
    private BigDecimal dec1,dec2,dec3,dec4,dec5,dec6,dec7,dec8,dec9,dec10,dec11,dec12,dec13,dec14,dec15;
    private Boolean log1,log2,log3,log4,log5;
    private Date fec1,fec2;

    public Generica(Integer pId) {
        this.id=pId;
    }

    @SuppressLint("Range")
    public static Generica leerCursor(Cursor cursor){
        if(cursor!=null){
            String info=cursor.getString(cursor.getColumnIndex("id"));
            if(Libreria.tieneInformacion(info)){
                Generica retorno =new Generica(Integer.parseInt(info));
                if(!cursor.isNull(cursor.getColumnIndex("ent1"))){
                    retorno.setEnt1(cursor.getInt(cursor.getColumnIndex("ent1")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("ent2"))){
                    retorno.setEnt2(cursor.getInt(cursor.getColumnIndex("ent2")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("ent3"))){
                    retorno.setEnt3(cursor.getInt(cursor.getColumnIndex("ent3")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("ent4"))){
                    retorno.setEnt4(cursor.getInt(cursor.getColumnIndex("ent4")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("ent5"))){
                    retorno.setEnt5(cursor.getInt(cursor.getColumnIndex("ent5")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("tex1"))){
                    retorno.setTex1(cursor.getString(cursor.getColumnIndex("tex1")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("tex2"))){
                    retorno.setTex2(cursor.getString(cursor.getColumnIndex("tex2")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("tex3"))){
                    retorno.setTex3(cursor.getString(cursor.getColumnIndex("tex3")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("tex4"))){
                    retorno.setTex4(cursor.getString(cursor.getColumnIndex("tex4")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("tex5"))){
                    retorno.setTex5(cursor.getString(cursor.getColumnIndex("tex5")));
                }
                if(!cursor.isNull(cursor.getColumnIndex("dec1"))){
                    retorno.setDec1(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec1"))));
                }
                if(!cursor.isNull(cursor.getColumnIndex("dec2"))){
                    retorno.setDec2(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec2"))));
                }
                if(!cursor.isNull(cursor.getColumnIndex("dec3"))){
                    retorno.setDec3(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec3"))));
                }
                if(!cursor.isNull(cursor.getColumnIndex("dec4"))){
                    retorno.setDec4(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec4"))));
                }
                if(!cursor.isNull(cursor.getColumnIndex("dec5"))){
                    retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec5"))));
                }
                if(!cursor.isNull(cursor.getColumnIndex("log1"))){
                    Integer valor=cursor.getInt(cursor.getColumnIndex("log1"));
                    retorno.setLog1(valor==1);
                }
                if(!cursor.isNull(cursor.getColumnIndex("log2"))){
                    retorno.setLog2(cursor.getInt(cursor.getColumnIndex("log2"))==1);
                }
                if(!cursor.isNull(cursor.getColumnIndex("log3"))){
                    retorno.setLog3(cursor.getInt(cursor.getColumnIndex("log3"))==1);
                }
                if(!cursor.isNull(cursor.getColumnIndex("log4"))){
                    retorno.setLog4(cursor.getInt(cursor.getColumnIndex("log4"))==1);
                }
                if(!cursor.isNull(cursor.getColumnIndex("log5"))){
                    retorno.setLog5(cursor.getInt(cursor.getColumnIndex("log5"))==1);
                }
                return retorno;
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public static Generica leerCursor2(Cursor cursor,String pCampos){
        if(cursor!=null){
            String info=cursor.getString(cursor.getColumnIndex("id"));
            String valor="";
            if(Libreria.tieneInformacion(info)){
                Generica retorno =new Generica(Integer.parseInt(info));
                String campos[]=pCampos.split(",");
                for(String llave:campos){
                    switch(llave){
                        case "ent1":
                            retorno.setEnt1(cursor.getInt(cursor.getColumnIndex("ent1")));
                            break;
                        case "ent2":
                            retorno.setEnt2(cursor.getInt(cursor.getColumnIndex("ent2")));
                            break;
                        case "ent3":
                            retorno.setEnt3(cursor.getInt(cursor.getColumnIndex("ent3")));
                            break;
                        case "ent4":
                            retorno.setEnt4(cursor.getInt(cursor.getColumnIndex("ent5")));
                            break;
                        case "ent5":
                            retorno.setEnt5(cursor.getInt(cursor.getColumnIndex("ent5")));
                            break;
                        case "tex1":
                            retorno.setTex1(cursor.getString(cursor.getColumnIndex("tex1")));
                            break;
                        case "tex2":
                            retorno.setTex2(cursor.getString(cursor.getColumnIndex("tex2")));
                            break;
                        case "tex3":
                            retorno.setTex3(cursor.getString(cursor.getColumnIndex("tex3")));
                            break;
                        case "tex4":
                            retorno.setTex4(cursor.getString(cursor.getColumnIndex("tex4")));
                            break;
                        case "tex5":
                            retorno.setTex5(cursor.getString(cursor.getColumnIndex("tex5")));
                            break;
                        case "dec1":
                            retorno.setDec1(new BigDecimal(Libreria.traeInfo(cursor.getString(cursor.getColumnIndex("dec1")),"0")));
                            break;
                        case "dec2":
                            retorno.setDec2(new BigDecimal(Libreria.traeInfo(cursor.getString(cursor.getColumnIndex("dec2")),"0")));
                            break;
                        case "dec3":
                            retorno.setDec3(new BigDecimal(Libreria.traeInfo(cursor.getString(cursor.getColumnIndex("dec3")),"0")));
                            break;
                        case "dec4":
                            retorno.setDec4(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec4"))));
                            break;
                        case "dec5":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec5"))));
                            break;
                        case "dec6":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec6"))));
                            break;
                        case "dec7":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec7"))));
                            break;
                        case "dec8":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec8"))));
                            break;
                        case "dec9":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec9"))));
                            break;
                        case "dec10":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec10"))));
                            break;
                        case "dec11":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec11"))));
                            break;
                        case "dec12":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec12"))));
                            break;
                        case "dec13":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec13"))));
                            break;
                        case "dec14":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec14"))));
                            break;
                        case "dec15":
                            retorno.setDec5(new BigDecimal(cursor.getString(cursor.getColumnIndex("dec15"))));
                            break;
                        case "log1":
                            valor=cursor.getString(cursor.getColumnIndex("log1"));
                            retorno.setLog1(Libreria.getBoolean(valor));
                            break;
                        case "log2":
                            valor=cursor.getString(cursor.getColumnIndex("log2"));
                            retorno.setLog2(Libreria.getBoolean(valor));
                            break;
                        case "log3":
                            valor=cursor.getString(cursor.getColumnIndex("log3"));
                            retorno.setLog3(Libreria.getBoolean(valor));
                            break;
                        case "log4":
                            valor=cursor.getString(cursor.getColumnIndex("log4"));
                            retorno.setLog4(Libreria.getBoolean(valor));
                            break;
                        case "log5":
                            valor=cursor.getString(cursor.getColumnIndex("log5"));
                            retorno.setLog5(Libreria.getBoolean(valor));
                            break;
                    }
                }
                return retorno;
            }
        }
        return null;
    }

    private void colocaValor(String pLlave,Generica pGen){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnt1() {
        return ent1;
    }

    public void setEnt1(Integer ent1) {
        this.ent1 = ent1;
    }

    public Integer getEnt2() {
        return ent2;
    }

    public void setEnt2(Integer ent2) {
        this.ent2 = ent2;
    }

    public Integer getEnt3() {
        return ent3;
    }

    public void setEnt3(Integer ent3) {
        this.ent3 = ent3;
    }

    public Integer getEnt4() {
        return ent4;
    }

    public void setEnt4(Integer ent4) {
        this.ent4 = ent4;
    }

    public Integer getEnt5() {
        return ent5;
    }

    public void setEnt5(Integer ent5) {
        this.ent5 = ent5;
    }

    public String getTex1() {
        return tex1;
    }

    public void setTex1(String tex1) {
        this.tex1 = tex1;
    }

    public String getTex2() {
        return tex2;
    }

    public void setTex2(String tex2) {
        this.tex2 = tex2;
    }

    public String getTex3() {
        return tex3;
    }

    public void setTex3(String tex3) {
        this.tex3 = tex3;
    }

    public String getTex4() {
        return tex4;
    }

    public void setTex4(String tex4) {
        this.tex4 = tex4;
    }

    public String getTex5() {
        return tex5;
    }

    public void setTex5(String tex5) {
        this.tex5 = tex5;
    }

    public BigDecimal getDec1() {
        return dec1;
    }

    public void setDec1(BigDecimal dec1) {
        this.dec1 = dec1;
    }

    public BigDecimal getDec2() {
        return dec2;
    }

    public void setDec2(BigDecimal dec2) {
        this.dec2 = dec2;
    }

    public BigDecimal getDec3() {
        return dec3;
    }

    public void setDec3(BigDecimal dec3) {
        this.dec3 = dec3;
    }

    public BigDecimal getDec4() {
        return dec4;
    }

    public void setDec4(BigDecimal dec4) {
        this.dec4 = dec4;
    }

    public BigDecimal getDec5() {
        return dec5;
    }

    public void setDec5(BigDecimal dec5) {
        this.dec5 = dec5;
    }

    public Boolean getLog1() {
        return log1;
    }

    public void setLog1(Boolean log1) {
        this.log1 = log1;
    }

    public Boolean getLog2() {
        return log2;
    }

    public void setLog2(Boolean log2) {
        this.log2 = log2;
    }

    public Boolean getLog3() {
        return log3;
    }

    public void setLog3(Boolean log3) {
        this.log3 = log3;
    }

    public Boolean getLog4() {
        return log4;
    }

    public void setLog4(Boolean log4) {
        this.log4 = log4;
    }

    public Boolean getLog5() {
        return log5;
    }

    public void setLog5(Boolean log5) {
        this.log5 = log5;
    }

    public Date getFec1() {
        return fec1;
    }

    public void setFec1(Date fec1) {
        this.fec1 = fec1;
    }

    public Date getFec2() {
        return fec2;
    }

    public void setFec2(Date fec2) {
        this.fec2 = fec2;
    }

    public BigDecimal getDec6() {
        return dec6;
    }

    public void setDec6(BigDecimal dec6) {
        this.dec6 = dec6;
    }

    public BigDecimal getDec7() {
        return dec7;
    }

    public void setDec7(BigDecimal dec7) {
        this.dec7 = dec7;
    }

    public BigDecimal getDec8() {
        return dec8;
    }

    public void setDec8(BigDecimal dec8) {
        this.dec8 = dec8;
    }

    public BigDecimal getDec9() {
        return dec9;
    }

    public void setDec9(BigDecimal dec9) {
        this.dec9 = dec9;
    }

    public BigDecimal getDec10() {
        return dec10;
    }

    public void setDec10(BigDecimal dec10) {
        this.dec10 = dec10;
    }

    public BigDecimal getDec11() {
        return dec11;
    }

    public void setDec11(BigDecimal dec11) {
        this.dec11 = dec11;
    }

    public BigDecimal getDec12() {
        return dec12;
    }

    public void setDec12(BigDecimal dec12) {
        this.dec12 = dec12;
    }

    public BigDecimal getDec13() {
        return dec13;
    }

    public void setDec13(BigDecimal dec13) {
        this.dec13 = dec13;
    }

    public BigDecimal getDec14() {
        return dec14;
    }

    public void setDec14(BigDecimal dec14) {
        this.dec14 = dec14;
    }

    public BigDecimal getDec15() {
        return dec15;
    }

    public void setDec15(BigDecimal dec15) {
        this.dec15 = dec15;
    }
}
