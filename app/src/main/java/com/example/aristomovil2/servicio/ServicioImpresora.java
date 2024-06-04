package com.example.aristomovil2.servicio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.aristomovil2.async.AsyncEscPosPrinter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ServicioImpresora {
    private final AsyncEscPosPrinter printer;
    private String textToPrint;
    private final Context context;

    public static String CENTER = "[C]";
    public static String LEFT = "[L]";
    public static String RIGHT = "[R]";
    public static String END_LINE = "\n";
    public static String TEXT_NORMAL = "'normal'";
    public static String TEXT_WIDE = "'wide'";
    public static String TEXT_TALL = "'tall'";
    public static String TEXT_BIG = "'big'";

    public  ServicioImpresora(DeviceConnection printerConnection, Context c){
        this.printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);
        this.textToPrint = LEFT + END_LINE;
        this.context = c;
    }

    public AsyncEscPosPrinter Imprimir(){
        return  this.printer.setTextToPrint(this.textToPrint);
    }

    /**
     * Agrega una linea al ticket con estilos
     * @param text Texto a imprimir
     * @param alignment Alineacion del texto
     * @param size Tamaño del texto
     * @param bold Especifica si el texto debe ser bold
     * @param underline Especifica si el texto debe estar subrayado
     */
    public void addLine(String text, String alignment, String size, boolean bold, boolean underline){
        this.textToPrint += alignment + (bold? "<b>":"") + (underline? "<u>":"") + "<font size=" + size + ">" +
                text + "</font>" + (underline? "</u>":"") + (bold? "</b>":"") + END_LINE;
    }

    /**
     * Agrega una linea al ticket
     * @param text Texto a imprimir
     * @param alignment Alineacion del texto
     * @param size Tamaño del texto
     */
    public void addLine(String text, String alignment, String size){
        this.addLine(text, alignment, size, false, false);
    }

    /**
     * Agrega una linea al ticket con una alineacion determinada
     * @param text Texto a imprimir
     * @param alignment Alineacion del texto
     */
    public void addLine(String text, String alignment){
        this.addLine(text, alignment, TEXT_NORMAL);
    }

    /**
     * Agrega una linea centrada al ticket
     * @param text Texto a imprimir
     */
    public void addLine(String text){
        this.addLine(text, CENTER);
    }

    /**
     * Agrega un titulo al ticket
     * @param text Texto del titulo
     * @param bold Determina si el texto debe ser bold
     */
    public void addTitle(String text, boolean bold){
        this.addLine(text, CENTER, TEXT_BIG, bold, false);
    }

    /**
     * Agrega un titulo al ticket con texto bold
     * @param text Texto del titulo
     */
    public void addTitle(String text){
        this.addTitle(text, true);
    }

    /**
     * Agrega una linea vacia
     */
    public void addEndLine(){
        //this.textToPrint += LEFT += END_LINE;
        this.addLine(" ", CENTER);
    }

    /**
     * Agrega un determinado numero de lineas vacias
     * @param n El numero de saltos de linea
     */
    public void addEndLine(int n){
        for(int i = 0; i<n; i++)
            this.addLine(" ", CENTER);
    }

    /**
     * Agrega un codigo de barras con la alineacion y tamaño espeficifado.
     * @param codigo Codigo de barras
     * @param alignment Alineacion del codigo
     * @param width Tamaño del codigo
     */
    public void addBarcode(String codigo, String alignment, int width){
        this.textToPrint += alignment + "<barcode type='128' width='" + width + "' text='below'>"
                + codigo + "</barcode>" + END_LINE;
    }

    /**
     * Agrega un codgio de barras con la alineacion especificada
     * @param codigo Codigo de barras
     * @param alignment Alineacion del codigo
     */
    public void addBarcode(String codigo, String alignment){
        this.addBarcode(codigo, alignment, 40);
    }

    /**
     * Agrega un codigo de barras centrado y del tamaño especificado
     * @param codigo Codigo de barras
     * @param width Tamaño del codigo
     */
    public void addBarcode(String codigo, int width){
        this.addBarcode(codigo, CENTER, width);
    }

    /**
     * Agrega un codigo de barras centrado y con tamaño 40
     * @param codigo Codgio de barras
     */
    public void addBarcode(String codigo){
        this.addBarcode(codigo, CENTER, 40);
    }

    /**
     * Añade una imagen con la alineacion especificada
     * @param resource ID de la imagen
     * @param alignment Alineacion de la imagen
     */
    public void addImage(int resource, String alignment){
        this.textToPrint += alignment + "<img>" +
                PrinterTextParserImg.bitmapToHexadecimalString(this.printer, this.context.getResources().getDrawableForDensity(resource, DisplayMetrics.DENSITY_MEDIUM))
                +"</img>" + END_LINE;
    }

    /**
     * Agrega una imagen centrada
     * @param resource ID de la imagen
     */
    public void addImage(int resource){
        this.addImage(resource, CENTER);
    }

    /**
     * Añade una imagen a partir de un Bitmap
     * @param img El bitmap a imprimir
     */
    public void addImage(Bitmap img){
        this.textToPrint +=  CENTER + "<img>" + PrinterTextParserImg.bitmapToHexadecimalString(this.printer, img) + "</img>" + END_LINE;
    }
    public void addQRCode(String qrData, String alignment, int size) {
        this.textToPrint += alignment + "<qrcode size='" + size + "'>"
                + qrData + "</qrcode>" + END_LINE + END_LINE;
    }

    public void addQRCode(String qrData, String alignment) {
        this.addQRCode(qrData, alignment, 25);
    }

    /**
     * Añade un codigo QR
     * @param qrData Los datos del codigo QR
     * @param alignment Tipo de alineación
     * @param size Tipo de tamaño
     */
    // size: 'c' chico , 'm' mediano, 'g' grande
    public void addQRCode(String qrData, String alignment, String size) {
        if(size != null && size.equals("c")){
            this.addQRCode(qrData, alignment, 20);
        } else if(size != null && size.equals("m")){
            this.addQRCode(qrData, alignment, 25);
        } else if(size != null && size.equals("g")){
            this.addQRCode(qrData, alignment, 30);
        } else {
            this.addQRCode(qrData, alignment, 25);
        }
    }

    public void addQRCode(String qrData, int size) {
        this.addQRCode(qrData, CENTER, size);
    }

    public void addQRCode(String qrData) {
        this.addQRCode(qrData, CENTER);
    }
    /**
     * Añade un codigo de barras para impresoras que no soportan en comando <barcode></barcode>
     * @param data Los datos del codigo de barras
     * @param width El ancho del codigo
     * @param height El alto del codigo
     * @param format El formato del codigo de barras
     */
    public void addBarcodeImage(String data, int width, int height, BarcodeFormat format){
        Bitmap bmp = null;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, format, width, height);
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bmp.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }

        this.addImage(bmp);
    }
}
