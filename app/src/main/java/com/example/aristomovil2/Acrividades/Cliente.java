package com.example.aristomovil2.Acrividades;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.adapters.ClienteAdapter;
import com.example.aristomovil2.adapters.CuentaAdapter;
import com.example.aristomovil2.adapters.VentasAdapter;
import com.example.aristomovil2.facade.Estatutos;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.modelos.Cuenta;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends ActividadBase {
    private Spinner v_colonia;
    private EditText v_calle,v_ext,v_int,v_refe,v_tele,v_cp,v_alias;
    private TextView v_estado,v_Muni;
    private ArrayList<Colonia> v_listacolonia;
    private Integer v_emprid,v_clteid,v_coloid,v_domiid,v_regiid,v_Tipo,v_fopa,v_cfdi;
    private boolean v_nuevo;
    private ScrollView v_LineaFiscal;
    private String v_razon,v_rfc,v_correo;
    private AlertDialog v_dialogo;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent resultado = result.getData();
                Integer actividad = resultado!=null ? resultado.getIntExtra("actividad",0):0;
                switch(actividad){
                    case 1:
                        traeCuentas(v_clteid+"");
                        break;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        inicializarActividad2("Folio  Tipo","Cliente");
        Bundle extras = getIntent().getExtras();
        v_clteid = extras.getInt("clteid",0);

        v_alias = findViewById(R.id.clteAlias);
        v_colonia = findViewById(R.id.clteColonia);
        v_calle  = findViewById(R.id.clteCalle);
        v_ext  = findViewById(R.id.clteExt);
        v_int  = findViewById(R.id.clteint);
        v_refe  = findViewById(R.id.clteRef);
        v_tele  = findViewById(R.id.clteTEl);
        v_cp  = findViewById(R.id.clteCp);
        v_estado = findViewById(R.id.clteEstado);
        v_Muni = findViewById(R.id.clteMunicipio);
        Button boton = findViewById(R.id.clteGuarda);
        Button fiscal = findViewById(R.id.clteFiscal);
        Button cuentas = findViewById(R.id.clteCuenta);
        Button edocta = findViewById(R.id.clteEdocta);
        v_LineaFiscal = findViewById(R.id.clteLyFiscal);
        v_listacolonia = new ArrayList();

        ArrayAdapter<String> coloniaAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, new ArrayList());
        v_colonia.setAdapter(coloniaAdapter);

        View.OnKeyListener listener = (view, i, keyEvent) -> {
            if(view.getId()==v_cp.getId()){
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(!Libreria.tieneInformacion(v_cp.getText().toString())){
                        dlgMensajeError("no puede estar vacio",R.drawable.mensaje_error);
                        return false;
                    }
                    traeColonias(v_cp.getText().toString());
                    hideKeyboard(view);
                    return true;
                }return false;
            }
            if(esHorizontal()){
                hideKeyboard(view);
            }
            return false;
        };

        v_cp.setOnKeyListener(listener);
        v_alias.setOnKeyListener(listener);
        v_colonia.setOnKeyListener(listener);
        v_calle.setOnKeyListener(listener);
        v_ext.setOnKeyListener(listener);
        v_int.setOnKeyListener(listener);
        v_refe.setOnKeyListener(listener);
        v_tele.setOnKeyListener(listener);

        v_nuevo = v_clteid==0;
        v_estado.setText("");
        v_Muni.setText("");

        boton.setOnClickListener(view -> wsGuarda());
        fiscal.setOnClickListener(view -> {
            Intent intent = new Intent(this, Fiscal.class);
            intent.putExtra("clteid",v_clteid);
            intent.putExtra("cliente",v_alias.getText().toString());
            startActivity(intent);
        });
        cuentas.setOnClickListener(view -> {
            traeCuentas(v_clteid+"");
        });
        edocta.setOnClickListener(view -> {
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
            dialog.setTitle("Imprime estado de cuenta");
            dialog.setMessage("¿Esta seguro de imprimir el estado de cuenta?");
            dialog.setNegativeButton("Pantalla",(dialogInterface, i1) -> {
                aPantalla(true);
                imprimeEdoCta();
            });
            dialog.setPositiveButton("Imprimir", (dialogInterface, i2) -> {
                aPantalla(true);
                imprimeEdoCta();
            });
            dialog.setNeutralButton("Regresar", (dialogInterface, i2) -> {dialogInterface.dismiss();});
            dialog.show();
        });

        if(v_nuevo){
            fiscal.setVisibility(View.GONE);
            cuentas.setVisibility(View.GONE);
        }else if(esMono()){
            edocta.setVisibility(View.VISIBLE);
        }
        actualizaToolbar2(v_nuevo ? "Registra un nuevo cliente": "Edita cliente","");
        v_emprid = 0;
        v_domiid = 0;
        v_coloid = 0;
        v_regiid = 0;
        v_razon=v_rfc=v_correo="";
        v_fopa = 362;
        v_cfdi = 437;
        traeCliente(v_clteid+"");
        actualizaVista(0);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent data = new Intent();
                data.putExtra("actividad",3);
                data.putExtra("alias",v_alias.getText().toString());
                setResult(RESULT_OK,data);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            switch (output.getTarea()){
                case TAREA_COLONIAS_CP:
                    actualizaColonias();
                    break;
                case TAREA_GUARDA_CLIENTE:
                    if(output.getExito()){
                        onBackPressed();
                    }else{
                        dlgMensajeError(output.getMensaje(),R.drawable.mensaje_error);
                    }
                    break;
                case TAREA_TRAECLTE:
                    String alias=obj.getAsString("alias");
                    String colonia=obj.getAsString("colonia");
                    v_alias.setText(alias);
                    v_razon=Libreria.traeInfo(obj.getAsString("razon"));
                    v_rfc=Libreria.traeInfo(obj.getAsString("rfc"));;
                    v_correo=Libreria.traeInfo(obj.getAsString("correo"));;
                    v_tele.setText(Libreria.traeInfo(obj.getAsString("tel")));
                    v_calle.setText(obj.getAsString("calle"));
                    v_ext.setText(Libreria.traeInfo(obj.getAsString("exterior")));
                    v_int.setText(Libreria.traeInfo(obj.getAsString("interior")));
                    v_refe.setText(Libreria.traeInfo(obj.getAsString("refe")));
                    v_cp.setText(obj.getAsString("cp"));
                    v_estado.setText(obj.getAsString("estado"));
                    v_Muni.setText(obj.getAsString("muni"));
                    v_emprid = obj.getAsInteger("emprid");
                    v_domiid = obj.getAsInteger("domiid");
                    v_coloid = obj.getAsInteger("coloid");
                    v_regiid = obj.getAsInteger("regiid");
                    v_fopa = obj.getAsInteger("fopa");
                    v_cfdi = obj.getAsInteger("cfdi");
                    ArrayList<String> coloniasNombres = new ArrayList<>();
                    coloniasNombres.add(colonia);
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,  R.layout.item_spinner, R.id.item_spinner, coloniasNombres);
                    v_colonia.setAdapter(spinnerAdapter);

                    v_alias.requestFocus();
                    actualizaToolbar2(v_nuevo ? "Registra un nuevo cliente": alias,"");
                    break;
                case TAREA_TRAECUENTA:
                    dlgBuscaCuentas();
                    break;
                case TAREA_IMPRIMEEDOCTA:
                    String v_ticket = obj.getAsString("anexo");
                    doImprime(v_ticket,false);
                    break;
            }
        } else {
            cierraDialogo();
            dlgMensajeError("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void traeColonias(String cp){
        peticionWS(Enumeradores.Valores.TAREA_COLONIAS_CP, "SQL", "SQL", cp, "", "");
    }

    private void traeCliente(String pid){
        peticionWS(Enumeradores.Valores.TAREA_TRAECLTE, "SQL", "SQL", pid, "", "");
    }

    private void traeCuentas(String pid){
        peticionWS(Enumeradores.Valores.TAREA_TRAECUENTA, "SQL", "SQL", pid, "", "");
    }

    private void imprimeEdoCta(){
        peticionWS(Enumeradores.Valores.TAREA_IMPRIMEEDOCTA, "SQL", "SQL", v_clteid+"", "", "");
    }

    public void wsGuarda(){
        Integer colo = v_colonia.getSelectedItemPosition();
        //Integer regi = v_regimen.getSelectedItemPosition();
        String alias = v_alias.getText().toString();
        String calle = v_calle.getText().toString();
        String ext = v_ext.getText().toString();
        String inte = v_int.getText().toString();
        String telefono = v_tele.getText().toString();
        String cp = v_cp.getText().toString();
        String referencia = v_refe.getText().toString();
        //String regimen = regi >0 ? (v_listaregimen.get(regi).getEnt1()+"") : (v_regiid+"");
        String colonia = (colo >=0 && !v_listacolonia.isEmpty()) ? (v_listacolonia.get(colo).getId()+"") : (v_coloid+"");

        ContentValues mapa = new ContentValues();
        mapa.put("rfc", Libreria.tieneInformacion(v_rfc) ? v_rfc: "RFC"+alias.substring(1,3));
        mapa.put("razon", !Libreria.tieneInformacion(v_razon) ? alias : v_razon);
        mapa.put("alias", alias);
        mapa.put("regiid", v_regiid);
        mapa.put("correo", v_correo);
        mapa.put("nota", "");
        mapa.put("telefono", telefono);
        mapa.put("calle", calle);
        mapa.put("exterior", ext);
        mapa.put("interior", inte);
        mapa.put("coloid", colonia);
        mapa.put("refe", referencia);
        mapa.put("usuaid", usuarioID);
        mapa.put("cp", cp);
        mapa.put("nuevo", v_nuevo);
        mapa.put("empr", v_emprid);
        mapa.put("domi", v_domiid);
        mapa.put("id", v_clteid);
        mapa.put("fopaid", v_fopa);
        mapa.put("cfdiid", v_cfdi);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_CLIENTE, "", "",xml,"","");
    }

    private void actualizaColonias(){
        v_listacolonia = servicio.traeColonias();

        ArrayList<String> coloniasNombres = new ArrayList<>();

        for(Colonia c : v_listacolonia)
            coloniasNombres.add(c.getColonia());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,  R.layout.item_spinner, R.id.item_spinner, coloniasNombres);
        //spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        v_colonia.setAdapter(spinnerAdapter);
        if(v_listacolonia!=null && !v_listacolonia.isEmpty()){
            v_estado.setText(v_listacolonia.get(0).getEstado());
            v_Muni.setText(v_listacolonia.get(0).getMuni());
        }else{
            v_estado.setText("");
            v_Muni.setText("");
        }
    }

    public void actualizaVista(Integer pTipo){
        v_Tipo=0;
        if(pTipo==0){
            v_LineaFiscal.setVisibility(View.GONE);
        }else if(pTipo==1){
            v_LineaFiscal.setVisibility(View.VISIBLE);
        }

    }

    public void dlgBuscaCuentas(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View vista=inflater.inflate(R.layout.dialogo_cliente, null);
        View titulo=inflater.inflate(R.layout.item_titulo, null);
        builder.setView(vista);
        builder.setCustomTitle(titulo);
        builder.setTitle("");
        //builder.setContentView();
        builder.setCancelable(true);

        LinearLayout busqueda = vista.findViewById(R.id.clteLtBusca);
        busqueda.setVisibility(View.GONE);

        final EditText cantidad = vista.findViewById(R.id.clteBusca);
        ImageButton busca = vista.findViewById(R.id.btnClteBusca);
        TextView titTitulo = titulo.findViewById(R.id.tit_titulo);
        ImageButton nuevo = titulo.findViewById(R.id.btnTitNuevo);
        ImageButton regresa = titulo.findViewById(R.id.btnTitRegresa);
        ListView v_lista = vista.findViewById(R.id.listClte);
        titTitulo.setText("Lista cuentas \n"+v_alias.getText().toString());

        List<Cuenta> listaCuentas = servicio.traeCuentas();
        CuentaAdapter cuenta = new CuentaAdapter(listaCuentas,this);
        v_lista.setAdapter(cuenta);
        v_lista.setEmptyView(vista.findViewById(R.id.clteListSinReg));
        //v_lista.setEmptyView(findViewById(R.id.cuclSinRegistro));

        cantidad.setText("");
        cantidad.requestFocus();
        cantidad.setSelectAllOnFocus(true);


        int[] colors = {0, 0xFF000000, 0};
        v_lista.setDivider(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors));
        v_lista.setDividerHeight(1);

        v_dialogo = builder.create();
        regresa.setOnClickListener(v-> {
            if(v_dialogo!=null){
                v_dialogo.dismiss();
            }
        });

        nuevo.setOnClickListener(v-> {
            Intent intent = new Intent(this, Cuentas.class);
            intent.putExtra("clteid",v_clteid);
            intent.putExtra("cuclid",0);
            mStartForResult.launch(intent);
            v_dialogo.dismiss();
        });

        cantidad.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                busca.callOnClick();
            }
            return false;
        });

        v_dialogo.show();
    }

    public void cambiaCuenta(Cuenta pCuenta){
        v_dialogo.dismiss();
        Intent intent = new Intent(this, Cuentas.class);
        intent.putExtra("clteid",pCuenta.getClteid());
        intent.putExtra("cuclid",pCuenta.getCuclid());
        intent.putExtra("cliente",pCuenta.getNombre());
        mStartForResult.launch(intent);
    }

    public ActivityResultLauncher<Intent> getmStartForResult() {
        return mStartForResult;
    }
}