package com.example.aristomovil2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.aristomovil2.Inventario;
import com.example.aristomovil2.modelos.Documento;
import com.example.aristomovil2.R;
import com.example.aristomovil2.RecibeDocumento;
import com.example.aristomovil2.utileria.Libreria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class DocumentosAdapter extends BaseAdapter {
    private final ArrayList<Documento> documentos;
    private final Inventario activity;
    private boolean inSelection;

    /**
     * Constructor del adapter
     * @param documentos Lista de documentos
     * @param activity Referencia al activity Inventario
     */
    public DocumentosAdapter(ArrayList<Documento> documentos, Inventario activity){
        this.documentos = documentos;
        this.activity = activity;
        this.inSelection = false;
    }

    /**
     * Retorna el tamaño de la lista de documentos
     * @return El tamaño de la lista de documentos
     */
    @Override
    public int getCount() { return documentos.size(); }

    /**
     * Retorna el id de un documento
     * @param i La posicion del codumento
     * @return El id del documento
     */
    @Override
    public Object getItem(int i) { return documentos.get(i); }

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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_documento, viewGroup, false);

        if(documentos.get(i).isSelected())
            view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_selected_shape);
        else {
            if(activity.nuevoCaptura)
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.venta_shape);
            else
                view.findViewById(R.id.ViewDocumento).setBackgroundResource(R.drawable.documento_captura);
        }

        ((TextView)view.findViewById(R.id.txtDocumentoProveedor)).setText(documentos.get(i).getProveedor() != null?documentos.get(i).getProveedor():"---");
        view.findViewById(R.id.txtDocumentoTarea).setVisibility(activity.documento == 16 && documentos.get(i).getGrupo() == 1? View.VISIBLE:View.GONE);


        if(activity.documento == 16){
            view.findViewById(R.id.txtDocumentoSurtidor).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.txtDocumentoSurtidor)).setText("Surtidor: " + documentos.get(i).getSurtidor());
            TextView prioridad = view.findViewById(R.id.txtDocumentoPrioridad);
            prioridad.setText("Prioridad:"+documentos.get(i).getPrioridad());
            prioridad.setVisibility(View.VISIBLE);
            int prior= documentos.get(i).getPrio();
            int color=prior == 0 ? R.color.colorNegro : (prior == 1 ? R.color.aristo_azul:R.color.fondoError);
            prioridad.setTextColor(activity.getResources().getColor(color));
        }

        //Factura
        if(documentos.get(i).getFactura() != null && (activity.documento == 14 || activity.documento == 17))
            ((TextView)view.findViewById(R.id.txtDocumentoFactura)).setText("Factura: " + documentos.get(i).getFactura());
        else
            view.findViewById(R.id.txtDocumentoFactura).setVisibility(View.GONE);

        //Pedido
        if(documentos.get(i).getPedido() != 0)
            ((TextView)view.findViewById(R.id.txtDocumentoPedido)).setText((activity.documento == 14?"OC:":
                    activity.documento == 16?"Pedido: ":"" ) + documentos.get(i).getPedido());
        else
            view.findViewById(R.id.txtDocumentoPedido).setVisibility(View.GONE);

        //Fecha
        if(documentos.get(i).getOrcofe() != null) {
            //((TextView)view.findViewById(R.id.txtDocumentoFecha)).setText(Libreria.fecha_to_fecha(documentos.get(i).getOrcofe(),"yyyy-MM-dd'T'HH:mm:ss","dd-MM-yyyy"));
            ((TextView)view.findViewById(R.id.txtDocumentoFecha)).setText(documentos.get(i).getOrcofe());
        }
        else
            view.findViewById(R.id.txtDocumentoFecha).setVisibility(View.GONE);

        //Renglones
        if(documentos.get(i).getRengsoc() != 0)
            ((TextView)view.findViewById(R.id.txtDocumentoRenglones)).setText("Rengs: " + documentos.get(i).getRengsoc());
        else
            view.findViewById(R.id.txtDocumentoRenglones).setVisibility(View.GONE);

        //Folio
        if(documentos.get(i).getFoliodi() != null){
            if(documentos.get(i).getFoliodi().length() > 3)
                ((TextView)view.findViewById(R.id.txtDocumentoFolio)).setText(documentos.get(i).getFoliodi().substring(documentos.get(i).getFoliodi().length() - 5));
            else
                ((TextView)view.findViewById(R.id.txtDocumentoFolio)).setText(documentos.get(i).getFoliodi());
        }
        else{
            if(documentos.get(i).getVntafolio().length() > 5)
                ((TextView)view.findViewById(R.id.txtDocumentoFolio)).setText(documentos.get(i).getVntafolio().substring(documentos.get(i).getVntafolio().length() - 5));
            else
                ((TextView)view.findViewById(R.id.txtDocumentoFolio)).setText(documentos.get(i).getVntafolio());
        }
        if(activity.documento==14)
            ((TextView)view.findViewById(R.id.txtDocumentoImporte)).setText(activity.format.format(documentos.get(i).getImporte()));
        else
            ((TextView)view.findViewById(R.id.txtDocumentoImporte)).setText("");
        ((Button)view.findViewById(R.id.btnDocumentoRecibeSurte)).setText(activity.documento == 14 || activity.documento == 17 ? "Recibe":(activity.documento == 16 ? "Surte":""));

        int pedido = documentos.get(i).getPedido();
        final String ordenCompra = pedido > 0 || pedido < -1? pedido+"":"Sin OC";
        final String folioDi = documentos.get(i).getFoliodi() != null? documentos.get(i).getFoliodi():"Sin folio";

        //if(inSelection)
            view.findViewById(R.id.btnDocumentoRecibeSurte).setVisibility(documentos.get(i).isSelected() || inSelection ? View.GONE:View.VISIBLE);
        {
            Button surte=view.findViewById(R.id.btnDocumentoRecibeSurte);
            int prior= documentos.get(i).getPrio();
            if( prior>0 && false){
                int color=prior >= 2 ? (prior > 2 ? R.color.aristo_amarillo_ant:R.color.colorWhite) :R.color.colorNegro;
                surte.setTextColor(activity.getResources().getColor(color));
            }
            if (activity.nuevoCaptura)
                surte.setOnClickListener(v -> activity.surte(documentos.get(i).getProvid(), documentos.get(i).getProveedor(), ordenCompra, documentos.get(i).getVntafolio(),documentos.get(i).getFactura(),Libreria.fecha_to_fecha(documentos.get(i).getOrcofe(),"yyyy-MM-dd'T'HH:mm:ss","yyMMdd"),documentos.get(i).getImporte(),documentos.get(i).getDiascred()));
            else {
                surte.setOnClickListener(v -> {
                    SharedPreferences sp = activity.getSharedPreferences("di", Context.MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();

                    editor.putString("facturaFolio", documentos.get(i).getFactura());
                    editor.putString("fechaFolio", documentos.get(i).getOrcofe());
                    editor.apply();
                    if(activity.documento==16 ||activity.documento==14){
                       activity.cambiaDdinControlador(i);
                    }else{
                        activity.cambiaDdinControlador(i);
                        /*Intent intent = new Intent(activity, RecibeDocumento.class);
                        intent.putExtra("documento", activity.documento);
                        intent.putExtra("OC", ordenCompra);
                        intent.putExtra("foliodi", folioDi);
                        intent.putExtra("prov/suc", documentos.get(i).getProveedor());
                        intent.putExtra("pedidos", ordenCompra);
                        if(activity.documento == 14)
                            intent.putExtra("divisa", documentos.get(i).getDivisa());

                        activity.startActivity(intent);*/
                    }

                });
            }
        }

        return view;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
