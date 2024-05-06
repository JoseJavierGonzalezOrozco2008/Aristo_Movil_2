package com.example.aristomovil2;

import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_CONT_LISTA_CONTADO;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_ELIMINAR_CONTADOS;
import static com.example.aristomovil2.utileria.Enumeradores.Valores.TAREA_RENGLONES_CONTADOS;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.aristomovil2.adapters.ContadosAdapter;
import com.example.aristomovil2.utileria.EnviaPeticion;
import java.util.ArrayList;

public class Contados extends ActividadBase {
private ArrayList<com.example.aristomovil2.modelos.Contados> contados;
    private ListView lista;
    private ContadosAdapter adapter;
    private String asifID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contados);

        Bundle extras = getIntent().getExtras();
        String conteo = extras.getString("conteo");
        boolean inventario = extras.getBoolean("inventario",true);
        asifID = extras.getString(inventario ? "asifid":"ascoid");
        boolean elimina = extras.getBoolean("elimina");


        inicializarActividad("Contados");

        lista = findViewById(R.id.listContados);

        if(elimina) {
            lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            lista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                    contados.get(i).setSelected(b);
                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.context_menu_contados, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    System.out.println("AQUI 1");
                    if (menuItem.getItemId() == R.id.menuContadosEliminar) {
                        StringBuilder productos = new StringBuilder();
                        int cantidad = 0;

                        for (com.example.aristomovil2.modelos.Contados c : contados)
                            if (c.isSelected()) {
                                cantidad++;
                                if (!productos.toString().equals(""))
                                    productos.append(",");
                                productos.append(c.getCodigo());
                            }
                        eliminar(productos.toString(), cantidad);
                        actionMode.finish();

                        return true;
                    }
                    System.out.println("AQUI 3" );
                    return false;


                }
                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    for (com.example.aristomovil2.modelos.Contados c : contados)
                        c.setSelected(false);
                }
            });
        }

        ((TextView)findViewById(R.id.txtContadosUbicacion)).setText(String.valueOf(conteo));
        peticionWS(inventario ? TAREA_RENGLONES_CONTADOS:TAREA_CONT_LISTA_CONTADO, "SQL", "SQL", asifID, "", "");


    }

    /**
     * Establece el contenido de la lista de ventas
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.emptyContados);
        ListView list = findViewById(R.id.listContados);
        list.setEmptyView(empty);
    }

    /**
     * Llama al servicio que elimina los productos seleccionados
     * @param poductos Lista de productos seleccionados
     * @param cantidad La cantidad de producto selecconados
     */
    private void eliminar(String poductos, int cantidad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Renglones");
        builder.setMessage("¿Seguro que quieres eliminar " + cantidad + " renglones?");
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton("Si", (dialog, which) -> {
            peticionWS(TAREA_ELIMINAR_CONTADOS, "SQL", "SQL", asifID, poductos, "");
            adapter.notifyDataSetChanged();
        });
        builder.show();

                //System.out.println("BORRAR CONTADOS ");
        //        adapter.notifyDataSetChanged();
        //        lista.setAdapter(adapter);
        //
        //        System.out.println(lista.getAdapter().getCount() + " "+lista.getAdapter().isEmpty());
    }

    /**
     * Procesa la respuesta de una peticion
     * @param output Repsuesta de la peticion
     */
    @Override
    public void Finish(EnviaPeticion output) {
        ContentValues obj = (ContentValues) output.getExtra1();
        cierraDialogo();
        if(obj != null) {
            switch (output.getTarea()){
                case TAREA_CONT_LISTA_CONTADO:
                case TAREA_RENGLONES_CONTADOS: {
                    contados = servicio.traeContados();
                    if (output.getExito()) {
                        adapter = new ContadosAdapter(contados, this);
                        lista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else
                        muestraMensaje(output.getMensaje(), R.drawable.mensaje_error);
                    break;
                }
                case TAREA_ELIMINAR_CONTADOS:
                    if(output.getExito()){
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_exito);
                        peticionWS(TAREA_RENGLONES_CONTADOS, "SQL", "SQL", asifID, "", "");
                    }
                    else
                        muestraMensaje(obj.getAsString("mensaje"), R.drawable.mensaje_error);

                    break;
            }
        }
        else {
            muestraMensaje("Error llamando al servicio", R.drawable.mensaje_error);
        }

    }
}