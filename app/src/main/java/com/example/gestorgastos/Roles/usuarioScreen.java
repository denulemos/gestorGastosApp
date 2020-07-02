package com.example.gestorgastos.Roles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.MainActivity;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Add.addGasto;
import com.example.gestorgastos.Listas.viajes_propios;
import com.example.gestorgastos.perfil_usuario;


public class usuarioScreen extends AppCompatActivity {

    private TextView bienvenida;
    private String dni;
    private SQLAdmin sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        bienvenida = (TextView) findViewById(R.id.textoBienvenida);
        setBienvenida();
        sql = new SQLAdmin(this);
    }

    private void setBienvenida() {
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String nombre = preferences.getString("nombre", "");
        String apellido = preferences.getString("apellido", "");
        if (nombre == "" && apellido == ""){
            final Intent i = new Intent(this, MainActivity.class);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.debe_estar_logueado))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        Log.d("usuarioScreen", "Nombre: " + nombre);
        Log.d("usuarioScreen", "Apellido: " + apellido);
        bienvenida.setText(getString(R.string.bienvenido) + " " + nombre + " " + apellido + "!");
    }

    public void addGasto(View view) {
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String dni = preferences.getString("dni", "");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (sql.elChoferTieneViajesEnCamino(dni)) {
                Intent i = new Intent(this, addGasto.class);
                startActivity(i);
            } else {

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.Error))
                        .setMessage(getString(R.string.se_necesita_viaje))
                        .setNegativeButton("Ok", null)
                        .show();
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

    public void verPerfil(View v) {
        Intent i = new Intent(this, perfil_usuario.class);
        startActivity(i);
    }

    public void verGastos(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, listaGastos.class);
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

    public void verViajes(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, viajes_propios.class);
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

    //Ver log out
    public void logOut(View view) {


        final Intent i = new Intent(this, MainActivity.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.esta_saliendo_sesion))
                .setMessage(getString(R.string.esta_seguro_cerrar_sesion))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        startActivity(i);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();


    }

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(this, MainActivity.class);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.esta_saliendo_sesion))
                .setMessage(getString(R.string.esta_seguro_cerrar_sesion))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        startActivity(i);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();

    }
}