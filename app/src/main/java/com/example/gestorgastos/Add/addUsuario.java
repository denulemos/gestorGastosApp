package com.example.gestorgastos.Add;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Gestores.gestionGastos;
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

public class addUsuario extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private TextInputLayout nombre, apellido, mail, contraseña, dni;
    private Switch esAdmin;
    private Spinner camion;
    private String camionNU;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SQLAdmin(this);
        setContentView(R.layout.activity_add_usuario);
        nombre = (TextInputLayout) findViewById(R.id.nombreNuevoConductor);
        apellido = (TextInputLayout) findViewById(R.id.apellidoNuevoConductor);
        mail = (TextInputLayout) findViewById(R.id.emailNuevoConductor);
        contraseña = (TextInputLayout) findViewById(R.id.contraseñaNuevoConductor);
        dni = (TextInputLayout) findViewById(R.id.dniNuevoConductor);
        camion = (Spinner) findViewById(R.id.camionNuevoConductor);
        esAdmin = (Switch) findViewById(R.id.esAdmin);

        camion.setOnItemSelectedListener(this);
        loadSpinnerData();
    }

    public void ocultarMostrarCamion(View v) {
        TextView textView14 = (TextView) findViewById(R.id.textView14);
        Boolean switchState = esAdmin.isChecked();
        if (switchState) {
            camion.setVisibility(View.GONE);
            textView14.setVisibility(View.GONE);
        } else {
            camion.setVisibility(View.VISIBLE);
            textView14.setVisibility(View.VISIBLE);
        }

    }
    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> camiones = new ArrayList<>();
        List<String> aux = db.getCamionesSinConductor();

        camiones.add("No asignar camion aun");

        if (aux.size() > 0) {
            for (String e : aux) {
                camiones.add(e);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, camiones);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        camion.setAdapter(dataAdapter);
    }

    public void volverAListaConductor(View view) {
        final Intent i = new Intent(this, listaConductores.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_usuario))
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

    public void registrarUsuarioConfirmacion(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.desea_registrar_a_este_usuario_nuevo) + dni.getEditText().getText().toString() + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registrarUsuario();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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

    private void registrarUsuario() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            final Intent i = new Intent(this, listaConductores.class);
            Boolean switchState = esAdmin.isChecked();
            SQLiteDatabase base = sql.getWritableDatabase();
            String nombreNU = nombre.getEditText().getText().toString();
            String apellidoNU = apellido.getEditText().getText().toString();
            String mailNU = mail.getEditText().getText().toString();
            String contraseñaNU = contraseña.getEditText().getText().toString();
            final String dniNU = dni.getEditText().getText().toString();

            int isAdmin = 0;

            if (!esVacio(nombreNU, apellidoNU, mailNU, contraseñaNU, dniNU) && (dniNU.length() == 8 || dniNU.length() == 7) && validarEmail(mailNU)) {

                if (switchState) {
                    isAdmin = 1;
                }
                if (sql.usuarioExiste(dniNU)) {
                    Toast.makeText(this, getString(R.string.usuario_existe), Toast.LENGTH_SHORT).show();
                } else {

                    ContentValues cv = new ContentValues();
                    cv.put("usuario_nombre", nombreNU);
                    cv.put("usuario_apellido", apellidoNU);
                    cv.put("usuario_dni", dniNU);
                    cv.put("usuario_contraseña", contraseñaNU);
                    cv.put("usuario_mail", mailNU);
                    //No es administrador ni se selecciono un camion vacio
                    if (isAdmin != 1) {
                        if (camionNU != null) {
                            cv.put("usuario_camion", camionNU);
                        }
                    }
                    cv.put("usuario_administrador", isAdmin);
                    cv.put("usuario_estado", 1);
                    cv.put("usuario_solicitud_contraseña", 0);

                    try {
                        base.insert("usuarios", null, cv);
                        Toast.makeText(this, getString(R.string.usuario_creado_con_exito), Toast.LENGTH_SHORT).show();

                        sql.asignarUsuarioaCamion(camionNU, dniNU);
                        base.close();
                        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("sheet", "Usuarios");
                            jsonObject.put("action", "add");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray = new JSONArray();


                        JSONArray row = new JSONArray();
                        row.put(sql.getUltimoUser());
                        row.put(nombreNU);
                        row.put(apellidoNU);
                        row.put(dniNU);
                        row.put(mailNU);
                        row.put("Activo");
                        if (isAdmin == 1) {
                            row.put("Administrador");
                        } else {
                            row.put("Conductor");
                        }


                        jsonArray.put(row);


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
                                        if (camionNU != null) {
                                            asignarCamionSheets(dniNU, camionNU);
                                        } else {

                                            startActivity(i);
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.dismiss();
                                if (camionNU != null) {
                                    asignarCamionSheets(dniNU, camionNU);
                                } else {

                                    startActivity(i);
                                }
                            }
                        });

                        queue.add(stringRequest);

                    } catch (Exception ex) {
                        base.close();
                        Toast.makeText(this, getString(R.string.errorUsuario), Toast.LENGTH_SHORT).show();
                    }


                }
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

    private void asignarCamionSheets(String dni, String camion) {
        final Intent i = new Intent(this, listaConductores.class);
        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "editarCamionChofer");
            jsonObject.put("id", sql.getIdCamion(camion));
            jsonObject.put("chofer", dni);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();

                        startActivity(i);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

                startActivity(i);

            }
        });

        queue.add(stringRequest);
    }

    private boolean esVacio(String nombre, String apellido, String mail, String contraseña, String dni) {
        boolean esVacio = false;
        if (nombre.isEmpty() || apellido.isEmpty() || mail.isEmpty() || contraseña.isEmpty() || dni.isEmpty()) {
            esVacio = true;
        }
        return esVacio;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String camiones = parent.getItemAtPosition(position).toString();
        if (camiones.equals("No asignar camion aun")) {
            camionNU = null;
        } else {
            camionNU = camiones;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(this, listaConductores.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_usuario))
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
}