package com.example.aristomovil2;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_INFO_PROD;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.aristomovil2.adapters.AsignadasAdapter;
import com.example.aristomovil2.modelos.Asignacion;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class BuscaProd extends ActividadBase {
    private ListView list;
    private boolean tecladoMonitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_prod);

        inicializarActividad("Busca el producto");
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        tecladoMonitor = preferences.getBoolean("tecladocodigo", true);

        Button producto = findViewById(R.id.btnProducto);
        ImageButton busca = findViewById(R.id.btnBuscaProd);
        EditText captura= findViewById(R.id.infoEdit);
        captura.setOnKeyListener((view, keyCode, event) -> {
            //System.out.println("evento: "+event.getAction());
            if ((event.getAction() == KeyEvent.ACTION_DOWN) ) {
                //System.out.println(keyCode);
                if((keyCode == KeyEvent.KEYCODE_ENTER )){
                    String codigo = ((EditText)view).getText().toString();
                    if(Libreria.tieneInformacion(codigo)) {
                        hideKeyboard(view);
                        wsBuscaProd(codigo);
                    }else
                        muestraMensaje("Ingresa el código", R.drawable.mensaje_warning);
                    return true;
                }
                return false;
            }else
                return false;
        });
        busca.setOnClickListener(view->barcodeEscaner());
        producto.setOnClickListener(view -> dialogoBuscaProductos(tecladoMonitor));
        cargaHTML("","");
    }

    private void cargaHTML(String pHtml,String pFoto){
        /*TextView web = findViewById(R.id.webPanel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            web.setText(Html.fromHtml(pHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            web.setText(Html.fromHtml(pHtml));
        }*/
        TextView myWebView = findViewById(R.id.webHtml);
        ImageView imagen = findViewById(R.id.webImagen);
        Bitmap foto=Libreria.recuperaFoto(pFoto);
        if(foto==null){
            imagen.setVisibility(View.GONE);
        }else{
            imagen.setVisibility(View.VISIBLE);
            imagen.setImageBitmap(foto);
            imagen.setOnClickListener(view -> {
                Dialog dialogo = new Dialog(this);
                dialogo.setContentView(R.layout.dialogo_imagen);
                ImageView ima = dialogo.findViewById(R.id.dlgImg);
                ima.setImageBitmap(foto);
                ima.setOnClickListener(view1 -> dialogo.dismiss());
                dialogo.show();
            });
        }
        String mime = "text/html";
        String encoding = "utf-8";
        myWebView.setText(pHtml);
        //myWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.loadDataWithBaseURL(null, pHtml, mime, encoding, null);
        //myWebView.setVisibility(View.GONE);
    }

    /**
     * Establece el contenido de la lista de ventas
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_INFO_PROD:{
                    EditText captura = findViewById(R.id.infoEdit);
                    if(output.getExito()){
                        String anexo=obj.getAsString("cadena");
                        String foto=obj.getAsString("foto");
                        cargaHTML(anexo,foto);
                        captura.setText("");
                    }else{
                        captura.setText("");
                    }
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito : R.drawable.mensaje_error);
                }break;
                case TAREA_PRODUCTOS_BUSQUEDA:{
                    llenarTablaProductos(view -> {
                        String tag=view.getTag().toString();
                        wsBuscaProd(tag);
                        dlgBuscaProds.dismiss();
                    });
                }break;
            }
        }else {

            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void wsBuscaProd(String pCodigo){
        peticionWS(TAREA_INFO_PROD,"SQL","SQL",pCodigo,"","");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult.getContents() != null ) {
           wsBuscaProd(intentResult.getContents());
        }
        else
            muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
    }
}
