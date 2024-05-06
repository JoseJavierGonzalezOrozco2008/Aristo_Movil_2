package com.example.aristomovil2.servicio;

import android.app.NotificationManager;
import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.StrictMode;

import androidx.core.app.NotificationCompat;

import com.example.aristomovil2.R;
import com.example.aristomovil2.facade.Servicio;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.example.aristomovil2.utileria.Xml;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class Tiempo extends TimerTask {
    private Context contexto;
    private Servicio servicio;
    private String URL,usuario;

    public Tiempo(Context pContexto,String pIp,String pUsuario) {
        System.out.println(new StringBuilder().append("Creando timer ").append(new Date()).toString());
        this.contexto = pContexto;
        this.URL = "http://"+pIp+":8080/Aws/Aws?wdsl";;
        this.usuario = pUsuario;
    }

    @Override
    public void run() {
        HttpTransportSE androidHttpTransport;
        String resultado;
        try{
            String peticion=servicio.notiConsulta(usuario);
            if(!Libreria.tieneInformacion(peticion)){
                Long tiempodefault = new Date().getTime()-86400000;
                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Calendar ayer= null;
                    ayer = Calendar.getInstance();
                    ayer.set(Calendar.DAY_OF_YEAR,-1);
                    tiempodefault=ayer.getTimeInMillis();
                }else{
                    LocalDate date = LocalDate.now().minusDays(1);
                    //Date ayer=new Date();
                    tiempodefault=date.toEpochDay();
                }*/
                peticion=tiempodefault+",16,"+usuario;
            }
            String NAMESPACE = "http://ws/";
            String METHOD_NAME = "Solicita";
            EnviaPeticion peti=new EnviaPeticion(Enumeradores.Valores.TAREA_NOTIHH);
            peti.setDato1(peticion);
            peti.setDato2("");
            peti.setDato3("");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("usuario", usuario);
            request.addProperty("clave", "SQL");
            request.addProperty("tarea", peti.getTarea().getTareaId());
            request.addProperty("origen", "SQL");
            request.addProperty("dato1", peti.getDato1());
            request.addProperty("dato2", peti.getDato2());
            request.addProperty("dato3", peti.getDato3());
            System.out.println(peti.getTarea().getTareaId()+"->"+peti.getDato1()+"||"+peticion);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            envelope.setOutputSoapObject(request);
            envelope.dotNet=false;
            envelope.implicitTypes=false;
            envelope.encodingStyle = SoapEnvelope.ENC;
            envelope.setAddAdornments(false);

            androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(null, envelope);

            if(envelope.getResponse() instanceof SoapPrimitive){
                SoapPrimitive resulta = (SoapPrimitive) envelope.getResponse();
                if (resulta!=null){
                    resultado = (String) resulta.getValue();
                    System.out.println(resultado);
                    Xml.obtenerInfoWS(peti, resultado, servicio);
                    //falta interpretar la respuesta
                    List<Generica> lista=servicio.traeNotificacion(usuario);
                    if(!lista.isEmpty()){
                        NotificationManager manager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
                        for(Generica gen:lista){
                            if(Libreria.tieneInformacion(gen.getTex1())){
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(contexto,"PEDI001")
                                                .setSmallIcon(R.drawable.inventory)
                                                .setContentTitle("Pedidos pendientes")
                                                .setContentText(gen.getTex1());
                                manager.notify(gen.getEnt1(), builder.build());
                            }
                        }
                    }
                }else {
                    System.out.println("Respuesta no esperada desde el servidor");
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }

    }

    @Override
    public boolean cancel() {
        System.out.println("Cancelando proceso");
        return super.cancel();
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }
}
