package com.example.aristomovil2.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aristomovil2.Acrividades.BuscaProducto;
import com.example.aristomovil2.Acrividades.Carrito;
import com.example.aristomovil2.ActividadBase;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.Generica;
import com.example.aristomovil2.utileria.Enumeradores;
import com.example.aristomovil2.utileria.Libreria;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ListaProdsAdapter extends ArrayAdapter<Generica> {
    private final Context context;
    private final List<Generica> items;

    private ActividadBase activity ;
    private Integer v_estacion;
    private String usuarioID;
    private Integer v_cliente;
    private String v_vntafolio;
    private Integer v_ultprod;
    private Integer v_tipovnta;
    private Boolean v_metpago;

    public ListaProdsAdapter(Context context, List<Generica> items, ActividadBase activity,Integer v_estacion, String usuarioID, Integer v_cliente, String v_vntafolio, Integer v_ultprod, Integer v_tipovnta , Boolean v_metpago) {
        super(context, R.layout.formato_lista_prods, items);
        this.context = context;
        this.items = items;
        this.activity = activity;
        this.v_estacion = v_estacion;
        this.usuarioID = usuarioID;
        this.v_cliente = v_cliente;
        this.v_vntafolio = v_vntafolio;
        this.v_ultprod = v_ultprod;
        this.v_tipovnta = v_tipovnta;
        this.v_metpago = v_metpago;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.formato_lista_prods, parent, false);
        }

        Button itemButton = convertView.findViewById(R.id.item_button);

        int limite = 8;
        String primeraLinea = items.get(position).getTex1();
                /*items.get(position).getTex1().length() > limite
                ? items.get(position).getTex1().substring(0, limite)
                : items.get(position).getTex1();

        String segundaLinea = items.get(position).getTex1().length() > limite
                ? items.get(position).getTex1().substring(limite)
                : "";

        if (segundaLinea.length() > limite) {
            segundaLinea = segundaLinea.substring(0, limite - 3) + "...";
        }*/
        String textoFinal = primeraLinea;// + (segundaLinea.isEmpty() ? "" : "\n" + segundaLinea);

        String[] partes = items.get(position).getTex1().split("\\$");
        itemButton.setText(textoFinal);
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this.getContext());
        itemButton.setOnClickListener(v -> {
            //Toast.makeText(context, "Acción para: " + this.v_ultprod, Toast.LENGTH_SHORT).show();
            //wsLineaCaptura(items.get(position).getTex3());


            if(partes.length >= 2){
                partes[1] = partes[1].trim();
                String regex = "^(?!0|\\.00)[0-9]+(,\\d{3})*(.[0-9]{0,2})$";
                if(Pattern.matches(regex, partes[1])){
                    //partes[1] = partes[1].replaceAll(",","");
                    //Double precio = Double.parseDouble(partes[1]);
                    if(activity instanceof Carrito){
                        ((Carrito)activity).wsLineaCaptura(items.get(position).getTex3());
                    }
                } else {
                    confirmacion.setTitle("Error");
                    confirmacion.setMessage("Este producto no cuenta con un precio correcto asignado...");
                    confirmacion.setCancelable(true);
                    confirmacion.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    confirmacion.show();
                }

            } else {
                confirmacion.setTitle("Error");
                confirmacion.setMessage("Este producto no cuenta con precio asignado...");
                confirmacion.setCancelable(true);
                confirmacion.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                confirmacion.show();
            }

        });

        return convertView;
    }
}
