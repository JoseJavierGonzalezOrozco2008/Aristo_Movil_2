package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.example.aristomovil2.MainMenu;
import com.example.aristomovil2.ProductosDI;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.MenuItem;
import com.example.aristomovil2.modelos.RenglonEnvio;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;
import java.util.Objects;

public class PorSurtirAdapter extends BaseExpandableListAdapter {
    private final ArrayList<ArrayList<RenglonEnvio>> listas;
    private final ArrayList<String> grupos;
    private final ProductosDI activity;

    public PorSurtirAdapter(ArrayList<ArrayList<RenglonEnvio>> listas, ArrayList<String> grupos, ProductosDI activity) {
        this.listas = listas;
        this.activity = activity;
        this.grupos = grupos;
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.replace("null", "").replace(";","-") + " (" + listas.get(i).size() + "Rengs)");

        return convertView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_producto_di, viewGroup, false);

        ((TextView)view.findViewById(R.id.txtProductoDIProducto)).setText("(" + listas.get(i).get(i1).getNumrenglon() +")" + listas.get(i).get(i1).getProducto());
        ((TextView)view.findViewById(R.id.txtProductoDICodigo)).setText(listas.get(i).get(i1).getCodigo());
        ((TextView)view.findViewById(R.id.txtProsuctoDIRegistrado)).setText(listas.get(i).get(i1).getCantpedida() + " por surtir");
        ((TextView)view.findViewById(R.id.txtProductoDIUsuario)).setText(listas.get(i).get(i1).getUbicacion());

        if(!Libreria.getBoolean(listas.get(i).get(i1).getFaltacadu()))
            view.findViewById(R.id.btnProductoDICaduca).setVisibility(View.GONE);
        else
            view.findViewById(R.id.btnProductoDICaduca).setOnClickListener( v -> {
                activity.ddin = listas.get(i).get(i1).getDdin();
                activity.producto = listas.get(i).get(i1).getProducto();
                activity.cantidad = listas.get(i).get(i1).getCantpedida();
                activity.traeCaducidadesEnvio();
            });

        view.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if(Objects.requireNonNull(activity.getIntent().getExtras()).getBoolean("result")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", true);
                        returnIntent.putExtra("renglon", listas.get(i).get(i1).getNumrenglon());
                        activity.setResult(Activity.RESULT_OK, returnIntent);
                        activity.finish();
                    }
                    return super.onDoubleTap(e);
                }
            });

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
