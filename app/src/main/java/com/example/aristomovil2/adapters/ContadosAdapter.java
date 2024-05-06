package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aristomovil2.modelos.Contados;
import com.example.aristomovil2.R;

import java.util.ArrayList;

public class ContadosAdapter extends BaseAdapter {
    private final ArrayList<Contados> contados;
    private boolean inSelection;
    private final com.example.aristomovil2.Contados activity;

    /**
     * Constructor del adaper
     * @param contados Lista de productos contados
     * @param activity Referencia del activity Contados
     */
    public ContadosAdapter(ArrayList<Contados> contados, com.example.aristomovil2.Contados activity) {
        this.contados = contados;
        this.activity = activity;
        this.inSelection = false;
    }

    /**
     * Retorna el tamaño de la lista de productos contados
     * @return El tamaño de la lista
     */
    @Override
    public int getCount() { return contados.size(); }

    /**
     * Retorna un elemento de la lista
     * @param i Psoicion del elemento
     * @return Elemento en la posicion i
     */
    @Override
    public Object getItem(int i) { return contados.get(i); }

    /**
     * Retorna el id de un item
     * @param i La posicion del item
     * @return El id del item en la posicion i
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista del producto contado
     * @param i Posicion del producto
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contados, viewGroup, false);

        if(contados.get(i).isSelected())
            view.findViewById(R.id.viewContado).setBackgroundResource(R.drawable.venta_selected_shape);
        else
            view.findViewById(R.id.viewContado).setBackgroundResource(R.drawable.venta_shape);

        ((TextView)view.findViewById(R.id.txtContadoCodigo)).setText(contados.get(i).getCodigo());
        ((TextView)view.findViewById(R.id.txtContadoCantidad)).setText(contados.get(i).getCant());
        ((TextView)view.findViewById(R.id.txtContadoProducto)).setText(contados.get(i).getProducto());

        return view;
    }
}
