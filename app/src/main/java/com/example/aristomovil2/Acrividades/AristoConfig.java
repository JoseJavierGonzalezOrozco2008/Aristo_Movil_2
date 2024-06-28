package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.servicio.Finish;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AristoConfig extends ActividadBase {
    private ListView listColonias;
    private AlertDialog dlgAlerta,dlgAlerta1;
    private Integer coloid;
    private TextView vColonia,vCp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aristo_config);
        inicializarActividad2("","");
        actualizaToolbar2(usuario+", "+v_nombreestacion,"Datos de la tienda");
        Button dSucursal = findViewById(R.id.datoSuc);
        Button dProducto = findViewById(R.id.datoProd);
        Button dTiempoAire = findViewById(R.id.datoTiAi);//btnBusca


        dSucursal.setOnClickListener(view -> leeDatosSucursales());
        dProducto.setOnClickListener(view -> leeDatosProductos());
        dTiempoAire.setOnClickListener(view -> leeeDatosTiempoAire());

        coloid = 0;
    }

    public void dlgDatosSucursal(ContentValues pValue){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_datos_sucursal, null);
        builder.setView(vista);
        builder.setCancelable(true);
        builder.setTitle("Datos de la sucursal");
        RadioGroup radio = vista.findViewById(R.id.tipoCosteo);
        Button buscacolonia = vista.findViewById(R.id.btnBusca);

        Button cerrar =  vista.findViewById(R.id.btnCerrar);
        Button guardar =  vista.findViewById(R.id.btnGuardar);

        EditText correo = vista.findViewById(R.id.admoncorreo);
        EditText sucursal = vista.findViewById(R.id.tienda);
        EditText telefono = vista.findViewById(R.id.telefono);
        EditText calle = vista.findViewById(R.id.calle);
        EditText exterior = vista.findViewById(R.id.numext);
        EditText interior = vista.findViewById(R.id.numint);
        EditText despedida = vista.findViewById(R.id.despedida);
        vColonia = vista.findViewById(R.id.coloniaText);
        vCp = vista.findViewById(R.id.datosCP);

        correo.setText(Libreria.traeInfo(pValue.getAsString("correoadmon")));
        sucursal.setText(Libreria.traeInfo(pValue.getAsString("sucursal")));
        telefono.setText(Libreria.traeInfo(pValue.getAsString("telefono")));
        calle.setText(Libreria.traeInfo(pValue.getAsString("calle")));
        exterior.setText(Libreria.traeInfo(pValue.getAsString("ext")));
        interior.setText(Libreria.traeInfo(pValue.getAsString("inter")));
        vCp.setText(Libreria.traeInfo(pValue.getAsString("cp")));//despedida
        despedida.setText(Libreria.traeInfo(pValue.getAsString("despedida")));
        String colonia=Libreria.traeInfo(pValue.getAsString("coloid"),"0");
        coloid = Integer.parseInt(colonia);
        vColonia.setText(Libreria.traeInfo(pValue.getAsString("colonia")));
        Integer tipocoste=1;
        try{
            tipocoste = new Float(Libreria.tieneInformacionFloat(pValue.getAsString("tipocosteo"),1)).intValue();
        }catch(NumberFormatException e){
            System.out.println(e);
        }
        switch (tipocoste){
            case 1:
                radio.check(R.id.radioUltimo);
                break;
            case 2:
                radio.check(R.id.radioPromedio);
                break;
            case 3:
                radio.check(R.id.radioMayor);
                break;
        }


        buscacolonia.setOnClickListener(view -> dlgDatosColonia());
        dlgAlerta1 = builder.create();
        cerrar.setOnClickListener(view -> dlgAlerta1.dismiss());
        guardar.setOnClickListener(view -> {
            Integer tipocosteo = radio.getCheckedRadioButtonId(),costeo=0;
            switch(tipocosteo){
                case R.id.radioUltimo:
                    costeo=1;
                    break;
                case R.id.radioPromedio:
                    costeo=2;
                    break;
                case R.id.radioMayor:
                    costeo=3;
                    break;
            }

            ContentValues mapa=new ContentValues();
            mapa.put("correoadmon",Libreria.traeInfo(correo.getText().toString()));
            mapa.put("sucursal",Libreria.traeInfo(sucursal.getText().toString()));
            mapa.put("telefono",Libreria.traeInfo(telefono.getText().toString()));
            mapa.put("calle",Libreria.traeInfo(calle.getText().toString()));
            mapa.put("ext",Libreria.traeInfo(exterior.getText().toString()));
            mapa.put("inter",Libreria.traeInfo(interior.getText().toString()));
            mapa.put("colonia",coloid);
            mapa.put("cp",Libreria.traeInfo(vCp.getText().toString()));
            mapa.put("tipocosteo",costeo);
            mapa.put("despedida",Libreria.traeInfo(despedida.getText().toString()));
            String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
            guardaDatosSucursales(xml);
        });
        dlgAlerta1.show();
    }

    public void dlgDatosProducto(ContentValues pValue){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialogo_datos_producto, null);
        builder.setView(vista);
        builder.setCancelable(true);
        builder.setTitle("Datos de producto a mostrar");
        CheckBox marca = vista.findViewById(R.id.datoMarca);
        CheckBox linea = vista.findViewById(R.id.datoMarca);
        CheckBox clavesat = vista.findViewById(R.id.datoMarca);
        CheckBox sustancia = vista.findViewById(R.id.datoMarca);
        CheckBox divisa = vista.findViewById(R.id.datoMarca);

        marca.setChecked(Libreria.getBoolean(Libreria.traeInfo(pValue.getAsString("marca"))));
        linea.setChecked(Libreria.getBoolean(Libreria.traeInfo(pValue.getAsString("linia"))));
        clavesat.setChecked(Libreria.getBoolean(Libreria.traeInfo(pValue.getAsString("clavesat"))));
        sustancia.setChecked(Libreria.getBoolean(Libreria.traeInfo(pValue.getAsString("sustancia"))));
        divisa.setChecked(Libreria.getBoolean(Libreria.traeInfo(pValue.getAsString("divisa"))));

        Button cerrar= vista.findViewById(R.id.btnCerrar);
        Button guardar= vista.findViewById(R.id.btnGuardar);

        dlgAlerta1 = builder.create();

        cerrar.setOnClickListener(view -> dlgAlerta1.dismiss());
        guardar.setOnClickListener(view -> {
            ContentValues mapa=new ContentValues();
            mapa.put("marca",marca.isChecked());
            mapa.put("linea",linea.isChecked());
            mapa.put("clavesat",clavesat.isChecked());
            mapa.put("sustancia",sustancia.isChecked());
            mapa.put("divisa",divisa.isChecked());
            String pXml =Libreria.xmlLineaCapturaSV(mapa,"linea");
            guardaDatosProductos(pXml);
        });

        dlgAlerta1.show();
    }

    public void dlgDatosTiempoAire(ContentValues pValue){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialogo_datos_extras, null);
        builder.setView(vista);
        builder.setCancelable(true);
        builder.setTitle("Datos de tiempo aire");

        EditText cuentaTA = vista.findViewById(R.id.cuentaTiAi);
        EditText contraTA = vista.findViewById(R.id.ClaveTiAi);

        cuentaTA.setText(Libreria.traeInfo(pValue.getAsString("cuentata")));
        contraTA.setText(Libreria.traeInfo(pValue.getAsString("contrata")));

        Button cerrar= vista.findViewById(R.id.btnCerrar);
        Button guardar= vista.findViewById(R.id.btnGuardar);
        Button consulta= vista.findViewById(R.id.btnConsultaSaldo);

        dlgAlerta1 = builder.create();

        cerrar.setOnClickListener(view -> dlgAlerta1.dismiss());
        guardar.setOnClickListener(view -> {
            ContentValues mapa = new ContentValues();
            mapa.put("cuentata",Libreria.traeInfo(cuentaTA.getText().toString()));
            mapa.put("contrata",Libreria.traeInfo(contraTA.getText().toString()));
            String pXml =Libreria.xmlLineaCapturaSV(mapa,"linea");
            guardaDatosTiempoAire(pXml);
        });
        consulta.setOnClickListener(view -> consultaSaldo());

        dlgAlerta1.show();
    }

    public void dlgDatosColonia(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialogo_colonia, null);
        builder.setView(vista);
        builder.setCancelable(true);
        EditText edCp = vista.findViewById(R.id.CPostal);
        listColonias =  vista.findViewById(R.id.listaColonias);

        edCp.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if(!Libreria.tieneInformacion(edCp.getText().toString())){
                    dlgMensajeError("no puede estar vacio",R.drawable.mensaje_error2);
                    return false;
                }
                traeColonias(edCp.getText().toString());
                //hideKeyboard(view);
                return true;
            }return false;
        });
        dlgAlerta =builder.create();
        dlgAlerta.show();
    }

    private void traeColonias(String cp){
        peticionWS(Enumeradores.Valores.TAREA_COLONIAS_CP, "SQL", "SQL", cp, "", "");
    }

    private void guardaDatosSucursales(String pXml){
        peticionWS(Enumeradores.Valores.TAREA_GUARDADATOSSUCURSAL, "SQL", "SQL", pXml, "", "");
    }


    private void guardaDatosProductos(String pXml){
        peticionWS(Enumeradores.Valores.TAREA_GUARDADATOSPRODUCTO, "SQL", "SQL", pXml, "", "");
    }

    private void guardaDatosTiempoAire(String pXml){
        peticionWS(Enumeradores.Valores.TAREA_GUARDADATOSEXTRA, "SQL", "SQL", pXml, "", "");
    }

    private void leeeDatosTiempoAire(){
        peticionWS(Enumeradores.Valores.TAREA_LEEDATOSTIAI, "SQL", "SQL", v_estacion+"", "", "");
    }

    private void leeDatosSucursales(){
        peticionWS(Enumeradores.Valores.TAREA_LEEDATOSSUCURSAL, "SQL", "SQL", v_estacion+"", "", "");
    }

    private void leeDatosProductos(){
        peticionWS(Enumeradores.Valores.TAREA_LEEDATOSPRODUCTO, "SQL", "SQL", v_estacion+"", "", "");
    }

    private void consultaSaldo(){
        peticionWS(Enumeradores.Valores.TAREA_CONSULTASALDOTA, "", "", "true", "", "");
    }

    public void eligeColonia(Generica pGen){
        //System.out.println(pGen.getTex1());
        dlgAlerta.hide();
        vColonia.setText(pGen.getTex1());
        vCp.setText(pGen.getTex5());
        coloid = pGen.getId();
    }

    @Override
    public void Finish(EnviaPeticion output){
        try{
            ContentValues obj =output.getExtra1()==null ? new ContentValues() : (ContentValues)output.getExtra1();
            switch (output.getTarea()){
                case TAREA_COLONIAS_CP:
                    List<Generica> colonias = servicio.traeColoniasGenerica();
                    GenericaAdapter genericaAdapter = new GenericaAdapter(colonias,this,2);
                    listColonias.setAdapter(genericaAdapter);
                    genericaAdapter.notifyDataSetChanged();
                    break;
                case TAREA_GUARDADATOSSUCURSAL:
                case TAREA_GUARDADATOSPRODUCTO:
                case TAREA_GUARDADATOSEXTRA:
                    if(output.getExito()){
                        dlgAlerta1.dismiss();
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_LEEDATOSSUCURSAL:
                    if(output.getExito()){
                        dlgDatosSucursal(obj);
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_LEEDATOSPRODUCTO:
                    if(output.getExito()){
                        dlgDatosProducto(obj);
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_LEEDATOSTIAI:
                    if(output.getExito()){
                        dlgDatosTiempoAire(obj);
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_CONSULTASALDOTA:
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error2);
                default:
                    super.Finish(output);
            }
        }catch(Exception a){
            System.out.println(a);
        }
    }
}