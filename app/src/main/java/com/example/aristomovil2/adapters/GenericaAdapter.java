package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;

import java.util.List;

public class GenericaAdapter extends BaseAdapter {
    private List<Generica> listado;
    private ActividadBase base;
    private int opcion;

    public GenericaAdapter(List<Generica> pListado, ActividadBase pBase, Integer pOpcion) {
        listado=pListado;
        base=pBase;
        opcion = pOpcion;
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_acomodo, viewGroup, false);
        }
        Generica gen=listado.get(i);
        switch(opcion){
            case 0://default
                ((TextView)view.findViewById(R.id.acomid)).setText(gen.getTex1());
                ((TextView)view.findViewById(R.id.acomfe)).setText(gen.getTex2());
                ((TextView)view.findViewById(R.id.acomDIfolio)).setVisibility(View.GONE);
                ((TextView)view.findViewById(R.id.acomubica)).setVisibility(View.GONE);
                Button accion=view.findViewById(R.id.acoAction1);
                accion.setVisibility(View.GONE);
                view.setTag(gen);
                break;
        }
        return view;
    }
}
