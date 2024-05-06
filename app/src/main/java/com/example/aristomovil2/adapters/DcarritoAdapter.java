package com.example.aristomovil2.adapters;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CIERRA_BULTO;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.PuntoVenta;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Renglon;

import java.text.MessageFormat;
import java.util.List;

public class DcarritoAdapter extends BaseAdapter{
    private final List<Renglon> renglones;
    private final ActividadBase activity;

    /**
     * Constructor del adapter
     * @param renglones Lista de renglones
     * @param activity Referencia al activity PutnoVenta
     */
    public DcarritoAdapter(List<Renglon> renglones, ActividadBase activity){
        this.renglones = renglones;
        this.activity = activity;
    }

    /**
     * Retorno el tamaño de la lista de renglones
     * @return El tamaño de la lista de renglones
     */
    @Override
    public int getCount() {
        return renglones.size();
    }

    /**
     * Retorno un renglon de la lista
     * @param i Posicion del renglon
     * @return El renglon en la posicion i
     */
    @Override
    public Object getItem(int i){ return renglones.get(i); }

    /**
     *Retorna el id de un renglon
     * @param i La posicion del renglon
     * @return El id del renglon
     */
    @Override
    public long getItemId(int i) { return 0; }

    /**
     * Construlle la vista del renglon
     * @param position Posicion del renglon
     * @param view .
     * @param viewGroup Padre de la vista
     * @return La vista del elemento
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_carrito, viewGroup, false);

        TextView cantidad=view.findViewById(R.id.item_cantidad);
        TextView precio=view.findViewById(R.id.item_precio);
        TextView total=view.findViewById(R.id.item_totalren);
        TextView producto=view.findViewById(R.id.item_producto);
        cantidad.setText(renglones.get(position).getCant()+"");
        precio.setText(renglones.get(position).getPrecio()+"");
        total.setText("$"+renglones.get(position).getTotal());
        producto.setText(renglones.get(position).getProducto());
        view.setOnLongClickListener(v -> {
            if(activity instanceof Carrito){
                Carrito actividad=(Carrito)activity;
                actividad.dlgRenglon(renglones.get(position));
            }
            return false;
        });

        return view;
    }
}
