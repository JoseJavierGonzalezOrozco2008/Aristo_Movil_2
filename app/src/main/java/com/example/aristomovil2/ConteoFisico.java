package com.example.aristomovil2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.adapters.UbicacionesAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;

public class ConteoFisico extends ActividadBase {
    private String folio;
    private boolean codigoBarras;
    private BluetoothConnection selectedDevice;
    private ArrayList<Ubicacion> ubicaciones;
    private ListView listUbicaciones;
    private UbicacionesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invetario_fisico);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Conteo"));

        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        String impresora = preferencesConf.getString("impresora", "Predeterminada");
        codigoBarras = preferencesConf.getBoolean(impresora + "CodigoBarras", true);
        Dialog d = new Dialog(this);
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if (bluetoothDevicesList != null) {
            for (int i = 1; i <= bluetoothDevicesList.length; i++) {
                if(Build.VERSION.SDK_INT >= 31){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        d.setContentView(R.layout.dial_no_permiso);
                        Button btn_ok = d.findViewById(R.id.btn_ok);
                        btn_ok.setOnClickListener(view -> {
                            d.dismiss();
                        });
                        d.show();
                    }
                }
                if (bluetoothDevicesList[i - 1].getDevice().getName().equals(impresora))
                    selectedDevice = bluetoothDevicesList[i - 1];
            }
        }
        folio = "";

        Button btnNuevo = findViewById(R.id.btnInventarioFisicoNuevo);
        EditText editNotas = findViewById(R.id.editInventarioFisicoNotas);

        btnNuevo.setOnClickListener(v -> creaInventario(editNotas.getText().toString()));

        listUbicaciones = findViewById(R.id.listUbicaciones);
        listUbicaciones.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listUbicaciones.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                ubicaciones.get(i).setSelected(b);
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.context_menu_ubicacion, menu);
                adapter.setInSelection(true);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.item_ubicacion_asigna:
                        StringBuilder espacios = new StringBuilder();

                        for(Ubicacion u: ubicaciones){
                            if(u.isSelected()){
                                if(!espacios.toString().equals(""))
                                    espacios.append(",");
                                espacios.append(u.getUbiid());
                            }
                        }

                        asignaUbicacion(espacios.toString());
                        actionMode.finish();
                        return true;
                    case R.id.item_ubicacion_quita:
                        StringBuilder espacio = new StringBuilder();

                        for(Ubicacion u:ubicaciones)
                            if(u.isSelected()){
                                if(!espacio.toString().equals(""))
                                    espacio.append(",");
                                espacio.append(u.getUbiid());
                            }

                        desasignaUbicacion(espacio.toString());
                        actionMode.finish();
                        return true;
                    case R.id.item_ubicacion_imprime:
                        ArrayList<Ubicacion> ubi = new ArrayList<>();
                        for (Ubicacion u : ubicaciones)
                            if (u.isSelected())
                                ubi.add(u);
                        imprimir(ubi);
                        actionMode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                for(Ubicacion u:ubicaciones)
                    u.setSelected(false);
                adapter.setInSelection(false);
            }
        });

        peticionWS(Enumeradores.Valores.TAREA_CONSULTA_INVENTARIO, "SQL", "SQL", "", "", "");
    }

    /**
     * Establece el contenido de la lista de ubicaciones
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyInventario);
        ListView list = findViewById(R.id.listUbicaciones);
        list.setEmptyView(empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conteo_fisico, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuInventarioFisicoAplica:
                AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                confirmacion.setTitle("Aplica y Cierra");
                confirmacion.setMessage("¿Estás seguro de aplicar y cerrar el inventario?");
                confirmacion.setCancelable(false);
                confirmacion.setPositiveButton("SI", (dialog, which) -> aplicaCierra());
                confirmacion.setNegativeButton("NO", null);
                confirmacion.show();
                break;
            case R.id.menuInventarioFisicoCancela:
                cancelaInventario();
                break;
            case R.id.menuInventarioFisicioinfo:
                infoInventario();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);*/
        super.onBackPressed();
        finish();
    }

    /**
     * Llama al servicio para crear un nuevo inventario
     * @param notas Las notas del inventario
     */
    private void creaInventario(String notas){
            peticionWS(Enumeradores.Valores.TAREA_NUEVO_INVENTARIO, "SQL", "SQL",
                    "<linea><nuevo>true</nuevo><folio></folio><usuaid>" + usuarioID + "</usuaid>" +
                            "<tipo>533</tipo><estatus>9</estatus><notas>" + notas + "</notas>" +
                            "<eslocal>true</eslocal><conteos>1</conteos></linea>"
                    , "", "");
    }

    /**
     * Llama al servicio para crear un nuevo inventario     *
     */
    private void cancelaInventario(){
        peticionWS(Enumeradores.Valores.TAREA_NUEVO_INVENTARIO, "SQL", "SQL",
                "<linea><nuevo>false</nuevo><folio>"+folio+"</folio><usuaid>" + usuarioID + "</usuaid>" +
                        "<tipo>533</tipo><estatus>106</estatus><notas>" + "Cancelado por:"+usuario + "</notas>" +
                        "<eslocal>true</eslocal><conteos>1</conteos></linea>"
                , "", "");
    }

    /**
     * Llama al servio que asigna las ubicaciones seleccionadas
     * @param espacios Lista de ubicaciones seleccionadas
     */
    public void asignaUbicacion(String espacios){
        peticionWS(Enumeradores.Valores.TAREA_ASIGNA_INVENTARIO, "SQL", "SQL",
                "<linea><espacios>" + espacios + "</espacios><conteo>2</conteo><usuaid>" + usuarioID + "</usuaid></linea>", "", "");
    }

    /**
     * Llama al servicio para desasignar las ubicaciones seleccionadas
     * @param espacios Lista de ubicaciones seleccionadas
     */
    public void desasignaUbicacion(String espacios){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quitar Espacios");
        builder.setMessage("¿Seguro de quitar los espacios seleccionados?");
        builder.setPositiveButton("Si", (dialogInterface, i) -> peticionWS(Enumeradores.Valores.TAREA_DESASIGNA_INVENTARIO, "SQL", "SQL", folio, "2", espacios));
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();

    }

    /**
     *Llama al servicio para aplicar y cerar el inventario
     */
    private void aplicaCierra(){
        peticionWS(Enumeradores.Valores.TAREA_APLICA_CIERRA, "SQL", "SQL", usuarioID, "", "");
    }

    /**
     * Inicia el conteo de una ubicacion
     * @param ubicacion La ubicacion a contar
     */
    public void contarUbicacion(String ubicacion) {
        peticionWS(Enumeradores.Valores.TAREA_CUENTA_UBICACION, "SQL", "SQL", usuarioID, ubicacion, "");
    }

    /**
     * Trae la lista de ubicaciones
     */
    private void traeUbicaciones(){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_UBICACIONES, "SQL", "SQL", folio, usuarioID, "");
    }

    /**
     * Muestra los productos contados en una unbicacion
     * @param asifID El asifID de la ubicacion
     * @param ubicacion El codigo dela ubicacion
     */
    public void verUbicacion(String asifID, String ubicacion){
        Intent intent = new Intent(this, Contados.class);
        intent.putExtra("conteo", ubicacion);
        intent.putExtra("asifid", asifID);
        intent.putExtra("modelo", 0);
        intent.putExtra("elimina", false);
        intent.putExtra("inventario", true);
        startActivity(intent);
    }

    /**
     * Abre una ubicacion
     * @param asifID El asifID de la ubicacion
     */
    public void abrirUbicacion(String asifID){
        peticionWS(Enumeradores.Valores.TAREA_ABRE_UBICACION, "SQL", "SQL", asifID, "108", "");
    }

    /**
     * Llama al servicio que regresa la informacion del inventario
     */
    private void infoInventario(){
        peticionWS(Enumeradores.Valores.TAREA_INFO_INVENTARIO, "SQL", "SQL", folio, "", "");
    }

    /**
     * Imprime las ubicaciones seleccionadas
     * @param ubicaciones Lista de ubicaciones seleccionadas
     */
    public void imprimir(ArrayList<Ubicacion> ubicaciones){
        Dialog d = new Dialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imprimir Ubicaciones");
        builder.setMessage("¿Estás seguro de imprimir las ubicaciones seleccionadas?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", (dialogInterface, i) -> {
                    SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
                    String tipoImp = preferences.getString("tImp","");

                    if(tipoImp != null && tipoImp.equals("Red")) {
                        String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                        int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
                        String contenido = "";
                        int espacios = getSharedPreferences("renglones", Context.MODE_PRIVATE).getInt("espacios", 3);

                        if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                            muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
                        } else {
                            for (Ubicacion u : ubicaciones){
                                contenido += u.getUbicacion()+",T2|";
                                contenido += u.getCodigo()+",T2|";
                                if (codigoBarras)
                                    contenido += u.getCodigo()+",**|";
                                else
                                    contenido += u.getCodigo()+",**|";
                                contenido += " ,T1|";
                            }
                            contenido = contenido.substring(contenido.length()-1);

                            new Impresora(ip,contenido,puerto,espacios);
                        }


                    }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                            d.setContentView(R.layout.dial_no_permiso);
                            Button btn_ok = d.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                d.dismiss();
                            });
                            d.show();
                            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                        } else {
                            ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

                            for (Ubicacion u : ubicaciones){
                                impresora.addTitle(u.getUbicacion());
                                impresora.addTitle(u.getCodigo(), false);
                                if (codigoBarras)
                                    impresora.addBarcodeImage(u.getCodigo(), 400, 50, BarcodeFormat.CODE_128);
                                else
                                    impresora.addBarcode(u.getCodigo());
                                impresora.addEndLine(2);
                            }

                            new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
                        }
                    }

        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }

    /**
     * Actualiza la vista a partir de valor del folio
     */
    private void actualizaView(){
        if(!Libreria.tieneInformacion(folio))
            findViewById(R.id.linearUbicacionesList).setVisibility(View.GONE);
        else {
            findViewById(R.id.linearUbicacionesNuevo).setVisibility(View.GONE);
            findViewById(R.id.linearUbicacionesList).setVisibility(View.VISIBLE);
            traeUbicaciones();
        }
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        if(obj != null){
            switch (output.getTarea()){
                case TAREA_CONSULTA_INVENTARIO:{
                    if(obj.getAsBoolean("exito")){
                        folio = obj.getAsString("anexo");
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);


                    cierraDialogo();
                    actualizaView();
                    break;
                }
                case TAREA_NUEVO_INVENTARIO:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito")){
                        if(!Libreria.tieneInformacion(folio)){
                            folio = obj.getAsString("anexo");
                            actualizaView();
                            muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        }else{
                            folio="";
                            actualizaView();
                            findViewById(R.id.linearUbicacionesNuevo).setVisibility(View.VISIBLE);
                            EditText editNotas = findViewById(R.id.editInventarioFisicoNotas);
                            editNotas.setText("");
                            muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        }
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_TRAE_UBICACIONES:{
                    ubicaciones = servicio.traeUbicaciones();
                    adapter = new UbicacionesAdapter(ubicaciones, this);
                    listUbicaciones.setAdapter(adapter);
                    cierraDialogo();
                    break;
                }
                case TAREA_CUENTA_UBICACION:{
                    cierraDialogo();
                    if(output.getExito()){
                        /*AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                        confirmacion.setTitle("Conteo de piezas");
                        confirmacion.setMessage("¿Desea Iniciar a contar esta ubicación?");
                        confirmacion.setCancelable(false);
                        confirmacion.setPositiveButton("SI", (dialog, which) -> {
                            Intent intent = new Intent(this, Conteo.class);
                            intent.putExtra("asifid", obj.getAsString("asifid"));
                            intent.putExtra("espaid", obj.getAsString("espaid"));
                            intent.putExtra("conteo", obj.getAsString("conteo"));
                            intent.putExtra("ubicacion", obj.getAsString("ubicacion"));
                            intent.putExtra("origen", true);
                            startActivity(intent);
                            finish();
                        });
                        confirmacion.setNegativeButton("NO", null);
                        confirmacion.show();*/
                        Intent intent = new Intent(this, Conteo.class);
                        intent.putExtra("asifid", obj.getAsString("asifid"));
                        intent.putExtra("espaid", obj.getAsString("espaid"));
                        intent.putExtra("conteo", obj.getAsString("conteo"));
                        intent.putExtra("ubicacion", obj.getAsString("ubicacion"));
                        intent.putExtra("origen", true);
                        startActivity(intent);
                        finish();
                    }else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_warning);
                    break;
                }
                case TAREA_APLICA_CIERRA:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito"))
                        onBackPressed();
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_warning);
                }
                case TAREA_ASIGNA_INVENTARIO:
                case TAREA_DESASIGNA_INVENTARIO:
                case TAREA_ABRE_UBICACION:{
                    if(obj.getAsBoolean("exito")){
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        cierraDialogo();
                        traeUbicaciones();
                    }
                    else{
                        cierraDialogo();
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_INFO_INVENTARIO:{
                    if(obj.getAsBoolean("exito")){
                        String html = obj.getAsString("anexo");
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);

                        WebView webview = new WebView(this);
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.loadData(html, "text/html; charset=utf-8", "UTF-8");

                        alert.setView(webview);
                        alert.setNegativeButton("Cerrar", (dialog, id) -> dialog.dismiss());
                        alert.show();
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }
}