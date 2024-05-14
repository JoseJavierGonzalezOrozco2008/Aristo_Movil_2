package com.example.aristomovil2.Acrividades;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.servicio.NumeroTelefonicoFormat;
import com.example.aristomovil2.utileria.Libreria;

import java.util.List;

public class Recargas extends ActividadBase {
    private ListView v_recarga,v_saldos;
    private LinearLayout v_referencia;
    private TextView v_Info;
    private ImageButton v_cancel;
    private String v_compa,v_tele,v_codigo,v_refe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recargas);
        inicializarActividad2("","");
        List<Generica> lista=servicio.traeDcatGenerica(51);

        v_recarga = findViewById(R.id.recaCompanias);
        v_saldos = findViewById(R.id.recaTelefonos);
        v_referencia = findViewById(R.id.recaReferencia);
        v_Info = findViewById(R.id.recaInfo);
        v_cancel = findViewById(R.id.recaCancel);
        EditText num1 = findViewById(R.id.recaNum1);
        EditText num2 = findViewById(R.id.recaNum2);
        num1.addTextChangedListener(new NumeroTelefonicoFormat(num1));
        num2.addTextChangedListener(new NumeroTelefonicoFormat(num2));
        Button aceptar = findViewById(R.id.recaAceptar);
        GenericaAdapter adapRecarga=new GenericaAdapter(lista,this,1);
        v_recarga.setAdapter(adapRecarga);
        v_compa=v_tele="";
        v_cancel.setOnClickListener(view -> {
            if(Libreria.tieneInformacion(v_tele)){
                v_saldos.setVisibility(View.VISIBLE);
                v_referencia.setVisibility(View.GONE);
                v_tele= "";
                recaInformacion();
            }else if(Libreria.tieneInformacion(v_compa)){
                v_recarga.setVisibility(View.VISIBLE);
                v_cancel.setVisibility(View.GONE);
                v_saldos.setVisibility(View.GONE);
                v_Info.setVisibility(View.GONE);
                v_compa= "";
                recaInformacion();
            }
        });
        aceptar.setOnClickListener(view -> {
            String dato = num1.getText().toString();
            String dato1 = num2.getText().toString();
            dato = dato.replace("-","");
            dato = dato.replace("(","");
            dato = dato.replace(")","");
            dato1 = dato1.replace("-","");
            dato1 = dato1.replace("(","");
            dato1 = dato1.replace(")","");

            if(!Libreria.tieneInformacion(dato)){
                muestraMensaje("Captura información",R.drawable.mensaje_error);
                return;
            }
            if(dato.length()>10){
                muestraMensaje("El número es demasiado largo",R.drawable.mensaje_error);
                return;
            }
            if(!dato.equalsIgnoreCase(dato1)){
                muestraMensaje("No coinciden los números",R.drawable.mensaje_error);
                return;
            }
            v_refe = dato;
            onBackPressed();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent data = new Intent();
                data.putExtra("actividad",2);
                if(Libreria.tieneInformacion(v_codigo) && Libreria.tieneInformacion(v_refe)){
                    data.putExtra("codigo",v_codigo+","+v_refe);
                }
                setResult(RESULT_OK,data);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void recaTelefono(Generica pGen){
        v_recarga.setVisibility(View.GONE);
        v_cancel.setVisibility(View.VISIBLE);
        v_saldos.setVisibility(View.VISIBLE);
        v_Info.setVisibility(View.VISIBLE);
        List<Generica> lista=servicio.traeDcatGenerica(pGen.getId());
        GenericaAdapter adapRecarga=new GenericaAdapter(lista,this,1);
        v_saldos.setAdapter(adapRecarga);
        v_compa= pGen.getTex1();
        recaInformacion();
    }

    public void recaReferencia(Generica pGen){
        v_saldos.setVisibility(View.GONE);
        v_referencia.setVisibility(View.VISIBLE);
        v_tele = pGen.getTex1();
        v_codigo = pGen.getTex2();
        recaInformacion();
    }

    public void recaInformacion(){
        String men = v_compa;
        if(Libreria.tieneInformacion(v_tele)){
            men = v_compa+" "+v_tele;
        }
        v_Info.setText(men);
    }
}