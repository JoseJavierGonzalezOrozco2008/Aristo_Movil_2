package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Ddincontrolador;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.ConteoFisico;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.modelos.Ubicacion;
import com.example.aristomovil2.modelos.contenedores.VistaLotes;
import com.example.aristomovil2.utileria.Libreria;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;

public class LoteAdapter extends BaseAdapter {
    private final ArrayList<Generica> lotes;
    private final Ddincontrolador activity;
    private boolean inSelection;
    private float maxPorCap,cant_cap;
    private VistaLotes vistaLotes;

    /**
     * Constructor del adapter
     * @param lotes Lista de lotes
     * @param activity Referencia del activity
     */
    public LoteAdapter(ArrayList<Generica> lotes, Ddincontrolador activity, float pMxPorCap, VistaLotes pVistaLotes){
        this.lotes = lotes;
        this.activity = activity;
        this.inSelection = false;
        this.maxPorCap = pMxPorCap;
        this.cant_cap = sumacap();
        this.vistaLotes=pVistaLotes;
    }

    /**
     * Retorna el tamaño de la lista de lotes
     * @return El tamaño de la lista de lotes
     */
    @Override
    public int getCount() { return lotes.size(); }

    /**
     * Retorna un elemento de la lista
     * @param i La posicion del elemento
     * @return El elemento en la posicion i
     */
    @Override
    public Object getItem(int i) { return lotes.get(i); }

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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lote, viewGroup, false);

        Generica gen=lotes.get(i);
        //Button restar=view.findViewById(R.id.btnMenos);
        Button sumar=view.findViewById(R.id.btnMas);

        TextView vistalote=view.findViewById(R.id.vistaLote);
        //TextView vistaFecha=view.findViewById(R.id.vistaFecha);
        //TextView vistacant=view.findViewById(R.id.vistaCant);
        EditText vistaCantDi=view.findViewById(R.id.vistaCantdi);

        //restar.setTag(gen);
        sumar.setTag(gen);

        String renglon1="{0}{1}{2}";
        String renglon2=gen.getTex2();

        boolean completo=sumacap()==maxPorCap;

        //vistaCantDi.setEnabled(!completo);

        vistaCantDi.setOnKeyListener((view1, i1, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i1 == KeyEvent.KEYCODE_ENTER )){
                actualizaCant();
                operacion(gen,vistaCantDi,0);
                activity.presentaMensaje(vistaLotes.getVmensaje(),"");
                actualizaVista();
                this.notifyDataSetChanged();
            }
            return false;
        });

        vistalote.setText(MessageFormat.format(renglon1, Libreria.RPAD(gen.getTex1()," ",10),Libreria.RPAD(gen.getTex2()," ",6),Libreria.LPAD(gen.getDec1().toString()," ",6)));
        //vistaFecha.setText(renglon2);
        //vistacant.setText(gen.getDec1().toString());
        vistaCantDi.setText(gen.getDec2().toString());
        vistaCantDi.setInputType(InputType.TYPE_CLASS_NUMBER);
        //int tipo=((Ddincontrolador)activity).getDocumento();
        sumar.setOnClickListener(view1 -> {
            actualizaCant();
            operacion(gen,vistaCantDi,0);
            activity.presentaMensaje(vistaLotes.getVmensaje(),"");
            actualizaVista();
            this.notifyDataSetChanged();
        });
        /*restar.setOnClickListener(view1 -> {
            operacion(gen,vistaCantDi,-1);
            activity.presentaMensaje(vistaLotes.getVmensaje(),"");
            actualizaVista();
            this.notifyDataSetChanged();
        });*/

        return view;
    }

    private void operacion(Generica gen,TextView pVista,float pValor){
        //cant_cap =sumacap();
        String valorcap=pVista.getText().toString();
        float suma=Libreria.tieneInformacionFloat(valorcap,0)+pValor;
        int tipo=((Ddincontrolador)activity).getDocumento();
        /*if(suma>gen.getDec1().floatValue() && gen.getDec1().floatValue()>0){
            return;
        }*/
        if(suma<0){
            return;
        }
        float ant=(suma-gen.getDec3().floatValue());
        float dif =sumacap()+ant;
        if((dif<=maxPorCap || (suma<=gen.getDec3().floatValue() )) && (dif)>=0 ){
            gen.setDec2(new BigDecimal(suma));
            gen.setDec3(gen.getDec2());
            pVista.setText(gen.getDec2().toString());
            actualizaCant();
        }else{
            gen.setDec2(gen.getDec3());
        }
    }

    public float sumacap(){
        BigDecimal total=BigDecimal.ZERO;
        for(Generica gen:lotes){
            total = total.add(gen.getDec2());
        }
        return total.floatValue();
    }

    public float sumaant(){
        BigDecimal total=BigDecimal.ZERO;
        for(Generica gen:lotes){
            total = total.add(gen.getDec3());
        }
        return total.floatValue();
    }

    public String lotes(){
        String lote="";
        for(Generica gen:lotes){
            lote+=(gen.getId()<0 ? 0 :gen.getId())+","+gen.getTex1()+","+Libreria.dateToString(gen.getFec1(),"yyyy-MM-dd")+","+
                    (gen.getId()<0 ? 0:gen.getDec1())+","+(gen.getDec2()==null ? 0 : gen.getDec2())+";";

        }
        lote = lote.length()>0 ? lote.substring(0,lote.length()-1):lote;
        return lote;
    }

    public void actualizaCant(){
        cant_cap =sumacap();
    }

    public void actualizaVista(){
        float valor=sumacap();
        String mensaje=vistaLotes.getVista().getTag().toString();
        vistaLotes.getVista().setText(MessageFormat.format(mensaje,valor));
        String titulo=vistaLotes.getPorCapturar().getTag().toString();
        float porcaptura=maxPorCap-valor;
        if(porcaptura==0){
            vistaLotes.getPorCapturar().setText("Completo");
            vistaLotes.getPorCapturar().setBackgroundColor(activity.getResources().getColor(R.color.colorExitoInsertado));
            vistaLotes.getFecha().setVisibility(View.GONE);
            vistaLotes.getLote().setVisibility(View.GONE);
            vistaLotes.getCant().setVisibility(View.GONE);
            vistaLotes.getVfecha().setVisibility(View.GONE);
            vistaLotes.getVlote().setVisibility(View.GONE);
            vistaLotes.getVcant().setVisibility(View.GONE);
            vistaLotes.getAgregar().setVisibility(View.GONE);
            vistaLotes.getGuardar().setVisibility(View.VISIBLE);
        }else{
            vistaLotes.getPorCapturar().setText(MessageFormat.format(titulo,maxPorCap-valor));
            vistaLotes.getPorCapturar().setBackgroundColor(activity.getResources().getColor(R.color.colorTextos));
            vistaLotes.getFecha().setVisibility(View.VISIBLE);
            vistaLotes.getLote().setVisibility(View.VISIBLE);
            vistaLotes.getCant().setVisibility(View.VISIBLE);
            vistaLotes.getAgregar().setVisibility(View.VISIBLE);
            vistaLotes.getVfecha().setVisibility(View.VISIBLE);
            vistaLotes.getVlote().setVisibility(View.VISIBLE);
            vistaLotes.getVcant().setVisibility(View.VISIBLE);
            vistaLotes.getGuardar().setVisibility(View.GONE);
        }

    }

    public void add(Generica gen){
        lotes.add(gen);
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }

    public ArrayList<Generica> getLista(){
        return this.lotes;
    }
}
