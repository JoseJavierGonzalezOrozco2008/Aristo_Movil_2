package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.ListaViaje;
import com.example.aristomovil2.Acrividades.Listado;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Viaje;

import java.util.List;

public class ViajeAdapter extends BaseAdapter {
    private List<Viaje> listado;
    private ActividadBase base;
    private int opcion;

    public ViajeAdapter(List<Viaje> pListado, ActividadBase pBase) {
        listado=pListado;
        base=pBase;
    }

    @Override
    public int getCount() {
        return listado.size();
    }

    @Override
    public Object getItem(int i) {
        return listado.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listado.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_viajes, viewGroup, false);
        }
        Viaje miViaje = listado.get(i);
        TextView info =view.findViewById(R.id.txtViajFolio);
        info.setText(miViaje.getTexto());
        Button accion=view.findViewById(R.id.btnBultoVer);
        accion.setOnClickListener(view1 -> {
            ((ListaViaje)base).wsLlamaCarga(miViaje);
        });
        accion.setVisibility(View.GONE);
        view.setOnClickListener(view1 -> ((ListaViaje)base).wsLlamaCarga(miViaje));
        info.setTag(miViaje);
        view.setTag(miViaje);

        return view;
    }
}
