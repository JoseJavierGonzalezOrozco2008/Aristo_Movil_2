package com.example.aristomovil2;

import static com.example.aristomovil2.facade.Estatutos.TABLA_GENERICA;
import static com.example.aristomovil2.facade.Estatutos.TABLA_RENGLON;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Ddincontrolador;
import com.example.aristomovil2.adapters.DocumentosAdapter;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.modelos.Documento;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Proveedor;
import com.example.aristomovil2.modelos.Subalmacen;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Inventario extends ActividadBase {
    public int documento, RSprveedorID,estadoProd,v_estacion;
    private String sucuId, RSproveedor, RSordenCompra, OCNuevo, provSucuNuevo, listaPedidos;
    public String divisa;
    public boolean surtidor, nuevoCaptura, ocultaFiltros = false, ocultaRecibe = false, capDivisa;
    private ArrayList<Documento> documentos;
    private ArrayList<Generica> subalmacenes;
    private ArrayList<Proveedor> proveedores;
    private ArrayList<Cliente> clientes;
    private ArrayList<String> subalmacenesNombre;
    public ListView listDocumentos, listProvedoresSucursales, listProvedoresSucursalesBuscar, listClientesBuscar;
    private LinearLayout recibeSurte,lyMultiSurtido;
    private FloatingActionButton btnBuscar;
    private Button btnProveedorSucursal, btnCerrarRS, btnCerrar;
    private RadioButton radioNuevo, radioCaptura;
    private DocumentosAdapter adapter;
    private Proveedor prov;
    private Subalmacen subalmacen;
    public final NumberFormat format = NumberFormat.getCurrencyInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        Bundle extras = getIntent().getExtras();

        inicializarActividad2("","");
        actualizaToolbar2(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Inventario")+" "+v_nombreestacion,"");
        assert extras != null;
        documento = extras.getInt("documento");
        nuevoCaptura = documento!=17;

        format.setMaximumFractionDigits(2);

        SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        surtidor = sharedPreferences.getBoolean("surtidorChecked", false);
        sucuId = sharedPreferences.getString("sucuID", "-1");
        capDivisa = sharedPreferences.getBoolean("capdivisa", false);
        estadoProd = sharedPreferences.getInt("estadoprod", 84);
        v_estacion = sharedPreferences.getInt("estaid", 0);
        divisa = "";
        //estadoProd = documento == 14 ? estadoProd : 84 ;
        subalmacenes = new ArrayList<>();
        subalmacenesNombre = new ArrayList<>();
        //subalmacenesNombre.add("Disponible");

        radioNuevo = findViewById(R.id.radioInventarioNuevo);
        radioCaptura = findViewById(R.id.radioInvetarioCaptura);
        RadioGroup groupNuevoCaptura = findViewById(R.id.groupInventarioNuevoCaptura);
        Button btnRecibeSurte = findViewById(R.id.btnInventarioRecibeSurte);
        Button btnFiltrar = findViewById(R.id.btnBuscarInventarioBuscar);
        btnCerrar = findViewById(R.id.btnBuscarInventarioCerrar);
        btnCerrarRS = findViewById(R.id.btnRSInventarioCerrar);
        Button btnGuardaRS = findViewById(R.id.btnRSInventarioGuardar);
        Button btnQrCode = findViewById(R.id.btnQRcode);
        btnProveedorSucursal = findViewById(R.id.btnRSInventariosProveedorSucursal);
        Button btnProveedorSucursalBuscar = findViewById(R.id.btnProveedorSucursalInventarios);
        Button btnClienteBuscar = findViewById(R.id.btnClienteInventarios);
        LinearLayout filtrar = findViewById(R.id.buscarOrdenInventario);
        LinearLayout lineaDiasCred = findViewById(R.id.linearDiasCred);
        lyMultiSurtido= findViewById(R.id.lyMulti);
        TextView txtDesde = findViewById(R.id.txtBuscarInventarioDesde);
        TextView txtHasta = findViewById(R.id.txtBuscarInventarioHasta);
        EditText editOrdenPedido = findViewById(R.id.editOrdenPedidoInventarios);
        EditText editProveedorSucursalBuscar = findViewById(R.id.editProveedorSucursalInventarios);
        EditText editClienteBuscar = findViewById(R.id.editClienteInventarios);
        EditText editProveedorSucursal = findViewById(R.id.editRSInventarioProveedorSucursal);
        EditText editFechaRS = findViewById(R.id.editRSInventarioFecha);
        EditText editFacturaRS = findViewById(R.id.editRSInventarioFactura);
        EditText editCostoRS = findViewById(R.id.editRSInventarioCosto);
        EditText editDiascred = findViewById(R.id.editDiasCredito);
        EditText editNotasRS = findViewById(R.id.editRSInventarioCNotas);
        EditText editDivisaRS = findViewById(R.id.editRSInventarioDivisa);
        EditText editUUID = findViewById(R.id.editUUID);
        Spinner spinnerSubalmacenes = findViewById(R.id.spinnerRSInventariosSubAlmacen);
        CheckBox misPedidos = findViewById(R.id.checkMisPedidosInventarios);
        recibeSurte = findViewById(R.id.RecibeSurteInventario);
        btnBuscar = findViewById(R.id.btnInventarioBuscar);
        listDocumentos = findViewById(R.id.inventario_list);
        listProvedoresSucursales = findViewById(R.id.listRSProveedoresSucursales);
        listProvedoresSucursalesBuscar = findViewById(R.id.listRSProveedoresSucursalesBuscar);
        listClientesBuscar = findViewById(R.id.listRSClienteBuscar);
        EditText solicitud=findViewById(R.id.editRSInventariosOrdenCompra);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mensaje_init);
        filtrar.startAnimation(animation);
        recibeSurte.startAnimation(animation);
        filtrar.setVisibility(View.GONE);
        recibeSurte.setVisibility(View.GONE);
        findViewById(R.id.linearFacturaInventarios).setVisibility(View.VISIBLE);
        radioNuevo.setChecked(true);



        btnRecibeSurte.setText(documento==14||documento==17?"Recibe":"Surte");
        btnRecibeSurte.setVisibility(documento==17?View.GONE:View.VISIBLE);
        groupNuevoCaptura.setVisibility(documento==17?View.GONE:View.VISIBLE);
        btnProveedorSucursal.setText(documento==14?"Proveedor":documento==16?"Sucursal":"");
        editProveedorSucursal.setHint(documento==14?"Proveedor...":documento==16?"Sucursal...":"");
        lineaDiasCred.setVisibility(documento==14?View.VISIBLE:View.GONE);
        spineUbicaciones();
        servicio.borraDatosTabla(TABLA_RENGLON);
        groupNuevoCaptura.setOnCheckedChangeListener((radioGroup, i) -> {
            if(radioCaptura.isChecked()){
                nuevoCaptura = false;
                btnRecibeSurte.setEnabled(false);
                findViewById(R.id.linearFacturaInventarios).setVisibility(View.VISIBLE);
                traeCatalogos(armaXmlCat(true,1), "false", "1");
            }
            if(radioNuevo.isChecked()){
                nuevoCaptura = true;
                btnRecibeSurte.setEnabled(true);
                findViewById(R.id.linearFacturaInventarios).setVisibility(View.GONE);
                traeCatalogos(armaXmlCat(false,1), "true", "1");
            }

        });

        solicitud.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                if(documento==14){
                    String dato=solicitud.getText().toString();
                    if(!Libreria.tieneInformacion(solicitud.getText().toString())){
                        dlgMensajeError("Debe de capturar la orden",R.drawable.mensaje_error2);
                        return true;
                    }
                    traeOrco(dato);
                }
            }
            return false;
        });
        btnRecibeSurte.setOnClickListener(v -> {
            if(documento == 17)
                busquedaFolio();
            else
                surte(0, "", "", "","","",null,0);
        });

        //btnQrCode.setVisibility(documento == 14 ? View.VISIBLE : View.GONE);
        btnQrCode.setVisibility(View.GONE);
        btnQrCode.setOnClickListener(view -> {
            barcodeEscaner();
        });

        btnCerrarRS.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            recibeSurte.startAnimation(anim);

            ocultaRecibe = false;


            proveedores = null;

            listProvedoresSucursales.setVisibility(View.GONE);
            editProveedorSucursal.setText("");
            editCostoRS.setText("");
            editFechaRS.setText("");
            editFacturaRS.setText("");
            editNotasRS.setText("");
            editDiascred.setText("");
            editUUID.setText("");
            hideKeyboard(v);

            anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
            anim.setDuration(300);
            btnBuscar.startAnimation(anim);
            btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { btnBuscar.setVisibility(View.VISIBLE); }

                @Override
                public void onAnimationEnd(Animation animation) {
                    disableEnableView(true, findViewById(R.id.inventario_content));
                    recibeSurte.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        btnGuardaRS.setOnClickListener(v -> {
            if(documento == 14){
                String provsuc=editProveedorSucursal.getText().toString();
                String fecha=editFechaRS.getText().toString();
                String factura=editFacturaRS.getText().toString();
                String total=editCostoRS.getText().toString();
                Integer almacen=servicio.traeDcatIdporAbrevi(14,Libreria.traeInfo(spinnerSubalmacenes.getSelectedItem()+""));
                String porCapturar="Llenar los campos:";
                String campos="";
                if(!Libreria.tieneInformacion(provsuc))
                    campos+="\n"+"Proveedor";
                if(!Libreria.tieneInformacion(fecha))
                    campos+="\n"+"Fecha";
                if(!Libreria.tieneInformacion(factura))
                    campos+="\n"+"Factura";
                if(!Libreria.tieneInformacion(total))
                    campos+="\n"+"Total";
                if(almacen == 0)
                    campos+="\n"+"Subalmacen";
                if(prov.getProvid() == 0)
                    campos+="\n"+"Seleccione el proveedor";
                if(Libreria.tieneInformacion(campos)){
                    dlgMensajeError(porCapturar+campos, R.drawable.mensaje_warning);
                    return;
                }
                String midivisa= editDivisaRS.getText().toString();
                String uuid = editUUID.getText().toString();
                if(capDivisa && !Libreria.isNumeric(midivisa)){
                    dlgMensajeError("captura el tipo de cambio",R.drawable.mensaje_warning);
                    return ;
                }
                RSordenCompra = solicitud.getText().toString();
                OCNuevo = RSordenCompra;
                divisa = midivisa;
                provSucuNuevo = provsuc;
                String dias=editDiascred.getText().toString();

                guardaDatos("true", documento, String.valueOf(prov.getProvid()), fecha,factura, total,editNotasRS.getText().toString(),
                        almacen, RSordenCompra, divisa.equals("")? 1:Float.parseFloat(divisa),Libreria.tieneInformacion(dias) ? Integer.parseInt(dias):1,uuid);
                btnCerrarRS.performClick();

            }
            else if(documento == 16){
                if(editProveedorSucursal.getText().toString().isEmpty())
                    dlgMensajeError("Selecciona sucursal", R.drawable.mensaje_warning);
                else{
                    OCNuevo = RSordenCompra;
                    provSucuNuevo = editProveedorSucursal.getText().toString();
                    guardaDatos("true", documento, String.valueOf(prov.getProvid()), "", "", "0", editNotasRS.getText().toString(), estadoProd, RSordenCompra, 1,1,"");
                    btnCerrarRS.performClick();
                }
            }
        });

        listProvedoresSucursales.setOnItemClickListener((adapterView, view, i, l) -> {
            editProveedorSucursal.setText(proveedores.get(i).getAliasp());
            prov.setProvid(proveedores.get(i).getProvid());
            editDiascred.setText(proveedores.get(i).getDiascred()+"");
            listProvedoresSucursales.setVisibility(View.GONE);
        });

        btnProveedorSucursal.setOnClickListener(v -> {
            listaProveedores(editProveedorSucursal.getText().toString());
            listProvedoresSucursales.setVisibility(View.VISIBLE);
        });

        editFechaRS.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(editFechaRS);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        editFechaRS.setOnFocusChangeListener((view, b) -> {
            if(b){
                DialogFragment newFragment = new DatePickerFragment(editFechaRS);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });



        btnBuscar.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
            filtrar.startAnimation(anim);
            filtrar.setVisibility(View.VISIBLE);
            ocultaFiltros = true;

            misPedidos.setChecked(surtidor);
            prov = new Proveedor();

            anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
            anim.setDuration(300);
            btnBuscar.startAnimation(anim);
            btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    disableEnableView(false, findViewById(R.id.inventario_content));
                    btnBuscar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        });

        btnCerrar.setOnClickListener(v -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_close);
            filtrar.startAnimation(anim);
            listProvedoresSucursalesBuscar.setVisibility(View.GONE);
            listClientesBuscar.setVisibility(View.GONE);
            ocultaFiltros = false;

            editProveedorSucursalBuscar.setText("");
            editClienteBuscar.setText("");

            anim = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
            anim.setDuration(300);
            btnBuscar.startAnimation(anim);
            btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) {
                    disableEnableView(true, findViewById(R.id.inventario_content));
                    btnBuscar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) { filtrar.setVisibility(View.GONE); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        btnFiltrar.setOnClickListener(v -> {
            String opcion=String.valueOf(((RadioButton) findViewById(R.id.radioBuscarInventarioFecha)).isChecked() ? 1 :
                    ((RadioButton) findViewById(R.id.radioBuscarInventarioProveedor)).isChecked() ? 2 :
                            ((RadioButton) findViewById(R.id.radioBuscarInventarioOrden)).isChecked() ? 3 : 2
            );

            traeCatalogos(armaXmlCat(!nuevoCaptura,Libreria.tieneInformacionEntero(opcion,1)),String.valueOf(nuevoCaptura), opcion);
            btnCerrar.performClick();
        });

        txtDesde.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(txtDesde);
            newFragment.show(getSupportFragmentManager(), "datePicker");

        });

        txtHasta.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(txtHasta);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        btnProveedorSucursalBuscar.setOnClickListener(v -> {
            listaProveedores(editProveedorSucursal.getText().toString());
            listProvedoresSucursalesBuscar.setVisibility(View.VISIBLE);
        });

        btnClienteBuscar.setOnClickListener(v -> {
            listaClientes(editClienteBuscar.getText().toString());
            listClientesBuscar.setVisibility(View.VISIBLE);
        });

        listProvedoresSucursalesBuscar.setOnItemClickListener((adapterView, view, i, l) -> {
            editProveedorSucursalBuscar.setText(proveedores.get(i).getAliasp());
            prov.setProvid(proveedores.get(i).getProvid());
            listProvedoresSucursalesBuscar.setVisibility(View.GONE);
        });

        listClientesBuscar.setOnItemClickListener((adapterView, view, i, l) -> {
            editClienteBuscar.setText(clientes.get(i).getCliente());
            /*prov.setProvid(proveedores.get(i).getProvid());*/
            listClientesBuscar.setVisibility(View.GONE);
        });

        misPedidos.setOnCheckedChangeListener((compoundButton, b) -> surtidor = b);

        if(documento == 17)
            traeCatalogos(armaXmlCat(true,1), "false", "1");
        else
            traeCatalogos(armaXmlCat(false,1), "true", "1");
        if(documento == 16){
            findViewById(R.id.linearUUID).setVisibility(View.GONE);
            findViewById(R.id.linearRSCosto).setVisibility(View.GONE);
            findViewById(R.id.linearRSFactura).setVisibility(View.GONE);
            findViewById(R.id.linearRSFecha).setVisibility(View.GONE);
            findViewById(R.id.linearRSSubalmacen).setVisibility(View.GONE);
            findViewById(R.id.linearRSDivisa).setVisibility(View.GONE);
            btnProveedorSucursalBuscar.setText("SUCURSAL");
            ((RadioButton)findViewById(R.id.radioBuscarInventarioProveedor)).setText("Cliente/Prov");
            ((TextView)findViewById(R.id.txtRSInventariosOrdenCompra)).setText("Orden de envio: ");
            //surtidor = false;

            listDocumentos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listDocumentos.setMultiChoiceModeListener(new MultiChoiceModeListener(){
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    if(documentos.get(position).getSurtid() == 0 || documentos.get(position).getSurtidor().equals(usuario)) {
                        documentos.get(position).setSelected(checked);
                        adapter.notifyDataSetChanged();
                    }
                    else if(checked) {
                        listDocumentos.setItemChecked(position, false);
                        dlgMensajeError("Pedido asignado a otro surtidor", R.drawable.mensaje_warning);
                    }
                }

                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    StringBuilder pedidos = new StringBuilder();

                    boolean asignad=true;
                    for (Documento c : documentos) {
                        if (c.isSelected()) {
                            if(asignad)
                                asignad=c.getGrupo()==1;
                            if (pedidos.toString().equals(""))
                                pedidos.append(c.getPedido());
                            else
                                pedidos.append(",").append(c.getPedido());
                        }
                    }

                    switch(item.getItemId()){
                        case R.id.itemDocumentoGuardaTarea: {
                            peticionWS(Enumeradores.Valores.TAREA_GUARDA_TAREA, "SQL", "SQL", usuarioID, pedidos.toString(), "");
                            mode.finish();
                            return true;
                        }
                        case R.id.itemDocumentoSurteTarea: {
                            if(!asignad){
                                dlgMensajeError("No se ha asignado los pedidos a una tarea",R.drawable.mensaje_error);
                                return false;
                            }
                            listaPedidos = pedidos.toString();
                            if(nuevoCaptura){
                                String pattern = "yyyy-MM-dd";
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String date = simpleDateFormat.format(new Date());
                                peticionWS(Enumeradores.Valores.TAREA_SURTE_TAREA, "SQL", "SQL",
                                        xmlDI("true",documento,"",date,"","","",estadoProd,"0",Libreria.tieneInformacionFloat(divisa,1),0,""),
                                        "",
                                        "");
                                /*"<linea><nuevo>true</nuevo><difolio></difolio><sucudest></sucudest>" +
                                                "<usuaid>" + usuarioID + "</usuaid><mvdi>" + documento + "</mvdi><edpr>"+estadoProd+"</edpr><prov>" +
                                                "</prov><pedi>0</pedi><fefac>" + date + "</fefac><refr></refr><totfac></totfac><notas></notas>" +
                                                "<notadif></notadif><vfolio></vfolio><xmlfac></xmlfac></linea>",*/
                            }
                            else{
                                System.out.println("doc:"+documento);
                                if(documento==16){
                                    cambiaDdinControlador(-1);
                                }else{
                                    Intent intent = new Intent(getApplicationContext(), RecibeDocumento.class);
                                    intent.putExtra("documento", documento);
                                    intent.putExtra("OC", "0");
                                    intent.putExtra("foliodi", "00000");
                                    intent.putExtra("prov/suc", "");
                                    intent.putExtra("pedidos", listaPedidos);
                                    startActivity(intent);
                                }

                            }
                            mode.finish();
                        }
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu_documento, menu);
                    menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    adapter.setInSelection(true);
                    lyMultiSurtido.setVisibility(View.VISIBLE);
                    disableEnableView(false, findViewById(R.id.linearInventario));
                    findViewById(R.id.linearInventario).setVisibility(View.GONE);
                    btnBuscar.setEnabled(false);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    for(Documento c:documentos)
                        c.setSelected(false);
                    adapter.notifyDataSetChanged();
                    adapter.setInSelection(false);
                    disableEnableView(true, findViewById(R.id.linearInventario));
                    btnRecibeSurte.setEnabled(nuevoCaptura);
                    btnBuscar.setEnabled(true);
                    lyMultiSurtido.setVisibility(View.GONE);
                    findViewById(R.id.linearInventario).setVisibility(View.VISIBLE);
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // Here you can perform updates to the CAB due to
                    // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                    return false;
                }
            });
        }
        if(documento == 14){
            btnProveedorSucursalBuscar.setText("PROVEEDOR");
            findViewById(R.id.linearBuscaCliente).setVisibility(View.GONE);

            if(!capDivisa)
                findViewById(R.id.linearRSDivisa).setVisibility(View.GONE);
        }
        subalmacenes = servicio.traeDcatGenerica(14);
        subalmacenesNombre = servicio.traeDcatalogo(14);
        spineUbicaciones();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        String menuDocs="Ultimos docs";
        switch(documento){
            case 14:
                menuDocs="Ultimas compras";
                break;
            case 16:
                menuDocs="Ultimos surtidos";
                break;
            case 17:
                menuDocs="Ultimos traspasos";
                break;
        }
        menu.add(Menu.NONE, 1, Menu.NONE, menuDocs);

        return true;
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case 1:
                repoDocumento();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult.getContents() != null ) {
                //wsBuscaprod(intentResult.getContents());
                System.out.println("qr---->"+intentResult.getContents());
            } else{
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
            }
        }
    }

    public void cambiaDdinControlador(Integer pPosicion){
        Intent intent = new Intent(getApplicationContext(), Ddincontrolador.class);
        intent.putExtra("documento", documento);
        if(pPosicion == -1){
            intent.putExtra("OC", "0");
            intent.putExtra("foliodi", "00000");
            intent.putExtra("prov/suc", "");
            intent.putExtra("pedidos", listaPedidos);
            intent.putExtra("divisa", "1.0");
            intent.putExtra("total", "0.0");
        }else{
            int pedido = documentos.get(pPosicion).getPedido();
            final String ordenCompra = pedido > 0 || pedido < -1? pedido+"":"Sin OC";
            final String folioDi = documentos.get(pPosicion).getFoliodi() != null? documentos.get(pPosicion).getFoliodi():"Sin folio";
            intent.putExtra("OC", ordenCompra);
            intent.putExtra("foliodi", folioDi);
            intent.putExtra("prov/suc", documentos.get(pPosicion).getProveedor());
            intent.putExtra("pedidos", ordenCompra);
            intent.putExtra("bulto", documentos.get(pPosicion).getBulto());
            intent.putExtra("divisa", documentos.get(pPosicion).getDivisa()+"");
            intent.putExtra("total", documentos.get(pPosicion).getImporte()+"");
        }
        startActivity(intent);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private final TextView txt;
        private String formato;

        public DatePickerFragment(TextView txt){
            this.txt = txt;
            formato = "yyMMdd";
        }

        public DatePickerFragment(TextView txt,String pFormato){
            this.txt = txt;
            formato = pFormato;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            if(Libreria.tieneInformacion(this.txt.getText().toString())){
                c.setTime(Libreria.texto_to_fecha(this.txt.getText().toString(),formato));
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String coloca="";
            if(formato.length()<=6){
                coloca =""+ (year-2000)  + (month<9? "0"+(month+1):month+1)+ (day<10? "0"+day:day);
            }else if(formato.contains("-")){
                coloca =(year) + "-" + (month<9? "0"+(month+1):month+1)+ "-" +(day<10? "0"+day:day);
            }
            txt.setText(coloca);
        }
    }

    @Override
    public void onBackPressed() {
        if(ocultaFiltros)
            btnCerrar.performClick();
        else if(ocultaRecibe)
            btnCerrarRS.performClick();
        else{
            /*Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);*/
            finish();
        }
    }

    public void spineUbicaciones(){
        Spinner spinnerSubalmacenes = findViewById(R.id.spinnerRSInventariosSubAlmacen);
        ArrayAdapter<String> adapterSubalmacenes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subalmacenesNombre);
        adapterSubalmacenes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubalmacenes.setAdapter(adapterSubalmacenes);

        spinnerSubalmacenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();

                for(Generica s:subalmacenes){
                    if(s.getTex1().equals(item)){
                        subalmacen.setDcatid(s.getId());
                        //System.out.println(s.getDcatid());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                dlgMensajeError("Nada seleccionado", R.drawable.mensaje_warning);
            }
        });
        for(Generica sub:subalmacenes){
            if(sub.getId() == estadoProd){
                String compareValue=sub.getTex1();
                if (compareValue != null) {
                    int spinnerPosition = adapterSubalmacenes.getPosition(compareValue);
                    spinnerSubalmacenes.setSelection(spinnerPosition);
                    break;
                }
            }
        }

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        divisa="";
        if(radioCaptura.isChecked() )
            traeCatalogos(armaXmlCat(true,1), "false", "1");
        if(radioNuevo.isChecked() )
            traeCatalogos(armaXmlCat(false,1), "true", "1");
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyDocumentos);
        ListView list = findViewById(R.id.inventario_list);
        list.setEmptyView(empty);
    }

    /**
     * Llama al servicio que trae la lista de documentos
     * @param dato1 Dato 1
     * @param dato2 Dato 2
     * @param dato3 Dato 3
     */
    private void traeCatalogos(String dato1, String dato2, String dato3){
        peticionWS(Enumeradores.Valores.TAREA_CATALOGOS, "SQL", "SQL", dato1, dato2, dato3);
    }

    private String armaXmlCat(boolean encaptura,int orden){
        EditText editOrdenPedido = findViewById(R.id.editOrdenPedidoInventarios);
        TextView txtDesde = findViewById(R.id.txtBuscarInventarioDesde);
        TextView txtHasta = findViewById(R.id.txtBuscarInventarioHasta);
        EditText cliente =findViewById(R.id.editClienteInventarios);
        String xml="<linea>";
        if( prov!=null && prov.getProvid() != 0 ){
            System.out.println("se lleno con "+prov);
            xml +="<prov>"+prov.getProvid()+"</prov>";
        }
        if(Libreria.tieneInformacion(editOrdenPedido.getText().toString())){
            xml +="<pedi>"+editOrdenPedido.getText().toString()+"</pedi>";
        }
        if(Libreria.tieneInformacion(txtDesde.getText().toString())){
            xml +="<feini>"+txtDesde.getText().toString()+"</feini>";
        }
        if(Libreria.tieneInformacion(txtHasta.getText().toString())){
            xml +="<fefin>"+txtHasta.getText().toString()+"</fefin>";
        }
        String factura=((EditText) findViewById(R.id.editFacturaInventarios)).getText().toString();
        if(Libreria.tieneInformacion(factura)){
            xml +="<factura>"+factura+"</factura>";
        }
        String strCliente=cliente.getText().toString();
        if(Libreria.tieneInformacion(strCliente)){
            xml +="<cliente>"+strCliente+"</cliente>";
        }
        xml +="<estacion>"+v_estacion+"</estacion>";
        xml +="<tipo>" + documento + "</tipo><surtidor>" + (surtidor ? usuarioID : "0") + "</surtidor><encaptura>"+(documento==17 ? true:encaptura)+"</encaptura><orden>"+orden+"</orden></linea>";
        return xml;
    }

    /**
     * Llama al servicio que trae la lista de proveedores
     * @param busqueda La busquda
     */
    public void listaProveedores(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_PROVEEDORES, "SQL", "SQL", busqueda, String.valueOf(documento), "");
    }

    public void listaClientes(String busqueda){
        peticionWS(Enumeradores.Valores.TAREA_BUSCAR_CLIENTE, "SQL", "SQL", busqueda, "", "");
    }

    /**
     * Llama al servicio que busca un folio
     */
    public void busquedaFolio(){
        dlgMensajeError("En construccion", R.drawable.mensaje_warning);
    }

    /**
     * LLama al servicio que guarda un documento
     * @param nuevo Indica si el documento es nuevo
     * @param documento El tipo de documento
     * @param prov el id del proveedor
     * @param fecha La fecha
     * @param factura La factura
     * @param facturaCant La cantidad facturada
     * @param notas Las notas del documento
     * @param alm .
     * @param OC La orden de compra
     */
    private void guardaDatos(String nuevo, int documento, String prov, String fecha, String factura, String facturaCant, String notas, int alm, String OC, float divisa, int diascred,String pUuid) {
        if (documento == 14) {

            peticionWS(Enumeradores.Valores.TAREA_GUARDA_COMPRA, "SQL", "SQL",xmlDI(nuevo,documento,prov,fecha,factura,facturaCant,notas,alm,OC,divisa,diascred,pUuid),"","");
            /*peticionWS(Enumeradores.Valores.TAREA_GUARDA_COMPRA, "SQL", "SQL",
                    "<linea><nuevo>" + nuevo + "</nuevo><difolio></difolio><sucudest></sucudest>" +
                            "<usuaid>" + usuarioID + "</usuaid><mvdi>" + documento + "</mvdi><edpr>" + alm + "</edpr>" +
                            "<prov>" + prov + "</prov><pedi>" + OC + "</pedi><fefac>" + fecha + "</fefac>" +
                            "<refr>" + factura + "</refr><totfac>" + facturaCant + "</totfac>" +
                            "<notas>" + notas + "</notas><notadif></notadif><vfolio></vfolio><xmlfac></xmlfac><tipocambio>" + divisa + "</tipocambio><diasc>"+diascred+"</diasc></linea>"
                    , "", "");*/

            SharedPreferences sp = getSharedPreferences("di", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("facturaFolio", factura);
            editor.putString("fechaFolio", fecha);
            editor.apply();
        } else if (documento == 16) {
            OC = OC.isEmpty() ? "-1" : OC;
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            int ocInt = Integer.parseInt(OC);


            if (ocInt < 0 && servicio.getVentaFolio(OC).length() > 9)
                peticionWS(Enumeradores.Valores.TAREA_SURTE_VENTA, "SQL", "SQL", servicio.getVentaFolio(OC), usuarioID, notas);
            else
                peticionWS(Enumeradores.Valores.TAREA_GUARDA_COMPRA, "SQL", "SQL",xmlDI(nuevo,documento,prov,fecha,factura,facturaCant,notas,alm,OC,divisa,diascred,pUuid),"","");
                /*peticionWS(Enumeradores.Valores.TAREA_GUARDA_COMPRA, "SQL", "SQL",
                        "<linea><nuevo>" + nuevo + "</nuevo><difolio></difolio><sucudest>" + prov + "</sucudest>" +
                                "<usuaid>" + usuarioID + "</usuaid><mvdi>" + documento + "</mvdi><edpr>" + alm + "</edpr><prov>" +
                                "</prov><pedi>"+ OC +"</pedi><fefac>" + date + "</fefac><refr>" + factura + "</refr><totfac>" +
                                facturaCant + "</totfac><notas>" + notas + "</notas>" +
                                "<notadif></notadif><vfolio></vfolio><xmlfac></xmlfac></linea>",
                        "",
                        "");*/

            SharedPreferences sp = getSharedPreferences("di", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            editor.putString("facturaFolio", factura);
            editor.putString("fechaFolio", date);
            editor.apply();
        }
    }

    private String xmlDI(String nuevo, int documento, String prov, String fecha, String factura, String facturaCant, String notas, int alm, String OC, float divisa, int diascred,String pUuid){
        ContentValues mapa=new ContentValues();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        mapa.put("nuevo",nuevo);
        mapa.put("difolio","");
        mapa.put("sucudest",documento==16 ? prov:"");
        mapa.put("usuaid",usuarioID);
        mapa.put("mvdi",documento);
        mapa.put("edpr",alm);
        mapa.put("prov",documento==14 ? prov:"");
        mapa.put("pedi",OC);
        mapa.put("fefac",documento==14 ? fecha:date);
        mapa.put("refr",factura);
        mapa.put("totfac",facturaCant);
        mapa.put("notas",notas);
        mapa.put("notadif","");
        mapa.put("vfolio","");
        mapa.put("xmlfac","");
        mapa.put("tipocambio",divisa);
        mapa.put("diasc",diascred);
        mapa.put("uuid",pUuid);
        mapa.put("estacion",v_estacion);
        return Libreria.xmlLineaCapturaSV(mapa,"linea");
    }

    /**
     * Inicializa el panel de nuevo documento
     * @param proveedorID El id del proveedor
     * @param proveedor El proveedor
     * @param ordenCompra La orden de compra
     */
    @SuppressLint("SetTextI18n")
    public void surte(int proveedorID, String proveedor, String ordenCompra, String folio,String pFolio,String pFecha,Float pImporte,Integer pDiasCompra) {
        RSprveedorID = proveedorID;
        RSproveedor = proveedor;
        RSordenCompra = ordenCompra;
        proveedores = new ArrayList<>();
        prov = new Proveedor();
        subalmacen = new Subalmacen();
        ocultaRecibe = true;

        if (proveedorID != 0) {
            ((EditText) findViewById(R.id.editRSInventarioProveedorSucursal)).setText(proveedor);
            prov.setProvid(proveedorID);
        }
        if (ordenCompra.equals("")){
            ((EditText) findViewById(R.id.editRSInventariosOrdenCompra)).setText("");
            findViewById(R.id.editRSInventariosOrdenCompra).setEnabled(true);
        }
        else{
            ((EditText)findViewById(R.id.editRSInventariosOrdenCompra)).setText(ordenCompra);
            findViewById(R.id.editRSInventariosOrdenCompra).setEnabled(false);
        }
        if(Libreria.tieneInformacion(pFolio)){
            ((EditText) findViewById(R.id.editRSInventarioFactura)).setText(pFolio);
        }
        if(Libreria.tieneInformacion(pFecha)){
            ((EditText) findViewById(R.id.editRSInventarioFecha)).setText(pFecha);
        }
        if(pImporte!=null && pImporte>0){
            ((EditText) findViewById(R.id.editRSInventarioCosto)).setText(pImporte+"");
        }
        if(pDiasCompra!=null && pDiasCompra>0){
            EditText editDiascred = findViewById(R.id.editDiasCredito);
            editDiascred.setText(pDiasCompra+"");
        }
        ((EditText)findViewById(R.id.editRSInventarioDivisa)).setText(divisa);
        findViewById(R.id.editRSInventarioProveedorSucursal).setEnabled(proveedorID == 0);
        findViewById(R.id.btnRSInventariosProveedorSucursal).setEnabled(proveedorID == 0);

        if(documento == 16)
            btnProveedorSucursal.setText(folio.startsWith("C") ? "CLIENTE" : "SUCURSAL");

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.filtros_open);
        recibeSurte.startAnimation(anim);
        recibeSurte.setVisibility(View.VISIBLE);

        anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        anim.setDuration(300);
        btnBuscar.startAnimation(anim);
        btnBuscar.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                disableEnableView(false, findViewById(R.id.inventario_content));
                btnBuscar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        //subalmacen.setDcatid(estadoProd);
        //peticionWS(Enumeradores.Valores.TAREA_SUBALMACENES, "SQL", "SQL", "14", "1", "");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void Finish(EnviaPeticion output){
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();
        if (obj != null) {
            switch (output.getTarea()) {
                case TAREA_CATALOGOS:{
                    documentos = servicio.getCatalogos(nuevoCaptura);

                    ((TextView)findViewById(R.id.txtInventarioDocumentos)).setText((nuevoCaptura && documento == 14? "Ordenes: ": nuevoCaptura && documento == 16? "Solicitudes: ":"Documentos: ") + documentos.size());

                    adapter = new DocumentosAdapter(documentos, this);
                    listDocumentos.setAdapter(adapter);
                    boolean haySelec=false;
                    if(documentos.size()>1) {
                        for (int i = 0; i < documentos.size(); i++)
                            if (documentos.get(i).getGrupo() == 1 && documentos.get(i).getSurtidor().equals(usuario)) {
                                listDocumentos.setItemChecked(i, true);
                                documentos.get(i).setSelected(true);
                                haySelec = true;
                            }
                        adapter.setInSelection(haySelec);
                    }
                    break;

                }
                case TAREA_PROVEEDORES: {
                    proveedores = servicio.getProveedores();
                    final ArrayList<String> proveedoresNombre = new ArrayList<>();

                    for(Proveedor p:proveedores)
                        proveedoresNombre.add(p.getAliasp());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proveedoresNombre);
                    listProvedoresSucursales.setAdapter(adapter);
                    listProvedoresSucursalesBuscar.setAdapter(adapter);
                    break;
                }
                case TAREA_BUSCAR_CLIENTE:{
                    clientes = servicio.getClientes();
                    final ArrayList<String> clientesNombre = new ArrayList<>();

                    for(Cliente c:clientes)
                        clientesNombre.add(c.getCliente());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clientesNombre);
                    listClientesBuscar.setAdapter(adapter);
                    break;
                }
                case TAREA_SUBALMACENES: {

                    break;
                }
                case TAREA_GUARDA_COMPRA:
                case TAREA_SURTE_VENTA:{
                    cierraDialogo();
                    if (obj.getAsBoolean("exito")){
                        String nuevoFolio = obj.getAsString("foliodi");

                        //Intent intent = new Intent(this, RecibeDocumento.class);
                        Intent intent = new Intent(this, Ddincontrolador.class);
                        intent.putExtra("documento", documento);
                        intent.putExtra("OC", OCNuevo);
                        intent.putExtra("foliodi", nuevoFolio);
                        intent.putExtra("prov/suc", provSucuNuevo);
                        intent.putExtra("pedidos", OCNuevo);

                        if(documento == 14)
                            intent.putExtra("divisa",divisa.equals("")? "1":divisa);

                        prov = null;
                        startActivity(intent);
                    }
                    else{
                        dlgMensajeError(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_GUARDA_TAREA:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        if(radioCaptura.isChecked())
                            traeCatalogos(armaXmlCat(true,1), "false", "1");
                        if(radioNuevo.isChecked())
                            traeCatalogos(armaXmlCat(false,1), "true", "1");
                    }
                    else{
                        dlgMensajeError(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                    }
                    break;
                }
                case TAREA_SURTE_TAREA:{
                    cierraDialogo();
                    if(obj.getAsBoolean("exito")){
                        if(documento == 16){
                            cambiaDdinControlador(-1);
                        }else{
                            Intent intent = new Intent(this, RecibeDocumento.class);
                            intent.putExtra("documento", documento);
                            intent.putExtra("OC", "0");
                            intent.putExtra("foliodi", "00000");
                            intent.putExtra("prov/suc", "-");
                            intent.putExtra("pedidos", listaPedidos);
                            startActivity(intent);
                        }
                    }
                    else
                        dlgMensajeError(obj.getAsString("mensaje"), R.drawable.mensaje_error);
                }
                case TAREA_REPODOCUMENTO:
                    dlgReporte(4);
                    break;
                case TAREA_IMPRIMEDOCS:
                    if(output.getExito()){
                        String pticket = obj.getAsString("anexo");
                        doImprime(pticket,false);
                    }else
                        dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error);
                    break;
                case TAREA_TRAEORCOINFO:
                    if(output.getExito()){
                        actualizaOrco(obj);
                    }else{
                        dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error);
                    }
                default:
                    cierraDialogo();
            }
        }
        else {
            cierraDialogo();
            dlgMensajeError("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void actualizaOrco(ContentValues pValores){
        String dato=pValores.getAsString("proveedor");
        EditText editProveedorSucursal = findViewById(R.id.editRSInventarioProveedorSucursal);
        editProveedorSucursal.setText(dato);
        dato = pValores.getAsString("totalfactura");
        EditText edita = findViewById(R.id.editRSInventarioCosto);
        edita.setText(dato);
        dato = pValores.getAsString("fechafactura");
        edita = findViewById(R.id.editRSInventarioFecha);
        edita.setText(Libreria.fecha_to_fecha(dato,"yyyy-MM-dd","yyMMdd"));
        dato = pValores.getAsString("diascredito");
        edita = findViewById(R.id.editDiasCredito);
        edita.setText(dato);
        dato = pValores.getAsString("referencia");
        edita = findViewById(R.id.editRSInventarioFactura);
        edita.setText(dato);
        btnProveedorSucursal.callOnClick();
    }

    private void repoDocumento(){
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_REPODOCUMENTO, "SQL", "SQL", documento+"",v_estacion+"",  "");
    }

    public void traeTicketDoc(String pFolio){
        peticionWS(Enumeradores.Valores.TAREA_IMPRIMEDOCS, "SQL", "SQL",  v_estacion+"",pFolio,"");
    }

    private void traeOrco(String pOrco){

        peticionWS(Enumeradores.Valores.TAREA_TRAEORCOINFO, "SQL", "SQL", pOrco,usuarioID+"",  v_estacion+"");
    }

}