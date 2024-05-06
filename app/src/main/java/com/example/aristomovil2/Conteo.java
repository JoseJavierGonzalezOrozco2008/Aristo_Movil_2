package com.example.aristomovil2;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class Conteo extends ActividadBase {
    private String asifID, espaID, ubicacion, ultimoCodigo, codigoId, cantidadProd;
    private float ultimoContado;
    private int cantidadUsual;
    private boolean countPasada = false, origen;
    private ArrayList<Producto> productos;
    private TextView txtMensaje, txtCodigo, txtProducto;
    private EditText editCodigo, editCantidad;
    private Dialog dialogoProductos, dialogo;
    private TableLayout tablaProductos;
    private boolean tecladocodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo);
        inicializarActividad("Conteo Fisico");

        Bundle extras = getIntent().getExtras();
        asifID = extras.getString("asifid");
        espaID = extras.getString("espaid");
        ubicacion = extras.getString("ubicacion");
        origen  = extras.getBoolean("origen");

        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        cantidadUsual = preferences.getInt("cantidadusual", 10);
        boolean permif = preferences.getBoolean("permif", false);
        tecladocodigo = preferences.getBoolean("tecladocodigo", true);

        ((TextView)findViewById(R.id.txtConteoUsuario)).setText(usuario);
        ((TextView)findViewById(R.id.txtConteoEspacio)).setText(ubicacion);
        txtMensaje = findViewById(R.id.txtConteoMensaje);
        editCodigo = findViewById(R.id.editConteoCodigo);
        editCantidad = findViewById(R.id.editConteoCantidad);
        txtCodigo = findViewById(R.id.txtConteoCodigo);
        txtProducto = findViewById(R.id.txtConteoProducto);

        if(!permif)
            findViewById(R.id.btnConteoCerrar).setVisibility(View.GONE);

        findViewById(R.id.btnConteoCodigo).setOnClickListener( v -> dialogoBuscaCodigo());

        editCodigo.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String codigo = ((EditText)view).getText().toString();
                if(Libreria.tieneInformacion(codigo)) {
                    codigoId = codigo;
                    if (countPasada)
                        insertarPasada(codigo, 1);
                    else
                        peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", codigo, "true", "");
                }
                else
                    muestraMensaje("Ingresa el código", R.drawable.mensaje_warning,R.raw.yaexiste);
                return true;
            }else
                return false;
        });

        findViewById(R.id.btnConteoBarcode).setOnClickListener(view -> barcodeEscaner());

        editCantidad.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String cantidad = ((EditText)view).getText().toString();
                if(Libreria.countQuantity(cantidad)) {
                    cantidadProd = cantidad;

                    if(Float.parseFloat(cantidadProd) > cantidadUsual) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Cantidad usual excedida");
                        builder.setMessage("¿Deseas ingresar una cantidad mayor a la cantidad usual?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", (dialogInterface, j) -> insertarProd(cantidadProd, "0"));
                        builder.setNegativeButton("Cancelar", (dialogInterface, j) -> dialogInterface.dismiss());
                        builder.show();
                    }
                    else
                        insertarProd(cantidadProd, "0");
                }
                else {
                    editCantidad.setText("");
                    editCantidad.requestFocus();
                    muestraMensaje("Cantidad no mayor a 6 dígitos y al menos 1 dígito", R.drawable.mensaje_warning,R.raw.yaexiste);
                }
                return true;
            }else
                return false;
        });

        findViewById(R.id.btnConteoMas).setOnClickListener(v -> insertarPasada(ultimoCodigo,1));

        findViewById(R.id.btnConteoMenos).setOnClickListener(v -> {
            if(ultimoContado>0)
                insertarPasada(ultimoCodigo,-1);
            else
                muestraMensaje("Cantidad no permitida", R.drawable.mensaje_error,0);
        });

        findViewById(R.id.btnConteoTipo).setOnClickListener(v -> {
            ((Button)v).setText(countPasada?"POR PASADA":"POR CANTIDAD");

                v.setBackgroundColor(getResources().getColor(countPasada?R.color.aristo_amarillo: R.color.acomodo_renglon));
            muestraMensaje(countPasada?"Conteo Por Pasada Desactivado":"Conteo Por Pasada Activado", R.drawable.mensaje_warning,R.raw.yaexiste);
            findViewById(R.id.linearConteoCantidad).setVisibility(countPasada? View.VISIBLE:View.GONE);
            findViewById(R.id.linearConteoPasada).setVisibility(countPasada? View.GONE:View.VISIBLE);
            countPasada = !countPasada;
            limpiar();
            editCodigo.requestFocus();
        });

        findViewById(R.id.btnConteoCerrar).setOnClickListener(v -> {
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("Finalización");
            confirmacion.setMessage("¿Quiere cerrar la Ubicación?");
            confirmacion.setCancelable(false);
            confirmacion.setPositiveButton("SI", (dialog, which) -> peticionWS(Enumeradores.Valores.TAREA_CIERRA_CONTEO, "SQL", "SQL", usuarioID, espaID, ""));
            confirmacion.setNegativeButton("NO", null);
            confirmacion.show();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null) {
            editCodigo.setText(intentResult.getContents());

            String codigo = editCodigo.getText().toString();
            if (Libreria.tieneInformacion(codigo)) {
                codigoId = codigo;
                if (countPasada)
                    insertarPasada(codigo, 1);
                else
                    peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", codigo, "true", "");
            }
        }
        else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error,0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conteo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_conteo_productos) {
            mostrarConteo();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(origen) {
            Intent intent = new Intent(this, ConteoFisico.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Llama al servicio para mostrar la lista de renglones contados
     */
    private void mostrarConteo(){
        if(asifID.equals("")){
            enviaMensaje("Seleccione una ubicación primero");
        }else {
            Intent intent = new Intent(this, Contados.class);
            intent.putExtra("conteo", ubicacion);
            intent.putExtra("asifid", asifID);
            intent.putExtra("modelo", 0);
            intent.putExtra("elimina", true);
            startActivity(intent);
        }
    }

    /**
     * Función que realiza la insercion del producto.
     * @param cantidad (String) Cantidad del producto a insertar.
     * @param accion (String) accion(Nuevo, sumar, remplazar, etc) que se realizara del producto a insertar.
     */
    private void insertarProd(String cantidad, String accion){
        if(!Libreria.tieneInformacion(codigoId))
            muestraMensaje("INGRESA CODIGO DEL PRODUCTO PRIMERO", R.drawable.mensaje_error,0);
        else
            peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON_CONTEO, "SQL", "SQL",
                    "<linea><accion>" + accion + "</accion><asifid>" + asifID + "</asifid><codigo>" + codigoId +
                            "</codigo><usuaid>" + usuarioID + "</usuaid><cant>" + cantidad + "</cant><cadu></cadu></linea>", "", "");
    }

    /**
     * Llama al servicio para contar un producto por pasada
     * @param codigo El codigo del producto
     * @param cantidad La cantidad contada
     */
    private void insertarPasada(String codigo, int cantidad){
        peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON_CONTEO, "SQL", "SQL",
                "<linea><accion>9</accion><asifid>" + asifID + "</asifid><codigo>" + codigo +
                        "</codigo><usuaid>" + usuarioID + "</usuaid><cant>" + cantidad + "</cant><cadu></cadu></linea>", "", "");
    }

    /**
     * Llama al serivcio para buscar un producto
     * @param busqueda La busqueda
     */
    public void buscarProducto(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA, "SQL", "SQL",
                "<linea><d1>" + busqueda + "</d1><cliente>-1</cliente></linea>", "", "");
    }

    /**
     * Muestra el dialogo de busqueda de productos
     */
    public void dialogoBuscaCodigo(){
        dialogoProductos = new Dialog(this);
        dialogoProductos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogoProductos.setContentView(R.layout.dialogo_busca_codigo);

        final EditText editProducto = dialogoProductos.findViewById(R.id.editProducto);
        tablaProductos = dialogoProductos.findViewById(R.id.tablaProductos);
        tablaProductos.removeAllViews();

        editProducto.requestFocus();
        if(tecladocodigo)
            editProducto.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editProducto.setInputType(InputType.TYPE_CLASS_TEXT);

        editProducto.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!editProducto.getText().toString().isEmpty()) {
                    String busqueda = editProducto.getText().toString();
                    buscarProducto(busqueda);
                } else
                    muestraMensaje("Campo vacío", R.drawable.mensaje_error,0);
            }
            return false;
        });

        dialogoProductos.show();
    }

    /**
     * Llena la tabla con los productos obtenidos en una busqueda
     */
    @SuppressLint("SetTextI18n")
    public void llenarTablaProductos(){
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

        for(final Producto producto: productos){
            final TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);

            btnSeleccionar.setOnClickListener(view -> {
                editCodigo.setText(producto.getCodigo());
                dialogoProductos.dismiss();
                codigoId = producto.getCodigo();
                if(countPasada)
                    insertarPasada(codigoId,1);
                else
                    peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", codigoId, "true", "");
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
     * Metodo que trata la una insercion ya existente y se trato de insertar como nuevo, muestra un
     * dialogo para verificar que se desea hacer (reemplaza, suma o ignora).
     * @param contentValues (ContentValues) informacion del producto.
     */
    @SuppressLint("SetTextI18n")
    private void prodInsertado(ContentValues contentValues){
        if(contentValues!=null){
            if( contentValues.get("accion").equals("1")){
                String num = Libreria.quitaDecimal(contentValues.getAsString("cant"));
                String msj = "Existen: " + num + " ¿Qué desea hacer?";
                mostrarDialogo(msj);
            }else {
                txtMensaje.setText(contentValues.getAsString("mensaje"));
                txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
                editCodigo.setText("");
                editCantidad.setText("");
            }
        }else{
            txtMensaje.setText("Error al recibir la respuesta");
            txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
            editCodigo.setText("");
        }
    }

    /**
     * Dialogo que se muestra con información del producto y decia que se realizara con ese producto
     * @param mensajeRepet (String) mensaje informativo que contiene la cantidad del producto que no se pudo insertar
     */
    @SuppressLint("SetTextI18n")
    private void mostrarDialogo(String mensajeRepet) {
        dialogo = new Dialog(this);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogo.setContentView(R.layout.dialogo_existencias);
        ((TextView)dialogo.findViewById(R.id.txtTitulo)).setText(mensajeRepet);
        Button bAgrega = dialogo.findViewById(R.id.btnSuma);
        bAgrega.setText("(1)SUMA");

        bAgrega.setOnClickListener(view -> {
            dialogo.dismiss();
            insertarProd(cantidadProd, "2");
        });

        dialogo.findViewById(R.id.btnReemplaza).setOnClickListener(view -> {
            insertarProd(cantidadProd, "3");
            dialogo.dismiss();
        });

        dialogo.findViewById(R.id.btnIgnora).setOnClickListener(view -> {
            limpiar();
            editCodigo.requestFocus();
            dialogo.dismiss();
        });

        dialogo.show();
    }

    /**
     * Limpia los elementos de la pantalla
     */
    private void limpiar(){
        txtProducto.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        txtCodigo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        txtProducto.setText("");
        txtCodigo.setText("");
        editCodigo.setText("");
        editCantidad.setText("");
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        if(obj != null) {
            switch (output.getTarea()) {
                case TAREA_INSERTA_RENGLON_CONTEO:{
                    if(output.getExito()) {
                        if (obj.getAsString("accion").equals("5")) {
                            txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorDiferencias));
                        } else {
                            txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorExitoInsertado));
                        }
                        txtMensaje.setText(output.getMensaje());
                        reproduceAudio(R.raw.insertado);
                        ultimoCodigo = obj.getAsString("codigo");
                        ultimoContado = Libreria.tieneInformacion(obj.getAsString("cant")) ? Float.parseFloat(obj.getAsString("cant")) : 0;
                        codigoId = "";
                        limpiar();
                        ((EditText)findViewById(R.id.editConteoCantidadPasada)).setText(String.valueOf(ultimoContado));
                        ((TextView)findViewById(R.id.txtConteoUltimo)).setText(ultimoCodigo);
                        ((TextView)findViewById(R.id.txtConteoUltimoCantidad)).setText(String.valueOf(ultimoContado));
                        editCodigo.requestFocus();
                        cierraDialogo();
                    }
                    else{
                        cierraDialogo();
                        prodInsertado(obj);
                    }
                    break;
                }
                case TAREA_PRODUCTOS_BUSQUEDA:{
                    if(productos != null){
                        productos.clear();
                    }

                    productos = servicio.getProductos(this);
                    if(productos.isEmpty()){
                        muestraMensaje("Producto no encontrado",R.drawable.mensaje_error,0);
                        return;
                    }
                    llenarTablaProductos();
                    hideKeyboard(dialogoProductos.findViewById(R.id.editProducto));
                    if(Libreria.tieneInformacion(output.getMensaje()))
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_warning,0);
                    cierraDialogo();
                    break;
                }
                case TAREA_PRODUCTO_CATALOGO:{
                    txtMensaje.setText(output.getMensaje());
                    if(output.getExito()){
                        txtCodigo.setText(obj.getAsString("codigo"));
                        txtProducto.setText(obj.getAsString("producto"));
                        txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        txtCodigo.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        txtProducto.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        reproduceAudio(R.raw.prodencontrado);
                        editCantidad.setFocusableInTouchMode(true);
                        if (editCantidad.requestFocus()){
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editCantidad, InputMethodManager.SHOW_FORCED);
                        }
                    }
                    else {
                        txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
                        txtProducto.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        txtCodigo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        txtProducto.setText("");
                        txtCodigo.setText("");
                        editCodigo.requestFocus();
                        editCodigo.setText("");
                        reproduceAudio(R.raw.prodsinenc);
                    }
                    cierraDialogo();
                    break;
                }
                case TAREA_CIERRA_CONTEO:{
                    cierraDialogo();
                    if(output.getExito())
                        onBackPressed();
                    else
                        enviaMensaje(output.getMensaje());
                    break;
                }
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error,0);
        }
    }

    private void muestraMensaje(String pMsj, int pTipo,int pAudio){
        muestraMensaje(pMsj,pTipo);
        /*if(pAudio==0 ){
            if(pTipo==R.drawable.mensaje_error){
                reproduceAudio(R.raw.error);
            }else{
                reproduceAudio(R.raw.exito);
            }
        }else{
            reproduceAudio(pAudio);
        }*/
    }
}