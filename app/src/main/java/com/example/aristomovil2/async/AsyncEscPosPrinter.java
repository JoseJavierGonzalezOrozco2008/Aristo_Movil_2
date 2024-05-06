package com.example.aristomovil2.async;

import com.dantsu.escposprinter.EscPosPrinterSize;
import com.dantsu.escposprinter.connection.DeviceConnection;

public class AsyncEscPosPrinter extends EscPosPrinterSize {
    private final DeviceConnection printerConnection;
    private String textToPrint = "";

    /**
     * Constructor de la Impresora
     * @param printerConnection Impresora a la que se va a cconectar
     * @param printerDpi DPi de la impresora
     * @param printerWidthMM Width en milimetros de la impresora
     * @param printerNbrCharactersPerLine Numero de caracteres por linea
     */
    public AsyncEscPosPrinter(DeviceConnection printerConnection, int printerDpi, float printerWidthMM, int printerNbrCharactersPerLine){
        super(printerDpi, printerWidthMM, printerNbrCharactersPerLine);
        this.printerConnection = printerConnection;
    }

    /**
     * Retorna la conexion a la impresora
     * @return Coneccion
     */
    public DeviceConnection getPrinterConnection() {
        return this.printerConnection;
    }

    /**
     * Establece el texto a imprimir
     * @param textToPrint El texto
     * @return Impresora
     */
    public AsyncEscPosPrinter setTextToPrint(String textToPrint) {
        this.textToPrint = textToPrint;
        return this;
    }

    /**
     * Retorna el texto a imprimir
     * @return El texto
     */
    public String getTextToPrint() {
        return this.textToPrint;
    }
}
