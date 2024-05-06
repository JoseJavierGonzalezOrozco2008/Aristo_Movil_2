package com.example.aristomovil2.adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.aristomovil2.modelos.Ventas;
import com.example.aristomovil2.R;
import com.example.aristomovil2.utileria.Libreria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class VentasAdapter extends BaseAdapter {
    private final List<Ventas> ventas;
    private final com.example.aristomovil2.Ventas activity;

    /**
     * Constructor del adapter
     * @param ventas Lista de ventas
     * @param activity Referencia al activity Ventas
     */
    public VentasAdapter(List<Ventas> ventas, com.example.aristomovil2.Ventas activity){
        this.ventas = ventas;
        this.activity = activity;
    }

    /**
     * Retorno el tamaño de la lista de ventas
     * @return El tamaño de la lista de ventas
     */
    @Override
    public int getCount() { return ventas.size(); }

    /**
     * Retorno una venta de la lista
     * @param i Posicion de la venta
     * @return La venta en la posicion i
     */
    @Override
    public Object getItem(int i) { return ventas.get(i); }

    /**
     *Retorna el id de una venta
     * @param i La posicion de la venta
     * @return El id de la venta
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista de la venta
     * @param i Posicion de la venta
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_venta, viewGroup, false);

        SimpleDateFormat parser, formatter;
        String date;

        parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = formatter.format(Objects.requireNonNull(parser.parse(ventas.get(i).getFecha())));
        } catch (ParseException e) {
            date = "Sin fecha";
            e.printStackTrace();
        }

        ((TextView)view.findViewById(R.id.rere_codigo)).setText(ventas.get(i).getFolio());
        ((TextView)view.findViewById(R.id.txtVentasCliente)).setText(ventas.get(i).getNombrecliente());
        ((TextView)view.findViewById(R.id.rere_can)).setText(date);

        view.setOnClickListener( v -> {
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out));

            v.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    SharedPreferences.Editor editor = activity.sharedPreferences.edit();

                    activity.folio = ventas.get(i).getFolio();
                    activity.clienteId = ventas.get(i).getCliente();
                    activity.cliente = ventas.get(i).getNombrecliente();
                    activity.ventaCredito = Libreria.getBoolean(ventas.get(i).getCredito());
                    editor.putString("tieneCredito", ventas.get(i).getTienecredito());
                    editor.putString("credito", ventas.get(i).getCredito());
                    editor.putString("notas", ventas.get(i).getNotas());
                    editor.putString("titulo", ventas.get(i).getTitulo());
                    editor.apply();

                    activity.puntoVenta();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        return view;
    }
}
