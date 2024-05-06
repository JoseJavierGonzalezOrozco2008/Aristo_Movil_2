package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.DiferenciasCaducidades;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class DiferenciaCaducidadesAdapter extends BaseAdapter {
    private final ArrayList<DiferenciasCaducidades> caducidades;

    public DiferenciaCaducidadesAdapter(ArrayList<DiferenciasCaducidades> caducidades) {
        this.caducidades = caducidades;
    }

    @Override
    public int getCount() { return caducidades.size(); }

    @Override
    public Object getItem(int i) { return caducidades.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_producto_di, viewGroup, false);

        ((TextView)view.findViewById(R.id.txtProductoDIProducto)).setText(caducidades.get(i).getProducto());
        ((TextView)view.findViewById(R.id.txtProductoDICodigo)).setText(caducidades.get(i).getCodigo());
        ((TextView)view.findViewById(R.id.txtProsuctoDIRegistrado)).setText("Cant: " + caducidades.get(i).getCantidad());
        ((TextView)view.findViewById(R.id.txtProductoDIUsuario)).setVisibility(View.GONE);
        view.findViewById(R.id.btnProductoDICaduca).setVisibility(View.GONE);

        return view;
    }
}
