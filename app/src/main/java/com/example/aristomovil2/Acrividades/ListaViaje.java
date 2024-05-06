package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Inventario;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.AcomodoAdapter;
import com.example.aristomovil2.adapters.ViajeAdapter;
import com.example.aristomovil2.modelos.Proveedor;
import com.example.aristomovil2.modelos.Reposicion;
import com.example.aristomovil2.modelos.Viaje;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListaViaje extends ActividadBase {
    private ListView listaViajes;
    private Viaje registroViaje;
    private FloatingActionButton btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_viaje);
        Bundle extras = getIntent().getExtras();
        inicializarActividad("Viajes");
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);
        title.setText("Traslados disponibles");
        listaViajes = findViewById(R.id.lista_viajes);//viajeradio
        LinearLayout radio=findViewById(R.id.viajeradio);
        radio.setVisibility(View.GONE);
        /*filtros*/
        btnBuscar = findViewById(R.id.btnFiltro);
        Button btnFiltra = findViewById(R.id.btnFiltra);
        Button btnCerrar = findViewById(R.id.btnCerrar);
        LinearLayoutCompat filtrar = findViewById(R.id.filtro_viaje);
        filtrar.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        filtrar.startAnimation(animation);


        btnBuscar.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
            filtrar.startAnimation(anim);
            filtrar.setVisibility(View.VISIBLE);


            anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
            anim.setDuration(300);
            btnBuscar.startAnimation(anim);
            btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    disableEnableView(false, findViewById(R.id.viaj_ordenes_content));
                    btnBuscar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        });

        btnCerrar.setOnClickListener(view -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            filtrar.startAnimation(anim);
            anim = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            anim.setDuration(300);
            btnBuscar.startAnimation(anim);
            btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) {
                    disableEnableView(true, findViewById(R.id.viaj_ordenes_content));
                    btnBuscar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) { filtrar.setVisibility(View.GONE); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        btnFiltra.setOnClickListener(view -> {
            solicitaViajes();
            btnCerrar.performClick();
        });

        RadioButton todos = findViewById(R.id.viaj_todos);
        todos.setChecked(true);
        EditText feini=findViewById(R.id.viaj_feini);
        EditText fefin=findViewById(R.id.viaj_fefin);
        feini.setOnClickListener(v -> {
            datePicker(feini);
        });
        feini.setOnFocusChangeListener((view, b)  -> {
            if(b){
                datePicker(feini);
            }
        });
        fefin.setOnClickListener(v -> {
            datePicker(fefin);
        });
        fefin.setOnFocusChangeListener((view, b)  -> {
            if(b){
                datePicker(fefin);
            }
        });
        /*crea el listado de viajes*/
        llenaListado();
    }

    public void wsLlamaCarga(Viaje pViaje){
        registroViaje = pViaje;
        peticionWS(Enumeradores.Valores.TAREA_DVIAJ_LISTA,"SQL","SQL","<linea><viaje>"+pViaje.getViajfolio()+"</viaje><estt>41</estt></linea>","","");
    }

    private void llenaListado(){
        List<Viaje> misViajes=servicio.traeViajes();
        ViajeAdapter adapter = new ViajeAdapter(misViajes, this);
        listaViajes.setAdapter(adapter);
        if(misViajes.isEmpty()){
            View vacio=findViewById(R.id.emptyViaje);
            listaViajes.setEmptyView(vacio);
        }
        adapter.notifyDataSetChanged();
    }

    private void datePicker(TextView pView){
        DialogFragment newFragment = new Inventario.DatePickerFragment(pView,"yyyy-MM-dd");
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    protected void onResume() {
        super.onResume();
        llenaListado();
    }

    private String xmlFiltro(){
        String xml="";
        ContentValues mapa=new ContentValues();
        EditText feini=findViewById(R.id.viaj_feini);
        EditText fefin=findViewById(R.id.viaj_fefin);
        EditText cliente=findViewById(R.id.viaj_cliente);
        EditText ruta=findViewById(R.id.viaj_ruta);
        RadioGroup grupo = findViewById(R.id.viaj_radio);
        int elegido = grupo.getCheckedRadioButtonId();
        String dato=feini.getText().toString();
        if(Libreria.tieneInformacion(dato)){
            mapa.put("feini",dato);
        }
        dato=fefin.getText().toString();
        if(Libreria.tieneInformacion(dato)){
            mapa.put("fefin",dato);
        }
        dato=ruta.getText().toString();
        if(Libreria.tieneInformacion(dato)){
            mapa.put("ruta",dato);
        }
        dato=cliente.getText().toString();
        if(Libreria.tieneInformacion(dato)){
            mapa.put("cliente",dato);
        }
        switch (elegido){
            case R.id.viaj_si: dato = "true"; break;
            case R.id.viaj_no: dato = "false"; break;
            case R.id.viaj_todos: dato = ""; break;
        }
        mapa.put("directo",dato);
        mapa.put("usua",usuarioID);
        xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
        return xml;
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        if(output!=null){
            switch(output.getTarea()){
                case TAREA_DVIAJ_LISTA:
                    if(output.getExito()){
                        Intent intent = new Intent(this, Dviaje.class);
                        intent.putExtra("folio",registroViaje.getFolio());
                        intent.putExtra("vfolio",registroViaje.getViajfolio());
                        startActivity(intent);
                    }else{
                        muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                    }
                    break;
                case TAREA_VIAJ_LISTA:
                    llenaListado();
                    break;
            }

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Accion");
        // add menu items
        menu.add(0, v.getId(), 0, "Cargar");
        if(v.getTag() instanceof Viaje){
            registroViaje = (Viaje)v.getTag();
        }

    }

    // menu item select listener
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Cargar") {
            wsLlamaCarga(registroViaje);
        }
        return true;
    }

    private void solicitaViajes(){
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_LISTA,"SQL","SQL",xmlFiltro(),"","");
    }
}