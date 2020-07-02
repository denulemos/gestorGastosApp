package com.example.gestorgastos.Add;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gestorgastos.Gestores.gestionGastos;
import com.example.gestorgastos.Listas.listaGastos;
import com.example.gestorgastos.Maps.MapsActivity;
import com.example.gestorgastos.Maps.MapsActivity2;
import com.example.gestorgastos.Modelos.GastosModel;
import com.example.gestorgastos.R;
import com.example.gestorgastos.Roles.usuarioScreen;
import com.example.gestorgastos.SQL.SQLAdmin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class addGasto extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        AdapterView.OnItemSelectedListener {


    private TextView ubicacionActual, viaje;
    private Spinner categoria;
    private LocationManager locManager;
    private Location loc;
    private TextInputLayout importe;
    private String dni, viajeGasto, categoriaGasto;
    private SQLAdmin sql;
    private String latitud = "No obtenido";
    private String longitud = "No obtenido";
    private Button irMap;
    private Bitmap image;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final float PREFERRED_WIDTH = 600;
    private static final float PREFERRED_HEIGHT = 600;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private ImageView previewFoto;
    private int numeroViaje;
    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SQLAdmin(this);
        setContentView(R.layout.activity_add_gasto);
        categoria = (Spinner) findViewById(R.id.gastoCategoria);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        irMap = (Button) findViewById(R.id.verUbicacionActual);
        viaje = (TextView) findViewById(R.id.numeroViaje);
        importe = (TextInputLayout) findViewById(R.id.importeGasto);
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        dni = preferences.getString("dni", "");
        categoria.setOnItemSelectedListener(this);
        previewFoto = (ImageView) findViewById(R.id.previewFoto);
        loadSpinnerData();
        definirUbicacion();
        definirViaje();


        Bundle bundle = this.getIntent().getExtras();
        if (getIntent().hasExtra("monto")) {
            if (bundle.getInt("monto") != -1) {
                importe.getEditText().setText(String.valueOf(bundle.getInt("monto")));
            }
        }


    }

    private void loadSpinnerData() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        List<String> categorias = db.getCategoriasString();


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categorias);


        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoria.setAdapter(dataAdapter);

    }

    public void irMapa(View v) {


        Intent i = new Intent(this, MapsActivity.class);

        i.putExtra("latitud", Double.parseDouble(latitud));
        i.putExtra("longitud", Double.parseDouble(longitud));
        if (importe.getEditText().getText().toString().trim().length() > 0) {
            i.putExtra("monto", Integer.parseInt(String.valueOf(importe.getEditText().getText())));
        } else {
            i.putExtra("monto", -1);
        }

        startActivity(i);


    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {


        categoriaGasto = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        categoriaGasto = parent.getItemAtPosition(0).toString();
    }


    //Volver al menu principal
    public void volverAlMenu(View view) {
        final Intent i = new Intent(this, usuarioScreen.class);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_creacion_gasto))
                .setMessage(getString(R.string.los_datos_guardados_se_perderan))
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("addGasto", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        startActivity(i);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void definirUbicacion() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            irMap.setVisibility(View.GONE);

            return;
        } else {
            irMap.setVisibility(View.VISIBLE);


            LocationListener locationListenerGPS = new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    latitud = String.valueOf(location.getLatitude());
                    longitud = String.valueOf(location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListenerGPS);
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());

        }

    }

    private void definirViaje() {
        SQLAdmin db = new SQLAdmin(getApplicationContext());
        ArrayList<String> viajelista = db.getViajesChoferId(dni);
        viaje.setText(viajelista.get(0) + " / " + viajelista.get(1));
        numeroViaje = Integer.parseInt(viajelista.get(2));


    }


    public void abrirCamara(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image = imageBitmap;
            previewFoto.setImageBitmap(image);
        }
    }

    private static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void confirmacionAgregarGasto(View v) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.agregando_gasto))
                .setMessage(getString(R.string.agregando_gasto_confirmacion))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agregarGasto();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();

    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = PREFERRED_WIDTH / width;
        float scaleHeight = PREFERRED_HEIGHT / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();
        return resizedBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void agregarGasto() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (importe.getEditText().length() > 0 && Integer.parseInt(String.valueOf(importe.getEditText().getText())) > 0) {
                String fecha;
                SQLiteDatabase base = sql.getWritableDatabase();
                SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm:ss", new Locale("ES"));
                Date fechaDate = new Date();

                fecha = formateador.format(fechaDate);

                ContentValues cv = new ContentValues();
                cv.put("gastos_chofer", dni);
                cv.put("gastos_viaje", numeroViaje);
                cv.put("gastos_importe", importe.getEditText().getText().toString());
                cv.put("gastos_categoria", categoriaGasto);
                cv.put("gastos_fecha", fecha);
                cv.put("gastos_ubicacion_lat", latitud);
                cv.put("gastos_ubicacion_long", longitud);
                if (image != null) {
                    cv.put("gastos_foto", bitmapToString(resizeBitmap(image)));
                }
                cv.put("gastos_estimacion", 1);
                cv.put("gastos_sincronizado", 0);

                try {
                    base.insert("gastos", null, cv);
                    final Intent i = new Intent(this, listaGastos.class);

                    Toast.makeText(this, getString(R.string.gastoAgregado), Toast.LENGTH_SHORT).show();
                    sql.sumarMonto(Integer.parseInt(importe.getEditText().getText().toString()), numeroViaje);
                    // sql.sincronizarGasto(sql.getUltimoGasto().getId());
                    base.close();

                    final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.sinc_info), getString(R.string.espere_un_momento));
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("sheet", "Viajes");
                        jsonObject.put("action", "actualizarMontoViaje");
                        jsonObject.put("id", numeroViaje);
                        jsonObject.put("monto", sql.getMontoGasto(numeroViaje));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RequestQueue queue = Volley.newRequestQueue(this);
                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzxLh-ycT1rVBUVyI8gcww21-Jt4-0Fdg8zZmtQakw2yFLBcdA/exec", jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    loading.dismiss();
                                    startActivity(i);


                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();

                            startActivity(i);
                        }
                    });

                    queue.add(stringRequest);

                    SharedPreferences preferences = getSharedPreferences("addGasto", Context.MODE_PRIVATE);
                    preferences.edit().clear().commit();


                } catch (Exception ex) {
                    base.close();
                    Toast.makeText(this, getString(R.string.errorGasto), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.ingrese_monto_valido), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(this, usuarioScreen.class);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.cancelando_gasto))
                .setMessage(getString(R.string.cancelando_creacion_gasto))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(i);
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();

    }


}