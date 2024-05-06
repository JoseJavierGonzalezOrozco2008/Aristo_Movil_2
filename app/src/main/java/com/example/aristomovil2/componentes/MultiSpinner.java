package com.example.aristomovil2.componentes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import java.util.Arrays;
import java.util.List;

/**
 * Clase que define un Spinner con seleccion multiple
 */
public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private List<String> items;
    private boolean[] selected;
    private MultiSpinnerListener listener;

    /**
     * Constructor del Spinner
     * @param context El contexto de la aplicacion
     */
    public MultiSpinner(Context context) {
        super(context);
    }

    /**
     * Constructor del Spinner
     * @param arg0 El contexto de la aplicacion
     * @param arg1 set de atributos
     */
    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    /**
     * Constructor del Spinner
     * @param arg0 El contexto de la aplicacion
     * @param arg1 Set de atributos
     * @param arg2 Styles
     */
    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    /**
     * Listener para el evento Click
     * @param dialog Dialogo
     * @param which Opcion seleccionada
     * @param isChecked Valor de la seleccion
     */
    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    /**
     * Listener para el evento Cancel
     */
    public void onCancel() {
        int seleccionados = 0;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i])
                seleccionados++;
        }

        String text;

        if(seleccionados == items.size())
            text = "Todos";
        else if(seleccionados == 0)
            text = "Sin seleccion";
        else if(seleccionados == 1)
            text = "1 seleccionado";
        else
            text = seleccionados + " seleccionados";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
        android.R.layout.simple_spinner_item,
        new String[]{text});
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    /**
     * Limpia la lista de elementos
     */
    public void limpiar(){
        Arrays.fill(selected, false);
    }

    /**
     * Listener del evento Click
     * @return Retorna true par indicar exito
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.cancel();
            onCancel();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();

        return true;
    }

    /**
     * Establece las opciones del Spinner
     * @param items Opciones del Spinner
     * @param allText Texto a mostrar
     * @param listener Listener
     */
    public void setItems(List<String> items, String allText, MultiSpinnerListener listener) {
        this.items = items;
        this.listener = listener;

        selected = new boolean[items.size()];
        Arrays.fill(selected, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{allText});
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }
}
