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

import java.text.MessageFormat;
import java.util.ArrayList;

public class ContProd extends ActividadBase {
    private String ascoid,conteo, ubicacion, ultimoCodigo, codigoId, cantidadProd,prodid;
    private float ultimoContado;
    private int cantidadUsual, ubikid;
    private boolean countPasada = false, origen;
    private ArrayList<Producto> productos;
    private TextView txtMensaje, txtCodigo, txtProducto;
    private Button btnBorra;
    private EditText editCodigo, editCantidad;
    private Dialog dialogoProductos, dialogo;
    private TableLayout tablaProductos;
    private MenuItem menuBorra;
    private boolean tecladocodigo,confirmacierre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo);
        inicializarActividad("Conteo Fisico");
        prodid="";
        Bundle extras = getIntent().getExtras();
        ascoid = extras.getString("ascoid");
        ubicacion = extras.getString("ubicacion");
        conteo  = extras.getString("conteo");
        ubikid = extras.getInt("ubikid");
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        cantidadUsual = preferences.getInt("cantidadusual", 10);
        boolean permif = preferences.getBoolean("permif", false);
        tecladocodigo = preferences.getBoolean("tecladocodigo", true);
        confirmacierre = preferences.getBoolean("confirmacierraubic", false);

        ((TextView)findViewById(R.id.txtConteoUsuario)).setText(usuario);
        ((TextView)findViewById(R.id.txtConteoEspacio)).setText(ubicacion);
        ((TextView)findViewById(R.id.labEspacio)).setText("Ubicacion:");
        txtMensaje = findViewById(R.id.txtConteoMensaje);
        editCodigo = findViewById(R.id.editConteoCodigo);
        editCantidad = findViewById(R.id.editConteoCantidad);
        txtCodigo = findViewById(R.id.txtConteoCodigo);
        txtProducto = findViewById(R.id.txtConteoProducto);

        if(!permif)
            findViewById(R.id.btnConteoCerrar).setVisibility(View.GONE);

        findViewById(R.id.btnConteoCodigo).setOnClickListener( v -> dialogoBuscaProductos(tecladocodigo));

        editCodigo.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String codigo = ((EditText)view).getText().toString();
                if(Libreria.tieneInformacion(codigo)) {
                    codigoId = codigo;
                    limpiar();
                    limpiaMensaje();
                    if (countPasada)
                        insertarPasada(codigo, 1);
                    else
                        buscarProductoConteo(codigo);
                }
                else
                    muestraMensaje("Ingresa el codigo", R.drawable.mensaje_warning);
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
                        builder.setMessage("¿Desas ingresar una cantidad mayor a la cantidad usual?");
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
                    muestraMensaje("Cantidad no mayor a 6 dígitos y al menos 1 dígito", R.drawable.mensaje_warning);
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
                muestraMensaje("Cantidad no permitida", R.drawable.mensaje_error);
        });

        findViewById(R.id.btnConteoTipo).setOnClickListener(v -> {
            ((Button)v).setText(countPasada?"POR PASADA":"POR CANTIDAD");

                v.setBackgroundColor(getResources().getColor(countPasada?R.color.aristo_amarillo: R.color.grisLigero));
            muestraMensaje(countPasada?"Conteo Por Pasada Desactivado":"Conteo Por Pasada Activado", R.drawable.mensaje_warning);
            findViewById(R.id.linearConteoCantidad).setVisibility(countPasada? View.VISIBLE:View.GONE);
            findViewById(R.id.linearConteoPasada).setVisibility(countPasada? View.GONE:View.VISIBLE);
            countPasada = !countPasada;
            limpiaMensaje();
            editCodigo.requestFocus();
        });

        findViewById(R.id.btnConteoCerrar).setOnClickListener(v -> {
            if(confirmacierre){
                AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                confirmacion.setTitle("Finalización");
                confirmacion.setMessage("¿Quiere cerrar el conteo de esta ubicación "+ubicacion+"?");
                confirmacion.setCancelable(false);
                confirmacion.setPositiveButton("SI", (dialog, which) -> wsCierraConteo());
                confirmacion.setNegativeButton("NO", null);
                confirmacion.show();
            }else{
                wsCierraConteo();
            }

        });
        /*btnBorra=findViewById(R.id.btnConteoBorra);
        btnBorra.setVisibility(View.GONE);
        btnBorra.setOnClickListener(v -> {
            borraRegnlonConteo();
        });*/
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
                    buscarProductoConteo(codigoId);
            }
        }
        else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conteo, menu);
        menuBorra = menu.findItem(R.id.menu_conteo_BorraRenglon);
        menuBorra.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_conteo_productos:
                mostrarConteo();
                break;
            case R.id.menu_conteo_BorraRenglon:
                borraRegnlonConteo();
                break;
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
        if(ascoid.equals("")){
            enviaMensaje("Seleccione una ubicación primero");
        }else {
            Intent intent = new Intent(this, Contados.class);
            intent.putExtra("conteo", ubicacion);
            intent.putExtra("ascoid", ascoid);
            intent.putExtra("modelo", 0);
            intent.putExtra("elimina", true);
            intent.putExtra("inventario", false);
            startActivity(intent);
        }
    }

    private void borraRegnlonConteo(){
        if(!Libreria.tieneInformacion(prodid) || !Libreria.tieneInformacion(txtProducto.getText().toString())){
            muestraMensaje("Busque el producto primero",R.drawable.mensaje_error);
            return;
        }
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("Borra renglón");
        confirmacion.setMessage(MessageFormat.format("{0} \n¿Está seguro de eliminar del conteo actual el producto ?",txtProducto.getText().toString()));
        confirmacion.setCancelable(false);
        confirmacion.setPositiveButton("SI", (dialog, which) -> peticionWS(Enumeradores.Valores.TAREA_CONT_BORRA_RENGLON, "SQL", "SQL", prodid,ubikid+"", ""));
        confirmacion.setNegativeButton("NO", null);
        confirmacion.show() ;
    }

    /**
     * Función que realiza la insercion del producto.
     * @param cantidad (String) Cantidad del producto a insertar.
     * @param accion (String) accion(Nuevo, sumar, remplazar, etc) que se realizara del producto a insertar.
     */
    private void insertarProd(String cantidad, String accion){
        if(!Libreria.tieneInformacion(codigoId))
            muestraMensaje("INGRESA CODIGO DEL PRODUCTO PRIMERO", R.drawable.mensaje_error);
        else
            peticionWS(Enumeradores.Valores.TAREA_CONT_GUARDA_RENGLON, "SQL", "SQL",
                    "<linea><accion>" + accion + "</accion><asco>" + ascoid + "</asco><codigo>" + codigoId +
                            "</codigo><usua>" + usuarioID + "</usua><cant>" + cantidad + "</cant>" +
                            //"<cadu></cadu>" +
                            "</linea>", "", "");
    }

    /**
     * Llama al servicio para contar un producto por pasada
     * @param codigo El codigo del producto
     * @param cantidad La cantidad contada
     */
    private void insertarPasada(String codigo, int cantidad){
        peticionWS(Enumeradores.Valores.TAREA_CONT_GUARDA_RENGLON, "SQL", "SQL",
                "<linea><accion>9</accion><asco>" + ascoid + "</asco><codigo>" + codigo +
                        "</codigo><usuaid>" + usuarioID + "</usuaid><cant>" + cantidad + "</cant><cadu></cadu></linea>", "", "");
    }

    /**
     * Llama al serivcio para buscar un producto
     * @param busqueda La busqueda
     */
    public void buscarProductoConteo(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_CONT_BUSCA_PROD, "SQL", "SQL",busqueda, ubikid+"", "");
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
                String num = contentValues.getAsString("cant");//Libreria.quitaDecimal(contentValues.getAsString("cant"));
                String msj = "Existen: " + num + " ¿Qué desea hacer?";
                mostrarDialogo(msj);
            }else {
                txtMensaje.setText(contentValues.getAsString("mensaje"));
                txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
                txtMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
                editCodigo.setText("");
                editCantidad.setText("");
                editCodigo.requestFocus();

            }
        }else{
            txtMensaje.setText("Error al recibir la respuesta");
            txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
            txtMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
            editCodigo.setText("");
            reproduceAudio(R.raw.error);
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
            limpiaMensaje();
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
        menuBorra.setEnabled(false);
    }

    private void limpiaMensaje(){
        txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        txtMensaje.setText("");
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();
        if(obj != null) {
            switch (output.getTarea()) {
                case TAREA_CONT_GUARDA_RENGLON:{
                    if(output.getExito()) {
                        String res_accion = obj.getAsString("accion");
                        txtMensaje.setTextColor(getResources().getColor(R.color.colorNegro));
                        if (res_accion.equals("5")) {
                            txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorDiferencias));
                        }else {
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
                    }else{
                        prodInsertado(obj);
                    }
                    break;
                }
                case TAREA_PRODUCTOS_BUSQUEDA:{

                    llenarTablaProductos(view -> {
                        codigoId = view.getTag()+"";
                        editCodigo.setText(codigoId);
                        dlgBuscaProds.dismiss();
                        if(countPasada)
                            insertarPasada(codigoId,1);
                        else
                            buscarProductoConteo(codigoId);
                    });
                    //hideKeyboard(dialogoProductos.findViewById(R.id.editProducto));
                    if(Libreria.tieneInformacion(output.getMensaje())){
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_warning);
                    }
                    break;
                }
                case TAREA_CONT_BUSCA_PROD:{
                    txtMensaje.setText(output.getMensaje());
                    if(output.getExito()){
                        prodid=obj.getAsString("prodid");
                        txtCodigo.setText(obj.getAsString("codigo"));
                        txtProducto.setText(obj.getAsString("producto"));
                        txtMensaje.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        txtMensaje.setTextColor(getResources().getColor(R.color.colorNegro));
                        txtCodigo.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        txtProducto.setBackgroundColor(getResources().getColor(R.color.colorExito));
                        reproduceAudio(R.raw.prodencontrado);
                        editCantidad.setFocusableInTouchMode(true);
                        if (editCantidad.requestFocus()){
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editCantidad, InputMethodManager.SHOW_FORCED);
                        }
                        menuBorra.setEnabled(true);
                    }
                    else {
                        txtMensaje.setBackgroundColor(getResources().getColor(R.color.color2));
                        txtMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
                        txtProducto.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        txtCodigo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        txtProducto.setText("");
                        txtCodigo.setText("");
                        editCodigo.requestFocus();
                        editCodigo.setText("");
                        reproduceAudio(R.raw.prodsinenc);
                    }
                    break;
                }
                case TAREA_CONT_ESTATUS:{
                    if(output.getExito()) {
                        onBackPressed();
                        Intent intent = new Intent(this, ContUbi.class);
                        startActivity(intent);
                    }else
                        enviaMensaje(output.getMensaje());
                    break;
                }
                case TAREA_CONT_BORRA_RENGLON:{
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito : R.drawable.mensaje_error);
                    if(output.getExito()){
                        prodid="";
                        limpiar();
                        limpiaMensaje();
                    }
                    break;
                }
            }
        }
        else {
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    public void wsCierraConteo(){
        peticionWS(Enumeradores.Valores.TAREA_CONT_ESTATUS, "SQL", "SQL", ascoid+"","107", usuarioID);
    }
}