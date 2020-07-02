package com.example.gestorgastos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.gestorgastos.Modelos.UsuariosModel;
import com.example.gestorgastos.Roles.adminScreen;
import com.example.gestorgastos.Roles.usuarioScreen;
import com.example.gestorgastos.SQL.SQLAdmin;

public class MainActivity extends AppCompatActivity {

    private EditText user, pass;
    private SQLAdmin sql;
    private Button olvideContraseña;
    private Switch recordarDni;
    private Cursor fila;

    @Override
    protected void onDestroy() {
        sql.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (EditText) findViewById(R.id.user_);
        pass = (EditText) findViewById(R.id.pass_);
        recordarDni = (Switch) findViewById(R.id.recordarDni);
        sql = new SQLAdmin(this);
        inicializar();
pedirPermisos();

    }

    private void pedirPermisos(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }

    }
    private void inicializar() {
        SharedPreferences preferences = getSharedPreferences("recordarDni", Context.MODE_PRIVATE);
        String dni = preferences.getString("dni", "");
        if (dni != "") {
            recordarDni.setChecked(true);
            user.setText(dni);
        } else {
            recordarDni.setChecked(false);
        }
    }


    private void reiniciarShared() {
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public void ingresar(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            SQLiteDatabase db = sql.getReadableDatabase();
            String username = user.getText().toString();
            String password = pass.getText().toString();
            reiniciarShared();

            //Validar si los campos fueron llenados
            if (username.length() == 0 && password.length() != 0) {
                Toast.makeText(this, getString(R.string.completar_user), Toast.LENGTH_SHORT).show();
            } else if (username.length() != 0 && password.length() == 0) {
                Toast.makeText(this, getString(R.string.completar_pwd), Toast.LENGTH_SHORT).show();
            } else if (username.length() == 0 && password.length() == 0) {
                Toast.makeText(this, getString(R.string.completar_campos), Toast.LENGTH_SHORT).show();
            } else {
                //Buscar usuario
                fila = db.rawQuery("select usuario_dni,usuario_contraseña, usuario_administrador, usuario_estado " +
                        "from usuarios where usuario_dni= '" + username + "' and usuario_contraseña = '" +
                        password + "'", null);

                if (fila.moveToFirst()) {
                    String usua = fila.getString(0);
                    String pass = fila.getString(1);
                    int admin = fila.getInt(2);
                    int estado = fila.getInt(3);

                    if (estado == 0) {
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.aviso))
                                .setMessage(getString(R.string.usuario_inactivo))
                                .setNegativeButton("Ok", null)
                                .show();
                    } else {


                        if (username.equals(usua) && password.equals(pass)) {
                            if (recordarDni.isChecked()) {
                                establecerRecordarDni(usua);
                            } else {
                                SharedPreferences preferences = getSharedPreferences("recordarDni", Context.MODE_PRIVATE);
                                preferences.edit().clear().commit();
                            }
                            establecerSharedPreferences(usua);
                            if (admin == 1) {
                                Intent i = new Intent(this, adminScreen.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(this, usuarioScreen.class);
                                startActivity(i);
                            }

                        } else {
                            Toast.makeText(this, getString(R.string.ingreso_invalido), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.ingreso_invalido), Toast.LENGTH_LONG).show();
                }

            }
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para usar la aplicacion")
                    .setNegativeButton("Ok", null)
                    .show();
        }


    }

    //Deshabilita navegabilidad a pantalla anterior y simula mejor el LogOff
    private long backPressedTime = 0;
    private Toast backToast = null;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            backToast = Toast.makeText(getBaseContext(), getString(R.string.dos_veces), Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void establecerSharedPreferences(String usuario) {
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        UsuariosModel usuarioModel = sql.getUsuarioShared(usuario);
        editor.putInt("id", usuarioModel.getId());
        editor.putString("dni", usuarioModel.getDni());
        editor.putString("nombre", usuarioModel.getNombre());
        editor.putString("apellido", usuarioModel.getApellido());
        editor.putString("mail", usuarioModel.getMail());
        editor.putString("contraseña", usuarioModel.getContraseña());
        editor.putString("camion", usuarioModel.getCamion());
        editor.putInt("admin", usuarioModel.getAdmin());
        editor.commit();

    }

    private void establecerRecordarDni(String usuario) {
        SharedPreferences preferences = getSharedPreferences("recordarDni", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        UsuariosModel usuarioModel = sql.getUsuarioShared(usuario);

        editor.putString("dni", usuarioModel.getDni());

        editor.commit();
    }

    public void recuperarContraseña(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Intent i = new Intent(this, Recuperar_password.class);
            startActivity(i);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Error")
                    .setMessage("Se requiere una conexion a internet para usar esta funcionalidad")
                    .setNegativeButton("Ok", null)
                    .show();
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