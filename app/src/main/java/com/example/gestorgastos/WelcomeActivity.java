package com.example.gestorgastos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WelcomeActivity extends AppCompatActivity {

    private int contadorLog = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("WelcomeActivity", contadorLog++ + " onCreate: Crea activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Log.d("WelcomeActivity", contadorLog++ + " onCreate: Muestra activity en pantalla");
        Log.d("WelcomeActivity", contadorLog++ + " onCreate: Ahora redirige a la Main Activity ");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}