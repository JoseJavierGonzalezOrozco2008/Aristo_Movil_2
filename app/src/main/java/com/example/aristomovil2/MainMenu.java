package com.example.aristomovil2;

import android.Manifest;
import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.ConCuenta;
import com.example.aristomovil2.Acrividades.ListaViaje;
import com.example.aristomovil2.adapters.GrupoAdapter;
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

import pub.devrel.easypermissions.EasyPermissions;

public class MainMenu extends ActividadBase  implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private ArrayList<com.example.aristomovil2.modelos.MenuItem> items;
    private boolean impresora,puede_asignar;
    private int tiempo_consulta;
    private Timer timerTrabajo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setNavigationViewListener();
        inicializarActividad("");
        actualizaToolbar("Bienvenido " + usuario);

        String androidId = getSharedPreferences("renglones", MODE_PRIVATE).getString("androidID", "");
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        impresora = preferences.getBoolean("mandaimprimir", false);//imprimedetalle
        puede_asignar = preferences.getBoolean("puedeasignar", false);
        tiempo_consulta = preferences.getInt("tiempoconsulta", 120);
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
        super.onBackPressed();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Login.class);
            WorkManager.getInstance(this).cancelAllWorkByTag("Pedidos");
            startActivity(intent);
            finish();
        }
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

        com.example.aristomovil2.modelos.MenuItem item1 = items.get(0);

        for(com.example.aristomovil2.modelos.MenuItem i:items)
            if(item.getItemId()  == i.getId())
                item1 = i;

        SharedPreferences.Editor editor = getSharedPreferences("renglones", MODE_PRIVATE).edit();
        editor.putString("titulo", item1.getTitulo());
        editor.apply();
        System.out.println("tarea "+item.getItemId());
        Dialog d = new Dialog(this);
        switch (item.getItemId()){
            case 0: {
                if(!puede_asignar){
                    muestraMensaje("No tiene privilegios para continuar",R.drawable.mensaje_error);
                    return false;
                }
                if(!checkBluetoothPermissions()){
                    d.setContentView(R.layout.dial_no_permiso);
                    Button btn_ok = d.findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(view -> {
                        d.dismiss();
                    });
                    d.show();
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
                //finish();
                break;
            }
            case 2: {
                Intent intent = new Intent(this, Consulta.class);
                intent.putExtra("venta", false);
                startActivity(intent);
                //finish();
                break;
            }
            case 3:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 803);//802 true
                //Intent intent = new Intent(this, Reposicion.class);
                startActivity(intent);
                //finish();
                break;
            }
            case 4:{
                SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
                int estaid = sharedPreferences.getInt("estaid", 0);
                if(estaid<=0){
                    muestraMensaje("No se tiene estacion asignada",R.drawable.mensaje_error);
                    return false;
                }
                Intent intent = new Intent(this, Carrito.class);
                startActivity(intent);
                //finish();
                break;
            }
            case 5:{
                Intent intent = new Intent(this, Inventario.class);
                intent.putExtra("documento", 14);
                startActivity(intent);
                //finish();
                break;
            }
            case 6:{
                Intent intent = new Intent(this, Inventario.class);
                intent.putExtra("documento", 16);
                startActivity(intent);
                //finish();
                break;
            }
            case 7:{
                Intent intent = new Intent(this, Inventario.class);
                intent.putExtra("documento", 17);
                startActivity(intent);
                //finish();
                break;
            }
            case 8:{
                Intent intent = new Intent(this, ConteoFisico.class);
                startActivity(intent);
                //finish();
                break;
            }
            case 9:{
                Intent intent = new Intent(this, ConteoUbicacion.class);
                startActivity(intent);
                //finish();
                break;
            }
            case 10:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 802);//803 false
                //Intent intent = new Intent(this, Reposicion.class);
                startActivity(intent);
                break;
            }
            case 11:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 476);//acomodo libre
                //Intent intent = new Intent(this, Reposicion.class);
                startActivity(intent);
                break;
            }
            case 12:{
                Intent intent = new Intent(this, ContUbi.class);
                //intent.putExtra("repone", 476);//803 false
                //Intent intent = new Intent(this, Reposicion.class);
                startActivity(intent);
                break;
            }
            case 13:{
                Intent intent = new Intent(this, BuscaProd.class);
                //Intent intent = new Intent(this, ConCuenta.class);
                startActivity(intent);
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
                /*Intent intent = new Intent(this, ListaViaje.class);
                startActivity(intent);*/
                solicitaViajes();
                break;
            }
            case 17:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 484);//devolucion
                startActivity(intent);
                break;
            }
        }

        return false;
    }

    private boolean checkBluetoothPermissions() {
        String[] perms = {Manifest.permission.BLUETOOTH_CONNECT};
        if(Build.VERSION.SDK_INT >= 31){
            if (EasyPermissions.hasPermissions(this, perms)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
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
                //items.set(0,new com.example.aristomovil2.modelos.MenuItem(0,0,"Configuracion",""+R.drawable.card,"","Configuracion"));
                for (com.example.aristomovil2.modelos.MenuItem i : items) {
                    menu.add(Menu.NONE, i.getId(), i.getOrden(), i.getTexto()).setIcon(R.drawable.logoaristo);
                }
                menu.add(Menu.NONE,0,0,"Configuracion").setIcon(R.drawable.editar);
                ArrayList<String> grupos = servicio.getGruposName();
                ArrayList<ArrayList<com.example.aristomovil2.modelos.MenuItem>> listas = new ArrayList<>();

                for(String s:grupos)
                    listas.add(servicio.getItemsMenu(s));

                ListView menuList = findViewById(R.id.menu_list);

                GrupoAdapter adapter = new GrupoAdapter(listas, grupos, this);
                menuList.setAdapter(adapter);

                findViewById(R.id.menuError).setVisibility(View.GONE);
            }else if (output.getTarea() == Enumeradores.Valores.TAREA_VIAJ_LISTA){
                List<Viaje> listado=servicio.traeViajes();
                if(listado.isEmpty()){
                    muestraMensaje("No se encontraron viajes disponibles",R.drawable.mensaje_error);
                    return ;
                }
                Intent intent = new Intent(this, ListaViaje.class);
                startActivity(intent);
            }
                cierraDialogo();
        }
        else {
            cierraDialogo();
            findViewById(R.id.menu_list).setVisibility(View.GONE);
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void solicitaViajes(){
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_LISTA,"SQL","SQL","<linea><usua>-1</usua></linea>","","");
    }

    @Override
    public void finish(){
        if(timerTrabajo!=null){
            timerTrabajo.cancel();
        }
        super.finish();
    }
}
