package com.example.aristomovil2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.aristomovil2.adapters.RenRepoAdapter;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.ProductosUbicacion;
import com.example.aristomovil2.modelos.RenglonRepo;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.flexbox.FlexboxLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Acomodo extends ActividadBase {
    private Integer acomid,posicion,proceso,prodid,max_posicion,dacoid,repone,ubicaid,cantidadUsual;
    private TextView pen_Codigo,pen_Cant,pen_Prod,pen_Origen,pen_Destino,txt_regnlon,repoUbicacion,repoCodigo,repoCant
            ,repoMen1,repoMen2,repoMen3,repoMen4,repoUbiclave;
    private Boolean esDialogo,accion_encarro,tecladocodigo,scanubi;
    private MenuItem porSubir,posAlmacenar;
    private Button btnSube,btnQuitar,btnCargar,btnActual,btnTermina,btnBorra;
    private String folioDI,miOrigen,ubicDestino,folioDIcorto;
    private CheckBox repoLleno;
    private Dialog dialogoProductos;
    private TableLayout tablaProductos;
    private ArrayList<Producto> productos;
    private ImageButton scanUbi,scanProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acomodo);
        Bundle extras = getIntent().getExtras();
        acomid=extras.getInt("idacom",0);
        folioDI=extras.getString("folioDI","");
        folioDIcorto=extras.getString("folioCorto","");
        miOrigen=extras.getString("ubicacion","");
        proceso=extras.getInt("proceso",1);

        proceso= proceso==1 ?2 :1;
        repone = extras.getInt("repone",802);
        max_posicion=0;
        esDialogo = false;
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        tecladocodigo = preferences.getBoolean("tecladocodigo", true);
        cantidadUsual = preferences.getInt("cantidadusual", 10);
        inicializarActividad("");
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);
        title.setText(traeTitulo());
        posicion=1;
        ubicDestino = "";
        ImageButton btnAnterior = findViewById(R.id.btnAcomodoAnterior);
        ImageButton btnSiguiente = findViewById(R.id.btnAcomodoSoguiente);
        btnQuitar = findViewById(R.id.btnAcomodoQuitar);
        btnCargar = findViewById(R.id.btnAcomodoCargar);
        scanUbi = findViewById(R.id.btnUbiBarcode);
        scanProd = findViewById(R.id.btnProdBarcode);
        //btnProceso = findViewById(R.id.btnAccion);
        btnSube= findViewById(R.id.btnAcomodoSube);
        btnActual = findViewById(R.id.btnActualCodigo);
        btnTermina = findViewById(R.id.btnAcomodoTermina);
        btnBorra = findViewById(R.id.btnAcomodoBorra);
        txt_regnlon=findViewById(R.id.txtAcomodoTraslado);
        repoUbicacion = findViewById(R.id.repoUbicacion);
        repoCant = findViewById(R.id.repoCantidad);
        repoCodigo = findViewById(R.id.repoCodigo);
        repoMen1 = findViewById(R.id.repoMen1);
        repoMen2 = findViewById(R.id.repoMen2);
        repoMen3 = findViewById(R.id.repoMen3);
        repoMen4 = findViewById(R.id.repoMen4);
        repoLleno = findViewById(R.id.repoLleno);
        repoUbiclave = findViewById(R.id.repoUbiclave);
        LinearLayout linea = findViewById(R.id.lyMensajesSig);
        LinearLayout acciones = findViewById(R.id.lyUbicIndic);

        Button btnCodigo = findViewById(R.id.btnRecibeDocumentoCodigo);
        RadioGroup nuevo=findViewById(R.id.radioGripo);
        RadioButton encarro=findViewById(R.id.radioCarro);
        RadioButton almacen=findViewById(R.id.radioAlmacenado);
        doCambiaProceso();
        if(proceso==1){
            if(encarro != null){
                encarro.setChecked(true);
            }
        }else{
            almacen.setChecked(true);
        }
        if(repone == 862){
            almacen.setVisibility(View.GONE);
        }
        if(nuevo != null){
            nuevo.setOnCheckedChangeListener((RadioGroup radioGroup, int i)-> {
                        if(encarro.isChecked()){
                            doCambiaProceso();
                        }else if(almacen.isChecked()){
                            doCambiaProceso();
                        }
                    }
            );
        }

        limpiaTextos();
        btnAnterior.setOnClickListener(v -> repoRenglonAnt());
        btnSiguiente.setOnClickListener(v -> repoRenglonSig());
        btnQuitar.setOnClickListener(v -> wsMueveProds(false));
        if(btnSube != null){
            btnSube.setOnClickListener(v -> {
                if(repone==476){
                    wsCreaDaco();
                }else{
                    wsMueveProds(true);
                }
            });
        }
        btnCargar.setOnClickListener(v -> wsGuarda());
        if(btnTermina != null){
            btnTermina.setOnClickListener(v -> wsFinaliza());
        }
        if(btnBorra != null){
            btnBorra.setOnClickListener(v -> wsDacoBorra());
        }
        //btnProceso.setOnClickListener(v -> doCambiaProceso());
        if(repoUbicacion != null){
            repoUbicacion.setOnKeyListener(((view, i, keyEvent) -> colocaEnter(view,i,keyEvent)));
            repoUbicacion.setOnEditorActionListener((view, i, keyEvent) -> colocaEnter(view,i,keyEvent));
        }
        if(repoCodigo != null){
            repoCodigo.setOnKeyListener(((view, i, keyEvent) -> colocaEnter(view,i,keyEvent)));
            repoCodigo.setOnEditorActionListener((view, i, keyEvent) -> colocaEnter(view,i,keyEvent));
        }
        if(repoCant != null){
            repoCant.setOnKeyListener(((view, i, keyEvent) -> colocaEnter(view,i,keyEvent)));
            repoCant.setOnEditorActionListener((view, i, keyEvent) -> colocaEnter(view,i,keyEvent));
            repoCant.setOnFocusChangeListener((view, b) -> {
                if(proceso==2 ){
                    LinearLayout mensajes=findViewById(R.id.lyUbicIndic);///lyUbicIndic acomMensajes
                    LinearLayout captura=findViewById(R.id.lyCapCodi);
                    mensajes.setVisibility(b ? View.GONE:View.VISIBLE);
                    captura.setVisibility(b ? View.GONE:View.VISIBLE);
                    muestraTeclado(repoCant);
                }
            });
        }
        if(btnCodigo != null){
            btnCodigo.setOnClickListener( v -> dialogoBuscaProductos(tecladocodigo)/*dialogoBuscaCodigo()*/);
        }

        //proceso=1;//1 en proceso,2 en carro

        accion_encarro=false;
        prodid=0;
        pen_Codigo=findViewById(R.id.pen_codigo);
        pen_Prod=findViewById(R.id.pen_prod);
        pen_Cant=findViewById(R.id.pen_cant);
        pen_Origen=findViewById(R.id.pen_origen);
        pen_Destino=findViewById(R.id.pen_destino);
        dacoid=0;
        if(btnActual != null){
            btnActual.setOnClickListener((view)->actualizaprod());
        }
        if(repoMen1 != null){
            repoMen1.setVisibility(View.GONE);
        }
        if(pen_Destino != null){
            pen_Destino.setVisibility(View.GONE);
        }
        //muestraPaneles(repone != 476);
        if(btnQuitar != null){
            btnQuitar.setVisibility( repone == 476 ? View.GONE:View.VISIBLE);
        }
        if(scanUbi != null){
            scanUbi.setOnClickListener(view -> {barcodeEscaner();scanubi=true;});

        }
        if(scanProd != null){
            scanProd.setOnClickListener(view -> {barcodeEscaner();scanubi=false;});
        }

    }

    private void muestraPaneles(boolean pMuestra){
        LinearLayout linea = findViewById(R.id.lyMensajesSig);
        LinearLayout acciones = findViewById(R.id.lyUbicIndic);
        if(pMuestra){
            linea.setVisibility(View.VISIBLE);
            acciones.setVisibility(View.VISIBLE);
        }else{
            if(linea != null){
                linea.setVisibility(View.GONE);
            }
            if(acciones != null){
                acciones.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();        
        if(output!=null){
            ContentValues obj = (ContentValues) output.getExtra1();
            if (output.getTarea()==Enumeradores.Valores.TAREA_REPO_FALTA_SUBIR){
                if(output.getExito()){
                    muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                    if(esDialogo){
                        muestraListaProductos();
                    }else{
                        repoRenglon(posicion);
                    }
                    esDialogo=false;
                }else{
                    repoRenglon(0);
                    esDialogo=false;
                }
            }else if(output.getTarea()==Enumeradores.Valores.TAREA_REPO_BAJADOS){
                muestraListaProductos();
            }else if(output.getTarea()==Enumeradores.Valores.TAREA_REPO_BUSCA_PRROD_UBIC ){
                ocultaTeclado(repoCodigo);
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                if(output.getExito()){
                    prodid=obj.getAsInteger("prodid");
                    String producto=obj.getAsString("producto");
                    String codigo=obj.getAsString("codigo");
                    Double piezas=obj.getAsDouble("pzasempq");
                    Double exis=obj.getAsDouble("existencia");
                    Double dispo=obj.getAsDouble("disponible");
                    Double cantubic=obj.getAsDouble("ucant");
                    String origen=obj.getAsString("origen");
                    String corigen=obj.getAsString("corigen");
                    Integer mipos=obj.getAsInteger("rid");
                    String men4 = (Libreria.tieneInformacion(origen) ? ("Origen:"+origen): "")+
                            (cantubic!=null ? (" cant en ubic:"+cantubic): "");
                    dacoid=obj.getAsInteger("dacoid");
                    dacoid = dacoid==null ?0 : dacoid;
                    repoMen1.setText(codigo);
                    repoMen2.setText(producto);
                    repoMen3.setText(codigo+", Dispo:"+(dispo!=null ? dispo:0)+" Pzs/Empq:"+piezas);
                    repoMen4.setText(men4);
                    posicion=mipos!=null && mipos>0 ? mipos:posicion;
                    repoRenglon(posicion);
                    repoCant.requestFocus();
                }else{
                    limpiaTextosEUb();
                    repoCodigo.requestFocus();
                }
            }else if(output.getTarea()==Enumeradores.Valores.TAREA_BUSCA_UBIC ){
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                if(output.getExito()){
                    repoCodigo.setFocusableInTouchMode(true);
                    repoCodigo.requestFocus();
                    repoUbiclave.setText(obj.getAsString("udestino"));
                    repoUbiclave.setVisibility(View.VISIBLE);
                    scanUbi.setVisibility(View.GONE);
                    ubicaid = obj.getAsInteger("iddestino");
                }else{
                    repoUbiclave.setText("");
                    repoUbiclave.setVisibility(View.GONE);
                    scanUbi.setVisibility(View.VISIBLE);
                }
            }else if(output.getTarea()==Enumeradores.Valores.TAREA_REPO_GUARDA ){
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                limpiaTextosEUb();
                if(output.getExito()){
                    wsSolicitaFaltantes();
                    //limpiar variables
                    limpiaTextosEUb();
                    if(proceso==2){
                        repoUbicacion.setText("");
                    }
                    controlFoco();
                }else{
                    controlFoco();
                }
            }else if( output.getTarea()==Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA){
                /*if(productos != null)
                    productos.clear();

                productos = servicio.getProductos(this);
                llenarTablaProductos();*/

                llenarTablaProductos(view->{
                    repoCodigo.setText(view.getTag()+"");
                    doBuscaProducto();
                    dlgBuscaProds.dismiss();});
                cierraDialogo();
            }else if( output.getTarea()==Enumeradores.Valores.TAREA_REPO_SUBE_RENGLON || output.getTarea() == Enumeradores.Valores.TAREA_REPO_DACOCREA){
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                if(output.getExito()){
                    wsSolicitaFaltantes();
                    limpiaTextos();
                    repoUbicacion.requestFocus();
                }
                cierraDialogo();
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_REPO_DACO_BORRA){
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                if(output.getExito()){
                    wsSolicitaFaltantes();
                    limpiaTextos();
                    repoUbicacion.requestFocus();
                }
            }else if(output.getTarea() == Enumeradores.Valores.TAREA_REPO_ACOM_CIERRA){
                muestraMensaje(output.getMensaje(),output.getExito() ?  R.color.acomodo_Info: R.drawable.mensaje_error,0);
                if(output.getExito()){
                    finish();
                }
            }
        }
    }

    public void repoRenglonSig(){
        posicion++;
        if(posicion>max_posicion){
            posicion=max_posicion;
        }
        repoRenglon(posicion);
    }
    public void repoRenglonAnt(){
        posicion--;
        if(posicion<=0){
            posicion=1;
        }
        repoRenglon(posicion);
    }

    public void actualizaprod(){
        List<RenglonRepo> lista=servicio.traeRenglones(posicion);
        if(!lista.isEmpty()){
        RenglonRepo elemento=lista.get(0);
            repoMen1.setText(elemento.getCodigo());
            repoMen2.setText(elemento.getProducto());
            repoMen3.setText(elemento.getCodigo()+", Faltan:"+elemento.getFaltan());
            repoMen4.setText("Origen:"+elemento.getOrigen());
            //dacoid=obj.getAsInteger("dacoid");
            //dacoid = dacoid==null ?0 : dacoid;
            dacoid = elemento.getDacoid();
            prodid=elemento.getProdid();
            repoCodigo.setText(elemento.getCodigo());
            repoCant.setText(elemento.getFaltan()+"");
            repoCant.requestFocus();
        }
    }

    public void repoRenglon(Integer pPosicion){
        List<RenglonRepo> lista=servicio.traeRenglones(pPosicion);
        if(!lista.isEmpty()){
            pen_Codigo.setText(lista.get(0).getCodigo());
            pen_Prod.setText(lista.get(0).getProducto());
            pen_Cant.setText("Faltan:"+lista.get(0).getFaltan());
            pen_Origen.setText("Origen:"+lista.get(0).getOrigen()+"("+miOrigen+")"+ "  Destino:"+lista.get(0).getDestino());
            pen_Destino.setText("Destino:"+lista.get(0).getDestino());
            lista=servicio.traeRenglones(0);
            max_posicion=lista.size();
            txt_regnlon.setText(posicion+" de "+max_posicion);
        }else{
            if(pen_Codigo != null){
                pen_Codigo.setText("");
            }
            if(pen_Prod != null){
                pen_Prod.setText("");
            }
            if(pen_Cant != null){
                pen_Cant.setText("");
            }
            if(pen_Origen != null){
                pen_Origen.setText("Ya no hay renglones por acomodar");
            }
            if(pen_Destino != null){
                pen_Destino.setText("");
            }
            if(txt_regnlon != null){
                txt_regnlon.setText("");
            }

            max_posicion=0;
        }
    }

    private void dialogoBuscaCodigo(){
        dialogoProductos = new Dialog(this);
        Objects.requireNonNull(dialogoProductos.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogoProductos.setContentView(R.layout.dialogo_busca_codigo);
        EditText editProducto = dialogoProductos.findViewById(R.id.editProducto);
        tablaProductos = dialogoProductos.findViewById(R.id.tablaProductos);
        tablaProductos.removeAllViews();

        if(tecladocodigo)
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
                    muestraMensaje("Campo vacío", R.drawable.mensaje_warning,R.raw.yaexiste);
            }return false;
        });
        dialogoProductos.show();
    }

    /**
     * LLena la tabla con los productos obtenidos al buscar un codigo
     */
    @SuppressLint("SetTextI18n")
    private void llenarTablaProductos(){
        tablaProductos.removeAllViews();

        final TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.DKGRAY);
        header.setGravity(Gravity.CENTER);

        TextView h1 = new TextView(this);
        h1.setText("");
        h1.setTextColor(Color.WHITE);
        h1.setPadding(10, 10, 10, 10);
        header.addView(h1);

        TextView h2 = new TextView(this);
        h2.setText("Producto");
        h2.setTextColor(Color.WHITE);
        h2.setPadding(10, 10, 10, 10);
        header.addView(h2);
        tablaProductos.addView(header);

        for(Producto producto: productos){
            final TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);
            btnSeleccionar.setOnClickListener(view -> {
                repoCodigo.setText(producto.getCodigo());
                doBuscaProducto();
                dialogoProductos.dismiss();
            });
            row.addView(btnSeleccionar);
            TextView r2 = new TextView(this);

            r2.setText(producto.getProducto());
            r2.setTextSize(18);
            r2.setTextColor(Color.BLACK);
            r2.setPadding(10, 10, 10, 10);
            row.addView(r2);

            tablaProductos.addView(row);
        }
    }

    /**
     * Muestra un dialogo con la lsta de productos en la ubicacionactual
     */
    public void muestraListaProductos(){
        Dialog dialogo = new Dialog(this, R.style.DialogWhiteTxt);


        dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
        dialogo.setContentView(R.layout.dialogo_reporenglones);
        dialogo.setTitle("Lista");

        ListView listProductosUbicaciones = dialogo.findViewById(R.id.listRepoRenglones);
        ArrayList<RenglonRepo> data=(ArrayList<RenglonRepo>)servicio.traeRenglones(0);

        RenRepoAdapter adapter = new RenRepoAdapter( data,this);
        listProductosUbicaciones.setAdapter(adapter);
        /*listProductosUbicaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object info = view.getTag();
                posicion = info != null ? (Integer) info : 1;
                Acomodo.this.repoRenglon(posicion);
                dialogo.dismiss();
            }
        });*/
        dialogo.setOnDismissListener(dialogInterface -> {wsSolicitaFaltantes();esDialogo=false;});
        dialogo.show();
    }

    public void wsSolicitaFaltantes(){
        servicio.borraDatosTabla(Enumeradores.Valores.TAREA_REPO_FALTA_SUBIR.getTablaBD());
        peticionWS(Enumeradores.Valores.TAREA_REPO_FALTA_SUBIR, "SQL", "SQL", acomid+"", "0", (proceso==1)+"");
    }

    public void doBuscaUbicacion(){
        String ubicacion=repoUbicacion.getText().toString();
        if(Libreria.tieneInformacion(ubicacion)){
            Boolean lleno=repoLleno.isChecked();
            ContentValues mapa=new ContentValues();
            mapa.put("acomid", acomid);
            mapa.put("esorigen", proceso==1);
            mapa.put("cantidad", 0);
            mapa.put("ubica", ubicacion);
            mapa.put("ubicdestino", "");
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_BUSCA_UBIC, "SQL", "SQL", xml, "0", "");
        }else{
            muestraMensaje("La ubicacion esta vacia",R.drawable.mensaje_error,0);
            repoUbicacion.requestFocus();
        }
    }

    public void wsCreaDaco(){
        String codigo=repoCodigo.getText().toString();
        String cant=repoCant.getText().toString();
        String ubica=repoUbicacion.getText().toString();
        if(Libreria.tieneInformacion(codigo) && Libreria.tieneInformacion(cant)){
            if(Libreria.menorLimite(cant,cantidadUsual)){
                wsCreaDaco1(codigo,cant);
            }else{
                AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                confirmacion.setTitle("Cantidad insuficiente");
                confirmacion.setMessage("La cantidad "+cant+" supera al permitido \n"+"¿ Esta seguro de continuar?");
                confirmacion.setCancelable(false);
                confirmacion.setPositiveButton("SI", (dialog, which) -> wsCreaDaco1(codigo,cant));
                confirmacion.setNegativeButton("NO", null);
                confirmacion.show();
            }
        }else{
            muestraMensaje("El codigo o cantidad esta vacia",R.drawable.mensaje_error,0);
            repoUbicacion.requestFocus();
        }
    }

    public void wsCreaDaco1(String pCodigo,String pCant){
        ContentValues mapa=new ContentValues();
        mapa.put("acom", acomid); //falta
        mapa.put("usua", usuarioID);
        mapa.put("codigo", pCodigo);
        mapa.put("cant", pCant);
        mapa.put("ubic", ubicaid);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_REPO_DACOCREA, "SQL", "SQL", xml, "", "");
    }

    public void wsMueveProds(Boolean pSube){
        String codigo=repoCodigo.getText().toString();
        String cant=repoCant.getText().toString();
        if(dacoid>0){
            if(Libreria.tieneInformacion(codigo) && Libreria.tieneInformacion(cant)){
                if(Libreria.menorLimite(cant,cantidadUsual)){
                    wsMueveProds(pSube,cant);
                }else{
                    AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                    confirmacion.setTitle("Cantidad insuficiente");
                    confirmacion.setMessage("La cantidad "+cant+" supera al permitido \n"+"¿ Está seguro de continuar?");
                    confirmacion.setCancelable(false);
                    confirmacion.setPositiveButton("SI", (dialog, which) -> wsMueveProds(pSube,cant));
                    confirmacion.setNegativeButton("NO", null);
                    confirmacion.show();
                }
            }else{
                muestraMensaje("El código o cantidad esta vacía",R.drawable.mensaje_error,0);
                repoUbicacion.requestFocus();
            }
        }else{
            muestraMensaje("Falta buscar el producto",R.drawable.mensaje_error,0);
            repoUbicacion.requestFocus();
        }
    }

    public void wsMueveProds(Boolean pSube,String pCant){
        ContentValues mapa=new ContentValues();
        mapa.put("daco", dacoid); //falta
        mapa.put("usua", usuarioID);
        mapa.put("cant", pCant);
        mapa.put("sube", pSube);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_REPO_SUBE_RENGLON, "SQL", "SQL", xml, "", "");
    }

    public void doBuscaProducto(){
        String codigo=repoCodigo.getText().toString();
        if(Libreria.tieneInformacion(codigo)){
            ContentValues mapa=new ContentValues();
            mapa.put("codigo",codigo);
            mapa.put("acom",acomid);
            mapa.put("porsubir",(proceso==1));
            mapa.put("ubik",ubicaid);
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_REPO_BUSCA_PRROD_UBIC, "SQL", "SQL", xml, "", "");
        }else{
            muestraMensaje("El código de producto esta vacío",R.drawable.mensaje_error,0);
            repoCodigo.requestFocus();
        }
    }

    public void wsGuarda(){
        String cant=repoCant.getText().toString();
        if(Libreria.tieneInformacion(cant)){
            if(Libreria.menorLimite(cant,cantidadUsual)){
                wsGuarda1(cant);
            }else{
                AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                confirmacion.setTitle("Cantidad insuficiente");
                confirmacion.setMessage("La cantidad "+cant+" supera al permitido \n"+"¿ Está seguro de continuar?");
                confirmacion.setCancelable(false);
                confirmacion.setPositiveButton("SI", (dialog, which) -> wsGuarda1(cant));
                confirmacion.setNegativeButton("NO", null);
                confirmacion.show();
            }
        }else{
            muestraMensaje("Falta cantidad",R.drawable.mensaje_error,0);
            repoCodigo.requestFocus();
        }
    }

    public void wsGuarda1(String pCant){
        String ubicacion=repoUbicacion.getText().toString();
        Boolean lleno=repoLleno.isChecked();
        ContentValues mapa=new ContentValues();
        mapa.put("acomid", acomid);
        mapa.put("usuaid", usuarioID);
        mapa.put("prodid", prodid);//falta prodid
        mapa.put("cantidad", pCant);
        mapa.put("ubicacion", ubicacion);
        mapa.put("lleno", lleno);
        mapa.put("dcin", folioDI);
        mapa.put("daco", dacoid);
        mapa.put("notas", "");
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_REPO_GUARDA, "SQL", "SQL", xml, "", "");
    }

    public void wsFinaliza(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("Acomodo libre");
        confirmacion.setMessage("¿Estás seguro de finalizar el acomodo ?");
        confirmacion.setCancelable(false);
        confirmacion.setPositiveButton("SI", (dialog, which) -> peticionWS(Enumeradores.Valores.TAREA_REPO_ACOM_CIERRA, "SQL", "SQL", acomid+"", usuarioID, ""));
        confirmacion.setNegativeButton("NO", null);
        confirmacion.show();

    }

    public void wsDacoBorra(){
        if(dacoid!=null && dacoid>0){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("Eliminar renglón");
        confirmacion.setMessage("¿Estás seguro de eliminar el producto "+repoMen2.getText().toString()+" ?");
        confirmacion.setCancelable(false);
        confirmacion.setPositiveButton("SI", (dialog, which) -> peticionWS(Enumeradores.Valores.TAREA_REPO_DACO_BORRA, "SQL", "SQL", dacoid+"", usuarioID, ""));
        confirmacion.setNegativeButton("NO", null);
        confirmacion.show();
        }else{
            muestraMensaje("Producto no está registrado en la orden de acomodo",R.drawable.mensaje_error,0);
        }
    }

    public void doCambiaProceso(){
        FlexboxLayout lineaUbica = findViewById(R.id.lineaUbicacion);
        LinearLayout lineaLlena = findViewById(R.id.lineaLleno);
        if(proceso==2){
            proceso=1;
            //btnProceso.setText("Al carro");
            btnQuitar.setClickable(true);
            if(btnSube != null){
                btnSube.setVisibility(View.VISIBLE);
            }
            btnCargar.setVisibility(View.GONE);
            if(lineaLlena != null){
                lineaLlena.setVisibility(View.GONE);
            }

            if(btnActual != null){
                btnActual.setVisibility(View.GONE);
            }
            if(btnTermina != null){
                btnTermina.setVisibility(View.GONE);
            }

            if(repone == 476){
                if(lineaUbica != null){
                    lineaUbica.setVisibility(View.VISIBLE);
                }
                if(btnBorra != null){
                    btnBorra.setVisibility(View.VISIBLE);
                }

            }else if(repone == 862){
                lineaUbica.setVisibility(View.GONE);
                btnBorra.setVisibility(View.GONE);
            }else{
                lineaUbica.setVisibility(View.GONE);
            }
            muestraPaneles(repone != 476);
        }else{
            proceso=2;
            //btnProceso.setText("Almacenando");
            btnQuitar.setClickable(false);
            btnSube.setVisibility(View.GONE);
            btnCargar.setVisibility(View.VISIBLE);
            lineaUbica.setVisibility(View.VISIBLE);
            btnActual.setVisibility(View.VISIBLE);
            btnBorra.setVisibility(View.GONE);
            btnTermina.setVisibility(View.GONE);
            if(repone == 802){
                lineaLlena.setVisibility(View.GONE);
            }else{
                lineaLlena.setVisibility(View.VISIBLE);
                if(repone == 476){
                    btnActual.setVisibility(View.GONE);
                    lineaLlena.setVisibility(View.GONE);
                    btnTermina.setVisibility(View.VISIBLE);
                }
            }
            muestraPaneles(true);
        }
        if(porSubir!=null)
            porSubir.setVisible(proceso==1);
        if(posAlmacenar!=null)
            posAlmacenar.setVisible(proceso==2);
        wsSolicitaFaltantes();
    }

    private void limpiaTextos(){
        limpiaTextosEUb();
        if(repoUbicacion != null){
            repoUbicacion.setText("");
        }
        if(repoUbiclave != null){
            repoUbiclave.setText("");
            repoUbiclave.setVisibility(View.GONE);
        }
        if(scanUbi != null){
            scanUbi.setVisibility(View.VISIBLE);
        }

    }

    private void limpiaTextosEUb(){
        if(repoMen1 != null){
            repoMen1.setText("");
        }
        if(repoMen2 != null){
            repoMen2.setText("");
        }
        if(repoMen3 != null){
            repoMen3.setText("");
        }
        if(repoMen4 != null){
            repoMen4.setText("");
        }
        if(repoCodigo != null){
            repoCodigo.setText("");
        }
        if(repoCant != null){
            repoCant.setText("");
        }
        if(repoLleno != null){
            repoLleno.setChecked(false);
        }
        prodid=0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_acomodo, menu);
        porSubir=menu.findItem(R.id.menuAcomodoProSubir);
        posAlmacenar=menu.findItem(R.id.menuAcomodoSubidos);
        porSubir.setVisible(proceso==1);
        posAlmacenar.setVisible(proceso==2);
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
        switch (item.getItemId()){
            case R.id.menuAcomodoProSubir:
                esDialogo=true;
                peticionWS(Enumeradores.Valores.TAREA_REPO_FALTA_SUBIR, "SQL", "SQL", acomid+"", "0", "true");
                break;
            case R.id.menuAcomodoSubidos:
                esDialogo=true;
                peticionWS(Enumeradores.Valores.TAREA_REPO_BAJADOS, "SQL", "SQL", acomid+"", "0", "false");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean colocaEnter(View view,int keyCode,KeyEvent event){
        //System.out.println(event +" -- "+ keyCode);
        if (((event!=null && event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) || keyCode == EditorInfo.IME_ACTION_DONE) {
            if(view.getId()==R.id.repoUbicacion){
                doBuscaUbicacion();
                ocultaTeclado(repoUbicacion);
            }else if(view.getId()==R.id.repoCodigo){
                doBuscaProducto();
            }else if(view.getId()==R.id.repoCantidad){
                if(proceso==2) {
                    wsGuarda();
                }else{
                    if(repone == 476){
                        wsCreaDaco();
                    }else{
                        wsMueveProds(true);
                    }
                }
                ocultaTeclado(repoCant);
            }else{
                muestraMensaje("La acción no configurada correctamente", R.drawable.mensaje_warning,R.raw.yaexiste);
            }
            return true;
        }else
            return false;
    }

    private void ocultaTeclado(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void muestraTeclado(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    private void controlFoco(){
        LinearLayout captura=findViewById(R.id.lyCapCodi);
        if(captura.getVisibility()==View.VISIBLE){
            repoCodigo.setFocusableInTouchMode(true);
            repoCodigo.requestFocus();
        }else{
            repoUbicacion.setFocusableInTouchMode(true);
            repoUbicacion.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult.getContents() != null ) {
            if(scanubi){
                repoUbicacion.setText(intentResult.getContents());
                doBuscaUbicacion();
            }else{
                repoCodigo.setText(intentResult.getContents());
                doBuscaProducto();
            }
        }
        else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error,0);
    }

    private void muestraMensaje(String pMsj, int pTipo,int pAudio){
        muestraMensaje(pMsj,pTipo);
        if(pAudio==0){
            if(pTipo==R.drawable.mensaje_error){
                reproduceAudio(R.raw.error);
            }else{
                reproduceAudio(R.raw.exito);
            }
        }else{
            reproduceAudio(pAudio);
        }
    }

    private String traeTitulo(){
        String doc;
        String titulo="Id Acom:{0}, {1}{2}";
        if(repone != 476 && Libreria.tieneInformacion(folioDI)){
            int pos=folioDI.length();
            String tipo = "";
            switch(repone){
                case 803:tipo="Compra";break;
                case 802:tipo="Repone";break;
                case 484:tipo="Devolucion";break;
            }
            String corto = Libreria.tieneInformacion(folioDIcorto) ? folioDIcorto :(":I*"+(pos>3 ? folioDI.substring(pos-3,pos):folioDI));
            doc=","+tipo+corto;
        }else{
            doc = " Libre";
        }
        return MessageFormat.format(titulo,acomid,usuario,doc);
    }
}