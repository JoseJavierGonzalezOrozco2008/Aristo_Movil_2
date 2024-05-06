package com.example.aristomovil2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.example.aristomovil2.adapters.OrdenesReposicionAdapter;
import com.example.aristomovil2.componentes.MultiSpinner;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import java.util.ArrayList;
import java.util.List;

public class Reposicion extends ActividadBase {
    public Dialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reposicion);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Reposicion"));

        List<String> items = new ArrayList<>();
        items.add("1");
        items.add("2");
        items.add("3");
        items.add("4");

        Button btnLimpiar = findViewById(R.id.btnAcomodoLimpiar);
        Button btnCalcular = findViewById(R.id.btnAcomodoCalcular);
        Button btnOrdenes = findViewById(R.id.btnAcomodoOrdenes);
        MultiSpinner spinnerE1 = findViewById(R.id.SpinnerReposicionE1);
        MultiSpinner spinnerE2 = findViewById(R.id.SpinnerReposicionE2);
        MultiSpinner spinnerE3 = findViewById(R.id.SpinnerReposicionE3);
        MultiSpinner spinnerE4 = findViewById(R.id.SpinnerReposicionE4);
        MultiSpinner spinnerE5 = findViewById(R.id.SpinnerReposicionE5);

        //Listener para el Spinner de nave

        spinnerE1.setItems(items, "Sin seleccion", selected -> {
            List<String> item2 = new ArrayList<>();
            for(int i = 0; i< 1 + Math.random()*(10); i++)
                item2.add(String.valueOf(i+1));

            spinnerE2.setItems(item2,"Sin seleccion", selected2 -> {
                List<String> items3 = new ArrayList<>();
                for(int i = 0; i< 1 + Math.random()*(10); i++)
                    items3.add(String.valueOf(i+1));

                spinnerE3.setItems(items3, "Sin seleccion", selected3 -> {
                    List<String> items4 = new ArrayList<>();
                    for(int i = 0; i< 1 + Math.random()*(10); i++)
                        items4.add(String.valueOf(i+1));

                    spinnerE4.setItems(items4, "Sin seleccion", selected4 -> {
                        List<String> items5 = new ArrayList<>();
                        for(int i = 0; i< 1 + Math.random()*(10); i++)
                            items5.add(String.valueOf(i+1));

                        spinnerE5.setItems(items5, "Sin seleccion", selected5 -> {

                        });
                        spinnerE5.setEnabled(true);
                    });
                    spinnerE4.setEnabled(true);
                    limpiarSpinner(spinnerE5);
                });
                spinnerE3.setEnabled(true);
                limpiarSpinner(spinnerE4);
                limpiarSpinner(spinnerE5);
            });
            spinnerE2.setEnabled(true);
            limpiarSpinner(spinnerE3);
            limpiarSpinner(spinnerE4);
            limpiarSpinner(spinnerE5);
        });

        limpiarSpinner(spinnerE2);
        limpiarSpinner(spinnerE3);
        limpiarSpinner(spinnerE4);
        limpiarSpinner(spinnerE5);

        //Listener para limpiar los Spinners
        btnLimpiar.setOnClickListener(view -> {
            spinnerE1.limpiar();
            spinnerE2.limpiar();
            spinnerE3.limpiar();
            spinnerE4.limpiar();
            spinnerE5.limpiar();
        });

        //Listener para el boton de calcular orden de preosicion
        btnCalcular.setOnClickListener(view -> {
            Dialog dialogo = new Dialog(this);
            dialogo.setContentView(R.layout.dialogo_orden_reposicion);

            dialogo.findViewById(R.id.btnCerrarDialogoOrden).setOnClickListener(v -> dialogo.dismiss());
            dialogo.findViewById(R.id.btnOrdenDialogoOrden).setOnClickListener(v -> muestraMensaje("En construcción", R.drawable.mensaje_warning));
            dialogo.show();
        });

        //Listener para el boton de ordenes, muetra un dialogo con la lista de ordenes
        btnOrdenes.setOnClickListener(view -> {
            dialogo = new Dialog(this);
            dialogo.setContentView(R.layout.dialogo_reposicion_ordenes);
            dialogo.setCancelable(false);

            ImageButton btnCerrar = dialogo.findViewById(R.id.btnDialogoOrdenesReposicionClose);
            ListView listOrdenes = dialogo.findViewById(R.id.listDialogoOrdenesReposicion);

            List<String> ordenes = new ArrayList<>();
            ordenes.add("");ordenes.add("");ordenes.add("");ordenes.add("");ordenes.add("");

            OrdenesReposicionAdapter adapter = new OrdenesReposicionAdapter(ordenes, this);
            listOrdenes.setAdapter(adapter);

            btnCerrar.setOnClickListener(v -> dialogo.dismiss());

            dialogo.show();
            Window window = dialogo.getWindow();
            window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        });

        peticionWS(Enumeradores.Valores.TAREA_ESPACIO_ETIQUETA, "SQL", "SQL", "", "", "");
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    private void limpiarSpinner(MultiSpinner spinner){
        spinner.setEnabled(false);
    }

    private void traeUbicacionXNivel(String nivel){
        peticionWS(Enumeradores.Valores.TAREA_TRAE_UBIC_X_NIVEL, "SQL", "SQL", "0|0|0|0|0", nivel, "");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues)output.getExtra1();

        if(obj != null){
            switch(output.getTarea()){
                case TAREA_ESPACIO_ETIQUETA: {
                    if (output.getExito()) {
                        ((TextView) findViewById(R.id.txtReposicionE1)).setText(obj.getAsString("e1") + ": ");
                        ((TextView) findViewById(R.id.txtReposicionE2)).setText(obj.getAsString("e2") + ": ");
                        ((TextView) findViewById(R.id.txtReposicionE3)).setText(obj.getAsString("e3") + ": ");
                        ((TextView) findViewById(R.id.txtReposicionE4)).setText(obj.getAsString("e4") + ": ");
                        ((TextView) findViewById(R.id.txtReposicionE5)).setText(obj.getAsString("e5") + ": ");
                    }
                    cierraDialogo();
                    traeUbicacionXNivel("1");
                    break;
                }
                case TAREA_TRAE_UBIC_X_NIVEL:{

                    cierraDialogo();
                    break;
                }
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }
}