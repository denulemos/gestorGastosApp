package com.example.gestorgastos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gestorgastos.SQL.SQLAdmin;

import java.util.Random;

public class Recuperar_password extends AppCompatActivity {

    private Button solicitarCambio, cancelar;
    private EditText dni, email;
    private Cursor fila;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);
        solicitarCambio = (Button) findViewById(R.id.solicitarCambio);
        cancelar = (Button) findViewById(R.id.Cancelar);
        dni = (EditText) findViewById(R.id.user_);
        email = (EditText) findViewById(R.id.email_);
        sql = new SQLAdmin(this);
    }

    public void cancelar(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    private int generarRandom(){
        Random rand = new Random();
        int rand_int1 = rand.nextInt((100000000 - 5000) + 1) + 5000;
        return rand_int1;
    }

    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void solicitarContraseña(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (dni.getText().toString().length() <= 0 || email.getText().length() <= 0) {
                Toast.makeText(this, getString(R.string.completar_todos_datos_para_continuar) , Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db = sql.getReadableDatabase();
                String usuarioIngresado = dni.getText().toString();
                String emailIngresado = email.getText().toString();
                fila = db.rawQuery("select usuario_id, usuario_mail from usuarios where usuario_dni= '" + usuarioIngresado + "' AND usuario_mail = '" +  emailIngresado + "'", null);

                if (fila.moveToFirst()) {
                    int iduser = fila.getInt(0);
                    String email = fila.getString(1);

                    int conTemp = generarRandom();

                    sql.cambiarContraseña(iduser , String.valueOf(conTemp));

                    SendMail se = new SendMail(this , email , getString(R.string.cambio_contrasena), getString(R.string.contrasena_temporal) + String.valueOf(conTemp));

                    se.execute();

                    Toast.makeText(this, getString(R.string.solicitud_cambio_exitosa), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(this, getString(R.string.mala_combinacion_dni_mail), Toast.LENGTH_SHORT).show();
                }

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
}