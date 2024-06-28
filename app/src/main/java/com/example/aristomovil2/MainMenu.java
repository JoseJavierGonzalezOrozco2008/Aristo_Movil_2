package com.example.aristomovil2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.aristomovil2.Acrividades.AristoConfig;
import com.example.aristomovil2.Acrividades.BuscaProducto;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.ConCuenta;
import com.example.aristomovil2.Acrividades.Corte;
import com.example.aristomovil2.Acrividades.ListaViaje;
import com.example.aristomovil2.adapters.GrupoAdapter;
import com.example.aristomovil2.adapters.ListaImpuestosAdapter;
import com.example.aristomovil2.adapters.ListaProdsAdapter;
import com.example.aristomovil2.modelos.Viaje;
import com.example.aristomovil2.servicio.MetodoRf;
import com.example.aristomovil2.servicio.MetodoWs;
import com.example.aristomovil2.servicio.Tiempo;
import com.example.aristomovil2.servicio.Trabajos;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainMenu extends ActividadBase  implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private ArrayList<com.example.aristomovil2.modelos.MenuItem> items;
    private boolean impresora,puede_asignar;
    private int tiempo_consulta,v_estacion;
    private Timer timerTrabajo;
    private AlertDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setNavigationViewListener();
        inicializarActividad("");
        actualizaToolbar(usuario+" "+v_nombreestacion);

        String androidId = getSharedPreferences("renglones", MODE_PRIVATE).getString("androidID", "");
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        impresora = preferences.getBoolean("mandaimprimir", false);//imprimedetalle
        puede_asignar = preferences.getBoolean("puedeasignar", false);
        tiempo_consulta = preferences.getInt("tiempoconsulta", 120);
        v_estacion = getSharedPreferences("renglones", MODE_PRIVATE).getInt("estaid", 0);
        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        peticionWS(Enumeradores.Valores.TAREA_ITEMS_MENU, "SQL", "SQL", usuarioID, "", "");

        //tempoMensajes();
        //checaActualizacion();
    }

    public void tempoMensajes(){
        String ip = getValuePreferences("ip");
        timerTrabajo = new Timer();
        Tiempo timerTaskObj = new Tiempo(this,ip,usuarioID);
        timerTaskObj.setServicio(this.servicio);
        timerTrabajo.scheduleAtFixedRate(timerTaskObj, 1000, tiempo_consulta*1000);
    }

    private void checaActualizacion(){
        JSONObject jason=new JSONObject();
        String jSon="";
        try {
            jason.put("idcliente",2);
            jason.put("nversion",1);
            jason.put("fecha","01/06/2023");
            jason.put("tipo",4);
            jSon = jason.toString();
            System.out.println(jSon);
        } catch (JSONException e) {
            System.out.println(e);
        }
        MetodoRf nuevo=new MetodoRf();
        nuevo.context = this;
        nuevo.servicio = this.servicio;
        nuevo.termina = this;
        nuevo.enviaPeticion = new EnviaPeticion(Enumeradores.Valores.REST_VERSION);
        nuevo.enviaPeticion.setDato1(jSon);
        nuevo.execute();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finaliza();
        }
    }

    private void finaliza(){
        Intent intent = new Intent(this, Login.class);
        WorkManager.getInstance(this).cancelAllWorkByTag("Pedidos");
        startActivity(intent);
        finish();
    }

    /**
     * Muestra el Drawe al seleccionar un item
     * @param item El item seleccionado
     * @return Retorna true para indicar exito
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        drawer.openDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Listener para la seleccion de un itewm en el menu el Tooolbar
     * @param item El item seleccionado
     * @return Retorna true para indicar exito
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        if(items!=null && !items.isEmpty()) {
            com.example.aristomovil2.modelos.MenuItem item1 = items.get(0);
            for (com.example.aristomovil2.modelos.MenuItem i : items)
                if (item.getItemId() == i.getId())
                    item1 = i;
            SharedPreferences.Editor editor = getSharedPreferences("renglones", MODE_PRIVATE).edit();
            editor.putString("titulo", item1.getTitulo());
            editor.apply();
        }

        switch (item.getItemId()){
            case 0: {
                if(!puede_asignar){
                    dlgMensajeError("No tiene privilegios para continuar",R.drawable.mensaje_error2);
                    return false;
                }
                Intent intent = new Intent(this, Configuracion.class);
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                intent.putExtra("androidID", androidId);
                intent.putExtra("login", false);
                intent.putExtra("impresora", impresora);
                startActivity(intent);
                break;
            }
            case 1: {
                Intent intent = new Intent(this, Ubicaciones.class);
                startActivity(intent);
                break;
            }
            case 2: {
                Intent intent = new Intent(this, Consulta.class);
                intent.putExtra("venta", false);
                startActivity(intent);
                break;
            }
            case 3:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 803);//802 true
                startActivity(intent);
                break;
            }
            case 4:{

                if(v_estacion<=0){
                    dlgMensajeError("No se tiene estacion asignada",R.drawable.mensaje_error2);
                    return false;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
                boolean vertourch = sharedPreferences.getBoolean("vertouch", false);
                if(vertourch){
                    wsBqdaProd(v_estacion);
                }else{
                    Intent intent = new Intent(this, Carrito.class);
                    startActivity(intent);
                }
                break;
            }
            case 5:{
                cambiaDocInv(14);
                break;
            }
            case 6:{
                cambiaDocInv(16);
                break;
            }
            case 7:{
                cambiaDocInv(17);
                break;
            }
            case 8:{
                Intent intent = new Intent(this, ConteoFisico.class);
                startActivity(intent);
                break;
            }
            case 9:{
                Intent intent = new Intent(this, ConteoUbicacion.class);
                startActivity(intent);
                break;
            }
            case 10:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 802);//803 false
                startActivity(intent);
                break;
            }
            case 11:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 476);//acomodo libre
                startActivity(intent);
                break;
            }
            case 12:{
                Intent intent = new Intent(this, ContUbi.class);
                startActivity(intent);
                break;
            }
            case 13:{
                if(esMono()){
                    wsConsultaPara();
                }else{
                    Intent intent = new Intent(this, BuscaProd.class);
                    intent.putExtra("marca","true");
                    intent.putExtra("line","true");
                    intent.putExtra("clavesat","true");
                    intent.putExtra("divisa","true");
                    intent.putExtra("sustancia","true");
                    startActivity(intent);
                }
                break;
            }
            case 14:{//
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 862);
                startActivity(intent);
                break;
            }
            case 15:{
                Intent intent = new Intent(this, ConCuenta.class);
                startActivity(intent);
                break;
            }
            case 16:{
                solicitaViajes();
                break;
            }
            case 17:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 484);//devolucion
                startActivity(intent);
                break;
            }
            case 18:
                if(v_estacion<=0){
                    dlgMensajeError("No se tiene estacion asignada",R.drawable.mensaje_error2);
                    return false;
                }
                wsTraeCorte();
                break;
            case 99:
                //aristo config
                Intent intent = new Intent(this, AristoConfig.class);
                startActivity(intent);
                break;
            case 100:
                dlgCambiaContrasena();
                break;
            case 101:
                finaliza();
                break;
        }

        return false;
    }

    /**
     * Establece lavista de navegacion
     */
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        if(output.getTarea()==Enumeradores.Valores.REST_VERSION){
            System.out.println(output.getMensaje());
            return ;
        }else if (output.getTarea() == Enumeradores.Valores.TAREA_TRAECORTMOVIL && obj==null){
            dlgMensajeError("Sin datos del corte",R.drawable.mensaje_error2);
            return;
        }else if(output.getTarea() == Enumeradores.Valores.TAREA_CAMBIAPASS && obj==null) {
            dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
            return;
        }
        if(obj != null){
            if (output.getTarea() == Enumeradores.Valores.TAREA_ITEMS_MENU) {
                NavigationView navigationView = findViewById(R.id.navigationView);
                Menu menu = navigationView.getMenu();
                menu.clear();

                items = servicio.getItemsMenu();
                if(items==null){
                    items = new ArrayList();
                }
                menu.add(0,99,999,"Aristo config").setIcon(R.drawable.logoaristo);
                menu.add(0,100,0,"Cambia contraseña").setIcon(R.drawable.editar);
                menu.add(0,101,1000,"Salir").setIcon(R.drawable.left_arrow);
                //items.set(0,new com.example.aristomovil2.modelos.MenuItem(0,0,"Configuracion",""+R.drawable.card,"","Configuracion"));
                for (com.example.aristomovil2.modelos.MenuItem i : items) {
                    menu.add(Menu.NONE, i.getId(), i.getOrden(), i.getTexto()).setIcon(R.drawable.logoaristo);
                }
                menu.add(Menu.NONE,0,0,"Dispositivos").setIcon(R.drawable.editar);
                ArrayList<String> grupos = servicio.getGruposName();
                ArrayList<ArrayList<com.example.aristomovil2.modelos.MenuItem>> listas = new ArrayList<>();

                for(String s:grupos)
                    listas.add(servicio.getItemsMenu(s));

                ListView menuList = findViewById(R.id.menu_list);

                GrupoAdapter adapter = new GrupoAdapter(listas, grupos, this);
                menuList.setAdapter(adapter);

                findViewById(R.id.menuError).setVisibility(View.GONE);
            } else if (output.getTarea() == Enumeradores.Valores.TAREA_VIAJ_LISTA){
                List<Viaje> listado=servicio.traeViajes();
                if(listado.isEmpty()){
                    muestraMensaje("No se encontraron viajes disponibles",R.drawable.mensaje_error);
                    return ;
                }
                Intent intent = new Intent(this, ListaViaje.class);
                startActivity(intent);
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_VNTAMASPROD){
                Intent intent = new Intent(this, Carrito.class);
                startActivity(intent);
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_PERMISOSPROD){
                Intent intent = new Intent(this, BuscaProd.class);
                if(obj!=null && obj.size()>0){
                    for(String llaves:obj.keySet()){
                        intent.putExtra(llaves,obj.getAsString(llaves));
                    }
                }
                startActivity(intent);
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_TRAECORTMOVIL){
                if(!output.getExito()){
                    dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    return;
                }
                Intent intent = new Intent(this, Corte.class);
                intent.putExtra("cortfolio",obj.getAsString("cortfolio"));
                intent.putExtra("fecha",obj.getAsString("fecha"));
                intent.putExtra("mensaje",obj.getAsString("mensaje"));
                startActivity(intent);
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_CAMBIAPASS){
                if(!output.getExito()){
                    dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    return;
                }else{
                    dlgMensajeError(output.getMensaje(),R.drawable.mensaje_exito);
                }
                dialogo.dismiss();
            }
            cierraDialogo();
        } else {
            cierraDialogo();
            findViewById(R.id.menu_list).setVisibility(View.GONE);
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void solicitaViajes(){
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_LISTA,"SQL","SQL","<linea><usua>-1</usua></linea>","","");
    }

    private void wsBqdaProd(Integer pEstaid){
        peticionWS(Enumeradores.Valores.TAREA_VNTAMASPROD, "SQL", "SQL", pEstaid+"","","");
    }

    private void wsConsultaPara(){
        peticionWS(Enumeradores.Valores.TAREA_PERMISOSPROD, "SQL", "SQL", v_estacion+"","","");
    }

    private void wsTraeCorte(){
        peticionWS(Enumeradores.Valores.TAREA_TRAECORTMOVIL, "SQL", "SQL", v_estacion+"","","");
    }

    private boolean cambiaDocInv(Integer pTipo){
        if(v_estacion <=0){
            dlgMensajeError("No se tiene estacion asignada",R.drawable.mensaje_error2);
            return false;
        }
        Intent intent = new Intent(this, Inventario.class);
        intent.putExtra("documento", pTipo);
        startActivity(intent);
        return true;
    }

    @Override
    public void finish(){
        if(timerTrabajo!=null){
            timerTrabajo.cancel();
        }
        super.finish();
    }

    private void dlgCambiaContrasena(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_contrasena, null);
        builder.setCancelable(true);
        builder.setView(vista);
        dialogo=builder.create();
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        dialogo.getWindow().setLayout(width, -2);

        TextView tvUsuario = vista.findViewById(R.id.tvUsuarioContra);
        EditText contra1 = vista.findViewById(R.id.nupaPass1);
        EditText contra2 = vista.findViewById(R.id.nupaPass2);
        Button cerrar = vista.findViewById(R.id.btnCerrar);
        Button guadar = vista.findViewById(R.id.btnGuardar);

        tvUsuario.setText(usuario);
        guadar.setOnClickListener(view -> {
            String texto=contra1.getText().toString();
            String texto1=contra2.getText().toString();
            if(!Libreria.tieneInformacion(texto) || !Libreria.tieneInformacion(texto1)){
                dlgMensajeError("Deben tener informacion",R.drawable.mensaje_error2);
                return;
            }
            if(!texto.equals(texto1)){
                dlgMensajeError("Las contraseñas no coinciden",R.drawable.mensaje_error2);
                return;
            }
            if(texto.length()>8){
                dlgMensajeError("No puede ser de mas de 8 digitos",R.drawable.mensaje_error2);
                return;
            }
            wsCambiaPass(texto);
        });
        contra2.setOnKeyListener(((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                guadar.performClick();
                return true;
            }
            return false;
        }));

        cerrar.setOnClickListener(view -> dialogo.dismiss());
        dialogo.show();
    }

    public void wsCambiaPass(String pass){
        ContentValues mapa=new ContentValues();
        mapa.put("pwd",pass);
        mapa.put("esta",v_estacion);
        mapa.put("usua",usuarioID);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_CAMBIAPASS,"SQL","SQL",xml,"","");
    }
}