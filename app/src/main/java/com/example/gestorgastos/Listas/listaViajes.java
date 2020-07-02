package com.example.gestorgastos.Listas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.gestorgastos.Adapters.ViajesAdapter;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Modelos.ViajesModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Add.addViaje;
import com.example.gestorgastos.Roles.adminScreen;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class listaViajes extends AppCompatActivity implements TextWatcher , PopupMenu.OnMenuItemClickListener{

    private ListView listViajes;
    private ArrayList<ViajesModel> viajes;
    private SQLAdmin sql;
    private TextView noHayAviso, indicador;
    private EditText filtrar;
    private ViajesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_viajes);
        listViajes = (ListView) findViewById(R.id.listaViajes);
        indicador = (TextView)findViewById(R.id.indicador);
        noHayAviso = (TextView)findViewById(R.id.no_hay_aviso2);
        viajes = new ArrayList<>();
        filtrar = (EditText) findViewById(R.id.filtroViaje);
        filtrar.addTextChangedListener(this);
        sql = new SQLAdmin(this);
        loadDataInListView_Planeados();
    }

    public void verMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_viajes);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_viajes , menu);
        return true;
    }




    public void loadDataInListView_disponibles() {
        viajes = sql.getAllViajes_disponibles();
        indicador.setText(getString(R.string.viajes_disponibles));
        if (viajes.size() > 0){
            filtrar.setEnabled(true);
            adapter = new ViajesAdapter(this, viajes, listaViajes.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noHayAviso.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);

        }
        else{
            filtrar.setEnabled(false);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
            listViajes.setVisibility(View.GONE);
        }


    }

    private void loadDataInListView_Planeados() {
        viajes = sql.getAllViajes_Planeados();
        indicador.setText(getString(R.string.viajes_planeados));
        if (viajes.size() > 0){
            filtrar.setEnabled(true);
            adapter = new ViajesAdapter(this, viajes, listaViajes.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noHayAviso.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);

        }
        else{
            filtrar.setEnabled(false);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
            listViajes.setVisibility(View.GONE);
        }


    }

    private void loadDataInListView_Camino() {
        viajes = sql.getAllViajes_Camino();
        indicador.setText(getString(R.string.viajes_en_camino));
        if (viajes.size() > 0){
            filtrar.setEnabled(true);
            adapter = new ViajesAdapter(this, viajes, listaViajes.this);
            listViajes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noHayAviso.setVisibility(View.GONE);
            listViajes.setVisibility(View.VISIBLE);

        }
        else{
            filtrar.setEnabled(false);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_viajes_busqueda));
            listViajes.setVisibility(View.GONE);
        }


    }


    public void volverAlMenu(View view) {
        Intent i = new Intent(this, adminScreen.class);
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
        switch (item.getItemId()){
            case R.id.disponibles:
                loadDataInListView_disponibles();
                return true;
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