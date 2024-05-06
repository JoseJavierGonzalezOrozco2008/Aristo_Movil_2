package com.example.aristomovil2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aristomovil2.adapters.UbikprodAdapter;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.ProductosUbicacion;
import com.example.aristomovil2.modelos.Ubikprod;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Ubicaciones extends ActividadBase {
    EditText editCodigo, editProducto, editMax, editMin, editCantidad, editDialogoCodigo;
    LinearLayout linearMensaje, linearUbicaciones, linearCantidades,linearMaxMin,linearCaptura;
    TextView txtMensaje, txtUbicacion, txtSucursal, txtBusqueda, txtDialogoUbicacion, txtDialogoProducto;
    CheckBox checkLleno;
    RadioGroup radioGroup;
    RadioButton radioAgrega, radioQuita,radioCuenta;
    ImageButton btnBarcodeUbicacion, btnBarcodeProducto;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swAutomatico;
    Button btnGuarda;
    ListView listProductosUbicaciones,listaUbikprod;
    int espacioID, barcodeRequest;
    Integer agregaQuita = 1; //1: Agrega, 2: Quita,3:Cuenta
    boolean auto = false;
    boolean tecladocodigo;
    private Dialog dialogoProductos;
    private TableLayout tablaProductos;
    private ArrayList<Producto> listaProductos;

    List<ProductosUbicacion> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Ubicaciones"));
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        tecladocodigo = preferences.getBoolean("tecladocodigo", true);
        editCodigo = findViewById(R.id.editUbicacionesCodigo);
        editProducto = findViewById(R.id.editUbicacionProducto);
        editMax = findViewById(R.id.editUbicacionesCantMax);
        editMin = findViewById(R.id.editUbicacionesCantMin);
        editCantidad = findViewById(R.id.editUbicacionesCantidad);
        txtMensaje = findViewById(R.id.txtUbicacionesMensajes);
        txtUbicacion = findViewById(R.id.txtUbicacionesUbicacion);
        txtSucursal  = findViewById(R.id.txtUbicacionesSucursal);
        txtBusqueda = findViewById(R.id.txtUbicacionesBusqueda);
        linearMensaje = findViewById(R.id.linearUbicacionesMensaje);
        linearUbicaciones = findViewById(R.id.linearUbicacionesLayout);
        linearCantidades = findViewById(R.id.linearUbicacionesCantidades);
        linearMaxMin = findViewById(R.id.linearUbicacionesMaxMin);
        linearCaptura = findViewById(R.id.linearUbicacionesCaptura);
        checkLleno = findViewById(R.id.checkUbicacionesLleno);
        radioGroup = findViewById(R.id.groupUbicacionesRadio);
        radioAgrega = findViewById(R.id.radioUbicacionesAgrega);
        radioCuenta = findViewById(R.id.radioUbicacionesCuenta);
        radioQuita = findViewById(R.id.radioUbicacionesQuita);
        swAutomatico = findViewById(R.id.switchUbicacionesAutomatico);
        btnGuarda = findViewById(R.id.btnUbicacionesGuarda);
        Button btnCodigo = findViewById(R.id.btnCodigo);
        btnBarcodeUbicacion = findViewById(R.id.btnUbicacionesBarcodeUbicacion);
        btnBarcodeProducto = findViewById(R.id.btnUbicacionesBarcodeProducto);

        linearUbicaciones.setVisibility(View.GONE);
        //Listener del EditText del codigo, busca una ubicacion al presionar enter
        editCodigo.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(this);
                if (!editCodigo.getText().toString().isEmpty()){
                    String ubicacion = editCodigo.getText().toString();
                    txtBusqueda.setText(ubicacion);
                    editCodigo.setText("");
                    buscaUbicacion(ubicacion);
                } else
                    muestraMensaje("Campo vacío", R.drawable.mensaje_error);
                return true;
            }
            return  false;
        });
        editMin.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(v);
                if (!editMin.getText().toString().isEmpty()){
                    guardaRegistro();
                } else
                    muestraMensaje("Campo mínimo vacío", R.drawable.mensaje_error);
                return true;
            }
            return  false;
        });

        editCantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                btnGuarda.performClick();
                return true;
            }
            return false;
        });

        btnCodigo.setOnClickListener((view)->{dialogoBuscaCodigo();});

        //Listener del RadioGroup, determinda si el producto se agrega o se quia de la ubicacion
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            btnGuarda.setText("Guarda");
            if(radioAgrega.isChecked()){
                agregaQuita = 1;
                swAutomatico.setVisibility(View.VISIBLE);
                linearCaptura.setVisibility(View.GONE);
            }else{
                swAutomatico.setChecked(false);
                swAutomatico.setVisibility(View.GONE);
                linearCaptura.setVisibility(View.VISIBLE);
            }
            if(radioQuita.isChecked()){
                agregaQuita = 2;
                linearCantidades.setVisibility(View.GONE);
                btnGuarda.setText("Quita");
                //btnGuarda.setVisibility(View.GONE);
            }else{
                linearCantidades.setVisibility(View.VISIBLE);
                //btnGuarda.setVisibility(View.VISIBLE);
            }
            if(radioCuenta.isChecked()){
                agregaQuita = 3;
                linearMaxMin.setVisibility(View.GONE);
            }else{
                linearMaxMin.setVisibility(View.VISIBLE);
            }
        });

        //Listener para el Switch automatico, detemrmina si la accion es manual o automatica
        swAutomatico.setOnCheckedChangeListener((compoundButton,b) -> {
            b = compoundButton.isChecked();
            auto = b;
            linearCantidades.setVisibility(b?View.GONE:View.VISIBLE);
            btnGuarda.setVisibility(b?View.GONE:View.VISIBLE);

            if (b)
                productoAutomatico();
            else
                productoManual();
        });

        //Listener para le boton de guardar, llama a la funcion de gardarProducto
        btnGuarda.setOnClickListener(view -> {
           guardaRegistro();
        });

        btnBarcodeUbicacion.setOnClickListener(view -> {
            barcodeRequest = 1;
            barcodeEscaner();
        });

        btnBarcodeProducto.setOnClickListener(view -> {
            barcodeRequest = 2;
            barcodeEscaner();
        });

        radioAgrega.setChecked(true);
        productoManual();

        if(tecladocodigo)
            editProducto.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editProducto.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void guardaRegistro(){
        if(!editProducto.getText().toString().isEmpty())
            guardaProducto(editProducto.getText().toString(), editMax.getText().toString(),
                    editMin.getText().toString(), editCantidad.getText().toString(),
                    checkLleno.isChecked());
        else
            muestraMensaje("Campo de Producto vacío", R.drawable.mensaje_error);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        hideKeyboard(this);

        if (intentResult.getContents() != null) {
            if(barcodeRequest == 1) {
                editCodigo.setText(intentResult.getContents());
                String ubicacion = editCodigo.getText().toString();
                txtBusqueda.setText(ubicacion);
                editCodigo.setText("");
                buscaUbicacion(ubicacion);
            }
            else if(barcodeRequest == 2) {
                editProducto.setText(intentResult.getContents());

                String producto = editProducto.getText().toString();
                if(auto)
                    guardaProducto(producto, "", "", "", false);
                else
                    buscaProducto(producto);
            }
            else if(barcodeRequest == 3){
                editDialogoCodigo.setText(intentResult.getContents());
                ubicaProducto(editDialogoCodigo.getText().toString());
            }
        } else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra("user", usuario);
        startActivity(intent);*/
        super.onBackPressed();
        finish();
    }

    /**
     * Crea las opciones del menu del Toolbar
     * @param menu Menu
     * @return .
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ubicaciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener para la seleccion de un item en el menu del Toolbar
     * @param item Item seleccionado
     * @return Retorna true para indicar exito
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideKeyboard(this);
        switch (item.getItemId()){
            case R.id.menu_ubicaciones_lista:
                    if(espacioID != 0)
                        listaProductos(espacioID);
                    else
                        muestraMensaje("No hay ubicación seleccionada", R.drawable.mensaje_warning);
                break;
            case R.id.menu_ubicaciones_ubica:
                    dialogoBuscarProducto();
                break;
        }
        return true;
    }

    /**
     * Establece el listener del EditText de producto para que inserte el producto automaticamente
     */
    public void productoAutomatico(){
        editProducto.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!editProducto.getText().toString().isEmpty()){
                    String producto = editProducto.getText().toString();
                    guardaProducto(producto, "", "", "", false);
                    hideKeyboard(this);
                } else
                    muestraMensaje("Campo de Producto Vacío", R.drawable.mensaje_error);
                return true;
            }
            return false;
        });
    }

    /**
     * Establece el listener del EditText de producto para que busque el producto indicado
     */
    public void productoManual(){
        editProducto.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!editProducto.getText().toString().isEmpty()){
                    String producto = editProducto.getText().toString();
                    buscaProducto(producto);
                } else
                    muestraMensaje("Campo de Producto Vacío", R.drawable.mensaje_error);
                return true;
            }
            return false;
        });
    }

    /**
     * Busca una ubicacion
     * @param ubicacion La ubicacion a buscar
     */
    public void buscaUbicacion(String ubicacion){
        peticionWS(TAREA_BUSCA_UBICACION, "SQL", "SQL", ubicacion, "", "");
    }

    /**
     * Busca un producto
     * @param producto El producto a buscar
     */
    public void buscaProducto(String producto){
        peticionWS(TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", producto, "false", "");
    }

    /**
     * Busca la ubicacion de un producto
     * @param producto El producto a buscar
     */
    public void ubicaProducto(String producto) {
        servicio.borraDatosTabla(TAREA_UBICACION_DE_PROD.getTablaBD());
        peticionWS(TAREA_UBICACION_DE_PROD, "SQL", "SQL", producto, "", "");
    }

    /**
     * Guarda un producto en una ubicacion
     * @param producto El producto a ubicar
     * @param cantmax La cantidad maxima del producto
     * @param cantmin La cantidad minima del producto
     * @param cantidad La cantidad actual del producto
     * @param lleno Indica si el espacio del producto se encuentra lleno
     */
    public void guardaProducto(String producto, String cantmax, String cantmin, String cantidad, boolean lleno){
        ContentValues mapa=new ContentValues();
        mapa.put("espaid",espacioID);
        mapa.put("codigo",producto);
        mapa.put("accion",agregaQuita);
        mapa.put("usuario",usuarioID);
        mapa.put("cantmax",cantmax);
        mapa.put("cantmin",cantmin);
        mapa.put("cantidad",cantidad);
        mapa.put("lleno",lleno);
        String xmlsalida=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(TAREA_UBICA_PRODUCTO, "SQL", "SQL", xmlsalida, "", "");
        /*"<linea>" +
                "<espaid>"+ espacioID + "</espaid>" +
                "<codigo>" + producto +"</codigo>" +
                "<accion>" + agregaQuita  +"</accion>" +
                "<usuario>" + usuarioID + "</usuario>" +
                (Libreria.tieneInformacion(cantmax) ? ("<cantmax>" + cantmax + "</cantmax>") : "") +
                (Libreria.tieneInformacion(cantmin) ? "<cantmin>" + cantmin + "</cantmin>" : "") +
                (Libreria.tieneInformacion(cantidad) ? "<cantidad>" + cantidad + "</cantidad>" : "") +
                "<lleno>" + lleno + "</lleno>" +
                "</linea>"*/
    }

    /**
     * Trae la lista de prductos en una ubicacion
     * @param espaid ID de la ubicacion
     */
    public void listaProductos (int espaid){
        peticionWS(TAREA_PRODS_UBICACION, "SQL", "SQL", String.valueOf(espaid), "", "");
    }

    /**
     * Muestra un dialogo con la lsta de productos en la ubicacionactual
     */
    public void muestraListaProductos(){
        Dialog dialogo = new Dialog(this, R.style.Dialog);


        dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
        dialogo.setContentView(R.layout.dialogo_ubicaciones_lista_productos);
        dialogo.setTitle(txtUbicacion.getText().toString());

        listProductosUbicaciones = dialogo.findViewById(R.id.listUbicacionesDialogoProductos);
        List<Map<String,String>> data = new ArrayList<>();

        for(ProductosUbicacion pu: productos){
            Map<String, String> item = new HashMap<>(2);
            item.put("producto", pu.getProducto()+" ("+pu.getCodigo()+")");
            String activo = pu.isActivo()?"Activo":"Inactivo";
            String lleno = pu.isLleno()?"LLeno":"";
            String info = "cmax: " + pu.getCantidadMax() + " cmin: " + pu.getCantidadMin() + " " + activo + " " + lleno;
            item.put("info", info);
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] {"producto", "info"}, new int[] {android.R.id.text1, android.R.id.text2});
        listProductosUbicaciones.setAdapter(adapter);
        listProductosUbicaciones.setOnItemClickListener(((adapterView, view, i, l) -> muestraMensaje("Código: " + productos.get(i).getCodigo(), R.drawable.mensaje_exito)));

        dialogo.show();
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
                    muestraMensaje("Campo vacío", R.drawable.mensaje_warning);
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

        for(Producto producto: listaProductos){
            final TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);
            btnSeleccionar.setOnClickListener(view -> {

                editProducto.setText(producto.getCodigo());
                buscaProducto(producto.getCodigo());
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
     * Dialogo para buscar un producto y mostrar su ubicacion correspondiente
     */
    public void dialogoBuscarProducto(){
        final Dialog dialogo = new Dialog(this, R.style.Dialog);

        TextView txtDial = new TextView(getApplicationContext());
        txtDial.setText("Consulta de Ubicación");
        txtDial.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        dialogo.setTitle(txtDial.getText());
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogo.setContentView(R.layout.dialogo_ubicaciones_busca_producto);

        editDialogoCodigo = dialogo.findViewById(R.id.editubicacionesDialogoCodigo);
        txtDialogoUbicacion = dialogo.findViewById(R.id.txtUbicacionesDialogoUbicacion);
        txtDialogoProducto = dialogo.findViewById(R.id.txtUbicacionesDialogoProducto);
        editDialogoCodigo.setFocusableInTouchMode(true);
        editDialogoCodigo.requestFocus();
        listaUbikprod = dialogo.findViewById(R.id.ltUbicaciones);

        editDialogoCodigo.setOnKeyListener(((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!editDialogoCodigo.getText().toString().isEmpty())
                    ubicaProducto(editDialogoCodigo.getText().toString());
                else
                    muestraMensaje("Campo vacío", R.drawable.mensaje_error);
                return true;
            }

            return false;
        }));

        Button btnUbiCodigo = dialogo.findViewById(R.id.btnUbiCodigo);
        btnUbiCodigo.setOnClickListener(view -> dialogoBuscaProductos(tecladocodigo));
        if(tecladocodigo)
            editDialogoCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editDialogoCodigo.setInputType(InputType.TYPE_CLASS_TEXT);

        dialogo.findViewById(R.id.btnUbicacionesDialogoBarcode).setOnClickListener(view -> {
            barcodeRequest = 3;
            barcodeEscaner();
        });

        dialogo.show();
    }

    /**
     * Limpia los elementos de la pantalla
     */
    public void limpiaPantalla(){
        editProducto.setText("");
        editMax.setText("");
        editMin.setText("");
        editCantidad.setText("");
        checkLleno.setChecked(false);
        editProducto.requestFocus();
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        ContentValues obj = (ContentValues) output.getExtra1();
        if (obj != null) {
            switch (output.getTarea()){
                case TAREA_BUSCA_UBICACION: {
                    if (obj.getAsBoolean("exito")) {
                        localMensaje(obj.getAsString("mensaje"),true,R.raw.exito);
                        linearUbicaciones.setVisibility(View.VISIBLE);
                        editProducto.requestFocus();
                        espacioID = obj.getAsInteger("espaid");
                        txtUbicacion.setText(obj.getAsString("ubicacion"));
                        txtSucursal.setText(obj.getAsString("sucursal"));
                    } else {
                        localMensaje(obj.getAsString("mensaje"),false,R.raw.error);
                        limpiaPantalla();
                        editCodigo.setText("");
                        linearUbicaciones.setVisibility(View.GONE);
                    }
                    break;
                }
                case TAREA_PRODUCTO_CATALOGO: {
                    if (obj.getAsBoolean("exito")) {
                        localMensaje(obj.getAsString("producto"),true,R.raw.exito);
                        editMax.requestFocus();
                    } else {
                        localMensaje(obj.getAsString("mensaje"),false,R.raw.error);
                        editProducto.setText("");
                        editProducto.requestFocus();
                    }
                    break;
                }
                case TAREA_UBICA_PRODUCTO: {
                    if (obj.getAsBoolean("exito")) {
                        if (agregaQuita==2) {
                            localMensaje(obj.getAsString("mensaje"),true,R.raw.yaexiste);
                        } else {
                            Boolean yaExiste = obj.getAsBoolean("yaexiste");

                            if (yaExiste != null ? yaExiste : false) {
                                localMensaje(obj.getAsString("mensaje"),true,R.raw.yaexiste);
                            } else {
                                localMensaje(obj.getAsString("mensaje"),true,R.raw.exito);
                            }
                        }
                        txtMensaje.setText(obj.getAsString("mensaje"));
                        limpiaPantalla();
                    } else {
                        localMensaje(obj.getAsString("mensaje"),false,R.raw.error);
                        limpiaPantalla();
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_PRODS_UBICACION: {
                    if (obj.getAsBoolean("exito")) {
                        productos = servicio.getProductosUbicacion();
                        muestraListaProductos();
                    } else {
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_UBICACION_DE_PROD: {
                    if (obj.getAsBoolean("exito")) {
                        //txtDialogoUbicacion.setText(obj.getAsString("ubicacion"));
                        txtDialogoUbicacion.setVisibility(View.GONE);
                        List<Ubikprod> lista=servicio.traeUbicaProd(editDialogoCodigo.getText().toString());
                        if(!lista.isEmpty())
                            txtDialogoProducto.setText(MessageFormat.format("{0}\n{1}", lista.get(0).getCodigo(), lista.get(0).getProducto()));
                        UbikprodAdapter ubiad=new UbikprodAdapter(lista,this);
                        listaUbikprod.setAdapter(ubiad);
                        editDialogoCodigo.setText("");
                    } else {
                        //txtDialogoUbicacion.setText(obj.getAsString("ubicacion"));
                        //txtDialogoProducto.setText(obj.getAsString("mensaje"));
                        editDialogoCodigo.setText("");
                        muestraMensaje("Producto no encontrado", R.drawable.mensaje_error);
                    }
                    break;
                }
                case  TAREA_PRODUCTOS_BUSQUEDA:{
                    if(dlgBuscaProds!=null && dlgBuscaProds.isShowing()){
                        llenarTablaProductos(view -> {editDialogoCodigo.setText(view.getTag()+"");ubicaProducto(view.getTag()+"");dlgBuscaProds.dismiss();});
                    }else{
                        if(listaProductos != null)
                            listaProductos.clear();

                        listaProductos = servicio.getProductos(this);
                        llenarTablaProductos();
                    }
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

    private void localMensaje(String pMensaje,boolean pExito,int pAudio){
        linearMensaje.setBackgroundColor(getResources().getColor(pExito ? R.color.fondoExito:R.color.fondoError));
        txtMensaje.setText(pMensaje);
        txtMensaje.setTextColor(getResources().getColor(pExito ? R.color.black:R.color.colorWhite));
        reproduceAudio(pAudio);
    }
}