package com.example.aristomovil2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Dviaje;
import com.example.aristomovil2.Acrividades.ListaViaje;
import com.example.aristomovil2.Acrividades.Listado;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Detviaje;
import com.example.aristomovil2.modelos.UtilViaje;
import com.example.aristomovil2.modelos.Viaje;
import com.example.aristomovil2.utileria.Libreria;

import java.math.BigDecimal;
import java.util.List;

public class ListadoAdapter extends BaseAdapter {
    private List<Object> listado;
    private ActividadBase base;
    private int opcion,extra1,v_selec;


    public ListadoAdapter(List pListado, ActividadBase pBase,Integer pTipo,Integer pExtra1) {
        listado = pListado;
        base = pBase;
        opcion = pTipo;
        extra1 = pExtra1;
        v_selec = -1;
        if(opcion == 2 && pExtra1!=null){
            UtilViaje util;
            for(int i=0;i<pListado.size();i++){
                util = (UtilViaje)pListado.get(i);
                if(util.getRid() == pExtra1){
                    v_selec = i;
                }
            }
        }
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
        long retorno = -1;
        switch(opcion){
            case 1:retorno =((Detviaje)listado.get(i)).getId();
            break;
        }
        return retorno;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Integer menuLista = 0;
        switch(opcion){
            case 1:
            case 3:menuLista =R.layout.list_listado;
            break;
            case 2:menuLista =R.layout.item_selec;
                break;
        }
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(menuLista, viewGroup, false);
        }
        if(opcion==1){
            Detviaje miViaje =(Detviaje) listado.get(i);
            ((TextView)view.findViewById(R.id.txtListaText)).setText(miViaje.getTexto());
            Button accion=view.findViewById(R.id.btnListado);
            accion.setOnClickListener(view1 -> {
                ((Listado)base).wsDviajAndenes(miViaje);
            });
            if(extra1==41){
                accion.setVisibility(View.GONE);
            }
            view.setTag(miViaje);
        }else if(opcion==2){
            UtilViaje miUtil =(UtilViaje) listado.get(i);
            TextView dato1 = view.findViewById(R.id.txtDato1);
            TextView dato2 = view.findViewById(R.id.txtDato2);
            RadioButton chec =  view.findViewById(R.id.selec);
            String str_dato2=miUtil.getNombre2();
            if(!Libreria.tieneInformacion(miUtil.getNombre2())){
                dato2.setVisibility(View.GONE);
            }
            if(miUtil.getCant()!=null && miUtil.getCant().compareTo(BigDecimal.ZERO)>0){
                str_dato2 += "("+miUtil.getCant()+")";
            }
            chec.setChecked(v_selec == i);
            dato1.setText(miUtil.getNombre());
            dato2.setText(str_dato2);
            view.setTag(miUtil);
            chec.setOnClickListener(view1 -> {
                v_selec= i;
                this.notifyDataSetChanged();
            });
            view.setOnClickListener(view1 -> {
                v_selec= i;
                this.notifyDataSetChanged();
            });
        }else if(opcion == 3){
            UtilViaje miUtil =(UtilViaje) listado.get(i);
            ((TextView)view.findViewById(R.id.txtListaText)).setText(miUtil.getNombre());
            Button accion=view.findViewById(R.id.btnListado);
            accion.setVisibility(View.GONE);
        }


        return view;
    }

    public Object getSelec(){
        if(v_selec<0){
            return null;
        }
        return listado.get(v_selec);
    }
}
