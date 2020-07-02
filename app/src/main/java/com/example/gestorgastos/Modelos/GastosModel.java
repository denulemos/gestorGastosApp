package com.example.gestorgastos.Modelos;

public class GastosModel {
    private int id, estimacion, sincronizacion, importe;
    private String chofer, categoria, ubicacionlat, ubicacionlong, foto, fecha, viaje;

    public GastosModel(int id, String viaje, String chofer, String categoria, String ubicacionlat, String ubicacionlong, String foto, String fecha, int importe, int estimacion,int sincronizacion) {
        this.id = id;
        this.viaje = viaje;
        this.chofer = chofer;
        this.categoria = categoria;
        this.ubicacionlat = ubicacionlat;
        this.ubicacionlong = ubicacionlong;
        this.foto = foto;
        this.fecha = fecha;
        this.importe = importe;
        this.sincronizacion = sincronizacion;
        this.estimacion = estimacion;
    }

    public int getEstimacion() {
        return estimacion;
    }

    public void setEstimacion(int estimacion) {
        this.estimacion = estimacion;
    }

    public GastosModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSincronizacion() {
        return sincronizacion;
    }

    public void setSincronizacion(int sincronizacion) {
        this.sincronizacion = sincronizacion;
    }

    public String getViaje() {
        return viaje;
    }

    public void setViaje(String viaje) {
        this.viaje = viaje;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUbicacionlat() {
        return ubicacionlat;
    }

    public void setUbicacionlat(String ubicacionlat) {
        this.ubicacionlat = ubicacionlat;
    }

    public String getUbicacionlong() {
        return ubicacionlong;
    }

    public void setUbicacionlong(String ubicacionlong) {
        this.ubicacionlong = ubicacionlong;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getImporte() {
        return importe;
    }

    public void setImporte(int importe) {
        this.importe = importe;
    }
}

