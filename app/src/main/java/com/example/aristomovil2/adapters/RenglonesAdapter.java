package com.example.aristomovil2.adapters;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.aristomovil2.modelos.Renglon;
import com.example.aristomovil2.PuntoVenta;
import com.example.aristomovil2.R;
import java.util.List;

public class RenglonesAdapter extends BaseAdapter{
    private final List<Renglon> renglones;
    private final PuntoVenta activity;

    /**
     * Constructor del adapter
     * @param renglones Lista de renglones
     * @param activity Referencia al activity PutnoVenta
     */
    public RenglonesAdapter(List<Renglon> renglones, PuntoVenta activity){
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_venta_renglon, viewGroup, false);

        EditText input = view.findViewById(R.id.adapter_renglones_input);

        ((ImageView)view.findViewById(R.id.adapter_renglones_img)).setImageBitmap(renglones.get(position).getFoto());
        ((TextView)view.findViewById(R.id.adapter_renglones_codigo)).setText(renglones.get(position).getCodigo());
        ((TextView)view.findViewById(R.id.adapter_renglones_producto)).setText(renglones.get(position).getProducto());
        ((TextView)view.findViewById(R.id.adapter_renglones_precio)).setText(String.format("Precio: $%s", renglones.get(position).getPrecio()));
        ((TextView)view.findViewById(R.id.adapter_renglones_total)).setText(String.format("Total: $%s", renglones.get(position).getTotal()));
        ((TextView)view.findViewById(R.id.adapter_renglones_dispoible)).setText(String.format("Disp: %s", renglones.get(position).getDisponible()));
        ((TextView)view.findViewById(R.id.adapter_renglones_cantidad)).setText(String.format("Cant: %s", renglones.get(position).getCant()));
        input.setText(String.valueOf((int) renglones.get(position).getCant()));

        view.findViewById(R.id.adapter_renglones_guardar).setOnClickListener(v -> activity.insertaRenglon("*"+ input.getText().toString(), renglones.get(position).getDvtaid()));
        view.findViewById(R.id.adapter_renglones_mas).setOnClickListener(v -> input.setText(String.valueOf(Integer.parseInt(input.getText().toString()) + 1)));

        view.findViewById(R.id.adapteR_renglones_menos).setOnClickListener(v -> {
            int cant = Integer.parseInt(input.getText().toString()) - 1;
            if(cant < 0)
                cant = 0;

            input.setText(String.valueOf(cant));
        });

        input.setOnKeyListener((v, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                activity.hideKeyboard(v);
                activity.insertaRenglon("*"+ input.getText().toString(), renglones.get(position).getDvtaid());
                return true;
            }
            return false;
        });

        view.findViewById(R.id.adapteR_renglones_eliminar).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("¿Deseas eliminar el producto?")
                    .setPositiveButton("Si", (dialog, id) -> activity.insertaRenglon("*0", renglones.get(position).getDvtaid()))
                    .setNegativeButton("No", (dialog, id) -> {});

            builder.show();
        });
        view.setOnLongClickListener(v -> false);

        return view;
    }
}
