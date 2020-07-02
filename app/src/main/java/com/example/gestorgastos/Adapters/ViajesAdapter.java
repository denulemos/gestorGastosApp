package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Editar.editar_usuario;
import com.example.gestorgastos.Editar.editar_viaje_encamino;
import com.example.gestorgastos.Editar.editar_viaje_planeado;
import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Listas.listaViajes;
import com.example.gestorgastos.Listas.viajes_propios;
import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.Modelos.ViajesModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViajesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ViajesModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog alertDialog;
    private android.app.AlertDialog.Builder builder;

    public ViajesAdapter(Context context, ArrayList<ViajesModel> arrayList, Activity parent) {
        this.context = context;
        this.arrayList = arrayList;
        this.tempArray = arrayList;
        this.parent = parent;
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
        convertView = inf.inflate(R.layout.viajelv, null);
        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());

        TextView chofer = (TextView) convertView.findViewById(R.id.choferViaje);
        TextView origen = (TextView) convertView.findViewById(R.id.origenViaje);
        TextView destino = (TextView) convertView.findViewById(R.id.destinoViaje);
        TextView numeroViaje = (TextView) convertView.findViewById(R.id.numeroViaje);
        TextView estadoViaje = (TextView) convertView.findViewById(R.id.estadoViaje);
        TextView montoViaje = (TextView) convertView.findViewById(R.id.montoViaje);

        final ViajesModel viajesModel = arrayList.get(position);

        if (viajesModel.getChofer() != null) {
            chofer.setText("DNI Chofer: " + viajesModel.getChofer());
        } else {
            chofer.setText("Disponible");
        }

        origen.setText("Origen: " + viajesModel.getOrigen());
        destino.setText("Destino: " + viajesModel.getDestino());
        numeroViaje.setText("Viaje: " + String.valueOf(viajesModel.getId()));
        estadoViaje.setText("Estado: " + viajesModel.getEstado());
        montoViaje.setText("Monto Acumulado: " + String.valueOf(viajesModel.getMontoTotal()) + "$");


        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ListView listView = new ListView(parent.getContext());
                ArrayList<String> items = new ArrayList<>();
                items.add("Editar");


                if (viajesModel.getChofer() == null){
                    items.add("Cancelar Viaje");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);
                listView.setAdapter(adapter);

                android.app.AlertDialog.Builder builder = new
                        android.app.AlertDialog.Builder(parent.getContext());

                builder.setCancelable(true);

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

                                                                ViewGroup vg = (ViewGroup) view;
                                                                alertDialog.dismiss();
                                                                TextView txt = (TextView) vg.findViewById(R.id.txtitem);

                                                                if (txt.getText().equals("Editar")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("Aviso")
                                                                            .setMessage("¿Desea editar este viaje?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    if (viajesModel.getEstado().equals("En Camino")) {
                                                                                        Intent i = new Intent(parent.getContext(), editar_viaje_encamino.class);
                                                                                        i.putExtra("destino", viajesModel.getDestino());
                                                                                        i.putExtra("id", viajesModel.getId());
                                                                                        parent.getContext().startActivity(i);


                                                                                    } else {
                                                                                        Intent i = new Intent(parent.getContext(), editar_viaje_planeado.class);
                                                                                        if (viajesModel.getChofer() != null) {
                                                                                            i.putExtra("chofer", viajesModel.getChofer());
                                                                                        } else {
                                                                                            i.putExtra("chofer", "");
                                                                                        }
                                                                                        i.putExtra("id", viajesModel.getId());
                                                                                        i.putExtra("origen", viajesModel.getOrigen());
                                                                                        i.putExtra("destino", viajesModel.getDestino());
                                                                                        parent.getContext().startActivity(i);

                                                                                    }


                                                                                }

                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .show();
                                                                }

                                                                else if (txt.getText().equals("Cancelar Viaje")){
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("¡Atencion!")
                                                                            .setMessage("¿Esta seguro que desea cancelar el viaje numero " + viajesModel.getId() +"?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                          sql.cancelarViaje(viajesModel.getId());

                                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Actualizando", "Espere un momento");
                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("id", viajesModel.getId());
                                                                                        jsonObject.put("action", "cancelarViaje");
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }


                                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {

                                                                                                    loading.dismiss();
                                                                                                    Toast.makeText(parent.getContext() , "Viaje Cancelado", Toast.LENGTH_SHORT).show();
                                                                                                    if (context instanceof listaViajes) {
                                                                                                        ((listaViajes) context).loadDataInListView_disponibles();
                                                                                                    }

                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            loading.dismiss();
                                                                                            Toast.makeText(parent.getContext() , "Viaje Cancelado", Toast.LENGTH_SHORT).show();
                                                                                            if (context instanceof listaViajes) {
                                                                                                ((listaViajes) context).loadDataInListView_disponibles();
                                                                                            }
                                                                                        }
                                                                                    });

                                                                                    queue.add(stringRequest);


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


                ArrayList<ViajesModel> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).getChofer().toUpperCase().contains(constraint)) {
                        ViajesModel um = new ViajesModel(tempArray.get(i).getId(), tempArray.get(i).getChofer(), tempArray.get(i).getOrigen(), tempArray.get(i).getDestino(), tempArray.get(i).getEstado(), tempArray.get(i).getMontoTotal());
                        filters.add(um);
                    } else if (String.valueOf(tempArray.get(i).getId()).toUpperCase().contains(constraint)) {
                        ViajesModel um = new ViajesModel(tempArray.get(i).getId(), tempArray.get(i).getChofer(), tempArray.get(i).getOrigen(), tempArray.get(i).getDestino(), tempArray.get(i).getEstado(), tempArray.get(i).getMontoTotal());
                        filters.add(um);
                    } else if (tempArray.get(i).getDestino().toUpperCase().contains(constraint)) {
                        ViajesModel um = new ViajesModel(tempArray.get(i).getId(), tempArray.get(i).getChofer(), tempArray.get(i).getOrigen(), tempArray.get(i).getDestino(), tempArray.get(i).getEstado(), tempArray.get(i).getMontoTotal());
                        filters.add(um);
                    } else if (tempArray.get(i).getOrigen().toUpperCase().contains(constraint)) {
                        ViajesModel um = new ViajesModel(tempArray.get(i).getId(), tempArray.get(i).getChofer(), tempArray.get(i).getOrigen(), tempArray.get(i).getDestino(), tempArray.get(i).getEstado(), tempArray.get(i).getMontoTotal());
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
            arrayList = (ArrayList<ViajesModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
