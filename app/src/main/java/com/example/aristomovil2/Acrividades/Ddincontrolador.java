package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_BORRAR_RENGLON;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_BUSCA_UBICACION;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CARGA_PEDIDO;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CIERRA_BULTO;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_GUARDA_ANDEN_SURT;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODUCTOS_BUSQUEDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_UBICA_PRODUCTO;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Bultos;
import com.example.aristomovil2.ProductosDI;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.LoteAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.modelos.Documento;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.modelos.RenglonEnvio;
import com.example.aristomovil2.modelos.contenedores.VistaLotes;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Ddincontrolador extends ActividadBase {

    private int documento,cantidadUsual,imprime_espacios,posicion_renglon;
    private static int LAUNCH_PRODUCTOS_DI_ACTIVITY=1,LAUNCH_BULTOS=2;
    private String ordenCompra,folioDi,provedorSucursal,bulto,nombre_impresora,cad_lotes,lista_pedidos,ddinautoriza,folioventa,cad_ubis,anden;
    private float divisa,cantc,imp_total;
    private boolean imprime_codbarras,imprime_detalle,manda_imprimir,tipo_teclado,muestra_bulto,con_impuestos,edita_compra,pide_anden,esUltimo;
    private RenglonEnvio registroPedido;
    private EditText editCodigo,editCant,editCosSc,editCaducado,editDanado,editFaltante,editNopedido,editCosto;
    private TextView vistaProducto,vistaCodigo,vistaPiezas,vistaDispo,vistaExst,vistaSurt,vistaExtra1,vistaCodigoSur,vistaProductoSur,vistaCantidadSur,vistaUbicacionSur,vistaNotasSur,vistaRenglon,vistaDivisa;
    private ContentValues conProducto;
    private int llamandproductos,divisa_default,valor_divisa;
    private Integer nuevo_espacio,ubicaid;
    private Button cierraBulto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cambio();
    }

    public void cambio(){
        setContentView(R.layout.activity_ddincontrolador);
        inicializarActividad("Folio doc");
        Bundle extras = getIntent().getExtras();
        documento = extras.getInt("documento");
        lista_pedidos = extras.getString("pedidos");
        ordenCompra = extras.getString("OC");
        folioDi = extras.getString("foliodi");
        provedorSucursal = extras.getString("prov/suc");
        String imptotal = extras.getString("total");
        folioventa= servicio.getVentaFolio(ordenCompra);
        bulto = "";
        anden = "";
        if(extras.getString("bulto") != null)
            bulto = extras.getString("bulto");
        String divi = extras.getString("divisa");
        divisa = Libreria.tieneInformacionFloat(divi,1);
        imp_total = Libreria.tieneInformacionFloat(imptotal,1);
        //factura;
        //fechaFact;
        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);

        esUltimo = false;

        nombre_impresora=preferencesConf.getString("impresora", "Predeterminada");
        imprime_codbarras = preferencesConf.getBoolean(nombre_impresora + "CodigoBarras", true);
        imprime_detalle = preferences.getBoolean("imprimedetalle", false);
        imprime_espacios = preferences.getInt("espacios", 3);
        manda_imprimir = preferences.getBoolean("mandaimprimir", false);
        cantidadUsual = preferences.getInt("cantidadusual", 10);
        tipo_teclado = preferences.getBoolean("tecladocodigo", true);
        muestra_bulto = preferences.getBoolean("muestrabultos", false);
        divisa_default = preferences.getInt("divisadefault", 212);
        edita_compra = preferences.getBoolean("editacompra", false);
        pide_anden = preferences.getBoolean("pideanden", false);
        //System.out.println("pide anden "+pide_anden);
        valor_divisa = divisa_default;
        con_impuestos=false;

        if(documento==16){
            Documento doc = servicio.traeCatalogoPorFolioDI(folioDi);
            pide_anden = pide_anden && (doc!=null ? doc.getDiascred() == 1 : true);
        }

        editCodigo=findViewById(R.id.editRecibeDocumentoCodigo);
        editCant=findViewById(R.id.ddinCant);
        editCosSc=findViewById(R.id.ddinCosSc);
        editCosto=findViewById(R.id.ddinCosto);

        editDanado=findViewById(R.id.ddinDanado);
        editNopedido=findViewById(R.id.ddinNopedido);
        editFaltante=findViewById(R.id.ddinFaltante);
        editCaducado=findViewById(R.id.ddinCaducado);

        vistaProducto=findViewById(R.id.txtRecibeDocumentoProductoResult);
        vistaCodigo=findViewById(R.id.txtRecibeDocumentoCodigoResult);
        vistaPiezas=findViewById(R.id.txtRecibeDocumentoPiezasResult);
        vistaDispo=findViewById(R.id.txtRecibeDocumentoDisponibleResult);
        vistaExst=findViewById(R.id.txtRecibeDocumentoExistenciaResult);
        vistaSurt=findViewById(R.id.txtRecibeDocumentoSurtidoResult);
        vistaExtra1=findViewById(R.id.txtExtra1);

        vistaCodigoSur = findViewById(R.id.txtRecibeDocumentoCodigo);
        vistaProductoSur = findViewById(R.id.txtRecibeDocumentoProducto);
        vistaCantidadSur = findViewById(R.id.txtRecibeDocumentoCantidad);
        vistaUbicacionSur = findViewById(R.id.txtRecibedocumentoUbicacion);
        vistaNotasSur = findViewById(R.id.txtRecibeDocumentoNotas);
        vistaRenglon = findViewById(R.id.txtrecibeDocumentoRenglon);
        vistaDivisa = findViewById(R.id.vistaDivisa);

        ImageButton atras= findViewById(R.id.btnRecibeDocumentoAnterior);
        ImageButton sig= findViewById(R.id.btnRecibeDocumentoSiguiente);
        Button bqdProducto= findViewById(R.id.btnRecibeDocumentoCodigo);
        cierraBulto= findViewById(R.id.btnRecibeDocumentoCierraContenedor);
        Button terminaDI= findViewById(R.id.btnRecibeDocumentoFinalizaFactura);
        Button ajusta= findViewById(R.id.btnDdinAjusta);
        ImageButton escaner= findViewById(R.id.btnRecibeDocumentoBarcode);
        LinearLayout lnCosSc=findViewById(R.id.lnCostoSC);
        LinearLayout lncap1=findViewById(R.id.lyDdinCap1);
        LinearLayout lncap2=findViewById(R.id.lyDdinCap2);
        LinearLayout lncap3=findViewById(R.id.lyDdinCap3);

        llamandproductos=0;
        editCodigo.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                String codigo = editCodigo.getText().toString();
                wsBuscaprod(codigo);
            }
            return false;
        });

        editCant.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editCosto.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editFaltante.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editCosSc.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editCaducado.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editDanado.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        editNopedido.setOnKeyListener((view, i, keyEvent) -> {
            return enterCant(view,i,keyEvent);
        });
        atras.setOnClickListener(view -> {posicion_renglon--;posicion_renglon=posicion_renglon<1 ? 1 :posicion_renglon;traeRenglon(posicion_renglon);});
        sig.setOnClickListener(view -> {
            posicion_renglon++;
            posicion_renglon = registroPedido!=null && posicion_renglon>registroPedido.getNumrengs() ? registroPedido.getNumrengs():posicion_renglon;
            traeRenglon(posicion_renglon);
        });
        cierraBulto.setOnClickListener(view -> cierraDialogo());
        terminaDI.setOnClickListener(view -> terminaDI(false));
        cierraBulto.setTag(getResources().getText(R.string.cierra_contenedor));
        editCant.setFocusableInTouchMode(true);
        conProducto = null;
        muestraProducto(true);
        posicion_renglon = 1;

        escaner.setOnClickListener(view -> {llamandproductos=0;barcodeEscaner();});
        bqdProducto.setOnClickListener(v -> dialogoBuscaProductos(tipo_teclado));
        cierraBulto.setVisibility(muestra_bulto ? View.VISIBLE:View.GONE);
        if(tipo_teclado)
            editCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        else
            editCodigo.setInputType(InputType.TYPE_CLASS_TEXT);
        textoBulto();
        ajusta.setOnClickListener(view -> dlgCosto());
        switch (documento){//acciones por tipo de documento
            case 16:
                System.out.println("La orden:"+ordenCompra);
                if(Libreria.tieneInformacion(ordenCompra) && Libreria.isNumeric(ordenCompra)){
                    wsSolicitado(0);
                }else{
                    findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
                    findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
                }
                lnCosSc.setVisibility(View.GONE);
                lncap1.setVisibility(View.GONE);
                lncap2.setVisibility(View.GONE);
                lncap3.setVisibility(View.GONE);
                ajusta.setVisibility(View.GONE);
                break;
            case 14:
                cierraBulto.setVisibility(View.GONE);
                findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
                findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
                if(!Libreria.isNumeric(ordenCompra)){
                    ajusta.setVisibility(View.GONE);
                }
                break;
            case 17:
                cierraBulto.setVisibility(View.GONE);
                lnCosSc.setVisibility(View.GONE);
                ajusta.setVisibility(View.GONE);
                lncap1.setVisibility(View.GONE);
                lncap2.setVisibility(View.GONE);
                lncap3.setVisibility(View.GONE);
                wsSolicitado(0);
                break;
        }
        lncap3.setVisibility(View.GONE);
        generaTitulo();
        ddinautoriza = "";
        editCodigo.requestFocus();
        ubicaid = 0;
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        cambio();
    }

    @Override
    public void Finish(EnviaPeticion output) throws UnsupportedEncodingException {
        if(output.getTarea()!=TAREA_PRODUCTOS_BUSQUEDA && output.getTarea()!=TAREA_CIERRA_BULTO && output.getTarea()!=TAREA_CARGA_PEDIDO
                && output.getTarea()!=TAREA_BORRAR_RENGLON)
            muestraMensaje(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito : R.drawable.mensaje_error);
        ContentValues obj=(ContentValues)output.getExtra1();
        System.out.println("Tarea finalizada: "+output.getTarea().getTareaId());
        switch (output.getTarea()){

            case TAREA_TRAE_PROD_DDIN:
                if(output.getExito()){
                    conProducto=(ContentValues)output.getExtra1();
                    String extra="";
                    if(registroPedido!=null && !conProducto.getAsString("producto").equalsIgnoreCase(registroPedido.getProducto())){
                        extra=" *****DIFIERE DEL INDICADO***** \n"+conProducto.getAsString("info");
                        conProducto.put("info",extra);
                        reproduceAudio(R.raw.error);
                        /*muestraMensaje("El producto no coincide con el solicitado",R.drawable.mensaje_error);
                        muestraProducto(true);
                        editCodigo.requestFocus();
                        editCodigo.setText("");
                        return ;*/
                    }
                    bulto = conProducto.getAsString("bultfolio");
                    textoBulto();
                    muestraProducto(false);
                    if(documento==17){
                        editCant.setText(conProducto.getAsString("cantdi"));
                    }
                    editCant.requestFocus();
                    editCodigo.setText("");
                }else{
                    muestraProducto(true);
                    editCodigo.requestFocus();
                    reproduceAudio(R.raw.error);
                }
                break;
            case TAREA_GUARDA_DETALLE_DI:
                if(output.getExito()){
                    String codigoprod=conProducto.getAsString("codigo");
                    conProducto = null;
                    muestraProducto(true);
                    editCant.setText("");
                    editCodigo.requestFocus();
                    ContentValues termina=(ContentValues) output.getExtra1();
                    bulto = termina.getAsString("bulto");
                    String porsurtir=termina.getAsString("cantxsurtir");
                    String esultimo=termina.getAsString("ultimo");
                    float porSurtir=Libreria.tieneInformacion(porsurtir) ? Float.parseFloat(porsurtir):0;
                    if(Libreria.isNumeric(ordenCompra) && (documento==16 || documento==17) && registroPedido!=null){
                        if(porSurtir==0 ){
                            if(Libreria.getBoolean(esultimo)){
                                terminaDI(true);return;
                            }
                            recargaPendientes();
                        }else if(servicio.actualiza(codigoprod,registroPedido.getPediid()+"",porSurtir)>0){
                            traeRenglon(posicion_renglon);
                        }
                    }
                    textoBulto();
                }
                break;
            case TAREA_CARGA_PEDIDO:
                if(output.getExito()){
                    traeRenglon(posicion_renglon);
                }else{
                    findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
                    findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
                }
                break;
            case TAREA_CIERRA_BULTO:
                Dialog d = new Dialog(this);
                if(obj!=null){
                    muestraMensaje("Bulto cerrado " + obj.getAsString("bulto"), R.drawable.mensaje_exito);
                    if(manda_imprimir){
                        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
                        String tipoImp = preferences.getString("tImp","");

                        if(tipoImp != null && tipoImp.equals("Red")){
                            String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                            int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
                            String contenido = "";
                            if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                                muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
                            } else {

                                Bulto bulto=new Bulto(obj.getAsString("bulto"),folioDi,"Cerrado",ordenCompra, obj.getAsString("fecha"),usuario,Integer.parseInt( obj.getAsString("renglones")), Float.parseFloat(obj.getAsString("piezas")), obj.getAsString("detalles"));
                                ServicioImpresionTicket impBult = new ServicioImpresionTicket();
                                contenido = impBult.impresionBultos(bulto,impBult,Libreria.upper(provedorSucursal),usuario,imprime_codbarras,imprime_detalle,imprime_espacios);
                                new Impresora(ip,contenido,puerto, imprime_espacios).execute();
                            }


                        } else if(tipoImp != null && tipoImp.equals("Bluethooth")){
                            if(Build.VERSION.SDK_INT >= 31){
                                if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                                    d.setContentView(R.layout.dial_no_permiso);
                                    Button btn_ok = d.findViewById(R.id.btn_ok);
                                    btn_ok.setOnClickListener(view -> {
                                        d.dismiss();
                                    });
                                    d.show();
                                }else{
                                    Bulto bulto=new Bulto(obj.getAsString("bulto"),folioDi,"Cerrado",ordenCompra, obj.getAsString("fecha"),usuario,Integer.parseInt( obj.getAsString("renglones")), Float.parseFloat(obj.getAsString("piezas")), obj.getAsString("detalles"));
                                    BluetoothConnection selectedDevice = traeImpresora(nombre_impresora);
                                    ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
                                    impresora=Libreria.imprimeBulto(bulto,impresora,Libreria.upper(provedorSucursal),usuario,imprime_codbarras,imprime_detalle,imprime_espacios);
                                    try {
                                        new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());//.get();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }
                            }else {

                                Bulto bulto=new Bulto(obj.getAsString("bulto"),folioDi,"Cerrado",ordenCompra, obj.getAsString("fecha"),usuario,Integer.parseInt( obj.getAsString("renglones")), Float.parseFloat(obj.getAsString("piezas")), obj.getAsString("detalles"));
                                BluetoothConnection selectedDevice = traeImpresora(nombre_impresora);
                                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
                                impresora=Libreria.imprimeBulto(bulto,impresora,Libreria.upper(provedorSucursal),usuario,imprime_codbarras,imprime_detalle,imprime_espacios);
                                try {
                                    new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());//.get();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                            }
                        }else{
                            muestraMensaje("Error al imprimir.",R.drawable.mensaje_error);
                        }
                    }
                    bulto = "";
                    textoBulto();
                }
                break;
            case TAREA_ENVIA_A_ESPERA:
                bulto = "";
                textoBulto();
                break;
            case TAREA_FINALIZA_DI:
                if(output.getExito()){
                    reproduceAudio(R.raw.exito);
                    muestraMensaje( obj.getAsString("mensaje"), R.drawable.mensaje_exito);

                    folioDi = obj.getAsString("foliodi");
                    try {
                        //if(manda_imprimir){
                        if(true){
                            SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
                            //System.out.println("Valor: "+preferences.getString("tImp",""));
                            d = new Dialog(this);
                            String tipoImp = preferences.getString("tImp","");

                            if(tipoImp != null && tipoImp.equals("Red")){
                                String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
                                int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
                                String contenido = "";

                                String bultencab=obj.getAsString("bultotik");
                                if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                                    muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
                                } else {
                                    if(Libreria.tieneInformacion(bultencab))
                                        contenido+=bultencab;
                                    String bultddindet=obj.getAsString("detallesbulto");
                                    if(Libreria.tieneInformacion(bultddindet))
                                        contenido+=bultddindet;
                                    String impencab=obj.getAsString("encabtik");
                                    if(Libreria.tieneInformacion(impencab))
                                        contenido+=impencab;
                                    String impddindet=obj.getAsString("prodstik");
                                    if(Libreria.tieneInformacion(impddindet))
                                        contenido+=impddindet;

                                    new Impresora(ip, contenido, puerto, imprime_espacios).execute();
                                }

                            }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
                                if(Build.VERSION.SDK_INT >= 31){
                                    if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                                        d.setContentView(R.layout.dial_no_permiso);
                                        Button btn_ok = d.findViewById(R.id.btn_ok);
                                        btn_ok.setOnClickListener(view -> {
                                            d.dismiss();
                                        });
                                        d.show();
                                    }else{
                                        impresionBluet(obj);
                                    }
                                }else{
                                    impresionBluet(obj);
                                }
                            }else{
                                muestraMensaje("Error al imprimir.",R.drawable.mensaje_error);
                            }

                        }
                        if(lista_pedidos.equals(ordenCompra)){
                            onBackPressed();
                        }else{
                            actualizaPedido();
                            posicion_renglon=1;
                            wsSolicitado(0);
                            muestraProducto(true);
                            editCodigo.requestFocus();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        onBackPressed();
                    }
                }else{

                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                }
                break;

            case TAREA_PRODUCTOS_BUSQUEDA:
                llenarTablaProductos(view->{
                    editCodigo.setText(view.getTag()+"");
                    wsBuscaprod(view.getTag()+"");
                    dlgBuscaProds.dismiss();});
                break;
            case TAREA_BORRAR_RENGLON:
                muestraMensaje("Eliminado con éxito",R.drawable.mensaje_exito);
                conProducto=null;
                muestraProducto(true);
                editCant.setText("");
                editCodigo.requestFocus();
                wsSolicitado(0);
                break;
            case TAREA_AJUSTA_OC:
                if(output.getExito()){
                    conProducto=null;
                    muestraProducto(true);
                    editCant.setText("");
                    editCodigo.requestFocus();
                }
                break;
            case TAREA_UBICA_PRODUCTO:
                if(output.getExito()){
                    traeRenglon(posicion_renglon);
                }break;
            case TAREA_BUSCA_UBICACION:
                if(output.getExito()){
                    ContentValues mapa=(ContentValues) output.getExtra1();
                    nuevo_espacio=mapa.getAsInteger("espaid");
                    wsUbicProducto(registroPedido.getCodigo(),false);
                }break;
            case TAREA_IMPORTESDI:
                if(output.getExtra1()!=null){
                    ContentValues mapa=(ContentValues) output.getExtra1();
                    float importe=mapa.getAsFloat("total");
                    dlgTotal(importe);
                }else{
                    muestraMensaje("No se recuperaron datos del servidor",R.drawable.mensaje_error);
                }
                break;
            case TAREA_TRAE_ANDENES_SURT:
                if(output.getExito()){
                    ContentValues values=(ContentValues)output.getExtra1();
                    String andenes=values.getAsString("anexo");
                    dlgAndenes(andenes);
                }else{
                    muestraMensaje(Libreria.tieneInformacion(output.getMensaje()) ? output.getMensaje() :"No hay andenes disponibles",R.drawable.mensaje_error);
                }
                break;
            case TAREA_GUARDA_ANDEN_SURT:
                if(output.getExito()){
                    pide_anden = false;
                    terminaDI(esUltimo);
                }else{
                    muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                }
            default:
                super.Finish(output);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_PRODUCTOS_DI_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("result", false)) {
                    posicion_renglon = data.getIntExtra("renglon", 1);
                    traeRenglon(posicion_renglon);
                }
            }
        }else if (requestCode == LAUNCH_BULTOS) {
            if(resultCode == Activity.RESULT_OK){
                String elBulto = data.getStringExtra("bulto");
                System.out.println("Bulto que regresa "+elBulto);
                bulto = Libreria.tieneInformacion(elBulto) && !elBulto.equals(bulto) ? elBulto: bulto ;
                textoBulto();
            }
        }else if(requestCode == REQUEST_CODE){
            if(llamandproductos==0){
                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if(intentResult.getContents() != null ) {
                    wsBuscaprod(intentResult.getContents());
                }
                else
                    muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recibe_documento, menu);

        menu.getItem(0).setVisible(muestra_bulto && documento==16 && Libreria.tieneInformacion(ordenCompra));
        menu.getItem(1).setVisible(Libreria.tieneInformacion(ordenCompra) && Libreria.isNumeric(ordenCompra.replace(",","")) && (documento==16 || documento==17));
        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(muestra_bulto && documento==16);
        menu.getItem(4).setVisible(servicio.hayMenuUbicacion() && Libreria.isNumeric(ordenCompra));
        menu.getItem(5).setVisible(true);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(documento==14);
        menu.getItem(8).setVisible(false);
        menu.getItem(9).setVisible(edita_compra);

        menu.getItem(1).setTitle(documento==16 ? "Renglones por Surtir":"Renglones por Recibir");
        menu.getItem(3).setTitle("Contenedor a espera");
        menu.getItem(2).setTitle("Renglones capturados");
        menu.getItem(7).setChecked(con_impuestos);
        menu.getItem(4).setTitle("Asigna Ubicacion");
        menu.getItem(9).setTitle("Edita Total");
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.menuRecibe_listaContenedor:
                listaBultos();
                break;
            case R.id.menuRecibe_EnviaEspera:
                wsEnviaEspera(bulto);
                break;
            case R.id.menuRecibe_RecibidoFaltantes:
                listaPorSurtir();
                break;
            case R.id.menuRecibe_BorrarRenglon:
                borraDDIN();
                break;
            case R.id.menuRecibe_ListaSurtidos:
                listaCapturados();
                break;
            case R.id.menuRecibe_Impuestos:
                con_impuestos=!item.isChecked();
                item.setChecked(con_impuestos);
                break;
            case R.id.menuRecibe_Ubicacioon:
                if(registroPedido==null){
                    muestraMensaje("Sin pedido",R.drawable.mensaje_error);
                    return false;
                }
                dlgUbicacion("Asigna Ubicación al código "+registroPedido.getCodigo());
                break;
            case R.id.menuRecibe_Info:
                peticionWS(Enumeradores.Valores.TAREA_IMPORTESDI,"SQL","SQL",folioDi,"","");
                //imprimeprueba();
                //onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void muestraMensaje(String msj, int tipo) {
        super.muestraMensaje(msj, tipo);
        if(tipo==R.drawable.mensaje_error){
            reproduceAudio(R.raw.error);
        }else if(tipo==R.drawable.mensaje_exito){
            reproduceAudio(R.raw.exito);
        }
    }

    public void impresionBluet(ContentValues obj){
        BluetoothConnection selectedDevice = traeImpresora(nombre_impresora);
        ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

        String bultencab=obj.getAsString("bultotik");
        if(Libreria.tieneInformacion(bultencab))
            Libreria.imprimeSol(impresora,bultencab,imprime_espacios);
        String bultddindet=obj.getAsString("detallesbulto");
        if(Libreria.tieneInformacion(bultddindet))
            Libreria.imprimeSol(impresora,bultddindet,imprime_espacios);
        String impencab=obj.getAsString("encabtik");
        if(Libreria.tieneInformacion(impencab))
            Libreria.imprimeSol(impresora,impencab,imprime_espacios);
        String impddindet=obj.getAsString("prodstik");
        if(Libreria.tieneInformacion(impddindet))
            Libreria.imprimeSol(impresora,impddindet,imprime_espacios);
        try {
            new AsyncBluetoothEscPosPrint(this,lista_pedidos.equals(ordenCompra)).execute(impresora.Imprimir());
        } catch (/*ExecutionException | InterruptedException e*/ Exception e) {
            //e.printStackTrace();
            System.out.println(e);
        }
    }
    public void muestraProducto(boolean pLimpia){
        vistaProducto.setText("");
        vistaCodigo.setText("");
        vistaExst.setText("");
        vistaExtra1.setText("");
        vistaDispo.setText("");
        vistaSurt.setText("");
        vistaPiezas.setText("");
        editCant.setText("");
        editCaducado.setText("");
        editDanado.setText("");
        editNopedido.setText("");
        editCosSc.setText("");
        editFaltante.setText("");
        editCosto.setText("");
        vistaDivisa.setText("");
        cad_lotes = "";
        cad_ubis = "";
        vistaDivisa.setVisibility(View.GONE);

        if(!pLimpia && conProducto!=null){
            String cadena=conProducto.getAsString("info");
            vistaProducto.setText(cadena);
            vistaCodigo.setText(conProducto.getAsString("codigo"));
            vistaExst.setText("Soli="+conProducto.getAsString("cantsolic"));
            vistaDispo.setText("Dispo="+conProducto.getAsString("cantdispo"));
            vistaSurt.setText("Capt="+conProducto.getAsString("cantdi"));
            vistaPiezas.setText("Pzs="+conProducto.getAsString("empqpzs"));
            vistaExtra1.setText("Cant Bultos:"+conProducto.getAsString("cantbultos")+"+ "+conProducto.getAsString("cantbnocaptura")+" en cerrado");
            if(conProducto.getAsBoolean("pidecaduc")){
                vistaExtra1.setText("\nReq bulto:"+(Libreria.getBoolean(conProducto.getAsString("pidecaduc"))? "Si":"No")+" Bulto"+conProducto.getAsString("bultfolio"));
            }
            editCosto.setText(conProducto.getAsString("costo"));

            editCaducado.setText(Libreria.tieneInformacionFloat(conProducto.getAsString("cantcad"),0)+"");
            editDanado.setText(Libreria.tieneInformacionFloat(conProducto.getAsString("cantdan"),0)+"");
            editNopedido.setText(Libreria.tieneInformacionFloat(conProducto.getAsString("cantnop"),0)+"");
            editCosSc.setText(Libreria.tieneInformacionFloat(conProducto.getAsString("cantsin"),0)+"");
            editFaltante.setText(Libreria.tieneInformacionFloat(conProducto.getAsString("cantfa"),0)+"");

            vistaCodigo.setVisibility(View.GONE);
            vistaExst.setVisibility(View.GONE);
            vistaDispo.setVisibility(View.GONE);
            vistaSurt.setVisibility(View.GONE);
            vistaPiezas.setVisibility(View.GONE);
            vistaExtra1.setVisibility(View.GONE);
            if(Libreria.tieneInformacionEntero(conProducto.getAsString("divisa"),divisa_default)!=divisa_default && documento==14){
                vistaDivisa.setText(MessageFormat.format("Tipo cambio: ${0} Costo({2}/I):(${1} MXN)",divisa,Libreria.tieneInformacionFloat(conProducto.getAsString("costo"),0)*divisa,con_impuestos ? "C":"S"));
                vistaDivisa.setVisibility(View.VISIBLE);
            }
            if(registroPedido!=null){
                if(Libreria.tieneInformacion(registroPedido.getListaubiks())){
                    String []cad=registroPedido.getListaubiks().split(",");
                    ubicaid=0;
                    if(cad.length>0){
                        asignaUbi(cad[0]);
                    }
                }
            }
        }else{
            valor_divisa = divisa_default;
        }

    }

    public void validaCap(Integer pNivel){
        String cant=editCant.getText().toString();
        int accion=pNivel <2 ? 0:pNivel;
        boolean nuevo=true;
        if(!Libreria.tieneInformacion(cant)){
            muestraMensaje("No se capturo cantidad",R.drawable.mensaje_error);
            return ;
        }
        float cantidad=Float.parseFloat(cant);
        //System.out.println("cantidad usuarl"+cantidadUsual);
        if(cantidad>=cantidadUsual && cantidadUsual>0 && pNivel<1){
            muestraMensaje("La cantidad es mayor a la permitida",R.drawable.mensaje_error);
            //falta el dialogo para confirmar o para continuar 1
            excedido();
            return ;
        }
        if(conProducto==null){
            muestraMensaje("Producto no buscado",R.drawable.mensaje_error);
            return;
        }
        String cap=conProducto.getAsString("cantbultocaptura");
        String dicant=conProducto.getAsString("cantdi");
        nuevo = !Libreria.tieneInformacion(dicant);
        if(Libreria.tieneInformacionFloat(cap,0) > 0){
            //dialogo de accion
            if(pNivel<2){
                String piezas=conProducto.getAsString("empkpzs");
                float paquePiezas = Libreria.tieneInformacionFloat(piezas,1);
                cantc =cantidad * paquePiezas;
                String nocaptura=conProducto.getAsString("cantbnocaptura");
                float nocapt=Libreria.tieneInformacionFloat(nocaptura,0);
                String mensaje="Ya tienes {0} en contenedor actual\n"+
                        (nocapt>0 ? "({1} en otros contenedores)\n" : "")+
                        "¿Qué deseas hacer con: {2}?";
                dlgCarga(1,MessageFormat.format(mensaje, (new BigDecimal(cap)).setScale(1,BigDecimal.ROUND_HALF_DOWN),nocapt,cantc));
                return;
            }
        }else{
            cap = "0";
        }
        String soli=conProducto.getAsString("cantsolic");
        cantc=pNivel!=2 && cantc==0 ? cantidad : cantc;
        Float solicitado=Libreria.tieneInformacionFloat(soli,-1);
        if(solicitado>=0 && pNivel<4 && Libreria.isNumeric(ordenCompra)){

            Boolean excede=Libreria.getBoolean(conProducto.getAsString("autoexceso"));
            float calculo=Float.parseFloat(cap)+cantc+Libreria.tieneInformacionFloat(conProducto.getAsString("cantbnocaptura"),0);
            if(calculo>solicitado ){
                //muestraMensaje("No se puede exceder de lo solicitado",R.drawable.mensaje_error);
                String mensaje="Se ha excedido por {0} de lo solicitado {1} \n"+"¿Desea autorizar?";
                        //(excede ? "¿Desea autorizar?":"");
                //dlgCarga(excede ? 3 :2,MessageFormat.format(mensaje, (calculo-solicitado),solicitado));
                dlgAutorizacion(MessageFormat.format(mensaje, (calculo-solicitado),solicitado),excede);
                return;
            }
        }
        String cadicadp=conProducto.getAsString("pidecaduc");
        //dlgLotes(conProducto.getAsString("lotes"),cantc);
        if(Libreria.tieneInformacion(cadicadp) && Libreria.getBoolean(cadicadp) && pNivel<4){
            float calculo=Float.parseFloat(cap)+(cantc);
            dlgLotes(conProducto.getAsString("lotes"),calculo);
            return;
        }

        String ubik=conProducto.getAsString("pideubik");
        if(Libreria.tieneInformacion(ubik) && Libreria.getBoolean(ubik) && pNivel<5){
            float calculo=Float.parseFloat(cap)+(cantc);
            dlgUbiks(conProducto.getAsString("ubik"),calculo);
            return;
        }
        String directa=conProducto.getAsString("ubikdirecta");
        if(Libreria.tieneInformacion(directa) && Libreria.getBoolean(directa) && pNivel<5){
            cad_ubis = "";
            float calculo=Float.parseFloat(cap)+(cantc);
            String ubicaciones=conProducto.getAsString("ubik");
            String los_Lotes[] = ubicaciones.replace("|",";").split(";");
            String el_renglon[] = los_Lotes[0].split(",");
            cad_ubis = Libreria.tieneInformacionEntero(el_renglon[0],-1) +","+ calculo;
            pNivel = 5;
        }

        accion = nuevo ? 0 : 2 ;

        wsGuarda(accion);
    }

    public void dlgCarga(Integer pTipo,String pTexto){
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_acciones);
        dialog.setCancelable(false);

        ((TextView)dialog.findViewById(R.id.txtTitulo)).setText(pTexto);
        Button suma=dialog.findViewById(R.id.btnSuma);
        Button resta=dialog.findViewById(R.id.btnResta);
        Button reemplaza=dialog.findViewById(R.id.btnReemplaza);
        Button ignora=dialog.findViewById(R.id.btnIgnora);
        suma.setTag(dialog);
        String cant=conProducto.getAsString("cantbultocaptura");
        float cantidad=Libreria.tieneInformacionFloat(cant,0);
        float opSuma=0,opResta=0,opReemplaza=0;
        if(pTipo==1){
            opSuma=cantidad+cantc;
            opResta=cantidad-cantc;
            opReemplaza=cantc;
        }
        int salida=pTipo==1 ? 2:4;
        suma.setOnClickListener(view -> {
            Dialog dlg=(Dialog)view.getTag();
            dlg.dismiss();
            //cantc=cantidad+cantc;
            validaCap(salida);
        });

        resta.setOnClickListener(view -> {
            dialog.dismiss();
            cantc=-cantc;
            validaCap(salida);
        });

        reemplaza.setOnClickListener(view -> {
            dialog.dismiss();
            cantc=cantc-cantidad;
            validaCap(salida);
        });

        ignora.setOnClickListener(view -> {
            muestraProducto(true);
            dialog.dismiss();
            editCodigo.requestFocus();
        });

        switch (pTipo){
            case 1:
                suma.setText(MessageFormat.format("SUMA (Quedaría {0})",opSuma));
                if(opResta<0){
                    resta.setVisibility(View.GONE);
                }
                resta.setText(MessageFormat.format("RESTA (Quedaría {0})",opResta));
                reemplaza.setText(MessageFormat.format("REEMPLAZA (Quedaría {0})",opReemplaza));
                ignora.setText(MessageFormat.format("IGNORA",cantidad));
                break;
            case 2:
                resta.setVisibility(View.GONE);
                reemplaza.setVisibility(View.GONE);
                suma.setVisibility(View.GONE);
                ignora.setText("Cancela");
                break;
            case 3:
                resta.setVisibility(View.GONE);
                reemplaza.setVisibility(View.GONE);
                suma.setText("Acepta");
                ignora.setText("Cancela");
                break;
        }

        //dialog.setOnCancelListener(dialogInterface -> editCodigo.requestFocus());
        dialog.show();
    }

    public void dlgLotes(String pLotes,float pCantidad){
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_lotes);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        dialog.getWindow().setLayout(width, height);

        //dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        dialog.setCancelable(false);//vistaProducto
        String titulo="Por capturar: {0}";
        TextView vistatitulo = dialog.findViewById(R.id.vistaProducto);
        TextView vistatotales = dialog.findViewById(R.id.vistaTotales);
        TextView vistaCaptura = dialog.findViewById(R.id.vistaCaptura);

        TextView vistaLote = dialog.findViewById(R.id.vistaLoteLote);
        TextView vistaCant = dialog.findViewById(R.id.vistaLoteCant);
        TextView vistaFecha = dialog.findViewById(R.id.vistaLoteFecha);

        TextView vistaMensaje = dialog.findViewById(R.id.vistaLoteMensaje);

        EditText editaLote = dialog.findViewById(R.id.editLoteLote);
        EditText editaCant = dialog.findViewById(R.id.editLoteCant);
        EditText editaFecha = dialog.findViewById(R.id.editLoteFecha);

        Button guardar = dialog.findViewById(R.id.btnGuarda);
        Button cerrar = dialog.findViewById(R.id.btnCerrar);
        Button agregar = dialog.findViewById(R.id.btnAgrega);

        ListView lista=dialog.findViewById(R.id.tablaLotes);//vistaTotales

        ArrayList<Generica> listado=new ArrayList();
        if(Libreria.tieneInformacion(pLotes)){
            String los_Lotes[]=pLotes.replace("|",";").split(";"),el_renglon[];
            Generica gen;
            Date felote;
            for(String renglon:los_Lotes){
                el_renglon=renglon.split(",");
                gen=new Generica(Libreria.tieneInformacionEntero(el_renglon[0],-1));
                gen.setTex1(el_renglon[1]);
                felote=Libreria.texto_to_fecha(el_renglon[2],"yyyy-MM-dd HH:mm");
                gen.setFec1(felote);
                gen.setTex2(Libreria.dateToString(felote,"ddMMyy"));
                gen.setDec1(new BigDecimal(el_renglon[3]).setScale(1,BigDecimal.ROUND_HALF_EVEN));
                gen.setDec2(el_renglon.length>4 ? new BigDecimal(el_renglon[4]).setScale(1,BigDecimal.ROUND_HALF_EVEN):BigDecimal.ZERO);
                gen.setDec3(gen.getDec2());
                listado.add(gen);
            }
        }
        guardar.setVisibility(View.GONE);
        VistaLotes contenedor=new VistaLotes(vistaCaptura,vistatitulo,agregar,editaFecha,editaLote,editaCant,guardar,vistaFecha,vistaLote,vistaCant);
        contenedor.setVmensaje(vistaMensaje);
        vistaCaptura.setTag("Capturados: {0}");
        vistatitulo.setTag(titulo);

        LoteAdapter lotead=new LoteAdapter(listado,this,pCantidad,contenedor);
        lista.setAdapter(lotead);
        float cap=lotead.sumacap();

        vistatotales.setText("Total en bulto:"+(pCantidad));
        vistatitulo.setText(MessageFormat.format(titulo,pCantidad-cap));
        vistaCaptura.setText(MessageFormat.format(vistaCaptura.getTag().toString(),cap));
        cerrar.setOnClickListener(view -> {muestraMensaje("No se guardó el registro",R.drawable.mensaje_error);dialog.dismiss();});
        guardar.setOnClickListener(view -> {
            cad_lotes=lotead.lotes();
            String nuevo=conProducto.getAsString("cantdi");
            wsGuarda(!Libreria.tieneInformacion(nuevo) ? 0 : 2);
            editaCant.setText("");
            editCodigo.requestFocus();
            dialog.dismiss();
        });
        editaCant.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                agregar.callOnClick();
            }
            return false;
        });
        agregar.setOnClickListener(view -> {
            String lote=editaLote.getText().toString();
            String cant=editaCant.getText().toString();
            String fecha=editaFecha.getText().toString();
            presentaMensaje(vistaMensaje,"");
            if(!Libreria.tieneInformacion(lote)){
                muestraMensaje("Código de lote requerido",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Código de lote requerido");
                return;
            }
            if(!Libreria.tieneInformacion(cant)){
                muestraMensaje("Cantidad no debe de estar vacío",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Cantidad no debe de estar vacío");
                return;
            }
            if(!Libreria.tieneInformacion(fecha) && !Libreria.validaFecha(fecha,fecha)){
                muestraMensaje("Fecha de lote inválido",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Fecha de lote inválido");
                return;
            }
            float total=0;//lotead.sumacap();
            boolean nuevo=true;
            Generica gene=new Generica(-1);
            for(Generica existe:lotead.getLista()){
                if(existe.getTex1().equals(lote)){
                    gene=existe;
                    nuevo=false;
                    total += Libreria.tieneInformacionFloat(cant,0);
                }else{
                    total += existe.getDec3().floatValue();
                }
            }
            if(nuevo){
                total += Libreria.tieneInformacionFloat(cant,0);
            }
            if(total>pCantidad){
                muestraMensaje("Cantidad no puede ser mayor a lo solicitado",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Cantidad no puede ser mayor a lo solicitado");
                return ;
            }
            String pFechamin=conProducto.getAsString("cadmin");
            Date mifecha = Libreria.texto_to_fecha(fecha,"ddMMyy");
            if(Libreria.tieneInformacion(pFechamin) && documento!=16){
                mifecha=Libreria.texto_to_fecha(fecha,"ddMMyy");
                if(mifecha == null){
                    muestraMensaje("Valor de fecha invalida",R.drawable.mensaje_error);
                    presentaMensaje(vistaMensaje,"Valor de fecha inválida");
                    return;
                }
                Date minimo=Libreria.texto_to_fecha(pFechamin,"yyyy-MM-dd'T'HH:mm");
                if(!mifecha.after(minimo)){
                    muestraMensaje("Fecha es inferior a la fecha mínima permitida ",R.drawable.mensaje_error);
                    presentaMensaje(vistaMensaje,"Fecha es inferior a la fecha mínima permitida");
                    String autocad=conProducto.getAsString("autocaduc");
                    if(!Libreria.getBoolean(autocad))
                        return;
                }
            }
            editaLote.setText("");
            editaCant.setText("");
            editaFecha.setText("");
            if(nuevo){
                gene.setTex1(lote);
                gene.setDec1(BigDecimal.ZERO);
                lotead.add(gene);
            }
            gene.setTex2(Libreria.dateToString(mifecha,"ddMMYY"));
            gene.setFec1(mifecha);
            gene.setDec2(new BigDecimal(cant));
            gene.setDec3(new BigDecimal(cant));
            //lotead.add(gene);
            lotead.actualizaCant();
            lotead.actualizaVista();
            lotead.notifyDataSetChanged();
        });
        lotead.actualizaVista();
        dialog.show();
    }

    public void dlgUbiks(String pLotes,float pCantidad){
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogoubiks);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        dialog.getWindow().setLayout(width, height);

        //dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        dialog.setCancelable(false);//vistaProducto
        String titulo="Por capturar: {0}";
        LinearLayout lyubic=dialog.findViewById(R.id.lyUbikubicacion);
        LinearLayout lycant=dialog.findViewById(R.id.lyUbikCant);
        TableLayout tabla = dialog.findViewById(R.id.ubikTabla);

        TextView vistatitulo = dialog.findViewById(R.id.vistaProducto);
        TextView vistatotales = dialog.findViewById(R.id.vistaTotales);
        TextView vistaCaptura = dialog.findViewById(R.id.vistaCaptura);//vistaUbikUbic
        TextView vistaUbicacion = dialog.findViewById(R.id.vistaUbikUbic);

        TextView vistaCant = dialog.findViewById(R.id.vistaUbikCant);
        TextView vistaMensaje = dialog.findViewById(R.id.vistaUbikMensaje);
        EditText editaCant = dialog.findViewById(R.id.editUbikCant);
        EditText editaUbic = dialog.findViewById(R.id.editUbikUbica);

        Button guardar = dialog.findViewById(R.id.btnGuarda);
        Button cerrar = dialog.findViewById(R.id.btnCerrar);
        Button agregar = dialog.findViewById(R.id.btnAgrega);
        Button busca = dialog.findViewById(R.id.btnBusca);

        vistaUbicacion.setText("");
        vistaUbicacion.setTag("{0}({1} dentro)");

        tabla.setVisibility(View.VISIBLE);
        //lyubic.setVisibility(View.VISIBLE);
        //lycant.setVisibility(View.GONE);

        tabla.removeAllViews();
        TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.DKGRAY);
        header.setGravity(Gravity.CENTER);
        TextView h1 = Libreria.columnH("Ubicacion",Color.WHITE,this);
        header.addView(h1);
        TextView h2 = Libreria.columnH("Cantidad",Color.WHITE,this);
        header.addView(h2);
        TextView h3 = Libreria.columnH("Capt",Color.WHITE,this);
        header.addView(h3);
        /*TextView h4 = Libreria.columnH("",Color.WHITE,this);
        header.addView(h4);*/
        tabla.addView(header);

        ArrayList<Generica> listado=new ArrayList();
        if(Libreria.tieneInformacion(pLotes)){
            String los_Lotes[]=pLotes.replace("|",";").split(";"),el_renglon[];
            Generica gen;
            for(String renglon:los_Lotes){
                el_renglon=renglon.split(",");
                gen=new Generica(Libreria.tieneInformacionEntero(el_renglon[0],-1));
                gen.setTex1(el_renglon[1]);
                gen.setTex2(el_renglon.length>4 ? el_renglon[4]:"");
                gen.setDec1(new BigDecimal(el_renglon[2]).setScale(1,BigDecimal.ROUND_HALF_EVEN));
                gen.setDec2(el_renglon.length>3 ? new BigDecimal(el_renglon[3]).setScale(1,BigDecimal.ROUND_HALF_EVEN):BigDecimal.ZERO);
                gen.setDec3(gen.getDec2());
                listado.add(gen);

                TableRow row = new TableRow(this);
                row.setGravity(Gravity.CENTER);
                if(this.ubicaid==gen.getId()){
                    row.setBackgroundColor(getResources().getColor(R.color.aristo_amarillo));
                }else{
                    row.setBackgroundColor(0);
                }
                TextView r1 = Libreria.columnInfo(gen.getTex1(),Color.BLACK,this);
                row.addView(r1);

                TextView r2 = Libreria.columnInfo(gen.getDec1()+"",Color.BLACK,this);
                r2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                row.addView(r2);

                TextView r3 = Libreria.columnInfo(gen.getDec2()+"",Color.BLACK,this);
                r3.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                row.addView(r3);

                /*ImageButton btnSeleccionar = new ImageButton(this);
                btnSeleccionar.setImageResource(R.drawable.check);
                btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
                btnSeleccionar.setPadding(10, 5, 10, 5);
                btnSeleccionar.setOnClickListener(view -> {
                    Generica gene = (Generica) view.getTag();
                    editaCant.setText("");
                    editaCant.setTag(gene);
                    //editaCant.clearFocus();
                    editaCant.requestFocus();
                    //editaCant.setSelectAllOnFocus(true);
                    vistaCant.setText(MessageFormat.format("Capt({0}):",gene.getDec2()));
                    vistaUbicacion.setText(MessageFormat.format(vistaUbicacion.getTag()+"",gene.getTex1(),gene.getDec2()));
                    //lyubic.setVisibility(View.GONE);
                    //lycant.setVisibility(View.VISIBLE);
                    row.setBackgroundColor(getResources().getColor(R.color.aristo_amarillo));
                    });
                btnSeleccionar.setTag(gen);
                row.addView(btnSeleccionar);*/
                row.setTag(gen);
                tabla.addView(row);
            }
        }
        guardar.setVisibility(View.GONE);

        vistaCaptura.setTag("Capturados: {0}");
        vistatitulo.setTag(titulo);


        float cap=sumaTabla(vistaMensaje,tabla);

        vistatotales.setText("Total en documento: "+(pCantidad));
        vistatitulo.setText(MessageFormat.format(titulo,pCantidad-cap));
        vistaCaptura.setText(MessageFormat.format(vistaCaptura.getTag().toString(),cap));
        cerrar.setOnClickListener(view -> {muestraMensaje("No se guardó el registro",R.drawable.mensaje_error);dialog.dismiss();});
        guardar.setOnClickListener(view -> {
            cad_ubis=traeUbik(vistaMensaje,tabla);//genera ubikaciones


            validaCap(6);
            editaCant.setText("");
            editCodigo.requestFocus();
            dialog.dismiss();
        });
        editaCant.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                agregar.callOnClick();
            }
            return false;
        });
        agregar.setOnClickListener(view -> {

            String cant=editaCant.getText().toString();

            presentaMensaje(vistaMensaje,"");

            if(!Libreria.tieneInformacion(cant)){
                muestraMensaje("Cantidad no debe de estar vacío",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Cantidad no debe de estar vacío");
                return;
            }
            if(editaCant.getTag()==null){
                muestraMensaje("Renglón no seleccionado",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Renglón no seleccionado");
                return;
            }
            float capt=Libreria.tieneInformacionFloat(cant,0);
            Generica edGen = (Generica) editaCant.getTag();
            float total=sumaTabla(vistaMensaje,tabla)+(capt-edGen.getDec2().floatValue());
            boolean nuevo=true;
            //sacar total
            if(capt>edGen.getDec1().floatValue()){
                muestraMensaje("Cantidad no puede ser mayor a la ubicación",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Cantidad no puede ser mayor a la ubicación");
                return;
            }
            if(total>pCantidad){
                muestraMensaje("Cantidad no puede ser mayor a lo solicitado",R.drawable.mensaje_error);
                presentaMensaje(vistaMensaje,"Cantidad no puede ser mayor a lo solicitado");
                return ;
            }
            agregarTabla(vistaMensaje,tabla,cant,edGen);

            editaCant.setText("");
            editaCant.setTag(null);
            float tocap=sumaTabla(vistaMensaje,tabla);
            vistatotales.setText("Total capturado"+tocap);
            String men=vistatitulo.getTag()+"";
            float resultado=pCantidad-tocap;
            if(resultado==0){
                vistatitulo.setText("Completo");
                vistatitulo.setBackgroundColor(this.getResources().getColor(R.color.colorExitoInsertado));
                guardar.setVisibility(View.VISIBLE);
                vistaCaptura.setText(MessageFormat.format(vistaCaptura.getTag().toString(),tocap));
            }else{
                vistatitulo.setText(MessageFormat.format(men,resultado));
                vistatitulo.setBackgroundColor(this.getResources().getColor(R.color.colorTextos));
                vistaCaptura.setText(MessageFormat.format(vistaCaptura.getTag().toString(),tocap));
                guardar.setVisibility(View.GONE);
                vistaUbicacion.setText("");
            }
            editaUbic.setText("");
            //lyubic.setVisibility(View.VISIBLE);
            //lycant.setVisibility(View.GONE);

            //lotead.notifyDataSetChanged();
        });
        busca.setOnClickListener(view -> {
         String ubicapt=editaUbic.getText().toString();
         if(!Libreria.tieneInformacion(ubicapt)){
             muestraMensaje("Escriba una ubicación ",R.drawable.mensaje_error);
             presentaMensaje(vistaMensaje,"Escriba una ubicación ");
             return ;
         }
         int hijos=tabla.getChildCount();
         TableRow row;Generica gen = null;
         boolean encontrado=false;
         for(int i=0;i<hijos;i++){
             row = (TableRow) tabla.getChildAt(i);
             gen = (Generica)row.getTag();
             if(gen!=null){
                 row.setBackgroundColor(0);
                 if(gen.getTex2().equals(ubicapt)){
                     encontrado=true;
                     row.setBackgroundColor(getResources().getColor(R.color.aristo_amarillo));
                     editaCant.setText("");
                     editaCant.setTag(gen);
                     vistaCant.setText(MessageFormat.format("Capt({0}):",gen.getDec2()));
                     vistaUbicacion.setText(MessageFormat.format(vistaUbicacion.getTag()+"",gen.getTex1(),gen.getDec2()));
                     //lyubic.setVisibility(View.GONE);
                     //lycant.setVisibility(View.VISIBLE);
                     editaCant.requestFocus();
                     //break;
                     //return;
                 }
             }
         }
        });
        editaUbic.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                busca.callOnClick();
                /*if(lycant.getVisibility()==View.VISIBLE){
                    editaCant.requestFocus();
                }*/
                return true;
            }
            return false;
        });
        dialog.show();
    }

    public void dlgCosto(){
        if(conProducto==null){
            muestraMensaje("Sin producto para ajustar",R.drawable.mensaje_error);
            return;
        }
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_ajusteoc);
        //dialog.setTitle("Ajusta Renglon de orden de compra");

        TextView vistaProducto = dialog.findViewById(R.id.vitaTitulo);
        TextView vistaCosto = dialog.findViewById(R.id.vistaCosto);
        TextView vistaTitulo = dialog.findViewById(R.id.vistaTitulo2);

        EditText editProducto = dialog.findViewById(R.id.editCosto);
        EditText editdCant = dialog.findViewById(R.id.editCant);
        EditText editCants = dialog.findViewById(R.id.editCantS);

        editProducto.setText(editCosto.getText().toString());
        editdCant.setText(editCant.getText().toString());
        editCants.setText(editCosSc.getText().toString());
        Button guarda = dialog.findViewById(R.id.btnGuarda);

        vistaProducto.setText(conProducto.getAsString("producto"));
        vistaTitulo.setText("Ajusta Renglón de orden de compra");
        vistaCosto.setText(MessageFormat.format("Costo ({0}/Imp):",con_impuestos ? "C":"S"));
        editdCant.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guarda.callOnClick();
            }
            return false;
        });

        guarda.setOnClickListener(view -> {
            String costo= editProducto.getText().toString();
            String cant= editdCant.getText().toString();
            Float cants= Libreria.tieneInformacionFloat(editCants.getText().toString(),0);
            if(Libreria.tieneInformacionFloat(costo,0)==0 || Libreria.tieneInformacionFloat(costo,0)==0){
                muestraMensaje("Costo y cantidad son requeridos",R.drawable.mensaje_error);
                return ;
            }
            wsInsertOC(costo,cant,cants+"");
            dialog.dismiss();
        });
        dialog.show();
    }

    public void dlgAutorizacion(String pMensaje,boolean pAutoriza){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_password);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.dPasswordTitle)).setText(pMensaje);

        final EditText contraseña = dialog.findViewById(R.id.dPasswordContraseña);
        Button aceptar = dialog.findViewById(R.id.dPasswordbtnAceptar);
        Button cancelar = dialog.findViewById(R.id.dPasswordbtnCancelar);
        TextView tituloPass = dialog.findViewById(R.id.dTitleEd);
        LinearLayout lineal = dialog.findViewById(R.id.lyPassword);
        tituloPass.setVisibility(pAutoriza ? View.GONE:View.VISIBLE);
        lineal.setVisibility(pAutoriza ? View.GONE:View.VISIBLE);
        contraseña.setText("");

        aceptar.setOnClickListener(v -> {
            hideKeyboard(contraseña);
            if(pAutoriza){
                validaCap(4);
                dialog.dismiss();
                return ;
            }
            if (Libreria.tieneInformacion(contraseña.getText().toString())) {
                ddinautoriza = contraseña.getText().toString();
                validaCap(4);
                dialog.dismiss();
            } else {
                muestraMensaje("Contraseña inválida", R.drawable.mensaje_error);
            }
        });
        aceptar.setText("Si");
        cancelar.setText("No");

        cancelar.setOnClickListener(view -> {dialog.dismiss();ddinautoriza="";});
        dialog.show();
    }

    public void dlgUbicacion(String pMensaje){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_password);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.dPasswordTitle)).setText(pMensaje);

        final EditText contraseña = dialog.findViewById(R.id.dPasswordContraseña);
        Button aceptar = dialog.findViewById(R.id.dPasswordbtnAceptar);
        Button cancelar = dialog.findViewById(R.id.dPasswordbtnCancelar);
        TextView tituloPass = dialog.findViewById(R.id.dTitleEd);
        LinearLayout lineal = dialog.findViewById(R.id.lyPassword);
        tituloPass.setVisibility(View.VISIBLE);

        contraseña.setText("");
        contraseña.setInputType(InputType.TYPE_CLASS_NUMBER);
        contraseña.setHint("101001...");
        contraseña.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        tituloPass.setText("Ubicacion:");
        contraseña.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                aceptar.callOnClick();
            }
            return false;
        });
        aceptar.setOnClickListener(v -> {
            hideKeyboard(contraseña);
            if (Libreria.tieneInformacion(contraseña.getText().toString())) {
                String ubicacion = contraseña.getText().toString();
                wsBuscaUbic(ubicacion);
                dialog.dismiss();
            } else {
                muestraMensaje("Ubicación inválida", R.drawable.mensaje_error);
            }
        });
        aceptar.setText("Buscar");
        cancelar.setText("Cancelar");

        cancelar.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void dlgSelecUbi(String pUbicaciones,String pProducto){
        if(Libreria.tieneInformacion(pUbicaciones)){
            Dialog dialog = new Dialog(this);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setContentView(R.layout.panel_lotes);
            //dialog.setCancelable(false);
            LinearLayout noutil=dialog.findViewById(R.id.paLayaout);
            LinearLayout general=dialog.findViewById(R.id.pnLayGen);
            TextView titulo=dialog.findViewById(R.id.pnLotes);
            TableLayout listado=dialog.findViewById(R.id.tablaPVLotes);
            listado.getLayoutParams().width= TableLayout.LayoutParams.MATCH_PARENT;
            noutil.setVisibility(View.GONE);
            general.setBackgroundResource(0);
            titulo.setText(pProducto);//"Ubicaciones de "+
            listado.removeAllViews();
            TableRow header = new TableRow(this);
            header.setBackgroundColor(Color.DKGRAY);
            header.setGravity(Gravity.CENTER);
            /*TextView h1 = new TextView(this);
            h1.setText("");
            h1.setTextColor(Color.WHITE);
            h1.setTextSize(22);
            h1.setPadding(10, 10, 10, 10);
            header.addView(h1);*/
            TextView h2 = new TextView(this);
            h2.setText("Ubicacion");
            h2.setTextSize(22);
            h2.setTextColor(Color.WHITE);
            h2.setPadding(10, 10, 10, 10);
            header.addView(h2);
            TextView h3 = new TextView(this);
            h3.setText("Cantidad");
            h3.setTextSize(22);
            h3.setTextColor(Color.WHITE);
            h3.setPadding(10, 10, 10, 10);
            header.addView(h3);
            listado.addView(header);
            String ubis[]=pUbicaciones.split(",");
            String content[];
            for(String ubicacion: ubis){
                content=ubicacion.split(Pattern.quote("|"));
                final TableRow row = new TableRow(this);
                row.setGravity(Gravity.CENTER);

                /*ImageButton btnSeleccionar = new ImageButton(this);
                btnSeleccionar.setImageResource(R.drawable.check);
                btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
                btnSeleccionar.setPadding(10, 10, 10, 10);
                btnSeleccionar.setOnClickListener(view -> {asignaUbi(ubicacion);dialog.dismiss();});
                btnSeleccionar.setTag(ubicacion);
                row.addView(btnSeleccionar);*/
                TextView r2 = new TextView(this);

                r2.setText(content[1]);
                r2.setTextSize(22);
                r2.setTextColor(Color.BLACK);
                r2.setPadding(10, 10, 10, 10);
                row.addView(r2);

                TextView r3 = new TextView(this);
                r3.setText(content[2]);
                r3.setTextSize(22);
                r3.setTextColor(Color.BLACK);
                r3.setPadding(10, 10, 10, 10);
                row.addView(r3);

                listado.addView(row);
            }
            dialog.show();
        }
    }

    public void dlgTotal(float pImporte){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_password);
        dialog.setCancelable(false);
        ((TextView)dialog.findViewById(R.id.dPasswordTitle)).setText("Total Calculado: $"+pImporte);

        final EditText contraseña = dialog.findViewById(R.id.dPasswordContraseña);
        Button aceptar = dialog.findViewById(R.id.dPasswordbtnAceptar);
        Button cancelar = dialog.findViewById(R.id.dPasswordbtnCancelar);
        TextView tituloPass = dialog.findViewById(R.id.dTitleEd);
        LinearLayout lineal = dialog.findViewById(R.id.lyPassword);
        tituloPass.setText("Nuevo actual:");
        tituloPass.setVisibility(View.VISIBLE);

        contraseña.setText("");
        contraseña.setInputType(InputType.TYPE_CLASS_NUMBER);
        contraseña.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        contraseña.requestFocus();
        contraseña.setHint("00.0");
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) contraseña.getLayoutParams();
        int margLeft = 20;
        int margTop = 30;
        int margRig = 20;
        int margBotto = 30;

        layoutParams.setMargins(margLeft, margTop, margRig, margBotto);
        contraseña.setLayoutParams(layoutParams);

        aceptar.setOnClickListener(v -> {
            hideKeyboard(contraseña);
            if (Libreria.tieneInformacion(contraseña.getText().toString())) {
                peticionWS(Enumeradores.Valores.TAREA_MOVIL_TOTAL,"SQL","SQL",folioDi,contraseña.getText().toString(),usuarioID);
                dialog.dismiss();
            } else {
                muestraMensaje("No se capturó valor", R.drawable.mensaje_error);
            }
        });
        aceptar.setText("Guardar");
        cancelar.setText("Cancelar");
        contraseña.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                aceptar.callOnClick();
            }
            return false;
        });

        cancelar.setOnClickListener(view -> {dialog.dismiss();});
        dialog.show();
    }

    public void dlgAndenes(String xmlAndenes){
        if(Libreria.tieneInformacion(xmlAndenes)){
            Dialog dialogo = new Dialog(this, R.style.Dialog);
            //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
            dialogo.setContentView(R.layout.dialogo_andenes);
            dialogo.setTitle("Andenes de salida");
            String[] andenes=xmlAndenes.replace("|",";").split(";");
            String[] datos;
            RadioGroup nuevo=dialogo.findViewById(R.id.radioAndenes);
            Button guarda=dialogo.findViewById(R.id.btnGuardaAnden);
            CheckBox auto=dialogo.findViewById(R.id.swAuto);
            TextView titulo=dialogo.findViewById(R.id.tv_anden_titulo);
            auto.setVisibility(View.GONE);
            //auto.setOnCheckedChangeListener((view,b)->onSeleccinaSwith(view,b));
            //ubicacion="";
            titulo.setText("Selecciona un Andén de Salida");
            for (String anden:andenes){
                datos=anden.split(",");
                RadioButton nuevoRadio = new RadioButton(this);
                LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                nuevoRadio.setLayoutParams(params);
                nuevoRadio.setText(datos[2]);
                nuevoRadio.setTag(anden);
                nuevoRadio.setOnClickListener(view -> onSeleccinaAnden(view));
                /*if(primer){
                    primer=false;
                    nuevoRadio.setChecked(true);
                    ubicacion =datos[0];
                }*/
                nuevo.addView(nuevoRadio);
            }
            ((RadioButton)nuevo.getChildAt(0)).setChecked(true);
            ((RadioButton)nuevo.getChildAt(0)).callOnClick();
            por_guardado = false;
            guarda.setOnClickListener(view -> {wsGuardaAnden();por_guardado=true;dialogo.dismiss();});
            dialogo.setOnDismissListener(dialogInterface -> {if(!por_guardado){recargaPendientes();}});
            dialogo.show();
        }
    }
    private boolean por_guardado ;

    public void onSeleccinaAnden(View v){
        boolean marcado = ((RadioButton) v).isChecked();
        if(marcado){
            String datos=v.getTag()+"";
            if(Libreria.tieneInformacion(datos)){
                String[] vector=datos.split(",");
                anden=vector[0];
            }else{
                muestraMensaje(datos,R.color.colorExitoInsertado);
            }
            muestraMensaje(datos,R.color.colorExitoInsertado);
        }
    }

    public void presentaMensaje(TextView pMensaje,String pCadena){
        if(Libreria.tieneInformacion(pCadena)){
            pMensaje.setText(pCadena);
            pMensaje.setBackgroundResource(R.drawable.mensaje_error);
            pMensaje.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            pMensaje.setText("");
            pMensaje.setTextColor(getResources().getColor(R.color.colorNegro));
            pMensaje.setBackgroundResource(R.color.colorWhite);
        }
    }

    public void wsBuscaprod(String pCodigo){
        if(Libreria.tieneInformacion(pCodigo)){
            ContentValues mapa=new ContentValues();
            mapa.put("foliodi",folioDi);
            mapa.put("codigo",pCodigo);
            mapa.put("tipodi",documento);
            mapa.put("usua",usuarioID);
            mapa.put("conimpuesto",con_impuestos);
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_TRAE_PROD_DDIN, "SQL", "SQL", xml, "", "");
        }else{
            muestraMensaje("El codigo esta vacio",R.drawable.mensaje_error);
        }
    }

    public void wsGuarda(Integer pAccion){
        if(conProducto!=null){
            ContentValues mapa=new ContentValues();
            mapa.put("folio",folioDi);
            mapa.put("codigo",conProducto.getAsString("codigo"));
            mapa.put("prod",conProducto.getAsString("prodid"));
            mapa.put("tipodi",documento);
            mapa.put("usuaid",usuarioID);
            mapa.put("accion",pAccion);
            mapa.put("pedido",Libreria.isNumeric(ordenCompra) ? ordenCompra:"");
            mapa.put("costo",Libreria.tieneInformacionFloat(editCosto.getText().toString(),0));
            mapa.put("cantc",cantc);
            mapa.put("cants",Libreria.tieneInformacionFloat(editCosSc.getText().toString(),0));
            mapa.put("cantd",Libreria.tieneInformacionFloat(editDanado.getText().toString(),0));
            mapa.put("cantv",Libreria.tieneInformacionFloat(editCaducado.getText().toString(),0));
            mapa.put("cantf",Libreria.tieneInformacionFloat(editFaltante.getText().toString(),0));
            mapa.put("cantnp",Libreria.tieneInformacionFloat(editNopedido.getText().toString(),0));
            mapa.put("foliobul",conProducto.getAsString("bultfolio"));
            mapa.put("lotes",cad_lotes);
            mapa.put("conimpuesto",con_impuestos);
            mapa.put("autopwd",ddinautoriza);
            mapa.put("ubica",cad_ubis);

            /*mapa.put("excedautoriz",cantc);//conProducto.getAsString("autoexceso")
            mapa.put("autoriza",usuarioID);
            mapa.put("cantbultos",1);
            mapa.put("notas","");
            mapa.put("exc_cc",false);
            mapa.put("fecadu","");*/
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            peticionWS(Enumeradores.Valores.TAREA_GUARDA_DETALLE_DI, "SQL", "SQL", xml, "", "");
        }else{
            muestraMensaje("Producto no encontrado",R.drawable.mensaje_error);
        }
    }

    private void wsSolicitado(int renglon){
        if(Libreria.isNumeric(ordenCompra.replace(",",""))){
            peticionWS(TAREA_CARGA_PEDIDO, "SQL", "SQL", usuarioID, String.valueOf(renglon),lista_pedidos.equals(ordenCompra)  ? ordenCompra:lista_pedidos);
        }
    }

    private void wsGuardaAnden(){
        if(!Libreria.tieneInformacion(anden)){
            muestraMensaje("No se ha seleccionado andén",R.drawable.mensaje_error);
            return;
        }
        peticionWS(TAREA_GUARDA_ANDEN_SURT, "SQL", "SQL", folioDi,anden,"");
    }

    private void wsEnviaEspera(String pFolio){
        if(!Libreria.tieneInformacion(pFolio)){
            muestraMensaje("El folio de bulto es inválido",R.drawable.mensaje_error);
            return ;
        }
        peticionWS(Enumeradores.Valores.TAREA_ENVIA_A_ESPERA, "SQL", "SQL", pFolio, "", "");

    }

    private void wsBorraDDin(){
        String ddin =conProducto.getAsString("ddinid");
        peticionWS(TAREA_BORRAR_RENGLON, "SQL", "SQL", ddin, "", "");
    }

    private void wsInsertOC(String pCosto,String pCant,String pCants){
        /**/
        ContentValues mapa=new ContentValues();
        mapa.put("codigo",conProducto.getAsString("codigo"));
        mapa.put("orcoid",ordenCompra);
        mapa.put("cant",pCant);
        mapa.put("cants",pCants);
        mapa.put("usua",usuarioID);
        mapa.put("costo",pCosto);
        mapa.put("conimpuesto",con_impuestos);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_AJUSTA_OC,"SQL","SQL",xml,"","");
    }

    private void traeRenglon(int renglon){
        registroPedido = servicio.getRenglon(renglon);
        //System.out.println("Pedidos:"+ordenCompra);
        if(registroPedido.getNumrenglon() != 0 && ordenCompra!=null && Libreria.isNumeric(ordenCompra.replace(",",""))) {
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.VISIBLE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.VISIBLE);

            float cantidadPedida = registroPedido.getCantpedida();
            renglon = registroPedido.getNumrenglon();

            vistaCantidadSur.setText(MessageFormat.format("Surte: {0}", cantidadPedida));
            vistaProductoSur.setText(registroPedido.getProducto());
            vistaCodigoSur.setText(registroPedido.getCodigo());
            String ubicaciones[]=Libreria.tieneInformacion(registroPedido.getListaubiks()) ? registroPedido.getListaubiks().split(","):new String[]{""};
            boolean ubis= ubicaciones.length>0 ;
            vistaUbicacionSur.setText(MessageFormat.format("{0}UB: {1}", (ubis ? "*":""),registroPedido.getUbicacion()));
            vistaRenglon.setText(MessageFormat.format("Renglón: {0} de {1}", renglon,registroPedido.getNumrengs()));
            if(ubis){
                vistaUbicacionSur.setOnClickListener(view -> dlgSelecUbi(registroPedido.getListaubiks(),registroPedido.getProducto()));
            }
            if (Libreria.tieneInformacion(registroPedido.getNotas())) {
                vistaNotasSur.setVisibility(View.VISIBLE);
                vistaNotasSur.setText("Notas: " + registroPedido.getNotas());
                vistaNotasSur.setTextColor(Color.RED);
            } else
                vistaNotasSur.setVisibility(View.GONE);

            if (documento == 16 && registroPedido!=null && Libreria.tieneInformacion(registroPedido.getDcinfolio())){
                folioDi = registroPedido.getDcinfolio();
                ordenCompra = registroPedido.getPediid();
                generaTitulo();
            }
            muestraProducto(true);
        }
        else{
            findViewById(R.id.LinearRecibeDocumentoInfo).setVisibility(View.GONE);
            findViewById(R.id.linearRecibeDocumentoRenglon).setVisibility(View.GONE);
        }
    }

    public void cierraDialogo(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cerrar Contenedor");
        dialog.setMessage("¿Seguro que quieres finalizar el contenedor?");
        dialog.setPositiveButton("Finalizar", (dialogInterface, i) -> peticionWS(TAREA_CIERRA_BULTO, "SQL", "SQL", bulto, "", ""));
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
        dialog.show();
    }

    public void terminaDI(boolean pUltimo){
        esUltimo = pUltimo;
        if(pide_anden  && (documento==16)){
            wsPideAndenes();
            return;
        }
        Dialog dialogo = new Dialog(this);
        //Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialogo.setContentView(R.layout.dialogo_confirmacion);
        //dialog.setCancelable(false);
        TextView titulo=dialogo.findViewById(R.id.cfm_titulo);
        TextView mensaje=dialogo.findViewById(R.id.cfm_men);
        TextView aviso=dialogo.findViewById(R.id.cfm_aviso);

        Button aceptar=dialogo.findViewById(R.id.cfm_but_acepta);
        Button cancelar=dialogo.findViewById(R.id.cfm_but_cancelar);
        int pendientes = 0;
        if(Libreria.isNumeric(ordenCompra) && !pUltimo){
            pendientes=servicio.getRenglones(ordenCompra).size();
            System.out.println("pendientes  "+pendientes);
        }
        titulo.setText("Finalizar documento I*" + folioDi.substring(folioDi.length() - 4) + "(" + ordenCompra + ")");
        titulo.setTextColor(getResources().getColor(R.color.colorNegro));

        mensaje.setText("¿Seguro que quieres finalizar el documento?");
        if(pendientes>0){
            aviso.setText("Faltante de surtir "+pendientes);
            aviso.setTextColor(getResources().getColor(R.color.fondoError));
        }else{
            aviso.setVisibility(View.GONE);
        }
        aceptar.setOnClickListener(view -> {wsFinaliza();dialogo.dismiss();});
        cancelar.setOnClickListener(view -> {
            RenglonEnvio renglon=servicio.getRenglon(0);
            if(renglon!=null || !lista_pedidos.equals(ordenCompra))
                recargaPendientes();
            dialogo.dismiss();
        });
        dialogo.show();

        /*final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Finalizar documento I*" + folioDi.substring(folioDi.length() - 4) + "(" + ordenCompra + ")");
        dialog.setMessage("¿Seguro que quieres finalizar el documento?");
        dialog.setPositiveButton("Finalizar", (dialogInterface, i) -> {
            if(pUltimo)
                wsFinaliza();
            else
                tienePendientes();});
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            RenglonEnvio renglon=servicio.getRenglon(0);
            if(renglon!=null || !lista_pedidos.equals(ordenCompra))
                recargaPendientes();
        });
        dialog.show();*/
    }

    public void tienePendientes(){
        int pendientes=0;
        if(Libreria.isNumeric(ordenCompra)){
            pendientes=servicio.getRenglones(ordenCompra).size();
        }
        if(pendientes==0){
            wsFinaliza();
        }else{
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Documento I*" + folioDi.substring(folioDi.length() - 4) + "(" + ordenCompra + ")");
            dialog.setMessage("Tienes "+pendientes+" renglones pendientes\n¿Seguro de finalizar el documento?");
            dialog.setPositiveButton("Si", (dialogInterface, i) -> wsFinaliza());
            dialog.setNegativeButton("No", (dialogInterface, i) -> {
                if(!lista_pedidos.equals(ordenCompra))
                    recargaPendientes();
            });
            dialog.show();
        }
    }

    public void wsPideAndenes(){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_ANDENES_SURT, "SQL", "SQL", folioDi, "", "");
    }

    public void wsFinaliza(){
        peticionWS(Enumeradores.Valores.TAREA_FINALIZA_DI, "", "SQL", folioDi, "0", "");
    }

    public void wsUbicProducto(String producto, boolean lleno){
        ContentValues mapa=new ContentValues();
        mapa.put("espaid",nuevo_espacio);
        mapa.put("codigo",registroPedido.getCodigo());
        mapa.put("accion",1);
        mapa.put("usuario",usuarioID);
        mapa.put("lleno",lleno);
        String xmlsalida=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(TAREA_UBICA_PRODUCTO, "SQL", "SQL", xmlsalida, "", "");
    }

    public void wsBuscaUbic(String pUbicacion){
        if(Libreria.tieneInformacion(pUbicacion)){
            peticionWS(TAREA_BUSCA_UBICACION, "SQL", "SQL", pUbicacion, "", "");
        }else{
            muestraMensaje("La ubicacion esta vacio",R.drawable.mensaje_error);
        }
    }

    public void excedido(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("La cantidad usual es mayor a la permitida ("+cantidadUsual+")");
        dialog.setMessage("¿Seguro que quieres continuar?");
        dialog.setPositiveButton("Si", (dialogInterface, i) -> validaCap(1));
        dialog.setNegativeButton("No", (dialogInterface, i) -> {});
        dialog.show();
    }

    public void borraDDIN(){
        if(conProducto == null){
            muestraMensaje("Producto vacío o no encontrado",R.drawable.mensaje_error);
            return;
        }
        String ddin=conProducto.getAsString("ddinid");
        if(Libreria.tieneInformacionEntero(ddin,0)==0){
            muestraMensaje("El producto no se ha capturado",R.drawable.mensaje_error);
            return ;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Se tiene el producto" + conProducto.getAsString("producto") );
        dialog.setMessage("¿Seguro que quieres borrar el renglón?");
        dialog.setPositiveButton("Si", (dialogInterface, i) -> wsBorraDDin());
        dialog.setNegativeButton("No", (dialogInterface, i) -> {});
        dialog.show();
    }

    public void listaBultos(){
        Intent intent = new Intent(this, Bultos.class);
        intent.putExtra("documento", documento);
        intent.putExtra("OC", ordenCompra);
        intent.putExtra("foliodi", lista_pedidos.equals(ordenCompra) ? folioDi :"00000");
        intent.putExtra("prov/suc", provedorSucursal);
        intent.putExtra("result", false);
        intent.putExtra("pedidos", lista_pedidos);
        if (bulto != null && !bulto.isEmpty())
            intent.putExtra("bulto", bulto);
        //startActivity(intent);
        startActivityForResult(intent, LAUNCH_BULTOS);
    }

    public void listaPorSurtir(){
        Intent intent = new Intent(this, ProductosDI.class);
        intent.putExtra("documento",documento);
        intent.putExtra("OC", ordenCompra);
        intent.putExtra("foliodi", lista_pedidos.equals(ordenCompra) ? folioDi:"00000");
        intent.putExtra("prov/suc", provedorSucursal);
        intent.putExtra("result", false);
        intent.putExtra("pedidos", lista_pedidos);
        if (bulto != null && !bulto.isEmpty())
            intent.putExtra("bulto", bulto);
        if(documento == 16) {
            intent.putExtra("muestraRegistros", ordenCompra.startsWith("S"));
            intent.putExtra("result", true);
        }else if(documento == 17) {
            intent.putExtra("muestraRegistros", false);
            intent.putExtra("result", true);
            intent.putExtra("pedidos", lista_pedidos);
        }
        llamandproductos=-1;
        startActivityForResult(intent, LAUNCH_PRODUCTOS_DI_ACTIVITY);
    }

    public void listaCapturados(){
        Intent intent = new Intent(this, ProductosDI.class);
        intent.putExtra("documento", documento);
        intent.putExtra("OC", ordenCompra);
        intent.putExtra("foliodi", lista_pedidos.equals(ordenCompra) ? folioDi:"00000");
        intent.putExtra("prov/suc", provedorSucursal);
        intent.putExtra("result", false);
        intent.putExtra("pedidos", lista_pedidos);
        if (bulto != null && !bulto.isEmpty())
            intent.putExtra("bulto", bulto);
        intent.putExtra("muestraRegistros", true);
        //startActivityForResult(intent, LAUNCH_PRODUCTOS_DI_ACTIVITY);
        startActivity(intent);
    }

    private boolean enterCant(View view,int i,KeyEvent keyEvent){
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
            cantc=0;
            validaCap(0);
        }
        return false;
    }

    private void textoBulto(){
        String concat = cierraBulto.getTag().toString();
        String text="";
        if(Libreria.tieneInformacion(bulto)){
            text=concat+"\n{0}";
            concat=MessageFormat.format(text,bulto.substring(0,1)+"*"+bulto.substring(bulto.length()-3,bulto.length()));
        }
        cierraBulto.setText(concat);
    }

    private void actualizaPedido(){
        if(!Libreria.tieneInformacion(lista_pedidos) || registroPedido==null){
            return ;
        }
        String pedidos[]=lista_pedidos.split(",");
        String nuevo_lista="";
        for(String pedi:pedidos){
            if(!pedi.equals(registroPedido.getPediid())){
                nuevo_lista += pedi+",";
            }
        }
        lista_pedidos=nuevo_lista.substring(0,nuevo_lista.length()-1);
        System.out.println("nueva lista "+lista_pedidos);
    }

    private void recargaPendientes(){
        servicio.borraDatosTabla("renglon");
        posicion_renglon = (registroPedido!=null && registroPedido.getNumrengs()==posicion_renglon) ? posicion_renglon-1:posicion_renglon;
        posicion_renglon = posicion_renglon<=0 ? 1 : posicion_renglon;
        wsSolicitado(0);
    }

    private void generaTitulo(){
        //String folio =folioDi.substring(0,1)+"*"+folioDi.substring(folioDi.length()-3,folioDi.length());
        String oc="";
        if(documento!=17 && Libreria.isNumeric(ordenCompra)){
            System.out.println(folioventa + "--" +ordenCompra);
            boolean hayvent=Libreria.tieneInformacion(folioventa) && Libreria.tieneInformacionEntero(ordenCompra,0)<0;
            int largo=hayvent ? folioventa.length() : ordenCompra.length();
            String cadena = hayvent ? folioventa : ordenCompra;
            String precad =documento==14 ? "OC*":(hayvent ? "C*":"P*");
            oc=(precad) + ""+(largo>4 ? cadena.substring(largo-4): cadena)+",";
        }
        String elTitulo=servicio.traeTitulo(folioDi,provedorSucursal,oc);//oc+"Folio: " + folio + " " + provedorSucursal + ", " + usuario;
        actualizaToolbar(elTitulo);
    }

    private void asignaUbi(String pCadena){
        if(Libreria.tieneInformacion(pCadena)){
            String []defa=pCadena.split(Pattern.quote("|"));
            ubicaid = Libreria.tieneInformacionEntero(defa[0],0);
        }
    }

    private float sumaTabla(TextView pMensaje,TableLayout pTabla){
        float suma = 0;
        try{
            if(pTabla==null){
                presentaMensaje(pMensaje,"Tabla vacía");
                return 0;
            }
            int hijos=pTabla.getChildCount();
            TableRow row;TextView cap;String info;
            for(int pos=1;pos<=hijos;pos++){
                row =(TableRow) pTabla.getChildAt(pos);
                cap =(TextView) row.getChildAt(2);
                info=cap.getText().toString();
                suma +=Libreria.tieneInformacionFloat(info,0);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return suma;
    }

    private void agregarTabla(TextView pMensaje,TableLayout pTabla,String pCapt,Generica pGene){
        float suma = 0;
        try{
            if(pTabla==null){
                presentaMensaje(pMensaje,"Tabla vacía");
                return ;
            }
            if(!Libreria.tieneInformacion(pCapt)){
                presentaMensaje(pMensaje,"Cantidad capturada esta vacía");
                return ;
            }
            int hijos=pTabla.getChildCount();
            TableRow row;TextView cap;String info;Generica gene;
            for(int pos=0;pos<=hijos;pos++){
                row =(TableRow) pTabla.getChildAt(pos);
                gene=(Generica) row.getTag();
                if(gene!=null && gene.getId()==pGene.getId()){
                    cap =(TextView) row.getChildAt(2);
                    cap.setText(pCapt);
                    gene.setDec2(new BigDecimal(pCapt));
                    gene.setDec3(new BigDecimal(pCapt));
                    row.setTag(gene);
                    return;
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return ;
    }

    private String traeUbik(TextView pMensaje,TableLayout pTabla){
        String retorno = "";
        try{
            if(pTabla==null){
                presentaMensaje(pMensaje,"Tabla vacía");
                return "";
            }
            int hijos=pTabla.getChildCount();
            TableRow row;TextView cap;String info;Generica gen;
            for(int pos=1;pos<=hijos;pos++){
                row =(TableRow) pTabla.getChildAt(pos);
                gen =(Generica) row.getTag();
                //info=cap.getText().toString();
                if( gen.getDec2().compareTo(BigDecimal.ZERO)>0){
                    retorno +=gen.getId()+","+gen.getDec2()+";";
                }
            }
            if(Libreria.tieneInformacion(retorno)){
                retorno=retorno.substring(0,retorno.length()-1);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return retorno;
    }

    public int getDocumento() {
        return documento;
    }

    public void imprimeprueba(){
        Dialog d = new Dialog(this);
        if(Build.VERSION.SDK_INT >= 31){
            if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                d.setContentView(R.layout.dial_no_permiso);
                Button btn_ok = d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
            }else{
                BluetoothConnection selectedDevice = traeImpresora(nombre_impresora);
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
                Libreria.imprimeSol(impresora,"Hola es una prueba,T1w|otro ;renglon,T2|75001476,**",imprime_espacios);
                try {
                    new AsyncBluetoothEscPosPrint(this,true).execute(impresora.Imprimir());
                } catch (/*ExecutionException | InterruptedException e*/ Exception e) {
                    //e.printStackTrace();
                    System.out.println(e);
                }
            }
        }else{
            BluetoothConnection selectedDevice = traeImpresora(nombre_impresora);
            ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
            Libreria.imprimeSol(impresora,"Hola es una prueba,T1w|otro ;renglon,T2|75001476,**",imprime_espacios);
            try {
                new AsyncBluetoothEscPosPrint(this,true).execute(impresora.Imprimir());
            } catch (/*ExecutionException | InterruptedException e*/ Exception e) {
                //e.printStackTrace();
                System.out.println(e);
            }
        }

    }
}