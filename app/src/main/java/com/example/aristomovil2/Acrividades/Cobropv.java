package com.example.aristomovil2.Acrividades;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.DcarritoAdapter;
import com.example.aristomovil2.async.AsyncBluetoothEscPosPrint;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.servicio.ServicioImpresora;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;

public class Cobropv extends ActividadBase {
    private Integer v_estacion,v_cuendefault,v_imprime_espacios,v_tipovnta;
    private String v_vntafolio,v_nombrecliente,v_ticket,v_nombre_impresora;
    private Boolean v_metpago,v_pideretiro,v_virtuales;
    private Double v_importe;
    private EditText v_efectivo,v_credito,v_trans,v_debito,v_credref,v_debref,v_trnsref;
    private TextView v_falta,v_cambio,v_cambio2;
    private ArrayList<EditText> v_captura;
    private ArrayList<Generica> v_listacuentas;
    private Spinner spinCred,spinDeb,spinTran;
    private LinearLayout v_lyCambio1,v_lyCapt01,v_lyCapt02,v_lyCapt03,v_lyCambio2;
    private ScrollView v_lyPagos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobropv);
        inicializarActividad2("Folio  Tipo","Cliente");
        Bundle extras = getIntent().getExtras();
        v_vntafolio = extras.getString("folio");
        v_importe = extras.getDouble("importe",99.99);
        v_estacion = extras.getInt("estacion");
        v_metpago = extras.getBoolean("metpago");
        v_nombrecliente = extras.getString("cliente");
        v_tipovnta = extras.getInt("tipoVenta");
        v_virtuales = extras.getBoolean("virtuales",false);
        SharedPreferences sharedPreferences = getSharedPreferences("renglones", MODE_PRIVATE);
        SharedPreferences preferencesConf = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        String cuentaBanco = sharedPreferences.getString("cuentabanco", "");
        v_cuendefault = sharedPreferences.getInt("cuentaid", -1);
        v_imprime_espacios = sharedPreferences.getInt("espacios", 3);
        v_nombre_impresora = preferencesConf.getString("impresora", "Predeterminada");
        TextView porcobrar = findViewById(R.id.cbroTotal);
        TextView leyenda = findViewById(R.id.cbroLeyendaCredito);
        v_falta = findViewById(R.id.cbroFalta);
        v_cambio = findViewById(R.id.cbroCambio);
        v_cambio2 = findViewById(R.id.cbroCambio2);
        v_lyCapt01 = findViewById(R.id.cbroLayCap01);
        v_lyCapt02 = findViewById(R.id.cbroLayCap02);
        LinearLayout v_lyCapt04 = findViewById(R.id.cbroLayCap04);
        v_lyCapt03 = findViewById(R.id.cbroLayCambio03);
        v_lyPagos = findViewById(R.id.cbroPagos);
        Button cobra = findViewById(R.id.cbroPagar);
        Button limpia = findViewById(R.id.cbroBtnLimpia);
        Button efectivo = findViewById(R.id.cbroBtnEfe);
        Button credito = findViewById(R.id.cbroBtnCred);
        Button debito = findViewById(R.id.cbroBtnDeb);
        Button transfer = findViewById(R.id.cbroBtnTran);
        Button btn20 = findViewById(R.id.cbro20);
        Button btn50 = findViewById(R.id.cbro50);
        Button btn100 = findViewById(R.id.cbro100);
        Button btn200 = findViewById(R.id.cbro200);
        Button btn500 = findViewById(R.id.cbro500);
        Button btnTicket = findViewById(R.id.cbroBtnTicket);
        Button btnNuevo = findViewById(R.id.cbroBtnNuevo);
        Button btnRegresa = findViewById(R.id.cbroBtnRegresa);
        Button btnFinaliza = findViewById(R.id.cbroBtnCredito);
        v_efectivo = findViewById(R.id.cbroEtEfe);
        v_credito = findViewById(R.id.cbroTCredito);
        v_debito = findViewById(R.id.cbroTDebito);
        v_trans = findViewById(R.id.cbroTrans);
        v_credref = findViewById(R.id.cbroTCreRef);
        v_debref = findViewById(R.id.cbroTDebRef);
        v_trnsref = findViewById(R.id.cbroTransRefe);
        spinCred = findViewById(R.id.cbrocuentacred);
        spinDeb = findViewById(R.id.cbrocuentadeb);
        spinTran = findViewById(R.id.cbrocuentatran);
        v_lyCambio1 = findViewById(R.id.cbroLayCambio02);
        v_lyCambio2 = findViewById(R.id.cbroLyCambio);
        v_pideretiro = false;

        porcobrar.setText( v_importe + "" );
        efectivo.setOnClickListener(view -> onColocarDinero(v_importe,v_efectivo,null,true));
        credito.setOnClickListener(view -> onColocarDinero(v_importe,v_credito,v_credref,true));
        debito.setOnClickListener(view -> onColocarDinero(v_importe,v_debito,v_debref,true));
        transfer.setOnClickListener(view -> onColocarDinero(v_importe,v_trans,v_trnsref,true));
        btn20.setOnClickListener(view -> onColocarDinero(20.0,v_efectivo,null,false));
        btn50.setOnClickListener(view -> onColocarDinero(50.0,v_efectivo,null,false));
        btn100.setOnClickListener(view -> onColocarDinero(100.0,v_efectivo,null,false));
        btn200.setOnClickListener(view -> onColocarDinero(200.0,v_efectivo,null,false));
        btn500.setOnClickListener(view -> onColocarDinero(500.0,v_efectivo,null,false));
        limpia.setOnClickListener(view -> onLimpiaCobro());
        cobra.setOnClickListener(view -> wsCobra());
        btnTicket.setOnClickListener(view -> {imprimeTicket();});
        btnNuevo.setOnClickListener(view -> onBackPressed());
        v_captura = new ArrayList();
        v_captura.add(v_efectivo);
        v_captura.add(v_credito);
        v_captura.add(v_debito);
        v_captura.add(v_trans);

        v_listacuentas = new ArrayList();
        String[] cuentasbanco = cuentaBanco.split(",");
        if(cuentasbanco.length > 0) {
            Generica cuenta;
            int contador = 0;
            for (String s : cuentasbanco) {
                String[] datos = s.split("\\|");
                cuenta = new Generica(contador);
                contador++;
                cuenta.setEnt1(Integer.parseInt(datos[1]));
                cuenta.setTex1(datos[0]);
                v_listacuentas.add(cuenta);
            }
        }
        if(v_listacuentas.size()==1){
            spinCred.setVisibility(View.GONE);
            spinDeb.setVisibility(View.GONE);
            spinTran.setVisibility(View.GONE);
        }

        View.OnKeyListener listener = (view, i, keyEvent) -> {
            onActualizaSaldos();
            return false;
        };
        v_efectivo.setOnKeyListener(listener);
        v_credito.setOnKeyListener(listener);
        v_debito.setOnKeyListener(listener);
        v_trans.setOnKeyListener(listener);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent data = new Intent();
                data.putExtra("actividad",1);
                data.putExtra("deberetirar",v_pideretiro);
                setResult(RESULT_OK,data);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
        if(!v_metpago){
            v_lyCapt03.setVisibility(View.VISIBLE);
            v_lyPagos.setVisibility(View.GONE);
            v_lyCapt01.setVisibility(View.GONE);
            v_lyCapt02.setVisibility(View.GONE);
            btnRegresa.setOnClickListener(view -> onBackPressed());
            btnFinaliza.setOnClickListener(view -> wsCobra());
            String mensaje = "Confirme que {0} del cliente:\n {1} \n debe de finalizar a credito" ;
            leyenda.setText(MessageFormat.format(mensaje,(v_tipovnta==52?"el pedido":"la venta"),v_nombrecliente));
        }
        onAdapterCuentas();
        onActualizaSaldos();
        colocaTitulo();
        v_efectivo.requestFocus();
    }

    public void onColocarDinero(Double pimporte, EditText pImpor, EditText pRefe,Boolean pReemplaza){
        if(pImpor!=null){
            double coloca= 0;
            if(pReemplaza){
                coloca = pimporte;
            }else{
                String info = pImpor.getText().toString();
                Float anterior = Libreria.tieneInformacionFloat(info,0);
                coloca = anterior + pimporte;
            }
            pImpor.setText(coloca + "");
            onActualizaSaldos();
            if(pRefe!=null){
                pRefe.requestFocus();
            }else{
                pImpor.requestFocus();
            }
        }
    }

    private void onActualizaSaldos(){
        if(!v_captura.isEmpty()){
            double registro = 0.0,cambio=0.0,falta=0.0;
            registro = saldoCapturado();
            if(registro>=v_importe){
                cambio = registro-v_importe;
            }else{
                falta = v_importe-registro;
            }
            BigDecimal cambios=new BigDecimal(cambio).setScale(2,BigDecimal.ROUND_HALF_EVEN);
            BigDecimal faltas=new BigDecimal(falta).setScale(2,BigDecimal.ROUND_HALF_EVEN);
            v_falta.setText(faltas.toString());
            v_cambio.setText(cambios.toString());
            v_cambio2.setText(cambios.toString());
        }
    }

    private double saldoCapturado(){
        double registro = 0.0;
        for(EditText captura:v_captura){
            if(captura!=null && captura.getText()!=null && Libreria.tieneInformacion(captura.getText().toString())){
                registro +=Double.parseDouble(captura.getText().toString());
            }
        }
        return registro;
    }

    private void onAdapterCuentas(){
        if(!v_listacuentas.isEmpty()){
            ArrayList<String> textos=new ArrayList();
            int posicion=0;
            for(int i=0;i<v_listacuentas.size();i++){
                textos.add(v_listacuentas.get(i).getTex1());
                if(v_cuendefault==v_listacuentas.get(i).getEnt1()){
                    posicion=i;
                }
            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, textos);

            spinDeb.setAdapter(spinnerAdapter);
            spinCred.setAdapter(spinnerAdapter);
            spinTran.setAdapter(spinnerAdapter);
            spinDeb.setSelection(posicion);
            spinCred.setSelection(posicion);
            spinTran.setSelection(posicion);
        }
    }

    private void onLimpiaCobro(){
        v_efectivo.setText("");
        v_credito.setText("");
        v_debito.setText("");
        v_trans.setText("");
        v_credref.setText("");
        v_debref.setText("");
        v_trnsref.setText("");
        v_efectivo.requestFocus();
    }

    private void wsCobra(){
        Integer cuendeb=v_cuendefault,cuencred=v_cuendefault,cuentrans=v_cuendefault;
        double registro ,falta;
        registro = saldoCapturado();
        falta = v_importe-registro;
        if(falta > 0 && v_metpago){
            muestraMensaje("Debe de cubrir el importe",R.drawable.mensaje_error);
            return;
        }

        if(v_listacuentas.size()>1){
            String cdeb="",ccred="",ctran="";
            cdeb  =spinDeb.getSelectedItem().toString();
            ccred  =spinCred.getSelectedItem().toString();
            ctran  =spinTran.getSelectedItem().toString();
            for(Generica gen:v_listacuentas){
                if(gen.getTex1().equals(cdeb)){
                    cuendeb=gen.getEnt1();
                }
                if(gen.getTex1().equals(ccred)){
                    cuencred=gen.getEnt1();
                }
                if(gen.getTex1().equals(ctran)){
                    cuentrans=gen.getEnt1();
                }
            }
        }

        String vale = "";
        if(Libreria.tieneInformacion(v_trans.getText().toString())){
            vale +="94,"+v_trans.getText().toString()+","+v_trnsref.getText().toString()+","+cuentrans+"|";
        }
        ContentValues mapa=new ContentValues();
        mapa.put("folioventa",v_vntafolio);
        mapa.put("estacion",v_estacion);
        mapa.put("usuaid",usuarioID);
        mapa.put("efectivo",v_efectivo.getText().toString());
        mapa.put("tcredito",v_credito.getText().toString());
        mapa.put("rcredito",v_credref.getText().toString());
        mapa.put("tdebito",v_debito.getText().toString());
        mapa.put("rdebito",v_debref.getText().toString());
        mapa.put("propina","");
        mapa.put("vales",vale);
        mapa.put("credito",!v_metpago);
        mapa.put("ccredito", (!v_credito.getText().equals("") && !v_credref.getText().equals("") ? cuencred:0));
        mapa.put("cdebito",(!v_debito.getText().equals("") && !v_debref.getText().equals("") ? cuendeb:0));

        String xml= Libreria.xmlLineaCapturaSV(mapa,"linea");
        if(v_virtuales || v_tipovnta==52){
            peticionWS(Enumeradores.Valores.TAREA_COBRAR_WS, "", "",xml,v_vntafolio, v_virtuales+"");
        }else{
            peticionWS(Enumeradores.Valores.TAREA_COBRAR, "SQL", "SQL",xml,"", "");
        }

        colocaTitulo();
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();
        switch (output.getTarea()){
            case TAREA_COBRAR_WS:
            case TAREA_COBRAR:
                if(output.getExito()){
                    v_ticket = obj.getAsString("impreso");
                    v_pideretiro = obj.getAsBoolean("deberetirar");
                    //v_ticket = "Hola es una prueba,T1w";
                    if(v_metpago){
                        Double cambio = obj.getAsDouble("cambioef");
                        if(cambio!=null && cambio!=0){
                            v_cambio2.setText(BigDecimal.valueOf(cambio).abs().toString());
                        }else{
                            v_cambio2.setText(BigDecimal.ZERO.toString());
                        }
                    }else{
                        v_lyCambio2.setVisibility(View.GONE);
                    }

                    v_lyCambio1.setVisibility(View.VISIBLE);
                    v_lyPagos.setVisibility(View.GONE);
                    v_lyCapt01.setVisibility(View.GONE);
                    v_lyCapt02.setVisibility(View.GONE);
                    v_lyCapt03.setVisibility(View.GONE);
                    imprimeTicket();
                }else{
                    muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                }
                break;
        }
        if(obj != null){

        }
    }


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

    private void imprimeTicket(){
        BluetoothConnection selectedDevice = traeImpresora(v_nombre_impresora);
        ServicioImpresora impresora = new ServicioImpresora(selectedDevice, this);
        Libreria.imprimeSol(impresora,v_ticket,0);
        try {
            new AsyncBluetoothEscPosPrint(this,false).execute(impresora.Imprimir());
        } catch (/*ExecutionException | InterruptedException e*/ Exception e) {
            //e.printStackTrace();
            System.out.println(e);
        }
    }

}