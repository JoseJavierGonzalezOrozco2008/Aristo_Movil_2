package com.example.aristomovil2.async;

import android.app.ProgressDialog;
import android.content.Context;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.example.aristomovil2.utileria.EnviaPeticion;


public class AsyncBluetoothEscPosPrint extends AsyncEscPosPrint {
    private ProgressDialog wsdialog;
    private Context context;
    /**
     * Constrcutor de la clase
     * @param context El contexto de la aplicacion
     * @param pDeSalida Si la actividad finaliza despues de imprimir
     */
    public AsyncBluetoothEscPosPrint(Context context,boolean pDeSalida) {
        super(context,pDeSalida);
        //this.context=context;
    }

    /**
     * Tarea de impresion que se ejecuta en segundo plano
     * @param printersData Imprsora
     * @return .
     */
    protected Integer doInBackground(AsyncEscPosPrinter... printersData) {
        if (printersData.length == 0) {
            return AsyncEscPosPrint.FINISH_NO_PRINTER;
        }

        AsyncEscPosPrinter printerData = printersData[0];
        DeviceConnection deviceConnection = printerData.getPrinterConnection();

        this.publishProgress(AsyncEscPosPrint.PROGRESS_CONNECTING);

        if (deviceConnection == null) {
            printersData[0] = new AsyncEscPosPrinter(
                    BluetoothPrintersConnections.selectFirstPaired(),
                    printerData.getPrinterDpi(),
                    printerData.getPrinterWidthMM(),
                    printerData.getPrinterNbrCharactersPerLine()
            );
            printersData[0].setTextToPrint(printerData.getTextToPrint());
        } else {
            try {
                deviceConnection.connect();
            } catch (EscPosConnectionException e) {
                e.printStackTrace();
            }
        }

        return super.doInBackground(printersData);
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
