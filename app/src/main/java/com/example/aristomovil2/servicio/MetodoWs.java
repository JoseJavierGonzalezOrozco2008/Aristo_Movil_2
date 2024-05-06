package com.example.aristomovil2.servicio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.Ubicaciones;
import com.example.aristomovil2.facade.Servicio;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.example.aristomovil2.utileria.Xml;
import com.example.aristomovil2.ActividadBase;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Clase que se encarga de realizar las peticion de los Web Services (Se trabaja con un
 * servidor a la vez dependiendo de la IP que se encuentre en el archivo llamado "preferencias").
 */
public class MetodoWs extends AsyncTask<Void, Void, EnviaPeticion>{
    private String URL= "http://192.168.1.91:8080/Aws/Aws?wdsl";
    public EnviaPeticion enviaPeticion;
    public Finish termina;
    public Servicio servicio;
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public int timeOut=30000;//en milisegundos

    private ProgressDialog wsdialog;

    public MetodoWs(String ip){
        URL ="http://"+ip+":8080/Aws/Aws?wdsl";
    }
    public MetodoWs(){}

    /**
     * Metodo que regresa a la actividad que lanzó la petición al servidor la respuesta de este
     * @param peticion (EnviaPeticion) respuesta del servidor
     */
    protected void onPostExecute(EnviaPeticion peticion) {
        wsdialog.dismiss();
        try {
            termina.Finish(peticion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        wsdialog = ProgressDialog.show(context, "Conectando", "Espere un momento...");
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

        try{
            String NAMESPACE = "http://ws/";
            String METHOD_NAME = "Solicita";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("usuario", enviaPeticion.getUsuario());
            request.addProperty("clave", enviaPeticion.getClave());
            request.addProperty("tarea", enviaPeticion.getTarea().getTareaId());
            request.addProperty("origen", enviaPeticion.getOrigen());
            request.addProperty("dato1", enviaPeticion.getDato1());
            request.addProperty("dato2", enviaPeticion.getDato2());
            request.addProperty("dato3", enviaPeticion.getDato3());

            System.out.println("Tarea: " + enviaPeticion.getTarea().getTareaId());
            System.out.println("Dato1: " + enviaPeticion.getDato1());
            System.out.println("Dato2: " + enviaPeticion.getDato2());
            System.out.println("Dato3: " + enviaPeticion.getDato3());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            envelope.setOutputSoapObject(request);
            envelope.dotNet=false;
            envelope.implicitTypes=false;
            envelope.encodingStyle = SoapEnvelope.ENC;
            envelope.setAddAdornments(false);

            androidHttpTransport = new HttpTransportSE(URL,timeOut);
            androidHttpTransport.call(null, envelope);

            if(envelope.getResponse() instanceof SoapPrimitive){
                SoapPrimitive resulta = (SoapPrimitive) envelope.getResponse();
                if (resulta!=null){
                    resultado = (String) resulta.getValue();
                    System.out.println(resultado);
                    Xml.obtenerInfoWS(enviaPeticion, resultado, servicio);
                }else {
                    enviaPeticion.setExito(false);
                    enviaPeticion.setMensaje("Servicio En Blanco");
                }
            }else if(enviaPeticion.getTarea().getTipoRespuesta().equals("texto")){
                enviaPeticion.setExito(false);
                enviaPeticion.setMensaje("No se recibió respuesta del servidor");
                enviaPeticion.setExtra1(new ContentValues());
            }else {
                enviaPeticion.setExito(false);
                enviaPeticion.setMensaje("No se recibió respuesta del servidor para la tarea "+enviaPeticion.getTarea().getTareaId());
            }

        } catch(SocketTimeoutException e){
            System.out.println("Excepcion de conexion");
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el Servidor,tiempo de conexión demasiado largo");
        } catch (ConnectException e){
            System.out.println("Excepcion de coneccion");
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el Servidor,No se estableción conexión con servidor");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Excepción de IO");
            System.out.println(e);
            enviaPeticion.setExito(false);
            System.err.println();
            enviaPeticion.setMensaje("Problemas con el servidor,No se estableció conexión");
        } catch (XmlPullParserException e){
            System.out.println("Excepción de Parse");
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el Servidor,Formato de respuesta inválido");
        } catch (Exception e){
            System.out.println("Excepción General");
            e.printStackTrace();
            System.out.println(e);
            enviaPeticion.setExito(false);
            enviaPeticion.setMensaje("Problemas con el Servidor");
        }
        if(enviaPeticion.getTarea().getTareaId() == 478 && !enviaPeticion.getExito()){
            final MediaPlayer mp;
            mp = MediaPlayer.create(context, R.raw.error);
            mp.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());//quitar si causa problemas
            mp.start();

        }
        return enviaPeticion;
    }
}
