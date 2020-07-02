package com.example.gestorgastos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.Roles.usuarioScreen;

public class previewFoto extends AppCompatActivity {
    private ImageView foto;
    private ImageButton volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_foto);
        foto = (ImageView) findViewById(R.id.imagen);
        Bundle bundle = this.getIntent().getExtras();
        String uri = bundle.getString("uri");
        foto.setImageBitmap(stringToBitmap(uri));

    }

    //Volver al menu principal
    public void volver(View view) {
        final Intent i;
        Bundle bundle = this.getIntent().getExtras();
        String parent = bundle.getString("parent");
        if (parent.equals("admin")){
         i = new Intent(this, gestionGastos.class);
        }
        else{
          i = new Intent(this, listaGastos.class);

        }
        startActivity(i);

    }

    private static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}