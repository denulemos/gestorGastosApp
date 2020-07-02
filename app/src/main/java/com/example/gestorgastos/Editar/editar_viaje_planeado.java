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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Listas.listaViajes;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class editar_viaje_planeado extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private SQLAdmin sql;
    int id;
    private TextInputLayout destinoTI, origenTI;
    private Spinner choferSP;
    String destino, origen, chofer;
    private TextView indicadorChofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_viaje_planeado2);
        Bundle bundle = this.getIntent().getExtras();
        sql = new SQLAdmin(this);
        destino = bundle.getString("destino");
        origen = bundle.getString("origen");
        chofer = bundle.getString("chofer");
        id = bundle.getInt("id");

        indicadorChofer = (TextView) findViewById(R.id.indicadorChofer);
        destinoTI = (TextInputLayout) findViewById(R.id.destinoViaje);
        destinoTI.getEditText().setText(destino);
        origenTI = (TextInputLayout) findViewById(R.id.origenViaje);
        origenTI.getEditText().setText(origen);

        choferSP = (Spinner) findViewById(R.id.choferViaje);
        choferSP.setOnItemSelectedListener(this);
        loadSpinnerData();


    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> choferes = db.getChoferesdni();


        if (!chofer.equals("")) {
            choferes.add(chofer);
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es) + sql.getNombreUsuario(chofer));

        } else {
            indicadorChofer.setVisibility(View.GONE);
        }

        choferes.add("Sin chofer asignado");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choferes);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        choferSP.setAdapter(dataAdapter);
    }

    public void volverAlMenu(View view) {
        final Intent i = new Intent(this, listaViajes.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_edicion_viaje))
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


    public void editarViajeConfirmacion(View v) {
        final Intent i = new Intent(this, listaViajes.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.guardar_datos_viaje))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editarViaje();
                        startActivity(i);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chofer = parent.getItemAtPosition(position).toString();
        if (chofer.equals("Sin chofer asignado")) {
            indicadorChofer.setVisibility(View.GONE);
        } else {
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es) + sql.getNombreUsuario(chofer));
        }
    }

    private void editarViaje() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (destinoTI.getEditText().length() > 0 && origenTI.getEditText().length() > 0) {
                String destinoNU = destinoTI.getEditText().getText().toString();
                String origenNU = origenTI.getEditText().getText().toString();
                final Intent i = new Intent(this, listaViajes.class);
                if (chofer.equals("Sin chofer asignado")) {
                    chofer = null;
                }

                sql.editarViajePlaneado(destinoNU, origenNU, chofer, id);

                final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("action", "editarViajePlan");
                    jsonObject.put("destino", destinoNU);
                    jsonObject.put("origen", origenNU);
                    if (chofer == null) {
                        jsonObject.put("chofer", "Disponible");
                    } else {
                        jsonObject.put("chofer", chofer);
                    }

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

                startActivity(i);
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}