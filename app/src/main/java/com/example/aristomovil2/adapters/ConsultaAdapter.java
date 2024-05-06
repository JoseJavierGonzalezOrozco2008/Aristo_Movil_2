package com.example.aristomovil2.adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aristomovil2.Consulta;
import com.example.aristomovil2.modelos.Producto;
import com.example.aristomovil2.R;
import com.example.aristomovil2.utileria.Libreria;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ViewHolder> {
    private final ArrayList<Producto> productos;
    private final Consulta activity;

    /**
     * Clase que define el ViewHolder que sera usado por el adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final View vista;

        /**
         * Constructor del ViewHolder
         * @param itemView vista del elemento de la lista
         */
        public ViewHolder(View itemView) {
            super(itemView);
            vista = itemView;
            vista.setOnCreateContextMenuListener(this);
        }

        /**
         * Retorna la vista del elemento
         * @return View
         */
        public View getVista(){ return vista; }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, 0, 0, "Modelos");    //groupId, itemId, order, title
        }
    }

    /**
     * Constructor del adapter
     * @param items Items del adapter
     * @param consulta Referencia a la actividad de Consulta
     */
    public ConsultaAdapter(ArrayList<Producto> items, Consulta consulta){
        this.productos = items;
        this.activity = consulta;
    }

    /**
     * Crea la vista paradado elemento
     * @param parent Padre de la vista
     * @param viewType Tipo
     * @return La vista creada
     */
    @NonNull
    @Override
    public ConsultaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta_producto, parent, false);
        return new ConsultaAdapter.ViewHolder(v);
    }

    /**
     * Construlle la vista del elemento y asigna la funcionalidad a acada elemento
     * @param holder ViewHolde
     * @param position Posicion del elemento en lista
     */
    @Override
    public void onBindViewHolder(@NonNull ConsultaAdapter.ViewHolder holder, int position) {
        View view = holder.getVista();

        EditText input = view.findViewById(R.id.editAdapterConsultaCantidad);
        LinearLayout agregaprod = view.findViewById(R.id.lyagrega);
        ((ImageView)view.findViewById(R.id.adapter_consulta_img)).setImageBitmap(productos.get(position).getFoto());
        ((TextView)view.findViewById(R.id.adapter_consulta_nombre)).setText(productos.get(position).getProducto());
        TextView precio = view.findViewById(R.id.adapter_consulta_precio);
        precio.setText(MessageFormat.format("${0}", productos.get(position).getPrecio()));
        ((TextView)view.findViewById(R.id.adapter_consulta_nombre)).setText(productos.get(position).getProducto());
        TextView tt=view.findViewById(R.id.adapter_consulta_disponible);
        tt.setText(MessageFormat.format("Disponible: {0}", productos.get(position).getDisponible()));
        ((TextView)view.findViewById(R.id.adapter_consulta_disponible)).setTextColor((productos.get(position).getDisponible()==0? Color.RED:Color.BLACK));

        agregaprod.setVisibility(activity.estacionID>0 ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.btnAdapterConsultaAgregar).setOnClickListener(v -> activity.insertaRenglon("*"+ (input.getText().toString().equals("0")?"1":input.getText().toString()), productos.get(position).getCodigo()));

        view.findViewById(R.id.btnAdapterConsultaPlus).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out));

            v.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    input.setText(String.valueOf(Integer.parseInt(input.getText().toString()) + 1));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        view.findViewById(R.id.btnAdapterConsultaMinus).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out));

            v.getAnimation().setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    int cant = Integer.parseInt(input.getText().toString()) - 1;
                    if(cant < 0)
                        cant = 0;

                    input.setText(String.valueOf(cant));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        input.setOnKeyListener((v, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                activity.hideKeyboard(v);
                activity.insertaRenglon("*"+ input.getText().toString(), productos.get(position).getCodigo());
                return true;
            }
            return false;
        });

        precio.setOnClickListener(vPrecio-> {
            String preciosvolume=productos.get(position).getPreciovolumen();
            if(Libreria.tieneInformacion(preciosvolume)){
                Dialog dialogo = new Dialog(this.activity, R.style.Dialog);
                //dialogo.getWindow().setBackgroundDrawableResource(R.color.aristo_azul);
                dialogo.setContentView(R.layout.item_repo);
                dialogo.setTitle(productos.get(position).getProducto());
                TextView precios=dialogo.findViewById(R.id.rere_prod);
                dialogo.findViewById(R.id.rere_codigo).setVisibility(View.GONE);
                dialogo.findViewById(R.id.rere_can).setVisibility(View.GONE);
                dialogo.findViewById(R.id.rere_origen).setVisibility(View.GONE);
                dialogo.findViewById(R.id.rere_destino).setVisibility(View.GONE);
                precios.setText(" "+preciosvolume.replace("<br/>","\n\r"));
                dialogo.show();
            }else{
                this.activity.muestraMensaje(this.activity,"lista de precios no disponible para "+productos.get(position).getProducto(),R.drawable.mensaje_warning);
            }
        });
    }

    /**
     * Retorna el tamaño de la lista del adapter
     * @return Tamaño de la lista
     */
    @Override
    public int getItemCount() {
        return productos.size();
    }
}
