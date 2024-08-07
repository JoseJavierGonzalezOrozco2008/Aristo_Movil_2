package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.Lista_Acomodo;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.RenglonRepo;
import com.example.aristomovil2.modelos.Reposicion;

import java.util.ArrayList;

public class RenRepoAdapter extends BaseAdapter {
    private final ArrayList<RenglonRepo> reposicion;
    private final Acomodo activity;
    private boolean inSelection;

    /**
     * Constructor del adapter
     * @param reposicion Lista de reposicion
     * @param activity Referencia al activity Inventario
     */
    public RenRepoAdapter(ArrayList<RenglonRepo> reposicion, Acomodo activity){
        this.reposicion = reposicion;
        this.activity = activity;
        this.inSelection = false;
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_repo, viewGroup, false);



        ((TextView)view.findViewById(R.id.rere_prod)).setText(reposicion.get(i).getProducto());
        ((TextView)view.findViewById(R.id.rere_codigo)).setText(reposicion.get(i).getCodigo());
        ((TextView)view.findViewById(R.id.rere_codigo)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.rere_can)).setText("Cant:"+reposicion.get(i).getFaltan()+"");
        ((TextView)view.findViewById(R.id.rere_can)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.rere_destino)).setText("De:"+reposicion.get(i).getDestino());
        ((TextView)view.findViewById(R.id.rere_destino)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.rere_origen)).setText("Or:"+reposicion.get(i).getOrigen());
        ((TextView)view.findViewById(R.id.rere_origen)).setVisibility(View.GONE);
        //view.setOnClickListener(v->{activity.repoRenglon(reposicion.get(i).getId());});
        view.setTag(reposicion.get(i).getId());
        return view;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
