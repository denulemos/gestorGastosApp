package com.example.gestorgastos.Editar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.Listas.listaViajes;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class editar_viaje_encamino extends AppCompatActivity {
    String destino;
    int id;
    private TextInputLayout destinoTI;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_viaje_encamino);
        Bundle bundle = this.getIntent().getExtras();
        sql = new SQLAdmin(this);
        id = bundle.getInt("id");
        destino = bundle.getString("destino");

        destinoTI = (TextInputLayout) findViewById(R.id.destinoViaje);
        destinoTI.getEditText().setText(destino);
    }

    public void editarViajeConfirmacion(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.guardar_datos_viaje))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editarViaje();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void editarViaje() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            if (destinoTI.getEditText().length() > 0) {
                final Intent i = new Intent(this, listaViajes.class);
                String destinoNU = destinoTI.getEditText().getText().toString();
                sql.editarViajeEnCamino(destinoNU, id);

                final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("action", "editarViajeCamino");
                    jsonObject.put("destino", destinoNU);
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

            } else {
                Toast.makeText(this, getString(R.string.completar_campos), Toast.LENGTH_SHORT).show();
            }


        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para realizar esta accion")
                    .setNegativeButton("Ok", null)
                    .show();
        }


    }

    //Volver al menu principal
    public void volverAlMenu(View view) {
        Intent i = new Intent(this, listaViajes.class);
        startActivity(i);
    }
}