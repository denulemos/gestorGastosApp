package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;

import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.Maps.MapsActivity2;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.previewFoto;

import java.util.ArrayList;

public class GastosAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GastosModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog alertDialog;

    public GastosAdapter(Context context, ArrayList<GastosModel> arrayList, Activity parent) {
        this.context = context;
        this.arrayList = arrayList;
        this.parent = parent;
        this.tempArray = arrayList;
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

    public Filter getFilter() {
        if (cs == null) {
            cs = new CustomFilter();

        }
        return cs;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inf.inflate(R.layout.gastolv, null);


        TextView choferGasto = (TextView) convertView.findViewById(R.id.choferGasto);
        choferGasto.setVisibility(View.GONE);

        TextView desestimado = (TextView) convertView.findViewById(R.id.desestimadoGasto);


        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());
        final GastosModel gastosModel = arrayList.get(position);
        TextView montoGasto = (TextView) convertView.findViewById(R.id.montoGasto);
        TextView viajeGasto = (TextView) convertView.findViewById(R.id.viajeGasto);
        TextView categoriaGasto = (TextView) convertView.findViewById(R.id.categoriaGasto);
        TextView fechaGasto = (TextView) convertView.findViewById(R.id.fechaGasto);

        if (gastosModel.getEstimacion() == 2) {
            desestimado.setText("Pendiente de Desestimacion");
            desestimado.setVisibility(View.VISIBLE);
        } else {
            desestimado.setVisibility(View.GONE);
        }





        montoGasto.setText("Monto: " + String.valueOf(gastosModel.getImporte()) + "$");
        viajeGasto.setText("Viaje: " + sql.getOrigenDestinoViaje(Integer.parseInt(gastosModel.getViaje())));
        categoriaGasto.setText("Categoria: " + gastosModel.getCategoria());
        fechaGasto.setText("Fecha: " + gastosModel.getFecha());


        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                android.app.AlertDialog.Builder builder = new
                        android.app.AlertDialog.Builder(parent.getContext());
                final ListView listView = new ListView(parent.getContext());
                builder.setCancelable(true);
               final ArrayList<String> items = new ArrayList<>();
                if (gastosModel.getUbicacionlong() != null) {
                    items.add("Ver Mapa");
                }
                if (gastosModel.getEstimacion() != 2) {
                    items.add("Solicitar Desestimacion");
                }


                if (gastosModel.getFoto() != null) {
                    items.add("Ver Imagen");
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);
                listView.setAdapter(adapter);
                builder.setPositiveButton("Salir", null);


                builder.setView(listView);

                alertDialog = builder.create();

                ConnectivityManager connectivityManager = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                   alertDialog.show();
                }
                else{
                    new AlertDialog.Builder(parent.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Se requiere una conexion a internet para gestionar")
                            .setNegativeButton("Ok", null)
                            .show();
                }




                listView.setOnItemClickListener(new
                                                        AdapterView.OnItemClickListener() {

                                                            @Override

                                                            public void onItemClick(final AdapterView<?> parent, View view, final int
                                                                    position, long id) {
                                                                alertDialog.dismiss();

                                                                ViewGroup vg = (ViewGroup) view;

                                                                TextView txt = (TextView) vg.findViewById(R.id.txtitem);

                                                                if (txt.getText().equals("Ver Mapa")) {
                                                                    Intent i = new Intent(parent.getContext(), MapsActivity2.class);

                                                                    i.putExtra("latitud", Double.parseDouble(gastosModel.getUbicacionlat()));
                                                                    i.putExtra("longitud", Double.parseDouble(gastosModel.getUbicacionlong()));

                                                                    parent.getContext().startActivity(i);
                                                                } else if (txt.getText().equals("Ver Imagen")) {
                                                                    Intent i = new Intent(parent.getContext(), previewFoto.class);

                                                                    i.putExtra("uri", gastosModel.getFoto());
                                                                    i.putExtra("parent", "user");
                                                                    parent.getContext().startActivity(i);
                                                                } else if (txt.getText().equals("Solicitar Desestimacion")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("¿Solicitar desestimacion del gasto?")
                                                                            .setMessage("Se le informara al administrador para proceder con la desestimacion del gasto")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    sql.solicitarDesestimacion(gastosModel.getId());
                                                                                    Toast.makeText(context.getApplicationContext(), "Desestimacion solicitada", Toast.LENGTH_SHORT).show();
                                                                                    if (context instanceof listaGastos) {
                                                                                        ((listaGastos) context).loadDataInListView_habiles();
                                                                                    }
                                                                                }

                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .show();

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


                ArrayList<GastosModel> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {

                    if (tempArray.get(i).getCategoria().toUpperCase().contains(constraint)) {
                        GastosModel um = new GastosModel(tempArray.get(i).getId(), tempArray.get(i).getViaje(), tempArray.get(i).getChofer(), tempArray.get(i).getCategoria(), tempArray.get(i).getUbicacionlat(), tempArray.get(i).getUbicacionlong(), tempArray.get(i).getFoto(), tempArray.get(i).getFecha(), tempArray.get(i).getImporte(), tempArray.get(i).getEstimacion(), tempArray.get(i).getSincronizacion());
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
            arrayList = (ArrayList<GastosModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
