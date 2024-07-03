package com.example.aristomovil2.adapters;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CIERRA_BULTO;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.AristoConfig;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.Acrividades.Cobropv;
import com.example.aristomovil2.Acrividades.Devolucion;
import com.example.aristomovil2.Acrividades.Recargas;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.BuscaProd;
import com.example.aristomovil2.Inventario;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.Libreria;

import java.text.MessageFormat;
import java.util.List;

public class GenericaAdapter extends BaseAdapter {
    private List<Generica> listado;
    private ActividadBase base;
    private int opcion,v_xml;

    public GenericaAdapter(List<Generica> pListado, ActividadBase pBase, Integer pOpcion) {
        listado=pListado;
        base=pBase;
        opcion = pOpcion;
        v_xml=R.layout.item_acomodo;
        switch(opcion){
            case 1:
                v_xml=R.layout.item_buton;
                break;
            case 2:
            case 8:
                v_xml=R.layout.item_carrito;
                break;
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
        return listado.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(v_xml, viewGroup, false);
        }
        Generica gen=listado.get(i);
        Button accion;
        switch(opcion){
            case 0://default
                ((TextView)view.findViewById(R.id.acomid)).setText(gen.getTex1());
                ((TextView)view.findViewById(R.id.acomfe)).setText(gen.getTex2());
                ((TextView)view.findViewById(R.id.acomDIfolio)).setVisibility(View.GONE);
                ((TextView)view.findViewById(R.id.acomubica)).setVisibility(View.GONE);
                accion=view.findViewById(R.id.acoAction1);
                accion.setVisibility(View.GONE);
                break;
            case 1:
                accion=view.findViewById(R.id.btnGen);
                accion.setVisibility(View.VISIBLE);
                accion.setText(gen.getTex1());
                accion.setOnClickListener(v->{
                    if(gen.getEnt1()==51){
                        ((Recargas)base).recaTelefono(gen);
                    }else{
                        ((Recargas)base).recaReferencia(gen);
                    }
                });

                break;
            case 2:
                TextView t1=view.findViewById(R.id.item_producto);
                TextView t2=view.findViewById(R.id.item_totalren);
                t2.setVisibility(View.GONE);
                t1.setText(gen.getTex1());
                t1.setTextColor(base.getResources().getColor(R.color.black));
                view.setOnClickListener(view1 -> accion(gen));
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
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
                info.setOnClickListener(v->{
                    if(opcion==10){
                        if(base instanceof Devolucion){
                            ((Devolucion)base).agregaPorDevol(gen);
                        }
                        return;
                    }
                    String titulo="Imprime ticket";
                    String Mensaje = "";
                    String boton = "Imprimir";
                    String pantalla = "Pantalla";
                    boolean activo = true;
                    switch (opcion){
                        case 5:
                            titulo="Continuar con la venta";
                            Mensaje=MessageFormat.format("¿Seguro de bajar la venta con folio {0}?",gen.getTex1());
                            boton = "Continuar";
                            pantalla ="";
                            break;
                        case 7:
                            titulo="Gestión del catalogo del producto";
                            Mensaje=MessageFormat.format("Que quiere hace a continuacion con el producto  \n {0}",gen.getTex3());
                            boton = "Editar";
                            pantalla = "";
                            activo = gen.getLog1()!=null ? !gen.getLog1() : true;
                            break;
                        case 10:
                            titulo="Gestión del catalogo del producto";
                            Mensaje=MessageFormat.format("Qué quiere hace a continuación con el producto  \n {0}",gen.getTex3());
                            boton = "Devolver";
                            pantalla = "";
                            activo = gen.getLog1()!=null ? !gen.getLog1() : true;
                            break;
                        default:
                            Mensaje=MessageFormat.format("¿Seguro que volver a imprimir el folio {0}?",gen.getTex1());
                    }
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(base);
                    dialog.setTitle(titulo);
                    dialog.setMessage(Mensaje);
                    if(Libreria.tieneInformacion(pantalla)){
                        dialog.setNegativeButton(pantalla,(dialogInterface, i1) -> {
                            base.aPantalla(true);
                            accion(gen);
                        });
                    }
                    if(activo){
                        dialog.setPositiveButton(boton, (dialogInterface, i2) -> {
                            base.aPantalla(false);
                            accion(gen);
                        });
                    }
                    dialog.setNeutralButton("Regresar", (dialogInterface, i2) -> {dialogInterface.dismiss();});
                    dialog.show();
                });
                break;
            case 8:
                TextView margen=view.findViewById(R.id.item_producto);
                TextView valor=view.findViewById(R.id.item_totalren);
                margen.setText(gen.getTex1());
                valor.setText(gen.getTex2());
                valor.setTextColor(base.getResources().getColor(R.color.black));
                break;
        }
        view.setTag(gen);
        return view;
    }

    public void accion(Generica pGen){
        switch (opcion){
            case 2:
                //((Carrito)base).traeTicketVnta(pGen.getTex1());
                //base.wsImprime(Enumeradores.Valores.TAREA_VNTAULTIMAVNTA,pGen.getTex1());
                ((AristoConfig)base).eligeColonia(pGen);
                break;
            case 3:
                base.wsImprime(Enumeradores.Valores.TAREA_IMPRIMEARQE,pGen.getTex1());
                break;
            case 4:
                //((Inventario)base).traeTicketDoc(pGen.getTex1());
                base.wsImprime(Enumeradores.Valores.TAREA_IMPRIMEDOCS,pGen.getTex1());
                break;
            case 5:
                if (base instanceof Carrito) {
                    ((Carrito)base).vntaLimpia();
                    ((Carrito)base).traeVnta(pGen.getTex1());
                }else if (base instanceof Cobropv) {
                    ((Cobropv)base).asignaVntafolio(pGen.getTex1());
                }
                break;
            case 6:
                base.wsImprime(Enumeradores.Valores.TAREA_IMPRIMERETIRO,pGen.getTex1());
                break;
            case 7:
                ((BuscaProd)base).wsBuscaProd(pGen.getEnt1());
                break;
            case 9:
                base.wsImprime(Enumeradores.Valores.TAREA_TIKCORTEMOVIL,pGen.getTex1());
                break;
            case 10:
                if(base instanceof Devolucion){
                    ((Devolucion)base).agregaPorDevol(pGen);
                }
                break;
        }
    }
}
