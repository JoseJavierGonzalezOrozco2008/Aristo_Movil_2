package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.CuentaAdapter;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.modelos.Cuenta;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;
import java.util.List;

public class Cuentas extends ActividadBase {
    private Integer v_clteid,v_cuclid,v_domiid,v_coloid;
    private Spinner v_colonia;
    private EditText v_calle,v_ext,v_int,v_refe,v_tele,v_cp,v_nombre,v_cuenta;
    private TextView v_estado,v_Muni;
    private boolean v_nuevo;
    private ArrayList<Colonia> v_listacolonia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);
        inicializarActividad2("Folio  Tipo","Cliente");
        Bundle extras = getIntent().getExtras();
        v_clteid = extras.getInt("clteid",-1);
        v_cuclid = extras.getInt("cuclid",0);
        String cliente = extras.getString("cliente");
        v_nuevo = v_cuclid==0;
        v_nombre = findViewById(R.id.cuclNombre);
        v_cuenta = findViewById(R.id.cuclCuenta);
        v_colonia = findViewById(R.id.clteColonia);
        v_calle  = findViewById(R.id.clteCalle);
        v_ext  = findViewById(R.id.clteExt);
        v_int  = findViewById(R.id.clteint);
        //v_refe  = findViewById(R.id.clteRef);
        v_tele  = findViewById(R.id.clteTEl);
        v_cp  = findViewById(R.id.clteCp);
        v_estado = findViewById(R.id.clteEstado);
        v_Muni = findViewById(R.id.clteMunicipio);

        Button nuevo = findViewById(R.id.cuclNuevo);

        v_cp.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if(!Libreria.tieneInformacion(v_cp.getText().toString())){
                    muestraMensaje("no puede estar vacio",R.drawable.mensaje_error);
                    return false;
                }
                traeColonias(v_cp.getText().toString());
                hideKeyboard(view);
                return true;
            }return false;
        });

        nuevo.setOnClickListener(view -> wsCuclGuarda());

        actualizaToolbar2("Cuentas auxiliares",cliente);
        actualizaListado();
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_COLONIAS_CP:{
                    actualizaColonias();
                    break;
                }
                case TAREA_GUARDA_CUENTA:
                    if(output.getExito()){
                        onBackPressed();
                    }
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito :R.drawable.mensaje_error);
                    break;
            }
        } else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void actualizaColonias(){
        v_listacolonia = servicio.traeColonias();
        ArrayList<String> coloniasNombres = new ArrayList<>();

        for(Colonia c : v_listacolonia)
            coloniasNombres.add(c.getColonia());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,  R.layout.item_spinner, R.id.item_spinner, coloniasNombres);
        v_colonia.setAdapter(spinnerAdapter);
        if(v_listacolonia!=null && !v_listacolonia.isEmpty()){
            v_estado.setText(v_listacolonia.get(0).getEstado());
            v_Muni.setText(v_listacolonia.get(0).getMuni());
        }else{
            v_estado.setText("");
            v_Muni.setText("");
        }
    }

    private void actualizaListado(){
        Cuenta cuenta = servicio.traeCuenta(v_cuclid);
        v_nombre.setText(cuenta.getNombre());
        v_cuenta.setText(cuenta.getCuenta());
        v_calle.setText(cuenta.getCalle());
        v_ext.setText(cuenta.getExterior());
        v_int.setText(cuenta.getInterior());
        //v_refe.setText("");
        v_tele.setText(cuenta.getTel());
        v_cp.setText(cuenta.getCp());
        v_Muni.setText(cuenta.getMuni());
        v_estado.setText(cuenta.getEstado());
        v_domiid = cuenta.getDomiid();
        v_coloid = cuenta.getColoid();

        ArrayList lista=new ArrayList();
        lista.add(Libreria.tieneInformacion(cuenta.getColonia()) ? cuenta.getColonia() : "Sin colonias");
        ArrayAdapter<String> coloniaAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, lista);
        v_colonia.setAdapter(coloniaAdapter);
        v_nombre.requestFocus();
        //traeColonias(cuenta.getCp());

    }

    private void wsCuclGuarda(){

        if(v_nombre.getText().toString() == null|| v_nombre.getText().toString().equals("")){
            muestraMensaje("Ingrese el nombre",R.drawable.mensaje_error);
        }else if(v_cuenta.getText().toString() == null || v_cuenta.getText().toString().equals("")){
            muestraMensaje("Ingrese la cuenta",R.drawable.mensaje_error);
        } else if(v_calle.getText().toString() == null || v_calle.getText().toString().equals("")){
            muestraMensaje("Ingrese la calle",R.drawable.mensaje_error);
        } else if(v_ext.getText().toString() == null || v_ext.getText().toString().equals("")){
            muestraMensaje("Ingrese el número exterior",R.drawable.mensaje_error);
        } else if(v_cp.getText().toString() == null || v_cp.getText().toString().equals("")){
            muestraMensaje("Ingrese el código postal",R.drawable.mensaje_error);
        } else {
            Integer colo = v_colonia.getSelectedItemPosition();
            String colonia = colo >0 ? (v_listacolonia.get(colo).getId()+"") : (v_coloid+"");
            String coloniades = colo >0 ? (v_listacolonia.get(colo).getColonia()+"") : (v_coloid+"");

            ContentValues mapa = new ContentValues();
            mapa.put("cliente", v_clteid);
            mapa.put("entrega", true);
            mapa.put("entero1", 0);
            mapa.put("entero2", 0);
            mapa.put("activo", v_nuevo);
            mapa.put("cucl", v_nuevo ? 0 :v_cuclid);
            mapa.put("cuenta", v_cuenta.getText().toString());
            mapa.put("nombre", v_nombre.getText().toString());
            mapa.put("notas", "");
            mapa.put("calle", v_calle.getText().toString());
            mapa.put("tel", v_tele.getText().toString());
            mapa.put("colonia", coloniades);
            mapa.put("cp", v_cp.getText().toString());
            //mapa.put("refe", v_refe.getText().toString());
            mapa.put("numext", v_ext.getText().toString());
            mapa.put("numint", v_int.getText().toString());
            mapa.put("latitud", 0);
            mapa.put("longitud", 0);
            mapa.put("correo", "");
            mapa.put("domi", v_nuevo ? 0 : v_domiid);
            mapa.put("coloid", colonia);

            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_GUARDA_CUENTA, "", "",xml,"","");

        }
       }

    private void traeColonias(String cp){
        peticionWS(Enumeradores.Valores.TAREA_COLONIAS_CP, "SQL", "SQL", cp, "", "");
    }
}