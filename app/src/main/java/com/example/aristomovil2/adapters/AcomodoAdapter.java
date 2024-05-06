package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.MessageFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.Acomodo;
import com.example.aristomovil2.Inventario;
import com.example.aristomovil2.Lista_Acomodo;
import com.example.aristomovil2.R;
import com.example.aristomovil2.RecibeDocumento;
import com.example.aristomovil2.modelos.Documento;
import com.example.aristomovil2.modelos.Reposicion;
import com.example.aristomovil2.utileria.Libreria;

import java.util.ArrayList;

public class AcomodoAdapter extends BaseAdapter {
    private final ArrayList<Reposicion> reposicion;
    private final Lista_Acomodo activity;
    private boolean inSelection,directo;
    private String btnLabel,texto1,texto2,texto3,texto4;
    private Integer repone,proceso;

    /**
     * Constructor del adapter
     * @param reposicion Lista de reposicion
     * @param activity Referencia al activity Inventario
     */
    public AcomodoAdapter(ArrayList<Reposicion> reposicion,Integer pRepone, Lista_Acomodo activity){
        this.reposicion = reposicion;
        this.activity = activity;
        this.inSelection = false;
        this.repone=pRepone;
        directo=false;
        switch(repone){
            case 802:btnLabel="Repone";directo=true;
                break;
            case 803:btnLabel="Almacena";directo=false;
                break;
            case 476:btnLabel="Coloca";directo=true;
                break;
            case 862:btnLabel="Salida";directo=true;
                break;

        }
        texto1 = "Rengs:{0} En carro:{1}";
        texto2 = "{2} {3}";
        texto4 = "{2} {3} {4}";
        texto3 = repone != 803 ? "{0} {1}{2}" : "{3}";
        proceso= (repone == 802 || repone == 476 || repone == 862) ? 1 : 2;
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_acomodo, viewGroup, false);

        /*Logica para selecionar varios*/
        /*if(reposicion.get(i).isSelected())
            view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_selected_shape);
        else {
            if(activity.nuevoCaptura)
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_shape);
            else
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.documento_captura);
        }*/
        String usuario=Libreria.tieneInformacion(reposicion.get(i).getUsuario()) ? (" ("+reposicion.get(i).getUsuario()+")") : "";
        String dif = repone == 862 ? texto2 :(repone==803 ? texto4 : texto1) ;
        String tPieza = repone==803 || repone == 862 ? "Rengs/Pzas: ":"";
        String vPieza = repone==803 || repone == 862 ? (reposicion.get(i).getAcomrengs()+"/"+reposicion.get(i).getPiezas()):"";
        ((TextView)view.findViewById(R.id.acomid)).setText((reposicion.get(i).isCaptura() ? "":("Id:"+reposicion.get(i).getAcomid().toString()))+usuario);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView)view.findViewById(R.id.acomDIfolio)).setText(MessageFormat.format(dif,reposicion.get(i).getAcomrengs(),reposicion.get(i).getEncarro(),reposicion.get(i).getDcin(),(Libreria.tieneInformacion(reposicion.get(i).getMovimiento()) ? reposicion.get(i).getMovimiento():""),(tPieza+vPieza)));
            ((TextView)view.findViewById(R.id.acomubica)).setText(MessageFormat.format(texto3,reposicion.get(i).getUbicacion(),tPieza,vPieza,reposicion.get(i).getProv()));
        }else{
            ((TextView)view.findViewById(R.id.acomDIfolio)).setText(repone == 862 || repone==803 ? (reposicion.get(i).getDcin()+" "+(Libreria.tieneInformacion(reposicion.get(i).getMovimiento()) ? reposicion.get(i).getMovimiento():"") +(repone==803 ? (tPieza+vPieza): "" )):("Rengs:"+reposicion.get(i).getAcomrengs()+" En carro:"+reposicion.get(i).getEncarro()));
            ((TextView)view.findViewById(R.id.acomubica)).setText(repone == 803 ? (reposicion.get(i).getProv()) : (reposicion.get(i).getUbicacion()+" "+tPieza+vPieza));
        }
        ((TextView)view.findViewById(R.id.acomfe)).setText(reposicion.get(i).getDcinfefin());
        Button accion=view.findViewById(R.id.acoAction1);

        accion.setText(btnLabel);
        accion.setOnClickListener(v->{
            if(!reposicion.get(i).isCaptura() || directo){
                int proc = reposicion.get(i).getEncarro()>0 ? 2 :1;
                Intent intent = new Intent(activity, Acomodo.class);
                intent.putExtra("idacom",reposicion.get(i).getAcomid());
                intent.putExtra("folioDI",reposicion.get(i).getAcomdcinfolio());
                intent.putExtra("ubicacion",reposicion.get(i).getClaveubicacion());
                intent.putExtra("proceso",proc);
                intent.putExtra("repone",repone );
                intent.putExtra("folioCorto", reposicion.get(i).getDcin());
                //Intent intent = new Intent(this, Reposicion.class);
                activity.startActivity(intent);
                activity.finish();
            }else{
                activity.wsPideAndenes();
                activity.setFolioDI(reposicion.get(i).getAcomdcinfolio());
            }
        });


        return view;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
