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
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.example.aristomovil2.adapters.VentasAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.facade.Estatutos;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.servicio.ServicioImpresionTicket;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Impresora;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Ventas extends ActividadBase {
    public int clienteId, estacionID;
    private int clienteDefault;
    public String cliente, impresora, folio;
    public boolean mandaimprimir, directoVenta, ventaCredito, ocultaNuevo = false;
    private ArrayList<Cliente> clientes;
    private ArrayList<Colonia> colonias;
    TableLayout tablaClientes;
    TextView txtClienteNueva;
    Dialog dialogoClt;
    Button btnCerrarNueva;
    Spinner spinnerFracc;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Ventas"));

        sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        int tipoVenta = sharedPreferences.getInt("tipoVenta", 48);
        int estaid = sharedPreferences.getInt("estaid", 0);
        directoVenta = false;// sharedPreferences.getBoolean("directoVenta", false); //****************************************************************Manda directo al punto de venta
        clienteDefault = sharedPreferences.getInt("clientedefault", -1);
        cliente = sharedPreferences.getString("nomCliente", "Publico en general");
        mandaimprimir = sharedPreferences.getBoolean("mandaimprimir", false);
        impresora = preferences.getString("impresora", "Predeterminada");
        estacionID = sharedPreferences.getInt("estaid", 0);

        FloatingActionButton btnNuevaVenta = findViewById(R.id.btnVentasNueva);
        LinearLayout nuevaVenta = findViewById(R.id.nuevaVenta);
        btnCerrarNueva = findViewById(R.id.btnVentasCerrar);
        EditText editClienteBuscar = findViewById(R.id.editVentaClienteBuscar);
        Button btnBuscarCliente = findViewById(R.id.btnVentasBuscarcliente);
        Button btnNuevoCliente = findViewById(R.id.btnVentasNuevoCliente);
        tablaClientes = findViewById(R.id.tableVentasClientes);
        txtClienteNueva = findViewById(R.id.txtVentasCliente);

        if (clienteDefault > 0) {
            editClienteBuscar.setVisibility(View.GONE);
            btnBuscarCliente.setVisibility(View.GONE);
            btnNuevoCliente.setVisibility(View.GONE);
            txtClienteNueva.setText(cliente);
        }

        folio = "";
        clienteId = -1;
        ventaCredito = false;

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        nuevaVenta.startAnimation(animation);
        editClienteBuscar.setEnabled(false);
        btnBuscarCliente.setEnabled(false);
        btnNuevoCliente.setEnabled(false);

        btnNuevaVenta.setOnClickListener(v -> {
            if (directoVenta)
                puntoVenta();
            else {
                ocultaNuevo = true;
                disableEnableView(false, findViewById(R.id.ventas_content));
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
                nuevaVenta.startAnimation(anim);
                editClienteBuscar.setEnabled(true);
                btnBuscarCliente.setEnabled(true);
                btnNuevoCliente.setEnabled(true);
                clienteDefault = sharedPreferences.getInt("clientedefault", -1);
                cliente = sharedPreferences.getString("nomCliente", "Publico en general");
                txtClienteNueva.setText(cliente);

                anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
                anim.setDuration(300);
                btnNuevaVenta.startAnimation(anim);
                btnNuevaVenta.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        btnNuevaVenta.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        btnCerrarNueva.setOnClickListener(v -> {
            ocultaNuevo = false;
            disableEnableView(true, findViewById(R.id.ventas_content));
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            nuevaVenta.startAnimation(anim);
            editClienteBuscar.setEnabled(false);
            btnBuscarCliente.setEnabled(false);
            btnNuevoCliente.setEnabled(false);

            tablaClientes.removeAllViews();
            anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
            anim.setDuration(300);
            btnNuevaVenta.startAnimation(anim);
            btnNuevaVenta.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    btnNuevaVenta.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        });

        editClienteBuscar.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                btnBuscarCliente.callOnClick();
                return true;
            }
            return false;
        });

        btnBuscarCliente.setOnClickListener(v -> {
            hideKeyboard(editClienteBuscar);
            buscarCliente(editClienteBuscar.getText().toString());
        });

        btnNuevoCliente.setOnClickListener(v -> {
            dialogoClt = new Dialog(this);
            dialogoClt.setContentView(R.layout.dialogo_nuevo_cliente);
            dialogoClt.setCancelable(true);

            EditText editRazon = dialogoClt.findViewById(R.id.editDialogoNuevoClienteRazonCliente);
            EditText editAlias = dialogoClt.findViewById(R.id.editDialogoNuevoClienteAliasCliente);
            EditText editRfc = dialogoClt.findViewById(R.id.editDialogoNuevoClienteRFC);
            EditText editCorreo = dialogoClt.findViewById(R.id.editDialogoNuevoClienteCorreo);
            EditText editNota = dialogoClt.findViewById(R.id.editDialogoNuevoClienteNota);
            EditText editTelefono = dialogoClt.findViewById(R.id.editDialogoNuevoClienteTelefono);
            EditText editCalle = dialogoClt.findViewById(R.id.editDialogoNuevoClienteCalle);
            EditText editNumExt = dialogoClt.findViewById(R.id.editDialogoNuevoClienteNumExterior);
            EditText editNumInt = dialogoClt.findViewById(R.id.editDialogoNuevoClienteNumInterior);
            spinnerFracc = dialogoClt.findViewById(R.id.SpinnerDialogoNuevoclienteFraccionamiento);
            EditText editCp = dialogoClt.findViewById(R.id.editDialogoNuevoClienteCP);
            Button btnGuardar = dialogoClt.findViewById(R.id.btnDialogoNuevoClienteGuardarCliente);

            editCp.setOnKeyListener((view, i, keyEvent) -> {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    traeColonias(editCp.getText().toString());
                    hideKeyboard(view);
                    return true;
                }
                return false;
            });

            btnGuardar.setOnClickListener(view -> {
                if (editRfc.getText().toString().isEmpty() ||
                        editAlias.getText().toString().isEmpty() ||
                        editRazon.getText().toString().isEmpty() ||
                        editTelefono.getText().toString().isEmpty() ||
                        editCalle.getText().toString().isEmpty() ||
                        editNumExt.getText().toString().isEmpty() ||
                        editCp.getText().toString().isEmpty() || colonias == null) {
                    muestraMensaje("Los campos * son obligatorios", R.drawable.mensaje_warning);
                } else {
                    guardaCliente(editRazon.getText().toString(), editAlias.getText().toString(), editRfc.getText().toString(),
                            editCorreo.getText().toString(), editNota.getText().toString(),
                            editTelefono.getText().toString(), editCalle.getText().toString(),
                            editNumExt.getText().toString(), editNumInt.getText().toString(),
                            colonias.get(spinnerFracc.getSelectedItemPosition()).getClave(),
                            colonias.get(spinnerFracc.getSelectedItemPosition()).getCodigo());
                }
            });
            dialogoClt.show();
        });

        peticionWS(Enumeradores.Valores.TAREA_VENTAS, "SQL", "SQL", String.valueOf(tipoVenta), String.valueOf(estaid), "");
        //peticionWS(Enumeradores.Valores.TAREA_VENTAS, "SQL", "SQL", String.valueOf(tipoVenta), String.valueOf(estacionId), "");
    }

    /**
     * Cierra la consulta y abre el menu principal de la aplicacion
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ocultaNuevo)
            btnCerrarNueva.performClick();
        else {
            /*Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);*/
            finish();
        }
    }

    /**
     * Establece el contenido de la lista de ventas
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyVentas);
        ListView list = findViewById(R.id.ventas_list);
        list.setEmptyView(empty);
    }

    /**
     * Cambia la la pantalla del punto de venta
     */
    public void puntoVenta() {
        Intent intent = new Intent(this, PuntoVenta.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("clienteId", clienteId);
        editor.putString("nomCliente", cliente);
        editor.putString("folio", folio);
        editor.putBoolean("ventaCredito", ventaCredito);
        editor.apply();
        startActivity(intent);
        finish();
    }

    /**
     * Busca los clientes que coincidan con la busqueda
     * @param busqueda La busqueda de clientes
     */
    private void buscarCliente(String busqueda) {
        peticionWS(Enumeradores.Valores.TAREA_BUSCAR_CLIENTE, "SQL", "SQL", busqueda, "", "");
    }

    /**
     * Guarda un cliente nuevo
     * @param razon La razon del cliente
     * @param alias El alias del cliente
     * @param rfc El RFC del cliente
     * @param correo El correo del cliente
     * @param nota La nota del cliente
     * @param telefono El telefono del cliente
     * @param calle La calle del cliente
     * @param numExt El numero exterior del cliente
     * @param numInt El numero interior del cliente
     * @param fraccionamiento El fraccionamiento del cliente
     * @param cp El codigo postal del cliente
     */
    private void guardaCliente(String razon, String alias, String rfc, String correo, String nota, String telefono, String calle, String numExt, String numInt, String fraccionamiento, String cp) {
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_CLIENTE, "", "SQL", "<linea>" +
                "<rfc>" + rfc + "</rfc>" + "<alias>" + alias + "</alias>" + "<razon>" + razon + "</razon>" + "<correo>" + correo + "</correo>" +
                "<nota>" + nota + "</nota>" + "<telefono>" + telefono + "</telefono>" + "<calle>" + calle + "</calle>" + "<numext>" + numExt + "</numext>" +
                "<numint>" + numInt + "</numint>" + "<colonia>" + fraccionamiento + "</colonia>" + "<cp>" + cp + "</cp>" + "</linea>", "0", "");
    }

    private void traeColonias(String cp) {
        peticionWS(Enumeradores.Valores.TAREA_COLONIAS_CP, "SQL", "SQL", cp, "", "");
    }

    private void wsCreaVntaClte() {
        ContentValues mapa = new ContentValues();
        mapa.put("folio", "nueva");
        mapa.put("cliente", clienteId);
        mapa.put("estacion", estacionID);
        mapa.put("usua", usuarioID);
        mapa.put("tarjeta", "");
        String xml = Libreria.xmlLineaCapturaSV(mapa, "linea");
        peticionWS(Enumeradores.Valores.TAREA_VNTACLTE, "SQL", "SQL", xml, "", "");
    }

    /**
     * Llena la tabla de clientes con los resultados obtenidos en la busqueda
     */
    @SuppressLint("SetTextI18n")
    private void llenarTablaClientes() {
        tablaClientes.removeAllViews();

        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.GRAY);
        row.setGravity(Gravity.CENTER);

        /*Acción*/
        TextView textView = new TextView(this);
        textView.setText(" ");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(0, 10, 10, 10);
        row.addView(textView);

        textView = new TextView(this);
        textView.setText(" ");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        row.addView(textView);

        /*Cliente*/
        textView = new TextView(this);
        textView.setText("Cliente");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        row.addView(textView);

        /*Credito*/
        textView = new TextView(this);
        textView.setText("Credito");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(10, 10, 10, 10);
        row.addView(textView);

        tablaClientes.addView(row);

        /*Llenamos los datos de la tabla con los resultados de la búsqueda de clientes*/
        for (Cliente c : clientes) {
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER);

            //Acción
            ImageButton btnSeleccionar = new ImageButton(this);
            btnSeleccionar.setImageResource(R.drawable.check);
            btnSeleccionar.setBackgroundColor(Color.TRANSPARENT);
            btnSeleccionar.setPadding(10, 10, 10, 10);

            btnSeleccionar.setOnClickListener(view -> {
                clienteId = c.getClteid();
                cliente = c.getCliente();
                ventaCredito = c.isVntacredito();

                wsCreaVntaClte();
                /*SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tieneCredito", String.valueOf(c.getCredito()));
                editor.putString("credito", String.valueOf(false));
                editor.apply();
                if (clienteId == 0)
                    clienteId = -1;

                puntoVenta();*/
            });

            ImageButton btnVerDetalle = new ImageButton(this);
            btnVerDetalle.setImageResource(R.drawable.eye);
            btnVerDetalle.setBackgroundColor(Color.TRANSPARENT);
            btnVerDetalle.setPadding(10, 10, 10, 10);
            btnVerDetalle.setOnClickListener(view -> muestraDetalleCliente(c));

            row.addView(btnSeleccionar);
            row.addView(btnVerDetalle);

            //Cliente
            textView = new TextView(this);
            textView.setText(MessageFormat.format("{0}", c.getCliente()));
            textView.setTextColor(Color.BLACK);
            textView.setPadding(10, 10, 10, 10);
            row.addView(textView);

            //Credito
            textView = new TextView(this);
            textView.setText(c.getCredito() ? "Si" : "No");
            textView.setTextColor(Color.BLACK);
            textView.setPadding(10, 10, 10, 10);
            row.addView(textView);

            tablaClientes.addView(row);
        }
    }

    /**
     * Muestra los dellates del cliente seleccionado
     * @param cliente Instancia del cliente a mostrar
     */
    private void muestraDetalleCliente(Cliente cliente) {
        Dialog dialogo = new Dialog(this);
        dialogo.setContentView(R.layout.dialogo_info_cliente);

        ((TextView) dialogo.findViewById(R.id.txtDialogoInfoClienteCliente)).setText(MessageFormat.format("Alisas: {0}", cliente.getCliente()));
        ((TextView) dialogo.findViewById(R.id.txtDialogoInfoClienteRazon)).setText(MessageFormat.format("Razon: {0}", cliente.getRazon()));
        ((TextView) dialogo.findViewById(R.id.txtDialogoInfoClienteDomicilio)).setText(MessageFormat.format("Domicilio: {0}", cliente.getDomicilio()));
        ((TextView) dialogo.findViewById(R.id.txtDialogoInfoClienteRfc)).setText(MessageFormat.format("RFC: {0}", cliente.getRfc()));

        dialogo.show();
    }

    /**
     * Imprime un ticket con los datos del cliente creado
     * @param idCliente El id del cliente
     * @param nombre El nombre del cliente
     * @param domicilio El domicilio del cliente
     * @param clave La clave del cliente
     */
    private void imprimir(int idCliente, String nombre, String domicilio, String clave) {
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
            if(ip == null || ip.equals("") || Integer.toString(puerto) == null || Integer.toString(puerto).equals("")){
                muestraMensaje("Configuración Incompleta para Impresora",R.drawable.mensaje_error);
            } else {
                contenido = "Nuevo Cliente,T2|Id Cliente: "+idCliente+",T1|Nombre: "+nombre+",T1|Dom: "+domicilio+",T1|Clave: "+clave+",T1|.,T1";

                new Impresora(ip,contenido,puerto,espacios).execute();
            }


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
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                d.setContentView(R.layout.dial_no_permiso);
                Button btn_ok = d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(view -> {
                    d.dismiss();
                });
                d.show();
            } else {
                ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);

                impresora.addTitle("Nuevo Ciente");
                impresora.addLine("Id Cliente: <b>" + idCliente + "</b>");
                impresora.addEndLine();
                impresora.addLine("Nombre: " + nombre);
                impresora.addLine("Dom: " + domicilio);
                impresora.addEndLine();
                impresora.addLine("Clave: " + clave );
                impresora.addLine(".");
                impresora.addLine(".");
                impresora.addLine(".");

                new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
            }
        } else {
            muestraMensaje("Error en la impresión", R.drawable.mensaje_error);
        }

    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_VENTAS:{
                    ArrayList<com.example.aristomovil2.modelos.Ventas> ventas = servicio.getVentas();
                    ListView listVentas = findViewById(R.id.ventas_list);

                    VentasAdapter adapter = new VentasAdapter(ventas, this);
                    listVentas.setAdapter(adapter);
                    actualizaToolbar(ventas.size() + " Ventas en captura");
                    break;
                }
                case TAREA_BUSCAR_CLIENTE:{
                    if(output.getExito()){
                        clientes = servicio.getClientes();
                        llenarTablaClientes();
                    }
                    else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_GUARDA_CLIENTE:{
                    if (obj.getAsBoolean("exito")) {
                        int idCliente = obj.getAsInteger("cltienteid");
                        String nombre = obj.getAsString("nombre");
                        String domicilio = obj.getAsString("domicilio");
                        String clave = obj.getAsString("clave");
                        if(mandaimprimir)
                            imprimir(idCliente, nombre, domicilio, clave);
                        muestraMensaje("Cliente guardado", R.drawable.mensaje_exito);
                        dialogoClt.dismiss();
                    } else {
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_COLONIAS_CP:{
                    if(obj.getAsBoolean("exito")){
                        colonias = servicio.traeColonias();

                        ArrayList<String> coloniasNombres = new ArrayList<>();

                        for(Colonia c:colonias)
                            coloniasNombres.add(c.getColonia());

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coloniasNombres);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFracc.setAdapter(spinnerAdapter);
                    }
                    else{
                        muestraMensaje("Codigo Postal invalida", R.drawable.mensaje_error);
                    }
                }
                case TAREA_VNTACLTE:
                    if(output.getExito()){
                        ContentValues mapa=new ContentValues();
                        folio = obj.getAsString("vntafolio");
                        clienteId = obj.getAsInteger("cliente");
                        cliente=obj.getAsString("nombrecliente");
                        ventaCredito = obj.getAsBoolean("tienecredito");
                        mapa.put("folio",folio);
                        mapa.put("tienecredito",ventaCredito);
                        mapa.put("rfc",obj.getAsString("rfc"));
                        mapa.put("nombrecliente",cliente);
                        mapa.put("empr",obj.getAsInteger("empr"));
                        mapa.put("regimen",obj.getAsString("regimen"));
                        mapa.put("domicilio",obj.getAsString("domicilio"));
                        mapa.put("usuario",obj.getAsInteger("usuario"));
                        mapa.put("tipo",obj.getAsInteger("tipo"));
                        mapa.put("vntatitulo",obj.getAsString("vntatitulo"));
                        mapa.put("fecha",obj.getAsString("fecha"));
                        mapa.put("estacion",obj.getAsInteger("estacion"));
                        mapa.put("credito",obj.getAsBoolean("credito"));
                        mapa.put("solofac",obj.getAsBoolean("solofac"));
                        mapa.put("cliente",clienteId);
                        mapa.put("telefono",0);
                        mapa.put("porcocinar",0);
                        servicio.guardaBD(mapa, Estatutos.TABLA_VENTAS);
                        puntoVenta();
                    }
                    else{
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    }
                    break;
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }

        cierraDialogo();
    }
}