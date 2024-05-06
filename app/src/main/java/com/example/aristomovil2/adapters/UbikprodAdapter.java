package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.ConteoFisico;
import com.example.aristomovil2.R;
import com.example.aristomovil2.Ubicaciones;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.modelos.Ubikprod;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UbikprodAdapter extends BaseAdapter {
    private final List<Ubikprod> ubicaciones;
    private final Ubicaciones activity;

    /**
     * Constructor del adapter
     * @param ubicaciones Lista de ubicaciones
     * @param activity Referencia del activity
     */
    public UbikprodAdapter(List<Ubikprod> ubicaciones, Ubicaciones activity){
        this.ubicaciones = ubicaciones;
        this.activity = activity;
    }

    /**
     * Retorna el tamaño de la lista de ubicaciones
     * @return El tamaño de la lista de ubicaciones
     */
    @Override
    public int getCount() { return ubicaciones.size(); }

    /**
     * Retorna un elemento de la lista
     * @param i La posicion del elemento
     * @return El elemento en la posicion i
     */
    @Override
    public Object getItem(int i) { return ubicaciones.get(i); }

    /**
     * Retornoa el id de ina ubicacion
     * @param i La posicion de la ubicacion
     * @return El id de lau ubicacion en la posicion i
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista de una ubicacion
     * @param i La posicion de la ubicacion
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista de la ubicacion
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_orden_reposicion, viewGroup, false);
        TextView men1=view.findViewById(R.id.repMen1);
        TextView men2=view.findViewById(R.id.repMen2);
        TextView men3=view.findViewById(R.id.repMen3);
        TextView men4=view.findViewById(R.id.repMen4);
        Ubikprod ubikprod=ubicaciones.get(i);
        men3.setText(ubikprod.getCodigo());
        men2.setText(ubikprod.getUbicacion());
        men1.setText("Cantidad:"+ubikprod.getCant());
        men4.setText(ubikprod.getTipo());

        return view;
    }

    /*public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }*/
}
