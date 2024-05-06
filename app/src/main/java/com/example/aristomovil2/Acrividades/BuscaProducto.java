package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.facade.Estatutos.TABLA_DCARRO;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.BqdaProdAdapter;
import com.example.aristomovil2.adapters.DcarritoAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.resources.TextAppearance;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BuscaProducto extends ActividadBase {
    private Integer v_estacion,v_cliente;
    private String v_vntafolio,v_nombrecliente,v_seleccion;
    private Boolean v_metpago;
    private EditText v_consulta;
    private CheckBox v_existencia;
    private ListView v_listaproductos;
    private TextView v_sinregistros;
    private LinearLayout v_lyCambio1,v_lyCapt01,v_lyCapt02;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_producto);
        inicializarActividad2("Folio  Tipo","Cliente");
        Bundle extras = getIntent().getExtras();
        v_vntafolio = extras.getString("folio");
        v_estacion = extras.getInt("estacion");
        v_metpago = extras.getBoolean("metpago");
        v_nombrecliente = extras.getString("cliente");
        v_cliente = extras.getInt("clteid",-1);
        v_consulta = findViewById(R.id.bqdaConsulta);
        v_existencia = findViewById(R.id.bqdaExis);
        v_listaproductos = findViewById(R.id.bqdaProductos);
        v_sinregistros = findViewById(R.id.bqdaSinRegistros);
        v_seleccion = "";

        v_consulta.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                wsBuscaProducto();
                v_consulta.setText("");
            }
            return false;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent data = new Intent();
                data.putExtra("actividad",2);
                data.putExtra("codigo",v_seleccion);
                setResult(RESULT_OK,data);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        colocaTitulo();
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();
        switch (output.getTarea()){
            case TAREA_VNTABQDPRODUCTO:
                List<Generica> renglones = servicio.traeGenericaBqdaProd();
                BqdaProdAdapter adapter = new BqdaProdAdapter(renglones, this);
                v_listaproductos.setAdapter(adapter);
                v_listaproductos.setEmptyView(v_sinregistros);
                break;

        }
        if(obj != null){

        }
    }


    private void wsBuscaProducto(){
        if(!Libreria.tieneInformacion(v_consulta.getText().toString())){
            muestraMensaje("No puede dejar vacio",R.drawable.mensaje_error);
            return;
        }
        servicio.borraDatosTabla(TABLA_DCARRO);
        String criterio = v_consulta.getText().toString();
        ContentValues mapa=new ContentValues();
        mapa.put("folioventa",v_vntafolio);
        mapa.put("estacion",v_estacion);
        mapa.put("usuaid",usuarioID);
        mapa.put("criterio",criterio);
        mapa.put("cliente",v_cliente);
        mapa.put("conexistencia",v_existencia.isChecked());
        mapa.put("tipo",0);
        String xml= Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_VNTABQDPRODUCTO,"SQL","SQL",xml,"","");
    }

    private void colocaTitulo(){
        int longitud = v_vntafolio.length();
        boolean esnuevo = v_vntafolio.equalsIgnoreCase("NUEVA");
        String folio = esnuevo ? v_vntafolio : ("C*"+v_vntafolio.substring(longitud-4,longitud));
        String linea1,linea2="";
        if(esHorizontal()){
            linea1 = MessageFormat.format("{0} {1} {2}", folio,esnuevo ? "" : (v_metpago ? "Contado":"Credito"),v_nombrecliente);
        }else{
            linea1= MessageFormat.format("{0} {1}", folio,esnuevo ? "" : (v_metpago ? "Contado":"Credito"));
            linea2= v_nombrecliente;
        }
        actualizaToolbar2(linea1,linea2);
    }

    public void setSeleccion(String pSeleccion){
        v_seleccion=pSeleccion;
    }
}