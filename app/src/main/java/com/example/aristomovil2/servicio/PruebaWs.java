package com.example.aristomovil2.servicio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.example.aristomovil2.facade.Servicio;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.example.aristomovil2.utileria.Xml;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Clase que se encarga de realizar las peticion de los Web Services (Se trabaja con un
 * servidor a la vez dependiendo de la IP que se encuentre en el archivo llamado "preferencias").
 */
public class PruebaWs extends AsyncTask<Void, Void, EnviaPeticion>{
    private String URL= "http://192.168.1.91:8080/Aws/Aws?wdsl";
    public EnviaPeticion enviaPeticion;
    public Finish termina;
    public Servicio servicio;
    @SuppressLint("StaticFieldLeak")
    public Context context;
    public int timeOut=10000;//en milisegundos

    private ProgressDialog wsdialog;

    public PruebaWs(String ip){
        URL ="http://"+ip+":8080/Aws/Aws?wdsl";
    }
    public PruebaWs(){}

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
        String resultado,respuesta="";
        int contExito=0,contFracaso=0,vueltas=50;
        List<Long> tiempos=new ArrayList();
        Date feini,fefin;

        //try{
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


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            envelope.setOutputSoapObject(request);
            envelope.dotNet=false;
            envelope.implicitTypes=false;
            envelope.encodingStyle = SoapEnvelope.ENC;
            envelope.setAddAdornments(false);

            for(int i=0;i<vueltas;i++) {
                androidHttpTransport = new HttpTransportSE(URL,timeOut);
                feini = new Date();
                try {
                    androidHttpTransport.call(null, envelope);
                    if (envelope.getResponse() instanceof SoapPrimitive) {
                        SoapPrimitive resulta = (SoapPrimitive) envelope.getResponse();
                        if (resulta != null) {
                            resultado = (String) resulta.getValue();
                            if(Libreria.tieneInformacion(resultado)){
                                contExito++;
                            }else{
                                contFracaso++;
                            }
                        } else {
                            contFracaso++;
                        }
                    } else if (enviaPeticion.getTarea().getTipoRespuesta().equals("texto")) {
                        contFracaso++;
                    } else {
                        contFracaso++;
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Excepción de conexion");
                    System.out.println(e);
                    contFracaso++;
                } catch (ConnectException e) {
                    System.out.println("Excepción de Conexion");
                    System.out.println(e);
                    contFracaso++;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Excepción de IO");
                    System.out.println(e);
                    contFracaso++;
                } catch (XmlPullParserException e) {
                    System.out.println("Excepción de Parse");
                    System.out.println(e);
                    contFracaso++;
                } catch (Exception e) {
                    System.out.println("Excepción General");
                    e.printStackTrace();
                    System.out.println(e);
                    contFracaso++;
                }
                fefin = new Date();
                tiempos.add(fefin.getTime()-feini.getTime());
                if(contFracaso>=3){
                    break;
                }
            }
        ContentValues pContenido=new ContentValues();
        if(!tiempos.isEmpty() && contFracaso<3){
             long sumatiempos=0,promedio,milisegundos;
             for(Long tiempo:tiempos){
                 sumatiempos+=tiempo;
             }
             promedio=sumatiempos/tiempos.size();
             //milisegundos=TimeUnit.MILLISECONDS.convert(promedio,TimeUnit.MILLISECONDS);
             //respuesta = "tiempo total:"+sumatiempos+" promedio:"+promedio+" tiempo:"+(milisegundos/1000)+" seg"+" exitos:"+contExito+" fracasos:"+contFracaso;
             //System.out.println(promedio);
             if(promedio<=100){
                 respuesta = "Conexión Buena";
             }else if(promedio>100 && promedio<=300){
                 respuesta = "Conexión Regular";
             }else if(promedio>300){
                 respuesta = "Conexión Mejorable";
             }
             respuesta += "\n--------------\nIntentos:"+vueltas+"\nCon éxito:"+contExito;
             pContenido.put("éxito",contExito == vueltas);
        }else{
            respuesta = "Sin Conexión con el Servidor" ;
            pContenido.put("exito",false);
        }

        pContenido.put("mensaje",respuesta);
        enviaPeticion.setExito((Boolean)pContenido.get("éxito"));
        enviaPeticion.setMensaje(pContenido.get("mensaje")+"");
        enviaPeticion.setExtra1(pContenido);
        return enviaPeticion;
    }
}
