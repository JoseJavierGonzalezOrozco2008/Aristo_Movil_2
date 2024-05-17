package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.MessageFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.Lista_Acomodo;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Cliente;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Reposicion;
import com.example.aristomovil2.utileria.Libreria;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends BaseAdapter {
    private final List<Generica> reposicion;
    private final ActividadBase activity;
    private boolean inSelection,directo;
    private Integer repone,proceso;

    /**
     * Constructor del adapter
     * @param reposicion Lista de reposicion
     * @param activity Referencia al activity Inventario
     */
    public ClienteAdapter(List<Generica> reposicion, ActividadBase activity){
        this.reposicion = reposicion;
        this.activity = activity;
        this.inSelection = false;
        directo=false;

    }

    /**
     * Retorna el tamaño de la lista de reposicion
     * @return El tamaño de la lista de reposicion
     */
    @Override
    public int getCount() { return reposicion.size(); }

    /**
     * Retorna el id de un documento
     * @param i La posicion del codumento
     * @return El id del documento
     */
    @Override
    public Object getItem(int i) { return reposicion.get(i); }

    /**
     * Retorna el id de un documento
     * @param i La posicion del codumento
     * @return El id del documento
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista del documento
     * @param i Posicion del documento
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cliente, viewGroup, false);

        TextView texto = view.findViewById(R.id.item_cliente);
        texto.setText(reposicion.get(i).getTex2());
        ImageButton seleccion=view.findViewById(R.id.item_selecciona);
        seleccion.setVisibility(View.GONE);
        ImageButton edita=view.findViewById(R.id.item_edita);
        texto.setOnClickListener(v->{
            if(activity instanceof Carrito){
                dlgDatos(reposicion.get(i));
                /*((Carrito)activity).setCliente(reposicion.get(i).getId(),reposicion.get(i).getTex1(),reposicion.get(i).getLog1());
                ((Carrito)activity).dlgClienteCierra();*/
            }
        });


        edita.setOnClickListener(v->{
            if(activity instanceof Carrito){
                ((Carrito)activity).cambiaCliente(reposicion.get(i).getId());

            }
        });
        //edita.setVisibility(reposicion.get(i).getId() > 0 ? View.VISIBLE:View.GONE);
        edita.setVisibility(View.GONE);

        return view;
    }

    public void dlgDatos(Generica pRen){
        Dialog dialog = new Dialog(activity);
        //Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.dialogo_dventa);
        dialog.setCancelable(true);
        TextView texto =((TextView)dialog.findViewById(R.id.dvtaProducto));
        texto.setText(pRen.getTex2());
        texto.setTypeface(Typeface.MONOSPACE);
        texto.setTextColor(Color.BLACK);
        ((TextView)dialog.findViewById(R.id.dvtaCantorig)).setVisibility(View.GONE);
        if(!activity.esHorizontal()){
            int width = (int)(activity.getResources().getDisplayMetrics().widthPixels);
            int height = (int)(dialog.getWindow().getWindowManager().getDefaultDisplay().getHeight());
            dialog.getWindow().setLayout(width, -2);
        }


        final RadioGroup grupo = dialog.findViewById(R.id.dvtaRadioGrup);
        LinearLayout lnCant = dialog.findViewById(R.id.dvtaLyCant);
        LinearLayout lnDesc = dialog.findViewById(R.id.dvtaLyDesc);
        LinearLayout lnPrec = dialog.findViewById(R.id.dvtaLyPrecio);
        lnCant.setVisibility(View.GONE);
        lnDesc.setVisibility(View.GONE);
        lnPrec.setVisibility(View.GONE);
        grupo.setVisibility(View.GONE);

        Button borrar = dialog.findViewById(R.id.btnBorrar);
        Button regresar = dialog.findViewById(R.id.btnRegresar);
        Button guardar = dialog.findViewById(R.id.btnGuardar);

        borrar.setText("Edita");
        borrar.setBackgroundResource(R.drawable.button_light_blue_900);

        guardar.setBackgroundResource(R.drawable.button_green);
        guardar.setText("Elige");

        borrar.setOnClickListener(v -> {
            ((Carrito)activity).cambiaCliente(pRen.getId());
            dialog.dismiss();
        });

        grupo.check(R.id.dvtaRadioCant);

        regresar.setBackgroundResource(R.drawable.button_colordiferencias);
        regresar.setOnClickListener(v-> dialog.dismiss());

        guardar.setOnClickListener(v-> {
            ((Carrito)activity).setCliente(pRen.getId(),pRen.getTex1(),pRen.getLog1());
            ((Carrito)activity).dlgClienteCierra();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}