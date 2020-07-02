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
import com.example.gestorgastos.Gestores.gestionCamiones;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class editar_camion extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private TextInputLayout activo;
    private String patenteB, activoB, choferB;
    private Spinner chofer;
    private int id;
    private String choferNU;
    private TextView avisoCamion, indicadorChofer;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_camion);
        Bundle bundle = this.getIntent().getExtras();
        sql = new SQLAdmin(this);
        activo = (TextInputLayout) findViewById(R.id.activoCamion);
        indicadorChofer = (TextView)findViewById(R.id.indicadorChofer);
        chofer = (Spinner) findViewById(R.id.choferCamion);
        id = bundle.getInt("id");
        activoB = bundle.getString("activo");
        patenteB = bundle.getString("patente");
        choferB = bundle.getString("chofer");
        activo.getEditText().setText(activoB);
        avisoCamion=(TextView)findViewById(R.id.avisoCamion) ;
        avisoCamion.setText("Se esta editando el camion " + patenteB);
        chofer.setOnItemSelectedListener(this);


        loadSpinnerData();
    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> choferes = new ArrayList<>();

        List<String> aux = db.getUsuariosSinCamionAsignado();
        if (choferB != null) {
            choferes.add(choferB);
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es)+  sql.getNombreUsuario(choferB));
        }
        else{
            indicadorChofer.setVisibility(View.GONE);
        }
        choferes.add("No asignar chofer");
        if (aux.size() > 0) {
            for (String c : aux) {
                choferes.add(c);
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choferes);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chofer.setAdapter(dataAdapter);
    }

    public void editarCamionConfirmacion(View v) {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.guardar_cambios_camion))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        editarCamion();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void editarCamion() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            final Intent i = new Intent(this, gestionCamiones.class);
            String activoF = activo.getEditText().getText().toString();
            if(activoF.trim().length() != 0){
                sql.editarCamion(patenteB, activoF, choferNU);

                final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id++);
                    jsonObject.put("action", "editarCamion");
                    jsonObject.put("activo", activoF);
                    jsonObject.put("patente", patenteB);
                    if (choferNU == null){
                        jsonObject.put("chofer" , "Disponible");
                    }
                    else{
                        jsonObject.put("chofer" , choferNU);
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
            }else{
                Toast.makeText(this, getString(R.string.descripcion_vacia), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(this, gestionCamiones.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_edicion_camion))
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

    //Volver al menu principal
    public void volverListaCamiones(View view) {

        final Intent i = new Intent(this, gestionCamiones.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_edicion_camion))
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

    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choferes = parent.getItemAtPosition(position).toString();
        if (choferes.equals("No asignar chofer")) {
            choferNU = null;
            indicadorChofer.setVisibility(View.GONE);
        } else {
            choferNU = choferes;
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es) +  sql.getNombreUsuario(choferes));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}