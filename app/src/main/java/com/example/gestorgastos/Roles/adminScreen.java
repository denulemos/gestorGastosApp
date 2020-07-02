package com.example.gestorgastos.Roles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gestorgastos.Add.addUsuario;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.Listas.listaViajes;
import com.example.gestorgastos.MainActivity;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Gestores.gestionCamiones;
import com.example.gestorgastos.Gestores.gestionCategorias;
import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.example.gestorgastos.perfil_usuario;

public class adminScreen extends AppCompatActivity {
    private TextView bienvenida;
    private ImageView aviso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        aviso = (ImageView)findViewById(R.id.hayGastosPendientes);
        bienvenida = (TextView) findViewById(R.id.textoBienvenida);
        SQLAdmin sql = new SQLAdmin(this);

        setBienvenida();
        if (sql.hayGastosPendiendientes()){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.hay_gastos_pendientes_de_revision))
                    .setNeutralButton("Ok", null)
                    .show();
            aviso.setVisibility(View.VISIBLE);

        }
        else{
            aviso.setVisibility(View.GONE);
        }


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
        Log.d("adminScreen", "Nombre: " + nombre);
        Log.d("adminScreen", "Apellido: " + apellido);
        bienvenida.setText(getString(R.string.bienvenido) + " " + nombre + " " + apellido + "!");


    }



    public void gestionCategorias(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, gestionCategorias.class);
            startActivity(i);
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para gestionar Categorias")
                    .setNegativeButton("Ok", null)
                    .show();
        }

    }

    public void gestionViajes(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, listaViajes.class);
            startActivity(i);
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para gestionar Viajes")
                    .setNegativeButton("Ok", null)
                    .show();
        }

    }

    public void gestionConductor(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, listaConductores.class);
            startActivity(i);
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet gestionar Usuarios")
                    .setNegativeButton("Ok", null)
                    .show();
        }

    }


    public void gestionCamiones(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, gestionCamiones.class);
            startActivity(i);
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para gestionar Camiones")
                    .setNegativeButton("Ok", null)
                    .show();
        }

    }

    public void verPerfil(View v) {
        Intent i = new Intent(this, perfil_usuario.class);
        startActivity(i);
    }


    public void gestionGastos(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, gestionGastos.class);
            startActivity(i);
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para gestionar Gastos")
                    .setNegativeButton("Ok", null)
                    .show();
        }


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