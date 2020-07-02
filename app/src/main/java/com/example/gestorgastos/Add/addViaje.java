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
import com.example.gestorgastos.Listas.listaViajes;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class addViaje extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private SQLAdmin sql;
    private TextInputLayout origen, destino;
    private Spinner chofer;
    private String choferNV;
    private TextView indicadorChofer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_viaje);
        sql = new SQLAdmin(this);
        origen = (TextInputLayout) findViewById(R.id.origenViaje);
        destino = (TextInputLayout) findViewById(R.id.destinoViaje);
        indicadorChofer = (TextView)findViewById(R.id.indicadorChofer);
        chofer = (Spinner) findViewById(R.id.choferViaje);
        chofer.setOnItemSelectedListener(this);
        loadSpinnerData();
    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> choferes = db.getChoferesdni();

        indicadorChofer.setVisibility(View.GONE);
        choferes.add("No asignar chofer");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choferes);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chofer.setAdapter(dataAdapter);
    }

    public void volverAlMenu(View view) {
        final Intent i = new Intent(this, listaViajes.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_viaje))
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        choferNV = parent.getItemAtPosition(position).toString();
        if (choferNV.equals("No asignar chofer")){
            indicadorChofer.setVisibility(View.GONE);
        }
        else{
            indicadorChofer.setVisibility(View.VISIBLE);
            indicadorChofer.setText(getString(R.string.chofer_seleccionado_es) + sql.getNombreUsuario(choferNV));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void agregarViajeConfirmacion(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.aviso)
                .setMessage(getString(R.string.alta_nuevo_viaje))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agregarViajeNuevo();
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

    private void agregarViajeNuevo() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            SQLiteDatabase base = sql.getWritableDatabase();
            final Intent i = new Intent(this, listaViajes.class);

            String origenNV = origen.getEditText().getText().toString();
            String destinoNV = destino.getEditText().getText().toString();
            if (!origenNV.isEmpty() && !destinoNV.isEmpty()) {

                ContentValues cv = new ContentValues();
                if (!choferNV.equals("No asignar chofer")) {
                    cv.put("viajes_chofer", choferNV);
                }
                cv.put("viaje_origen", origenNV);
                cv.put("viaje_destino", destinoNV);
                cv.put("viaje_estado", "Planeado");
                cv.put("viaje_monto_total", 0 );

                base.insert("viajes", null, cv);

                int id = sql.getUltimoViaje();
                base.close();

                Toast.makeText(this, getString(R.string.agregarViaje), Toast.LENGTH_SHORT).show();

                final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sheet", "Viajes");
                    jsonObject.put("action", "add");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = new JSONArray();


                JSONArray row = new JSONArray();
                row.put(id);
                if (!choferNV.equals("No asignar chofer")) {
                    row.put(String.valueOf(choferNV));
                } else {
                    row.put("Disponible");
                }

                row.put(origenNV);
                row.put(destinoNV);
                row.put("Planeado");
                row.put(0);


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
        final Intent i = new Intent(this, listaViajes.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_viaje))
                .setMessage(getString(R.string.los_datos_guardados_se_perderan))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(i);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();

    }
}