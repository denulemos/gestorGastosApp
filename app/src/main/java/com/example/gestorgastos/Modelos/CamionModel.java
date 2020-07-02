package com.example.gestorgastos.Modelos;

public class CamionModel {

    private int id;
    private String patente, activo, chofer;

    public CamionModel(int id, String patente, String activo, String chofer) {
        this.id = id;
        this.patente = patente;
        this.activo = activo;
        this.chofer = chofer;
    }

    public CamionModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }
}

