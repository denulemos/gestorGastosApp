package com.example.gestorgastos.Maps;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitud;
    private double longitud;
    private LatLng ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        Bundle bundle = this.getIntent().getExtras();
        latitud = bundle.getDouble("latitud");
        longitud = bundle.getDouble("longitud");

        setearUbicacion(latitud, longitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void volverAlMenu(View v) {

        final SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final Intent i;

        if (preferences.getInt("admin", 9) == 1) {
            i = new Intent(this, gestionGastos.class);
        } else {
            i = new Intent(this, listaGastos.class);
        }

        this.startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng marker = ubicacion;
        mMap.addMarker(new MarkerOptions().position(marker).title("Ubicacion Gasto").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 7));
    }

    public void setearUbicacion(double lat, double longi) {

        ubicacion = new LatLng(lat, longi);


    }
}