package com.example.aristomovil2.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.aristomovil2.Acrividades.BuscaProducto;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Renglon;

import java.util.List;

public class BqdaProdAdapter extends BaseAdapter{
    private final List<Generica> renglones;
    private final ActividadBase activity;

    /**
     * Constructor del adapter
     * @param renglones Lista de renglones
     * @param activity Referencia al activity PutnoVenta
     */
    public BqdaProdAdapter(List<Generica> renglones, ActividadBase activity){
        this.renglones = renglones;
        this.activity = activity;
    }

    /**
     * Retorno el tamaño de la lista de renglones
     * @return El tamaño de la lista de renglones
     */
    @Override
    public int getCount() {
        return renglones.size();
    }

    /**
     * Retorno un renglon de la lista
     * @param i Posicion del renglon
     * @return El renglon en la posicion i
     */
    @Override
    public Object getItem(int i){ return renglones.get(i); }

    /**
     *Retorna el id de un renglon
     * @param i La posicion del renglon
     * @return El id del renglon
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista del renglon
     * @param position Posicion del renglon
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_carrito, viewGroup, false);

        LinearLayout linea1 = view.findViewById(R.id.itemlylinea1);
        linea1.setVisibility(View.GONE);

        TextView producto=view.findViewById(R.id.item_producto);
        producto.setText(renglones.get(position).getTex1());
        producto.setTypeface(Typeface.MONOSPACE);
        producto.setTextColor(Color.BLACK);
        view.setOnClickListener(v -> {
            System.out.println("dato aqui "+renglones.get(position).getTex3());
            if(activity instanceof BuscaProducto){
                ((BuscaProducto)activity).setSeleccion(renglones.get(position).getTex3());
                ((BuscaProducto)activity).onBackPressed();
            }
        });

        return view;
    }
}
