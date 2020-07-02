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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Gestores.gestionCamiones;
import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class addCamion extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private TextInputLayout patente, activo;
    private Spinner chofer;
    private String choferNU;
    private TextView indicadorChofer;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camion);
        sql = new SQLAdmin(this);
        patente = (TextInputLayout) findViewById(R.id.patenteCamion);
        activo = (TextInputLayout) findViewById(R.id.activoCamion);
        indicadorChofer = (TextView) findViewById(R.id.indicadorChofer);
        chofer = (Spinner) findViewById(R.id.choferCamion);
        chofer.setOnItemSelectedListener(this);
        loadSpinnerData();


    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private boolean validarPatente(String patente) {
        String patenteVieja = "[a-z]{3}[\\d]{3}";
        String patenteNueva = "[a-z]{2}[\\d]{2}[a-z]{2}";

        Pattern pat = Pattern.compile(patenteVieja);
        Pattern pat2 = Pattern.compile(patenteNueva);
        if (patente == null)
            return false;
        return (pat.matcher(patente).matches() || pat2.matcher(patente).matches());
    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> choferes = new ArrayList<>();
        List<String> aux = db.getUsuariosSinCamionAsignado();
        choferes.add(getString(R.string.no_asignar_chofer_aun));
        indicadorChofer.setVisibility(View.GONE);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        String choferes = parent.getItemAtPosition(position).toString();
        choferNU = choferes;
        if (choferes.equals(getString(R.string.no_asignar_chofer_aun))) {
            indicadorChofer.setVisibility(View.GONE);
        } else {
            choferNU = choferes;
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es) + sql.getNombreUsuario(choferes));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        choferNU = "";
    }

    //Volver al menu principal
    public void volverListaCamiones(View view) {

        final Intent i = new Intent(this, gestionCamiones.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.esta_creando_camion)
                .setMessage(R.string.los_datos_guardados_se_perderan)
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(i);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void confirmarAgregarCamion(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.esta_creando_camion)
                .setMessage(R.string.confirma_creacion_camion)
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            agregarCamion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void agregarCamion() throws JSONException {


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            final Intent i = new Intent(this, gestionCamiones.class);
            SQLiteDatabase base = sql.getWritableDatabase();
            String patenteNU = patente.getEditText().getText().toString();
            String activoNU = activo.getEditText().getText().toString();

            if (!patenteNU.isEmpty() && !activoNU.isEmpty() && validarPatente(patenteNU)) {
                if (sql.camionExiste(patenteNU)) {
                    Toast.makeText(this, getString(R.string.patente_ya_existe), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("camion_patente", patenteNU.toUpperCase());
                    cv.put("camion_activo", activoNU);
                    if (!choferNU.equals("No asignar chofer aun")) {
                        cv.put("camion_chofer", choferNU);
                    }
                    base.insert("camiones", null, cv);
                    CamionModel cm = sql.getUltimoCamion();
                    base.close();
                    sql.asignarCamionUsuario(choferNU, patenteNU);
                    Toast.makeText(this, getString(R.string.camion_agregado_exito), Toast.LENGTH_SHORT).show();
                    final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("action", "addCamion");
                        jsonObject.put("id", cm.getId());
                        jsonObject.put("patente", patenteNU.toUpperCase());
                        jsonObject.put("activo", "activoNU");
                        if (!choferNU.equals(getString(R.string.no_asignar_chofer_aun))) {
                            jsonObject.put("chofer", choferNU);
                        } else {
                            jsonObject.put("chofer", "Disponible");
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

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(this, gestionCamiones.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_camion))
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