package com.example.aristomovil2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aristomovil2.adapters.ConsultaAdapter;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.Locale;

public class Consulta extends ActividadBase{
    ArrayList<Producto> productos;
    private LinearLayout filtros;
    public int clienteId, tipoVenta, estacionID;
    public String folio, prodid, busqueda;
    public boolean ventaCredito, venta, faltacadu, promociones, ocultaFiltros = false;
    private float caducant;
    private EditText editBusqueda;
    private Button btnFiltrar, btnCerrarFiltros;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        Bundle extras = getIntent().getExtras();

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Consulta"));

        SharedPreferences preferences = getSharedPreferences("renglones", MODE_PRIVATE);
        estacionID = preferences.getInt("estaid", -1);
        tipoVenta = preferences.getInt("tipoVenta", 48);
        ventaCredito = preferences.getBoolean("ventaCredito", false);

        venta = extras.getBoolean("venta");

        if(venta){
            clienteId = extras.getInt("clienteId");
            folio = extras.getString("folio");
            busqueda = extras.getString("busqueda");
            promociones = extras.getBoolean("promociones");
        }
        else{
            clienteId = preferences.getInt("clienteId", -1);
            folio = "";
            promociones = false;
        }

        FloatingActionButton btnFiltros = findViewById(R.id.btnConsultaFiltros);
        filtros = findViewById(R.id.FiltrosConsulta);
        btnFiltrar = filtros.findViewById(R.id.btnFiltrosConsultaFiltrar);
        btnCerrarFiltros = filtros.findViewById(R.id.btnFiltrosConsultaCerrar);
        ImageButton btnBrcode = filtros.findViewById(R.id.btnFiltrosConsultaBarcode);
        ImageButton btnMicro = filtros.findViewById(R.id.btnFiltrosConsultaMicrofono);
        editBusqueda = filtros.findViewById(R.id.btnFiltrosConsultaBusqueda);
        CheckBox checkExistencias = filtros.findViewById(R.id.checkFiltrosConsultaExistencias);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        filtros.startAnimation(animation);
        filtros.setVisibility(View.GONE);

        /*Listener del boton de filtros, muestra el panel de filtros y oculta el boton de filtros*/
        btnFiltros.setOnClickListener(view -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
            filtros.startAnimation(anim);
            filtros.setVisibility(View.VISIBLE);
            ocultaFiltros = true;

            anim = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out);
            anim.setDuration(300);
            btnFiltros.startAnimation(anim);
            btnFiltros.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    disableEnableView(false, findViewById(R.id.consulta_content));
                    btnFiltros.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        /*Listener del boton de cerrar filtros, cierra el panel de filtros y muestra el boton de filtros*/
        btnCerrarFiltros.setOnClickListener(view -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            filtros.startAnimation(anim);
            hideKeyboard(view);
            ocultaFiltros = false;

            anim = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
            anim.setDuration(300);
            btnFiltros.startAnimation(anim);
            btnFiltros.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) {
                    disableEnableView(true, findViewById(R.id.consulta_content));
                    btnFiltros.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    filtros.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        btnFiltrar.setOnClickListener(view -> {
            hideKeyboard(editBusqueda);
            traeProductos(editBusqueda.getText().toString(), checkExistencias.isChecked());
            btnCerrarFiltros.callOnClick();
        });

        btnBrcode.setOnClickListener(view -> barcodeEscaner());

        editBusqueda.setOnKeyListener(((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                btnFiltrar.callOnClick();
                return true;
            }return false;
        }));

        productos = new ArrayList<>();
        actualizaView();

