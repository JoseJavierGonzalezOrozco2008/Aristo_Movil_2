package com.example.aristomovil2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aristomovil2.MainMenu;
import com.example.aristomovil2.R;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import com.example.aristomovil2.modelos.MenuItem;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final ArrayList<MenuItem> items;
    private final NavigationView nav;
    private final MainMenu activity;

    /**
     * Clase que define el ViewHolder que sera usado por el adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View vista;

        /**
         *Constructordel ViewHolder
         * @param v La vista
         */
        public ViewHolder(View v) {
            super(v);
            vista = v;
        }

        /**
         * Retorna la vista del elemento actual
         * @return La vista
         */
        public View getView() {
            return vista;
        }
    }

    /**
     * Constructor del adapter
     * @param dataSet Lista de elementos
     * @param nav Referencia del menu
     * @param activity Activity Mainmenu
     */
    public MenuAdapter(ArrayList<MenuItem> dataSet, NavigationView nav, MainMenu activity) {
        items = dataSet;
        this.nav = nav;
        this.activity = activity;
    }

    /**
     * Crea la vista paradado elemento
     * @param viewGroup Padre de la vista
     * @param viewType Tipo
     * @return La vista creada
     */
    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_main, viewGroup, false);
        return new MenuAdapter.ViewHolder(v);
    }

    /**
     * Inicializa la vista del elemento
     * @param viewHolder ViwHolder del elemento
     * @param position Posicion delcelemento
     */
    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder viewHolder, final int position) {
        int resId = 0;
        resId = activity.getResources().getIdentifier(items.get(position).getImagen(), "drawable", activity.getPackageName());//R.mipmap.vehiculo01
        //R.mipmap.vehiculo01_foreground
        Bitmap bitmap;

        if(resId == 0)
            bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.sinfoto);
        else
            bitmap = BitmapFactory.decodeResource(activity.getResources(),resId);

        ((ImageView)viewHolder.getView().findViewById(R.id.adapter_menu_imagen)).setImageBitmap(bitmap);

        ((TextView)viewHolder.getView().findViewById(R.id.adapter_menu_texto)).setText(items.get(position).getTexto());

        viewHolder.getView().setOnClickListener(v -> {
            Menu menu = nav.getMenu();
            android.view.MenuItem menuItem = menu.findItem(items.get(position).getId());
            activity.onNavigationItemSelected(menuItem);
        });
    }

    /**Retorna El tamaño de la lista
     * @return El tamaño de la lista
     */
    @Override
    public int getItemCount() {
        return items.size();
    }
}
