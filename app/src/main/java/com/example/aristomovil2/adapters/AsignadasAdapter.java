package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Asignacion;

import java.util.ArrayList;

public class AsignadasAdapter extends BaseAdapter {
    private final ArrayList<Asignacion> asignadas;

    public AsignadasAdapter(ArrayList<Asignacion> asignadas) {
        this.asignadas = asignadas;
    }

    @Override
    public int getCount() { return asignadas.size(); }

    @Override
    public Object getItem(int i) { return asignadas.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_asignada, viewGroup, false);

        if(asignadas.get(i).getEstatus().equals("En conteo"))
            view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.item_verde);
        else if(asignadas.get(i).getEstatus().equals("Cerrado"))
            view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.item_amarillo);

        ((TextView)view.findViewById(R.id.txtAsignacionCodigo)).setText(asignadas.get(i).getUbicacion() + " (" + asignadas.get(i).getEstatus() + ")");

        return view;
    }
}
