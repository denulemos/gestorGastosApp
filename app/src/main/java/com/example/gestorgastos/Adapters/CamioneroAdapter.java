package com.example.gestorgastos.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowId;
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
import com.example.gestorgastos.Editar.editar_usuario;
import com.example.gestorgastos.Gestores.gestionCamiones;
import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.Listas.viajes_propios;
import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.SendMail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CamioneroAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UsuariosModel> arrayList, tempArray;
    private Activity parent;
    private CustomFilter cs;
    private android.app.AlertDialog alertDialog;

    public CamioneroAdapter(Context context, ArrayList<UsuariosModel> arrayList, Activity parent) {
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
        convertView = inf.inflate(R.layout.conductorlv, null);


        TextView nombre = (TextView) convertView.findViewById(R.id.nombreConductor);
        TextView dni = (TextView) convertView.findViewById(R.id.dniConductor);
        TextView email = (TextView) convertView.findViewById(R.id.emailConductor);
        TextView camion = (TextView) convertView.findViewById(R.id.camionConductor);
        TextView estado = (TextView) convertView.findViewById(R.id.estadoConductor);


        final UsuariosModel usuariosModel = arrayList.get(position);
        nombre.setText(usuariosModel.getNombre() + " , " + usuariosModel.getApellido());
        dni.setText("Dni: " + usuariosModel.getDni());

        if (usuariosModel.getAdmin() == 1) {
            camion.setText("Administrador");
            camion.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            camion.setVisibility(View.GONE);
        }

        email.setText(usuariosModel.getMail());
        if (usuariosModel.getEstado() == 1) {
            estado.setText("Activo");
        } else {
            estado.setText("Inhabilitado");
        }



        final listaConductores lc = new listaConductores();

        final SQLAdmin sql = new SQLAdmin(context.getApplicationContext());



        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ListView listView = new ListView(parent.getContext());
                android.app.AlertDialog.Builder builder = new
                        android.app.AlertDialog.Builder(parent.getContext());
                String[] items;

                if (usuariosModel.getEstado() == 1) {

                    items = new String[]{"Editar", "Deshabilitar"};
                } else {

                    items = new String[]{ "Habilitar"};
                }

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


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                        R.layout.list_item, R.id.txtitem, items);
                listView.setAdapter(adapter);


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
                                                                            .setMessage("¿Desea editar al usuario DNI " + usuariosModel.getDni().toString() + "?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    Intent i = new Intent(parent.getContext(), editar_usuario.class);
                                                                                    i.putExtra("id", usuariosModel.getId());
                                                                                    i.putExtra("nombre", usuariosModel.getNombre());
                                                                                    i.putExtra("apellido", usuariosModel.getApellido());
                                                                                    i.putExtra("contraseña", usuariosModel.getContraseña());
                                                                                    i.putExtra("email", usuariosModel.getMail());
                                                                                    i.putExtra("camion", usuariosModel.getCamion());
                                                                                    i.putExtra("esAdmin", usuariosModel.getAdmin());
                                                                                    i.putExtra("dni", usuariosModel.getDni());
                                                                                    parent.getContext().startActivity(i);
                                                                                }


                                                                            })
                                                                            .setNegativeButton("No", null)
                                                                            .show();
                                                                } else if (txt.getText().equals("Deshabilitar")) {
                                                                    if (usuariosModel.getDni().equals("00000000")) {
                                                                        new AlertDialog.Builder(parent.getContext())
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .setTitle("Este usuario no se puede inhabilitar")
                                                                                .setNegativeButton("Ok", null)
                                                                                .show();
                                                                    } else if (sql.elUsuarioEstaEnViaje(usuariosModel.getDni())) {
                                                                        new AlertDialog.Builder(parent.getContext())
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .setTitle("Este usuario posee un viaje en curso. No puede ser inhabilitado")
                                                                                .setNegativeButton("Ok", null)
                                                                                .show();
                                                                    } else {
                                                                        new AlertDialog.Builder(parent.getContext())
                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                .setTitle("Aviso")
                                                                                .setMessage("¿Desea inhabilitar al usuario " + usuariosModel.getNombre() + " " + usuariosModel.getApellido() + " ?")
                                                                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        sql.inhabilitarUsuario(usuariosModel.getDni());
                                                                                        ArrayList<Integer> viajes = sql.getViajesUsuario(usuariosModel.getDni());
                                                                                        if (viajes.size() > 0) {
                                                                                            sql.deshabilitarViajesDeUser(usuariosModel.getDni());
                                                                                            final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Actualizando", "Espere un momento");
                                                                                            JSONObject jsonObject = new JSONObject();
                                                                                            try {
                                                                                                jsonObject.put("action", "desasignarViajes");
                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                            JSONArray jsonArray = new JSONArray();

                                                                                            for (int i : viajes) {
                                                                                                JSONArray row = new JSONArray();
                                                                                                row.put(i+1);
                                                                                                jsonArray.put(row);
                                                                                            }


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


                                                                                                        }
                                                                                                    }, new Response.ErrorListener() {
                                                                                                @Override
                                                                                                public void onErrorResponse(VolleyError error) {
                                                                                                    loading.dismiss();


                                                                                                }
                                                                                            });

                                                                                            queue.add(stringRequest);
                                                                                        }
                                                                                        if (usuariosModel.getCamion() != null) {
                                                                                            sql.desasignarCamion(usuariosModel.getDni());
                                                                                            final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Actualizando", "Espere un momento");
                                                                                            JSONObject jsonObject = new JSONObject();
                                                                                            try {
                                                                                                jsonObject.put("id", sql.getIdCamion(usuariosModel.getCamion()));
                                                                                                jsonObject.put("action", "editarCamionChofer");
                                                                                                jsonObject.put("chofer", "Disponible");
                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                            }


                                                                                            RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                                    new Response.Listener<JSONObject>() {
                                                                                                        @Override
                                                                                                        public void onResponse(JSONObject response) {

                                                                                                            loading.dismiss();


                                                                                                        }
                                                                                                    }, new Response.ErrorListener() {
                                                                                                @Override
                                                                                                public void onErrorResponse(VolleyError error) {
                                                                                                    loading.dismiss();


                                                                                                }
                                                                                            });

                                                                                            queue.add(stringRequest);
                                                                                        }

                                                                                        final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Actualizando", "Espere un momento");
                                                                                        JSONObject jsonObject = new JSONObject();
                                                                                        try {
                                                                                            jsonObject.put("id", usuariosModel.getId());
                                                                                            jsonObject.put("action", "deshabilitarUsuario");
                                                                                        } catch (JSONException e) {
                                                                                            e.printStackTrace();
                                                                                        }


                                                                                        RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {

                                                                                                        loading.dismiss();

                                                                                                        if (context instanceof listaConductores) {
                                                                                                            ((listaConductores) context).loadDataInListView_habilitados();
                                                                                                        }


                                                                                                    }
                                                                                                }, new Response.ErrorListener() {
                                                                                            @Override
                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                loading.dismiss();


                                                                                                if (context instanceof listaConductores) {
                                                                                                    ((listaConductores) context).loadDataInListView_habilitados();
                                                                                                }
                                                                                            }
                                                                                        });

                                                                                        queue.add(stringRequest);


                                                                                    }

                                                                                })
                                                                                .setNegativeButton("No", null)
                                                                                .show();
                                                                    }
                                                                } else if (txt.getText().equals("Habilitar")) {
                                                                    new AlertDialog.Builder(parent.getContext())
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .setTitle("Aviso")
                                                                            .setMessage("¿Desea volver al habilitar al usuario " + usuariosModel.getNombre() + " " + usuariosModel.getApellido() + " ?")
                                                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    sql.habilitarUsuario(usuariosModel.getDni());

                                                                                    final ProgressDialog loading = ProgressDialog.show(parent.getContext(), "Actualizando", "Espere un momento");
                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("id", usuariosModel.getId());
                                                                                        jsonObject.put("action", "habilitarUsuario");
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }


                                                                                    RequestQueue queue = Volley.newRequestQueue(parent.getContext());
                                                                                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {
                                                                                                    loading.dismiss();
                                                                                                    Toast.makeText(parent.getContext(), "Usuario Habilitado", Toast.LENGTH_SHORT).show();
                                                                                                    if (context instanceof listaConductores) {
                                                                                                        ((listaConductores) context).loadDataInListView_habilitados();
                                                                                                    }


                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            loading.dismiss();
                                                                                            Toast.makeText(parent.getContext(), "Usuario Habilitado", Toast.LENGTH_SHORT).show();
                                                                                            if (context instanceof listaConductores) {
                                                                                                ((listaConductores) context).loadDataInListView_habilitados();
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


                ArrayList<UsuariosModel> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).getNombre().toUpperCase().contains(constraint)) {
                        UsuariosModel um = new UsuariosModel(tempArray.get(i).getId(), tempArray.get(i).getDni(), tempArray.get(i).getContraseña(), tempArray.get(i).getNombre(), tempArray.get(i).getApellido(), tempArray.get(i).getMail(), tempArray.get(i).getCamion(), tempArray.get(i).getAdmin(), tempArray.get(i).getSolicitud(), tempArray.get(i).getEstado());
                        filters.add(um);
                    } else if (tempArray.get(i).getApellido().toUpperCase().contains(constraint)) {
                        UsuariosModel um = new UsuariosModel(tempArray.get(i).getId(), tempArray.get(i).getDni(), tempArray.get(i).getContraseña(), tempArray.get(i).getNombre(), tempArray.get(i).getApellido(), tempArray.get(i).getMail(), tempArray.get(i).getCamion(), tempArray.get(i).getAdmin(), tempArray.get(i).getSolicitud(), tempArray.get(i).getEstado());
                        filters.add(um);
                    } else if (tempArray.get(i).getDni().toUpperCase().contains(constraint)) {
                        UsuariosModel um = new UsuariosModel(tempArray.get(i).getId(), tempArray.get(i).getDni(), tempArray.get(i).getContraseña(), tempArray.get(i).getNombre(), tempArray.get(i).getApellido(), tempArray.get(i).getMail(), tempArray.get(i).getCamion(), tempArray.get(i).getAdmin(), tempArray.get(i).getSolicitud(), tempArray.get(i).getEstado());
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
            arrayList = (ArrayList<UsuariosModel>) results.values;
            notifyDataSetChanged();

        }
    }
}
