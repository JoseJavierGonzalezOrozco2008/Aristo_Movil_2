package com.example.aristomovil2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.Acrividades.ConCuenta;
import com.example.aristomovil2.adapters.GrupoAdapter;
import com.example.aristomovil2.adapters.GrupoAdapterPV;
import com.example.aristomovil2.adapters.RenglonesAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.modelos.Caducidad;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Cobrados;
import com.example.aristomovil2.modelos.Lote;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.modelos.Subalmacen;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class PuntoVenta extends ActividadBase  implements NavigationView.OnNavigationItemSelectedListener{
    private String folio, nomCliente, prodid, notas, titulo, fechaCad, dlot;
    private String empresavende, domiempr, domisucu, domiclte, despedida, pagare;
    private boolean tieneCredito, mandaImprimir, codigoBarras, caducidadesAbierto, pidefact, solifact;
    private boolean credito, ventaCredito, faltacadu, cobraCredito, cambiacredito;
    private boolean cobrarLotes, lotesEdit = false, ocultaCaducidad = false, ocultaLotes = false;
    private int clienteId, lastVenta, estacionID, tipoVenta, barcodeRequest;
    private float caducant, totVenta, cant;
    private ArrayList<Renglon> renglones;
    private ArrayList<Cobrados> cobrados;
    private ArrayList<Caducidad> caducidades;
    private ArrayList<Lote> lotes;
    private BluetoothConnection selectedDevice;
    private final int LAUNCH_CONSULTA_ACTIVITY = 1;
    private ListView listRenglones;
    private TextView txtTotal, txtArticulos;
    private FloatingActionButton btnCobrar;
    private LinearLayout panelCaducidad, panelLotes;
    private EditText editRSCantidad, editRSFecha, editRSLote, editRsCantEnv, editBusqueda;

    private ArrayList<com.example.aristomovil2.modelos.MenuItem> items;
    private DrawerLayout drawerLayoutMPV;

    private NavigationView navigationViewMPV;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_venta);

        setToolBar();
        drawerLayoutMPV =  findViewById(R.id.menu_pv);
        navigationViewMPV = findViewById(R.id.navigationViewPV);
        navigationViewMPV.setNavigationItemSelectedListener(this);
        if(drawerLayoutMPV == null){
            System.out.println("drawerLayoutMPV null");
        }
        SharedPreferences preferences = getSharedPreferences("renglones", MODE_PRIVATE);
        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        String impresora = preferencesConf.getString("impresora", "Predeterminada");
        codigoBarras = preferencesConf.getBoolean(impresora + "CodigoBarras", true);

        estacionID = preferences.getInt("estaid", -1);
        tipoVenta = preferences.getInt("tipoVenta", 48);
        mandaImprimir = preferences.getBoolean("mandaimprimir", false);
        boolean puedecobrar = preferences.getBoolean("puedecobrar", false);
        folio = preferences.getString("folio", "");
        notas = preferences.getString("notas", "");
        titulo = preferences.getString("titulo", "");
        nomCliente = preferences.getString("nomCliente", "Publico en general");
        clienteId = preferences.getInt("clienteId", -1);
        tieneCredito = Libreria.getBoolean(preferences.getString("tieneCredito", "false"));
        credito = Libreria.getBoolean(preferences.getString("credito", "false"));
        cambiacredito = preferences.getBoolean("cambiacredito", true);
        ventaCredito = preferences.getBoolean("ventaCredito", false);
        fechaCad = preferences.getString("fechacad", "MMAA");
        pidefact = preferences.getBoolean("pidefac", false);
        solifact = preferences.getBoolean("solifac", false);

        String title = "";
        if (Libreria.tieneInformacion(folio))
            title = "Folio: " + folio.substring(7);

        inicializarActividad(title + " " + nomCliente);

        if (mandaImprimir) {
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
                    if (bluetoothDevicesList[i - 1].getDevice().getName().equals(impresora)) {
                        selectedDevice = bluetoothDevicesList[i - 1];
                    }
                }
            }
        }

        com.example.aristomovil2.modelos.Ventas miventa=servicio.traeVentaPorFolio(folio);
        if(miventa!=null ){
            pidefact = pidefact && !miventa.isPideFac();
            tieneCredito=Libreria.tieneInformacion(miventa.getCredito());
        }

        listRenglones = findViewById(R.id.renglones_list);
        txtArticulos = findViewById(R.id.txtPuntoVentaArticulos);
        txtTotal = findViewById(R.id.txtPuntoVentaTotal);
        editBusqueda = findViewById(R.id.editPuntoVentaBusqueda);
        Button btnRSCaducidadGuardar = findViewById(R.id.btnRSCaducidadGuardar);
        btnCobrar = findViewById(R.id.btnPuntoVentaCobrar);
        panelCaducidad = findViewById(R.id.PVCaducidad);
        panelLotes  =findViewById(R.id.PVLotes);
        editRSCantidad = findViewById(R.id.editRSCaducidadCantidad);
        editRSFecha = findViewById(R.id.editRSCaducidadFecha);
        editRSLote = findViewById(R.id.editRSCaducidadLote);
        editRsCantEnv = findViewById(R.id.editRSCaducidadCantEnv);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        panelCaducidad.startAnimation(animation);
        panelCaducidad.setVisibility(View.GONE);
        panelLotes.startAnimation(animation);
        panelLotes.setVisibility(View.GONE);
        findViewById(R.id.editRSCaducidadLote).setEnabled(false);
        findViewById(R.id.editRSCaducidadFecha).setEnabled(false);
        ((TextView)findViewById(R.id.txtRSProducto)).setText("");
        ((TextView)findViewById(R.id.txtRSCaducidadCantEnv)).setText("Disp:");
        ((TextView)findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant \nEnv:");
        findViewById(R.id.txtRSCaducidadCantEnv).setVisibility(View.VISIBLE);
        findViewById(R.id.editRSCaducidadCantEnv).setVisibility(View.VISIBLE);
        findViewById(R.id.switchRSCaducidadCalendario).setVisibility(View.GONE);

        if (!puedecobrar)
            btnCobrar.setVisibility(View.GONE);

        editBusqueda.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(editBusqueda);
                insertaRenglon(editBusqueda.getText().toString(), lastVenta);
                editBusqueda.setText("");
                return true;
            }return false;
        });

        findViewById(R.id.btnPuntoVentaBarcode).setOnClickListener(view -> {
            barcodeRequest = 1;
            barcodeEscaner();
        });

        findViewById(R.id.btnPuntoVentaBuscar).setOnClickListener(v -> consulta(editBusqueda.getText().toString(), false));

        findViewById(R.id.btnPuntoVentaPromociones).setOnClickListener(v -> consulta("", true));

        btnCobrar.setOnClickListener(view -> {
            if(tipoVenta!=52){
                cobrarLotes = true;
                peticionWS(Enumeradores.Valores.TAREA_LOTES_PENDIENTES,"SQL","SQL",folio,"false","");
            }else{
                peticionWS(Enumeradores.Valores.TAREA_DVTA_SINEXIST,"SQL","SQL",folio,"","");
            }
        });

        findViewById(R.id.btnRSCaducidadCerrar).setOnClickListener( v -> {
            ocultaCaducidad = false;
            disableEnableView(true, findViewById(R.id.punto_venta_content));
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            panelCaducidad.startAnimation(anim);
            animacionBotonCobra(android.R.anim.fade_in, View.VISIBLE);

            panelCaducidad.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) { panelCaducidad.setVisibility(View.GONE); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            caducidadesAbierto = false;
        });

        findViewById(R.id.btnPVLotesCerrar).setOnClickListener( v -> {
            ocultaLotes = false;
            disableEnableView(true, findViewById(R.id.punto_venta_content));
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            panelLotes.startAnimation(anim);
            animacionBotonCobra(android.R.anim.fade_in, View.VISIBLE);

            panelLotes.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) { panelLotes.setVisibility(View.GONE); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        btnRSCaducidadGuardar.setOnClickListener( v -> {
            if (!editRSCantidad.getText().toString().isEmpty() && !editRSFecha.getText().toString().isEmpty() && !editRSLote.getText().toString().isEmpty()){
                if (!Libreria.validaFecha(fechaCad, editRSFecha.getText().toString()))
                    muestraMensaje("Fecha incompleta", R.drawable.mensaje_error);
                else{
                    hideKeyboard(v);
                    if (Float.parseFloat(editRsCantEnv.getText().toString()) <= Float.parseFloat(editRsCantEnv.getText().toString())){
                        if(Float.parseFloat(editRSCantidad.getText().toString()) <= cant)
                            guardaLote(editRSCantidad.getText().toString());
                        else
                            muestraMensaje("La cantidad a enviar no puede superar la cantidad por ingresar", R.drawable.mensaje_error);
                    }
                    else
                        muestraMensaje("La cantidad a enviar no puede superar la cantidad en almacen", R.drawable.mensaje_error );

                    editRSCantidad.setText("");
                    editRSFecha.setText("");
                    editRSLote.setText("");
                    editRsCantEnv.setText("");
                }
            } else
                muestraMensaje("Debes llenar todos los campos", R.drawable.mensaje_warning);
        });

        editRSCantidad.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                btnRSCaducidadGuardar.performClick();
            }
            return false;
        });

        registerForContextMenu(listRenglones);
        traeRenglones();
        editBusqueda.requestFocus();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayoutMPV, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayoutMPV.addDrawerListener(toggle);
        toggle.syncState();
        peticionWS(Enumeradores.Valores.TAREA_ITEMS_MENU, "SQL", "SQL", usuarioID, "", "");

    }

    private void setToolBar(){
        Toolbar toolbarMPV = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMPV);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_pv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ocultaCaducidad)
            findViewById(R.id.btnRSCaducidadCerrar).performClick();
        else if (ocultaLotes)
            findViewById(R.id.btnPVLotesCerrar).performClick();
        else {
            SharedPreferences preferences = getSharedPreferences("renglones", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("clienteId", -1);
            editor.putString("nomCliente", "Publico en general");
            editor.putString("folio", "");
            editor.putBoolean("ventaCredito", false);
            editor.putString("notas", "");
            editor.apply();

            Intent intent = new Intent(this, Ventas.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Crea las opciones del menu
     * @param menu El menu
     * @return Retorna true para indicar exito
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_punto_venta, menu);

        if(tipoVenta == 52)
            menu.getItem(3).setVisible(false);

        if(tieneCredito) {
            if (ventaCredito || credito) {
                cobraCredito = true;
                menu.getItem(4).setChecked(true);
            }
            if (!cambiacredito)
                menu.getItem(4).setEnabled(false);
        }
        else{
            menu.getItem(4).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener para las opciones del menu
     * @param item El item seleccionado
     * @return Retorna true para indicar exito
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerLayoutMPV == null){
            System.out.println("null drawer");
        }
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayoutMPV.openDrawer(GravityCompat.START);
                return true;
            case R.id.checkExistencias:{
                boolean checkExistencias;
                if (item.isChecked()){
                    item.setChecked(false);
                    checkExistencias = false;
                } else{
                    item.setChecked(true);
                    checkExistencias = true;
                }

                SharedPreferences sharedPreferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("existencias", checkExistencias);
                editor.apply();
                break;
            }
            case R.id.cancelarVenta:{
                if(null != folio) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage("¿Deseas cancelar la venta?")
                            .setPositiveButton("Si", (dialog, id) -> peticionWS(Enumeradores.Valores.TAREA_CANCELA_VENTA, "SQL", "SQL", folio, String.valueOf(usuarioID), "-1"))
                            .setNegativeButton("No", (dialog, id) -> {});

                    builder.show();
                }
                break;
            }
            case R.id.lotesList:{
                cobrarLotes = false;
                peticionWS(Enumeradores.Valores.TAREA_LOTES_PENDIENTES,"SQL","SQL",folio,"true","");
                break;
            }
            case R.id.notasVenta:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notas de venta");
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.dialogo_notas_titulo_venta, null);
                EditText editNotas = v.findViewById(R.id.editPuntoVentaNotas);
                EditText editTitulo = v.findViewById(R.id.editPuntoVentaTitulo);
                editNotas.setText(notas);
                editTitulo.setText(titulo);

                builder.setView(v).
                setPositiveButton("Guardar", (dialog, which) -> {
                    hideKeyboard(this);
                    notas = editNotas.getText().toString();
                    titulo = editTitulo.getText().toString();
                    peticionWS(Enumeradores.Valores.TAREA_ACTUALIZA_TITULO_NOTAS, "SQL", "SQL", folio, titulo, notas);
                }).
                setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel()).
                show();

                break;
            }
            case R.id.credito:{
                if (item.isChecked()){
                    item.setChecked(false);
                    cobraCredito = false;
                } else{
                    item.setChecked(true);
                    cobraCredito = true;
                }
                break;
            }
        }
        return true;
    }

    /**
     * Crea el context menu para los elementos de la lista
     * @param menu El menu
     * @param v La vista a la que se asignara el menu
     * @param menuInfo menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_renglon, menu);
    }

    /**
     * Realiza la accion correspondiente a la opcion del context menu seleccionada
     * @param item Opcion seleccionada
     * @return Retorna true para indicar exito
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.item_renglon_lotes:{
                if(renglones.get(info.position).getCaduca() > 0){
                    lotesEdit = true;
                    caducant = renglones.get(info.position).getCaduca();
                    prodid = String.valueOf(renglones.get(info.position).getProdid());
                    ((TextView)findViewById(R.id.txtRSProducto)).setText(renglones.get(info.position).getProducto());
                    peticionWS(Enumeradores.Valores.TAREA_LOTES_VENTA,"SQL","SQL", folio, prodid, "");
                }
                else
                    muestraMensaje("Producto sin caducidad", R.drawable.mensaje_warning);
                return true;
            }
            case R.id.item_renglon_precio:{
                Dialog dialogo = new Dialog(this);
                dialogo.setContentView(R.layout.dialogo_cambiar_precio);

                TextView txtActual = dialogo.findViewById(R.id.txtDialogoCambiarPrecioActual);
                EditText editNuevo = dialogo.findViewById(R.id.editDialogoCambiarPrecioNuevo);
                Button btnGuardar = dialogo.findViewById(R.id.btnDialogoCambiarPrecioGuardar);

                txtActual.setText(MessageFormat.format("Precio actual: ${0}", renglones.get(info.position).getPrecio()));
                btnGuardar.setOnClickListener(view -> {
                    if(!editNuevo.getText().toString().equals("")){
                        insertaRenglon(renglones.get(info.position).getCodigo() + "@" + editNuevo.getText().toString(),
                                renglones.get(info.position).getDvtaid());
                        dialogo.dismiss();
                    }
                });

                dialogo.show();
                Window window = dialogo.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                return true;
            }
            case R.id.item_renglon_eliminar:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("¿Deseas eliminar el producto?")
                        .setPositiveButton("Si", (dialog, id) -> insertaRenglon("*0", renglones.get(info.position).getDvtaid()))
                        .setNegativeButton("No", (dialog, id) -> {});

                builder.show();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Establece el contenido de la lista de productos
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyPuntoVenta);
        ListView list = findViewById(R.id.renglones_list);
        list.setEmptyView(empty);
    }

    /**
     * Obtiene el resultado del activity consulta
     * @param requestCode El codigo de la peticion
     * @param resultCode El codigo resultado
     * @param data Los datos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_CONSULTA_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                folio = data.getStringExtra("folio");
                prodid = data.getStringExtra("prodId");
                caducant = data.getFloatExtra("caduCant", caducant);
                faltacadu = data.getBooleanExtra("faltaCadu", faltacadu);
                traeRenglones();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                traeRenglones();
            }
        }

        if(barcodeRequest == 1){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if(intentResult.getContents() != null && editBusqueda != null) {
                editBusqueda.setText(intentResult.getContents());
                hideKeyboard(editBusqueda);
                insertaRenglon(editBusqueda.getText().toString(), lastVenta);
                editBusqueda.setText("");
            }
            else
                muestraMensaje("Error al escanear código", R.drawable.mensaje_error);
        }
    }

    /**
     * Cambia a la pantalla de consulta
     */
    public void consulta(String busqueda, boolean promociones){
        ((EditText)findViewById(R.id.editPuntoVentaBusqueda)).setText("");
        Intent intent = new Intent(this, Consulta.class);
        intent.putExtra("venta", true);
        intent.putExtra("clienteId", clienteId);
        intent.putExtra("folio", folio);
        intent.putExtra("busqueda", busqueda);
        intent.putExtra("promociones", promociones);

        barcodeRequest = -1;
        startActivityForResult(intent, LAUNCH_CONSULTA_ACTIVITY);
    }

    /**
     * Trae los renglones de la venta
     */
    public void traeRenglones(){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_RENGLONES, "SQL", "SQL", "<linea><folio>"+folio+"</folio><estatus>0</estatus><noestatus>0</noestatus></linea>", "", "");
    }

    /**
     * Metodo que llama al servicio para guardar un lote
     * @param cant la cantidad del lote
     */
    private void guardaLote(String cant){
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_LOTE_VENTA, "SQL", "SQL", dlot, cant, folio);
    }

    /**
     * Metodo que llama al servicio para borrar un lote
     * @param dlote El lote
     */
    private void borraLote(int dlote){
        peticionWS(Enumeradores.Valores.TAREA_BORRA_LOTE, "SQL", "SQL", String.valueOf(dlote), String.valueOf(prodid), folio);
    }

    private void animacionBotonCobra(int animacion, int visivility){
        Animation anim = AnimationUtils.loadAnimation(this, animacion);
        anim.setDuration(300);
        btnCobrar.startAnimation(anim);
        btnCobrar.getAnimation().setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnCobrar.setVisibility(visivility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Muestra el panel de caducidades
     */
    @SuppressLint("SetTextI18n")
    private void panelCaducidad(){
        ocultaCaducidad = true;
        disableEnableView(false, findViewById(R.id.punto_venta_content));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
        panelCaducidad.startAnimation(anim);
        panelCaducidad.setVisibility(View.VISIBLE);
        animacionBotonCobra(android.R.anim.fade_out, View.GONE);
        caducidadesAbierto = true;
        lotesEdit = false;
        llenarTablaCaducidades();
    }

    public void panelLotes(){
        ocultaLotes = true;
        disableEnableView(false, findViewById(R.id.punto_venta_content));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
        panelLotes.startAnimation(anim);
        panelLotes.setVisibility(View.VISIBLE);
        animacionBotonCobra(android.R.anim.fade_out, View.GONE);

        TableLayout tablaLotes = findViewById(R.id.tablaPVLotes);

        tablaLotes.removeAllViews();
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.DKGRAY);
        row.setGravity(Gravity.CENTER);

        row.addView(getTextview("", Color.WHITE));
        row.addView(getTextview("Cantidad", Color.WHITE));
        row.addView(getTextview("Producto", Color.WHITE));
        row.addView(getTextview("Codigo", Color.WHITE));
        tablaLotes.addView(row);

        for(Lote l:lotes){
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            ImageButton btnLote = new ImageButton(this);
            btnLote.setImageResource(R.drawable.editar);
            btnLote.setBackgroundColor(Color.TRANSPARENT);
            btnLote.setPadding(10, 10, 10, 10);

            View.OnClickListener listener = v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

                v.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationStart(Animation animation) {
                        v.setBackgroundColor(Color.LTGRAY);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        caducidadesAbierto = false;
                        caducant = l.getCantidad();
                        peticionWS(Enumeradores.Valores.TAREA_LOTES_VENTA,"SQL","SQL", folio, String.valueOf(l.getProdid()), "");
                        findViewById(R.id.btnPVLotesCerrar).performClick();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            };

            row.addView(btnLote);
            row.addView(getTextview(String.valueOf(l.getCantidad()), Color.BLACK));
            row.setOnClickListener(listener);
            btnLote.setOnClickListener(listener);
            row.addView(getTextview(l.getDescripcion(), Color.BLACK));
            row.addView(getTextview(l.getCodigo(), Color.BLACK));
            tablaLotes.addView(row);
        }
    }

    /**
     * LLena la tabla de caducidades
     */
    @SuppressLint("SetTextI18n")
    private void llenarTablaCaducidades() {
        TableLayout tablaCaducidades = findViewById(R.id.tablaRSCaducidad);
        tablaCaducidades.setGravity(Gravity.CENTER);
        tablaCaducidades.removeAllViews();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.DKGRAY);
        row.setGravity(Gravity.CENTER);

        row.addView(getTextview("Accion", Color.WHITE));
        row.addView(getTextview("Alm", Color.WHITE));
        row.addView(getTextview("Env", Color.WHITE));
        row.addView(getTextview("Lote", Color.WHITE));
        row.addView(getTextview("Fecha Cad.", Color.WHITE));
        row.setPadding(10, 10, 10, 10);
        tablaCaducidades.addView(row);

        for (int i = 0; i < caducidades.size(); i++) {
            final TableRow rowData = new TableRow(this);
            rowData.setGravity(Gravity.CENTER);

            final int position = i;
            final String date;
            int dlotaux = caducidades.get(i).getDlot();
            String date1;

            try {
                date1 = formatter.format(Objects.requireNonNull(parser.parse(caducidades.get(i).getFecha())));
            } catch (ParseException e) {
                date1 = "Error en Fecha";
                e.printStackTrace();
            }

            /*Boton para editar doc 16*/
            date = date1;
            ImageButton btnEditar = new ImageButton(this);
            btnEditar.setImageResource(R.drawable.editar);
            btnEditar.setBackgroundColor(Color.TRANSPARENT);
            btnEditar.setPadding(10, 10, 10, 10);
            btnEditar.setOnClickListener(view -> {
                editRsCantEnv.setText(String.valueOf(caducidades.get(position).getCant()));
                editRSLote.setText(caducidades.get(position).getLote());
                String[] division = date.split("-", 3);
                editRSFecha.setText(division[0] + division[1] + division[2].substring(division[2].length() - 2));

                dlot = String.valueOf(dlotaux);

                editRSCantidad.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editRSCantidad, InputMethodManager.SHOW_IMPLICIT);
            });
            if (caducidades.get(i).getCant() == caducidades.get(i).getCantl()) {
                btnEditar.setImageResource(R.drawable.editdisabled);
                btnEditar.setEnabled(false);
            }

            ImageButton btnBorrar = new ImageButton(this);
            btnBorrar.setImageResource(R.drawable.eliminar);
            btnBorrar.setBackgroundColor(Color.TRANSPARENT);
            btnBorrar.setPadding(10, 10, 10, 10);
            btnBorrar.setOnClickListener(view -> {

                final android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(this);
                d.setTitle("Borrar Caducidad");
                d.setMessage("¿Seguro que quieres borrar la Caducidad?");
                d.setPositiveButton("Borrar", (dialogInterface, i12) -> {
                    borraLote(caducidades.get(position).getDlot());
                        caducidades.get(position).setCantl(0);
                });
                d.setNegativeButton("Cancelar", (dialogInterface, i1) -> {
                });
                d.show();
            });
            if (caducidades.get(i).getDlot() == 0 || caducidades.get(i).getCantl() == 0){
                btnBorrar.setImageResource(R.drawable.eliminardisabled);
                btnBorrar.setEnabled(false);
            }

            rowData.addView(btnEditar);
            rowData.addView(btnBorrar);
            rowData.addView(getTextview("" + caducidades.get(i).getCant(), Color.BLACK));
            rowData.addView(getTextview("" + caducidades.get(i).getCantl(), Color.BLACK));
            rowData.addView(getTextview("" + caducidades.get(i).getLote(), Color.BLACK));
            rowData.addView(getTextview("" + date, Color.BLACK));

            tablaCaducidades.addView(rowData);
        }
    }

    /**
     * Llama al servicio para insertar un renglon a la venta
     * @param cadena La cadena
     * @param lastVenta El id del ultimo producto insertado
     */
    public void insertaRenglon(String cadena, int lastVenta){
        if(lastVenta != 0 && (cadena.equals("+") || cadena.equals("-")))
            peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                Libreria.xmlInsertVenta(estacionID, usuarioID, cadena, clienteId, folio, lastVenta, tipoVenta, "a", ventaCredito),
                "", "");
        else if(cadena.equals("@"))
            muestraMensaje("Cadena inválida", R.drawable.mensaje_warning);
        else if(cadena.contains("*") || cadena.contains("@"))
            peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                    Libreria.xmlInsertVenta(estacionID, usuarioID, cadena, clienteId, folio, lastVenta, tipoVenta, "a", ventaCredito),
                    "", "");
        else{
            boolean existe = false;
            for(Renglon r:renglones){
                if(r.getCodigo().equals(cadena)){
                    existe = true;
                    peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                            Libreria.xmlInsertVenta(estacionID, usuarioID, cadena, clienteId, folio, r.getDvtaid(), tipoVenta, "a", ventaCredito),
                            "", "");
                    break;
                }
            }

            if(!existe)
                peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                        Libreria.xmlInsertVenta(estacionID, usuarioID, cadena+"*1", clienteId, folio, 0, tipoVenta, "a", ventaCredito),
                        "", "");
        }
    }

    /**
     * Cambia a la pantalla de cobro a contado
     */
    private void cobraContado(){
        Intent intent = new Intent(this, Cobro.class);
        float totPagar = renglones.get(0).getVntatotal();

        intent.putExtra("folio", folio);
        intent.putExtra("cliente", nomCliente);
        intent.putExtra("total", totPagar);
        cierraDialogo();
        startActivity(intent);
        finish();
    }

    /**
     * Llama al servicio de cobro a credito
     */
    private void cobraCredito(){
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("Venta a Crédito");
        dialog.setMessage("¿Desea cobrar esta venta a crédito?");
        dialog.setPositiveButton("Cobrar", (dialogInterface, i) ->
                wsCredito());
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
        dialog.show();
    }

    private void imprimir(){
        Dialog d = new Dialog(this);
        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
        String tipoImp = preferences.getString("tImp","");
        if(tipoImp != null && tipoImp.equals("Red")){
            String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
            int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
            String contenido = "";
            int espacios = getSharedPreferences("renglones",Context.MODE_PRIVATE).getInt("espacios",3);

            if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
            } else {
                for(int copias=0; copias<2; copias++) {
                    contenido += "Venta,T2|";
                    contenido += empresavende+",T1|";
                    contenido += domiempr+",T1|";
                    contenido += "SUCURSAL,T1|";
                    contenido += domisucu+",T1|";
                    contenido += "Vendedor: "+usuario+",T1|";
                    contenido += "Cliente: "+nomCliente+",T1|";
                    contenido += domiclte+",T1|";
                    contenido += folio+",T2|";
                    contenido += "Prod   Can   Precio   Subtotal,T1|";
                    contenido += "----------------,T1|";
                    /*Productos comprados*/
                    for (int i = 0; i < cobrados.size(); i++) {
                        contenido += cobrados.get(i).getProducto() + " " + cobrados.get(i).getCantidad() + " x $" + cobrados.get(i).getPreciou() + " $" + cobrados.get(i).getSubtotal()+",T1|";
                    }
                    contenido += "----------------,T1|";
                    contenido += "TOTAL: "+totVenta+",T2|";

                    if (codigoBarras)
                        contenido += folio+",**|";
                    else
                        contenido += folio+",T1|";

                    contenido += despedida+",T1|";
                    contenido += pagare+",T1|";
                    contenido += "Firma:_________________,T1";
                }

                new Impresora(ip,contenido,puerto,espacios).execute();
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
            }
            else {
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

                for(int copias=0; copias<2; copias++) {
                    impresora.addTitle("Venta");
                    impresora.addLine(empresavende);
                    impresora.addLine(domiempr);
                    impresora.addLine("SUCURSAL");
                    impresora.addLine(domisucu);
                    impresora.addEndLine();
                    impresora.addLine("Vendedor: " + usuario);
                    impresora.addLine("Cliente: " + nomCliente);
                    impresora.addLine(domiclte);
                    impresora.addTitle(folio);
                    impresora.addEndLine();
                    impresora.addLine("Prod | Can | Precio | Subtotal");
                    impresora.addLine("----------------");
                    /*Productos comprados*/
                    for (int i = 0; i < cobrados.size(); i++) {
                        impresora.addLine(cobrados.get(i).getProducto() + " " + cobrados.get(i).getCantidad() + " x $" + cobrados.get(i).getPreciou() + " $" + cobrados.get(i).getSubtotal());
                    }
                    impresora.addLine("----------------");
                    impresora.addTitle("TOTAL: " + totVenta);

                    if (codigoBarras)
                        impresora.addBarcodeImage(folio, 400, 100, BarcodeFormat.CODE_128);
                    else
                        impresora.addBarcode(folio);

                    impresora.addEndLine();
                    impresora.addLine(despedida);
                    impresora.addEndLine();
                    impresora.addLine(pagare);
                    impresora.addEndLine();
                    impresora.addLine("Firma:_________________");
                    impresora.addEndLine(2);
                }

                new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
            }
        } else{
            muestraMensaje("Error en la impresión",R.drawable.mensaje_error);
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayoutMPV.closeDrawer(GravityCompat.START);

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
                Intent intent = new Intent(this, Ventas.class);
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
            case 17:{
                Intent intent = new Intent(this, Lista_Acomodo.class);
                intent.putExtra("repone", 484);//devolucion
                startActivity(intent);
                break;
            }
        }

        return false;
    }
    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();

        if(obj != null){
            if (output.getTarea() == Enumeradores.Valores.TAREA_ITEMS_MENU) {
                NavigationView navigationView = findViewById(R.id.navigationViewPV);
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

                ListView menuList = findViewById(R.id.menu_listpv);

                //GrupoAdapterPV adapter = new GrupoAdapterPV(listas, grupos, this);
                //menuList.setAdapter(adapter);

            }
            switch (output.getTarea()){

                case TAREA_TRAE_RENGLONES:{
                    renglones = servicio.getRenglones(this, folio);

                    totVenta = 0;
                    for (Renglon r:renglones) {
                        totVenta += r.getTotal();
                        if(lastVenta == r.getDvtaid())
                            ((TextView)findViewById(R.id.txtRSProducto)).setText(r.getProducto());
                    }

                    txtTotal.setText(MessageFormat.format("Total: ${0}", totVenta));
                    txtArticulos.setText(MessageFormat.format("Articulos: {0}", renglones.size()));

                    RenglonesAdapter adapter = new RenglonesAdapter(renglones, this);
                    listRenglones.setAdapter(adapter);

                    cierraDialogo();

                    if(faltacadu){
                        peticionWS(Enumeradores.Valores.TAREA_LOTES_VENTA,"SQL","SQL", folio, prodid, "");
                        faltacadu = false;
                    }

                    break;
                }


                case TAREA_INSERTA_RENGLON:{
                    cierraDialogo();
                    if(output.getExito()){
                        folio = obj.getAsString("vntafolio");
                        traeRenglones();

                        String title = "";
                        if(null != folio)
                            title = folio.substring(7);

                        actualizaToolbar("Folio: " +  title + " " + nomCliente);

                        if (obj.get("dvtaid") != null){
                            lastVenta = obj.getAsInteger("dvtaid");
                        } else
                            lastVenta = 0;

                        caducant = obj.getAsFloat("caducant");
                        prodid = obj.getAsString("prodid");
                        faltacadu = obj.getAsBoolean("faltacadu");
                    }
                    else
                        muestraMensaje(obj.getAsString("msg"), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_CANCELA_VENTA:{
                    if(output.getExito()) {
                        folio = null;
                        renglones.clear();
                        RenglonesAdapter adapter = new RenglonesAdapter(renglones, this);
                        listRenglones.setAdapter(adapter);

                        actualizaToolbar(nomCliente);
                        muestraMensaje("Venta Cancelada", R.drawable.mensaje_exito);
                        onBackPressed();
                    }
                    else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
                case TAREA_LOTES_VENTA:{
                    if(caducidades != null)
                        caducidades.clear();
                    if(obj.getAsBoolean("exito")){
                        caducidades = servicio.getCaducidades();
                        float cantLotesTot = 0;

                        for (Caducidad c : caducidades)
                            cantLotesTot += c.getCantl();

                        if ((!caducidadesAbierto && cantLotesTot != caducant) || lotesEdit){
                            panelCaducidad();
                            cant = caducant - cantLotesTot;
                            ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + cant);
                        }
                        if(obj.getAsBoolean("exito")){
                            cant = caducant - cantLotesTot;
                            ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + cant);
                            llenarTablaCaducidades();
                        }
                    }
                    else
                        muestraMensaje("Lista de lotes vacía", R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
                case TAREA_GUARDA_LOTE_VENTA:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        reproduceAudio(R.raw.exito);

                        if (obj.getAsBoolean("cierra"))
                            findViewById(R.id.btnRSCaducidadCerrar).performClick();
                        else
                            peticionWS(Enumeradores.Valores.TAREA_LOTES_VENTA, "SQL", "SQL", folio, prodid, "");
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_BORRA_LOTE:{
                    if(obj.getAsBoolean("exito"))
                        peticionWS(Enumeradores.Valores.TAREA_LOTES_VENTA, "SQL", "SQL", folio, prodid, "");
                    else
                        muestraMensaje("Error al borrar el lote", R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
                case TAREA_LOTES_PENDIENTES:{                    cierraDialogo();

                    if(!obj.getAsBoolean("exito")){
                        lotes = servicio.getLotesVenta();
                        panelLotes();
                    }
                    else{
                        if(cobrarLotes){
                            renglones = servicio.getRenglones(this, folio);

                            if (renglones.size() != 0){
                                int sinExistencias = 0;
                                for (Renglon r:renglones){
                                    if (r.getDisponible() <= 0)
                                        sinExistencias++;
                                }
                                if (sinExistencias > 0){
                                    final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
                                    dialog.setTitle("Productos sin Existencia");
                                    dialog.setMessage("Existen " + sinExistencias + " productos sin existencias, ¿desea terminar la venta o regresar a editarla?");
                                    dialog.setPositiveButton("Terminar Venta", (dialogInterface, i) -> {
                                        cobraVenta();
                                    });
                                    dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
                                    dialog.show();
                                } else{
                                   cobraVenta();
                                }
                            } else
                                muestraMensaje("No hay nada que cobrar", R.drawable.mensaje_warning);
                        }
                        else
                            muestraMensaje("Lista de lotes vacía", R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_DVTA_SINEXIST:{
                    if(pidefact){
                        dlgFactura();
                        return;
                    }
                    String sinExist=obj.getAsString("anexo");
                    sinExist = Libreria.tieneInformacion(sinExist) ? sinExist : "";
                    sinExist = sinExist.replace("<br/>","\n");
                    String mensaje="¿Está seguro de finalizar el pedido?\n{0}";
                    final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
                    dialog.setTitle("Confirmación");
                    dialog.setMessage(MessageFormat.format(mensaje, sinExist));
                    dialog.setPositiveButton("Terminar Venta", (dialogInterface, i) -> {
                        wsCredito();
                    });
                    dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
                    dialog.show();
                    break;
                }
                case TAREA_COBRAR_WS:
                case TAREA_COBRAR: {
                    if (obj.getAsBoolean("exito")) {
                        cobrados = servicio.getCobrados();

                        empresavende = obj.getAsString("empresavende");
                        domiempr = obj.getAsString("domiempr");
                        domisucu = obj.getAsString("domisucu");
                        domiclte = obj.getAsString("domiclte");
                        despedida = obj.getAsString("despedida");
                        pagare = obj.getAsString("pagare");

                        if (mandaImprimir) {
                            imprimir();

                            muestraMensaje("Gracias por su compra", R.drawable.mensaje_exito);
                            btnCobrar.setOnClickListener(v -> onBackPressed());
                        }
                        else {
                            cierraDialogo();
                            muestraMensaje(obj.getAsString("Gracias por su compra"), R.drawable.mensaje_exito);
                            onBackPressed();
                        }
                    } else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                    cierraDialogo();
                    break;
                }
                case TAREA_ACTUALIZA_TITULO_NOTAS:{
                    if(obj.getAsBoolean("exito"))
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
                case TAREA_FACT_GUARDA:
                case TAREA_ACOBRAR:
                    if(output.getExito()){
                        pidefact=false;
                        cobraVenta();
                    }else{
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    }
                    break;
            }
        }else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void cobraVenta(){
        if(pidefact){
            dlgFactura();
        }
        if (!cobraCredito){
            cobraContado();
        } else
            cobraCredito();
    }

    private void wsCredito(){
        String xml="<linea><folioventa>" + folio + "</folioventa><estacion>" + estacionID + "</estacion><usuaid>"
                + usuarioID +"</usuaid><credito>true</credito></linea>";
        if(tipoVenta == 52){
            peticionWS(Enumeradores.Valores.TAREA_COBRAR_WS, "", "", xml, "", "");
        }else{
            peticionWS(Enumeradores.Valores.TAREA_COBRAR, "SQL", "SQL", xml, folio, "");
        }

    }

    public void dlgFactura(){

        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dlg_prepfact);
        //dialog.setTitle("Ajusta Renglon de orden de compra");

        TextView rfc=dialog.findViewById(R.id.fact_rfc);
        TextView domi=dialog.findViewById(R.id.fact_domicilio);
        TextView razon=dialog.findViewById(R.id.fact_razon);
        Button aCobrar=dialog.findViewById(R.id.btnFactCobro);
        Button facturar=dialog.findViewById(R.id.btnFactGuarda);

        //clienteId
        com.example.aristomovil2.modelos.Ventas cliente=servicio.traeVentaPorFolio(folio);
        if(cliente!=null){
            rfc.setText(cliente.getRfc());
            domi.setText(cliente.getDomicilio());
            razon.setText(cliente.getNombrecliente());
        }
        Spinner usoCFDI = dialog.findViewById(R.id.spUso);
        Spinner formaPago = dialog.findViewById(R.id.spForma);
        ArrayList<String> uso=new ArrayList();
        ArrayList<String> forma=new ArrayList();
        uso=servicio.traeDcatalogo(83);
        ArrayAdapter<String> adapterSubalmacenes = new ArrayAdapter(this, android.R.layout.simple_spinner_item, uso);
        adapterSubalmacenes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usoCFDI.setAdapter(adapterSubalmacenes);
        forma=servicio.traeDcatalogo(74);
        ArrayAdapter<String> adapterForma = new ArrayAdapter(this, android.R.layout.simple_spinner_item, forma);
        adapterForma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formaPago.setAdapter(adapterForma);


        aCobrar.setOnClickListener(view -> {
            wsACobrar();
            dialog.dismiss();
        });
        facturar.setOnClickListener(view -> {
            Integer formaId = servicio.traeDcatIdporAbrevi(74,formaPago.getSelectedItem().toString());
            Integer usoId = servicio.traeDcatIdporAbrevi(83,usoCFDI.getSelectedItem().toString());
            wsFactGuarda(usoId,formaId);
            dialog.dismiss();
        });
        /*guarda.setOnClickListener(view -> {
            String costo= editProducto.getText().toString();
            String cant= editdCant.getText().toString();
            Float cants= Libreria.tieneInformacionFloat(editCants.getText().toString(),0);
            if(Libreria.tieneInformacionFloat(costo,0)==0 || Libreria.tieneInformacionFloat(costo,0)==0){
                muestraMensaje("Costo y cantidad son requeridos",R.drawable.mensaje_error);
                return ;
            }
            wsInsertOC(costo,cant,cants+"");
            dialog.dismiss();
        });*/
        dialog.show();
    }

    private void wsFactGuarda(Integer pUso,Integer pForma){
        com.example.aristomovil2.modelos.Ventas venta=servicio.traeVentaPorFolio(folio);
        ContentValues mapa=new ContentValues();
        mapa.put("folio","");
        mapa.put("clteid",clienteId);
        mapa.put("mdpaid",cobraCredito ? 99:98);
        mapa.put("fopaid",pForma);
        mapa.put("cfdiid",pUso);
        mapa.put("ventas",folio);
        mapa.put("esttid",137);
        mapa.put("tipo",444);
        mapa.put("modelo",1);
        mapa.put("empresa",venta.getEmpr());
        mapa.put("unico",true);
        mapa.put("notas","");
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_FACT_GUARDA, "SQL", "SQL", xml, "", "");
    }

    private void wsACobrar(){
        peticionWS(Enumeradores.Valores.TAREA_ACOBRAR, "SQL", "SQL",folio, "", "");
    }
}