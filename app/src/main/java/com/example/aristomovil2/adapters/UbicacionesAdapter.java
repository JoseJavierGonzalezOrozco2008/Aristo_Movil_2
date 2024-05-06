package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.ConteoFisico;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.R;

import java.text.MessageFormat;
import java.util.ArrayList;

public class UbicacionesAdapter extends BaseAdapter {
    private final ArrayList<Ubicacion> ubicaciones;
    private final ConteoFisico activity;
    private boolean inSelection;

    /**
     * Constructor del adapter
     * @param ubicaciones Lista de ubicaciones
     * @param activity Referencia del activity
     */
    public UbicacionesAdapter(ArrayList<Ubicacion> ubicaciones, ConteoFisico activity){
        this.ubicaciones = ubicaciones;
        this.activity = activity;
        this.inSelection = false;
    }

    /**
     * Retorna el tamaño de la lista de ubicaciones
     * @return El tamaño de la lista de ubicaciones
     */
    @Override
    public int getCount() { return ubicaciones.size(); }

    /**
     * Retorna un elemento de la lista
     * @param i La posicion del elemento
     * @return El elemento en la posicion i
     */
    @Override
    public Object getItem(int i) { return ubicaciones.get(i); }

    /**
     * Retornoa el id de ina ubicacion
     * @param i La posicion de la ubicacion
     * @return El id de lau ubicacion en la posicion i
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista de una ubicacion
     * @param i La posicion de la ubicacion
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista de la ubicacion
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ubicacion, viewGroup, false);

        if(ubicaciones.get(i).isSelected())
            view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_selected_shape);
        else {
            if(ubicaciones.get(i).getEstatus().equals("En conteo")) {
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.item_verde);
                ((Button)view.findViewById(R.id.btnUbicacionContar)).setText("Contar");
            }
            else if(ubicaciones.get(i).getEstatus().equals("Cerrado")) {
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.item_amarillo);
                ((Button) view.findViewById(R.id.btnUbicacionContar)).setText("Abrir");
            }
            else {
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_shape);
                ((Button)view.findViewById(R.id.btnUbicacionContar)).setText("Asignar");
            }
        }

        view.findViewById(R.id.btnUbicacionVer).setVisibility(ubicaciones.get(i).getEstatus().equals("Cerrado")? View.VISIBLE:View.GONE);
        ((TextView)view.findViewById(R.id.txtUbicacionCodigo)).setText(ubicaciones.get(i).getUbicacion());
        ((TextView)view.findViewById(R.id.txtUbicacionEstatus)).setText(MessageFormat.format("({0}) {1}", ubicaciones.get(i).getCodigo(), ubicaciones.get(i).getEstatus()));

        if(inSelection)
            view.findViewById(R.id.btnUbicacionContar).setOnClickListener(null);
        else {
            if(ubicaciones.get(i).getEstatus().equals("En conteo"))
                view.findViewById(R.id.btnUbicacionContar).setOnClickListener(v -> activity.contarUbicacion(ubicaciones.get(i).getCodigo()));
            else if(ubicaciones.get(i).getEstatus().equals("Cerrado"))
                view.findViewById(R.id.btnUbicacionContar).setOnClickListener(v -> activity.abrirUbicacion(ubicaciones.get(i).getAsifid()));
            else
                view.findViewById(R.id.btnUbicacionContar).setOnClickListener(v -> activity.asignaUbicacion(String.valueOf(ubicaciones.get(i).getUbiid())));
        }

        view.findViewById(R.id.btnUbicacionVer).setOnClickListener(view1 -> activity.verUbicacion(ubicaciones.get(i).getAsifid(), ubicaciones.get(i).getUbicacion()));

        return view;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
