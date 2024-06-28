package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Colonia;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class Fiscal extends ActividadBase {
    private Spinner v_regimen,v_colonia,v_uso,v_fopa;
    private Integer v_emprid,v_clteid,v_coloid,v_domiid,v_regiid;
    private EditText v_razon,v_rfc,v_correo,v_calle,v_ext,v_int,v_refe,v_cp;
    private TextView v_estado,v_Muni;
    private String v_alias,v_tel;
    private ArrayList<Colonia> v_listacolonia;
    private ArrayList<Generica> v_listaregimen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiscal);
        inicializarActividad2("Folio  Tipo","Cliente");
        Bundle extras = getIntent().getExtras();
        v_clteid = extras.getInt("clteid",-1);
        String cliente = extras.getString("cliente");

        v_regimen = findViewById(R.id.clteRegimen);
        v_colonia = findViewById(R.id.clteColonia);
        v_razon  = findViewById(R.id.clteRazon);
        v_rfc  = findViewById(R.id.clteRfc);
        v_correo  = findViewById(R.id.clteCorreo);
        v_calle  = findViewById(R.id.clteCalle);
        v_ext  = findViewById(R.id.clteExt);
        v_int  = findViewById(R.id.clteint);
        v_refe  = findViewById(R.id.clteRef);
        v_cp  = findViewById(R.id.clteCp);
        v_estado = findViewById(R.id.clteEstado);
        v_Muni = findViewById(R.id.clteMunicipio);
        v_uso = findViewById(R.id.clteUso);
        v_fopa = findViewById(R.id.clteFopa);
        Button boton = findViewById(R.id.clteGuarda);
        v_listaregimen = servicio.traeDcatGenerica(82);
        ArrayList<String> textos=servicio.traeDcatalogo(82);
        if(textos.size()==0){
            textos.add("Sin regimen");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, textos);
        v_regimen.setAdapter(spinnerAdapter);
        v_regimen.setSelection(0);
        v_emprid = 0;
        v_domiid = 0;
        v_coloid = 0;
        v_regiid = 0;
        v_alias = v_tel = "";
        actualizaToolbar2("Datos Fiscales",cliente);
        traeCliente(v_clteid+"");
        boton.setOnClickListener(view -> wsGuarda());


        View.OnKeyListener listener = (view, i, keyEvent) -> {
            if(view.getId()==v_cp.getId()){
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(!Libreria.tieneInformacion(v_cp.getText().toString())){
                        dlgMensajeError("Debe de capturar C.P.",R.drawable.mensaje_error);
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
        v_regimen.setOnKeyListener(listener);
        v_razon.setOnKeyListener(listener);
        v_rfc.setOnKeyListener(listener);
        v_correo.setOnKeyListener(listener);
        v_calle.setOnKeyListener(listener);
        v_ext.setOnKeyListener(listener);
        v_int.setOnKeyListener(listener);
        v_refe.setOnKeyListener(listener);

        ArrayList<String> uso = servicio.traeDcatalogo(83);
        ArrayList<String> forma =servicio.traeDcatalogo(74);
        ArrayAdapter<String> usoAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, uso);
        ArrayAdapter<String> fopaAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, forma);
        v_uso.setAdapter(usoAdapter);
        v_fopa.setAdapter(fopaAdapter);
    }

    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();
        if(obj != null){
            switch (output.getTarea()){
                case TAREA_COLONIAS_CP:
                    actualizaColonias();
                    break;
                case TAREA_GUARDA_CLIENTE:
                    dlgMensajeError(output.getMensaje(),output.getExito() ? R.drawable.mensaje_exito :R.drawable.mensaje_error);
                    break;
                case TAREA_TRAECLTE:
                    String alias=obj.getAsString("alias");
                    String colonia=obj.getAsString("colonia");
                    v_alias = alias;
                    v_tel = Libreria.traeInfo(obj.getAsString("tel"));
                    v_rfc.setText(obj.getAsString("rfc"));
                    v_razon.setText(obj.getAsString("razon"));
                    v_correo.setText(obj.getAsString("correo"));
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
                    Integer cfdi  = obj.getAsInteger("cfdi");
                    Integer fopa  = obj.getAsInteger("fopa");
                    String dato= servicio.traeAbreviPorCata(82,v_regiid);
                    ArrayList<String> textos=servicio.traeDcatalogo(82);
                    if(textos.size()==0){
                        textos.add("Sin regimen");
                    }
                    ArrayAdapter<String> ref = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, textos);
                    v_regimen.setAdapter(ref);
                    for(int i=0;i<textos.size();i++){
                        if(textos.get(i).equalsIgnoreCase(dato) ){
                            v_regimen.setSelection(i);
                            break;
                        }
                    }

                    ArrayList<String> coloniasNombres = new ArrayList();
                    coloniasNombres.add(colonia);
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this,  R.layout.item_spinner, R.id.item_spinner, coloniasNombres);
                    v_colonia.setAdapter(spinnerAdapter);
                    String formapago = servicio.traeAbreviPorCata(74,fopa);
                    String usocfdi = servicio.traeAbreviPorCata(83,cfdi);
                    ArrayList<String> uso = servicio.traeDcatalogo(83);
                    ArrayList<String> forma =servicio.traeDcatalogo(74);
                    ArrayAdapter<String> usoAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, uso);
                    ArrayAdapter<String> fopaAdapter = new ArrayAdapter(this, R.layout.item_spinner, R.id.item_spinner, forma);
                    v_uso.setAdapter(usoAdapter);
                    v_fopa.setAdapter(fopaAdapter);
                    for(int i=0;i<usoAdapter.getCount();i++){
                        if(usocfdi.equalsIgnoreCase(usoAdapter.getItem(i))){
                            v_uso.setSelection(i);
                            break;
                        }
                    }
                    for(int i=0;i<fopaAdapter.getCount();i++){
                        if(formapago.equalsIgnoreCase(fopaAdapter.getItem(i))){
                            v_fopa.setSelection(i);
                            break;
                        }
                    }
                    v_razon.requestFocus();
                    break;
            }
        } else {
            cierraDialogo();
            dlgMensajeError("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }

    private void traeCliente(String pid){
        peticionWS(Enumeradores.Valores.TAREA_TRAECLTE, "SQL", "SQL", pid, "", "");
    }

    public void wsGuarda(){
        ArrayList<String> listauso = servicio.traeDcatalogo(83);
        ArrayList<String> listaforma =servicio.traeDcatalogo(74);
        ArrayList<String> regimens =servicio.traeDcatalogo(82);
        String razon = v_razon.getText().toString();
        Integer colo = v_colonia.getSelectedItemPosition();
        Integer regi = v_regimen.getSelectedItemPosition();
        Integer uso = v_uso.getSelectedItemPosition();
        Integer fopa = v_fopa.getSelectedItemPosition();
        String rfc = v_rfc.getText().toString();
        String correo = v_correo.getText().toString();
        String calle = v_calle.getText().toString();
        String ext = v_ext.getText().toString();
        String inte = v_int.getText().toString();
        String cp = v_cp.getText().toString();
        String referencia = v_refe.getText().toString();
        String colonia = colo >=0 && v_listacolonia!=null && !v_listacolonia.isEmpty() ? (v_listacolonia.get(colo).getId()+"") : (v_coloid+"");
        Integer regimen = servicio.traeDcatIdporAbrevi(82,regimens.get(regi));
        Integer usoid = servicio.traeDcatIdporAbrevi(83,listauso.get(uso));
        Integer fopaid = servicio.traeDcatIdporAbrevi(74,listaforma.get(fopa));

        ContentValues mapa = new ContentValues();
        mapa.put("rfc", rfc);
        mapa.put("razon", razon);
        mapa.put("alias", v_alias);
        mapa.put("regiid", regimen);
        mapa.put("correo", correo);
        mapa.put("nota", "");
        mapa.put("telefono", v_tel);
        mapa.put("calle", calle);
        mapa.put("exterior", ext);
        mapa.put("interior", inte);
        mapa.put("coloid", colonia);
        mapa.put("refe", referencia);
        mapa.put("usuaid", usuarioID);
        mapa.put("cp", cp);
        mapa.put("nuevo", false);
        mapa.put("empr", v_emprid);
        mapa.put("domi", v_domiid);
        mapa.put("id", v_clteid);
        mapa.put("fopaid", fopaid);
        mapa.put("cfdiid", usoid);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_GUARDA_CLIENTE, "", "",xml,"","");
    }

    private void actualizaColonias(){
        v_listacolonia = servicio.traeColonias();
        ArrayList<String> coloniasNombres = new ArrayList<>();

        for(Colonia c : v_listacolonia)
            coloniasNombres.add(c.getColonia());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,  R.layout.item_spinner, R.id.item_spinner, coloniasNombres);
        v_colonia.setAdapter(spinnerAdapter);
        if(v_listacolonia!=null && !v_listacolonia.isEmpty()){
            v_estado.setText(v_listacolonia.get(0).getEstado());
            v_Muni.setText(v_listacolonia.get(0).getMuni());
        }else{
            v_estado.setText("");
            v_Muni.setText("");
        }
    }

    private void traeColonias(String cp){
        peticionWS(Enumeradores.Valores.TAREA_COLONIAS_CP, "SQL", "SQL", cp, "", "");
    }
}