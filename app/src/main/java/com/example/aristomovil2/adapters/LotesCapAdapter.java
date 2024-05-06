package com.example.aristomovil2.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Bultos;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Bulto;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class LotesCapAdapter extends BaseExpandableListAdapter {
    final ArrayList<ArrayList<String>> listas;
    final ArrayList<String> grupos;
    final ActividadBase activity;

    public LotesCapAdapter(String detalles, Bultos activity) {
        grupos=new ArrayList<>();
        listas=new ArrayList<>();
        String[] elGrupo=detalles.split(",");
        String renglon[],cabGrupo;
        ArrayList<String> detGrupo;
        for(String grupo:elGrupo){
            cabGrupo=grupo.substring(0,32);
            grupos.add(cabGrupo);
            detGrupo=new ArrayList<>();
            if(grupo.length()>32){
                renglon=grupo.substring(32,grupo.length()).split("¿");
                for(String x:renglon){
                    detGrupo.add(x);
                    System.out.println(x);
                }
            }
            listas.add(detGrupo);
        }
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

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.replace("null;","").replace(";","-"));

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_orden_reposicion, viewGroup, false);

        ((TextView)view.findViewById(R.id.repMen1)).setText(listas.get(i).get(i1));
        ((TextView)view.findViewById(R.id.repMen2)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.repMen3)).setVisibility(View.GONE);
        ((TextView)view.findViewById(R.id.repMen4)).setVisibility(View.GONE);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
