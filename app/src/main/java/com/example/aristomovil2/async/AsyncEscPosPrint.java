package com.example.aristomovil2.async;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosCharsetEncoding;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import java.lang.ref.WeakReference;

public abstract class AsyncEscPosPrint extends AsyncTask<AsyncEscPosPrinter, Integer, Integer> {
    protected final static int FINISH_SUCCESS = 1;
    protected final static int FINISH_NO_PRINTER = 2;
    protected final static int FINISH_PRINTER_DISCONNECTED = 3;
    protected final static int FINISH_PARSER_ERROR = 4;
    protected final static int FINISH_ENCODING_ERROR = 5;
    protected final static int FINISH_BARCODE_ERROR = 6;
    protected final static int PROGRESS_CONNECTING = 1;
    protected final static int PROGRESS_CONNECTED = 2;
    protected final static int PROGRESS_PRINTING = 3;
    protected final static int PROGRESS_PRINTED = 4;

    protected ProgressDialog dialog;
    protected WeakReference<Context> weakContext;
    protected boolean deSalida;

    /**
     * Constructor del servico de impresion
     * @param context Contexto de la aplicacion
     *@param pDeSalida Si la actividad finaliza despues de imprimir
     */
    public AsyncEscPosPrint(Context context,boolean pDeSalida) {
        this.weakContext = new WeakReference<>(context);
        this.deSalida=pDeSalida;
    }

    /**
     * Tarea que manda a imprimir en segundo plano
     * @param printersData Impresora
     * @return .
     */
    protected Integer doInBackground(AsyncEscPosPrinter... printersData) {
        if (printersData.length == 0)
            return AsyncEscPosPrint.FINISH_NO_PRINTER;

        this.publishProgress(AsyncEscPosPrint.PROGRESS_CONNECTING);
        AsyncEscPosPrinter printerData = printersData[0];
        try {
            DeviceConnection deviceConnection = printerData.getPrinterConnection();
            if(deviceConnection == null)
                return AsyncEscPosPrint.FINISH_NO_PRINTER;

            EscPosPrinter printer = new EscPosPrinter(
                    deviceConnection,
                    printerData.getPrinterDpi(),
                    printerData.getPrinterWidthMM(),
                    printerData.getPrinterNbrCharactersPerLine(),
                    new EscPosCharsetEncoding("windows-1252", 16)
            );

            this.publishProgress(AsyncEscPosPrint.PROGRESS_PRINTING);
            printer.printFormattedTextAndCut(printerData.getTextToPrint());
            this.publishProgress(AsyncEscPosPrint.PROGRESS_PRINTED);

        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_PRINTER_DISCONNECTED;
        } catch (EscPosParserException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_PARSER_ERROR;
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_ENCODING_ERROR;
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
            return AsyncEscPosPrint.FINISH_BARCODE_ERROR;
        }

        return AsyncEscPosPrint.FINISH_SUCCESS;
    }

    /**
     * Inicializa el dialogo de status antes de ejecutar la tarea de impresion
     */
    protected void onPreExecute() {
        if (this.dialog == null) {
            Context context = weakContext.get();
            System.out.println("Va de salida?---------"+deSalida+"-------------"+context);
            if (context == null || deSalida)
                return;

            this.dialog = new ProgressDialog(context);
            this.dialog.setTitle("Imprimiendo...");
            this.dialog.setMessage("...");
            this.dialog.setProgressNumberFormat("%1d / %2d");
            this.dialog.setCancelable(false);
            this.dialog.setIndeterminate(false);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.show();
        }
    }

    /**
     * Actualiza el estatus de la impresion
     * @param progress Estatus de la impresion
     */
    protected void onProgressUpdate(Integer... progress) {
        if(this.dialog ==null || deSalida){
            return ;
        }
        switch (progress[0]) {
            case AsyncEscPosPrint.PROGRESS_CONNECTING:
                this.dialog.setMessage("Conectando impresora...");
                break;
            case AsyncEscPosPrint.PROGRESS_CONNECTED:
                this.dialog.setMessage("Impresora conectada...");
                break;
            case AsyncEscPosPrint.PROGRESS_PRINTING:
                this.dialog.setMessage("Imprimiendo...");
                break;
            case AsyncEscPosPrint.PROGRESS_PRINTED:
                this.dialog.setMessage("Impresion finalizada...");
                this.dialog.dismiss();
                break;
        }
        this.dialog.setProgress(progress[0]);
        this.dialog.setMax(4);
    }

    /**
     * Muestra un dialogo con el resultado de la tarea de impresion
     * @param result El resultado
     */
    protected void onPostExecute(Integer result) {
        if(this.dialog != null){
            this.dialog.dismiss();
        }
        this.dialog = null;

        Context context = weakContext.get();
        if (context == null ) {
            return;
        }
        String titulo="Inesperado",mensaje="Se ha recibido una respuesta inesperada";
        switch (result) {
            case AsyncEscPosPrint.FINISH_SUCCESS:
                titulo = "Exito";
                mensaje = "Impresion finalizada";
                break;
            case AsyncEscPosPrint.FINISH_NO_PRINTER:
                titulo = "Sin Impresora";
                mensaje = "No se encontraron impresoras conectadas.";
                break;
            case AsyncEscPosPrint.FINISH_PRINTER_DISCONNECTED:
                titulo = "Conexion perdida";
                mensaje = "No se puede conectar con la impresora.";
                break;
            case AsyncEscPosPrint.FINISH_PARSER_ERROR:
                titulo = "Formato de texto invalido";
                mensaje = "Parece que hay un problema de sintaxis.";
                break;
            case AsyncEscPosPrint.FINISH_ENCODING_ERROR:
                titulo = "Error de codificiacion";
                mensaje = "El caracter de codificacion seleccionado retorno un error.";
                break;
            case AsyncEscPosPrint.FINISH_BARCODE_ERROR:
                titulo = "Codigo de barras invalido";
                mensaje = "Los datos enviados para ser convertidos a codigo de barras o codigo QR no correctos.";
                break;
        }
        if(!deSalida){
            new AlertDialog.Builder(context)
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .show();
        }else{
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
        }
    }
}
