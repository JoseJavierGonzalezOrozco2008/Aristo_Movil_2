package com.example.aristomovil2;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.adapters.BultosAdapter;
import com.example.aristomovil2.adapters.LotesCapAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.BarcodeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Bultos extends ActividadBase {
    int documento, imprime_espacios;
    String ordenCompra, folioDi, provedorSucursal, bulto, impresora;
    public boolean codigoBarras, mandaImprimir, imprimeDetalles;
    private ArrayList<ArrayList<Bulto>> bultos;
    private BluetoothConnection selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bultos);
        inicializarActividad("");

        Bundle extras = getIntent().getExtras();

        documento = extras.getInt("documento");
        ordenCompra = extras.getString("OC");
        folioDi = extras.getString("foliodi");
        provedorSucursal = extras.getString("prov/suc");

        if (extras.getString("bulto") != null)
            bulto = extras.getString("bulto");

        SharedPreferences preferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        impresora = preferencesConf.getString("impresora", "Predeterminada");
        codigoBarras = preferencesConf.getBoolean(impresora + "CodigoBarras", true);
        mandaImprimir = preferences.getBoolean("mandaimprimir", false);
        imprimeDetalles = preferences.getBoolean("imprimedetalle", false);
        imprime_espacios = preferences.getInt("espacios", 3);

        Dialog d = new Dialog(this);
        if (mandaImprimir) {
            final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
            if (bluetoothDevicesList != null) {
                for (int i = 1; i <= bluetoothDevicesList.length; i++) {

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
                    if (bluetoothDevicesList[i - 1].getDevice().getName().equals(impresora))
                        selectedDevice = bluetoothDevicesList[i - 1];





                }
            }
        }

        listaBultos(folioDi);
    }

    /**
     * Establece el contenido de la lista de ventas
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyBultos);
        ListView list = findViewById(R.id.bultos_list);
        list.setEmptyView(empty);
    }

    /**
     * Llama al servicio que trae la lista de bultos
     * @param folio El folio del documento
     */
    private void listaBultos(String folio){
        peticionWS(Enumeradores.Valores.TAREA_LISTA_BULTOS, "SQL", "SQL", folio, "true", usuarioID);
    }

    /**
     * Llama al servicio para bajar un contenedor a captura
     * @param bulto El codigo del bulto
     */
    public void bajaCaptura(String bulto){
        peticionWS(Enumeradores.Valores.TAREA_BAJA_CAPTURA, "SQL", "SQL", bulto, "true", "");
    }

    /**
     * Llama al servicio para abrir un contenedor
     * @param bulto El codigo del bulto
     */
    public void abreContenedor(String bulto){
        peticionWS(Enumeradores.Valores.TAREA_ABRE_BULTO, "SQL", "SQL", bulto, "true", "");
    }

    /**
     * Imprime los dato de un bulto
     * @param i La posicion del bulto
     */
    public void imprimir(int i, int j){
        Dialog d = new Dialog(this);
        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
        String tipoImp = preferences.getString("tImp","");
        if(tipoImp != null && tipoImp.equals("Red")){
            String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
            int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
            String contenido = "";
            int espacios = getSharedPreferences("renglones",Context.MODE_PRIVATE).getInt("espacios",3);
            Bulto bulto=bultos.get(i).get(j);
            ServicioImpresionTicket impBult = new ServicioImpresionTicket();
            contenido = impBult.impresionBultos(bulto,impBult,Libreria.upper(provedorSucursal),usuario,codigoBarras,imprimeDetalles,imprime_espacios);

            new Impresora(ip,contenido,puerto,espacios).execute();

        }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                d.setContentView(R.layout.dial_no_permiso);
                Button btn_ok = d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
            }
            else {
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
                Bulto bulto=bultos.get(i).get(j);
                impresora = Libreria.imprimeBulto(bulto,new ServicioImpresora(selectedDevice, this),Libreria.upper(provedorSucursal),usuario,codigoBarras,imprimeDetalles,imprime_espacios);

                new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
            }
        }

    }


    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if (obj != null) {
            switch (output.getTarea()){
                case TAREA_LISTA_BULTOS:{
                    ArrayList<String> grupos = servicio.traeBultos();
                    bultos = new ArrayList<>();
                    int bultosCount = 0;

                    for(String grupo:grupos){
                        ArrayList<Bulto> bulto = servicio.getBultos(grupo.split(";")[1]);
                        bultos.add(bulto);
                        bultosCount += bulto.size();
                    }

                    ExpandableListView bultosList = findViewById(R.id.bultos_list);
                    BultosAdapter adapter = new BultosAdapter(bultos, grupos, this);
                    bultosList.setAdapter(adapter);

                    for(int i =0; i<grupos.size(); i++)
                        bultosList.expandGroup(i);

                    String title = "Pedidos: " + grupos.size() + ", Bultos: " + bultosCount;
                    actualizaToolbar(title);

                    cierraDialogo();
                    break;
                }
                case TAREA_BAJA_CAPTURA:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito")){
                        muestraMensaje("Bajando bulto", R.drawable.mensaje_exito);
                        listaBultos(folioDi);
                        bulto = obj.getAsString("anexo");
                    } else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_ABRE_BULTO:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito")){
                        muestraMensaje("Contenedor abierto", R.drawable.mensaje_exito);
                        listaBultos(folioDi);
                        bulto = obj.getAsString("anexo");
                    } else
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

    public void dlgDetBultos(String pBultos){
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_detalle_bulto);
        ExpandableListView bultosList = dialog.findViewById(R.id.detBulto_list);
        LotesCapAdapter adapter = new LotesCapAdapter(pBultos, this);
        bultosList.setAdapter(adapter);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("bulto", bulto);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}