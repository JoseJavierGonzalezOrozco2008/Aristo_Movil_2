package com.example.aristomovil2;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_BUSCA_UBICACION;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CONT_LISTA_CONTADO;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CONT_MIS_ASIG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.UbicacionesAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ContUbi extends ActividadBase {
    private String folio,ascoid,ubicacion;
    private Integer ubikid,estatus;
    private Boolean asignado;
    private boolean codigoBarras;
    private BluetoothConnection selectedDevice;
    private  ArrayList<Ubicacion> ubicaciones;
    private ListView listUbicaciones;
    private UbicacionesAdapter adapter;
    private Button btnContar,btnNuevo,btnAsigna,btnabre;
    private TextView codi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_conteo_ubicacion);
        setContentView(R.layout.activity_contubi);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Conteo"));
        folio = "";
        estatus = -1;
        asignado = false;

        codi=findViewById(R.id.editConteoUbicacionUbicacion);
        btnContar = findViewById(R.id.btnCoUbContar);
        btnNuevo = findViewById(R.id.btnCoUbNuevo);
        btnAsigna = findViewById(R.id.btnCoUbAsigna);
        btnabre = findViewById(R.id.btnCoUbAbre);
        btnContar.setOnClickListener((view) -> {
            //String codigo = ((EditText)findViewById(R.id.editConteoUbicacionUbicacion)).getText().toString();
            if(Libreria.tieneInformacion(ubicacion)){
                /*AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                confirmacion.setTitle("Conteo de piezas");
                confirmacion.setMessage("¿Desea Iniciar a contar esta ubicación?");
                confirmacion.setCancelable(false);
                confirmacion.setPositiveButton("SI", (dialog, which) -> {
                    Intent intent = new Intent(this, ContProd.class);
                    intent.putExtra("ascoid", ascoid);
                    intent.putExtra("ubikid", ubikid);
                    intent.putExtra("conteo", folio);
                    intent.putExtra("ubicacion", ubicacion);
                    intent.putExtra("origen", true);
                    startActivity(intent);
                    finish();
                });
                confirmacion.setNegativeButton("NO", null);
                confirmacion.show();*/
                Intent intent = new Intent(this, ContProd.class);
                intent.putExtra("ascoid", ascoid);
                intent.putExtra("ubikid", ubikid);
                intent.putExtra("conteo", folio);
                intent.putExtra("ubicacion", ubicacion);
                intent.putExtra("origen", true);
                startActivity(intent);
                finish();
            }else{
                muestraMensaje("Escanea una ubicacion valida",R.drawable.mensaje_error);
            }
        });
        btnNuevo.setOnClickListener((view) -> wsCreaConteo());
        btnAsigna.setOnClickListener((view) -> wsAsignaUbi());
        btnabre.setOnClickListener((view) -> wsAscoEstatus(estatus==108 ?107:108));

        codi.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                String codigo = ((EditText)view).getText().toString();
                if(Libreria.tieneInformacion(codigo)) {
                    hideKeyboard(view);
                    //contarUbicacion(codigo);
                    contarUbicacion(codigo);
                }
                else
                    muestraMensaje("Ingresa una ubicación", R.drawable.mensaje_warning);
                return true;
            }else
                return false;
        });

        findViewById(R.id.btnConteoUbicacionBarcode).setOnClickListener(view -> barcodeEscaner());
        wsTraeConteo();
        actualizaBotones();
    }

    /**
     * Establece el contenido de la lista de ubicaciones
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        /*View empty = findViewById(R.id.emptyInventario);
        ListView list = findViewById(R.id.listUbicaciones);
        list.setEmptyView(empty);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conteo_fisico, menu);
        MenuItem lista=menu.findItem(R.id.menuInventarioFisicioinfo);
        lista.setVisible(true);
        lista.setTitle("Mis asignaciones");
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
                if(Libreria.tieneInformacion(folio)){
                    AlertDialog.Builder confirmacioncan = new AlertDialog.Builder(this);
                    confirmacioncan.setTitle("Cancela el conteo "+folio);
                    confirmacioncan.setMessage("¿Estás seguro de cancelar el conteo?");
                    confirmacioncan.setCancelable(false);
                    confirmacioncan.setPositiveButton("SI", (dialog, which) -> cancelaInventario());
                    confirmacioncan.setNegativeButton("NO", null);
                    confirmacioncan.show();
                }  else{
                    muestraMensaje("No hay conteo disponible",R.drawable.mensaje_error);
                }
                break;
            case R.id.menuInventarioFisicioinfo:
                servicio.borraDatosTabla("generica");
                peticionWS(TAREA_CONT_MIS_ASIG,"SQL","SQL",usuarioID,folio,"");
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
     * Llama al servicio para crear un nuevo inventario     *
     */
    private void cancelaInventario(){
        peticionWS(Enumeradores.Valores.TAREA_CONT_CANCELA, "SQL", "SQL",folio, usuarioID, "");
    }

    /**
     *Llama al servicio para aplicar y cerar el inventario
     */
    private void aplicaCierra(){
        peticionWS(Enumeradores.Valores.TAREA_CONT_APLICA, "SQL", "SQL", usuarioID, folio, "");
    }

    /**
     * Inicia el conteo de una ubicacion
     * @param ubicacion La ubicacion a contar
     */
    public void contarUbicacion(String ubicacion) {
        peticionWS(Enumeradores.Valores.TAREA_CONT_VALIDA_ASIG, "SQL", "SQL", usuarioID, ubicacion, "");
    }

    /**
     * Trae la lista de ubicaciones
     */
    private void wsTraeConteo(){
        peticionWS(Enumeradores.Valores.TAREA_CONT_TRAECONTEO, "SQL", "SQL", usuarioID, "", "");
    }

    public void wsBuscaUbicacion(String ubicacion){
        peticionWS(TAREA_BUSCA_UBICACION, "SQL", "SQL", ubicacion, "", "");
    }

    private void wsCreaConteo(){
        peticionWS(Enumeradores.Valores.TAREA_CONT_GUARDA, "SQL", "SQL", "<linea><cont>0</cont><usua>"+usuarioID+"</usua>" +
                "<tipo>533</tipo><estatus>9</estatus><notas>Desde HH</notas></linea>", "", "");
    }

    private void wsAsignaUbi(){
        if(ubikid!=null && ubikid>0){
            peticionWS(Enumeradores.Valores.TAREA_CONT_ASIGNA, "SQL", "SQL", "<linea><asco>0</asco><cont>"+folio+"</cont><usua>"+usuarioID+"</usua>" +
                "<ubik>"+ubikid+"</ubik><estatus>105</estatus><notas>Asignada desde HH</notas></linea>", "", "");
        }else{
            muestraMensaje("No se encontro una ubicacion",R.drawable.mensaje_error);
        }
    }

    private void wsAscoEstatus(Integer pEstatus){
        if(Libreria.tieneInformacion(ubicacion)){
            peticionWS(Enumeradores.Valores.TAREA_CONT_ESTATUS, "SQL", "SQL", ascoid, ""+pEstatus, "");
        }else{
            muestraMensaje("Escanea una ubicacion valida",R.drawable.mensaje_error);
        }
    }

    @SuppressLint("SetTextI18n")
    private void dlgMuestaUbicaciones() {
        Dialog dialogo = new Dialog(this);

        //dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogo.setContentView(R.layout.dialogo_ubicaciones_lista_productos2);
        TextView titulo = dialogo.findViewById(R.id.listUbicacionesDialogoTitulo);
        ListView lista = dialogo.findViewById(R.id.listUbicacionesDialogoProductos);
        List<Generica> listaGenerica=servicio.traeGenerica();
        GenericaAdapter adapter= new GenericaAdapter(listaGenerica,this,0);
        titulo.setText("Mis asignaciones ("+listaGenerica.size()+" rengs.)");
        titulo.setTextColor(getResources().getColor(R.color.colorWhite));
        lista.setAdapter(adapter);
        dialogo.show();
    }

    /**
     * Imprime las ubicaciones seleccionadas
     * @param ubicaciones Lista de ubicaciones seleccionadas
     */
    public void imprimir(ArrayList<Ubicacion> ubicaciones){
        Dialog d = new Dialog(ContUbi.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imprimir Ubicaciones");
        builder.setMessage("¿Estas seguro de imprimir las ubicaciones seleccionadas?");
        builder.setCancelable(false);
        builder.setPositiveButton("SI", (dialogInterface, i) -> {
                    SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
                    String tipoImp = preferences.getString("tImp","");

                    if(tipoImp != null && tipoImp.equals("Red")) {
                        String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                        int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
                        String contenido = "";
                        int espacios = getSharedPreferences("renglones", Context.MODE_PRIVATE).getInt("espacios", 3);

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

                    }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                            d.setContentView(R.layout.dial_no_permiso);
                            Button btn_ok = d.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {
                                d.dismiss();
                            });
                            d.show();
                        }
                        else {
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
        if(!Libreria.tieneInformacion(folio)) {
            findViewById(R.id.lyUbicacion).setVisibility(View.GONE);
            findViewById(R.id.lyNuevo).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.lyUbicacion).setVisibility(View.VISIBLE);
            findViewById(R.id.lyNuevo).setVisibility(View.GONE);
            //findViewById(R.id.linearUbicacionesList).setVisibility(View.VISIBLE);
            //traeUbicaciones();
        }
    }

    private void actualizaBotones(){
        if(!asignado && estatus!=0){
            btnContar.setVisibility(View.GONE);
            btnAsigna.setVisibility(View.GONE);
            btnabre.setVisibility(View.GONE);
            return;
        }
        if(estatus==-1){
            btnContar.setVisibility(View.GONE);
            btnAsigna.setVisibility(View.GONE);
            btnabre.setVisibility(View.GONE);
        }else if(estatus==0){
            btnContar.setVisibility(View.GONE);
            btnAsigna.setVisibility(View.VISIBLE);
            btnabre.setVisibility(View.GONE);
        }else if(estatus==108){
            btnContar.setVisibility(View.VISIBLE);
            btnAsigna.setVisibility(View.GONE);
            btnabre.setVisibility(View.VISIBLE);
            btnabre.setText("CIERRA");
        }else{
            btnContar.setVisibility(View.GONE);
            btnAsigna.setVisibility(View.GONE);
            btnabre.setVisibility(View.VISIBLE);
            btnabre.setText("ABRE");
        }
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        ContentValues obj = (ContentValues) output.getExtra1();
        if(obj != null){
            switch (output.getTarea()){
                case TAREA_CONT_TRAECONTEO:{
                    if(obj.getAsBoolean("exito")){
                        folio = obj.getAsString("anexo");
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    actualizaView();
                    break;
                }
                case TAREA_CONT_GUARDA:{
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
                case TAREA_BUSCA_UBICACION:{
                    TextView men1 = findViewById(R.id.contubi_Men1);
                    men1.setText(obj.getAsString("ubicacion"));
                    /*ubicaciones = servicio.traeUbicaciones();
                    adapter = new UbicacionesAdapter(ubicaciones, this);
                    listUbicaciones.setAdapter(adapter);*/
                    break;
                }
                case TAREA_CONT_VALIDA_ASIG:{
                    ascoid = obj.getAsString("ascoid");
                    ubikid = obj.getAsInteger("ubikid");
                    asignado = obj.getAsBoolean("asignado");
                    estatus = obj.getAsInteger("estatus");
                    estatus = estatus==null ? 0 : estatus;
                    TextView men1 = findViewById(R.id.contubi_Men1);
                    men1.setText(obj.getAsString("ubicacion"));
                    ubicacion = "";
                    if(!output.getExito()){
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_warning);
                    }
                    if(output.getExito() || estatus==107){
                        ubicacion = obj.getAsString("ubicacion");
                    }
                    actualizaBotones();
                    break;
                }
                case TAREA_CONT_CANCELA:
                case TAREA_CONT_APLICA:{
                    if (obj.getAsBoolean("exito"))
                        onBackPressed();
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_warning);
                }
                case TAREA_DESASIGNA_INVENTARIO:
                case TAREA_CONT_ASIGNA:
                case TAREA_CONT_ESTATUS:{
                    if(obj.getAsBoolean("exito")){
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        //traeUbicaciones();
                        //TextView codi=findViewById(R.id.editConteoUbicacionUbicacion);
                        contarUbicacion(codi.getText().toString());
                    }else{
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
                    break;
                }
                case TAREA_CONT_MIS_ASIG:{
                    if(output.getExito()){
                        dlgMuestaUbicaciones();
                    }else{
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                }break;
            }
        }
        else {
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //TextView editBusqueda = findViewById(R.id.editConteoUbicacionUbicacion);
        if(intentResult.getContents() != null && codi != null) {
            codi.setText(intentResult.getContents());
            contarUbicacion(intentResult.getContents());
        }
        else
            muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
    }
}