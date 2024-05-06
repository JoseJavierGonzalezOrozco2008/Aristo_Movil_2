package com.example.aristomovil2.Acrividades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;

public class Cliente extends ActividadBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        inicializarActividad2("Folio  Tipo","Cliente");
    }
}