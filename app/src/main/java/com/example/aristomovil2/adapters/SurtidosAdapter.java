package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.aristomovil2.ProductosDI;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.ProductoDI;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class SurtidosAdapter extends BaseExpandableListAdapter {
    private final ArrayList<ArrayList<ProductoDI>> productos;
    private final ArrayList<String> grupos;
    private final ProductosDI activity;

    public SurtidosAdapter(ArrayList<ArrayList<ProductoDI>> productos, ArrayList<String> grupos, ProductosDI activity) {
        this.productos = productos;
        this.grupos = grupos;
        this.activity = activity;
    }

    @Override
    public int getGroupCount() { return grupos.size(); }

    @Override
    public int getChildrenCount(int i) { return productos.get(i).size(); }

    @Override
    public Object getGroup(int i) { return grupos.get(i); }

    @Override
    public Object getChild(int i, int i1) { return productos.get(i).get(i1); }

    @Override
    public long getGroupId(int i) { return 0; }

    @Override
    public long getChildId(int i, int i1) { return 0; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_group, null);
        }
        String vector[]=headerTitle.split(";");
        String folio="";
        int pos=1;
        if(vector.length>1){
            folio=vector[1];
            pos = 1;
        }else{
            folio=vector[0];
            pos = 0;
        }
        int largof=folio.length();
        folio=largof>4 ? folio.substring(0,1)+"*"+folio.substring(largof-4,largof) : folio;
        vector[pos]=folio;
        headerTitle="";
        for(String cad:vector){
            headerTitle+=cad+";";
        }
        int largo=headerTitle.length();
        headerTitle=headerTitle.substring(0,largo-1);
        TextView lblListHeader = view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.replace("null;", "").replace(";","-"));

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_producto_di, viewGroup, false);

        ((TextView)view.findViewById(R.id.txtProductoDIProducto)).setText(productos.get(i).get(i1).getProducto());
        ((TextView)view.findViewById(R.id.txtProductoDICodigo)).setText("Codigo: " + productos.get(i).get(i1).getCodigo());
        ((TextView)view.findViewById(R.id.txtProsuctoDIRegistrado)).setText(productos.get(i).get(i1).getRegistrado() + " pzas");

        if(productos.get(i).get(i1).getUsuario() == null)
            view.findViewById(R.id.txtProductoDIUsuario).setVisibility(View.GONE);
        else
            ((TextView)view.findViewById(R.id.txtProductoDIUsuario)).setText(productos.get(i).get(i1).getUsuario());
        view.findViewById(R.id.btnProductoDICaduca).setVisibility(View.GONE);
        if(!Libreria.getBoolean(productos.get(i).get(i1).getFaltacadu()))
            view.findViewById(R.id.btnProductoDICaduca).setVisibility(View.GONE);
        else
            view.findViewById(R.id.btnProductoDICaduca).setOnClickListener( v -> {
                activity.ddin = productos.get(i).get(i1).getDdinid();
                activity.producto = productos.get(i).get(i1).getProducto();
                activity.cantidad = productos.get(i).get(i1).getRegistrado();

                if(activity.muestraRegistros)
                    activity.traeCaducidadesEnvio();
                else
                    activity.traeCaducidades();
            });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
