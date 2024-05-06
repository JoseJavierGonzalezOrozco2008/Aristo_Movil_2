package com.example.aristomovil2.servicio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.example.aristomovil2.facade.Servicio;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import org.json.JSONObject;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeoutException;


/**
 * Clase que se encarga de realizar las peticion de los Web Services (Se trabaja con un
 * servidor a la vez dependiendo de la IP que se encuentre en el archivo llamado "preferencias").
 */
public class MetodoRf extends AsyncTask<Void, Void, EnviaPeticion>{
    private String URI= "http://192.168.1.165:8080/RestService/";
    public EnviaPeticion enviaPeticion;
    public Finish termina;
    public Servicio servicio;
    @SuppressLint("StaticFieldLeak")
    public Context context;

    private ProgressDialog wsdialog;

    public MetodoRf(String ip){
        URI ="http://"+ip+"/RestService/";
    }
    public MetodoRf(){}

    /**
     * Metodo que regresa a la actividad que lanzó la petición al servidor la respuesta de este
     * @param peticion (EnviaPeticion) respuesta del servidor
     */
    protected void onPostExecute(EnviaPeticion peticion) {
        try {
            termina.Finish(peticion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Salida de la peticion");
        //wsdialog = ProgressDialog.show(context, "Conectando", "Espere un momento...");
    }

    /**
     * Metodo encargado de realizar la peticion al servidor, en la cual el metodo recibe una
     * cantidad de parametros para asignar a la petición y asi el servidor nos responda con
     * la informacion correspondiente.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected EnviaPeticion doInBackground(Void... voids) {
        HttpTransportSE androidHttpTransport;
        String resultado;
        HttpURLConnection myConnection = null;
        try{
            String destino=this.URI+enviaPeticion.getTarea().getTablaBD()+enviaPeticion.getDato1();
            System.out.println(destino);
            URL servidorMaxse = new URL(destino);
            myConnection = (HttpURLConnection) servidorMaxse.openConnection();
            myConnection.setRequestMethod("GET");
            myConnection.setRequestProperty("dato1", enviaPeticion.getDato1());

            System.out.println("Tarea: " + enviaPeticion.getTarea().getTareaId());
            System.out.println("Dato1: " + enviaPeticion.getDato1());
            System.out.println("Dato2: " + enviaPeticion.getDato2());
            System.out.println("Dato3: " + enviaPeticion.getDato3());
            myConnection.connect();
            if (myConnection.getResponseCode() == 200) {
                InputStream ini = myConnection.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(ini));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //ini.close();
                // print result
                System.out.println(response.toString());
                if(Libreria.tieneInformacion(response.toString())){
                    JSONObject jason=new JSONObject(response.toString());
                    enviaPeticion.setExito(jason.getBoolean("exito"));
                    enviaPeticion.setMensaje(jason.getString("msg"));
                }else{
                    System.out.println("enco"+myConnection.getContentEncoding());
                    int lenghtOfFile = myConnection.getContentLength();                    ;
                    InputStream input = new BufferedInputStream(myConnection.getInputStream(),
                            lenghtOfFile);
                    // Output stream
                    OutputStream output = new FileOutputStream(Environment
                            .getExternalStorageDirectory().toString()
                            + "/AristoMovil pruebas.apk");
                    byte data[] = new byte[1024];
                    int count;
                    long total=0;
                    while ((count = input.read(data)) != -1) {
                        total += count;

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                }
            } else {
                enviaPeticion.setExito(false);
                enviaPeticion.setMensaje("No se recibio respuesta del servidor para la tarea "+enviaPeticion.getTarea().name());
            }

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Excepcion de IO");
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el servidor");
        } catch (Exception e){
            System.out.println("Excepcion General");
            e.printStackTrace();
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el Servidor");
        }finally {
            if(myConnection!=null){
                myConnection.disconnect();
                System.out.println("Finalizo peticion por restful");
            }
        }

        return enviaPeticion;
    }
}
