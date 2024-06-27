package com.example.aristomovil2.adapters;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.Cobropv;
import com.example.aristomovil2.Acrividades.Devolucion;
import com.example.aristomovil2.Acrividades.Recargas;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.BuscaProd;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.Libreria;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

public class DevolucionAdapter extends BaseAdapter {
    private List<Generica> listado;
    private ActividadBase base;
    private int opcion,v_xml;

    public DevolucionAdapter(List<Generica> pListado, ActividadBase pBase, Integer pOpcion) {
        listado=pListado;
        base=pBase;
        opcion = pOpcion;
        v_xml=R.layout.item_acomodo;
    }

    @Override
    public int getCount() {
        return listado.size();
    }

    @Override
    public Object getItem(int i) {
        return listado.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listado.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(v_xml, viewGroup, false);
        }
        Generica gen=listado.get(i);
        TextView info=view.findViewById(R.id.acomid);
        String aMostrar=Libreria.traeInfo(gen.getTex4(),gen.getTex2());
        info.setText(aMostrar.replace("##1##",gen.getDec2().setScale(2, BigDecimal.ROUND_HALF_EVEN)+""));
        info.setTypeface(Typeface.MONOSPACE);
        ViewGroup.LayoutParams layoutParams =info.getLayoutParams();
        layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        info.setLayoutParams(layoutParams);
        (view.findViewById(R.id.acomfe)).setVisibility(View.GONE);
        (view.findViewById(R.id.acomDIfolio)).setVisibility(View.GONE);
        (view.findViewById(R.id.acomubica)).setVisibility(View.GONE);
        view.findViewById(R.id.acoAction1).setVisibility(View.GONE);
        info.setOnClickListener(v->{
            final AlertDialog.Builder dialog = new AlertDialog.Builder(base);
            dialog.setTitle("Producto por devolver");
            dialog.setMessage(MessageFormat.format("¿Que deser hacer con {0} ?",gen.getTex3()));
            dialog.setNegativeButton("Quitar",(dialogInterface, i1) -> {
                ((Devolucion)base).quitarElemento(gen);
            });
            dialog.setPositiveButton("Editar", (dialogInterface, i2) -> {
                ((Devolucion)base).agregaPorDevol(gen);
            });
            dialog.setNeutralButton("Regresar", (dialogInterface, i2) -> {dialogInterface.dismiss();});
            dialog.show();
        });

        /*Generica gen=listado.get(i);
        TextView cant = view.findViewById(R.id.devCantidad);
        TextView Producto = view.findViewById(R.id.devProducto);
        TextView Precio = view.findViewById(R.id.devPrecio);
        Producto.setText(gen.getTex3());
        cant.setText("Cant:"+gen.getDec2());
        Precio.setText("$ "+Libreria.traeInfo(gen.getDec1()+"","0"));*/

        view.setTag(gen);
        return view;
    }

    public BigDecimal traeTotal(){
        BigDecimal total=BigDecimal.ZERO;
        for(Generica gen:listado){
            total=total.add(gen.getDec1().multiply(gen.getDec2()));
        }
        return total;
    }


}