        if(venta)
            traeProductos(busqueda, false);
        Dialog d = new Dialog(this);
        if(SpeechRecognizer.isRecognitionAvailable(this)) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                d.setContentView(R.layout.dial_no_permiso_voice);
                Button btn_ok = d.findViewById(R.id.btn_ok_voice);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
                //checkPermission();
            }


            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

            final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) { }

                @Override
                public void onBeginningOfSpeech() {
                    vibrar(100);

                    editBusqueda.setText("");
                    editBusqueda.setHint("Escuchando...");
                }

                @Override
                public void onRmsChanged(float v) { }

                @Override
                public void onBufferReceived(byte[] bytes) { }

                @Override
                public void onEndOfSpeech() { }

                @Override
                public void onError(int i) {
                    hideKeyboard(filtros);
                    muestraMensaje("Error", R.drawable.mensaje_error);
                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    editBusqueda.setText(data.get(0));
                    btnFiltrar.performClick();
                }

                @Override
                public void onPartialResults(Bundle bundle) { }

                @Override
                public void onEvent(int i, Bundle bundle) { }
            });

            btnMicro.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    vibrar(100);
                    editBusqueda.setHint("");
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    speechRecognizer.startListening(speechRecognizerIntent);
                return true;
            });
        } else
            btnMicro.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(speechRecognizer != null)
            speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RecordAudioRequestCode && grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                muestraMensaje("Permiso otorgado", R.drawable.mensaje_exito);
    }

    /**
     * Cierra la consulta y abre el menu principal de la aplicacion
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ocultaFiltros) {
            btnCerrarFiltros.performClick();
        } else if (venta) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("folio", folio);
            returnIntent.putExtra("prodId", prodid);
            returnIntent.putExtra("caduCant", caducant);
            returnIntent.putExtra("faltaCadu", faltacadu);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else if (folio.equals("")) {
            /*Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);*/
            finish();
        } else {
            Intent intent = new Intent(this, PuntoVenta.class);
            SharedPreferences.Editor editor = getSharedPreferences("renglones", MODE_PRIVATE).edit();
            editor.putString("folio", folio);
            editor.apply();
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null && editBusqueda != null) {
            editBusqueda.setText(intentResult.getContents());
            btnFiltrar.performClick();
        }
        else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == 0){
            enConstruccion();
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    /**
     * llama el servicio que trae los productos
     * @param busqueda Texto a buscar
     */
    private void traeProductos(String busqueda, boolean existencias){
        peticionWS(Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA, "SQL", "SQL",
                "<linea><d1>" + busqueda + "</d1><d2></d2><d3>||" + (existencias? "true":"") + "</d3><d4>" + (promociones? "true":"") +"</d4><cliente></cliente></linea>",
                "", "");
    }

    /**
     * Llama el serivicio que inserta in renglo na la venta
     * @param cadena La cadena de la insercion
     * @param codigo El codigo  de barras
     */
    public void insertaRenglon(String cadena, String codigo){
        boolean existe = false;
        ArrayList<Renglon> renglones = new ArrayList<>();
        if(folio != null)
           renglones = servicio.getRenglones(this, folio);
        for(Renglon r:renglones){
            if(r.getCodigo().equals(codigo)){
                existe = true;
                peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                        Libreria.xmlInsertVenta(estacionID, usuarioID, cadena, clienteId, folio, r.getDvtaid(), tipoVenta, "a", ventaCredito),
                        "", "");
                break;
            }
        }

        if(!existe)
            peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL",
                    Libreria.xmlInsertVenta(estacionID, usuarioID, codigo + cadena, clienteId, folio, 0, tipoVenta, "a", ventaCredito),
                    "", "");
    }

    private void actualizaView(){
        if(productos.isEmpty()){
            findViewById(R.id.consulta_recycler_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.consulta_recycler).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.consulta_recycler_empty).setVisibility(View.GONE);
            findViewById(R.id.consulta_recycler).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_PRODUCTOS_BUSQUEDA:{
                    productos = servicio.getProductos(this);

                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                    int cols = dpWidth >= 600? 4:2;

                    actualizaView();

                    RecyclerView consultaRecylcer = findViewById(R.id.consulta_recycler);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, cols, GridLayoutManager.VERTICAL, false);
                    consultaRecylcer.setLayoutManager(layoutManager);

                    ConsultaAdapter consultaAdapter = new ConsultaAdapter(productos, this);
                    consultaRecylcer.setAdapter(consultaAdapter);
                    break;
                }
                case TAREA_INSERTA_RENGLON:{
                    cierraDialogo();
                    if(output.getExito()){
                        folio = obj.getAsString("vntafolio");

                        caducant = obj.getAsFloat("caducant");
                        prodid = obj.getAsString("prodid");
                        faltacadu = obj.getAsBoolean("faltacadu");
                        onBackPressed();
                    }
                    else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    break;
                }
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }

        cierraDialogo();
    }
}