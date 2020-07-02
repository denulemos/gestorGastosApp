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
import android.widget.Button;
import android.widget.EditText;
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
import com.example.gestorgastos.Adapters.CamionAdapter;
import com.example.gestorgastos.Modelos.CamionModel;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Add.addCamion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class gestionCamiones extends AppCompatActivity implements TextWatcher, PopupMenu.OnMenuItemClickListener {
    private ListView listCamiones;
    private EditText filtrar;
    private TextView aviso_no_hay, indicador;
    private ArrayList<CamionModel> camiones;
    private SQLAdmin sql;
    private CamionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_camiones);
        listCamiones = (ListView) findViewById(R.id.listaCamiones);
        camiones = new ArrayList<>();
        indicador = (TextView) findViewById(R.id.indicador);
        filtrar = (EditText) findViewById(R.id.buscadorCamiones);
        aviso_no_hay = (TextView) findViewById(R.id.aviso_no_hay);
        filtrar.addTextChangedListener(this);
        sql = new SQLAdmin(this);
        loadDataInListView_Ocupados();
    }

    public void verMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_camiones);
        popupMenu.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_camiones, menu);
        return true;
    }

    public void loadDataInListView_Ocupados() {
        camiones = sql.getAllCamiones_Ocupados();
        indicador.setText(getString(R.string.camiones_ocupados));
        if (camiones.size() > 0) {
            filtrar.setEnabled(true);
            aviso_no_hay.setVisibility(View.GONE);
            listCamiones.setVisibility(View.VISIBLE);
            adapter = new CamionAdapter(this, camiones, this);
            listCamiones.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            aviso_no_hay.setVisibility(View.VISIBLE);
            aviso_no_hay.setText(R.string.no_se_encontraron_camiones);
            listCamiones.setVisibility(View.GONE);
        }
    }

    public void loadDataInListView_Disponibles() {
        camiones = sql.getAllCamiones_Disponibles();
        indicador.setText(getString(R.string.camiones_diponibles));
        if (camiones.size() > 0) {
            filtrar.setEnabled(true);
            aviso_no_hay.setVisibility(View.GONE);
            listCamiones.setVisibility(View.VISIBLE);
            adapter = new CamionAdapter(this, camiones, this);
            listCamiones.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            aviso_no_hay.setVisibility(View.VISIBLE);
            aviso_no_hay.setText(R.string.no_se_encontraron_camiones);
            listCamiones.setVisibility(View.GONE);
        }
    }

    public void volver(View view) {
        Intent i = new Intent(this, adminScreen.class);
        startActivity(i);
    }

    public void agregarCamiones(View view) {
        Intent i = new Intent(this, addCamion.class);
        startActivity(i);
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
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camionesOcupados:
                loadDataInListView_Ocupados();
                return true;
            case R.id.camionesDisponibles:
                loadDataInListView_Disponibles();
                return true;
            default:
                return false;
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

}