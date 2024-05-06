package com.example.aristomovil2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.aristomovil2.adapters.AcomodoAdapter;
import com.example.aristomovil2.adapters.RenCalcAdapter;
import com.example.aristomovil2.adapters.RenRepoAdapter;
import com.example.aristomovil2.adapters.SurtidosAdapter;
import com.example.aristomovil2.modelos.RenglonCalcula;
import com.example.aristomovil2.modelos.RenglonRepo;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class Lista_Acomodo extends ActividadBase {


    private ListView listOrdenes;
    private Button bntAcomoda;
    private RadioButton radioNuevo, radioCaptura;
    private String ubicacion,folioDI;
    private Integer esReposicion;
    private Dialog dlgCreaRepo;
    private boolean repoCalcula,repoAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_acomodo);
        inicializarActividad("Lista de Ordenes");
        Bundle extras = getIntent().getExtras();
        esReposicion=extras.getInt("repone",803);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.TitleToolbar);
        String titulo = (esReposicion() ? "Ordenes por Reponer":"Ordenes por Almacenar") + " ( "+usuario+" )";
        title.setText(titulo);
        listOrdenes=findViewById(R.id.lista_acomodo);
        RadioGroup nuevo=findViewById(R.id.grupoNuevoCaptura);
        radioNuevo=findViewById(R.id.radioNuevo);
        radioCaptura=findViewById(R.id.radioCaptura);
        bntAcomoda=findViewById(R.id.btnAcomoda);
        radioCaptura.setChecked(true);
        nuevo.setOnCheckedChangeListener((RadioGroup radioGroup, int i)-> {
                if(radioNuevo.isChecked()){
                    servicio.borraDatosTabla(Enumeradores.Valores.TAREA_CAPTURA_REPO.getTablaBD());
                    peticionWS(Enumeradores.Valores.TAREA_CAPTURA_REPO, "SQL", "SQL", "120", esReposicion+"", "");
                }else if(radioCaptura.isChecked()){
                    servicio.borraDatosTabla(Enumeradores.Valores.TAREA_ORDENES_REPO.getTablaBD());
                    peticionWS(Enumeradores.Valores.TAREA_ORDENES_REPO, "SQL", "SQL", "120", esReposicion+"", "");
                }
            }
        );
        bntAcomoda.setOnClickListener(view -> dlgNuevaRepo());
        if(esReposicion!=803 ){
            bntAcomoda.setVisibility(View.VISIBLE);
            nuevo.setVisibility(View.GONE);
            if(esReposicion==862){
                bntAcomoda.setVisibility(View.GONE);
            }
        }else{
            bntAcomoda.setVisibility(View.GONE);
            nuevo.setVisibility(View.VISIBLE);
        }
        doLlenaListado();
        servicio.borraDatosTabla(Enumeradores.Valores.TAREA_ORDENES_REPO.getTablaBD());
        peticionWS(Enumeradores.Valores.TAREA_ORDENES_REPO, "SQL", "SQL", "120", esReposicion+"", "");
        repoCalcula=true;
        repoAuto = false;
    }

    @Override
    public void Finish(EnviaPeticion output) {
        cierraDialogo();
        if(output!=null){
            if ((output.getTarea()==Enumeradores.Valores.TAREA_ORDENES_REPO || output.getTarea()==Enumeradores.Valores.TAREA_CAPTURA_REPO)){
                doLlenaListado();
            }else if(output.getTarea()==Enumeradores.Valores.TAREA_TRAE_ANDENES ){
                if(output.getExito()){
                    ContentValues values=(ContentValues)output.getExtra1();
                    String andenes=values.getAsString("anexo");
                    dlgAndenes(andenes);
                }else{
                    muestraMensaje("No hay andenes disponibles",R.drawable.mensaje_error);
                }
            }else if (output.getTarea()==Enumeradores.Valores.TAREA_REPO_CREA || output.getTarea()==Enumeradores.Valores.TAREA_REPO_NUEVO_LIBRE){
                //System.out.println("mensaje "+output.getMensaje());
                if(output.getExito()){
                    ContentValues values=(ContentValues)output.getExtra1();
                    String id=values.getAsString("anexo");
                    if(Libreria.tieneInformacion(id)){
                        Intent intent = new Intent(this, Acomodo.class);
                        intent.putExtra("idacom",Integer.parseInt(id));
                        intent.putExtra("folioDI",folioDI);
                        intent.putExtra("ubicacion",ubicacion);
                        intent.putExtra("proceso",2);
                        intent.putExtra("repone",esReposicion);
                        //Intent intent = new Intent(this, Reposicion.class);
                        this.startActivity(intent);
                        this.finish();
                    }
                }else{
                    muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                }
            }else if (output.getTarea()==Enumeradores.Valores.TAREA_REPO_CALCULA){
                System.out.println("mensaje "+output.getMensaje());
                if(output.getExito()){
                    if(repoCalcula){
                        doDlgLlenaListado();
                    }else{
                        peticionWS(Enumeradores.Valores.TAREA_ORDENES_REPO, "SQL", "SQL", "120", esReposicion+"", "");
                    }
                }else if(repoCalcula){
                    doDlgLlenaListado();
                }else{
                    muestraMensaje(output.getMensaje(),R.drawable.mensaje_error);
                }
            }
        }
    }

    public void dlgAndenes(String xmlAndenes){
        if(Libreria.tieneInformacion(xmlAndenes)){
            Dialog dialogo = new Dialog(this, R.style.Dialog);
            //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
            dialogo.setContentView(R.layout.dialogo_andenes);
            dialogo.setTitle("Andenes");
            String[] andenes=xmlAndenes.replace("|",";").split(";");
            String[] datos;
            RadioGroup nuevo=dialogo.findViewById(R.id.radioAndenes);
            Button guarda=dialogo.findViewById(R.id.btnGuardaAnden);
            CheckBox auto=dialogo.findViewById(R.id.swAuto);
            TextView titulo=dialogo.findViewById(R.id.tv_anden_titulo);
            auto.setOnCheckedChangeListener((view,b)->onSeleccinaSwith(view,b));
            ubicacion="";
            titulo.setText("Selecciona un Andén de Entrada");
            boolean primer=true;
            for (String anden:andenes){
                datos=anden.split(",");
                RadioButton nuevoRadio = new RadioButton(this);
                LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                nuevoRadio.setLayoutParams(params);
                nuevoRadio.setText(datos[2]);
                nuevoRadio.setTag(anden);
                nuevoRadio.setOnClickListener(view -> onSeleccinaAnden(view));
                /*if(primer){
                    primer=false;
                    nuevoRadio.setChecked(true);
                    ubicacion =datos[0];
                }*/
                nuevo.addView(nuevoRadio);
            }
            ((RadioButton)nuevo.getChildAt(0)).setChecked(true);
            ((RadioButton)nuevo.getChildAt(0)).callOnClick();

            guarda.setOnClickListener(view -> {wsCreOrden();dialogo.dismiss();});
            dialogo.show();
        }
    }

    public void dlgNuevaRepo(){
        if(esReposicion == 802) {
            dlgCreaRepo = new Dialog(this, R.style.Dialog);
            //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
            dlgCreaRepo.setContentView(R.layout.dialogo_orden_reposicion);
            dlgCreaRepo.setTitle("Nueva Orden de Reposición");

            TextView titulo = dlgCreaRepo.findViewById(R.id.nrepoTitulo);
            titulo.setText("Ubicacion(U1, U2, U3, U4 y U5)");
            RadioGroup nuevo = dlgCreaRepo.findViewById(R.id.radioAndenes);
            Button calcula = dlgCreaRepo.findViewById(R.id.btnOrdenDialogoOrden);
            Button crea = dlgCreaRepo.findViewById(R.id.btnCerrarDialogoOrden);
            calcula.setOnClickListener(view -> {
                doRepoCalcula(false);
            });
            crea.setOnClickListener(view -> {
                new AlertDialog.Builder(this)
                        .setTitle("Nueva orden de reposición")
                        .setMessage("¿Está seguro de crear una orden de reposición?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Si", (dialog, whichButton) -> {
                            doRepoCalcula(true);
                            dlgCreaRepo.dismiss();
                        })
                        .setNegativeButton("No", null).show();
            });
            Window window = dlgCreaRepo.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            servicio.borraDatosTabla(Enumeradores.Valores.TAREA_REPO_CALCULA.getTablaBD());
            doDlgLlenaListado();
            dlgCreaRepo.show();
        }else if(esReposicion == 476){
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
            confirmacion.setTitle("Acomodo libre");
            confirmacion.setMessage("¿Estás seguro de crear una orden de acomodo libre?");
            confirmacion.setCancelable(false);
            confirmacion.setPositiveButton("SI", (dialog, which) -> wsCreOrden());
            confirmacion.setNegativeButton("NO", null);
            confirmacion.show();
        }
    }

    public void doRepoCalcula(Boolean pCrea){
        EditText u1=dlgCreaRepo.findViewById(R.id.nrepoUbi1);
        EditText u2=dlgCreaRepo.findViewById(R.id.nrepoUbi2);
        EditText u3=dlgCreaRepo.findViewById(R.id.nrepoUbi3);
        EditText u4=dlgCreaRepo.findViewById(R.id.nrepoUbi4);
        EditText u5=dlgCreaRepo.findViewById(R.id.nrepoUbi5);
        String ubi1=u1.getText().toString();
        String ubi2=u2.getText().toString();
        String ubi3=u3.getText().toString();
        String ubi4=u4.getText().toString();
        String ubi5=u5.getText().toString();
        String ordena=u5.getText().toString();
        wsCreaReposicion(pCrea,ubi1,ubi2,ubi3,ubi4,ubi5,ordena);
    }

    public void wsPideAndenes(){
        //folioDI="I0100000003";
        peticionWS(Enumeradores.Valores.TAREA_TRAE_ANDENES, "SQL", "SQL", "", "", "");
    }

    public void wsCreaReposicion(boolean pCrea,String pU1,String pU2,String pU3,String pU4,String pU5,String pOrden){
        repoCalcula=!pCrea;
        servicio.borraDatosTabla(Enumeradores.Valores.TAREA_REPO_CALCULA.getTablaBD());
        ContentValues mapa=new ContentValues();
        mapa.put("usua", usuarioID);
        mapa.put("crear",pCrea);
        mapa.put("u1",pU1);
        mapa.put("u2",pU2);
        mapa.put("u3",pU3);
        mapa.put("u4",pU4);
        mapa.put("u5",pU5);
        mapa.put("ordenorigen",pOrden);
        String xml=Libreria.xmlLineaCapturaSV(mapa,"linea");
        peticionWS(Enumeradores.Valores.TAREA_REPO_CALCULA, "SQL", "SQL", xml, "", "");
    }

    public void wsCreOrden(){
        ContentValues mapa=new ContentValues();
        Enumeradores.Valores tarea;
        String dato1;
        if(esReposicion == 476){
            tarea=Enumeradores.Valores.TAREA_REPO_NUEVO_LIBRE;
            dato1=usuarioID;
        }else{
            mapa.put("usua", usuarioID);
            tarea = Enumeradores.Valores.TAREA_REPO_CREA;
            mapa.put("dcin", folioDI);
            mapa.put("ubik", ubicacion);
            mapa.put("prods", "");
            mapa.put("notas", "");
            mapa.put("auto", repoAuto);
            dato1=Libreria.xmlLineaCapturaSV(mapa,"linea");
        }
        peticionWS(tarea, "SQL", "SQL", dato1, "", "");
    }

    public void onSeleccinaAnden(View v){
        boolean marcado = ((RadioButton) v).isChecked();
        if(marcado){
            String datos=v.getTag()+"";
            if(Libreria.tieneInformacion(datos)){
                String[] vector=datos.split(",");
                ubicacion=vector[0];
            }else{
                muestraMensaje(datos,R.color.colorExitoInsertado);
            }
            muestraMensaje(datos,R.color.colorExitoInsertado);
        }
    }

    public void onSeleccinaSwith(View v,Boolean pCambio){
        repoAuto = pCambio;
    }

    @Override
    public boolean onSupportNavigateUp() {
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_lista_acomodo);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();*/
        return false;
    }

    public void doLlenaListado(){
        ArrayList<com.example.aristomovil2.modelos.Reposicion> reposiciones=servicio.traeOrdenesAcomodo();
        AcomodoAdapter adapter = new AcomodoAdapter(reposiciones,esReposicion, this);
        listOrdenes.setAdapter(adapter);
        if(reposiciones.isEmpty()){
            View vacio=findViewById(R.id.emptyReposicion);
            listOrdenes.setEmptyView(vacio);
        }
        //RecyclerView spinnerSubalmacenes = findViewById(R.id.ac);
    }

    public void doDlgLlenaListado(){
        List<RenglonCalcula> reposiciones=servicio.traeRepoCalcula();
        RenCalcAdapter adapter = new RenCalcAdapter(reposiciones, this);
        ListView listado=dlgCreaRepo.findViewById(R.id.lista_acomodo);
        listado.setAdapter(adapter);
        if(reposiciones.isEmpty()){
            View vacio=dlgCreaRepo.findViewById(R.id.vacioRepo);
            listado.setEmptyView(vacio);
        }
        //RecyclerView spinnerSubalmacenes = findViewById(R.id.ac);
    }

    public void setFolioDI(String folioDI) {
        this.folioDI = folioDI;
    }

    private boolean esReposicion(){ return esReposicion==802;}
}
