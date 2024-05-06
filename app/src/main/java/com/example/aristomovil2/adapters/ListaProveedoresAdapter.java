package com.example.aristomovil2.adapters;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import androidx.annotation.ArrayRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * You can use this adapter to provide views for an {@link AdapterView},
 * Returns a view for each object in a collection of data objects you
 * provide, and can be used with list-based user interface widgets such as
 * {@link ListView} or {@link Spinner}.
 * <p>
 * By default, the array adapter creates a view by calling {@link Object#toString()} on each
 * data object in the collection you provide, and places the result in a TextView.
 * You may also customize what type of view is used for the data object in the collection.
 * To customize what type of view is used for the data object,
 * override {@link #getView(int, View, ViewGroup)}
 * and inflate a view resource.
 * </p>
 * <p>
 * For an example of using an array adapter with a ListView, see the
 * <a href="{@docRoot}guide/topics/ui/declaring-layout.html#AdapterViews">
 * Adapter Views</a> guide.
 * </p>
 * <p>
 * For an example of using an array adapter with a Spinner, see the
 * <a href="{@docRoot}guide/topics/ui/controls/spinner.html">Spinners</a> guide.
 * </p>
 * <p class="note"><strong>Note:</strong>
 * If you are considering using array adapter with a ListView, consider using
 * {@link androidx.recyclerview.widget.RecyclerView} instead.
 * RecyclerView offers similar features with better performance and more flexibility than
 * ListView provides.
 * See the
 * <a href="https://developer.android.com/guide/topics/ui/layout/recyclerview.html">
 * Recycler View</a> guide.</p>
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class ListaProveedoresAdapter<T> extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> mItems;
    private int mCount; // Variable para almacenar el número de elementos a mostrar

    public ListaProveedoresAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
        mContext = context;
        mItems = items;
        mCount = mItems.size(); // Por defecto, mostrar todos los elementos
    }


    // Método para actualizar el número de elementos a mostrar
    public void updateCount(int count) {
        mCount = Math.min(count, mItems.size()); // Asegurarse de no exceder el tamaño de la lista
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    @Override
    public int getCount() {
        return 3;
    }

}
