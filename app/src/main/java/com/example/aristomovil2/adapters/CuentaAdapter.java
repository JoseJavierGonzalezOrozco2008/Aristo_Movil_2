package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.Cuentas;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Cuenta;
import com.example.aristomovil2.modelos.Generica;

import java.util.List;

public class CuentaAdapter extends BaseAdapter {
    private final List<Cuenta> reposicion;
    private final ActividadBase activity;


    /**
     * Constructor del adapter
     * @param reposicion Lista de reposicion
     * @param activity Referencia al activity Inventario
     */
    public CuentaAdapter(List<Cuenta> reposicion, ActividadBase activity){
        this.reposicion = reposicion;
        this.activity = activity;
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cliente, viewGroup, false);


        TextView texto = view.findViewById(R.id.item_cliente);

        texto.setText(reposicion.get(i).getInfo());
        ImageButton seleccion = view.findViewById(R.id.item_selecciona);
        ImageButton edita=view.findViewById(R.id.item_edita);
        seleccion.setVisibility(View.GONE);
        edita.setVisibility(View.GONE);
        texto.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, Cuentas.class);
            intent.putExtra("clteid",reposicion.get(i).getClteid());
            intent.putExtra("cuclid",reposicion.get(i).getCuclid());
            intent.putExtra("cliente",reposicion.get(i).getNombre());
            activity.startActivity(intent);
        });

        return view;
    }


}
