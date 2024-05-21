package com.example.aristomovil2.servicio;

import static com.example.aristomovil2.utileria.Libreria.tieneInformacion;
import static com.example.aristomovil2.utileria.Libreria.upper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.BarcodeFormat;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class ServicioImpresionTicket  {

    private byte[] imprVal;


    public ServicioImpresionTicket() {
        this.imprVal = new byte[0];
    }

    public String impresionBultos(Bulto pBulto, ServicioImpresionTicket pServicio, String ptitulo, String pUsuario, Boolean pCodbarras, Boolean pDetalles, int pEspacios)  {
        String contenido = "";
        contenido += "Contenedor,T1|";
        ptitulo = ptitulo.length() > 15 ? ptitulo.substring(0, 15) : ptitulo;
        contenido += upper(ptitulo) + ",T2|";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = "";
        try {
            if (tieneInformacion(date))
                date = formatter.format(Objects.requireNonNull(parser.parse(pBulto.getFecha())));
        } catch (ParseException e) {
            date = "Sin fecha";
            e.printStackTrace();
        }
        //impresora.addLine(date);
        String v_usuario = pUsuario;
        if (pUsuario.length() >= 15) {
            v_usuario = pUsuario.substring(0, 14);
        }
        v_usuario = Libreria.RPAD(v_usuario, " ", 16);
        contenido += v_usuario + date + ",T1|";
        contenido += "Rengs: " + pBulto.getRengs() + " Piezas: " + pBulto.getPiezas() + ",T1|";

        contenido += "Folio: " + pBulto.getDcinfolio() + ",T1";
        contenido += "|.,T1";
        if (pCodbarras) {
            contenido += "|" + pBulto.getContenedor() + ",**";
        }
        contenido += "|"+pBulto.getContenedor()+",T2";

        if(pDetalles && tieneInformacion(pBulto.getDetalles())){
            contenido += "|--------------------------------,T1";
            contenido += "|Código       Producto       Cant,T1";
            contenido += "|--------------------------------,T1";
            String detall[]=pBulto.getDetalles().split(",");
            String renglon_det;
            for(String detalle:detall){
                renglon_det=detalle.substring(0,32);
                contenido += "|"+renglon_det+",T1";
            }
        }

        return contenido;
    }
    public ServicioImpresionTicket impresionTicketServicio(ServicioImpresionTicket sIT, String cont, int n) throws UnsupportedEncodingException {
        if(tieneInformacion(cont)){
            String detall[]=cont.split(Pattern.quote("|")),lexico[];
            String texto;
            for(String linea:detall){
                lexico=linea.split(",");
                if(lexico.length>1){
                    texto=lexico[0].replace(";",",");
                    if(tieneInformacion(texto)){
                        switch (lexico[1]){
                            case "T1":sIT.textNormCentrado(texto, n);break;
                            case "T2":sIT.tituloGrande(texto,n);break;
                            case "**":sIT.generaCodigoBarras(texto,n);break;
                            case "T1n":sIT.textNormIzq(texto,n);break;
                            case "T2n":sIT.textNormBold(texto,n);break;
                            case "T1w":sIT.textNormDer(texto,n);break;
                            case "CT":sIT.cortTick(); break;
                        }
                    }
                }
            }
        }
        return sIT;
    }

    public void cortTick(){
        ArrayList<Byte> listaBytes = new ArrayList<>();

        listaBytes.add((byte) 29);
        listaBytes.add((byte) 86);
        listaBytes.add((byte) 65);
        listaBytes.add((byte) 1);


        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }
    public void textNormIzq(String texto, int n) {
        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x00);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x21);
        listaBytes.add((byte) 0x00);

        for (byte b : texto.getBytes()) {
            listaBytes.add(b);
        }

        //listaBytes.add((byte) 0x0D);
        //listaBytes.add((byte) 0x0A);
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }

    public void textNormCentrado(String texto, int n){
        ArrayList<Byte> listaBytes = new ArrayList<>();

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x01);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x21);
        listaBytes.add((byte) 0x00);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x01);

        for (byte b : texto.getBytes()) {
            listaBytes.add(b);
        }

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x00);

        //listaBytes.add((byte) 0x0D);
        //listaBytes.add((byte) 0x0A);
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }

    public void textNormBold(String texto, int n){
        byte[] boldOnCommand = {0x1B, 0x45, 0x01};

        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x01);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x21);
        listaBytes.add((byte) 0x00);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x01);

        for (byte b : texto.getBytes()) {
            listaBytes.add(b);
        }

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x00);

        //listaBytes.add((byte) 0x0D);
       // listaBytes.add((byte) 0x0A);
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }
    public void textNormDer(String texto, int n) throws UnsupportedEncodingException {

        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 2);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x21);
        listaBytes.add((byte) 0x00);

        for (byte b : texto.getBytes()) {
            listaBytes.add(b);
        }

        //listaBytes.add((byte) 0x0D);
        //listaBytes.add((byte) 0x0A);
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }

    public void tituloGrande(String titulo , int n)  {

        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x01);

        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x21);
        listaBytes.add((byte) 0x11);

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x01);

        for (byte b : titulo.getBytes()) {
            listaBytes.add(b);
        }

        //listaBytes.add((byte) 0x0D);
       // listaBytes.add((byte) 0x0A);
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }

        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }


    public void generaCodigoBarras(String valCB, int n) {
        ArrayList<Byte> listaBytes = new ArrayList<>();

        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x01);

        listaBytes.add((byte) 29);
        listaBytes.add((byte) 107);
        listaBytes.add((byte) 72);
        listaBytes.add((byte) valCB.getBytes().length);
        for (byte b : valCB.getBytes()) {
            listaBytes.add(b);
        }
        byte[] bytesArray = new byte[listaBytes.size()];
        for (int i = 0; i < listaBytes.size(); i++) {
            bytesArray[i] = listaBytes.get(i);
        }
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 0x64);
        listaBytes.add((byte) 1);

        //this.imprVal = bytesArray;
        byte[] aux = new byte[this.imprVal.length + bytesArray.length];
        System.arraycopy(this.imprVal, 0, aux, 0, this.imprVal.length);
        System.arraycopy(bytesArray, 0, aux, this.imprVal.length, bytesArray.length);
        this.imprVal = aux;
    }


    public byte[] getImprVal() {
        return imprVal;
    }

    public void setImprVal(byte[] imprVal) {
        this.imprVal = imprVal;
    }
}
