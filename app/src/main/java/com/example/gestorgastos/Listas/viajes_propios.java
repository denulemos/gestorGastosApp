package com.example.gestorgastos.Listas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.gestorgastos.Adapters.ViajesPropioAdapter;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Modelos.ViajesModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.usuarioScreen;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Add.addViaje;

import java.util.ArrayList;

public class viajes_propios extends AppCompatActivity implements TextWatcher, PopupMenu.OnMenuItemClickListener {

    private ListView listViajes;
    private String dni;
    private EditText filtrarViaje;
    private ArrayList<ViajesModel> viajes;
    private SQLAdmin sql;
    private ViajesPropioAdapter adapter;
    private TextView avisoCamion, avisoNoHay, indicador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_propios);
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        dni = preferences.getString("dni", "");
        listViajes = (ListView) findViewById(R.id.listaViajes);
        indicador = (TextView)findViewById(R.id.indicador);
        filtrarViaje = (EditText)findViewById(R.id.filtroViaje);
        avisoNoHay = (TextView)findViewById(R.id.no_encontrado_aviso);
        filtrarViaje.addTextChangedListener(this);
        avisoCamion = (TextView) findViewById(R.id.avisoNoCamion);


        viajes = new ArrayList<>();
        sql = new SQLAdmin(this);
        if (!(sql.elUsuarioTieneCamion(dni))) {
            avisoCamion.setVisibility(View.VISIBLE);
        } else {
            avisoCamion.setVisibility(View.GONE);
        }
        loadDataInListView_Planeados();
    }

    public void loadDataInListView_Planeados() {

        indicador.setText(getString(R.string.viajes_planeados));
        viajes = sql.getViajesDeChofer_Planeados(dni);
        if (viajes.size() > 0){
            filtrarViaje.setEnabled(true);
            avisoNoHay.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);
            adapter = new ViajesPropioAdapter(this, viajes, viajes_propios.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            filtrarViaje.setEnabled(false);
            listViajes.setVisibility(View.GONE);
            avisoNoHay.setVisibility(View.VISIBLE);
            avisoNoHay.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
        }



    }
    public void loadDataInListView_Camino() {
        indicador.setText(getString(R.string.viajes_en_camino));
        viajes = sql.getViajesDeChofer_Camino(dni);
        if (viajes.size() > 0){
            filtrarViaje.setEnabled(true);
            avisoNoHay.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);
            adapter = new ViajesPropioAdapter(this, viajes, viajes_propios.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            filtrarViaje.setEnabled(false);
            listViajes.setVisibility(View.GONE);
            avisoNoHay.setVisibility(View.VISIBLE);
            avisoNoHay.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
        }


    }
    public void loadDataInListView_Completados() {
        indicador.setText(getString(R.string.viajes_completados));
        viajes = sql.getViajesDeChofer_Terminados(dni);
        if (viajes.size() > 0){
            filtrarViaje.setEnabled(true);
            avisoNoHay.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);
            adapter = new ViajesPropioAdapter(this, viajes, viajes_propios.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            filtrarViaje.setEnabled(false);
            listViajes.setVisibility(View.GONE);
            avisoNoHay.setVisibility(View.VISIBLE);
            avisoNoHay.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
        }


    }

    public void volverAlMenu(View view) {
        Intent i = new Intent(this, usuarioScreen.class);
        startActivity(i);
    }

    public void addViaje(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, addViaje.class);
            startActivity(i);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void verMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_viajes_propios);
        popupMenu.show();

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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.viajesPlaneados:
                loadDataInListView_Planeados();
                return true;
            case R.id.viajesEnCamino:
                loadDataInListView_Camino();
                return true;
            default:
                return false;
        }


    }
}