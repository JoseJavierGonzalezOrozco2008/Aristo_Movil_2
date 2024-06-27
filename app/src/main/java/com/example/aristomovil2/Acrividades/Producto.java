package com.example.aristomovil2.Acrividades;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_IMPUGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRECGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_PRODGUARDA;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_TRAEPRODMOVIL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ListaImpuestosAdapter;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Producto extends ActividadBase {
    private Integer v_prodid;
    private String v_producto;
    private Button v_impu,v_costo;
    private ArrayList<Generica> v_impuestos;
    private Generica v_costos;
    private AlertDialog v_dialogo;
    private ListaImpuestosAdapter v_impAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        rowMarca.setVisibility(mMarca ? View.VISIBLE : View.GONE);
        rowLinea.setVisibility(mLinea ? View.VISIBLE : View.GONE);
        rowSustancia.setVisibility(mSustancia ? View.VISIBLE : View.GONE);
        rowClavesat.setVisibility(mClavesat ? View.VISIBLE : View.GONE);
        rowDivisa.setVisibility(mDivisa ? View.VISIBLE : View.GONE);

        ArrayList<String> marcas = servicio.traeDcatalogo(42);
        ArrayList<String> lineas = servicio.traeDcatalogo(44);
        ArrayList<String> calpre = servicio.traeDcatalogo(54);
        ArrayList<String> margen = servicio.traeDcatalogo(-1);
        ArrayList<String> divisa = servicio.traeDcatalogo(32);

        ArrayAdapter<String> spinMarca = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, marcas);
        marca.setAdapter(spinMarca);
        ArrayAdapter<String> spinlinea = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, lineas);
        linea.setAdapter(spinlinea);
        ArrayAdapter<String> spincalpreca = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, calpre);
        spincalpre.setAdapter(spincalpreca);
        ArrayAdapter<String> spinmargen = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, margen);
        margenes.setAdapter(spinmargen);
        ArrayAdapter<String> spindivisa = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, divisa);
        divisas.setAdapter(spindivisa);
        v_impuestos = new ArrayList();
        spincalpre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> margen = servicio.traeMargen(servicio.traeDcatIdporAbrevi(54,adapterView.getSelectedItem()+""));
                ArrayAdapter<String> spinmargen = new ArrayAdapter(adapterView.getContext(), R.layout.item_spinner, R.id.item_spinner, margen);
                margenes.setAdapter(spinmargen);
                spinmargen.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        v_costos = new Generica(0);
        if(v_prodid > 0){
            v_producto = extras.getString("producto");
            vista.setText(v_producto);
            producto.setText(v_producto);
            rowProd.setVisibility(View.GONE);
            codigo.setText(extras.getString("codigo"));
            claveSat.setText(extras.getString("sat"));
            sustancia.setText(extras.getString("sustancia"));
            caduca.setChecked(Libreria.getBoolean(extras.getString("caduca")));
            fracci.setChecked(Libreria.getBoolean(extras.getString("granel")));
            activo.setChecked(Libreria.getBoolean(extras.getString("activo")));

            Integer cprecio = 0;
            Integer datos = Integer.parseInt(extras.getString("marca","0"));
            marca.setSelection(servicio.traePosicion(42,datos));
            datos = Integer.parseInt(extras.getString("linea","0"));
            linea.setSelection(servicio.traePosicion(44,datos));
            datos = Integer.parseInt(extras.getString("cprecio","0"));
            cprecio = datos;
            spincalpre.setSelection(servicio.traePosicion(54,cprecio));
            datos = Integer.parseInt(extras.getString("familia","0"));
            margenes.setSelection(servicio.traePosicion(-1,datos));
            datos = Integer.parseInt(extras.getString("divisa","0"));
            divisas.setSelection(servicio.traePosicion(32,datos));

            v_costos.setDec1(new BigDecimal(extras.getString("cult","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec2(new BigDecimal(extras.getString("cstd","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec3(new BigDecimal(extras.getString("cpro","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec4(new BigDecimal(extras.getString("pnormal","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec5(new BigDecimal(extras.getString("p1","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec6(new BigDecimal(extras.getString("p2","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec7(new BigDecimal(extras.getString("p3","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec8(new BigDecimal(extras.getString("culti","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec9(new BigDecimal(extras.getString("cstdi","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec10(new BigDecimal(extras.getString("cproi","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec11(new BigDecimal(extras.getString("pnormali","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec12(new BigDecimal(extras.getString("p1i","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec13(new BigDecimal(extras.getString("p2i","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setDec14(new BigDecimal(extras.getString("p3i","0")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
            v_costos.setTex1(extras.getString("valor","0%"));
            v_costos.setTex2(extras.getString("mensaje",""));
            v_costos.setLog1(cprecio==303 || cprecio==304);

            String tasas=extras.getString("impuestos");
            if(Libreria.tieneInformacion(tasas)){
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
        v_impu.setOnClickListener(view -> dlgImpuestos());
        v_costo.setOnClickListener(view -> dlgCostos());
        avisos.setText(Libreria.traeInfo(v_costos.getTex2()));

        bguarda.setOnClickListener(view -> {
            ContentValues mapa=new ContentValues();
            mapa.put("prodid",v_prodid);
            mapa.put("codigo",codigo.getText().toString());
            mapa.put("articulo",producto.getText().toString());
            mapa.put("sustancia",sustancia.getText().toString());
            mapa.put("marca",servicio.traeDcatIdporAbrevi(42,marca.getSelectedItem()+""));
            mapa.put("line",servicio.traeDcatIdporAbrevi(44,linea.getSelectedItem()+""));
            mapa.put("cprecio",servicio.traeDcatIdporAbrevi(54,spincalpre.getSelectedItem()+""));
            mapa.put("divisa",servicio.traeDcatIdporAbrevi(32,divisas.getSelectedItem()+""));
            mapa.put("margen",servicio.traeDcatIdporAbrevi(-1,margenes.getSelectedItem()+""));
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

       /* TextView ultimoa = vista.findViewById(R.id.prodCostoUlta);
        TextView promedioa = vista.findViewById(R.id.prodCostoProa);
        TextView estandara = vista.findViewById(R.id.prodCostoStda);
        TextView normala = vista.findViewById(R.id.prodPcioNora);
        TextView p1a = vista.findViewById(R.id.prodPcio1a);
        TextView p2a = vista.findViewById(R.id.prodPcio2a);
        TextView p3a = vista.findViewById(R.id.prodPcio3a);*/



        View tabla = vista.findViewById(R.id.costprecios);
        tabla.setVisibility(v_costos.getLog1() ? View.VISIBLE:View.GONE);

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
        /*ultimoa.setText(v_costos.getDec1().toString());
        estandara.setText(v_costos.getDec2().toString());
        promedioa.setText(v_costos.getDec3().toString());
        normala.setText(v_costos.getDec4().toString());
        p1a.setText(v_costos.getDec5().toString());
        p2a.setText(v_costos.getDec6().toString());
        p3a.setText(v_costos.getDec7().toString());*/

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

        dato=Libreria.traeInfo(pContentValues.getAsString("cprecio"),"0");
        Integer cprecio=Integer.parseInt(dato);
        v_costos.setLog1(cprecio==303 || cprecio==304);

        if(v_prodid>0){
            v_impu.setVisibility(View.VISIBLE);
            v_costo.setVisibility(View.VISIBLE);
            actualizaToolbar2(usuario, v_prodid==0 ? "Alta de Producto" : "Edicion de producto");
            TextView vista = findViewById(R.id.prodTitulo);
            v_producto = pContentValues.getAsString("producto");
            vista.setText(v_producto);
            TextView codigo = findViewById(R.id.prodCodigo);
            codigo.requestFocus();
        }else{
            v_impu.setVisibility(View.GONE);
            v_costo.setVisibility(View.GONE);
        }
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
                    }
                case TAREA_PRECGUARDA:
                case TAREA_MGNFAMIGUARDA:
                case TAREA_IMPUGUARDA:
                    if(output.getExito()){
                        if(v_dialogo!=null)
                            v_dialogo.dismiss();
                        wsBuscaProd(v_prodid);
                    }
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito:R.drawable.mensaje_error2);
                    break;
                case TAREA_TRAEPRODMOVIL:
                    asignaValores(obj);
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
}