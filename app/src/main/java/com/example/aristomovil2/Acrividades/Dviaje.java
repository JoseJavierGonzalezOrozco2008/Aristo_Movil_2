package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CIERRA_BULTO;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Capture;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ListadoAdapter;
import com.example.aristomovil2.facade.Estatutos;
import com.example.aristomovil2.modelos.Detviaje;
import com.example.aristomovil2.modelos.UtilViaje;
import com.example.aristomovil2.modelos.Viaje;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.MessageFormat;
import java.util.List;

public class Dviaje extends ActividadBase {
    private String v_Folio,v_EnviFolio,v_DcinFolio,v_Alias;
    private Integer v_Posicion,v_estatus;
    private Detviaje registroDetalle;
    private TextView v_Mensaje,v_Busqueda,v_Renglon;
    private ImageButton v_Ant,v_Sig;
    private LinearLayout v_LayAcciones;
    private EditText v_CapFolio;
    private Button v_Carga,v_Detalle,v_Cierra;
    private Viaje registroViaje;
    private ColorFilter v_ColorDefault;
    private boolean v_PorTerminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dviaje);
        Bundle extras = getIntent().getExtras();
        v_Alias = extras.getString("folio");
        v_Folio = extras.getString("vfolio");
        inicializarActividad("Viajes");

        registroViaje = servicio.traeViaje(v_Folio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);


        v_CapFolio = findViewById(R.id.editCodigo);
        v_CapFolio.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                wsBuscarFolio(v_CapFolio.getText().toString());
            }
            return false;
        });
        v_CapFolio.setInputType(InputType.TYPE_CLASS_TEXT);
        v_Posicion = 1;
        v_Mensaje = findViewById(R.id.txtMensaje01);
        v_Busqueda = findViewById(R.id.txtBqd);
        v_Renglon = findViewById(R.id.txtReng);
        v_Renglon.setTag("{0}/{1}");
        v_Ant = findViewById(R.id.btnAnt);
        v_Sig = findViewById(R.id.btnSig);
        ImageButton escaner= findViewById(R.id.btnBarcode);
        v_Ant.setOnClickListener(view -> {v_Posicion--;doIrPagina();});
        v_Sig.setOnClickListener(view -> {v_Posicion++;doIrPagina();});
        escaner.setOnClickListener(view -> {barcodeEscaner();});

        v_Detalle = findViewById(R.id.btnVerDetalle);
        v_Carga = findViewById(R.id.btnCargaEnvio);
        v_Cierra = findViewById(R.id.btnFinViaje);
        v_Detalle.setOnClickListener(view -> {
            //muestraMensaje("En construccion",R.drawable.mensaje_warning);
            wsDetalles();
        });
        v_Carga.setOnClickListener(view -> {
            aletCarga();
        });
        v_Cierra.setOnClickListener(view -> {
            wsInfoViaje();
        });

        v_LayAcciones = findViewById(R.id.layAcciones);

        /*detalle.setEnabled(false);
        cierra.setEnabled(Libreria.tieneInformacion(registroViaje.getChofer()) && Libreria.tieneInformacion(registroViaje.getVehiculo()));
        ;*/
        v_ColorDefault=v_Detalle.getBackground().getColorFilter();

        if(!Libreria.tieneInformacion(registroViaje.getVehiculo())){
            disenBtn(v_Carga,false);
        }
        if(!Libreria.tieneInformacion(registroViaje.getChofer()) && !Libreria.tieneInformacion(registroViaje.getVehiculo())){
            disenBtn(v_Cierra,false);
        }
        disenBtn(v_Detalle,false);


        String titulo="Traslado {0} {1}";
        title.setText(MessageFormat.format(titulo,v_Alias,usuario));
        v_estatus = 41;
        doIrPagina();
        v_PorTerminar = false;
    }

    public void doIrPagina(){
        int disponible=servicio.disponiblesViaje();

        System.out.println("posicion "+v_Posicion+"  disponible "+disponible);

        if(v_Posicion<=0){
            v_Posicion = 1;
        }else if(v_Posicion>disponible){
            v_Posicion = disponible;
        }
        if(disponible>0){
            registroDetalle = servicio.traeDviaje(v_Posicion);
            String formato=String.valueOf(v_Renglon.getTag());
            if(registroDetalle!=null && registroDetalle.getId()>0){
                v_Mensaje.setText(registroDetalle.getTexto());
                v_Renglon.setText(MessageFormat.format(formato, v_Posicion,disponible));
            } else{
                muestraMensaje("No se encontro el registro", R.drawable.mensaje_error);
            }
        }else{
            v_Mensaje.setText("No hay pendientes por cargar");
            v_Renglon.setText("");
        }
    }


    private void limpia(){
        v_Busqueda.setText("");
        v_EnviFolio = "";
        v_CapFolio.requestFocus();
        disenBtn(v_Detalle,false);
        v_LayAcciones.setVisibility(View.VISIBLE);
        v_Mensaje.setVisibility(View.VISIBLE);
    }

    private void actualizaPendientes(){
        int resultado=servicio.actualizaViaje(v_DcinFolio);
        if(resultado>=0){
            doIrPagina();
        }else{
            muestraMensaje("No se pudo actualizar los pendientes por carga al traslado",R.drawable.mensaje_error);
        }
    }

    public void aletCarga(){
        if(!Libreria.tieneInformacion(v_DcinFolio)){
            muestraMensaje("No se ha buscado un surtido valido",R.drawable.mensaje_error);
            return;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cargar Surtido");
        dialog.setMessage(MessageFormat.format("¿Esta seguro de cargar el folio {0} ?",v_DcinFolio));
        dialog.setPositiveButton("Cargar", (dialogInterface, i) -> wsCargaDoc());
        dialog.setNegativeButton("Cancelar", (dialogInterface, i) -> {});
        dialog.show();
    }

    public void aletCierre(String pInfo){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cerrar Traslado");
        final View customLayout = getLayoutInflater().inflate(R.layout.alert_viaje, null);
        dialog.setView(customLayout);
        TextView info=customLayout.findViewById(R.id.txtInfo);
        TextView pregunta=customLayout.findViewById(R.id.txtPregunta);
        info.setText(pInfo);
        String mensaje ="¿Esta seguro de cerrar el traslado con folio {0} ?";
        pregunta.setText(MessageFormat.format(mensaje,v_Folio));
        dialog.setPositiveButton("Cerrar traslado", (dialogInterface, i) -> wsCierraViaje());
        dialog.setNegativeButton("Regresar", (dialogInterface, i) -> {});
        dialog.show();
    }

    public void dlgVehiculos(){
        Dialog dialogo = new Dialog(this, R.style.Dialog);
        //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
        dialogo.setContentView(R.layout.dialogo_utilviaje);
        dialogo.setTitle("Vehiculos disponibles");
        ListView lista=dialogo.findViewById(R.id.lista_selec);
        List<UtilViaje> vehiculos=servicio.traeUtilViaje();
        Button asigna=dialogo.findViewById(R.id.utAsigna);
        final ListadoAdapter adapter = new ListadoAdapter(vehiculos,this,2,registroViaje.getPatrid());
        if(vehiculos.isEmpty()){
            TextView vacio=dialogo.findViewById(R.id.emptyListado);
            lista.setEmptyView(vacio);
            lista.setAdapter(null);
        }else{
            lista.setAdapter(adapter);
        }
        asigna.setOnClickListener(view -> {
            UtilViaje elejido = (UtilViaje)adapter.getSelec();
            wsAsigVehiculo(elejido.getRid());
            dialogo.dismiss();
        });
        dialogo.show();
    }

    public void dlgChofer(){
        Dialog dialogo = new Dialog(this, R.style.Dialog);
        //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
        dialogo.setContentView(R.layout.dialogo_utilviaje);
        dialogo.setTitle("Choferes disponibles");
        ListView lista=dialogo.findViewById(R.id.lista_selec);
        List<UtilViaje> vehiculos=servicio.traeUtilViaje();
        Button asigna=dialogo.findViewById(R.id.utAsigna);
        final ListadoAdapter adapter = new ListadoAdapter(vehiculos,this,2,registroViaje.getChoferid());
        if(vehiculos.isEmpty()){
            TextView vacio=dialogo.findViewById(R.id.emptyListado);
            lista.setEmptyView(vacio);
            lista.setAdapter(null);
        }else{
            lista.setAdapter(adapter);
        }
        asigna.setOnClickListener(view -> {
            UtilViaje elejido = (UtilViaje)adapter.getSelec();
            wsAsigChofe(elejido.getRid());
            dialogo.dismiss();
        });
        dialogo.show();
    }

    public void barcodeEscaner(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setPrompt("");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doIrPagina();
        limpia();
        v_DcinFolio = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Asigna Vehiculo");
        menu.add(Menu.NONE, 2, Menu.NONE, "Asigna Chofer");
        menu.add(Menu.NONE, 3, Menu.NONE, "Lista Cargados");
        menu.add(Menu.NONE, 4, Menu.NONE, "Lista Pendientes");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                wsVehiculo();
                break;
            case 2:
                wsChofe();
                break;
            case 3:
                v_estatus = 44;
                wsLlamaCarga();
                break;
            case 4:
                v_estatus = 41;
                wsLlamaCarga();
                break;
            default:
                muestraMensaje("En construccion",R.drawable.mensaje_warning);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult.getContents() != null ) {
                wsBuscarFolio(intentResult.getContents());
            }
            else
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
        }
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        String titulo="";
        Intent intent;
        if(output!=null){
            switch(output.getTarea()){
                case TAREA_DVIAJ_IND:
                    if(output.getExito()){
                        ContentValues values=(ContentValues)output.getExtra1();
                        v_DcinFolio = values.getAsString("dcin");
                        v_EnviFolio = values.getAsString("envio");
                        v_Busqueda.setText(values.getAsString("texto"));
                        disenBtn(v_Detalle,true);
                        if(!registroDetalle.getDcin().equals(v_DcinFolio)){
                            reproduceAudio(R.raw.yaexiste);
                        }else{
                            reproduceAudio(R.raw.prodencontrado);
                        }
                        v_LayAcciones.setVisibility(View.GONE);
                        v_Mensaje.setVisibility(View.GONE);
                    }else{
                        limpia();
                        muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                    }
                    v_CapFolio.setText("");
                    break;
                case TAREA_VIAJ_SUBE:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        actualizaPendientes();
                        limpia();
                    }
                    break;
                case TAREA_VIAJ_BAJA:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        v_estatus = 41;
                        wsLlamaCarga();
                    }
                    break;
                case TAREA_DVIAJ_LISTA:
                    switch (v_estatus){
                        case 41:titulo="Surtidos pendientes para {0}";
                            break;
                        case 44:titulo="Surtidos cargados para {0}";
                            break;
                    }
                    intent = new Intent(this, Listado.class);
                    intent.putExtra("folio",v_Folio);
                    intent.putExtra("titulo",MessageFormat.format(titulo,v_Alias));
                    intent.putExtra("tipo",1);
                    intent.putExtra("estatus",v_estatus);
                    startActivity(intent);
                    break;
                case TAREA_VIAJ_CIERRA:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        //manda a imprimir
                        wsSolicitaViajes();
                        v_PorTerminar = true;
                    }
                    break;
                case TAREA_VIAJ_INFO:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        ContentValues values=(ContentValues)output.getExtra1();
                        String info=values.getAsString("texto");
                        aletCierre(info);
                    }
                    break;
                case TAREA_TRAE_VEHICULO:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        dlgVehiculos();
                    }
                    break;
                case TAREA_TRAE_CHOFER:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        dlgChofer();
                    }
                    break;
                case TAREA_ASIGNA_VVEHICULO:
                case TAREA_ASIGNA_VCHOFER:
                    muestraMensaje(output.getMensaje(),output.getExito() ? R.color.colorExito:R.drawable.mensaje_error);
                    if(output.getExito()){
                        wsSolicitaViajes();
                        //falta actualizar viaje
                    }
                    break;
                case TAREA_VIAJ_LISTA:
                    registroViaje = servicio.traeViaje(v_Folio);
                    if(registroViaje==null){
                        System.out.println("La cosa esta vacia");
                    }
                    if(v_PorTerminar){
                        finish();
                    }
                    break;
                case TAREA_VIAJE_DETALLES:
                    List lista=servicio.traeUtilViaje();
                    if(lista.isEmpty()){
                        muestraMensaje("Domunento sin renglones",R.drawable.mensaje_warning);
                        return;
                    }
                    titulo="Detalles de {0}";
                    intent = new Intent(this, Listado.class);
                    intent.putExtra("folio",v_Folio);
                    intent.putExtra("titulo",MessageFormat.format(titulo,v_DcinFolio));
                    intent.putExtra("tipo",3);
                    intent.putExtra("estatus",0);
                    startActivity(intent);
                    break;
            }

        }
    }

    @Override
    public void muestraMensaje(String pMensaje, int ptipo) {
        super.muestraMensaje(pMensaje, ptipo);
        if(ptipo == R.color.colorExito){
            reproduceAudio(R.raw.prodencontrado);
        }else{
            reproduceAudio(R.raw.error);
        }
    }

    public void wsBuscarFolio(String pFolio){
        pFolio = Libreria.tieneInformacion(pFolio) ? pFolio.trim() : "";
        String xml="<linea><viaje>"+v_Folio+"</viaje><dcin>"+Libreria.remplazaXml(pFolio)+"</dcin></linea>";
        peticionWS(Enumeradores.Valores.TAREA_DVIAJ_IND,"SQL","SQL",xml,"","");
    }

    public void wsCargaDoc(){
        String xml = MessageFormat.format("<linea><viaje>{0}</viaje><envio>{1}</envio><usua>{2}</usua></linea>",v_Folio,v_EnviFolio,usuarioID);
        //peticionWS(Enumeradores.Valores.TAREA_VIAJ_SUBE,"SQL","SQL",v_Folio,v_EnviFolio,""+usuarioID);
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_SUBE,"SQL","SQL",xml,"","");
    }

    public void wsCierraViaje(){
        ContentValues mapa=new ContentValues();
        mapa.put("viajeorigen",v_Folio);
        mapa.put("usua",usuarioID);
        mapa.put("viajeextra","");
        mapa.put("accion",0);
        String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_CIERRA,"SQL","SQL",xml,"","");
        //peticionWS(Enumeradores.Valores.TAREA_VIAJ_CIERRA,"SQL","SQL",v_Folio,""+usuarioID,"");
    }

    public void wsInfoViaje(){
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_INFO,"SQL","SQL",v_Folio,""+usuarioID,"");
    }

    public void wsLlamaCarga(){
        peticionWS(Enumeradores.Valores.TAREA_DVIAJ_LISTA,"SQL","SQL","<linea><viaje>"+v_Folio+"</viaje><estt>"+v_estatus+"</estt></linea>","","");
    }

    public void wsVehiculo(){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_VEHICULO,"SQL","SQL","","","");
    }

    public void wsChofe(){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_CHOFER,"SQL","SQL","","","");
    }

    public void wsAsigVehiculo(Integer pVehiculo){
        peticionWS(Enumeradores.Valores.TAREA_ASIGNA_VVEHICULO,"SQL","SQL",""+pVehiculo,v_Folio,"");
    }

    public void wsAsigChofe(Integer pChofer){
        peticionWS(Enumeradores.Valores.TAREA_ASIGNA_VCHOFER,"SQL","SQL",""+pChofer,v_Folio,"");
    }

    private void wsSolicitaViajes(){
        peticionWS(Enumeradores.Valores.TAREA_VIAJ_LISTA,"SQL","SQL","<linea><usua>-1</usua></linea>","","");
    }

    public void wsDetalles(){
        servicio.borraDatosTabla(Estatutos.TABLA_UTIVIAJE);
        peticionWS(Enumeradores.Valores.TAREA_VIAJE_DETALLES,"SQL","SQL",v_DcinFolio,"","");
    }

    private void disenBtn(View pView,Boolean pEnable){
        pView.setEnabled(pEnable);
        if(pEnable){
            pView.getBackground().setColorFilter(v_ColorDefault);
        }else{
            pView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }



}