package com.example.gestorgastos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestorgastos.Editar.editar_viaje_encamino;
import com.example.gestorgastos.Editar.editar_viaje_planeado;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.Roles.usuarioScreen;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

public class perfil_usuario extends AppCompatActivity {
    private TextInputLayout password;
    private String emailoriginal, passwordoriginal, dnicheck;
    private TextView nombreCompleto, dni, email, camion;
    private final SQLAdmin sql = new SQLAdmin(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        email = (TextView) findViewById(R.id.emailPerfil);
        password = (TextInputLayout) findViewById(R.id.contraseñaPerfil);
        nombreCompleto = (TextView) findViewById(R.id.nombrePerfil);
        dni = (TextView) findViewById(R.id.dniPerfil);
        camion = (TextView) findViewById(R.id.camionPerfil);

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        passwordoriginal = preferences.getString("contraseña", "");
        dnicheck = preferences.getString("dni", "");
        email.setText("Email: " + preferences.getString("mail", ""));
        password.getEditText().setText(passwordoriginal);
        nombreCompleto.setText(preferences.getString("nombre", "") + " " + preferences.getString("apellido", ""));
        dni.setText("Dni: " + preferences.getString("dni", ""));

        if (preferences.getInt("admin", 9) == 1) {
            camion.setVisibility(View.GONE);
        } else {
            if (preferences.contains("camion")) {
                camion.setText("Camion: " + preferences.getString("camion", ""));
            } else {
                camion.setText(getString(R.string.camion_no_asignado));
            }
        }


    }

    //Log Out
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
                        final String passwordActual = password.getEditText().getText().toString();


                        if (passwordActual.equals(passwordoriginal)) {
                            preferences.edit().clear().commit();
                            startActivity(i);
                        }
                        else{
                            confirmarCambioContraseña();
                            dialog.dismiss();
                        }


                    }


                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    public void confirmarCambioContraseña(){
        final Intent i = new Intent(this, MainActivity.class);
        final SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final String passwordActual = password.getEditText().getText().toString();

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.aviso))
                .setMessage(getString(R.string.hemos_visto_cambio_contrasena))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        sql.cambiarContraseña(preferences.getInt("id", 0), password.getEditText().getText().toString());
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.chof_contraseña), password.getEditText().getText().toString());
                        editor.commit();
                        preferences.edit().clear().commit();
                        startActivity(i);


                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().clear().commit();
                        startActivity(i);
                    }
                })
                .show();
    }

    public void hideKeyboard (View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void volverMenuBoton(View view) {
        final SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final Intent i;

        if (preferences.getInt("admin", 9) == 1) {
            i = new Intent(this, adminScreen.class);
        } else {
            i = new Intent(this, usuarioScreen.class);
        }

        final String passwordActual = password.getEditText().getText().toString();

        if (passwordActual.equals(passwordoriginal)) {
            startActivity(i);
        } else {
            if (passwordoriginal.isEmpty()) {
                Toast.makeText(this, getString(R.string.password_no_puede_quedar_vacio), Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.aviso))
                        .setMessage(getString(R.string.hemos_visto_cambio_contrasena))
                        .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                sql.cambiarContraseña(preferences.getInt("id", 0), password.getEditText().getText().toString());
                                final SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(getString(R.string.chof_contraseña), password.getEditText().getText().toString());
                                editor.commit();
                                startActivity(i);


                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(i);
                            }
                        })
                        .show();
            }


        }
    }
}