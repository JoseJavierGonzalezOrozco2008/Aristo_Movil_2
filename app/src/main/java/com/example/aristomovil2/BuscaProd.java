package com.example.aristomovil2;

import static com.example.aristomovil2.facade.Estatutos.TABLA_GENERICA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_IMPUGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_INFO_PROD;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_MGNFAMIGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRECGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_TRAEMARGEN;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_TRAEPRODMOVIL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.aristomovil2.Acrividades.Cobropv;
import com.example.aristomovil2.Acrividades.Producto;
import com.example.aristomovil2.adapters.AsignadasAdapter;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.ListaImpuestosAdapter;
import com.example.aristomovil2.modelos.Asignacion;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BuscaProd extends ActividadBase {
    private ListView list;
    private boolean tecladoMonitor;
    private Integer v_estacion;
    private ListaImpuestosAdapter v_impAdapter;
    private AlertDialog v_dialogo;
    private Generica v_parametros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_prod);

        inicializarActividad2("Busca el producto","");
        actualizaToolbar2(usuario,"Busca el producto");
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        tecladoMonitor = preferences.getBoolean("tecladocodigo", true);
        v_estacion = preferences.getInt("estaid", 0);
        Bundle extras = getIntent().getExtras();
        v_parametros = new Generica(-1);
        /*v_parametros.setLog1(true);
        v_parametros.setLog2(true);
        v_parametros.setLog3(true);
        v_parametros.setLog4(true);
        v_parametros.setLog5(true);*/

        v_parametros.setLog1(Libreria.getBoolean(extras.getString("marca","true")));
        v_parametros.setLog2(Libreria.getBoolean(extras.getString("line","true")));
        v_parametros.setLog3(Libreria.getBoolean(extras.getString("clavesat","true")));
        v_parametros.setLog4(Libreria.getBoolean(extras.getString("divisa","true")));
        v_parametros.setLog5(Libreria.getBoolean(extras.getString("sustancia","true")));
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
                        muestraMensaje("Ingresa el codigo", R.drawable.mensaje_warning);
                    return true;
                }
                return false;
            }else
                return false;
        });
        captura.requestFocus();
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
                        cargaHTML("","");
                    }
                    captura.requestFocus();
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito : R.drawable.mensaje_error);
                }break;
                case TAREA_PRODUCTOS_BUSQUEDA:{
                    llenarTablaProductos(view -> {
                        String tag=view.getTag().toString();
                        wsBuscaProd(tag);
                        dlgBuscaProds.dismiss();
                    });
                }break;
                case TAREA_REPORTEEXIS:
                    dlgReporte(7);
                    break;
                case TAREA_TRAEPRODMOVIL:
                    Intent intent = new Intent(this, Producto.class);
                    Object dato;
                    for(String llaves:obj.keySet()){
                        dato=obj.get(llaves);
                        if(dato instanceof Integer)
                            intent.putExtra(llaves,obj.getAsInteger(llaves));
                        else if (dato instanceof String)
                            intent.putExtra(llaves,obj.getAsString(llaves));
                        else if (dato instanceof Double)
                            intent.putExtra(llaves,obj.getAsDouble(llaves));
                        else if (dato instanceof Boolean)
                            intent.putExtra(llaves,obj.getAsBoolean(llaves));
                        else
                            System.out.println("excluido "+llaves+" valor "+obj.get(llaves));
                    }
                    intent.putExtra("pmarca",v_parametros.getLog1());
                    intent.putExtra("plinea",v_parametros.getLog2());
                    intent.putExtra("pclavesat",v_parametros.getLog3());
                    intent.putExtra("pdivisa",v_parametros.getLog4());
                    intent.putExtra("psustancia",v_parametros.getLog5());
                    startActivity(intent);
                    //dlgProducto(obj);
                    break;
                case TAREA_PRODGUARDA:
                case TAREA_PRECGUARDA:
                case TAREA_MGNFAMIGUARDA:
                    if(output.getExito() && v_dialogo!=null){
                        v_dialogo.dismiss();
                    }
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error2);
                    break;
                case TAREA_TRAEMARGEN:
                    dlgMargen(obj);
            }
        }else {

            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void wsBuscaProd(String pCodigo){
        peticionWS(TAREA_INFO_PROD,"SQL","SQL",pCodigo,"","");
    }

    public void wsBuscaProd(Integer pProdid){
        peticionWS(TAREA_TRAEPRODMOVIL,"SQL","SQL",pProdid+"","","");
    }

    private void repoExistencias(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPORTEEXIS, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Reporte de Productos");
        if(esMono()){
            menu.add(Menu.NONE, 2, Menu.NONE, "Nuevo Producto");
            menu.add(Menu.NONE, 3, Menu.NONE, "Margenes para precio");
        }
        return true;
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 1:
                repoExistencias();
                break;
            case 2:

                Intent intent = new Intent(this, Producto.class);
                intent.putExtra("prodid","0");
                intent.putExtra("pmarca",v_parametros.getLog1());
                intent.putExtra("plinea",v_parametros.getLog2());
                intent.putExtra("pclavesat",v_parametros.getLog3());
                intent.putExtra("pdivisa",v_parametros.getLog4());
                intent.putExtra("psustancia",v_parametros.getLog5());
                startActivity(intent);
                break;
            case 3:
                //dlgMargen(null);
                traeMargenes();
                break;
            default:
                muestraMensaje("Menu en construccion",R.drawable.mensaje_warning);
        }
        return false;
    }



    public void dlgMargen(ContentValues pContenido){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_margenes, null);
        builder.setCancelable(true);
        builder.setView(vista);
        v_dialogo=builder.create();
        Spinner margenes = vista.findViewById(R.id.margMargenes);
        EditText margen = vista.findViewById(R.id.margMargen);
        ListView margenLista = vista.findViewById(R.id.margLista);


        Button cerrar = vista.findViewById(R.id.MargCierra);
        Button guarda = vista.findViewById(R.id.MargGuarda);

        List<Generica> listado = servicio.traeGenerica();

        GenericaAdapter genericaAdapter = new GenericaAdapter(listado,this,8);
        margenLista.setAdapter(genericaAdapter);

        margen.setText("");

        //ArrayList<String> listamargen = servicio.traeMargenes();
        //ArrayAdapter<String> spinmargen = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, listamargen);
        List<Generica> listamargen = servicio.traeDcatGenerica(-1);
        GenericaAdapter adapRecarga=new GenericaAdapter(listamargen,this,11);
        margenes.setAdapter(adapRecarga);
        margenes.setSelection(0);

        cerrar.setOnClickListener(view -> v_dialogo.dismiss());
        guarda.setOnClickListener(view -> {
            String captura = margen.getText().toString();
            if(!Libreria.tieneInformacion(captura)){
                dlgMensajeError("Se debe capturar un valor",R.drawable.mensaje_error2);
                return;
            }
            Generica selectedItem = (Generica) margenes.getSelectedItem();
            ContentValues mapa = new ContentValues();
            //mapa.put("fami",servicio.traeDcatIdporAbrevi(-1,margenes.getSelectedItem()+""));
            mapa.put("fami",selectedItem.getId());
            mapa.put("valor",captura);
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            margenGuarda(xml);
        });

        v_dialogo.show();
    }



    private void traeMargenes(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(TAREA_TRAEMARGEN,"SQL","SQL",usuarioID,v_estacion+"","");
    }

    private void margenGuarda(String pXml){
        peticionWS(TAREA_MGNFAMIGUARDA,"SQL","SQL",pXml,usuarioID,"");
    }

}
