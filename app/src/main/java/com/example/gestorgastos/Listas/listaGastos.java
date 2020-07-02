package com.example.gestorgastos.Listas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.gestorgastos.Adapters.GastosAdapter;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Add.addGasto;
import com.example.gestorgastos.Roles.usuarioScreen;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class listaGastos extends AppCompatActivity implements TextWatcher, PopupMenu.OnMenuItemClickListener {

    private ListView listGastos;
    private ArrayList<GastosModel> gastos;
    private TextView noHayAviso, indicador;
    private SQLAdmin sql;
    private GastosAdapter adapter;
    private String dni;
    private EditText filtrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        dni = preferences.getString("dni", "");
        setContentView(R.layout.activity_lista_gastos);
        listGastos = (ListView) findViewById(R.id.listGastos);
        noHayAviso = (TextView)findViewById(R.id.noHayAviso);
        filtrar = (EditText) findViewById(R.id.search);
        indicador = (TextView)findViewById(R.id.indicador);
        filtrar.addTextChangedListener(this);
        gastos = new ArrayList<>();

        sql = new SQLAdmin(this);
        loadDataInListView_habiles();
    }

    public void loadDataInListView_habiles() {
        gastos = sql.getMisGastos_habiles(dni);
        indicador.setText(R.string.gastos_ingresados);
        if (gastos.size() > 0){
            filtrar.setEnabled(true);
            listGastos.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new GastosAdapter(this, gastos, listaGastos.this);
            listGastos.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else{
            filtrar.setEnabled(false);
            listGastos.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(R.string.no_se_encontraron_gastos_en_la_busqueda);
        }

    }


    public void loadDataInListView_pendientes() {
        gastos = sql.getMisGastos_pendientes(dni);
        indicador.setText(R.string.gastos_pendientes_de_revision);
        if (gastos.size() > 0){
            filtrar.setEnabled(true);
            listGastos.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new GastosAdapter(this, gastos, listaGastos.this);
            listGastos.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else{
            filtrar.setEnabled(false);
            listGastos.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(R.string.no_se_encontraron_gastos_en_la_busqueda);
        }

    }



    public void volver(View view) {
        Intent i = new Intent(this, usuarioScreen.class);
        startActivity(i);
    }


    public void verMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_gastos);
        popupMenu.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gastos , menu);
        return true;
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
    public void onBackPressed() {
        Intent i = new Intent(this, usuarioScreen.class);
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

}