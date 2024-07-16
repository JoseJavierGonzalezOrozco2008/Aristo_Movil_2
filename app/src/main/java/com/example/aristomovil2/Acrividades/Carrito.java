package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.facade.Estatutos.TABLA_GENERICA;
import static com.example.aristomovil2.facade.Estatutos.TABLA_RENGLON;
import static com.example.aristomovil2.facade.Estatutos.TABLA_RENGLONES;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Inventario;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ClienteAdapter;
import com.example.aristomovil2.adapters.CuentaAdapter;
import com.example.aristomovil2.adapters.DcarritoAdapter;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.ListaProdsAdapter;
import com.example.aristomovil2.modelos.Cuenta;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Carrito extends ActividadBase {
    private String v_vntafolio,v_nombrecliente,v_nombre_impresora,v_ticket,v_titulo,v_notas,v_fereq,v_cuentanombre;
    private Integer v_estacion,v_tipovnta,v_ultprod,v_cliente,v_imprime_espacios,v_cantidadUsual,v_visibleF,v_cuenta;
    private Double v_importe,v_retiimporte;
    private Boolean v_metpago,v_granel,v_tieneCredito,v_virtuales,v_vertouch,v_puedeCobrar,v_registrar,v_mostrar;
    private ListView v_carrito,v_listaclientes;
    private GridView gridProds;
    private Generica v_default;
    private TextView v_Total,v_TvCuenta;
    private EditText v_codigo;
    private List<Generica> v_clientes;
    private ClienteAdapter v_Adacliente;
    private AlertDialog v_dlgCliente;
    private Menu v_menu;
    private List<Generica> renglonesGen;
    private Button v_F2,v_F0;
    //v_puedeCobrar
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultado = result.getData();
                Integer actividad = resultado!=null ? resultado.getIntExtra("actividad",0) : 0;
                switch(actividad){
                    case 1://cobro
                        Boolean pideretiro = resultado.getBooleanExtra("deberetirar",false);
                        String vntafolio = resultado.getStringExtra("vntafolio");
                        vntaLimpia();
                        if(Libreria.tieneInformacion(vntafolio)){
                            traeVnta( vntafolio);
                        }
                        if(pideretiro){
                            dlgRetiroParcial();
                        }
                        break;
                    case 2://buscar prod
                        String codigo = resultado.getStringExtra("codigo");
                        if(Libreria.tieneInformacion(codigo)){
                            wsLineaCaptura2(codigo);
                        }
                        break;
                    case 3:
                        String alias = resultado.getStringExtra("alias");
                        if(Libreria.tieneInformacion(alias)){
                            buscarCliente(alias);
                        }
                        break;
                    default:
                        muestraMensaje("Sin resultados obtenidos",R.drawable.mensaje_warning);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        cambio();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_carrito);
        cambio();
    }


    public void cambio(){
        inicializarActividad2("Folio Cliente Tipo","");
        SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        v_tipovnta = sharedPreferences.getInt("tipoVenta", 48);
        v_estacion = sharedPreferences.getInt("estaid", 0);
        v_cliente = sharedPreferences.getInt("clientedefault",-1);
        v_nombrecliente = sharedPreferences.getString("nomCliente", "Publico en general");
        v_metpago = preferences.getBoolean("ventaCredito", false);
        v_vertouch = sharedPreferences.getBoolean("vertouch", false);
        v_imprime_espacios = sharedPreferences.getInt("espacios", 3);
        v_visibleF = sharedPreferences.getInt("muestraf", 3);
        v_nombre_impresora = preferences.getString("impresora", "Predeterminada");
        v_puedeCobrar = sharedPreferences.getBoolean("puedecobrar", false);
        v_cantidadUsual = sharedPreferences.getInt("cantidadusualvnta", 1000);
        v_registrar = sharedPreferences.getBoolean("rompe", false);
        v_vntafolio = "Nueva";
        v_ticket = "";
        v_titulo = "";
        v_notas = "";
        v_cuentanombre = "";
        v_cuenta = 0;
        v_ultprod = 0;
        v_mostrar  = false;
        v_fereq = "" ;
        v_default = new Generica(0);
        v_default.setEnt1(v_cliente);
        v_default.setEnt2(v_tipovnta);
        v_default.setEnt3(v_cuenta);
        v_default.setTex1(v_nombrecliente);
        v_default.setTex2(v_vntafolio);
        v_default.setTex3(v_titulo);
        v_default.setTex4(v_notas);
        v_default.setTex5(v_fereq);
        v_default.setLog1(v_metpago);
        v_default.setLog2(false);
        v_default.setLog3(false);
        v_default.setLog4(v_mostrar);
        v_virtuales = v_default.getLog3();
        v_retiimporte = 1000.0;
        v_tieneCredito = v_default.getLog2();

        v_codigo = findViewById(R.id.vntaCodigo);
        ImageButton enter = findViewById(R.id.vntaEnter);
        v_F2 = findViewById(R.id.vntaF2);
        v_F0 = findViewById(R.id.vntaF0);
        Button f1 = findViewById(R.id.vntaF1);
        Button f3 = findViewById(R.id.vntaF3);
        Button f4 = findViewById(R.id.vntaF4);
        Button f5 = findViewById(R.id.vntaF5);
        Button f6 = findViewById(R.id.vntaF6);
        Button f7 = findViewById(R.id.vntaF7);
        Button f8 = findViewById(R.id.vntaF8);
        Button f9 = findViewById(R.id.vntaF9);
        Button f10 = findViewById(R.id.vntaF10);
        Button f11 = findViewById(R.id.vntaF11);
        Button f12 = findViewById(R.id.vntaF12);
        v_Total = findViewById(R.id.vntaTotal);
        v_carrito = findViewById(R.id.vntacarrito);
        ImageButton escaner= findViewById(R.id.vntaBarcode);

        escaner.setOnClickListener(view -> {barcodeEscaner();});

        v_Total.setText("$0.0");
        v_granel = false;
        v_codigo.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                String captura = v_codigo.getText().toString();
                wsLineaCaptura2(captura);
                v_codigo.setText("");
            }
            return false;
        });
        v_F2.setOnClickListener(accion);
        v_F0.setOnClickListener(accion);
        f1.setOnClickListener(accion);
        f3.setOnClickListener(accion);
        f11.setOnClickListener(accion);
        enter.setOnClickListener(view -> {
            String captura = v_codigo.getText().toString();
            wsLineaCaptura2(captura);
            v_codigo.setText("");
            v_codigo.requestFocus();
        });
        if(f4 != null){
            f4.setOnClickListener(accion);
            f5.setOnClickListener(accion);
            f6.setOnClickListener(accion);
            f7.setOnClickListener(accion);
            f8.setOnClickListener(accion);
            f9.setOnClickListener(accion);
            f10.setOnClickListener(accion);
            f12.setOnClickListener(accion);
        }
        switch (v_visibleF){
            case 3:
                f3.setVisibility(View.VISIBLE);
                break;
            case 11:
                f11.setVisibility(View.VISIBLE);
                break;
        }
        v_F2.setVisibility(View.GONE);
        v_F0.setVisibility(View.GONE);
        v_codigo.requestFocus();
        gridProds = findViewById(R.id.listPrueba);
        gridProds.setVisibility(v_vertouch ? View.VISIBLE : View.GONE);
        wsBqdaProd();
        colocaTitulo();
        traeUltVnta();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult.getContents() != null ) {
                wsLineaCaptura2(intentResult.getContents());
            }else
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Ws">
    public void wsLineaCaptura(String pCodigo){
        if(!Libreria.tieneInformacion(pCodigo)){
            dlgMensajeError("Captura un codigo",R.drawable.mensaje_warning2);
            return;
        }

        String xml = Libreria.xmlInsertVenta(v_estacion,usuarioID,pCodigo.trim(),v_cliente,v_vntafolio.equalsIgnoreCase(v_default.getTex2()) ? "":v_vntafolio,v_ultprod==null ? 0 : v_ultprod,v_tipovnta,"",v_metpago);
        peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL", xml,v_vntafolio==v_default.getTex2() ? "":v_vntafolio,"");
    }

    private void wsBqdaProd(){
        renglonesGen = servicio.traeProductosVnta();
        if(gridProds != null && renglonesGen != null && !renglonesGen.isEmpty()){
            ListaProdsAdapter adapter = new ListaProdsAdapter(this, renglonesGen,this,v_estacion,usuarioID,v_cliente,v_vntafolio,v_ultprod,v_tipovnta,v_metpago);
            gridProds.setAdapter(adapter);
        }
    }

    private boolean wsRetiroParcial(String pCantidad,String pAuto){
        if(!(Libreria.tieneInformacion(pCantidad) && Libreria.isNumeric(pCantidad))){
            dlgMensajeError("Necesita capturar una cantidad",R.drawable.mensaje_warning2);
            return false;
        }
        if(!Libreria.tieneInformacion(pAuto)){
            dlgMensajeError("Requiere capturar su clave numerica ",R.drawable.mensaje_warning2);
            return false;
        }
        ContentValues mapa = new ContentValues();
        mapa.put("tipo", 71);
        mapa.put("conceptoid", 1);
        mapa.put("formapago", 89);
        mapa.put("impuesto", 0);
        mapa.put("cantidad", pCantidad);
        mapa.put("reqauto", true);
        mapa.put("notas", "Desde movil");
        mapa.put("estaid", v_estacion);
        mapa.put("usuaid", usuarioID);
        mapa.put("sinpregunta", false);
        mapa.put("claveauto", pAuto);
        String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");

        peticionWS(Enumeradores.Valores.TAREA_VNTARETIRO, "SQL", "SQL", xml,"","");
        return true;
    }

    private void wsArqeGuarda(String pCantidad,String pArqefolio){
        if(!Libreria.tieneInformacion(pArqefolio)){
            dlgMensajeError("No hay arqueo disponible",R.drawable.mensaje_warning2);
            return;
        }
        /*ContentValues mapa = new ContentValues();
        mapa.put("arqefolio", pArqefolio);
        mapa.put("captura", pCantidad);
        mapa.put("usuaid", usuarioID);
        String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");*/
        peticionWS(Enumeradores.Valores.TAREA_ARQEGUARDA, "SQL", "SQL", v_estacion+"",usuarioID,pCantidad);
    }

    private void wsGuardaVntaClte(){
        ContentValues mapa = new ContentValues();
        mapa.put("folio", v_vntafolio);
        mapa.put("cliente", v_cliente);
        mapa.put("estacion", v_estacion);
        mapa.put("usua", usuarioID);
        mapa.put("tarjeta", "");
        String xml= Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_VNTACLTE, "SQL", "SQL", xml, "", "");
    }

    private void traeRenglones(){

        peticionWS(Enumeradores.Valores.TAREA_TRAE_RENGLONES, "SQL", "SQL", "<linea><folio>"+v_vntafolio+"</folio><estatus>0</estatus><noestatus>0</noestatus></linea>", "", "");
    }

    private void traeUltVnta(){
        peticionWS(Enumeradores.Valores.TAREA_VNTATRAEULTIMA, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    private void traeUltTicket(String pFolio){

        peticionWS(Enumeradores.Valores.TAREA_VNTAULTIMAVNTA, "SQL", "SQL", v_estacion+"", pFolio, usuarioID+"");
    }

    private void traeValoresArqueo(){
        peticionWS(Enumeradores.Valores.TAREA_ARQEINICIO, "SQL", "SQL",v_estacion+"" , "", "");
    }

    private void buscarCliente(String busqueda){

        peticionWS(Enumeradores.Valores.TAREA_BQDACLTE, "SQL", "SQL", busqueda, ""+usuarioID, "");
    }

    private void cambioCredito(){

        peticionWS(Enumeradores.Valores.TAREA_VNTACONCREDITO, "SQL", "SQL", v_vntafolio, ""+usuarioID, "");
    }

    private void wsFactGuarda(Integer pUso,Integer pForma){
        ContentValues mapa=new ContentValues();
        mapa.put("folio","");
        mapa.put("clteid",v_cliente);
        mapa.put("mdpaid",v_metpago ? 99:98);
        mapa.put("fopaid",pForma);
        mapa.put("cfdiid",pUso);
        mapa.put("ventas",v_vntafolio);
        mapa.put("esttid",137);
        mapa.put("tipo",444);
        mapa.put("modelo",1);
        mapa.put("empresa",1);
        mapa.put("unico",true);
        mapa.put("notas","");
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_FACT_GUARDA, "SQL", "SQL", xml, "", "");
    }

    private void cancelaVenta(){
        peticionWS(Enumeradores.Valores.TAREA_VNTACANCELA, "SQL", "SQL", v_vntafolio, ""+usuarioID, "");
    }

    private void repoVenta(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPORTEVNTA, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    private void repoArqueo(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPORTEARQE, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    public void traeTicketVnta(String pFolio){
        traeUltTicket(pFolio);
    }

    public void traeTicketArqe(Enumeradores.Valores pTarea, String pFolio){
        peticionWS(pTarea, "SQL", "SQL",  v_estacion+"",pFolio,"");
    }

    public void vntaRegistrar(){
        ContentValues mapa=new ContentValues();
        mapa.put("folio",v_vntafolio);
        mapa.put("usua",usuarioID);
        mapa.put("estacion",v_estacion);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_VNTAREGISTRAR, "SQL", "SQL",  xml,"","");
    }

    private void repoVentasEspera(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPOVNTAESPERA, "SQL", "SQL", v_estacion+"", usuarioID+"", v_vntafolio);
    }

    public void traeVnta(String pFolio){
        if(v_dlgreporte!=null && v_dlgreporte.isShowing()){
            v_dlgreporte.dismiss();
        }
        pFolio = pFolio == v_default.getTex2() ? "" : pFolio;
        peticionWS(Enumeradores.Valores.TAREA_VNTATRAEULTIMA, "SQL", "SQL", v_estacion+"", usuarioID+"", pFolio);
    }

    private void repoRetiros(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPORETIROS, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    public void wsLineaCaptura2(String pCodigo){
        if(!Libreria.tieneInformacion(pCodigo)){
            dlgMensajeError("Captura un codigo",R.drawable.mensaje_warning2);
            return;
        }
        servicio.borraDatosTabla(TABLA_RENGLONES);
        String xml = Libreria.xmlInsertVenta(v_estacion,usuarioID,pCodigo.trim(),v_cliente,v_vntafolio.equalsIgnoreCase(v_default.getTex2()) ? "":v_vntafolio,v_ultprod==null ? 0 : v_ultprod,v_tipovnta,"",v_metpago);
        peticionWS(Enumeradores.Valores.TAREA_VNTAINSERTARENGLON, "SQL", "SQL", xml,v_vntafolio==v_default.getTex2() ? "":v_vntafolio,"");
    }

    private void vntaGuardaInfo(String pXml){
        peticionWS(Enumeradores.Valores.TAREA_GUARDAF10, "SQL", "SQL", pXml, "", "");
    }

    private void traeCuentas(String pid){
        peticionWS(Enumeradores.Valores.TAREA_TRAECUENTA, "SQL", "SQL", pid, "", "");
    }

    private void traeCuentaNombre(String pid){
        peticionWS(Enumeradores.Valores.TAREA_TRAECUCL, "SQL", "SQL", pid, "", "");
    }
    //</editor-fold>


    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        ContentValues obj = (ContentValues)output.getExtra1();

        switch (output.getTarea()){
            case TAREA_TRAE_RENGLONES:
                //System.out.println("imprimiendo la prueba "+ obj.getAsString("prueba"));
                ArrayList<Renglon> renglones = servicio.getRenglones(this, v_vntafolio);
                int visiblef2=View.GONE;
                int visiblef0=View.GONE;
                if(!renglones.isEmpty()){
                    v_importe = Double.valueOf(renglones.get(0).getVntatotal()+"");
                    DcarritoAdapter adapter = new DcarritoAdapter(renglones, this);
                    v_carrito.setAdapter(adapter);
                    v_codigo.requestFocus();
                    if(v_ultprod==null || v_ultprod == 0){
                        v_ultprod = renglones.get(0).getDvtaid();
                    }else if(v_granel){
                        hideKeyboard(v_codigo);
                        Renglon esAGranel=renglones.get(0);
                        for(Renglon renglon:renglones){
                            if(renglon.getDvtaid()==v_ultprod){
                                esAGranel=renglon;
                                break;
                            }
                        }
                        dlgRenglon(esAGranel);
                        v_granel = false;
                    }
                    actualizaTotal();
                    visiblef2 = v_puedeCobrar ? View.VISIBLE : View.GONE;
                    visiblef0 = v_registrar ? View.VISIBLE : View.GONE;
                }else{
                    DcarritoAdapter nuevoada = new DcarritoAdapter(new ArrayList(), this);
                    v_carrito.setAdapter(nuevoada);
                    nuevoada.notifyDataSetChanged();
                    v_ultprod = 0;
                    v_importe = 0.0;
                    actualizaTotal();
                    v_codigo.requestFocus();
                }
                v_F2.setVisibility(visiblef2);
                v_F0.setVisibility(visiblef0);
                ocultaTeclaEnLand();
                v_carrito.setEmptyView(findViewById(R.id.vntaSinRegistros));
                break;
            case TAREA_INSERTA_RENGLON:
                if(output.getExito()){
                    String anterior = v_vntafolio;
                    String info = obj.getAsString("vntafolio");

                    v_ultprod = obj.getAsInteger("dvtaid");
                    v_granel = obj.getAsBoolean("granel");
                    v_granel = v_granel == null ? false : v_granel;
                    if(!v_vntafolio.equals(info)){
                        v_vntafolio = info;
                        traeVnta(v_vntafolio);
                    } else{
                        traeRenglones();
                    }

                    if(v_ultprod == null){
                        v_ultprod = 0;
                    }
                }else{
                    String mensaje=obj.getAsString("msg");
                    dlgMensajeError(mensaje,R.drawable.mensaje_warning2);
                }
                v_codigo.requestFocus();
                break;
            case TAREA_VNTATRAEULTIMA:
                if(output.getExito()){
                    String info=obj.getAsString("vntafolio");
                    v_vntafolio = info;
                    info = obj.getAsString("cliente");
                    v_nombrecliente = info;
                    v_cliente = obj.getAsInteger("clteid");
                    v_tipovnta = obj.getAsInteger("tipo");
                    v_metpago = obj.getAsBoolean("contado");
                    v_tieneCredito = obj.getAsBoolean("concredito");
                    v_virtuales = obj.getAsBoolean("virtuales");
                    v_titulo = obj.getAsString("titulo");
                    v_notas = obj.getAsString("notas");
                    v_fereq = obj.getAsString("fecha");
                    v_mostrar = obj.getAsBoolean("llevar");
                    v_cuenta = obj.getAsInteger("cuenta");
                    v_cuentanombre = obj.getAsString("vcuenta");
                    menuCredito();
                    traeRenglones();
                    colocaTitulo();
                }else{
                    vntaLimpia();
                }
                break;
            case TAREA_VNTARETIRO:
                v_ticket = "";
                if(output.getExito()){
                    v_ticket = obj.getAsString("impreso");
                    imprimeTicket();
                    dlgMensajeError("Se hizo un retiro exitosamente",R.drawable.mensaje_exito2);
                }else{
                    dlgMensajeError(output.getMensaje(),R.drawable.mensaje_warning2);
                }
                ocultaTeclaEnLand();
                break;
            case TAREA_VNTAULTIMAVNTA:
                v_ticket = "";
                if(output.getExito()) {
                    v_ticket = obj.getAsString("anexo");
                    imprimeTicket();
                }else{
                    dlgMensajeError(output.getMensaje(), R.drawable.mensaje_warning2);
                }
                break;
            case TAREA_ARQEINICIO:
                if(output.getExito()) {
                    String trans = obj.getAsString("trans");
                    String cred = obj.getAsString("cred");
                    String deb = obj.getAsString("deb");
                    String arquefolio = obj.getAsString("arqefolio");
                    trans = Libreria.tieneInformacion(trans) ? trans : "0.0" ;
                    cred = Libreria.tieneInformacion(cred) ? cred : "0.0" ;
                    deb = Libreria.tieneInformacion(deb) ? deb : "0.0" ;
                    dlgArqueo(trans,cred,deb,arquefolio);
                }
                dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2 : R.drawable.mensaje_warning2);
                break;
            case TAREA_BQDACLTE:
                v_clientes = servicio.traeGenerica();
                v_Adacliente = new ClienteAdapter(v_clientes,this);
                v_Adacliente.notifyDataSetChanged();
                v_listaclientes.setAdapter(v_Adacliente);
                break;
            case TAREA_VNTACLTE:
                v_vntafolio=obj.getAsString("vntafolio");
                traeVnta(v_vntafolio);
                break;
            case TAREA_VNTACANCELA:
            case TAREA_VNTACONCREDITO:
                if(output.getExito()) {
                    //Boolean cred = obj.getAsBoolean("anexo");
                    //v_metpago = cred;
                    traeVnta(v_vntafolio);
                }else{
                    dlgMensajeError(output.getMensaje(), R.drawable.mensaje_warning2);
                }
                break;
            case TAREA_FACT_GUARDA:

                break;
            case TAREA_ARQEGUARDA:
                if(output.getExito()){
                    String arqefolio = obj.getAsString("anexo");
                    aPantalla(false);
                    traeTicketArqe(Enumeradores.Valores.TAREA_IMPRIMEARQE,arqefolio);
                    //imprimeTicket();
                }else{
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2:R.drawable.mensaje_warning2);
                }
                ocultaTeclaEnLand();
                break;
            case TAREA_REPORTEVNTA:
                dlgReporte(2);
                break;
            case TAREA_REPORTEARQE:
                dlgReporte(3);
                break;
            case TAREA_REPOVNTAESPERA:
                dlgReporte(5);
                break;
            case TAREA_REPORETIROS:
                dlgReporte(6);
                break;
            case TAREA_IMPRIMERETIRO:
            case TAREA_IMPRIMEARQE:
                if(output.getExito()){
                    v_ticket = obj.getAsString("anexo");
                    imprimeTicket();
                }else{
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2 : R.drawable.mensaje_warning2);
                }
                ocultaTeclaEnLand();
                break;
            case TAREA_VNTAREGISTRAR:
                if(output.getExito()){
                    v_ticket = obj.getAsString("anexo");
                    imprimeTicket();
                    traeUltVnta();
                }
                dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2 : R.drawable.mensaje_warning2);
                ocultaTeclaEnLand();
                break;
            case TAREA_GUARDAF10:
                if(output.getExito()){
                    v_dlgreporte.dismiss();
                    colocaTitulo();
                    menuCredito();
                }
                dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2 : R.drawable.mensaje_warning2);
                ocultaTeclaEnLand();
                break;
            case TAREA_VNTAINSERTARENGLON:
                if(output.getExito()){
                    String info=obj.getAsString("vntafolio");
                    v_vntafolio = info;
                    info = obj.getAsString("cliente");
                    v_nombrecliente = info;
                    v_cliente = obj.getAsInteger("clteid");
                    v_tipovnta = obj.getAsInteger("tipo");
                    v_metpago = obj.getAsBoolean("contado");
                    v_tieneCredito = obj.getAsBoolean("concredito");
                    v_virtuales = obj.getAsBoolean("virtuales");

                    v_ultprod = obj.getAsInteger("dvtaid");
                    v_granel = obj.getAsBoolean("granel");
                    v_granel = v_granel == null ? false : v_granel;

                    v_titulo = obj.getAsString("titulo");
                    v_notas = obj.getAsString("notas");
                    v_fereq = obj.getAsString("fecha");
                    v_mostrar = obj.getAsBoolean("llevar");
                    v_cuenta = obj.getAsInteger("cuenta");
                    v_cuentanombre = obj.getAsString("vcuenta");

                    renglones = servicio.getRenglones(this, v_vntafolio);
                    visiblef2 = View.GONE;
                    visiblef0 = View.GONE;
                    if(!renglones.isEmpty()){
                        v_importe = Double.valueOf(renglones.get(0).getVntatotal()+"");
                        DcarritoAdapter adapter = new DcarritoAdapter(renglones, this);
                        v_carrito.setAdapter(adapter);
                        v_codigo.requestFocus();
                        if(v_ultprod==null || v_ultprod == 0){
                            v_ultprod = renglones.get(0).getDvtaid();
                        }else if(v_granel){
                            hideKeyboard(v_codigo);
                            Renglon esAGranel=renglones.get(0);
                            for(Renglon renglon:renglones){
                                if(renglon.getDvtaid()==v_ultprod){
                                    esAGranel=renglon;
                                    break;
                                }
                            }
                            dlgRenglon(esAGranel);
                            v_granel = false;
                        }
                        actualizaTotal();
                        visiblef2 = v_puedeCobrar ? View.VISIBLE : View.GONE;
                        visiblef0 = v_registrar ? View.VISIBLE : View.GONE;
                    }else{
                        DcarritoAdapter nuevoada = new DcarritoAdapter(new ArrayList(), this);
                        v_carrito.setAdapter(nuevoada);
                        nuevoada.notifyDataSetChanged();
                        v_ultprod = 0;
                        v_importe = 0.0;
                        actualizaTotal();
                        v_codigo.requestFocus();
                    }
                    v_F2.setVisibility(visiblef2);
                    v_F0.setVisibility(visiblef0);
                    ocultaTeclaEnLand();
                    v_carrito.setEmptyView(findViewById(R.id.vntaSinRegistros));
                    menuCredito();
                    colocaTitulo();
                }else{
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito2:R.drawable.mensaje_warning2);
                }
                break;
            case TAREA_TRAECUENTA:
                dlgBuscaCuentas();
                break;
            case TAREA_TRAECUCL:
                if(output.getExito()){
                    v_cuentanombre = obj.getAsString("anexo");
                    if(v_TvCuenta!=null){
                        v_TvCuenta.setText(v_cuentanombre);
                    }
                }
                break;
        }
        if(obj != null){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cierraDialogo();
        v_codigo.requestFocus();
        ocultaTeclaEnLand();
        //traeUltVnta();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_punto_venta, menu);
        menu.clear();
        createMenuItem(menu, 0,1,  !v_metpago ? "Contado" : "Credito");//1
        createMenuItem(menu, 0,3,  "Cancelar Venta");//2
        createMenuItem(menu, 0,11,  "Nueva venta");//3
        createMenuItem(menu, 0,10,  "Ventas en captura");//4
        createMenuItem(menu, 1,2,  "Cliente F11");//5
        createMenuItem(menu, 1,9,  "Recargas F3");//6
        createMenuItem(menu, 1,4,  "Retiro F9");//7
        createMenuItem(menu, 1,13,  "Notas F10");//8
        createMenuItem(menu, 1,5,  "Devolucion");//9
        createMenuItem(menu, 1,6,  "Arqueo F12");//10
        createMenuItem(menu, 2,7,  "Reporte Ventas");//11
        createMenuItem(menu, 2,12,  "Reporte Retiros");//12
        createMenuItem(menu, 2,8,  "Reporte Arqueos");//13

        //menu.add(Menu.NONE, 7, Menu.NONE, "Lealtad");
        //menu.add(Menu.NONE, 8, Menu.NONE, "Devolucion");

        //menu.add(Menu.NONE, 10, 1, usuario);
        //menu.add(Menu.NONE, 11, 1, v_nombreestacion);
        System.out.println((v_visibleF!=3)+"    "+(v_visibleF!=11));
        menu.getItem(4).setVisible(v_visibleF!=11);
        menu.getItem(5).setVisible(v_visibleF!=3);
        menu.getItem(11).setVisible(v_puedeCobrar);
        menu.getItem(12).setVisible(v_puedeCobrar);
        v_menu = menu;
        menuCredito();
        return true;
    }

    private void createMenuItem(Menu menu, int groupId,int pId, String title) {
        MenuItem menuItem;
        if(groupId==2){
            menuItem = menu.add(groupId, pId, Menu.NONE, title);
        }else{
            menuItem = menu.add(groupId, pId, Menu.NONE, title);
        }


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setPadding(0, 0, 0, 0);

        TextView textView = new TextView(this);
        //SpannableString s = new SpannableString("Texto");
        //s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        textView.setText(title+MessageFormat.format("({0})",pId));
        //menuItem.setTitle(s);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(10, 5, 10, 5);
        textView.setTextSize(16);
        layout.addView(textView);

        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2);
        params.setMargins(20, 0, 20, 0);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(Color.GRAY);
        layout.addView(divider);

        menuItem.setActionView(textView);
        /*if(pId==1){
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            menu.setGroupDividerEnabled(true);
        }
        return true;
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 1:
                cambioCredito();
                break;
            case 2:
                dlgBuscaCliente();
                break;
            case 3:
                dlgVntaCancela();
                break;
            case 4:
                dlgRetiroParcial();
                break;
            case 5:
                Intent intent = new Intent(this, Devolucion.class);
                //mStartForResult.launch(intent);
                startActivity(intent);
                break;
            case 6:
                traeValoresArqueo();
                break;
            case 7:
                repoVenta();
                break;
            case 8:
                repoArqueo();
                break;
            case 9:
                doF3();
                break;
            case 10:
                repoVentasEspera();
                break;
            case 11:
                dlgVntaNueva();
                break;
            case 12:
                repoRetiros();
                break;
            case 13:
                dlgVentaInfo();
                break;
            default:
                muestraMensaje("Menu en construccion",R.drawable.mensaje_warning);
        }
        return false;
    }

    public void vntaLimpia(){
        DcarritoAdapter nuevoada = new DcarritoAdapter(new ArrayList(), this);
        v_carrito.setAdapter(nuevoada);
        nuevoada.notifyDataSetChanged();
        v_nombrecliente = v_default.getTex1();
        v_vntafolio = v_default.getTex2();
        v_cliente = v_default.getEnt1();
        v_metpago = v_default.getLog1();
        v_tipovnta = v_default.getEnt2();
        v_tieneCredito = v_default.getLog2();
        v_titulo = v_default.getTex3();
        v_notas = v_default.getTex4();
        v_fereq = v_default.getTex5();
        v_mostrar = v_default.getLog4();
        v_cuenta = v_default.getEnt3();

        v_Total.setText("$0.0");
        v_importe = 0.0;
        v_ultprod = null;
        colocaTitulo();
        v_F2.setVisibility(View.GONE);
    }

    private boolean cambiaF2(){
        if(Libreria.tieneInformacion(v_vntafolio) && !v_vntafolio.equals(v_default.getTex2())){
            Intent intent = new Intent(this, Cobropv.class);
            intent.putExtra("folio",v_vntafolio);
            intent.putExtra("importe",v_importe);
            intent.putExtra("estacion",v_estacion);
            intent.putExtra("metpago",v_metpago);
            intent.putExtra("cliente",v_nombrecliente);
            intent.putExtra("tipoVenta",v_tipovnta);
            intent.putExtra("virtuales",v_virtuales);
            mStartForResult.launch(intent);
            //startActivity(intent);
        }else{
            dlgMensajeError("No tiene venta para continuar",R.drawable.mensaje_warning2);
        }
        return true;
    }

    View.OnClickListener accion = (view) -> {
        switch(view.getId()){
            case R.id.vntaF0:
                dlgVntaRegistrar();
                break;
            case R.id.vntaF2:
                cambiaF2();
                break;
            case R.id.vntaF1:
                Intent intent = new Intent(this, BuscaProducto.class);
                intent.putExtra("folio",v_vntafolio);
                intent.putExtra("importe",v_importe);
                intent.putExtra("estacion",v_estacion);
                intent.putExtra("credito",v_metpago);
                intent.putExtra("cliente",v_nombrecliente);
                intent.putExtra("clteid",v_cliente);
                intent.putExtra("tipoVenta",v_tipovnta);
                mStartForResult.launch(intent);
                break;
            case R.id.vntaF6:
                traeUltTicket("");
                break;
            case R.id.vntaF9:
                dlgRetiroParcial();
                break;
            case R.id.vntaF10:
                dlgVentaInfo();
                break;
            case R.id.vntaF11:
                dlgBuscaCliente();
                break;
            case R.id.vntaF12:
                traeValoresArqueo();
                break;
            case R.id.vntaF3:
                doF3();
                break;
            default:
                muestraMensaje("En construccion",R.drawable.mensaje_warning);
        }
    };

    private void doF3(){
        Intent recarga = new Intent(this, Recargas.class);
        recarga.putExtra("folio",v_vntafolio);
        recarga.putExtra("importe",v_importe);
        recarga.putExtra("estacion",v_estacion);
        recarga.putExtra("credito",v_metpago);
        recarga.putExtra("cliente",v_nombrecliente);
        mStartForResult.launch(recarga);
    }

    private void colocaTitulo(){
        int longitud = v_vntafolio.length();
        boolean esnuevo = v_vntafolio.equalsIgnoreCase("NUEVA");
        String tipoventa = servicio.traeAbreviPorCata(7,v_tipovnta);
        String folio = esnuevo ? v_vntafolio : ("C*"+v_vntafolio.substring(longitud-4,longitud));
        String linea1,linea2 = "";
        linea1 = MessageFormat.format("{2} {0} {1}", folio,(v_nombreestacion+" "+usuario),"");
        linea2 = MessageFormat.format("{2} {0} {1}",(esnuevo || v_tipovnta == 50) ? "" : (v_metpago ? "Contado":"Credito"), v_nombrecliente,tipoventa);
        actualizaToolbar2(linea1,linea2);
    }

    public void dlgRenglon(Renglon pRen){
        Dialog dialog = new Dialog(this);
        //Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_dventa);
        dialog.setCancelable(true);
        ((TextView)dialog.findViewById(R.id.dvtaProducto)).setText(pRen.getProducto());
        ((TextView)dialog.findViewById(R.id.dvtaCantorig)).setText("Cant. Actual:"+pRen.getCant());
        if(!esHorizontal()){
            int width = (int)(getResources().getDisplayMetrics().widthPixels);
            int height = (int)(dialog.getWindow().getWindowManager().getDefaultDisplay().getHeight());
            dialog.getWindow().setLayout(width, -2);
        }

        final EditText cantidad = dialog.findViewById(R.id.dvtaCantidad);
        final EditText descuento = dialog.findViewById(R.id.dvtaDesc);
        final EditText precioN = dialog.findViewById(R.id.dvtaPrecioN);
        final RadioGroup grupo = dialog.findViewById(R.id.dvtaRadioGrup);
        TextView avisos = dialog.findViewById(R.id.dvtaAvisos);
        ScrollView scroll1 = dialog.findViewById(R.id.dvtaScrollA);
        LinearLayout lnCant = dialog.findViewById(R.id.dvtaLyCant);
        LinearLayout lnDesc = dialog.findViewById(R.id.dvtaLyDesc);
        LinearLayout lnPrec = dialog.findViewById(R.id.dvtaLyPrecio);
        ImageButton mas = dialog.findViewById(R.id.btnMasmas);
        ImageButton menos = dialog.findViewById(R.id.btnMenor);
        Button borrar = dialog.findViewById(R.id.btnBorrar);
        Button regresar = dialog.findViewById(R.id.btnRegresar);
        Button guardar = dialog.findViewById(R.id.btnGuardar);

        v_ultprod = pRen.getDvtaid();
        cantidad.setText(pRen.getCant()+"");
        precioN.setText(pRen.getPrecio()+"");
        cantidad.setSelectAllOnFocus(true);
        precioN.setSelectAllOnFocus(true);
        cantidad.requestFocus();
        avisos.setText(pRen.getNotas());
        avisos.setVisibility(Libreria.traeInfo(pRen.getNotas()).length()>2 ? View.VISIBLE:View.GONE);

        mas.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura2("+");
            dialog.dismiss();
        });

        menos.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura2("-");
            dialog.dismiss();
        });

        borrar.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura2(pRen.getCodigo()+"*0");
            dialog.dismiss();
        });

        grupo.check(R.id.dvtaRadioCant);
        //hideKeyboard(cantidad);
        showKeyboard(cantidad);
        lnCant.setVisibility(View.VISIBLE);
        lnPrec.setVisibility(View.GONE);
        lnDesc.setVisibility(View.GONE);

        grupo.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.dvtaRadioCant:
                    lnCant.setVisibility(View.VISIBLE);
                    lnPrec.setVisibility(View.GONE);
                    lnDesc.setVisibility(View.GONE);
                    cantidad.requestFocus();
                    hideKeyboard(cantidad);
                    showKeyboard(cantidad);
                    break;
                case R.id.dvtaRadioPrec:
                    lnCant.setVisibility(View.GONE);
                    lnPrec.setVisibility(View.VISIBLE);
                    lnDesc.setVisibility(View.GONE);
                    precioN.requestFocus();
                    //showKeyboard(precioN);
                    break;
                case R.id.dvtaRadioDesc:
                    lnCant.setVisibility(View.GONE);
                    lnPrec.setVisibility(View.GONE);
                    lnDesc.setVisibility(View.VISIBLE);
                    descuento.requestFocus();
                    //showKeyboard(descuento);
                    break;
            }
        });

        regresar.setOnClickListener(v-> dialog.dismiss());

        guardar.setOnClickListener(v-> {
            String cadena="", dato="Cantidad ";
            String numero = cantidad.getText().toString();
            int maxCant=v_cantidadUsual;
            switch (grupo.getCheckedRadioButtonId()){
                case R.id.dvtaRadioCant:
                    numero = cantidad.getText().toString();
                    dato="Cantidad ";
                    cadena="*";
                    break;
                case R.id.dvtaRadioPrec:
                    numero = precioN.getText().toString();
                    dato="Precio ";
                    cadena="@";
                    break;
                case R.id.dvtaRadioDesc:
                    numero = descuento.getText().toString();
                    dato="Descuento ";
                    cadena="/";
                    maxCant=100;
                    break;
            }
            if(Libreria.tieneInformacionFloat(numero,0)>maxCant){
                dlgMensajeError(dato+" excede al permitido",R.drawable.mensaje_warning2);
                return ;
            }
            DecimalFormat df = new DecimalFormat("0.0000");
            cadena +=df.format(Libreria.tieneInformacionFloat(numero,0));
            wsLineaCaptura2(cadena);
            dialog.dismiss();
        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guardar.callOnClick();
            }
            return false;
        });
        descuento.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guardar.callOnClick();
            }
            return false;
        });
        precioN.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guardar.callOnClick();
            }
            return false;
        });
        dialog.show();
    }

    public void dlgRetiroParcial(){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_retiro);
        dialog.setCancelable(false);

        final EditText cantidad = dialog.findViewById(R.id.retiCaptura);
        final EditText autoriza = dialog.findViewById(R.id.retiAutoriza);

        Button regresar = dialog.findViewById(R.id.retibtnregresa);
        Button guardar = dialog.findViewById(R.id.retibtnguarda);

        cantidad.setText(v_retiimporte+"");
        cantidad.requestFocus();

        regresar.setOnClickListener(v-> {
            dialog.dismiss();
            ocultaTeclaEnLand();
        });

        guardar.setOnClickListener(v-> {
            String cantReti=cantidad.getText().toString();
            if(Libreria.tieneInformacionFloat(cantReti,0)<=0){
                dlgMensajeError("Es un valor no permitido",R.drawable.mensaje_warning2);
                return ;
            }
            if(wsRetiroParcial(cantReti,autoriza.getText().toString())){
                dialog.dismiss();
            }
        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                if(esHorizontal()){
                    hideKeyboard(view);
                }
            }
            return false;
        });

        autoriza.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guardar.callOnClick();
            }
            return false;
        });
        dialog.show();
    }

    public void dlgArqueo(String pTrans,String pCred,String pDeb,String pArqueo){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_arqueo);
        dialog.setCancelable(false);

        EditText captura = dialog.findViewById(R.id.arqeCaptura);
        Button regresar = dialog.findViewById(R.id.arqebtnregresa);
        Button guardar = dialog.findViewById(R.id.arqebtnguarda);
        TextView trans = dialog.findViewById(R.id.arqeTrans);
        TextView credito = dialog.findViewById(R.id.arqeCredito);
        TextView debito = dialog.findViewById(R.id.arqeDebito);

        captura.setText("");
        captura.requestFocus();

        trans.setText(pTrans);
        credito.setText(pCred);
        debito.setText(pDeb);

        captura.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                guardar.callOnClick();
            }
            return false;
        });

        regresar.setOnClickListener(v-> dialog.dismiss());
        guardar.setOnClickListener(v-> {
            if(!Libreria.tieneInformacion(captura.getText().toString())){
                dlgMensajeError("Debe de capturar un valor",R.drawable.mensaje_warning2);
                return;
            }
            wsArqeGuarda(captura.getText().toString(),pArqueo);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void dlgBuscaCliente(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_cliente, null);
        View titulo=inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        //builder.setContentView();
        builder.setCancelable(true);

        final EditText cantidad = vista.findViewById(R.id.clteBusca);
        ImageButton busca = vista.findViewById(R.id.btnClteBusca);
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        ImageButton nuevo = titulo.findViewById(R.id.btnTitNuevo);
        ImageButton regresa = titulo.findViewById(R.id.btnTitRegresa);
        v_listaclientes = vista.findViewById(R.id.listClte);
        titTitulo.setText("Busca Cliente");

        cantidad.setText("");
        cantidad.requestFocus();
        cantidad.setSelectAllOnFocus(true);
        v_Adacliente = new ClienteAdapter(new ArrayList(),this);
        v_listaclientes.setAdapter(v_Adacliente);
        TextView texto=vista.findViewById(R.id.clteListSinReg);
        texto.setText("");
        v_listaclientes.setEmptyView(texto);

        int[] colors = {0, 0xFF000000, 0};
        v_listaclientes.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        v_listaclientes.setDividerHeight(1);
        busca.setOnClickListener(v-> {
            buscarCliente(cantidad.getText().toString());
            cantidad.requestFocus();
            //cantidad.setText("");
        });

        regresa.setOnClickListener(v-> dlgClienteCierra());

        nuevo.setOnClickListener(v-> {
            cambiaCliente(0);
        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                busca.callOnClick();
                cantidad.requestFocus();
            }
            return false;
        });
        v_dlgCliente = builder.create();
        v_dlgCliente.show();
    }

    private void imprimeTicket(){
        doImprime(v_ticket,false);
    }

    public void setCliente(Integer pClteid,String pCliente,Boolean pLogico){
        v_cliente = pClteid;
        v_nombrecliente = pCliente;
        v_tieneCredito = pLogico;
        wsGuardaVntaClte();
    }

    public void cambiaCliente(Integer pId){
        Intent intent = new Intent(this, com.example.aristomovil2.Acrividades.Cliente.class);
        intent.putExtra("clteid",pId);
        mStartForResult.launch(intent);
    }

    public void dlgClienteCierra(){
        if(v_dlgCliente!=null){
            v_dlgCliente.dismiss();
        }
    }

    public void dlgVntaCancela(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Deseas cancelar la venta?")
                .setPositiveButton("Si", (dialog, id) -> cancelaVenta())
                .setNegativeButton("No", (dialog, id) -> {});

        builder.show();
    }

    public void dlgVntaRegistrar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(MessageFormat.format("¿Desea concluir el registro de los articulos e imprimir codigo de cobro?",v_vntafolio))
                .setPositiveButton("Si", (dialog, id) -> vntaRegistrar())
                .setNegativeButton("No", (dialog, id) -> {});

        builder.show();
    }

    public void dlgVntaNueva(){
        if(v_vntafolio!=v_default.getTex2()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(MessageFormat.format("¿Desea empezar una venta nueva?\nLa venta con folio {0} seguira en captura",v_vntafolio))
                    .setPositiveButton("Si", (dialog, id) -> vntaLimpia())
                    .setNegativeButton("No", (dialog, id) -> {});
            builder.show();
        }
    }

    public void dlgVentaInfo(){
        //dialogoFragment dialogFragment = new dialogoFragment();
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //dialogFragment.show(fragmentManager, "");
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialogo_vntainfo, null);
        View titulo = inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        titTitulo.setText("Informacion de la venta "+v_vntafolio+" "+v_nombrecliente);

        EditText titu = vista.findViewById(R.id.vntaTitulo);
        EditText indica = vista.findViewById(R.id.vntaindica);
        EditText fereq = vista.findViewById(R.id.vntaFeReq);
        Spinner spin = vista.findViewById(R.id.vntaTipos);
        RadioGroup grupo = vista.findViewById(R.id.vntaGrop);
        RadioButton mostrador = vista.findViewById(R.id.vntaMostrador);
        RadioButton domicilio = vista.findViewById(R.id.vntaDomicilio);
        Button regresar = vista.findViewById(R.id.vntabtnregresa);
        Button guarda = vista.findViewById(R.id.vntabtnguarda);
        Button cuentas = vista.findViewById(R.id.vntabtnCuenta);
        ImageButton reg = titulo.findViewById(R.id.btnTitRegresa);
        ImageButton mas = titulo.findViewById(R.id.btnTitNuevo);
        TableRow row = vista.findViewById(R.id.cuentass);

        v_TvCuenta = vista.findViewById(R.id.vntaCuenta);

        reg.setVisibility(View.GONE);
        mas.setVisibility(View.GONE);

        v_TvCuenta.setText(v_cuentanombre);

        titu.setText(v_titulo);
        indica.setText(v_notas);


        String []dates = Libreria.traeInfo(v_fereq,"").split("T");//------
        fereq.setText(Libreria.fecha_to_fecha(dates[0],"yyyy-MM-dd","yyMMdd"));

        List<Generica> tipos = servicio.traeDcatGenerica(7);
        GenericaAdapter tiposadapter = new GenericaAdapter(tipos,this,11);
        spin.setAdapter(tiposadapter);

        grupo.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.vntaMostrador:
                    row.setVisibility(View.GONE);
                    break;
                case R.id.vntaDomicilio:
                    row.setVisibility(View.VISIBLE);
                    traeCuentaNombre(v_cuenta+"");
                    break;
            }
        });

        if( v_mostrar==null || !v_mostrar ){
            mostrador.setChecked(true);
        }else{
            domicilio.setChecked(true);
        }
        for(int i=0;i<tipos.size();i++){
            if(tipos.get(i).getId().compareTo(v_tipovnta)==0){
                spin.setSelection(i);
            }
        }

        cuentas.setOnClickListener(view -> traeCuentas(""+v_cliente));

        fereq.setOnClickListener(v -> {
            DialogFragment newFragment = new Inventario.DatePickerFragment(fereq);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        fereq.setOnFocusChangeListener((view, b) -> {
            if(b){
                DialogFragment newFragment = new Inventario.DatePickerFragment(fereq);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        builder.setCancelable(true);

        v_dlgreporte = builder.create();
        v_dlgreporte.setOnShowListener(dialog -> {
            Window window = v_dlgreporte.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
            }
        });
        regresar.setOnClickListener(view -> v_dlgreporte.dismiss());
        guarda.setOnClickListener(view -> {
            boolean esmostra= domicilio.isChecked();
            Integer tipo=((Generica)spin.getSelectedItem()).getId();
            ContentValues mapa =new ContentValues();
            mapa.put("vntafolio",v_vntafolio);
            mapa.put("titulo",titu.getText().toString());
            mapa.put("indicaciones",indica.getText().toString());
            mapa.put("fereq",fereq.getText().toString());
            mapa.put("llevar",esmostra);
            mapa.put("cuenta",v_cuenta);
            mapa.put("tipo",tipo);
            String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
            vntaGuardaInfo(xml);
            v_tipovnta=tipo;
        });
        v_dlgreporte.show();
    }

    public void dlgBuscaCuentas(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_cliente, null);
        View titulo=inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        //builder.setContentView();
        builder.setCancelable(true);

        LinearLayout busqueda = vista.findViewById(R.id.clteLtBusca);
        busqueda.setVisibility(View.GONE);

        final EditText cantidad = vista.findViewById(R.id.clteBusca);
        ImageButton busca = vista.findViewById(R.id.btnClteBusca);
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        ImageButton nuevo = titulo.findViewById(R.id.btnTitNuevo);
        ImageButton regresa = titulo.findViewById(R.id.btnTitRegresa);
        ListView v_lista = vista.findViewById(R.id.listClte);
        titTitulo.setText("Lista cuentas \n"+v_nombrecliente);

        List<Cuenta> listaCuentas = servicio.traeCuentas();
        CuentaAdapter cuenta = new CuentaAdapter(listaCuentas,this);
        v_lista.setAdapter(cuenta);
        v_lista.setEmptyView(vista.findViewById(R.id.clteListSinReg));

        cantidad.setVisibility(View.GONE);
        busca.setVisibility(View.GONE);

        int[] colors = {0, 0xFF000000, 0};
        v_lista.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        v_lista.setDividerHeight(1);

        v_dlgCliente = builder.create();
        regresa.setOnClickListener(v-> {
            if(v_dlgCliente!=null){
                v_dlgCliente.dismiss();
            }
        });

        nuevo.setVisibility(View.GONE);

        v_dlgCliente.show();
    }

    public void eligeCuenta(Cuenta pGen){
        v_cuenta = pGen.getCuclid();
        traeCuentaNombre(v_cuenta+"");
        v_dlgCliente.dismiss();
    }

    public ActivityResultLauncher<Intent> getmStartForResult() {
        return mStartForResult;
    }

    private void menuCredito(){
        if(v_menu!=null){
            String titulo ="Cambiar a {0}";
            v_menu.getItem(0).setTitle(MessageFormat.format(titulo,v_tieneCredito ? (!v_metpago ? "Contado" : "Credito"):"Sin credito"));
            v_menu.getItem(0).setEnabled(v_tieneCredito||v_tipovnta==50);
        }
    }

    private void actualizaTotal(){
        Integer escala = esHorizontal() ? 1:2 ;
        v_Total.setText("$"+new BigDecimal(v_importe).setScale(escala,BigDecimal.ROUND_HALF_EVEN));
    }

    private void ocultaTeclaEnLand(){
        if(esHorizontal()){
            hideKeyboard(v_codigo);
        }
    }



}