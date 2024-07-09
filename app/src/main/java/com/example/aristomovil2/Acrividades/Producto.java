package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_IMPUGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRECGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_TRAEPRODMOVIL;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.GenericaAdapter;
import com.example.aristomovil2.adapters.ListaImpuestosAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Producto extends ActividadBase {
    private Integer v_prodid,v_peticion,v_Costeo;
    private String v_producto;
    private Button v_impu,v_costo;
    private ArrayList<Generica> v_impuestos;
    private Generica v_costos;
    private AlertDialog v_dialogo;
    private ListaImpuestosAdapter v_impAdapter;
    private ContentValues v_mapaDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cambio();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
        for(String llave:v_mapaDatos.keySet()){
            savedInstanceState.putString(llave,v_mapaDatos.getAsString(llave));
        }
        savedInstanceState.putInt("prodid",v_prodid);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        ContentValues v_daValues=new ContentValues();

        v_daValues.put("producto",savedInstanceState.getString("producto"));
        v_daValues.put("codigo",savedInstanceState.getString("codigo"));
        v_daValues.put("sat",savedInstanceState.getString("sat"));
        v_daValues.put("sustancia",savedInstanceState.getString("sustancia"));
        v_daValues.put("caduca",savedInstanceState.getString("caduca"));
        v_daValues.put("granel",savedInstanceState.getString("granel"));
        v_daValues.put("activo",savedInstanceState.getString("activo"));
        v_daValues.put("marca",savedInstanceState.getString("marca"));
        v_daValues.put("linea",savedInstanceState.getString("linea"));
        v_daValues.put("familia",savedInstanceState.getString("familia"));
        v_daValues.put("divisa",savedInstanceState.getString("divisa"));
        v_daValues.put("impuestos",savedInstanceState.getString("impuestos"));
        v_daValues.put("valor",savedInstanceState.getString("valor"));
        v_daValues.put("mensaje",savedInstanceState.getString("mensaje"));
        v_daValues.put("cult",savedInstanceState.getString("cult"));
        v_daValues.put("cstd",savedInstanceState.getString("cstd"));
        v_daValues.put("cpro",savedInstanceState.getString("cpro"));
        v_daValues.put("pnormal",savedInstanceState.getString("pnormal"));
        v_daValues.put("p1",savedInstanceState.getString("p1"));
        v_daValues.put("p2",savedInstanceState.getString("p2"));
        v_daValues.put("p3",savedInstanceState.getString("p3"));
        v_daValues.put("culti",savedInstanceState.getString("culti"));
        v_daValues.put("cstdi",savedInstanceState.getString("cstdi"));
        v_daValues.put("cproi",savedInstanceState.getString("cproi"));
        v_daValues.put("pnormali",savedInstanceState.getString("pnormali"));
        v_daValues.put("p1i",savedInstanceState.getString("p1i"));
        v_daValues.put("p12",savedInstanceState.getString("p12"));
        v_daValues.put("p13",savedInstanceState.getString("p13"));
        v_prodid=savedInstanceState.getInt("prodid");
        asignaValores(v_daValues);
    }

    public void cambio(){
        setContentView(R.layout.activity_producto);
        Bundle extras = getIntent().getExtras();
        v_prodid = Integer.parseInt(extras.getString("prodid"));
        inicializarActividad2("","");
        actualizaToolbar2(usuario, v_prodid==0 ? "Alta de Producto" : "Edicion de producto");
        Boolean mMarca = extras.getBoolean("pmarca");
        Boolean mLinea = extras.getBoolean("plinea");
        Boolean mClavesat = extras.getBoolean("pclavesat");
        Boolean mDivisa = extras.getBoolean("pdivisa");
        Boolean mSustancia = extras.getBoolean("psustancia");
        v_Costeo = Libreria.tieneInformacionEntero(extras.getString("tcosteo","0"));
        v_peticion = 0;

        TextView vista = findViewById(R.id.prodTitulo);

        Spinner marca = findViewById(R.id.prodMarca);
        Spinner linea = findViewById(R.id.prodLinea);
        Spinner spincalpre = findViewById(R.id.prodCalprecio);
        Spinner margenes = findViewById(R.id.prodTipomargen);
        Spinner divisas = findViewById(R.id.prodDivisa);

        EditText producto = findViewById(R.id.prodProducto);
        EditText codigo = findViewById(R.id.prodCodigo);
        EditText sustancia = findViewById(R.id.prodSustancia);
        EditText claveSat = findViewById(R.id.prodClavesat);
        CheckBox caduca = findViewById(R.id.prodCaduca);
        CheckBox fracci = findViewById(R.id.prodFracc);
        CheckBox activo = findViewById(R.id.prodActivo);

        View rowProd = findViewById(R.id.prodRowProd);
        View rowMarca = findViewById(R.id.prodRowMarca);
        View rowLinea = findViewById(R.id.prodRowLinea);
        View rowSustancia = findViewById(R.id.prodRowSustancia);
        View rowClavesat = findViewById(R.id.prodRowClvsat);
        View rowDivisa = findViewById(R.id.prodRowDivisa);
        TextView avisos = findViewById(R.id.costAviso);

        v_impu = findViewById(R.id.ProdImpuestos);
        v_costo = findViewById(R.id.ProdCierra);
        Button bguarda = findViewById(R.id.prodGuarda);
        ImageButton btnBarcode = findViewById(R.id.btnBarcode);

        btnBarcode.setOnClickListener(view -> barcodeEscaner());

        rowMarca.setVisibility(mMarca ? View.VISIBLE : View.GONE);
        rowLinea.setVisibility(mLinea ? View.VISIBLE : View.GONE);
        rowSustancia.setVisibility(mSustancia ? View.VISIBLE : View.GONE);
        rowClavesat.setVisibility(mClavesat ? View.VISIBLE : View.GONE);
        rowDivisa.setVisibility(mDivisa ? View.VISIBLE : View.GONE);

        //ArrayList<String> marcas = servicio.traeDcatalogo(42);
        List<Generica> marcas= servicio.traeDcatGenerica(42);
        //ArrayList<String> lineas = servicio.traeDcatalogo(44);
        List<Generica> lineas= servicio.traeDcatGenerica(44);
        //ArrayList<String> calpre = servicio.traeDcatalogo(54);
        List<Generica> calpre = servicio.traeDcatGenerica(54);
        //ArrayList<String> margen = servicio.traeDcatalogo(-1);
        List<Generica> margen = servicio.traeDcatGenerica(-1);
        //ArrayList<String> divisa = servicio.traeDcatalogo(32);
        List<Generica> divisa = servicio.traeDcatGenerica(32);

        //ArrayAdapter<String> spinMarca = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, marcas);
        GenericaAdapter spinMarca=new GenericaAdapter(marcas,this,11);
        marca.setAdapter(spinMarca);
        //ArrayAdapter<String> spinlinea = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, lineas);
        GenericaAdapter spinlinea=new GenericaAdapter(lineas,this,11);
        linea.setAdapter(spinlinea);
        //ArrayAdapter<String> spincalpreca = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, calpre);
        GenericaAdapter spincalpreca=new GenericaAdapter(calpre,this,11);
        spincalpre.setAdapter(spincalpreca);
        //ArrayAdapter<String> spinmargen = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, margen);
        GenericaAdapter spinmargen=new GenericaAdapter(margen,this,11);
        margenes.setAdapter(spinmargen);
        //ArrayAdapter<String> spindivisa = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, divisa);
        GenericaAdapter spindivisa=new GenericaAdapter(divisa,this,11);
        divisas.setAdapter(spindivisa);

        v_impuestos = new ArrayList();
        spincalpre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Generica selectedItem = (Generica) spincalpre.getSelectedItem();
                List<Generica> margen = servicio.traeMargen2(selectedItem.getId());
                GenericaAdapter spinmargen=new GenericaAdapter(margen, (ActividadBase) adapterView.getContext(),11);
                margenes.setAdapter(spinmargen);
                spinmargen.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        v_costos = new Generica(0);
        if(v_prodid > 0){
            ContentValues contenido = new ContentValues();
            contenido.put("producto",extras.getString("producto"));
            contenido.put("codigo",extras.getString("codigo"));
            contenido.put("sat",extras.getString("sat"));
            contenido.put("sustancia",extras.getString("sustancia"));
            contenido.put("caduca",extras.getString("caduca"));
            contenido.put("granel",extras.getString("granel"));
            contenido.put("activo",extras.getString("activo"));
            contenido.put("marca",extras.getString("marca"));
            contenido.put("linea",extras.getString("linea"));
            contenido.put("familia",extras.getString("familia"));
            contenido.put("divisa",extras.getString("divisa"));
            contenido.put("impuestos",extras.getString("impuestos"));
            contenido.put("valor",extras.getString("valor"));
            contenido.put("mensaje",extras.getString("mensaje"));

            contenido.put("cult",extras.getString("cult"));
            contenido.put("cstd",extras.getString("cstd"));
            contenido.put("cpro",extras.getString("cpro"));
            contenido.put("pnormal",extras.getString("pnormal"));
            contenido.put("p1",extras.getString("p1"));
            contenido.put("p2",extras.getString("p2"));
            contenido.put("p3",extras.getString("p3"));
            contenido.put("culti",extras.getString("culti"));
            contenido.put("cstdi",extras.getString("cstdi"));
            contenido.put("cproi",extras.getString("cproi"));
            contenido.put("pnormali",extras.getString("pnormali"));
            contenido.put("p1i",extras.getString("p1i"));
            contenido.put("p2i",extras.getString("p2i"));
            contenido.put("p3i",extras.getString("p3i"));
            v_peticion=0;
            asignaValores(contenido);
            codigo.requestFocus();
        }else{
            v_impuestos = servicio.traeDcatGenerica(36);
            for(Generica gen : v_impuestos){
                gen.setLog1(false);
            }
            vista.setVisibility(View.GONE);
            v_impu.setVisibility(View.GONE);
            v_costo.setVisibility(View.GONE);
            activo.setChecked(true);
            producto.requestFocus();
        }
        v_impu.setOnClickListener(view -> {
            v_peticion=1;
            wsBuscaProd(v_prodid);
            //dlgImpuestos();
        });
        v_costo.setOnClickListener(view -> {
            v_peticion=2;
            wsBuscaProd(v_prodid);
            //dlgCostos()
        });
        avisos.setText(Libreria.traeInfo(v_costos.getTex2()));

        bguarda.setOnClickListener(view -> {
            ContentValues mapa=new ContentValues();
            mapa.put("prodid",v_prodid);
            mapa.put("codigo",codigo.getText().toString());
            mapa.put("articulo",producto.getText().toString());
            mapa.put("sustancia",sustancia.getText().toString());
            mapa.put("marca",((Generica) marca.getSelectedItem()).getId());
            mapa.put("line",((Generica) linea.getSelectedItem()).getId());
            mapa.put("cprecio",((Generica) spincalpre.getSelectedItem()).getId());
            mapa.put("divisa",((Generica) divisas.getSelectedItem()).getId());
            mapa.put("margen",((Generica) margenes.getSelectedItem()).getId());
            mapa.put("caduca",caduca.isChecked());
            mapa.put("granel",fracci.isChecked());
            mapa.put("activo",activo.isChecked());
            mapa.put("usua",usuarioID);
            String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
            prodGuarda(xml);
        });
    }

    public void dlgCostos(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_costos, null);
        builder.setCancelable(true);
        builder.setView(vista);
        v_dialogo=builder.create();
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        v_dialogo.getWindow().setLayout(width, -2);
        Button bCerrar = vista.findViewById(R.id.PrecCerrar);
        Button bGuarda = vista.findViewById(R.id.PrecGuarda);

        EditText ultimo = vista.findViewById(R.id.prodCostoUlt);
        EditText promedio = vista.findViewById(R.id.prodCostoPro);
        EditText estandar = vista.findViewById(R.id.prodCostoStd);
        EditText normal = vista.findViewById(R.id.prodPcioNormal);
        EditText p1 = vista.findViewById(R.id.prodPcio1);
        EditText p2 = vista.findViewById(R.id.prodPcio2);
        EditText p3 = vista.findViewById(R.id.prodPcio3);

        TextView ultimoi = vista.findViewById(R.id.prodCostoUlti);
        TextView promedioi = vista.findViewById(R.id.prodCostoProi);
        TextView estandari = vista.findViewById(R.id.prodCostoStdi);
        TextView normali = vista.findViewById(R.id.prodPcioNori);
        TextView p1i = vista.findViewById(R.id.prodPcio1i);
        TextView p2i = vista.findViewById(R.id.prodPcio2i);
        TextView p3i = vista.findViewById(R.id.prodPcio3i);
        TextView pMargen = vista.findViewById(R.id.prodMargen);

        View tabla = vista.findViewById(R.id.costprecios);
        tabla.setVisibility(v_costos.getLog1() ? View.VISIBLE:View.GONE);

        TableRow rowStd = vista.findViewById(R.id.rowStd);
        TableRow rowUltimo = vista.findViewById(R.id.rowUltimo);
        TableRow rowProm = vista.findViewById(R.id.rowProm);
        rowStd.setVisibility(View.GONE);
        rowUltimo.setVisibility(View.GONE);
        rowProm.setVisibility(View.GONE);

        switch(v_Costeo){
            case 1:
                rowUltimo.setVisibility(View.VISIBLE);
                break;
            case 2:
                rowProm.setVisibility(View.VISIBLE);
                break;
            case 3:
                rowStd.setVisibility(View.VISIBLE);
                break;
        }

        ultimo.setText(v_costos.getDec1().toString());
        estandar.setText(v_costos.getDec2().toString());
        promedio.setText(v_costos.getDec3().toString());
        normal.setText(v_costos.getDec4().toString());
        p1.setText(v_costos.getDec5().toString());
        p2.setText(v_costos.getDec6().toString());
        p3.setText(v_costos.getDec7().toString());
        ultimoi.setText(v_costos.getDec8().toString());
        estandari.setText(v_costos.getDec9().toString());
        promedioi.setText(v_costos.getDec10().toString());
        normali.setText(v_costos.getDec11().toString());
        p1i.setText(v_costos.getDec12().toString());
        p2i.setText(v_costos.getDec13().toString());
        p3i.setText(v_costos.getDec14().toString());

        ultimo.setSelectAllOnFocus(true);
        estandar.setSelectAllOnFocus(true);
        promedio.setSelectAllOnFocus(true);
        normal.setSelectAllOnFocus(true);
        p1.setSelectAllOnFocus(true);
        p2.setSelectAllOnFocus(true);
        p3.setSelectAllOnFocus(true);
        ultimo.requestFocus();


        pMargen.setText(MessageFormat.format("{0}\n{1}",v_producto,v_costos.getTex1()));

        bCerrar.setOnClickListener(view -> v_dialogo.dismiss());
        bGuarda.setOnClickListener(view -> {
            ContentValues mapa=new ContentValues();
            mapa.put("prod",v_prodid);
            mapa.put("cult",ultimo.getText().toString());
            mapa.put("cstd",promedio.getText().toString());
            mapa.put("cpro",estandar.getText().toString());
            mapa.put("pnormal",normal.getText().toString());
            mapa.put("p1",p1.getText().toString());
            mapa.put("p2",p2.getText().toString());
            mapa.put("p3",p3.getText().toString());
            mapa.put("usuaid",usuarioID);
            String xml = Libreria.xmlLineaCapturaSV(mapa,"linea");
            precGuarda(xml);
        });
        v_dialogo.show();
    }

    public void dlgImpuestos(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_nuevoprod, null);
        builder.setCancelable(true);
        builder.setView(vista);
        v_dialogo=builder.create();
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        v_dialogo.getWindow().setLayout(width, -2);
        GridView impuestos = vista.findViewById(R.id.prodImpuestos);
        v_impAdapter = new ListaImpuestosAdapter(v_impuestos,this);
        impuestos.setAdapter(v_impAdapter);
        Button bCerrar = vista.findViewById(R.id.ImpuCerrar);
        Button bGuarda = vista.findViewById(R.id.ImpuGuarda);
        TextView impuesto = vista.findViewById(R.id.impuProducto);
        impuesto.setText(v_producto);
        bCerrar.setOnClickListener(view -> v_dialogo.dismiss());
        bGuarda.setOnClickListener(view -> {
            impuestosGuarda();
        });
        v_dialogo.show();
    }

    private void asignaValores(ContentValues pContentValues){
        TextView vista = findViewById(R.id.prodTitulo);
        Spinner marca = findViewById(R.id.prodMarca);
        Spinner linea = findViewById(R.id.prodLinea);
        Spinner spincalpre = findViewById(R.id.prodCalprecio);
        Spinner margenes = findViewById(R.id.prodTipomargen);
        Spinner divisas = findViewById(R.id.prodDivisa);

        EditText producto = findViewById(R.id.prodProducto);
        EditText codigo = findViewById(R.id.prodCodigo);
        EditText sustancia = findViewById(R.id.prodSustancia);
        EditText claveSat = findViewById(R.id.prodClavesat);
        CheckBox caduca = findViewById(R.id.prodCaduca);
        CheckBox fracci = findViewById(R.id.prodFracc);
        CheckBox activo = findViewById(R.id.prodActivo);

        TextView avisos = findViewById(R.id.costAviso);

        v_producto = pContentValues.getAsString("producto");
        vista.setText(v_producto);
        producto.setText(v_producto);
        codigo.setText(pContentValues.getAsString("codigo"));
        claveSat.setText(pContentValues.getAsString("sat"));
        sustancia.setText(pContentValues.getAsString("sustancia"));
        caduca.setChecked(Libreria.getBoolean(pContentValues.getAsString("caduca")));
        fracci.setChecked(Libreria.getBoolean(pContentValues.getAsString("granel")));
        activo.setChecked(Libreria.getBoolean(pContentValues.getAsString("activo")));

        Integer cprecio = 0;
        Integer datos = Integer.parseInt(Libreria.traeInfo(pContentValues.getAsString("marca"),"0"));
        marca.setSelection(servicio.traePosicion(42,datos));
        datos = Integer.parseInt(Libreria.traeInfo(pContentValues.getAsString("linea"),"0"));
        linea.setSelection(servicio.traePosicion(44,datos));
        datos = Integer.parseInt(Libreria.traeInfo(pContentValues.getAsString("cprecio"),"0"));
        cprecio = datos;
        spincalpre.setSelection(servicio.traePosicion(54,cprecio));
        datos = Integer.parseInt(Libreria.traeInfo(pContentValues.getAsString("familia"),"0"));
        margenes.setSelection(servicio.traePosicion(-1,datos));
        datos = Integer.parseInt(Libreria.traeInfo(pContentValues.getAsString("divisa"),"0"));
        divisas.setSelection(servicio.traePosicion(32,datos));

        String tasas=pContentValues.getAsString("impuestos");
        if(Libreria.tieneInformacion(tasas)){
            v_impuestos = new ArrayList<>();
            Generica gen;
            String[] portasa=tasas
                    .split(Pattern.quote("|"));
            if(portasa.length!=0){
                String campos[];
                for(String ptasa:portasa){
                    campos = ptasa.split(",");
                    gen = new Generica(Integer.parseInt(campos[0]));
                    gen.setTex1(campos[1]);
                    gen.setLog1(Libreria.getBoolean(campos[2]));
                    v_impuestos.add(gen);
                }
            }
        }

        v_costos = new Generica(0);

        String dato=Libreria.traeInfo(pContentValues.getAsString("cult"),"0");
        v_costos.setDec1(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("cstd"),"0");
        v_costos.setDec2(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("cpro"),"0");
        v_costos.setDec3(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("pnormal"),"0");
        v_costos.setDec4(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p1"),"0");
        v_costos.setDec5(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p2"),"0");
        v_costos.setDec6(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p3"),"0");
        v_costos.setDec7(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("culti"),"0");
        v_costos.setDec8(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("cstdi"),"0");
        v_costos.setDec9(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("cproi"),"0");
        v_costos.setDec10(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("pnormali"),"0");
        v_costos.setDec11(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p1i"),"0");
        v_costos.setDec12(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p2i"),"0");
        v_costos.setDec13(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("p3i"),"0");
        v_costos.setDec14(new BigDecimal(dato).setScale(2,BigDecimal.ROUND_HALF_EVEN));
        dato=Libreria.traeInfo(pContentValues.getAsString("valor"),"0");
        v_costos.setTex1(dato);
        dato=Libreria.traeInfo(pContentValues.getAsString("mensaje"),"");
        v_costos.setTex2(dato);

        //dato=Libreria.traeInfo(pContentValues.getAsString("cprecio"),"0");
        //Integer cprecio=Integer.parseInt(dato);
        v_costos.setLog1(cprecio==303 || cprecio==304);

        avisos.setText(Libreria.traeInfo(v_costos.getTex2()));

        if(v_prodid>0){
            v_impu.setVisibility(View.VISIBLE);
            v_costo.setVisibility(View.VISIBLE);
            actualizaToolbar2(usuario, v_prodid==0 ? "Alta de Producto" : "Edicion de producto");
            v_producto = pContentValues.getAsString("producto");
            vista.setText(v_producto);
            codigo.requestFocus();
        }else{
            v_impu.setVisibility(View.GONE);
            v_costo.setVisibility(View.GONE);
        }

        v_Costeo = Libreria.tieneInformacionEntero(Libreria.traeInfo(pContentValues.getAsString("tcosteo"),"0"));
        v_mapaDatos = pContentValues;
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_PRODGUARDA:
                    if(output.getExito()){
                        Integer prod = obj.getAsInteger("anexo");
                        v_prodid = prod!=null ? prod : v_prodid;
                        v_peticion = 0;
                        wsBuscaProd(v_prodid);
                    }
                case TAREA_PRECGUARDA:
                case TAREA_MGNFAMIGUARDA:
                case TAREA_IMPUGUARDA:
                    if(output.getExito()){
                        if(v_dialogo!=null)
                            v_dialogo.dismiss();
                        //wsBuscaProd(v_prodid);
                    }
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error2);
                    break;
                case TAREA_TRAEPRODMOVIL:
                    asignaValores(obj);
                    if(v_peticion==1){
                        dlgImpuestos();
                    }else if(v_peticion==2){
                        dlgCostos();
                    }
                    break;
            }
        }else {

            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void precGuarda(String pXml){
        peticionWS(TAREA_PRECGUARDA,"SQL","SQL",pXml,usuarioID,"");
    }

    public void wsBuscaProd(Integer pProdid){
        peticionWS(TAREA_TRAEPRODMOVIL,"SQL","SQL",pProdid+"","","");
    }

    private void prodGuarda(String pXml){
        peticionWS(TAREA_PRODGUARDA,"SQL","SQL",pXml,"","");
    }

    private void impuestosGuarda(){
        if(v_impAdapter == null){
            dlgMensajeError("Sin impuestos capturados",R.drawable.mensaje_error2);
        }
        String ids="";
        List<Generica> impuestos = v_impAdapter.getItems();
        if(!impuestos.isEmpty()){
            for(Generica gen:impuestos){
                ids+=gen.getId()+","+gen.getLog1()+"|";
            }
            ids=ids.substring(0,ids.length()-1);
        }
        peticionWS(TAREA_IMPUGUARDA,"SQL","SQL",ids,v_prodid+"","");
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult.getContents() != null ) {
                EditText codigo = findViewById(R.id.prodCodigo);
                codigo.setText(Libreria.traeInfo(intentResult.getContents()));

            }else
                muestraMensaje("Error al escanear codigo", R.drawable.mensaje_error);
        }
    }
}