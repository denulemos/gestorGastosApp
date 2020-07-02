package com.example.gestorgastos.Maps;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gestorgastos.Add.addGasto;
import com.example.gestorgastos.Listas.listaConductores;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.R;
import com.example.gestorgastos.previewFoto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener , GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private FloatingActionButton volverMenu;
    private double latitud;
    private double longitud;
    private LatLng ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = this.getIntent().getExtras();
        latitud = bundle.getDouble("latitud");
        longitud = bundle.getDouble("longitud");
        setearUbicacion(latitud, longitud);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        volverMenu = (FloatingActionButton)findViewById(R.id.volverMenu);


    }

    public void volverAlMenu (View v){

        Bundle bundle = this.getIntent().getExtras();

        Intent i = new Intent(this, addGasto.class);

            i.putExtra("monto", bundle.getInt("monto"));

        this.startActivity(i);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng marker = ubicacion;
        mMap.addMarker(new MarkerOptions().position(marker).title("Ubicacion Actual").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 7));


        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }


      @Override
    public void onMarkerDrag(Marker marker){
        Log.d("MapsActivity" , String.valueOf(marker.getPosition()));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void setearUbicacion(double lat, double longi) {

        ubicacion = new LatLng(lat, longi);


    }
}