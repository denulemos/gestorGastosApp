package com.example.gestorgastos.Gestores;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Adapters.GastosAdminAdapter;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.SQL.SQLAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class gestionGastos extends AppCompatActivity implements TextWatcher, PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    private SQLAdmin sql;
    private ArrayList<GastosModel> gastos;
    private EditText filtrar;
    private TextView noHayAviso, indicador;
    private ImageButton volverMenu;
    private ListView listGastos;
    private GastosAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_gastos_admin);
        filtrar = (EditText) findViewById(R.id.search);
        noHayAviso = (TextView) findViewById(R.id.noHayAviso);
        filtrar.addTextChangedListener(this);
        sql = new SQLAdmin(this);
        indicador = (TextView) findViewById(R.id.indicador);
        listGastos = (ListView) findViewById(R.id.listGastos);
        volverMenu = (ImageButton) findViewById(R.id.volverMenu);
        volverMenu.setOnClickListener(this);

        loadDataInListView_habiles();
    }

    public void loadDataInListView_habiles() {
        gastos = sql.getGastos_habiles();
        indicador.setText(R.string.gastos_pendientes_de_sincronizacion);
        if (gastos.size() > 0) {
            filtrar.setEnabled(true);
            listGastos.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new GastosAdminAdapter(this, gastos, gestionGastos.this);
            listGastos.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            listGastos.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(R.string.no_se_encontraron_gastos_en_la_busqueda);
        }


    }

    public void loadDataInListView_pendientes() {
        indicador.setText(R.string.gastos_pendientes_de_revision);
        gastos = sql.getGastos_pendientes();
        if (gastos.size() > 0) {
            filtrar.setEnabled(true);
            listGastos.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new GastosAdminAdapter(this, gastos, gestionGastos.this);
            listGastos.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            listGastos.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(R.string.no_se_encontraron_gastos_en_la_busqueda);
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

    public void verMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_gastos_admin);
        popupMenu.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gastos_admin, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, adminScreen.class);
        startActivity(i);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gastosHabiles:
                loadDataInListView_habiles();
                return true;
            case R.id.gastosPendientes:
                loadDataInListView_pendientes();
                return true;

            default:
                return false;
        }


    }


    private void addItemToSheet() throws JSONException {

        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
        long millis = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(millis);
        String fecha = date.toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sheet", "Gastos");
        JSONArray jsonArray = new JSONArray();
        ArrayList<GastosModel> gastos = sql.getAllGastos();
        for (GastosModel gm : gastos) {
            JSONArray row = new JSONArray();
            row.put(gm.getId());
            row.put(gm.getViaje());
            row.put(gm.getChofer());
            row.put(gm.getCategoria());
            row.put(gm.getUbicacionlong() + " , " + gm.getUbicacionlat());
            row.put(gm.getFecha().trim());
            row.put(gm.getImporte());
            row.put(gm.getEstimacion());
            row.put(fecha.trim());
            jsonArray.put(row);
        }

        jsonObject.put("rows", jsonArray);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loading.dismiss();
                        Toast.makeText(gestionGastos.this, response.toString(), Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

            }
        });

        queue.add(stringRequest);


    }


    @Override
    public void onClick(View v) {


        if (v == volverMenu) {
            Intent i = new Intent(this, adminScreen.class);
            startActivity(i);
        }
    }
}