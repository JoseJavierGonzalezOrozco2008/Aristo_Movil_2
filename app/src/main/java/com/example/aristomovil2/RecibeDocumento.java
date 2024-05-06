package com.example.aristomovil2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.text.InputFilter;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.adapters.DiferenciaCaducidadesAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.modelos.Caducidad;
import com.example.aristomovil2.modelos.DiferenciasCaducidades;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.RenglonEnvio;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RecibeDocumento extends ActividadBase {
    private int documento, cantidadUsual, espacios, renglon, maxRenglones, ddin, accion, barcodeRequest;
    private int caducidadFinalizaInserta = 0; //0:caducidad, 1:Finaliza, 2:Inserta
    private float cantidadPedida, cantdi, cantpedido, costo, impuestos, cant, cantTotal, divisa;
    public boolean conCaducidad, mandaImprimir, muestraImportes, imprimeDetalle, tecladocodigo, codigoBarras, puedeBorrar;
    private boolean tieneImp = false, buscaCodigo = false, porPasada = false, caduca, caducidadesAbierto = false;
    private boolean ocultaCaducidad = false, enLista = false, salirFinaliza = false, capdivisa = false, doc14soli = false;
    private String ordenCompra, folioDi, provedorSucursal, factura, fechaFolio, fechaCad, impresora, empresa;
    private String bulto, rengsdi, piezasdi, totaldi, pedidos, monedadivisa, fechaCaducidad, claveCaducidad = "";
    private RenglonEnvio renglonBD;
    private ArrayList<Producto> productos;
    private ArrayList<Caducidad> caducidades;
    private ArrayList<DiferenciasCaducidades> diferenciasCaducidades;
    private TextView txtCantidad, txtCodigo, txtProducto, txtUbicacion, txtNotas, txtRenglon, txtProductoResult;
    private TextView txtPiezasResult, txtDisponibleResult, txtSurtidoResult, txtCodigoResult, txtBultosCostos;
    private TextView txtExistenciaResult;
    private EditText editCodigo, editBultosCostos, editCargo, editSinCargo, editCaducado, editDañado, editFaltante;
    private EditText editRSCantidad, editRSFecha, editRSLote, editRsCantEnv;
    private Dialog dialogoProductos;
    private TableLayout tablaProductos;
    private LinearLayout panelCaducidad;
    private final int LAUNCH_PRODUCTOS_DI_ACTIVITY = 1;
    private DecimalFormat dfSharp = new DecimalFormat("#.###");

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibe_documento);
        inicializarActividad("");
        Bundle extras = getIntent().getExtras();

        assert extras != null;
        documento = extras.getInt("documento");
        ordenCompra = extras.getString("OC");
        folioDi = extras.getString("foliodi");
        provedorSucursal = extras.getString("prov/suc");

        if (extras.getString("bulto") != null)
            bulto = extras.getString("bulto");

        if (extras.containsKey("divisa"))
            divisa = extras.getFloat("divisa");
        else
            divisa = 1;

        SharedPreferences preferencesDI = getSharedPreferences("di", Context.MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        factura = preferencesDI.getString("facturaFolio", "");
        fechaFolio = preferencesDI.getString("fechaFolio", "Sin Fecha");
        impresora = preferencesConf.getString("impresora", "Predeterminada");
        codigoBarras = preferencesConf.getBoolean(impresora + "CodigoBarras", true);
        int digitos = preferences.getInt("digitos", 0);
        conCaducidad = preferences.getBoolean("concaducidad", false);
        fechaCad = preferences.getString("fechacad", "MMAA");
        mandaImprimir = preferences.getBoolean("mandaimprimir", false);
        cantidadUsual = preferences.getInt("cantidadusual", 10);
        empresa = preferences.getString("empresa", "Empresa");
        espacios = preferences.getInt("espacios", 3);
        muestraImportes = preferences.getBoolean("muestraimportes", false);
        imprimeDetalle = preferences.getBoolean("imprimedetalle", false);
        tecladocodigo = preferences.getBoolean("tecladocodigo", true);

        doc14soli = preferences.getBoolean("doc14soli", false);

        String title = folioDi.substring(folioDi.length() - 4);

        txtCodigo = findViewById(R.id.txtRecibeDocumentoCodigo);
        txtProducto = findViewById(R.id.txtRecibeDocumentoProducto);
        txtCantidad = findViewById(R.id.txtRecibeDocumentoCantidad);
        txtUbicacion = findViewById(R.id.txtRecibedocumentoUbicacion);
        txtNotas = findViewById(R.id.txtRecibeDocumentoNotas);
        txtRenglon = findViewById(R.id.txtrecibeDocumentoRenglon);
        txtBultosCostos = findViewById(R.id.txtRecibeDocumentoBultosCostos);
        txtProductoResult = findViewById(R.id.txtRecibeDocumentoProductoResult);
        txtCodigoResult = findViewById(R.id.txtRecibeDocumentoCodigoResult);
        txtPiezasResult = findViewById(R.id.txtRecibeDocumentoPiezasResult);
        txtDisponibleResult = findViewById(R.id.txtRecibeDocumentoDisponibleResult);
        txtExistenciaResult = findViewById(R.id.txtRecibeDocumentoExistenciaResult);
        txtSurtidoResult = findViewById(R.id.txtRecibeDocumentoSurtidoResult);
        editCodigo = findViewById(R.id.editRecibeDocumentoCodigo);
        editBultosCostos = findViewById(R.id.editRecibeDocumentoBultosCostos);
        editCargo = findViewById(R.id.editRecibeDocumentoCargo);
        editSinCargo = findViewById(R.id.editRecibeDocumentoSinCargo);
        editCaducado = findViewById(R.id.editRecibeDocumentoCaducado);
        editDañado = findViewById(R.id.editRecibeDocumentoDañado);
        editFaltante = findViewById(R.id.editRecibeDocumentoFaltante);
        editRSCantidad = findViewById(R.id.editRSCaducidadCantidad);
        editRSFecha = findViewById(R.id.editRSCaducidadFecha);
        editRSLote = findViewById(R.id.editRSCaducidadLote);
        editRsCantEnv = findViewById(R.id.editRSCaducidadCantEnv);
        TextView txtSinCargo = findViewById(R.id.txtRecibedocumentoSinCargo);
        Button btnCodigo = findViewById(R.id.btnRecibeDocumentoCodigo);
        Button btnCierraContenedor = findViewById(R.id.btnRecibeDocumentoCierraContenedor);
        Button btnFinalizaFactura = findViewById(R.id.btnRecibeDocumentoFinalizaFactura);
        Button btnRSCaducidadGuardar = findViewById(R.id.btnRSCaducidadGuardar);
        ImageButton btnAnterior = findViewById(R.id.btnRecibeDocumentoAnterior);
        ImageButton btnSiguiente = findViewById(R.id.btnRecibeDocumentoSiguiente);
        LinearLayout nopedidos = findViewById(R.id.lynopedido);
        panelCaducidad = findViewById(R.id.RSCaducidad);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        panelCaducidad.startAnimation(animation);
        panelCaducidad.setVisibility(View.GONE);

        editBultosCostos.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editCargo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editSinCargo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editCaducado.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editDañado.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editFaltante.setFilters(new InputFilter[]{new InputFilter.LengthFilter(digitos)});
        editCodigo.requestFocus();

        if (!ordenCompra.startsWith("S")) {
            txtSinCargo.setVisibility(View.GONE);
            editSinCargo.setVisibility(View.GONE);
        } else {
            nopedidos.setVisibility(View.GONE);
        }

        btnAnterior.setOnClickListener(v -> {
            if (renglon - 1 == 0) {
                renglon = maxRenglones;
                cargaPedido(renglon);
            } else
                cargaPedido(--renglon);
        });

        btnSiguiente.setOnClickListener(v -> {
            if (renglon + 1 > maxRenglones) {
                renglon = 1;
                cargaPedido(renglon);
            } else
                cargaPedido(++renglon);
        });

        editBultosCostos.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                if (documento == 16) {
                    insertaCantidad(editBultosCostos);
                } else {
                    editCodigo.requestFocus();
                }
            else {
                if (documento == 14 && capdivisa)
                    if (editBultosCostos.getText().toString().equals(""))
                        ((TextView) findViewById(R.id.txtRecibeDocumentoDivisaTotal)).setText("");
                    else
                        ((TextView) findViewById(R.id.txtRecibeDocumentoDivisaTotal)).setText("(" + dfSharp.format(Float.parseFloat(editBultosCostos.getText().toString()) * divisa) + "MXN)");
            }
            return false;
        });

        editCodigo.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                RenglonEnvio consulta = servicio.buscaRenglon(editCodigo.getText().toString());
                if (!consulta.getCodigo().equals("")) {
                    enLista = true;
                    traeRenglon(consulta.getNumrenglon());
                    folioDi = consulta.getDcinfolio();
                } else
                    enLista = false;
                peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", editCodigo.getText().toString(), "false", folioDi);
                buscaCodigo = true;
                editCargo.setFocusableInTouchMode(true);
                editCargo.requestFocus();
                editCargo.setText("");
                editSinCargo.setText("");
                editDañado.setText("");
                editCaducado.setText("");
                editCodigo.requestFocus();
                return true;
            }

            return false;
        });

        findViewById(R.id.btnRecibeDocumentoBarcode).setOnClickListener(view -> {
            barcodeRequest = 1;
            barcodeEscaner();
        });

        btnCodigo.setOnClickListener(v -> dialogoBuscaCodigo());

        editCargo.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!editCargo.getText().toString().equals("") && Float.parseFloat(editCargo.getText().toString()) > 0) {
                    if (!txtPiezasResult.getText().toString().equals("")) {
                        hideKeyboard(editCargo);
                        if (Libreria.tieneInformacion(folioDi) && folioDi.equals("00000")) {
                            caducidadFinalizaInserta = 2;
                            peticionWS(Enumeradores.Valores.TAREA_PEDIDOS_FOLIOS, "SQL", "SQL", usuarioID, "", "");
                        } else {
                            if (Libreria.tieneInformacion(editCargo.getText().toString()) && Float.parseFloat(editCargo.getText().toString()) > cantidadUsual) {
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                                dialog.setTitle("Cantidad usual excedida");
                                dialog.setMessage("¿Desas ingresar una cantidad mayor a la cantidad usual?");
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("Aceptar", (dialogInterface, j) -> insertaCantidad(editCargo));
                                dialog.setNegativeButton("Cancelar", (dialogInterface, j) -> {
                                    editCargo.setText("");
                                    editCargo.setFocusableInTouchMode(true);
                                    editCargo.requestFocus();
                                });
                                dialog.show();
                            } else
                                insertaCantidad(editCargo);
                        }
                    } else {
                        setResultColor(R.color.color2, R.color.white);
                        muestraMensaje("Falta escanear un código de producto", R.drawable.mensaje_warning);
                        editCargo.requestFocus();
                    }
                } else {
                    hideKeyboard(view);
                    muestraMensaje("Cantidad inválida", R.drawable.mensaje_warning);
                    editCargo.requestFocus();
                    editCargo.setText("");
                }
            }

            return false;
        });

        editSinCargo.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!txtPiezasResult.getText().toString().equals(""))
                    insertaCantidad(editSinCargo);
                else {
                    setResultColor(R.color.color2, R.color.white);
                    muestraMensaje("Falta escanear un código de producto", R.drawable.mensaje_warning);
                    editSinCargo.requestFocus();
                }
            }
            return false;
        });

        editDañado.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!txtPiezasResult.getText().toString().equals(""))
                    insertaCantidad(editDañado);
                else {
                    setResultColor(R.color.color2, R.color.white);
                    muestraMensaje("Falta escanear un código de producto", R.drawable.mensaje_warning);
                    editSinCargo.requestFocus();
                }
            }
            return false;
        });

        editCaducado.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!txtPiezasResult.getText().toString().equals(""))
                    insertaCantidad(editCaducado);
                else {
                    setResultColor(R.color.color2, R.color.white);
                    muestraMensaje("Falta escanear un código de producto", R.drawable.mensaje_warning);
                    editSinCargo.requestFocus();
                }
            }
            return false;
        });

        editFaltante.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                if (!txtPiezasResult.getText().toString().equals(""))
                    insertaCantidad(editFaltante);
                else {
                    setResultColor(R.color.color2, R.color.white);
                    muestraMensaje("Falta escanear un código de producto", R.drawable.mensaje_warning);
                    editSinCargo.requestFocus();
                }
            }
            return false;
        });

        btnCierraContenedor.setOnClickListener(v -> {
            if (Libreria.tieneInformacion(bulto)) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Cerrar Contenedor");
                dialog.setMessage("¿Seguro que quieres finalizar el contenedor?");
                dialog.setPositiveButton("Finalizar", (dialogInterface, i) -> peticionWS(Enumeradores.Valores.TAREA_CIERRA_BULTO, "SQL", "SQL", bulto, "", ""));
                dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {
                });
                dialog.show();
            } else
                muestraMensaje("Sin bulto en Captura", R.drawable.mensaje_warning);
        });

        btnFinalizaFactura.setOnClickListener(v -> {
            if (ordenCompra.equals("0")) {
                caducidadFinalizaInserta = 1;
                peticionWS(Enumeradores.Valores.TAREA_PEDIDOS_FOLIOS, "SQL", "SQL", usuarioID, "", "");
            } else
                dialogoFinaliza(folioDi, ordenCompra, true);
        });

        btnRSCaducidadGuardar.setOnClickListener(v -> {
            if (!editRSCantidad.getText().toString().isEmpty() && !editRSFecha.getText().toString().isEmpty() && !editRSLote.getText().toString().isEmpty()) {
                if (!Libreria.validaFecha(fechaCad, editRSFecha.getText().toString()))
                    muestraMensaje("Fecha incompleta", R.drawable.mensaje_error);
                else {
                    fechaCaducidad = editRSFecha.getText().toString();
                    hideKeyboard(v);
                    if (fechaCaducidad.length() == 4) {
                        fechaCaducidad = fechaCaducidad + "01";
                    }
                    if (documento == 16) {
                        if (Float.parseFloat(editRsCantEnv.getText().toString()) <= Float.parseFloat(editRsCantEnv.getText().toString())) {
                            if (Float.parseFloat(editRSCantidad.getText().toString()) <= cant)
                                guardaLote(ddin, editRSLote.getText().toString(), fechaCaducidad, editRSCantidad.getText().toString());
                            else
                                muestraMensaje("La cantidad a enviar no puede superar la cantidad por ingresar", R.drawable.mensaje_error);
                        } else
                            muestraMensaje("La cantidad a enviar no puede superar la cantidad en almacén", R.drawable.mensaje_error);
                    } else
                        guardaLote(ddin, editRSLote.getText().toString(), fechaCaducidad, editRSCantidad.getText().toString());
                }
            } else
                muestraMensaje("Debes llenar todos los campos", R.drawable.mensaje_warning);
        });

        editRSCantidad.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                btnRSCaducidadGuardar.performClick();
            }
            return false;
        });

        findViewById(R.id.btnRSCaducidadCerrar).setOnClickListener(v -> {
            ocultaCaducidad = false;
            disableEnableView(true, findViewById(R.id.recibe_documento_content));
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            panelCaducidad.startAnimation(anim);

            panelCaducidad.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    panelCaducidad.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            caducidadesAbierto = false;
            if (!txtRenglon.getText().toString().equals("Renglon")) {
                if (renglon == Integer.parseInt(txtRenglon.getText().toString().substring(txtRenglon.getText().toString().length() - 1))) {
                    if (cantidadPedida - Float.parseFloat(editCargo.getText().toString()) <= 0) {
                        if (renglon - 1 == 0) {
                            traeRenglon(renglon);
                        } else
                            traeRenglon(--renglon);
                    } else
                        traeRenglon(renglon);
                }
            }

            limpiarPantalla();
        });
        TextView extra1 = findViewById(R.id.txtExtra1);
        extra1.setVisibility(View.GONE);
        if (documento == 16) {
            pedidos = extras.getString("pedidos");
            /*if(pedidos.length()>0)
                cargaPedido(0);*/
            editBultosCostos.setVisibility(View.GONE);
            txtBultosCostos.setVisibility(View.GONE);
            if (ordenCompra.isEmpty() || ordenCompra.startsWith("S")) {
                ordenCompra = "Sin ped";

                findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
                findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
            } else {
                bultoActual(folioDi);
            }

            actualizaToolbar("Pedido: " + ordenCompra + ", Folio: " + title + " " + (Libreria.tieneInformacion(provedorSucursal) ? provedorSucursal : "") + ", " + usuario);
            editBultosCostos.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtPiezasResult.setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoCaducadoDañado).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoFaltanteNoPedido).setVisibility(View.GONE);
            txtSinCargo.setText("pzas empq: ");
            txtBultosCostos.setText("Bultos:");
            editSinCargo.setEnabled(false);

            findViewById(R.id.LinearRecibeDocumentoInfo).setOnTouchListener(new View.OnTouchListener() {
                private final GestureDetector gestureDetector = new GestureDetector(RecibeDocumento.this, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        detallesRenglon();
                        return super.onDoubleTap(e);
                    }
                });

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            ((TextView) findViewById(R.id.txtRSCaducidadCantEnv)).setText("Cant:");
            ((TextView) findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant \nEnv:");
            findViewById(R.id.editRSCaducidadLote).setEnabled(false);
            editRSFecha.setEnabled(false);
            findViewById(R.id.txtRSCaducidadCantEnv).setVisibility(View.VISIBLE);
            findViewById(R.id.editRSCaducidadCantEnv).setVisibility(View.VISIBLE);
            findViewById(R.id.switchRSCaducidadCalendario).setVisibility(View.GONE);
        } else if (documento == 14) {
            ordenCompra = ordenCompra.isEmpty() ? "Sin OC" : ordenCompra;
            actualizaToolbar("OC: " + ordenCompra + ", Folio: " + title + " " + provedorSucursal + ", " + usuario);

            extra1.setVisibility(View.VISIBLE);
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
            btnCierraContenedor.setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoCierraContenedor).setVisibility(View.GONE);
            txtBultosCostos.setText("Costo(S): ");

            findViewById(R.id.txtRSCaducidadCantEnv).setVisibility(View.GONE);
            findViewById(R.id.editRSCaducidadCantEnv).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant:");
        } else if (documento == 17) {
            pedidos = extras.getString("pedidos");

            actualizaToolbar("Folio: " + title + "" + provedorSucursal + ", " + usuario);
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoResult).setVisibility(View.VISIBLE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoCierraContenedor).setVisibility(View.GONE);
            txtBultosCostos.setVisibility(View.GONE);
            editBultosCostos.setVisibility(View.GONE);
            txtPiezasResult.setVisibility(View.GONE);
            txtSinCargo.setVisibility(View.GONE);
            editSinCargo.setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoCaducadoDañado).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoFaltanteNoPedido).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtRSCaducidadCantCad)).setText("Cant:");
        }
        if (documento == 14 || documento == 17) {
            txtExistenciaResult.setVisibility(documento == 17 || doc14soli ? View.VISIBLE : View.GONE);
            ((Switch) findViewById(R.id.switchRSCaducidadCalendario)).setChecked(true);
            listenerFechaLote();
            ((Switch) findViewById(R.id.switchRSCaducidadCalendario)).setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    listenerFechaLote();
                } else {
                    muestraMensaje("Calendario desactivad", R.drawable.mensaje_warning);
                    editRSFecha.setOnClickListener(null);
                    editRSFecha.setOnFocusChangeListener(null);
                }
            });
        }

        if (tecladocodigo)
            editCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editCodigo.setInputType(InputType.TYPE_CLASS_TEXT);
        limpiaResult();
    }

    /**
     * Finaliza la actividad
     */
    @Override
    public void onBackPressed() {
        if (ocultaCaducidad)
            findViewById(R.id.btnRSCaducidadCerrar).performClick();
        else
            finish();
    }

    /**
     * Obtiene el resultado del activity ProductosDI
     * @param requestCode El codigo de la peticion
     * @param resultCode El codigo resultado
     * @param data Los datos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_PRODUCTOS_DI_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getBooleanExtra("result", false)) {
                    renglon = data.getIntExtra("renglon", 1);
                    traeRenglon(renglon);
                }
            }
        }

        if (barcodeRequest == 1) {

            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (intentResult.getContents() != null) {
                editCodigo.setText(intentResult.getContents());
                RenglonEnvio consulta = servicio.buscaRenglon(editCodigo.getText().toString());
                if (!consulta.getCodigo().equals("")) {
                    enLista = true;
                    traeRenglon(consulta.getNumrenglon());
                    folioDi = consulta.getDcinfolio();
                } else
                    enLista = false;
                peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", editCodigo.getText().toString(), "false", folioDi);
                buscaCodigo = true;
                editCargo.setFocusableInTouchMode(true);
                editCargo.requestFocus();
                editCargo.setText("");
                editSinCargo.setText("");
                editDañado.setText("");
                editCaducado.setText("");
                editCodigo.requestFocus();
            } else
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
        }
    }

    /**
     * Crea las opciones del menu
     * @param menu La referencia del menu
     * @return El menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recibe_documento, menu);
        System.out.println("documento " + documento);
        if (documento != 16) {
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(8).setChecked(porPasada);
            if (documento == 17) {
                menu.getItem(1).setTitle("Renglones por recibir");
                menu.getItem(2).setTitle("Lista recibidos");
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(true);
                menu.getItem(7).setVisible(false);
                menu.getItem(9).setVisible(false);//opcion para mostrar la info servicio 593
            } else {
                menu.getItem(1).setTitle("Lista Surtidos");
            }
        } else {
            if (ordenCompra.startsWith("S"))
                menu.getItem(1).setVisible(false);

            menu.getItem(4).setVisible(false);
            menu.getItem(7).setVisible(false);
            menu.getItem(8).setVisible(false);
        }
        menu.getItem(5).setEnabled(!porPasada);

        return super.onCreateOptionsMenu(menu);
    }

    public void dialogoFinaliza(String folioFinaliza, String pedidoFinaliza, boolean salir) {
        salirFinaliza = salir;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Finalizar Factura " + folioFinaliza.substring(folioFinaliza.length() - 4) + "(" + pedidoFinaliza + ")");
        dialog.setMessage("¿Seguro que quieres finalizar la factura?");
        dialog.setPositiveButton("Finalizar", (dialogInterface, i) -> peticionWS(Enumeradores.Valores.TAREA_FINALIZA_DI, "", "SQL", folioFinaliza, "0", ""));
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {
        });
        dialog.show();
    }

    /**
     * Listener para las opciones del menu
     * @param item El item seleccionado
     * @return Retorna true para indicar exito
     */
    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, ProductosDI.class);
        intent.putExtra("documento", documento);
        intent.putExtra("OC", ordenCompra);
        intent.putExtra("foliodi", documento != 16 ? folioDi : (pedidos.split(",").length > 1 ? "00000" : folioDi));
        intent.putExtra("prov/suc", provedorSucursal);
        intent.putExtra("result", false);
        intent.putExtra("pedidos", pedidos);

        switch (item.getItemId()) {
            case R.id.menuRecibe_listaContenedor: {
                intent.setClass(this, Bultos.class);

                if (bulto != null && !bulto.isEmpty())
                    intent.putExtra("bulto", bulto);

                startActivity(intent);
                break;
            }
            case R.id.menuRecibe_RecibidoFaltantes: {
                if (bulto != null && !bulto.isEmpty())
                    intent.putExtra("bulto", bulto);
                if (documento == 16) {
                    intent.putExtra("muestraRegistros", ordenCompra.startsWith("S"));
                    intent.putExtra("result", true);
                } else if (documento == 17) {
                    intent.putExtra("muestraRegistros", false);
                    intent.putExtra("result", true);
                    intent.putExtra("pedidos", ordenCompra);
                }

                barcodeRequest = -1;
                startActivityForResult(intent, LAUNCH_PRODUCTOS_DI_ACTIVITY);
                break;
            }
            case R.id.menuRecibe_ListaSurtidos: {
                intent.putExtra("muestraRegistros", true);

                if (bulto != null && !bulto.isEmpty())
                    intent.putExtra("bulto", bulto);

                startActivity(intent);
                break;
            }
            case R.id.menuRecibe_EnviaEspera: {
                if (bulto != null && !bulto.isEmpty()) {
                    peticionWS(Enumeradores.Valores.TAREA_ENVIA_A_ESPERA, "SQL", "SQL", bulto, "", "");
                }
                break;
            }
            case R.id.menuRecibe_Ubicacioon: {
                muestraMensaje("En contrucción", R.drawable.mensaje_warning);
                break;
            }
            case R.id.menuRecibe_BorrarRenglon: {
                if (puedeBorrar) {
                    final AlertDialog.Builder d = new AlertDialog.Builder(this);
                    d.setTitle("Borrar REnglón");
                    d.setMessage("¿Seguro que quieres borrar el renglón?");
                    d.setPositiveButton("Borrar", (dialogInterface, i) -> borrarRenglon(ddin));
                    d.setNegativeButton("Cancelar", (dialogInterface, i) -> {
                    });
                    d.show();
                } else
                    muestraMensaje("Sin renglón para borrar", R.drawable.mensaje_warning);
                break;
            }
            case R.id.menuRecibe_DiferenciasCaucidades: {
                if (ordenCompra.equals("0")) {
                    caducidadFinalizaInserta = 0;
                    peticionWS(Enumeradores.Valores.TAREA_PEDIDOS_FOLIOS, "SQL", "SQL", usuarioID, "", "");
                } else
                    listaDiferenciaCaducidades(folioDi);
                break;
            }
            case R.id.menuRecibe_Impuestos: {
                DecimalFormat dfSharp = new DecimalFormat("#.00");
                if (item.isChecked()) {
                    item.setChecked(false);
                    tieneImp = false;
                    txtBultosCostos.setText("Costo(S): ");
                    if (costo > 0)
                        editBultosCostos.setText(dfSharp.format(costo));
                } else {
                    item.setChecked(true);
                    tieneImp = true;
                    txtBultosCostos.setText("Costo(I): ");
                    if (costo > 0)
                        editBultosCostos.setText(dfSharp.format(costo * (1 + impuestos)));
                }
                break;
            }
            case R.id.menuRecibe_PorPasada: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    porPasada = false;
                } else {
                    item.setChecked(true);
                    porPasada = true;
                }
                invalidateOptionsMenu();
                break;
            }
            case R.id.menuRecibe_Info: {
                infoDocumento();
                break;
            }
        }

        return true;
    }

    /**
     * Clase que crea un dialogo de calendario para seleccionar una fecha
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private final TextView txt;

        /**
         * Constructor de la Clase
         * @param txt El EditText donde se mostrara la fecha seleccionada
         */
        public DatePickerFragment(TextView txt) {
            this.txt = txt;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            txt.setText("" + (year - 2000) + (month < 9 ? "0" + (month + 1) : month + 1) + (day < 10 ? "0" + day : day));
        }
    }

    private void setResultColor(int background, int texto) {
        findViewById(R.id.linearRecibeDocumentoResult).setBackgroundColor(getResources().getColor(background));
        txtProductoResult.setTextColor(getResources().getColor(texto));
        txtCodigoResult.setTextColor(getResources().getColor(texto));
        txtPiezasResult.setTextColor(getResources().getColor(texto));
        txtDisponibleResult.setTextColor(getResources().getColor(texto));
        txtExistenciaResult.setTextColor(getResources().getColor(texto));
        txtSurtidoResult.setTextColor(getResources().getColor(texto));
    }

    /**
     * Metodo que llama al metodo para traer el siguiente renglon del pedido
     * @param cantidadCaptura La cantidad en captura
     */
    private void consultaSiguienteRenglon(float cantidadCaptura) {
        if (!txtRenglon.getText().toString().equals("Renglon")) {
            if (enLista) {
                if (cantidadPedida - cantidadCaptura <= 0)
                    renglon -= renglon == maxRenglones ? 1 : 0;

                cargaPedido(renglon);
            }
        }
    }

    /**
     * Metodo que llama al servicio para traer un renglon del pedido
     * @param renglon El nuemero de renglon
     */
    private void cargaPedido(int renglon) {
        if (Libreria.isNumeric(pedidos.replace(",", ""))) {
            peticionWS(Enumeradores.Valores.TAREA_CARGA_PEDIDO, "SQL", "SQL", usuarioID, String.valueOf(renglon), pedidos);
        }
    }

    @SuppressLint("SetTextI18n")
    private void traeRenglon(int renglon) {
        renglonBD = servicio.getRenglon(renglon);
        System.out.println("Pedidos:" + pedidos);
        if (renglonBD.getNumrenglon() != 0 && pedidos != null && Libreria.isNumeric(pedidos.replace(",", ""))) {
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.VISIBLE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.VISIBLE);

            cantidadPedida = renglonBD.getCantpedida();
            renglon = renglonBD.getNumrenglon();
            maxRenglones = renglonBD.getNumrengs();

            txtCantidad.setText(MessageFormat.format("Surte: {0}", cantidadPedida));
            txtProducto.setText(renglonBD.getProducto());
            txtCodigo.setText(renglonBD.getCodigo());
            txtUbicacion.setText(MessageFormat.format("UB: {0}", renglonBD.getUbicacion()));
            txtRenglon.setText(MessageFormat.format("Renglón: {0}", renglon));

            if (Libreria.tieneInformacion(renglonBD.getNotas())) {
                txtNotas.setVisibility(View.VISIBLE);
                txtNotas.setText("Notas: " + renglonBD.getNotas());
                txtNotas.setTextColor(Color.RED);
            } else
                txtNotas.setVisibility(View.GONE);

            if (documento == 16)
                actualizaToolbar("Pedido: " + renglonBD.getPediid() + ", Folio: " + renglonBD.getDcinfolio().substring(renglonBD.getDcinfolio().length() - 4)
                        + " " + (Libreria.tieneInformacion(provedorSucursal) ? provedorSucursal : "") + ", " + usuario);
        } else {
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
        }
    }

    /**
     * Metodo que llama al servicio que trae el bulto actual
     * @param folio El folio del bulto
     */
    private void bultoActual(String folio) {
        peticionWS(Enumeradores.Valores.TAREA_TRAE_BULTO_CAPTURA, "SQL", "SQL", folio, "", "");
    }

    /**
     * Elimna un renglon
     * @param opBorrar El ddin del renglon
     */
    private void borrarRenglon(int opBorrar) {
        peticionWS(Enumeradores.Valores.TAREA_BORRAR_RENGLON, "SQL", "SQL", String.valueOf(opBorrar), "", "");
    }

    /**
     * LLama al servicio que busca un producto
     * @param busqueda La busqueda realizada
     */
    protected void buscarProducto(String busqueda) {
        peticionWS(Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA, "SQL", "SQL",
                "<linea><d1>" + busqueda + "</d1><d2></d2><d3>|||</d3><cliente></cliente></linea>", "", "");
    }

    /**
     * Llama al servicio que regresa la informacion del documento
     */
    private void infoDocumento() {
        peticionWS(Enumeradores.Valores.TAREA_INFO_DOCUMENTO, "SQL", "SQL", ordenCompra, "", "");
    }

    /**
     * Metodo que intenta insertar la cantidad a un producto al presionar enter en algun input de la
     * parte inferior de la pantalla
     * @param edit El input donde se presiono enter
     */
    private void insertaCantidad(EditText edit) {
        if (cantdi > 0) {
            if (editCargo.getText().toString().equals("") && editSinCargo.getText().toString().equals("") &&
                    editDañado.getText().toString().equals("") && editCaducado.getText().toString().equals("") &&
                    editFaltante.getText().toString().equals("")) {
                muestraMensaje("No se ha llenado ningún campo", R.drawable.mensaje_error);
                edit.setFocusableInTouchMode(true);
                edit.requestFocus();
            } else {
                String cantidad = editCargo.getText().toString();
                String sincargo = editSinCargo.getText().toString();
                String dañado = editDañado.getText().toString();
                String caducado = editCaducado.getText().toString();
                String faltante = editFaltante.getText().toString();

                cantidad = cantidad.isEmpty() ? "0" : cantidad;
                sincargo = sincargo.isEmpty() ? "0" : sincargo;
                dañado = dañado.isEmpty() ? "0" : dañado;
                caducado = caducado.isEmpty() ? "0" : caducado;
                faltante = faltante.isEmpty() ? "0" : faltante;

                dialogoExistencias(cantidad, sincargo, dañado, caducado, faltante);
            }
        } else {
            if (editCargo.getText().toString().equals("") && editSinCargo.getText().toString().equals("") &&
                    editDañado.getText().toString().equals("") && editCaducado.getText().toString().equals("") &&
                    editFaltante.getText().toString().equals("")) {
                muestraMensaje("No se ha llenado ningún campo", R.drawable.mensaje_error);
                editCargo.requestFocus();
            } else {
                float cantTot, cargo, sincargo, dañado, caducado, faltante;

                cargo = Libreria.tieneInformacion(editCargo.getText().toString()) ? Float.parseFloat(editCargo.getText().toString()) : 0;
                sincargo = Libreria.tieneInformacion(editSinCargo.getText().toString()) ? Float.parseFloat(editSinCargo.getText().toString()) : 0;
                dañado = Libreria.tieneInformacion(editDañado.getText().toString()) ? Float.parseFloat(editDañado.getText().toString()) : 0;
                caducado = Libreria.tieneInformacion(editCaducado.getText().toString()) ? Float.parseFloat(editCaducado.getText().toString()) : 0;
                faltante = Libreria.tieneInformacion(editFaltante.getText().toString()) ? Float.parseFloat(editFaltante.getText().toString()) : 0;

                if (documento == 14)
                    cantTot = cargo + sincargo + dañado + caducado + faltante + cantdi;
                else if (documento == 16 || documento == 17 || documento == 18)
                    cantTot = cargo + cantdi;
                else
                    cantTot = 0;

                String codigo = editCodigo.getText().toString();
                String cantidadStr = editCargo.getText().toString();
                String sincargoStr = editSinCargo.getText().toString();
                String dañadoStr = editDañado.getText().toString();
                String caducadoStr = editCaducado.getText().toString();
                String faltanteStr = editFaltante.getText().toString();

                cantidadStr = cantidadStr.isEmpty() ? "0" : cantidadStr;
                sincargoStr = sincargoStr.isEmpty() ? "0" : sincargoStr;
                dañadoStr = dañadoStr.isEmpty() ? "0" : dañadoStr;
                caducadoStr = caducadoStr.isEmpty() ? "0" : caducadoStr;
                faltanteStr = faltanteStr.isEmpty() ? "0" : faltanteStr;

                if (!ordenCompra.startsWith("S") && cantTot > cantpedido)
                    dialogoAutoriza(cantTot, cantidadStr, sincargoStr, dañadoStr, caducadoStr, faltanteStr);
                else
                    guardaDatos(accion, !ordenCompra.startsWith("S") ? ordenCompra : "", folioDi, codigo, costo, cantidadStr, sincargoStr, dañadoStr, caducadoStr, faltanteStr, "");
            }
        }
    }

    /**
     * Metodo que llama al servicio para guardar la cantidad insertada a un producto
     * @param accion La La accion a realizar, 2 = Suma, 3 = Reemplaza
     * @param OC La orden de compra
     * @param foliodi El foliodi
     * @param codigo El codigo del producto
     * @param costo El costo del producto
     * @param cantidad La cantidad a guardar
     * @param sincargo La cantidad sin cargo
     * @param dañado La cantidad dañada
     * @param caducado La cantidad caducada
     * @param faltante La cantidad faltante
     * @param exedente La cantidad excedente
     */
    private void guardaDatos(int accion, String OC, String foliodi, String codigo, float costo, String cantidad, String sincargo, String dañado, String caducado, String faltante, String exedente) {
        int cantBultos = 1;

        if (documento == 16) {
            sincargo = "0";
            dañado = "0";
            caducado = "0";
            faltante = "0";
            try {
                cantBultos = Integer.parseInt(editBultosCostos.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                muestraMensaje("Bulto tiene que ser entero; Bultos por default: 1", R.drawable.mensaje_warning);
            }
        }

        if (documento == 14)
            costo = Float.parseFloat(editBultosCostos.getText().toString()) / (1 + (tieneImp ? impuestos : 0));

        peticionWS(Enumeradores.Valores.TAREA_GUARDA_DETALLE_DI, "SQL", "SQL",
                "<linea><accion>" + accion + "</accion><pedido>" + (documento == 17 ? OC : OC) + "</pedido>" +
                        "<folio>" + foliodi + "</folio><codigo>" + codigo + "</codigo><usuaid>" + usuarioID + "</usuaid>" +
                        "<costo>" + costo + "</costo><cantc>" + cantidad + "</cantc><cants>" + (documento == 17 ? "" : sincargo) + "</cants>" +
                        "<cantd>" + (documento == 17 ? "" : dañado) + "</cantd>" + "<cantv>" + (documento == 17 ? "" : caducado) + "</cantv>" +
                        "<cantf>" + (documento == 17 ? "" : faltante) + "</cantf><excedautoriz>" + exedente + "</excedautoriz>" +
                        "<autoriza></autoriza><cantbultos>" + (documento == 14 ? "1" : documento == 16 ? cantBultos : "") + "</cantbultos>" +
                        "<notas></notas><bultfolio></bultfolio><exc_cc>true</exc_cc><fecadu>20200820</fecadu></linea>", "", "");

    }

    /**
     * LLama al servicio que guarda la ubicacion para un producto
     * @param producto El producto
     * @param espaid El id de la ubicacion
     */
    private void guardaUbicacion(String producto, int espaid) {
        peticionWS(Enumeradores.Valores.TAREA_UBICA_PRODUCTO, "SQL", "SQL",
                "<linea><espaid>" + espaid + "</espaid><codigo>" + producto + "</codigo><agrega>true</agrega>" +
                        "<usuario>" + usuarioID + "</usuario><cantmax></cantmax><cantmin></cantmin><cantidad></cantidad>" +
                        "<lleno>false</lleno></linea>", "", "");
    }

    /**
     * Muestra un dialogo con los detalles del renglon actual
     */
    @SuppressLint("SetTextI18n")
    private void detallesRenglon() {
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_detalles_renglon);

        TextView text = dialog.findViewById(R.id.txtProductoRenglon);
        text.setText(renglonBD.getProducto());
        text = dialog.findViewById(R.id.txtCodigoRenglon);
        text.setText(renglonBD.getCodigo());
        text = dialog.findViewById(R.id.txtCantPedidaRenglon);
        text.setText("Cantidad pedida: " + renglonBD.getCantpedida());
        text = dialog.findViewById(R.id.txtCantIngresadaRenglon);
        text.setText("Cantidad ingresada: " + renglonBD.getIngresado());
        text = dialog.findViewById(R.id.txtUbicacionRenglon);
        text.setText("Ubicacion: " + renglonBD.getUbicacion());
        text = dialog.findViewById(R.id.txtDispoRenglon);
        text.setText("Disponibilidad: " + renglonBD.getDispo());
        text = dialog.findViewById(R.id.txtAnaquelRenglon);
        text.setText("Anaquel: " + renglonBD.getAnaquel());
        text = dialog.findViewById(R.id.txtNotasREnglon);
        text.setText("Notas: " + renglonBD.getNotas());
        text = dialog.findViewById(R.id.txtDocumentoRenglon);
        text.setText("Documento: " + renglonBD.getDcinfolio());
        text = dialog.findViewById(R.id.txtPedidoRenglon);
        text.setText("Pedido: " + renglonBD.getPediid());

        if (null == renglonBD.getNotas()) {
            text.setVisibility(View.GONE);
        }
        text = dialog.findViewById(R.id.txtRenglonRenglon);
        text.setText("Renglon: " + renglonBD.getNumrenglon());
        text = dialog.findViewById(R.id.txtRenglonesRenglon);
        text.setText("Renglones: " + renglonBD.getNumrengs());

        ImageButton btnCerrar = dialog.findViewById(R.id.btnCerrarRenglon);
        btnCerrar.setOnClickListener(view -> dialog.dismiss());

        Button ubicacion = dialog.findViewById(R.id.btnUbicacionDialogo);

        if (!renglonBD.getUbicacion().equals("Sin ubicar"))
            ubicacion.setVisibility(View.GONE);
        else
            ubicacion.setVisibility(View.VISIBLE);

        ubicacion.setOnClickListener(view -> {
            dialog.dismiss();
            dialogoUbicacion();
        });

        dialog.show();
    }

    /**
     * Muestra un dialogo que permite establecer la ubicacion del producto actual
     */
    private void dialogoUbicacion() {
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_ubicacion);

        EditText editUbicacion = dialog.findViewById(R.id.editUbicacion);

        editUbicacion.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!editUbicacion.getText().toString().isEmpty()) {
                    String ubicacion = editUbicacion.getText().toString();
                    peticionWS(Enumeradores.Valores.TAREA_BUSCA_UBICACION, "SQL", "SQL", ubicacion, "", "");
                    dialog.dismiss();
                } else
                    enviaMensaje("Campo vacio");
            }
            return false;
        });

        dialog.show();
    }

    /**
     * Metodo que llama al servicio que trae la lista de diferencias de caducidades
     * @param foliodi El folio
     */
    private void listaDiferenciaCaducidades(String foliodi) {
        peticionWS(Enumeradores.Valores.TAREA_LISTA_DIF_CADUCIDADES, "SQL", "SQL", foliodi, "", "");
    }

    /**
     * Metodo que llama al servicio para guardar un lote
     * @param ddin El ddin del lote
     * @param lote El lote
     * @param fecha La fecha del lote
     * @param cant la cantidad del lote
     */
    private void guardaLote(int ddin, String lote, String fecha, String cant) {
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_LOTE, "SQL", "SQL",
                "<linea><ddin>" + ddin + "</ddin><lote>" + lote + "</lote><fecha>" + fecha + "</fecha>" +
                        "<usua>" + usuarioID + "</usua><cant>" + cant + "</cant><autoriza>" + claveCaducidad + "</autoriza></linea>", "", "");
        claveCaducidad = "";
    }

    /**
     * Metodo que llama al servicio para borrar un lote
     * @param ddin El ddin del lote
     */
    private void borraLote(int ddin) {
        peticionWS(Enumeradores.Valores.TAREA_BORRA_LOTE, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * LLama al servicio que trae las caducidades del envio
     * @param ddin El ddin del lote
     */
    public void traeCaducidadesEnv(int ddin) {
        peticionWS(Enumeradores.Valores.TAREA_LISTA_LOTE_ENVIO, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * LLama al servicio que trae las caducidades
     * @param ddin El ddin del lote
     */
    private void traeCaducidades(int ddin) {
        peticionWS(Enumeradores.Valores.TAREA_LISTA_LOTE, "SQL", "SQL", String.valueOf(ddin), "", "");
    }

    /**
     * Muestra un dialogo con las diferencias de caducidades
     */
    private void dialogoDiferenciaCaducidades() {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_dif_caducidades);
        dialog.setTitle("Diferencias de Caducidades");

        ListView listaDifCaducidades = dialog.findViewById(R.id.listaDifCaducidades);
        DiferenciaCaducidadesAdapter adapter = new DiferenciaCaducidadesAdapter(diferenciasCaducidades);
        listaDifCaducidades.setAdapter(adapter);

        dialog.show();
    }

    /**
     * Muestra un dialogo para buscar el codigo de un producto
     */
    private void dialogoBuscaCodigo() {
        dialogoProductos = new Dialog(this);
        Objects.requireNonNull(dialogoProductos.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogoProductos.setContentView(R.layout.dialogo_busca_codigo);


        EditText editProducto = dialogoProductos.findViewById(R.id.editProducto);
        tablaProductos = dialogoProductos.findViewById(R.id.tablaProductos);
        tablaProductos.removeAllViews();

        if (tecladocodigo)
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
            }
            return false;
        });

        dialogoProductos.show();
    }

    /**
     * Muestra un dialogo con las acciones disponibles (suma, reemplaza, ignora) al insertar una
     * cantidad a un renglon
     * @param cantidad La cantidad a insertar
     * @param sincargo La cantidad sin cargo
     * @param dañado La cantidad dañada
     * @param caducado La cantidad caducada
     * @param faltante La cantidad faltante
     */
    @SuppressLint("SetTextI18n")
    private void dialogoExistencias(final String cantidad, final String sincargo, final String dañado, final String caducado, final String faltante) {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_existencias);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.txtTitulo)).setText("Ya existen " + cantdi + " piezas \n¿Qué desea hacer?");

        dialog.findViewById(R.id.btnSuma).setOnClickListener(view -> listenerDialogoExistencias(2, cantidad, sincargo, dañado, caducado, faltante, dialog));

        dialog.findViewById(R.id.btnReemplaza).setOnClickListener(view -> listenerDialogoExistencias(3, cantidad, sincargo, dañado, caducado, faltante, dialog));

        dialog.findViewById(R.id.btnIgnora).setOnClickListener(view -> {
            limpiarPantalla();
            dialog.dismiss();
        });

        dialog.setOnCancelListener(dialogInterface -> editCodigo.requestFocus());
        dialog.show();
    }

    /**
     * Listener para los botones de suma y reemplaza al insertar una cantidad en un renglon
     * @param accion La accion a realizar, 2 = Suma, 3 = Reemplaza
     * @param cantidad La cantidad a insertar
     * @param sincargo La cantidad sin cargo
     * @param dañado La cantidad dañada
     * @param caducado La cantidad caducada
     * @param faltante La cantidad faltante
     * @param dialog La referencia al dialogo donde se muestran los botones
     */
    private void listenerDialogoExistencias(int accion, String cantidad, String sincargo, String dañado, String caducado, String faltante, Dialog dialog) {
        this.accion = accion;
        if (!ordenCompra.startsWith("S")) {
            float cantidadTot = Float.parseFloat(cantidad);
            if (documento == 16 || documento == 17) {
                if (accion == 2)
                    cantidadTot = Float.parseFloat(cantidad) + cantdi;
                else if (accion == 3)
                    cantidadTot = Float.parseFloat(cantidad);
            } else if (documento == 14) {
                if (accion == 2)
                    cantidadTot = Float.parseFloat(cantidad) + Float.parseFloat(dañado) + Float.parseFloat(caducado) + Float.parseFloat(faltante) + cantdi;
                else
                    cantidadTot = Float.parseFloat(cantidad) + Float.parseFloat(sincargo) + Float.parseFloat(dañado) + Float.parseFloat(caducado) + Float.parseFloat(faltante);
            } else
                cantidadTot = 0;

            if ((Float.parseFloat(cantidad) > cantpedido && accion == 3) || (cantidadTot > cantpedido && accion == 2))
                dialogoAutoriza(cantidadTot, cantidad, sincargo, dañado, caducado, faltante);
            else
                guardaDatos(accion, ordenCompra, folioDi, editCodigo.getText().toString(), costo, cantidad, sincargo, dañado, caducado, faltante, "");
        } else
            guardaDatos(accion, "", folioDi, editCodigo.getText().toString(), costo, cantidad, sincargo, dañado, caducado, faltante, "");
        dialog.dismiss();
    }

    /**
     * Muestra un dialogo que pide autorizar la insercion de un exceso de cantidad a un renglon
     * @param cantidadTot La cantidad total, incluyendo el exceso
     * @param cantidad La cantidad pedida, sin el exceso
     * @param sincargo El sin cargo
     * @param dañado La cantidad dañada
     * @param caducado La cantidad caducada
     * @param faltante La cantidad faltante
     */
    private void dialogoAutoriza(float cantidadTot, final String cantidad, final String sincargo, final String dañado, final String caducado, final String faltante) {
        final AlertDialog.Builder dialogAutoriza = new AlertDialog.Builder(this);
        dialogAutoriza.setTitle("Autorizar Cantidad");
        dialogAutoriza.setMessage("¿Desas autorizar la cantidad de " + (cantidadTot - cantpedido) + "?");
        dialogAutoriza.setCancelable(false);
        dialogAutoriza.setPositiveButton("Aceptar", (dialogInterface, j) -> guardaDatos(accion, ordenCompra, folioDi, editCodigo.getText().toString(), costo, cantidad, sincargo, dañado, caducado, faltante, String.valueOf(cantidadTot - cantpedido)));
        dialogAutoriza.setNegativeButton("Cancelar", (dialogInterface, j) -> guardaDatos(accion, ordenCompra, folioDi, editCodigo.getText().toString(), costo, cantidad, sincargo, dañado, caducado, faltante, "0"));
        dialogAutoriza.show();
    }

    /**
     * Muestra el panel de caducidades
     */
    @SuppressLint("SetTextI18n")
    private void panelCaducidad() {
        ocultaCaducidad = true;
        disableEnableView(false, findViewById(R.id.recibe_documento_content));
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
        panelCaducidad.startAnimation(anim);
        panelCaducidad.setVisibility(View.VISIBLE);
        caducidadesAbierto = true;
        String producto = txtProductoResult.getText().toString();

        ((TextView) findViewById(R.id.txtRSProducto)).setText(producto);
        llenarTablaCaducidades();
    }

    /**
     * Listener para el EditText de fecha del panel de caducidades
     */
    private void listenerFechaLote() {
        editRSFecha.setOnClickListener(view -> {
            hideKeyboard(view);
            DialogFragment newFragment = new DatePickerFragment((EditText) view);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
        editRSFecha.setOnFocusChangeListener((view, b) -> {
            if (b) {
                hideKeyboard(view);
                DialogFragment newFragment = new DatePickerFragment((EditText) view);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
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

    /**
     * LLena la tabla con los productos obtenidos al buscar un codigo
     */
    @SuppressLint("SetTextI18n")
    private void llenarTablaProductos() {
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

        for (Producto producto : productos) {
            final TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);
            btnSeleccionar.setOnClickListener(view -> {
                buscaCodigo = true;
                limpiaInputs();
                editCodigo.setText(producto.getCodigo());
                peticionWS(Enumeradores.Valores.TAREA_PRODUCTO_CATALOGO, "SQL", "SQL", editCodigo.getText().toString(), "false", folioDi);

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
     * Limpia el panel donde se muestra el resultado del codigo escaneado
     */
    private void limpiaResult() {
        txtCodigoResult.setText("");
        txtProductoResult.setText("");
        txtPiezasResult.setText("");
        txtDisponibleResult.setText("");
        txtExistenciaResult.setText("");
        txtSurtidoResult.setText("");
        setResultColor(R.color.fondoRecibe, R.color.black);
        TextView ubi = findViewById(R.id.txtExtra1);
        ubi.setText("");
    }

    /**
     * Limpia los inputs de la parte inferior de la pantalla
     */
    private void limpiaInputs() {
        editCodigo.setText("");
        editCargo.setText("");
        editSinCargo.setText("");
        editDañado.setText("");
        editCaducado.setText("");
        editBultosCostos.setText("");
        editFaltante.setText("");

        editCodigo.requestFocus();
    }

    /**
     * Limpia todos los elementos en la pantalla
     */
    private void limpiarPantalla() {
        setResultColor(R.color.fondoNeutro, R.color.black);
        limpiaResult();
        limpiaInputs();
        cantdi = 0;
        cantpedido = 0;
        costo = 0;
        impuestos = 0;
        cant = 0;
        puedeBorrar = false;
        caduca = false;
    }

    /**
     * Imprime los tickets de bulto y factura
     * @param tituloTicket El titulo del ticket
     * @param contenedor El id del bulto
     * @param fecha La fecha
     * @param rengs Renglones en el bulto
     * @param piezas Las piezas en el bulto
     * @param cantImpresiones Numero de copias a imprmir
     * @param caso El caso de impresion
     */
    @SuppressLint("SimpleDateFormat")
    private void imprimir(String tituloTicket, String contenedor, String fecha, String rengs, String piezas, String pDetalle, int cantImpresiones, int caso) {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        BluetoothConnection selectedDevice = null;
        Dialog d = new Dialog(this);
        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
        String tipoImp = preferences.getString("tImp","");
        if(tipoImp != null && tipoImp.equals("Red")){
            String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
            int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
            String contenido = "";
            int espacios = getSharedPreferences("renglones",Context.MODE_PRIVATE).getInt("espacios",3);
            ServicioImpresionTicket impBult = new ServicioImpresionTicket();

            SimpleDateFormat parser;
            SimpleDateFormat formatter;
            String date;

            if(caso == 1){
                cantImpresiones = 1;
                caso = 2;
            }

            Bulto bulto;
            switch (caso){
                case 2:
                    String[] contenedores = contenedor.split(",");
                    String[] fechas = fecha.split(",");
                    String[] renglones = rengs.split(",");
                    String[] piezasMul = piezas.split(",");
                    String[] detalles = pDetalle.split(",");

                    for (int i = 0; i < cantImpresiones; i++) {
                        bulto=new Bulto(contenedores[i],folioDi,"Cerrado",pedidos,fechas[i],usuario,Integer.parseInt(renglones[i]),Float.parseFloat(piezasMul[i]),detalles[i]);
                        contenido += impBult.impresionBultos(bulto,impBult,provedorSucursal.toUpperCase(),usuario,codigoBarras,imprimeDetalle,espacios);
                        for (int j = 0; j < espacios; j++)
                            contenido += "|.,T1";
                    }
                    break;
                case 3:
                    if (documento == 16) {
                        bulto=new Bulto(contenedor,folioDi,"Cerrado",pedidos,fecha,usuario,Integer.parseInt(rengs),Float.parseFloat(piezas),pDetalle);
                        contenido = impBult.impresionBultos(bulto,impBult,provedorSucursal.toUpperCase(),usuario,codigoBarras,imprimeDetalle,espacios);
                    }
                    if(contenido.equals("")){
                        contenido += tituloTicket+",T1|";
                        contenido += empresa.toUpperCase()+",T2|";
                        contenido += provedorSucursal.toUpperCase()+",T2|";
                        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                        contenido += "Folio: "+folioDi + "\n"+"Fech mov: "+fechaHoy+",T1|";
                        contenido += folioDi+",T1";
                        if (documento == 14) {
                            /*Elemetos recuperados de la pantalla de Compra*/
                            contenido += "|Fac: "+factura+" "+fechaFolio+",T1";
                        }
                        contenido += "|"+usuario+",T1";
                        contenido += "|Rengs: "+rengsdi+" Piezas: "+piezasdi+",T1";
                        if (muestraImportes) {
                            contenido += "|Total: "+totaldi+",T1";
                        }
                        if (codigoBarras)
                            contenido += "|"+folioDi+",**";
                        else
                            contenido += "|"+folioDi+",T1";
                        for (int i = 0; i < espacios; i++)
                            contenido += "|.,T1";
                    }

                    break;
            }

            new Impresora(ip,contenido,puerto,espacios).execute();

        }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
            if (bluetoothDevicesList != null) {
                for (BluetoothConnection device : bluetoothDevicesList) {
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
                    if (device.getDevice().getName().equals(impresora))
                        selectedDevice = device;
                }

            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                d.setContentView(R.layout.dial_no_permiso);
                Button btn_ok = d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            } else {
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

                SimpleDateFormat parser;
                SimpleDateFormat formatter;
                String date;

                if(caso == 1){
                    cantImpresiones = 1;
                    caso = 2;
                }

                Bulto bulto;
                switch (caso){
                    case 2:
                        String[] contenedores = contenedor.split(",");
                        String[] fechas = fecha.split(",");
                        String[] renglones = rengs.split(",");
                        String[] piezasMul = piezas.split(",");
                        String[] detalles = pDetalle.split(",");

                        for (int i = 0; i < cantImpresiones; i++) {
                            bulto=new Bulto(contenedores[i],folioDi,"Cerrado",pedidos,fechas[i],usuario,Integer.parseInt(renglones[i]),Float.parseFloat(piezasMul[i]),detalles[i]);
                            impresora = Libreria.imprimeBulto(bulto,impresora,provedorSucursal.toUpperCase(),usuario,codigoBarras,imprimeDetalle,espacios);
                            for (int j = 0; j < espacios; j++)
                                impresora.addLine(".");
                        }
                        break;
                    case 3:
                        if (documento == 16) {
                            bulto=new Bulto(contenedor,folioDi,"Cerrado",pedidos,fecha,usuario,Integer.parseInt(rengs),Float.parseFloat(piezas),pDetalle);
                            impresora = Libreria.imprimeBulto(bulto,impresora,provedorSucursal.toUpperCase(),usuario,codigoBarras,imprimeDetalle,espacios);

                            impresora.addEndLine(espacios);
                        }

                        impresora.addLine(tituloTicket);
                        impresora.addTitle(empresa.toUpperCase());
                        impresora.addTitle(provedorSucursal.toUpperCase());
                        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                        impresora.addLine("Folio: " + folioDi + "\n" + "Fech mov: " + fechaHoy);
                        impresora.addBarcode(folioDi);
                        if (documento == 14) {
                            /*Elemetos recuperados de la pantalla de Compra*/
                            impresora.addLine("Fac: " + factura + " " + fechaFolio);
                        }
                        impresora.addLine(usuario);
                        impresora.addLine("Rengs: " + rengsdi + " Piezas: " + piezasdi);
                        if (muestraImportes) {
                            impresora.addLine("Total: " + totaldi);
                        }
                        if (codigoBarras)
                            impresora.addBarcodeImage(folioDi, 400, 100, BarcodeFormat.CODE_128);
                        else
                            impresora.addBarcode(folioDi);
                        for (int i = 0; i < espacios; i++)
                            impresora.addLine(".");
                        break;
                }

                try {
                    Integer dato=new AsyncBluetoothEscPosPrint(this,salirFinaliza).execute(impresora.Imprimir()).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(output.getTarea() == Enumeradores.Valores.TAREA_BORRAR_RENGLON){
            muestraMensaje("Renglon Borrado", R.drawable.mensaje_exito);
            reproduceAudio(R.raw.exito);
            limpiarPantalla();
            ddin = 0;
            cierraDialogo();
        }
        else if (obj != null) {
            switch (output.getTarea()) {
                case TAREA_CARGA_PEDIDO:{
                    if (obj.getAsBoolean("exito")){
                        traeRenglon(renglon);
                    }
                    else {
                        traeRenglon(renglon);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                        findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
                    }
                    cierraDialogo();
                    break;
                }
                case TAREA_TRAE_BULTO_CAPTURA:{
                    if (obj.getAsString("linea") != null && !obj.getAsString("linea").isEmpty()){
                        bulto = obj.getAsString("linea");
                        muestraMensaje("Bulto en Captura: " + obj.getAsString("linea"), R.drawable.mensaje_exito);
                    }
                    cierraDialogo();
                    renglon = 1;
                    cargaPedido(renglon);
                    break;
                }
                case TAREA_PRODUCTO_CATALOGO:{
                    if(obj.getAsBoolean("exito")){
                        if (obj.getAsInteger("ddin") != null)
                            ddin = obj.getAsInteger("ddin");

                        cantdi = obj.getAsFloat("cantdi");
                        cantpedido = obj.getAsFloat("cantpedido");
                        impuestos = obj.getAsFloat("impuptje");
                        costo = obj.getAsFloat("costo");
                        accion = 0;
                        String ubicacion = obj.getAsString("ubicacion");
                        if(documento == 14) {
                            capdivisa = obj.getAsBoolean("capdivisa");
                            monedadivisa = obj.getAsString("monedadivisa");
                            TextView ubi=findViewById(R.id.txtExtra1);
                            ubi.setText(ubicacion);
                            ((TextView)findViewById(R.id.txtRecibeDocumentoDivisaProducto)).setText("(" + monedadivisa + ")");

                            if(capdivisa)
                                ((TextView)findViewById(R.id.txtRecibeDocumentoDivisaTotal)).setText("(" + costo*divisa + "MXN)");
                            else
                                ((TextView)findViewById(R.id.txtRecibeDocumentoDivisaTotal)).setText("");
                        }

                        if(documento == 16) {
                            txtDisponibleResult.setText(MessageFormat.format("Disp: {0}", obj.getAsString("disponible")));
                            txtExistenciaResult.setText(MessageFormat.format("Fisi: {0}", obj.getAsString("existencia")));
                        }
                        else{
                            txtDisponibleResult.setText(MessageFormat.format("Disp: {0}", obj.getAsString("disponible")));
                            txtExistenciaResult.setText(MessageFormat.format("Soli: {0}", cantpedido));
                        }

                        if (obj.getAsFloat("cantdi") > 0){
                            reproduceAudio(R.raw.yaexiste);
                            txtSurtidoResult.setVisibility(View.VISIBLE);
                            txtSurtidoResult.setText(MessageFormat.format("Cap: {0}", obj.getAsFloat("cantdi")));
                            puedeBorrar = true;
                        }
                        else{
                            reproduceAudio(R.raw.exito);
                            txtSurtidoResult.setVisibility(View.GONE);
                        }

                        if (obj.getAsFloat("faltante") > 0)
                            editFaltante.setText(String.valueOf(obj.getAsFloat("faltante")));
                        if (obj.getAsFloat("danado") > 0)
                            editDañado.setText(String.valueOf(obj.getAsFloat("danado")));
                        if (obj.getAsFloat("caducado") > 0)
                            editCaducado.setText(String.valueOf(obj.getAsFloat("caducado")));
                        if (obj.getAsFloat("cc") > 0)
                            editCargo.setText(String.valueOf(obj.getAsFloat("cc")));
                        if (obj.getAsFloat("cs") > 0)
                            editSinCargo.setText(String.valueOf(obj.getAsFloat("cs")));

                        txtProductoResult.setText(obj.getAsString("producto"));
                        txtCodigoResult.setText(obj.getAsString("codigo"));

                        if(documento == 14){
                            if (tieneImp){
                                editBultosCostos.setText(dfSharp.format(costo * (1 + impuestos)) );
                            } else {
                                editBultosCostos.setText(dfSharp.format(costo) );
                            }
                        }
                        else if (documento == 16){
                            editSinCargo.setText(obj.getAsString("pzasempq"));
                            editBultosCostos.setText("1");

                            if(buscaCodigo && renglonBD != null){
                                /*if(renglones.size() == 0 || obj.getAsString("codigo").equals(renglones.get(0).getCodigo()))*/
                                if(renglonBD.getNumrenglon() == 0 || (renglonBD.getCodigos()!=null &&  renglonBD.getCodigos().contains(obj.getAsString("codigo"))))
                                    setResultColor(R.color.light_blue_A200, R.color.black);
                                else {
                                    setResultColor(R.color.color2, R.color.white);
                                    reproduceAudio(R.raw.yaexiste);
                                }
                            }
                        }
                        txtPiezasResult.setText("PzsEmq: " + obj.getAsString("pzasempq"));
                        editCargo.requestFocus();
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);

                        if(porPasada && documento == 14){
                            cierraDialogo();
                            accion = 2;
                            editCargo.setText("1");
                            if (!ordenCompra.startsWith("S"))
                                guardaDatos(cantdi==0?0:2, ordenCompra, folioDi, editCodigo.getText().toString(), costo, "1", "0", "0", "0", "0", "");
                            else
                                guardaDatos(cantdi==0?0:2, "", folioDi, editCodigo.getText().toString(), costo, "1", "0", "0", "0", "0", "");
                        }
                    }
                    else{
                        reproduceAudio(R.raw.error);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                        limpiaResult();
                        limpiaInputs();

                        cantdi = 0;
                        cantpedido = 0;
                        costo = 0;
                    }
                    buscaCodigo = false;
                    cierraDialogo();
                    break;
                }
                case TAREA_GUARDA_DETALLE_DI:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito")){
                        ((TextView)findViewById(R.id.txtRecibeDocumentoDivisaProducto)).setText("");
                        ((TextView)findViewById(R.id.txtRecibeDocumentoDivisaTotal)).setText("");
                        reproduceAudio(R.raw.exito);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);

                        ddin = obj.getAsInteger("ddin");
                        caduca = obj.getAsBoolean("caducable");
                        cant = obj.getAsFloat("cant");
                        cantTotal = obj.getAsFloat("canttotal");
                        boolean abreCaducidades = false;

                        if (caduca) {
                            if (documento == 16)
                                traeCaducidadesEnv(ddin);
                            else if (documento == 14 || documento == 17)
                                traeCaducidades(ddin);
                            abreCaducidades = true;
                        }

                        accion = obj.getAsInteger("accion");

                         if (!abreCaducidades){
                             LinearLayout mensajes = findViewById(R.id.LinearRecibeDocumentoInfo);
                             String cantidad = editCargo.getText().toString();
                             if(mensajes.getVisibility() == View.VISIBLE){
                                 System.out.println("Entre a checar el siguiente elemento");
                                 cantidad = Libreria.tieneInformacion(cantidad) ? cantidad : "0";
                                consultaSiguienteRenglon(Float.parseFloat(cantidad));
                             }
                         }

                        String [] bultos;
                        String [] bultRengs;
                        String [] bultPzas;

                        if (documento == 16){
                            if (obj.getAsString("bultos") != null){
                                int cantImpresiones = 0;
                                StringBuilder contMul = new StringBuilder();
                                StringBuilder fechMul = new StringBuilder();
                                StringBuilder rengsMul = new StringBuilder();
                                StringBuilder piezasMul = new StringBuilder();
                                bulto = obj.getAsString("bultos");
                                String bultrengs = obj.getAsString("bultrengs");
                                String bultpzas = obj.getAsString("bultpzas");

                                try {
                                    if(bulto != null && !bulto.equals("")) {
                                        bultos = bulto.split(",");
                                        bultRengs = bultrengs.split(",");
                                        bultPzas = bultpzas.split(",");

                                        for (int i = 0; i < bultos.length-1; i++) {
                                            cantImpresiones++;
                                            contMul.append(bultos[i]).append(",");
                                            fechMul.append(obj.getAsString("bultfe")).append(",");
                                            rengsMul.append(bultRengs[i]).append(",");
                                            piezasMul.append(bultPzas[i]).append(",");
                                        }
                                        if(bultos.length > 1) {
                                            contMul = new StringBuilder(contMul.substring(0, contMul.length() - 1));
                                            fechMul = new StringBuilder(fechMul.substring(0, fechMul.length() - 1));
                                            rengsMul = new StringBuilder(rengsMul.substring(0, rengsMul.length() - 1));
                                            piezasMul = new StringBuilder(piezasMul.substring(0, piezasMul.length() - 1));
                                            if (mandaImprimir)
                                                imprimir("Contenedor", contMul.toString(), fechMul.toString(), rengsMul.toString(), piezasMul.toString(),"", cantImpresiones, 2);
                                        }
                                        bulto = bultos[bultos.length - 1];
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                    muestraMensaje("Error con los bultos recuperados", R.drawable.mensaje_error);
                                }
                            }
                        }
                        if (!abreCaducidades)
                            limpiarPantalla();

                        if(porPasada)
                            muestraMensaje("Producto insertado exitosamente", R.drawable.mensaje_exito);
                    }
                    else{
                        reproduceAudio(R.raw.error);
                        limpiarPantalla();
                        editCodigo.requestFocus();
                    }
                    break;
                }
                case TAREA_ENVIA_A_ESPERA:{
                    muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_warning);
                    cierraDialogo();
                    break;
                }
                case TAREA_BORRAR_RENGLON:{
                    muestraMensaje("Renglon Borrado", R.drawable.mensaje_exito);
                    reproduceAudio(R.raw.exito);
                    limpiarPantalla();
                    ddin = 0;
                    cierraDialogo();
                    break;
                }
                case TAREA_LISTA_DIF_CADUCIDADES:{
                    diferenciasCaducidades = servicio.getDiferenciasCaducidades();
                    if (obj.getAsBoolean("exito")) {
                        if(diferenciasCaducidades.size() > 0)
                            dialogoDiferenciaCaducidades();
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);

                    cierraDialogo();
                    break;
                }
                case TAREA_PRODUCTOS_BUSQUEDA:{
                    if(productos != null)
                        productos.clear();

                    productos = servicio.getProductos(this);
                    llenarTablaProductos();
                    cierraDialogo();
                    break;
                }
                case TAREA_CIERRA_BULTO:{
                    muestraMensaje("Bulto cerrado " + obj.getAsString("bulto"), R.drawable.mensaje_exito);
                    if(mandaImprimir)
                        imprimir("Contenedor", obj.getAsString("bulto"), obj.getAsString("fecha"), obj.getAsString("renglones"), obj.getAsString("piezas"),obj.getAsString("detalles"), 1, 1);
                    bulto = "";
                    cierraDialogo();
                    break;
                }
                case TAREA_FINALIZA_DI:{
                    if(obj.getAsBoolean("exito")){
                        reproduceAudio(R.raw.exito);
                        muestraMensaje( obj.getAsString("mensaje"), R.drawable.mensaje_exito);

                        rengsdi = obj.getAsString("rengsdi");
                        piezasdi = obj.getAsString("piezasdi");
                        totaldi = obj.getAsString("totaldi");
                        folioDi = obj.getAsString("foliodi");

                        String titulo = documento==14?"-COMPRA-":documento==16?"-ENVIO-":documento==17?"-RECIBO":"Documentocerrado";

                        try {
                            if (documento == 16 && mandaImprimir)
                                imprimir(titulo, obj.getAsString("bultocerrado"), obj.getAsString("bultfe"), obj.getAsString("renglones"), obj.getAsString("piezas"),obj.getAsString("detalles"), 1, 3);
                            else if(mandaImprimir)
                                imprimir(titulo, "", "", "", "","", 1, 3);
                            if(salirFinaliza)
                                onBackPressed();
                        } catch (Exception e){
                            e.printStackTrace();
                            onBackPressed();
                        }
                    }
                    else{
                        reproduceAudio(R.raw.error);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                   cierraDialogo();
                   break;
                }
                case TAREA_LISTA_LOTE_ENVIO:
                case TAREA_LISTA_LOTE:{
                    if(caducidades != null)
                        caducidades.clear();
                    if (obj.getAsBoolean("exito") || documento == 14){
                        caducidades = output.getTarea()==Enumeradores.Valores.TAREA_LISTA_LOTE? servicio.getCaducidades():servicio.getCaducidadesEnvio();
                        float cantLotesTot = 0;

                        for(Caducidad c: caducidades)
                            cantLotesTot += output.getTarea()==Enumeradores.Valores.TAREA_LISTA_LOTE? c.getCant():c.getCantl();

                        if(!caducidadesAbierto && cantLotesTot != cantTotal)
                            panelCaducidad();

                        cant = cantTotal - cantLotesTot;
                        ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + cant);
                        cierraDialogo();
                    }
                    else{
                        cierraDialogo();
                        muestraMensaje( "Lista de lote vacía", R.drawable.mensaje_error);
                        if(output.getTarea() == Enumeradores.Valores.TAREA_LISTA_LOTE_ENVIO)
                            limpiarPantalla();
                        consultaSiguienteRenglon((float) 0);
                    }
                    break;
                }
                case TAREA_GUARDA_LOTE:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        reproduceAudio(R.raw.exito);

                        editRSCantidad.setText("");
                        editRSFecha.setText("");
                        editRSLote.setText("");
                        editRsCantEnv.setText("");

                        caducidades = servicio.getCaducidades();
                        float cantLotesTot = 0;

                        for(Caducidad c:caducidades)
                            if(documento == 16)
                                cantLotesTot += c.getCantl();
                            else
                                cantLotesTot += c.getCant();

                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        llenarTablaCaducidades();

                        if(obj.getAsBoolean("cierra")) {
                            consultaSiguienteRenglon(cantLotesTot);
                            findViewById(R.id.btnRSCaducidadCerrar).performClick();
                        }
                        else{
                            cant = cantTotal - cantLotesTot;
                            ((TextView)findViewById(R.id.txtRSCaducidadTitulo)).setText("Por ingresar: " + cant);
                        }
                    }
                    else{
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
                    muestraMensaje("Renglon borrado", R.drawable.mensaje_exito);
                    reproduceAudio(R.raw.exito);
                    llenarTablaCaducidades();
                    cierraDialogo();
                    if (documento == 16)
                        traeCaducidadesEnv(ddin);
                    else if (documento == 14 || documento == 17)
                        traeCaducidades(ddin);
                    break;
                }
                case TAREA_BUSCA_UBICACION:{
                    if (obj.getAsBoolean("exito")){
                        reproduceAudio(R.raw.exito);
                        int espaid = obj.getAsInteger("espaid");
                        guardaUbicacion(renglonBD.getCodigo(), espaid);
                    } else{
                        reproduceAudio(R.raw.error);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    cierraDialogo();
                    break;
                }
                case TAREA_UBICA_PRODUCTO:{
                    if (obj.getAsBoolean("exito")){
                        Boolean yaExiste = obj.getAsBoolean("yaexiste");
                        yaExiste = yaExiste != null ? yaExiste : false;
                        if (yaExiste)
                            reproduceAudio(R.raw.yaexiste);
                        else
                            reproduceAudio(R.raw.exito);

                        traeRenglon(renglon);//cargaPedido(String.valueOf(renglon));
                    } else{
                        reproduceAudio(R.raw.error);
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    cierraDialogo();
                    break;
                }
                case TAREA_INFO_DOCUMENTO:{
                    if(obj.getAsBoolean("exito")){
                        String html = obj.getAsString("anexo");
                        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);

                        WebView webview = new WebView(this);
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.loadData(html, "text/html; charset=utf-8", "UTF-8");

                        alert.setView(webview);
                        alert.setNegativeButton("Cerrar", (dialog, id) -> dialog.dismiss());
                        alert.show();
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    cierraDialogo();
                    break;
                }
                case TAREA_PEDIDOS_FOLIOS:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        String anexo = obj.getAsString("anexo");
                        String[] renglones = anexo.split(";");

                        if(renglones.length > 1) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                            builderSingle.setTitle("Pedidos");
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);

                            for (String renglon : renglones)
                                arrayAdapter.add(renglon.replace(",", "(") + ")");


                            builderSingle.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

                            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                                String renglon = renglones[which];
                                if(caducidadFinalizaInserta == 0)
                                    listaDiferenciaCaducidades(renglon.split(",")[0]);
                                else if(caducidadFinalizaInserta == 1) {
                                    dialogoFinaliza(renglon.split(",")[0], renglon.split(",")[1], false);
                                }
                                else{
                                    folioDi = renglon.split(",")[0];
                                    if(Libreria.tieneInformacion(editCargo.getText().toString()) && Float.parseFloat(editCargo.getText().toString()) > cantidadUsual) {
                                        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                                        dialog1.setTitle("Cantidad usual excedida");
                                        dialog1.setMessage("¿Desas ingresar una cantidad mayor a la cantidad usual?");
                                        dialog1.setCancelable(false);
                                        dialog1.setPositiveButton("Aceptar", (dialogInterface, j) -> insertaCantidad(editCargo));
                                        dialog1.setNegativeButton("Cancelar", (dialogInterface, j) -> {
                                            editCargo.setText("");
                                            editCargo.setFocusableInTouchMode(true);
                                            editCargo.requestFocus();
                                        });
                                        dialog1.show();
                                    }
                                    else
                                        insertaCantidad(editCargo);
                                }
                            });
                            builderSingle.show();
                        }
                        else {
                            if(caducidadFinalizaInserta == 0)
                                listaDiferenciaCaducidades(renglones[0].split(",")[0]);
                            else if(caducidadFinalizaInserta == 1)
                                dialogoFinaliza(renglones[0].split(",")[0], renglones[0].split(",")[1], true);
                            else{
                                folioDi = renglones[0].split(",")[0];
                                if(Libreria.tieneInformacion(editCargo.getText().toString()) && Float.parseFloat(editCargo.getText().toString()) > cantidadUsual) {
                                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                                    dialog.setTitle("Cantidad usual excedida");
                                    dialog.setMessage("¿Desas ingresar una cantidad mayor a la cantidad usual?");
                                    dialog.setCancelable(false);
                                    dialog.setPositiveButton("Aceptar", (dialogInterface, j) -> insertaCantidad(editCargo));
                                    dialog.setNegativeButton("Cancelar", (dialogInterface, j) -> {
                                        editCargo.setText("");
                                        editCargo.setFocusableInTouchMode(true);
                                        editCargo.requestFocus();
                                    });
                                    dialog.show();
                                }
                                else
                                    insertaCantidad(editCargo);
                            }
                        }
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
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