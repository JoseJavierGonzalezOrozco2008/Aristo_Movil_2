package com.example.aristomovil2;

import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.aristomovil2.adapters.PorSurtirAdapter;
import com.example.aristomovil2.adapters.SurtidosAdapter;
import com.example.aristomovil2.modelos.Caducidad;
import com.example.aristomovil2.modelos.ProductoDI;
import com.example.aristomovil2.modelos.RenglonEnvio;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ProductosDI extends ActividadBase {
    private int documento;
    public int ddin;
    public String producto, folioDi, fechaCad, pedidos, fechaCaducidad, claveCaducidad = "";
    public float cantidad;
    public boolean muestraRegistros, ocultaCaducidad = false;
    private ArrayList<Caducidad> caducidades;
    private ExpandableListView listProductos;
    private LinearLayout panelCaducidad;
    private EditText editRSCantidad, editRSFecha, editRSLote, editRsCantEnv;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_di);

        inicializarActividad("");

        Bundle extras = getIntent().getExtras();

        assert extras != null;
        documento = extras.getInt("documento");
        String ordenCompra = extras.getString("OC");
        folioDi = extras.getString("foliodi");
        pedidos = extras.getString("pedidos");

        muestraRegistros = extras.getBoolean("muestraRegistros");

        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        fechaCad = preferences.getString("fechacad", "MMAA");

        listProductos = findViewById(R.id.listProductosDI);
        panelCaducidad = findViewById(R.id.PDICaducidad);
        editRSCantidad = findViewById(R.id.editRSCaducidadCantidad);
        editRSFecha = findViewById(R.id.editRSCaducidadFecha);
        editRSLote = findViewById(R.id.editRSCaducidadLote);
        editRsCantEnv = findViewById(R.id.editRSCaducidadCantEnv);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        panelCaducidad.startAnimation(animation);
        panelCaducidad.setVisibility(View.GONE);

        findViewById(R.id.btnRSCaducidadGuardar).setOnClickListener( v -> {
            if (!editRSCantidad.getText().toString().isEmpty() && !editRSFecha.getText().toString().isEmpty() && !editRSLote.getText().toString().isEmpty()){
                if (!Libreria.validaFecha(fechaCad, editRSFecha.getText().toString()))
                    muestraMensaje("Fecha incompleta", R.drawable.mensaje_error);
                else{
                    fechaCaducidad = editRSFecha.getText().toString();
                    if(fechaCaducidad.length()==4){
                        fechaCaducidad=fechaCaducidad+"01";
                    }
                    if (documento == 16){
                        if (Float.parseFloat(editRsCantEnv.getText().toString()) <= Float.parseFloat(editRsCantEnv.getText().toString()))
                                guardaLote(ddin, editRSLote.getText().toString(), fechaCaducidad, editRSCantidad.getText().toString());
                        else
                            muestraMensaje("La cantidad a enviar no puede superar la cantidad en almacén", R.drawable.mensaje_error );
                    } else
                        guardaLote(ddin, editRSLote.getText().toString(), fechaCaducidad, editRSCantidad.getText().toString());

                    editRSCantidad.setText("");
                    editRSFecha.setText("");
                    editRSLote.setText("");
                    editRsCantEnv.setText("");
                }
            } else
                muestraMensaje("Debes llenar todos los campos", R.drawable.mensaje_warning);
        });

        findViewById(R.id.btnRSCaducidadCerrar).setOnClickListener( v -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            panelCaducidad.startAnimation(anim);
            ocultaCaducidad = false;
            disableEnableView(true, findViewById(R.id.productos_di_content));

            panelCaducidad.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) { panelCaducidad.setVisibility(View.GONE); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        String title = folioDi.substring(folioDi.length() - 4);

        if (documento == 14 || documento == 17) {
            actualizaToolbar("Detalle de " + title + " Rengs: 0");
            if(documento == 17 && !muestraRegistros){
                cargaPedido();
            }else{
                traeProductos();
            }


            findViewById(R.id.txtRSCaducidadCantEnv).setVisibility(View.GONE);
            findViewById(R.id.editRSCaducidadCantEnv).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant:");

            ((Switch)findViewById(R.id.switchRSCaducidadCalendario)).setOnCheckedChangeListener((compoundButton, b) -> {
                if(b){
                    findViewById(R.id.editRSCaducidadFecha).setOnClickListener(view -> {
                        DialogFragment newFragment = new Inventario.DatePickerFragment((EditText) view);
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    });
                    findViewById(R.id.editRSCaducidadFecha).setOnFocusChangeListener((view, b1) -> {
                        if(b1){
                            hideKeyboard(view);
                            DialogFragment newFragment = new Inventario.DatePickerFragment((EditText)view);
                            newFragment.show(getSupportFragmentManager(), "datePicker");
                        }
                    });
                }
                else{
                    muestraMensaje("Calendario desactivad", R.drawable.mensaje_warning);
                    findViewById(R.id.editRSCaducidadFecha).setOnTouchListener(null);
                    findViewById(R.id.editRSCaducidadFecha).setOnFocusChangeListener(null);
                }
            });
        }
        else if (documento == 16) {
            actualizaToolbar("Lista de " + (muestraRegistros ? "Recibidos" : "Pedidos") + " ; Folio: " + title + " Rengs: 0");

            if (muestraRegistros)
                traeProductos();
            else {
                assert ordenCompra != null;
                if (!ordenCompra.startsWith("S")) {
                    cargaPedido();
                }
                else
                    muestraMensaje("Sin pedidos por surtir pendientes" , R.drawable.mensaje_warning);
            }

            ((TextView)findViewById(R.id.txtRSCaducidadCantEnv)).setText("Cant:");
            ((TextView)findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant \nEnv:");
            findViewById(R.id.txtRSCaducidadCantEnv).setVisibility(View.VISIBLE);
            findViewById(R.id.editRSCaducidadCantEnv).setVisibility(View.VISIBLE);
            findViewById(R.id.switchRSCaducidadCalendario).setVisibility(View.GONE);
            findViewById(R.id.editRSCaducidadLote).setEnabled(false);
            findViewById(R.id.editRSCaducidadFecha).setEnabled(false);
        }
    }

    /**
     * Establece el contenido de la lista de Productos
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyProductosDI);
        ExpandableListView list = findViewById(R.id.listProductosDI);
        list.setEmptyView(empty);
    }

    @Override
    public void onBackPressed() {
        if(ocultaCaducidad)
            findViewById(R.id.btnRSCaducidadCerrar).performClick();
        else
            finish();
    }

    /**
     * Muestra el panel de caducidades
     */
    @SuppressLint("SetTextI18n")
    private void panelCaducidad(){
        ocultaCaducidad = true;
        disableEnableView(false, findViewById(R.id.productos_di_content));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
        panelCaducidad.startAnimation(anim);
        panelCaducidad.setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txtRSProducto)).setText(producto);

        float ingresar, aux = 0;
        for (Caducidad c:caducidades)
            if (documento == 16)
                aux += c.getCantl();
            else
                aux += c.getCant();
        ingresar = cantidad - aux;

        ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + ingresar);

        llenarTablaCaducidades();
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

        if (documento == 16)
            row.addView(getTextview("", Color.WHITE));
        row.addView(getTextview("Accion", Color.WHITE));
        row.addView(getTextview(documento == 16 ? "Alm" : "Cant", Color.WHITE));
        if (documento == 16)
            row.addView(getTextview("Env", Color.WHITE));
        row.addView(getTextview("Lote", Color.WHITE));
        row.addView(getTextview("Fecha Cad.", Color.WHITE));
        row.addView(getTextview("Notas", Color.WHITE));
        row.setPadding(10, 10, 10, 10);
        tablaCaducidades.addView(row);

        for (int i = 0; i < caducidades.size(); i++) {
            final TableRow rowData = new TableRow(this);
            rowData.setGravity(Gravity.CENTER);

            final int position = i;
            final String date;
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

                final AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setTitle("Borrar Caducidad");
                d.setMessage("¿Seguro que quieres borrar la Caducidad?");
                d.setPositiveButton("Borrar", (dialogInterface, i12) -> {
                    borraLote(caducidades.get(position).getDlot());
                    if (documento == 16) {
                        caducidades.get(position).setCantl(0);
                    } else if (documento == 14 || documento == 17) {
                        tablaCaducidades.removeView(row);
                        caducidades.remove(position);
                    }
                });
                d.setNegativeButton("Cancelar", (dialogInterface, i1) -> {
                });
                d.show();
            });
            if (caducidades.get(i).getDlot() == 0 || (documento == 16 && caducidades.get(i).getCantl() == 0)) {
                btnBorrar.setImageResource(R.drawable.eliminardisabled);
                btnBorrar.setEnabled(false);
            }

            if (documento == 16)
                rowData.addView(btnEditar);
            rowData.addView(btnBorrar);

            rowData.addView(getTextview("" + caducidades.get(i).getCant(), Color.BLACK));
            if (documento == 16)
                rowData.addView(getTextview("" + caducidades.get(i).getCantl(), Color.BLACK));
            rowData.addView(getTextview("" + caducidades.get(i).getLote(), Color.BLACK));
            rowData.addView(getTextview("" + date, Color.BLACK));
            rowData.addView(getTextview("" + caducidades.get(i).getNotas(), Color.BLACK));

            tablaCaducidades.addView(rowData);
        }
    }

    private void cargaPedido(){
        peticionWS(Enumeradores.Valores.TAREA_CARGA_PEDIDO, "SQL", "SQL", usuarioID, "0", pedidos);
    }

    /**
     * Llama al servicio que trae los productos
     */
    private void traeProductos(){
        peticionWS(Enumeradores.Valores.TAREA_PRODUCTOS_DI, "SQL", "SQL", folioDi, usuarioID, "");
    }

    /**
     * LLama al servicio que trae las caducidades
     */
    public void traeCaducidades(){
        peticionWS(Enumeradores.Valores.TAREA_LISTA_LOTE, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * LLama al servicio que trae las caducidades del envio
     */
    public void traeCaducidadesEnvio(){
        peticionWS(Enumeradores.Valores.TAREA_LISTA_LOTE_ENVIO, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * Metodo que llama al servicio para guardar un lote
     * @param ddin El ddin del lote
     * @param lote El lote
     * @param fecha La fecha del lote
     * @param cant la cantidad del lote
     */
    private void guardaLote(int ddin, String lote, String fecha, String cant){
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_LOTE, "SQL", "SQL",
                "<linea><ddin>" + ddin + "</ddin><lote>"+ lote +"</lote><fecha>"+ fecha + "</fecha>" +
                        "<usua>" + usuarioID + "</usua><cant>"+ cant +"</cant><autoriza>" + claveCaducidad + "</autoriza></linea>", "", "");
        claveCaducidad = "";
    }

    /**
     * Metodo que llama al servicio para borrar un lote
     * @param ddin El ddin del lote
     */
    private void borraLote(int ddin){
        peticionWS(Enumeradores.Valores.TAREA_BORRA_LOTE, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if (obj != null) {
            switch (output.getTarea()) {
                case TAREA_PRODUCTOS_DI:{
                    ArrayList<String> grupos = new ArrayList<>();
                    ArrayList<ArrayList<ProductoDI>> productos = new ArrayList<>();
                    grupos = servicio.getProductosDIGrupos();
                    if(documento == 16 && grupos.size()>0) {
                        int count = 0;
                        for (String grupo : grupos) {
                            ArrayList<ProductoDI> producto = servicio.getProductosDI(grupo.split(";")[1]);
                            productos.add(producto);
                            count += producto.size();
                        }

                        actualizaToolbar("Pedidos: " + grupos.size() + " Surtidos: " + count);
                    }else{
                        grupos.add(folioDi);
                        ArrayList<ProductoDI> producto = servicio.getProductosDI();
                        productos.add(producto);
                        actualizaToolbar("Detalle de " + folioDi.substring(folioDi.length() - 4) + " Rengs: " + productos.size());
                    }

                    SurtidosAdapter adapter = new SurtidosAdapter(productos, grupos, this);
                    listProductos.setAdapter(adapter);

                    for(int i = 0; i<grupos.size(); i++)
                        listProductos.expandGroup(i);

                    cierraDialogo();
                    break;
                }
                case TAREA_LISTA_LOTE:
                case TAREA_LISTA_LOTE_ENVIO:{
                    caducidades = new ArrayList<>();
                    if(obj.getAsBoolean("exito")){
                        if(output.getTarea() == Enumeradores.Valores.TAREA_LISTA_LOTE)
                            caducidades = servicio.getCaducidades();
                        else
                            caducidades = servicio.getCaducidadesEnvio();

                        muestraMensaje("Lista actual de lote", R.drawable.mensaje_exito);
                    }
                    else {
                        muestraMensaje("Lista de lote vacía", R.drawable.mensaje_warning);
                    }
                    panelCaducidad();
                    cierraDialogo();
                    break;
                }
                case TAREA_GUARDA_LOTE:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        reproduceAudio(R.raw.exito);

                        caducidades = servicio.getCaducidades();
                        float ingresar,cantLotesTot = 0;

                        for(Caducidad c:caducidades)
                            if (documento == 16)
                                cantLotesTot += c.getCantl();
                            else
                                cantLotesTot += c.getCant();

                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        llenarTablaCaducidades();
                        if(obj.getAsBoolean("cierra"))
                            findViewById(R.id.btnRSCaducidadCerrar).performClick();
                        else{
                            ingresar = cantidad - cantLotesTot;
                            ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + ingresar);
                        }
                    }
                    else {
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);

                        if(documento == 14 && obj.getAsBoolean("pideauto")){
                            final Dialog dialogo = new Dialog(this);
                            dialogo.setCancelable(false);
                            Objects.requireNonNull(dialogo.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                            dialogo.setContentView(R.layout.dialogo_password);
                            ((TextView)dialogo.findViewById(R.id.dPasswordTitle)).setText("Autorizacion para corta caducidad");

                            final EditText contraseña = dialogo.findViewById(R.id.dPasswordContraseña);
                            Button aceptar = dialogo.findViewById(R.id.dPasswordbtnAceptar);
                            Button cancelar = dialogo.findViewById(R.id.dPasswordbtnCancelar);

                            aceptar.setOnClickListener(v -> {
                                hideKeyboard(contraseña);
                                if (!contraseña.getText().toString().equals("")) {
                                    claveCaducidad = contraseña.getText().toString();
                                    guardaLote(ddin, editRSLote.getText().toString(), fechaCaducidad, editRSCantidad.getText().toString());
                                    dialogo.dismiss();
                                } else {
                                    muestraMensaje("Contraseña invalida", R.drawable.mensaje_error);
                                }
                            });

                            cancelar.setOnClickListener(view -> dialogo.dismiss());
                            dialogo.show();
                        }
                    }

                    break;
                }
                case TAREA_BORRA_LOTE:{
                    muestraMensaje("Renglón Borrado", R.drawable.mensaje_exito);
                    reproduceAudio(R.raw.exito);
                    llenarTablaCaducidades();
                    cierraDialogo();
                    if (documento == 16)
                        traeCaducidadesEnvio();
                    else if (documento == 14 || documento == 17)
                        traeCaducidades();
                    break;
                }
                case TAREA_CARGA_PEDIDO:{
                    if (obj.getAsBoolean("exito")) {
                        ArrayList<String> grupos = servicio.getDocumentosGrupo();
                        ArrayList<ArrayList<RenglonEnvio>> pedidos = new ArrayList<>();
                        int renglones = 0;

                        for (String grupo : grupos) {
                            ArrayList<RenglonEnvio> pedido = servicio.getRenglones(grupo.split(";")[0]);
                            pedidos.add(pedido);
                            renglones += pedido.size();
                        }
                        actualizaToolbar("Por surtir; Pedidos: " + grupos.size() + " Rengs: " + renglones);

                        PorSurtirAdapter adapter = new PorSurtirAdapter(pedidos, grupos, this);
                        listProductos.setAdapter(adapter);

                        for (int i = 0; i < grupos.size(); i++)
                            listProductos.expandGroup(i);
                    }
                    else {
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                        //findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
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
}