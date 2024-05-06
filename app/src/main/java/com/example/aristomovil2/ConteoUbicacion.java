package com.example.aristomovil2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;
import com.example.aristomovil2.utileria.Libreria;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ConteoUbicacion extends ActividadBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_ubicacion);

        inicializarActividad(getSharedPreferences("renglones", MODE_PRIVATE).getString("titulo", "Conteo"));

        findViewById(R.id.editConteoUbicacionUbicacion).setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                String codigo = ((EditText)view).getText().toString();
                if(Libreria.tieneInformacion(codigo)) {
                    hideKeyboard(view);
                    contarUbicacion(codigo);
                }
                else
                    muestraMensaje("Ingresa una ubicación", R.drawable.mensaje_warning);
                return true;
            }else
                return false;
        });

        findViewById(R.id.btnConteoUbicacionBarcode).setOnClickListener(view -> barcodeEscaner());
    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);*/
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult.getContents() != null) {
            ((EditText) findViewById(R.id.editConteoUbicacionUbicacion)).setText(intentResult.getContents());
            hideKeyboard(this);
            contarUbicacion(intentResult.getContents()       );
        }
        else
            muestraMensaje("Error al escanear código", R.drawable.mensaje_error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conteo_ubicacion, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuConteoUbicaciones) {
            Intent intent = new Intent(this, UbicacionesAsignadas.class);
            startActivity(intent);
        }

        return true;
    }

    /**
     * Inicia el conteo de una ubicacion
     * @param ubicacion La ubicacion a contar
     */
    public void contarUbicacion(String ubicacion) {
        peticionWS(Enumeradores.Valores.TAREA_CUENTA_UBICACION, "SQL", "SQL", usuarioID, ubicacion, "");
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        if(obj != null){
            if(output.getTarea() == Enumeradores.Valores.TAREA_CUENTA_UBICACION){
                cierraDialogo();
                if(output.getExito()){
                    AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
                    confirmacion.setTitle("Conteo de piezas");
                    confirmacion.setMessage("¿Desea Iniciar a contar esta ubicación?");
                    confirmacion.setCancelable(false);
                    confirmacion.setPositiveButton("SI", (dialog, which) -> {
                        Intent intent = new Intent(this, Conteo.class);
                        intent.putExtra("asifid", obj.getAsString("asifid"));
                        intent.putExtra("espaid", obj.getAsString("espaid"));
                        intent.putExtra("conteo", obj.getAsString("conteo"));
                        intent.putExtra("ubicacion", obj.getAsString("ubicacion"));
                        intent.putExtra("origen", false);
                        startActivity(intent);
                    });
                    confirmacion.setNegativeButton("NO", null);
                    confirmacion.show();
                }else
                    muestraMensaje(output.getMensaje(), R.drawable.mensaje_warning);
            }
            else {
                cierraDialogo();
                muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }
}