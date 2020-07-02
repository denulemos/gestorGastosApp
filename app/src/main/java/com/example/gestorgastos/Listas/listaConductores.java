package com.example.gestorgastos.Listas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
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
import com.example.gestorgastos.Adapters.CamioneroAdapter;
import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Roles.adminScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class listaConductores extends AppCompatActivity implements TextWatcher, PopupMenu.OnMenuItemClickListener {
    private ListView listChoferes;
    private ArrayList<UsuariosModel> usuarios;
    private SQLAdmin sql;
    private Button buttonAddItem;
    private CamioneroAdapter adapter;
    private TextView noHayAviso, indicador;
    private EditText filtrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_conductores);
        listChoferes = (ListView) findViewById(R.id.listaCamiones);
        noHayAviso = (TextView) findViewById(R.id.no_hay_aviso);
        indicador = (TextView) findViewById(R.id.indicador);
        filtrar = (EditText) findViewById(R.id.search);
        filtrar.addTextChangedListener(this);
        usuarios = new ArrayList<>();
        sql = new SQLAdmin(this);
        loadDataInListView_habilitados();


    }

    public void verMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_usuario);
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
        inflater.inflate(R.menu.menu_usuario, menu);
        return true;
    }

    public void loadDataInListView_habilitados() {
        indicador.setText(getString(R.string.usuarios_habilitados));
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String dni = preferences.getString("dni", "");
        usuarios = sql.getAllCamioneros(dni);
        if (usuarios.size() > 0) {
            filtrar.setEnabled(true);
            listChoferes.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new CamioneroAdapter(this, usuarios, listaConductores.this);
            listChoferes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            listChoferes.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_usuarios));
        }
    }

    public void loadDataInListView_Inhabilitados() {
        indicador.setText(getString(R.string.usuarios_deshabilitados));
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String dni = preferences.getString("dni", "");
        usuarios = sql.getAllCamioneros_inhabilitados(dni);
        if (usuarios.size() > 0) {
            filtrar.setEnabled(true);
            listChoferes.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new CamioneroAdapter(this, usuarios, listaConductores.this);
            listChoferes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            listChoferes.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_usuarios));
        }

    }


    public void loadDataInListView_Administradores() {
        indicador.setText(getString(R.string.usuarios_administradores));
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String dni = preferences.getString("dni", "");
        usuarios = sql.getAllAdministradores(dni);
        if (usuarios.size() > 0) {
            filtrar.setEnabled(true);
            listChoferes.setVisibility(View.VISIBLE);
            noHayAviso.setVisibility(View.GONE);
            adapter = new CamioneroAdapter(this, usuarios, listaConductores.this);
            listChoferes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            filtrar.setEnabled(false);
            listChoferes.setVisibility(View.GONE);
            noHayAviso.setVisibility(View.VISIBLE);
            noHayAviso.setText(getString(R.string.no_se_encontraron_usuarios));
        }

    }


    public void volverAlMenu(View view) {
        Intent i = new Intent(this, adminScreen.class);
        startActivity(i);
    }

    public void agregarChofer(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
        Intent i = new Intent(this, addUsuario.class);
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
        switch (item.getItemId()) {
            case R.id.habilitados:
                loadDataInListView_habilitados();
                return true;
            case R.id.deshabilitados:
                loadDataInListView_Inhabilitados();
                return true;
            case R.id.administradores:
                loadDataInListView_Administradores();
                return true;
            default:
                return false;
        }


    }
}