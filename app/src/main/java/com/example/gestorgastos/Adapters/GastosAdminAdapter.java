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
import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Maps.MapsActivity2;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.previewFoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GastosAdminAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GastosModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog dialog;

    public GastosAdminAdapter(Context context, ArrayList<GastosModel> arrayList, Activity parent) {
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inf.inflate(R.layout.gastolv, null);
        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());


        TextView desestimado = (TextView) convertView.findViewById(R.id.desestimadoGasto);
        final GastosModel gastosModel = arrayList.get(position);
        final MapsActivity2 mp2 = new MapsActivity2();
        TextView choferGasto = (TextView) convertView.findViewById(R.id.choferGasto);
        TextView montoGasto = (TextView) convertView.findViewById(R.id.montoGasto);
        TextView viajeGasto = (TextView) convertView.findViewById(R.id.viajeGasto);
        TextView categoriaGasto = (TextView) convertView.findViewById(R.id.categoriaGasto);
        TextView fechaGasto = (TextView) convertView.findViewById(R.id.fechaGasto);

        if (gastosModel.getEstimacion() == 2) {
            //Gasto pendiente de desestimacion
            desestimado.setText("Pendiente de Evaluacion");
        } else {
            desestimado.setVisibility(View.GONE);
        }

        choferGasto.setText("Chofer: " + gastosModel.getChofer());
        montoGasto.setText("Monto: " + String.valueOf(gastosModel.getImporte()) + "$");
        viajeGasto.setText("Numero Viaje: " + gastosModel.getViaje());
        categoriaGasto.setText("Categoria: " + gastosModel.getCategoria());
        fechaGasto.setText("Fecha: " + gastosModel.getFecha());

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ListView listView = new ListView(parent.getContext());
                ArrayList<String> items = new ArrayList<>();
                if (gastosModel.getEstimacion() == 2) {
                    //Gasto pendiente de desestimacion
                    items.add("Evaluar Desestimacion");
                } else {
                    items.add("Aprobar");
                    items.add("Desaprobar");
                }

                android.app.AlertDialog.Builder builder = new
                        android.app.AlertDialog.Builder(parent.getContext());

                builder.setCancelable(true);

                builder.setPositiveButton("Salir", null);

                if (gastosModel.getUbicacionlong() != null) {
                    items.add("Ver Mapa");
                }
                if (gastosModel.getFoto() != null) {
                    items.add("Ver fotografia");
                }

                builder.setView(listView);

                dialog = builder.create();
                ConnectivityManager connectivityManager = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    dialog.show();
                }
                else{
                    new AlertDialog.Builder(parent.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Error")
                            .setMessage("Se requiere una conexion a internet para gestionar")
                            .setNegativeButton("Ok", null)
                            .show();
                }




                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new
                                                        AdapterView.OnItemClickListener() {

                                                            @Override

                                                            public void onItemClick(final AdapterView<?> parent, View view, final int
                                                                    position, long id) {

                                                                dialog.dismiss();
                                                                ViewGroup vg = (ViewGroup) view;

                                                                TextView txt = (TextView) vg.findViewById(R.id.txtitem);

                                                                if (txt.getText().equals("Ver Mapa")) {
                                                                    Intent i = new Intent(parent.getContext(), MapsActivity2.class);

                                                                    i.putExtra("latitud", Double.parseDouble(gastosModel.getUbicacionlat()));
                                                                    i.putExtra("longitud", Double.parseDouble(gastosModel.getUbicacionlong()));

                                                                    parent.getContext().startActivity(i);
                                                                } else if (txt.getText().equals("Aprobar")) {
                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");
                                                                    long millis = System.currentTimeMillis();
                                                                    java.util.Date date = new java.util.Date(millis);
                                                                    SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm:ss", new Locale("ES"));
                                                                    Date fechaDate = new Date();
                                                                    String fecha = formateador.format(fechaDate);
                                                                    JSONObject jsonObject = new JSONObject();
                                                                    try {
                                                                        jsonObject.put("sheet", "Gastos");
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    JSONArray jsonArray = new JSONArray();


                                                                    JSONArray row = new JSONArray();
                                                                    row.put(gastosModel.getId());
                                                                    row.put(gastosModel.getViaje());
                                                                    row.put(gastosModel.getChofer());
                                                                    row.put(gastosModel.getCategoria());
                                                                    row.put(gastosModel.getUbicacionlong() + " , " + gastosModel.getUbicacionlat());
                                                                    row.put(gastosModel.getFecha().trim());
                                                                    row.put(gastosModel.getImporte());
                                                                    row.put("Estimado");
                                                                    row.put(fecha.trim());
                                                                    jsonArray.put(row);


                                                                    try {
                                                                        jsonObject.put("rows", jsonArray);
                                                                        jsonObject.put("action", "add");
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                            new Response.Listener<JSONObject>() {
                                                                                @Override
                                                                                public void onResponse(JSONObject response) {

                                                                                    loading.dismiss();
                                                                                    sql.sincronizarGasto(gastosModel.getId());
                                                                                    if (context instanceof gestionGastos) {
                                                                                        ((gestionGastos) context).loadDataInListView_habiles();
                                                                                    }


                                                                                }
                                                                            }, new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            loading.dismiss();
                                                                            sql.sincronizarGasto(gastosModel.getId());
                                                                            if (context instanceof gestionGastos) {
                                                                                ((gestionGastos) context).loadDataInListView_habiles();
                                                                            }
                                                                        }
                                                                    });

                                                                    queue.add(stringRequest);

                                                                }
                                                                else if (txt.getText().equals("Desaprobar")){
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("¿Desaprobar gasto?")
                                                                            .setMessage("Sera descontado del monto de viaje total.")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    double montoFinal =  sql.desestimarGasto(gastosModel.getId(), gastosModel.getViaje(), gastosModel.getImporte());
                                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");

                                                                                    SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm:ss", new Locale("ES"));
                                                                                    Date fechaDate = new Date();
                                                                                    String fecha = formateador.format(fechaDate);
                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("viaje" , Integer.parseInt(gastosModel.getViaje()));
                                                                                        jsonObject.put("monto" , montoFinal);
                                                                                        jsonObject.put("action", "addDesestimado");

                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    JSONArray jsonArray = new JSONArray();


                                                                                    JSONArray row = new JSONArray();
                                                                                    row.put(gastosModel.getId());
                                                                                    row.put(gastosModel.getViaje());
                                                                                    row.put(gastosModel.getChofer());
                                                                                    row.put(gastosModel.getCategoria());
                                                                                    row.put(gastosModel.getUbicacionlong() + " , " + gastosModel.getUbicacionlat());
                                                                                    row.put(gastosModel.getFecha().trim());
                                                                                    row.put(gastosModel.getImporte());

                                                                                    row.put("Desaprobado");
                                                                                    row.put(fecha.trim());
                                                                                    jsonArray.put(row);


                                                                                    try {
                                                                                        jsonObject.put("rows", jsonArray);

                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {

                                                                                                    loading.dismiss();
                                                                                                    sql.sincronizarGasto(gastosModel.getId());
                                                                                                    Toast.makeText(parent.getContext(), "Gasto Desaprobado con exito.", Toast.LENGTH_SHORT).show();


                                                                                                    if (context instanceof gestionGastos) {
                                                                                                        ((gestionGastos) context).loadDataInListView_habiles();
                                                                                                    }


                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            loading.dismiss();
                                                                                            sql.sincronizarGasto(gastosModel.getId());
                                                                                            if (context instanceof gestionGastos) {
                                                                                                ((gestionGastos) context).loadDataInListView_habiles();
                                                                                            }




                                                                                        }
                                                                                    });

                                                                                    queue.add(stringRequest);


                                                                                }

                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .show();

                                                                }

                                                                else if (txt.getText().equals("Ver fotografia")) {
                                                                    Intent i = new Intent(parent.getContext(), previewFoto.class);
                                                                    i.putExtra("uri", gastosModel.getFoto());
                                                                    i.putExtra("parent", "admin");
                                                                    parent.getContext().startActivity(i);
                                                                } else if (txt.getText().equals("Evaluar Desestimacion")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("¿Desestimar gasto?")
                                                                            .setMessage("Si se desestima el gasto, no se podra deshacer. De lo contrario, volvera a su estado original.")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    double montoFinal =  sql.desestimarGasto(gastosModel.getId(), gastosModel.getViaje(), gastosModel.getImporte());
                                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");

                                                                                    SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm:ss", new Locale("ES"));
                                                                                    Date fechaDate = new Date();
                                                                                    String fecha = formateador.format(fechaDate);
                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("viaje" , Integer.parseInt(gastosModel.getViaje()));
                                                                                        jsonObject.put("monto" , montoFinal);
                                                                                        jsonObject.put("action", "addDesestimado");

                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    JSONArray jsonArray = new JSONArray();


                                                                                    JSONArray row = new JSONArray();
                                                                                    row.put(gastosModel.getId());
                                                                                    row.put(gastosModel.getViaje());
                                                                                    row.put(gastosModel.getChofer());
                                                                                    row.put(gastosModel.getCategoria());
                                                                                    row.put(gastosModel.getUbicacionlong() + " , " + gastosModel.getUbicacionlat());
                                                                                    row.put(gastosModel.getFecha().trim());
                                                                                    row.put(gastosModel.getImporte());

                                                                                    row.put("Desestimado");
                                                                                    row.put(fecha.trim());
                                                                                    jsonArray.put(row);


                                                                                    try {
                                                                                        jsonObject.put("rows", jsonArray);

                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {

                                                                                                    loading.dismiss();
                                                                                                    sql.sincronizarGasto(gastosModel.getId());
                                                                                                    Toast.makeText(parent.getContext(), "Gasto Desestimado con exito.", Toast.LENGTH_SHORT).show();


                                                                                                    if (context instanceof gestionGastos) {
                                                                                                        ((gestionGastos) context).loadDataInListView_habiles();
                                                                                                    }


                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            loading.dismiss();
                                                                                            sql.sincronizarGasto(gastosModel.getId());
                                                                                            if (context instanceof gestionGastos) {
                                                                                                ((gestionGastos) context).loadDataInListView_habiles();
                                                                                            }




                                                                                        }
                                                                                    });

                                                                                    queue.add(stringRequest);


                                                                                }

                                                                            })
                                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    sql.volverAEstimar(gastosModel.getId());
                                                                                    Toast.makeText(context.getApplicationContext(), "El gasto volvio a ser estimado con exito.", Toast.LENGTH_SHORT).show();
                                                                                    if (context instanceof gestionGastos) {
                                                                                        ((gestionGastos) context).loadDataInListView_habiles();
                                                                                    }
                                                                                }

                                                                            })
                                                                            .setNeutralButton("Lo decidire despues", null)
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
                    if (tempArray.get(i).getChofer().toUpperCase().contains(constraint)) {
                        GastosModel um = new GastosModel(tempArray.get(i).getId(), tempArray.get(i).getViaje(), tempArray.get(i).getChofer(), tempArray.get(i).getCategoria(), tempArray.get(i).getUbicacionlat(), tempArray.get(i).getUbicacionlong(), tempArray.get(i).getFoto(), tempArray.get(i).getFecha(), tempArray.get(i).getImporte(), tempArray.get(i).getEstimacion(), tempArray.get(i).getSincronizacion());
                        filters.add(um);
                    } else if (tempArray.get(i).getViaje().toUpperCase().contains(constraint)) {
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
