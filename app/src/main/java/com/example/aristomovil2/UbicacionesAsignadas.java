package com.example.aristomovil2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.aristomovil2.adapters.AsignadasAdapter;
import com.example.aristomovil2.modelos.Asignacion;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.EnviaPeticion;

import java.util.ArrayList;

public class UbicacionesAsignadas extends ActividadBase {
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones_asignadas);

        inicializarActividad("Ubicaciones Asignadas");

        list = findViewById(R.id.listUbicacionesAsignadas);

        peticionWS(Enumeradores.Valores.TAREA_UBICACIONES_ASIGNADAS, "SQL", "SQL", usuarioID, "", "");
    }

    /**
     * Establece el contenido de la lista de ventas
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.emptyAsignadas);
        list = findViewById(R.id.listUbicacionesAsignadas);
        list.setEmptyView(empty);
    }

    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();

        if(obj != null){
            if(output.getTarea() == Enumeradores.Valores.TAREA_UBICACIONES_ASIGNADAS){
                if(obj.getAsBoolean("exito")){
                    ArrayList<Asignacion> asignaciones = servicio.traeAsignaciones();
                    AsignadasAdapter adapter = new AsignadasAdapter(asignaciones);
                    list.setAdapter(adapter);
                }
                cierraDialogo();
            }
        }
        else {
            cierraDialogo();
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }
    }
}