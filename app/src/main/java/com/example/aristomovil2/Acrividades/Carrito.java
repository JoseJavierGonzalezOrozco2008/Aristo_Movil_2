package com.example.aristomovil2.Acrividades;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ClienteAdapter;
import com.example.aristomovil2.adapters.DcarritoAdapter;
import com.example.aristomovil2.adapters.ListaProdsAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Carrito extends ActividadBase {
    private String v_vntafolio,v_nombrecliente,v_nombreestacion,v_nombre_impresora,v_ticket;
    private Integer v_estacion,v_tipovnta,v_ultprod,v_cliente,v_imprime_espacios;
    private Double v_importe,v_retiimporte;
    private Boolean v_metpago,v_granel,v_tieneCredito,v_virtuales;
    private ListView v_carrito,v_listaclientes;
    private GridView gridProds;
    private Generica v_default;
    private TextView v_Total;
    private EditText v_codigo;
    private List<Generica> v_clientes;
    private ClienteAdapter v_Adacliente;
    private Dialog v_dlgCliente;
    private Menu v_menu;

    private List<Generica> renglonesGen;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultado = result.getData();
                Integer actividad = resultado!=null ? resultado.getIntExtra("actividad",0):0;
                switch(actividad){
                    case 1://cobro
                        Boolean pideretiro = resultado.getBooleanExtra("deberetirar",false);
                        traeUltVnta();
                        if(pideretiro){
                            dlgRetiroParcial();
                        }
                        break;
                    case 2://buscar prod
                        String codigo = resultado.getStringExtra("codigo");
                        if(Libreria.tieneInformacion(codigo))
                            wsLineaCaptura(codigo);
                        break;

                    default:
                        muestraMensaje("Sin resultados obtenidos",R.drawable.mensaje_warning);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        inicializarActividad2("Folio Cliente Tipo","");
        SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        v_tipovnta = sharedPreferences.getInt("tipoVenta", 48);
        v_estacion = sharedPreferences.getInt("estaid", 0);
        v_nombreestacion = sharedPreferences.getString("estacion", "Estacion");
        v_cliente = sharedPreferences.getInt("clientedefault",-1);
        v_nombrecliente = sharedPreferences.getString("nomCliente", "Publico en general");
        v_metpago = preferences.getBoolean("ventaCredito", false);
        v_imprime_espacios = sharedPreferences.getInt("espacios", 3);
        Integer visible = sharedPreferences.getInt("muestaf", 3);
        v_nombre_impresora = preferences.getString("impresora", "Predeterminada");
        v_vntafolio = "Nueva";
        v_ticket = "";
        v_ultprod = 0;
        v_default = new Generica(0);
        v_default.setEnt1(v_cliente);
        v_default.setEnt2(v_tipovnta);
        v_default.setTex1(v_nombrecliente);
        v_default.setTex2(v_vntafolio);
        v_default.setLog1(v_metpago);
        v_default.setLog2(false);
        v_default.setLog3(false);
        v_virtuales=v_default.getLog3();
        v_codigo = findViewById(R.id.vntaCodigo);
        v_retiimporte = 1000.0;
        v_tieneCredito = v_default.getLog2();
        ImageButton enter = findViewById(R.id.vntaEnter);
        Button f2 = findViewById(R.id.vntaF2);
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
        v_Total.setText("$0.0");
        v_granel = false;
        v_codigo.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                String captura = v_codigo.getText().toString();
                wsLineaCaptura(captura);
                v_codigo.setText("");
            }
            return false;
        });
        f2.setOnClickListener(accion);
        f1.setOnClickListener(accion);
        f3.setOnClickListener(accion);
        enter.setOnClickListener(view -> {
            String captura = v_codigo.getText().toString();
            wsLineaCaptura(captura);
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
            f11.setOnClickListener(accion);
            f12.setOnClickListener(accion);
        }
        switch (visible){
            case 3:
                f3.setVisibility(View.VISIBLE);
                break;
            case 11:
                f11.setVisibility(View.VISIBLE);
                break;
        }
        gridProds = findViewById(R.id.listPrueba);
        wsBqdaProd();
        v_codigo.requestFocus();
        colocaTitulo();
        traeUltVnta();


    }

    //<editor-fold defaultstate="collapsed" desc="Ws">
    public void wsLineaCaptura(String pCodigo){
        if(!Libreria.tieneInformacion(pCodigo)){
            muestraMensaje("Captura un código",R.drawable.mensaje_error);
            return;
        }
        Toast.makeText(this, "Acción para: " + this.v_ultprod, Toast.LENGTH_SHORT).show();
        String xml = Libreria.xmlInsertVenta(v_estacion,usuarioID,pCodigo.trim(),v_cliente,v_vntafolio.equalsIgnoreCase("nueva") ? "":v_vntafolio,v_ultprod==null ? 0 : v_ultprod,v_tipovnta,"",v_metpago);
        peticionWS(Enumeradores.Valores.TAREA_INSERTA_RENGLON, "SQL", "SQL", xml,v_vntafolio,"");
    }
    private void wsBqdaProd(){
        peticionWS(Enumeradores.Valores.TAREA_VNTAMASPROD, "SQL", "SQL", "","","");
    }
    private void wsRetiroParcial(String pCantidad,String pAuto){
        if(!(Libreria.tieneInformacion(pCantidad) && Libreria.isNumeric(pCantidad))){
            muestraMensaje("Necesita capturar una cantidad",R.drawable.mensaje_error);
            return;
        }
        if(!Libreria.tieneInformacion(pAuto)){
            muestraMensaje("Requiere capturar su clave numérica ",R.drawable.mensaje_error);
            return;
        }
        ContentValues mapa = new ContentValues();
        mapa.put("tipo", 71);
        mapa.put("conceptoid", 1);
        mapa.put("formapago", 89);
        mapa.put("impuesto", 0);
        mapa.put("cantidad", pCantidad);
        mapa.put("reqauto", false);
        mapa.put("notas", "Desde movil");
        mapa.put("estaid", v_estacion);
        mapa.put("usuaid", usuarioID);
        mapa.put("sinpregunta", false);
        mapa.put("claveauto", pAuto);
        String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_VNTARETIRO, "SQL", "SQL", xml,"","");
    }

    private void wsArqeGuarda(String pCantidad,String pArqefolio){
        if(!Libreria.tieneInformacion(pArqefolio)){
            muestraMensaje("No hay arqueo disponible",R.drawable.mensaje_error);
            return;
        }
        ContentValues mapa = new ContentValues();
        mapa.put("arqefolio", pArqefolio);
        mapa.put("captura", pCantidad);
        mapa.put("usuaid", usuarioID);
        String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_ARQEGUARDA, "SQL", "SQL", xml,"","");
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

    private void traeUltTicket(){
        peticionWS(Enumeradores.Valores.TAREA_VNTAULTIMAVNTA, "SQL", "SQL", v_estacion+"", usuarioID+"", "");
    }

    private void traeValoresArqueo(){
        if(!v_vntafolio.equalsIgnoreCase(v_default.getTex2())){
            muestraMensaje("Tiene una venta en captura",R.drawable.mensaje_error);
            return;
        }
        peticionWS(Enumeradores.Valores.TAREA_ARQEINICIO, "SQL", "SQL",v_estacion+"" , "", "");
    }

    private void buscarCliente(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_BQDACLTE, "SQL", "SQL", busqueda, ""+usuarioID, "");
    }

    private void cambioCredito(){
        peticionWS(Enumeradores.Valores.TAREA_VNTACONCREDITO, "SQL", "SQL", v_vntafolio, ""+usuarioID, "");
    }
    //</editor-fold>


    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();
        switch (output.getTarea()){
            case TAREA_TRAE_RENGLONES:
                System.out.println("Entre trae renglones "+v_vntafolio);
                ArrayList<Renglon> renglones = servicio.getRenglones(this, v_vntafolio);
                if(!renglones.isEmpty()){
                    v_importe = Double.valueOf(renglones.get(0).getVntatotal()+"");
                    DcarritoAdapter adapter = new DcarritoAdapter(renglones, this);
                    v_carrito.setAdapter(adapter);
                    v_carrito.setEmptyView(findViewById(R.id.vntaSinRegistros));
                    v_codigo.requestFocus();
                    if(v_ultprod == 0){
                        v_ultprod = renglones.get(0).getDvtaid();
                    }else if(v_granel){
                        dlgRenglon(renglones.get(0));
                        v_granel = false;
                    }
                    System.out.println("Valor ultprod: "+v_ultprod);
                    v_Total.setText("$"+v_importe);
                }else{
                    DcarritoAdapter nuevoada = new DcarritoAdapter(new ArrayList(), this);
                    v_carrito.setAdapter(nuevoada);
                    v_carrito.setEmptyView(findViewById(R.id.vntaSinRegistros));
                    nuevoada.notifyDataSetChanged();
                    v_ultprod = 0;
                    v_importe = 0.0;
                    v_Total.setText("$"+v_importe);
                    v_codigo.requestFocus();
                }
                break;
            case TAREA_INSERTA_RENGLON:
                System.out.println("Estatus: "+output.getExito());
                if(output.getExito()){
                    traeUltVnta();
                    traeRenglones();
                    v_granel = obj.getAsBoolean("granel");
                    v_granel = v_granel == null ? false : v_granel;

                    if(v_ultprod == null){
                        v_ultprod = 0;
                    }
                    if(gridProds != null && renglonesGen != null && !renglonesGen.isEmpty()){
                        ListaProdsAdapter adapter = new ListaProdsAdapter(this, renglonesGen,this,v_estacion,usuarioID,v_cliente,v_vntafolio,v_ultprod,v_tipovnta,v_metpago);
                        gridProds.setAdapter(adapter);
                    }
                }else{
                    if(obj != null){
                        String mensaje=obj.getAsString("msg");
                        muestraMensaje(mensaje,R.drawable.mensaje_error);
                    }

                }
                v_codigo.requestFocus();
                break;
            case TAREA_VNTAMASPROD:
                renglonesGen = servicio.traeProductosVnta();
                if(gridProds != null && renglonesGen != null && !renglonesGen.isEmpty()){
                    ListaProdsAdapter adapter = new ListaProdsAdapter(this, renglonesGen,this,v_estacion,usuarioID,v_cliente,v_vntafolio,v_ultprod,v_tipovnta,v_metpago);
                    gridProds.setAdapter(adapter);
                }
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
                    ArrayList<Renglon> renglonesdv = servicio.getRenglones(this, v_vntafolio);
                    if(!renglonesdv.isEmpty() && renglonesGen != null && !renglonesGen.isEmpty()){
                        v_ultprod = renglonesdv.get(0).getDvtaid();
                    }
                    if(gridProds != null){
                        ListaProdsAdapter adapter = new ListaProdsAdapter(this, renglonesGen,this,v_estacion,usuarioID,v_cliente,v_vntafolio,v_ultprod,v_tipovnta,v_metpago);
                        gridProds.setAdapter(adapter);
                    }
                    if(v_menu!=null){
                        v_menu.getItem(0).setTitle(v_tieneCredito ? (!v_metpago ? "Contado" : "Credito"):"Sin credito");
                        v_menu.getItem(0).setEnabled(v_tieneCredito);
                    }
                    traeRenglones();
                    colocaTitulo();
                }else{
                    DcarritoAdapter nuevoada = new DcarritoAdapter(new ArrayList(), this);
                    v_carrito.setAdapter(nuevoada);
                    nuevoada.notifyDataSetChanged();
                    v_nombrecliente = v_default.getTex1();
                    v_vntafolio = v_default.getTex2();
                    v_cliente = v_default.getEnt1();
                    v_metpago = v_default.getLog1();
                    v_tipovnta = v_default.getEnt2();
                    v_tieneCredito = v_default.getLog2();
                    v_Total.setText("$0.0");
                    v_importe = 0.0;
                    v_ultprod = null;
                    colocaTitulo();
                }
                break;
            case TAREA_VNTARETIRO:
                v_ticket = "";
                if(output.getExito()){
                    v_ticket = obj.getAsString("impreso");
                    imprimeTicket();
                }
                muestraMensaje(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito: R.drawable.mensaje_error);
                break;
            case TAREA_VNTAULTIMAVNTA:
                v_ticket = "";
                if(output.getExito()) {
                    v_ticket = obj.getAsString("anexo");
                    imprimeTicket();
                }else{
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
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
                }else{
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                }
                break;
            case TAREA_BQDACLTE:
                v_clientes = servicio.traeGenerica();
                v_Adacliente = new ClienteAdapter(v_clientes,this);
                v_Adacliente.notifyDataSetChanged();
                v_listaclientes.setAdapter(v_Adacliente);
                break;
            case TAREA_VNTACLTE:
                traeUltVnta();
                break;
            case TAREA_VNTACONCREDITO:
                if(output.getExito()) {
                    //Boolean cred = obj.getAsBoolean("anexo");
                    //v_metpago = cred;
                    traeUltVnta();
                }else{
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                }
                break;
        }
        if(obj != null){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        v_codigo.requestFocus();
        //traeUltVnta();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, !v_metpago ? "Contado" : "Credito");
        menu.add(Menu.NONE, 2, Menu.NONE, "Cliente");
        menu.add(Menu.NONE, 3, Menu.NONE, "Borra Venta");
        menu.add(Menu.NONE, 4, Menu.NONE, "Retiro");
        menu.add(Menu.NONE, 5, Menu.NONE, "Ultimo ticket");
        menu.add(Menu.NONE, 6, Menu.NONE, "Arqueo");
        menu.add(Menu.NONE, 7, Menu.NONE, "Lealtad");
        menu.add(Menu.NONE, 8, Menu.NONE, "Devolucion");
        menu.add(Menu.NONE, 10, 1, usuario);
        menu.add(Menu.NONE, 11, 1, v_nombreestacion);
        menu.getItem(2).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);
        menu.getItem(0).setTitle(v_tieneCredito ? (!v_metpago ? "Contado" : "Credito"):"Sin credito");
        menu.getItem(0).setEnabled(v_tieneCredito);
        v_menu = menu;
        return true;
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 10:
            case 11:
                break;
            case 2:
                dlgBuscaCliente();
                break;
            case 4:
                dlgRetiroParcial();
                break;
            case 5:
                traeUltTicket();
                break;
            case 6:
                traeValoresArqueo();
                break;
            case 1:
                //cambia de credito a cotado y viceversa
                cambioCredito();
                break;
            default:
                muestraMensaje("Menu en construccion",R.drawable.mensaje_warning);
        }
        return false;
    }

    View.OnClickListener accion = (view) -> {
        switch(view.getId()){
            case R.id.vntaF2:
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
                    muestraMensaje("No tiene venta para continuar",R.drawable.mensaje_error);
                }
                break;
            case R.id.vntaF1:
                Intent intent = new Intent(this, BuscaProducto.class);
                intent.putExtra("folio",v_vntafolio);
                intent.putExtra("importe",v_importe);
                intent.putExtra("estacion",v_estacion);
                intent.putExtra("credito",v_metpago);
                intent.putExtra("cliente",v_nombrecliente);
                mStartForResult.launch(intent);
                break;
            case R.id.vntaF6:
                traeUltTicket();
                break;
            case R.id.vntaF9:
                dlgRetiroParcial();
                break;
            case R.id.vntaF11:
                dlgBuscaCliente();
                break;
            case R.id.vntaF12:
                traeValoresArqueo();
                break;
            case R.id.vntaF3:
                Intent recarga = new Intent(this, Recargas.class);
                recarga.putExtra("folio",v_vntafolio);
                recarga.putExtra("importe",v_importe);
                recarga.putExtra("estacion",v_estacion);
                recarga.putExtra("credito",v_metpago);
                recarga.putExtra("cliente",v_nombrecliente);
                mStartForResult.launch(recarga);
                break;
            default:
                muestraMensaje("En construcción",R.drawable.mensaje_warning);
        }
    };

    private void colocaTitulo(){
        int longitud = v_vntafolio.length();
        boolean esnuevo = v_vntafolio.equalsIgnoreCase("NUEVA");
        String folio = esnuevo ? v_vntafolio : ("C*"+v_vntafolio.substring(longitud-4,longitud));
        String linea1,linea2="";
        if(esHorizontal()){
            linea1 = MessageFormat.format("{0} {1} {2}", folio,esnuevo ? "" : (v_metpago ? "Contado":"Credito"),v_nombrecliente);
        }else{
            linea1= MessageFormat.format("{0} {1}", folio,esnuevo ? "" : (v_metpago ? "Contado":"Credito"));
            linea2= v_nombrecliente;
        }
        actualizaToolbar2(linea1,linea2);
    }

    public void dlgRenglon(Renglon pRen){
        Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_dventa);
        dialog.setCancelable(true);
        ((TextView)dialog.findViewById(R.id.dvtaProducto)).setText(pRen.getProducto());
        ((TextView)dialog.findViewById(R.id.dvtaCantorig)).setText("Cant. Actual:"+pRen.getCant());

        final EditText cantidad = dialog.findViewById(R.id.dvtaCantidad);
        ImageButton mas = dialog.findViewById(R.id.btnMasmas);
        ImageButton menos = dialog.findViewById(R.id.btnMenor);
        Button borrar = dialog.findViewById(R.id.btnBorrar);
        Button regresar = dialog.findViewById(R.id.btnRegresar);
        Button guardar = dialog.findViewById(R.id.btnGuardar);

        v_ultprod = pRen.getDvtaid();
        cantidad.setText(pRen.getCant()+"");
        cantidad.setSelectAllOnFocus(true);

        mas.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura("+");
            dialog.dismiss();
        });

        menos.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura("-");
            dialog.dismiss();
        });

        borrar.setOnClickListener(v -> {
            hideKeyboard(cantidad);
            wsLineaCaptura(pRen.getCodigo()+"*0");
            dialog.dismiss();
        });

        regresar.setOnClickListener(v-> dialog.dismiss());

        guardar.setOnClickListener(v-> {
            wsLineaCaptura("*"+cantidad.getText().toString());
            dialog.dismiss();
        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
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

        regresar.setOnClickListener(v-> dialog.dismiss());

        guardar.setOnClickListener(v-> {
            wsRetiroParcial(cantidad.getText().toString(),autoriza.getText().toString());
            dialog.dismiss();
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
                muestraMensaje("Debe de capturar un valor",R.drawable.mensaje_error);
                return;
            }
            wsArqeGuarda(captura.getText().toString(),pArqueo);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void dlgBuscaCliente(){
        v_dlgCliente = new Dialog(this);
        Objects.requireNonNull(v_dlgCliente.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        v_dlgCliente.setContentView(R.layout.dialogo_cliente);
        v_dlgCliente.setCancelable(true);

        final EditText cantidad = v_dlgCliente.findViewById(R.id.clteBusca);
        ImageButton busca = v_dlgCliente.findViewById(R.id.btnClteBusca);
        Button nuevo = v_dlgCliente.findViewById(R.id.btnClteNuevo);
        ImageButton regresa = v_dlgCliente.findViewById(R.id.btnClteRegresa);
        v_listaclientes = v_dlgCliente.findViewById(R.id.listClte);

        cantidad.setText("");
        cantidad.requestFocus();
        cantidad.setSelectAllOnFocus(true);
        v_Adacliente = new ClienteAdapter(new ArrayList(),this);
        v_listaclientes.setAdapter(v_Adacliente);

        int[] colors = {0, 0xFF000000, 0};
        v_listaclientes.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        v_listaclientes.setDividerHeight(1);
        busca.setOnClickListener(v-> {
            buscarCliente(cantidad.getText().toString());
            cantidad.setText("");
        });

        regresa.setOnClickListener(v-> dlgClienteCierra());

        nuevo.setOnClickListener(v-> {


        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                busca.callOnClick();
            }
            return false;
        });
        v_dlgCliente.show();
    }

    private void imprimeTicket(){
        BluetoothConnection selectedDevice = traeImpresora(v_nombre_impresora);
        ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
        Libreria.imprimeSol(impresora,v_ticket,-1);
        try {
            new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
        } catch (/*ExecutionException | InterruptedException e*/ Exception e) {
            System.out.println(e);
        }
    }

    public void setCliente(Integer pClteid,String pCliente,Boolean pLogico){
        v_cliente = pClteid;
        v_nombrecliente = pCliente;
        v_tieneCredito = pLogico;
        wsGuardaVntaClte();
    }

    public void dlgClienteCierra(){
        if(v_dlgCliente!=null){
            v_dlgCliente.dismiss();
        }
    }

}