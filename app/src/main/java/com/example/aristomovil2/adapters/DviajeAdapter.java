package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Detviaje;
import com.example.aristomovil2.modelos.Viaje;

import java.util.List;

public class DviajeAdapter extends BaseAdapter {
    private List<Detviaje> listado;
    private ActividadBase base;
    private int opcion;

    public DviajeAdapter(List<Detviaje> pListado, ActividadBase pBase) {
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
        Detviaje miViaje = listado.get(i);
        view.setTag(miViaje);

        return view;
    }
}
