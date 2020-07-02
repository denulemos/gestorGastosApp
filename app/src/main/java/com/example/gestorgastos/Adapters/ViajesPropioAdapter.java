package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Listas.viajes_propios;
import com.example.gestorgastos.Modelos.ViajesModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViajesPropioAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ViajesModel> arrayList, tempArray;
    private Activity parent;
    private android.app.AlertDialog alertDialog;
    private CustomFilter cs;

    public ViajesPropioAdapter(Context context, ArrayList<ViajesModel> arrayList, Activity parent) {
        this.context = context;
        this.tempArray = arrayList;
        this.arrayList = arrayList;
        this.parent = parent;
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
        convertView = inf.inflate(R.layout.viajepropiolv, null);
        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());
        TextView origen = (TextView) convertView.findViewById(R.id.origenViaje);
        TextView destino = (TextView) convertView.findViewById(R.id.destinoViaje);
        TextView numeroViaje = (TextView) convertView.findViewById(R.id.numeroViaje);
        TextView estadoViaje = (TextView) convertView.findViewById(R.id.estadoViaje);
        TextView montoViaje = (TextView) convertView.findViewById(R.id.montoViaje);

        final ViajesModel viajesModel = arrayList.get(position);


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

                if (viajesModel.getEstado().equals("Planeado") || viajesModel.getEstado().equals("En Camino")) {
                    if (sql.elChoferTieneViajesEnCamino(viajesModel.getChofer())) {
                        if (viajesModel.getEstado().equals("En Camino")) {
                            items.add("Cambiar Estado");
                        }

                    } else {
                        items.add("Cambiar Estado");
                    }

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
                    if ((sql.elUsuarioTieneCamion(viajesModel.getChofer()))) {
                        if (sql.elChoferTieneViajesEnCamino(viajesModel.getChofer())) {
                            if (viajesModel.getEstado().equals("En Camino")) {
                                alertDialog.show();
                            }
                        } else {
                            alertDialog.show();
                        }
                    }
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
                                                                if (txt.getText().equals("Cambiar Estado")) {
                                                                    if (arrayList.get(position).getEstado().equals("Planeado")) {
                                                                        new AlertDialog.Builder(parent.getContext())
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .setTitle("Este viaje esta Planeado")
                                                                                .setMessage("¿Desea comenzar viaje?")
                                                                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        sql.cambiarAEnCamino(arrayList.get(position).getId());

                                                                                        final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");
                                                                                        JSONObject jsonObject = new JSONObject();
                                                                                        try {
                                                                                            jsonObject.put("id", viajesModel.getId());
                                                                                            jsonObject.put("action", "viajeAEnCamino");
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }


                                                                                        RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {

                                                                                                        loading.dismiss();
                                                                                                        Toast.makeText(context.getApplicationContext(), "Viaje en Camino!", Toast.LENGTH_SHORT).show();
                                                                                                        if (context instanceof viajes_propios) {
                                                                                                            ((viajes_propios) context).loadDataInListView_Camino();
                                                                                                        }


                                                                                                    }
                                                                                                }, new Response.ErrorListener() {
                                                                                            @Override
                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                loading.dismiss();
                                                                                                Toast.makeText(context.getApplicationContext(), "Viaje en Camino!", Toast.LENGTH_SHORT).show();
                                                                                                if (context instanceof viajes_propios) {
                                                                                                    ((viajes_propios) context).loadDataInListView_Camino();
                                                                                                }


                                                                                            }
                                                                                        });

                                                                                        queue.add(stringRequest);


                                                                                    }

                                                                                })
                                                                                .setNegativeButton("No", null)
                                                                                .show();
                                                                    } else if (arrayList.get(position).getEstado().equals("En Camino")) {
                                                                        new AlertDialog.Builder(parent.getContext())
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .setTitle("Este viaje esta en camino")
                                                                                .setMessage("¿Finalizar Viaje?")
                                                                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        sql.vencerViaje(arrayList.get(position).getId());
                                                                                        final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");
                                                                                        JSONObject jsonObject = new JSONObject();
                                                                                        try {
                                                                                            jsonObject.put("id", arrayList.get(position).getId());
                                                                                            jsonObject.put("action", "viajeFinalizado");
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }


                                                                                        RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {

                                                                                                        loading.dismiss();
                                                                                                        Toast.makeText(context.getApplicationContext(), "Viaje Completado ", Toast.LENGTH_SHORT).show();
                                                                                                        if (context instanceof viajes_propios) {
                                                                                                            ((viajes_propios) context).loadDataInListView_Planeados();
                                                                                                        }


                                                                                                    }
                                                                                                }, new Response.ErrorListener() {
                                                                                            @Override
                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                loading.dismiss();
                                                                                                Toast.makeText(context.getApplicationContext(), "Viaje Completado ", Toast.LENGTH_SHORT).show();
                                                                                                if (context instanceof viajes_propios) {
                                                                                                    ((viajes_propios) context).loadDataInListView_Planeados();
                                                                                                }


                                                                                            }
                                                                                        });

                                                                                        queue.add(stringRequest);


                                                                                    }

                                                                                })
                                                                                .setNegativeButton("No", null)
                                                                                .show();


                                                                    } else {
                                                                        Toast.makeText(context.getApplicationContext(), "Hubo un error en el cambio de estado", Toast.LENGTH_SHORT).show();
                                                                    }
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

                    if (String.valueOf(tempArray.get(i).getId()).toUpperCase().contains(constraint)) {
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
