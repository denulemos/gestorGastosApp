package com.example.gestorgastos.Gestores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gestorgastos.Adapters.CategoriaAdapter;
import com.example.gestorgastos.Modelos.CategoriasModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.SQL.SQLAdmin;

import java.util.ArrayList;

public class gestionCategorias extends AppCompatActivity implements TextWatcher {

    private ListView listaCategorias;
    private SQLAdmin sql;
    private ArrayList<CategoriasModel> arrayCat;
    private ImageButton delete;
    private EditText filtrar;
    private CategoriaAdapter adapter;
    private EditText agregarCategoriaInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_categorias);

        listaCategorias = (ListView) findViewById(R.id.listaCategorias);
        arrayCat = new ArrayList<>();
        filtrar = (EditText) findViewById(R.id.filtrarCategoria);
        filtrar.addTextChangedListener(this);
        sql = new SQLAdmin(this);
        agregarCategoriaInput = (EditText) findViewById(R.id.agregarCategoriaInput);

        loadDataInListView();


    }


    public void loadDataInListView() {
        arrayCat = sql.getAllCategorias();
        sql.close();
        adapter = new CategoriaAdapter(this, arrayCat, gestionCategorias.this);
        listaCategorias.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public void agregar(View view) {
        SQLiteDatabase base = sql.getWritableDatabase();
        String nombre = agregarCategoriaInput.getText().toString();
        if (nombre.trim().length()!=0) {
            boolean found = validarExistente(nombre.toLowerCase());
            if (!found) {
                ContentValues cv = new ContentValues();
                cv.put("categoria_nombre", nombre);

                base.insert("categoria", null, cv);
                base.close();

                agregarCategoriaInput.setText("");

                Toast.makeText(this, getString(R.string.categoria_agregada), Toast.LENGTH_SHORT).show();
                loadDataInListView();
            } else {
                Toast.makeText(this, getString(R.string.categoria_existente), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, getString(R.string.no_se_recibio_categoria), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarExistente(String nombre) {
        boolean found = false;
        for (CategoriasModel category : arrayCat) {
            if (category.getNombre().toLowerCase().equals(nombre)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void update() {

        adapter.notifyDataSetChanged();
    }

    public void volver(View view) {
        Intent i = new Intent(this, adminScreen.class);
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
        this.adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}