package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Modelos.CategoriasModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;


import java.util.ArrayList;

public class CategoriaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CategoriasModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog alertDialog;

    public CategoriaAdapter(Context context, ArrayList<CategoriasModel> arrayList, Activity parent) {
        this.context = context;
        this.arrayList = arrayList;
        this.parent = parent;
        this.tempArray = arrayList;
    }

    public Filter getFilter() {
        if (cs == null) {
            cs = new CustomFilter();

        }
        return cs;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inf.inflate(R.layout.categorialv, null);

        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());

        final TextView categoria_nombre = (TextView) convertView.findViewById(R.id.nombreCategoria);

        final CategoriasModel categoriasModel = arrayList.get(position);

        categoria_nombre.setText(categoriasModel.getNombre());


        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new
                        AlertDialog.Builder(parent.getContext());

                builder.setCancelable(true);

                builder.setPositiveButton("Salir", null);
                final ListView listView = new ListView(parent.getContext());
                String[] items = {"Eliminar"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);
                listView.setAdapter(adapter);


                builder.setView(listView);

                alertDialog = builder.create();

                ConnectivityManager connectivityManager = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    alertDialog.show();
                }
                else{
                    new androidx.appcompat.app.AlertDialog.Builder(parent.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Se requiere una conexion a internet para gestionar")
                            .setNegativeButton("Ok", null)
                            .show();
                }


                listView.setOnItemClickListener(new
                                                        AdapterView.OnItemClickListener() {

                                                            @Override

                                                            public void onItemClick(AdapterView<?> parent, View view, int
                                                                    position, long id) {
                                                                alertDialog.dismiss();
                                                                ViewGroup vg = (ViewGroup) view;

                                                                TextView txt = (TextView) vg.findViewById(R.id.txtitem);

                                                                sql.eliminarCategoria(categoriasModel.getId());
                                                                Toast.makeText(context.getApplicationContext(), "Categoria eliminada", Toast.LENGTH_SHORT).show();
                                                                if (context instanceof gestionCategorias) {
                                                                    ((gestionCategorias) context).loadDataInListView();
                                                                }


                                                            }

                                                        });
            }
        });

        return convertView;
    }

    class CustomFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();


                ArrayList<CategoriasModel> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).getNombre().toUpperCase().contains(constraint)) {
                        CategoriasModel um = new CategoriasModel(tempArray.get(i).getId(), tempArray.get(i).getNombre());
                        filters.add(um);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = tempArray.size();
                results.values = tempArray;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<CategoriasModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
