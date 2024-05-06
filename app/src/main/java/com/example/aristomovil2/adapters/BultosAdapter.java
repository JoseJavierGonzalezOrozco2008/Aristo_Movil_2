package com.example.aristomovil2.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.Bultos;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class BultosAdapter extends BaseExpandableListAdapter {
    final ArrayList<ArrayList<Bulto>> listas;
    final ArrayList<String> grupos;
    final Bultos activity;

    public BultosAdapter(ArrayList<ArrayList<Bulto>> listas, ArrayList<String> grupos, Bultos activity) {
        this.listas = listas;
        this.grupos = grupos;
        this.activity = activity;
    }

    @Override
    public int getGroupCount() {
        return grupos.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listas.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return grupos.get(i);
    }

    @Override
    public Object getChild(int i, int i1) { return listas.get(i).get(i1); }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        String vector[]=headerTitle.split(";");
        vector[1]=vector[1].substring(0,1)+"*"+vector[1].substring(vector[1].length()-4,vector[1].length());
        headerTitle="";
        for(String cad:vector){
            headerTitle+=cad+";";
        }
        int largo=headerTitle.length();
        headerTitle=headerTitle.substring(0,largo-1);
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.replace("null;","").replace(";","-"));

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bulto2, viewGroup, false);

        ((TextView)view.findViewById(R.id.txtBultoFolio)).setText(listas.get(i).get(i1).getContenedor());
        ((TextView)view.findViewById(R.id.txtBultoEstatus)).setText(listas.get(i).get(i1).getEstatus());  //CASE
        ((TextView)view.findViewById(R.id.txtBultoUsuario)).setText(listas.get(i).get(i1).getUsuario());
        (view.findViewById(R.id.txtBultoUsuario)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.txtBultoRenglones)).setText("Rengs: " + listas.get(i).get(i1).getRengs());
        ((TextView)view.findViewById(R.id.txtBultoPiezas)).setText("Piezas: " + listas.get(i).get(i1).getPiezas());
        Button ver= view.findViewById(R.id.btnBultoVer);
        Button imprime= view.findViewById(R.id.btnBultoImprimir);
        String detalle=listas.get(i).get(i1).getDetalles();
        ver.setVisibility(Libreria.tieneInformacion(detalle) ? View.VISIBLE : View.GONE );
        view.findViewById(R.id.btnBultoBaja).setVisibility(View.GONE);

        ver.setOnClickListener(view1 -> {
            ((Bultos)activity).dlgDetBultos(detalle);
        });

        switch(listas.get(i).get(i1).getEstatus()){
            case "Captura":{
                view.findViewById(R.id.btnBultoImprimir).setVisibility(View.GONE);
                view.findViewById(R.id.txtBultoEstatus).setBackgroundResource(R.color.colorExitoInsertado);
                ((TextView) view.findViewById(R.id.txtBultoEstatus)).setTextColor(activity.getResources().getColor(R.color.black));
                imprime.setVisibility(View.GONE);
                break;
            }
            case "Espera":{
                view.findViewById(R.id.btnBultoBaja).setVisibility(View.VISIBLE);
                ((Button)view.findViewById(R.id.btnBultoBaja)).setText("Baja a Captura");
                view.findViewById(R.id.btnBultoBaja).setOnClickListener(v -> activity.bajaCaptura(listas.get(i).get(i1).getContenedor()));
                view.findViewById(R.id.txtBultoEstatus).setBackgroundResource(R.color.color1);
                ((TextView) view.findViewById(R.id.txtBultoEstatus)).setTextColor(activity.getResources().getColor(R.color.colorTextos));
                imprime.setVisibility(View.GONE);
                break;
            }
            case "Cerrado":{
                view.findViewById(R.id.btnBultoBaja).setVisibility(View.VISIBLE);
                ((Button)view.findViewById(R.id.btnBultoBaja)).setText("Abre Contenedor");
                ((TextView) view.findViewById(R.id.txtBultoEstatus)).setTextColor(activity.getResources().getColor(R.color.black));
                view.findViewById(R.id.btnBultoBaja).setOnClickListener(v -> activity.abreContenedor(listas.get(i).get(i1).getContenedor()));
                imprime.setVisibility(View.VISIBLE);
                break;
            }
        }

        imprime.setOnClickListener(v -> activity.imprimir(i, i1));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
