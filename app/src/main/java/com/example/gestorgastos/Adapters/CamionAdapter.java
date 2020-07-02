package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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
import com.example.gestorgastos.Editar.editar_camion;
import com.example.gestorgastos.Editar.editar_viaje_planeado;
import com.example.gestorgastos.Gestores.gestionCamiones;
import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CamionAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CamionModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog alertDialog;

    public CamionAdapter(Context context, ArrayList<CamionModel> arrayList, Activity parent) {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inf.inflate(R.layout.camionlv, null);
        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());
        TextView patente = (TextView) convertView.findViewById(R.id.patenteCamion);
        TextView activo = (TextView) convertView.findViewById(R.id.activoCamion);
        TextView chofer = (TextView) convertView.findViewById(R.id.choferCamion);

        final ListView listView = new ListView(parent.getContext());


        final CamionModel camionModel = arrayList.get(position);

        patente.setText("Patente: " + camionModel.getPatente());
        activo.setText(camionModel.getActivo());

        if (camionModel.getChofer() == null) {
            chofer.setText("Camion Disponible");
        } else {
            chofer.setText(camionModel.getChofer());
        }


        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                android.app.AlertDialog.Builder builder = new
                        android.app.AlertDialog.Builder(parent.getContext());

                builder.setCancelable(true);

                builder.setPositiveButton("Salir", null);
                final ListView listView = new ListView(parent.getContext());
                String[] items = new String[]{"Eliminar", "Editar"};


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);

                listView.setAdapter(adapter);

                builder.setView(listView);

                alertDialog = builder.create();

                ConnectivityManager connectivityManager = (ConnectivityManager) parent.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    if (!sql.elCamionEstaEnViaje(camionModel.getPatente())) {
                        alertDialog.show();
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

                                                                if (txt.getText().equals("Eliminar")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("¡Atencion!")
                                                                            .setMessage("¿Esta seguro que desea eliminar a este camion " + camionModel.getPatente() + " ?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                    sql.eliminarCamion(camionModel.getPatente());
                                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Sincronizando Informacion", "Espere un momento");
                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("id", camionModel.getId());
                                                                                        jsonObject.put("action", "editarCamion");
                                                                                        jsonObject.put("activo", "CAMION FUERA DE INVENTARIO");
                                                                                        jsonObject.put("patente", camionModel.getPatente());
                                                                                       jsonObject.put("chofer" , "CAMION FUERA DE INVENTARIO");


                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }


                                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {

                                                                                                    loading.dismiss();

                                                                                                    Toast.makeText(context.getApplicationContext(), "Camion eliminado", Toast.LENGTH_SHORT).show();
                                                                                                    if (context instanceof gestionCamiones) {
                                                                                                        ((gestionCamiones) context).loadDataInListView_Disponibles();
                                                                                                    }



                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            loading.dismiss();
                                                                                            Toast.makeText(context.getApplicationContext(), "Camion eliminado", Toast.LENGTH_SHORT).show();
                                                                                            if (context instanceof gestionCamiones) {
                                                                                                ((gestionCamiones) context).loadDataInListView_Disponibles();
                                                                                            }

                                                                                        }
                                                                                    });

                                                                                    queue.add(stringRequest);


                                                                                }

                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .show();
                                                                } else if (txt.getText().equals("Editar")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("Aviso")
                                                                            .setMessage("¿Desea editar este camion?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    Intent i = new Intent(parent.getContext(), editar_camion.class);
                                                                                    i.putExtra("id", camionModel.getId());
                                                                                    i.putExtra("patente", camionModel.getPatente());
                                                                                    i.putExtra("activo", camionModel.getActivo());
                                                                                    i.putExtra("chofer", camionModel.getChofer());
                                                                                    parent.getContext().startActivity(i);
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


                ArrayList<CamionModel> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).getPatente().toUpperCase().contains(constraint)) {
                        CamionModel cm = new CamionModel(tempArray.get(i).getId(), tempArray.get(i).getPatente(), tempArray.get(i).getActivo(), tempArray.get(i).getChofer());
                        filters.add(cm);
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
            arrayList = (ArrayList<CamionModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
