package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.facade.Estatutos.TABLA_GENERICA;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.DevolucionAdapter;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.ListaImpuestosAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Devolucion extends ActividadBase {
    private TextView v_devmensajes,v_devTitulo,v_total,v_foliovnta;
    private EditText v_captura;
    private TableLayout v_tablaCaptura;
    private LinearLayout v_prod1,v_prod2;
    private boolean v_admitevale;
    private DevolucionAdapter v_devoconAdapter;
    private Button v_aplica;
    private ArrayList<Generica> porDevol;
    private AlertDialog v_dlgDevol;
    private TableRow prodRowProd;
    private ImageButton barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolucion);
        inicializarActividad2("","");
        actualizaToolbar2(usuario+" "+v_nombreestacion,"DEVOLUCION DE VENTA");
        v_admitevale = true;
        v_aplica = findViewById(R.id.devoAplicar);
        Button porDevolver = findViewById(R.id.btnDevoPorDevo);
        Button captDevol = findViewById(R.id.btnDevoCaptDevo);
        v_captura = findViewById(R.id.devoVntaFolio);
        TextView notas = findViewById(R.id.devoNotas);
        v_foliovnta = findViewById(R.id.devoVntaFolioTxt);
        v_total = findViewById(R.id.devoTotal);
        v_devTitulo= findViewById(R.id.devoTitulo);
        v_tablaCaptura= findViewById(R.id.devoCapturas);
        v_prod1 = findViewById(R.id.devoProd1);
        v_prod2 = findViewById(R.id.devoProd2);
        v_devmensajes =findViewById(R.id.devoInfo);
        RadioGroup devocon = findViewById(R.id.devoCon);
        RadioGroup tipo = findViewById(R.id.devoTipo);
        Spinner motivo = findViewById(R.id.devoMotivo);
        RadioButton efectivo = findViewById(R.id.radioDevoEfectivo);
        RadioButton parcial = findViewById(R.id.radioDevoParcial);
        prodRowProd = findViewById(R.id.prodRowProd);
        barcode=findViewById(R.id.btnBarcode);

        ArrayList<String> motivos=servicio.traeDcatalogo(40);
        ArrayAdapter<String> spinMotivos = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, motivos);
        motivo.setAdapter(spinMotivos);
        barcode.setOnClickListener(view->barcodeEscaner());

        v_captura.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                wsBuscaFolio(v_captura.getText().toString());
            }return false;
        });
        v_captura.requestFocus();
        porDevol =new ArrayList<>();

        v_aplica.setOnClickListener(view -> {
            ContentValues mapa =new ContentValues();
            mapa.put("venta",v_captura.getText().toString().trim().toUpperCase());
            mapa.put("esta",v_estacion);
            mapa.put("usua",usuarioID);
            mapa.put("motivo",servicio.traeDcatIdporAbrevi(40,motivo.getSelectedItem()+""));
            mapa.put("convale",devocon.getCheckedRadioButtonId()==R.id.radioDevoVale);
            mapa.put("notas",notas.getText().toString());
            mapa.put("completa",tipo.getCheckedRadioButtonId()==R.id.radioDevoTotal);
            mapa.put("prods", tipo.getCheckedRadioButtonId()==R.id.radioDevoTotal? "" : traeProds() );
            String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
            wsAplicaDevol(xml);
        });
        efectivo.setChecked(true);
        parcial.setChecked(true);
        tipo.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i==R.id.radioDevoParcial){
                //porDevolver.setVisibility(View.VISIBLE);
                v_prod2.setVisibility(View.VISIBLE);
                v_total.setText("$"+traeTotal());
            }else if(i==R.id.radioDevoTotal){
                //porDevolver.setVisibility(View.GONE);
                v_prod2.setVisibility(View.GONE);
                wsTraeImporte(v_captura.getText().toString());

            }
        });
        porDevolver.setOnClickListener(view -> {
            wsProdPorDevol(v_captura.getText().toString());
        });
        porDevolver.setVisibility(View.GONE);
        captDevol.setOnClickListener(view -> {
            dlgPorDevolver();
        });
        v_aplica.setVisibility(View.GONE);
        v_devTitulo.setVisibility(View.GONE);
        v_tablaCaptura.setVisibility(View.GONE);
        v_prod1.setVisibility(View.GONE);
        v_prod2.setVisibility(View.GONE);
    }

    @Override
    public void Finish(EnviaPeticion output) {
        try{
            ContentValues obj=(ContentValues)output.getExtra1();
            switch (output.getTarea()){
                case TAREA_BUSCAFOLIODEV:
                    if(output.getExito()){
                        v_devmensajes.setText(obj.getAsString("origen"));
                        v_admitevale = Libreria.getBoolean(obj.getAsString("admitevale"));
                        v_aplica.setVisibility(View.VISIBLE);
                        v_devTitulo.setVisibility(View.VISIBLE);
                        v_tablaCaptura.setVisibility(View.VISIBLE);
                        v_prod1.setVisibility(View.VISIBLE);
                        v_prod2.setVisibility(View.VISIBLE);
                        v_foliovnta.setVisibility(View.GONE);
                        v_captura.setVisibility(View.GONE);
                        barcode.setVisibility(View.GONE);
                        ((RadioButton)findViewById(R.id.radioDevoVale)).setClickable(v_admitevale);
                        prodRowProd.setVisibility( !Libreria.getBoolean(obj.getAsString("tienecred")) ? View.VISIBLE:View.GONE);
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_DEVOLTOTAL:
                    if(output.getExito()){
                        v_total.setText(new BigDecimal(Libreria.traeInfo(obj.getAsString("anexo"),"0")).setScale(2,BigDecimal.ROUND_HALF_EVEN)+"");
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error2);
                    }
                    break;
                case TAREA_DEVOLAPLICA:
                    if(output.getExito()){
                        v_aplica.setVisibility(View.GONE);
                        v_devTitulo.setVisibility(View.GONE);
                        v_tablaCaptura.setVisibility(View.GONE);
                        v_prod1.setVisibility(View.GONE);
                        v_prod2.setVisibility(View.GONE);
                        v_devmensajes.setText("");
                        ((RadioButton)findViewById(R.id.radioDevoVale)).setClickable(v_admitevale);
                        String folio=obj.getAsString("anexo");
                        imprimeTicket(folio);
                        v_captura.setVisibility(View.VISIBLE);
                        barcode.setVisibility(View.VISIBLE);
                        v_foliovnta.setVisibility(View.VISIBLE);
                        v_captura.setText("");
                    }
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error2);
                    break;
                case TAREA_DEVOLPRODS:
                    dlgReporte(10);
                    break;
                case TAREA_VNTAULTIMAVNTA:
                    if(output.getExito()) {
                        String v_ticket = obj.getAsString("anexo");
                        doImprime(v_ticket,true);
                    }else{
                        dlgMensajeError(output.getMensaje(), R.drawable.mensaje_error2);
                    }
                    break;
                default:
                    super.Finish(output);
            }
        }catch(UnsupportedEncodingException e){
            System.out.println(e);
        }
    }

    public void wsBuscaFolio(String pFolio){
        if(!Libreria.tieneInformacion(pFolio)){
            dlgMensajeError("Escriba un folio valido",R.drawable.mensaje_error2);
            return;
        }
        peticionWS(Enumeradores.Valores.TAREA_BUSCAFOLIODEV,"SQL","SQL",pFolio.trim().toUpperCase(),v_estacion+"",usuarioID+"");
    }

    public void wsAplicaDevol(String xml){
        if(!Libreria.tieneInformacion(xml)){
            dlgMensajeError("Sin datos que enviar al servidor",R.drawable.mensaje_error2);
            return;
        }
        peticionWS(Enumeradores.Valores.TAREA_DEVOLAPLICA,"SQL","SQL",xml,"","");
    }

    public void wsTraeImporte(String pFolio){
        if(!Libreria.tieneInformacion(pFolio)){
            dlgMensajeError("Escriba un folio valido",R.drawable.mensaje_error2);
            return;
        }
        peticionWS(Enumeradores.Valores.TAREA_DEVOLTOTAL,"SQL","SQL",pFolio.trim().toUpperCase(),v_estacion+"",usuarioID+"");
    }

    public void wsProdPorDevol(String pFolio){
        if(!Libreria.tieneInformacion(pFolio)){
            dlgMensajeError("Escriba un folio valido",R.drawable.mensaje_error2);
            return;
        }
        servicio.borraDatosTabla(TABLA_GENERICA);
        peticionWS(Enumeradores.Valores.TAREA_DEVOLPRODS,"SQL","SQL",pFolio.trim().toUpperCase(),v_estacion+"",usuarioID+"");
    }

    private void imprimeTicket(String pFolio){
        peticionWS(Enumeradores.Valores.TAREA_VNTAULTIMAVNTA, "SQL", "SQL", v_estacion+"", pFolio, usuarioID+"");
    }

    public void agregaPorDevol(Generica pGen){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_contrasena, null);
        builder.setTitle("");
        builder.setView(vista);
        TextView titulo=vista.findViewById(R.id.tvTituloContra);
        TextView mensaje=vista.findViewById(R.id.tvUsuarioContra);
        TextView nueva=vista.findViewById(R.id.pwdNueva);
        EditText cantidad=vista.findViewById(R.id.nupaPass1);
        LinearLayout pwdConfirma=vista.findViewById(R.id.pwdConfirma);
        Button cierra=vista.findViewById(R.id.btnCerrar);
        Button guarda=vista.findViewById(R.id.btnGuardar);

        AlertDialog dialogo=builder.create();

        titulo.setText(pGen.getTex3());
        mensaje.setText("¿Cuantas piezas a devolver?");
        nueva.setText("Cantidad:");
        pwdConfirma.setVisibility(View.GONE);

        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        cantidad.setText(pGen.getDec2()==null ? null:(pGen.getDec2().compareTo(BigDecimal.ZERO)>0 ? pGen.getDec2() : 1)+"");
        cantidad.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                guarda.callOnClick();
            }return false;
        });
        cantidad.requestFocus();
        cierra.setOnClickListener(view -> dialogo.dismiss());
        guarda.setOnClickListener(view -> {
            if(!Libreria.tieneInformacion(cantidad.getText().toString())){
                dlgMensajeError("No puede estar vacio",R.drawable.mensaje_error2);
                return;
            }
            Integer cantcap = Libreria.tieneInformacionEntero(cantidad.getText().toString());
            if(cantcap <= 0){
                dlgMensajeError("Tiene que ser mayor a cero",R.drawable.mensaje_error2);
                return;
            }
            if(cantcap.compareTo(pGen.getDec3().intValue())>0){
                dlgMensajeError("No puede devolver mas de lo comprado",R.drawable.mensaje_error2);
                return;
            }
            Generica generica = null;
            if(!porDevol.isEmpty()){
                for(Generica gen:porDevol){
                    if(gen.getId().compareTo(pGen.getId())==0){
                        generica = gen;
                        break;
                    }
                }
            }
            if(generica==null){
                generica = pGen;
                porDevol.add(pGen);
            }
            generica.setDec2(new BigDecimal(Libreria.traeInfo(cantidad.getText().toString(),"0")));


            v_total.setText("$"+traeTotal());
            v_dlgDevol.dismiss();
            v_dlgreporte.dismiss();
            dialogo.dismiss();
            dlgPorDevolver();
        });
        dialogo.show();
    }


    public void dlgPorDevolver(){
        Generica encabezado=new Generica(0);
        encabezado.setTex1("Productos por devolver");
        encabezado.setTex2("Click en +, para seleccionar productos vendidos");
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_reporenglones, null);
        View titulo=inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        builder.setCancelable(true);

        TextView ayuda = vista.findViewById(R.id.repoAyuda);
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        ImageButton nuevo = titulo.findViewById(R.id.btnTitNuevo);
        ImageButton regresa = titulo.findViewById(R.id.btnTitRegresa);
        //nuevo.setVisibility(View.GONE);
        nuevo.setOnClickListener(view -> {
            wsProdPorDevol(v_captura.getText().toString());
        });
        ListView ventas = vista.findViewById(R.id.listRepoRenglones);
        titTitulo.setText(encabezado.getTex1());
        ayuda.setText(encabezado.getTex2());
        ayuda.setVisibility(View.VISIBLE);

        v_devoconAdapter = new DevolucionAdapter(porDevol,this,0);
        ventas.setAdapter(v_devoconAdapter);
        TextView texto=vista.findViewById(R.id.repoVacio);
        texto.setText("Sin elementos");
        ventas.setEmptyView(texto);

        int[] colors = {0, 0xFF000000, 0};
        ventas.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        ventas.setDividerHeight(1);
        v_dlgDevol = builder.create();

        regresa.setOnClickListener(v-> v_dlgDevol.dismiss());

        v_dlgDevol.show();
    }

    public BigDecimal traeTotal(){
        BigDecimal total=BigDecimal.ZERO;
        for(Generica gen:porDevol){
            //System.out.println(gen.getDec1()+"--"+(gen.getDec2()));
            total=total.add(gen.getDec1().multiply(gen.getDec2()));
        }
        return total.setScale(2,BigDecimal.ROUND_HALF_EVEN);
    }

    public void quitarElemento(Generica pGen){
        for(int i=0;i<porDevol.size();i++){
            if(pGen.getId()==porDevol.get(i).getId()){
                porDevol.remove(i);
                break;
            }
        }
        v_total.setText("$"+traeTotal());
        v_dlgDevol.dismiss();
        dlgPorDevolver();
    }

    public String traeProds(){
        String prodis="";
        for(Generica gen : porDevol){
            prodis += gen.getId()+","+gen.getDec2()+"|";
        }

        return prodis.length()>0 ? prodis.substring(0,prodis.length()-1) : prodis;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult.getContents() != null ) {
                v_captura.setText(Libreria.traeInfo(intentResult.getContents()));
                wsBuscaFolio(v_captura.getText().toString());
            }else
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
        }
    }

}