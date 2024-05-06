package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.MessageFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Lista_Acomodo;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Reposicion;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends BaseAdapter {
    private final List<Generica> reposicion;
    private final ActividadBase activity;
    private boolean inSelection,directo;
    private Integer repone,proceso;

    /**
     * Constructor del adapter
     * @param reposicion Lista de reposicion
     * @param activity Referencia al activity Inventario
     */
    public ClienteAdapter(List<Generica> reposicion, ActividadBase activity){
        this.reposicion = reposicion;
        this.activity = activity;
        this.inSelection = false;
        directo=false;

    }

    /**
     * Retorna el tamaño de la lista de reposicion
     * @return El tamaño de la lista de reposicion
     */
    @Override
    public int getCount() { return reposicion.size(); }

    /**
     * Retorna el id de un documento
     * @param i La posicion del codumento
     * @return El id del documento
     */
    @Override
    public Object getItem(int i) { return reposicion.get(i); }

    /**
     * Retorna el id de un documento
     * @param i La posicion del codumento
     * @return El id del documento
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista del documento
     * @param i Posicion del documento
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_carrito, viewGroup, false);

        LinearLayout linea1 = view.findViewById(R.id.itemlylinea1);
        linea1.setVisibility(View.GONE);
        TextView texto = view.findViewById(R.id.item_producto);
        texto.setText(reposicion.get(i).getTex2());
        texto.setTypeface(Typeface.MONOSPACE);
        texto.setTextColor(Color.BLACK);
        texto.setGravity(Gravity.LEFT);
        view.setOnClickListener(v->{
            if(activity instanceof Carrito){
                ((Carrito)activity).setCliente(reposicion.get(i).getId(),reposicion.get(i).getTex1(),reposicion.get(i).getLog1());
                ((Carrito)activity).dlgClienteCierra();
            }
        });

        return view;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
