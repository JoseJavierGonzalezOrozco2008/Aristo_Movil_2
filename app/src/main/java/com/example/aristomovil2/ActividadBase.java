package com.example.aristomovil2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.Cobropv;
import com.example.aristomovil2.Acrividades.Devolucion;
import com.example.aristomovil2.adapters.ClienteAdapter;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.ReporteAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.facade.Servicio;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.servicio.Finish;
import com.example.aristomovil2.servicio.MetodoWs;
import com.example.aristomovil2.servicio.PruebaWs;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.flexbox.FlexboxLayout;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class ActividadBase extends AppCompatActivity implements Finish {
    public ProgressDialog dialogoCarga;
    public Servicio servicio;
    private Toast lToast;
    private TextView txtToast, txtMensaje,dlgSinRegistros;
    public String usuario, usuarioID,v_nombreestacion;
    private LinearLayout mensaje, mensajeLayout;
    protected Dialog dlgBuscaProds;
    protected AlertDialog v_dlgreporte;
    protected boolean v_esMono;
    private TableLayout tblaProducto;
    private boolean v_pantalla;
    protected Integer v_estacion;
    protected DialogInterface.OnClickListener vOnClick1;
    protected String v_mensajeExtra;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Inicializa la vista para los Toast
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.tost, findViewById(R.id.toast_layout_root));
        txtToast = layout.findViewById(R.id.Toastmensaje);
        lToast = new Toast(getApplicationContext());
        lToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        lToast.setView(layout);
        v_mensajeExtra = "";
    }

    /**
     * Inicializa el toolbar de un activity con el titulo proporcionado
     * Inicilaiza la vista para mostrar los mensajes de la vista
     * inicializa los valores comunes en todas las actividades
     * @param titulo Titulo que sera mostrado en el toolbar
     */
    public void inicializarActividad(String titulo){
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);
        title.setText(titulo);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        inicializarActividad();
    }

    public void inicializarActividad2(String pLinea1,String pLinea2){
        Toolbar toolbar = findViewById(R.id.toolbar2);
        ImageButton regresa = toolbar.findViewById(R.id.toolRegresa);
        regresa.setOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);
        actualizaToolbar2(pLinea1,pLinea2);
        inicializarActividad();
    }

    public void inicializarActividad(){
        servicio = new Servicio(this);

        mensaje = findViewById(R.id.Mensaje);
        txtMensaje = mensaje.findViewById(R.id.txtMensaje);
        mensajeLayout = mensaje.findViewById(R.id.Mensaje_layout_root);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        mensaje.startAnimation(animation);

        SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        usuario = sharedPreferences.getString("user", "administrador");
        usuarioID = sharedPreferences.getString("usuarioID", "-1");
        v_nombreestacion = sharedPreferences.getString("estacion", "Estacion");
        v_estacion  = sharedPreferences.getInt("estaid", 0);
        v_esMono = sharedPreferences.getBoolean("mono", false);
        v_pantalla = false;
    }

    /**
     * Actualiza el titulo en el toolbar
     * @param titulo El titulo nuevo
     */
    public void actualizaToolbar(String titulo){
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);
        title.setText(titulo);
        //title.setOnClickListener(view -> wsPrueba(""));
    }

    /**
     * Actualiza el titulo en el toolbar2
     * @param pLinea1 El texto de la linea1
     * @param pLinea2 El texto de la linea2
     */
    public void actualizaToolbar2(String pLinea1,String pLinea2){
        Toolbar toolbar = findViewById(R.id.toolbar2);
        TextView linea1 = toolbar.findViewById(R.id.TitleToolbar);
        TextView linea2 = toolbar.findViewById(R.id.TitleToolbar2);
        linea1.setText(pLinea1);
        linea2.setText(pLinea2);
        if(!Libreria.tieneInformacion(pLinea2)){
            linea2.setVisibility(View.GONE);
        }else{
            linea2.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Envia una peticion SOAP
     * @param tarea Contiene los datos de la tarea a realizar
     * @param Origen El origen
     * @param Clave Clave
     * @param Dato1 Dato que sera enviado a la peticion
     * @param Dato2 Dato que sera enviado a la peticion
     * @param Dato3 Dato que sera enviado a la peticion
     */
    public void peticionWS(Enumeradores.Valores tarea, String Origen, String Clave, String Dato1, String Dato2, String Dato3){
        cargaDialogo();
        String ip = getValuePreferences("ip");
        MetodoWs metodoWS = Libreria.tieneInformacion(ip) ? new MetodoWs(ip):new MetodoWs();
        metodoWS.termina = this;
        metodoWS.context = this;
        metodoWS.servicio = servicio;
        metodoWS.enviaPeticion = new EnviaPeticion(tarea);
        metodoWS.enviaPeticion.setOrigen(Origen);
        metodoWS.enviaPeticion.setClave(Clave);
        metodoWS.enviaPeticion.setDato1(Dato1);
        metodoWS.enviaPeticion.setDato2(Dato2);
        metodoWS.enviaPeticion.setDato3(Dato3);
        metodoWS.enviaPeticion.setUsuario("1"); //****************************************************************************
        metodoWS.execute();
    }

    public void wsPrueba(String pId){
        if(!Libreria.tieneInformacion(pId)){
            pId = getValuePreferences("ip");
        }
        System.out.println("Ip"+pId);
        PruebaWs metodoWS = new PruebaWs(pId);
        metodoWS.termina = this;
        metodoWS.context = this;
        metodoWS.servicio = servicio;
        metodoWS.enviaPeticion = new EnviaPeticion(Enumeradores.Valores.TAREA_PRUEBA_CONEXION);
        metodoWS.enviaPeticion.setOrigen("");
        metodoWS.enviaPeticion.setClave("");
        metodoWS.enviaPeticion.setDato1("");
        metodoWS.enviaPeticion.setDato2("");
        metodoWS.enviaPeticion.setDato3("");
        metodoWS.enviaPeticion.setUsuario(Libreria.tieneInformacionEntero(usuarioID+"",1)+""); //****************************************************************************
        metodoWS.execute();
    }

    public void wsImprime(Enumeradores.Valores pTarea,String pFolio){
        peticionWS(pTarea, "SQL", "SQL", v_estacion+"", pFolio, usuarioID+"");
    }

    /**
     * LLama al servicio que busca un producto
     * @param busqueda La busqueda realizada
     */
    protected void buscarProducto(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA, "SQL", "SQL",
                "<linea><d1>" + busqueda + "</d1><d2></d2><d3>|||</d3><cliente></cliente></linea>", "", "");
    }

    /**
     * Recibe un llave para buscar un elemento en los SharedPreferences de la aplicacion
     * @param key LLave a buscar
     * @return El string del campo correspondiente a la llave proporcionada
     */
    public String getValuePreferences(String key){
        SharedPreferences preferences;
        preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * Muestra un dialogo de carga
     */
    public void cargaDialogo(){
        /*dialogoCarga = new ProgressDialog(this);
        dialogoCarga.setMessage("Cargando");
        dialogoCarga.setCancelable(false);
        dialogoCarga.setInverseBackgroundForced(false);
        try {
            dialogoCarga.show();
        }catch (Exception ignored){}
        */
        /*FragmentManager fragmento = getSupportFragmentManager();
        if(getApplicationContext()!=null && !fragmento.isDestroyed()){
            ProgressDialogFragment newFragment = new ProgressDialogFragment();
            if(newFragment!=null){
                newFragment.show(fragmento, "Dialogo");
            }
        }*/
    }

    /**
     * Cierra el dialogo de carga
     */
    public void cierraDialogo(){
        /*if(dialogoCarga!=null ){
            dialogoCarga.dismiss();
        }*/
        /*Fragment prev = getSupportFragmentManager().findFragmentByTag("Dialogo");
        if(prev!=null){
            ProgressDialogFragment df = (ProgressDialogFragment) prev;
            df.dismiss();
        }*/
    }

    public static class ProgressDialogFragment extends DialogFragment {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Conectando", "Espere un momento...");
            return progressDialog;

        }
    }

    /**
     * Crea un TextView con el texto y color especificados
     * @param txt El texto a mostrar
     * @param color El color del texto
     * @return El TextView
     */
    public TextView getTextview(String txt, int color){
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextColor(color);
        tv.setPadding(10, 10, 10, 10);

        return tv;
    }

    /**
     * Crea un TextView con el texto, color y longitud maxima especificados
     * @param txt El texto a mostrar
     * @param color El color del texto
     * @param length La longitud maxima del texto
     * @return El TextView
     */
    public TextView getTextview(String txt, int color, int length){
        TextView tv = new TextView(this);
        tv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        tv.setText(txt);
        tv.setTextColor(color);
        tv.setPadding(10, 10, 10, 10);

        return tv;
    }

    /**
     * Muestra un mensaje en la parte inferior de la pantalla
     * @param msj El mensaje a mostrar
     * @param tipo El tipo de mensaje (error, exito, warning)
     */
    public void muestraMensaje(String msj, int tipo){
        txtMensaje.setText(msj);
        mensajeLayout.setBackgroundResource(tipo);
        if(tipo==R.drawable.mensaje_error){
            txtMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            txtMensaje.setTextColor(getResources().getColor(R.color.colorNegro));
        }
        mensajeLayout.setAlpha(Float.parseFloat("1.0"));
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje);
        mensaje.startAnimation(animation);
    }

    public void muestraMensaje(Context context,String msj, int tipo){
        txtMensaje.setText(msj);
        mensajeLayout.setBackgroundResource(tipo);
        if(tipo==R.drawable.mensaje_error){
            txtMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            txtMensaje.setTextColor(getResources().getColor(R.color.colorNegro));
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.mensaje);
        mensaje.startAnimation(animation);
    }

    /**
     * Muestra un mensaje que indicia que un metodo esta en construccion
     */
    public void enConstruccion(){
        muestraMensaje("En construccion", R.drawable.mensaje_warning);
    }

    /**
     * Muestra un Toast con un mensaje
     * @param pMensaje El mensaje a mostrar
     * @param duracion La duracion del mensaje
     */
    public void enviaMensaje(String pMensaje, int duracion){
        lToast.setDuration(duracion);
        txtToast.setText(pMensaje);
        lToast.show();
    }

    /**
     * Muestra un Toast con un mensaje
     * @param pMensaje El mensaje a mostrar
     */
    public void enviaMensaje(String pMensaje){
        enviaMensaje(pMensaje, 1);
    }

    /**
     * Cambia el estado (enable/disable) de una vista y sus hijos
     * @param enable El estado de la vista
     * @param vg La vista
     */
    public void disableEnableView(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableView(enable, (ViewGroup)child);
            }
        }
    }

    /**
     * Reproduce un audio
     * @param id El ID del recurso de audio
     */
    public void reproduceAudio(int id){
        final MediaPlayer mp;
        mp = MediaPlayer.create(this, id);
        mp.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());//quitar si causa problemas
        mp.start();
    }

    public void vibrar(int time){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(time);
    }

    /**
     * Muestra la pantalla para escanear un codigo de barras
     */
    public void barcodeEscaner(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setPrompt("");
        intentIntegrator.initiateScan();
    }

    public void addPreferenceString(SharedPreferences.Editor pEditor, ContentValues pObj, String pKey, String pName, String pDefault){
        if(pObj.containsKey(pKey))
            pEditor.putString(pName, pObj.getAsString(pKey));
        else
            pEditor.putString(pName, pDefault);
    }

    public void addPreferenceInt(SharedPreferences.Editor pEditor, ContentValues pObj, String pKey, String pName, Integer pDefault){
        if(pObj.containsKey(pKey))
            pEditor.putInt(pName, pObj.getAsInteger(pKey));
        else
            pEditor.putInt(pName, pDefault);
    }

    public void addPreferenceBoolean(SharedPreferences.Editor pEditor, ContentValues pObj, String pKey, String pName, Boolean pDefault){
        if(pObj.containsKey(pKey))
            pEditor.putBoolean(pName, pObj.getAsBoolean(pKey));
        else
            pEditor.putBoolean(pName, pDefault);
    }

    /**
     * Oculta el teclado tomando un Activity como referencia
     * @param activity Actividad en la que se ocultara el teclado
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Oculta el teclado tomando un elemento View como referencia
     * @param view El elemento desde el que se ocultara el teclado (Comunmente es un EditText)
     */
    public void hideKeyboard(View view) {
        view .postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        },50);
        /*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }

    public void showKeyboard(View view) {
        view .postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        },50);
        /*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/
    }

    /**
     * Procesa la espuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) throws UnsupportedEncodingException { }

    public void dialogoBuscaProductos(Boolean pTeclado){
        dlgBuscaProds = new Dialog(this);
        Objects.requireNonNull(dlgBuscaProds.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dlgBuscaProds.setContentView(R.layout.dialogo_busca_codigo);
        EditText editProducto = dlgBuscaProds.findViewById(R.id.editProducto);
        tblaProducto = dlgBuscaProds.findViewById(R.id.tablaProductos);
        tblaProducto.removeAllViews();
        dlgSinRegistros = dlgBuscaProds.findViewById(R.id.dlgVistaSinRegistros);
        if(pTeclado)
            editProducto.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editProducto.setInputType(InputType.TYPE_CLASS_TEXT);
        editProducto.requestFocus();

        editProducto.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!editProducto.getText().toString().isEmpty()) {
                    String busqueda = editProducto.getText().toString();
                    buscarProducto(busqueda);
                } else
                    muestraMensaje("Campo vacio", R.drawable.mensaje_warning);
            }return false;
        });
        dlgBuscaProds.show();
    }

    public void llenarTablaProductos(View.OnClickListener boton){
        tblaProducto.removeAllViews();
        ArrayList<Producto> productos = servicio.getProductos(this);
        dlgSinRegistros.setVisibility(View.GONE);
        if(productos.isEmpty()){
            dlgSinRegistros.setVisibility(View.VISIBLE);
            return;
        }
        final TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.DKGRAY);
        header.setGravity(Gravity.CENTER);
        /*TextView h1 = new TextView(this);
        h1.setText("");
        h1.setTextColor(Color.WHITE);
        h1.setPadding(10, 10, 10, 10);
        header.addView(h1);*/

        TextView h2 = new TextView(this);
        h2.setText("Producto");
        h2.setTextColor(Color.WHITE);
        h2.setPadding(10, 10, 10, 10);
        header.addView(h2);
        tblaProducto.addView(header);

        for(Producto producto: productos){
            final TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            /*ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);
            btnSeleccionar.setOnClickListener(boton);
            btnSeleccionar.setTag(producto.getCodigo());
            row.addView(btnSeleccionar);*/
            TextView r2 = new TextView(this);

            r2.setText(producto.getProducto());
            r2.setTextSize(18);
            r2.setTextColor(Color.BLACK);
            r2.setPadding(10, 10, 10, 10);
            row.addView(r2);
            row.setOnClickListener(boton);
            row.setTag(producto.getCodigo());

            tblaProducto.addView(row);
        }
    }

    public BluetoothConnection traeImpresora(String nombre_impresora){
        BluetoothConnection selectedDevice = null;
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if (bluetoothDevicesList != null) {
            for (BluetoothConnection device : bluetoothDevicesList) {
                if (device.getDevice().getName().equals(nombre_impresora))
                    selectedDevice = device;
            }
        }
        return selectedDevice;
    }

    public boolean esHorizontal(){
        int display_mode = getResources().getConfiguration().orientation;
        return display_mode == Configuration.ORIENTATION_LANDSCAPE;
    }

    public void dlgMensajeError(String pMensaje,Integer pTipo){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.item_venta, null);
        LinearLayout linea = vista.findViewById(R.id.regeGen);
        TextView uno=vista.findViewById(R.id.rere_codigo);
        TextView dos=vista.findViewById(R.id.rere_can);
        uno.setVisibility(View.GONE);
        dos.setVisibility(View.GONE);
        Button regresa= vista.findViewById(R.id.repoCerrar);
        //linea.addView(regresa);
        TextView mensaje = vista.findViewById(R.id.txtVentasCliente);
        if(pMensaje.contains("</") || pMensaje.contains("/>")){
            String html="<html>{0}</html>";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mensaje.setText(Html.fromHtml(MessageFormat.format(html,pMensaje), Html.FROM_HTML_MODE_COMPACT));
            } else {
                mensaje.setText(Html.fromHtml(MessageFormat.format(html,pMensaje)));
            }
        }else{
            mensaje.setText(pMensaje);
        }
        builder.setView(vista);
        builder.setTitle("");

        builder.setCancelable(true);
        AlertDialog alert=builder.create();
        /*regresa.setText("Regresar");
        regresa.setGravity(Gravity.CENTER);
        regresa.setLayoutParams(new LinearLayout.LayoutParams(-2,-1));*/
        regresa.setOnClickListener(view -> alert.dismiss());
        regresa.setVisibility(View.VISIBLE);
        mensaje.setBackgroundResource(pTipo);
        if( pTipo == R.drawable.mensaje_error ){
            mensaje.setBackgroundResource(R.drawable.mensaje_error2);
            mensaje.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            mensaje.setTextColor(getResources().getColor(R.color.colorNegro));
        }
        alert.show();
    }

    public void doImprime(String pTexto,Boolean pSalida){
        if(muestraPantalla()){
            dlgImprimirAPantalla(pTexto,pSalida);
            return;
        }
        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
        String tipoImp = preferences.getString("tImp","");
        int espacios = getSharedPreferences("renglones",Context.MODE_PRIVATE).getInt("espacios",3);
        if(Libreria.tieneInformacion(tipoImp) ){
            if(tipoImp.equals("Red")){
                String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
                new Impresora(ip,pTexto,puerto,espacios).execute();
            }else if(tipoImp.equals("Bluetooth")){
                if ( doPermisos()) {
                    System.out.println("imprimiendo");
                    SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
                    String pimpresora = preferencesConf.getString("impresora", "Predeterminada");
                    BluetoothConnection selectedDevice = traeImpresora(pimpresora);
                    ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
                    Libreria.imprimeSol(impresora,pTexto,espacios);
                    try {
                        new AsyncBluetoothEscPosPrint(this,pSalida).execute(impresora.Imprimir());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }else if(tipoImp.equals("Pantalla")){
                v_pantalla = true;
                dlgImprimirAPantalla(pTexto,pSalida);
            }
        }else{
            dlgImprimirAPantalla(pTexto,pSalida);
            //dlgMensajeError("No esta configurada la impresora",R.drawable.mensaje_error);
        }
    }

    public boolean doPermisos(){
        String[] perms = { Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        String[] permBluConn = {Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN};

        if(Build.VERSION.SDK_INT < 31){
            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, "Se requieren algunos permisos.",
                        123, perms);
                return false;
            } else {
                return true;
            }
        } else if(Build.VERSION.SDK_INT >= 31){
            if (!EasyPermissions.hasPermissions(this, permBluConn)) {
                EasyPermissions.requestPermissions(this, "Se requieren algunos permisos.", 123, permBluConn);
                return false;
            } else {
                System.out.println(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED);
                return true;
            }
        }
        return true;
    }

    public void aPantalla(Boolean pPantalla){
        v_pantalla = pPantalla;
    }

    public boolean muestraPantalla(){
        return v_pantalla;
    }

    public void dlgImprimirAPantalla(String pCadena,Boolean pFinishAct){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        String pMensaje = Libreria.toHtml(pCadena);
        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.item_dlgreporte, null);
        ScrollView scrollMsj = vista.findViewById(R.id.scrollTicket);
        FlexboxLayout flexLayout = vista.findViewById(R.id.flexOpcs);

        Button regresa= new Button(this);
        //linea.addView(regresa,0);

        builder.setView(vista);
        builder.setTitle("");

        builder.setCancelable(true);
        AlertDialog alert=builder.create();
        regresa.setText("Regresar");
        regresa.setGravity(Gravity.CENTER);
        regresa.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        regresa.setOnClickListener(view -> {
            alert.dismiss();
            if(pFinishAct){
                this.onBackPressed();
            }
        });

        WebView webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        //ImageButton compartir = new ImageButton(this);
        Button compartir = new Button(this);
        compartir.setText("Compartir");
        //compartir.setBackgroundColor(Color.TRANSPARENT);
        compartir.setOnClickListener(view -> {
            Bitmap bitmap = captureWebView(webView);
            if (bitmap != null) {
                File imageFile = saveBitmap(bitmap);
                String mensaje = Libreria.traeInfo(v_mensajeExtra,"Compartir");
                compartirMensaje(mensaje, imageFile);
            } else {
                Toast.makeText(this, "Error al generar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
        flexLayout.addView(regresa);
        flexLayout.addView(compartir);
        scrollMsj.addView(webView);

        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        String html = "<html><style type=\"text/css\">\n" +
                "body {\n" +
                "    font-family: 'monospace';\n" +
                "     color: #3e3e3e;\n"+
                "    font-size: 100%;\n" +
                "    word-break: break-all;\n" +
                "}\n" +
                "</style><body><pre>" + pMensaje + "</pre></body></html>";
        //System.out.println(html);
        webView.loadDataWithBaseURL(null,html   , "text/html", "UTF-8",null);

        alert.getWindow().setLayout(width, -2);
        alert.show();
    }

    public void dlgReporte(Integer pOpcion){
        Generica encabezado=servicio.traeGenReporteEnca();
        if(encabezado==null){
            dlgMensajeError("No se pudo obtener informacion del Reporte",R.drawable.mensaje_error);
            return;
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_reporenglones, null);
        View titulo=inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        builder.setCancelable(true);

        TextView ayuda = vista.findViewById(R.id.repoAyuda);
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        ImageButton nuevo = titulo.findViewById(R.id.btnTitNuevo);
        ImageButton regresa = titulo.findViewById(R.id.btnTitRegresa);
        nuevo.setVisibility(View.GONE);
        ListView ventas = vista.findViewById(R.id.listRepoRenglones);
        titTitulo.setText(encabezado.getTex1());
        ayuda.setText(encabezado.getTex2());
        ayuda.setVisibility(View.VISIBLE);

        List<Generica> reporte = servicio.traeGenReporte();
        //GenericaAdapter v_Adavnta = new GenericaAdapter(reporte,this,pOpcion);
        ReporteAdapter v_Adavnta = traeadaptador(reporte,pOpcion,encabezado.getTex1());
        ventas.setAdapter(v_Adavnta);
        TextView texto=vista.findViewById(R.id.repoVacio);
        texto.setText("Sin elementos");
        ventas.setEmptyView(texto);

        int[] colors = {0, 0xFF000000, 0};
        ventas.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        ventas.setDividerHeight(1);
        v_dlgreporte = builder.create();

        regresa.setOnClickListener(v-> v_dlgreporte.dismiss());

        v_dlgreporte.show();
    }

    private ReporteAdapter traeadaptador(List<Generica> pListado,Integer pOpcion,String pTitulo){
        ReporteAdapter retorno ;
        Enumeradores.Valores pImprime = null;
        String titulo="",mensaje="",boton1="",boton2="";
        switch(pOpcion){
            case 2:
                pImprime = Enumeradores.Valores.TAREA_VNTAULTIMAVNTA;
                break;
            case 3:
                pImprime = Enumeradores.Valores.TAREA_IMPRIMEARQE;
                break;
            case 4:
                pImprime = Enumeradores.Valores.TAREA_IMPRIMEDOCS;
                break;
            case 6:
                pImprime = Enumeradores.Valores.TAREA_IMPRIMERETIRO;
                break;
            case 9:
                pImprime = Enumeradores.Valores.TAREA_TIKCORTEMOVIL;
                break;
            case 5:
                titulo="Continuar con la venta";
                mensaje="¿Seguro de bajar la venta con folio {0}?";
                boton1 = "Continuar";
                boton2 = "";
                break;
            case 7:
                titulo="Gestión del catalogo del producto";
                mensaje="¿Qué quiere hacer a continuación con el producto  \n {1}?";
                boton1 = esMono() ? "Editar":"";
                boton2 = "";
                break;
            case 10:
                titulo = "Devolución del producto";
                mensaje = "¿Qué quiere hace a continuación con el producto  \n {1}?";
                boton1 = "Devolver";
                boton2 = "";
                break;
        }
        retorno = new ReporteAdapter(pListado,this,pImprime,pOpcion);
        if(Libreria.tieneInformacion(titulo)){
            retorno.cambiaMensaje(titulo,mensaje,boton1,boton2);
        }
        return retorno;
    }

    public boolean esMono() {
        return v_esMono;
    }

    private Bitmap captureWebView(WebView webView) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private File saveBitmap(Bitmap bitmap) {
        File imagePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_image.png");
        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void compartirMensaje(String pMensaje, File imageFile) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");

        // Mensaje Extra
        intent.putExtra(Intent.EXTRA_TEXT, pMensaje);

        // Imagen
        //Uri imageUri = Uri.fromFile(imageFile);
        Uri imageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imageFile);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);

        startActivity(intent);

    }

    public void setMensajeExtra(String v_mensajeExtra) {
        this.v_mensajeExtra = v_mensajeExtra;
    }
}
