package com.example.aristomovil2.Acrividades;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ListadoAdapter;
import com.example.aristomovil2.facade.Estatutos;
import com.example.aristomovil2.modelos.Detviaje;
import com.example.aristomovil2.modelos.UtilViaje;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Listado extends ActividadBase {
    private String v_Folio,v_Anden;
    private Integer v_Estatus,v_Tipo;
    private ListView v_Listado;
    private Detviaje registroDviaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Bundle extras = getIntent().getExtras();
        String titulo = extras.getString("titulo");
        v_Folio = extras.getString("folio");
        v_Tipo = extras.getInt("tipo",1);
        v_Estatus = extras.getInt("estatus",-1);
        inicializarActividad(titulo);
        v_Listado=findViewById(R.id.lista_listado);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if(v_Tipo == 1){
                    v_Estatus = 41;
                    wsLlamaCarga();
                }else{
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        actualizaListado();
    }

    private void actualizaListado(){
        ListadoAdapter adapter=null;
        if(v_Tipo==1){
            List<Detviaje> detalle=servicio.traeDviajes();
            if(!detalle.isEmpty()){
                adapter=new ListadoAdapter(detalle,this,v_Tipo,v_Estatus);
                v_Listado.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else{
                v_Listado.setAdapter(null);
                TextView texto=findViewById(R.id.emptyListado);
                v_Listado.setEmptyView(texto);
            }
        }else if(v_Tipo==3){
            List<UtilViaje> detalle=servicio.traeUtilViaje();
            System.out.println("tamaño "+ detalle.size());
            if(detalle.isEmpty()){
                v_Listado.setAdapter(null);
                TextView texto=findViewById(R.id.emptyListado);
                v_Listado.setEmptyView(texto);
                return;
            }
            adapter=new ListadoAdapter(detalle,this,v_Tipo,0);
            v_Listado.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    public void dlgAndenes(String xmlAndenes){
        if(Libreria.tieneInformacion(xmlAndenes)){
            Dialog dialogo = new Dialog(this, R.style.Dialog);
            //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
            dialogo.setContentView(R.layout.dialogo_andenes);
            dialogo.setTitle("Andenes de salida");
            String[] andenes = xmlAndenes.replace("|",";").split(";");
            String[] datos;
            RadioGroup nuevo=dialogo.findViewById(R.id.radioAndenes);
            Button guarda=dialogo.findViewById(R.id.btnGuardaAnden);
            CheckBox auto=dialogo.findViewById(R.id.swAuto);
            TextView titulo=dialogo.findViewById(R.id.tv_anden_titulo);
            auto.setVisibility(View.GONE);
            //auto.setOnCheckedChangeListener((view,b)->onSeleccinaSwith(view,b));
            //ubicacion="";
            titulo.setText("Selecciona un Anden de Salida");
            for (String anden:andenes){
                datos=anden.split(",");
                RadioButton nuevoRadio = new RadioButton(this);
                LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                nuevoRadio.setLayoutParams(params);
                nuevoRadio.setText(datos[2]);
                nuevoRadio.setTag(anden);
                nuevoRadio.setOnClickListener(view -> onSeleccinaAnden(view));
                /*if(primer){
                    primer=false;
                    nuevoRadio.setChecked(true);
                    ubicacion =datos[0];
                }*/
                nuevo.addView(nuevoRadio);
            }
            ((RadioButton)nuevo.getChildAt(0)).setChecked(true);
            ((RadioButton)nuevo.getChildAt(0)).callOnClick();
            guarda.setOnClickListener(view -> {wsDviajBaja();dialogo.dismiss();});
            //dialogo.setOnDismissListener(dialogInterface -> {dialogo.dismiss();});
            dialogo.show();
        }
    }

    public void onSeleccinaAnden(View v){
        boolean marcado = ((RadioButton) v).isChecked();
        if(marcado){
            String datos=v.getTag()+"";
            if(Libreria.tieneInformacion(datos)){
                String[] vector=datos.split(",");
                v_Anden=vector[0];
            }else{
                muestraMensaje(datos,R.color.colorExitoInsertado);
            }
           // muestraMensaje(datos,R.color.colorExitoInsertado);
        }
    }

    public void wsDviajBaja(){
        String xml= MessageFormat.format("<linea><viaje>{0}</viaje><envio>{1}</envio><usua>{2}</usua><anden>{3}</anden></linea>",v_Folio,registroDviaje.getEnvio(),usuarioID,v_Anden);
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_BAJA,"SQL","SQL",xml,"","");
    }

    public void wsDviajAndenes(Detviaje pDetviaje){
        registroDviaje = pDetviaje;
        //String xml= MessageFormat.format("<linea><viaje>{0}</viaje><envio>{1}</envio><usua>{2}</usua><anden>{3}</anden></linea>",v_Folio,pDetviaje.getEnvio(),usuarioID,v_Anden);
        peticionWS(Enumeradores.Valores.TAREA_TRAE_ANDENES_SURT,"SQL","SQL","","","");
    }

    public void wsLlamaCarga(){
        servicio.borraDatosTabla(Estatutos.TABLA_DVIAJE);
        peticionWS(Enumeradores.Valores.TAREA_DVIAJ_LISTA,"SQL","SQL","<linea><viaje>"+v_Folio+"</viaje><estt>"+v_Estatus+"</estt></linea>","","");
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        if(output!=null){
            switch(output.getTarea()){
                case TAREA_TRAE_ANDENES_SURT:
                    ContentValues values=(ContentValues)output.getExtra1();
                    String andenes=values.getAsString("anexo");
                    dlgAndenes(andenes);
                    break;
                case TAREA_VIAJ_BAJA:
                    if(output.getExito()){
                        wsLlamaCarga();
                    }else{
                        muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                    }
                    break;
                case TAREA_DVIAJ_LISTA:
                    if(output.getExito()){
                        if(v_Tipo == 1){
                            if(v_Estatus==41){
                                finish();
                            }else{
                                actualizaListado();
                            }
                        }
                    }else{
                        muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                    }
                    break;
            }

        }
    }
}