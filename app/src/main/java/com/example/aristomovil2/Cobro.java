package com.example.aristomovil2;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Cobrados;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Cobro extends ActividadBase {
    private final NumberFormat format = NumberFormat.getCurrencyInstance();
    private String nomCliente;
    private String folio;
    private float totPagar;
    private int cuentaId, estacionId,tipoVenta;
    private boolean mandaImprimir, pagado, codigoBarras;
    private ArrayList<Cobrados> cobrados = new ArrayList<>();
    private String empresavende, domiempr, domisucu, domiclte, despedida;
    private BluetoothConnection selectedDevice;
    TextView txtMonto, txtCobrado, txtFaltante, txtCambio;
    EditText editEfectivo, editMontoCredito, editRefCredito, editMontoDebito, editRefDebito;
    Spinner spinnerCuenta;
    Button btnReimprimir, btnSalir;
    FloatingActionButton btnCobrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobro);

        Bundle extras = getIntent().getExtras();
        folio = extras.getString("folio");
        nomCliente = extras.getString("cliente");
        totPagar = extras.getFloat("total");

        SharedPreferences sharedPreferences = getSharedPreferences("renglones", Context.MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        mandaImprimir = sharedPreferences.getBoolean("mandaimprimir", false);
        String cuentaBanco = sharedPreferences.getString("cuentabanco", "");
        cuentaId = sharedPreferences.getInt("cuentaid", 0);
        estacionId = sharedPreferences.getInt("estaid", 1);
        String impresora = preferences.getString("impresora", "Predeterminada");
        codigoBarras = preferences.getBoolean(impresora + "CodigoBarras", true);
        tipoVenta = preferences.getInt("tipoVenta", 48);

        String title = folio.substring(7);
        inicializarActividad("******" + title + " " + nomCliente);

        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();
        if (bluetoothDevicesList != null) {
            for (int i = 1; i<= bluetoothDevicesList.length; i++) {
                if(bluetoothDevicesList[i-1].getDevice().getName().equals(impresora))
                    selectedDevice = bluetoothDevicesList[i-1];
            }
        }

        String[] cuentasbanco = cuentaBanco.split(",");
        ArrayList<String> cuentasNombre = new ArrayList<>();
        ArrayList<String> cuentasID = new ArrayList<>();

        if(cuentasbanco.length > 0) {
            for (String s : cuentasbanco) {
                String[] datos = s.split("\\|");
                try {
                    cuentasID.add(datos[1]);
                    cuentasNombre.add(datos[0]);
                }
                catch (RuntimeException e){
                    System.out.println("Cuanta " + s + " incorrecta");
                }
            }
        }

        txtMonto = findViewById(R.id.txtCobroMonto);
        txtCobrado = findViewById(R.id.txtcobroCobrado);
        txtFaltante = findViewById(R.id.txtcobroFaltante);
        txtCambio = findViewById(R.id.txtcobroCambio);
        editEfectivo = findViewById(R.id.editCobroEfectivo);
        editMontoCredito = findViewById(R.id.editCobroMontoCredito);
        editRefCredito = findViewById(R.id.editCobroRefCredito);
        editMontoDebito = findViewById(R.id.editCobroMontoDebito);
        editRefDebito = findViewById(R.id.editCobroRefDebito);
        spinnerCuenta = findViewById(R.id.spinnerCobroCuenta);
        btnReimprimir = findViewById(R.id.btnCobroReimprimir);
        btnSalir = findViewById(R.id.btnCobroSalir);
        btnCobrar = findViewById(R.id.btnCobroCobrar);

        btnReimprimir.setVisibility(View.GONE);
        txtMonto.setText(MessageFormat.format("${0}", totPagar));

        if(cuentaBanco.equals("")){
            LinearLayout lycuentas;
            if(cuentaId == 0)
                lycuentas = findViewById(R.id.loCobroCuentas);
            else
                lycuentas = findViewById(R.id.loCobroSpinner);
            lycuentas.setVisibility(View.GONE);
            //TextView txtMensajeCuentas = findViewById(R.id.txtCobroMensajeCuentas)
            //txtMensajeCuentas.setVisibility(View.VISIBLE)
        }
        else if(cuentasbanco.length > 1) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cuentasNombre);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCuenta.setAdapter(spinnerAdapter);

            int index = 0;

            for(int i = 0; i<cuentasID.size(); i++) {
                if(cuentasID.get(i).equals(String.valueOf(cuentaId))){
                    index = i;
                    break;
                }
            }

            spinnerCuenta.setSelection(index);
            spinnerCuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item != null) {
                        cuentaId = Integer.parseInt(cuentasID.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
        }
        else{
            cuentaId = Integer.parseInt(cuentasID.get(0));
            TextView txtCuenta = findViewById(R.id.txtCobroCuenta);
            txtCuenta.setText(cuentasNombre.get(0));
            spinnerCuenta.setVisibility(View.GONE);
        }

        btnCobrar.setOnClickListener(view -> {
            float credito;
            try {
                credito = Float.parseFloat(editMontoCredito.getText().toString());
            } catch (Exception e){
                credito = 0;
            }
            if (credito > totPagar && credito != 0){
                enviaMensaje("El pago en tarjeta debe ser exacto" );
            } else{
                wsCobraVenta();
            }

        });

        btnSalir.setOnClickListener(v -> {
            Intent intent = new Intent(this, Ventas.class);
            intent.putExtra("user", usuario);
            startActivity(intent);
            finish();
        });

        View.OnKeyListener listener = (view, i, keyEvent) -> {
            float cobrado = (editEfectivo.getText().toString().equals("")? 0:Float.parseFloat(editEfectivo.getText().toString())) +
                    (editMontoCredito.getText().toString().equals("")? 0:Float.parseFloat(editMontoCredito.getText().toString())) +
                    (editMontoDebito.getText().toString().equals("")? 0:Float.parseFloat(editMontoDebito.getText().toString()));
            float faltante = totPagar - cobrado;
            float cambio = cobrado - totPagar;
            faltante = faltante < 0? 0:faltante;
            cambio = cambio < 0? 0:cambio;

            txtCobrado.setText(format.format(cobrado));
            txtFaltante.setText(format.format(faltante));
            txtCambio.setText(format.format(cambio));

            return false;
        };

        editEfectivo.setOnKeyListener(listener);
        editMontoCredito.setOnKeyListener(listener);
        editMontoDebito.setOnKeyListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (!pagado){
            super.onBackPressed();
            Intent intent = new Intent(Cobro.this, PuntoVenta.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Imprime el ticket de la venta
     * @param caducidades Las caducidades
     */
    private void imprimir(String caducidades){
        Dialog d = new Dialog(this);
        SharedPreferences preferences = getSharedPreferences("tipoImpresion", Context.MODE_PRIVATE);
        String tipoImp = preferences.getString("tImp","");

        if(tipoImp != null && tipoImp.equals("Red")) {
            String ip = getSharedPreferences("configuracion_edit_ip_impresora", Context.MODE_PRIVATE).getString("ipImpRed", "");
            int puerto = Integer.parseInt(getSharedPreferences("configuracion_edit_puerto_impresora", Context.MODE_PRIVATE).getString("puertoImpRed", ""));
            String contenido = "";
            int espacios = getSharedPreferences("renglones", Context.MODE_PRIVATE).getInt("espacios", 3);
            if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
            } else {
                contenido += "Venta,T2|";
                contenido += empresavende+",T1|";
                contenido += domiempr+",T1|";
                contenido += "SUCURSAL,T1|";
                contenido += domisucu+",T1|";
                contenido += "Vendedor: "+usuario+",T1|";
                contenido += "CLiente: "+nomCliente+",T1|";
                contenido += domiclte+",T1|";
                contenido += folio+",T2|";
                contenido += "Prod   Can   Precio   Subtotal,T1|";
                contenido += "----------------,T1|";
                /*Productos comprados*/
                for (int i = 0; i < cobrados.size(); i++) {
                    contenido += cobrados.get(i).getProducto() + " " + cobrados.get(i).getCantidad() + " x $" + cobrados.get(i).getPreciou() + " $" + cobrados.get(i).getSubtotal()+",T1|";
                }
                contenido += "----------------,T1|";

                float efectivo, credito, pagoTot;
                try {
                    efectivo = Float.parseFloat(editEfectivo.getText().toString());
                } catch (Exception e) {
                    efectivo = 0;
                }
                try {
                    credito = Float.parseFloat(editMontoCredito.getText().toString());
                } catch (Exception e) {
                    credito = 0;
                }
                pagoTot = efectivo + credito;

                contenido += "TOTAL: "+txtMonto.getText()+",T2|";
                contenido += "PAGO: "+format.format(pagoTot)+",T2|";
                contenido += "CAMBIO: "+txtCambio.getText().toString()+",T2|";

                if (codigoBarras)
                    contenido += folio+",**|";
                else
                    contenido += folio+",T1|";

                contenido += despedida+",T1";

                if (null != caducidades && !caducidades.equals("")) {
                    contenido += "|Caducidades,T2";
                    contenido += "|Cod   Prod   Lote   Fecha   Cant,T1";
                    contenido += "|----------------,T1";

                    String[] caducidad = caducidades.split("\\|");

                    for (String c : caducidad) {
                        String[] datos = c.split(",");
                        int i = 0;
                        StringBuilder txtImprimir = new StringBuilder();

                        while (i < 5) {
                            if (i == 0)
                                txtImprimir = new StringBuilder(datos[i++] + " ");

                            while (i < 5 && (txtImprimir + datos[i] + " ").length() <= 32) {
                                txtImprimir.append(datos[i]).append(" ");
                                i++;
                            }
                            contenido += "|"+txtImprimir.toString()+",T1";
                            txtImprimir = new StringBuilder();
                        }
                    }
                    contenido += "|----------------,T1";
                }
                new Impresora(ip,contenido,puerto,espacios);
            }


        }else if(tipoImp != null && tipoImp.equals("Bluethooth")){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                d.setContentView(R.layout.dial_no_permiso);
                Button btn_ok = d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            else {
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

                impresora.addTitle("Venta");
                impresora.addLine(empresavende);
                impresora.addLine(domiempr);
                impresora.addLine("SUCURSAL");
                impresora.addLine(domisucu);
                impresora.addEndLine();
                impresora.addLine("Vendedor: " + usuario);
                impresora.addLine("Cliente: " + nomCliente);
                impresora.addLine(domiclte);
                impresora.addTitle(folio);
                impresora.addEndLine();
                impresora.addLine("Prod | Can | Precio | Subtotal");
                impresora.addLine("----------------");
                /*Productos comprados*/
                for (int i = 0; i < cobrados.size(); i++) {
                    impresora.addLine(cobrados.get(i).getProducto() + " " + cobrados.get(i).getCantidad() + " x $" + cobrados.get(i).getPreciou() + " $" + cobrados.get(i).getSubtotal());
                }
                impresora.addLine("----------------");

                float efectivo, credito, pagoTot;
                try {
                    efectivo = Float.parseFloat(editEfectivo.getText().toString());
                } catch (Exception e) {
                    efectivo = 0;
                }
                try {
                    credito = Float.parseFloat(editMontoCredito.getText().toString());
                } catch (Exception e) {
                    credito = 0;
                }
                pagoTot = efectivo + credito;

                impresora.addTitle("TOTAL: " + txtMonto.getText());
                impresora.addTitle("PAGO: " + format.format(pagoTot));
                impresora.addTitle("CAMBIO: " + txtCambio.getText().toString());
                impresora.addEndLine();

                if (codigoBarras)
                    impresora.addBarcodeImage(folio, 400, 100, BarcodeFormat.CODE_128);
                else
                    impresora.addBarcode(folio);

                impresora.addEndLine();
                impresora.addLine(despedida);
                impresora.addEndLine();

                if (null != caducidades && !caducidades.equals("")) {
                    impresora.addTitle("Caducidades");
                    impresora.addEndLine();
                    impresora.addLine("Cod | Prod | Lote | Fecha | Cant");
                    impresora.addLine("----------------");

                    String[] caducidad = caducidades.split("\\|");

                    for (String c : caducidad) {
                        String[] datos = c.split(",");
                        int i = 0;
                        StringBuilder txtImprimir = new StringBuilder();

                        while (i < 5) {
                            if (i == 0)
                                txtImprimir = new StringBuilder(datos[i++] + " ");

                            while (i < 5 && (txtImprimir + datos[i] + " ").length() <= 32) {
                                txtImprimir.append(datos[i]).append(" ");
                                i++;
                            }
                            impresora.addLine(txtImprimir.toString());
                            txtImprimir = new StringBuilder();
                        }
                        impresora.addEndLine();
                    }
                    impresora.addLine("----------------");
                    impresora.addEndLine(2);
                }
                new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
            }
        } else{
            muestraMensaje("Error en la impresión", R.drawable.mensaje_error);
        }

    }

    /**
     * Procesa la repsuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();

        if(obj != null){
            if (output.getTarea() == Enumeradores.Valores.TAREA_COBRAR || output.getTarea() == Enumeradores.Valores.TAREA_COBRAR_WS) {
                if (output.getExito()) {
                    editEfectivo.setEnabled(false);
                    spinnerCuenta.setEnabled(false);
                    editMontoCredito.setEnabled(false);
                    editMontoDebito.setEnabled(false);
                    editRefCredito.setEnabled(false);
                    editRefDebito.setEnabled(false);
                    btnCobrar.setEnabled(false);
                    btnSalir.setVisibility(View.VISIBLE);

                    float cambio = obj.getAsFloat("cambioef");
                    txtCambio.setText(format.format(Math.abs(cambio)));
                    pagado = true;

                    cobrados = servicio.getCobrados();
                    empresavende = obj.getAsString("empresavende");
                    domiempr = obj.getAsString("domiempr");
                    domisucu = obj.getAsString("domisucu");
                    domiclte = obj.getAsString("domiclte");
                    despedida = obj.getAsString("despedida");

                    if (mandaImprimir) {
                        btnReimprimir.setVisibility(View.VISIBLE);
                        btnReimprimir.setOnClickListener(view -> {
                            btnReimprimir.setEnabled(false);

                            if (obj.containsKey("caducidad"))
                                imprimir(obj.getAsString("caducidad"));
                            else
                                imprimir("");

                            new CountDownTimer(4000, 1000) {
                                @Override
                                public void onTick(long l) {
                                }

                                @Override
                                public void onFinish() {
                                    btnReimprimir.setEnabled(true);
                                }
                            }.start();
                        });

                        if (obj.containsKey("caducidad"))
                            imprimir(obj.getAsString("caducidad"));
                        else
                            imprimir("");
                    }

                    cierraDialogo();
                    muestraMensaje(obj.getAsString("Gracias por su compra"), R.drawable.mensaje_exito);
                } else {
                    muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    cierraDialogo();
                }
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }
    private void wsCobraVenta(){
        ContentValues mapa=new ContentValues();
        mapa.put("folioventa",folio);
        mapa.put("estacion",estacionId);
        mapa.put("usuaid",usuarioID);
        mapa.put("efectivo",editEfectivo.getText().toString());
        mapa.put("tcredito",editMontoCredito.getText().toString());
        mapa.put("rcredito",editRefCredito.getText().toString());
        mapa.put("tdebito",editMontoDebito.getText().toString());
        mapa.put("rdebito",editRefDebito.getText().toString());
        mapa.put("propina","");
        mapa.put("vales","");
        mapa.put("ccredito", (!editMontoCredito.getText().equals("") && !editRefCredito.getText().equals("") ? cuentaId:0));
        mapa.put("cdebito",(!editMontoDebito.getText().equals("") && !editRefDebito.getText().equals("") ? cuentaId:0));

        String xml= Libreria.xmlLineaCapturaSV(mapa,"linea");
                /*"<linea><folioventa>" + folio + "</folioventa><estacion>" + estacionId + "</estacion><usuaid>" + usuarioID + "</usuaid><efectivo>" +
                editEfectivo.getText() + "</efectivo><tcredito>" + editMontoCredito.getText() + "</tcredito><rcredito>" + editRefCredito.getText()
                + "</rcredito><tdebito>" + editMontoDebito.getText() + "</tdebito><rdebito>" + editRefDebito.getText() +
                "</rdebito><propina></propina><vales></vales><ccredito>" +
                (!editMontoCredito.getText().equals("") && !editRefCredito.getText().equals("") ? cuentaId:0) + "</ccredito><cdebito>" +
                (!editMontoDebito.getText().equals("") && !editRefDebito.getText().equals("") ? cuentaId:0) + "</cdebito> </linea>";*/
        if(tipoVenta == 52){
            peticionWS(Enumeradores.Valores.TAREA_COBRAR, "SQL", "SQL",xml,"", "");
        }else{
            peticionWS(Enumeradores.Valores.TAREA_COBRAR_WS, "", "",xml,folio, "");
        }

    }
}
