package com.example.aristomovil2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.BuscaProd;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;

import java.util.List;

public class ListaImpuestosAdapter extends ArrayAdapter<Generica> {
    private final ActividadBase context;
    private final List<Generica> items;

    private ActividadBase activity ;

    public ListaImpuestosAdapter(List<Generica> items, ActividadBase activity) {
        super(activity, R.layout.item_impuesto, items);
        this.context = activity;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_impuesto, parent, false);
        }

        CheckBox itemButton = convertView.findViewById(R.id.item_impuestos);
        Generica gen = items.get(position);

        itemButton.setText(gen.getTex1());
        itemButton.setChecked(gen.getLog1()!=null ? gen.getLog1() : false);
        itemButton.setOnCheckedChangeListener((compoundButton, b) -> {
            boolean seleccionado =compoundButton.isChecked();
            System.out.println(seleccionado + " a "+gen.getTex1()+" b "+b);
            gen.setLog1(b);
        });


        return convertView;
    }

    public List<Generica> getItems() {
        return items;
    }
}
