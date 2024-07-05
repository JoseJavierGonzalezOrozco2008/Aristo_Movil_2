package com.example.aristomovil2.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ReporteAdapter extends BaseAdapter {
    private List<Generica> listado;
    private ActividadBase base;
    private Enumeradores.Valores imprime;
    private int opcion,v_xml;
    private String vTitulo,vMensaje,vBoton1,vBoton2;
    private DialogInterface.OnClickListener vOnClick1,vOnClick2;

    public ReporteAdapter(List<Generica> pListado, ActividadBase pBase, Enumeradores.Valores pImprime,Integer pOpcion) {
        listado=pListado;
        base=pBase;
        opcion = pOpcion;
        imprime = pImprime;
        v_xml=R.layout.item_acomodo;
        vTitulo = "Imprime ticket";
        vMensaje = "¿Seguro que volver a imprimir el folio {0}?";
        vBoton1 = "Imprimir";
        vBoton2 = "Pantalla";
    }

    public void cambiaMensaje(String pTitulo,String pMensaje,String pBoton1,String pBoton2){
        vTitulo = Libreria.traeInfo(pTitulo,vTitulo);
        vMensaje = Libreria.traeInfo(pMensaje,vMensaje);
        vBoton1 = Libreria.traeInfo(pBoton1);
        vBoton2 = Libreria.traeInfo(pBoton2);
    }

    public void setvOnClick1(DialogInterface.OnClickListener vOnClick1) {
        this.vOnClick1 = vOnClick1;
    }

    public void setvOnClick2(DialogInterface.OnClickListener vOnClick2) {
        this.vOnClick2 = vOnClick2;
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
        info.setText(gen.getTex2());
        info.setTypeface(Typeface.MONOSPACE);
        ViewGroup.LayoutParams layoutParams =info.getLayoutParams();
        layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        info.setLayoutParams(layoutParams);
        (view.findViewById(R.id.acomfe)).setVisibility(View.GONE);
        (view.findViewById(R.id.acomDIfolio)).setVisibility(View.GONE);
        (view.findViewById(R.id.acomubica)).setVisibility(View.GONE);
        view.findViewById(R.id.acoAction1).setVisibility(View.GONE);
        boolean muestra = gen.getLog1() &&  (Libreria.tieneInformacion(vBoton2) || Libreria.tieneInformacion(vBoton1));
        if(muestra){
            info.setOnClickListener(v->{
                final AlertDialog.Builder dialog = new AlertDialog.Builder(base);
                dialog.setTitle(vTitulo);
                dialog.setMessage(MessageFormat.format(vMensaje,gen.getTex1(),gen.getTex3()));
                if(Libreria.tieneInformacion(vBoton2)){
                    dialog.setNegativeButton(vBoton2,(dialogInterface, i1) -> {
                        base.aPantalla(true);
                        accion(gen);
                    });
                }

                if(Libreria.tieneInformacion(vBoton1)){
                    dialog.setPositiveButton(vBoton1, (dialogInterface, i1) -> {
                        base.aPantalla(false);
                        accion(gen);
                    });
                }

                dialog.setNeutralButton("Regresar", (dialogInterface, i2) -> {dialogInterface.dismiss();});
                dialog.show();
            });
        }
        view.setTag(gen);
        return view;
    }

    public void accion(Generica pGen){
        switch (opcion){
            case 5:
                if (base instanceof Carrito) {
                    ((Carrito)base).vntaLimpia();
                    ((Carrito)base).traeVnta(pGen.getTex1());
                }else if (base instanceof Cobropv) {
                    ((Cobropv)base).asignaVntafolio(pGen.getTex1());
                }
                break;
            case 7:
                ((BuscaProd)base).wsBuscaProd(pGen.getEnt1());
                break;
            case 10:
                if(base instanceof Devolucion){
                    if(pGen.getDec3().compareTo(BigDecimal.ZERO)>0){
                        ((Devolucion)base).agregaPorDevol(pGen);
                    }
                }
                break;
            default:
                base.setMensajeExtra(pGen.getTex4());
                base.wsImprime(imprime,pGen.getTex1());
                break;
        }
    }
}
