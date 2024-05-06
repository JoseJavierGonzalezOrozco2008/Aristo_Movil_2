package com.example.aristomovil2.adapters;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aristomovil2.MainMenu;
import com.example.aristomovil2.R;
import com.example.aristomovil2.modelos.MenuItem;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class GrupoAdapter extends BaseAdapter {
    private final ArrayList<ArrayList<MenuItem>> listas;
    private final ArrayList<String> grupos;
    private final MainMenu activity;

    public GrupoAdapter(ArrayList<ArrayList<MenuItem>> listas, ArrayList<String> grupos, MainMenu activity) {
        this.listas = listas;
        this.activity = activity;
        this.grupos = grupos;
    }

    @Override
    public int getCount() { return listas.size(); }

    @Override
    public Object getItem(int i) { return listas.get(i); }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grupo, viewGroup, false);

        NavigationView navigationView = activity.findViewById(R.id.navigationView);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int cols = dpWidth >= 600 ? 4 : 3;

        ((TextView)view.findViewById(R.id.itemGrupoTitulo)).setText(grupos.get(i));

        RecyclerView recycler = view.findViewById(R.id.item_menu_recycler);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity, cols, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        MenuAdapter adapter = new MenuAdapter(listas.get(i), navigationView, activity);
        recycler.setAdapter(adapter);

        return view;
    }
}
