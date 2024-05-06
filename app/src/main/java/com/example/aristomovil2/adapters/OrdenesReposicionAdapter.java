package com.example.aristomovil2.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.R;
import com.example.aristomovil2.Reposicion;

import java.util.List;


public class OrdenesReposicionAdapter extends BaseAdapter {
    private final List<String> ordenes;
    private final Reposicion activity;

    /**
     * Constructor del adapter
     * @param ordenes Lista de ordenes
     * @param reposicion Referencia del activity Reposicion
     */
    public OrdenesReposicionAdapter(List<String> ordenes, Reposicion reposicion){
        this.ordenes = ordenes;
        this.activity = reposicion;
    }

    /**
     * Retorna el tamaño de la lista de ordenes
     * @return El tamaño de la lista de ordenes
     */
    @Override
    public int getCount() {
        return ordenes.size();
    }

    /**
     * Retorna un elemento de la lista
     * @param i Posicion del elemento
     * @return Elemento en la posicion i
     */
    @Override
    public Object getItem(int i) {
        return ordenes.get(i);
    }

    /**
     * Retorna el id de una orden
     * @param i La posicion de la orden
     * @return El id de la orden
     */
    @Override
    public long getItemId(int i){
        return 0;
    }

    /**
     * Construlle la vista de la orden
     * @param i Posicion de la orde
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_orden_reposicion, viewGroup, false);

        view.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out));

            v.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    activity.dialogo.dismiss();

                    Intent intent = new Intent(activity, Acomodo.class);
                    activity.startActivity(intent);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        return view;
    }
}
