package com.example.aristomovil2.utileria;

import android.content.Context;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Configuracion;
import com.example.aristomovil2.R;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Impresora extends AsyncTask<Void, Void, Boolean>  {
    private static final String PRINTER_IP_ADDRESS = "192.168.0.11";
    private static final int PRINTER_PORT = 9100;
    private static final String DATA_TO_PRINT = "Prueba impresión";

    public String IP_RED;

    public String TEXT;
    public int PUERTO_RED;

    public int n_salto;
    public Impresora(String IP_RED, String TEXT, int PUERTO_RED, int n_salto) {
        this.IP_RED = IP_RED;
        this.TEXT = TEXT;
        this.PUERTO_RED = PUERTO_RED;
        this.n_salto = n_salto;
    }

    public Impresora() {
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket socket = new Socket(IP_RED, PUERTO_RED);
            OutputStream outputStream = socket.getOutputStream();

            byte[] centerCommand = new byte[] { 0x1B, 'a', 0x01 };
            byte[] lineFeedCommand = new byte[] { 0x1B, 'd', 3 };
            byte[] charsetCommand = new byte[] { 0x1B, 0x74, 0x10 };
            byte[] comandoTamañoNormal = new byte[]{0x1B, 0x21, 0x00};
            outputStream.write(charsetCommand);

            ServicioImpresionTicket x = new ServicioImpresionTicket();
            x = x.impresionTicketServicio(x,TEXT, n_salto);
            outputStream.write(new String(x.getImprVal()).getBytes("ISO-8859-15"));
            byte[] cutCommand = new byte[] {29,86,66,3};
            outputStream.write(cutCommand);

            // Flushing y cerrando el OutputStream
            outputStream.flush();
            outputStream.close();
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            System.out.println("Exito");
        } else {
            System.out.println("Error");
        }
    }


    public String getIP_RED() {
        return IP_RED;
    }

    public void setIP_RED(String IP_RED) {
        this.IP_RED = IP_RED;
    }

    public int getPUERTO_RED() {
        return PUERTO_RED;
    }

    public void setPUERTO_RED(int PUERTO_RED) {
        this.PUERTO_RED = PUERTO_RED;
    }

    public String getTEXT() {
        return TEXT;
    }

    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }
}
