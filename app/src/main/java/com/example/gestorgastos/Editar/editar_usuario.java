package com.example.gestorgastos.Editar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class editar_usuario extends AppCompatActivity {

    String nombre, apellido, contraseña, email, camion, dni;
    int esAdmin, id;
    private TextInputLayout nombreTI, apellidoTI, mailTI, contraseñaTI;
    private Switch esAdminSW;
    private Spinner camionSP;
    private String camionNU;
    TextView avisoViaje;

    private SQLAdmin sql;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        Bundle bundle = this.getIntent().getExtras();
        sql = new SQLAdmin(this);
        dni = bundle.getString("dni");
        id = bundle.getInt("id");
        nombre = bundle.getString("nombre");
        apellido = bundle.getString("apellido");
        email = bundle.getString("email");
        esAdmin = bundle.getInt("esAdmin");
        camion = bundle.getString("camion");
        nombreTI = (TextInputLayout) findViewById(R.id.nombreEditarConductor);
        apellidoTI = (TextInputLayout) findViewById(R.id.apellidoEditarConductor);
        mailTI = (TextInputLayout) findViewById(R.id.emailEditarConductor);
        esAdminSW = (Switch) findViewById(R.id.esAdminEditar);
        esAdminSW.setChecked(esAdmin == 1);


        nombreTI.getEditText().setText(nombre);
        apellidoTI.getEditText().setText(apellido);
        mailTI.getEditText().setText(email);


        avisoViaje = (TextView) findViewById(R.id.avisoViaje);
        avisoViaje.setVisibility(View.GONE);


        ocultarMostrarCamion();


    }


    public void ocultarMostrarCamion() {
        TextView textView14 = (TextView) findViewById(R.id.textView14);
        Boolean switchState = esAdminSW.isChecked();
        Boolean viajeCurso = sql.elUsuarioEstaEnViaje(dni);

        if (switchState || viajeCurso) {
            if (viajeCurso) {
                avisoViaje.setVisibility(View.VISIBLE);
                esAdminSW.setVisibility(View.GONE);
            }
        }

    }

    private boolean validarEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    public void editarUsuarioConfirmacion(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.guardar_cambios_usuario))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editarUsuario();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void editarUsuario() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Boolean switchState = esAdminSW.isChecked();
            final Intent i = new Intent(this, listaConductores.class);
            String nombreNU = nombreTI.getEditText().getText().toString();
            String apellidoNU = apellidoTI.getEditText().getText().toString();
            String mailNU = mailTI.getEditText().getText().toString();
            final int isAdmin;

            if (nombreNU.length() > 0 && apellidoNU.length() > 0 && mailNU.length() > 0 && validarEmail(mailNU) && (dni.length() == 8 || dni.length() == 7)) {
                if (switchState) {
                    isAdmin = 1;

                } else {
                    isAdmin = 0;
                }

                sql.editarUsuario(nombreNU, apellidoNU, mailNU, isAdmin, dni);

                final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("action", "editarUsuario");
                    jsonObject.put("nombre", nombreNU);
                    jsonObject.put("apellido", apellidoNU);
                    jsonObject.put("email", mailNU);
                    if (isAdmin == 1) {
                        jsonObject.put("rol", "Administrador");

                    } else {
                        jsonObject.put("rol", "Conductor");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                                if (isAdmin == 1 && camion != null) {
                                    if (camion != null) {
                                        actualizarCamionANull(sql.getIdCamion(camion));
                                    }
                                    ArrayList<Integer> viajes = sql.getViajesUsuario(dni);
                                    if (viajes.size() > 0) {
                                        desasignarViajes(dni);
                                    }

                                }

                                loading.dismiss();
                                startActivity(i);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        if (isAdmin == 1 && camion != null) {
                            if (camion != null) {
                                actualizarCamionANull(sql.getIdCamion(camion));
                            }

                            ArrayList<Integer> viajes = sql.getViajesUsuario(dni);
                            if (viajes.size() > 0) {
                                desasignarViajes(dni);
                            }
                        }

                        loading.dismiss();
                        startActivity(i);
                    }
                });

                queue.add(stringRequest);


            } else {
                Toast.makeText(this, getString(R.string.ingresar_info_valida), Toast.LENGTH_SHORT).show();
            }



        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para realizar esta accion")
                    .setNegativeButton("Ok", null)
                    .show();
        }


    }


    public void volverAListaConductor(View view) {
        final Intent i = new Intent(this, listaConductores.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_editar_usuario))
                .setMessage(getString(R.string.los_datos_guardados_se_perderan))
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(i);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void desasignarViajes(String dni) {
        ArrayList<Integer> viajes = sql.getViajesUsuario(dni);
        sql.deshabilitarViajesDeUser(dni);
        final ProgressDialog loading = ProgressDialog.show(this, "Actualizando", "Espere un momento");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "desasignarViajes");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();

        for (int i : viajes) {
            JSONArray row = new JSONArray();
            row.put(i + 1);
            jsonArray.put(row);
        }


        try {
            jsonObject.put("rows", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this);
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

    private void actualizarCamionANull(int id) {
        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("action", "editarCamionChofer");
            jsonObject.put("chofer", "Disponible");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this);
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


}