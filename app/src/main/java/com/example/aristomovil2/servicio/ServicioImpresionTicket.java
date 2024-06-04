package com.example.aristomovil2.servicio;

import static com.example.aristomovil2.utileria.Libreria.tieneInformacion;
import static com.example.aristomovil2.utileria.Libreria.upper;

import android.annotation.SuppressLint;

import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.utileria.Libreria;

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
                            case "QRc":sIT.generaQR(texto,'c');break;
                            case "QRm":sIT.generaQR(texto,'m');break;
                            case "QRg":sIT.generaQR(texto,'g');break;
                            case "CT":sIT.cortTick(); break;
                        }
                    }
                }
            }
        }
        return sIT;
    }

    public void generaQR(String texto, char tam) {
        Byte tama = switch (tam) {
            case 'c' -> 0x08;
            case 'm' -> 0x0A;
            case 'g' -> 0x0C;
            default -> 0xA;
        };

        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x01);

        int store_len = texto.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);

        //Selecciona el modelo
        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x28);
        listaBytes.add((byte) 0x6B);
        listaBytes.add((byte) 0x04);
        listaBytes.add((byte) 0x00);
        listaBytes.add((byte) 0x31);
        listaBytes.add((byte) 0x41);
        listaBytes.add((byte) 0x32); // n1 [49 x31, modelo 1] [50 x32, modelo 2] [51 x33, micro qr]
        listaBytes.add((byte) 0x00);

        //Asigna el tamaño del módulo
        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x28);
        listaBytes.add((byte) 0x6B);
        listaBytes.add((byte) 0x03);
        listaBytes.add((byte) 0x00);
        listaBytes.add((byte) 0x31);
        listaBytes.add((byte) 0x43);
        listaBytes.add(tama);//Tamaño QR: c = 8, m = 10, g = 12

        //Asigna n para corrección de errores
        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x28);
        listaBytes.add((byte) 0x6B);
        listaBytes.add((byte) 0x03);
        listaBytes.add((byte) 0x00);
        listaBytes.add((byte) 0x31);
        listaBytes.add((byte) 0x45);
        listaBytes.add((byte) 0x31); // n: 48 -> 7% , 49 -> 15%, 50 -> 25%, 51 -> 30%

        //Almacena los datos del símbolo del código QR (d1...dk) en el área de almacenamiento de símbolos.
        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x28);
        listaBytes.add((byte) 0x6B);
        listaBytes.add(store_pL);
        listaBytes.add(store_pH);
        listaBytes.add((byte) 0x31);
        listaBytes.add((byte) 0x50);
        listaBytes.add((byte) 0x30);

        //d1...dk
        for (byte b : texto.getBytes()) {
            listaBytes.add(b);
        }

        //Imprimir los datos del símbolo en el área de almacenamiento de símbolos
        listaBytes.add((byte) 0x1D);
        listaBytes.add((byte) 0x28);
        listaBytes.add((byte) 0x6B);
        listaBytes.add((byte) 0x03);
        listaBytes.add((byte) 0x00);
        listaBytes.add((byte) 0x31);
        listaBytes.add((byte) 0x51);
        listaBytes.add((byte) 0x30);

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

    public void cortTick(){
        ArrayList<Byte> listaBytes = new ArrayList<>();
        listaBytes.add((byte) 0x1B);
        listaBytes.add((byte) 'a');
        listaBytes.add((byte) 0x00);

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
